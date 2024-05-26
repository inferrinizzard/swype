package android.support.multidex;

import com.nuance.swype.input.hardkey.HardKeyboardManager;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.zip.CRC32;
import java.util.zip.ZipException;

/* loaded from: classes.dex */
final class ZipUtil {

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class CentralDirectory {
        long offset;
        long size;

        CentralDirectory() {
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static long getZipCrc(File apk) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(apk, "r");
        try {
            CentralDirectory dir = findCentralDirectory(raf);
            return computeCrcOfCentralDir(raf, dir);
        } finally {
            raf.close();
        }
    }

    private static CentralDirectory findCentralDirectory(RandomAccessFile raf) throws IOException, ZipException {
        long scanOffset = raf.length() - 22;
        if (scanOffset < 0) {
            throw new ZipException("File too short to be a zip file: " + raf.length());
        }
        long stopOffset = scanOffset - 65536;
        if (stopOffset < 0) {
            stopOffset = 0;
        }
        int endSig = Integer.reverseBytes(101010256);
        do {
            raf.seek(scanOffset);
            if (raf.readInt() != endSig) {
                scanOffset--;
            } else {
                raf.skipBytes(2);
                raf.skipBytes(2);
                raf.skipBytes(2);
                raf.skipBytes(2);
                CentralDirectory dir = new CentralDirectory();
                dir.size = Integer.reverseBytes(raf.readInt()) & 4294967295L;
                dir.offset = Integer.reverseBytes(raf.readInt()) & 4294967295L;
                return dir;
            }
        } while (scanOffset >= stopOffset);
        throw new ZipException("End Of Central Directory signature not found");
    }

    private static long computeCrcOfCentralDir(RandomAccessFile raf, CentralDirectory dir) throws IOException {
        CRC32 crc = new CRC32();
        long stillToRead = dir.size;
        raf.seek(dir.offset);
        int length = (int) Math.min(16384L, stillToRead);
        byte[] buffer = new byte[HardKeyboardManager.META_CTRL_RIGHT_ON];
        int length2 = raf.read(buffer, 0, length);
        while (length2 != -1) {
            crc.update(buffer, 0, length2);
            stillToRead -= length2;
            if (stillToRead == 0) {
                break;
            }
            int length3 = (int) Math.min(16384L, stillToRead);
            length2 = raf.read(buffer, 0, length3);
        }
        return crc.getValue();
    }
}
