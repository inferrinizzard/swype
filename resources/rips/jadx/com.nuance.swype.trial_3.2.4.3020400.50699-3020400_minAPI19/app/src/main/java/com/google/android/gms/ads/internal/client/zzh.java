package com.google.android.gms.ads.internal.client;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.internal.reward.client.RewardedVideoAdRequestParcel;
import com.google.android.gms.ads.search.SearchAdRequest;
import com.google.android.gms.internal.zzin;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

@zzin
/* loaded from: classes.dex */
public class zzh {
    public static final zzh zzauq = new zzh();

    protected zzh() {
    }

    public static zzh zzih() {
        return zzauq;
    }

    public AdRequestParcel zza(Context context, zzad zzadVar) {
        Date birthday = zzadVar.getBirthday();
        long time = birthday != null ? birthday.getTime() : -1L;
        String contentUrl = zzadVar.getContentUrl();
        int gender = zzadVar.getGender();
        Set<String> keywords = zzadVar.getKeywords();
        List unmodifiableList = !keywords.isEmpty() ? Collections.unmodifiableList(new ArrayList(keywords)) : null;
        boolean isTestDevice = zzadVar.isTestDevice(context);
        int zzji = zzadVar.zzji();
        Location location = zzadVar.getLocation();
        Bundle networkExtrasBundle = zzadVar.getNetworkExtrasBundle(AdMobAdapter.class);
        boolean manualImpressionsEnabled = zzadVar.getManualImpressionsEnabled();
        String publisherProvidedId = zzadVar.getPublisherProvidedId();
        SearchAdRequest zzjf = zzadVar.zzjf();
        SearchAdRequestParcel searchAdRequestParcel = zzjf != null ? new SearchAdRequestParcel(zzjf) : null;
        Context applicationContext = context.getApplicationContext();
        return new AdRequestParcel(7, time, networkExtrasBundle, gender, unmodifiableList, isTestDevice, zzji, manualImpressionsEnabled, publisherProvidedId, searchAdRequestParcel, location, contentUrl, zzadVar.zzjh(), zzadVar.getCustomTargeting(), Collections.unmodifiableList(new ArrayList(zzadVar.zzjj())), zzadVar.zzje(), applicationContext != null ? zzm.zziw().zza(Thread.currentThread().getStackTrace(), applicationContext.getPackageName()) : null, zzadVar.isDesignedForFamilies());
    }

    public RewardedVideoAdRequestParcel zza(Context context, zzad zzadVar, String str) {
        return new RewardedVideoAdRequestParcel(zza(context, zzadVar), str);
    }
}
