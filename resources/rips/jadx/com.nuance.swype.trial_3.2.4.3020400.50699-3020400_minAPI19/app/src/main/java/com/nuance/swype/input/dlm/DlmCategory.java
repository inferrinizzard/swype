package com.nuance.swype.input.dlm;

import com.nuance.input.swypecorelib.XT9CoreAlphaInput;

/* loaded from: classes.dex */
public class DlmCategory {
    private final int categoryID;
    private final XT9CoreAlphaInput coreInput;

    /* loaded from: classes.dex */
    public static class Builder {
        public DlmCategory create(XT9CoreAlphaInput coreInput, int categoryID, String name, String info) {
            if (coreInput.createDlmCategoryInfo(categoryID, name, info)) {
                return new DlmCategory(coreInput, categoryID);
            }
            return null;
        }
    }

    private DlmCategory(XT9CoreAlphaInput coreInput, int categoryID) {
        this.coreInput = coreInput;
        this.categoryID = categoryID;
    }

    public void delete() {
        this.coreInput.deleteDlmCategory(this.categoryID);
    }

    public void addWord(String word) {
        this.coreInput.addDlmCategoryWord(this.categoryID, word);
    }
}
