package com.nuance.speech;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.text.TextUtils;
import android.util.Xml;
import com.facebook.internal.NativeProtocol;
import com.nuance.connect.common.Strings;
import com.nuance.nmdp.speechkit.SpeechKit;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.input.InputMethods;
import com.nuance.swype.input.R;
import com.nuance.swype.util.LogManager;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.xmlpull.v1.XmlPullParserException;

/* loaded from: classes.dex */
public final class SpeechInfo {
    private static final LogManager.Log log = LogManager.getLog("SpeechInfo");
    private String appName;
    private boolean defaultCustomWordsSync;
    short defaultPort;
    SpeechKit.PartialResultsMode defaultResultMode;
    String defaultServer;
    SpeechKit.CmdSetType defaultSpeechCmd;
    private final Map<String, SpeechServerInfo> speechServerMap;

    /* loaded from: classes.dex */
    public static class SpeechServerInfo {
        String abbrDisplayName;
        String displayName;
        boolean isCustomWordsSyncSupport;
        String langCode;
        String langName;
        short port;
        SpeechKit.PartialResultsMode responseMode;
        String server;
        SpeechKit.CmdSetType speechCommand;

        public String toString() {
            return String.format("langName: %s, langCode: %s, displayName: %s, dns: %s, portId: %d, customword = %b, responseMode = %s, cmd: %s", this.langName, this.langCode, this.displayName, this.server, Short.valueOf(this.port), Boolean.valueOf(this.isCustomWordsSyncSupport), this.responseMode, this.speechCommand);
        }

        public String server() {
            return this.server;
        }

        public short port() {
            return this.port;
        }

        public String langName() {
            return this.langName;
        }

        public String displayName() {
            return this.displayName;
        }

        public String abbrDisplayName() {
            return this.abbrDisplayName;
        }

        public String langCode() {
            return this.langCode;
        }
    }

    @SuppressLint({"PrivateResource"})
    public SpeechInfo(Context context) {
        this(context, R.xml.speech_info);
    }

    @SuppressLint({"PrivateResource"})
    public SpeechInfo(Context context, int speechInfoResId) {
        this.speechServerMap = new HashMap();
        InputMethods inputmethods = IMEApplication.from(context).getInputMethods();
        try {
            Resources res = context.getResources();
            XmlResourceParser xmlparser = res.getXml(speechInfoResId);
            while (true) {
                try {
                    int event = xmlparser.next();
                    if (event == 1) {
                        break;
                    }
                    if (event == 2) {
                        String tag = xmlparser.getName();
                        if ("speech_info".equals(tag)) {
                            TypedArray a = res.obtainAttributes(Xml.asAttributeSet(xmlparser), R.styleable.dragon_speech);
                            this.defaultCustomWordsSync = a.getBoolean(R.styleable.dragon_speech_sync_custom_words, false);
                            this.defaultSpeechCmd = getSpeechCmd(a, SpeechKit.CmdSetType.NVC);
                            this.defaultResultMode = getPartialResultsMode(a, SpeechKit.PartialResultsMode.NO_PARTIAL_RESULTS);
                            a.recycle();
                            this.appName = xmlparser.getAttributeValue(null, NativeProtocol.BRIDGE_ARG_APP_NAME_STRING);
                            log.d("appName:", this.appName);
                            this.defaultServer = xmlparser.getAttributeValue(null, "default_gateway_ip_addr");
                            this.defaultPort = Short.decode(xmlparser.getAttributeValue(null, "default_port_id")).shortValue();
                        } else if (Strings.MESSAGE_BUNDLE_LANGUAGE.equals(tag)) {
                            SpeechServerInfo server = new SpeechServerInfo();
                            TypedArray a2 = res.obtainAttributes(Xml.asAttributeSet(xmlparser), R.styleable.dragon_speech);
                            server.isCustomWordsSyncSupport = a2.getBoolean(R.styleable.dragon_speech_sync_custom_words, this.defaultCustomWordsSync);
                            server.speechCommand = getSpeechCmd(a2, this.defaultSpeechCmd);
                            server.responseMode = getPartialResultsMode(a2, this.defaultResultMode);
                            a2.recycle();
                            server.langCode = xmlparser.getAttributeValue(null, "lang_code");
                            server.server = xmlparser.getAttributeValue(null, "lang_server");
                            if (TextUtils.isEmpty(server.server)) {
                                server.server = this.defaultServer;
                            }
                            String value = xmlparser.getAttributeValue(null, "port");
                            if (TextUtils.isEmpty(value)) {
                                server.port = this.defaultPort;
                            } else {
                                server.port = Short.decode(value).shortValue();
                            }
                            String langName = xmlparser.getAttributeValue(null, "lang_name");
                            String displayName = xmlparser.getAttributeValue(null, "display_name");
                            InputMethods.Language lang = inputmethods.getAllLanguages().get(langName);
                            if (TextUtils.isEmpty(displayName)) {
                                if (lang != null) {
                                    displayName = lang.mNativeLanguageName;
                                } else {
                                    displayName = langName;
                                }
                            }
                            String abbrDisplayName = xmlparser.getAttributeValue(null, "abbr_display_name");
                            if (TextUtils.isEmpty(abbrDisplayName)) {
                                if (lang != null) {
                                    abbrDisplayName = lang.mLanguageAbbr;
                                } else {
                                    abbrDisplayName = displayName;
                                }
                            }
                            server.langName = langName;
                            server.displayName = displayName;
                            server.abbrDisplayName = abbrDisplayName;
                            this.speechServerMap.put(langName, server);
                            log.d(server.toString());
                        }
                    }
                } finally {
                    if (xmlparser != null) {
                        xmlparser.close();
                    }
                }
            }
        } catch (IOException e) {
        } catch (XmlPullParserException e2) {
        }
    }

