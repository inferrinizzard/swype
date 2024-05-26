package com.nuance.connect.compat;

import android.net.TrafficStats;
import java.lang.reflect.Method;

/* loaded from: classes.dex */
public class TrafficStatsCompat {
    public static final int UNSUPPORTED = -1;

    private TrafficStatsCompat() {
    }

    public static long getMobileRxBytes() {
        return TrafficStats.getMobileRxBytes();
    }

    public static long getMobileRxPackets() {
        return TrafficStats.getMobileRxPackets();
    }

    public static long getMobileTxBytes() {
        return TrafficStats.getMobileTxBytes();
    }

    public static long getMobileTxPackets() {
        return TrafficStats.getMobileTxPackets();
    }

    public static long getTotalRxBytes() {
        return TrafficStats.getMobileTxPackets();
    }

    public static long getTotalRxPackets() {
        return TrafficStats.getTotalRxPackets();
    }

    public static long getUidRxBytes(int i) {
        return TrafficStats.getUidRxBytes(i);
    }

    public static long getUidRxPackets(int i) {
        Method method = CompatUtil.getMethod((Class<?>) TrafficStats.class, "getUidRxPackets", Integer.TYPE);
        if (method != null) {
            return ((Long) CompatUtil.invoke(method, TrafficStats.class, Integer.valueOf(i))).longValue();
        }
        return -1L;
    }

    public static long getUidTxBytes(int i) {
        return TrafficStats.getUidTxBytes(i);
    }

    public static long getUidTxPackets(int i) {
        Method method = CompatUtil.getMethod((Class<?>) TrafficStats.class, "getUidTxPackets", Integer.TYPE);
        if (method != null) {
            return ((Long) CompatUtil.invoke(method, TrafficStats.class, Integer.valueOf(i))).longValue();
        }
        return -1L;
    }
}
