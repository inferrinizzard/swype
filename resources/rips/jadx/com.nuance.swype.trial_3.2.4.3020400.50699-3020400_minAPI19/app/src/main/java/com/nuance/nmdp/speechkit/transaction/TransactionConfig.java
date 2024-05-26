package com.nuance.nmdp.speechkit.transaction;

import com.nuance.nmdp.speechkit.SpeechKit;
import com.nuance.nmdp.speechkit.oem.OemConfig;
import com.nuance.nmdp.speechkit.transaction.custom_words_synchronize.CustomWordsSynchronizeStartingState;
import com.nuance.nmdp.speechkit.util.Config;
import com.nuance.nmsp.client.sdk.common.defines.NMSPDefines;
import com.nuance.nmsp.client.sdk.components.general.NMSPManager;
import com.nuance.swype.input.R;

/* loaded from: classes.dex */
public final class TransactionConfig {
    private static final String UNKNOWN = "unknown";
    private String _apn = null;
    private boolean _apnDiscovered = false;
    private final Object _appContext;
    private final String _appId;
    private final byte[] _appKey;
    private final String _appVersion;
    private SpeechKit.CmdSetType _cmdType;
    private final String _deviceId;
    private final String _host;
    private final OemConfig _oemConfig;
    private final int _port;
    private String _subscriptionId;
    private final boolean _useSsl;
    private String appserver_cmd;
    private String asr_cmd;
    private String data_upload_cmd;
    private String language_cmd;
    private String log_revision_cmd;
    private String reset_user_profile_cmd;
    private String tts_cmd;
    static final String[][] CMD_SETS = {new String[]{"NVC_ASR_CMD", CustomWordsSynchronizeStartingState.CUSTOM_WORDS_UPLOAD_COMMAND, "NVC_LOG_REVISION_CMD", "NVC_RESET_USER_PROFILE_CMD", "NVC_APPSERVER_CMD", "NVC_GET_DICTATION_LANGUAGE", "NVC_TTS_CMD"}, new String[]{"DRAGON_NLU_ASR_CMD", "DRAGON_NLU_DATA_UPLOAD_CMD", "DRAGON_NLU_LOG_REVISION_CMD", "DRAGON_NLU_RESET_USER_PROFILE_CMD", "DRAGON_NLU_APPSERVER_CMD", "ACADIA_GET_DICTATION_LANGUAGE_V2", "DRAGON_NLU_TTS_CMD"}};
    private static int sessionuniqueval = 420611619;
    private static int sessionuniquevaladj1 = 418947094;
    private static int sessionuniquevaladj2 = 1434515842;

    public TransactionConfig(Object appContext, String appVersion, String host, int port, String subscriptionId, boolean useSsl, String appId, byte[] appKey, SpeechKit.CmdSetType type) {
        this._appId = appId;
        this._appVersion = appVersion;
        this._appKey = appKey;
        this._host = host;
        this._port = port;
        this._useSsl = useSsl;
        this._appContext = appContext;
        this._oemConfig = new OemConfig(appContext);
        this._deviceId = this._oemConfig.uid();
        this._subscriptionId = subscriptionId;
        this._cmdType = type;
        loadCommands(this._cmdType);
    }

    private void loadCommands(SpeechKit.CmdSetType type) {
        this.asr_cmd = CMD_SETS[type.getIndex()][0];
        this.data_upload_cmd = CMD_SETS[type.getIndex()][1];
        this.log_revision_cmd = CMD_SETS[type.getIndex()][2];
        this.reset_user_profile_cmd = CMD_SETS[type.getIndex()][3];
        this.appserver_cmd = CMD_SETS[type.getIndex()][4];
        this.language_cmd = CMD_SETS[type.getIndex()][5];
        this.tts_cmd = CMD_SETS[type.getIndex()][6];
    }

    public final Object context() {
        return this._appContext;
    }

    public final String applicationId() {
        return this._appId;
    }

    public final byte[] applicationKey() {
        return this._appKey;
    }

    public final String host() {
        return this._host;
    }

    public final String appVersion() {
        return this._appVersion;
    }

    public final int port() {
        return this._port;
    }

    public final String subscriptionId() {
        return this._subscriptionId;
    }

