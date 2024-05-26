package com.bumptech.glide.load.engine;

import android.util.Log;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.Encoder;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.engine.cache.DiskCache;
import com.bumptech.glide.load.resource.transcode.ResourceTranscoder;
import com.bumptech.glide.provider.DataLoadProvider;
import com.bumptech.glide.util.LogTime;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/* loaded from: classes.dex */
public final class DecodeJob<A, T, Z> {
    private static final FileOpener DEFAULT_FILE_OPENER = new FileOpener();
    private final DiskCacheProvider diskCacheProvider;
    final DiskCacheStrategy diskCacheStrategy;
    public final DataFetcher<A> fetcher;
    private final FileOpener fileOpener;
    private final int height;
    public volatile boolean isCancelled;
    private final DataLoadProvider<A, T> loadProvider;
    private final Priority priority;
    final EngineKey resultKey;
    private final ResourceTranscoder<T, Z> transcoder;
    private final Transformation<T> transformation;
    private final int width;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public interface DiskCacheProvider {
        DiskCache getDiskCache();
    }

    public DecodeJob(EngineKey resultKey, int width, int height, DataFetcher<A> fetcher, DataLoadProvider<A, T> loadProvider, Transformation<T> transformation, ResourceTranscoder<T, Z> transcoder, DiskCacheProvider diskCacheProvider, DiskCacheStrategy diskCacheStrategy, Priority priority) {
        this(resultKey, width, height, fetcher, loadProvider, transformation, transcoder, diskCacheProvider, diskCacheStrategy, priority, DEFAULT_FILE_OPENER);
    }

    private DecodeJob(EngineKey resultKey, int width, int height, DataFetcher<A> fetcher, DataLoadProvider<A, T> loadProvider, Transformation<T> transformation, ResourceTranscoder<T, Z> transcoder, DiskCacheProvider diskCacheProvider, DiskCacheStrategy diskCacheStrategy, Priority priority, FileOpener fileOpener) {
        this.resultKey = resultKey;
        this.width = width;
        this.height = height;
        this.fetcher = fetcher;
        this.loadProvider = loadProvider;
        this.transformation = transformation;
        this.transcoder = transcoder;
        this.diskCacheProvider = diskCacheProvider;
        this.diskCacheStrategy = diskCacheStrategy;
        this.priority = priority;
        this.fileOpener = fileOpener;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Resource<Z> transformEncodeAndTranscode(Resource<T> decoded) {
        Resource<T> transformed;
        long startTime = LogTime.getLogTime();
        if (decoded == null) {
            transformed = null;
        } else {
            transformed = this.transformation.transform(decoded, this.width, this.height);
            if (!decoded.equals(transformed)) {
                decoded.recycle();
            }
        }
        if (Log.isLoggable("DecodeJob", 2)) {
            logWithTimeAndKey("Transformed resource from source", startTime);
        }
        if (transformed != null && this.diskCacheStrategy.cacheResult) {
            long logTime = LogTime.getLogTime();
            this.diskCacheProvider.getDiskCache().put(this.resultKey, new SourceWriter(this.loadProvider.getEncoder(), transformed));
            if (Log.isLoggable("DecodeJob", 2)) {
                logWithTimeAndKey("Wrote transformed from source to cache", logTime);
            }
        }
        long startTime2 = LogTime.getLogTime();
        Resource<Z> result = transcode(transformed);
        if (Log.isLoggable("DecodeJob", 2)) {
            logWithTimeAndKey("Transcoded transformed from source", startTime2);
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Resource<T> decodeSource() throws Exception {
        Resource<T> decoded;
        try {
            long startTime = LogTime.getLogTime();
            A data = this.fetcher.loadData(this.priority);
            if (Log.isLoggable("DecodeJob", 2)) {
                logWithTimeAndKey("Fetched data", startTime);
            }
            if (this.isCancelled) {
                this.fetcher.cleanup();
                return null;
            }
            if (!this.diskCacheStrategy.cacheSource) {
                long logTime = LogTime.getLogTime();
                decoded = this.loadProvider.getSourceDecoder().decode(data, this.width, this.height);
                if (Log.isLoggable("DecodeJob", 2)) {
                    logWithTimeAndKey("Decoded from source", logTime);
                }
            } else {
                long logTime2 = LogTime.getLogTime();
                this.diskCacheProvider.getDiskCache().put(this.resultKey.getOriginalKey(), new SourceWriter(this.loadProvider.getSourceEncoder(), data));
                if (Log.isLoggable("DecodeJob", 2)) {
                    logWithTimeAndKey("Wrote source to cache", logTime2);
                }
                long logTime3 = LogTime.getLogTime();
                decoded = loadFromCache(this.resultKey.getOriginalKey());
                if (Log.isLoggable("DecodeJob", 2) && decoded != null) {
                    logWithTimeAndKey("Decoded source from cache", logTime3);
                }
            }
            return decoded;
        } finally {
            this.fetcher.cleanup();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Resource<T> loadFromCache(Key key) throws IOException {
        File cacheFile = this.diskCacheProvider.getDiskCache().get(key);
        if (cacheFile == null) {
            return null;
        }
        try {
            Resource<T> result = this.loadProvider.getCacheDecoder().decode(cacheFile, this.width, this.height);
            return result == null ? result : result;
        } finally {
            this.diskCacheProvider.getDiskCache().delete(key);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Resource<Z> transcode(Resource<T> transformed) {
        if (transformed == null) {
            return null;
        }
        return this.transcoder.transcode(transformed);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void logWithTimeAndKey(String message, long startTime) {
        Log.v("DecodeJob", message + " in " + LogTime.getElapsedMillis(startTime) + ", key: " + this.resultKey);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class SourceWriter<DataType> implements DiskCache.Writer {
        private final DataType data;
        private final Encoder<DataType> encoder;

        public SourceWriter(Encoder<DataType> encoder, DataType data) {
            this.encoder = encoder;
            this.data = data;
        }

        @Override // com.bumptech.glide.load.engine.cache.DiskCache.Writer
        public final boolean write(File file) {
            boolean success = false;
            OutputStream os = null;
            try {
                try {
                    FileOpener unused = DecodeJob.this.fileOpener;
                    OutputStream os2 = new BufferedOutputStream(new FileOutputStream(file));
                    try {
                        success = this.encoder.encode(this.data, os2);
                        try {
                            os2.close();
                            os = os2;
                        } catch (IOException e) {
                            os = os2;
                        }
                    } catch (FileNotFoundException e2) {
                        os = os2;
                        Log.isLoggable("DecodeJob", 3);
                        if (os != null) {
                            try {
                                os.close();
                            } catch (IOException e3) {
                            }
                        }
                        return success;
                    } catch (Throwable th) {
                        th = th;
                        os = os2;
                        if (os != null) {
                            try {
                                os.close();
                            } catch (IOException e4) {
                            }
                        }
                        throw th;
                    }
                } catch (FileNotFoundException e5) {
                }
                return success;
            } catch (Throwable th2) {
                th = th2;
            }
        }
    }

    /* loaded from: classes.dex */
    static class FileOpener {
        FileOpener() {
        }
    }
}
