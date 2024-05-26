package com.nuance.nmsp.client.sdk.oem;

import android.os.Looper;
import com.nuance.nmsp.client.sdk.common.oem.api.LogFactory;
import com.nuance.nmsp.client.sdk.common.oem.api.MessageSystem;
import com.nuance.nmsp.client.sdk.common.oem.api.TimerSystem;
import java.util.Hashtable;

/* loaded from: classes.dex */
public class MessageSystemOEM implements MessageSystem {
    private static final LogFactory.Log a = LogFactory.getLog(MessageSystemOEM.class);
    private final Hashtable<TimerSystem.TimerSystemTask, TimerRunnable> d = new Hashtable<>();
    private final AttachableHandler b = new AttachableHandler();
    private final Thread c = new Thread(new Runnable() { // from class: com.nuance.nmsp.client.sdk.oem.MessageSystemOEM.1
        @Override // java.lang.Runnable
        public final void run() {
            Looper.prepare();
            MessageSystemOEM.this.b.attachToCurrentThread();
            Looper.loop();
        }
    });

    /* loaded from: classes.dex */
    public static class Message {
        public Object msgData;
        public MessageSystem.MessageHandler msgHandler;
        public Thread msgReceiver;
        public Thread msgSender;

        public Message(Object obj, MessageSystem.MessageHandler messageHandler) {
            this.msgData = obj;
            this.msgHandler = messageHandler;
        }
    }

    /* loaded from: classes.dex */
    public class TimerRunnable implements Runnable {
        private TimerSystem.TimerSystemTask a;

        public TimerRunnable(TimerSystem.TimerSystemTask timerSystemTask) {
            this.a = timerSystemTask;
            MessageSystemOEM.this.d.put(timerSystemTask, this);
        }

        @Override // java.lang.Runnable
        public void run() {
            TimerRunnable timerRunnable = (TimerRunnable) MessageSystemOEM.this.d.remove(this.a);
            if (MessageSystemOEM.a.isDebugEnabled()) {
                MessageSystemOEM.a.debug("TIMER run() _pendingTimerTasks.size():" + MessageSystemOEM.this.d.size() + ", this:" + this + ", r:" + timerRunnable);
            }
            if (timerRunnable != null) {
                timerRunnable.a.run();
            }
        }
    }

    public MessageSystemOEM() {
        this.c.start();
    }

    public static void terminate() {
    }

    @Override // com.nuance.nmsp.client.sdk.common.oem.api.MessageSystem
    public boolean cancelTask(TimerSystem.TimerSystemTask timerSystemTask) {
        TimerRunnable remove = this.d.remove(timerSystemTask);
        if (a.isDebugEnabled()) {
            a.debug("TIMER cancelTask() _pendingTimerTasks.size():" + this.d.size());
        }
        if (remove != null) {
            if (a.isDebugEnabled()) {
                a.debug("TIMER _handler.removeCallbacks(" + remove + ")");
            }
            this.b.removeCallbacks(remove);
        }
        return remove != null;
    }

    public Message createMessage(Object obj, MessageSystem.MessageHandler messageHandler) {
        return new Message(obj, messageHandler);
    }

    @Override // com.nuance.nmsp.client.sdk.common.oem.api.MessageSystem
    public Object getMyAddr() {
        return Thread.currentThread();
    }

    @Override // com.nuance.nmsp.client.sdk.common.oem.api.MessageSystem
    public int getNumOfVRThreads() {
        return 1;
    }

    @Override // com.nuance.nmsp.client.sdk.common.oem.api.MessageSystem
    public Object[] getVRAddr() {
        return new Object[]{Thread.currentThread()};
    }

    @Override // com.nuance.nmsp.client.sdk.common.oem.api.MessageSystem
    public void scheduleTask(TimerSystem.TimerSystemTask timerSystemTask, long j) {
        TimerRunnable timerRunnable = new TimerRunnable(timerSystemTask);
        if (a.isDebugEnabled()) {
            a.debug("TIMER _handler.postDelayed(" + timerRunnable + ")");
        }
        this.b.postDelayed(timerRunnable, j);
    }

    @Override // com.nuance.nmsp.client.sdk.common.oem.api.MessageSystem
    public void send(Object obj, MessageSystem.MessageHandler messageHandler, Object obj2, Object obj3) {
        final Message createMessage = createMessage(obj, messageHandler);
        createMessage.msgReceiver = (Thread) obj3;
        createMessage.msgSender = (Thread) obj2;
        this.b.post(new Runnable(this) { // from class: com.nuance.nmsp.client.sdk.oem.MessageSystemOEM.2
            @Override // java.lang.Runnable
            public final void run() {
                if (MessageSystemOEM.a.isTraceEnabled()) {
                    MessageSystemOEM.a.trace("Executing Message");
                }
                createMessage.msgHandler.handleMessage(createMessage.msgData, createMessage.msgSender);
                if (MessageSystemOEM.a.isTraceEnabled()) {
                    MessageSystemOEM.a.trace("Done Executing Message");
                }
            }
        });
    }

    @Override // com.nuance.nmsp.client.sdk.common.oem.api.MessageSystem
    public void setSessionId(byte[] bArr) {
    }

    @Override // com.nuance.nmsp.client.sdk.common.oem.api.MessageSystem
    public void stop() {
        this.b.post(new Runnable(this) { // from class: com.nuance.nmsp.client.sdk.oem.MessageSystemOEM.3
            @Override // java.lang.Runnable
            public final void run() {
                Looper.myLooper().quit();
            }
        });
    }
}