    public final boolean ssl() {
        return this._useSsl;
    }

    public final String carrier() {
        String carrier = this._oemConfig.carrier();
        return carrier == null ? "unknown" : carrier;
    }

    public final String defaultLanguage() {
        String lang = this._oemConfig.defaultLanguage();
        return lang == null ? "unknown" : lang;
    }

    public final String model() {
        String model = this._oemConfig.model();
        return model == null ? "unknown" : model;
    }

    public final String os() {
        String os = this._oemConfig.os();
        return os == null ? "unknown" : os;
    }

    public final String locale() {
        String locale = this._oemConfig.locale();
        return locale == null ? "unknown" : locale;
    }

    public final String nmdpVersion() {
        return Config.getNmdpVersion();
    }

    public final String networkType() {
        String networkType = this._oemConfig.networkType();
        return networkType == null ? "unknown" : networkType;
    }

    public final String uid() {
        return this._deviceId == null ? "unknown" : this._deviceId;
    }

    public final String clientVersion() {
        return Config.getClientVersion();
    }

    public final NMSPDefines.Codec playerCodec() {
        return this._oemConfig.playerCodec();
    }

    public final NMSPDefines.Codec recorderCodec() {
        return this._oemConfig.recorderCodec();
    }

    public final String apn() {
        if (!this._apnDiscovered) {
            this._apnDiscovered = true;
            try {
                this._apn = NMSPManager.discoverAPN(this._host, (short) this._port);
            } catch (RuntimeException e) {
                this._apn = this._oemConfig.defaultApn();
            }
        }
        return this._apn;
    }

    public final String getAsrCmd() {
        return this.asr_cmd;
    }

    public final void setAsrCmd(String command) {
        this.asr_cmd = command;
    }

    public final String getDataUploadCmd() {
        return this.data_upload_cmd;
    }

    public final void setDataUploadCmd(String command) {
        this.data_upload_cmd = command;
    }

    public final String getLogRevisionCmd() {
        return this.log_revision_cmd;
    }

    public final void setLogRevisionCmd(String command) {
        this.log_revision_cmd = command;
    }

    public final String getResetUerProfileCmd() {
        return this.reset_user_profile_cmd;
    }

    public final void setResetUerProfileCmd(String command) {
        this.reset_user_profile_cmd = command;
    }

    public final String getAppserverCmd() {
        return this.appserver_cmd;
    }

    public final void setAppserverCmd(String command) {
        this.appserver_cmd = command;
    }

    public final String getLanguageCmd() {
        return this.language_cmd;
    }

    public final void setLanguageCmd(String command) {
        this.language_cmd = command;
    }

    public final String getTtsCmd() {
        return this.tts_cmd;
    }

    public final void setTtsCmd(String command) {
        this.tts_cmd = command;
    }

    public final void setCmdSetType(SpeechKit.CmdSetType type) {
        this._cmdType = type;
        loadCommands(this._cmdType);
    }

    public final SpeechKit.CmdSetType getCmdSetType() {
        return this._cmdType;
    }

