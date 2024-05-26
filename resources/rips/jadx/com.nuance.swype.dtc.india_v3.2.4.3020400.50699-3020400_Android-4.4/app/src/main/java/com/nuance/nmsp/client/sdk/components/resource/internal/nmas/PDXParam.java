package com.nuance.nmsp.client.sdk.components.resource.internal.nmas;

/* loaded from: classes.dex */
public class PDXParam {
    protected static final byte AUDIO_PARAMETER = 1;
    protected static final byte CHOICE_PARAMETER = 3;
    protected static final byte DATA_PARAMETER = 4;
    protected static final byte DICT_PARAMETER = 5;
    protected static final byte SEQ_PARAMETER_CHUNK = 7;
    protected static final byte SEQ_PARAMETER_END = 8;
    protected static final byte SEQ_PARAMETER_START = 6;
    protected static final byte TEXT_PARAMETER = 2;
    protected static final byte TTS_PARAMETER = Byte.MAX_VALUE;
    private String a;
    private byte b;

    public PDXParam(String str) {
        this.a = str;
    }

    public PDXParam(String str, byte b) {
        this.a = str;
        this.b = b;
    }

    public String getName() {
        return this.a;
    }

    public byte getType() {
        return this.b;
    }

    public void setType(byte b) {
        this.b = b;
    }
}
