package io.fabric.sdk.android.services.common;

import com.nuance.swype.input.hardkey.HardKeyboardManager;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

/* loaded from: classes.dex */
public class QueueFile implements Closeable {
    private static final Logger LOGGER = Logger.getLogger(QueueFile.class.getName());
    private final byte[] buffer = new byte[16];
    private int elementCount;
    int fileLength;
    private Element first;
    private Element last;
    private final RandomAccessFile raf;

    /* loaded from: classes.dex */
    public interface ElementReader {
        void read(InputStream inputStream, int i) throws IOException;
    }

    public QueueFile(File file) throws IOException {
        if (!file.exists()) {
            File file2 = new File(file.getPath() + ".tmp");
            RandomAccessFile open = open(file2);
            try {
                open.setLength(4096L);
                open.seek(0L);
                byte[] bArr = new byte[16];
                writeInts(bArr, HardKeyboardManager.META_CTRL_ON, 0, 0, 0);
                open.write(bArr);
                open.close();
                if (!file2.renameTo(file)) {
                    throw new IOException("Rename failed!");
                }
            } catch (Throwable th) {
                open.close();
                throw th;
            }
        }
        this.raf = open(file);
        this.raf.seek(0L);
        this.raf.readFully(this.buffer);
        this.fileLength = readInt(this.buffer, 0);
        if (this.fileLength > this.raf.length()) {
            throw new IOException("File is truncated. Expected length: " + this.fileLength + ", Actual length: " + this.raf.length());
        }
        this.elementCount = readInt(this.buffer, 4);
        int readInt = readInt(this.buffer, 8);
        int readInt2 = readInt(this.buffer, 12);
        this.first = readElement(readInt);
        this.last = readElement(readInt2);
    }

    private static void writeInt(byte[] buffer, int offset, int value) {
        buffer[offset] = (byte) (value >> 24);
        buffer[offset + 1] = (byte) (value >> 16);
        buffer[offset + 2] = (byte) (value >> 8);
        buffer[offset + 3] = (byte) value;
    }

    private static void writeInts(byte[] buffer, int... values) {
        int offset = 0;
        for (int i$ = 0; i$ < 4; i$++) {
            int value = values[i$];
            writeInt(buffer, offset, value);
            offset += 4;
        }
    }

    private static int readInt(byte[] buffer, int offset) {
        return ((buffer[offset] & 255) << 24) + ((buffer[offset + 1] & 255) << 16) + ((buffer[offset + 2] & 255) << 8) + (buffer[offset + 3] & 255);
    }

    private void writeHeader(int fileLength, int elementCount, int firstPosition, int lastPosition) throws IOException {
        writeInts(this.buffer, fileLength, elementCount, firstPosition, lastPosition);
        this.raf.seek(0L);
        this.raf.write(this.buffer);
    }

    private Element readElement(int position) throws IOException {
        if (position == 0) {
            return Element.NULL;
        }
        this.raf.seek(position);
        return new Element(position, this.raf.readInt());
    }

