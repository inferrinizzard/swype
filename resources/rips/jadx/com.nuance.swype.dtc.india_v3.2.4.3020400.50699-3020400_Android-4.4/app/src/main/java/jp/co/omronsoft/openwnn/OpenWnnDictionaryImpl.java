package jp.co.omronsoft.openwnn;

import android.content.ContentValues;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import com.nuance.swype.input.R;
import java.lang.reflect.Array;

/* loaded from: classes.dex */
public class OpenWnnDictionaryImpl implements WnnDictionary {
    protected static final String COLUMN_NAME_CANDIDATE = "candidate";
    protected static final String COLUMN_NAME_ID = "rowid";
    protected static final String COLUMN_NAME_POS_LEFT = "posLeft";
    protected static final String COLUMN_NAME_POS_RIGHT = "posRight";
    protected static final String COLUMN_NAME_PREVIOUS_CANDIDATE = "prevCandidate";
    protected static final String COLUMN_NAME_PREVIOUS_POS_LEFT = "prevPosLeft";
    protected static final String COLUMN_NAME_PREVIOUS_POS_RIGHT = "prevPosRight";
    protected static final String COLUMN_NAME_PREVIOUS_STROKE = "prevStroke";
    protected static final String COLUMN_NAME_STROKE = "stroke";
    protected static final String COLUMN_NAME_TYPE = "type";
    protected static final int FAST_QUERY_LENGTH = 20;
    protected static final String LINK_QUERY = "select distinct stroke,candidate,posLeft,posRight,type from dic where %s = ? and %s = ? and %s order by type DESC, %s";
    public static final int MAX_CANDIDATE_LENGTH = 50;
    protected static final int MAX_LENGTH_OF_QUERY = 50;
    protected static final int MAX_PATTERN_OF_APPROX = 6;
    public static final int MAX_STROKE_LENGTH = 50;
    protected static final int MAX_WORDS_IN_LEARN_DICTIONARY = 2000;
    protected static final int MAX_WORDS_IN_USER_DICTIONARY = 100;
    protected static final String NORMAL_QUERY = "select distinct stroke,candidate,posLeft,posRight,type from dic where %s order by type DESC, %s";
    protected static final int OFFSET_FREQUENCY_OF_LEARN_DICTIONARY = 2000;
    protected static final int OFFSET_FREQUENCY_OF_USER_DICTIONARY = 1000;
    protected static final String TABLE_NAME_DIC = "dic";
    protected static final int TYPE_NAME_LEARN = 1;
    protected static final int TYPE_NAME_USER = 0;
    protected int mCountCursor;
    protected SQLiteCursor mDbCursor;
    protected SQLiteDatabase mDbDic;
    protected String mDicFilePath;
    protected String[] mExactQueryArgs;
    protected String mExactQuerySqlOrderByFreq;
    protected String mExactQuerySqlOrderByKey;
    protected String mFastLinkQuerySqlOrderByFreq;
    protected String mFastLinkQuerySqlOrderByKey;
    protected String mFastPrefixQuerySqlOrderByFreq;
    protected String mFastPrefixQuerySqlOrderByKey;
    protected String[] mFastQueryArgs;
    protected int mFrequencyOffsetOfLearnDictionary;
    protected int mFrequencyOffsetOfUserDictionary;
    protected String mFullLinkQuerySqlOrderByFreq;
    protected String mFullLinkQuerySqlOrderByKey;
    protected String mFullPrefixQuerySqlOrderByFreq;
    protected String mFullPrefixQuerySqlOrderByKey;
    protected String[] mFullQueryArgs;
    protected int mTypeOfQuery;
    protected long mWnnWork;

    public OpenWnnDictionaryImpl(String dicLibPath) {
        this(dicLibPath, null);
    }

