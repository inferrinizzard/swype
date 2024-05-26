package com.nuance.swype.stats;

import android.content.Context;
import android.graphics.Point;
import com.nuance.android.compat.UserManagerCompat;
import com.nuance.input.swypecorelib.Candidates;
import com.nuance.input.swypecorelib.Shift;
import com.nuance.input.swypecorelib.WordCandidate;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.input.KeyboardEx;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

/* loaded from: classes.dex */
public class UsageManager {
    private static final String DEFAULT_USAGE_SCRIBE = "com.nuance.swype.stats.basiclogging.DefaultUsageScribe";
    private static final String DTC_USAGE_SCRIBE = "com.nuance.swype.stats.dtclogging.DtcUsageScribe";
    private static final String EMPTY_USAGE_SCRIBE = "com.nuance.swype.stats.basiclogging.BasicUsageScribe";
    private static final String FULL_USAGE_SCRIBE = "com.nuance.swype.stats.fulllogging.FullUsageScribe";
    protected static final String TAG = "Stats";
    private KeyboardUsageScribe keyboardScribe;

    /* loaded from: classes.dex */
    public interface KeyboardUsageScribe {
        String filterInputBuffer(String str);

        void recordActiveWord(String str, String str2, String str3);

        void recordAlternativeText(String str);

        void recordAppInstallRemove(String str, String str2);

        void recordCommittedSentence(String str);

        void recordCompletedText(String str);

        void recordDeletedWord(String str);

        void recordGestureType(String str);

        void recordInitialLocaleSetting(String str);

        void recordInlineText(String str);

        void recordKeyboardLayerChange(KeyboardEx.KeyboardLayerType keyboardLayerType, KeyboardEx.KeyboardLayerType keyboardLayerType2);

        void recordKeyboardPageXML(String str);

        void recordKeycodeLongpress(int i);

        void recordKeycodeTapped(int i);

        void recordRecapture(String str, int i);

        void recordSelectedWord(String str);

        void recordSelectionListContext(WordCandidate wordCandidate, String str);

        void recordSelectionListOptionString(List<CharSequence> list);

        void recordSelectionListOptions(Candidates candidates);

        void recordSetContext(String str);

        void recordShiftMargin(int i);

        void recordShiftState(Shift.ShiftState shiftState);

        void recordTextBuffer(String str);

        void recordTracePath(List<Point> list, List<Long> list2);

        void recordUsedTimeSelectionListDisplay(String str, String str2);

        void recordWordWCLDataPoint(String str);
    }

    public static UsageManager from(Context ctx) {
        return ((IMEApplication) ctx.getApplicationContext()).getUsageManager();
    }

    public UsageManager(Context ctx) {
        if (!UserManagerCompat.isUserUnlocked(ctx)) {
            loadScribe(EMPTY_USAGE_SCRIBE, null);
        } else if (!loadScribe(FULL_USAGE_SCRIBE, ctx) && !loadScribe(DTC_USAGE_SCRIBE, ctx) && !loadScribe(DEFAULT_USAGE_SCRIBE, ctx)) {
            loadScribe(EMPTY_USAGE_SCRIBE, null);
        }
    }

    private boolean loadScribe(String scribeClass, Context ctx) {
        try {
            Class<? extends U> asSubclass = Class.forName(scribeClass).asSubclass(AbstractScribe.class);
            asSubclass.asSubclass(KeyboardUsageScribe.class);
            if (ctx == null) {
                Constructor<? extends AbstractScribe> ctor = asSubclass.getConstructor(new Class[0]);
                this.keyboardScribe = (KeyboardUsageScribe) ctor.newInstance(new Object[0]);
            } else {
                Constructor<? extends AbstractScribe> ctor2 = asSubclass.getConstructor(Context.class);
                this.keyboardScribe = (KeyboardUsageScribe) ctor2.newInstance(ctx);
            }
            return true;
        } catch (ClassCastException e) {
            return false;
        } catch (ClassNotFoundException e2) {
            return false;
        } catch (IllegalAccessException e3) {
            return false;
        } catch (InstantiationException e4) {
            return false;
        } catch (NoSuchMethodException e5) {
            return false;
        } catch (InvocationTargetException e6) {
            return false;
        }
    }

    public static KeyboardUsageScribe getKeyboardUsageScribe(Context ctx) {
        UsageManager manager = ((IMEApplication) ctx.getApplicationContext()).getUsageManager();
        if (manager == null) {
            return null;
        }
        KeyboardUsageScribe scribe = manager.getKeyboardUsageScribe();
        return scribe;
    }

    public KeyboardUsageScribe getKeyboardUsageScribe() {
        return this.keyboardScribe;
    }
}
