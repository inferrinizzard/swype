package com.nuance.nmsp.client.sdk.components.internal.audioplayback;

import com.nuance.nmsp.client.sdk.common.defines.NMSPDefines;
import com.nuance.nmsp.client.sdk.common.oem.api.AudioSystem;
import com.nuance.nmsp.client.sdk.common.oem.api.LogFactory;
import com.nuance.nmsp.client.sdk.common.oem.api.MessageSystem;
import com.nuance.nmsp.client.sdk.common.util.Util;
import com.nuance.nmsp.client.sdk.components.audioplayback.NMSPAudioPlaybackListener;
import com.nuance.nmsp.client.sdk.components.audioplayback.Player;
import com.nuance.nmsp.client.sdk.components.core.calllog.SessionEvent;
import com.nuance.nmsp.client.sdk.components.general.NMSPAudioSink;
import com.nuance.nmsp.client.sdk.components.general.TransactionProcessingException;
import com.nuance.nmsp.client.sdk.components.resource.common.Manager;
import com.nuance.nmsp.client.sdk.components.resource.internal.common.ManagerImpl;
import com.nuance.nmsp.client.sdk.oem.AudioSystemOEM;
import com.nuance.nmsp.client.sdk.oem.BluetoothSystemOEM;
import java.io.ByteArrayOutputStream;
import java.util.LinkedList;
import java.util.Vector;

/* loaded from: classes.dex */
public class PlayerImpl implements AudioSystem.AudioCallback, AudioSystem.DoneCallback, AudioSystem.StopCallback, MessageSystem.MessageHandler, Player, NMSPAudioSink {
    public static final short STATE_INIT = -1;
    public static final short STATE_PAUSED = 2;
    public static final short STATE_RESUMING = 3;
    public static final short STATE_STARTED = 1;
    public static final short STATE_STARTING = 0;
    public static final short STATE_STOPPED = 5;
    public static final short STATE_STOPPING = 4;
    private static final LogFactory.Log a = LogFactory.getLog(PlayerImpl.class);
    private NMSPDefines.Codec b;
    private AudioSystem c;
    private NMSPAudioPlaybackListener d;
    private LinkedList<byte[]> h;
    private Vector k;
    private MessageSystem l;
    private AudioSystem.OutputDevice n;
    private SessionEvent o;
    private short j = -1;
    private Object m = new Object();
    private ByteArrayOutputStream e = new ByteArrayOutputStream();
    private Vector f = new Vector();
    private boolean i = false;
    private boolean g = false;

    public PlayerImpl(NMSPAudioPlaybackListener nMSPAudioPlaybackListener, Manager manager, Vector vector, NMSPDefines.AudioSystem.OutputDevice outputDevice) {
        this.l = null;
        this.b = ((ManagerImpl) manager).getOutputCodec();
        this.d = nMSPAudioPlaybackListener;
        this.l = ((ManagerImpl) manager).getMsgSys();
        this.k = vector;
        this.c = new AudioSystemOEM(this.l, this.b, vector);
        if (outputDevice.equals(NMSPDefines.AudioSystem.OutputDevice.BLUETOOTH_A2DP)) {
            this.n = AudioSystem.OutputDevice.BLUETOOTH_A2DP;
        } else if (outputDevice.equals(NMSPDefines.AudioSystem.OutputDevice.BLUETOOTH_HEADSET)) {
            this.n = AudioSystem.OutputDevice.BLUETOOTH_HEADSET;
        } else if (outputDevice.equals(NMSPDefines.AudioSystem.OutputDevice.NETWORK)) {
            this.n = AudioSystem.OutputDevice.NETWORK;
        } else if (outputDevice.equals(NMSPDefines.AudioSystem.OutputDevice.SPEAKER)) {
            this.n = AudioSystem.OutputDevice.SPEAKER;
        } else if (outputDevice.equals(NMSPDefines.AudioSystem.OutputDevice.WIRED_HEADSET)) {
            this.n = AudioSystem.OutputDevice.WIRED_HEADSET;
        }
        if (Util.isSpeexCodec(this.b)) {
            this.h = new LinkedList<>();
        }
        SessionEvent calllogSession = ((ManagerImpl) manager).getCalllogSession();
        if (calllogSession == null || calllogSession == null) {
            return;
        }
        this.o = calllogSession.createChildEventBuilder(SessionEvent.NMSP_CALLLOG_PLAYER_EVENT).commit();
    }

