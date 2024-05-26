package com.google.android.gms.internal;

import android.content.Context;
import android.text.TextUtils;
import java.util.Map;

@zzin
/* loaded from: classes.dex */
public final class zzgz extends zzhf {
    final Context mContext;
    private final Map<String, String> zzbeg;
    String zzbpy;
    long zzbpz;
    long zzbqa;
    String zzbqb;
    String zzbqc;

    private String zzbq(String str) {
        return TextUtils.isEmpty(this.zzbeg.get(str)) ? "" : this.zzbeg.get(str);
    }

    private long zzbr(String str) {
        String str2 = this.zzbeg.get(str);
        if (str2 == null) {
            return -1L;
        }
        try {
            return Long.parseLong(str2);
        } catch (NumberFormatException e) {
            return -1L;
        }
    }

    public zzgz(zzlh zzlhVar, Map<String, String> map) {
        super(zzlhVar, "createCalendarEvent");
        this.zzbeg = map;
        this.mContext = zzlhVar.zzue();
        this.zzbpy = zzbq("description");
        this.zzbqb = zzbq("summary");
        this.zzbpz = zzbr("start_ticks");
        this.zzbqa = zzbr("end_ticks");
        this.zzbqc = zzbq("location");
    }
}