    public OpenWnnDictionaryImpl(String dicLibPath, String dicFilePath) {
        this.mWnnWork = 0L;
        this.mDicFilePath = "";
        this.mDbDic = null;
        this.mDbCursor = null;
        this.mCountCursor = 0;
        this.mTypeOfQuery = -1;
        this.mExactQueryArgs = new String[1];
        this.mFullQueryArgs = new String[R.styleable.ThemeTemplate_symKeyboardHwr123];
        this.mFastQueryArgs = new String[R.styleable.ThemeTemplate_glowStretch];
        this.mFrequencyOffsetOfUserDictionary = -1;
        this.mFrequencyOffsetOfLearnDictionary = -1;
        this.mWnnWork = OpenWnnDictionaryImplJni.createWnnWork(dicLibPath);
        if (this.mWnnWork != 0 && dicFilePath != null) {
            String queryFullBaseString = OpenWnnDictionaryImplJni.createQueryStringBase(this.mWnnWork, 50, 6, "stroke");
            String queryFastBaseString = OpenWnnDictionaryImplJni.createQueryStringBase(this.mWnnWork, 20, 6, "stroke");
            this.mExactQuerySqlOrderByFreq = String.format(NORMAL_QUERY, String.format("%s=?", "stroke"), String.format("%s DESC", COLUMN_NAME_ID));
            this.mExactQuerySqlOrderByKey = String.format(NORMAL_QUERY, String.format("%s=?", "stroke"), "stroke");
            this.mFullPrefixQuerySqlOrderByFreq = String.format(NORMAL_QUERY, queryFullBaseString, String.format("%s DESC", COLUMN_NAME_ID));
            this.mFastPrefixQuerySqlOrderByFreq = String.format(NORMAL_QUERY, queryFastBaseString, String.format("%s DESC", COLUMN_NAME_ID));
            this.mFullPrefixQuerySqlOrderByKey = String.format(NORMAL_QUERY, queryFullBaseString, "stroke");
            this.mFastPrefixQuerySqlOrderByKey = String.format(NORMAL_QUERY, queryFastBaseString, "stroke");
            this.mFullLinkQuerySqlOrderByFreq = String.format(LINK_QUERY, COLUMN_NAME_PREVIOUS_STROKE, COLUMN_NAME_PREVIOUS_CANDIDATE, queryFullBaseString, String.format("%s DESC", COLUMN_NAME_ID));
            this.mFastLinkQuerySqlOrderByFreq = String.format(LINK_QUERY, COLUMN_NAME_PREVIOUS_STROKE, COLUMN_NAME_PREVIOUS_CANDIDATE, queryFastBaseString, String.format("%s DESC", COLUMN_NAME_ID));
            this.mFullLinkQuerySqlOrderByKey = String.format(LINK_QUERY, COLUMN_NAME_PREVIOUS_STROKE, COLUMN_NAME_PREVIOUS_CANDIDATE, queryFullBaseString, "stroke");
            this.mFastLinkQuerySqlOrderByKey = String.format(LINK_QUERY, COLUMN_NAME_PREVIOUS_STROKE, COLUMN_NAME_PREVIOUS_CANDIDATE, queryFastBaseString, "stroke");
            try {
                this.mDicFilePath = dicFilePath;
                setInUseState(true);
                createDictionaryTable(TABLE_NAME_DIC);
            } catch (SQLException e) {
            }
        }
    }

    protected void finalize() {
        if (this.mWnnWork != 0) {
            OpenWnnDictionaryImplJni.freeWnnWork(this.mWnnWork);
            this.mWnnWork = 0L;
            freeDatabase();
        }
    }

    protected void createDictionaryTable(String tableName) {
        String sqlStr = "create table if not exists " + tableName + " (rowid integer primary key autoincrement, type integer, stroke text, candidate text, posLeft integer, posRight integer, prevStroke text, prevCandidate text, prevPosLeft integer, prevPosRight integer)";
        if (this.mDbDic != null) {
            this.mDbDic.execSQL(sqlStr);
        }
    }

    protected void freeDatabase() {
        freeCursor();
        if (this.mDbDic != null) {
            this.mDbDic.close();
            this.mDbDic = null;
        }
    }

    protected void freeCursor() {
        if (this.mDbCursor != null) {
            this.mDbCursor.close();
            this.mDbCursor = null;
            this.mTypeOfQuery = -1;
        }
    }

    @Override // jp.co.omronsoft.openwnn.WnnDictionary
    public boolean isActive() {
        return this.mWnnWork != 0;
    }

    @Override // jp.co.omronsoft.openwnn.WnnDictionary
    public boolean setInUseState(boolean flag) {
        if (flag) {
            try {
                if (this.mDbDic == null) {
                    this.mDbDic = SQLiteDatabase.openOrCreateDatabase(this.mDicFilePath, (SQLiteDatabase.CursorFactory) null);
                }
            } catch (SQLException e) {
            }
            return this.mDbDic != null;
        }
        freeDatabase();
        return true;
    }

