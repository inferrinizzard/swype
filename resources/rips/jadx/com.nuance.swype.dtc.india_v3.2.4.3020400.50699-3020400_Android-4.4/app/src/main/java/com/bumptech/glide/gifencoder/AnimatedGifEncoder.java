package com.bumptech.glide.gifencoder;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import com.nuance.swype.input.R;
import java.io.IOException;
import java.io.OutputStream;

/* loaded from: classes.dex */
public final class AnimatedGifEncoder {
    private int colorDepth;
    private byte[] colorTab;
    private boolean hasTransparentPixels;
    private int height;
    private Bitmap image;
    private byte[] indexedPixels;
    private OutputStream out;
    private byte[] pixels;
    private int transIndex;
    private int width;
    private Integer transparent = null;
    private int repeat = -1;
    public int delay = 0;
    private boolean started = false;
    private boolean[] usedEntry = new boolean[256];
    private int palSize = 7;
    private int dispose = -1;
    private boolean closeStream = false;
    private boolean firstFrame = true;
    private boolean sizeSet = false;
    private int sample = 10;

    public final boolean addFrame(Bitmap im) {
        int i;
        int i2 = 0;
        if (im == null || !this.started) {
            return false;
        }
        try {
            if (!this.sizeSet) {
                int width = im.getWidth();
                int height = im.getHeight();
                if (!this.started || this.firstFrame) {
                    this.width = width;
                    this.height = height;
                    if (this.width <= 0) {
                        this.width = R.styleable.ThemeTemplate_japaneseLeftPopup;
                    }
                    if (this.height <= 0) {
                        this.height = R.styleable.ThemeTemplate_pressableBackgroundHighlight;
                    }
                    this.sizeSet = true;
                }
            }
            this.image = im;
            int width2 = this.image.getWidth();
            int height2 = this.image.getHeight();
            if (width2 != this.width || height2 != this.height) {
                Bitmap createBitmap = Bitmap.createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888);
                new Canvas(createBitmap).drawBitmap(createBitmap, 0.0f, 0.0f, (Paint) null);
                this.image = createBitmap;
            }
            int[] iArr = new int[width2 * height2];
            this.image.getPixels(iArr, 0, width2, 0, 0, width2, height2);
            this.pixels = new byte[iArr.length * 3];
            this.hasTransparentPixels = false;
            int length = iArr.length;
            int i3 = 0;
            int i4 = 0;
            for (int i5 : iArr) {
                if (i5 == 0) {
                    i3++;
                }
                int i6 = i4 + 1;
                this.pixels[i4] = (byte) (i5 & 255);
                int i7 = i6 + 1;
                this.pixels[i6] = (byte) ((i5 >> 8) & 255);
                i4 = i7 + 1;
                this.pixels[i7] = (byte) ((i5 >> 16) & 255);
            }
            double d = (i3 * 100) / length;
            this.hasTransparentPixels = d > 4.0d;
            if (Log.isLoggable("AnimatedGifEncoder", 3)) {
                new StringBuilder("got pixels for frame with ").append(d).append("% transparent pixels");
            }
            analyzePixels();
            if (this.firstFrame) {
                writeShort(this.width);
                writeShort(this.height);
                this.out.write(this.palSize | R.styleable.ThemeTemplate_pressableBackgroundHighlight);
                this.out.write(0);
                this.out.write(0);
                writePalette();
                if (this.repeat >= 0) {
                    this.out.write(33);
                    this.out.write(255);
                    this.out.write(11);
                    writeString("NETSCAPE2.0");
                    this.out.write(3);
                    this.out.write(1);
                    writeShort(this.repeat);
                    this.out.write(0);
                }
            }
            this.out.write(33);
            this.out.write(R.styleable.ThemeTemplate_chineseStroke2);
            this.out.write(4);
            if (this.transparent != null || this.hasTransparentPixels) {
                i = 2;
                i2 = 1;
            } else {
                i = 0;
            }
            if (this.dispose >= 0) {
                i = this.dispose & 7;
            }
            this.out.write((i << 2) | 0 | 0 | i2);
            writeShort(this.delay);
            this.out.write(this.transIndex);
            this.out.write(0);
            this.out.write(44);
            writeShort(0);
            writeShort(0);
            writeShort(this.width);
            writeShort(this.height);
            if (this.firstFrame) {
                this.out.write(0);
            } else {
                this.out.write(this.palSize | 128);
            }
            if (!this.firstFrame) {
                writePalette();
            }
            LZWEncoder lZWEncoder = new LZWEncoder(this.width, this.height, this.indexedPixels, this.colorDepth);
            OutputStream outputStream = this.out;
            outputStream.write(lZWEncoder.initCodeSize);
            lZWEncoder.remaining = lZWEncoder.imgW * lZWEncoder.imgH;
            lZWEncoder.curPixel = 0;
            lZWEncoder.compress(lZWEncoder.initCodeSize + 1, outputStream);
            outputStream.write(0);
            this.firstFrame = false;
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public final boolean finish() {
        if (!this.started) {
            return false;
        }
        boolean ok = true;
        this.started = false;
        try {
            this.out.write(59);
            this.out.flush();
            if (this.closeStream) {
                this.out.close();
            }
        } catch (IOException e) {
            ok = false;
        }
        this.transIndex = 0;
        this.out = null;
        this.image = null;
        this.pixels = null;
        this.indexedPixels = null;
        this.colorTab = null;
        this.closeStream = false;
        this.firstFrame = true;
        return ok;
    }

    public final boolean start(OutputStream os) {
        if (os == null) {
            return false;
        }
        boolean ok = true;
        this.closeStream = false;
        this.out = os;
        try {
            writeString("GIF89a");
        } catch (IOException e) {
            ok = false;
        }
        this.started = ok;
        return ok;
    }

    private void analyzePixels() {
        int len = this.pixels.length;
        int nPix = len / 3;
        this.indexedPixels = new byte[nPix];
        NeuQuant nq = new NeuQuant(this.pixels, len, this.sample);
        nq.learn();
        nq.unbiasnet();
        nq.inxbuild();
        this.colorTab = nq.colorMap();
        for (int i = 0; i < this.colorTab.length; i += 3) {
            byte temp = this.colorTab[i];
            this.colorTab[i] = this.colorTab[i + 2];
            this.colorTab[i + 2] = temp;
            this.usedEntry[i / 3] = false;
        }
        int i2 = 0;
        int k = 0;
        while (i2 < nPix) {
            int k2 = k + 1;
            int i3 = this.pixels[k] & 255;
            int k3 = k2 + 1;
            int index = nq.map(i3, this.pixels[k2] & 255, this.pixels[k3] & 255);
            this.usedEntry[index] = true;
            this.indexedPixels[i2] = (byte) index;
            i2++;
            k = k3 + 1;
        }
        this.pixels = null;
        this.colorDepth = 8;
        this.palSize = 7;
        if (this.transparent != null) {
            this.transIndex = findClosest(this.transparent.intValue());
        } else if (this.hasTransparentPixels) {
            this.transIndex = findClosest(0);
        }
    }

    private int findClosest(int color) {
        if (this.colorTab == null) {
            return -1;
        }
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        int minpos = 0;
        int dmin = 16777216;
        int len = this.colorTab.length;
        int i = 0;
        while (i < len) {
            int i2 = i + 1;
            int dr = r - (this.colorTab[i] & 255);
            int i3 = i2 + 1;
            int dg = g - (this.colorTab[i2] & 255);
            int db = b - (this.colorTab[i3] & 255);
            int d = (dr * dr) + (dg * dg) + (db * db);
            int index = i3 / 3;
            if (this.usedEntry[index] && d < dmin) {
                dmin = d;
                minpos = index;
            }
            i = i3 + 1;
        }
        return minpos;
    }

    private void writePalette() throws IOException {
        this.out.write(this.colorTab, 0, this.colorTab.length);
        int n = 768 - this.colorTab.length;
        for (int i = 0; i < n; i++) {
            this.out.write(0);
        }
    }

    private void writeShort(int value) throws IOException {
        this.out.write(value & 255);
        this.out.write((value >> 8) & 255);
    }

    private void writeString(String s) throws IOException {
        for (int i = 0; i < s.length(); i++) {
            this.out.write((byte) s.charAt(i));
        }
    }
}
