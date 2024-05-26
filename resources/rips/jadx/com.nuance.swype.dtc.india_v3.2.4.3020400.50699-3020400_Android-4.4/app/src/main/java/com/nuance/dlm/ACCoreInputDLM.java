package com.nuance.dlm;

import com.nuance.swype.connect.Connect;

/* loaded from: classes.dex */
public class ACCoreInputDLM {
    private static ACAlphaInput acAlphaInput;
    private static ACChineseInput acChineseInput;
    private static ACKoreanInput acKoreanInput;

    public static void initializeACAlphaInput(final Connect connect) {
        if (acAlphaInput == null) {
            connect.doPostStart(new Runnable() { // from class: com.nuance.dlm.ACCoreInputDLM.1
                @Override // java.lang.Runnable
                public final void run() {
                    if (ACCoreInputDLM.acAlphaInput == null) {
                        ACAlphaInput unused = ACCoreInputDLM.acAlphaInput = new ACAlphaInput(Connect.this.getDLMConnector());
                    }
                }
            });
        }
    }

    public static void initializeACKoreanInput(final Connect connect) {
        if (acKoreanInput == null) {
            connect.doPostStart(new Runnable() { // from class: com.nuance.dlm.ACCoreInputDLM.2
                @Override // java.lang.Runnable
                public final void run() {
                    if (ACCoreInputDLM.acKoreanInput == null) {
                        ACKoreanInput unused = ACCoreInputDLM.acKoreanInput = new ACKoreanInput(Connect.this.getDLMConnector());
                    }
                }
            });
        }
    }

    public static void destroyACKoreanInput() {
        if (acKoreanInput != null) {
            acKoreanInput.release();
            acKoreanInput = null;
        }
    }

    public static void initializeACChineseDLM(final Connect connect) {
        if (acChineseInput == null) {
            connect.doPostStart(new Runnable() { // from class: com.nuance.dlm.ACCoreInputDLM.3
                @Override // java.lang.Runnable
                public final void run() {
                    ACChineseInput unused = ACCoreInputDLM.acChineseInput = new ACChineseInput(Connect.this.getDLMConnector());
                }
            });
        }
    }

    public static void destroyACAlphaInput() {
        if (acAlphaInput != null) {
            acAlphaInput.release();
            acAlphaInput = null;
        }
    }

    public static void destroyACChineseDLM() {
        if (acChineseInput != null) {
            acChineseInput.release();
            acChineseInput = null;
        }
    }
}
