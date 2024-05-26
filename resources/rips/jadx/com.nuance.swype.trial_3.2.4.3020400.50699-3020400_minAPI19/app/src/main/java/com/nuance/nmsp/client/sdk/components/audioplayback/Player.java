package com.nuance.nmsp.client.sdk.components.audioplayback;

import com.nuance.nmsp.client.sdk.components.general.NMSPAudioSink;

/* loaded from: classes.dex */
public interface Player extends NMSPAudioSink {
    public static final String BUFFERING = "BUFFERING";
    public static final String BUFFER_PLAYED = "BUFFER_PLAYED";
    public static final String PLAYBACK_ERROR = "PLAYBACK_ERROR";
    public static final String STARTED = "STARTED";
    public static final String STOPPED = "STOPPED";

    void start();

    void stop();
}
