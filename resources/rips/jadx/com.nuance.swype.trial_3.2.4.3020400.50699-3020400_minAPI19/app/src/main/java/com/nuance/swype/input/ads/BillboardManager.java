package com.nuance.swype.input.ads;

import android.content.Context;

/* loaded from: classes.dex */
public class BillboardManager {
    private final Billboard mBillboard;
    private BillboardDisplayStrategy mDisplayStrategy;

    public BillboardManager(Context context) {
        this.mBillboard = new Billboard(context);
    }

    public void setBillboardDisplayStrategy(BillboardDisplayStrategy displayStrategy) {
        this.mDisplayStrategy = displayStrategy;
    }

    public boolean canShowBillboard() {
        return this.mDisplayStrategy != null && this.mDisplayStrategy.canShowBillboard();
    }

    public Billboard getBillboard() {
        return this.mBillboard;
    }

    public void hide() {
        this.mBillboard.requestHide();
    }

    public void showView() {
        if (this.mDisplayStrategy.canShowBillboard()) {
            this.mBillboard.show();
        }
    }
}