    private static RandomAccessFile open(File file) throws FileNotFoundException {
        return new RandomAccessFile(file, "rwd");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int wrapPosition(int position) {
        return position < this.fileLength ? position : (position + 16) - this.fileLength;
    }

    private void ringWrite$101cc16b(int position, byte[] buffer, int count) throws IOException {
        int position2 = wrapPosition(position);
        if (position2 + count <= this.fileLength) {
            this.raf.seek(position2);
            this.raf.write(buffer, 0, count);
            return;
        }
        int beforeEof = this.fileLength - position2;
        this.raf.seek(position2);
        this.raf.write(buffer, 0, beforeEof);
        this.raf.seek(16L);
        this.raf.write(buffer, beforeEof + 0, count - beforeEof);
    }

    public final synchronized void add$1cf967a4(byte[] data, int count) throws IOException {
        nonNull(data, "buffer");
        if ((count | 0) < 0 || count > data.length + 0) {
            throw new IndexOutOfBoundsException();
        }
        expandIfNecessary(count);
        boolean wasEmpty = isEmpty();
        int position = wasEmpty ? 16 : wrapPosition(this.last.position + 4 + this.last.length);
        Element newLast = new Element(position, count);
        writeInt(this.buffer, 0, count);
        ringWrite$101cc16b(newLast.position, this.buffer, 4);
        ringWrite$101cc16b(newLast.position + 4, data, count);
        int firstPosition = wasEmpty ? newLast.position : this.first.position;
        writeHeader(this.fileLength, this.elementCount + 1, firstPosition, newLast.position);
        this.last = newLast;
        this.elementCount++;
        if (wasEmpty) {
            this.first = this.last;
        }
    }

    public final int usedBytes() {
        if (this.elementCount == 0) {
            return 16;
        }
        if (this.last.position >= this.first.position) {
            return (this.last.position - this.first.position) + 4 + this.last.length + 16;
        }
        return (((this.last.position + 4) + this.last.length) + this.fileLength) - this.first.position;
    }

    public final synchronized boolean isEmpty() {
        return this.elementCount == 0;
    }

    private void expandIfNecessary(int dataLength) throws IOException {
        int newLength;
        int elementLength = dataLength + 4;
        int remainingBytes = this.fileLength - usedBytes();
        if (remainingBytes < elementLength) {
            int previousLength = this.fileLength;
            do {
                remainingBytes += previousLength;
                newLength = previousLength << 1;
                previousLength = newLength;
            } while (remainingBytes < elementLength);
            this.raf.setLength(newLength);
            this.raf.getChannel().force(true);
            int endOfLastElement = wrapPosition(this.last.position + 4 + this.last.length);
            if (endOfLastElement < this.first.position) {
                FileChannel channel = this.raf.getChannel();
                channel.position(this.fileLength);
                int count = endOfLastElement - 4;
                if (channel.transferTo(16L, count, channel) != count) {
                    throw new AssertionError("Copied insufficient number of bytes!");
                }
            }
            if (this.last.position < this.first.position) {
                int newLastPosition = (this.fileLength + this.last.position) - 16;
                writeHeader(newLength, this.elementCount, this.first.position, newLastPosition);
                this.last = new Element(newLastPosition, this.last.length);
            } else {
                writeHeader(newLength, this.elementCount, this.first.position, this.last.position);
            }
            this.fileLength = newLength;
        }
    }

    public final synchronized void forEach(ElementReader reader) throws IOException {
        int position = this.first.position;
        for (int i = 0; i < this.elementCount; i++) {
            Element current = readElement(position);
            reader.read(new ElementInputStream(this, current, (byte) 0), current.length);
            position = wrapPosition(current.position + 4 + current.length);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static <T> T nonNull(T t, String name) {
        if (t == null) {
            throw new NullPointerException(name);
        }
        return t;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public final class ElementInputStream extends InputStream {
        private int position;
        private int remaining;

        /* synthetic */ ElementInputStream(QueueFile x0, Element x1, byte b) {
            this(x1);
        }

        private ElementInputStream(Element element) {
            this.position = QueueFile.this.wrapPosition(element.position + 4);
            this.remaining = element.length;
        }

        @Override // java.io.InputStream
        public final int read(byte[] buffer, int offset, int length) throws IOException {
            QueueFile.nonNull(buffer, "buffer");
            if ((offset | length) < 0 || length > buffer.length - offset) {
                throw new ArrayIndexOutOfBoundsException();
            }
            if (this.remaining <= 0) {
                return -1;
            }
            if (length > this.remaining) {
                length = this.remaining;
            }
            QueueFile.access$300(QueueFile.this, this.position, buffer, offset, length);
            this.position = QueueFile.this.wrapPosition(this.position + length);
            this.remaining -= length;
            return length;
        }

        @Override // java.io.InputStream
        public final int read() throws IOException {
            if (this.remaining != 0) {
                QueueFile.this.raf.seek(this.position);
                int read = QueueFile.this.raf.read();
                this.position = QueueFile.this.wrapPosition(this.position + 1);
                this.remaining--;
                return read;
            }
            return -1;
        }
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public synchronized void close() throws IOException {
        this.raf.close();
    }

    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(getClass().getSimpleName()).append('[');
        builder.append("fileLength=").append(this.fileLength);
        builder.append(", size=").append(this.elementCount);
        builder.append(", first=").append(this.first);
        builder.append(", last=").append(this.last);
        builder.append(", element lengths=[");
        try {
            forEach(new ElementReader() { // from class: io.fabric.sdk.android.services.common.QueueFile.1
                boolean first = true;

                @Override // io.fabric.sdk.android.services.common.QueueFile.ElementReader
                public final void read(InputStream in, int length) throws IOException {
                    if (this.first) {
                        this.first = false;
                    } else {
                        builder.append(", ");
                    }
                    builder.append(length);
                }
            });
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "read error", (Throwable) e);
        }
        builder.append("]]");
        return builder.toString();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class Element {
        static final Element NULL = new Element(0, 0);
        final int length;
        final int position;

        Element(int position, int length) {
            this.position = position;
            this.length = length;
        }

        public final String toString() {
            return getClass().getSimpleName() + "[position = " + this.position + ", length = " + this.length + "]";
        }
    }

    static /* synthetic */ void access$300(QueueFile x0, int x1, byte[] x2, int x3, int x4) throws IOException {
        int wrapPosition = x0.wrapPosition(x1);
        if (wrapPosition + x4 <= x0.fileLength) {
            x0.raf.seek(wrapPosition);
            x0.raf.readFully(x2, x3, x4);
            return;
        }
        int i = x0.fileLength - wrapPosition;
        x0.raf.seek(wrapPosition);
        x0.raf.readFully(x2, x3, i);
        x0.raf.seek(16L);
        x0.raf.readFully(x2, x3 + i, x4 - i);
    }
}
