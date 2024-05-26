package com.nuance.connect.internal;

import android.content.Context;
import android.util.SparseArray;
import com.nuance.connect.util.Logger;
import java.io.IOException;
import java.util.Map;

/* loaded from: classes.dex */
public class AssetsLegalDocuments {
    private static final SparseArray<String> documentTypeMapping;
    private static final Logger.Log log = Logger.getLog(Logger.LoggerType.DEVELOPER, AssetsLegalDocuments.class.getSimpleName());
    private final Context context;
    private final SparseArray<Map<String, String>> documentFiles = new SparseArray<>();

    static {
        SparseArray<String> sparseArray = new SparseArray<>();
        documentTypeMapping = sparseArray;
        sparseArray.put(1, "TOS");
        documentTypeMapping.put(2, "EULA");
        documentTypeMapping.put(3, "PRIVACY");
        documentTypeMapping.put(4, "USAGE");
    }

    public AssetsLegalDocuments(Context context) {
        this.context = context;
    }

    private void loadDocumentFiles(int i) {
        String str = documentTypeMapping.get(i);
        log.d("loadDocumentFiles(", str, ")");
        try {
            for (String str2 : this.context.getAssets().list(str)) {
                if (str2.endsWith(".html")) {
                    String[] split = str2.split("\\.");
                    log.d("loadDocumentFiles(", split[0], " : ", str2);
                    this.documentFiles.get(i).put(split[0], str + "/" + str2);
                }
            }
        } catch (IOException e) {
            log.e(e.getMessage());
        }
        log.d("loadDocumentFiles(", str, "): found ", Integer.valueOf(this.documentFiles.get(i).size()), " docs");
    }

    /* JADX WARN: Removed duplicated region for block: B:25:0x0084  */
    /* JADX WARN: Removed duplicated region for block: B:29:0x0095  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public java.lang.String getText(int r9, java.lang.String r10) {
        /*
            r8 = this;
            r2 = 0
            android.util.SparseArray<java.util.Map<java.lang.String, java.lang.String>> r0 = r8.documentFiles
            java.lang.Object r0 = r0.valueAt(r9)
            if (r0 != 0) goto L16
            android.util.SparseArray<java.util.Map<java.lang.String, java.lang.String>> r0 = r8.documentFiles
            java.util.HashMap r1 = new java.util.HashMap
            r1.<init>()
            r0.put(r9, r1)
            r8.loadDocumentFiles(r9)
        L16:
            android.util.SparseArray<java.util.Map<java.lang.String, java.lang.String>> r0 = r8.documentFiles
            java.lang.Object r0 = r0.get(r9)
            java.util.Map r0 = (java.util.Map) r0
            if (r0 == 0) goto L9b
            java.lang.String r3 = r10.toUpperCase()
            java.lang.Object r1 = r0.get(r3)
            java.lang.String r1 = (java.lang.String) r1
        L2a:
            if (r1 != 0) goto L41
            r4 = 95
            int r4 = r3.lastIndexOf(r4)
            r5 = -1
            if (r4 == r5) goto L41
            r1 = 0
            java.lang.String r3 = r3.substring(r1, r4)
            java.lang.Object r1 = r0.get(r3)
            java.lang.String r1 = (java.lang.String) r1
            goto L2a
        L41:
            if (r1 == 0) goto L9b
            java.io.BufferedReader r3 = new java.io.BufferedReader     // Catch: java.io.IOException -> L66
            java.io.InputStreamReader r0 = new java.io.InputStreamReader     // Catch: java.io.IOException -> L66
            android.content.Context r4 = r8.context     // Catch: java.io.IOException -> L66
            android.content.res.AssetManager r4 = r4.getAssets()     // Catch: java.io.IOException -> L66
            java.io.InputStream r1 = r4.open(r1)     // Catch: java.io.IOException -> L66
            r0.<init>(r1)     // Catch: java.io.IOException -> L66
            r3.<init>(r0)     // Catch: java.io.IOException -> L66
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch: java.io.IOException -> L66
            r0.<init>()     // Catch: java.io.IOException -> L66
        L5c:
            java.lang.String r1 = r3.readLine()     // Catch: java.io.IOException -> L66
            if (r1 == 0) goto L8c
            r0.append(r1)     // Catch: java.io.IOException -> L66
            goto L5c
        L66:
            r0 = move-exception
            r1 = r0
            r0 = r2
        L69:
            com.nuance.connect.util.Logger$Log r2 = com.nuance.connect.internal.AssetsLegalDocuments.log
            java.lang.String r1 = r1.getMessage()
            r2.e(r1)
            r7 = r0
        L73:
            com.nuance.connect.util.Logger$Log r0 = com.nuance.connect.internal.AssetsLegalDocuments.log
            java.lang.String r1 = "getText("
            java.lang.Integer r2 = java.lang.Integer.valueOf(r9)
            java.lang.String r3 = ", "
            java.lang.String r5 = ") "
            if (r7 != 0) goto L95
            java.lang.String r6 = " not found"
        L87:
            r4 = r10
            r0.d(r1, r2, r3, r4, r5, r6)
            return r7
        L8c:
            java.lang.String r0 = r0.toString()     // Catch: java.io.IOException -> L66
            r3.close()     // Catch: java.io.IOException -> L99
            r7 = r0
            goto L73
        L95:
            java.lang.String r6 = " found"
            goto L87
        L99:
            r1 = move-exception
            goto L69
        L9b:
            r7 = r2
            goto L73
        */
        throw new UnsupportedOperationException("Method not decompiled: com.nuance.connect.internal.AssetsLegalDocuments.getText(int, java.lang.String):java.lang.String");
    }
}