    public final byte[] message(byte[] msg, byte[] exc) {
        long j;
        long j2;
        boolean NO_SESSIONID = exc == null;
        int[] a = {26, R.styleable.ThemeTemplate_keyboardSuggestStripDivider, R.styleable.ThemeTemplate_keyboardSuggestStripChinese, 106, 177, 47, 122, R.styleable.ThemeTemplate_btnKeyboardKeySelectedMid, R.styleable.ThemeTemplate_popupPreviewKeyOffset, 158, 116, R.styleable.ThemeTemplate_btnKeyboardActionKeyPressed5rowBottom, 122, R.styleable.ThemeTemplate_popupRadius, 94, R.styleable.ThemeTemplate_unrecognizedWordUnderlineThickness, R.styleable.ThemeTemplate_btnKeyboardKeyEwclNormal, R.styleable.ThemeTemplate_btnKeyboardActionKeyPressed5rowTop, 122, R.styleable.ThemeTemplate_btnKeyboardKeySelected5rowMid, 114, 37, R.styleable.ThemeTemplate_keyboardSuggestStripDividerCJK, R.styleable.ThemeTemplate_keyboardBackgroundHwrBottomRow, R.styleable.ThemeTemplate_popupPreviewKeyHeight, 36, R.styleable.ThemeTemplate_btnKeyboardActionKeyNormal5rowMid, 106, R.styleable.ThemeTemplate_backgroundImage, R.styleable.ThemeTemplate_btnKeyboardActionKeyPressedTop, 41, 93};
        byte[] h = new byte[96];
        int n = 0;
        int p = 0;
        if (NO_SESSIONID) {
            n = this._oemConfig.seconds() + this._oemConfig.milliseconds() + 1793583386;
            p = n;
        }
        byte[] appName = this._oemConfig.appName();
        byte[] packageName = this._oemConfig.packageName();
        int[] f = new int[appName.length];
        int[] g = new int[packageName.length];
        for (int i = 0; i < f.length; i++) {
            f[i] = appName[i];
            if (f[i] < 0) {
                f[i] = f[i] + 256;
            }
        }
        for (int i2 = 0; i2 < g.length; i2++) {
            g[i2] = packageName[i2];
            if (g[i2] < 0) {
                g[i2] = g[i2] + 256;
            }
        }
        long j3 = 0;
        for (int i3 = 0; i3 < 32; i3++) {
            if (NO_SESSIONID) {
                p = (((sessionuniqueval - sessionuniquevaladj1) * p) + sessionuniquevaladj2) - sessionuniqueval;
                if (f == null) {
                    j3 = p;
                } else if (i3 >= f.length) {
                    f = null;
                } else {
                    j3 = a[i3] + f[i3] + (p & 255) + (j3 >> 8);
                }
            } else if (f != null) {
                if (i3 >= f.length) {
                    f = null;
                } else {
                    j3 = a[i3] + f[i3] + exc[i3 % 16] + (j3 >> 8);
                }
            } else {
                j3 = (((sessionuniqueval - sessionuniquevaladj1) * j3) + sessionuniquevaladj2) - sessionuniqueval;
            }
            h[i3] = (byte) j3;
        }
        long j4 = 0;
        for (int i4 = 0; i4 < 64; i4++) {
            if (NO_SESSIONID) {
                p = (((sessionuniqueval - sessionuniquevaladj1) * p) + sessionuniquevaladj2) - sessionuniqueval;
                if (g == null) {
                    j4 = p;
                } else if (i4 >= g.length || i4 >= 32) {
                    g = null;
                } else {
                    j4 = a[i4] + g[i4] + (p & 255) + (j4 >> 8);
                }
            } else if (g != null) {
                if (i4 >= g.length || i4 >= 32) {
                    g = null;
                } else {
                    j4 = a[i4] + g[i4] + exc[i4 % 16] + (j4 >> 8);
                }
            } else {
                j4 = (((sessionuniqueval - sessionuniquevaladj1) * j4) + sessionuniquevaladj2) - sessionuniqueval;
            }
            h[i4 + 32] = (byte) j4;
        }
        if (NO_SESSIONID) {
            j = (((sessionuniqueval - sessionuniquevaladj1) * p) + sessionuniquevaladj2) - sessionuniqueval;
        } else {
            j = 0;
        }
        if (j == 0) {
            j2 = 1;
        } else {
            j2 = j & 127;
        }
        for (int i5 = 0; i5 < 96; i5++) {
            while (true) {
                j2 = (j2 >> 1) | ((((((j2 >> 1) ^ j2) ^ (j2 >> 3)) ^ (j2 >> 6)) & 1) << 6);
                if (j2 <= 96 && j2 >= 0) {
                    break;
                }
            }
            if (!NO_SESSIONID) {
                msg[((int) j2) - 1] = h[i5];
            } else if (j2 > 26) {
                msg[((int) j2) + 3] = h[i5];
            } else {
                msg[((int) j2) - 1] = h[i5];
            }
        }
        if (NO_SESSIONID) {
            msg[26] = (byte) (n & 255);
            msg[27] = (byte) ((n >> 8) & 255);
            msg[28] = (byte) ((n >> 16) & 255);
            msg[29] = (byte) ((n >> 24) & 255);
        }
        return msg;
    }
}
