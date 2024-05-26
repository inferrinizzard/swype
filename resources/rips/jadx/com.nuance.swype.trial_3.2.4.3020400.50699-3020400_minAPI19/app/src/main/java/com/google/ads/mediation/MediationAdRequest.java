package com.google.ads.mediation;

import android.location.Location;
import com.google.ads.AdRequest;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;

@Deprecated
/* loaded from: classes.dex */
public class MediationAdRequest {
    private final Date zzfp;
    private final AdRequest.Gender zzfq;
    private final Set<String> zzfr;
    private final boolean zzfs;
    private final Location zzft;

    public MediationAdRequest(Date date, AdRequest.Gender gender, Set<String> set, boolean z, Location location) {
        this.zzfp = date;
        this.zzfq = gender;
        this.zzfr = set;
        this.zzfs = z;
        this.zzft = location;
    }

    public Integer getAgeInYears() {
        if (this.zzfp == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();
        calendar.setTime(this.zzfp);
        Integer valueOf = Integer.valueOf(calendar2.get(1) - calendar.get(1));
        return (calendar2.get(2) < calendar.get(2) || (calendar2.get(2) == calendar.get(2) && calendar2.get(5) < calendar.get(5))) ? Integer.valueOf(valueOf.intValue() - 1) : valueOf;
    }

    public Date getBirthday() {
        return this.zzfp;
    }

    public AdRequest.Gender getGender() {
        return this.zzfq;
    }

    public Set<String> getKeywords() {
        return this.zzfr;
    }

    public Location getLocation() {
        return this.zzft;
    }

    public boolean isTesting() {
        return this.zzfs;
    }
}
