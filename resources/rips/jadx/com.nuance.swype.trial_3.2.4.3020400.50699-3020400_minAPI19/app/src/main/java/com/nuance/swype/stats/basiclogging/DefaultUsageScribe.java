package com.nuance.swype.stats.basiclogging;

import android.content.Context;
import android.graphics.Point;
import com.nuance.input.swypecorelib.Candidates;
import com.nuance.input.swypecorelib.Shift;
import com.nuance.input.swypecorelib.WordCandidate;
import com.nuance.swype.input.KeyboardEx;
import com.nuance.swype.stats.AbstractScribe;
import com.nuance.swype.stats.UsageManager;
import java.util.List;

/* loaded from: classes.dex */
public class DefaultUsageScribe extends AbstractScribe implements UsageManager.KeyboardUsageScribe {
    public DefaultUsageScribe() {
        log.v("Starting Default Usage Scribe");
    }

    public DefaultUsageScribe(Context ctx) {
        super(ctx);
        log.v("Starting Default Usage Scribe");
    }

    @Override // com.nuance.swype.stats.UsageManager.KeyboardUsageScribe
    public void recordKeycodeTapped(int keycode) {
    }

    @Override // com.nuance.swype.stats.UsageManager.KeyboardUsageScribe
    public void recordTracePath(List<Point> tracePath, List<Long> eventTime) {
    }

    @Override // com.nuance.swype.stats.UsageManager.KeyboardUsageScribe
    public void recordDeletedWord(String word) {
    }

    @Override // com.nuance.swype.stats.UsageManager.KeyboardUsageScribe
    public void recordKeycodeLongpress(int keycode) {
    }

    @Override // com.nuance.swype.stats.UsageManager.KeyboardUsageScribe
    public void recordCompletedText(String text) {
    }

    @Override // com.nuance.swype.stats.UsageManager.KeyboardUsageScribe
    public void recordRecapture(String context, int offset) {
    }

    @Override // com.nuance.swype.stats.UsageManager.KeyboardUsageScribe
    public void recordSelectionListOptions(Candidates candidates) {
    }

    @Override // com.nuance.swype.stats.UsageManager.KeyboardUsageScribe
    public void recordSelectionListOptionString(List<CharSequence> selection) {
    }

    @Override // com.nuance.swype.stats.UsageManager.KeyboardUsageScribe
    public void recordSelectedWord(String word) {
    }

    @Override // com.nuance.swype.stats.UsageManager.KeyboardUsageScribe
    public void recordShiftState(Shift.ShiftState state) {
    }

    @Override // com.nuance.swype.stats.UsageManager.KeyboardUsageScribe
    public void recordSelectionListContext(WordCandidate candate, String contextBuffer) {
    }

    @Override // com.nuance.swype.stats.UsageManager.KeyboardUsageScribe
    public void recordKeyboardLayerChange(KeyboardEx.KeyboardLayerType from, KeyboardEx.KeyboardLayerType to) {
    }

    @Override // com.nuance.swype.stats.UsageManager.KeyboardUsageScribe
    public void recordAlternativeText(String text) {
    }

    @Override // com.nuance.swype.stats.UsageManager.KeyboardUsageScribe
    public void recordInlineText(String text) {
    }

    @Override // com.nuance.swype.stats.UsageManager.KeyboardUsageScribe
    public void recordShiftMargin(int margin) {
    }

    @Override // com.nuance.swype.stats.UsageManager.KeyboardUsageScribe
    public void recordTextBuffer(String text) {
    }

    @Override // com.nuance.swype.stats.UsageManager.KeyboardUsageScribe
    public void recordKeyboardPageXML(String xml) {
    }

    @Override // com.nuance.swype.stats.UsageManager.KeyboardUsageScribe
    public void recordActiveWord(String candidate, String spelling, String prefix) {
    }

    @Override // com.nuance.swype.stats.UsageManager.KeyboardUsageScribe
    public void recordCommittedSentence(String sentence) {
    }

    @Override // com.nuance.swype.stats.UsageManager.KeyboardUsageScribe
    public void recordSetContext(String context) {
    }

    @Override // com.nuance.swype.stats.UsageManager.KeyboardUsageScribe
    public void recordGestureType(String type) {
    }

    @Override // com.nuance.swype.stats.UsageManager.KeyboardUsageScribe
    public void recordUsedTimeSelectionListDisplay(String name, String time) {
    }

    @Override // com.nuance.swype.stats.UsageManager.KeyboardUsageScribe
    public void recordInitialLocaleSetting(String localeSetting) {
    }

    @Override // com.nuance.swype.stats.UsageManager.KeyboardUsageScribe
    public String filterInputBuffer(String buffer) {
        return null;
    }

    @Override // com.nuance.swype.stats.UsageManager.KeyboardUsageScribe
    public void recordWordWCLDataPoint(String json) {
    }

    @Override // com.nuance.swype.stats.UsageManager.KeyboardUsageScribe
    public void recordAppInstallRemove(String appId, String Event) {
    }
}
