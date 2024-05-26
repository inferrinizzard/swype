package com.facebook.share.internal;

import com.facebook.internal.DialogFeature;
import com.facebook.internal.NativeProtocol;

/* loaded from: classes.dex */
public enum LikeDialogFeature implements DialogFeature {
    LIKE_DIALOG(NativeProtocol.PROTOCOL_VERSION_20140701);

    private int minVersion;

    LikeDialogFeature(int minVersion) {
        this.minVersion = minVersion;
    }

    @Override // com.facebook.internal.DialogFeature
    public final String getAction() {
        return NativeProtocol.ACTION_LIKE_DIALOG;
    }

    @Override // com.facebook.internal.DialogFeature
    public final int getMinVersion() {
        return this.minVersion;
    }
}