    @SuppressLint({"PrivateResource"})
    private SpeechKit.CmdSetType getSpeechCmd(TypedArray a, SpeechKit.CmdSetType defaultValue) {
        int value = a.getInt(R.styleable.dragon_speech_speech_cmd, 0);
        if (value != 0) {
            if (value == 1) {
                SpeechKit.CmdSetType defaultValue2 = SpeechKit.CmdSetType.DRAGON_NLU;
                return defaultValue2;
            }
            SpeechKit.CmdSetType defaultValue3 = SpeechKit.CmdSetType.NVC;
            return defaultValue3;
        }
        return defaultValue;
    }

    @SuppressLint({"PrivateResource"})
    private SpeechKit.PartialResultsMode getPartialResultsMode(TypedArray a, SpeechKit.PartialResultsMode defaultValue) {
        switch (a.getInt(R.styleable.dragon_speech_speech_result_mode, 0)) {
            case 0:
                return defaultValue;
            case 1:
                SpeechKit.PartialResultsMode resultMode = SpeechKit.PartialResultsMode.UTTERANCE_DETECTION_DEFAULT;
                return resultMode;
            case 2:
                SpeechKit.PartialResultsMode resultMode2 = SpeechKit.PartialResultsMode.UTTERANCE_DETECTION_VERY_AGRESSIVE;
                return resultMode2;
            case 3:
                SpeechKit.PartialResultsMode resultMode3 = SpeechKit.PartialResultsMode.UTTERANCE_DETECTION_AGRESSIVE;
                return resultMode3;
            case 4:
                SpeechKit.PartialResultsMode resultMode4 = SpeechKit.PartialResultsMode.UTTERANCE_DETECTION_AVERAGE;
                return resultMode4;
            case 5:
                SpeechKit.PartialResultsMode resultMode5 = SpeechKit.PartialResultsMode.UTTERANCE_DETECTION_CONSERVATIVE;
                return resultMode5;
            case 6:
                SpeechKit.PartialResultsMode resultMode6 = SpeechKit.PartialResultsMode.CONTINUOUS_STREAMING_RESULTS;
                return resultMode6;
            default:
                SpeechKit.PartialResultsMode resultMode7 = SpeechKit.PartialResultsMode.NO_PARTIAL_RESULTS;
                return resultMode7;
        }
    }

    public static SpeechInfo makeInstance(Context context) {
        return new SpeechInfo(context);
    }

    public final Map<String, SpeechServerInfo> getSpeechInfoTable() {
        return new HashMap(this.speechServerMap);
    }

    public final String getAppName() {
        return this.appName;
    }
}
