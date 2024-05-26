package com.nuance.dlm;

import com.nuance.swypeconnect.ac.ACDLMConnector;
import com.nuance.swypeconnect.ac.ACException;

/* loaded from: classes.dex */
public class ACKoreanInput implements ACDLMConnector.ACKoreanDlmDb {
    private ACDLMConnector.ACKoreanDlmEventCallback callback;
    private ACDLMConnector connector;

    private native int acKoreanDeleteCategory(int i);

    private native int acKoreanDeleteCategoryLanguage(int i, int i2);

    private native int acKoreanExportAsEvent(boolean z, int i);

    private native int acKoreanProcessEvent(byte[] bArr);

    private native int acKoreanRegisterEventHandlerCallback();

    private native int acKoreanScanBuffer(char[] cArr, int i, int i2, int i3, int i4, boolean z, boolean z2);

    public ACKoreanInput(ACDLMConnector connector) {
        this.connector = connector;
        try {
            this.callback = this.connector.bindKoreanDlm(this);
        } catch (ACException e) {
            e.printStackTrace();
        }
        acKoreanRegisterEventHandlerCallback();
    }

    public void release() {
        this.connector.releaseKoreanDlm();
    }

    private void onEventCallback(byte[] event, boolean highPriority) {
        if (this.callback != null) {
            this.callback.onKoreanDlmEvent(event, highPriority);
        }
    }

    @Override // com.nuance.swypeconnect.ac.ACDLMConnector.ACKoreanDlmDb
    public void processEvent(byte[] event) {
        acKoreanProcessEvent(event);
    }

    @Override // com.nuance.swypeconnect.ac.ACDLMConnector.ACKoreanDlmDb
    public void exportAsEvents(boolean usingCategory, int category) {
        acKoreanExportAsEvent(usingCategory, category);
    }

    @Override // com.nuance.swypeconnect.ac.ACDLMConnector.ACKoreanDlmDb
    public void deleteCategory(int category) {
        acKoreanDeleteCategory(category);
    }

    @Override // com.nuance.swypeconnect.ac.ACDLMConnector.ACKoreanDlmDb
    public void deleteCategoryLanguage(int category, int language) {
        acKoreanDeleteCategoryLanguage(category, language);
    }

    @Override // com.nuance.swypeconnect.ac.ACDLMConnector.ACKoreanDlmDb
    public boolean scanBuffer(char[] buffer, int len, int wordQuality, boolean sentenceBased, boolean rescanning) {
        return acKoreanScanBuffer(buffer, len, 0, len, wordQuality, sentenceBased, rescanning) == 0;
    }
}
