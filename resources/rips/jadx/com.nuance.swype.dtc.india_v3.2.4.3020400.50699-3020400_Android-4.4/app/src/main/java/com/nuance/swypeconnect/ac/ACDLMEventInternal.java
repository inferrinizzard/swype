package com.nuance.swypeconnect.ac;

import com.nuance.connect.util.Logger;
import com.nuance.swypeconnect.ac.ACDLMEventData;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public abstract class ACDLMEventInternal implements ACDLMEventData.ACDLMEvent {
    public static final int ADD_WORD = 21;
    public static final int ADD_WORD_PAIR = 60;
    public static final int ADJUST_QUALITY = 26;
    public static final int CONTENT_SCANNED = 80;
    public static final int DELETE_ALL = 40;
    public static final int DELETE_CATEGORY = 41;
    public static final int DELETE_CATEGORY_LANGUAGE = 43;
    public static final int DELETE_LANGUAGE = 42;
    public static final int DELETE_WORD = 22;
    public static final int LOCATION = 201;
    public static final int MARK_NON_USE = 25;
    public static final int MARK_UNDO = 24;
    public static final int MARK_USE = 23;
    public static final int MARK_USE_HP = 29;
    public static final int MARK_USE_NA = 28;
    public static final int MARK_USE_WITH_SYLLABLE = 62;
    public static final int MARK_USE_WITH_SYLLABLE_NA = 63;
    public static final int MAX_CATEGORY_INFO_LEN = 64;
    public static final int MAX_CATEGORY_NAME_LEN = 32;
    public static final int MAX_SPELLING_LEN = 160;
    public static final int MAX_WORD_LEN = 64;
    public static final int MAX_WORD_SYLLABLE_LEN = 32;
    public static final int NEW_CATEGORY = 20;
    public static final int NEW_WORD_NA = 27;
    public static final int NEW_WORD_SYLLABLE_PAIR = 61;
    public static final int NEW_WORD_SYLLABLE_PAIR_NA = 64;
    public static final int NEW_WORK_SYLLABLE_PAIR_EX = 65;
    public static final int SPEED = 202;
    public static final int STRING_MARK_USE = 100;
    public static final int TIME = 200;
    private static Logger.Log log = Logger.getLog(Logger.LoggerType.DEVELOPER, ACDLMEventInternal.class.getSimpleName());
    private int type;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class AddWordDlmEventImpl extends ACDLMEventInternal implements ACDLMEventData.ACAddWordDLMEvent {
        public final int categoryId;
        public final boolean highPriority;
        public final int languageId;
        public final byte quality;
        public final int useCount;
        public final byte[] word;
        public final int wordLen;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        AddWordDlmEventImpl(ByteBuffer byteBuffer, int i) {
            super(i);
            boolean z = false;
            this.word = new byte[128];
            this.categoryId = byteBuffer.getShort();
            this.languageId = byteBuffer.getShort();
            this.useCount = byteBuffer.getShort();
            this.quality = byteBuffer.get();
            this.wordLen = byteBuffer.get() & 255;
            byteBuffer.get(this.word, 0, this.wordLen * 2);
            if (this.categoryId <= 255 && this.quality != 2 && this.quality != 3) {
                z = true;
            }
            this.highPriority = z;
        }

        @Override // com.nuance.swypeconnect.ac.ACDLMEventData.ACAddWordDLMEvent
        public final int getCategoryId() {
            return this.categoryId;
        }

        @Override // com.nuance.swypeconnect.ac.ACDLMEventData.ACAddWordDLMEvent
        public final int getLanguageId() {
            return this.languageId;
        }

        @Override // com.nuance.swypeconnect.ac.ACDLMEventData.ACAddWordDLMEvent
        public final String getWord() {
            return new String(this.word, 0, this.wordLen * 2, Charset.forName("UTF-16"));
        }

        @Override // com.nuance.swypeconnect.ac.ACDLMEventData.ACDLMEvent
        public final String toString() {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(ACDLMEventData.ACAddWordDLMEvent.class.getSimpleName()).append("\nCategory=").append(this.categoryId).append("\nLanguage=").append(this.languageId).append("\nUse Count=").append(this.useCount).append("\nQuality=").append((int) this.quality).append("\nWord Len=").append(this.wordLen).append("\nWord=").append(getWord());
            return stringBuffer.toString();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class AddWordPairDlmEventImpl extends ACDLMEventInternal implements ACDLMEventData.ACAddWordPairDLMEvent {
        public final int categoryId;
        public final int languageId;
        public final byte quality;
        public final byte[] spelling;
        public final int spellingLen;
        public final int spellingType;
        public final int useCount;
        public final byte[] word;
        public final int wordLen;

        AddWordPairDlmEventImpl(ByteBuffer byteBuffer) {
            super(60);
            this.word = new byte[128];
            this.spelling = new byte[128];
            this.categoryId = byteBuffer.getShort();
            this.languageId = byteBuffer.getShort();
            this.useCount = byteBuffer.getShort();
            this.quality = byteBuffer.get();
            this.spellingType = byteBuffer.get() & 255;
            this.wordLen = byteBuffer.get() & 255;
            this.spellingLen = byteBuffer.get() & 255;
            byteBuffer.get(this.word, 0, this.wordLen * 2);
            byteBuffer.get(this.spelling, 0, this.spellingLen);
        }

        public final int getCategoryId() {
            return this.categoryId;
        }

        public final int getLanguageId() {
            return this.languageId;
        }

        public final String getSpelling() {
            return new String(this.spelling, 0, this.spellingLen * 2, Charset.forName("UTF-16"));
        }

        public final String getWord() {
            return new String(this.word, 0, this.wordLen * 2, Charset.forName("UTF-16"));
        }

        @Override // com.nuance.swypeconnect.ac.ACDLMEventData.ACDLMEvent
        public final String toString() {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(ACDLMEventData.ACAddWordPairDLMEvent.class.getSimpleName());
            return stringBuffer.toString();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class AdjustQualityDlmEventImpl extends ACDLMEventInternal implements ACDLMEventData.ACAdjustQualityDLMEvent {
        public final int categoryId;
        public final int languageId;
        public final byte quality;
        public final long wordHash;

        AdjustQualityDlmEventImpl(ByteBuffer byteBuffer) {
            super(26);
            this.wordHash = byteBuffer.getInt();
            this.categoryId = byteBuffer.getShort();
            this.languageId = byteBuffer.getShort();
            this.quality = byteBuffer.get();
        }

        public final int getCategoryId() {
            return this.categoryId;
        }

        public final int getLanguageId() {
            return this.languageId;
        }

        @Override // com.nuance.swypeconnect.ac.ACDLMEventData.ACDLMEvent
        public final String toString() {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(ACDLMEventData.ACAdjustQualityDLMEvent.class.getSimpleName());
            return stringBuffer.toString();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class Compiler {
        ACDLMEventData.ACDLMEvent compile(ByteBuffer byteBuffer) {
            int i;
            int i2;
            try {
                int position = byteBuffer.position();
                i = byteBuffer.get() & 255;
                try {
                    int i3 = byteBuffer.get() & 255;
                    i2 = position + (i3 == 0 ? 2 : i3);
                } catch (IllegalArgumentException e) {
                    e = e;
                    ACDLMEventInternal.log.e("IllegalArgumentException " + e.getMessage() + "; cat=" + i + ", len=-1");
                    return null;
                } catch (IndexOutOfBoundsException e2) {
                    e = e2;
                    ACDLMEventInternal.log.e("OutOfBounds " + e.getMessage() + "; cat=" + i + ", len=-1");
                    return null;
                } catch (BufferUnderflowException e3) {
                    e = e3;
                    ACDLMEventInternal.log.e("Underflow " + e.getMessage() + "; cat=" + i + ", len=-1");
                    return null;
                }
            } catch (IllegalArgumentException e4) {
                e = e4;
                i = -1;
            } catch (IndexOutOfBoundsException e5) {
                e = e5;
                i = -1;
            } catch (BufferUnderflowException e6) {
                e = e6;
                i = -1;
            }
            switch (i) {
                case 20:
                    return new NewCategoryDlmEventImpl(byteBuffer);
                case 21:
                    return new AddWordDlmEventImpl(byteBuffer, 21);
                case 22:
                    return new DeleteWordDlmEventImpl(byteBuffer);
                case 23:
                    return new MarkUseDlmEventImpl(byteBuffer, 23);
                case 24:
                    return new MarkUndoUseDlmEventImpl(byteBuffer);
                case 25:
                    return new MarkNonUseDlmEventImpl(byteBuffer);
                case 26:
                    return new AdjustQualityDlmEventImpl(byteBuffer);
                case 27:
                    return new AddWordDlmEventImpl(byteBuffer, 27);
                case 28:
                    return new MarkUseDlmEventImpl(byteBuffer, 28);
                case 29:
                    return new MarkUseDlmEventImpl(byteBuffer, 29);
                case 41:
                    return new DeleteCategoryDlmEventImpl(byteBuffer);
                case 42:
                    return new DeleteLanguageDlmEventImpl(byteBuffer);
                case 43:
                    return new DeleteCategoryLanguageDlmEventImpl(byteBuffer);
                case 60:
                    return new AddWordPairDlmEventImpl(byteBuffer);
                case 61:
                    return new NewWordSyllablePairDlmEventImpl(byteBuffer, 61);
                case 62:
                    return new MarkUseSyllableDlmEventImpl(byteBuffer, 62);
                case 63:
                    return new MarkUseSyllableDlmEventImpl(byteBuffer, 63);
                case 64:
                    return new NewWordSyllablePairDlmEventImpl(byteBuffer, 64);
                case 65:
                    return new NewWordSyllablePairDlmEventImpl(byteBuffer, 65);
                case 80:
                    return new ContentScannedDlmEventImpl(byteBuffer);
                case 100:
                    return new StringMarkUseDlmEventImpl(byteBuffer, i2);
                default:
                    byteBuffer.position(i2);
                    return null;
            }
        }

        public ACDLMEventData.ACDLMEvent compile(byte[] bArr) {
            return compile(ByteBuffer.wrap(bArr));
        }

        public ACDLMEventData.ACDLMEvent[] compileAll(byte[] bArr) {
            ByteBuffer wrap = ByteBuffer.wrap(bArr);
            ArrayList arrayList = new ArrayList();
            while (wrap.hasRemaining()) {
                ACDLMEventData.ACDLMEvent compile = compile(wrap);
                if (compile != null) {
                    arrayList.add(compile);
                }
            }
            return (ACDLMEventData.ACDLMEvent[]) arrayList.toArray(new ACDLMEventData.ACDLMEvent[arrayList.size()]);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class ContentScannedDlmEventImpl extends ACDLMEventInternal implements ACDLMEventData.ACContentScannedDLMEvent {
        public final long contentHash;

        ContentScannedDlmEventImpl(ByteBuffer byteBuffer) {
            super(80);
            this.contentHash = byteBuffer.getInt();
        }

        @Override // com.nuance.swypeconnect.ac.ACDLMEventData.ACDLMEvent
        public final String toString() {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(ACDLMEventData.ACContentScannedDLMEvent.class.getSimpleName());
            return stringBuffer.toString();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class DeleteCategoryDlmEventImpl extends ACDLMEventInternal implements ACDLMEventData.ACDeleteCategoryDLMEvent {
        public final int categoryId;

        DeleteCategoryDlmEventImpl(ByteBuffer byteBuffer) {
            super(41);
            this.categoryId = byteBuffer.getShort();
        }

        @Override // com.nuance.swypeconnect.ac.ACDLMEventData.ACDeleteCategoryDLMEvent
        public final int getCategoryId() {
            return this.categoryId;
        }

        @Override // com.nuance.swypeconnect.ac.ACDLMEventData.ACDLMEvent
        public final String toString() {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(ACDLMEventData.ACDeleteCategoryDLMEvent.class.getSimpleName()).append("\nCategoryID=").append(this.categoryId);
            return stringBuffer.toString();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class DeleteCategoryLanguageDlmEventImpl extends ACDLMEventInternal implements ACDLMEventData.ACDeleteCategoryLanguageDLMEvent {
        public final int categoryId;
        public final int languageId;

        DeleteCategoryLanguageDlmEventImpl(ByteBuffer byteBuffer) {
            super(43);
            this.categoryId = byteBuffer.getShort();
            this.languageId = byteBuffer.getShort();
        }

        @Override // com.nuance.swypeconnect.ac.ACDLMEventData.ACDeleteCategoryLanguageDLMEvent
        public final int getCategoryId() {
            return this.categoryId;
        }

        @Override // com.nuance.swypeconnect.ac.ACDLMEventData.ACDeleteCategoryLanguageDLMEvent
        public final int getLanguageId() {
            return this.languageId;
        }

        @Override // com.nuance.swypeconnect.ac.ACDLMEventData.ACDLMEvent
        public final String toString() {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(ACDLMEventData.ACDeleteCategoryLanguageDLMEvent.class.getSimpleName()).append("Category=").append(this.categoryId).append("\nLanguage=").append(this.languageId);
            return stringBuffer.toString();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class DeleteLanguageDlmEventImpl extends ACDLMEventInternal implements ACDLMEventData.ACDeleteLanguageDLMEvent {
        public final int languageId;

        DeleteLanguageDlmEventImpl(ByteBuffer byteBuffer) {
            super(42);
            this.languageId = byteBuffer.getShort();
        }

        @Override // com.nuance.swypeconnect.ac.ACDLMEventData.ACDeleteLanguageDLMEvent
        public final int getLanguageId() {
            return this.languageId;
        }

        @Override // com.nuance.swypeconnect.ac.ACDLMEventData.ACDLMEvent
        public final String toString() {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(ACDLMEventData.ACDeleteLanguageDLMEvent.class.getSimpleName()).append("\nLanguage=").append(this.languageId);
            return stringBuffer.toString();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class DeleteWordDlmEventImpl extends ACDLMEventInternal implements ACDLMEventData.ACDeleteWordDLMEvent {
        public final byte[] word;
        public final int wordLen;

        DeleteWordDlmEventImpl(ByteBuffer byteBuffer) {
            super(22);
            this.word = new byte[128];
            this.wordLen = byteBuffer.get() & 255;
            byteBuffer.get(this.word, 0, this.wordLen * 2);
        }

        @Override // com.nuance.swypeconnect.ac.ACDLMEventData.ACDeleteWordDLMEvent
        public final String getWord() {
            return new String(this.word, 0, this.wordLen * 2, Charset.forName("UTF-16"));
        }

        @Override // com.nuance.swypeconnect.ac.ACDLMEventData.ACDLMEvent
        public final String toString() {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(ACDLMEventData.ACDeleteWordDLMEvent.class.getSimpleName()).append("\nWord Len=").append(this.wordLen).append("\nWord=").append(getWord());
            return stringBuffer.toString();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class MarkNonUseDlmEventImpl extends ACDLMEventInternal implements ACDLMEventData.ACMarkNonUseDLMEvent {
        public final int categoryId;
        public final int languageId;
        public final long wordHash;

        MarkNonUseDlmEventImpl(ByteBuffer byteBuffer) {
            super(25);
            this.wordHash = byteBuffer.getInt();
            this.categoryId = byteBuffer.getShort();
            this.languageId = byteBuffer.getShort();
        }

        public final int getCategoryId() {
            return this.categoryId;
        }

        public final int getLanguageId() {
            return this.languageId;
        }

        @Override // com.nuance.swypeconnect.ac.ACDLMEventData.ACDLMEvent
        public final String toString() {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(ACDLMEventData.ACMarkNonUseDLMEvent.class.getSimpleName());
            return stringBuffer.toString();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class MarkUndoUseDlmEventImpl extends ACDLMEventInternal implements ACDLMEventData.ACMarkUndoUseDLMEvent {
        public final int categoryId;
        public final long contextHash;
        public final int languageId;
        public final int order;
        public final int updateUnigram;
        public final int useCount;
        public final long wordHash;

        MarkUndoUseDlmEventImpl(ByteBuffer byteBuffer) {
            super(24);
            this.order = byteBuffer.get() & 255;
            this.contextHash = byteBuffer.getInt();
            this.wordHash = byteBuffer.getInt();
            this.categoryId = byteBuffer.getShort();
            this.languageId = byteBuffer.getShort();
            this.useCount = byteBuffer.getShort();
            this.updateUnigram = byteBuffer.get() & 255;
        }

        public final int getCategoryId() {
            return this.categoryId;
        }

        public final int getLanguageId() {
            return this.languageId;
        }

        @Override // com.nuance.swypeconnect.ac.ACDLMEventData.ACDLMEvent
        public final String toString() {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(ACDLMEventData.ACMarkUndoUseDLMEvent.class.getSimpleName());
            return stringBuffer.toString();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class MarkUseDlmEventImpl extends ACDLMEventInternal implements ACDLMEventData.ACMarkUseDLMEvent {
        public final int categoryId;
        public final long contextHash;
        public final int languageId;
        public final int order;
        public final int updateUnigram;
        public final int useCount;
        public final long wordHash;

        MarkUseDlmEventImpl(ByteBuffer byteBuffer, int i) {
            super(i);
            this.order = byteBuffer.get() & 255;
            this.contextHash = byteBuffer.getInt();
            this.wordHash = byteBuffer.getInt();
            this.categoryId = byteBuffer.getShort();
            this.languageId = byteBuffer.getShort();
            this.useCount = byteBuffer.getShort();
            this.updateUnigram = byteBuffer.get() & 255;
        }

        public final int getCategoryId() {
            return this.categoryId;
        }

        public final int getLanguageId() {
            return this.languageId;
        }

        @Override // com.nuance.swypeconnect.ac.ACDLMEventData.ACDLMEvent
        public final String toString() {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(ACDLMEventData.ACMarkUseDLMEvent.class.getSimpleName());
            return stringBuffer.toString();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class MarkUseSyllableDlmEventImpl extends ACDLMEventInternal implements ACDLMEventData.ACMarkUseSyllableDLMEvent {
        public final int categoryId;
        public final long contextHash;
        public final int languageId;
        public final int order;
        public final long syllableHash;
        public final int updateUnigram;
        public final int useCount;
        public final long wordHash;

        MarkUseSyllableDlmEventImpl(ByteBuffer byteBuffer, int i) {
            super(i);
            this.order = byteBuffer.get() & 255;
            this.contextHash = byteBuffer.getInt();
            this.wordHash = byteBuffer.getInt();
            this.syllableHash = byteBuffer.getInt();
            this.categoryId = byteBuffer.getShort();
            this.languageId = byteBuffer.getShort();
            this.useCount = byteBuffer.getShort();
            this.updateUnigram = byteBuffer.get() & 255;
        }

        public final int getCategoryId() {
            return this.categoryId;
        }

        public final int getLanguageId() {
            return this.languageId;
        }

        @Override // com.nuance.swypeconnect.ac.ACDLMEventData.ACDLMEvent
        public final String toString() {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(ACDLMEventData.ACMarkUseSyllableDLMEvent.class.getSimpleName());
            return stringBuffer.toString();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class NewCategoryDlmEventImpl extends ACDLMEventInternal implements ACDLMEventData.ACNewCategoryDLMEvent {
        public final int active;
        public final int categoryId;
        public final byte[] categoryInfo;
        public final byte[] categoryName;
        public final int infoLen;
        public final int nameLen;
        public final long paddingCt;
        public final long properties;

        NewCategoryDlmEventImpl(ByteBuffer byteBuffer) {
            super(20);
            this.categoryName = new byte[64];
            this.categoryInfo = new byte[128];
            this.categoryId = byteBuffer.getShort();
            byteBuffer.position(byteBuffer.position() + 5);
            this.paddingCt = byteBuffer.getInt();
            byteBuffer.position(byteBuffer.position() + 5);
            this.properties = byteBuffer.getInt();
            this.active = byteBuffer.get();
            this.nameLen = byteBuffer.get();
            byteBuffer.get(this.categoryName, 0, this.nameLen * 2);
            this.infoLen = byteBuffer.get();
            byteBuffer.get(this.categoryInfo, 0, this.infoLen * 2);
        }

        @Override // com.nuance.swypeconnect.ac.ACDLMEventData.ACNewCategoryDLMEvent
        public final int getCategoryId() {
            return this.categoryId;
        }

        @Override // com.nuance.swypeconnect.ac.ACDLMEventData.ACNewCategoryDLMEvent
        public final String getCategoryInfo() {
            return new String(this.categoryInfo, 0, this.infoLen * 2, Charset.forName("UTF-16"));
        }

        @Override // com.nuance.swypeconnect.ac.ACDLMEventData.ACNewCategoryDLMEvent
        public final String getCategoryName() {
            return new String(this.categoryName, 0, this.nameLen * 2, Charset.forName("UTF-16"));
        }

        @Override // com.nuance.swypeconnect.ac.ACDLMEventData.ACDLMEvent
        public final String toString() {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(ACDLMEventData.ACNewCategoryDLMEvent.class.getSimpleName()).append("\nCategory=").append(this.categoryId).append("\nName Len=").append(this.nameLen).append("\nInfo Len=").append(this.infoLen).append("\nName=").append(getCategoryName()).append("\nInfo=").append(getCategoryInfo());
            return stringBuffer.toString();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class NewWordSyllablePairDlmEventImpl extends ACDLMEventInternal implements ACDLMEventData.ACNewWordSyllablePairDLMEvent {
        public final int categoryId;
        public final int languageId;
        public final byte quality;
        public final byte sylCount;
        public final byte[] syllables;
        public final int useCount;
        public final byte[] word;
        public final int wordLen;

        NewWordSyllablePairDlmEventImpl(ByteBuffer byteBuffer, int i) {
            super(i);
            this.word = new byte[64];
            this.syllables = new byte[64];
            this.categoryId = byteBuffer.getShort();
            this.languageId = byteBuffer.getShort();
            this.useCount = byteBuffer.getShort();
            this.quality = byteBuffer.get();
            this.wordLen = byteBuffer.get() & 255;
            this.sylCount = byteBuffer.get();
            byteBuffer.get(this.word, 0, this.wordLen * 2);
            byteBuffer.get(this.syllables, 0, this.sylCount * 2);
        }

        @Override // com.nuance.swypeconnect.ac.ACDLMEventData.ACNewWordSyllablePairDLMEvent
        public final int getCategoryId() {
            return this.categoryId;
        }

        @Override // com.nuance.swypeconnect.ac.ACDLMEventData.ACNewWordSyllablePairDLMEvent
        public final int getLanguageId() {
            return this.languageId;
        }

        @Override // com.nuance.swypeconnect.ac.ACDLMEventData.ACNewWordSyllablePairDLMEvent
        public final String getSyllables() {
            return new String(this.syllables, 0, this.sylCount * 2, Charset.forName("UTF-16"));
        }

        @Override // com.nuance.swypeconnect.ac.ACDLMEventData.ACNewWordSyllablePairDLMEvent
        public final String getWord() {
            return new String(this.word, 0, this.wordLen * 2, Charset.forName("UTF-16"));
        }

        @Override // com.nuance.swypeconnect.ac.ACDLMEventData.ACDLMEvent
        public final String toString() {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(ACDLMEventData.ACNewWordSyllablePairDLMEvent.class.getSimpleName()).append("\nCategory=").append(this.categoryId).append("\nLanguage=").append(this.languageId).append("\nUse Count=").append(this.useCount).append("\nQuality=").append((int) this.quality).append("\nWord Len=").append(this.wordLen).append("\nWord=").append(getWord()).append("\nSyllable Len=").append(this.wordLen).append("\nSyllables=").append(getSyllables());
            return stringBuffer.toString();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class StringMarkUseDlmEventImpl extends ACDLMEventInternal implements ACDLMEventData.ACStringMarkUseDLMEvent {
        public final int categoryId;
        public final int languageId;
        public final byte quality;
        public final byte usage;

        StringMarkUseDlmEventImpl(ByteBuffer byteBuffer, int i) {
            super(100);
            this.categoryId = byteBuffer.getShort();
            this.languageId = byteBuffer.getShort();
            this.quality = byteBuffer.get();
            this.usage = byteBuffer.get();
            byteBuffer.position(i);
        }

        @Override // com.nuance.swypeconnect.ac.ACDLMEventData.ACStringMarkUseDLMEvent
        public final int getCategoryId() {
            return this.categoryId;
        }

        @Override // com.nuance.swypeconnect.ac.ACDLMEventData.ACStringMarkUseDLMEvent
        public final int getLanguageId() {
            return this.languageId;
        }

        @Override // com.nuance.swypeconnect.ac.ACDLMEventData.ACDLMEvent
        public final String toString() {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(ACDLMEventData.ACStringMarkUseDLMEvent.class.getSimpleName()).append("Category=").append(this.categoryId).append("\nLanguage=").append(this.languageId);
            return stringBuffer.toString();
        }
    }

    private ACDLMEventInternal(int i) {
        this.type = i;
    }

    @Override // com.nuance.swypeconnect.ac.ACDLMEventData.ACDLMEvent
    public int getType() {
        return this.type;
    }
}
