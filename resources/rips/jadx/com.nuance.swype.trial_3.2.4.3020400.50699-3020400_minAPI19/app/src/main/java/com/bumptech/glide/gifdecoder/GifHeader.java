package com.bumptech.glide.gifdecoder;

import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public final class GifHeader {
    int bgColor;
    int bgIndex;
    GifFrame currentFrame;
    boolean gctFlag;
    int gctSize;
    int height;
    int pixelAspect;
    int width;
    int[] gct = null;
    public int status = 0;
    public int frameCount = 0;
    List<GifFrame> frames = new ArrayList();
    public int loopCount = -1;
}
