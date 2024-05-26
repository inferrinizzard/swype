package com.nuance.nmdp.speechkit.oem.prompts;

import android.content.Context;
import android.os.Vibrator;
import com.nuance.nmdp.speechkit.util.JobRunner;
import com.nuance.nmdp.speechkit.util.Logger;
import com.nuance.nmdp.speechkit.util.audio.IPlayerHelperListener;
import com.nuance.nmdp.speechkit.util.audio.IPrompt;

/* loaded from: classes.dex */
public class OemVibratePrompt implements IPrompt {
    private final int _duration;
    private boolean _isDisposed = false;
    private Vibrator _vibrator = null;

    public OemVibratePrompt(int duration) {
        this._duration = duration;
    }

    @Override // com.nuance.nmdp.speechkit.util.audio.IPrompt
    public void dispose() {
        this._isDisposed = true;
        if (this._vibrator != null) {
            this._vibrator.cancel();
            this._vibrator = null;
        }
    }

    @Override // com.nuance.nmdp.speechkit.util.audio.IPrompt
    public boolean isDisposed() {
        return this._isDisposed;
    }

    @Override // com.nuance.nmdp.speechkit.util.audio.IPrompt
    public void start(Object appContext, final IPlayerHelperListener listener, final Object context) {
        this._vibrator = (Vibrator) ((Context) appContext).getSystemService("vibrator");
        if (this._vibrator == null) {
            Logger.error(this, "Unable to get vibrator service");
            listener.error(context);
            return;
        }
        if (this._isDisposed) {
            Logger.error(this, "Can't start disposed vibration prompt.");
            listener.error(context);
            return;
        }
        try {
            Logger.info(this, "Starting vibration (" + this._duration + " ms)");
            this._vibrator.vibrate(this._duration);
            JobRunner.addJob(new Runnable() { // from class: com.nuance.nmdp.speechkit.oem.prompts.OemVibratePrompt.1
                @Override // java.lang.Runnable
                public void run() {
                    Logger.info(OemVibratePrompt.this, "Vibration finished");
                    listener.stopped(context);
                    OemVibratePrompt.this._vibrator = null;
                }
            }, this._duration);
        } catch (Throwable tr) {
            Logger.error(this, "Unable to vibrate", tr);
            listener.error(context);
        }
    }

    @Override // com.nuance.nmdp.speechkit.util.audio.IPrompt
    public void stop() {
        if (this._vibrator != null) {
            this._vibrator.cancel();
            this._vibrator = null;
        }
    }

    @Override // com.nuance.nmdp.speechkit.util.audio.IPrompt
    public int getDuration() {
        return this._duration;
    }
}
