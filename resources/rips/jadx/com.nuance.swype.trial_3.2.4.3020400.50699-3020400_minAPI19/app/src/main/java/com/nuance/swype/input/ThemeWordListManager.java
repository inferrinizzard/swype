package com.nuance.swype.input;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.text.TextUtils;
import com.nuance.swype.input.udb.NewWordsBucketFactory;
import com.nuance.swype.plugin.ThemeApkInfo;
import com.nuance.swype.plugin.ThemeMetaData;
import com.nuance.swype.util.LogManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/* loaded from: classes.dex */
public class ThemeWordListManager {
    private static final String ID_SEPARATOR = "#";
    static final String REMAINING_COUNT_KEY_PREFIX = "remaining_count_for";
    static final String SHARED_PREFERNCE_FILE_NAME = "word_list_state_preference_file";
    static final String WORD_LIST_PENDING_KEY_PREFIX = "word_list_pending_for";
    protected static final LogManager.Log log = LogManager.getLog("ThemeWordListManager");
    private static ThemeWordListManager sInstance;
    private Context mContext;
    SharedPreferences mPreferences;
    List<NewWordsBucketFactory.NewWordsBucket> mBucketList = Collections.synchronizedList(new ArrayList());
    List<PendingWordList> mPendingList = Collections.synchronizedList(new ArrayList());

