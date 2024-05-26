package com.nuance.swypeconnect.ac;

import android.annotation.SuppressLint;
import com.nuance.connect.api.DLMConnector;
import com.nuance.connect.common.FeaturesLastUsed;
import com.nuance.connect.compat.CompatUtil;
import com.nuance.connect.util.Logger;
import com.nuance.swypeconnect.ac.ACScannerService;
import com.nuance.swypeconnect.ac.ACScannerSms;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
public class ACScannerGmail2 extends ACScanner {
    private static final int GMAIL_MAX_DEFAULT = 100;
    public static final int REASON_EMAIL_NOT_AVAILABLE = 2;
    private static final String SCANNER_GMAIL_LAST_RUN_CALENDAR = "SCANNER_GMAIL_LAST_RUN_CALENDAR";
    private String accountToken;
    private String applicationName;
    private String gmailAccount;
    private GmailCompat gmailCompat;
    private final ArrayList<ACScannerGmailContentType> scanContentTypes;
    private final ArrayList<ACScannerGmailType> scanTypes;
    private static final Logger.Log log = Logger.getLog(Logger.LoggerType.DEVELOPER, ACScannerGmail2.class.getSimpleName());
    static final ACScannerService.ScannerType TYPE = ACScannerService.ScannerType.GMAIL2;

    /* loaded from: classes.dex */
    public enum ACScannerGmailContentType {
        SUBJECT("Subject"),
        BODY("Body"),
        TO("To"),
        CC("Cc"),
        FROM("From");

        private String keyName;

        ACScannerGmailContentType(String str) {
            this.keyName = str;
        }
    }

    /* loaded from: classes.dex */
    public enum ACScannerGmailType {
        SENT(ACScannerSms.SMSReader.SENT),
        INBOX(ACScannerSms.SMSReader.INBOX);

        private String keyName;

        ACScannerGmailType(String str) {
            this.keyName = str;
        }
    }

    /* loaded from: classes.dex */
    public static class GmailCompat {
        private static final String ANDROID_HTTP_TRANSPORT_CLASS = "com.google.api.client.extensions.android.http.AndroidHttp";
        private static final String GMAIL_BUILDER_CLASS = "com.google.api.services.gmail.Gmail$Builder";
        private static final String GMAIL_CLASS = "com.google.api.services.gmail.Gmail";
        private static final String GMAIL_MESSAGE_CLASS = "com.google.api.services.gmail.model.Message";
        private static final String GMAIL_MESSAGE_PART = "com.google.api.services.gmail.model.MessagePart";
        private static final String GMAIL_MESSAGE_PART_BODY = "com.google.api.services.gmail.model.MessagePartBody";
        private static final String GMAIL_MESSAGE_PART_HEADER = "com.google.api.services.gmail.model.MessagePartHeader";
        private static final String GMAIL_USERS_CLASS = "com.google.api.services.gmail.Gmail$Users";
        private static final String GMAIL_USERS_MESSAGES_CLASS = "com.google.api.services.gmail.Gmail$Users$Messages";
        private static final String GMAIL_USERS_MESSAGES_GET_CLASS = "com.google.api.services.gmail.Gmail$Users$Messages$Get";
        private static final String GMAIL_USERS_MESSAGES_LIST_CLASS = "com.google.api.services.gmail.Gmail$Users$Messages$List";
        private static final String GOOGLE_CREDENTIAL_CLASS = "com.google.api.client.googleapis.auth.oauth2.GoogleCredential";
        private static final String HTTP_REQUEST_INITIALIZER_CLASS = "com.google.api.client.http.HttpRequestInitializer";
        private static final String HTTP_TRANSPORT_CLASS = "com.google.api.client.http.HttpTransport";
        private static final String JACKSON_CLASS = "com.google.api.client.json.jackson2.JacksonFactory";
        private static final String JSON_CLASS = "com.google.api.client.json.JsonFactory";
        private static final String LIST_MESSAGES_RESPONSE_CLASS = "com.google.api.services.gmail.model.ListMessagesResponse";
        private static final String OR = " OR ";
        private String accountToken;
        private Object gmailBuilder;
        private Object gmailService;
        private Calendar lastScan;
        Class<?> JsonClass = CompatUtil.getClass(JSON_CLASS);
        Class<?> HttpTransportClass = CompatUtil.getClass(HTTP_TRANSPORT_CLASS);
        Class<?> HttpRequestInitializerClass = CompatUtil.getClass(HTTP_REQUEST_INITIALIZER_CLASS);
        private String gmailAccount = "me";
        private final ArrayList<ACScannerGmailType> scanTypes = new ArrayList<>(Arrays.asList(ACScannerGmailType.SENT));
        private final ArrayList<ACScannerGmailContentType> scanContentTypes = new ArrayList<>(Arrays.asList(ACScannerGmailContentType.TO, ACScannerGmailContentType.SUBJECT, ACScannerGmailContentType.BODY));
        private int maxCount = 100;
        private String applicationName = "SCSDK-GMail-Scanner/2.0";
        private Object jsonFactory = CompatUtil.invoke(CompatUtil.getMethod(JACKSON_CLASS, "getDefaultInstance", (Class<?>[]) null), null, null);
        private Object httpTransport = CompatUtil.invoke(CompatUtil.getMethod(ANDROID_HTTP_TRANSPORT_CLASS, "newCompatibleTransport", (Class<?>[]) null), null, null);
        private Object credential = CompatUtil.newInstance(GOOGLE_CREDENTIAL_CLASS);