    private void a(String str) {
        if (this.d != null) {
            try {
                this.d.playerUpdate(this, str);
            } catch (Throwable th) {
                if (a.isErrorEnabled()) {
                    a.error("Got an exp while calling NMSPAudioPlaybackListener.playerUpdate(" + str + ")[" + th.getClass().getName() + "] msg [" + th.getMessage() + "]");
                }
            }
        }
    }

    private void b(String str) {
        if (this.o != null) {
            this.o.createChildEventBuilder(str).commit();
        }
    }

    @Override // com.nuance.nmsp.client.sdk.components.general.NMSPAudioSink
    public void addAudioBuf(byte[] bArr, int i, int i2, boolean z) throws TransactionProcessingException {
        if (a.isInfoEnabled()) {
            a.info("addAudioBuf(" + bArr + ", " + z + ")");
        }
        if (bArr == null && !z) {
            throw new NullPointerException("buffer is null!");
        }
        if (bArr != null && i < 0) {
            throw new IllegalArgumentException("offset cannot be negative!!!");
        }
        if (bArr != null && i2 <= 0) {
            throw new IllegalArgumentException("length can only be positive!!!");
        }
        if ((this.b == NMSPDefines.Codec.SPEEX_11K || this.b == NMSPDefines.Codec.SPEEX_16K || this.b == NMSPDefines.Codec.SPEEX_8K) && i2 > 153600) {
            a(Player.PLAYBACK_ERROR);
            stop();
        } else {
            if (this.g) {
                throw new TransactionProcessingException("audio player is full, the last buffer has already apended!");
            }
            if (!this.g && z) {
                this.g = true;
            }
            byte[] bArr2 = null;
            if (bArr != null) {
                bArr2 = new byte[i2];
                System.arraycopy(bArr, i, bArr2, 0, i2);
            }
            this.l.send(new MessageSystem.MessageData((byte) 6, new Object[]{bArr2, new Boolean(z)}), this, Thread.currentThread(), this.l.getVRAddr()[0]);
        }
    }

