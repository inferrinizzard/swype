package com.nuance.connect.util;

import android.net.Uri;
import android.util.Base64;
import android.util.Pair;
import com.google.api.client.http.UrlEncodedParser;
import com.localytics.android.BuildConfig;
import com.nuance.connect.util.Logger;
import com.nuance.nmsp.client.util.internal.dictationresult.parser.xml.xmlResults.XMLResultsHandler;
import com.nuance.swype.input.InputFieldInfo;
import com.nuance.swypeconnect.ac.ACBuildConfigRuntime;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HttpsURLConnection;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public final class HttpUtils {
    private static final String ENCODING = "UTF-8";
    private static final String HTTPS_API_TWITTER_COM_OAUTH_ACCESS_TOKEN = "https://api.twitter.com/oauth/access_token";
    private static final String HTTPS_API_TWITTER_COM_OAUTH_REQUEST_TOKEN = "https://api.twitter.com/oauth/request_token";
    private static final int HTTP_TOO_MANY_REQUESTS = 429;
    private static final String METHOD_GET = "GET";
    private static final String METHOD_POST = "POST";
    private static final String STATUS = "status";
    private static final String TWITTER_CALLBACK = "twitter://callback";
    private static final String TWITTER_DELETE_TWEET = "https://api.twitter.com/1.1/statuses/destroy/";
    private static final String TWITTER_FRIENDS_LIST = "https://api.twitter.com/1.1/friends/list.json";
    private static final String TWITTER_OAUTH_AUTHORIZE_URL = "https://api.twitter.com/oauth/authorize";
    private static final String TWITTER_RATE_LIMIT = "https://api.twitter.com/1.1/application/rate_limit_status.json";
    private static final String TWITTER_TWEET = "https://api.twitter.com/1.1/statuses/update.json";
    private static final String TWITTER_USER_TIMELINE_STATUSES = "https://api.twitter.com/1.1/statuses/user_timeline.json";
    private static final String TWITTER_VERIFY_CREDENTIALS = "https://api.twitter.com/1.1/account/verify_credentials.json";
    private static final Logger.Log log = Logger.getLog(Logger.LoggerType.DEVELOPER, HttpUtils.class.getSimpleName());
    private static final String oauth_callback = "oauth_callback";
    private static final String oauth_consumer_key = "oauth_consumer_key";
    private static final String oauth_nonce = "oauth_nonce";
    private static final String oauth_signature = "oauth_signature";
    private static final String oauth_signature_method = "oauth_signature_method";
    private static final String oauth_timestamp = "oauth_timestamp";
    private static final String oauth_token = "oauth_token";
    private static final String oauth_token_secret = "oauth_token_secret";
    private static final String oauth_verifier = "oauth_verifier";
    private static final String oauth_version = "oauth_version";

    /* loaded from: classes.dex */
    public interface FriendListCallback {
        void friend(String str, String str2);
    }

    /* loaded from: classes.dex */
    public static class HttpUtilsException extends Exception {
        private static final long serialVersionUID = 7055568931133587076L;
        private int code;

        public HttpUtilsException(String str) {
            super("HttpUtilsException: " + str);
            this.code = -1;
        }

        public HttpUtilsException(String str, int i) {
            super("HttpUtilsException(" + i + "): " + str);
            this.code = -1;
            this.code = i;
        }

        public boolean rateIsExceeded() {
            return this.code == 403 || this.code == 429;
        }
    }

    /* loaded from: classes.dex */
    public interface TimelineCallback {
        void saveSinceId(long j);

        void tweet(String str, String str2, String str3, long j);
    }

    private static void addOAuthHeader(HttpsURLConnection httpsURLConnection, String str, String str2, String str3, String str4, String str5, String str6, Map<String, String> map) throws Exception {
        String base64Encode = base64Encode(UUID.randomUUID().toString().replaceAll(XMLResultsHandler.SEP_HYPHEN, "").getBytes(ENCODING));
        String valueOf = String.valueOf(new Date().getTime() / 1000);
        TreeMap treeMap = new TreeMap();
        treeMap.put(oauth_consumer_key, str);
        treeMap.put(oauth_nonce, base64Encode);
        treeMap.put(oauth_signature_method, "HMAC-SHA1");
        treeMap.put(oauth_timestamp, valueOf);
        treeMap.put(oauth_token, str3);
        treeMap.put(oauth_version, BuildConfig.VERSION_NAME);
        map.putAll(treeMap);
        if ("GET".equals(str5)) {
            treeMap.putAll(map);
        }
        treeMap.put(oauth_signature, generateHmacSHA1(percentEncode(str2) + "&" + percentEncode(str4), str5 + "&" + percentEncode(str6) + "&" + percentEncode(getParametersString(map))));
        httpsURLConnection.setRequestProperty("Authorization", getOAuthHeaderString(treeMap));
    }

    private static String base64Encode(byte[] bArr) {
        return Base64.encodeToString(bArr, 2);
    }

    public static String deleteTweet(String str, String str2, String str3, String str4, long j) throws HttpUtilsException {
        log.v("deleteTweet id=", Long.valueOf(j));
        TreeMap treeMap = new TreeMap();
        treeMap.put("id", String.valueOf(j));
        System.setProperty("http.keepAlive", ACBuildConfigRuntime.DEVELOPER_LOG_ENABLED);
        try {
            URL url = new URL(TWITTER_DELETE_TWEET + j + ".json");
            HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
            try {
                httpsURLConnection.setDoInput(true);
                httpsURLConnection.setDoOutput(true);
                httpsURLConnection.setRequestMethod("POST");
                httpsURLConnection.setRequestProperty("Content-Type", UrlEncodedParser.CONTENT_TYPE);
                Uri.Builder builder = new Uri.Builder();
                builder.appendQueryParameter("id", String.valueOf(j));
                addOAuthHeader(httpsURLConnection, str, str2, str3, str4, "POST", url.toString(), treeMap);
                writeRequest(httpsURLConnection, builder.build().getEncodedQuery());
                int responseCode = httpsURLConnection.getResponseCode();
                if (responseCode != 200) {
                    throw new HttpUtilsException("Did not get expected response (status code: " + responseCode + ")", responseCode);
                }
                return readResponse(httpsURLConnection);
            } finally {
                httpsURLConnection.disconnect();
            }
        } catch (Exception e) {
            throw new HttpUtilsException(e.getMessage());
        }
    }

    public static void friendsListCallback(String str, String str2, String str3, String str4, FriendListCallback friendListCallback) throws HttpUtilsException {
        log.d("getFriendsListCallback");
        System.setProperty("http.keepAlive", ACBuildConfigRuntime.DEVELOPER_LOG_ENABLED);
        String str5 = "-1";
        boolean z = false;
        while (!z) {
            Uri.Builder buildUpon = Uri.parse(TWITTER_FRIENDS_LIST).buildUpon();
            TreeMap treeMap = new TreeMap();
            treeMap.put("count", "200");
            buildUpon.appendQueryParameter("count", "200");
            treeMap.put("skip_status", Boolean.FALSE.toString());
            buildUpon.appendQueryParameter("skip_status", Boolean.FALSE.toString());
            treeMap.put("include_user_entities", Boolean.FALSE.toString());
            buildUpon.appendQueryParameter("include_user_entities", Boolean.FALSE.toString());
            treeMap.put("cursor", str5);
            buildUpon.appendQueryParameter("cursor", str5);
            System.setProperty("http.keepAlive", ACBuildConfigRuntime.DEVELOPER_LOG_ENABLED);
            try {
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) new URL(buildUpon.toString()).openConnection();
                try {
                    httpsURLConnection.setDoInput(true);
                    httpsURLConnection.setRequestMethod("GET");
                    httpsURLConnection.setRequestProperty("Content-Type", UrlEncodedParser.CONTENT_TYPE);
                    addOAuthHeader(httpsURLConnection, str, str2, str3, str4, "GET", TWITTER_FRIENDS_LIST, treeMap);
                    int responseCode = httpsURLConnection.getResponseCode();
                    if (responseCode != 200) {
                        throw new HttpUtilsException("Did not get expected response (status code: " + responseCode + ")", responseCode);
                    }
                    JSONObject jSONObject = new JSONObject(readResponse(httpsURLConnection));
                    JSONArray jSONArray = jSONObject.getJSONArray("users");
                    for (int i = 0; i < jSONArray.length(); i++) {
                        JSONObject jSONObject2 = (JSONObject) jSONArray.get(i);
                        friendListCallback.friend(jSONObject2.getString("name"), jSONObject2.getString("screen_name"));
                    }
                    String string = jSONObject.getString("next_cursor_str");
                    z = "0".equals(string);
                    str5 = string;
                } finally {
                    httpsURLConnection.disconnect();
                }
            } catch (Exception e) {
                throw new HttpUtilsException(e.getMessage());
            }
        }
    }

    private static String generateHmacSHA1(String str, String str2) throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec(str.getBytes(ENCODING), "HmacSHA1");
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(secretKeySpec);
        return base64Encode(mac.doFinal(str2.getBytes(ENCODING)));
    }

    public static Pair<String, String> getAccessToken(String str, String str2, String str3, String str4) {
        log.d("getAuthorization");
        System.setProperty("http.keepAlive", ACBuildConfigRuntime.DEVELOPER_LOG_ENABLED);
        try {
            log.d("Get the access token");
            URL url = new URL(HTTPS_API_TWITTER_COM_OAUTH_ACCESS_TOKEN);
            HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
            try {
                httpsURLConnection.setDoInput(true);
                httpsURLConnection.setDoOutput(true);
                httpsURLConnection.setRequestMethod("POST");
                httpsURLConnection.setRequestProperty("Content-Type", UrlEncodedParser.CONTENT_TYPE);
                log.d("Generate the nonce / timestamp");
                String base64Encode = base64Encode(UUID.randomUUID().toString().replaceAll(XMLResultsHandler.SEP_HYPHEN, "").getBytes(ENCODING));
                String valueOf = String.valueOf(new Date().getTime() / 1000);
                Uri.Builder builder = new Uri.Builder();
                TreeMap treeMap = new TreeMap();
                treeMap.put(oauth_consumer_key, str);
                treeMap.put(oauth_nonce, base64Encode);
                treeMap.put(oauth_signature_method, "HMAC-SHA1");
                treeMap.put(oauth_timestamp, valueOf);
                treeMap.put(oauth_token, str3);
                treeMap.put(oauth_version, BuildConfig.VERSION_NAME);
                if (str4 != null) {
                    builder.appendQueryParameter(oauth_verifier, str4);
                    treeMap.put(oauth_verifier, str4);
                }
                String parametersString = getParametersString(treeMap);
                log.d("Parameter String: ", parametersString);
                treeMap.put(oauth_signature, generateHmacSHA1(percentEncode(str2) + "&", "POST&" + percentEncode(url.toString()) + "&" + percentEncode(parametersString)));
                String oAuthHeaderString = getOAuthHeaderString(treeMap);
                log.d("Authorization Header: ", oAuthHeaderString);
                httpsURLConnection.setRequestProperty("Authorization", oAuthHeaderString);
                Pair<String, String> parseTokenResponse = parseTokenResponse(readResponse(httpsURLConnection));
                log.d("request token: ", parseTokenResponse.first);
                return parseTokenResponse;
            } finally {
                httpsURLConnection.disconnect();
            }
        } catch (Exception e) {
            log.d("getAccessToken failed: " + e.getMessage());
            return null;
        }
    }

    public static String getAuthorizeUrl(String str) {
        log.d("Token: ", str);
        return "https://api.twitter.com/oauth/authorize?oauth_token=" + str;
    }

    private static String getOAuthHeaderString(Map<String, String> map) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        boolean z = true;
        sb.append("OAuth ");
        Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
        while (true) {
            boolean z2 = z;
            if (!it.hasNext()) {
                return sb.toString();
            }
            Map.Entry<String, String> next = it.next();
            if (!z2) {
                sb.append(", ");
            }
            sb.append(percentEncode(next.getKey())).append("=\"").append(percentEncode(next.getValue())).append("\"");
            z = false;
        }
    }

    private static String getParametersString(Map<String, String> map) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        boolean z = true;
        Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
        while (true) {
            boolean z2 = z;
            if (!it.hasNext()) {
                return sb.toString();
            }
            Map.Entry<String, String> next = it.next();
            if (!z2) {
                sb.append("&");
            }
            sb.append(percentEncode(next.getKey())).append("=").append(percentEncode(next.getValue()));
            z = false;
        }
    }

    public static String getRateLimit(String str, String str2, String str3, String str4, String str5) throws HttpUtilsException {
        log.v("resources: ", str5);
        Uri.Builder buildUpon = Uri.parse(TWITTER_RATE_LIMIT).buildUpon();
        TreeMap treeMap = new TreeMap();
        if (str5 != null) {
            treeMap.put("resources", str5);
            buildUpon.appendQueryParameter("resources", str5);
        }
        System.setProperty("http.keepAlive", ACBuildConfigRuntime.DEVELOPER_LOG_ENABLED);
        try {
            HttpsURLConnection httpsURLConnection = (HttpsURLConnection) new URL(buildUpon.toString()).openConnection();
            try {
                httpsURLConnection.setDoInput(true);
                httpsURLConnection.setRequestMethod("GET");
                httpsURLConnection.setRequestProperty("Content-Type", UrlEncodedParser.CONTENT_TYPE);
                addOAuthHeader(httpsURLConnection, str, str2, str3, str4, "GET", TWITTER_RATE_LIMIT, treeMap);
                int responseCode = httpsURLConnection.getResponseCode();
                if (responseCode != 200) {
                    throw new HttpUtilsException("Did not get expected response (status code: " + responseCode + ")", responseCode);
                }
                return readResponse(httpsURLConnection);
            } finally {
                httpsURLConnection.disconnect();
            }
        } catch (Exception e) {
            throw new HttpUtilsException(e.getMessage());
        }
    }

    public static String getRequestToken(String str, String str2) {
        log.d("getRequestToken");
        System.setProperty("http.keepAlive", ACBuildConfigRuntime.DEVELOPER_LOG_ENABLED);
        try {
            log.d("Get the request token");
            URL url = new URL(HTTPS_API_TWITTER_COM_OAUTH_REQUEST_TOKEN);
            HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
            try {
                httpsURLConnection.setDoInput(true);
                httpsURLConnection.setDoOutput(true);
                httpsURLConnection.setRequestMethod("POST");
                httpsURLConnection.setRequestProperty("Content-Type", UrlEncodedParser.CONTENT_TYPE);
                log.d("Generate the nonce / timestamp");
                String base64Encode = base64Encode(UUID.randomUUID().toString().replaceAll(XMLResultsHandler.SEP_HYPHEN, "").getBytes(ENCODING));
                String valueOf = String.valueOf(new Date().getTime() / 1000);
                TreeMap treeMap = new TreeMap();
                treeMap.put(oauth_callback, "twitter://callback");
                treeMap.put(oauth_consumer_key, str);
                treeMap.put(oauth_nonce, base64Encode);
                treeMap.put(oauth_signature_method, "HMAC-SHA1");
                treeMap.put(oauth_timestamp, valueOf);
                treeMap.put(oauth_version, BuildConfig.VERSION_NAME);
                String parametersString = getParametersString(treeMap);
                log.d("Parameter String: ", parametersString);
                treeMap.put(oauth_signature, generateHmacSHA1(percentEncode(str2) + "&", "POST&" + percentEncode(url.toString()) + "&" + percentEncode(parametersString)));
                String oAuthHeaderString = getOAuthHeaderString(treeMap);
                log.d("Authorization Header: ", oAuthHeaderString);
                httpsURLConnection.setRequestProperty("Authorization", oAuthHeaderString);
                return (String) parseTokenResponse(readResponse(httpsURLConnection)).first;
            } finally {
                httpsURLConnection.disconnect();
            }
        } catch (Exception e) {
            log.d("getRequestToken failed: " + e.getMessage());
            return "";
        }
    }

    private static String getTwitterTimelineStatuses(String str, String str2, String str3, String str4, int i, long j, long j2) throws HttpUtilsException {
        log.d("getTwitterTimelineStatuses max=", Integer.valueOf(i), " since=", Long.valueOf(j), " maxId=", Long.valueOf(j2));
        String valueOf = String.valueOf(i);
        Uri.Builder buildUpon = Uri.parse(TWITTER_USER_TIMELINE_STATUSES).buildUpon();
        TreeMap treeMap = new TreeMap();
        treeMap.put("count", valueOf);
        buildUpon.appendQueryParameter("count", valueOf);
        if (j >= 0) {
            treeMap.put("since_id", String.valueOf(j));
            buildUpon.appendQueryParameter("since_id", Long.toString(j));
        }
        if (j2 >= 0) {
            treeMap.put("max_id", String.valueOf(j2));
            buildUpon.appendQueryParameter("max_id", Long.toString(j2));
        }
        System.setProperty("http.keepAlive", ACBuildConfigRuntime.DEVELOPER_LOG_ENABLED);
        try {
            HttpsURLConnection httpsURLConnection = (HttpsURLConnection) new URL(buildUpon.toString()).openConnection();
            try {
                httpsURLConnection.setDoInput(true);
                httpsURLConnection.setRequestMethod("GET");
                httpsURLConnection.setRequestProperty("Content-Type", UrlEncodedParser.CONTENT_TYPE);
                addOAuthHeader(httpsURLConnection, str, str2, str3, str4, "GET", TWITTER_USER_TIMELINE_STATUSES, treeMap);
                int responseCode = httpsURLConnection.getResponseCode();
                if (responseCode != 200) {
                    throw new HttpUtilsException("Did not get expected response (status code: " + responseCode + ")", responseCode);
                }
                return readResponse(httpsURLConnection);
            } finally {
                httpsURLConnection.disconnect();
            }
        } catch (Exception e) {
            throw new HttpUtilsException(e.getMessage());
        }
    }

    private static String getValueFromResponseString(String str, String str2) {
        for (String str3 : str.split("&")) {
            if (str3.contains(str2)) {
                return str3.replace(str2, "").replace("=", "").replace("&", "");
            }
        }
        return "";
    }

    private static Pair<String, String> parseTokenResponse(String str) throws IllegalStateException {
        log.d("Success! response=", str);
        return new Pair<>(getValueFromResponseString(str, oauth_token), getValueFromResponseString(str, oauth_token_secret));
    }

    private static String percentEncode(String str) throws UnsupportedEncodingException {
        return URLEncoder.encode(str, ENCODING).replace("+", "%20").replace("*", "%2A").replace("%7E", "~");
    }

    private static String readResponse(HttpsURLConnection httpsURLConnection) throws Exception {
        BufferedReader bufferedReader;
        int responseCode = httpsURLConnection.getResponseCode();
        log.d("serverConn.getResponseCode(", Integer.valueOf(responseCode), ") ");
        if (responseCode != 200) {
            throw new HttpUtilsException("Invalid server response. code: " + responseCode, responseCode);
        }
        try {
            try {
                StringBuilder sb = new StringBuilder();
                bufferedReader = new BufferedReader(new InputStreamReader(httpsURLConnection.getInputStream(), ENCODING), 1024);
                while (true) {
                    try {
                        String readLine = bufferedReader.readLine();
                        if (readLine == null) {
                            break;
                        }
                        sb.append(readLine);
                    } catch (Throwable th) {
                        th = th;
                        if (bufferedReader != null) {
                            bufferedReader.close();
                        }
                        throw th;
                    }
                }
                String sb2 = sb.toString();
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
                return sb2;
            } catch (Throwable th2) {
                th = th2;
                bufferedReader = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static String tweet(String str, String str2, String str3, String str4, String str5) throws HttpUtilsException {
        log.v("tweet: ", str5);
        TreeMap treeMap = new TreeMap();
        treeMap.put("status", str5);
        System.setProperty("http.keepAlive", ACBuildConfigRuntime.DEVELOPER_LOG_ENABLED);
        try {
            HttpsURLConnection httpsURLConnection = (HttpsURLConnection) new URL(TWITTER_TWEET).openConnection();
            try {
                httpsURLConnection.setDoInput(true);
                httpsURLConnection.setDoOutput(true);
                httpsURLConnection.setRequestMethod("POST");
                httpsURLConnection.setRequestProperty("Content-Type", UrlEncodedParser.CONTENT_TYPE);
                Uri.Builder builder = new Uri.Builder();
                builder.appendQueryParameter("status", str5);
                addOAuthHeader(httpsURLConnection, str, str2, str3, str4, "POST", TWITTER_TWEET, treeMap);
                writeRequest(httpsURLConnection, builder.build().getEncodedQuery());
                int responseCode = httpsURLConnection.getResponseCode();
                if (responseCode != 200) {
                    throw new HttpUtilsException("Did not get expected response (status code: " + responseCode + ")", responseCode);
                }
                return readResponse(httpsURLConnection);
            } finally {
                httpsURLConnection.disconnect();
            }
        } catch (Exception e) {
            throw new HttpUtilsException(e.getMessage());
        }
    }

    public static void userTimelineCallback(String str, String str2, String str3, String str4, int i, long j, TimelineCallback timelineCallback) throws JSONException, HttpUtilsException {
        int i2 = 0;
        long j2 = -1;
        long j3 = -1;
        int i3 = i;
        while (i3 > 0 && i2 < i) {
            int i4 = i3 > 200 ? 200 : i3;
            JSONArray jSONArray = new JSONArray(getTwitterTimelineStatuses(str, str2, str3, str4, i4, j, j2));
            if (jSONArray.length() != 0) {
                long j4 = -1;
                long j5 = j3;
                int i5 = i2;
                boolean z = i4 > 0 && jSONArray.length() > 0;
                int i6 = 0;
                while (z) {
                    JSONObject jSONObject = jSONArray.getJSONObject(i6);
                    JSONObject jSONObject2 = jSONObject.getJSONObject("user");
                    log.v("Tweet: " + jSONObject2.getString("name") + "," + jSONObject2.getString("screen_name") + "," + jSONObject.getString(InputFieldInfo.INPUT_TYPE_TEXT) + "," + jSONObject.getString("created_at"));
                    long j6 = jSONObject.getLong("id");
                    if (j5 < 0) {
                        timelineCallback.saveSinceId(j6);
                    } else {
                        j6 = j5;
                    }
                    j4 = jSONObject.getLong("id");
                    timelineCallback.tweet(jSONObject2.getString("name"), jSONObject2.getString("screen_name"), jSONObject.getString(InputFieldInfo.INPUT_TYPE_TEXT), j4);
                    int i7 = i5 + 1;
                    int i8 = i6 + 1;
                    boolean z2 = i8 < i4 && i8 < jSONArray.length();
                    i5 = i7;
                    j5 = j6;
                    z = z2;
                    i6 = i8;
                }
                i3 -= i4;
                j3 = j5;
                j2 = j4;
                i2 = i5;
            } else {
                i3 = 0;
            }
        }
    }

    public static boolean verifyTwitterCredentials(String str, String str2, String str3, String str4) {
        log.d("verifyTwitterCredentials consumerKey[", str, "] consumerSecret[", str2, "] accessToken [", str3, "] secretToken [", str4, "]");
        Uri.Builder buildUpon = Uri.parse(TWITTER_VERIFY_CREDENTIALS).buildUpon();
        TreeMap treeMap = new TreeMap();
        System.setProperty("http.keepAlive", ACBuildConfigRuntime.DEVELOPER_LOG_ENABLED);
        try {
            HttpsURLConnection httpsURLConnection = (HttpsURLConnection) new URL(buildUpon.toString()).openConnection();
            try {
                httpsURLConnection.setDoInput(true);
                httpsURLConnection.setRequestMethod("GET");
                httpsURLConnection.setRequestProperty("Content-Type", UrlEncodedParser.CONTENT_TYPE);
                addOAuthHeader(httpsURLConnection, str, str2, str3, str4, "GET", TWITTER_VERIFY_CREDENTIALS, treeMap);
                return httpsURLConnection.getResponseCode() == 200;
            } finally {
                httpsURLConnection.disconnect();
            }
        } catch (Exception e) {
            log.d("verifyTwitterCredentials failed: " + e.getMessage());
            return false;
        }
    }

    private static void writeRequest(HttpsURLConnection httpsURLConnection, String str) {
        BufferedOutputStream bufferedOutputStream;
        byte[] bytes;
        try {
            try {
                bytes = str.getBytes(ENCODING);
                bufferedOutputStream = new BufferedOutputStream(httpsURLConnection.getOutputStream());
            } catch (Throwable th) {
                th = th;
                bufferedOutputStream = null;
            }
            try {
                bufferedOutputStream.write(bytes);
                bufferedOutputStream.flush();
                if (bufferedOutputStream != null) {
                    bufferedOutputStream.close();
                }
            } catch (Throwable th2) {
                th = th2;
                if (bufferedOutputStream != null) {
                    bufferedOutputStream.close();
                }
                throw th;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
