package com.nuance.nmsp.client.sdk.components.resource.internal.nmas;

import com.nuance.nmsp.client.sdk.components.core.internal.pdx.PDXDictionary;
import com.nuance.nmsp.client.sdk.components.general.NMSPAudioSink;
import com.nuance.nmsp.client.sdk.components.resource.internal.common.NMSPSession;
import com.nuance.nmsp.client.sdk.components.resource.nmas.Parameter;

/* loaded from: classes.dex */
public class PDXTTSParam extends PDXParam implements Parameter {
    private PDXDictionary a;
    private NMSPAudioSink b;
    private int c;

    public PDXTTSParam(String str, NMSPSession nMSPSession, PDXDictionary pDXDictionary, NMSPAudioSink nMSPAudioSink) {
        super(str, Byte.MAX_VALUE);
        this.a = pDXDictionary;
        this.b = nMSPAudioSink;
        this.c = nMSPSession.getNewAudioId();
        pDXDictionary.addInteger("audio_id", this.c);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int getAudioId() {
        return this.c;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public NMSPAudioSink getAudioSink() {
        return this.b;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public byte[] getContent() {
        return this.a.getContent();
    }
}
