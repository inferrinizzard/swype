package com.crashlytics.android;

import android.os.Process;
import io.fabric.sdk.android.services.common.CommonUtils;
import io.fabric.sdk.android.services.common.IdManager;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicLong;

/* loaded from: classes.dex */
final class CLSUUID {
    private static String _clsId;
    private static final AtomicLong _sequenceNumber = new AtomicLong(0);

    public CLSUUID(IdManager idManager) {
        long time = new Date().getTime();
        ByteBuffer allocate = ByteBuffer.allocate(4);
        allocate.putInt((int) (time / 1000));
        allocate.order(ByteOrder.BIG_ENDIAN);
        allocate.position(0);
        byte[] array = allocate.array();
        byte[] convertLongToTwoByteBuffer = convertLongToTwoByteBuffer(time % 1000);
        byte[] convertLongToTwoByteBuffer2 = convertLongToTwoByteBuffer(_sequenceNumber.incrementAndGet());
        byte[] convertLongToTwoByteBuffer3 = convertLongToTwoByteBuffer(Integer.valueOf(Process.myPid()).shortValue());
        byte[] bytes = {array[0], array[1], array[2], array[3], convertLongToTwoByteBuffer[0], convertLongToTwoByteBuffer[1], convertLongToTwoByteBuffer2[0], convertLongToTwoByteBuffer2[1], convertLongToTwoByteBuffer3[0], convertLongToTwoByteBuffer3[1]};
        String idSha = CommonUtils.sha1(idManager.getAppInstallIdentifier());
        String timeSeqPid = CommonUtils.hexify(bytes);
        _clsId = String.format(Locale.US, "%s-%s-%s-%s", timeSeqPid.substring(0, 12), timeSeqPid.substring(12, 16), timeSeqPid.subSequence(16, 20), idSha.substring(0, 12)).toUpperCase(Locale.US);
    }

    private static byte[] convertLongToTwoByteBuffer(long value) {
        ByteBuffer buf = ByteBuffer.allocate(2);
        buf.putShort((short) value);
        buf.order(ByteOrder.BIG_ENDIAN);
        buf.position(0);
        return buf.array();
    }

    public final String toString() {
        return _clsId;
    }
}
