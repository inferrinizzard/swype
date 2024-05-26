package com.google.api.client.http;

import com.google.api.client.repackaged.com.google.common.base.Preconditions;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/* loaded from: classes.dex */
public final class FileContent extends AbstractInputStreamContent {
    private final File file;

    public FileContent(String type, File file) {
        super(type);
        this.file = (File) Preconditions.checkNotNull(file);
    }

    @Override // com.google.api.client.http.HttpContent
    public final long getLength() {
        return this.file.length();
    }

    @Override // com.google.api.client.http.HttpContent
    public final boolean retrySupported() {
        return true;
    }

    @Override // com.google.api.client.http.AbstractInputStreamContent
    public final InputStream getInputStream() throws FileNotFoundException {
        return new FileInputStream(this.file);
    }

    public final File getFile() {
        return this.file;
    }

    @Override // com.google.api.client.http.AbstractInputStreamContent
    public final FileContent setType(String type) {
        return (FileContent) super.setType(type);
    }

    @Override // com.google.api.client.http.AbstractInputStreamContent
    public final FileContent setCloseInputStream(boolean closeInputStream) {
        return (FileContent) super.setCloseInputStream(closeInputStream);
    }
}
