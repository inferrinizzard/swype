package com.nuance.connect.comm;

import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
public class Command {
    public static final String DELETE = "DELETE";
    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String PUT = "PUT";
    public byte[] bufferData;
    public volatile boolean canceled;
    public String command;
    public String commandFamily;
    public Map<String, String> extraHeaders;
    public String identifier;
    public HashMap<String, Object> parameters;
    public ResponseCallback responseCallback;
    public int retryCount;
    public String sendFileLocation;
    public String thirdPartyURL;
    public String transactionId;
    public int version;
    public REQUEST_TYPE requestType = REQUEST_TYPE.BACKGROUND;
    public DATA_SOURCE dataSource = DATA_SOURCE.JSON;
    public DATA_SOURCE dataResponse = DATA_SOURCE.JSON;
    public boolean requireDevice = false;
    public boolean needDevice = true;
    public boolean requireSession = true;
    public String method = "POST";
    public boolean hasBody = true;
    public boolean handleIOExceptionInConnector = true;
    public boolean allowDuplicateOfCommand = true;
    public boolean notifyDownloadStatus = false;
    public boolean sent = false;
    public boolean wifiOnly = false;

    /* loaded from: classes.dex */
    public enum DATA_SOURCE {
        NONE,
        FILE,
        JSON,
        PBUFF
    }

    /* loaded from: classes.dex */
    public enum REQUEST_TYPE {
        CRITICAL,
        USER,
        BACKGROUND
    }

    public String getContentType() {
        switch (this.dataSource) {
            case FILE:
                return "application/octet-stream";
            case PBUFF:
                return "application/x-protobuf";
            default:
                return "text/json";
        }
    }

    public void setOutgoingFileLocation(String str) {
        this.sendFileLocation = str;
        this.method = "PUT";
        this.dataSource = DATA_SOURCE.FILE;
    }

    public String shortString() {
        return this.commandFamily + "/" + this.version + "/" + this.command;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Command\ncommandFamily = ").append(this.commandFamily).append("\nversion = ").append(String.valueOf(this.version)).append("\ncommand = ").append(this.command).append("\ndeviceId = ").append(this.requireDevice).append("\nsessionId = ").append(this.requireSession).append("\nthirdPartyURL = ").append(this.thirdPartyURL).append("\nretryCount = ").append(this.retryCount).append("\nidentifier = ").append(this.identifier).append("\n\nsent = ").append(this.sent).append("\ncanceled = ").append(this.canceled).append("\nwifiOnly = ").append(this.wifiOnly).append("\nsendFileLocation = ").append(this.sendFileLocation).append("\n");
        return sb.toString();
    }
}
