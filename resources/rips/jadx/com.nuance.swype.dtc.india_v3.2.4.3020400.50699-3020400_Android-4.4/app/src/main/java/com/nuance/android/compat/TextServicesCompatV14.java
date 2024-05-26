package com.nuance.android.compat;

import android.annotation.TargetApi;
import android.content.Context;
import android.view.textservice.SentenceSuggestionsInfo;
import android.view.textservice.SpellCheckerSession;
import android.view.textservice.SuggestionsInfo;
import android.view.textservice.TextServicesManager;

@TargetApi(14)
/* loaded from: classes.dex */
public class TextServicesCompatV14 implements SpellCheckerSession.SpellCheckerSessionListener, TextServicesInfo {
    @Override // android.view.textservice.SpellCheckerSession.SpellCheckerSessionListener
    public void onGetSentenceSuggestions(SentenceSuggestionsInfo[] results) {
    }

    @Override // android.view.textservice.SpellCheckerSession.SpellCheckerSessionListener
    public void onGetSuggestions(SuggestionsInfo[] results) {
    }

    @Override // com.nuance.android.compat.TextServicesInfo
    public boolean isSpellCheckerEnabled(Context context) {
        SpellCheckerSession scs;
        TextServicesManager tsm = (TextServicesManager) context.getSystemService("textservices");
        if (tsm != null && (scs = tsm.newSpellCheckerSession(null, null, this, true)) != null) {
            scs.close();
            return true;
        }
        return false;
    }
}
