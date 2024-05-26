package com.nuance.connect.comm;

import java.io.File;
import java.util.HashMap;

/* loaded from: classes.dex */
public class Response {
    public String command;
    public String commandFamily;
    public int delayedFor;
    public String deviceId;
    public File file;
    public String fileLocation;
    public String identifier;
    public Command initialCommand;
    public HashMap<String, Object> parameters;
    public boolean processed = true;
    public int randomDelayedFor;
    public byte[] responseArray;
    public String sessionId;
    public int status;
    public String statusMessage;
    public String thirdPartyURL;
    public String transactionId;
    public int version;

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Response Object:\n");
        sb.append("status = ").append(this.status).append("\n");
        sb.append("commandFamily = ").append(this.commandFamily).append("\n");
        sb.append("version = ").append(String.valueOf(this.version)).append("\n");
        sb.append("command = ").append(this.command).append("\n");
        sb.append("deviceId = ").append(this.deviceId).append("\n");
        sb.append("sessionId = ").append(this.sessionId).append("\n");
        sb.append("transactionId = ").append(this.transactionId).append("\n");
        sb.append("delayedFor = ").append(this.delayedFor).append("\n");
        sb.append("fileLocation = ").append(this.fileLocation).append("\n");
        sb.append("thirdPartyURL = ").append(this.thirdPartyURL).append("\n");
        sb.append("identifier = ").append(this.identifier).append("\n");
        return sb.toString();
    }
}