        GmailCompat(String str) {
            this.accountToken = str;
            CompatUtil.invoke(CompatUtil.getMethod(GOOGLE_CREDENTIAL_CLASS, "setAccessToken", (Class<?>[]) new Class[]{String.class}), this.credential, this.accountToken);
            this.gmailBuilder = CompatUtil.newInstance(CompatUtil.getConstructor(GMAIL_BUILDER_CLASS, (Class<?>[]) new Class[]{this.HttpTransportClass, this.JsonClass, this.HttpRequestInitializerClass}), this.httpTransport, this.jsonFactory, this.credential);
            CompatUtil.invoke(CompatUtil.getMethod(GMAIL_BUILDER_CLASS, "setApplicationName", (Class<?>[]) new Class[]{String.class}), this.gmailBuilder, this.applicationName);
            this.gmailService = CompatUtil.invoke(CompatUtil.getMethod(GMAIL_BUILDER_CLASS, "build", (Class<?>[]) null), this.gmailBuilder, null);
        }

        @SuppressLint({"SimpleDateFormat"})
        private String buildQueryString(List<ACScannerGmailType> list, Calendar calendar) {
            StringBuilder sb = new StringBuilder();
            StringBuilder sb2 = new StringBuilder();
            for (ACScannerGmailType aCScannerGmailType : list) {
                if (sb2.length() > 0) {
                    sb2.append(OR);
                }
                sb2.append(aCScannerGmailType.keyName);
            }
            if (sb2.length() > 0) {
                sb.append("in:(" + sb2.toString() + ")");
            }
            if (calendar != null) {
                sb.append(" after:").append(new SimpleDateFormat("yyyy/MM/dd H:m:s ZZZZ").format(calendar.getTime()));
            }
            return sb.toString();
        }

        private String extractPlainName(String str) {
            if (str.indexOf("<") > 0) {
                String trim = str.substring(0, str.indexOf("<")).trim();
                if (trim.length() > 0) {
                    return trim;
                }
            }
            return null;
        }

