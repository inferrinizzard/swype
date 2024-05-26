package com.nuance.android.gif;

import android.graphics.Bitmap;
import android.util.Log;
import com.nuance.swype.input.R;
import com.nuance.swype.input.hardkey.HardKeyboardManager;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class GifDecoder {
    private static final String TAG = GifDecoder.class.getSimpleName();
    protected int[] act;
    protected int bgColor;
    protected int bgIndex;
    protected int[] copyScratch;
    protected GifFrame currentFrame;
    protected Bitmap currentImage;
    protected int frameCount;
    protected int framePointer;
    protected ArrayList<GifFrame> frames;
    protected int[] gct;
    protected boolean gctFlag;
    protected int gctSize;
    protected int height;
    protected boolean lctFlag;
    protected int lctSize;
    protected byte[] mainPixels;
    protected int[] mainScratch;
    protected int pixelAspect;
    protected byte[] pixelStack;
    protected short[] prefix;
    protected Bitmap previousImage;
    protected ByteBuffer rawData;
    protected int status;
    protected byte[] suffix;
    protected int width;
    protected int loopCount = 1;
    protected byte[] block = new byte[256];
    protected int blockSize = 0;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class GifFrame {
        public int bufferFrameStart;
        public int delay;
        public int dispose;
        public int ih;
        public boolean interlace;
        public int iw;
        public int ix;
        public int iy;
        public int[] lct;
        public int transIndex;
        public boolean transparency;

        private GifFrame() {
        }

        /* synthetic */ GifFrame(byte b) {
            this();
        }
    }

    public final void advance() {
        this.framePointer = (this.framePointer + 1) % this.frameCount;
    }

    public final int getDelay(int n) {
        if (n < 0 || n >= this.frameCount) {
            return -1;
        }
        int delay = this.frames.get(n).delay;
        return delay;
    }

    public final int getFrameCount() {
        return this.frameCount;
    }

    public final int getLoopCount() {
        return this.loopCount;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public final Bitmap getNextFrame() {
        GifFrame gifFrame;
        int i;
        if (this.frameCount <= 0 || this.framePointer < 0 || this.currentImage == null) {
            return null;
        }
        GifFrame frame = this.frames.get(this.framePointer);
        if (frame.lct == null) {
            this.act = this.gct;
        } else {
            this.act = frame.lct;
            if (this.bgIndex == frame.transIndex) {
                this.bgColor = 0;
            }
        }
        int save = 0;
        if (frame.transparency) {
            save = this.act[frame.transIndex];
            this.act[frame.transIndex] = 0;
        }
        if (this.act == null) {
            Log.w(TAG, "No Valid Color Table");
            this.status = 1;
            return null;
        }
        int i2 = this.framePointer;
        GifFrame gifFrame2 = this.frames.get(i2);
        int i3 = i2 - 1;
        if (i3 < 0) {
            gifFrame = null;
        } else {
            gifFrame = this.frames.get(i3);
        }
        int[] iArr = this.mainScratch;
        if (gifFrame != null && gifFrame.dispose > 0) {
            if (gifFrame.dispose == 1 && this.currentImage != null) {
                this.currentImage.getPixels(iArr, 0, this.width, 0, 0, this.width, this.height);
            }
            if (gifFrame.dispose == 2) {
                int i4 = 0;
                if (!gifFrame2.transparency) {
                    i4 = this.bgColor;
                }
                for (int i5 = 0; i5 < gifFrame.ih; i5++) {
                    int i6 = ((gifFrame.iy + i5) * this.width) + gifFrame.ix;
                    int i7 = gifFrame.iw + i6;
                    while (i6 < i7) {
                        iArr[i6] = i4;
                        i6++;
                    }
                }
            }
            if (gifFrame.dispose == 3 && this.previousImage != null) {
                this.previousImage.getPixels(iArr, 0, this.width, 0, 0, this.width, this.height);
            }
        }
        decodeBitmapData(gifFrame2, this.mainPixels);
        int i8 = 1;
        int i9 = 8;
        int i10 = 0;
        for (int i11 = 0; i11 < gifFrame2.ih; i11++) {
            if (gifFrame2.interlace) {
                if (i10 >= gifFrame2.ih) {
                    i8++;
                    switch (i8) {
                        case 2:
                            i10 = 4;
                            break;
                        case 3:
                            i10 = 2;
                            i9 = 4;
                            break;
                        case 4:
                            i10 = 1;
                            i9 = 2;
                            break;
                    }
                }
                int i12 = i10;
                i10 += i9;
                i = i12;
            } else {
                i = i11;
            }
            int i13 = i + gifFrame2.iy;
            if (i13 < this.height) {
                int i14 = this.width * i13;
                int i15 = i14 + gifFrame2.ix;
                int i16 = gifFrame2.iw + i15;
                if (this.width + i14 < i16) {
                    i16 = this.width + i14;
                }
                int i17 = gifFrame2.iw * i11;
                int i18 = i15;
                while (i18 < i16) {
                    int i19 = i17 + 1;
                    int i20 = this.act[this.mainPixels[i17] & 255];
                    if (i20 != 0) {
                        iArr[i18] = i20;
                    }
                    i18++;
                    i17 = i19;
                }
            }
        }
        this.currentImage.getPixels(this.copyScratch, 0, this.width, 0, 0, this.width, this.height);
        this.previousImage.setPixels(this.copyScratch, 0, this.width, 0, 0, this.width, this.height);
        this.currentImage.setPixels(iArr, 0, this.width, 0, 0, this.width, this.height);
        if (frame.transparency) {
            this.act[frame.transIndex] = save;
        }
        return this.currentImage;
    }

    public final int read(InputStream is, int contentLength) {
        long startTime = System.currentTimeMillis();
        if (is != null) {
            int capacity = contentLength > 0 ? contentLength + HardKeyboardManager.META_CTRL_ON : HardKeyboardManager.META_CTRL_ON;
            try {
                ByteArrayOutputStream buffer = new ByteArrayOutputStream(capacity);
                byte[] data = new byte[HardKeyboardManager.META_CTRL_RIGHT_ON];
                while (true) {
                    int nRead = is.read(data, 0, HardKeyboardManager.META_CTRL_RIGHT_ON);
                    if (nRead == -1) {
                        break;
                    }
                    buffer.write(data, 0, nRead);
                }
                buffer.flush();
                byte[] byteArray = buffer.toByteArray();
                this.status = 0;
                this.frameCount = 0;
                this.framePointer = -1;
                this.frames = new ArrayList<>();
                this.gct = null;
                if (byteArray != null) {
                    this.rawData = ByteBuffer.wrap(byteArray);
                    this.rawData.rewind();
                    this.rawData.order(ByteOrder.LITTLE_ENDIAN);
                    readHeader();
                    if (!err()) {
                        readContents();
                        if (this.frameCount < 0) {
                            this.status = 1;
                        }
                    }
                } else {
                    this.status = 2;
                }
            } catch (IOException e) {
                Log.w(TAG, "Error reading data from stream", e);
            }
        } else {
            this.status = 2;
        }
        try {
            is.close();
        } catch (Exception e2) {
            Log.w(TAG, "Error closing stream", e2);
        }
        long endTime = System.currentTimeMillis();
        Log.i("GifDecoder", "nhbinh-decodeGif = " + (endTime - startTime));
        return this.status;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r6v12 */
    /* JADX WARN: Type inference failed for: r6v3 */
    /* JADX WARN: Type inference failed for: r6v4 */
    /* JADX WARN: Type inference failed for: r6v5, types: [short] */
    /* JADX WARN: Type inference failed for: r6v6 */
    private void decodeBitmapData(GifFrame gifFrame, byte[] bArr) {
        int i;
        System.currentTimeMillis();
        if (gifFrame != null) {
            this.rawData.position(gifFrame.bufferFrameStart);
        }
        int i2 = gifFrame == null ? this.width * this.height : gifFrame.iw * gifFrame.ih;
        if (bArr == null || bArr.length < i2) {
            bArr = new byte[i2];
        }
        if (this.prefix == null) {
            this.prefix = new short[HardKeyboardManager.META_CTRL_ON];
        }
        if (this.suffix == null) {
            this.suffix = new byte[HardKeyboardManager.META_CTRL_ON];
        }
        if (this.pixelStack == null) {
            this.pixelStack = new byte[4097];
        }
        int read = read();
        int i3 = 1 << read;
        int i4 = i3 + 1;
        int i5 = i3 + 2;
        int i6 = -1;
        int i7 = read + 1;
        int i8 = (1 << i7) - 1;
        for (int i9 = 0; i9 < i3; i9++) {
            this.prefix[i9] = 0;
            this.suffix[i9] = (byte) i9;
        }
        int i10 = 0;
        int i11 = 0;
        int i12 = 0;
        int i13 = 0;
        int i14 = 0;
        int i15 = 0;
        int i16 = 0;
        int i17 = 0;
        while (i15 < i2) {
            if (i17 != 0) {
                i = i17;
            } else if (i13 < i7) {
                if (i12 == 0) {
                    i12 = readBlock();
                    if (i12 <= 0) {
                        break;
                    } else {
                        i10 = 0;
                    }
                }
                i14 += (this.block[i10] & 255) << i13;
                i13 += 8;
                i10++;
                i12--;
            } else {
                int i18 = i14 & i8;
                i14 >>= i7;
                i13 -= i7;
                if (i18 > i5 || i18 == i4) {
                    break;
                }
                if (i18 == i3) {
                    i7 = read + 1;
                    i8 = (1 << i7) - 1;
                    i5 = i3 + 2;
                    i6 = -1;
                } else if (i6 == -1) {
                    this.pixelStack[i17] = this.suffix[i18 == true ? 1 : 0];
                    i6 = i18 == true ? 1 : 0;
                    i11 = i18 == true ? 1 : 0;
                    i17++;
                } else {
                    short s = i18;
                    if (i18 == i5) {
                        this.pixelStack[i17] = (byte) i11;
                        s = i6;
                        i17++;
                    }
                    while (s > i3) {
                        this.pixelStack[i17] = this.suffix[s];
                        s = this.prefix[s];
                        i17++;
                    }
                    i11 = this.suffix[s] & 255;
                    if (i5 >= 4096) {
                        break;
                    }
                    i = i17 + 1;
                    this.pixelStack[i17] = (byte) i11;
                    this.prefix[i5] = (short) i6;
                    this.suffix[i5] = (byte) i11;
                    i5++;
                    if ((i5 & i8) == 0 && i5 < 4096) {
                        i7++;
                        i8 += i5;
                    }
                    i6 = i18 == true ? 1 : 0;
                }
            }
            int i19 = i - 1;
            bArr[i16] = this.pixelStack[i19];
            i15++;
            i16++;
            i17 = i19;
        }
        for (int i20 = i16; i20 < i2; i20++) {
            bArr[i20] = 0;
        }
    }

    private boolean err() {
        return this.status != 0;
    }

    private int read() {
        try {
            int curByte = this.rawData.get() & 255;
            return curByte;
        } catch (Exception e) {
            this.status = 1;
            return 0;
        }
    }

    private int readBlock() {
        this.blockSize = read();
        int n = 0;
        if (this.blockSize > 0) {
            while (n < this.blockSize) {
                try {
                    int count = this.blockSize - n;
                    this.rawData.get(this.block, n, count);
                    n += count;
                } catch (Exception e) {
                    Log.w(TAG, "Error Reading Block", e);
                    this.status = 1;
                }
            }
        }
        return n;
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
            Log.w(TAG, "Format Error Reading Color Table", e);
            this.status = 1;
        }
        return tab;
    }

    private void readContents() {
        byte b = 0;
        boolean done = false;
        while (!done && !err()) {
            switch (read()) {
                case 33:
                    switch (read()) {
                        case 1:
                            skip();
                            break;
                        case R.styleable.ThemeTemplate_chineseStroke2 /* 249 */:
                            this.currentFrame = new GifFrame(b);
                            read();
                            int read = read();
                            this.currentFrame.dispose = (read & 28) >> 2;
                            if (this.currentFrame.dispose == 0) {
                                this.currentFrame.dispose = 1;
                            }
                            this.currentFrame.transparency = (read & 1) != 0;
                            this.currentFrame.delay = this.rawData.getShort() * 10;
                            this.currentFrame.transIndex = read();
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
                    this.currentFrame.ix = this.rawData.getShort();
                    this.currentFrame.iy = this.rawData.getShort();
                    this.currentFrame.iw = this.rawData.getShort();
                    this.currentFrame.ih = this.rawData.getShort();
                    int read2 = read();
                    this.lctFlag = (read2 & 128) != 0;
                    this.lctSize = (int) Math.pow(2.0d, (read2 & 7) + 1);
                    this.currentFrame.interlace = (read2 & 64) != 0;
                    if (this.lctFlag) {
                        this.currentFrame.lct = readColorTable(this.lctSize);
                    } else {
                        this.currentFrame.lct = null;
                    }
                    this.currentFrame.bufferFrameStart = this.rawData.position();
                    decodeBitmapData(null, this.mainPixels);
                    skip();
                    if (err()) {
                        break;
                    } else {
                        this.frameCount++;
                        this.frames.add(this.currentFrame);
                        break;
                    }
                case 59:
                    done = true;
                    break;
                default:
                    this.status = 1;
                    break;
            }
        }
    }

    private void readHeader() {
        String id = "";
        for (int i = 0; i < 6; i++) {
            id = id + ((char) read());
        }
        if (!id.startsWith("GIF")) {
            this.status = 1;
            return;
        }
        this.width = this.rawData.getShort();
        this.height = this.rawData.getShort();
        int read = read();
        this.gctFlag = (read & 128) != 0;
        this.gctSize = 2 << (read & 7);
        this.bgIndex = read();
        this.pixelAspect = read();
        this.mainPixels = new byte[this.width * this.height];
        this.mainScratch = new int[this.width * this.height];
        this.copyScratch = new int[this.width * this.height];
        this.previousImage = Bitmap.createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888);
        this.currentImage = Bitmap.createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888);
        if (this.gctFlag && !err()) {
            this.gct = readColorTable(this.gctSize);
            this.bgColor = this.gct[this.bgIndex];
        }
    }

    private void readNetscapeExt() {
        do {
            readBlock();
            if (this.block[0] == 1) {
                int b1 = this.block[1] & 255;
                int b2 = this.block[2] & 255;
                this.loopCount = (b2 << 8) | b1;
            }
            if (this.blockSize <= 0) {
                return;
            }
        } while (!err());
    }

    private void skip() {
        do {
            readBlock();
            if (this.blockSize <= 0) {
                return;
            }
        } while (!err());
    }
}
