package com.nuance.swype.input.accessibility;

import android.content.Context;
import android.media.SoundPool;
import com.nuance.swype.input.R;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
public class SoundResources {
    public static final int MAX_STREAMS = 1;
    public static final int SOUND_END_OF_LIST = 3;
    public static final int SOUND_KEY_CLICK = 2;
    public static final int SOUND_OUT_OF_RANGE = 1;
    private static SoundResources instance;
    private Context context;
    private SoundPool soundPool = new SoundPool(1, 3, 0);
    private Map<Integer, Integer> resources = new HashMap();

    public static SoundResources getInstance(Context context) {
        if (instance == null) {
            instance = new SoundResources(context);
        }
        return instance;
    }

    public static SoundResources getInstance() {
        return instance;
    }

    private SoundResources(Context context) {
        this.context = context;
        addTrack(1, R.raw.click1c);
        addTrack(2, R.raw.click8c);
        addTrack(3, R.raw.list);
    }

    protected void addTrack(int internalName, int resId) {
        this.resources.put(Integer.valueOf(internalName), Integer.valueOf(this.soundPool.load(this.context, resId, 0)));
    }

    public void play(int internalName) {
        Integer soundId = this.resources.get(Integer.valueOf(internalName));
        if (soundId != null) {
            play(soundId.intValue(), 1);
        }
    }

    public void play(int soundId, int numberOfTimes) {
        this.soundPool.play(soundId, 1.0f, 1.0f, 1, numberOfTimes - 1, 1.0f);
    }
}
