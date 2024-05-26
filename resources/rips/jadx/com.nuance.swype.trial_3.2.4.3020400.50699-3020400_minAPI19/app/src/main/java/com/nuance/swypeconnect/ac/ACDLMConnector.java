package com.nuance.swypeconnect.ac;

import android.util.Base64;
import com.nuance.connect.api.DLMConnector;
import com.nuance.connect.store.PersistentDataStore;
import com.nuance.connect.util.ConcurrentCallbackSet;
import com.nuance.connect.util.Logger;
import com.nuance.swypeconnect.ac.ACDLMEventData;
import com.nuance.swypeconnect.ac.ACDLMEventInternal;
import java.util.Arrays;
import java.util.List;

/* loaded from: classes.dex */
public final class ACDLMConnector extends ACService {
    public static final int CORE_ALPHA = 1;
    public static final int CORE_CHINESE = 3;
    public static final int CORE_JAPANESE = 4;
    public static final int CORE_KOREAN = 2;
    public static final int DLM_RESERVED_DEFAULT = 0;
    public static final int DLM_SIZE_LARGE = 3145728;
    public static final int DLM_SIZE_MIN = 102400;
    public static final int DLM_SIZE_NORMAL = 2097152;
    public static final int DLM_SIZE_SMALL = 1048576;
    public static final int DLM_SIZE_TINY = 204800;
    public static final int DLM_USER_CATEGORY = 0;
    private static final ACDLMEventInternal.Compiler eventCompiler = new ACDLMEventInternal.Compiler();
    private ACAlphaDlmDb alphaDlmDb;
    private ACAlphaDlmEventCallback alphaDlmEventCallback;
    private ACChineseDlmDb chineseDlmDb;
    private ACChineseDlmEventCallback chineseDlmEventCallback;
    private ACKoreanDlmDb koreanDlmDb;
    private ACKoreanDlmEventCallback koreanDlmEventCallback;
    private Logger.Log log = Logger.getLog(Logger.LoggerType.OEM);
    private final ACManager manager;
    private final DLMConnector service;

    /* loaded from: classes.dex */
    private abstract class ACAbstractDlmEventCallback {
        protected final ConcurrentCallbackSet<ACDlmEventCallbackObserver> observers;

        private ACAbstractDlmEventCallback() {
            this.observers = new ConcurrentCallbackSet<>();
        }

        public void registerObserver(ACDlmEventCallbackObserver aCDlmEventCallbackObserver) {
            this.observers.add(aCDlmEventCallbackObserver);
        }

        public void unregisterObserver(ACDlmEventCallbackObserver aCDlmEventCallbackObserver) {
            this.observers.remove(aCDlmEventCallbackObserver);
        }

        public void unregisterObservers() {
            this.observers.clear();
        }
    }

    /* loaded from: classes.dex */
    public interface ACAlphaDlmDb {
        void deleteCategory(int i);

        void deleteCategoryLanguage(int i, int i2);

        void exportAsEvents(boolean z, int i);

        void processEvent(byte[] bArr);

        void scanBegin(String str, String str2, String str3, int i, int i2);

        boolean scanBuffer(char[] cArr, int i, int i2, int i3, int i4, int i5, boolean z, boolean z2);

        void scanEnd();
    }

    /* loaded from: classes.dex */
    public static class ACAlphaDlmDbBase implements ACAlphaDlmDb {
        @Override // com.nuance.swypeconnect.ac.ACDLMConnector.ACAlphaDlmDb
        public void deleteCategory(int i) {
        }

        @Override // com.nuance.swypeconnect.ac.ACDLMConnector.ACAlphaDlmDb
        public void deleteCategoryLanguage(int i, int i2) {
        }

        @Override // com.nuance.swypeconnect.ac.ACDLMConnector.ACAlphaDlmDb
        public void exportAsEvents(boolean z, int i) {
        }

        @Override // com.nuance.swypeconnect.ac.ACDLMConnector.ACAlphaDlmDb
        public void processEvent(byte[] bArr) {
        }