    public static ThemeWordListManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new ThemeWordListManager(context);
        }
        return sInstance;
    }

    protected ThemeWordListManager(Context context) {
        this.mContext = context;
        this.mPreferences = this.mContext.getSharedPreferences(SHARED_PREFERNCE_FILE_NAME, 0);
    }

    public List<NewWordsBucketFactory.NewWordsBucket> createWordListBucket(String sku, String themeApkPath) throws IOException {
        List<NewWordsBucketFactory.NewWordsBucket> wordLists = new ArrayList<>();
        ThemeApkInfo themeApkInfo = ThemeApkInfo.fromStaticApkFile(this.mContext, themeApkPath);
        if (themeApkInfo == null) {
            log.d("createWordListBucket... themeApkInfo is null.");
        } else {
            List<ThemeMetaData.WordListMetadata> list = themeApkInfo.themeMetaData.wordListMetadataList;
            AssetManager am = themeApkInfo.apkResources.getAssets();
            IMEApplication imeApp = IMEApplication.from(this.mContext);
            log.d("number of word lists: ", Integer.valueOf(list.size()));
            for (ThemeMetaData.WordListMetadata wordListMetadata : list) {
                log.d(String.format("type: %s, version: %s, file: %s", wordListMetadata.type, wordListMetadata.version, wordListMetadata.fileName));
                LinkedHashSet<String> lines = readWordsFromFile(am, wordListMetadata.fileName);
                if (!lines.iterator().hasNext()) {
                    log.d("word list ID not found");
                } else {
                    String firstLine = lines.iterator().next();
                    log.d("ID line: ", firstLine);
                    String[] items = firstLine.split(ID_SEPARATOR);
                    if (items.length > 1) {
                        String wordListId = items[1];
                        log.d("Word list id is: ", wordListId);
                        if (!PendingWordList.isWordListAddedToQueue(wordListId, this.mContext)) {
                            lines.remove(firstLine);
                            Set<String> wordList = new HashSet<>(lines);
                            NewWordsBucketFactory.NewWordsBucket bucket = imeApp.getNewWordsBucketFactory().createNewWordsListBucket(wordListMetadata.type.equalsIgnoreCase("phrases"));
                            bucket.add(wordList);
                            log.d("Word Bucket created for : ", sku);
                            PendingWordList pendingWordList = new PendingWordList(bucket, wordListId, this.mContext);
                            NewWordsBucketFactory.NewWordsBucket filteredBucket = pendingWordList.getBucket();
                            this.mBucketList.add(filteredBucket);
                            this.mPendingList.add(pendingWordList);
                            wordLists.add(filteredBucket);
                        } else {
                            log.d("Word list already being processed. id: ", wordListId);
                        }
                    } else {
                        log.d("Word list file is empty");
                    }
                }
            }
        }
        return wordLists;
    }

    public List<NewWordsBucketFactory.NewWordsBucket> getBucketList() {
        return this.mBucketList;
    }

    public void pauseAddingWordList() {
        Iterator<PendingWordList> it = this.mPendingList.iterator();
        while (it.hasNext()) {
            it.next().pause();
        }
    }

    public void updateStateForBucket(NewWordsBucketFactory.NewWordsBucket bucket) {
        log.d("Updating state for bucket");
        for (PendingWordList pwl : this.mPendingList) {
            if (pwl.getBucket() == bucket) {
                log.d("Updating state");
                pwl.updateState();
            }
        }
    }

    public void resumeAddingWordList() {
    }

    public LinkedHashSet<String> readWordsFromFile(AssetManager am, String fileName) throws IOException {
        IMEApplication imeApp = IMEApplication.from(this.mContext);
        LinkedHashSet<String> lines = new LinkedHashSet<>();
        if (imeApp != null) {
            BufferedReader br = null;
            try {
                InputStream file = am.open(fileName);
                BufferedReader br2 = new BufferedReader(new InputStreamReader(file, "UTF-8"));
                try {
                    log.d("Reading file data");
                    while (true) {
                        String line = br2.readLine();
                        if (line == null) {
                            break;
                        }
                        String[] lineArray = line.split(":");
                        for (int j = 0; j < lineArray.length; j++) {
                            if (!TextUtils.isEmpty(lineArray[j])) {
                                lines.add(lineArray[j]);
                            }
                        }
                    }
                    br2.close();
                    log.d("data read from file:", lines.toString());
                } catch (Throwable th) {
                    th = th;
                    br = br2;
                    if (br != null) {
                        br.close();
                    }
                    throw th;
                }
            } catch (Throwable th2) {
                th = th2;
            }
        }
        return lines;
    }

    /* loaded from: classes.dex */
    public static class PendingWordList {
        private NewWordsBucketFactory.NewWordsBucket mBucket;
        SharedPreferences mPreferences;
        private boolean mProcessingStarted;
        String mProcessingStartedKey;
        private int mRemaining;
        String mRemainingCountKey;
        private String mWordListId;

        public PendingWordList(NewWordsBucketFactory.NewWordsBucket bucket, String wordListId, Context context) {
            this.mBucket = bucket;
            this.mWordListId = wordListId;
            this.mPreferences = context.getSharedPreferences(ThemeWordListManager.SHARED_PREFERNCE_FILE_NAME, 0);
            this.mProcessingStartedKey = getProcessingStartedKey(this.mWordListId);
            this.mRemainingCountKey = getRemainingCountKey(this.mWordListId);
            ThemeWordListManager.log.d("Processing started key is: ", this.mProcessingStartedKey);
            ThemeWordListManager.log.d("Remaining key is: ", this.mRemainingCountKey);
            readState();
            if (hasProcessingStarted()) {
                syncBucket();
                return;
            }
            ThemeWordListManager.log.d("No sync required");
            setRemaining(this.mBucket.size());
            startProcessing();
        }

        private static String getRemainingCountKey(String wordListId) {
            return String.format("%s_%s", ThemeWordListManager.REMAINING_COUNT_KEY_PREFIX, wordListId);
        }

        private static String getProcessingStartedKey(String wordListId) {
            return String.format("%s_%s", ThemeWordListManager.WORD_LIST_PENDING_KEY_PREFIX, wordListId);
        }

        public static boolean isWordListAddedToQueue(String wordListId, Context context) {
            String key = getProcessingStartedKey(wordListId);
            return context.getSharedPreferences(ThemeWordListManager.SHARED_PREFERNCE_FILE_NAME, 0).contains(key);
        }

        private void startProcessing() {
            ThemeWordListManager.log.d("Starting processing");
            this.mProcessingStarted = true;
            writeState();
        }

        public void pause() {
            setRemaining(this.mBucket.size());
        }

        private void syncBucket() {
            ThemeWordListManager.log.d("Syncing the bucket. Remaining is: ", Integer.valueOf(this.mRemaining), ", bucket size is: ", Integer.valueOf(this.mBucket.size()));
            if (this.mRemaining > 0) {
                while (this.mBucket.size() > this.mRemaining) {
                    String removedWord = this.mBucket.remove();
                    ThemeWordListManager.log.d("Removing ", removedWord);
                }
            }
        }

        private void setRemaining(int size) {
            this.mRemaining = size;
            writeState();
        }

        public boolean hasProcessingStarted() {
            return this.mProcessingStarted;
        }

        private void readState() {
            this.mProcessingStarted = this.mPreferences.getBoolean(this.mProcessingStartedKey, false);
            this.mRemaining = this.mPreferences.getInt(this.mRemainingCountKey, 0);
            ThemeWordListManager.log.d("State is: ProcessingStarted: ", Boolean.valueOf(this.mProcessingStarted), ", Remaining: ", Integer.valueOf(this.mRemaining));
        }

        private void writeState() {
            SharedPreferences.Editor editor = this.mPreferences.edit();
            if (this.mRemaining > 0) {
                ThemeWordListManager.log.d("Updating state info");
                editor.putBoolean(this.mProcessingStartedKey, this.mProcessingStarted);
                editor.putInt(this.mRemainingCountKey, this.mRemaining);
            } else {
                ThemeWordListManager.log.d("Removing state info");
                editor.remove(this.mProcessingStartedKey);
                editor.remove(this.mRemainingCountKey);
            }
            editor.apply();
            ThemeWordListManager.log.d("State updated: ProcessingStarted: ", Boolean.valueOf(this.mProcessingStarted), ", Remaining: ", Integer.valueOf(this.mRemaining));
        }

        public void updateState() {
            setRemaining(this.mBucket.size());
        }

        public NewWordsBucketFactory.NewWordsBucket getBucket() {
            return this.mBucket;
        }
    }

    /* loaded from: classes.dex */
    public static class WordListAdderAsyncTask extends AsyncTask<Void, Void, List<NewWordsBucketFactory.NewWordsBucket>> {
        String mApkFilePath;
        Context mContext;
        String mSku;

        public WordListAdderAsyncTask(Context context, String sku, String apkFilePath) {
            this.mContext = context;
            this.mSku = sku;
            this.mApkFilePath = apkFilePath;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public List<NewWordsBucketFactory.NewWordsBucket> doInBackground(Void... params) {
            ThemeWordListManager listManager = ThemeWordListManager.getInstance(this.mContext);
            try {
                List<NewWordsBucketFactory.NewWordsBucket> buckets = listManager.createWordListBucket(this.mSku, this.mApkFilePath);
                return buckets;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(List<NewWordsBucketFactory.NewWordsBucket> buckets) {
            IMEApplication imeApp = IMEApplication.from(this.mContext);
            if (imeApp != null) {
                for (NewWordsBucketFactory.NewWordsBucket bucket : buckets) {
                    imeApp.getNewWordsBucketFactory().addWordListBucket(bucket);
                }
            }
        }
    }
}
