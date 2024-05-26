package com.google.android.gms.internal;

import android.content.Context;
import android.net.Uri;
import android.view.MotionEvent;

/* loaded from: classes.dex */
public final class zzas {
    static final String[] zzafy = {"/aclk", "/pcs/click"};
    String zzafu = "googleads.g.doubleclick.net";
    String zzafv = "/pagead/ads";
    private String zzafw = "ad.doubleclick.net";
    String[] zzafx = {".doubleclick.net", ".googleadservices.com", ".googlesyndication.com"};
    public zzan zzafz;

    public zzas(zzan zzanVar) {
        this.zzafz = zzanVar;
    }

    private boolean zzb(Uri uri) {
        if (uri == null) {
            throw new NullPointerException();
        }
        try {
            return uri.getHost().equals(this.zzafw);
        } catch (NullPointerException e) {
            return false;
        }
    }

    public final void zza(MotionEvent motionEvent) {
        this.zzafz.zza(motionEvent);
    }

    public final boolean zza(Uri uri) {
        if (uri == null) {
            throw new NullPointerException();
        }
        try {
            if (uri.getHost().equals(this.zzafu)) {
                return uri.getPath().equals(this.zzafv);
            }
            return false;
        } catch (NullPointerException e) {
            return false;
        }
    }

    public final Uri zzb(Uri uri, Context context) throws zzat {
        try {
            return zza(uri, context, uri.getQueryParameter("ai"), true);
        } catch (UnsupportedOperationException e) {
            throw new zzat("Provided Uri is not in a valid state");
        }
    }

    public final boolean zzc(Uri uri) {
        if (uri == null) {
            throw new NullPointerException();
        }
        try {
            String host = uri.getHost();
            for (String str : this.zzafx) {
                if (host.endsWith(str)) {
                    return true;
                }
            }
            return false;
        } catch (NullPointerException e) {
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Uri zza(Uri uri, Context context, String str, boolean z) throws zzat {
        try {
            boolean zzb = zzb(uri);
            if (zzb) {
                if (uri.toString().contains("dc_ms=")) {
                    throw new zzat("Parameter already exists: dc_ms");
                }
            } else if (uri.getQueryParameter("ms") != null) {
                throw new zzat("Query parameter already exists: ms");
            }
            String zzb2 = z ? this.zzafz.zzb(context, str) : this.zzafz.zzb(context);
            if (zzb) {
                String uri2 = uri.toString();
                int indexOf = uri2.indexOf(";adurl");
                if (indexOf != -1) {
                    return Uri.parse(uri2.substring(0, indexOf + 1) + "dc_ms=" + zzb2 + ";" + uri2.substring(indexOf + 1));
                }
                String encodedPath = uri.getEncodedPath();
                int indexOf2 = uri2.indexOf(encodedPath);
                return Uri.parse(uri2.substring(0, encodedPath.length() + indexOf2) + ";dc_ms=" + zzb2 + ";" + uri2.substring(encodedPath.length() + indexOf2));
            }
            String uri3 = uri.toString();
            int indexOf3 = uri3.indexOf("&adurl");
            if (indexOf3 == -1) {
                indexOf3 = uri3.indexOf("?adurl");
            }
            return indexOf3 != -1 ? Uri.parse(uri3.substring(0, indexOf3 + 1) + "ms=" + zzb2 + "&" + uri3.substring(indexOf3 + 1)) : uri.buildUpon().appendQueryParameter("ms", zzb2).build();
        } catch (UnsupportedOperationException e) {
            throw new zzat("Provided Uri is not in a valid state");
        }
    }
}
