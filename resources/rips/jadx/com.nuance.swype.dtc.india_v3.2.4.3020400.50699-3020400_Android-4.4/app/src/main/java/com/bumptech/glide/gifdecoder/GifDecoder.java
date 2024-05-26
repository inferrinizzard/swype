package com.bumptech.glide.gifdecoder;

import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;
import com.nuance.swype.input.hardkey.HardKeyboardManager;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Iterator;

/* loaded from: classes.dex */
public class GifDecoder {
    private int[] act;
    public BitmapProvider bitmapProvider;
    public byte[] data;
    public int framePointer;
    public byte[] mainPixels;
    public int[] mainScratch;
    private byte[] pixelStack;
    private short[] prefix;
    public Bitmap previousImage;
    public ByteBuffer rawData;
    private boolean savePrevious;
    private int status;
    private byte[] suffix;
    private static final String TAG = GifDecoder.class.getSimpleName();
    private static final Bitmap.Config BITMAP_CONFIG = Bitmap.Config.ARGB_8888;
    private final int[] pct = new int[256];
    private final byte[] block = new byte[256];
    public GifHeader header = new GifHeader();

    /* loaded from: classes.dex */
    public interface BitmapProvider {
        Bitmap obtain(int i, int i2, Bitmap.Config config);

        void release(Bitmap bitmap);
    }

    public GifDecoder(BitmapProvider provider) {
        this.bitmapProvider = provider;
    }

    public final void advance() {
        this.framePointer = (this.framePointer + 1) % this.header.frameCount;
    }

    public final int getDelay(int n) {
        if (n < 0 || n >= this.header.frameCount) {
            return -1;
        }
        int delay = this.header.frames.get(n).delay;
        return delay;
    }

    public final synchronized Bitmap getNextFrame() {
        Bitmap bitmap = null;
        synchronized (this) {
            if (this.header.frameCount <= 0 || this.framePointer < 0) {
                if (Log.isLoggable(TAG, 3)) {
                    new StringBuilder("unable to decode frame, frameCount=").append(this.header.frameCount).append(" framePointer=").append(this.framePointer);
                }
                this.status = 1;
            }
            if (this.status == 1 || this.status == 2) {
                if (Log.isLoggable(TAG, 3)) {
                    new StringBuilder("Unable to decode frame, status=").append(this.status);
                }
            } else {
                this.status = 0;
                GifFrame currentFrame = this.header.frames.get(this.framePointer);
                GifFrame previousFrame = null;
                int previousIndex = this.framePointer - 1;
                if (previousIndex >= 0) {
                    GifFrame previousFrame2 = this.header.frames.get(previousIndex);
                    previousFrame = previousFrame2;
                }
                this.act = currentFrame.lct != null ? currentFrame.lct : this.header.gct;
                if (this.act == null) {
                    Log.isLoggable(TAG, 3);
                    this.status = 1;
                } else {
                    if (currentFrame.transparency) {
                        System.arraycopy(this.act, 0, this.pct, 0, this.act.length);
                        this.act = this.pct;
                        this.act[currentFrame.transIndex] = 0;
                    }
                    bitmap = setPixels(currentFrame, previousFrame);
                }
            }
        }
        return bitmap;
    }

