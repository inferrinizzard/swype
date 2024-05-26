package com.google.api.client.http;

import com.google.api.client.repackaged.com.google.common.base.Preconditions;
import com.google.api.client.util.StringUtils;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

/* loaded from: classes.dex */
public final class ByteArrayContent extends AbstractInputStreamContent {
    private final byte[] byteArray;
    private final int length;
    private final int offset;

    public ByteArrayContent(String type, byte[] array) {
        this(type, array, 0, array.length);
    }

    public ByteArrayContent(String type, byte[] array, int offset, int length) {
        super(type);
        this.byteArray = (byte[]) Preconditions.checkNotNull(array);
        com.google.api.client.util.Preconditions.checkArgument(offset >= 0 && length >= 0 && offset + length <= array.length, "offset %s, length %s, array length %s", Integer.valueOf(offset), Integer.valueOf(length), Integer.valueOf(array.length));
        this.offset = offset;
        this.length = length;
    }

    public static ByteArrayContent fromString(String type, String contentString) {
        return new ByteArrayContent(type, StringUtils.getBytesUtf8(contentString));
    }

    @Override // com.google.api.client.http.HttpContent
    public final long getLength() {
        return this.length;
    }

    @Override // com.google.api.client.http.HttpContent
    public final boolean retrySupported() {
        return true;
    }

    @Override // com.google.api.client.http.AbstractInputStreamContent
    public final InputStream getInputStream() {
        return new ByteArrayInputStream(this.byteArray, this.offset, this.length);
    }

    @Override // com.google.api.client.http.AbstractInputStreamContent
    public final ByteArrayContent setType(String type) {
        return (ByteArrayContent) super.setType(type);
    }

    @Override // com.google.api.client.http.AbstractInputStreamContent
    public final ByteArrayContent setCloseInputStream(boolean closeInputStream) {
        return (ByteArrayContent) super.setCloseInputStream(closeInputStream);
    }
}
