package com.nuance.nmdp.speechkit.oem;

import android.os.Environment;
import com.nuance.nmdp.speechkit.util.Logger;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/* loaded from: classes.dex */
public class OemFile {
    public static InputStream openForRead(String filename) {
        File infile = new File(Environment.getExternalStorageDirectory(), filename);
        try {
            if (!infile.exists() || !infile.canRead() || ((int) infile.length()) <= 0) {
                return null;
            }
            InputStream stream = new FileInputStream(infile);
            return stream;
        } catch (Throwable tr) {
            Logger.error("OemFile", "Cannot read audio input file: " + filename, tr);
            return null;
        }
    }

    public static OutputStream openForWrite(String filename) {
        File outfile = new File(Environment.getExternalStorageDirectory(), filename);
        try {
            if (outfile.exists() && !outfile.delete()) {
                Logger.error("OemFile", "Can't delete existing audio output file");
            }
            if (outfile.createNewFile()) {
                if (outfile.canWrite()) {
                    OutputStream stream = new FileOutputStream(outfile);
                    return stream;
                }
                Logger.error("OemFile", "Can't write to audio output file");
                return null;
            }
            Logger.error("OemFile", "Can't create audio output file");
            return null;
        } catch (Exception ex) {
            Logger.error("OemFile", "Cannot open audio output file: " + filename, ex);
            return null;
        }
    }
}
