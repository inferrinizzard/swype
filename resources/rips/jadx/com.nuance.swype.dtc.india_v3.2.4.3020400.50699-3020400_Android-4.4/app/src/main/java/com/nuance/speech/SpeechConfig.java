package com.nuance.speech;

import android.content.Context;
import android.util.Log;
import com.nuance.input.swypecorelib.SwypeCoreLibrary;
import com.nuance.nmdp.speechkit.RecognizerConstants;
import com.nuance.nmdp.speechkit.SpeechKit;
import com.nuance.speech.SpeechInfo;
import com.nuance.swype.input.InputFieldInfo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
public class SpeechConfig {
    protected final byte[] appKey;
    protected final String appName;
    protected final Map<String, SpeechInfo.SpeechServerInfo> speechServerMap;

    public SpeechConfig(String appName, byte[] appKey, Map<String, SpeechInfo.SpeechServerInfo> speechServerMap) {
        this.appName = appName;
        this.appKey = appKey;
        this.speechServerMap = speechServerMap;
    }

    public SpeechConfig(String appName, Map<String, SpeechInfo.SpeechServerInfo> speechServerMap) {
        this(appName, null, speechServerMap);
    }

    public String getAppName() {
        return this.appName;
    }

    public boolean isCustomWordsSynchronizationSupported(String langName) {
        return getServerInfo(langName).isCustomWordsSyncSupport;
    }

    public SpeechKit.CmdSetType getSpeechCmd(String langName) {
        return getServerInfo(langName).speechCommand;
    }

    public SpeechKit.PartialResultsMode getResponseMode(String langName) {
        return getServerInfo(langName).responseMode;
    }

    public String getLanguageCode(String langName) {
        return getServerInfo(langName).langCode;
    }

    public String getLanguageDisplayName(String langName) {
        return getServerInfo(langName).displayName;
    }

    public String getLanguageAbbrDisplayName(String langName) {
        return getServerInfo(langName).abbrDisplayName;
    }

    public String getLanguageServerName(String langName) {
        return getServerInfo(langName).server;
    }

    public short getPortId(String langName) {
        return getServerInfo(langName).port;
    }

    public boolean isLanguageSupported(String langName) {
        return this.speechServerMap.containsKey(langName);
    }

    protected SpeechInfo.SpeechServerInfo getServerInfo(String langName) {
        SpeechInfo.SpeechServerInfo server = this.speechServerMap.get(langName);
        if (server == null) {
            return this.speechServerMap.entrySet().iterator().next().getValue();
        }
        return server;
    }

    public List<SpeechInfo.SpeechServerInfo> getSpeechServerInfoList() {
        return new ArrayList(this.speechServerMap.values());
    }

    public String getDictationType(Context context, InputFieldInfo inputFieldInfo) {
        Log.i("TEST", "inputFieldInfo.getInputType() = " + inputFieldInfo.getInputType());
        return inputFieldInfo.getInputType().equals(InputFieldInfo.INPUT_TYPE_WEB_SEARCH) ? RecognizerConstants.RecognizerType.Search : RecognizerConstants.RecognizerType.Dictation;
    }

    public byte[] getAppKey(Context context) {
        return this.appKey != null ? Arrays.copyOf(this.appKey, this.appKey.length) : SwypeCoreLibrary.getSpeechKey(context);
    }

    public static SpeechConfig createInstance(Context context) {
        SpeechInfo speechInfo = SpeechInfo.makeInstance(context);
        return new SpeechConfig(speechInfo.getAppName(), speechInfo.getSpeechInfoTable());
    }
}
