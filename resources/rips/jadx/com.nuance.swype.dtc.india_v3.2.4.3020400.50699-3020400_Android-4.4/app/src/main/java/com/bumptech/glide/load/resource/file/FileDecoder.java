package com.bumptech.glide.load.resource.file;

import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.engine.Resource;
import java.io.File;
import java.io.IOException;

/* loaded from: classes.dex */
public final class FileDecoder implements ResourceDecoder<File, File> {
    @Override // com.bumptech.glide.load.ResourceDecoder
    public final /* bridge */ /* synthetic */ Resource<File> decode(File file, int x1, int x2) throws IOException {
        return new FileResource(file);
    }

    @Override // com.bumptech.glide.load.ResourceDecoder
    public final String getId() {
        return "";
    }
}
