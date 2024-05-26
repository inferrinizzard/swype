package com.nuance.sns;

import android.util.Base64;
import android.util.Pair;
import com.google.api.client.http.UrlEncodedParser;
import com.localytics.android.BuildConfig;
import com.nuance.nmsp.client.util.internal.dictationresult.parser.xml.xmlResults.XMLResultsHandler;
import com.nuance.swype.util.LogManager;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/* loaded from: classes.dex */
final class HttpUtils {
    private static final String ENCODING = "UTF-8";
    public static final String TWITTER_CALLBACK = "twitter://callback";
    private static final String TWITTER_OAUTH_AUTHORIZE_URL = "https://api.twitter.com/oauth/authorize";
    private static final String URL_TWITTER = "https://api.twitter.com/oauth/access_token";
    private static final LogManager.Log log = LogManager.getLog("HttpUtils");
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

    HttpUtils() {
    }

    public static String getPostResultAsString(String urlString, Map<String, String> headers, String body) throws IOException {
        return new String(getPostResult(urlString, headers, body), ENCODING);
    }

    public static byte[] getPostResult(String urlString, Map<String, String> headers, String body) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(urlString).openConnection();
        if (headers != null) {
            try {
                for (String header : headers.keySet()) {
                    conn.setRequestProperty(header, headers.get(header));
                }
            } finally {
                conn.disconnect();
            }
        }
        preparePostBody(conn, body);
        int resCode = conn.getResponseCode();
        if (resCode == 200) {
            return readResponse(conn.getInputStream());
        }
        throw new IOException(String.format("Response code %d", Integer.valueOf(resCode)));
    }

    private static byte[] readResponse(InputStream inputStream) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        while (true) {
            int bytesRead = inputStream.read(buffer);
            if (bytesRead > 0) {
                out.write(buffer, 0, bytesRead);
            } else {
                out.close();
                return out.toByteArray();
            }
        }
    }

    private static void preparePostBody(HttpURLConnection conn, String body) throws IOException {
        conn.setDoOutput(true);
        if (body != null) {
            conn.setFixedLengthStreamingMode(body.length());
            OutputStream out = new BufferedOutputStream(conn.getOutputStream());
            out.write(body.getBytes(ENCODING));
            out.flush();
            out.close();
        }
    }

    private static String percentEncode(String s) throws UnsupportedEncodingException {
        return URLEncoder.encode(s, ENCODING).replace("+", "%20").replace("*", "%2A").replace("%7E", "~");
    }

    private static String base64Encode(byte[] array) {
        return Base64.encodeToString(array, 2);
    }

    private static String generateHmacSHA1(String key, String value) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(ENCODING), "HmacSHA1");
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(keySpec);
        return base64Encode(mac.doFinal(value.getBytes(ENCODING)));
    }

    private static String getValueFromResponseString(String responseString, String key) {
        for (String s : responseString.split("&")) {
            if (s.contains(key)) {
                return s.replace(key, "").replace("=", "").replace("&", "");
            }
        }
        return "";
    }

    private static Pair<String, String> parseTokenResponse(String response) throws IllegalStateException, IOException {
        if (response != null) {
            return new Pair<>(getValueFromResponseString(response, oauth_token), getValueFromResponseString(response, oauth_token_secret));
        }
        return null;
    }

    private static String getParametersString(Map<String, String> parameters) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            if (!first) {
                sb.append("&");
            }
            sb.append(percentEncode(entry.getKey())).append("=").append(percentEncode(entry.getValue()));
            first = false;
        }
        return sb.toString();
    }

    private static String getOAuthHeaderString(Map<String, String> parameters) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        sb.append("OAuth ");
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append(percentEncode(entry.getKey())).append("=\"").append(percentEncode(entry.getValue())).append("\"");
            first = false;
        }
        return sb.toString();
    }

    public static String getRequestToken(String consumerKey, String consumerSecret) {
        log.d("getRequestToken");
        try {
            log.d("Get the request token");
            log.d("Generate the nonce / timestamp");
            String nonce = base64Encode(UUID.randomUUID().toString().replaceAll(XMLResultsHandler.SEP_HYPHEN, "").getBytes(ENCODING));
            String timeStamp = String.valueOf(new Date().getTime() / 1000);
            Map<String, String> parameters = new TreeMap<>();
            parameters.put(oauth_callback, TWITTER_CALLBACK);
            parameters.put(oauth_consumer_key, consumerKey);
            parameters.put(oauth_nonce, nonce);
            parameters.put(oauth_signature_method, "HMAC-SHA1");
            parameters.put(oauth_timestamp, timeStamp);
            parameters.put(oauth_version, BuildConfig.VERSION_NAME);
            String parametersString = getParametersString(parameters);
            log.d("Parameter String: " + parametersString);
            String signatureBaseString = "POST&" + percentEncode("https://api.twitter.com/oauth/request_token") + "&" + percentEncode(parametersString);
            String signature = generateHmacSHA1(percentEncode(consumerSecret) + "&", signatureBaseString);
            parameters.put(oauth_signature, signature);
            String oauthHeader = getOAuthHeaderString(parameters);
            log.d("Authorization Header: " + oauthHeader);
            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", oauthHeader);
            headers.put("Content-Type", UrlEncodedParser.CONTENT_TYPE);
            Pair<String, String> token = parseTokenResponse(getPostResultAsString("https://api.twitter.com/oauth/request_token", headers, null));
            return token == null ? "" : (String) token.first;
        } catch (Exception e) {
            log.e(e.getMessage());
            return "";
        }
    }

    public static String getAuthorizeUrl(String token) {
        log.d("Token: " + token);
        return "https://api.twitter.com/oauth/authorize?oauth_token=" + token;
    }

    public static Pair<String, String> getAccessToken(String consumerKey, String consumerSecret, String token, String verifier) {
        log.d("getAuthorization");
        try {
            log.d("Get the access token");
            log.d("Generate the nonce / timestamp");
            String nonce = base64Encode(UUID.randomUUID().toString().replaceAll(XMLResultsHandler.SEP_HYPHEN, "").getBytes(ENCODING));
            String timeStamp = String.valueOf(new Date().getTime() / 1000);
            Map<String, String> parameters = new TreeMap<>();
            parameters.put(oauth_consumer_key, consumerKey);
            parameters.put(oauth_nonce, nonce);
            parameters.put(oauth_signature_method, "HMAC-SHA1");
            parameters.put(oauth_timestamp, timeStamp);
            parameters.put(oauth_token, token);
            parameters.put(oauth_version, BuildConfig.VERSION_NAME);
            String parametersString = getParametersString(parameters);
            log.d("Parameter String: " + parametersString);
            String signatureBaseString = "POST&" + percentEncode(URL_TWITTER) + "&" + percentEncode(parametersString);
            String signature = generateHmacSHA1(percentEncode(consumerSecret) + "&", signatureBaseString);
            parameters.put(oauth_signature, signature);
            String oauthHeader = getOAuthHeaderString(parameters);
            log.d("Authorization Header: " + oauthHeader);
            String body = percentEncode(oauth_verifier) + "=" + percentEncode(verifier);
            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", oauthHeader);
            headers.put("Content-Type", UrlEncodedParser.CONTENT_TYPE);
            return parseTokenResponse(getPostResultAsString(URL_TWITTER, headers, body));
        } catch (Exception e) {
            log.e(e.getMessage());
            return null;
        }
    }
}
