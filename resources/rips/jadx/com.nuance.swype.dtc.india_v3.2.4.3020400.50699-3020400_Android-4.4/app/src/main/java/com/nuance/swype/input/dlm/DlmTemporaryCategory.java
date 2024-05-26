package com.nuance.swype.input.dlm;

import com.nuance.input.swypecorelib.XT9CoreAlphaInput;
import com.nuance.swype.input.dlm.DlmCategory;

/* loaded from: classes.dex */
public class DlmTemporaryCategory {
    private static final int CATEGORY_ID = 65382;
    DlmCategory dlmCategory;

    public void create(XT9CoreAlphaInput coreInput) {
        if (this.dlmCategory != null) {
            destroy();
        }
        this.dlmCategory = new DlmCategory.Builder().create(coreInput, CATEGORY_ID, "Session.Category", "Temporary");
    }

    public void addWord(String word) {
        if (this.dlmCategory != null) {
            this.dlmCategory.addWord(word);
        }
    }

    public void destroy() {
        if (this.dlmCategory != null) {
            this.dlmCategory.delete();
            this.dlmCategory = null;
        }
    }
}
