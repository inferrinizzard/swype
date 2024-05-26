package com.bumptech.glide.gifdecoder;

import android.util.Log;
import com.nuance.swype.input.R;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

/* loaded from: classes.dex */
public final class GifHeaderParser {
    private final byte[] block = new byte[256];
    private int blockSize = 0;
    public GifHeader header;
    public ByteBuffer rawData;

    public final GifHeader parseHeader() {
        if (this.rawData == null) {
            throw new IllegalStateException("You must call setData() before parseHeader()");
        }
        if (err()) {
            return this.header;
        }
        readHeader();
        if (!err()) {
            readContents();
            if (this.header.frameCount < 0) {
                this.header.status = 1;
            }
        }
        return this.header;
    }

    private void readContents() {
        boolean done = false;
        while (!done && !err()) {
            switch (read()) {
                case 33:
                    switch (read()) {
                        case 1:
                            skip();
                            break;
                        case R.styleable.ThemeTemplate_chineseStroke2 /* 249 */:
                            this.header.currentFrame = new GifFrame();
                            read();
                            int read = read();
                            this.header.currentFrame.dispose = (read & 28) >> 2;
                            if (this.header.currentFrame.dispose == 0) {
                                this.header.currentFrame.dispose = 1;
                            }
                            this.header.currentFrame.transparency = (read & 1) != 0;
                            short s = this.rawData.getShort();
                            if (s < 3) {
                                s = 10;
                            }
                            this.header.currentFrame.delay = s * 10;
                            this.header.currentFrame.transIndex = read();
                            read();
                            break;
                        case R.styleable.ThemeTemplate_chineseFunctionBarDictionaries /* 254 */:
                            skip();
                            break;
                        case 255:
                            readBlock();
                            String app = "";
                            for (int i = 0; i < 11; i++) {
                                app = app + ((char) this.block[i]);
                            }
                            if (app.equals("NETSCAPE2.0")) {
                                readNetscapeExt();
                                break;
                            } else {
                                skip();
                                break;
                            }
                        default:
                            skip();
                            break;
                    }
                case 44:
                    if (this.header.currentFrame == null) {
                        this.header.currentFrame = new GifFrame();
                    }
                    this.header.currentFrame.ix = this.rawData.getShort();
                    this.header.currentFrame.iy = this.rawData.getShort();
                    this.header.currentFrame.iw = this.rawData.getShort();
                    this.header.currentFrame.ih = this.rawData.getShort();
                    int read2 = read();
                    boolean z = (read2 & 128) != 0;
                    int pow = (int) Math.pow(2.0d, (read2 & 7) + 1);
                    this.header.currentFrame.interlace = (read2 & 64) != 0;
                    if (z) {
                        this.header.currentFrame.lct = readColorTable(pow);
                    } else {
                        this.header.currentFrame.lct = null;
                    }
                    this.header.currentFrame.bufferFrameStart = this.rawData.position();
                    read();
                    skip();
                    if (err()) {
                        break;
                    } else {
                        this.header.frameCount++;
                        this.header.frames.add(this.header.currentFrame);
                        break;
                    }
                case 59:
                    done = true;
                    break;
                default:
                    this.header.status = 1;
                    break;
            }
        }
    }

    private void readNetscapeExt() {
        do {
            readBlock();
            if (this.block[0] == 1) {
                int b1 = this.block[1] & 255;
                int b2 = this.block[2] & 255;
                this.header.loopCount = (b2 << 8) | b1;
            }
            if (this.blockSize <= 0) {
                return;
            }
        } while (!err());
    }

    private void readHeader() {
        String id = "";
        for (int i = 0; i < 6; i++) {
            id = id + ((char) read());
        }
        if (!id.startsWith("GIF")) {
            this.header.status = 1;
            return;
        }
        this.header.width = this.rawData.getShort();
        this.header.height = this.rawData.getShort();
        int read = read();
        this.header.gctFlag = (read & 128) != 0;
        this.header.gctSize = 2 << (read & 7);
        this.header.bgIndex = read();
        this.header.pixelAspect = read();
        if (this.header.gctFlag && !err()) {
            this.header.gct = readColorTable(this.header.gctSize);
            this.header.bgColor = this.header.gct[this.header.bgIndex];
        }
    }

    private int[] readColorTable(int ncolors) {
        int nbytes = ncolors * 3;
        int[] tab = null;
        byte[] c = new byte[nbytes];
        try {
            this.rawData.get(c);
            tab = new int[256];
            int j = 0;
            int i = 0;
            while (i < ncolors) {
                int j2 = j + 1;
                int r = c[j] & 255;
                int j3 = j2 + 1;
                int g = c[j2] & 255;
                int j4 = j3 + 1;
                int b = c[j3] & 255;
                int i2 = i + 1;
                tab[i] = (-16777216) | (r << 16) | (g << 8) | b;
                j = j4;
                i = i2;
            }
        } catch (BufferUnderflowException e) {
            Log.isLoggable("GifHeaderParser", 3);
            this.header.status = 1;
        }
        return tab;
    }

    private void skip() {
        int blockSize;
        do {
            blockSize = read();
            this.rawData.position(this.rawData.position() + blockSize);
        } while (blockSize > 0);
    }

    private int readBlock() {
        this.blockSize = read();
        int n = 0;
        if (this.blockSize > 0) {
            int count = 0;
            while (n < this.blockSize) {
                try {
                    count = this.blockSize - n;
                    this.rawData.get(this.block, n, count);
                    n += count;
                } catch (Exception e) {
                    if (Log.isLoggable("GifHeaderParser", 3)) {
                        new StringBuilder("Error Reading Block n: ").append(n).append(" count: ").append(count).append(" blockSize: ").append(this.blockSize);
                    }
                    this.header.status = 1;
                }
            }
        }
        return n;
    }

    private int read() {
        try {
            int curByte = this.rawData.get() & 255;
            return curByte;
        } catch (Exception e) {
            this.header.status = 1;
            return 0;
        }
    }

    private boolean err() {
        return this.header.status != 0;
    }

    public final GifHeaderParser setData(byte[] data) {
        this.rawData = null;
        Arrays.fill(this.block, (byte) 0);
        this.header = new GifHeader();
        this.blockSize = 0;
        if (data != null) {
            this.rawData = ByteBuffer.wrap(data);
            this.rawData.rewind();
            this.rawData.order(ByteOrder.LITTLE_ENDIAN);
        } else {
            this.rawData = null;
            this.header.status = 2;
        }
        return this;
    }
}
