package com.crashlytics.android;

import com.nuance.swype.input.hardkey.HardKeyboardManager;
import java.io.ByteArrayInputStream;
import java.io.Flushable;
import java.io.IOException;
import java.io.OutputStream;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class CodedOutputStream implements Flushable {
    private final byte[] buffer;
    private final OutputStream output;
    private int position = 0;
    private final int limit = HardKeyboardManager.META_CTRL_ON;

    private CodedOutputStream(OutputStream output, byte[] buffer) {
        this.output = output;
        this.buffer = buffer;
    }

    public final void writeFloat$255e752(float value) throws IOException {
        writeTag(1, 5);
        int floatToRawIntBits = Float.floatToRawIntBits(value);
        writeRawByte(floatToRawIntBits & 255);
        writeRawByte((floatToRawIntBits >> 8) & 255);
        writeRawByte((floatToRawIntBits >> 16) & 255);
        writeRawByte((floatToRawIntBits >> 24) & 255);
    }

    public final void writeUInt64(int fieldNumber, long value) throws IOException {
        writeTag(fieldNumber, 0);
        writeRawVarint64(value);
    }

    public final void writeBool(int fieldNumber, boolean value) throws IOException {
        writeTag(fieldNumber, 0);
        writeRawByte(value ? 1 : 0);
    }

    public final void writeString$4f708078(String value) throws IOException {
        writeTag(1, 2);
        byte[] bytes = value.getBytes("UTF-8");
        writeRawVarint32(bytes.length);
        writeRawBytes(bytes);
    }

    public final void writeBytes(int fieldNumber, ByteString value) throws IOException {
        writeTag(fieldNumber, 2);
        writeRawVarint32(value.bytes.length);
        int length = value.bytes.length;
        if (this.limit - this.position >= length) {
            value.copyTo(this.buffer, 0, this.position, length);
            this.position = length + this.position;
            return;
        }
        int i = this.limit - this.position;
        value.copyTo(this.buffer, 0, this.position, i);
        int i2 = i + 0;
        int i3 = length - i;
        this.position = this.limit;
        refreshBuffer();
        if (i3 <= this.limit) {
            value.copyTo(this.buffer, i2, 0, i3);
            this.position = i3;
            return;
        }
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(value.bytes);
        if (i2 != byteArrayInputStream.skip(i2)) {
            throw new IllegalStateException("Skip failed.");
        }
        while (i3 > 0) {
            int min = Math.min(i3, this.limit);
            int read = byteArrayInputStream.read(this.buffer, 0, min);
            if (read != min) {
                throw new IllegalStateException("Read failed.");
            }
            this.output.write(this.buffer, 0, read);
            i3 -= read;
        }
    }

    public final void writeUInt32(int fieldNumber, int value) throws IOException {
        writeTag(fieldNumber, 0);
        writeRawVarint32(value);
    }

    public final void writeEnum(int fieldNumber, int value) throws IOException {
        writeTag(fieldNumber, 0);
        writeInt32NoTag(value);
    }

    public final void writeSInt32$255f295(int value) throws IOException {
        writeTag(2, 0);
        writeRawVarint32(encodeZigZag32(value));
    }

    public final void writeInt32NoTag(int value) throws IOException {
        if (value >= 0) {
            writeRawVarint32(value);
        } else {
            writeRawVarint64(value);
        }
    }

    public static int computeFloatSize$255e745() {
        return computeTagSize(1) + 4;
    }

    public static int computeUInt64Size(int fieldNumber, long value) {
        int i;
        int computeTagSize = computeTagSize(fieldNumber);
        if (((-128) & value) == 0) {
            i = 1;
        } else if (((-16384) & value) == 0) {
            i = 2;
        } else if (((-2097152) & value) == 0) {
            i = 3;
        } else if (((-268435456) & value) == 0) {
            i = 4;
        } else if (((-34359738368L) & value) == 0) {
            i = 5;
        } else if (((-4398046511104L) & value) == 0) {
            i = 6;
        } else if (((-562949953421312L) & value) == 0) {
            i = 7;
        } else if (((-72057594037927936L) & value) == 0) {
            i = 8;
        } else {
            i = (Long.MIN_VALUE & value) == 0 ? 9 : 10;
        }
        return i + computeTagSize;
    }

    public static int computeInt32Size$255f288() {
        return computeTagSize(2) + computeInt32SizeNoTag(0);
    }

    public static int computeBoolSize$2563259(int fieldNumber) {
        return computeTagSize(fieldNumber) + 1;
    }

    public static int computeBytesSize(int fieldNumber, ByteString value) {
        return computeTagSize(fieldNumber) + computeRawVarint32Size(value.bytes.length) + value.bytes.length;
    }

    public static int computeUInt32Size(int fieldNumber, int value) {
        return computeTagSize(fieldNumber) + computeRawVarint32Size(value);
    }

    public static int computeEnumSize(int fieldNumber, int value) {
        return computeTagSize(fieldNumber) + computeInt32SizeNoTag(value);
    }

    public static int computeSInt32Size$255f288(int value) {
        return computeTagSize(2) + computeRawVarint32Size(encodeZigZag32(value));
    }

    private static int computeInt32SizeNoTag(int value) {
        if (value >= 0) {
            return computeRawVarint32Size(value);
        }
        return 10;
    }

    private void refreshBuffer() throws IOException {
        if (this.output == null) {
            throw new OutOfSpaceException();
        }
        this.output.write(this.buffer, 0, this.position);
        this.position = 0;
    }

    @Override // java.io.Flushable
    public final void flush() throws IOException {
        if (this.output != null) {
            refreshBuffer();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class OutOfSpaceException extends IOException {
        OutOfSpaceException() {
            super("CodedOutputStream was writing to a flat byte array and ran out of space.");
        }
    }

    private void writeRawByte(int value) throws IOException {
        byte b = (byte) value;
        if (this.position == this.limit) {
            refreshBuffer();
        }
        byte[] bArr = this.buffer;
        int i = this.position;
        this.position = i + 1;
        bArr[i] = b;
    }

    public final void writeRawBytes(byte[] value) throws IOException {
        int length = value.length;
        if (this.limit - this.position >= length) {
            System.arraycopy(value, 0, this.buffer, this.position, length);
            this.position = length + this.position;
            return;
        }
        int i = this.limit - this.position;
        System.arraycopy(value, 0, this.buffer, this.position, i);
        int i2 = i + 0;
        int i3 = length - i;
        this.position = this.limit;
        refreshBuffer();
        if (i3 <= this.limit) {
            System.arraycopy(value, i2, this.buffer, 0, i3);
            this.position = i3;
        } else {
            this.output.write(value, i2, i3);
        }
    }

    public final void writeTag(int fieldNumber, int wireType) throws IOException {
        writeRawVarint32(WireFormat.makeTag(fieldNumber, wireType));
    }

    public static int computeTagSize(int fieldNumber) {
        return computeRawVarint32Size(WireFormat.makeTag(fieldNumber, 0));
    }

    public final void writeRawVarint32(int value) throws IOException {
        while ((value & (-128)) != 0) {
            writeRawByte((value & 127) | 128);
            value >>>= 7;
        }
        writeRawByte(value);
    }

    public static int computeRawVarint32Size(int value) {
        if ((value & (-128)) == 0) {
            return 1;
        }
        if ((value & (-16384)) == 0) {
            return 2;
        }
        if (((-2097152) & value) == 0) {
            return 3;
        }
        return ((-268435456) & value) == 0 ? 4 : 5;
    }

    private void writeRawVarint64(long value) throws IOException {
        while (((-128) & value) != 0) {
            writeRawByte((((int) value) & 127) | 128);
            value >>>= 7;
        }
        writeRawByte((int) value);
    }

    private static int encodeZigZag32(int n) {
        return (n << 1) ^ (n >> 31);
    }

    public static CodedOutputStream newInstance(OutputStream output) {
        return new CodedOutputStream(output, new byte[HardKeyboardManager.META_CTRL_ON]);
    }
}