    public final void setData(GifHeader header, byte[] data) {
        this.header = header;
        this.data = data;
        this.status = 0;
        this.framePointer = -1;
        this.rawData = ByteBuffer.wrap(data);
        this.rawData.rewind();
        this.rawData.order(ByteOrder.LITTLE_ENDIAN);
        this.savePrevious = false;
        Iterator<GifFrame> it = header.frames.iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            } else if (it.next().dispose == 3) {
                this.savePrevious = true;
                break;
            }
        }
        this.mainPixels = new byte[header.width * header.height];
        this.mainScratch = new int[header.width * header.height];
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    private Bitmap setPixels(GifFrame currentFrame, GifFrame previousFrame) {
        int i;
        int width = this.header.width;
        int height = this.header.height;
        int[] dest = this.mainScratch;
        if (previousFrame == null) {
            Arrays.fill(dest, 0);
        }
        if (previousFrame != null && previousFrame.dispose > 0) {
            if (previousFrame.dispose == 2) {
                int c = 0;
                if (!currentFrame.transparency) {
                    c = this.header.bgColor;
                    if (currentFrame.lct != null && this.header.bgIndex == currentFrame.transIndex) {
                        c = 0;
                    }
                }
                int topLeft = (previousFrame.iy * width) + previousFrame.ix;
                int bottomLeft = topLeft + (previousFrame.ih * width);
                for (int left = topLeft; left < bottomLeft; left += width) {
                    int right = left + previousFrame.iw;
                    for (int pointer = left; pointer < right; pointer++) {
                        dest[pointer] = c;
                    }
                }
            } else if (previousFrame.dispose == 3 && this.previousImage != null) {
                this.previousImage.getPixels(dest, 0, width, 0, 0, width, height);
            }
        }
        if (currentFrame != null) {
            this.rawData.position(currentFrame.bufferFrameStart);
        }
        int i2 = currentFrame == null ? this.header.width * this.header.height : currentFrame.iw * currentFrame.ih;
        if (this.mainPixels == null || this.mainPixels.length < i2) {
            this.mainPixels = new byte[i2];
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
        int i15 = i7;
        int i16 = i8;
        int i17 = i5;
        int i18 = 0;
        int i19 = 0;
        int i20 = 0;
        while (true) {
            if (i11 >= i2) {
                break;
            }
            if (i19 == 0) {
                i19 = readBlock();
                if (i19 <= 0) {
                    this.status = 3;
                    break;
                }
                i18 = 0;
            }
            int i21 = i10 + ((this.block[i18] & 255) << i14);
            int i22 = i14 + 8;
            int i23 = i18 + 1;
            int i24 = i19 - 1;
            int i25 = i15;
            int i26 = i16;
            int i27 = i13;
            int i28 = i21;
            int i29 = i20;
            int i30 = i17;
            int i31 = i22;
            while (true) {
                if (i31 < i25) {
                    i13 = i27;
                    i16 = i26;
                    i19 = i24;
                    i15 = i25;
                    i18 = i23;
                    int i32 = i31;
                    i17 = i30;
                    i20 = i29;
                    i10 = i28;
                    i14 = i32;
                    break;
                }
                int i33 = i28 & i26;
                int i34 = i28 >> i25;
                i31 -= i25;
                if (i33 == i3) {
                    i25 = read + 1;
                    i26 = (1 << i25) - 1;
                    i30 = i3 + 2;
                    i28 = i34;
                    i6 = -1;
                } else {
                    if (i33 > i30) {
                        this.status = 3;
                        i13 = i27;
                        i14 = i31;
                        i15 = i25;
                        i17 = i30;
                        i18 = i23;
                        i20 = i29;
                        i10 = i34;
                        i16 = i26;
                        i19 = i24;
                        break;
                    }
                    if (i33 == i4) {
                        i13 = i27;
                        i14 = i31;
                        i15 = i25;
                        i17 = i30;
                        i18 = i23;
                        i20 = i29;
                        i10 = i34;
                        i16 = i26;
                        i19 = i24;
                        break;
                    }
                    if (i6 == -1) {
                        this.pixelStack[i12] = this.suffix[i33];
                        i12++;
                        i27 = i33;
                        i6 = i33;
                        i28 = i34;
                    } else {
                        if (i33 >= i30) {
                            this.pixelStack[i12] = (byte) i27;
                            i12++;
                            i = i6;
                        } else {
                            i = i33;
                        }
                        while (i >= i3) {
                            this.pixelStack[i12] = this.suffix[i];
                            i = this.prefix[i];
                            i12++;
                        }
                        i27 = this.suffix[i] & 255;
                        int i35 = i12 + 1;
                        this.pixelStack[i12] = (byte) i27;
                        if (i30 < 4096) {
                            this.prefix[i30] = (short) i6;
                            this.suffix[i30] = (byte) i27;
                            i30++;
                            if ((i30 & i26) == 0 && i30 < 4096) {
                                i25++;
                                i26 += i30;
                            }
                        }
                        int i36 = i11;
                        while (i35 > 0) {
                            int i37 = i35 - 1;
                            this.mainPixels[i29] = this.pixelStack[i37];
                            i36++;
                            i29++;
                            i35 = i37;
                        }
                        i11 = i36;
                        i6 = i33;
                        i12 = i35;
                        i28 = i34;
                    }
                }
            }
        }
        for (int i38 = i20; i38 < i2; i38++) {
            this.mainPixels[i38] = 0;
        }
        int pass = 1;
        int inc = 8;
        int iline = 0;
        for (int i39 = 0; i39 < currentFrame.ih; i39++) {
            int line = i39;
            if (currentFrame.interlace) {
                if (iline >= currentFrame.ih) {
                    pass++;
                    switch (pass) {
                        case 2:
                            iline = 4;
                            break;
                        case 3:
                            iline = 2;
                            inc = 4;
                            break;
                        case 4:
                            iline = 1;
                            inc = 2;
                            break;
                    }
                }
                line = iline;
                iline += inc;
            }
            int line2 = line + currentFrame.iy;
            if (line2 < this.header.height) {
                int k = line2 * this.header.width;
                int dx = k + currentFrame.ix;
                int dlim = dx + currentFrame.iw;
                if (this.header.width + k < dlim) {
                    dlim = k + this.header.width;
                }
                int sx = i39 * currentFrame.iw;
                int sx2 = sx;
                while (dx < dlim) {
                    int sx3 = sx2 + 1;
                    int index = this.mainPixels[sx2] & 255;
                    int c2 = this.act[index];
                    if (c2 != 0) {
                        dest[dx] = c2;
                    }
                    dx++;
                    sx2 = sx3;
                }
            }
        }
        if (this.savePrevious && (currentFrame.dispose == 0 || currentFrame.dispose == 1)) {
            if (this.previousImage == null) {
                this.previousImage = getNextBitmap();
            }
            this.previousImage.setPixels(dest, 0, width, 0, 0, width, height);
        }
        Bitmap result = getNextBitmap();
        result.setPixels(dest, 0, width, 0, 0, width, height);
        return result;
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
        int blockSize = read();
        int n = 0;
        if (blockSize > 0) {
            while (n < blockSize) {
                int count = blockSize - n;
                try {
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

    private Bitmap getNextBitmap() {
        Bitmap result = this.bitmapProvider.obtain(this.header.width, this.header.height, BITMAP_CONFIG);
        if (result == null) {
            result = Bitmap.createBitmap(this.header.width, this.header.height, BITMAP_CONFIG);
        }
        if (Build.VERSION.SDK_INT >= 12) {
            result.setHasAlpha(true);
        }
        return result;
    }
}