        @Override // com.nuance.swypeconnect.ac.ACDLMConnector.ACAlphaDlmDb
        public void scanBegin(String str, String str2, String str3, int i, int i2) {
        }

        @Override // com.nuance.swypeconnect.ac.ACDLMConnector.ACAlphaDlmDb
        public boolean scanBuffer(char[] cArr, int i, int i2, int i3, int i4, int i5, boolean z, boolean z2) {
            return false;
        }

        @Override // com.nuance.swypeconnect.ac.ACDLMConnector.ACAlphaDlmDb
        public void scanEnd() {
        }
    }

    /* loaded from: classes.dex */
    public final class ACAlphaDlmEventCallback extends ACAbstractDlmEventCallback implements ACDlmEventCallback {
        protected static final int CORE = 1;

        ACAlphaDlmEventCallback() {
            super();
        }

        public final void onAlphaDlmEvent(byte[] bArr, boolean z) {
            ACDLMEventData.ACDLMEvent[] compileAll;
            if (ACDLMConnector.this.manager.getConnect().isLicensed()) {
                ACDLMConnector.this.service.onDlmEvent(1, Base64.encodeToString(bArr, 2), System.currentTimeMillis(), z);
                ACReportingService reportingService = ACDLMConnector.this.manager.getReportingService();
                if (reportingService == null || !reportingService.isEnabled() || (compileAll = ACDLMConnector.eventCompiler.compileAll(bArr)) == null) {
                    return;
                }
                for (ACDLMEventData.ACDLMEvent aCDLMEvent : compileAll) {
                    switch (aCDLMEvent.getType()) {
                        case 21:
                            if ((aCDLMEvent instanceof ACDLMEventInternal.AddWordDlmEventImpl) && ((ACDLMEventInternal.AddWordDlmEventImpl) aCDLMEvent).highPriority) {
                                reportingService.getHelper().recordUdbAdd(((ACDLMEventData.ACAddWordDLMEvent) aCDLMEvent).getWord());
                                break;
                            }
                            break;
                        case 22:
                            reportingService.getHelper().recordUdbDelete(((ACDLMEventData.ACDeleteWordDLMEvent) aCDLMEvent).getWord());
                            break;
                    }
                }
            }
        }

        @Override // com.nuance.swypeconnect.ac.ACDLMConnector.ACDlmEventCallback
        public final void onReset(boolean z) {
            ACDLMConnector.this.log.v("ACAlphaDlmEventCallback.onReset userCategory=", Boolean.valueOf(z));
            if (ACDLMConnector.this.manager.getConnect().isLicensed()) {
                ACDLMConnector.this.service.onReset(1, z);
            }
        }

        @Override // com.nuance.swypeconnect.ac.ACDLMConnector.ACAbstractDlmEventCallback, com.nuance.swypeconnect.ac.ACDLMConnector.ACDlmEventCallback
        public final /* bridge */ /* synthetic */ void registerObserver(ACDlmEventCallbackObserver aCDlmEventCallbackObserver) {
            super.registerObserver(aCDlmEventCallbackObserver);
        }

        @Override // com.nuance.swypeconnect.ac.ACDLMConnector.ACDlmEventCallback
        public final void resume() {
            ACDLMConnector.this.log.v("ACAlphaDlmEventCallback.resume");
            if (ACDLMConnector.this.manager.getConnect().isLicensed()) {
                ACDLMConnector.this.service.resume(1);
                for (ACDlmEventCallbackObserver aCDlmEventCallbackObserver : (ACDlmEventCallbackObserver[]) this.observers.toArray(new ACDlmEventCallbackObserver[0])) {
                    aCDlmEventCallbackObserver.onResume();
                }
            }
        }

