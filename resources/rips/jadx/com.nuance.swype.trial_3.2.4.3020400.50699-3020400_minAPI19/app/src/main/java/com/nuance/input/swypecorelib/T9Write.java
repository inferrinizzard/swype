package com.nuance.input.swypecorelib;

import android.graphics.Point;
import com.nuance.input.swypecorelib.T9WriteRecognizerListener;
import com.nuance.input.swypecorelib.WriteThreadQueue;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/* loaded from: classes.dex */
public abstract class T9Write implements WriteThreadQueue.OnWriteThreadQueueConsumer {
    public static final int CHINESE_HONGKONG_LANGUAGEID = 226;
    public static final int CHINESE_TRAD_LANGUAGEID = 224;
    protected int mLanguageID;
    protected final Object mLock = new Object();
    protected final List<T9WriteRecognizerListener.OnWriteRecognizerListener> mRecognizerListeners = new LinkedList();
    protected final WriteThreadQueue mWriteThreadQueue = new WriteThreadQueue(this);
    protected long nativeContext;
    protected final T9WriteSetting settings;

    protected abstract long create_native_context(String str);

    protected abstract void destroy_native_context(long j);

    public abstract void finishSession();

    public abstract String getDatabaseVersion();

    public abstract String getVersion();

    public abstract void startSession(int i);

    /* JADX INFO: Access modifiers changed from: protected */
    public T9Write(T9WriteSetting settings) {
        this.settings = settings;
    }

    public boolean hasSession() {
        return this.nativeContext != 0;
    }

    public void createSession(String databaseConfigFile, String[] externalDatabasePath) {
        synchronized (this.mLock) {
            if (this.nativeContext == 0) {
                this.nativeContext = create_native_context(databaseConfigFile);
                this.mWriteThreadQueue.start();
            }
            XT9CoreInput.setExternalDatabasePath(externalDatabasePath);
        }
    }

