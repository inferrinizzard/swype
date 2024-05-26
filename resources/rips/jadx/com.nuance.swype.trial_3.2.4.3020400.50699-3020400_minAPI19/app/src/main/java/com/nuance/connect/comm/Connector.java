package com.nuance.connect.comm;

import android.os.Build;
import android.os.StatFs;
import android.util.JsonWriter;
import com.nuance.connect.comm.Command;
import com.nuance.connect.comm.CommandQueue;
import com.nuance.connect.util.Logger;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.net.ssl.HttpsURLConnection;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public abstract class Connector {
    protected static final int DEFAULT_COMPRESSION_THRESHOLD = 200;
    protected static final int DEFAULT_DELAY_FOR = -1;
    protected static final String ENCODING = "UTF-8";
    protected static final int SEND_PERCENT_UPDATE_INTERVAL = 500;
    private static final Logger.Log log = Logger.getThreadLog(Logger.LoggerType.DEVELOPER, Connector.class.getSimpleName());
    protected final AnalyticsDataUsageScribe analyticsScribe;
    protected final MessageSendingBus client;
    protected int compressionThreshold = 200;
    protected final CommandQueue.ConnectionStatus connectionStatus;
    protected final ConnectorCallback connectorCallback;
    protected final String requestKey;

    /* loaded from: classes.dex */
    public interface ResponseData {
        Response getResponse();

        int getStatusCode();

        String getStatusMessage();
    }

    public Connector(MessageSendingBus messageSendingBus, CommandQueue.ConnectionStatus connectionStatus, ConnectorCallback connectorCallback, AnalyticsDataUsageScribe analyticsDataUsageScribe, String str) {
        this.client = messageSendingBus;
        this.connectionStatus = connectionStatus;
        this.connectorCallback = connectorCallback;
        this.analyticsScribe = analyticsDataUsageScribe;
        this.requestKey = str;
    }

    private String commandToURL(String str, Command command) {
        return str + command.commandFamily + "/" + command.version + "/" + command.command;
    }

    private void logServerResponse(Command command, byte[] bArr) {
        try {
            File file = new File(this.client.getContext().getFilesDir(), "server-sessions.txt");
            if (!file.exists() && !file.createNewFile()) {
                log.w("Unable to create server-sessions.txt");
                return;
            }
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file, true));
            try {
                try {
                    bufferedOutputStream.write(("\"/" + command.commandFamily + "/" + command.version + "/" + command.command + "\", ").getBytes(ENCODING));
                    String replaceAll = new String(bArr, ENCODING).replaceAll("\"", "\\\\\"");
                    bufferedOutputStream.write("\"".getBytes(ENCODING));
                    bufferedOutputStream.write(replaceAll.getBytes(ENCODING));
                    bufferedOutputStream.write("\"\n".getBytes(ENCODING));
                } finally {
                    bufferedOutputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
                bufferedOutputStream.close();
            }
        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }

    private static void processPbuffResult(byte[] bArr, Response response) {
        response.responseArray = bArr;
        response.processed = true;
        response.status = 1;
    }

    private void writeJsonValue(JsonWriter jsonWriter, Object obj) throws IOException {
        if (obj instanceof String) {
            jsonWriter.value(obj.toString());
            return;
        }
        if (obj instanceof Double) {
            jsonWriter.value(((Double) obj).doubleValue());
            return;
        }
        if (obj instanceof Long) {
            jsonWriter.value(((Long) obj).longValue());
            return;
        }
        if (obj instanceof Integer) {
            jsonWriter.value(((Integer) obj).intValue());
            return;
        }
        if (obj instanceof Boolean) {
            jsonWriter.value(((Boolean) obj).booleanValue());
            return;
        }
        if (obj instanceof HashMap) {
            try {
                jsonWriter.beginObject();
                for (Map.Entry entry : ((HashMap) obj).entrySet()) {
                    String str = (String) entry.getValue();
                    if (str != null) {
                        jsonWriter.name((String) entry.getKey()).value(str);
                    }
                }
                return;
            } finally {
            }
        }
        if (obj instanceof JSONObject) {
            try {
                jsonWriter.beginObject();
                JSONObject jSONObject = (JSONObject) obj;
                Iterator<String> keys = jSONObject.keys();
                while (keys.hasNext()) {
                    String next = keys.next();
                    jsonWriter.name(next);
                    writeJsonValue(jsonWriter, jSONObject.get(next));
                }
                return;
            } catch (JSONException e) {
                log.e("json obj e: " + e.getMessage());
                return;
            } finally {
            }
        }
        try {
            if (obj instanceof JSONArray) {
                jsonWriter.beginArray();
                JSONArray jSONArray = (JSONArray) obj;
                for (int i = 0; i < jSONArray.length(); i++) {
                    writeJsonValue(jsonWriter, jSONArray.get(i));
                }
                return;
            }
            if (!(obj instanceof Iterator)) {
                log.d("Error: unusable value=", obj);
                return;
            }
            try {
                jsonWriter.beginArray();
                Iterator it = (Iterator) obj;
                while (it.hasNext()) {
                    writeJsonValue(jsonWriter, it.next());
                }
            } finally {
            }
        } catch (JSONException e2) {
            log.e("json array e: " + e2.getMessage());
        } finally {
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean allowsOutput() {
        return Build.VERSION.SDK_INT < 14;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public HttpURLConnection connectToUrl(String str, Command command) throws IOException, MalformedURLException, URISyntaxException {
        URL url;
        URL url2 = new URL(isThirdPartyRequest(command) ? command.thirdPartyURL : commandToURL(str, command));
        try {
            url2.toURI();
            url = url2;
        } catch (URISyntaxException e) {
            url = new URI(url2.getProtocol(), url2.getUserInfo(), url2.getHost(), url2.getPort(), url2.getPath(), url2.getQuery(), url2.getRef()).toURL();
        }
        HttpURLConnection httpURLConnection = url.getProtocol().equals("https") ? (HttpsURLConnection) url.openConnection() : (HttpURLConnection) url.openConnection();
        log.d("connectToUrl: " + url.toString());
        return httpURLConnection;
    }

    public void destroyConnection() {
        this.analyticsScribe.flush();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public byte[] generateBody(Command command) throws RuntimeException {
        byte[] bArr;
        UnsupportedEncodingException e;
        if (command.dataSource.equals(Command.DATA_SOURCE.JSON)) {
            JSONObject jSONObject = new JSONObject();
            try {
                if (command.requireDevice) {
                    jSONObject.put(MessageAPI.DEVICE_ID, this.connectorCallback.getDeviceId());
                }
                if (command.requireSession) {
                    jSONObject.put(MessageAPI.SESSION_ID, this.connectorCallback.getSessionId());
                }
                jSONObject.put("0", this.requestKey);
                if (command.transactionId != null) {
                    jSONObject.put(MessageAPI.TRANSACTION_ID, command.transactionId);
                }
                for (String str : command.parameters.keySet()) {
                    Object obj = command.parameters.get(str);
                    if (obj instanceof String) {
                        jSONObject.put(str, obj);
                    } else if (obj instanceof Long) {
                        jSONObject.put(str, ((Long) obj).longValue());
                    } else if (obj instanceof Integer) {
                        jSONObject.put(str, ((Integer) obj).intValue());
                    } else if (obj instanceof Boolean) {
                        jSONObject.put(str, (Boolean) obj);
                    } else if (obj instanceof HashMap) {
                        JSONObject jSONObject2 = new JSONObject();
                        for (Map.Entry entry : ((HashMap) obj).entrySet()) {
                            jSONObject2.put((String) entry.getKey(), (String) entry.getValue());
                        }
                        jSONObject.put(str, jSONObject2);
                    } else if (obj instanceof JSONObject) {
                        jSONObject.put(str, obj);
                    } else if (obj instanceof JSONArray) {
                        jSONObject.put(str, obj);
                    } else {
                        log.d("Error: unusable key type key=", str, " value=", obj);
                    }
                }
                try {
                    bArr = jSONObject.toString().getBytes(ENCODING);
                    try {
                        log.d("body: ", jSONObject.toString());
                    } catch (UnsupportedEncodingException e2) {
                        e = e2;
                        log.e("Failed string encoding: ", e.getMessage());
                        return bArr;
                    }
                } catch (UnsupportedEncodingException e3) {
                    e = e3;
                    bArr = null;
                }
            } catch (JSONException e4) {
                throw new RuntimeException(e4.getMessage());
            }
        } else {
            if (!command.dataSource.equals(Command.DATA_SOURCE.PBUFF)) {
                throw new RuntimeException("Unknown Content being transmitted");
            }
            log.d("generateBody for protobuffers");
            bArr = command.bufferData;
            if (bArr != null) {
                log.d("generateBody: ", Arrays.toString(bArr));
            } else {
                log.d("generateBody: null");
            }
        }
        return bArr;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean hasSufficientSpace(int i) {
        StatFs statFs = new StatFs(this.client.getContext().getCacheDir().getPath());
        return ((long) i) < ((long) statFs.getAvailableBlocks()) * ((long) statFs.getBlockSize());
    }

    public boolean isThirdPartyRequest(Command command) {
        return command.thirdPartyURL != null && command.thirdPartyURL.length() > 5;
    }

    public abstract boolean processCommand(Command command, Transaction transaction, CommandQueue.NetworkExpirer networkExpirer) throws ConnectorException;

    /* JADX INFO: Access modifiers changed from: protected */
    public void processFileResult(Response response, Command command, File file) {
        response.command = command.command;
        response.commandFamily = command.commandFamily;
        response.transactionId = command.transactionId;
        response.identifier = command.identifier;
        response.status = 1;
        response.initialCommand = command;
        response.fileLocation = file.getAbsolutePath();
        response.file = file;
        this.connectorCallback.onResponse(response);
        log.d("Downloaded File: \n", file.getAbsolutePath(), "\nSize: ", Long.valueOf(file.length()));
    }

    /* JADX WARN: Failed to find 'out' block for switch in B:37:0x0124. Please report as an issue. */
    /* JADX WARN: Removed duplicated region for block: B:43:0x0141 A[Catch: JSONException -> 0x014f, LOOP:0: B:41:0x013b->B:43:0x0141, LOOP_END, TRY_LEAVE, TryCatch #3 {JSONException -> 0x014f, blocks: (B:10:0x004e, B:12:0x0057, B:13:0x0066, B:15:0x006f, B:16:0x007e, B:18:0x0087, B:19:0x0096, B:21:0x009f, B:22:0x00ae, B:24:0x00b7, B:25:0x00bd, B:27:0x00c6, B:28:0x00d5, B:30:0x00de, B:31:0x00f9, B:33:0x0102, B:40:0x0137, B:41:0x013b, B:43:0x0141, B:70:0x018c, B:72:0x0194, B:73:0x019f, B:75:0x0173, B:77:0x017b), top: B:9:0x004e }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    protected void processJSONResult(byte[] r12, com.nuance.connect.comm.Response r13) throws com.nuance.connect.comm.ConnectorException {
        /*
            Method dump skipped, instructions count: 612
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.nuance.connect.comm.Connector.processJSONResult(byte[], com.nuance.connect.comm.Response):void");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean processResult(Response response, Command command, int i) {
        log.d("processResult starting");
        response.command = command.command;
        response.commandFamily = command.commandFamily;
        response.identifier = command.identifier;
        response.processed = true;
        response.status = i;
        response.initialCommand = command;
        this.connectorCallback.onResponse(response);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean processResult(Response response, Command command, byte[] bArr) throws ConnectorException {
        log.d("processResult starting");
        response.command = command.command;
        response.commandFamily = command.commandFamily;
        response.identifier = command.identifier;
        response.initialCommand = command;
        if (command.dataResponse.equals(Command.DATA_SOURCE.PBUFF)) {
            processPbuffResult(bArr, response);
        } else {
            processJSONResult(bArr, response);
        }
        log.d("processResult processed:", Boolean.valueOf(response.processed));
        if (!response.processed) {
            return false;
        }
        this.connectorCallback.onResponse(response);
        return true;
    }

    public void setCompressionThreshold(int i) {
        this.compressionThreshold = i;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean shouldCompress(long j) {
        boolean z = this.compressionThreshold != -1 && ((long) this.compressionThreshold) < j;
        log.d("Size of content: ", Long.valueOf(j), z ? " compressing" : " not compressing");
        return z;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean shouldCompress(Command command) {
        if (this.compressionThreshold < 0) {
            log.d(" not compressing");
            return false;
        }
        if (command.dataSource == Command.DATA_SOURCE.PBUFF) {
            return command.bufferData.length > this.compressionThreshold;
        }
        int i = 0;
        for (Map.Entry<String, Object> entry : command.parameters.entrySet()) {
            int length = String.valueOf(entry.getValue()).length() + entry.getKey().length() + i;
            if (this.compressionThreshold < length) {
                log.d("Size of content: ", Integer.valueOf(length), " compressing");
                return true;
            }
            i = length;
        }
        log.d("Size of content: ", Integer.valueOf(i), " not compressing");
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void writeJsonBody(Command command, JsonWriter jsonWriter) throws RuntimeException {
        log.d("Write JSON command=", command.command);
        if (!command.dataSource.equals(Command.DATA_SOURCE.JSON)) {
            log.e("writeJsonBody without JSON source!");
        }
        try {
            try {
                jsonWriter.beginObject();
                if (command.requireDevice) {
                    jsonWriter.name(MessageAPI.DEVICE_ID).value(this.connectorCallback.getDeviceId());
                }
                if (command.requireSession) {
                    jsonWriter.name(MessageAPI.SESSION_ID).value(this.connectorCallback.getSessionId());
                }
                jsonWriter.name("0").value(this.requestKey);
                if (command.transactionId != null) {
                    jsonWriter.name(MessageAPI.TRANSACTION_ID).value(command.transactionId);
                }
                for (String str : command.parameters.keySet()) {
                    Object obj = command.parameters.get(str);
                    log.d("    Output key=", str, " value=", obj);
                    jsonWriter.name(str);
                    writeJsonValue(jsonWriter, obj);
                }
            } catch (IOException e) {
                log.e("IO Error streaming json: ", e.getMessage());
                throw new RuntimeException(e.getMessage());
            }
        } finally {
            try {
                jsonWriter.endObject();
            } catch (IOException e2) {
                log.e("Error writing endObject: ", e2.getMessage());
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void writeProtocolBufferBody(Command command, OutputStream outputStream) throws RuntimeException {
        try {
            outputStream.write(command.bufferData);
        } catch (IOException e) {
            log.e("IO Error streaming protocol buffer: ", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }
}
