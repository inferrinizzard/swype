package com.nuance.nmdp.speechkit.util.audio;

/* loaded from: classes.dex */
public interface IRecorderHelperListener {
    public static final int STOPPED_END_OF_SPEECH = 2;
    public static final int STOPPED_ERROR = 4;
    public static final int STOPPED_MANUALLY = 0;
    public static final int STOPPED_NO_SPEECH = 1;
    public static final int STOPPED_TIMEOUT = 3;

    void signalEnergyUpdate(Object obj, float f);

    void started(Object obj);

    void stopped(Object obj, int i);
}
