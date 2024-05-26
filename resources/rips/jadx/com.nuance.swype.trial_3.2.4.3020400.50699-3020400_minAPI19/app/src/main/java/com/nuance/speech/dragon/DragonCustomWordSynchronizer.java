package com.nuance.speech.dragon;

import android.os.AsyncTask;
import android.os.Handler;
import com.nuance.nmdp.speechkit.CustomWordsSynchronizeResult;
import com.nuance.nmdp.speechkit.CustomWordsSynchronizer;
import com.nuance.nmdp.speechkit.RecognizerConstants;
import com.nuance.nmdp.speechkit.SpeechError;
import com.nuance.speech.CustomWordSynchronizer;
import com.nuance.swype.input.AppPreferences;
import com.nuance.swype.input.udb.UserDictionaryIterator;
import java.util.HashSet;
import java.util.Set;

/* loaded from: classes.dex */
public final class DragonCustomWordSynchronizer extends CustomWordSynchronizer {
    private ResyncUdbTask mCurrentResyncUdbTask;
    private CustomWordsSynchronizer mCustomWordsSynchronizer;
    private SpeechKitWrapper mSpeechKitWrapper;
    private final CustomWordsSynchronizer.Listener synchronizerListener = new CustomWordsSynchronizer.Listener() { // from class: com.nuance.speech.dragon.DragonCustomWordSynchronizer.1
        @Override // com.nuance.nmdp.speechkit.CustomWordsSynchronizer.Listener
        public void onResults(CustomWordsSynchronizer synchronizer, int actionType, CustomWordsSynchronizeResult result) {
            if (result.Status) {
                DragonCustomWordSynchronizer.this.setCustomWordsSynchronizationForceResync(false);
                DragonCustomWordSynchronizer.this.setCustomWordsSynchronizationServerWordsCount(result.FinalCount);
            }
        }

        @Override // com.nuance.nmdp.speechkit.CustomWordsSynchronizer.Listener
        public void onError(CustomWordsSynchronizer synchronizer, int actionType, SpeechError error) {
            if (error.getErrorCode() == 7) {
                DragonCustomWordSynchronizer.this.setCustomWordsSynchronizationForceResync(true);
                DragonCustomWordSynchronizer.this.setCustomWordsSynchronizationServerWordsCount(-1);
            }
        }
    };
    private final UserDictionaryIterator udbIterator;

    public DragonCustomWordSynchronizer(SpeechKitWrapper speechKitWrapper, UserDictionaryIterator udbIterator) {
        this.mSpeechKitWrapper = speechKitWrapper;
        this.udbIterator = udbIterator;
    }

    private String recognitionType2Str(int recognizerType) {
        return recognizerType == 0 ? RecognizerConstants.RecognizerType.Dictation : RecognizerConstants.RecognizerType.Search;
    }

