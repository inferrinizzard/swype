package com.nuance.nmsp.client.sdk.components.resource.internal.nmas;

import com.nuance.nmsp.client.sdk.common.oem.api.LogFactory;
import com.nuance.nmsp.client.sdk.common.oem.api.MessageSystem;
import com.nuance.nmsp.client.sdk.common.util.Util;
import com.nuance.nmsp.client.sdk.components.general.TransactionProcessingException;
import com.nuance.nmsp.client.sdk.components.resource.internal.common.NMSPSession;
import com.nuance.nmsp.client.sdk.components.resource.nmas.AudioParam;
import java.io.ByteArrayOutputStream;

/* loaded from: classes.dex */
public class PDXAudioParam extends PDXParam implements MessageSystem.MessageHandler, AudioParam {
    private static final LogFactory.Log a = LogFactory.getLog(PDXAudioParam.class);
    private int b;
    private NMSPSession c;
    private boolean d;
    private MessageSystem e;

    public PDXAudioParam(String str, NMSPSession nMSPSession, MessageSystem messageSystem) {
        super(str, (byte) 1);
        this.d = false;
        this.c = nMSPSession;
        this.e = messageSystem;
        this.b = nMSPSession.getNewAudioId();
        messageSystem.send(new MessageSystem.MessageData((byte) 1, null), this, Thread.currentThread(), messageSystem.getVRAddr()[0]);
    }

    @Override // com.nuance.nmsp.client.sdk.components.general.NMSPAudioSink
    public void addAudioBuf(byte[] bArr, int i, int i2, boolean z) throws TransactionProcessingException {
        if (bArr == null && !z) {
            a.error("PDXAudioParam.addAudioBuf() in (NMSPAudioSink)PDXAudioParam.addAudioBuf(), the param \"buffer\" is null.");
            throw new NullPointerException("in (NMSPAudioSink)PDXAudioParam.addAudioBuf(), the param \"buffer\" is null.");
        }
        if (bArr != null && i < 0) {
            a.error("PDXAudioParam.addAudioBuf() the offset of the \"buffer\" is less than 0");
            throw new IllegalArgumentException("the offset of the \"buffer\" is less than 0");
        }
        if (bArr != null && i2 <= 0) {
            a.error("PDXAudioParam.addAudioBuf() the indicated length of the \"buffer\" is less than 1 byte");
            throw new IllegalArgumentException("the indicated length of the \"buffer\" is less than 1 byte");
        }
        if (this.d) {
            a.error("PDXAudioParam.addAudioBuf() last audio buffer already added!");
            throw new TransactionProcessingException("last audio buffer already added!");
        }
        if (z) {
            this.d = true;
        }
        byte[] bArr2 = null;
        if (bArr != null) {
            bArr2 = new byte[i2];
            System.arraycopy(bArr, i, bArr2, 0, i2);
        }
        this.e.send(new MessageSystem.MessageData((byte) 2, new Object[]{bArr2, new Boolean(z)}), this, Thread.currentThread(), this.e.getVRAddr()[0]);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int getBufferId() {
        return this.b;
    }

    @Override // com.nuance.nmsp.client.sdk.common.oem.api.MessageSystem.MessageHandler
    public void handleMessage(Object obj, Object obj2) {
        MessageSystem.MessageData messageData = (MessageSystem.MessageData) obj;
        switch (messageData.command) {
            case 1:
                this.c.vapRecordBegin(this.b);
                return;
            case 2:
                Object[] objArr = (Object[]) messageData.data;
                byte[] bArr = (byte[]) objArr[0];
                boolean booleanValue = ((Boolean) objArr[1]).booleanValue();
                if (bArr != null) {
                    if (Util.isSpeexCodec(this.c.getInputCodec()) || Util.isOpusCodec(this.c.getInputCodec())) {
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        if (bArr.length < 128) {
                            byteArrayOutputStream.write(bArr.length & 127);
                        } else if (bArr.length < 16384) {
                            byteArrayOutputStream.write(((bArr.length >> 7) & 127) | 128);
                            byteArrayOutputStream.write(bArr.length & 127);
                        } else if (bArr.length < 2097152) {
                            byteArrayOutputStream.write(((bArr.length >> 14) & 127) | 128);
                            byteArrayOutputStream.write(((bArr.length >> 7) & 127) | 128);
                            byteArrayOutputStream.write(bArr.length & 127);
                        } else {
                            if (bArr.length >= 268435456) {
                                a.error("buffer size is too big!");
                                return;
                            }
                            byteArrayOutputStream.write(((bArr.length >> 21) & 127) | 128);
                            byteArrayOutputStream.write(((bArr.length >> 14) & 127) | 128);
                            byteArrayOutputStream.write(((bArr.length >> 7) & 127) | 128);
                            byteArrayOutputStream.write(bArr.length & 127);
                        }
                        byteArrayOutputStream.write(bArr, 0, bArr.length);
                        this.c.vapRecord(byteArrayOutputStream.toByteArray(), this.b);
                    } else {
                        this.c.vapRecord(bArr, this.b);
                    }
                }
                if (booleanValue) {
                    this.c.vapRecordEnd(this.b);
                    return;
                }
                return;
            default:
                return;
        }
    }
}