    @Override // jp.co.omronsoft.openwnn.WnnDictionary
    public int clearDictionary() {
        if (this.mWnnWork == 0) {
            return -1;
        }
        this.mFrequencyOffsetOfUserDictionary = -1;
        this.mFrequencyOffsetOfLearnDictionary = -1;
        return OpenWnnDictionaryImplJni.clearDictionaryParameters(this.mWnnWork);
    }

    @Override // jp.co.omronsoft.openwnn.WnnDictionary
    public int setDictionary(int index, int base, int high) {
        if (this.mWnnWork == 0) {
            return -1;
        }
        switch (index) {
            case -2:
                if (base < 0 || high < 0 || base > high) {
                    this.mFrequencyOffsetOfLearnDictionary = -1;
                    return 0;
                }
                this.mFrequencyOffsetOfLearnDictionary = high;
                return 0;
            case -1:
                if (base < 0 || high < 0 || base > high) {
                    this.mFrequencyOffsetOfUserDictionary = -1;
                    return 0;
                }
                this.mFrequencyOffsetOfUserDictionary = high;
                return 0;
            default:
                return OpenWnnDictionaryImplJni.setDictionaryParameter(this.mWnnWork, index, base, high);
        }
    }

    protected void createQuery(String keyString, WnnWord wnnWord, int operation, int order) {
        String querySqlOrderByFreq;
        String querySqlOrderByKey;
        int newTypeOfQuery;
        int maxBindsOfQuery;
        String[] queryArgs;
        if (operation != 2) {
            wnnWord = null;
        }
        switch (operation) {
            case 0:
                querySqlOrderByFreq = this.mExactQuerySqlOrderByFreq;
                querySqlOrderByKey = this.mExactQuerySqlOrderByKey;
                newTypeOfQuery = 0;
                queryArgs = this.mExactQueryArgs;
                queryArgs[0] = keyString;
                break;
            case 1:
            case 2:
                if (keyString.length() <= 20) {
                    if (wnnWord != null) {
                        querySqlOrderByFreq = this.mFastLinkQuerySqlOrderByFreq;
                        querySqlOrderByKey = this.mFastLinkQuerySqlOrderByKey;
                        newTypeOfQuery = 1;
                    } else {
                        querySqlOrderByFreq = this.mFastPrefixQuerySqlOrderByFreq;
                        querySqlOrderByKey = this.mFastPrefixQuerySqlOrderByKey;
                        newTypeOfQuery = 2;
                    }
                    maxBindsOfQuery = 20;
                } else {
                    if (wnnWord != null) {
                        querySqlOrderByFreq = this.mFullLinkQuerySqlOrderByFreq;
                        querySqlOrderByKey = this.mFullLinkQuerySqlOrderByKey;
                        newTypeOfQuery = 3;
                    } else {
                        querySqlOrderByFreq = this.mFullPrefixQuerySqlOrderByFreq;
                        querySqlOrderByKey = this.mFullPrefixQuerySqlOrderByKey;
                        newTypeOfQuery = 4;
                    }
                    maxBindsOfQuery = 50;
                }
                if (wnnWord != null) {
                    String[] queryArgsTemp = OpenWnnDictionaryImplJni.createBindArray(this.mWnnWork, keyString, maxBindsOfQuery, 6);
                    queryArgs = new String[queryArgsTemp.length + 2];
                    for (int i = 0; i < queryArgsTemp.length; i++) {
                        queryArgs[i + 2] = queryArgsTemp[i];
                    }
                    queryArgs[0] = wnnWord.stroke;
                    queryArgs[1] = wnnWord.candidate;
                    break;
                } else {
                    queryArgs = OpenWnnDictionaryImplJni.createBindArray(this.mWnnWork, keyString, maxBindsOfQuery, 6);
                    break;
                }
            default:
                this.mCountCursor = 0;
                freeCursor();
                return;
        }
        this.mCountCursor = 0;
        if (this.mDbCursor == null || this.mTypeOfQuery != newTypeOfQuery) {
            freeCursor();
            try {
                switch (order) {
                    case 0:
                        this.mDbCursor = (SQLiteCursor) this.mDbDic.rawQuery(querySqlOrderByFreq, queryArgs);
                        break;
                    case 1:
                        this.mDbCursor = (SQLiteCursor) this.mDbDic.rawQuery(querySqlOrderByKey, queryArgs);
                        break;
                    default:
                        return;
                }
                this.mTypeOfQuery = newTypeOfQuery;
            } catch (SQLException e) {
                this.mCountCursor = 0;
                freeCursor();
                return;
            }
        } else {
            try {
                this.mDbCursor.setSelectionArguments(queryArgs);
                this.mDbCursor.requery();
            } catch (SQLException e2) {
                this.mCountCursor = 0;
                freeCursor();
                return;
            }
        }
        if (this.mDbCursor != null) {
            try {
                this.mCountCursor = this.mDbCursor.getCount();
                if (this.mCountCursor == 0) {
                    this.mDbCursor.deactivate();
                }
            } catch (SQLException e3) {
                this.mCountCursor = 0;
                freeCursor();
            }
        }
    }

