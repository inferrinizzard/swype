package com.bumptech.glide;

import android.os.ParcelFileDescriptor;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.model.ModelLoader;
import java.io.InputStream;

/* loaded from: classes.dex */
public final class DrawableTypeRequest<ModelType> extends DrawableRequestBuilder<ModelType> {
    private final ModelLoader<ModelType, ParcelFileDescriptor> fileDescriptorModelLoader;
    private final RequestManager.OptionsApplier optionsApplier;
    private final ModelLoader<ModelType, InputStream> streamModelLoader;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Illegal instructions before constructor call */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public DrawableTypeRequest(java.lang.Class<ModelType> r8, com.bumptech.glide.load.model.ModelLoader<ModelType, java.io.InputStream> r9, com.bumptech.glide.load.model.ModelLoader<ModelType, android.os.ParcelFileDescriptor> r10, android.content.Context r11, com.bumptech.glide.Glide r12, com.bumptech.glide.manager.RequestTracker r13, com.bumptech.glide.manager.Lifecycle r14, com.bumptech.glide.RequestManager.OptionsApplier r15) {
        /*
            r7 = this;
            java.lang.Class<com.bumptech.glide.load.resource.gifbitmap.GifBitmapWrapper> r0 = com.bumptech.glide.load.resource.gifbitmap.GifBitmapWrapper.class
            java.lang.Class<com.bumptech.glide.load.resource.drawable.GlideDrawable> r1 = com.bumptech.glide.load.resource.drawable.GlideDrawable.class
            if (r9 != 0) goto L19
            if (r10 != 0) goto L19
            r3 = 0
        L9:
            r0 = r7
            r1 = r11
            r2 = r8
            r4 = r12
            r5 = r13
            r6 = r14
            r0.<init>(r1, r2, r3, r4, r5, r6)
            r7.streamModelLoader = r9
            r7.fileDescriptorModelLoader = r10
            r7.optionsApplier = r15
            return
        L19:
            com.bumptech.glide.load.resource.transcode.TranscoderRegistry r2 = r12.transcoderRegistry
            com.bumptech.glide.load.resource.transcode.ResourceTranscoder r1 = r2.get(r0, r1)
            java.lang.Class<com.bumptech.glide.load.model.ImageVideoWrapper> r2 = com.bumptech.glide.load.model.ImageVideoWrapper.class
            com.bumptech.glide.provider.DataLoadProvider r0 = r12.buildDataProvider(r2, r0)
            com.bumptech.glide.load.model.ImageVideoModelLoader r2 = new com.bumptech.glide.load.model.ImageVideoModelLoader
            r2.<init>(r9, r10)
            com.bumptech.glide.provider.FixedLoadProvider r3 = new com.bumptech.glide.provider.FixedLoadProvider
            r3.<init>(r2, r1, r0)
            goto L9
        */
        throw new UnsupportedOperationException("Method not decompiled: com.bumptech.glide.DrawableTypeRequest.<init>(java.lang.Class, com.bumptech.glide.load.model.ModelLoader, com.bumptech.glide.load.model.ModelLoader, android.content.Context, com.bumptech.glide.Glide, com.bumptech.glide.manager.RequestTracker, com.bumptech.glide.manager.Lifecycle, com.bumptech.glide.RequestManager$OptionsApplier):void");
    }
}
