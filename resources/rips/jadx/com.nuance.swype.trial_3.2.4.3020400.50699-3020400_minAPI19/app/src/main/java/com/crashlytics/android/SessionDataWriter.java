package com.crashlytics.android;

import android.app.ActivityManager;
import android.content.Context;
import com.nuance.nmsp.client.util.internal.dictationresult.parser.xml.xmlResults.XMLResultsHandler;
import io.fabric.sdk.android.services.common.ApiKey;
import io.fabric.sdk.android.services.common.IdManager;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
final class SessionDataWriter {
    private static final ByteString SIGNAL_DEFAULT_BYTE_STRING = ByteString.copyFromUtf8("0");
    final Context context;
    StackTraceElement[] exceptionStack;
    private final int maxChainedExceptionsDepth = 8;
    private final ByteString optionalBuildIdBytes;
    private final ByteString packageNameBytes;
    ActivityManager.RunningAppProcessInfo runningAppProcessInfo;
    List<StackTraceElement[]> stacks;
    Thread[] threads;

    public SessionDataWriter(Context context, String buildId, String packageName) {
        this.context = context;
        this.packageNameBytes = ByteString.copyFromUtf8(packageName);
        this.optionalBuildIdBytes = buildId == null ? null : ByteString.copyFromUtf8(buildId.replace(XMLResultsHandler.SEP_HYPHEN, ""));
    }

    public static void writeBeginSession(CodedOutputStream cos, String sessionId, String generator, long startedAtSeconds) throws Exception {
        cos.writeBytes(1, ByteString.copyFromUtf8(generator));
        cos.writeBytes(2, ByteString.copyFromUtf8(sessionId));
        cos.writeUInt64(3, startedAtSeconds);
    }

