package com.nuance.nmdp.speechkit.util.audio;

import com.nuance.nmdp.speechkit.util.List;
import com.nuance.nmdp.speechkit.util.Logger;
import com.nuance.nmsp.client.sdk.common.defines.NMSPDefines;
import com.nuance.nmsp.client.sdk.components.audioplayback.NMSPAudioManager;
import com.nuance.nmsp.client.sdk.components.audioplayback.NMSPAudioPlaybackListener;
import com.nuance.nmsp.client.sdk.components.audioplayback.Player;
import com.nuance.nmsp.client.sdk.components.general.Parameter;
import com.nuance.nmsp.client.sdk.components.resource.common.Manager;

/* loaded from: classes.dex */
public final class PlayerHelper {
    private final Object _appContext;
    private final Object _context;
    private final IPlayerHelperListener _listener;
    private final Manager _manager;
    private Player _player;
    private final NMSPAudioPlaybackListener _playbackListener = createPlaybackListener();
    private boolean _starting = false;
    private boolean _stopping = false;
    private boolean _stopped = false;
    private boolean _waitingForStop = false;
    private Object _stopSync = new Object();

    public PlayerHelper(Manager manager, Object context, Object appContext, IPlayerHelperListener listener, NMSPDefines.Codec codec) {
        this._listener = listener;
        this._manager = manager;
        this._context = context;
        this._appContext = appContext;
        List parameters = new List();
        parameters.add(new Parameter(NMSPDefines.NMSP_DEFINES_ANDROID_CONTEXT, this._appContext, Parameter.Type.SDK));
        try {
            this._player = NMSPAudioManager.createPlayer(this._playbackListener, this._manager, parameters.toVector(), NMSPDefines.AudioSystem.OutputDevice.SPEAKER);
        } catch (Throwable tr) {
            Logger.error(this, "Error creating player", tr);
            this._player = null;
        }
    }

    public final void startPlayer() {
        if (!this._starting) {
            if (this._player != null) {
                this._starting = true;
                try {
                    Logger.info(this, "Starting audio player");
                    this._player.start();
                    return;
                } catch (Throwable tr) {
                    Logger.error(this, "Error starting player", tr);
                }
            }
        } else {
            Logger.error(this, "Player already started");
        }
        this._listener.error(this._context);
    }

    public final void stopPlayer() {
        if (this._starting && !this._stopping) {
            if (this._player != null) {
                this._stopping = true;
                synchronized (this._stopSync) {
                    try {
                        if (!this._stopped) {
                            Logger.info(this, "Stopping audio player");
                            this._player.stop();
                            this._waitingForStop = true;
                            while (!this._stopped) {
                                try {
                                    this._stopSync.wait();
                                } catch (InterruptedException e) {
                                }
                            }
                        }
                    } catch (Throwable tr) {
                        Logger.error(this, "Error stopping player", tr);
                        this._stopped = true;
                    }
                }
                return;
            }
            this._listener.error(this._context);
        }
    }

    public final Player getPlayer() {
        return this._player;
    }

    private NMSPAudioPlaybackListener createPlaybackListener() {
        return new NMSPAudioPlaybackListener() { // from class: com.nuance.nmdp.speechkit.util.audio.PlayerHelper.1
            private boolean _bufferPlayed = false;

            private void handleStop() {
                synchronized (PlayerHelper.this._stopSync) {
                    PlayerHelper.this._stopped = true;
                    if (PlayerHelper.this._waitingForStop) {
                        PlayerHelper.this._stopSync.notify();
                        PlayerHelper.this._waitingForStop = false;
                    }
                }
            }

            @Override // com.nuance.nmsp.client.sdk.components.audioplayback.NMSPAudioPlaybackListener
            public void playerUpdate(Player player, String event) {
                if (event == Player.BUFFER_PLAYED) {
                    if (!this._bufferPlayed) {
                        Logger.info(PlayerHelper.this, "First audio buffer played");
                        PlayerHelper.this._listener.started(PlayerHelper.this._context);
                        this._bufferPlayed = true;
                        return;
                    }
                    return;
                }
                if (event != Player.BUFFERING) {
                    if (event == "STARTED") {
                        Logger.info(PlayerHelper.this, "Audio playback started");
                        return;
                    }
                    if (event == "STOPPED") {
                        Logger.info(PlayerHelper.this, "Audio playback stopped");
                        handleStop();
                        PlayerHelper.this._listener.stopped(PlayerHelper.this._context);
                    } else if (event == Player.PLAYBACK_ERROR) {
                        Logger.error(PlayerHelper.this, "Audio playback error");
                        handleStop();
                        PlayerHelper.this._listener.error(PlayerHelper.this._context);
                    }
                }
            }
        };
    }
}
