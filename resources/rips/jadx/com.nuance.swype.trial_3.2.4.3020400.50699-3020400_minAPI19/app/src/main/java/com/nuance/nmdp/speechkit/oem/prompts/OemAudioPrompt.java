package com.nuance.nmdp.speechkit.oem.prompts;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import com.nuance.nmdp.speechkit.util.JobRunner;
import com.nuance.nmdp.speechkit.util.Logger;
import com.nuance.nmdp.speechkit.util.audio.IAudioPrompt;
import com.nuance.nmdp.speechkit.util.audio.IPlayerHelperListener;
import java.io.IOException;

/* loaded from: classes.dex */
public class OemAudioPrompt implements IAudioPrompt {
    private IPlayerHelperListener _currentListener = null;
    private Object _context = null;
    private MediaPlayer _player = null;
    private boolean _restart = false;
    private AssetFileDescriptor _file = null;
    private boolean _isDisposed = false;
    private int _duration = 0;

    @Override // com.nuance.nmdp.speechkit.util.audio.IAudioPrompt
    public void init(AssetFileDescriptor file) {
        this._file = file;
        this._player = createPlayer();
        if (this._player == null) {
            dispose();
        }
    }

    @Override // com.nuance.nmdp.speechkit.util.audio.IPrompt
    public void start(Object appContext, IPlayerHelperListener listener, Object context) {
        if (this._player == null || this._isDisposed) {
            Logger.error(this, "Can't start disposed audio prompt");
            listener.error(context);
            return;
        }
        Logger.info(this, "Starting audio prompt");
        this._currentListener = listener;
        this._context = context;
        if (this._player.isPlaying()) {
            Logger.warn(this, "Audio prompt is already playing. Stopping to restart.");
            this._player.stop();
            this._restart = true;
            return;
        }
        play();
    }

    @Override // com.nuance.nmdp.speechkit.util.audio.IPrompt
    public void stop() {
        if (this._player != null) {
            this._restart = false;
            if (this._player.isPlaying()) {
                try {
                    this._player.stop();
                } catch (Throwable tr) {
                    Logger.error(this, "Error stopping player", tr);
                }
                this._player.release();
                this._player = null;
                this._player = createPlayer();
                if (this._player == null) {
                    dispose();
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void play() {
        this._restart = false;
        this._player.start();
        this._currentListener.started(this._context);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void playerDone(final boolean error) {
        JobRunner.addJob(new Runnable() { // from class: com.nuance.nmdp.speechkit.oem.prompts.OemAudioPrompt.1
            @Override // java.lang.Runnable
            public void run() {
                if (OemAudioPrompt.this._player != null && !OemAudioPrompt.this._isDisposed) {
                    if (error) {
                        OemAudioPrompt.this._player.release();
                        OemAudioPrompt.this._player = null;
                        OemAudioPrompt.this._player = OemAudioPrompt.this.createPlayer();
                        if (OemAudioPrompt.this._player == null) {
                            if (OemAudioPrompt.this._currentListener != null) {
                                OemAudioPrompt.this._currentListener.error(OemAudioPrompt.this._context);
                                OemAudioPrompt.this._currentListener = null;
                                OemAudioPrompt.this._context = null;
                            }
                            OemAudioPrompt.this.dispose();
                            return;
                        }
                    }
                    if (OemAudioPrompt.this._restart) {
                        OemAudioPrompt.this.play();
                        return;
                    }
                    if (OemAudioPrompt.this._currentListener != null) {
                        if (error) {
                            OemAudioPrompt.this._currentListener.error(OemAudioPrompt.this._context);
                        } else {
                            OemAudioPrompt.this._currentListener.stopped(OemAudioPrompt.this._context);
                        }
                    }
                    OemAudioPrompt.this._currentListener = null;
                    OemAudioPrompt.this._context = null;
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public MediaPlayer createPlayer() {
        MediaPlayer player = new MediaPlayer();
        try {
            long offset = this._file.getStartOffset();
            long length = this._file.getLength();
            if (length == -1) {
                Logger.info(this, "Attempting to initialize MediaPlayer with asset file of unknown length");
                player.setDataSource(this._file.getFileDescriptor());
            } else {
                player.setDataSource(this._file.getFileDescriptor(), offset, length);
            }
            player.prepare();
            this._duration = player.getDuration();
            player.setOnErrorListener(new MediaPlayer.OnErrorListener() { // from class: com.nuance.nmdp.speechkit.oem.prompts.OemAudioPrompt.2
                @Override // android.media.MediaPlayer.OnErrorListener
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    Logger.error(OemAudioPrompt.this, "Error during audio prompt: " + what);
                    OemAudioPrompt.this.playerDone(true);
                    return true;
                }
            });
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() { // from class: com.nuance.nmdp.speechkit.oem.prompts.OemAudioPrompt.3
                @Override // android.media.MediaPlayer.OnCompletionListener
                public void onCompletion(MediaPlayer mp) {
                    Logger.info(OemAudioPrompt.this, "Audio prompt completed");
                    OemAudioPrompt.this.playerDone(false);
                }
            });
            return player;
        } catch (Exception e) {
            Logger.error(this, "Unable to create MediaPlayer for audio prompt", e);
            try {
                player.release();
            } catch (Exception e2) {
            }
            return null;
        }
    }

    @Override // com.nuance.nmdp.speechkit.util.audio.IPrompt
    public boolean isDisposed() {
        return this._isDisposed;
    }

    @Override // com.nuance.nmdp.speechkit.util.audio.IPrompt
    public void dispose() {
        this._isDisposed = true;
        if (this._player != null) {
            this._player.release();
            this._player = null;
        }
        if (this._file != null) {
            try {
                this._file.close();
            } catch (IOException e) {
                Logger.error(this, "Error closing audio prompt file", e);
            }
            this._file = null;
        }
        if (this._currentListener != null) {
            this._currentListener.error(this._context);
            this._currentListener = null;
        }
        this._context = null;
    }

    @Override // com.nuance.nmdp.speechkit.util.audio.IPrompt
    public int getDuration() {
        return this._duration;
    }
}
