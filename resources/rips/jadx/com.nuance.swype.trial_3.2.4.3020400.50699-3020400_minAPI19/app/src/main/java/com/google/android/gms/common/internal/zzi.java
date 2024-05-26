package com.google.android.gms.common.internal;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import com.google.android.gms.internal.zzqk;

/* loaded from: classes.dex */
public abstract class zzi implements DialogInterface.OnClickListener {
    public static zzi zza(final Activity activity, final Intent intent, final int i) {
        return new zzi() { // from class: com.google.android.gms.common.internal.zzi.1
            @Override // com.google.android.gms.common.internal.zzi
            public final void zzasr() {
                if (intent != null) {
                    activity.startActivityForResult(intent, i);
                }
            }
        };
    }

    public static zzi zza$5fe6cfb0(zzqk zzqkVar, Intent intent) {
        return new zzi(intent, zzqkVar, 2) { // from class: com.google.android.gms.common.internal.zzi.3
            final /* synthetic */ Intent val$intent;
            final /* synthetic */ int val$requestCode = 2;
            final /* synthetic */ zzqk yp;

            @Override // com.google.android.gms.common.internal.zzi
            @TargetApi(11)
            public final void zzasr() {
                if (this.val$intent != null) {
                    this.yp.startActivityForResult(this.val$intent, this.val$requestCode);
                }
            }
        };
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialogInterface, int i) {
        try {
            zzasr();
            dialogInterface.dismiss();
        } catch (ActivityNotFoundException e) {
            Log.e("DialogRedirect", "Can't redirect to app settings for Google Play services", e);
        }
    }

    public abstract void zzasr();
}
