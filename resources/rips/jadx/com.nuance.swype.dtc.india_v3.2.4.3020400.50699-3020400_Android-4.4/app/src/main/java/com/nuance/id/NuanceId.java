package com.nuance.id;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import java.lang.reflect.Field;

/* loaded from: classes.dex */
public final class NuanceId {
    private Context context;
    public NuanceIdImpl hashingInternal;
    public String id;
    private int idsUsed;

    public NuanceId(Context context) {
        this(context, (byte) 0);
    }

    private NuanceId(Context context, byte b) {
        this.context = context;
        this.hashingInternal = new NuanceIdImpl();
        this.idsUsed = 15;
    }

    private static String getCompatibilityDeviceProperty(String str) {
        try {
            Field declaredField = Build.class.getDeclaredField(str);
            declaredField.setAccessible(true);
            return declaredField.get(Build.class).toString();
        } catch (Exception e) {
            return null;
        }
    }

    private boolean isIdIncluded(int i) {
        return (this.idsUsed & i) != 0;
    }

    public final String getAndroidId() {
        String str = null;
        if (this.context != null && isIdIncluded(8)) {
            str = Settings.Secure.getString(this.context.getContentResolver(), "android_id");
        }
        return str == null ? "00000000000000000" : str;
    }

    /* JADX WARN: Removed duplicated region for block: B:14:0x0038  */
    /* JADX WARN: Removed duplicated region for block: B:17:0x004a  */
    /* JADX WARN: Removed duplicated region for block: B:19:0x0053  */
    /* JADX WARN: Removed duplicated region for block: B:21:0x0082  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final java.lang.String getId() {
        /*
            r6 = this;
            r1 = 0
            r0 = 1
            r2 = 0
            java.lang.String r3 = r6.id
            if (r3 != 0) goto L7b
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            android.content.Context r4 = r6.context
            if (r4 == 0) goto L84
            boolean r4 = r6.isIdIncluded(r0)
            if (r4 == 0) goto L84
            java.lang.String r4 = "android.permission.READ_PHONE_STATE"
            android.content.Context r5 = r6.context
            if (r5 == 0) goto L80
            android.content.Context r5 = r6.context
            int r4 = r5.checkCallingOrSelfPermission(r4)
            if (r4 != 0) goto L7e
        L25:
            if (r0 == 0) goto L84
            android.content.Context r0 = r6.context
            java.lang.String r2 = "phone"
            java.lang.Object r0 = r0.getSystemService(r2)
            android.telephony.TelephonyManager r0 = (android.telephony.TelephonyManager) r0
            java.lang.String r0 = r0.getDeviceId()
        L36:
            if (r0 != 0) goto L3b
            java.lang.String r0 = "00000000000000000"
        L3b:
            java.lang.String r0 = com.nuance.id.NuanceIdImpl.generateHash(r0)
            java.lang.StringBuilder r2 = r3.append(r0)
            r0 = 2
            boolean r0 = r6.isIdIncluded(r0)
            if (r0 == 0) goto L82
            java.lang.String r0 = "SERIAL"
            java.lang.String r0 = getCompatibilityDeviceProperty(r0)
        L51:
            if (r0 != 0) goto L56
            java.lang.String r0 = "0000000000000000000000"
        L56:
            java.lang.String r0 = com.nuance.id.NuanceIdImpl.generateHash(r0)
            java.lang.StringBuilder r0 = r2.append(r0)
            java.lang.String r1 = "000000000000"
            java.lang.String r1 = com.nuance.id.NuanceIdImpl.generateHash(r1)
            java.lang.StringBuilder r0 = r0.append(r1)
            java.lang.String r1 = r6.getAndroidId()
            java.lang.String r1 = com.nuance.id.NuanceIdImpl.generateHash(r1)
            java.lang.StringBuilder r0 = r0.append(r1)
            java.lang.String r0 = r0.toString()
            r6.id = r0
        L7b:
            java.lang.String r0 = r6.id
            return r0
        L7e:
            r0 = r2
            goto L25
        L80:
            r0 = r2
            goto L25
        L82:
            r0 = r1
            goto L51
        L84:
            r0 = r1
            goto L36
        */
        throw new UnsupportedOperationException("Method not decompiled: com.nuance.id.NuanceId.getId():java.lang.String");
    }
}
