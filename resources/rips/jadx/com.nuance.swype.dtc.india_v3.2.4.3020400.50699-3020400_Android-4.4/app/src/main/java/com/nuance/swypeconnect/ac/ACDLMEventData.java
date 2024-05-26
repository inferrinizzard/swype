package com.nuance.swypeconnect.ac;

/* loaded from: classes.dex */
public class ACDLMEventData {
    public static final int DLM_EVENT_TYPE_ADD_WORD = 21;
    public static final int DLM_EVENT_TYPE_ADD_WORD_PAIR = 60;
    public static final int DLM_EVENT_TYPE_ADJUST_QUALITY = 26;
    public static final int DLM_EVENT_TYPE_CONTENT_SCANNED = 80;
    static final int DLM_EVENT_TYPE_DELETE_ALL = 40;
    public static final int DLM_EVENT_TYPE_DELETE_CATEGORY = 41;
    public static final int DLM_EVENT_TYPE_DELETE_CATEGORY_LANGUAGE = 43;
    public static final int DLM_EVENT_TYPE_DELETE_LANGUAGE = 42;
    public static final int DLM_EVENT_TYPE_DELETE_WORD = 22;
    static final int DLM_EVENT_TYPE_LOCATION = 201;
    public static final int DLM_EVENT_TYPE_MARK_NON_USE = 25;
    public static final int DLM_EVENT_TYPE_MARK_UNDO = 24;
    public static final int DLM_EVENT_TYPE_MARK_USE = 23;
    public static final int DLM_EVENT_TYPE_MARK_USE_HP = 29;
    public static final int DLM_EVENT_TYPE_MARK_USE_NA = 28;
    public static final int DLM_EVENT_TYPE_MARK_USE_WITH_SYLLABLE = 62;
    public static final int DLM_EVENT_TYPE_MARK_USE_WITH_SYLLABLE_NA = 63;
    public static final int DLM_EVENT_TYPE_NEW_CATEGORY = 20;
    public static final int DLM_EVENT_TYPE_NEW_WORD_NA = 27;
    public static final int DLM_EVENT_TYPE_NEW_WORD_SYLLABLE_PAIR = 61;
    public static final int DLM_EVENT_TYPE_NEW_WORD_SYLLABLE_PAIR_NA = 64;
    public static final int DLM_EVENT_TYPE_NEW_WORK_SYLLABLE_PAIR_EX = 65;
    static final int DLM_EVENT_TYPE_SPEED = 202;
    public static final int DLM_EVENT_TYPE_STRING_MARK_USE = 100;
    static final int DLM_EVENT_TYPE_TIME = 200;

    /* loaded from: classes.dex */
    public interface ACAddWordDLMEvent extends ACDLMEvent {
        int getCategoryId();

        int getLanguageId();

        String getWord();
    }

    /* loaded from: classes.dex */
    public interface ACAddWordPairDLMEvent extends ACDLMEvent {
    }

    /* loaded from: classes.dex */
    public interface ACAdjustQualityDLMEvent extends ACDLMEvent {
    }

    /* loaded from: classes.dex */
    public interface ACContentScannedDLMEvent extends ACDLMEvent {
    }

    /* loaded from: classes.dex */
    public interface ACDLMEvent {
        int getType();

        String toString();
    }

    /* loaded from: classes.dex */
    public interface ACDeleteCategoryDLMEvent extends ACDLMEvent {
        int getCategoryId();
    }

    /* loaded from: classes.dex */
    public interface ACDeleteCategoryLanguageDLMEvent extends ACDLMEvent {
        int getCategoryId();

        int getLanguageId();
    }

    /* loaded from: classes.dex */
    public interface ACDeleteLanguageDLMEvent extends ACDLMEvent {
        int getLanguageId();
    }

    /* loaded from: classes.dex */
    public interface ACDeleteWordDLMEvent extends ACDLMEvent {
        String getWord();
    }

    /* loaded from: classes.dex */
    public interface ACMarkNonUseDLMEvent extends ACDLMEvent {
    }

    /* loaded from: classes.dex */
    public interface ACMarkUndoUseDLMEvent extends ACDLMEvent {
    }

    /* loaded from: classes.dex */
    public interface ACMarkUseDLMEvent extends ACDLMEvent {
    }

    /* loaded from: classes.dex */
    public interface ACMarkUseSyllableDLMEvent extends ACDLMEvent {
    }

    /* loaded from: classes.dex */
    public interface ACNewCategoryDLMEvent extends ACDLMEvent {
        int getCategoryId();

        String getCategoryInfo();

        String getCategoryName();
    }

    /* loaded from: classes.dex */
    public interface ACNewWordSyllablePairDLMEvent extends ACDLMEvent {
        int getCategoryId();

        int getLanguageId();

        String getSyllables();

        String getWord();
    }

    /* loaded from: classes.dex */
    public interface ACStringMarkUseDLMEvent extends ACDLMEvent {
        int getCategoryId();

        int getLanguageId();
    }
}
