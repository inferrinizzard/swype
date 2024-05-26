package com.google.android.gms.common;

import android.R;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.FragmentActivity;
import android.util.TypedValue;
import android.widget.ProgressBar;
import com.google.android.gms.common.api.GoogleApiActivity;
import com.google.android.gms.common.internal.zzh;
import com.google.android.gms.common.internal.zzi;
import com.google.android.gms.common.util.zzs;
import com.google.android.gms.internal.zzqe;
import com.google.android.gms.internal.zzqk;

/* loaded from: classes.dex */
public final class GoogleApiAvailability extends zzc {
    private static final GoogleApiAvailability re = new GoogleApiAvailability();
    public static final int GOOGLE_PLAY_SERVICES_VERSION_CODE = zzc.GOOGLE_PLAY_SERVICES_VERSION_CODE;

    GoogleApiAvailability() {
    }

    public static GoogleApiAvailability getInstance() {
        return re;
    }

    public static Dialog zza(Activity activity, DialogInterface.OnCancelListener onCancelListener) {
        ProgressBar progressBar = new ProgressBar(activity, null, R.attr.progressBarStyleLarge);
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(0);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setView(progressBar);
        builder.setMessage(activity.getResources().getString(com.google.android.gms.R.string.common_google_play_services_updating_text, GooglePlayServicesUtil.zzbv(activity)));
        builder.setTitle(com.google.android.gms.R.string.common_google_play_services_updating_title);
        builder.setPositiveButton("", (DialogInterface.OnClickListener) null);
        AlertDialog create = builder.create();
        zza(activity, create, "GooglePlayServicesUpdatingDialog", onCancelListener);
        return create;
    }

    public final Dialog getErrorDialog$2675af88(Activity activity, int i) {
        return getErrorDialog(activity, i, 100, null);
    }

    @Override // com.google.android.gms.common.zzc
    public final PendingIntent getErrorResolutionPendingIntent(Context context, int i, int i2) {
        return super.getErrorResolutionPendingIntent(context, i, i2);
    }

    @Override // com.google.android.gms.common.zzc
    public final int isGooglePlayServicesAvailable(Context context) {
        return super.isGooglePlayServicesAvailable(context);
    }

    @Override // com.google.android.gms.common.zzc
    public final boolean isUserResolvableError(int i) {
        return super.isUserResolvableError(i);
    }

    public final boolean showErrorDialogFragment$1887901c(Activity activity, int i, DialogInterface.OnCancelListener onCancelListener) {
        Dialog errorDialog = getErrorDialog(activity, i, 2, onCancelListener);
        if (errorDialog == null) {
            return false;
        }
        zza(activity, errorDialog, "GooglePlayServicesErrorDialog", onCancelListener);
        return true;
    }

    @Override // com.google.android.gms.common.zzc
    public final PendingIntent zza(Context context, int i, int i2, String str) {
        return super.zza(context, i, i2, str);
    }

    @Override // com.google.android.gms.common.zzc
    public final Intent zza(Context context, int i, String str) {
        return super.zza(context, i, str);
    }

    @Override // com.google.android.gms.common.zzc
    public final int zzbn(Context context) {
        return super.zzbn(context);
    }

    @Override // com.google.android.gms.common.zzc
    public final boolean zzc(Context context, int i) {
        return super.zzc(context, i);
    }

    @Override // com.google.android.gms.common.zzc
    @Deprecated
    public final Intent zzfc(int i) {
        return super.zzfc(i);
    }

    private Dialog getErrorDialog(Activity activity, int i, int i2, DialogInterface.OnCancelListener onCancelListener) {
        return zza(activity, i, zzi.zza(activity, super.zza(activity, i, "d"), i2), onCancelListener);
    }

    public final boolean zza$31f23251(Activity activity, zzqk zzqkVar, int i, DialogInterface.OnCancelListener onCancelListener) {
        Dialog zza = zza(activity, i, zzi.zza$5fe6cfb0(zzqkVar, super.zza(activity, i, "d")), onCancelListener);
        if (zza == null) {
            return false;
        }
        zza(activity, zza, "GooglePlayServicesErrorDialog", onCancelListener);
        return true;
    }

    public final void zza(Context context, ConnectionResult connectionResult, int i) {
        PendingIntent errorResolutionPendingIntent;
        if (connectionResult.hasResolution()) {
            errorResolutionPendingIntent = connectionResult.mPendingIntent;
        } else {
            int i2 = connectionResult.ok;
            if (com.google.android.gms.common.util.zzi.zzck(context) && i2 == 2) {
                i2 = 42;
            }
            errorResolutionPendingIntent = super.getErrorResolutionPendingIntent(context, i2, 0);
        }
        if (errorResolutionPendingIntent != null) {
            GooglePlayServicesUtil.zza(connectionResult.ok, context, GoogleApiActivity.zza(context, errorResolutionPendingIntent, i));
        }
    }

    public static zzqe zza(Context context, zzqe.zza zzaVar) {
        IntentFilter intentFilter = new IntentFilter("android.intent.action.PACKAGE_ADDED");
        intentFilter.addDataScheme("package");
        zzqe zzqeVar = new zzqe(zzaVar);
        context.registerReceiver(zzqeVar, intentFilter);
        zzqeVar.setContext(context);
        if (zze.zzl(context, "com.google.android.gms")) {
            return zzqeVar;
        }
        zzaVar.zzaou();
        zzqeVar.unregister();
        return null;
    }

    @TargetApi(14)
    private static Dialog zza(Context context, int i, zzi zziVar, DialogInterface.OnCancelListener onCancelListener) {
        AlertDialog.Builder builder = null;
        if (i == 0) {
            return null;
        }
        if (com.google.android.gms.common.util.zzi.zzck(context) && i == 2) {
            i = 42;
        }
        if (zzs.zzhb(14)) {
            TypedValue typedValue = new TypedValue();
            context.getTheme().resolveAttribute(R.attr.alertDialogTheme, typedValue, true);
            if ("Theme.Dialog.Alert".equals(context.getResources().getResourceEntryName(typedValue.resourceId))) {
                builder = new AlertDialog.Builder(context, 5);
            }
        }
        if (builder == null) {
            builder = new AlertDialog.Builder(context);
        }
        builder.setMessage(zzh.zzc(context, i, zze.zzbv(context)));
        if (onCancelListener != null) {
            builder.setOnCancelListener(onCancelListener);
        }
        String zzh = zzh.zzh(context, i);
        if (zzh != null) {
            builder.setPositiveButton(zzh, zziVar);
        }
        String zzf = zzh.zzf(context, i);
        if (zzf != null) {
            builder.setTitle(zzf);
        }
        return builder.create();
    }

    @TargetApi(11)
    private static void zza(Activity activity, Dialog dialog, String str, DialogInterface.OnCancelListener onCancelListener) {
        if (activity instanceof FragmentActivity) {
            SupportErrorDialogFragment.newInstance(dialog, onCancelListener).show(((FragmentActivity) activity).getSupportFragmentManager(), str);
        } else {
            if (!zzs.zzhb(11)) {
                throw new RuntimeException("This Activity does not support Fragments.");
            }
            ErrorDialogFragment.newInstance(dialog, onCancelListener).show(activity.getFragmentManager(), str);
        }
    }
}
