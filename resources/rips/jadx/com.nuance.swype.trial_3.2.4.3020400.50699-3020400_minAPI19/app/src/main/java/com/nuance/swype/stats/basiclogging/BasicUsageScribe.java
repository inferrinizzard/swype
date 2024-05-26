package com.nuance.swype.stats.basiclogging;

import android.content.Context;
import android.graphics.Point;
import com.nuance.input.swypecorelib.Candidates;
import com.nuance.input.swypecorelib.Shift;
import com.nuance.input.swypecorelib.WordCandidate;
import com.nuance.nmsp.client.util.internal.dictationresult.parser.xml.xmlResults.XMLResultsHandler;
import com.nuance.swype.input.KeyboardEx;
import com.nuance.swype.stats.AbstractScribe;
import com.nuance.swype.stats.UsageManager;
import java.util.List;

/* loaded from: classes.dex */
public class BasicUsageScribe extends AbstractScribe implements UsageManager.KeyboardUsageScribe {
    public BasicUsageScribe() {
        log.v("Starting Basic Usage Scribe");
    }

    public BasicUsageScribe(Context ctx) {
        super(ctx);
        log.v("Starting Basic Usage Scribe");
    }

    @Override // com.nuance.swype.stats.UsageManager.KeyboardUsageScribe
    public void recordKeycodeTapped(int keycode) {
        log.v("recordKeycodeTapped keycode=" + keycode);
    }

    @Override // com.nuance.swype.stats.UsageManager.KeyboardUsageScribe
    public void recordTracePath(List<Point> tracePath, List<Long> eventTime) {
        log.v("recordTracePath");
    }

    @Override // com.nuance.swype.stats.UsageManager.KeyboardUsageScribe
    public void recordDeletedWord(String word) {
        log.v("recordDeletedWord word=" + word);
    }

    @Override // com.nuance.swype.stats.UsageManager.KeyboardUsageScribe
    public void recordKeycodeLongpress(int keycode) {
        log.v("recordKeycodeLongpress keycode=" + keycode);
    }

    @Override // com.nuance.swype.stats.UsageManager.KeyboardUsageScribe
    public void recordCompletedText(String text) {
        log.v("recordCompletedText text=" + text);
    }

    @Override // com.nuance.swype.stats.UsageManager.KeyboardUsageScribe
    public void recordRecapture(String context, int offset) {
        log.v("recordRecapture context=" + context + " offset=" + offset);
    }

    public void recordUdbAdd(String word) {
        log.v("recordUdbAdd word=" + word);
    }

    public void recordUdbDelete(String word) {
        log.v("recordUdbDelete word=" + word);
    }

    @Override // com.nuance.swype.stats.UsageManager.KeyboardUsageScribe
    public void recordSelectionListOptions(Candidates candidates) {
        log.v("recordSelectionListOptions");
    }

    @Override // com.nuance.swype.stats.UsageManager.KeyboardUsageScribe
    public void recordSelectionListOptionString(List<CharSequence> selection) {
        log.v("recordSelectionListOptionString");
    }

    @Override // com.nuance.swype.stats.UsageManager.KeyboardUsageScribe
    public void recordSelectedWord(String word) {
        log.v("recordSelectedWord word=" + word);
    }

    @Override // com.nuance.swype.stats.UsageManager.KeyboardUsageScribe
    public void recordShiftState(Shift.ShiftState state) {
        log.v("recordShiftState");
    }

    @Override // com.nuance.swype.stats.UsageManager.KeyboardUsageScribe
    public void recordSelectionListContext(WordCandidate candate, String contextBuffer) {
        log.v("recordSelectionListContext");
    }

    public void recordKeyboardLayerChange(KeyboardEx.KeyboardLayerType keyboardLayer) {
        log.v("recordKeyboardLayerChange mode=" + keyboardLayer);
    }

    @Override // com.nuance.swype.stats.UsageManager.KeyboardUsageScribe
    public void recordKeyboardLayerChange(KeyboardEx.KeyboardLayerType from, KeyboardEx.KeyboardLayerType to) {
        log.v("recordKeyboardLayerChange mode from=" + from + " to=" + to);
    }

    @Override // com.nuance.swype.stats.UsageManager.KeyboardUsageScribe
    public void recordAlternativeText(String text) {
        log.v("recordAlternativeText");
    }

    @Override // com.nuance.swype.stats.UsageManager.KeyboardUsageScribe
    public void recordInlineText(String text) {
        log.v("recordInlineText");
    }

    @Override // com.nuance.swype.stats.UsageManager.KeyboardUsageScribe
    public void recordShiftMargin(int margin) {
        log.v("recordShiftMargin margin=" + margin);
    }

    @Override // com.nuance.swype.stats.UsageManager.KeyboardUsageScribe
    public void recordTextBuffer(String text) {
        log.v("recordTextBuffer text=" + text);
    }

    @Override // com.nuance.swype.stats.UsageManager.KeyboardUsageScribe
    public void recordKeyboardPageXML(String xml) {
        log.v("recordKeyboardPageXML xml=" + xml);
    }

    public void recordSearchBuffer(String text) {
        log.v("recordSearchBuffer search=" + text);
    }

    @Override // com.nuance.swype.stats.UsageManager.KeyboardUsageScribe
    public void recordGestureType(String type) {
        log.v("recordGestureType type=" + type);
    }

    @Override // com.nuance.swype.stats.UsageManager.KeyboardUsageScribe
    public void recordAppInstallRemove(String appId, String Event) {
        log.v("recordAppInstallRemove appId=" + appId + XMLResultsHandler.SEP_SPACE + Event);
    }

    @Override // com.nuance.swype.stats.UsageManager.KeyboardUsageScribe
    public void recordActiveWord(String candidate, String spelling, String prefix) {
        log.v("recordActiveWord");
    }

    @Override // com.nuance.swype.stats.UsageManager.KeyboardUsageScribe
    public void recordCommittedSentence(String sentence) {
        log.v("recordCommittedSentence sentence=" + sentence);
    }

    @Override // com.nuance.swype.stats.UsageManager.KeyboardUsageScribe
    public void recordSetContext(String context) {
        log.v("recordSetContext context=" + context);
    }

    public void recordTotalSwypeDistance(String distance) {
        log.v("recordTotalSwypeDistance distance=" + distance);
    }

    @Override // com.nuance.swype.stats.UsageManager.KeyboardUsageScribe
    public void recordInitialLocaleSetting(String locale) {
        log.v("recordInitialLocaleSetting locale settings:" + locale);
    }

    @Override // com.nuance.swype.stats.UsageManager.KeyboardUsageScribe
    public void recordUsedTimeSelectionListDisplay(String name, String time) {
        log.v("recordUsedTimeSelectionListDisplay name:" + name + " time:" + time);
    }

    @Override // com.nuance.swype.stats.UsageManager.KeyboardUsageScribe
    public String filterInputBuffer(String buffer) {
        return null;
    }

    @Override // com.nuance.swype.stats.UsageManager.KeyboardUsageScribe
    public void recordWordWCLDataPoint(String json) {
        log.v(json);
    }
}
