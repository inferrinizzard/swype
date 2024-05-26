package com.nuance.swype.usagedata;

import com.nuance.input.swypecorelib.usagedata.SessionData;
import com.nuance.swype.stats.UsageManager;

/* loaded from: classes.dex */
public class WordCandidateDataPointWriter implements DataPointWriter {
    private static final String TAG = WordCandidateDataPointWriter.class.getSimpleName();
    private final UsageManager.KeyboardUsageScribe keyboardScribe;

    public WordCandidateDataPointWriter(UsageManager.KeyboardUsageScribe keyboardScribe) {
        this.keyboardScribe = keyboardScribe;
    }

    @Override // com.nuance.swype.usagedata.DataPointWriter
    public final void write(SessionData data) {
        this.keyboardScribe.recordWordWCLDataPoint(data.getData().toString());
    }
}
