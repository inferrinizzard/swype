package com.crashlytics.android;

import com.crashlytics.android.internal.models.BinaryImageData;
import com.crashlytics.android.internal.models.CustomAttributeData;
import com.crashlytics.android.internal.models.SessionEventData;
import com.crashlytics.android.internal.models.SignalData;
import com.crashlytics.android.internal.models.ThreadData;
import java.io.IOException;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class NativeCrashWriter {
    private static final SignalData DEFAULT_SIGNAL = new SignalData("", "");
    private static final ProtobufMessage[] EMPTY_CHILDREN = new ProtobufMessage[0];
    private static final ThreadMessage[] EMPTY_THREAD_MESSAGES = new ThreadMessage[0];
    private static final FrameMessage[] EMPTY_FRAME_MESSAGES = new FrameMessage[0];
    private static final BinaryImageMessage[] EMPTY_BINARY_IMAGE_MESSAGES = new BinaryImageMessage[0];
    private static final CustomAttributeMessage[] EMPTY_CUSTOM_ATTRIBUTE_MESSAGES = new CustomAttributeMessage[0];

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static abstract class ProtobufMessage {
        private final ProtobufMessage[] children;
        private final int tag;

        public ProtobufMessage(int tag, ProtobufMessage... children) {
            this.tag = tag;
            this.children = children;
        }

        public int getSize() {
            int sizeNoTag = getSizeNoTag();
            return sizeNoTag + CodedOutputStream.computeRawVarint32Size(sizeNoTag) + CodedOutputStream.computeTagSize(this.tag);
        }

        private int getSizeNoTag() {
            int size = getPropertiesSize();
            ProtobufMessage[] arr$ = this.children;
            for (ProtobufMessage child : arr$) {
                size += child.getSize();
            }
            return size;
        }

        public void write(CodedOutputStream cos) throws IOException {
            cos.writeTag(this.tag, 2);
            cos.writeRawVarint32(getSizeNoTag());
            writeProperties(cos);
            ProtobufMessage[] arr$ = this.children;
            for (ProtobufMessage protobufMessage : arr$) {
                protobufMessage.write(cos);
            }
        }

        public int getPropertiesSize() {
            return 0;
        }

        public void writeProperties(CodedOutputStream cos) throws IOException {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class RepeatedMessage extends ProtobufMessage {
        private final ProtobufMessage[] messages;

        public RepeatedMessage(ProtobufMessage... messages) {
            super(0, new ProtobufMessage[0]);
            this.messages = messages;
        }

        @Override // com.crashlytics.android.NativeCrashWriter.ProtobufMessage
        public final void write(CodedOutputStream cos) throws IOException {
            ProtobufMessage[] arr$ = this.messages;
            for (ProtobufMessage protobufMessage : arr$) {
                protobufMessage.write(cos);
            }
        }

        @Override // com.crashlytics.android.NativeCrashWriter.ProtobufMessage
        public final int getSize() {
            int size = 0;
            ProtobufMessage[] arr$ = this.messages;
            for (ProtobufMessage message : arr$) {
                size += message.getSize();
            }
            return size;
        }
    }

    /* loaded from: classes.dex */
    private static final class EventMessage extends ProtobufMessage {
        private final String crashType;
        private final long time;

        public EventMessage(long time, String crashType, ApplicationMessage applicationMessage, DeviceMessage device) {
            super(10, applicationMessage, device);
            this.time = time;
            this.crashType = crashType;
        }

        @Override // com.crashlytics.android.NativeCrashWriter.ProtobufMessage
        public final int getPropertiesSize() {
            int timeSize = CodedOutputStream.computeUInt64Size(1, this.time);
            int typeSize = CodedOutputStream.computeBytesSize(2, ByteString.copyFromUtf8(this.crashType));
            return timeSize + typeSize;
        }

        @Override // com.crashlytics.android.NativeCrashWriter.ProtobufMessage
        public final void writeProperties(CodedOutputStream cos) throws IOException {
            cos.writeUInt64(1, this.time);
            cos.writeBytes(2, ByteString.copyFromUtf8(this.crashType));
        }
    }

    /* loaded from: classes.dex */
    private static final class DeviceMessage extends ProtobufMessage {
        public DeviceMessage() {
            super(5, new ProtobufMessage[0]);
        }

        @Override // com.crashlytics.android.NativeCrashWriter.ProtobufMessage
        public final int getPropertiesSize() {
            return CodedOutputStream.computeFloatSize$255e745() + 0 + CodedOutputStream.computeInt32Size$255f288() + CodedOutputStream.computeBoolSize$2563259(3) + CodedOutputStream.computeUInt32Size(4, 0) + CodedOutputStream.computeUInt64Size(5, 0L) + CodedOutputStream.computeUInt64Size(6, 0L);
        }

        @Override // com.crashlytics.android.NativeCrashWriter.ProtobufMessage
        public final void writeProperties(CodedOutputStream cos) throws IOException {
            cos.writeFloat$255e752(0.0f);
            cos.writeTag(2, 0);
            cos.writeInt32NoTag(0);
            cos.writeBool(3, false);
            cos.writeUInt32(4, 0);
            cos.writeUInt64(5, 0L);
            cos.writeUInt64(6, 0L);
        }
    }

    /* loaded from: classes.dex */
    private static final class ApplicationMessage extends ProtobufMessage {
        public ApplicationMessage(ExecutionMessage executionMessage, RepeatedMessage customAttrs) {
            super(3, executionMessage, customAttrs);
        }
    }

    /* loaded from: classes.dex */
    private static final class ExecutionMessage extends ProtobufMessage {
        public ExecutionMessage(SignalMessage signalMessage, RepeatedMessage threads, RepeatedMessage binaryImages) {
            super(1, threads, signalMessage, binaryImages);
        }
    }

    /* loaded from: classes.dex */
    private static final class ThreadMessage extends ProtobufMessage {
        private final int importance;

        public ThreadMessage(ThreadData threadData, RepeatedMessage frames) {
            super(1, frames);
            this.importance = threadData.importance;
        }

        @Override // com.crashlytics.android.NativeCrashWriter.ProtobufMessage
        public final int getPropertiesSize() {
            return CodedOutputStream.computeUInt32Size(2, this.importance);
        }

        @Override // com.crashlytics.android.NativeCrashWriter.ProtobufMessage
        public final void writeProperties(CodedOutputStream cos) throws IOException {
            cos.writeUInt32(2, this.importance);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class FrameMessage extends ProtobufMessage {
        private final long address;
        private final String file;
        private final int importance;
        private final long offset;
        private final String symbol;

        public FrameMessage(ThreadData.FrameData frameData) {
            super(3, new ProtobufMessage[0]);
            this.address = frameData.address;
            this.symbol = frameData.symbol;
            this.file = frameData.file;
            this.offset = frameData.offset;
            this.importance = frameData.importance;
        }

        @Override // com.crashlytics.android.NativeCrashWriter.ProtobufMessage
        public final int getPropertiesSize() {
            return CodedOutputStream.computeUInt64Size(1, this.address) + CodedOutputStream.computeBytesSize(2, ByteString.copyFromUtf8(this.symbol)) + CodedOutputStream.computeBytesSize(3, ByteString.copyFromUtf8(this.file)) + CodedOutputStream.computeUInt64Size(4, this.offset) + CodedOutputStream.computeUInt32Size(5, this.importance);
        }

        @Override // com.crashlytics.android.NativeCrashWriter.ProtobufMessage
        public final void writeProperties(CodedOutputStream cos) throws IOException {
            cos.writeUInt64(1, this.address);
            cos.writeBytes(2, ByteString.copyFromUtf8(this.symbol));
            cos.writeBytes(3, ByteString.copyFromUtf8(this.file));
            cos.writeUInt64(4, this.offset);
            cos.writeUInt32(5, this.importance);
        }
    }

    /* loaded from: classes.dex */
    private static final class SignalMessage extends ProtobufMessage {
        private final long sigAddr;
        private final String sigCode;
        private final String sigName;

        public SignalMessage(SignalData signalData) {
            super(3, new ProtobufMessage[0]);
            this.sigName = signalData.name;
            this.sigCode = signalData.code;
            this.sigAddr = signalData.faultAddress;
        }

        @Override // com.crashlytics.android.NativeCrashWriter.ProtobufMessage
        public final int getPropertiesSize() {
            return CodedOutputStream.computeBytesSize(1, ByteString.copyFromUtf8(this.sigName)) + CodedOutputStream.computeBytesSize(2, ByteString.copyFromUtf8(this.sigCode)) + CodedOutputStream.computeUInt64Size(3, this.sigAddr);
        }

        @Override // com.crashlytics.android.NativeCrashWriter.ProtobufMessage
        public final void writeProperties(CodedOutputStream cos) throws IOException {
            cos.writeBytes(1, ByteString.copyFromUtf8(this.sigName));
            cos.writeBytes(2, ByteString.copyFromUtf8(this.sigCode));
            cos.writeUInt64(3, this.sigAddr);
        }
    }

    /* loaded from: classes.dex */
    private static final class BinaryImageMessage extends ProtobufMessage {
        private final long baseAddr;
        private final String filePath;
        private final long imageSize;
        private final String uuid;

        public BinaryImageMessage(BinaryImageData binaryImageData) {
            super(4, new ProtobufMessage[0]);
            this.baseAddr = binaryImageData.baseAddress;
            this.imageSize = binaryImageData.size;
            this.filePath = binaryImageData.path;
            this.uuid = binaryImageData.id;
        }

        @Override // com.crashlytics.android.NativeCrashWriter.ProtobufMessage
        public final int getPropertiesSize() {
            int addrSize = CodedOutputStream.computeUInt64Size(1, this.baseAddr);
            int imgSize = CodedOutputStream.computeUInt64Size(2, this.imageSize);
            int pathSize = CodedOutputStream.computeBytesSize(3, ByteString.copyFromUtf8(this.filePath));
            int uuidSize = CodedOutputStream.computeBytesSize(4, ByteString.copyFromUtf8(this.uuid));
            return pathSize + addrSize + imgSize + uuidSize;
        }

        @Override // com.crashlytics.android.NativeCrashWriter.ProtobufMessage
        public final void writeProperties(CodedOutputStream cos) throws IOException {
            cos.writeUInt64(1, this.baseAddr);
            cos.writeUInt64(2, this.imageSize);
            cos.writeBytes(3, ByteString.copyFromUtf8(this.filePath));
            cos.writeBytes(4, ByteString.copyFromUtf8(this.uuid));
        }
    }

    /* loaded from: classes.dex */
    private static final class CustomAttributeMessage extends ProtobufMessage {
        private final String key;
        private final String value;

        public CustomAttributeMessage(CustomAttributeData customAttributeData) {
            super(2, new ProtobufMessage[0]);
            this.key = customAttributeData.key;
            this.value = customAttributeData.value;
        }

        @Override // com.crashlytics.android.NativeCrashWriter.ProtobufMessage
        public final int getPropertiesSize() {
            return CodedOutputStream.computeBytesSize(2, ByteString.copyFromUtf8(this.value == null ? "" : this.value)) + CodedOutputStream.computeBytesSize(1, ByteString.copyFromUtf8(this.key));
        }

        @Override // com.crashlytics.android.NativeCrashWriter.ProtobufMessage
        public final void writeProperties(CodedOutputStream cos) throws IOException {
            cos.writeBytes(1, ByteString.copyFromUtf8(this.key));
            cos.writeBytes(2, ByteString.copyFromUtf8(this.value == null ? "" : this.value));
        }
    }

    private static RepeatedMessage createFramesMessage(ThreadData.FrameData[] frames) {
        FrameMessage[] frameMessages = frames != null ? new FrameMessage[frames.length] : EMPTY_FRAME_MESSAGES;
        for (int frameIdx = 0; frameIdx < frameMessages.length; frameIdx++) {
            frameMessages[frameIdx] = new FrameMessage(frames[frameIdx]);
        }
        return new RepeatedMessage(frameMessages);
    }

    public static void writeNativeCrash(SessionEventData crashEventData, CodedOutputStream cos) throws IOException {
        SignalMessage signalMessage = new SignalMessage(crashEventData.signal != null ? crashEventData.signal : DEFAULT_SIGNAL);
        ThreadData[] threadDataArr = crashEventData.threads;
        ThreadMessage[] threadMessageArr = threadDataArr != null ? new ThreadMessage[threadDataArr.length] : EMPTY_THREAD_MESSAGES;
        for (int i = 0; i < threadMessageArr.length; i++) {
            ThreadData threadData = threadDataArr[i];
            threadMessageArr[i] = new ThreadMessage(threadData, createFramesMessage(threadData.frames));
        }
        RepeatedMessage repeatedMessage = new RepeatedMessage(threadMessageArr);
        BinaryImageData[] binaryImageDataArr = crashEventData.binaryImages;
        BinaryImageMessage[] binaryImageMessageArr = binaryImageDataArr != null ? new BinaryImageMessage[binaryImageDataArr.length] : EMPTY_BINARY_IMAGE_MESSAGES;
        for (int i2 = 0; i2 < binaryImageMessageArr.length; i2++) {
            binaryImageMessageArr[i2] = new BinaryImageMessage(binaryImageDataArr[i2]);
        }
        ExecutionMessage executionMessage = new ExecutionMessage(signalMessage, repeatedMessage, new RepeatedMessage(binaryImageMessageArr));
        CustomAttributeData[] customAttributeDataArr = crashEventData.customAttributes;
        CustomAttributeMessage[] customAttributeMessageArr = customAttributeDataArr != null ? new CustomAttributeMessage[customAttributeDataArr.length] : EMPTY_CUSTOM_ATTRIBUTE_MESSAGES;
        for (int i3 = 0; i3 < customAttributeMessageArr.length; i3++) {
            customAttributeMessageArr[i3] = new CustomAttributeMessage(customAttributeDataArr[i3]);
        }
        new EventMessage(crashEventData.timestamp, "ndk-crash", new ApplicationMessage(executionMessage, new RepeatedMessage(customAttributeMessageArr)), new DeviceMessage()).write(cos);
    }
}
