package com.bumptech.glide.signature;

import com.bumptech.glide.load.Key;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;

/* loaded from: classes.dex */
public final class StringSignature implements Key {
    private final String signature;

    public StringSignature(String signature) {
        if (signature == null) {
            throw new NullPointerException("Signature cannot be null!");
        }
        this.signature = signature;
    }

    @Override // com.bumptech.glide.load.Key
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        StringSignature that = (StringSignature) o;
        return this.signature.equals(that.signature);
    }

    @Override // com.bumptech.glide.load.Key
    public final int hashCode() {
        return this.signature.hashCode();
    }

    @Override // com.bumptech.glide.load.Key
    public final void updateDiskCacheKey(MessageDigest messageDigest) throws UnsupportedEncodingException {
        messageDigest.update(this.signature.getBytes("UTF-8"));
    }

    public final String toString() {
        return "StringSignature{signature='" + this.signature + "'}";
    }
}
