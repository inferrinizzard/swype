package com.nuance.swype.input.udb;

import com.nuance.input.swypecorelib.XT9CoreAlphaInput;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

/* loaded from: classes.dex */
public class NewWordsBucketFactory {
    private List<NewWordsBucket> additionalBuckets = Collections.synchronizedList(new ArrayList());
    private NewWordsBucket androidWordsBucket;
    private NewWordsBucket mlsThemeBucket;
    private NewWordsBucket smsCalllogContactsBucket;
    private NewWordsBucket smsWordsBucket;
    private NewWordsBucket twitterWordsBucket;

    public NewWordsBucket getTwitterWordsBucketInstance() {
        boolean z = false;
        int i = 1;
        if (this.twitterWordsBucket == null) {
            this.twitterWordsBucket = new NewWordsBucket(i, i, i, z, z);
        }
        return this.twitterWordsBucket;
    }

    public NewWordsBucket getSmsWordsBucketInstance() {
        int i = 1;
        boolean z = true;
        boolean z2 = true;
        boolean z3 = true;
        if (this.smsWordsBucket == null) {
            this.smsWordsBucket = new NewWordsBucket(i, z3 ? 1 : 0, z2 ? 1 : 0, z ? 1 : 0, false);
        }
        return this.smsWordsBucket;
    }

    public NewWordsBucket getMlsThemeWordsBucketInstance() {
        int i = 1;
        boolean z = true;
        boolean z2 = true;
        boolean z3 = true;
        if (this.mlsThemeBucket == null) {
            this.mlsThemeBucket = new NewWordsBucket(i, z3 ? 1 : 0, z2 ? 1 : 0, false, z ? 1 : 0);
            this.mlsThemeBucket.setBigramDlm(true);
        }
        return this.mlsThemeBucket;
    }

    public NewWordsBucket getAndroidNewWordsBucketInstance() {
        int i = 1;
        boolean z = true;
        boolean z2 = true;
        boolean z3 = true;
        if (this.androidWordsBucket == null) {
            this.androidWordsBucket = new NewWordsBucket(i, z3 ? 1 : 0, z2 ? 1 : 0, false, z ? 1 : 0);
        }
        return this.androidWordsBucket;
    }

    public NewWordsBucket getSmsCallLogContactsBucketInstance() {
        int i = 1;
        boolean z = true;
        boolean z2 = true;
        boolean z3 = true;
        if (this.smsCalllogContactsBucket == null) {
            this.smsCalllogContactsBucket = new NewWordsBucket(i, z3 ? 1 : 0, z2 ? 1 : 0, false, z ? 1 : 0);
        }
        return this.smsCalllogContactsBucket;
    }

    public NewWordsBucket[] getNewWordBuckets() {
        NewWordsBucket[] buckets = {getSmsWordsBucketInstance(), getAndroidNewWordsBucketInstance(), getTwitterWordsBucketInstance(), getSmsCallLogContactsBucketInstance(), getMlsThemeWordsBucketInstance()};
        List<NewWordsBucket> bucketList = new ArrayList<>();
        bucketList.addAll(Arrays.asList(buckets));
        if (this.additionalBuckets != null) {
            bucketList.addAll(this.additionalBuckets);
        }
        return (NewWordsBucket[]) bucketList.toArray(buckets);
    }

    public NewWordsBucket createNewWordsListBucket(boolean z) {
        NewWordsBucket newWordsBucket = new NewWordsBucket(1, true ? 1 : 0, true ? 1 : 0, z, true ? 1 : 0);
        newWordsBucket.setBigramDlm(true);
        return newWordsBucket;
    }

    public void addWordListBucket(NewWordsBucket bucket) {
        this.additionalBuckets.add(bucket);
    }

    /* loaded from: classes.dex */
    public static class NewWordsBucket {
        private Queue<String> buckets;
        private boolean isBigramDlm;
        public final boolean isHighQualityWord;
        public final int scanActionCount;
        public final boolean sentenceBasedLearning;
        public final int userExplicitActionCount;
        public final int userImplicitActionCount;

        private NewWordsBucket(int userImplicitActionCount, int userExplicitActionCount, int scanActionCount, boolean sentenceBasedLearning, boolean isHighQualityWord) {
            this.buckets = new ConcurrentLinkedQueue();
            this.userImplicitActionCount = userImplicitActionCount;
            this.userExplicitActionCount = userExplicitActionCount;
            this.scanActionCount = scanActionCount;
            this.sentenceBasedLearning = sentenceBasedLearning;
            this.isHighQualityWord = isHighQualityWord;
        }

        public void add(String word) {
            if (word != null) {
                this.buckets.add(word);
            }
        }

        public void add(Set<String> text) {
            if (text != null) {
                this.buckets.addAll(text);
            }
        }

        public String remove() {
            return this.buckets.poll();
        }

        public boolean isEmpty() {
            return this.buckets.isEmpty();
        }

        public int size() {
            return this.buckets.size();
        }

        public void setScanContext(XT9CoreAlphaInput alphaInput) {
            alphaInput.setWordQuarantineLevel(this.userImplicitActionCount, this.userImplicitActionCount, this.scanActionCount);
            alphaInput.clearApplicationPredictionContext();
        }

        public void setBigramDlm(boolean bigram) {
            this.isBigramDlm = bigram;
        }

        public boolean isBigramDlm() {
            return this.isBigramDlm;
        }
    }
}
