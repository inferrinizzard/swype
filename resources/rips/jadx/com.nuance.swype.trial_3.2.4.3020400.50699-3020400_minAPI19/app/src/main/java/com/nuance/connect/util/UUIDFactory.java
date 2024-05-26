package com.nuance.connect.util;

import com.nuance.swype.input.IME;
import java.util.UUID;

/* loaded from: classes.dex */
public class UUIDFactory {
    private static long clockSeqAndNode = 0;
    private static final long deltaFromEpochForType1UUID = 122192928000000000L;
    private static final int uuidTypeVersion = 4096;

    static {
        clockSeqAndNode = Long.MIN_VALUE;
        long random = Long.MIN_VALUE | (((long) (Math.random() * 1.40737488355327E14d)) ^ 3298534883328L);
        clockSeqAndNode = random;
        clockSeqAndNode = random | (((long) (Math.random() * 16383.0d)) << 48);
    }

    private static long generateTimeChunk(long j) {
        long j2 = (IME.RETRY_DELAY_IN_MILLIS * j) + deltaFromEpochForType1UUID;
        return ((j2 >> 48) & 4095) | 4096 | (j2 << 32) | ((281470681743360L & j2) >> 16);
    }

    public static final UUID generateUUID(long j) {
        return new UUID(generateTimeChunk(j), clockSeqAndNode);
    }
}