    public static void writeSessionDevice(CodedOutputStream cos, String clsDeviceId, int arch, String model, int availableProcessors, long totalRam, long diskSpace, boolean isEmulator, Map<IdManager.DeviceIdentifierType, String> ids, int state, String manufacturer, String modelClass) throws Exception {
        int i;
        ByteString clsDeviceIDBytes = ByteString.copyFromUtf8(clsDeviceId);
        ByteString modelBytes = stringToByteString(model);
        ByteString modelClassBytes = stringToByteString(modelClass);
        ByteString manufacturerBytes = stringToByteString(manufacturer);
        cos.writeTag(9, 2);
        int computeBytesSize = (modelBytes == null ? 0 : CodedOutputStream.computeBytesSize(4, modelBytes)) + CodedOutputStream.computeEnumSize(3, arch) + CodedOutputStream.computeBytesSize(1, clsDeviceIDBytes) + 0 + CodedOutputStream.computeUInt32Size(5, availableProcessors) + CodedOutputStream.computeUInt64Size(6, totalRam) + CodedOutputStream.computeUInt64Size(7, diskSpace) + CodedOutputStream.computeBoolSize$2563259(10);
        if (ids != null) {
            Iterator<Map.Entry<IdManager.DeviceIdentifierType, String>> it = ids.entrySet().iterator();
            while (true) {
                i = computeBytesSize;
                if (!it.hasNext()) {
                    break;
                }
                Map.Entry<IdManager.DeviceIdentifierType, String> next = it.next();
                int deviceIdentifierSize = getDeviceIdentifierSize(next.getKey(), next.getValue());
                computeBytesSize = deviceIdentifierSize + CodedOutputStream.computeTagSize(11) + CodedOutputStream.computeRawVarint32Size(deviceIdentifierSize) + i;
            }
        } else {
            i = computeBytesSize;
        }
        cos.writeRawVarint32((modelClassBytes == null ? 0 : CodedOutputStream.computeBytesSize(14, modelClassBytes)) + i + CodedOutputStream.computeUInt32Size(12, state) + (manufacturerBytes == null ? 0 : CodedOutputStream.computeBytesSize(13, manufacturerBytes)));
        cos.writeBytes(1, clsDeviceIDBytes);
        cos.writeEnum(3, arch);
        cos.writeBytes(4, modelBytes);
        cos.writeUInt32(5, availableProcessors);
        cos.writeUInt64(6, totalRam);
        cos.writeUInt64(7, diskSpace);
        cos.writeBool(10, isEmulator);
        for (Map.Entry<IdManager.DeviceIdentifierType, String> id : ids.entrySet()) {
            cos.writeTag(11, 2);
            cos.writeRawVarint32(getDeviceIdentifierSize(id.getKey(), id.getValue()));
            cos.writeEnum(1, id.getKey().protobufIndex);
            cos.writeBytes(2, ByteString.copyFromUtf8(id.getValue()));
        }
        cos.writeUInt32(12, state);
        if (manufacturerBytes != null) {
            cos.writeBytes(13, manufacturerBytes);
        }
        if (modelClassBytes != null) {
            cos.writeBytes(14, modelClassBytes);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void writeSessionEventAppExecution(CodedOutputStream cos, Thread exceptionThread, Throwable ex) throws Exception {
        cos.writeTag(1, 2);
        cos.writeRawVarint32(getEventAppExecutionSize(exceptionThread, ex));
        writeThread(cos, exceptionThread, this.exceptionStack, 4, true);
        int len = this.threads.length;
        for (int i = 0; i < len; i++) {
            Thread thread = this.threads[i];
            writeThread(cos, thread, this.stacks.get(i), 0, false);
        }
        int i2 = 1;
        int i3 = 2;
        while (true) {
            cos.writeTag(i3, 2);
            cos.writeRawVarint32(getEventAppExecutionExceptionSize(ex, 1));
            cos.writeBytes(1, ByteString.copyFromUtf8(ex.getClass().getName()));
            String localizedMessage = ex.getLocalizedMessage();
            if (localizedMessage != null) {
                cos.writeBytes(3, ByteString.copyFromUtf8(localizedMessage));
            }
            for (StackTraceElement stackTraceElement : ex.getStackTrace()) {
                writeFrame(cos, 4, stackTraceElement, true);
            }
            ex = ex.getCause();
            if (ex == null) {
                break;
            }
            if (i2 < this.maxChainedExceptionsDepth) {
                i2++;
                i3 = 6;
            } else {
                int i4 = 0;
                while (ex != null) {
                    ex = ex.getCause();
                    i4++;
                }
                cos.writeUInt32(7, i4);
            }
        }
        cos.writeTag(3, 2);
        cos.writeRawVarint32(getEventAppExecutionSignalSize());
        cos.writeBytes(1, SIGNAL_DEFAULT_BYTE_STRING);
        cos.writeBytes(2, SIGNAL_DEFAULT_BYTE_STRING);
        cos.writeUInt64(3, 0L);
        cos.writeTag(4, 2);
        cos.writeRawVarint32(getBinaryImageSize());
        cos.writeUInt64(1, 0L);
        cos.writeUInt64(2, 0L);
        cos.writeBytes(3, this.packageNameBytes);
        if (this.optionalBuildIdBytes != null) {
            cos.writeBytes(4, this.optionalBuildIdBytes);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void writeSessionEventAppCustomAttributes(CodedOutputStream cos, Map<String, String> customAttributes) throws Exception {
        for (Map.Entry<String, String> entry : customAttributes.entrySet()) {
            cos.writeTag(2, 2);
            cos.writeRawVarint32(getEventAppCustomAttributeSize(entry.getKey(), entry.getValue()));
            cos.writeBytes(1, ByteString.copyFromUtf8(entry.getKey()));
            String value = entry.getValue();
            if (value == null) {
                value = "";
            }
            cos.writeBytes(2, ByteString.copyFromUtf8(value));
        }
    }

    private static void writeThread(CodedOutputStream cos, Thread thread, StackTraceElement[] stackTraceElements, int importance, boolean isCrashedThread) throws Exception {
        cos.writeTag(1, 2);
        int s = getThreadSize(thread, stackTraceElements, importance, isCrashedThread);
        cos.writeRawVarint32(s);
        cos.writeBytes(1, ByteString.copyFromUtf8(thread.getName()));
        cos.writeUInt32(2, importance);
        for (StackTraceElement stackTraceElement : stackTraceElements) {
            writeFrame(cos, 3, stackTraceElement, isCrashedThread);
        }
    }

    private static void writeFrame(CodedOutputStream cos, int fieldIndex, StackTraceElement element, boolean isCrashedThread) throws Exception {
        cos.writeTag(fieldIndex, 2);
        cos.writeRawVarint32(getFrameSize(element, isCrashedThread));
        if (element.isNativeMethod()) {
            cos.writeUInt64(1, Math.max(element.getLineNumber(), 0));
        } else {
            cos.writeUInt64(1, 0L);
        }
        cos.writeBytes(2, ByteString.copyFromUtf8(element.getClassName() + "." + element.getMethodName()));
        if (element.getFileName() != null) {
            cos.writeBytes(3, ByteString.copyFromUtf8(element.getFileName()));
        }
        if (!element.isNativeMethod() && element.getLineNumber() > 0) {
            cos.writeUInt64(4, element.getLineNumber());
        }
        cos.writeUInt32(5, isCrashedThread ? 4 : 0);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final int getSessionAppOrgSize() {
        new ApiKey();
        return CodedOutputStream.computeBytesSize(1, ByteString.copyFromUtf8(ApiKey.getValue(this.context))) + 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int getSessionOSSize$3c0313b2(ByteString release, ByteString codeName) {
        return CodedOutputStream.computeEnumSize(1, 3) + 0 + CodedOutputStream.computeBytesSize(2, release) + CodedOutputStream.computeBytesSize(3, codeName) + CodedOutputStream.computeBoolSize$2563259(4);
    }

    private static int getDeviceIdentifierSize(IdManager.DeviceIdentifierType type, String value) {
        return CodedOutputStream.computeEnumSize(1, type.protobufIndex) + CodedOutputStream.computeBytesSize(2, ByteString.copyFromUtf8(value));
    }

    private int getBinaryImageSize() {
        int size = CodedOutputStream.computeUInt64Size(1, 0L) + 0 + CodedOutputStream.computeUInt64Size(2, 0L) + CodedOutputStream.computeBytesSize(3, this.packageNameBytes);
        if (this.optionalBuildIdBytes != null) {
            return size + CodedOutputStream.computeBytesSize(4, this.optionalBuildIdBytes);
        }
        return size;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final int getEventAppSize(Thread thread, Throwable ex, int orientation, Map<String, String> customAttributes) {
        int executionSize = getEventAppExecutionSize(thread, ex);
        int size = CodedOutputStream.computeTagSize(1) + CodedOutputStream.computeRawVarint32Size(executionSize) + executionSize + 0;
        if (customAttributes != null) {
            for (Map.Entry<String, String> entry : customAttributes.entrySet()) {
                int entrySize = getEventAppCustomAttributeSize(entry.getKey(), entry.getValue());
                size += CodedOutputStream.computeTagSize(2) + CodedOutputStream.computeRawVarint32Size(entrySize) + entrySize;
            }
        }
        if (this.runningAppProcessInfo != null) {
            int i = this.runningAppProcessInfo.importance;
            size += CodedOutputStream.computeBoolSize$2563259(3);
        }
        return CodedOutputStream.computeUInt32Size(4, orientation) + size;
    }

    private int getEventAppExecutionSize(Thread exceptionThread, Throwable ex) {
        int threadSize = getThreadSize(exceptionThread, this.exceptionStack, 4, true);
        int size = CodedOutputStream.computeTagSize(1) + CodedOutputStream.computeRawVarint32Size(threadSize) + threadSize + 0;
        int len = this.threads.length;
        for (int i = 0; i < len; i++) {
            int threadSize2 = getThreadSize(this.threads[i], this.stacks.get(i), 0, false);
            size += CodedOutputStream.computeTagSize(1) + CodedOutputStream.computeRawVarint32Size(threadSize2) + threadSize2;
        }
        int exceptionSize = getEventAppExecutionExceptionSize(ex, 1);
        int size2 = size + CodedOutputStream.computeTagSize(2) + CodedOutputStream.computeRawVarint32Size(exceptionSize) + exceptionSize;
        int signalSize = getEventAppExecutionSignalSize();
        int size3 = size2 + CodedOutputStream.computeTagSize(3) + CodedOutputStream.computeRawVarint32Size(signalSize) + signalSize;
        int binaryImageSize = getBinaryImageSize();
        return CodedOutputStream.computeTagSize(3) + CodedOutputStream.computeRawVarint32Size(binaryImageSize) + binaryImageSize + size3;
    }

    private static int getEventAppCustomAttributeSize(String key, String value) {
        int computeBytesSize = CodedOutputStream.computeBytesSize(1, ByteString.copyFromUtf8(key));
        if (value == null) {
            value = "";
        }
        return computeBytesSize + CodedOutputStream.computeBytesSize(2, ByteString.copyFromUtf8(value));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int getEventDeviceSize$45a61cda(int batterVelocity, int orientation, long heapAllocatedSize, long diskUsed) {
        return CodedOutputStream.computeFloatSize$255e745() + 0 + CodedOutputStream.computeSInt32Size$255f288(batterVelocity) + CodedOutputStream.computeBoolSize$2563259(3) + CodedOutputStream.computeUInt32Size(4, orientation) + CodedOutputStream.computeUInt64Size(5, heapAllocatedSize) + CodedOutputStream.computeUInt64Size(6, diskUsed);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int getEventLogSize(ByteString log) {
        return CodedOutputStream.computeBytesSize(1, log);
    }

    private int getEventAppExecutionExceptionSize(Throwable ex, int chainDepth) {
        int size = CodedOutputStream.computeBytesSize(1, ByteString.copyFromUtf8(ex.getClass().getName())) + 0;
        String message = ex.getLocalizedMessage();
        if (message != null) {
            size += CodedOutputStream.computeBytesSize(3, ByteString.copyFromUtf8(message));
        }
        StackTraceElement[] arr$ = ex.getStackTrace();
        for (StackTraceElement stackTraceElement : arr$) {
            int frameSize = getFrameSize(stackTraceElement, true);
            size += CodedOutputStream.computeTagSize(4) + CodedOutputStream.computeRawVarint32Size(frameSize) + frameSize;
        }
        Throwable cause = ex.getCause();
        if (cause != null) {
            if (chainDepth < this.maxChainedExceptionsDepth) {
                int exceptionSize = getEventAppExecutionExceptionSize(cause, chainDepth + 1);
                return size + CodedOutputStream.computeTagSize(6) + CodedOutputStream.computeRawVarint32Size(exceptionSize) + exceptionSize;
            }
            int overflowCount = 0;
            while (cause != null) {
                cause = cause.getCause();
                overflowCount++;
            }
            return size + CodedOutputStream.computeUInt32Size(7, overflowCount);
        }
        return size;
    }

    private static int getEventAppExecutionSignalSize() {
        return CodedOutputStream.computeBytesSize(1, SIGNAL_DEFAULT_BYTE_STRING) + 0 + CodedOutputStream.computeBytesSize(2, SIGNAL_DEFAULT_BYTE_STRING) + CodedOutputStream.computeUInt64Size(3, 0L);
    }

    private static int getFrameSize(StackTraceElement element, boolean isCrashedThread) {
        int size;
        if (element.isNativeMethod()) {
            size = CodedOutputStream.computeUInt64Size(1, Math.max(element.getLineNumber(), 0)) + 0;
        } else {
            size = CodedOutputStream.computeUInt64Size(1, 0L) + 0;
        }
        int size2 = size + CodedOutputStream.computeBytesSize(2, ByteString.copyFromUtf8(element.getClassName() + "." + element.getMethodName()));
        if (element.getFileName() != null) {
            size2 += CodedOutputStream.computeBytesSize(3, ByteString.copyFromUtf8(element.getFileName()));
        }
        if (!element.isNativeMethod() && element.getLineNumber() > 0) {
            size2 += CodedOutputStream.computeUInt64Size(4, element.getLineNumber());
        }
        return CodedOutputStream.computeUInt32Size(5, isCrashedThread ? 2 : 0) + size2;
    }

    private static int getThreadSize(Thread thread, StackTraceElement[] stackTraceElements, int importance, boolean isCrashedThread) {
        int size = CodedOutputStream.computeBytesSize(1, ByteString.copyFromUtf8(thread.getName())) + CodedOutputStream.computeUInt32Size(2, importance);
        for (StackTraceElement stackTraceElement : stackTraceElements) {
            int frameSize = getFrameSize(stackTraceElement, isCrashedThread);
            size += CodedOutputStream.computeTagSize(3) + CodedOutputStream.computeRawVarint32Size(frameSize) + frameSize;
        }
        return size;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ByteString stringToByteString(String s) {
        if (s == null) {
            return null;
        }
        return ByteString.copyFromUtf8(s);
    }
}
