package com.nuance.connect.comm;

/* loaded from: classes.dex */
public interface AnalyticsDataUsageScribe {
    void flush();

    void mark();

    void start();

    void writeRequest(Command command, long j);

    void writeResponse(Command command, long j);
}
