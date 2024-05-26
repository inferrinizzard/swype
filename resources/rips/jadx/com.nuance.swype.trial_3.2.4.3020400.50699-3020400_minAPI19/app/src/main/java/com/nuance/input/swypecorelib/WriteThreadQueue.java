package com.nuance.input.swypecorelib;

import android.graphics.Point;
import android.util.Log;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/* loaded from: classes.dex */
class WriteThreadQueue {
    private static final String TAG = "WriteThreadQueue";
    private final BlockingQueue<QueueItem> queue = new LinkedBlockingQueue();
    private Thread thread;
    private final OnWriteThreadQueueConsumer writeThreadQueueConsumer;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public interface OnWriteThreadQueueConsumer {
        void consume(QueueItem queueItem);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public WriteThreadQueue(OnWriteThreadQueueConsumer consumer) {
        this.writeThreadQueueConsumer = consumer;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void start() {
        if (this.thread == null) {
            this.thread = new Thread(new QueueConsumer(this.queue, this.writeThreadQueueConsumer));
            this.thread.start();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void stop() {
        if (this.thread != null) {
            this.queue.clear();
            try {
                this.queue.put(new QueueItem(0, null));
            } catch (InterruptedException e) {
                Log.e(TAG, e.toString());
            }
            this.thread = null;
        }
    }

    synchronized boolean isStarted() {
        return this.thread != null;
    }

    void put(QueueItem item) {
        if (isStarted()) {
            try {
                this.queue.put(item);
            } catch (InterruptedException e) {
                Log.e(TAG, e.toString());
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void clearQueue() {
        this.queue.clear();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void addText(CharSequence text) {
        put(new TextQueueItem(text));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void addChar(char ch) {
        put(new CharQueueItem(ch));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void addKey(int key) {
        put(new KeyQueueItem(key));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void addArcs(List<Point> arc1, List<Point> arc2, CharSequence startWord) {
        put(new ArcQueueItem(arc1, arc2, startWord));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void addRecognize(CharSequence startWord) {
        put(new RecognizeQueueItem(startWord));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void addSettings(T9WriteSetting settings) {
        put(new ChangeSettings(settings));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void startArcSequence() {
        put(new StartArcSequence());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void endArcSequence() {
        put(new EndArcSequence());
    }

    /* loaded from: classes.dex */
    private static class QueueConsumer implements Runnable {
        private final BlockingQueue<QueueItem> queue;
        private final OnWriteThreadQueueConsumer writeThreadQueueConsumer;

        public QueueConsumer(BlockingQueue<QueueItem> queue, OnWriteThreadQueueConsumer consumer) {
            this.writeThreadQueueConsumer = consumer;
            this.queue = queue;
        }

        @Override // java.lang.Runnable
        public void run() {
            while (true) {
                try {
                    QueueItem item = this.queue.take();
                    if (item.mType == 0) {
                        this.queue.clear();
                        return;
                    }
                    consume(item);
                } catch (InterruptedException e) {
                    Log.e(WriteThreadQueue.TAG, e.toString());
                    return;
                }
            }
        }

        private void consume(QueueItem item) {
            if (this.writeThreadQueueConsumer != null) {
                this.writeThreadQueueConsumer.consume(item);
            }
        }
    }

    /* loaded from: classes.dex */
    public static class QueueItem {
        public static final int ITEM_TYPE_ARC = 1;
        public static final int ITEM_TYPE_CHAR = 3;
        public static final int ITEM_TYPE_END_ARC_SEQUENCE = 7;
        public static final int ITEM_TYPE_EXIT = 0;
        public static final int ITEM_TYPE_KEY = 8;
        public static final int ITEM_TYPE_RECOGNIZE = 2;
        public static final int ITEM_TYPE_SETTINGS = 5;
        public static final int ITEM_TYPE_SPEECH_RESULT = 9;
        public static final int ITEM_TYPE_START_ARC_SEQUENCE = 6;
        public static final int ITEM_TYPE_TEXT = 4;
        public final Object mData;
        public final int mType;

        public QueueItem(int type, Object data) {
            this.mType = type;
            this.mData = data;
        }
    }

    /* loaded from: classes.dex */
    public static class ArcQueueItem extends QueueItem {
        public final List<Point> mArc1;
        public final List<Point> mArc2;
        public final CharSequence mStartWord;

        public ArcQueueItem(List<Point> arc1, List<Point> arc2, CharSequence startWord) {
            super(1, null);
            this.mArc1 = arc1;
            this.mArc2 = arc2;
            this.mStartWord = startWord;
        }
    }

    /* loaded from: classes.dex */
    public static class RecognizeQueueItem extends QueueItem {
        public final CharSequence mStartWord;

        public RecognizeQueueItem(CharSequence startWord) {
            super(2, null);
            this.mStartWord = startWord;
        }
    }

    /* loaded from: classes.dex */
    public static class CharQueueItem extends QueueItem {
        public final char mChar;

        public CharQueueItem(char ch) {
            super(3, Character.valueOf(ch));
            this.mChar = ch;
        }
    }

    /* loaded from: classes.dex */
    public static class KeyQueueItem extends QueueItem {
        public final int mKey;

        public KeyQueueItem(int key) {
            super(8, Integer.valueOf(key));
            this.mKey = key;
        }
    }

    /* loaded from: classes.dex */
    public static class TextQueueItem extends QueueItem {
        public final CharSequence mText;

        public TextQueueItem(CharSequence text) {
            super(4, text);
            this.mText = text;
        }
    }

    /* loaded from: classes.dex */
    public static class ChangeSettings extends QueueItem {
        public final T9WriteSetting mSettings;

        public ChangeSettings(T9WriteSetting settings) {
            super(5, settings);
            this.mSettings = settings;
        }
    }

    /* loaded from: classes.dex */
    public static class StartArcSequence extends QueueItem {
        public StartArcSequence() {
            super(6, null);
        }
    }

    /* loaded from: classes.dex */
    public static class EndArcSequence extends QueueItem {
        public EndArcSequence() {
            super(7, null);
        }
    }
}
