package com.nuance.input.swypecorelib;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.util.Log;
import java.io.FileDescriptor;
import java.io.IOException;

/* loaded from: classes.dex */
public class ApkFileReader {
    static final /* synthetic */ boolean $assertionsDisabled;
    private static final int INDEX_FILE_DESCRIPTOR = 0;
    private static final int INDEX_IS_COMPRESSED = 3;
    private static final int INDEX_OFFSET = 1;
    private static final int INDEX_SIZE = 2;
    private static final int NUM_OF_PROPERTIES = 4;
    private static final String TAG = "ApkFileReader";
    private final AssetFileDescriptor currentAssetFd;
    private final boolean isCompressed;

    static {
        $assertionsDisabled = !ApkFileReader.class.desiredAssertionStatus();
    }

    private ApkFileReader(AssetFileDescriptor fd, boolean isCompressed) {
        if (!$assertionsDisabled && fd == null) {
            throw new AssertionError();
        }
        this.currentAssetFd = fd;
        this.isCompressed = isCompressed;
    }

    public static ApkFileReader create(Context context, String assetFileName) {
        if (assetFileName == null) {
            return null;
        }
        AssetFileDescriptor fd = null;
        try {
            try {
                return new ApkFileReader(context.getAssets().openFd(assetFileName + SwypeCoreLibrary.COMPRESSED_FILE_EXTENSION), true);
            } catch (Throwable th) {
                if (fd != null) {
                    try {
                        fd.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                throw th;
            }
        } catch (IOException e2) {
            try {
                fd = context.getAssets().openFd(assetFileName);
                ApkFileReader apkFileReader = fd != null ? new ApkFileReader(fd, false) : null;
                if (fd != null) {
                    try {
                        fd.close();
                    } catch (IOException e3) {
                        e3.printStackTrace();
                    }
                }
                return apkFileReader;
            } catch (IOException ex) {
                Log.e(TAG, "openFd", ex);
                if (fd == null) {
                    return null;
                }
                try {
                    fd.close();
                    return null;
                } catch (IOException e4) {
                    e4.printStackTrace();
                    return null;
                }
            }
        }
    }

    public long[] getProperties() {
        long[] prop = new long[4];
        if (this.currentAssetFd != null) {
            FileDescriptor fileDescriptor = this.currentAssetFd.getFileDescriptor();
            prop[0] = XT9CoreInput.getFdFromFileDescriptor(fileDescriptor);
            prop[1] = this.currentAssetFd.getStartOffset();
            prop[2] = this.currentAssetFd.getLength();
            prop[3] = this.isCompressed ? 1L : 0L;
            return prop;
        }
        return null;
    }

    public void close() {
        if (this.currentAssetFd != null) {
            try {
                this.currentAssetFd.close();
            } catch (IOException ex) {
                Log.e(TAG, "close()", ex);
            }
        }
    }
}