    @Override // com.nuance.nmsp.client.sdk.common.oem.api.AudioSystem.AudioCallback
    public void audioCallback(Vector vector, Vector vector2, AudioSystem.IntegerMutable integerMutable, AudioSystem.IntegerMutable integerMutable2, Float f, Object obj) {
        int i;
        int i2 = 0;
        if (a.isDebugEnabled()) {
            a.debug("audio call back is called time: [" + System.currentTimeMillis() + "]");
        }
        if (this.j == 0) {
            this.j = (short) 1;
            a("STARTED");
        }
        if (Util.isAMRCodec(this.b) || Util.isSpeexCodec(this.b) || Util.isMP3Codec(this.b)) {
            vector = vector2;
        } else if (!Util.isPCMCodec(this.b)) {
            vector = null;
        }
        synchronized (this.m) {
            if (this.f.size() == 0) {
                i = this.i ? -1 : 0;
            } else {
                int size = this.f.size();
                int i3 = 0;
                while (i3 < size) {
                    byte[] bArr = (byte[]) this.f.elementAt(0);
                    this.f.removeElementAt(0);
                    vector.addElement(bArr);
                    i3++;
                    i2 = bArr.length + i2;
                }
                if (a.isDebugEnabled()) {
                    a.debug("feed oem audio data len [" + i2 + "]");
                }
                i = i2;
            }
        }
        if (i > 0) {
            a(Player.BUFFER_PLAYED);
        }
        if (Util.isAMRCodec(this.b) || Util.isSpeexCodec(this.b) || Util.isMP3Codec(this.b)) {
            integerMutable2.i = i;
        } else if (Util.isPCMCodec(this.b)) {
            integerMutable.i = i;
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:72:0x00fb A[Catch: all -> 0x00d2, TryCatch #1 {, blocks: (B:53:0x00c3, B:55:0x00cb, B:58:0x00d0, B:61:0x00d7, B:63:0x00dd, B:65:0x00e3, B:67:0x00ef, B:70:0x00f8, B:72:0x00fb, B:73:0x0102, B:75:0x010a, B:76:0x0132, B:77:0x016e, B:81:0x0178, B:83:0x0180, B:84:0x0184, B:86:0x018c, B:90:0x0139), top: B:52:0x00c3, inners: #2 }] */
    /* JADX WARN: Removed duplicated region for block: B:75:0x010a A[Catch: all -> 0x00d2, TryCatch #1 {, blocks: (B:53:0x00c3, B:55:0x00cb, B:58:0x00d0, B:61:0x00d7, B:63:0x00dd, B:65:0x00e3, B:67:0x00ef, B:70:0x00f8, B:72:0x00fb, B:73:0x0102, B:75:0x010a, B:76:0x0132, B:77:0x016e, B:81:0x0178, B:83:0x0180, B:84:0x0184, B:86:0x018c, B:90:0x0139), top: B:52:0x00c3, inners: #2 }] */
    @Override // com.nuance.nmsp.client.sdk.common.oem.api.AudioSystem.AudioCallback
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void audioCallback(byte[] r9, java.lang.Object r10, com.nuance.nmsp.client.sdk.common.oem.api.AudioSystem.IntegerMutable r11, com.nuance.nmsp.client.sdk.common.oem.api.AudioSystem.IntegerMutable r12, java.lang.Float r13, java.lang.Object r14) {
        /*
            Method dump skipped, instructions count: 415
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.nuance.nmsp.client.sdk.components.internal.audioplayback.PlayerImpl.audioCallback(byte[], java.lang.Object, com.nuance.nmsp.client.sdk.common.oem.api.AudioSystem$IntegerMutable, com.nuance.nmsp.client.sdk.common.oem.api.AudioSystem$IntegerMutable, java.lang.Float, java.lang.Object):void");
    }

    @Override // com.nuance.nmsp.client.sdk.common.oem.api.AudioSystem.DoneCallback
    public void doneCallback(AudioSystem.AudioStatus audioStatus, Object obj) {
        if (a.isDebugEnabled()) {
            a.debug("PlayerImpl.doneCallback()");
        }
        this.l.send(new MessageSystem.MessageData((byte) 7, new Object[]{audioStatus, obj}), this, Thread.currentThread(), this.l.getVRAddr()[0]);
    }

    public NMSPAudioSink getAudioSink() {
        return this;
    }

    @Override // com.nuance.nmsp.client.sdk.common.oem.api.MessageSystem.MessageHandler
    public void handleMessage(Object obj, Object obj2) {
        MessageSystem.MessageData messageData = (MessageSystem.MessageData) obj;
        if (a.isDebugEnabled()) {
            a.debug("\nXMode handleMessage - [" + ((int) messageData.command) + "]");
        }
        switch (messageData.command) {
            case 1:
                if (a.isDebugEnabled()) {
                    a.debug("PlayerImpl.handleStartPlayback()");
                }
                if (new BluetoothSystemOEM(this.k).isBluetoothHeadsetConnected()) {
                    this.b = Util.adjustCodecForBluetooth(this.b);
                }
                if (this.j == -1) {
                    this.j = (short) 0;
                } else {
                    if (this.j != 2) {
                        this.j = (short) 5;
                        if (a.isErrorEnabled()) {
                            a.error("PlayImpl.handleStartPlayback() wrong state [state != STATE_INIT]!!!");
                        }
                        a(Player.PLAYBACK_ERROR);
                        return;
                    }
                    this.j = (short) 3;
                }
                if (this.j != 0 && this.j != 3) {
                    this.j = (short) 5;
                    if (a.isErrorEnabled()) {
                        a.error("PlayImpl.handleStartPlayback() wrong state [state != STATE_STARTING]!!!");
                    }
                    a(Player.PLAYBACK_ERROR);
                    return;
                }
                if (this.j == 0) {
                    if (!this.c.startPlayback(this.n, this, this, null)) {
                        this.j = (short) 5;
                        if (a.isErrorEnabled()) {
                            a.error("PlayImpl.handleStartPlayback() audioSys.startPlayback() return false!!!");
                        }
                        a(Player.PLAYBACK_ERROR);
                        return;
                    }
                } else if (!this.c.pausePlayback(0)) {
                    this.j = (short) 5;
                    if (a.isErrorEnabled()) {
                        a.error("PlayImpl.handleStartPlayback() audioSys.pausePlayback() return false!!!");
                    }
                    a(Player.PLAYBACK_ERROR);
                    return;
                }
                b("StreamStart");
                return;
            case 2:
                if (a.isDebugEnabled()) {
                    a.debug("PlayerImpl.handleStopPlayback()");
                }
                if (this.j == 5 || this.j == 4) {
                    return;
                }
                this.j = (short) 4;
                this.c.stopPlayback(this, null);
                return;
            case 3:
                if (a.isDebugEnabled()) {
                    a.debug("PlayerImpl.handlePausePlayback()");
                }
                if (this.j == 5 || this.j == 4) {
                    this.j = (short) 5;
                    if (a.isErrorEnabled()) {
                        a.error("PlayImpl.handlePausePlayback() wrong state [state == " + ((int) this.j) + "]!!!");
                    }
                    a(Player.PLAYBACK_ERROR);
                    return;
                }
                this.j = (short) 2;
                if (this.c.pausePlayback(1)) {
                    return;
                }
                this.j = (short) 5;
                if (a.isErrorEnabled()) {
                    a.error("PlayImpl.handleStartPlayback() audioSys.pausePlayback() return false!!!");
                }
                a(Player.PLAYBACK_ERROR);
                return;
            case 4:
                if (a.isDebugEnabled()) {
                    a.debug("PlayerImpl.handlePrevious()");
                }
                if (this.j == 5 || this.j == 4) {
                    this.j = (short) 5;
                    if (a.isErrorEnabled()) {
                        a.error("PlayImpl.handlePausePlayback() wrong state [state == " + ((int) this.j) + "]!!!");
                    }
                    a(Player.PLAYBACK_ERROR);
                    return;
                }
                if (this.c.previousPlayback()) {
                    return;
                }
                this.j = (short) 5;
                if (a.isErrorEnabled()) {
                    a.error("PlayImpl.handleStartPlayback() audioSys.previousPlayback() return false!!!");
                }
                a(Player.PLAYBACK_ERROR);
                return;
            case 5:
                if (a.isDebugEnabled()) {
                    a.debug("PlayerImpl.handleNext()");
                }
                if (this.j == 5 || this.j == 4) {
                    this.j = (short) 5;
                    if (a.isErrorEnabled()) {
                        a.error("PlayImpl.handlePausePlayback() wrong state [state == " + ((int) this.j) + "]!!!");
                    }
                    a(Player.PLAYBACK_ERROR);
                    return;
                }
                if (this.c.nextPlayback()) {
                    return;
                }
                this.j = (short) 5;
                if (a.isErrorEnabled()) {
                    a.error("PlayImpl.handleStartPlayback() audioSys.nextPlayback() return false!!!");
                }
                a(Player.PLAYBACK_ERROR);
                return;
            case 6:
                Object[] objArr = (Object[]) messageData.data;
                byte[] bArr = (byte[]) objArr[0];
                boolean booleanValue = ((Boolean) objArr[1]).booleanValue();
                if (a.isDebugEnabled()) {
                    a.debug("PlayerImpl.handleAddBuffer()");
                }
                synchronized (this.m) {
                    if (bArr != null) {
                        if (Util.isAMRCodec(this.b) && bArr[0] == 35 && bArr[1] == 33 && bArr[2] == 65 && bArr[3] == 77 && bArr[4] == 82 && bArr[5] == 10) {
                            byte[] bArr2 = new byte[bArr.length - 6];
                            System.arraycopy(bArr, 6, bArr2, 0, bArr2.length);
                            bArr = bArr2;
                        }
                        if (Util.isSpeexCodec(this.b)) {
                            this.h.add(bArr);
                        } else {
                            this.e.write(bArr, 0, bArr.length);
                            this.f.addElement(bArr);
                        }
                    }
                    if (booleanValue) {
                        this.i = true;
                    }
                }
                return;
            case 7:
                AudioSystem.AudioStatus audioStatus = (AudioSystem.AudioStatus) ((Object[]) messageData.data)[0];
                if (a.isDebugEnabled()) {
                    a.debug("audio done call back is called");
                }
                if (audioStatus.equals(AudioSystem.AudioStatus.AUDIO_ERROR)) {
                    this.j = (short) 5;
                    a(Player.PLAYBACK_ERROR);
                    return;
                } else {
                    this.j = (short) 5;
                    b("StreamStop");
                    a("STOPPED");
                    return;
                }
            default:
                return;
        }
    }

    public void next() {
        if (a.isDebugEnabled()) {
            a.debug("PlayerImpl.next()");
        }
        this.l.send(new MessageSystem.MessageData((byte) 5, this), this, Thread.currentThread(), this.l.getVRAddr()[0]);
    }

    public void pause() {
        if (a.isDebugEnabled()) {
            a.debug("PlayerImpl.pause()");
        }
        this.l.send(new MessageSystem.MessageData((byte) 3, this), this, Thread.currentThread(), this.l.getVRAddr()[0]);
    }

    public void previous() {
        if (a.isDebugEnabled()) {
            a.debug("PlayerImpl.previous()");
        }
        this.l.send(new MessageSystem.MessageData((byte) 4, this), this, Thread.currentThread(), this.l.getVRAddr()[0]);
    }

    @Override // com.nuance.nmsp.client.sdk.components.audioplayback.Player
    public void start() {
        if (a.isDebugEnabled()) {
            a.debug("PlayerImpl.start()");
        }
        b(SessionEvent.NMSP_CALLLOG_PLAYERSTART_EVENT);
        this.l.send(new MessageSystem.MessageData((byte) 1, this), this, Thread.currentThread(), this.l.getVRAddr()[0]);
    }

    @Override // com.nuance.nmsp.client.sdk.components.audioplayback.Player
    public void stop() {
        if (a.isDebugEnabled()) {
            a.debug("PlayerImpl.start()");
        }
        b(SessionEvent.NMSP_CALLLOG_PLAYERSTOP_EVENT);
        this.l.send(new MessageSystem.MessageData((byte) 2, this), this, Thread.currentThread(), this.l.getVRAddr()[0]);
    }

    @Override // com.nuance.nmsp.client.sdk.common.oem.api.AudioSystem.StopCallback
    public void stopCallback(AudioSystem.AudioStatus audioStatus, Object obj) {
        if (a.isDebugEnabled()) {
            a.debug("audio stop call back is called");
        }
        this.j = (short) 5;
        a(audioStatus.equals(AudioSystem.AudioStatus.AUDIO_ERROR) ? Player.PLAYBACK_ERROR : "STOPPED");
    }
}