    @Override // com.nuance.speech.CustomWordSynchronizer
    public final void addCustomWord(int recognizerType, String word) {
        boolean bForceReSync = isForceResyncNeeded();
        if (bForceReSync) {
            resyncAllUserWords(recognizerType);
        } else if (word != null && word.length() != 0) {
            Set<String> wordsSet = new HashSet<>();
            wordsSet.add(word);
            addCustomWordsSet(recognizerType, wordsSet, bForceReSync);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void addCustomWordsSet(int recognizerType, Set<String> words, boolean bForceResync) {
        if (words != null && !words.isEmpty() && isSettingsEnabled() && isCurrentLanguageSupported()) {
            getSynchronizer(recognizerType).addCustomWordsSet(words, bForceResync);
        }
    }

    @Override // com.nuance.speech.CustomWordSynchronizer
    public final void resyncAllUserWords(int recognizerType) {
        setCustomWordsSynchronizationForceResync(true);
        if (this.mCurrentResyncUdbTask != null) {
            this.mCurrentResyncUdbTask.cancel(true);
            this.mCurrentResyncUdbTask = null;
        }
        this.mCurrentResyncUdbTask = new ResyncUdbTask();
        this.mCurrentResyncUdbTask.execute(Integer.valueOf(recognizerType));
    }

    @Override // com.nuance.speech.CustomWordSynchronizer
    public final void removeCustomWord(int recognizerType, String word) {
        if (isSettingsEnabled() && isCurrentLanguageSupported() && word != null && word.length() != 0) {
            CustomWordsSynchronizer synchronizer = getSynchronizer(recognizerType);
            Set<String> wordsSet = new HashSet<>();
            wordsSet.add(word);
            synchronizer.removeCustomWordsSet(wordsSet);
        }
    }

    @Override // com.nuance.speech.CustomWordSynchronizer
    public final void clearAllCustomWords() {
        if (isSettingsEnabled() && isCurrentLanguageSupported()) {
            getDefaultCustomWordSyncer().clearAllCustomWords();
        }
    }

    @Override // com.nuance.speech.CustomWordSynchronizer
    public final void removeCustomWords(Set<String> words) {
        if (isSettingsEnabled() && isCurrentLanguageSupported()) {
            getDefaultCustomWordSyncer().removeCustomWordsSet(new HashSet(words));
        }
    }

    public final void deleteAllUserInformation() {
        if (isSettingsEnabled() && isCurrentLanguageSupported()) {
            getDefaultCustomWordSyncer().deleteAllUserInformation();
        }
    }

    public final void cancel() {
        if (this.mSpeechKitWrapper.isCustomWordsSynchronizationSupported()) {
            if (this.mCurrentResyncUdbTask != null) {
                this.mCurrentResyncUdbTask.cancel(true);
                this.mCurrentResyncUdbTask = null;
            }
            getDefaultCustomWordSyncer().cancel();
        }
    }

    public final void releaseCustomWordsSynchronizerInstance() {
        if (this.mCustomWordsSynchronizer != null) {
            this.mCustomWordsSynchronizer.cancel();
            this.mCustomWordsSynchronizer = null;
        }
    }

    private CustomWordsSynchronizer getDefaultCustomWordSyncer() {
        return getSynchronizer(0);
    }

    private boolean isForceResyncNeeded() {
        return AppPreferences.from(this.mSpeechKitWrapper.getContext()).getCustomWordsSynchronizationForceResync();
    }

    private boolean isCurrentLanguageSupported() {
        return this.mSpeechKitWrapper.isCustomWordsSynchronizationSupported();
    }

    private boolean isSettingsEnabled() {
        return AppPreferences.from(this.mSpeechKitWrapper.getContext()).getCustomWordsSynchronizationAvailability();
    }

    private CustomWordsSynchronizer getSynchronizer(int recognizerType) {
        if (this.mCustomWordsSynchronizer == null) {
            this.mCustomWordsSynchronizer = this.mSpeechKitWrapper.createCustomWordsSynchronizer(recognitionType2Str(recognizerType), this.synchronizerListener, new Handler());
        }
        return this.mCustomWordsSynchronizer;
    }

    protected final void setCustomWordsSynchronizationForceResync(boolean resync) {
        AppPreferences.from(this.mSpeechKitWrapper.getContext()).setCustomWordsSynchronizationForceResync(resync);
    }

    protected final void setCustomWordsSynchronizationServerWordsCount(int count) {
        AppPreferences.from(this.mSpeechKitWrapper.getContext()).setCustomWordsSynchronizationServerWordsCount(count);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class ResyncUdbTask extends AsyncTask<Integer, Void, Set<String>> {
        private int mRecognizerType;

        private ResyncUdbTask() {
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Set<String> doInBackground(Integer... recognizerType) {
            this.mRecognizerType = recognizerType[0].intValue();
            Set<String> wordsSet = new HashSet<>();
            StringBuilder sb = new StringBuilder();
            boolean cancelled = false;
            while (true) {
                if (!DragonCustomWordSynchronizer.this.udbIterator.getNext(sb)) {
                    break;
                }
                if (isCancelled()) {
                    cancelled = true;
                    break;
                }
                wordsSet.add(sb.toString());
            }
            if (cancelled) {
                return null;
            }
            return wordsSet;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Set<String> resultSet) {
            DragonCustomWordSynchronizer.this.addCustomWordsSet(this.mRecognizerType, resultSet, true);
        }
    }
}
