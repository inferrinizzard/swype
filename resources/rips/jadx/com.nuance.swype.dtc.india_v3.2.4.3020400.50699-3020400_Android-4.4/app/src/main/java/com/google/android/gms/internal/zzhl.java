package com.google.android.gms.internal;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import com.facebook.share.internal.ShareConstants;
import org.json.JSONObject;

@zzin
/* loaded from: classes.dex */
public final class zzhl extends Handler {
    private final zzhk zzbwg;

    public zzhl(Context context) {
        this(new zzhm(context.getApplicationContext() != null ? context.getApplicationContext() : context));
    }

    private zzhl(zzhk zzhkVar) {
        this.zzbwg = zzhkVar;
    }

    @Override // android.os.Handler
    public final void handleMessage(Message message) {
        try {
            Bundle data = message.getData();
            if (data == null) {
                return;
            }
            JSONObject jSONObject = new JSONObject(data.getString(ShareConstants.WEB_DIALOG_PARAM_DATA));
            if ("fetch_html".equals(jSONObject.getString("message_name"))) {
                try {
                    zzhk zzhkVar = this.zzbwg;
                    jSONObject.getString("request_id");
                    zzhkVar.zza$14e1ec6d(jSONObject.getString("base_url"), jSONObject.getString("html"));
                } catch (Exception e) {
                }
            }
        } catch (Exception e2) {
        }
    }
}
