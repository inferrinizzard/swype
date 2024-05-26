package com.crashlytics.android;

import com.nuance.nmsp.client.util.internal.dictationresult.parser.xml.xmlResults.XMLResultsHandler;
import io.fabric.sdk.android.Fabric;
import io.fabric.sdk.android.services.common.CommonUtils;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class ExceptionUtils {
    public static void writeStackTraceIfNotNull(Throwable ex, OutputStream os) {
        PrintWriter printWriter;
        if (os != null) {
            try {
                try {
                    printWriter = new PrintWriter(os);
                } catch (Exception e) {
                    e = e;
                    printWriter = null;
                } catch (Throwable th) {
                    th = th;
                    printWriter = null;
                    CommonUtils.closeOrLog(printWriter, "Failed to close stack trace writer.");
                    throw th;
                }
                try {
                    writeStackTrace(ex, printWriter);
                    CommonUtils.closeOrLog(printWriter, "Failed to close stack trace writer.");
                } catch (Exception e2) {
                    e = e2;
                    Fabric.getLogger().e("Fabric", "Failed to create PrintWriter", e);
                    CommonUtils.closeOrLog(printWriter, "Failed to close stack trace writer.");
                }
            } catch (Throwable th2) {
                th = th2;
                CommonUtils.closeOrLog(printWriter, "Failed to close stack trace writer.");
                throw th;
            }
        }
    }

    private static void writeStackTrace(Throwable ex, Writer writer) {
        String message;
        boolean first = true;
        while (ex != null) {
            try {
                String localizedMessage = ex.getLocalizedMessage();
                if (localizedMessage == null) {
                    message = null;
                } else {
                    message = localizedMessage.replaceAll("(\r\n|\n|\f)", XMLResultsHandler.SEP_SPACE);
                }
                if (message == null) {
                    message = "";
                }
                String causedBy = first ? "" : "Caused by: ";
                writer.write(causedBy + ex.getClass().getName() + ": " + message + "\n");
                first = false;
                StackTraceElement[] arr$ = ex.getStackTrace();
                for (StackTraceElement element : arr$) {
                    writer.write("\tat " + element.toString() + "\n");
                }
                ex = ex.getCause();
            } catch (Exception e) {
                Fabric.getLogger().e("Fabric", "Could not write stack trace", e);
                return;
            }
        }
    }
}
