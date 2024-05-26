package com.nuance.swypeconnect.ac;

import android.content.Context;
import com.nuance.connect.util.Logger;
import com.nuance.swypeconnect.ac.ACLanguageDownloadService;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.zip.GZIPInputStream;

/* loaded from: classes.dex */
class ACLdbInfo {
    private static final int ALPHA_CORE = 2;
    private static final int CHINESE_CORE = 10;
    private static final int ET9CONTENTSDEVIATION_ISMINIALM = 9;
    private static final int ET9LDBOFFSET_BODY = 65;
    private static final int ET9LDBOFFSET_CHUNK_COUNT_BYTE = 35;
    private static final int ET9LDBOFFSET_CONTENTSDEVIATION = 55;
    private static final int ET9LDBOFFSET_CONTENTSMAJORVER = 53;
    private static final int ET9LDBOFFSET_DATABASETYPE = 33;
    private static final int ET9LDBOFFSET_LDBLAYOUTVER = 32;
    private static final int ET9LDBOFFSET_PRIMARYLANGID = 57;
    private static final int ET9LDBOFFSET_SECONDARYLANGID = 58;
    private static final int ET9NGRAM_CHUNK_ID = 5;
    private static final int ET9_CP_CONTENT_VER_OFFSET = 42;
    private static final int ET9_CP_LANGUAGE_ID_OFFSET = 40;
    private static final int HEADER_SIZE = 66;
    private static final int SKIP_BUFFER_SIZE = 1024;
    private static final int STREAM_BUFFER_SIZE = 262144;
    private static final Logger.Log log = Logger.getLog(Logger.LoggerType.DEVELOPER, ACLdbInfo.class.getSimpleName());
    private int langId;
    private ACLanguageDownloadService.ACLdbType type;
    private int version;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class PositionInputStream extends FilterInputStream {
        private long pos;

        public PositionInputStream(InputStream inputStream) {
            super(inputStream);
            this.pos = 0L;
        }

        @Override // java.io.FilterInputStream, java.io.InputStream
        public final synchronized int read() throws IOException {
            int read;
            read = super.read();
            if (read >= 0) {
                this.pos++;
            }
            return read;
        }

        @Override // java.io.FilterInputStream, java.io.InputStream
        public final synchronized int read(byte[] bArr, int i, int i2) throws IOException {
            int read;
            read = super.read(bArr, i, i2);
            if (read > 0) {
                this.pos += read;
            }
            return read;
        }

        @Override // java.io.FilterInputStream, java.io.InputStream
        public final synchronized long skip(long j) throws IOException {
            long j2 = 0;
            synchronized (this) {
                int i = ((int) j) % 1024;
                byte[] bArr = new byte[1024];
                for (long j3 = 0; j3 < j / 1024; j3++) {
                    j2 += super.read(bArr);
                }
                if (i > 0) {
                    j2 += super.read(new byte[i]);
                }
            }
            return j2;
        }

        public final synchronized void skipToPosition(long j) throws IOException {
            long j2 = j - this.pos;
            int i = 0;
            while (j2 > 0) {
                j2 -= skip(j2);
                int i2 = i + 1;
                if (i >= 5) {
                    throw new IOException("Failed to skip to position " + j + " after 5 tries.");
                }
                i = i2;
            }
        }
    }

    private ACLdbInfo(int i, ACLanguageDownloadService.ACLdbType aCLdbType, int i2) {
        log.d("ACLdbInfo version=", Integer.valueOf(i), " ACLdbType=", aCLdbType, " langId=0x", Integer.toHexString(i2));
        this.version = i;
        this.type = aCLdbType;
        this.langId = i2;
    }

    private static int getChunkCount(byte[] bArr) throws IOException {
        byte b;
        byte b2 = bArr[32];
        if (b2 < 0 || (b = bArr[35]) < 0) {
            return -1;
        }
        return b2 <= 3 ? b >> 2 : b;
    }

    public static InputStream gzWrapInputStream(InputStream inputStream) {
        PushbackInputStream pushbackInputStream = new PushbackInputStream(inputStream, 2);
        byte[] bArr = new byte[2];
        try {
            int read = pushbackInputStream.read(bArr);
            pushbackInputStream.unread(bArr);
            if (read == 2 && isCompressed(bArr)) {
                inputStream = new GZIPInputStream(pushbackInputStream, 262144);
            } else if (!(inputStream instanceof BufferedInputStream)) {
                inputStream = new BufferedInputStream(pushbackInputStream, 262144);
            }
        } catch (IOException e) {
            log.d("ACLdbInfo.gzWrapInputStream IOException trying to wrap stream.");
        }
        return inputStream;
    }

    public static boolean isCompressed(byte[] bArr) {
        if (bArr == null || bArr.length < 2) {
            return false;
        }
        return bArr[0] == 31 && bArr[1] == -117;
    }

    /* JADX WARN: Removed duplicated region for block: B:89:0x00d5 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:95:? A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:96:0x00d0 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static com.nuance.swypeconnect.ac.ACLdbInfo load(java.io.InputStream r7) {
        /*
            Method dump skipped, instructions count: 276
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.nuance.swypeconnect.ac.ACLdbInfo.load(java.io.InputStream):com.nuance.swypeconnect.ac.ACLdbInfo");
    }

    public static ACLdbInfo load(String str, Context context) {
        InputStream inputStream;
        log.d("load(", str, ")");
        File file = new File(str);
        try {
        } catch (FileNotFoundException e) {
            log.d("ACLdbInfo.load FileNotFoundException for file or asset: ", str);
        } catch (IOException e2) {
            log.d("ACLdbInfo.load IOException for file or asset: ", str);
        }
        try {
            inputStream = !file.exists() ? gzWrapInputStream(context.getAssets().open(str)) : gzWrapInputStream(new FileInputStream(file));
            if (inputStream == null) {
                if (inputStream != null) {
                    inputStream.close();
                }
                return null;
            }
            try {
                ACLdbInfo load = load(inputStream);
                if (inputStream == null) {
                    return load;
                }
                inputStream.close();
                return load;
            } catch (Throwable th) {
                th = th;
                if (inputStream != null) {
                    inputStream.close();
                }
                throw th;
            }
        } catch (Throwable th2) {
            th = th2;
            inputStream = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ACLanguageDownloadService.ACLdbType getType() {
        return this.type == null ? ACLanguageDownloadService.ACLdbType.Unspecified : this.type;
    }

    public int getVersion() {
        return this.version;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int getXT9LanguageId() {
        return this.langId;
    }
}
