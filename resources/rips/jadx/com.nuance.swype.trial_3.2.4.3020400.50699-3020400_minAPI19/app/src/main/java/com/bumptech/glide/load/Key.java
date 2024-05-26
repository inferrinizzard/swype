package com.bumptech.glide.load;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;

/* loaded from: classes.dex */
public interface Key {
    boolean equals(Object obj);

    int hashCode();

    void updateDiskCacheKey(MessageDigest messageDigest) throws UnsupportedEncodingException;
}
