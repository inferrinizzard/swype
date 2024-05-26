package com.nuance.dlm;

import com.nuance.swypeconnect.ac.ACDLMConnector;
import com.nuance.swypeconnect.ac.ACException;

/* loaded from: classes.dex */
public class ACChineseInput implements ACDLMConnector.ACChineseDlmDb {
    private ACDLMConnector.ACChineseDlmEventCallback callback;
    private ACDLMConnector connector;

    private final native int acChineseDeleteCategory(int i);

    private final native int acChineseDeleteCategoryLanguage(int i, int i2);

    private final native int acChineseExportAsEvent(boolean z, int i);

    private final native int acChineseProcessEvent(byte[] bArr);

    private final native int acChineseRegisterEventHandlerCallback();

    public ACChineseInput(ACDLMConnector connector) {
        this.connector = connector;
        try {
            this.callback = this.connector.bindChineseDlm(this);
        } catch (ACException e) {
            e.printStackTrace();
        }
        acChineseRegisterEventHandlerCallback();
    }

    public void release() {
        this.connector.releaseChineseDlm();
    }

    private void onEventCallback(byte[] event, boolean highPriority) {
        if (this.callback != null) {
            this.callback.onChineseDlmEvent(event, highPriority);
        }
    }

    @Override // com.nuance.swypeconnect.ac.ACDLMConnector.ACChineseDlmDb
    public void processEvent(byte[] event) {
        acChineseProcessEvent(event);
    }

    @Override // com.nuance.swypeconnect.ac.ACDLMConnector.ACChineseDlmDb
    public void exportAsEvents(boolean usingCategory, int category) {
        acChineseExportAsEvent(usingCategory, category);
    }

    @Override // com.nuance.swypeconnect.ac.ACDLMConnector.ACChineseDlmDb
    public void deleteCategory(int category) {
        acChineseDeleteCategory(category);
    }

    @Override // com.nuance.swypeconnect.ac.ACDLMConnector.ACChineseDlmDb
    public void deleteCategoryLanguage(int category, int language) {
        acChineseDeleteCategoryLanguage(category, language);
    }
}