    @Override // jp.co.omronsoft.openwnn.WnnDictionary
    public int searchWord(int operation, int order, String keyString) {
        OpenWnnDictionaryImplJni.clearResult(this.mWnnWork);
        if (this.mDbDic != null && (this.mFrequencyOffsetOfUserDictionary >= 0 || this.mFrequencyOffsetOfLearnDictionary >= 0)) {
            try {
                if (keyString.length() > 0) {
                    createQuery(keyString, null, operation, order);
                    if (this.mDbCursor != null) {
                        this.mDbCursor.moveToFirst();
                    }
                } else {
                    if (this.mDbCursor != null) {
                        this.mDbCursor.deactivate();
                    }
                    this.mCountCursor = 0;
                }
            } catch (SQLException e) {
                if (this.mDbCursor != null) {
                    this.mDbCursor.deactivate();
                }
                this.mCountCursor = 0;
            }
        } else {
            this.mCountCursor = 0;
        }
        if (this.mWnnWork != 0) {
            int ret = OpenWnnDictionaryImplJni.searchWord(this.mWnnWork, operation, order, keyString);
            if (this.mCountCursor > 0) {
                return 1;
            }
            return ret;
        }
        return -1;
    }

    @Override // jp.co.omronsoft.openwnn.WnnDictionary
    public int searchWord(int operation, int order, String keyString, WnnWord wnnWord) {
        if (wnnWord == null || wnnWord.partOfSpeech == null) {
            return -1;
        }
        if (this.mDbDic != null && (this.mFrequencyOffsetOfUserDictionary >= 0 || this.mFrequencyOffsetOfLearnDictionary >= 0)) {
            try {
                createQuery(keyString, wnnWord, operation, order);
                if (this.mDbCursor != null) {
                    this.mDbCursor.moveToFirst();
                }
            } catch (SQLException e) {
                if (this.mDbCursor != null) {
                    this.mDbCursor.deactivate();
                }
                this.mCountCursor = 0;
            }
        } else {
            this.mCountCursor = 0;
        }
        OpenWnnDictionaryImplJni.clearResult(this.mWnnWork);
        OpenWnnDictionaryImplJni.setStroke(this.mWnnWork, wnnWord.stroke);
        OpenWnnDictionaryImplJni.setCandidate(this.mWnnWork, wnnWord.candidate);
        OpenWnnDictionaryImplJni.setLeftPartOfSpeech(this.mWnnWork, wnnWord.partOfSpeech.left);
        OpenWnnDictionaryImplJni.setRightPartOfSpeech(this.mWnnWork, wnnWord.partOfSpeech.right);
        OpenWnnDictionaryImplJni.selectWord(this.mWnnWork);
        if (this.mWnnWork == 0) {
            return -1;
        }
        int searchWord = OpenWnnDictionaryImplJni.searchWord(this.mWnnWork, operation, order, keyString);
        if (this.mCountCursor > 0) {
            return 1;
        }
        return searchWord;
    }

    @Override // jp.co.omronsoft.openwnn.WnnDictionary
    public WnnWord getNextWord() {
        return getNextWord(0);
    }

