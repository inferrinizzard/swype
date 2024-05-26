package android.support.v4.app;

import android.os.Bundle;
import android.support.v4.app.RemoteInputCompatBase;
import com.facebook.applinks.AppLinkData;

/* loaded from: classes.dex */
final class RemoteInputCompatJellybean {
    /* JADX INFO: Access modifiers changed from: package-private */
    public static Bundle[] toBundleArray(RemoteInputCompatBase.RemoteInput[] remoteInputs) {
        if (remoteInputs == null) {
            return null;
        }
        Bundle[] bundles = new Bundle[remoteInputs.length];
        for (int i = 0; i < remoteInputs.length; i++) {
            RemoteInputCompatBase.RemoteInput remoteInput = remoteInputs[i];
            Bundle bundle = new Bundle();
            bundle.putString("resultKey", remoteInput.getResultKey());
            bundle.putCharSequence("label", remoteInput.getLabel());
            bundle.putCharSequenceArray("choices", remoteInput.getChoices());
            bundle.putBoolean("allowFreeFormInput", remoteInput.getAllowFreeFormInput());
            bundle.putBundle(AppLinkData.ARGUMENTS_EXTRAS_KEY, remoteInput.getExtras());
            bundles[i] = bundle;
        }
        return bundles;
    }
}
