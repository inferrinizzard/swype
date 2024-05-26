package com.bumptech.glide.load.resource.gif;

import com.bumptech.glide.Priority;
import com.bumptech.glide.gifdecoder.GifDecoder;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.model.ModelLoader;

/* loaded from: classes.dex */
final class GifFrameModelLoader implements ModelLoader<GifDecoder, GifDecoder> {
    @Override // com.bumptech.glide.load.model.ModelLoader
    public final /* bridge */ /* synthetic */ DataFetcher<GifDecoder> getResourceFetcher(GifDecoder gifDecoder, int x1, int x2) {
        return new GifFrameDataFetcher(gifDecoder);
    }

    /* loaded from: classes.dex */
    private static class GifFrameDataFetcher implements DataFetcher<GifDecoder> {
        private final GifDecoder decoder;

        public GifFrameDataFetcher(GifDecoder decoder) {
            this.decoder = decoder;
        }

        @Override // com.bumptech.glide.load.data.DataFetcher
        public final void cleanup() {
        }

        @Override // com.bumptech.glide.load.data.DataFetcher
        public final String getId() {
            return String.valueOf(this.decoder.framePointer);
        }

        @Override // com.bumptech.glide.load.data.DataFetcher
        public final void cancel() {
        }

        @Override // com.bumptech.glide.load.data.DataFetcher
        public final /* bridge */ /* synthetic */ GifDecoder loadData(Priority x0) throws Exception {
            return this.decoder;
        }
    }
}
