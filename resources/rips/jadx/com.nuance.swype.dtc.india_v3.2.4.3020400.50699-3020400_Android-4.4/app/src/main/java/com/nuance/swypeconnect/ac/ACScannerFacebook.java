package com.nuance.swypeconnect.ac;

import android.os.Bundle;
import android.text.TextUtils;
import com.facebook.GraphRequest;
import com.facebook.internal.ServerProtocol;
import com.facebook.share.internal.ShareConstants;
import com.nuance.android.compat.CorrectionSpan;
import com.nuance.connect.api.DLMConnector;
import com.nuance.connect.util.Logger;
import com.nuance.swypeconnect.ac.ACScannerService;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public final class ACScannerFacebook extends ACScanner {
    private static final String EDGE_ALBUMS = "albums";
    private static final String EDGE_NONE = "edge_none";
    private static final String EDGE_PHOTOS = "photos";
    private static final String EDGE_POSTS = "posts";
    private static final String EDGE_PUBLIC_FRIENDS = "friends";
    private static final String EDGE_TAGGABLE_FRIENDS = "taggable_friends";
    private static final String EDGE_TAGGED_PLACES = "tagged_places";
    private static final String ENCODING = "UTF-8";
    private static final int FACEBOOK_MAX_DEFAULT = 100;
    public static final int HTTP_FAILURE = 1100;
    public static final int PARSE_RESPOSE_FAILURE = 1101;
    public static final String PERMISSION_EDUCATION_HISTORY = "user_education_history";
    public static final String PERMISSION_FRIENDS = "user_friends";
    public static final String PERMISSION_LIKES = "user_likes";
    public static final String PERMISSION_PHOTOS = "user_photos";
    public static final String PERMISSION_POSTS = "user_posts";
    public static final String PERMISSION_TAGGED_PLACES = "user_tagged_places";
    public static final String PERMISSION_WORK_HISTORY = "user_work_history";
    private static final String SCANNER_FACEBOOK_LAST_RUN_CALENDAR = "SCANNER_FACEBOOK_LAST_RUN_CALENDAR";
    private long accessExpires;
    private String accessToken;
    private String appId;
    private final List<String> ignore;
    private final TreeSet<ACFacebookScannerType> scanTypes;
    private final ACScannerService service;
    private static final Logger.Log log = Logger.getLog(Logger.LoggerType.DEVELOPER, ACScannerFacebook.class.getSimpleName());
    static final ACScannerService.ScannerType TYPE = ACScannerService.ScannerType.FACEBOOK;

    /* loaded from: classes.dex */
    public enum ACFacebookScannerType {
        POSTS_TEXT(ACScannerFacebook.EDGE_POSTS, "message", new String[]{ACScannerFacebook.PERMISSION_POSTS}, true),
        POSTS_LIKES(ACScannerFacebook.EDGE_POSTS, "likes{name}", new String[]{ACScannerFacebook.PERMISSION_POSTS, ACScannerFacebook.PERMISSION_FRIENDS}, true),
        TAGGABLE_FRIENDS(ACScannerFacebook.EDGE_TAGGABLE_FRIENDS, "name", new String[]{ACScannerFacebook.PERMISSION_FRIENDS}, false),
        SHARED_FRIENDS("friends", "name", new String[]{ACScannerFacebook.PERMISSION_FRIENDS}, false),
        EDUCATION(ACScannerFacebook.EDGE_NONE, "education{school{name}}", new String[]{ACScannerFacebook.PERMISSION_EDUCATION_HISTORY}, false),
        WORK(ACScannerFacebook.EDGE_NONE, "work{employer{name}}", new String[]{ACScannerFacebook.PERMISSION_WORK_HISTORY}, false),
        FAVORITE_TEAMS(ACScannerFacebook.EDGE_NONE, "favorite_teams{name}", new String[]{ACScannerFacebook.PERMISSION_LIKES}, false),
        FAVORITE_ATHLETES(ACScannerFacebook.EDGE_NONE, "favorite_athletes{name}", new String[]{ACScannerFacebook.PERMISSION_LIKES}, false),
        TAGGED_PLACES(ACScannerFacebook.EDGE_TAGGED_PLACES, "place", new String[]{ACScannerFacebook.PERMISSION_TAGGED_PLACES}, false),
        PHOTO_ALBUM_NAME(ACScannerFacebook.EDGE_ALBUMS, "name", new String[]{ACScannerFacebook.PERMISSION_PHOTOS}, false),
        PHOTO_NAME(ACScannerFacebook.EDGE_PHOTOS, "name", new String[]{ACScannerFacebook.PERMISSION_PHOTOS}, false),
        PHOTO_TAGGED_PEOPLE(ACScannerFacebook.EDGE_PHOTOS, "tags{name}", new String[]{ACScannerFacebook.PERMISSION_PHOTOS, ACScannerFacebook.PERMISSION_FRIENDS}, false),
        PHOTO_LIKES(ACScannerFacebook.EDGE_PHOTOS, "likes{name}", new String[]{ACScannerFacebook.PERMISSION_PHOTOS, ACScannerFacebook.PERMISSION_FRIENDS}, false);

        String edge;
        String field;
        String[] permissions;
        boolean timeBased;

        ACFacebookScannerType(String str, String str2, String[] strArr, boolean z) {
            this.edge = str;
            this.field = str2;
            this.permissions = strArr;
            this.timeBased = z;
        }

        public final List<String> getPermissions() {
            return Arrays.asList(this.permissions);
        }

        public final boolean isTimeBased() {
            return this.timeBased;
        }
    }

    /* loaded from: classes.dex */
    private static final class FacebookApi {
        public static final String CANCEL_URI = "fbconnect://cancel";
        private static final String DIALOG_BASE_URL = "https://m.facebook.com/v2.7/dialog/";
        public static final String EXPIRES = "expires_in";
        private static final String FACEBOOK_API_VERSION = "2.7";
        public static final int FORCE_DIALOG_AUTH = -1;
        private static final String GRAPH_BASE_URL = "https://graph.facebook.com/v2.7/";
        public static final String LOGIN = "oauth";
        public static final String REDIRECT_URI = "fbconnect://success";
        private static final String RESTSERVER_URL = "https://api.facebook.com/restserver.php";
        public static final String SINGLE_SIGN_ON_DISABLED = "service_disabled";
        public static final String TOKEN = "access_token";
        private String mAppId;
        private String mAccessToken = null;
        private long mAccessExpires = 0;

        public FacebookApi(String str) {
            if (str == null) {
                throw new IllegalArgumentException("You must specify your application ID when instantiating a Facebook object. See README for details.");
            }
            this.mAppId = str;
        }

        public final long getAccessExpires() {
            return this.mAccessExpires;
        }

        public final String getAccessToken() {
            return this.mAccessToken;
        }

        public final String getAuthorizationUrl(String[] strArr) {
            Bundle bundle = new Bundle();
            if (strArr != null && strArr.length > 0) {
                bundle.putString("scope", TextUtils.join(",", strArr));
            }
            bundle.putString(ServerProtocol.DIALOG_PARAM_DISPLAY, "touch");
            bundle.putString(ServerProtocol.DIALOG_PARAM_REDIRECT_URI, "fbconnect://success");
            bundle.putString("type", "user_agent");
            bundle.putString("client_id", this.mAppId);
            if (isSessionValid()) {
                bundle.putString("access_token", getAccessToken());
            }
            return "https://m.facebook.com/v2.7/dialog/oauth?" + FacebookUtil.encodeUrl(bundle);
        }

        public final boolean isSessionValid() {
            return getAccessToken() != null && (getAccessExpires() == 0 || System.currentTimeMillis() < getAccessExpires());
        }

        public final String request(Bundle bundle) throws IOException {
            if (bundle.containsKey("method")) {
                return request(null, bundle, "GET");
            }
            throw new IllegalArgumentException("API method must be specified. (parameters must contain key \"method\" and value). See http://developers.facebook.com/docs/reference/rest/");
        }

        public final String request(String str) throws IOException {
            return request(str, new Bundle(), "GET");
        }

        public final String request(String str, Bundle bundle) throws IOException {
            return request(str, bundle, "GET");
        }

        public final String request(String str, Bundle bundle, String str2) throws IOException {
            bundle.putString("format", "json");
            if (isSessionValid()) {
                bundle.putString("access_token", getAccessToken());
            }
            String str3 = str != null ? GRAPH_BASE_URL + str : RESTSERVER_URL;
            ACScannerFacebook.log.d("request url: " + str3 + " method=" + str2 + " params=" + bundle);
            return FacebookUtil.openUrl(str3, str2, bundle);
        }

        public final void setAccessExpires(long j) {
            this.mAccessExpires = j;
        }

        public final void setAccessExpiresIn(String str) {
            if (str == null || str.equals("0")) {
                return;
            }
            setAccessExpires(System.currentTimeMillis() + (Integer.parseInt(str) * 1000));
        }

        public final void setAccessToken(String str) {
            this.mAccessToken = str;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class FacebookUtil {
        private FacebookUtil() {
        }

        public static String encodePostBody(Bundle bundle, String str) {
            if (bundle == null) {
                return "";
            }
            StringBuilder sb = new StringBuilder();
            for (String str2 : bundle.keySet()) {
                if (bundle.getByteArray(str2) == null) {
                    sb.append("Content-Disposition: form-data; name=\"");
                    sb.append(str2);
                    sb.append("\"\r\n\r\n");
                    sb.append(bundle.getString(str2));
                    sb.append("\r\n--").append(str).append("\r\n");
                }
            }
            return sb.toString();
        }

        public static String encodeUrl(Bundle bundle) {
            if (bundle == null) {
                return "";
            }
            StringBuilder sb = new StringBuilder();
            for (String str : bundle.keySet()) {
                if (sb.length() != 0) {
                    sb.append("&");
                }
                sb.append(urlEncodeUtf8(str));
                sb.append('=');
                sb.append(urlEncodeUtf8(bundle.getString(str)));
            }
            return sb.toString();
        }

        public static String openUrl(String str, String str2, Bundle bundle) throws IOException {
            BufferedOutputStream bufferedOutputStream;
            if (bundle == null) {
                bundle = new Bundle();
            }
            if (str2.equals("GET")) {
                if (bundle.containsKey(GraphRequest.FIELDS_PARAM)) {
                    String string = bundle.getString(GraphRequest.FIELDS_PARAM);
                    bundle.remove(GraphRequest.FIELDS_PARAM);
                    str = str + "?fields=" + string + "&" + encodeUrl(bundle);
                } else {
                    str = str + "?" + encodeUrl(bundle);
                }
            }
            new StringBuilder().append(str2).append(" URL: ").append(str);
            HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(str).openConnection();
            httpURLConnection.setRequestProperty("User-Agent", System.getProperties().getProperty("http.agent") + " FacebookAndroidSDK");
            if (!str2.equals("GET")) {
                Bundle bundle2 = new Bundle();
                for (String str3 : bundle.keySet()) {
                    if (bundle.getByteArray(str3) != null) {
                        bundle2.putByteArray(str3, bundle.getByteArray(str3));
                    }
                }
                if (!bundle.containsKey("method")) {
                    bundle.putString("method", str2);
                }
                if (bundle.containsKey("access_token")) {
                    bundle.putString("access_token", urlDecodeUtf8(bundle.getString("access_token")));
                }
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=3i2ndDfv2rTHiSisAbouNdArYfORhtTPEefj3q2f");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
                httpURLConnection.connect();
                BufferedOutputStream bufferedOutputStream2 = null;
                try {
                    bufferedOutputStream = new BufferedOutputStream(httpURLConnection.getOutputStream());
                } catch (Throwable th) {
                    th = th;
                }
                try {
                    bufferedOutputStream.write(("--3i2ndDfv2rTHiSisAbouNdArYfORhtTPEefj3q2f\r\n").getBytes(ACScannerFacebook.ENCODING));
                    bufferedOutputStream.write(encodePostBody(bundle, "3i2ndDfv2rTHiSisAbouNdArYfORhtTPEefj3q2f").getBytes(ACScannerFacebook.ENCODING));
                    bufferedOutputStream.write(("\r\n--3i2ndDfv2rTHiSisAbouNdArYfORhtTPEefj3q2f\r\n").getBytes(ACScannerFacebook.ENCODING));
                    if (!bundle2.isEmpty()) {
                        for (String str4 : bundle2.keySet()) {
                            bufferedOutputStream.write(("Content-Disposition: form-data; filename=\"" + str4 + "\"\r\n").getBytes(ACScannerFacebook.ENCODING));
                            bufferedOutputStream.write(("Content-Type: content/unknown\r\n\r\n").getBytes(ACScannerFacebook.ENCODING));
                            bufferedOutputStream.write(bundle2.getByteArray(str4));
                            bufferedOutputStream.write(("\r\n--3i2ndDfv2rTHiSisAbouNdArYfORhtTPEefj3q2f\r\n").getBytes(ACScannerFacebook.ENCODING));
                        }
                    }
                    bufferedOutputStream.flush();
                    bufferedOutputStream.close();
                } catch (Throwable th2) {
                    th = th2;
                    bufferedOutputStream2 = bufferedOutputStream;
                    if (bufferedOutputStream2 != null) {
                        bufferedOutputStream2.close();
                    }
                    throw th;
                }
            }
            try {
                return read(httpURLConnection.getInputStream());
            } catch (FileNotFoundException e) {
                return read(httpURLConnection.getErrorStream());
            }
        }

        private static String read(InputStream inputStream) throws IOException {
            StringBuilder sb = new StringBuilder();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, ACScannerFacebook.ENCODING), 1000);
            try {
                for (String readLine = bufferedReader.readLine(); readLine != null; readLine = bufferedReader.readLine()) {
                    sb.append(readLine);
                }
                bufferedReader.close();
                return sb.toString();
            } catch (Throwable th) {
                bufferedReader.close();
                throw th;
            }
        }

        private static String urlDecodeUtf8(String str) {
            try {
                return URLDecoder.decode(str, ACScannerFacebook.ENCODING);
            } catch (UnsupportedEncodingException e) {
                throw new IllegalStateException(e);
            }
        }

        private static String urlEncodeUtf8(String str) {
            try {
                return URLEncoder.encode(str, ACScannerFacebook.ENCODING);
            } catch (UnsupportedEncodingException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ACScannerFacebook(ACScannerService aCScannerService) {
        super(aCScannerService, SCANNER_FACEBOOK_LAST_RUN_CALENDAR);
        this.accessExpires = 0L;
        this.scanTypes = new TreeSet<>(new Comparator<ACFacebookScannerType>() { // from class: com.nuance.swypeconnect.ac.ACScannerFacebook.1
            @Override // java.util.Comparator
            public int compare(ACFacebookScannerType aCFacebookScannerType, ACFacebookScannerType aCFacebookScannerType2) {
                return String.CASE_INSENSITIVE_ORDER.compare(aCFacebookScannerType.edge + aCFacebookScannerType.field, aCFacebookScannerType2.edge + aCFacebookScannerType2.field);
            }
        });
        this.ignore = Arrays.asList("id", CorrectionSpan.SUGGESTION_SPAN_PICKED_BEFORE, CorrectionSpan.SUGGESTION_SPAN_PICKED_AFTER, "created_time", "cursor", "next", "picture", "link", "icons", "actions", ShareConstants.WEB_DIALOG_PARAM_PRIVACY);
        this.maxToProcess = 100;
        this.service = aCScannerService;
    }

    private String buildScanUrl(ACFacebookScannerType aCFacebookScannerType) {
        return "me" + (!EDGE_NONE.equals(aCFacebookScannerType.edge) ? "/" + aCFacebookScannerType.edge : "");
    }

    private List<String> extractContent(DLMConnector.ScannerBucket scannerBucket, JSONObject jSONObject, AtomicInteger atomicInteger) throws JSONException {
        ArrayList arrayList = new ArrayList();
        Iterator<String> keys = jSONObject.keys();
        while (keys.hasNext() && atomicInteger.get() > 0) {
            String next = keys.next();
            if (!this.ignore.contains(next)) {
                Object obj = jSONObject.get(next);
                if (obj instanceof JSONObject) {
                    if ("paging".equals(next) && ((JSONObject) obj).has("next")) {
                        arrayList.add(((JSONObject) obj).getString("next"));
                    } else {
                        extractContent(scannerBucket, (JSONObject) obj, atomicInteger);
                    }
                } else if (obj instanceof String) {
                    if (!TextUtils.isEmpty((String) obj)) {
                        scannerBucket.scan((String) obj);
                        atomicInteger.getAndDecrement();
                        this.currentProcess++;
                    }
                } else if (obj instanceof JSONArray) {
                    int i = 0;
                    while (true) {
                        int i2 = i;
                        if (i2 < ((JSONArray) obj).length() && atomicInteger.get() > 0) {
                            extractContent(scannerBucket, ((JSONArray) obj).getJSONObject(i2), atomicInteger);
                            i = i2 + 1;
                        }
                    }
                }
            }
        }
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.nuance.swypeconnect.ac.ACScanner
    public final void fail(int i, String str) {
        if (this.callback != null) {
            this.callback.onFailure(i, str);
        }
    }

    public final String getAuthorizationUrl(String[] strArr) {
        FacebookApi facebookApi = new FacebookApi(this.appId);
        facebookApi.setAccessExpires(this.accessExpires);
        facebookApi.setAccessToken(this.accessToken);
        return facebookApi.getAuthorizationUrl(strArr);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.nuance.swypeconnect.ac.ACScanner
    public final ACScannerService.ScannerType getType() {
        return TYPE;
    }

    public final boolean isSessionValid() {
        FacebookApi facebookApi = new FacebookApi(this.appId);
        facebookApi.setAccessExpires(this.accessExpires);
        facebookApi.setAccessToken(this.accessToken);
        return facebookApi.isSessionValid();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Finally extract failed */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:39:0x00e6 A[ADDED_TO_REGION] */
    /* JADX WARN: Type inference failed for: r3v10, types: [com.nuance.swypeconnect.ac.ACScannerFacebook$ACFacebookScannerType[], java.lang.Object[]] */
    /* JADX WARN: Type inference failed for: r4v1 */
    /* JADX WARN: Type inference failed for: r4v10 */
    /* JADX WARN: Type inference failed for: r4v11 */
    /* JADX WARN: Type inference failed for: r4v12 */
    /* JADX WARN: Type inference failed for: r4v13 */
    /* JADX WARN: Type inference failed for: r4v14 */
    /* JADX WARN: Type inference failed for: r4v15 */
    /* JADX WARN: Type inference failed for: r4v17 */
    /* JADX WARN: Type inference failed for: r4v18 */
    /* JADX WARN: Type inference failed for: r4v19 */
    /* JADX WARN: Type inference failed for: r4v2 */
    /* JADX WARN: Type inference failed for: r4v20 */
    /* JADX WARN: Type inference failed for: r4v3 */
    /* JADX WARN: Type inference failed for: r4v4 */
    /* JADX WARN: Type inference failed for: r4v5 */
    /* JADX WARN: Type inference failed for: r4v6 */
    /* JADX WARN: Type inference failed for: r4v7 */
    /* JADX WARN: Type inference failed for: r4v8 */
    /* JADX WARN: Type inference failed for: r4v9 */
    @Override // com.nuance.swypeconnect.ac.ACScanner
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final void scan() {
        /*
            Method dump skipped, instructions count: 577
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.nuance.swypeconnect.ac.ACScannerFacebook.scan():void");
    }

    public final void setAccessExpires(long j) {
        this.accessExpires = j;
    }

    public final void setAccessToken(String str) {
        this.accessToken = str;
    }

    public final void setAppId(String str) {
        this.appId = str;
    }

    public final void setScanType(ACFacebookScannerType[] aCFacebookScannerTypeArr) {
        this.scanTypes.clear();
        if (aCFacebookScannerTypeArr != null) {
            this.scanTypes.addAll(Arrays.asList(aCFacebookScannerTypeArr));
        }
    }

    @Override // com.nuance.swypeconnect.ac.ACScanner
    public final void start(ACScannerService.ACScannerCallback aCScannerCallback) throws ACScannerException {
        if (this.accessToken == null || this.appId == null) {
            throw new ACScannerException(103);
        }
        super.start(aCScannerCallback);
        this.service.scheduleScan(this);
    }
}