        private SimpleMessage getMessage(String str, Object obj) throws IOException {
            Object invoke;
            byte[] bArr;
            SimpleMessage simpleMessage = new SimpleMessage();
            Object invoke2 = CompatUtil.invoke(CompatUtil.getMethod(GMAIL_MESSAGE_CLASS, "getPayload", (Class<?>[]) null), CompatUtil.invoke(CompatUtil.getMethod(GMAIL_USERS_MESSAGES_GET_CLASS, "execute", (Class<?>[]) null), CompatUtil.invoke(CompatUtil.getMethod(GMAIL_USERS_MESSAGES_CLASS, "get", (Class<?>[]) new Class[]{String.class, String.class}), obj, this.gmailAccount, str), null), null);
            Method method = CompatUtil.getMethod(GMAIL_MESSAGE_PART, "getHeaders", (Class<?>[]) null);
            Method method2 = CompatUtil.getMethod(GMAIL_MESSAGE_PART_HEADER, "getName", (Class<?>[]) null);
            Method method3 = CompatUtil.getMethod(GMAIL_MESSAGE_PART_HEADER, "getValue", (Class<?>[]) null);
            Method method4 = CompatUtil.getMethod(GMAIL_MESSAGE_PART, "getParts", (Class<?>[]) null);
            Method method5 = CompatUtil.getMethod(GMAIL_MESSAGE_PART, "containsKey", (Class<?>[]) new Class[]{Object.class});
            Method method6 = CompatUtil.getMethod(GMAIL_MESSAGE_PART, "get", (Class<?>[]) new Class[]{Object.class});
            Method method7 = CompatUtil.getMethod(GMAIL_MESSAGE_PART, "getBody", (Class<?>[]) null);
            Method method8 = CompatUtil.getMethod(GMAIL_MESSAGE_PART_BODY, "decodeData", (Class<?>[]) null);
            List list = (List) CompatUtil.invoke(method, invoke2, null);
            if (list != null) {
                for (Object obj2 : list) {
                    if (((String) CompatUtil.invoke(method2, obj2, null)).equals(ACScannerGmailContentType.SUBJECT.keyName)) {
                        simpleMessage.subject = (String) CompatUtil.invoke(method3, obj2, null);
                    }
                    if (((String) CompatUtil.invoke(method2, obj2, null)).equals(ACScannerGmailContentType.FROM.keyName)) {
                        simpleMessage.from = extractPlainName((String) CompatUtil.invoke(method3, obj2, null));
                    }
                    if (((String) CompatUtil.invoke(method2, obj2, null)).equals(ACScannerGmailContentType.TO.keyName)) {
                        simpleMessage.to = extractPlainName((String) CompatUtil.invoke(method3, obj2, null));
                    }
                    if (((String) CompatUtil.invoke(method2, obj2, null)).equals(ACScannerGmailContentType.CC.keyName)) {
                        simpleMessage.cc = extractPlainName((String) CompatUtil.invoke(method3, obj2, null));
                    }
                }
            }
            List list2 = (List) CompatUtil.invoke(method4, invoke2, null);
            if (list2 != null) {
                for (Object obj3 : list2) {
                    if (((Boolean) CompatUtil.invoke(method5, obj3, "mimeType")).booleanValue() && ((String) CompatUtil.invoke(method6, obj3, "mimeType")).equals("text/plain") && (invoke = CompatUtil.invoke(method7, obj3, null)) != null && (bArr = (byte[]) CompatUtil.invoke(method8, invoke, null)) != null) {
                        simpleMessage.body = new String(bArr, "UTF-8");
                    }
                }
            }
            return simpleMessage;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public List<SimpleMessage> readMessages() throws IOException {
            String buildQueryString = buildQueryString(this.scanTypes, this.lastScan);
            Object invoke = CompatUtil.invoke(CompatUtil.getMethod(GMAIL_USERS_CLASS, "messages", (Class<?>[]) null), CompatUtil.invoke(CompatUtil.getMethod(GMAIL_CLASS, "users", (Class<?>[]) null), this.gmailService, null), null);
            Object invoke2 = CompatUtil.invoke(CompatUtil.getMethod(GMAIL_USERS_MESSAGES_CLASS, "list", (Class<?>[]) new Class[]{String.class}), invoke, this.gmailAccount);
            CompatUtil.invoke(CompatUtil.getMethod(GMAIL_USERS_MESSAGES_LIST_CLASS, "setQ", (Class<?>[]) new Class[]{String.class}), invoke2, buildQueryString);
            Method method = CompatUtil.getMethod(GMAIL_USERS_MESSAGES_LIST_CLASS, "execute", (Class<?>[]) null);
            Object invoke3 = CompatUtil.invoke(method, invoke2, null);
            ArrayList arrayList = new ArrayList();
            Method method2 = CompatUtil.getMethod(LIST_MESSAGES_RESPONSE_CLASS, "getMessages", (Class<?>[]) null);
            Method method3 = CompatUtil.getMethod(GMAIL_MESSAGE_CLASS, "getId", (Class<?>[]) null);
            Method method4 = CompatUtil.getMethod(LIST_MESSAGES_RESPONSE_CLASS, "getNextPageToken", (Class<?>[]) null);
            Method method5 = CompatUtil.getMethod(GMAIL_USERS_MESSAGES_LIST_CLASS, "setPageToken", (Class<?>[]) new Class[]{String.class});
            Object obj = invoke3;
            List list = (List) CompatUtil.invoke(method2, invoke3, null);
            while (list != null && arrayList.size() < this.maxCount) {
                Iterator it = list.iterator();
                while (it.hasNext()) {
                    SimpleMessage message = getMessage((String) CompatUtil.invoke(method3, it.next(), null), invoke);
                    if ((this.scanContentTypes.contains(ACScannerGmailContentType.SUBJECT) && message.subject != null) || ((this.scanContentTypes.contains(ACScannerGmailContentType.BODY) && message.body != null) || ((this.scanContentTypes.contains(ACScannerGmailContentType.TO) && message.to != null) || ((this.scanContentTypes.contains(ACScannerGmailContentType.FROM) && message.from != null) || (this.scanContentTypes.contains(ACScannerGmailContentType.CC) && message.cc != null))))) {
                        arrayList.add(message);
                        if (arrayList.size() >= this.maxCount) {
                            break;
                        }
                    }
                }
                if (arrayList.size() < this.maxCount) {
                    String str = (String) CompatUtil.invoke(method4, obj, null);
                    if (str == null) {
                        break;
                    }
                    CompatUtil.invoke(method5, invoke2, str);
                    Object invoke4 = CompatUtil.invoke(method, invoke2, null);
                    obj = invoke4;
                    list = (List) CompatUtil.invoke(method2, invoke4, null);
                }
            }
            return arrayList;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setApplicationName(String str) {
            this.applicationName = str;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setGmailAccount(String str) {
            this.gmailAccount = str;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setLastScan(Calendar calendar) {
            this.lastScan = calendar;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setMaxCount(int i) {
            this.maxCount = i;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setScanContentTypes(List<ACScannerGmailContentType> list) {
            this.scanContentTypes.clear();
            this.scanContentTypes.addAll(list);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setScanTypes(List<ACScannerGmailType> list) {
            this.scanTypes.clear();
            this.scanTypes.addAll(list);
        }

        Object getGmailService() {
            return this.gmailService;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class SimpleMessage {
        public String body;
        public String cc;
        public String from;
        public String subject;
        public String to;

        private SimpleMessage() {
        }

        public String toString() {
            return "SimpleMessage\nFrom: " + this.from + "\nTo: " + this.to + "\nCC: " + this.cc + "\nSubject: " + this.subject + "\n" + this.body;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ACScannerGmail2(ACScannerService aCScannerService) {
        super(aCScannerService, SCANNER_GMAIL_LAST_RUN_CALENDAR);
        this.scanTypes = new ArrayList<>();
        this.scanContentTypes = new ArrayList<>();
        this.applicationName = "SCSDK-GMail-Scanner/2.0";
        this.maxToProcess = 100;
        setScanType(new ACScannerGmailType[]{ACScannerGmailType.SENT});
        setScanContentType(new ACScannerGmailContentType[]{ACScannerGmailContentType.SUBJECT, ACScannerGmailContentType.BODY, ACScannerGmailContentType.TO});
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean checkDependencies() {
        return (((((((((((((((CompatUtil.doesClassExist("com.google.api.client.json.jackson2.JacksonFactory").booleanValue() && CompatUtil.doesClassExist("com.google.api.client.http.HttpTransport").booleanValue()) && CompatUtil.doesClassExist("com.google.api.client.json.JsonFactory").booleanValue()) && CompatUtil.doesClassExist("com.google.api.client.extensions.android.http.AndroidHttp").booleanValue()) && CompatUtil.doesClassExist("com.google.api.services.gmail.Gmail$Builder").booleanValue()) && CompatUtil.doesClassExist("com.google.api.services.gmail.Gmail").booleanValue()) && CompatUtil.doesClassExist("com.google.api.client.googleapis.auth.oauth2.GoogleCredential").booleanValue()) && CompatUtil.doesClassExist("com.google.api.client.http.HttpRequestInitializer").booleanValue()) && CompatUtil.doesClassExist("com.google.api.services.gmail.Gmail$Users").booleanValue()) && CompatUtil.doesClassExist("com.google.api.services.gmail.Gmail$Users$Messages").booleanValue()) && CompatUtil.doesClassExist("com.google.api.services.gmail.Gmail$Users$Messages$Get").booleanValue()) && CompatUtil.doesClassExist("com.google.api.services.gmail.Gmail$Users$Messages$List").booleanValue()) && CompatUtil.doesClassExist("com.google.api.services.gmail.model.ListMessagesResponse").booleanValue()) && CompatUtil.doesClassExist("com.google.api.services.gmail.model.Message").booleanValue()) && CompatUtil.doesClassExist("com.google.api.services.gmail.model.MessagePart").booleanValue()) && CompatUtil.doesClassExist("com.google.api.services.gmail.model.MessagePartHeader").booleanValue()) && CompatUtil.doesClassExist("com.google.api.services.gmail.model.MessagePartBody").booleanValue();
    }

    private void clearCache() {
        File cacheDir = this.service.getContext().getCacheDir();
        if (cacheDir == null) {
            return;
        }
        log.d("clearing cache in " + cacheDir);
        File[] listFiles = cacheDir.listFiles(new FilenameFilter() { // from class: com.nuance.swypeconnect.ac.ACScannerGmail2.1
            @Override // java.io.FilenameFilter
            public boolean accept(File file, String str) {
                return str.startsWith("body");
            }
        });
        if (listFiles != null) {
            for (File file : listFiles) {
                log.v("deleting " + file);
                if (!file.delete()) {
                    log.w("error deleting " + file);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.nuance.swypeconnect.ac.ACScanner
    public void fail(int i, String str) {
        if (this.callback != null) {
            this.callback.onFailure(i, str);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.nuance.swypeconnect.ac.ACScanner
    public ACScannerService.ScannerType getType() {
        return TYPE;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.nuance.swypeconnect.ac.ACScanner
    public void scan() {
        String str;
        int i;
        boolean z = true;
        this.service.getManager().getConnect().updateFeatureLastUsed(FeaturesLastUsed.Feature.SCANNER_GMAIL, System.currentTimeMillis());
        if (this.accountToken == null) {
            fail(1, "Scan of Gmail failed, no access token.");
            return;
        }
        startScan();
        log.d("start gmail scanning");
        DLMConnector.ScannerBucket bucket = getBucket(this.scanCoreList, 1, false);
        try {
            try {
                for (SimpleMessage simpleMessage : this.gmailCompat.readMessages()) {
                    if (this.scanContentTypes.contains(ACScannerGmailContentType.SUBJECT) && simpleMessage.subject != null) {
                        bucket.scan(simpleMessage.subject);
                    }
                    if (this.scanContentTypes.contains(ACScannerGmailContentType.BODY) && simpleMessage.body != null) {
                        bucket.scan(simpleMessage.body);
                    }
                    if (this.scanContentTypes.contains(ACScannerGmailContentType.FROM) && simpleMessage.from != null) {
                        bucket.scan(simpleMessage.from);
                    }
                    if (this.scanContentTypes.contains(ACScannerGmailContentType.TO) && simpleMessage.to != null) {
                        bucket.scan(simpleMessage.to);
                    }
                    if (this.scanContentTypes.contains(ACScannerGmailContentType.CC) && simpleMessage.cc != null) {
                        bucket.scan(simpleMessage.cc);
                    }
                    if (simpleMessage.subject != null || simpleMessage.body != null) {
                        this.currentProcess++;
                        if (this.currentProcess >= this.maxToProcess) {
                            break;
                        }
                    }
                }
                str = "Scan of Gmail failed, likely due to invalid consumer or access tokens.";
                i = 1;
            } catch (IOException e) {
                str = "Exception accessing GMail messages. " + e.getMessage();
                i = 2;
                z = false;
            } catch (RuntimeException e2) {
                str = "Exception accessing GMail messages. " + e2.getMessage();
                i = 2;
                z = false;
            }
            clearCache();
            bucket.close();
            log.d("end Gmail scanning. success: ", Boolean.valueOf(z));
            endScan(z);
            if (z) {
                return;
            }
            fail(i, str);
        } catch (Throwable th) {
            bucket.close();
            throw th;
        }
    }

    public void setAccount(String str, String str2) throws ACScannerException {
        if (str == null || str2 == null) {
            throw new ACScannerException(122, "A GMail email address and OAuth token must be provided before continuing.");
        }
        this.gmailAccount = str;
        this.accountToken = str2;
    }

    public void setApplicationName(String str) {
        this.applicationName = str;
    }

    @Override // com.nuance.swypeconnect.ac.ACScanner
    public void setMaxToProcess(int i) throws ACScannerException {
        Logger.getLog(Logger.LoggerType.CUSTOMER).d("Gmail scanner set to scan at most ", Integer.valueOf(i), " records.");
        if (i > 100) {
            Logger.getLog(Logger.LoggerType.CUSTOMER).w("Gmail scanner max records exceeds the recommended number of records.  This may cause performance problems.");
        }
        super.setMaxToProcess(i);
    }

    public void setScanContentType(ACScannerGmailContentType[] aCScannerGmailContentTypeArr) {
        synchronized (this.scanContentTypes) {
            this.scanContentTypes.clear();
            if (aCScannerGmailContentTypeArr != null) {
                this.scanContentTypes.addAll(Arrays.asList(aCScannerGmailContentTypeArr));
            }
        }
    }

    public void setScanType(ACScannerGmailType[] aCScannerGmailTypeArr) {
        synchronized (this.scanTypes) {
            this.scanTypes.clear();
            if (aCScannerGmailTypeArr != null) {
                this.scanTypes.addAll(Arrays.asList(aCScannerGmailTypeArr));
            }
        }
    }

    @Override // com.nuance.swypeconnect.ac.ACScanner
    public void start(ACScannerService.ACScannerCallback aCScannerCallback) throws ACScannerException {
        if (this.scanTypes.size() == 0) {
            throw new ACScannerException(101);
        }
        if (this.gmailAccount == null || this.accountToken == null) {
            throw new ACScannerException(122, "A GMail email address and OAuth token must be provided before continuing.");
        }
        this.gmailCompat = new GmailCompat(this.accountToken);
        this.gmailCompat.setGmailAccount(this.gmailAccount);
        this.gmailCompat.setLastScan(getLastRun());
        this.gmailCompat.setScanTypes(this.scanTypes);
        this.gmailCompat.setScanContentTypes(this.scanContentTypes);
        this.gmailCompat.setApplicationName(this.applicationName);
        this.gmailCompat.setMaxCount(getMaxToProcess());
        super.start(aCScannerCallback);
        this.callback = aCScannerCallback;
        this.service.scheduleScan(this);
    }
}
