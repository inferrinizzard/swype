package com.nuance.swype.input.chinese;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import com.nuance.android.util.WeakReferenceHandler;
import com.nuance.connect.api.ChinesePredictionService;
import com.nuance.swype.connect.Connect;
import com.nuance.swype.input.IME;
import com.nuance.swype.util.LogManager;
import com.nuance.swypeconnect.ac.ACChinesePredictionService;
import com.nuance.swypeconnect.ac.ACException;
import java.lang.ref.WeakReference;
import java.util.Arrays;

/* loaded from: classes.dex */
public class ChineseCloudPrediction {
    private static final int MSG_POST_CHINESE_PREDICTION_TIMEOUT = 104;
    private static final int MSG_START_CHINESE_PREDICTION = 103;
    private static final int MSG_UPDATE_CHINESE_PREDICTION_RESULT = 102;
    WeakReference<ChinesePredictionListener> mChinesePredictionListenerWeakRef;
    private ACChinesePredictionService mChinesePredictionService;
    protected long startRequestPredictionTime;
    private static final LogManager.Log log = LogManager.getLog("ChineseCloudPrediction");
    private static volatile ChineseCloudPrediction instance = null;
    private static Context mContext = null;
    private boolean mbChinsePredictionSessionStarted = false;
    private String mChinesePredictionID = null;
    private byte[] mRequestCldInputData = null;
    private final Handler.Callback chinesePredictionHandlerCallback = new Handler.Callback() { // from class: com.nuance.swype.input.chinese.ChineseCloudPrediction.1
        @Override // android.os.Handler.Callback
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 102:
                    ChinesePredictionListener chinesePredictionListener = ChineseCloudPrediction.this.getChinesePredictionListener();
                    if (chinesePredictionListener != null) {
                        ChineseCloudPrediction.log.d("begin to show cloud result.");
                        chinesePredictionListener.showPredictionResult(msg.arg1 == 1, (ACChinesePredictionService.ACChinesePredictionResult) msg.obj);
                        if (ChineseCloudPrediction.this.startRequestPredictionTime > 0) {
                            ChineseCloudPrediction.log.d("Timing from request Prediction to show cloud result:", Long.valueOf(System.currentTimeMillis() - ChineseCloudPrediction.this.startRequestPredictionTime), " ms");
                            ChineseCloudPrediction.this.startRequestPredictionTime = 0L;
                            break;
                        }
                    }
                    break;
                case 103:
                    ChineseCloudPrediction.log.d("begin request Chinese Prediction.");
                    ChineseCloudPrediction.this.requestChinesePrediction(ChineseCloudPrediction.this.mRequestCldInputData);
                    break;
                case 104:
                    ChineseCloudPrediction.this.startRequestPredictionTime = 0L;
                    ChineseCloudPrediction.log.d("cloud timeout..hide prediction view");
                    ChineseCloudPrediction.this.postChinesePredictionResult(false, null, 100);
                    break;
                default:
                    return true;
            }
            return false;
        }
    };
    private final Handler mChinesePredictionHandler = WeakReferenceHandler.create(this.chinesePredictionHandlerCallback);
    private ACChinesePredictionService.ACChinesePredictionCallback chinesePredictionCallback = new ACChinesePredictionService.ACChinesePredictionCallback() { // from class: com.nuance.swype.input.chinese.ChineseCloudPrediction.2
        @Override // com.nuance.swypeconnect.ac.ACChinesePredictionService.ACChinesePredictionCallback
        public void onPredictionCancel(String predictionId) {
            ChineseCloudPrediction.log.d("ChinesePrediction ", "ChinesePrediction onPredictionCancel id = ", predictionId);
            if (ChineseCloudPrediction.this.mChinesePredictionID != null && ChineseCloudPrediction.this.mChinesePredictionID.equals(predictionId)) {
                ChineseCloudPrediction.this.mChinesePredictionID = null;
            }
        }

        @Override // com.nuance.swypeconnect.ac.ACChinesePredictionService.ACChinesePredictionCallback
        public void onPredictionFailure(String predictionId, int code, String description) {
            ChineseCloudPrediction.log.d("ChinesePrediction ", "ChinesePrediction onPredictionFailure id = ", predictionId, " code = ", Integer.valueOf(code), " msg= ", description);
            if (ChineseCloudPrediction.this.mChinesePredictionID != null && ChineseCloudPrediction.this.mChinesePredictionID.equals(predictionId)) {
                ChineseCloudPrediction.this.mChinesePredictionID = null;
                ChineseCloudPrediction.this.postChinesePredictionResult(false, null, 100);
            }
        }

        @Override // com.nuance.swypeconnect.ac.ACChinesePredictionService.ACChinesePredictionCallback
        public void onPredictionResult(ACChinesePredictionService.ACChinesePredictionResult result) {
            ChineseCloudPrediction.log.d("ChinesePrediction ", "ChinesePrediction onPredictionResult id = ", result.getPredictionId());
            if (ChineseCloudPrediction.this.mChinesePredictionID != null && ChineseCloudPrediction.this.mChinesePredictionID.equals(result.getPredictionId())) {
                ChineseCloudPrediction.this.postChinesePredictionResult(true, result, 1);
                ChineseCloudPrediction.log.d("cloud results when success.");
                ChineseCloudPrediction.this.mChinesePredictionHandler.removeMessages(104);
            }
        }

        @Override // com.nuance.swypeconnect.ac.ACChinesePredictionService.ACChinesePredictionCallback
        public void onPredictionStatus(int code, String description) {
        }
    };

    /* loaded from: classes.dex */
    public interface ChinesePredictionListener {
        void showPredictionResult(boolean z, ACChinesePredictionService.ACChinesePredictionResult aCChinesePredictionResult);
    }

    private ChineseCloudPrediction() {
    }

    public static ChineseCloudPrediction getInstance(Context context) {
        if (instance == null) {
            instance = new ChineseCloudPrediction();
            mContext = context;
        }
        return instance;
    }

    public void setChinesePredictionListener(ChinesePredictionListener listener) {
        this.mChinesePredictionListenerWeakRef = new WeakReference<>(listener);
    }

    public ChinesePredictionListener getChinesePredictionListener() {
        if (this.mChinesePredictionListenerWeakRef != null) {
            return this.mChinesePredictionListenerWeakRef.get();
        }
        return null;
    }

    public void finish() {
        this.mChinesePredictionHandler.removeMessages(102);
        this.mChinesePredictionHandler.removeMessages(103);
        this.mChinesePredictionHandler.removeMessages(104);
        this.startRequestPredictionTime = 0L;
        log.d("begin finishing service");
        if (this.mChinesePredictionService != null) {
            if (this.mbChinsePredictionSessionStarted) {
                log.d("end session");
                this.mChinesePredictionService.endSession();
            }
            this.mbChinsePredictionSessionStarted = false;
            if (this.mChinesePredictionID != null) {
                log.d("cancel prediction");
                this.mChinesePredictionService.cancelPrediction(this.mChinesePredictionID);
            }
            if (this.chinesePredictionCallback != null) {
                log.d("unregister Callback");
                this.mChinesePredictionService.unregisterCallback(this.chinesePredictionCallback);
            }
            this.mChinesePredictionService = null;
        }
    }

    public void destroy() {
        finish();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String requestChinesePrediction(byte[] requestCldInputData) {
        if (requestCldInputData == null || requestCldInputData.length <= 0 || !Connect.from(mContext).isInitialized()) {
            return null;
        }
        String id = doRequestPrediction(requestCldInputData);
        this.mChinesePredictionID = id;
        logChinesePredictionID();
        this.mChinesePredictionHandler.removeMessages(104);
        this.mChinesePredictionHandler.sendEmptyMessageDelayed(104, IME.RETRY_DELAY_IN_MILLIS);
        return id;
    }

    private synchronized String doRequestPrediction(byte[] requestCldInputData) {
        String id;
        String id2 = null;
        try {
            try {
                log.d("begin requesting Prediction ID");
                id2 = this.mChinesePredictionService.requestPrediction(requestCldInputData);
                this.startRequestPredictionTime = System.currentTimeMillis();
                log.d("requested Prediction ID:", id2);
                id = id2;
            } catch (ACException e) {
                log.e("requested Prediction ID...ACException: ", e);
                id = id2;
            }
        } catch (Throwable th) {
            id = id2;
        }
        return id;
    }

    public void requestChinesePrediction(byte[] requestData, int coreLanguageID, int cldPredicitonCharacterSet) {
        if (this.mChinesePredictionService == null) {
            this.mChinesePredictionService = Connect.from(mContext).getChinesePredictionService();
            if (this.mChinesePredictionService != null && Connect.from(mContext).isInitialized()) {
                doRegisterCallback();
            } else {
                return;
            }
        }
        if (!this.mbChinsePredictionSessionStarted) {
            try {
                doStartSession(coreLanguageID, cldPredicitonCharacterSet);
            } catch (ACException e) {
                log.e("start session...ACException: ", e);
                return;
            }
        }
        if (this.mChinesePredictionHandler.hasMessages(103)) {
            this.mChinesePredictionHandler.removeMessages(103);
        } else {
            doCancelPrediction();
        }
        if (requestData != null) {
            this.mRequestCldInputData = Arrays.copyOf(requestData, requestData.length);
        }
        log.d("New Chinese Prediction request created.");
        this.mChinesePredictionHandler.sendEmptyMessageDelayed(103, 200L);
    }

    private synchronized void doRegisterCallback() {
        this.mChinesePredictionService.registerCallback(this.chinesePredictionCallback);
        try {
            this.mChinesePredictionService.setIdleTimeout(30);
            this.mChinesePredictionService.setPredictionResults(1);
        } catch (ACException e) {
            log.e("registerCallback...ACException: ", e);
        }
    }

    private synchronized void doCancelPrediction() {
        if (this.mChinesePredictionID != null) {
            log.d("Cancel Chinese Prediction request ID:", this.mChinesePredictionID);
            this.mChinesePredictionService.cancelPrediction(this.mChinesePredictionID);
            this.mChinesePredictionID = null;
        }
    }

    private synchronized void doStartSession(int languageId, int characterSet) throws ACException {
        log.d("Start session");
        this.mChinesePredictionService.startSession(languageId, characterSet);
        this.mbChinsePredictionSessionStarted = true;
    }

    public void cancelChinesePrediction() {
        this.startRequestPredictionTime = 0L;
        this.mChinesePredictionHandler.removeMessages(103);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void postChinesePredictionResult(boolean show, ACChinesePredictionService.ACChinesePredictionResult result, int delayMs) {
        this.mChinesePredictionHandler.removeMessages(102);
        this.mChinesePredictionHandler.sendMessageDelayed(this.mChinesePredictionHandler.obtainMessage(102, show ? 1 : 0, 0, result), delayMs);
    }

    private void logChinesePredictionID() {
        if (this.mChinesePredictionService != null && this.mbChinsePredictionSessionStarted && this.mChinesePredictionID != null && Connect.from(mContext).isInitialized()) {
            doLogResultSelection(this.mChinesePredictionID, null, null, null, null);
        }
    }

    public void logSelectedWordForChinesePrediction(String word, String spell) {
        if (word != null && this.mChinesePredictionService != null && this.mbChinsePredictionSessionStarted && this.mChinesePredictionID != null && Connect.from(mContext).isInitialized()) {
            doLogResultSelection(this.mChinesePredictionID, word, spell, null, null);
        }
    }

    private synchronized void doLogResultSelection(String predictionId, String word, String spell, String fullSpell, int[] attributes) {
        if (word == null && spell == null && fullSpell == null && attributes == null) {
            try {
                this.mChinesePredictionService.logResultSelection(predictionId);
                log.d("logResultSelection Prediction ID:", predictionId);
            } catch (ACException e) {
                log.e("logChinesePredictionID...ACException: ", e);
            }
        } else if (fullSpell == null && attributes == null) {
            try {
                this.mChinesePredictionService.logResultSelection(this.mChinesePredictionID, word, spell);
                log.d("logResultSelection ID:", this.mChinesePredictionID);
            } catch (ACException e2) {
                log.e("logSelectedWordForChinesePrediction...ACException: ", e2);
            }
        } else {
            try {
                log.d("logResultSelection ID:", predictionId);
                this.mChinesePredictionService.logResultSelection(predictionId, word, spell, fullSpell, attributes);
            } catch (ACException e3) {
                log.e("logSelectedWordForChinesePrediction...ACException: ", e3);
            }
        }
    }

    public void logSelectedWordForChinesePrediction(ChinesePredictionService.ChinesePrediction chinesePrediction) {
        if (chinesePrediction != null && this.mChinesePredictionService != null && this.mbChinsePredictionSessionStarted && this.mChinesePredictionID != null) {
            doLogResultSelection(chinesePrediction.getPredictionId(), chinesePrediction.getPhrase(), chinesePrediction.getSpell(), chinesePrediction.getFullSpell(), chinesePrediction.getAttributes());
        }
    }
}
