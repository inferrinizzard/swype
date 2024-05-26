package com.google.android.voiceime;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import com.google.android.voiceime.IntentApiTrigger;
import com.google.android.voiceime.ServiceHelper;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class ServiceBridge {
    final IntentApiTrigger.Callback mCallback;
    ConnectionRequest mCurrentRequest;

    public ServiceBridge() {
        this(null);
    }

    public ServiceBridge(IntentApiTrigger.Callback callback) {
        this.mCallback = callback;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class ConnectionRequest implements ServiceConnection {
        private final String mLanguageCode;
        private ServiceHelper.Callback mServiceCallback;

        /* JADX INFO: Access modifiers changed from: package-private */
        public /* synthetic */ ConnectionRequest(ServiceBridge x0, String x1, byte b) {
            this(x1);
        }

        private ConnectionRequest(String languageCode) {
            this.mLanguageCode = languageCode;
        }

        @Override // android.content.ServiceConnection
        public final void onServiceConnected(ComponentName className, IBinder service) {
            ServiceHelper serviceHelper = ServiceHelper.this;
            ServiceHelper.Callback callback = this.mServiceCallback;
            ServiceHelper.log.d("#startRecognition");
            serviceHelper.mCallback = callback;
            Intent intent = new Intent(serviceHelper, (Class<?>) ActivityHelper.class);
            intent.addFlags(268435456);
            serviceHelper.startActivity(intent);
        }

        @Override // android.content.ServiceConnection
        public final void onServiceDisconnected(ComponentName className) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class ConnectionResponse implements ServiceConnection {
        private final Context mContext;
        private final String mRecognitionResult;

        /* JADX INFO: Access modifiers changed from: package-private */
        public /* synthetic */ ConnectionResponse(ServiceBridge x0, Context x1, String x2, byte b) {
            this(x1, x2);
        }

        private ConnectionResponse(Context context, String recognitionResult) {
            this.mRecognitionResult = recognitionResult;
            this.mContext = context;
        }

        @Override // android.content.ServiceConnection
        public final void onServiceDisconnected(ComponentName name) {
        }

        @Override // android.content.ServiceConnection
        public final void onServiceConnected(ComponentName name, IBinder service) {
            ServiceHelper serviceHelper = ServiceHelper.this;
            String str = this.mRecognitionResult;
            if (serviceHelper.mCallback != null) {
                serviceHelper.mCallback.onResult(str);
            }
            this.mContext.unbindService(this);
        }
    }
}