    @Override // jp.co.omronsoft.openwnn.WnnDictionary
    public WnnWord getNextWord(int length) {
        if (this.mWnnWork == 0) {
            return null;
        }
        if (this.mDbDic != null && this.mDbCursor != null && this.mCountCursor > 0) {
            WnnWord result = new WnnWord();
            while (this.mCountCursor > 0 && ((this.mFrequencyOffsetOfUserDictionary < 0 && this.mDbCursor.getInt(4) == 0) || ((this.mFrequencyOffsetOfLearnDictionary < 0 && this.mDbCursor.getInt(4) == 1) || (length > 0 && this.mDbCursor.getString(0).length() != length)))) {
                try {
                    this.mDbCursor.moveToNext();
                    this.mCountCursor--;
                } catch (SQLException e) {
                    this.mDbCursor.deactivate();
                    this.mCountCursor = 0;
                }
            }
            if (this.mCountCursor > 0) {
                result.stroke = this.mDbCursor.getString(0);
                result.candidate = this.mDbCursor.getString(1);
                result.partOfSpeech.left = this.mDbCursor.getInt(2);
                result.partOfSpeech.right = this.mDbCursor.getInt(3);
                if (this.mDbCursor.getInt(4) == 0) {
                    result.frequency = this.mFrequencyOffsetOfUserDictionary;
                } else {
                    result.frequency = this.mFrequencyOffsetOfLearnDictionary;
                }
                this.mDbCursor.moveToNext();
                int i = this.mCountCursor - 1;
                this.mCountCursor = i;
                if (i <= 0) {
                    this.mDbCursor.deactivate();
                    return result;
                }
                return result;
            }
            this.mDbCursor.deactivate();
        }
        int res = OpenWnnDictionaryImplJni.getNextWord(this.mWnnWork, length);
        if (res <= 0) {
            return res == 0 ? null : null;
        }
        WnnWord result2 = new WnnWord();
        result2.stroke = OpenWnnDictionaryImplJni.getStroke(this.mWnnWork);
        result2.candidate = OpenWnnDictionaryImplJni.getCandidate(this.mWnnWork);
        result2.frequency = OpenWnnDictionaryImplJni.getFrequency(this.mWnnWork);
        result2.partOfSpeech.left = OpenWnnDictionaryImplJni.getLeftPartOfSpeech(this.mWnnWork);
        result2.partOfSpeech.right = OpenWnnDictionaryImplJni.getRightPartOfSpeech(this.mWnnWork);
        return result2;
    }

    @Override // jp.co.omronsoft.openwnn.WnnDictionary
    public WnnWord[] getUserDictionaryWords() {
        if (this.mWnnWork != 0 && this.mDbDic != null) {
            SQLiteCursor cursor = null;
            try {
                cursor = (SQLiteCursor) this.mDbDic.query(TABLE_NAME_DIC, new String[]{"stroke", COLUMN_NAME_CANDIDATE}, String.format("%s=%d", "type", 0), null, null, null, null);
                int numOfWords = cursor.getCount();
                if (numOfWords > 0) {
                    WnnWord[] words = new WnnWord[numOfWords];
                    cursor.moveToFirst();
                    for (int i = 0; i < numOfWords; i++) {
                        words[i] = new WnnWord();
                        words[i].stroke = cursor.getString(0);
                        words[i].candidate = cursor.getString(1);
                        cursor.moveToNext();
                    }
                    if (cursor == null) {
                        return words;
                    }
                    cursor.close();
                    return words;
                }
                if (cursor != null) {
                    cursor.close();
                }
            } catch (SQLException e) {
                if (cursor != null) {
                    cursor.close();
                }
                return null;
            } catch (Throwable th) {
                if (cursor != null) {
                    cursor.close();
                }
                throw th;
            }
        }
        return null;
    }

    @Override // jp.co.omronsoft.openwnn.WnnDictionary
    public void clearApproxPattern() {
        if (this.mWnnWork != 0) {
            OpenWnnDictionaryImplJni.clearApproxPatterns(this.mWnnWork);
        }
    }

    @Override // jp.co.omronsoft.openwnn.WnnDictionary
    public int setApproxPattern(String src, String dst) {
        if (this.mWnnWork != 0) {
            return OpenWnnDictionaryImplJni.setApproxPattern(this.mWnnWork, src, dst);
        }
        return -1;
    }

    @Override // jp.co.omronsoft.openwnn.WnnDictionary
    public int setApproxPattern(int approxPattern) {
        if (this.mWnnWork != 0) {
            return OpenWnnDictionaryImplJni.setApproxPattern(this.mWnnWork, approxPattern);
        }
        return -1;
    }

    @Override // jp.co.omronsoft.openwnn.WnnDictionary
    public byte[][] getConnectMatrix() {
        byte[][] result;
        if (this.mWnnWork != 0) {
            int lcount = OpenWnnDictionaryImplJni.getNumberOfLeftPOS(this.mWnnWork);
            result = new byte[lcount + 1];
            for (int i = 0; i < lcount + 1; i++) {
                result[i] = OpenWnnDictionaryImplJni.getConnectArray(this.mWnnWork, i);
                if (result[i] == null) {
                    return null;
                }
            }
        } else {
            result = (byte[][]) Array.newInstance((Class<?>) Byte.TYPE, 1, 1);
        }
        return result;
    }