        @Override // com.nuance.swypeconnect.ac.ACDLMConnector.ACAbstractDlmEventCallback, com.nuance.swypeconnect.ac.ACDLMConnector.ACDlmEventCallback
        public final /* bridge */ /* synthetic */ void unregisterObserver(ACDlmEventCallbackObserver aCDlmEventCallbackObserver) {
            super.unregisterObserver(aCDlmEventCallbackObserver);
        }

        @Override // com.nuance.swypeconnect.ac.ACDLMConnector.ACAbstractDlmEventCallback, com.nuance.swypeconnect.ac.ACDLMConnector.ACDlmEventCallback
        public final /* bridge */ /* synthetic */ void unregisterObservers() {
            super.unregisterObservers();
        }

        @Override // com.nuance.swypeconnect.ac.ACDLMConnector.ACDlmEventCallback
        public final void yield() {
            ACDLMConnector.this.log.v("ACAlphaDlmEventCallback.yield");
            if (ACDLMConnector.this.manager.getConnect().isLicensed()) {
                ACDLMConnector.this.service.yield(1);
                for (ACDlmEventCallbackObserver aCDlmEventCallbackObserver : (ACDlmEventCallbackObserver[]) this.observers.toArray(new ACDlmEventCallbackObserver[0])) {
                    aCDlmEventCallbackObserver.onYield();
                }
            }
        }
    }

    /* loaded from: classes.dex */
    public interface ACChineseDlmDb {
        void deleteCategory(int i);

        void deleteCategoryLanguage(int i, int i2);

        void exportAsEvents(boolean z, int i);

        void processEvent(byte[] bArr);
    }

    /* loaded from: classes.dex */
    public static class ACChineseDlmDbBase implements ACChineseDlmDb {
        @Override // com.nuance.swypeconnect.ac.ACDLMConnector.ACChineseDlmDb
        public void deleteCategory(int i) {
        }

        @Override // com.nuance.swypeconnect.ac.ACDLMConnector.ACChineseDlmDb
        public void deleteCategoryLanguage(int i, int i2) {
        }

        @Override // com.nuance.swypeconnect.ac.ACDLMConnector.ACChineseDlmDb
        public void exportAsEvents(boolean z, int i) {
        }

        @Override // com.nuance.swypeconnect.ac.ACDLMConnector.ACChineseDlmDb
        public void processEvent(byte[] bArr) {
        }
    }

    /* loaded from: classes.dex */
    public final class ACChineseDlmEventCallback extends ACAbstractDlmEventCallback implements ACDlmEventCallback {
        protected static final int CORE = 3;

        ACChineseDlmEventCallback() {
            super();
        }

        public final void onChineseDlmEvent(byte[] bArr, boolean z) {
            if (ACDLMConnector.this.manager.getConnect().isLicensed()) {
                ACDLMConnector.this.service.onDlmEvent(3, Base64.encodeToString(bArr, 2), System.currentTimeMillis(), z);
            }
        }

        @Override // com.nuance.swypeconnect.ac.ACDLMConnector.ACDlmEventCallback
        public final void onReset(boolean z) {
            ACDLMConnector.this.log.v("ACChineseDlmEventCallback.resume");
            if (ACDLMConnector.this.manager.getConnect().isLicensed()) {
                ACDLMConnector.this.service.onReset(3, z);
            }
        }

        @Override // com.nuance.swypeconnect.ac.ACDLMConnector.ACAbstractDlmEventCallback, com.nuance.swypeconnect.ac.ACDLMConnector.ACDlmEventCallback
        public final /* bridge */ /* synthetic */ void registerObserver(ACDlmEventCallbackObserver aCDlmEventCallbackObserver) {
            super.registerObserver(aCDlmEventCallbackObserver);
        }

        @Override // com.nuance.swypeconnect.ac.ACDLMConnector.ACDlmEventCallback
        public final void resume() {
            ACDLMConnector.this.log.v("ACChineseDlmEventCallback.resume");
            if (ACDLMConnector.this.manager.getConnect().isLicensed()) {
                ACDLMConnector.this.service.resume(3);
                for (ACDlmEventCallbackObserver aCDlmEventCallbackObserver : (ACDlmEventCallbackObserver[]) this.observers.toArray(new ACDlmEventCallbackObserver[0])) {
                    aCDlmEventCallbackObserver.onResume();
                }
            }
        }

