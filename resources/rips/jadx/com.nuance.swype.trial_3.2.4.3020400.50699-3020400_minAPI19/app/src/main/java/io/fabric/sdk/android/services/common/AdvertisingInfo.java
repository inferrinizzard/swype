package io.fabric.sdk.android.services.common;

/* loaded from: classes.dex */
final class AdvertisingInfo {
    public final String advertisingId;
    public final boolean limitAdTrackingEnabled;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AdvertisingInfo(String advertisingId, boolean limitAdTrackingEnabled) {
        this.advertisingId = advertisingId;
        this.limitAdTrackingEnabled = limitAdTrackingEnabled;
    }

    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AdvertisingInfo infoToCompare = (AdvertisingInfo) o;
        if (this.limitAdTrackingEnabled != infoToCompare.limitAdTrackingEnabled) {
            return false;
        }
        if (this.advertisingId != null) {
            if (this.advertisingId.equals(infoToCompare.advertisingId)) {
                return true;
            }
        } else if (infoToCompare.advertisingId == null) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        int result = this.advertisingId != null ? this.advertisingId.hashCode() : 0;
        return (this.limitAdTrackingEnabled ? 1 : 0) + (result * 31);
    }
}