    @Override // jp.co.omronsoft.openwnn.WnnDictionary
    public WnnPOS getPOS(int type) {
        WnnPOS result = new WnnPOS();
        if (this.mWnnWork != 0) {
            result.left = OpenWnnDictionaryImplJni.getLeftPartOfSpeechSpecifiedType(this.mWnnWork, type);
            result.right = OpenWnnDictionaryImplJni.getRightPartOfSpeechSpecifiedType(this.mWnnWork, type);
            if (result.left < 0 || result.right < 0) {
                return null;
            }
            return result;
        }
        return result;
    }

    @Override // jp.co.omronsoft.openwnn.WnnDictionary
    public int clearUserDictionary() {
        if (this.mDbDic != null) {
            this.mDbDic.execSQL(String.format("delete from %s where %s=%d", TABLE_NAME_DIC, "type", 0));
        }
        return 0;
    }

    @Override // jp.co.omronsoft.openwnn.WnnDictionary
    public int clearLearnDictionary() {
        if (this.mDbDic != null) {
            this.mDbDic.execSQL(String.format("delete from %s where %s=%d", TABLE_NAME_DIC, "type", 1));
        }
        return 0;
    }

    @Override // jp.co.omronsoft.openwnn.WnnDictionary
    public int addWordToUserDictionary(WnnWord[] word) {
        int result = 0;
        if (this.mDbDic != null) {
            SQLiteCursor cursor = (SQLiteCursor) this.mDbDic.query(TABLE_NAME_DIC, new String[]{COLUMN_NAME_ID}, String.format("%s=%d", "type", 0), null, null, null, null);
            int count = cursor.getCount();
            cursor.close();
            if (word.length + count > 100) {
                return -1;
            }
            this.mDbDic.beginTransaction();
            try {
                StringBuilder strokeSQL = new StringBuilder();
                StringBuilder candidateSQL = new StringBuilder();
                for (int index = 0; index < word.length; index++) {
                    if (word[index].stroke.length() > 0 && word[index].stroke.length() <= 50 && word[index].candidate.length() > 0 && word[index].candidate.length() <= 50) {
                        strokeSQL.setLength(0);
                        candidateSQL.setLength(0);
                        DatabaseUtils.appendEscapedSQLString(strokeSQL, word[index].stroke);
                        DatabaseUtils.appendEscapedSQLString(candidateSQL, word[index].candidate);
                        SQLiteCursor cursor2 = (SQLiteCursor) this.mDbDic.query(TABLE_NAME_DIC, new String[]{COLUMN_NAME_ID}, String.format("%s=%d and %s=%s and %s=%s", "type", 0, "stroke", strokeSQL.toString(), COLUMN_NAME_CANDIDATE, candidateSQL.toString()), null, null, null, null);
                        if (cursor2.getCount() > 0) {
                            result = -2;
                        } else {
                            ContentValues content = new ContentValues();
                            content.clear();
                            content.put("type", (Integer) 0);
                            content.put("stroke", word[index].stroke);
                            content.put(COLUMN_NAME_CANDIDATE, word[index].candidate);
                            content.put(COLUMN_NAME_POS_LEFT, Integer.valueOf(word[index].partOfSpeech.left));
                            content.put(COLUMN_NAME_POS_RIGHT, Integer.valueOf(word[index].partOfSpeech.right));
                            this.mDbDic.insert(TABLE_NAME_DIC, null, content);
                        }
                        cursor2.close();
                        cursor = null;
                    }
                }
                this.mDbDic.setTransactionSuccessful();
                this.mDbDic.endTransaction();
                if (cursor != null) {
                    cursor.close();
                }
            } catch (SQLException e) {
                this.mDbDic.endTransaction();
                if (cursor != null) {
                    cursor.close();
                }
                return -1;
            } catch (Throwable th) {
                this.mDbDic.endTransaction();
                if (cursor != null) {
                    cursor.close();
                }
                throw th;
            }
        }
        return result;
    }

    @Override // jp.co.omronsoft.openwnn.WnnDictionary
    public int addWordToUserDictionary(WnnWord word) {
        WnnWord[] words = {word};
        return addWordToUserDictionary(words);
    }

