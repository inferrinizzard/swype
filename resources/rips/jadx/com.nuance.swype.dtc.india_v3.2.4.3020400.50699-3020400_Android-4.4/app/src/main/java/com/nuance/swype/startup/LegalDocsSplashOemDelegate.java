package com.nuance.swype.startup;

import android.os.Bundle;

/* loaded from: classes.dex */
public class LegalDocsSplashOemDelegate extends LegalDocsSplashDelegate {
    /* JADX INFO: Access modifiers changed from: package-private */
    public static LegalDocsSplashOemDelegate newInstance(Bundle savedInstanceState) {
        LegalDocsSplashOemDelegate f = new LegalDocsSplashOemDelegate();
        f.setArguments(savedInstanceState);
        return f;
    }

    @Override // com.nuance.swype.startup.LegalDocsSplashDelegate
    protected final boolean showEulaLink() {
        return false;
    }

    @Override // com.nuance.swype.startup.LegalDocsSplashDelegate
    protected final boolean showTosLink() {
        return !this.mStartupSequenceInfo.isTosAccepted() && this.mStartupSequenceInfo.wasTosAccepted();
    }

    @Override // com.nuance.swype.startup.LegalDocsSplashDelegate
    protected final boolean showChangedNotice() {
        return this.mStartupSequenceInfo.isTosChanged();
    }
}
