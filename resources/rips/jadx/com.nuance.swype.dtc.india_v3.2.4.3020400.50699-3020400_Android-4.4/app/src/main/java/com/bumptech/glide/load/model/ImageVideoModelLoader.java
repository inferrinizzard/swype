package com.bumptech.glide.load.model;

import android.os.ParcelFileDescriptor;
import android.util.Log;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.data.DataFetcher;
import java.io.InputStream;

/* loaded from: classes.dex */
public final class ImageVideoModelLoader<A> implements ModelLoader<A, ImageVideoWrapper> {
    private final ModelLoader<A, ParcelFileDescriptor> fileDescriptorLoader;
    private final ModelLoader<A, InputStream> streamLoader;

    public ImageVideoModelLoader(ModelLoader<A, InputStream> streamLoader, ModelLoader<A, ParcelFileDescriptor> fileDescriptorLoader) {
        if (streamLoader == null && fileDescriptorLoader == null) {
            throw new NullPointerException("At least one of streamLoader and fileDescriptorLoader must be non null");
        }
        this.streamLoader = streamLoader;
        this.fileDescriptorLoader = fileDescriptorLoader;
    }

    @Override // com.bumptech.glide.load.model.ModelLoader
    public final DataFetcher<ImageVideoWrapper> getResourceFetcher(A model, int width, int height) {
        DataFetcher<InputStream> streamFetcher = null;
        if (this.streamLoader != null) {
            streamFetcher = this.streamLoader.getResourceFetcher(model, width, height);
        }
        DataFetcher<ParcelFileDescriptor> fileDescriptorFetcher = null;
        if (this.fileDescriptorLoader != null) {
            fileDescriptorFetcher = this.fileDescriptorLoader.getResourceFetcher(model, width, height);
        }
        if (streamFetcher == null && fileDescriptorFetcher == null) {
            return null;
        }
        return new ImageVideoFetcher(streamFetcher, fileDescriptorFetcher);
    }

    /* loaded from: classes.dex */
    static class ImageVideoFetcher implements DataFetcher<ImageVideoWrapper> {
        private final DataFetcher<ParcelFileDescriptor> fileDescriptorFetcher;
        private final DataFetcher<InputStream> streamFetcher;

        public ImageVideoFetcher(DataFetcher<InputStream> streamFetcher, DataFetcher<ParcelFileDescriptor> fileDescriptorFetcher) {
            this.streamFetcher = streamFetcher;
            this.fileDescriptorFetcher = fileDescriptorFetcher;
        }

        /* JADX INFO: Access modifiers changed from: private */
        @Override // com.bumptech.glide.load.data.DataFetcher
        public ImageVideoWrapper loadData(Priority priority) throws Exception {
            InputStream is = null;
            if (this.streamFetcher != null) {
                try {
                    is = this.streamFetcher.loadData(priority);
                } catch (Exception e) {
                    if (Log.isLoggable("IVML", 2)) {
                        Log.v("IVML", "Exception fetching input stream, trying ParcelFileDescriptor", e);
                    }
                    if (this.fileDescriptorFetcher == null) {
                        throw e;
                    }
                }
            }
            ParcelFileDescriptor fileDescriptor = null;
            if (this.fileDescriptorFetcher != null) {
                try {
                    fileDescriptor = this.fileDescriptorFetcher.loadData(priority);
                } catch (Exception e2) {
                    if (Log.isLoggable("IVML", 2)) {
                        Log.v("IVML", "Exception fetching ParcelFileDescriptor", e2);
                    }
                    if (is == null) {
                        throw e2;
                    }
                }
            }
            return new ImageVideoWrapper(is, fileDescriptor);
        }

        @Override // com.bumptech.glide.load.data.DataFetcher
        public final void cleanup() {
            if (this.streamFetcher != null) {
                this.streamFetcher.cleanup();
            }
            if (this.fileDescriptorFetcher != null) {
                this.fileDescriptorFetcher.cleanup();
            }
        }

        @Override // com.bumptech.glide.load.data.DataFetcher
        public final String getId() {
            return this.streamFetcher != null ? this.streamFetcher.getId() : this.fileDescriptorFetcher.getId();
        }

        @Override // com.bumptech.glide.load.data.DataFetcher
        public final void cancel() {
            if (this.streamFetcher != null) {
                this.streamFetcher.cancel();
            }
            if (this.fileDescriptorFetcher != null) {
                this.fileDescriptorFetcher.cancel();
            }
        }
    }
}
