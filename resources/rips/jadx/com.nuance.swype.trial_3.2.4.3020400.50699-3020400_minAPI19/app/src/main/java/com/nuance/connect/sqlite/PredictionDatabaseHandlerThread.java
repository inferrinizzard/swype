package com.nuance.connect.sqlite;

import android.os.Message;
import com.nuance.connect.util.HandlerThread;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.LinkedBlockingQueue;

/* loaded from: classes.dex */
class PredictionDatabaseHandlerThread extends HandlerThread {
    private static final int MAX_EVENTS = 100;
    public static final int MSG_CLEAR_PREDICTIONS = 1;
    public static final int MSG_PROCESS_PREDICTIONS = 0;
    private static final int QUEUE_PROCESS_DELAY = 100;
    private final ChinesePredictionDataSource dataSource;
    private final LinkedBlockingQueue<Object> queue = new LinkedBlockingQueue<>();
    private final Runnable delayedProcessor = new Runnable() { // from class: com.nuance.connect.sqlite.PredictionDatabaseHandlerThread.1
        @Override // java.lang.Runnable
        public void run() {
            PredictionDatabaseHandlerThread.this.process(0, null);
        }
    };

    /* JADX INFO: Access modifiers changed from: package-private */
    public PredictionDatabaseHandlerThread(ChinesePredictionDataSource chinesePredictionDataSource) {
        this.dataSource = chinesePredictionDataSource;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void addPrediction(Object obj) {
        this.queue.add(obj);
        if (this.queue.size() >= 100) {
            process(0, null);
        } else {
            processDelayed(this.delayedProcessor, 100L, true);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.connect.util.HandlerThread
    public Message handleMessage(Message message) {
        switch (message.what) {
            case 0:
                ArrayList arrayList = new ArrayList();
                this.queue.drainTo(arrayList);
                Iterator it = arrayList.iterator();
                while (it.hasNext()) {
                    Object next = it.next();
                    if (next instanceof ChinesePredictionDataRow) {
                        this.dataSource.insertPredictionObject((ChinesePredictionDataRow) next);
                    } else if (next instanceof ChinesePredictionDataResultRow) {
                        this.dataSource.insertPredictionResultObject((ChinesePredictionDataResultRow) next);
                    }
                }
                return null;
            case 1:
                this.dataSource.deletePredictions(message.arg1);
                return null;
            default:
                return null;
        }
    }
}
