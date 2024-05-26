package com.nuance.connect.sqlite;

import android.os.Message;
import com.nuance.connect.util.HandlerThread;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.LinkedBlockingQueue;

/* loaded from: classes.dex */
class DatabaseHandlerThread extends HandlerThread {
    private static final int MAX_EVENTS = 100;
    public static final int MSG_CLEAR_EVENTS = 1;
    public static final int MSG_PROCESS_EVENTS = 0;
    private static final int QUEUE_PROCESS_DELAY = 100;
    private final DlmEventsDataSource dataSource;
    private final LinkedBlockingQueue<DlmEvent> queue = new LinkedBlockingQueue<>();
    private final Runnable delayedProcessor = new Runnable() { // from class: com.nuance.connect.sqlite.DatabaseHandlerThread.1
        @Override // java.lang.Runnable
        public void run() {
            DatabaseHandlerThread.this.process(0, null);
        }
    };

    /* JADX INFO: Access modifiers changed from: package-private */
    public DatabaseHandlerThread(DlmEventsDataSource dlmEventsDataSource) {
        this.dataSource = dlmEventsDataSource;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void addEvent(DlmEvent dlmEvent) {
        this.queue.add(dlmEvent);
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
                    DlmEvent dlmEvent = (DlmEvent) it.next();
                    if (dlmEvent.highPriority) {
                        this.dataSource._insertHighPriorityEvent(dlmEvent.event, dlmEvent.category, dlmEvent.timestamp);
                    } else {
                        this.dataSource._insertEvent(dlmEvent.event, dlmEvent.category, dlmEvent.timestamp, dlmEvent.appname, dlmEvent.location, dlmEvent.locale, dlmEvent.inputType);
                    }
                }
                return null;
            case 1:
                this.dataSource._clearEvents(message.arg1, ((Long) message.obj).longValue());
                return null;
            default:
                return null;
        }
    }
}