    @Override // jp.co.omronsoft.openwnn.WnnDictionary
    public int removeWordFromUserDictionary(WnnWord[] word) {
        if (this.mDbDic == null) {
            return 0;
        }
        this.mDbDic.beginTransaction();
        try {
            StringBuilder strokeSQL = new StringBuilder();
            StringBuilder candidateSQL = new StringBuilder();
            for (int index = 0; index < word.length; index++) {
                if (word[index].stroke.length() > 0 && word[index].stroke.length() <= 50 && word[index].candidate.length() > 0 && word[index].candidate.length() <= 50) {
                    strokeSQL.setLength(0);
                    candidateSQL.setLength(0);
                    DatabaseUtils.appendEscapedSQLString(strokeSQL, word[index].stroke);
                    DatabaseUtils.appendEscapedSQLString(candidateSQL, word[index].candidate);
                    this.mDbDic.delete(TABLE_NAME_DIC, String.format("%s=%d and %s=%s and %s=%s", "type", 0, "stroke", strokeSQL, COLUMN_NAME_CANDIDATE, candidateSQL), null);
                }
            }
            this.mDbDic.setTransactionSuccessful();
            this.mDbDic.endTransaction();
            return 0;
        } catch (SQLException e) {
            this.mDbDic.endTransaction();
            return -1;
        } catch (Throwable th) {
            this.mDbDic.endTransaction();
            throw th;
        }
    }

    @Override // jp.co.omronsoft.openwnn.WnnDictionary
    public int removeWordFromUserDictionary(WnnWord word) {
        WnnWord[] words = {word};
        return removeWordFromUserDictionary(words);
    }

    @Override // jp.co.omronsoft.openwnn.WnnDictionary
    public int learnWord(WnnWord word) {
        return learnWord(word, null);
    }

