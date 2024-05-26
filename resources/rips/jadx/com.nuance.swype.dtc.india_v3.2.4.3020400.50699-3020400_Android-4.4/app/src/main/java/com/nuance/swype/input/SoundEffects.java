package com.nuance.swype.input;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.SparseIntArray;

/* loaded from: classes.dex */
public class SoundEffects {
    protected Context context;
    private SoundPool soundPool;
    private final SparseIntArray soundPoolMap = new SparseIntArray();
    private final int volume;

    public SoundEffects(int size, Context c) {
        this.context = c;
        this.soundPool = new SoundPool(size, 5, 40);
        AudioManager mgr = (AudioManager) this.context.getSystemService("audio");
        this.volume = mgr.getStreamVolume(5);
    }

    public void addSound(int resid) {
        if (this.soundPool != null) {
            int soundId = this.soundPool.load(this.context, resid, 1);
            this.soundPoolMap.put(resid, soundId);
            this.soundPool.setLoop(soundId, 1);
        }
    }

    public void addLoopSound(int resid) {
        if (this.soundPool != null) {
            int soundId = this.soundPool.load(this.context, resid, 1);
            this.soundPoolMap.put(resid, soundId);
            this.soundPool.setLoop(soundId, -1);
        }
    }

    public void play(int resid) {
        if (this.soundPool != null) {
            int soundId = this.soundPoolMap.get(resid);
            this.soundPool.setLoop(soundId, 1);
            this.soundPool.play(soundId, this.volume, this.volume, 1, 0, 1.0f);
        }
    }

    public void playLoop(int resid) {
        if (this.soundPool != null) {
            int soundId = this.soundPoolMap.get(resid);
            this.soundPool.setLoop(soundId, -1);
            this.soundPool.play(soundId, this.volume, this.volume, 1, 0, 1.0f);
        }
    }

    public void stop(int resid) {
        if (this.soundPool != null) {
            int soundId = this.soundPoolMap.get(resid);
            this.soundPool.setLoop(soundId, 0);
            this.soundPool.setVolume(soundId, 0.0f, 0.0f);
        }
    }

    public void release() {
        if (this.soundPool != null) {
            this.soundPool.release();
            this.soundPool = null;
        }
    }
}