    public void destroySession() {
        synchronized (this.mLock) {
            if (this.nativeContext != 0) {
                destroy_native_context(this.nativeContext);
                this.nativeContext = 0L;
                this.mWriteThreadQueue.stop();
                this.mRecognizerListeners.clear();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void notifyWriteEventListeners(T9WriteRecognizerListener.WriteEvent event) {
        Iterator<T9WriteRecognizerListener.OnWriteRecognizerListener> it = this.mRecognizerListeners.iterator();
        while (it.hasNext()) {
            it.next().onHandleWriteEvent(event);
        }
    }

    public void addRecognizeListener(T9WriteRecognizerListener.OnWriteRecognizerListener listener) {
        if (!this.mRecognizerListeners.contains(listener)) {
            this.mRecognizerListeners.add(listener);
        }
    }

    public void removeRecognizeListener(T9WriteRecognizerListener.OnWriteRecognizerListener listener) {
        if (this.mRecognizerListeners.contains(listener)) {
            this.mRecognizerListeners.remove(listener);
        }
    }

    public void removeAllRecognizeListener() {
        if (this.mRecognizerListeners.size() > 0) {
            this.mRecognizerListeners.clear();
        }
    }

    public boolean isRecognizeListenerEmpty() {
        return this.mRecognizerListeners != null && this.mRecognizerListeners.isEmpty();
    }

    public void addTextCategory() {
        this.settings.addTextCategory();
    }

    public void addGestureCategory() {
        this.settings.addGestureCategory();
    }

    public void setRecognizerDelay(int delayMS) {
        this.settings.setRecognizerDelay(delayMS);
    }

    public int getRecognizerDelay() {
        return this.settings.getRecognizerDelay();
    }

    public void addNumberOnlyCategory() {
        this.settings.addNumberOnlyCategory();
    }

    public void addOnlyTextCategory() {
        this.settings.addOnlyTextCategory();
    }

    public void addOnlyLatinLetterCategory() {
        this.settings.addOnlyLatinLetterCategory();
    }

    public void addPunctuationCategory() {
        this.settings.addPunctuationCategory();
    }

    public void addLatinLetterCategory() {
        this.settings.addLatinLetterCategory();
    }

    public void addSymbolCategory() {
        this.settings.addSymbolCategory();
    }

    public void addNumberCategory() {
        this.settings.addNumberCategory();
    }

    public void addEmailOnlyCategory() {
        this.settings.addEmailOnlyCategory();
    }

    public void addUrlOnlyCategory() {
        this.settings.addUrlOnlyCategory();
    }

    public void clearCategory() {
        this.settings.clearCategory();
    }

    public final void setWidth(int w) {
        this.settings.setWidth(w);
    }

    public final void setHeight(int h) {
        this.settings.setHeight(h);
    }

    public int getWidth() {
        return this.settings.getWidth();
    }

    public int getHeight() {
        return this.settings.getHeight();
    }

    public final void setInputGuide(int inputGuide) {
        this.settings.setInputGuide(inputGuide);
    }

    public final void setSupportLineSet(int supportLineSet) {
        this.settings.setSupportLineSet(supportLineSet);
    }

    public final void setBaseline(int b) {
        this.settings.setBaseline(b);
    }

    public final void setHelpline(int h) {
        this.settings.setHelpline(h);
    }

    public final void setTopline(int t) {
        this.settings.setTopline(t);
    }

    public final int getInputGuide() {
        return this.settings.getInputGuide();
    }

    public int getRecognitionMode() {
        return this.settings.getRecognitionMode();
    }

    public void setRecognitionMode(int mode) {
        this.settings.setRecognitionMode(mode);
    }

    public void setWritingDirection(int direction) {
        this.settings.setWritingDirection(direction);
    }

    public void addPhoneNumberOnlyCategory() {
        this.settings.addPhoneNumberOnlyCategory();
    }

    public void addDigitsAndSymbolsOnlyCategory() {
        this.settings.addDigitsAndSymbolsOnlyCategory();
    }

    public T9WriteSetting getSettings() {
        return this.settings;
    }

    public void applyChangedSettings() {
        synchronized (this.mLock) {
            if (hasSession()) {
                this.mWriteThreadQueue.addSettings(this.settings);
            }
        }
    }

    public void startArcsAddingSequence() {
        synchronized (this.mLock) {
            if (hasSession()) {
                this.mWriteThreadQueue.startArcSequence();
            }
        }
    }

    public void endArcsAddingSequence() {
        synchronized (this.mLock) {
            if (hasSession()) {
                this.mWriteThreadQueue.endArcSequence();
            }
        }
    }

    public void queueAddArcs(List<Point> arc1, List<Point> arc2, CharSequence startWord) {
        synchronized (this.mLock) {
            if (hasSession()) {
                if (arc1 != null && arc2 != null) {
                    this.mWriteThreadQueue.addArcs(new ArrayList(arc1), new ArrayList(arc2), startWord);
                } else if (arc1 != null) {
                    this.mWriteThreadQueue.addArcs(new ArrayList(arc1), null, startWord);
                } else if (arc2 != null) {
                    this.mWriteThreadQueue.addArcs(null, new ArrayList(arc2), startWord);
                }
            }
        }
    }

    public void queueRecognition(CharSequence startWord) {
        synchronized (this.mLock) {
            if (hasSession()) {
                this.mWriteThreadQueue.addRecognize(startWord);
            }
        }
    }

    public void queueText(CharSequence text) {
        synchronized (this.mLock) {
            if (hasSession()) {
                this.mWriteThreadQueue.addText(text);
            }
        }
    }

    public void queueChar(char ch) {
        synchronized (this.mLock) {
            if (hasSession()) {
                this.mWriteThreadQueue.addChar(ch);
            }
        }
    }

    public void queueKey(int key) {
        synchronized (this.mLock) {
            if (hasSession()) {
                this.mWriteThreadQueue.addKey(key);
            }
        }
    }
}