        @Override // com.nuance.swypeconnect.ac.ACDLMConnector.ACAbstractDlmEventCallback, com.nuance.swypeconnect.ac.ACDLMConnector.ACDlmEventCallback
        public final /* bridge */ /* synthetic */ void unregisterObserver(ACDlmEventCallbackObserver aCDlmEventCallbackObserver) {
            super.unregisterObserver(aCDlmEventCallbackObserver);
        }

        @Override // com.nuance.swypeconnect.ac.ACDLMConnector.ACAbstractDlmEventCallback, com.nuance.swypeconnect.ac.ACDLMConnector.ACDlmEventCallback
        public final /* bridge */ /* synthetic */ void unregisterObservers() {
            super.unregisterObservers();
        }

        @Override // com.nuance.swypeconnect.ac.ACDLMConnector.ACDlmEventCallback
        public final void yield() {
            ACDLMConnector.this.log.v("ACChineseDlmEventCallback.yield");
            if (ACDLMConnector.this.manager.getConnect().isLicensed()) {
                ACDLMConnector.this.service.yield(3);
                for (ACDlmEventCallbackObserver aCDlmEventCallbackObserver : (ACDlmEventCallbackObserver[]) this.observers.toArray(new ACDlmEventCallbackObserver[0])) {
                    aCDlmEventCallbackObserver.onYield();
                }
            }
        }
    }

    /* loaded from: classes.dex */
    public interface ACDlmEventCallback {
        void onReset(boolean z);

        void registerObserver(ACDlmEventCallbackObserver aCDlmEventCallbackObserver);

        void resume();

        void unregisterObserver(ACDlmEventCallbackObserver aCDlmEventCallbackObserver);

        void unregisterObservers();

        void yield();
    }

    /* loaded from: classes.dex */
    public interface ACDlmEventCallbackObserver {
        void onResume();

        void onYield();
    }

    /* loaded from: classes.dex */
    public interface ACKoreanDlmDb {
        void deleteCategory(int i);

        void deleteCategoryLanguage(int i, int i2);

        void exportAsEvents(boolean z, int i);

        void processEvent(byte[] bArr);

        boolean scanBuffer(char[] cArr, int i, int i2, boolean z, boolean z2);
    }

    /* loaded from: classes.dex */
    public static class ACKoreanDlmDbBase implements ACKoreanDlmDb {
        @Override // com.nuance.swypeconnect.ac.ACDLMConnector.ACKoreanDlmDb
        public void deleteCategory(int i) {
        }

        @Override // com.nuance.swypeconnect.ac.ACDLMConnector.ACKoreanDlmDb
        public void deleteCategoryLanguage(int i, int i2) {
        }

        @Override // com.nuance.swypeconnect.ac.ACDLMConnector.ACKoreanDlmDb
        public void exportAsEvents(boolean z, int i) {
        }

        @Override // com.nuance.swypeconnect.ac.ACDLMConnector.ACKoreanDlmDb
        public void processEvent(byte[] bArr) {
        }

        @Override // com.nuance.swypeconnect.ac.ACDLMConnector.ACKoreanDlmDb
        public boolean scanBuffer(char[] cArr, int i, int i2, boolean z, boolean z2) {
            return false;
        }
    }

    /* loaded from: classes.dex */
    public final class ACKoreanDlmEventCallback extends ACAbstractDlmEventCallback implements ACDlmEventCallback {
        protected static final int CORE = 2;

        ACKoreanDlmEventCallback() {
            super();
        }

