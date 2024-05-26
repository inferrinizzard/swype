package com.nuance.swype.input;

import com.nuance.swype.input.AbstractTapDetector;

/* loaded from: classes.dex */
public class TapDetector14 extends AbstractTapDetector {
    public TapDetector14(AbstractTapDetector.TapHandler[] tapHandlers) {
        super(tapHandlers);
    }

    @Override // com.nuance.swype.input.AbstractTapDetector
    public void onViewClicked(boolean focusChanged) {
        if (!focusChanged) {
            onTap();
        }
    }
}
