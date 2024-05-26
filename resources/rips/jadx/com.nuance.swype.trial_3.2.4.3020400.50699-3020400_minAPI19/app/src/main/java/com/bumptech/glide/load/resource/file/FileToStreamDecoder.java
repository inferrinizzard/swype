package com.bumptech.glide.load.resource.file;

import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.engine.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/* loaded from: classes.dex */
public final class FileToStreamDecoder<T> implements ResourceDecoder<File, T> {
    private static final FileOpener DEFAULT_FILE_OPENER = new FileOpener();
    private final FileOpener fileOpener;
    private ResourceDecoder<InputStream, T> streamDecoder;

    public FileToStreamDecoder(ResourceDecoder<InputStream, T> streamDecoder) {
        this(streamDecoder, DEFAULT_FILE_OPENER);
    }

    private FileToStreamDecoder(ResourceDecoder<InputStream, T> streamDecoder, FileOpener fileOpener) {
        this.streamDecoder = streamDecoder;
        this.fileOpener = fileOpener;
    }

    /* JADX INFO: Access modifiers changed from: private */
    @Override // com.bumptech.glide.load.ResourceDecoder
    public Resource<T> decode(File source, int width, int height) throws IOException {
        InputStream is = null;
        try {
            InputStream is2 = new FileInputStream(source);
            try {
                Resource<T> result = this.streamDecoder.decode(is2, width, height);
                try {
                    is2.close();
                } catch (IOException e) {
                }
                return result;
            } catch (Throwable th) {
                th = th;
                is = is2;
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e2) {
                    }
                }
                throw th;
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    @Override // com.bumptech.glide.load.ResourceDecoder
    public final String getId() {
        return "";
    }

    /* loaded from: classes.dex */
    static class FileOpener {
        FileOpener() {
        }
    }
}