        public final void onKoreanDlmEvent(byte[] bArr, boolean z) {
            ACDLMEventData.ACDLMEvent[] compileAll;
            if (ACDLMConnector.this.manager.getConnect().isLicensed()) {
                ACDLMConnector.this.service.onDlmEvent(2, Base64.encodeToString(bArr, 2), System.currentTimeMillis(), z);
                ACReportingService reportingService = ACDLMConnector.this.manager.getReportingService();
                if (reportingService == null || !reportingService.isEnabled() || (compileAll = ACDLMConnector.eventCompiler.compileAll(bArr)) == null) {
                    return;
                }
                for (ACDLMEventData.ACDLMEvent aCDLMEvent : compileAll) {
                    switch (aCDLMEvent.getType()) {
                        case 21:
                            reportingService.getHelper().recordUdbAdd(((ACDLMEventData.ACAddWordDLMEvent) aCDLMEvent).getWord());
                            break;
                        case 22:
                            reportingService.getHelper().recordUdbDelete(((ACDLMEventData.ACDeleteWordDLMEvent) aCDLMEvent).getWord());
                            break;
                    }
                }
            }
        }

        @Override // com.nuance.swypeconnect.ac.ACDLMConnector.ACDlmEventCallback
        public final void onReset(boolean z) {
            ACDLMConnector.this.log.v("onKoreanDlmEvent.onReset userCategory=", Boolean.valueOf(z));
            if (ACDLMConnector.this.manager.getConnect().isLicensed()) {
                ACDLMConnector.this.service.onReset(2, z);
            }
        }

        @Override // com.nuance.swypeconnect.ac.ACDLMConnector.ACAbstractDlmEventCallback, com.nuance.swypeconnect.ac.ACDLMConnector.ACDlmEventCallback
        public final /* bridge */ /* synthetic */ void registerObserver(ACDlmEventCallbackObserver aCDlmEventCallbackObserver) {
            super.registerObserver(aCDlmEventCallbackObserver);
        }

        @Override // com.nuance.swypeconnect.ac.ACDLMConnector.ACDlmEventCallback
        public final void resume() {
            ACDLMConnector.this.log.v("onKoreanDlmEvent.resume");
            if (ACDLMConnector.this.manager.getConnect().isLicensed()) {
                ACDLMConnector.this.service.resume(2);
                for (ACDlmEventCallbackObserver aCDlmEventCallbackObserver : (ACDlmEventCallbackObserver[]) this.observers.toArray(new ACDlmEventCallbackObserver[0])) {
                    aCDlmEventCallbackObserver.onResume();
                }
            }
        }

        @Override // com.nuance.swypeconnect.ac.ACDLMConnector.ACAbstractDlmEventCallback, com.nuance.swypeconnect.ac.ACDLMConnector.ACDlmEventCallback
        public final /* bridge */ /* synthetic */ void unregisterObserver(ACDlmEventCallbackObserver aCDlmEventCallbackObserver) {
            super.unregisterObserver(aCDlmEventCallbackObserver);
        }

        @Override // com.nuance.swypeconnect.ac.ACDLMConnector.ACAbstractDlmEventCallback, com.nuance.swypeconnect.ac.ACDLMConnector.ACDlmEventCallback
        public final /* bridge */ /* synthetic */ void unregisterObservers() {
            super.unregisterObservers();
        }

