package com.bumptech.glide.request.target;

import com.bumptech.glide.util.Util;
import com.nuance.connect.common.Integers;

/* loaded from: classes.dex */
public abstract class SimpleTarget<Z> extends BaseTarget<Z> {
    private final int height;
    private final int width;

    public SimpleTarget() {
        this((byte) 0);
    }

    private SimpleTarget(byte b) {
        this.width = Integers.STATUS_SUCCESS;
        this.height = Integers.STATUS_SUCCESS;
    }

    @Override // com.bumptech.glide.request.target.Target
    public final void getSize(SizeReadyCallback cb) {
        if (!Util.isValidDimensions(this.width, this.height)) {
            throw new IllegalArgumentException("Width and height must both be > 0 or Target#SIZE_ORIGINAL, but given width: " + this.width + " and height: " + this.height + ", either provide dimensions in the constructor or call override()");
        }
        cb.onSizeReady(this.width, this.height);
    }
}