    @Override // jp.co.omronsoft.openwnn.WnnDictionary
    public int learnWord(WnnWord word, WnnWord previousWord) {
        if (this.mDbDic != null) {
            StringBuilder previousStrokeSQL = new StringBuilder();
            StringBuilder previousCandidateSQL = new StringBuilder();
            boolean isConnectLearn = false;
            if (previousWord != null && previousWord.stroke.length() > 0 && previousWord.stroke.length() <= 50 && previousWord.candidate.length() > 0 && previousWord.candidate.length() <= 50) {
                DatabaseUtils.appendEscapedSQLString(previousStrokeSQL, previousWord.stroke);
                DatabaseUtils.appendEscapedSQLString(previousCandidateSQL, previousWord.candidate);
                isConnectLearn = true;
            }
            if (word.stroke.length() > 0 && word.stroke.length() <= 50 && word.candidate.length() > 0 && word.candidate.length() <= 50) {
                StringBuilder strokeSQL = new StringBuilder();
                StringBuilder candidateSQL = new StringBuilder();
                DatabaseUtils.appendEscapedSQLString(strokeSQL, word.stroke);
                DatabaseUtils.appendEscapedSQLString(candidateSQL, word.candidate);
                SQLiteCursor cursor = (SQLiteCursor) this.mDbDic.query(TABLE_NAME_DIC, new String[]{COLUMN_NAME_ID, "stroke", COLUMN_NAME_CANDIDATE, COLUMN_NAME_POS_LEFT, COLUMN_NAME_POS_RIGHT, COLUMN_NAME_PREVIOUS_STROKE, COLUMN_NAME_PREVIOUS_CANDIDATE, COLUMN_NAME_PREVIOUS_POS_LEFT, COLUMN_NAME_PREVIOUS_POS_RIGHT}, String.format("%s=%d and %s=%s and %s=%s", "type", 1, "stroke", strokeSQL.toString(), COLUMN_NAME_CANDIDATE, candidateSQL.toString()), null, null, null, String.format("%s ASC", COLUMN_NAME_ID));
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    ContentValues content = new ContentValues();
                    int[] idArray = new int[cursor.getCount()];
                    int idIndex = 0;
                    boolean isExistConnectLearnData = false;
                    this.mDbDic.beginTransaction();
                    do {
                        int idIndex2 = idIndex;
                        idIndex = idIndex2 + 1;
                        try {
                            idArray[idIndex2] = cursor.getInt(0);
                            content.clear();
                            content.put("type", (Integer) 1);
                            content.put("stroke", cursor.getString(1));
                            content.put(COLUMN_NAME_CANDIDATE, cursor.getString(2));
                            content.put(COLUMN_NAME_POS_LEFT, Integer.valueOf(cursor.getInt(3)));
                            content.put(COLUMN_NAME_POS_RIGHT, Integer.valueOf(cursor.getInt(4)));
                            content.put(COLUMN_NAME_PREVIOUS_STROKE, cursor.getString(5));
                            content.put(COLUMN_NAME_PREVIOUS_CANDIDATE, cursor.getString(6));
                            content.put(COLUMN_NAME_PREVIOUS_POS_LEFT, Integer.valueOf(cursor.getInt(7)));
                            content.put(COLUMN_NAME_PREVIOUS_POS_RIGHT, Integer.valueOf(cursor.getInt(8)));
                            this.mDbDic.insert(TABLE_NAME_DIC, null, content);
                            if (isConnectLearn && previousWord.stroke.equals(cursor.getString(5)) && previousWord.candidate.equals(cursor.getString(6))) {
                                isExistConnectLearnData = true;
                            }
                        } catch (SQLException e) {
                            this.mDbDic.endTransaction();
                            cursor.close();
                            return -1;
                        } catch (Throwable th) {
                            this.mDbDic.endTransaction();
                            cursor.close();
                            throw th;
                        }
                    } while (cursor.moveToNext());
                    while (true) {
                        idIndex--;
                        if (idIndex < 0) {
                            break;
                        }
                        this.mDbDic.delete(TABLE_NAME_DIC, String.format("%s=%d", COLUMN_NAME_ID, Integer.valueOf(idArray[idIndex])), null);
                    }
                    this.mDbDic.setTransactionSuccessful();
                    if (!isConnectLearn || isExistConnectLearnData) {
                        this.mDbDic.endTransaction();
                        cursor.close();
                        return 0;
                    }
                }
                cursor.close();
                SQLiteCursor cursor2 = (SQLiteCursor) this.mDbDic.query(TABLE_NAME_DIC, new String[]{"stroke", COLUMN_NAME_CANDIDATE}, String.format("%s=%d", "type", 1), null, null, null, String.format("%s ASC", COLUMN_NAME_ID));
                if (cursor2.getCount() >= 2000) {
                    this.mDbDic.beginTransaction();
                    try {
                        cursor2.moveToFirst();
                        StringBuilder oldestStrokeSQL = new StringBuilder();
                        StringBuilder oldestCandidateSQL = new StringBuilder();
                        DatabaseUtils.appendEscapedSQLString(oldestStrokeSQL, cursor2.getString(0));
                        DatabaseUtils.appendEscapedSQLString(oldestCandidateSQL, cursor2.getString(1));
                        this.mDbDic.delete(TABLE_NAME_DIC, String.format("%s=%d and %s=%s and %s=%s", "type", 1, "stroke", oldestStrokeSQL.toString(), COLUMN_NAME_CANDIDATE, oldestCandidateSQL.toString()), null);
                        this.mDbDic.setTransactionSuccessful();
                        this.mDbDic.endTransaction();
                    } catch (SQLException e2) {
                        this.mDbDic.endTransaction();
                        cursor2.close();
                        return -1;
                    } catch (Throwable th2) {
                        this.mDbDic.endTransaction();
                        cursor2.close();
                        throw th2;
                    }
                }
                cursor2.close();
                ContentValues content2 = new ContentValues();
                content2.clear();
                content2.put("type", (Integer) 1);
                content2.put("stroke", word.stroke);
                content2.put(COLUMN_NAME_CANDIDATE, word.candidate);
                content2.put(COLUMN_NAME_POS_LEFT, Integer.valueOf(word.partOfSpeech.left));
                content2.put(COLUMN_NAME_POS_RIGHT, Integer.valueOf(word.partOfSpeech.right));
                if (previousWord != null) {
                    content2.put(COLUMN_NAME_PREVIOUS_STROKE, previousWord.stroke);
                    content2.put(COLUMN_NAME_PREVIOUS_CANDIDATE, previousWord.candidate);
                    content2.put(COLUMN_NAME_PREVIOUS_POS_LEFT, Integer.valueOf(previousWord.partOfSpeech.left));
                    content2.put(COLUMN_NAME_PREVIOUS_POS_RIGHT, Integer.valueOf(previousWord.partOfSpeech.right));
                }
                this.mDbDic.beginTransaction();
                try {
                    this.mDbDic.insert(TABLE_NAME_DIC, null, content2);
                    this.mDbDic.setTransactionSuccessful();
                } catch (SQLException e3) {
                    this.mDbDic.endTransaction();
                    return -1;
                } finally {
                    this.mDbDic.endTransaction();
                }
            }
        }
        return 0;
    }
}