        @Override // com.nuance.swypeconnect.ac.ACDLMConnector.ACDlmEventCallback
        public final void yield() {
            ACDLMConnector.this.log.v("onKoreanDlmEvent.yield");
            if (ACDLMConnector.this.manager.getConnect().isLicensed()) {
                ACDLMConnector.this.service.yield(2);
                for (ACDlmEventCallbackObserver aCDlmEventCallbackObserver : (ACDlmEventCallbackObserver[]) this.observers.toArray(new ACDlmEventCallbackObserver[0])) {
                    aCDlmEventCallbackObserver.onYield();
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ACDLMConnector(DLMConnector dLMConnector, PersistentDataStore persistentDataStore, ACManager aCManager) {
        this.service = dLMConnector;
        this.manager = aCManager;
    }

    public static List<ACDLMEventData.ACDLMEvent> decodeAllEvents(byte[] bArr) {
        Logger.getLog(Logger.LoggerType.OEM).d("decodeAllEvents");
        return Arrays.asList(eventCompiler.compileAll(bArr));
    }

    public static ACDLMEventData.ACDLMEvent decodeEvent(byte[] bArr) {
        return eventCompiler.compile(bArr);
    }

    public final ACAlphaDlmEventCallback bindAlphaDlm(ACAlphaDlmDb aCAlphaDlmDb) throws ACException {
        if (this.alphaDlmEventCallback != null) {
            throw new ACException(115, "The Alpha DLM you are attempting to bind is already bound.");
        }
        if (!this.manager.getConnect().isLicensed()) {
            throw new ACException(129, "This SDK is unlicensed.");
        }
        this.alphaDlmDb = aCAlphaDlmDb;
        this.alphaDlmEventCallback = new ACAlphaDlmEventCallback();
        this.service.registerDlmCallback(1, new DLMConnector.DlmEventCallback() { // from class: com.nuance.swypeconnect.ac.ACDLMConnector.1
            @Override // com.nuance.connect.api.DLMConnector.DlmEventCallback
            public boolean backupDlm() {
                if (ACDLMConnector.this.alphaDlmDb == null) {
                    return false;
                }
                ACDLMConnector.this.alphaDlmDb.exportAsEvents(true, 0);
                return true;
            }

            @Override // com.nuance.connect.api.DLMConnector.DlmEventCallback
            public boolean processDlmDelete(int i) {
                if (ACDLMConnector.this.alphaDlmDb == null) {
                    return false;
                }
                ACDLMConnector.this.alphaDlmDb.deleteCategory(i);
                return true;
            }

            @Override // com.nuance.connect.api.DLMConnector.DlmEventCallback
            public boolean processDlmDelete(int i, int i2) {
                if (ACDLMConnector.this.alphaDlmDb == null) {
                    return false;
                }
                ACDLMConnector.this.alphaDlmDb.deleteCategoryLanguage(i, i2);
                return true;
            }

            @Override // com.nuance.connect.api.DLMConnector.DlmEventCallback
            public boolean processEvent(String str) {
                byte[] bArr = null;
                try {
                    bArr = Base64.decode(str, 2);
                } catch (Exception e) {
                }
                if (ACDLMConnector.this.alphaDlmDb == null || bArr == null) {
                    return false;
                }
                ACDLMConnector.this.alphaDlmDb.processEvent(bArr);
                return true;
            }

            @Override // com.nuance.connect.api.DLMConnector.DlmEventCallback
            public boolean scan(char[] cArr, int i, int i2, boolean z) {
                if (ACDLMConnector.this.alphaDlmDb != null) {
                    ACDLMConnector.this.alphaDlmDb.scanBuffer(cArr, i, 0, i, 0, i2, z, false);
                }
                return false;
            }

            @Override // com.nuance.connect.api.DLMConnector.DlmEventCallback
            public void scanBegin(String str, String str2, String str3, int i, int i2) {
                if (ACDLMConnector.this.alphaDlmDb != null) {
                    ACDLMConnector.this.alphaDlmDb.scanBegin(str, str2, str3, i, i2);
                }
            }

            @Override // com.nuance.connect.api.DLMConnector.DlmEventCallback
            public boolean scanCategory(int i, char[] cArr, int i2, int i3, boolean z) {
                if (ACDLMConnector.this.alphaDlmDb != null) {
                    ACDLMConnector.this.alphaDlmDb.scanBuffer(cArr, i2, 0, i2, 0, i3, z, false);
                }
                return false;
            }

            @Override // com.nuance.connect.api.DLMConnector.DlmEventCallback
            public void scanEnd() {
                if (ACDLMConnector.this.alphaDlmDb != null) {
                    ACDLMConnector.this.alphaDlmDb.scanEnd();
                }
            }
        });
        return this.alphaDlmEventCallback;
    }

    public final ACChineseDlmEventCallback bindChineseDlm(ACChineseDlmDb aCChineseDlmDb) throws ACException {
        if (this.chineseDlmEventCallback != null) {
            throw new ACException(115, "The Chinese DLM you are attempting to bind is already bound.");
        }
        if (!this.manager.getConnect().isLicensed()) {
            throw new ACException(129, "This SDK is unlicensed.");
        }
        this.chineseDlmDb = aCChineseDlmDb;
        this.chineseDlmEventCallback = new ACChineseDlmEventCallback();
        this.service.registerDlmCallback(3, new DLMConnector.DlmEventCallback() { // from class: com.nuance.swypeconnect.ac.ACDLMConnector.3
            @Override // com.nuance.connect.api.DLMConnector.DlmEventCallback
            public boolean backupDlm() {
                if (ACDLMConnector.this.chineseDlmDb == null) {
                    return false;
                }
                ACDLMConnector.this.chineseDlmDb.exportAsEvents(true, 0);
                return true;
            }

            @Override // com.nuance.connect.api.DLMConnector.DlmEventCallback
            public boolean processDlmDelete(int i) {
                if (ACDLMConnector.this.chineseDlmDb == null) {
                    return false;
                }
                ACDLMConnector.this.chineseDlmDb.deleteCategory(i);
                return true;
            }

            @Override // com.nuance.connect.api.DLMConnector.DlmEventCallback
            public boolean processDlmDelete(int i, int i2) {
                if (ACDLMConnector.this.chineseDlmDb == null) {
                    return false;
                }
                ACDLMConnector.this.chineseDlmDb.deleteCategoryLanguage(i, i2);
                return true;
            }

            @Override // com.nuance.connect.api.DLMConnector.DlmEventCallback
            public boolean processEvent(String str) {
                byte[] bArr = null;
                try {
                    bArr = Base64.decode(str, 2);
                } catch (Exception e) {
                }
                if (ACDLMConnector.this.chineseDlmDb == null || bArr == null) {
                    return false;
                }
                ACDLMConnector.this.chineseDlmDb.processEvent(bArr);
                return true;
            }

            @Override // com.nuance.connect.api.DLMConnector.DlmEventCallback
            public boolean scan(char[] cArr, int i, int i2, boolean z) {
                return false;
            }

            @Override // com.nuance.connect.api.DLMConnector.DlmEventCallback
            public void scanBegin(String str, String str2, String str3, int i, int i2) {
            }

            @Override // com.nuance.connect.api.DLMConnector.DlmEventCallback
            public boolean scanCategory(int i, char[] cArr, int i2, int i3, boolean z) {
                return false;
            }

            @Override // com.nuance.connect.api.DLMConnector.DlmEventCallback
            public void scanEnd() {
            }
        });
        return this.chineseDlmEventCallback;
    }

    public final ACKoreanDlmEventCallback bindKoreanDlm(ACKoreanDlmDb aCKoreanDlmDb) throws ACException {
        if (this.koreanDlmEventCallback != null) {
            throw new ACException(115, "The Korean DLM you are attempting to bind is already bound.");
        }
        if (!this.manager.getConnect().isLicensed()) {
            throw new ACException(129, "This SDK is unlicensed.");
        }
        this.koreanDlmDb = aCKoreanDlmDb;
        this.koreanDlmEventCallback = new ACKoreanDlmEventCallback();
        this.service.registerDlmCallback(2, new DLMConnector.DlmEventCallback() { // from class: com.nuance.swypeconnect.ac.ACDLMConnector.2
            @Override // com.nuance.connect.api.DLMConnector.DlmEventCallback
            public boolean backupDlm() {
                if (ACDLMConnector.this.koreanDlmDb == null) {
                    return false;
                }
                ACDLMConnector.this.koreanDlmDb.exportAsEvents(true, 0);
                return true;
            }

            @Override // com.nuance.connect.api.DLMConnector.DlmEventCallback
            public boolean processDlmDelete(int i) {
                if (ACDLMConnector.this.koreanDlmDb == null) {
                    return false;
                }
                ACDLMConnector.this.koreanDlmDb.deleteCategory(i);
                return true;
            }

            @Override // com.nuance.connect.api.DLMConnector.DlmEventCallback
            public boolean processDlmDelete(int i, int i2) {
                if (ACDLMConnector.this.koreanDlmDb == null) {
                    return false;
                }
                ACDLMConnector.this.koreanDlmDb.deleteCategoryLanguage(i, i2);
                return true;
            }

            @Override // com.nuance.connect.api.DLMConnector.DlmEventCallback
            public boolean processEvent(String str) {
                byte[] bArr = null;
                try {
                    bArr = Base64.decode(str, 2);
                } catch (Exception e) {
                }
                if (ACDLMConnector.this.koreanDlmDb == null || bArr == null) {
                    return false;
                }
                ACDLMConnector.this.koreanDlmDb.processEvent(bArr);
                return true;
            }

            @Override // com.nuance.connect.api.DLMConnector.DlmEventCallback
            public boolean scan(char[] cArr, int i, int i2, boolean z) {
                if (ACDLMConnector.this.koreanDlmDb != null) {
                    ACDLMConnector.this.koreanDlmDb.scanBuffer(cArr, i, i2, z, false);
                }
                return false;
            }

            @Override // com.nuance.connect.api.DLMConnector.DlmEventCallback
            public void scanBegin(String str, String str2, String str3, int i, int i2) {
            }

            @Override // com.nuance.connect.api.DLMConnector.DlmEventCallback
            public boolean scanCategory(int i, char[] cArr, int i2, int i3, boolean z) {
                if (ACDLMConnector.this.koreanDlmDb != null) {
                    ACDLMConnector.this.koreanDlmDb.scanBuffer(cArr, i2, i3, z, false);
                }
                return false;
            }

            @Override // com.nuance.connect.api.DLMConnector.DlmEventCallback
            public void scanEnd() {
            }
        });
        return this.koreanDlmEventCallback;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final DLMConnector getConnector() {
        return this.service;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.nuance.swypeconnect.ac.ACService
    public final String getName() {
        return "DLM";
    }

    public final void releaseAlphaDlm() {
        this.alphaDlmDb = null;
        this.alphaDlmEventCallback = null;
        this.service.unregisterDlmCallback(1);
    }

    public final void releaseChineseDlm() {
        this.chineseDlmDb = null;
        this.chineseDlmEventCallback = null;
        this.service.unregisterDlmCallback(3);
    }

    public final void releaseKoreanDlm() {
        this.koreanDlmDb = null;
        this.koreanDlmEventCallback = null;
        this.service.unregisterDlmCallback(2);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.nuance.swypeconnect.ac.ACService
    public final boolean requiresDocument(int i) {
        return false;
    }

    public final void setDLMSize(int i, int i2, int i3) throws ACException {
        if (!this.manager.getConnect().isLicensed()) {
            throw new ACException(129, "This SDK is unlicensed.");
        }
        if (i2 < 0 || i3 < 0) {
            throw new ACException(122, "Reserved space and value cannot be negative.");
        }
        if (i2 < i3) {
            throw new ACException(122, "Reserved space cannot be greater than the DLM size.");
        }
        if (!this.service.isSupportedCore(i)) {
            throw new ACException(123, i + " is not a valid DLM core");
        }
        this.service.setDLMInfo(new DLMConnector.DLMInfo(i, i2, i3));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.nuance.swypeconnect.ac.ACService
    public final void shutdown() {
        this.isShutdown = true;
        releaseAlphaDlm();
        releaseChineseDlm();
        releaseKoreanDlm();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.nuance.swypeconnect.ac.ACService
    public final void start() {
        this.isShutdown = false;
    }
}
