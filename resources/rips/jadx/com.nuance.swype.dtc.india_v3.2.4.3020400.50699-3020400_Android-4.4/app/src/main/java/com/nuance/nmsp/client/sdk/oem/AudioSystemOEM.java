package com.nuance.nmsp.client.sdk.oem;

import android.content.Context;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.nuance.nmsp.client.sdk.common.defines.NMSPDefines;
import com.nuance.nmsp.client.sdk.common.oem.api.AudioSystem;
import com.nuance.nmsp.client.sdk.common.oem.api.LogFactory;
import com.nuance.nmsp.client.sdk.common.oem.api.MessageSystem;
import com.nuance.nmsp.client.sdk.components.general.Parameter;
import com.nuance.nmsp.client.sdk.oem.EndPointerOEM;
import com.nuance.nmsp.client.sdk.oem.bluetooth.Bluetooth;
import java.util.Vector;

/* loaded from: classes.dex */
public class AudioSystemOEM implements AudioSystem, MessageSystem.MessageHandler {
    private static final Integer L;
    private static final Integer M;
    private static final Integer N;
    public static int bufferOffset;
    public static boolean isSpeexDecodeInit;
    public static int maxOffset;
    public static Object speexDecodeLock;
    public static Object speexEncodeLock;
    private static Object x = new Object();
    private static Object y = new Object();
    private static Object z = new Object();
    private EndPointerOEM A;
    private short[] B;
    private byte[] C;
    private int D;
    private int E;
    private int F;
    private CodecType G;
    private boolean H;
    private MessageSystem I;
    private boolean J;
    private boolean K;
    private Context O;
    private NMSPDefines.Codec Q;
    private int b;
    private int c;
    private int d;
    private int e;
    private int f;
    private int g;
    private int h;
    private int i;
    private int j;
    private int k;
    private int l;
    private int m;
    private int n;
    private int o;
    private int p;
    private int q;
    private int r;
    private int s;
    private int t;
    private int u;
    private a v;
    private b w;
    private LogFactory.Log a = LogFactory.getLog(getClass());
    private Bluetooth P = null;

    /* loaded from: classes.dex */
    public static class AudioSystemException extends Exception {
        public AudioSystemException(String str) {
            super(str);
        }
    }

    /* loaded from: classes.dex */
    public static class CodecType {
        public static CodecType SPEEX = new CodecType();
        public static CodecType PCM = new CodecType();

        private CodecType() {
        }
    }

    /* loaded from: classes.dex */
    class a extends Thread implements AudioTrack.OnPlaybackPositionUpdateListener {
        private LogFactory.Log b;
        private Handler c;
        private AudioTrack d;
        private byte[] e;
        private short[] f;
        private boolean g;
        private int h;
        private int i;
        private boolean j;
        private int k;
        private boolean l;
        private boolean m;
        private AudioSystem.AudioCallback n;
        private AudioSystem.DoneCallback o;
        private AudioSystem.StopCallback p;
        private Object q;
        private Object r;
        private int s;

        private a() {
            this.b = LogFactory.getLog(getClass());
            this.j = true;
            this.l = false;
            this.m = false;
            this.n = null;
            this.o = null;
            this.p = null;
            this.q = null;
            this.r = null;
            this.s = 0;
        }

        /* synthetic */ a(AudioSystemOEM audioSystemOEM, byte b) {
            this();
        }

        private int a(short[] sArr, int i, AudioSystem.IntegerMutable integerMutable, Object obj) throws AudioSystemException {
            int decodeSpeex;
            if (i == 0) {
                return 0;
            }
            if (AudioSystemOEM.this.G == CodecType.SPEEX) {
                int i2 = AudioSystemOEM.maxOffset - AudioSystemOEM.bufferOffset;
                if (i2 >= i) {
                    System.arraycopy(AudioSystemOEM.this.B, AudioSystemOEM.bufferOffset, sArr, 0, i);
                    AudioSystemOEM.bufferOffset += i;
                    return i;
                }
                if (i2 > 0) {
                    System.arraycopy(AudioSystemOEM.this.B, AudioSystemOEM.bufferOffset, sArr, 0, i2);
                }
                AudioSystemOEM.bufferOffset = 0;
                this.n.audioCallback((byte[]) null, AudioSystemOEM.this.C, new AudioSystem.IntegerMutable(0), integerMutable, (Float) null, obj);
                if (integerMutable.i > 0) {
                    synchronized (AudioSystemOEM.speexDecodeLock) {
                        decodeSpeex = AudioSystemOEM.decodeSpeex(AudioSystemOEM.this.C, integerMutable.i, AudioSystemOEM.this.B, AudioSystemOEM.this.B.length);
                        AudioSystemOEM.maxOffset = decodeSpeex;
                        if (decodeSpeex > AudioSystemOEM.this.B.length) {
                            AudioSystemOEM.maxOffset = AudioSystemOEM.this.B.length;
                        }
                    }
                    if (decodeSpeex < 0) {
                        throw new AudioSystemException("Call to decodeSpeex() failed with code: " + AudioSystemOEM.speexCodeToString(decodeSpeex));
                    }
                    if (this.b.isDebugEnabled()) {
                        this.b.debug("Obtained " + decodeSpeex + " shorts of PCM data after decoding SPEEX.");
                    }
                }
                if (i2 > 0) {
                    return i2;
                }
            }
            return 0;
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* JADX WARN: Code restructure failed: missing block: B:109:0x004c, code lost:            r12.j = false;     */
        /* JADX WARN: Code restructure failed: missing block: B:111:0x0050, code lost:            r0 = 0;     */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        public void a() throws com.nuance.nmsp.client.sdk.oem.AudioSystemOEM.AudioSystemException {
            /*
                Method dump skipped, instructions count: 979
                To view this dump change 'Code comments level' option to 'DEBUG'
            */
            throw new UnsupportedOperationException("Method not decompiled: com.nuance.nmsp.client.sdk.oem.AudioSystemOEM.a.a():void");
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void a(AudioSystemException audioSystemException) {
            synchronized (AudioSystemOEM.y) {
                if (this.b.isErrorEnabled()) {
                    this.b.error(audioSystemException.getMessage());
                }
                if (this.d != null) {
                    if (this.d.getPlayState() != 1) {
                        this.d.stop();
                    }
                    this.d.release();
                    Looper myLooper = Looper.myLooper();
                    if (myLooper != null) {
                        myLooper.quit();
                    }
                    this.j = false;
                }
                if (AudioSystemOEM.this.P != null) {
                    AudioSystemOEM.this.P.stopBluetoothSco();
                    AudioSystemOEM.this.g();
                }
                this.o.doneCallback(AudioSystem.AudioStatus.AUDIO_ERROR, this.q);
            }
        }

        static /* synthetic */ boolean a(a aVar, boolean z) {
            aVar.m = true;
            return true;
        }

        static /* synthetic */ void b(a aVar) {
            if (aVar.b.isDebugEnabled()) {
                aVar.b.debug("Calling AudioSystemOEM.onStop()");
            }
            synchronized (AudioSystemOEM.y) {
                try {
                    aVar.d.stop();
                } catch (IllegalStateException e) {
                    if (aVar.b.isDebugEnabled()) {
                        aVar.b.debug("Could not stop audioTrack.");
                    }
                }
                aVar.j = false;
                aVar.d.flush();
                aVar.d.release();
                if (AudioSystemOEM.this.P != null) {
                    AudioSystemOEM.this.P.stopBluetoothSco();
                    AudioSystemOEM.this.g();
                }
                if (AudioSystemOEM.this.G == CodecType.SPEEX) {
                    synchronized (AudioSystemOEM.speexDecodeLock) {
                        if (AudioSystemOEM.isSpeexDecodeInit) {
                            AudioSystemOEM.decodeCleanupSpeex();
                            AudioSystemOEM.this.B = null;
                            AudioSystemOEM.this.C = null;
                            AudioSystemOEM.isSpeexDecodeInit = false;
                        } else if (aVar.b.isDebugEnabled()) {
                            aVar.b.debug("Attempting to cleanup speex decode while it is already stopped");
                        }
                    }
                    aVar.p.stopCallback(AudioSystem.AudioStatus.AUDIO_OK, aVar.r);
                    Looper myLooper = Looper.myLooper();
                    if (myLooper != null) {
                        myLooper.quit();
                    }
                }
            }
        }

        static /* synthetic */ boolean b(a aVar, boolean z) {
            aVar.j = false;
            return false;
        }

        public final void a(AudioSystem.AudioCallback audioCallback, AudioSystem.DoneCallback doneCallback, Object obj) throws AudioSystemException {
            if (AudioSystemOEM.this.P != null) {
                AudioSystemOEM.this.P.startBluetoothSco();
            }
            synchronized (this) {
                this.s = 0;
                this.n = audioCallback;
                this.o = doneCallback;
                this.q = obj;
                if (AudioSystemOEM.this.G == CodecType.SPEEX) {
                    synchronized (AudioSystemOEM.speexDecodeLock) {
                        int decodeInitSpeex = AudioSystemOEM.decodeInitSpeex(AudioSystemOEM.this.D, 0, AudioSystemOEM.this.F);
                        if (decodeInitSpeex < 0) {
                            throw new AudioSystemException("decodeInitSpeex failed with code: " + AudioSystemOEM.speexCodeToString(decodeInitSpeex));
                        }
                        AudioSystemOEM.this.B = new short[524288];
                        AudioSystemOEM.this.C = new byte[524288];
                        AudioSystemOEM.isSpeexDecodeInit = true;
                    }
                    AudioSystemOEM.bufferOffset = 0;
                    AudioSystemOEM.maxOffset = 0;
                }
                start();
                while (isAlive() && this.c == null) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                    }
                }
                if (isAlive()) {
                    if (this.c != null) {
                        this.c.sendMessage(this.c.obtainMessage(1));
                    }
                }
            }
        }

        public final void a(AudioSystem.StopCallback stopCallback, Object obj) {
            synchronized (AudioSystemOEM.z) {
                this.l = true;
            }
            synchronized (AudioSystemOEM.y) {
                this.j = false;
            }
            synchronized (this) {
                this.r = obj;
                this.p = stopCallback;
                if (!isAlive() || this.g) {
                    return;
                }
                if (this.c != null) {
                    this.c.sendMessage(this.c.obtainMessage(2));
                }
                while (isAlive() && !this.g) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                    }
                }
            }
        }

        @Override // android.media.AudioTrack.OnPlaybackPositionUpdateListener
        public final void onMarkerReached(AudioTrack audioTrack) {
            try {
                a();
            } catch (AudioSystemException e) {
                a(e);
            }
        }

        @Override // android.media.AudioTrack.OnPlaybackPositionUpdateListener
        public final void onPeriodicNotification(AudioTrack audioTrack) {
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public final void run() {
            Looper.prepare();
            synchronized (this) {
                this.c = new Handler() { // from class: com.nuance.nmsp.client.sdk.oem.AudioSystemOEM.a.1
                    @Override // android.os.Handler
                    public final void handleMessage(Message message) {
                        switch (message.what) {
                            case 1:
                                try {
                                    a.this.a();
                                    return;
                                } catch (AudioSystemException e) {
                                    a.this.a(e);
                                    return;
                                }
                            case 2:
                                a.b(a.this);
                                return;
                            case 3:
                                Looper myLooper = Looper.myLooper();
                                if (myLooper != null) {
                                    myLooper.quit();
                                }
                                a.a(a.this, true);
                                return;
                            default:
                                return;
                        }
                    }
                };
                try {
                    synchronized (AudioSystemOEM.y) {
                        if (this.b.isDebugEnabled()) {
                            this.b.debug("Creating Audio Track");
                        }
                        this.e = new byte[AudioSystemOEM.this.g];
                        this.f = new short[AudioSystemOEM.this.k];
                        try {
                            this.k = 0;
                            this.d = new AudioTrack(AudioSystemOEM.this.b, AudioSystemOEM.this.d, 2, 2, AudioSystemOEM.this.h, AudioSystemOEM.this.c);
                            if (this.d == null || this.d.getState() != 1) {
                                throw new AudioSystemException("AudioTrack object has not been initialized correctly. One or several parameters used to create it must be wrong.");
                            }
                            this.d.setPlaybackPositionUpdateListener(this);
                            this.d.play();
                        } catch (IllegalArgumentException e) {
                            throw new AudioSystemException("Could not instanciate AudioTrack object.");
                        }
                    }
                    notifyAll();
                } catch (AudioSystemException e2) {
                    a(e2);
                    return;
                }
            }
            Looper.loop();
            synchronized (this) {
                this.g = true;
                notifyAll();
            }
        }
    }

    /* loaded from: classes.dex */
    class b implements AudioRecord.OnRecordPositionUpdateListener, MessageSystem.MessageHandler {
        private LogFactory.Log a;
        private AudioRecord b;
        private boolean c;
        private AudioSystem.AudioCallback d;
        private AudioSystem.EndOfSpeechCallback e;
        private AudioSystem.StartOfSpeechCallback f;
        private AudioSystem.EndPointerStartedCallback g;
        private AudioSystem.EndPointerStoppedCallback h;
        private AudioSystem.StopCallback i;
        private AudioSystem.ErrorCallback j;
        private boolean k;
        private boolean l;
        private Object m;
        private int n;

        private b() {
            this.a = LogFactory.getLog(getClass());
            this.d = null;
            this.e = null;
            this.f = null;
            this.g = null;
            this.h = null;
            this.i = null;
            this.j = null;
            this.k = false;
            this.l = false;
            this.m = null;
            this.n = 0;
        }

        /* synthetic */ b(AudioSystemOEM audioSystemOEM, byte b) {
            this();
        }

        private static float a(short[] sArr) {
            double log10;
            long j = 0;
            for (long j2 : sArr) {
                j += (j2 * j2) >> 9;
            }
            double d = j / 1.073741824E9d;
            if (d < 1.0E-9d) {
                log10 = -90.0d;
            } else {
                log10 = Math.log10(d) * 10.0d;
                if (log10 > 0.0d) {
                    log10 = 0.0d;
                }
            }
            return (float) log10;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void a(AudioSystemException audioSystemException) {
            if (this.a.isErrorEnabled()) {
                this.a.error(audioSystemException.getMessage());
            }
            if (this.c) {
                this.b.stop();
                synchronized (AudioSystemOEM.x) {
                    this.c = false;
                    this.b.release();
                    this.b = null;
                }
                this.j.errorCallback(this.m);
                if (AudioSystemOEM.this.G == CodecType.SPEEX) {
                    synchronized (AudioSystemOEM.speexEncodeLock) {
                        AudioSystemOEM.encodeCleanupSpeex();
                    }
                }
                if (AudioSystemOEM.this.P != null) {
                    AudioSystemOEM.this.P.stopBluetoothSco();
                    AudioSystemOEM.this.g();
                }
                if (this.i != null) {
                    this.i.stopCallback(AudioSystem.AudioStatus.AUDIO_ERROR, this.m);
                }
            }
        }

        public final void a(AudioSystem.EndPointerStartedCallback endPointerStartedCallback) {
            this.g = endPointerStartedCallback;
            AudioSystemOEM.this.I.send(new Object[]{AudioSystemOEM.L}, this, AudioSystemOEM.this.I.getMyAddr(), AudioSystemOEM.this.I.getVRAddr()[0]);
        }

        public final void a(AudioSystem.EndPointerStoppedCallback endPointerStoppedCallback) {
            this.h = endPointerStoppedCallback;
            AudioSystemOEM.this.I.send(new Object[]{AudioSystemOEM.M}, this, AudioSystemOEM.this.I.getMyAddr(), AudioSystemOEM.this.I.getVRAddr()[0]);
        }

        public final void a(AudioSystem.StopCallback stopCallback, AudioSystem.EndOfSpeechCallback endOfSpeechCallback, Object obj) {
            boolean z = false;
            if (this.c) {
                try {
                    Thread.sleep(AudioSystemOEM.this.u);
                    if (this.c) {
                        this.b.stop();
                        z = true;
                    }
                    synchronized (AudioSystemOEM.x) {
                        this.c = false;
                        if (z) {
                            this.b.release();
                            this.b = null;
                        }
                    }
                    if (z && AudioSystemOEM.this.G == CodecType.SPEEX) {
                        synchronized (AudioSystemOEM.speexEncodeLock) {
                            AudioSystemOEM.encodeCleanupSpeex();
                        }
                    }
                } catch (IllegalStateException e) {
                    if (this.a.isDebugEnabled()) {
                        this.a.debug("Could not stop audioRecord.");
                    }
                } catch (Exception e2) {
                }
            }
            if (AudioSystemOEM.this.P != null) {
                AudioSystemOEM.this.P.stopBluetoothSco();
                AudioSystemOEM.this.g();
            }
            if (endOfSpeechCallback != null) {
                endOfSpeechCallback.endOfSpeechCallback(obj);
            }
            if (stopCallback != null) {
                stopCallback.stopCallback(AudioSystem.AudioStatus.AUDIO_OK, obj);
            }
        }

        public final void a(boolean z, AudioSystem.AudioCallback audioCallback, AudioSystem.StopCallback stopCallback, AudioSystem.ErrorCallback errorCallback, AudioSystem.StartOfSpeechCallback startOfSpeechCallback, AudioSystem.EndOfSpeechCallback endOfSpeechCallback, AudioSystem.EndPointerStartedCallback endPointerStartedCallback, AudioSystem.EndPointerStoppedCallback endPointerStoppedCallback, Object obj) throws AudioSystemException {
            int encodeInitSpeex;
            if (AudioSystemOEM.this.P != null) {
                AudioSystemOEM.this.P.startBluetoothSco();
            }
            synchronized (AudioSystemOEM.x) {
                this.n = 0;
                this.m = obj;
                this.d = audioCallback;
                this.e = endOfSpeechCallback;
                this.f = startOfSpeechCallback;
                this.g = endPointerStartedCallback;
                this.h = endPointerStoppedCallback;
                this.i = stopCallback;
                this.j = errorCallback;
                if (z) {
                    a(this.g);
                }
                if (AudioSystemOEM.this.G == CodecType.SPEEX) {
                    synchronized (AudioSystemOEM.speexEncodeLock) {
                        encodeInitSpeex = AudioSystemOEM.encodeInitSpeex(AudioSystemOEM.this.D, AudioSystemOEM.this.F, 3, AudioSystemOEM.this.E, 1);
                    }
                    if (encodeInitSpeex < 0) {
                        throw new AudioSystemException("encodeInitSpeex failed with code:" + AudioSystemOEM.speexCodeToString(encodeInitSpeex));
                    }
                }
                try {
                    this.b = new AudioRecord(AudioSystemOEM.this.o, AudioSystemOEM.this.p, 2, 2, AudioSystemOEM.this.s);
                    if (this.b == null || this.b.getState() != 1) {
                        throw new AudioSystemException("AudioRecord object has not been initialized correctly. One or several parameters used to create it must be wrong.");
                    }
                    this.c = true;
                    this.b.setRecordPositionUpdateListener(this);
                    int positionNotificationPeriod = this.b.setPositionNotificationPeriod(AudioSystemOEM.this.q);
                    if (positionNotificationPeriod != 0) {
                        throw new AudioSystemException("Call to AudioRecord.setPositionNotificationPeriod() failed with code:" + positionNotificationPeriod);
                    }
                    try {
                        this.b.startRecording();
                        int i = 0;
                        short[] sArr = new short[AudioSystemOEM.this.q];
                        do {
                            int read = this.b.read(sArr, i, AudioSystemOEM.this.q - i);
                            if (read != -3 && read != -2) {
                                if (read <= 0) {
                                    break;
                                } else {
                                    i += read;
                                }
                            } else {
                                throw new AudioSystemException("Call to AudioRecord.read() failed with code:" + read);
                            }
                        } while (i < AudioSystemOEM.this.q);
                    } catch (IllegalStateException e) {
                        throw new AudioSystemException("Call to AudioRecord.startRecording() failed.");
                    }
                } catch (IllegalArgumentException e2) {
                    throw new AudioSystemException("Could not instanciate AudioRecord object.");
                }
            }
        }

        @Override // com.nuance.nmsp.client.sdk.common.oem.api.MessageSystem.MessageHandler
        public final void handleMessage(Object obj, Object obj2) {
            Object[] objArr = (Object[]) obj;
            if (((Integer) objArr[0]).intValue() == AudioSystemOEM.L.intValue()) {
                if (this.k) {
                    return;
                }
                this.k = true;
                AudioSystemOEM.this.A.resetEndpointingDetection();
                this.l = false;
                if (this.g != null) {
                    this.g.endPointerStartedCallback(null);
                    return;
                }
                return;
            }
            if (((Integer) objArr[0]).intValue() == AudioSystemOEM.M.intValue()) {
                if (this.k) {
                    this.k = false;
                    if (this.h != null) {
                        this.h.endPointerStoppedCallback(null);
                        return;
                    }
                    return;
                }
                return;
            }
            if (((Integer) objArr[0]).intValue() == AudioSystemOEM.N.intValue()) {
                int intValue = ((Integer) objArr[1]).intValue();
                Object obj3 = objArr[2];
                if (this.c) {
                    if (AudioSystemOEM.this.G != CodecType.SPEEX) {
                        if (this.c) {
                            byte[] bArr = (byte[]) obj3;
                            if (intValue < 0) {
                                a(new AudioSystemException("Call to AudioRecord.read() failed with code: " + intValue));
                                return;
                            }
                            if (intValue == 0) {
                                if (this.a.isDebugEnabled()) {
                                    this.a.debug("AudioRecorder has no audio.");
                                    return;
                                }
                                return;
                            }
                            if (this.a.isDebugEnabled()) {
                                LogFactory.Log log = this.a;
                                StringBuilder sb = new StringBuilder("RECORDING: Sample #");
                                int i = this.n;
                                this.n = i + 1;
                                log.debug(sb.append(i).append(". Sending ").append(intValue).append(" bytes from recorder.").toString());
                            }
                            this.d.audioCallback(bArr, (Object) null, new AudioSystem.IntegerMutable(intValue), new AudioSystem.IntegerMutable(0), (Float) null, this.m);
                            return;
                        }
                        return;
                    }
                    byte[] bArr2 = new byte[AudioSystemOEM.this.t];
                    if (this.c) {
                        short[] sArr = (short[]) obj3;
                        if (intValue < 0) {
                            a(new AudioSystemException("Call to AudioRecord.read() failed with code: " + intValue));
                            return;
                        }
                        if (intValue == 0) {
                            if (this.a.isDebugEnabled()) {
                                this.a.debug("AudioRecorder has no audio.");
                                return;
                            }
                            return;
                        }
                        EndPointerOEM.EpType epType = EndPointerOEM.EpType.DETECT_NOTHING;
                        synchronized (AudioSystemOEM.speexEncodeLock) {
                            if (this.c) {
                                int encodeSpeex = AudioSystemOEM.encodeSpeex(sArr, bArr2, AudioSystemOEM.this.t);
                                EndPointerOEM.EpType detectEndPointing = (!this.k || this.l) ? epType : AudioSystemOEM.this.A.detectEndPointing();
                                if (encodeSpeex < 0) {
                                    a(new AudioSystemException("Call to encodeSpeex() failed with code: " + AudioSystemOEM.speexCodeToString(encodeSpeex)));
                                    return;
                                }
                                if (this.a.isDebugEnabled()) {
                                    LogFactory.Log log2 = this.a;
                                    StringBuilder sb2 = new StringBuilder("RECORDING: Sample #");
                                    int i2 = this.n;
                                    this.n = i2 + 1;
                                    log2.debug(sb2.append(i2).append(". Got ").append(intValue).append(" shorts from recorder, sending ").append(encodeSpeex).append(" SPEEX vocoded bytes. ").toString());
                                }
                                if (this.a.isDebugEnabled()) {
                                    if (detectEndPointing == EndPointerOEM.EpType.SPEECH_END) {
                                        this.a.debug("ENDPOINTING SPEECH_END");
                                    } else if (detectEndPointing == EndPointerOEM.EpType.SPEECH_START) {
                                        this.a.debug("ENDPOINTING SPEECH_START");
                                    }
                                }
                                float a = AudioSystemOEM.this.H ? a(sArr) + 90.0f : -1.0f;
                                this.d.audioCallback((byte[]) null, bArr2, new AudioSystem.IntegerMutable(0), new AudioSystem.IntegerMutable(encodeSpeex), a < 0.0f ? null : Float.valueOf(a), this.m);
                                if (detectEndPointing == EndPointerOEM.EpType.SPEECH_END) {
                                    this.l = true;
                                    if (!AudioSystemOEM.this.J) {
                                        a(this.i, this.e, this.m);
                                    } else if (this.e != null) {
                                        this.e.endOfSpeechCallback(this.m);
                                    }
                                }
                                if (detectEndPointing == EndPointerOEM.EpType.SPEECH_START) {
                                    this.f.startOfSpeechCallback(this.m);
                                }
                            }
                        }
                    }
                }
            }
        }

        @Override // android.media.AudioRecord.OnRecordPositionUpdateListener
        public final void onMarkerReached(AudioRecord audioRecord) {
        }

        @Override // android.media.AudioRecord.OnRecordPositionUpdateListener
        public final void onPeriodicNotification(AudioRecord audioRecord) {
            int i = 0;
            if (AudioSystemOEM.this.G == CodecType.SPEEX) {
                synchronized (AudioSystemOEM.x) {
                    if (this.c) {
                        short[] sArr = new short[AudioSystemOEM.this.q];
                        do {
                            int read = audioRecord.read(sArr, i, AudioSystemOEM.this.q - i);
                            if (read == -3 || read == -2 || read <= 0) {
                                break;
                            } else {
                                i += read;
                            }
                        } while (i < AudioSystemOEM.this.q);
                        AudioSystemOEM.this.I.send(new Object[]{AudioSystemOEM.N, new Integer(i), sArr}, this, AudioSystemOEM.this.I.getMyAddr(), AudioSystemOEM.this.I.getVRAddr()[0]);
                    }
                }
                return;
            }
            synchronized (AudioSystemOEM.x) {
                if (this.c) {
                    byte[] bArr = new byte[AudioSystemOEM.this.r];
                    do {
                        int read2 = audioRecord.read(bArr, i, AudioSystemOEM.this.r - i);
                        if (read2 == -3 || read2 == -2 || read2 <= 0) {
                            break;
                        } else {
                            i += read2;
                        }
                    } while (i < AudioSystemOEM.this.r);
                    AudioSystemOEM.this.I.send(new Object[]{AudioSystemOEM.N, new Integer(i), bArr}, this, AudioSystemOEM.this.I.getMyAddr(), AudioSystemOEM.this.I.getVRAddr()[0]);
                }
            }
        }
    }

    static {
        System.loadLibrary("nmsp_speex");
        isSpeexDecodeInit = false;
        speexEncodeLock = new Object();
        speexDecodeLock = new Object();
        bufferOffset = 0;
        maxOffset = 0;
        L = new Integer(1);
        M = new Integer(2);
        N = new Integer(3);
    }

    public AudioSystemOEM(MessageSystem messageSystem, NMSPDefines.Codec codec, Vector vector) {
        int parseInt;
        int i = 0;
        this.u = 40;
        this.A = null;
        this.H = false;
        this.J = false;
        this.K = false;
        this.O = null;
        this.Q = codec;
        this.I = messageSystem;
        this.A = new EndPointerOEM();
        this.A.initialize(vector);
        if (vector != null) {
            while (true) {
                int i2 = i;
                if (i2 >= vector.size()) {
                    break;
                }
                Parameter parameter = (Parameter) vector.get(i2);
                String name = parameter.getName();
                if (parameter.getType() == Parameter.Type.SDK) {
                    if (name.equals(NMSPDefines.NMSP_DEFINES_USE_ENERGY_LEVEL)) {
                        if (new String(parameter.getValue()).equalsIgnoreCase("TRUE")) {
                            if (this.a.isDebugEnabled()) {
                                this.a.debug("Use energy level is activated.");
                            }
                            this.H = true;
                        }
                    } else if (name.equals(NMSPDefines.NMSP_DEFINES_RECORDER_CONTINUES_ON_ENDPOINTER_AND_TIMER_STOPPING)) {
                        if (new String(parameter.getValue()).equalsIgnoreCase("TRUE")) {
                            if (this.a.isDebugEnabled()) {
                                this.a.debug("_continuesOnEndPointerAndTimerStopping is activated.");
                            }
                            this.J = true;
                        }
                    } else if (name.equals(NMSPDefines.NMSP_DEFINES_CAPTURING_CONTINUES_ON_ENDPOINTER)) {
                        if (new String(parameter.getValue()).equalsIgnoreCase("TRUE")) {
                            if (this.a.isDebugEnabled()) {
                                this.a.debug("_capturingContinuesOnEndPointer is activated.");
                            }
                            this.K = true;
                        }
                    } else if (name.equals(NMSPDefines.NMSP_DEFINES_ANDROID_CONTEXT)) {
                        this.O = (Context) parameter.getValueRaw();
                        if (this.a.isDebugEnabled()) {
                            this.a.debug("NMSP_DEFINES_ANDROID_CONTEXT is passed in as" + this.O);
                        }
                    } else if (name.equals("Audio_Packet_Duration") && (parseInt = Integer.parseInt(new String(parameter.getValue()))) > 0) {
                        this.u = parseInt;
                    }
                }
                i = i2 + 1;
            }
        }
        a(codec);
    }

    private void a(NMSPDefines.Codec codec) {
        int i;
        int minBufferSize;
        int i2;
        int minBufferSize2;
        if (codec != NMSPDefines.Codec.PCM_16_8K) {
            if (codec == NMSPDefines.Codec.PCM_16_11K) {
                this.G = CodecType.PCM;
                this.F = 11000;
                this.D = -1;
                this.E = -1;
            } else if (codec == NMSPDefines.Codec.PCM_16_16K) {
                this.G = CodecType.PCM;
                this.F = 16000;
                this.D = -1;
                this.E = -1;
            } else if (codec == NMSPDefines.Codec.PCM_16_32K) {
                this.G = CodecType.PCM;
                this.F = 32000;
                this.D = -1;
                this.E = -1;
            } else if (codec == NMSPDefines.Codec.SPEEX_8K) {
                this.G = CodecType.SPEEX;
                this.F = 8000;
                this.E = 6;
                this.D = 0;
            } else if (codec == NMSPDefines.Codec.SPEEX_16K) {
                this.G = CodecType.SPEEX;
                this.F = 16000;
                this.E = 8;
                this.D = 1;
            } else if (codec == NMSPDefines.Codec.SPEEX_11K) {
                this.G = CodecType.SPEEX;
                this.F = 16000;
                this.E = 8;
                this.D = 1;
            } else if (this.a.isErrorEnabled()) {
                this.a.error("Codec " + codec + " is not handled, using PCM_16_8K by default.");
            }
            this.c = 1;
            this.d = this.F;
            this.l = 750;
            this.m = (this.l * this.d) / 1000;
            this.n = this.m << 1;
            this.e = 300;
            this.f = (this.d * this.e) / 1000;
            this.g = this.f << 1;
            i = this.g * 5;
            minBufferSize = AudioTrack.getMinBufferSize(this.d, 2, 2);
            if (minBufferSize != -2 || minBufferSize == -1 || minBufferSize <= i) {
                this.i = i;
            } else {
                this.i = minBufferSize;
            }
            this.h = this.i / 2;
            this.j = 500;
            this.k = (this.d * this.j) / 1000;
            this.p = this.F;
            this.q = (this.p * this.u) / 1000;
            this.r = this.q << 1;
            i2 = this.r * 3;
            minBufferSize2 = AudioRecord.getMinBufferSize(this.p, 2, 2);
            if (minBufferSize2 != -2 || minBufferSize2 == -1 || minBufferSize2 <= i2) {
                this.s = i2;
            } else {
                this.s = minBufferSize2;
            }
            this.t = this.r;
        }
        this.G = CodecType.PCM;
        this.F = 8000;
        this.D = -1;
        this.E = -1;
        this.c = 1;
        this.d = this.F;
        this.l = 750;
        this.m = (this.l * this.d) / 1000;
        this.n = this.m << 1;
        this.e = 300;
        this.f = (this.d * this.e) / 1000;
        this.g = this.f << 1;
        i = this.g * 5;
        minBufferSize = AudioTrack.getMinBufferSize(this.d, 2, 2);
        if (minBufferSize != -2) {
        }
        this.i = i;
        this.h = this.i / 2;
        this.j = 500;
        this.k = (this.d * this.j) / 1000;
        this.p = this.F;
        this.q = (this.p * this.u) / 1000;
        this.r = this.q << 1;
        i2 = this.r * 3;
        minBufferSize2 = AudioRecord.getMinBufferSize(this.p, 2, 2);
        if (minBufferSize2 != -2) {
        }
        this.s = i2;
        this.t = this.r;
    }

    public static native void decodeCleanupSpeex();

    public static native int decodeInitSpeex(int i, int i2, int i3);

    public static native int decodeSpeex(byte[] bArr, int i, short[] sArr, int i2);

    public static native void encodeCleanupSpeex();

    public static native int encodeInitSpeex(int i, int i2, int i3, int i4, int i5);

    public static native int encodeSpeex(short[] sArr, byte[] bArr, int i);

    /* JADX INFO: Access modifiers changed from: private */
    public void g() {
        if (this.P != null) {
            this.P.close();
            this.P = null;
        }
    }

    private void h() {
        if (this.Q == NMSPDefines.Codec.SPEEX_16K || this.Q == NMSPDefines.Codec.SPEEX_11K) {
            this.Q = NMSPDefines.Codec.SPEEX_8K;
            a(this.Q);
        } else if (this.Q == NMSPDefines.Codec.PCM_16_11K || this.Q == NMSPDefines.Codec.PCM_16_16K || this.Q == NMSPDefines.Codec.PCM_16_22K || this.Q == NMSPDefines.Codec.PCM_16_32K) {
            this.Q = NMSPDefines.Codec.PCM_16_8K;
            a(this.Q);
        }
    }

    public static native String speexCodeToString(int i);

    @Override // com.nuance.nmsp.client.sdk.common.oem.api.AudioSystem
    public AudioSystem.InputDevice getInputDevice() {
        return AudioSystem.InputDevice.MICROPHONE;
    }

    @Override // com.nuance.nmsp.client.sdk.common.oem.api.AudioSystem
    public AudioSystem.OutputDevice getOutputDevice() {
        return AudioSystem.OutputDevice.SPEAKER;
    }

    @Override // com.nuance.nmsp.client.sdk.common.oem.api.AudioSystem
    public Vector getParams(Vector vector) {
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 >= vector.size()) {
                return vector;
            }
            Parameter parameter = (Parameter) vector.elementAt(i2);
            if (parameter.getType() == Parameter.Type.SDK) {
                if (parameter.getName().equals(NMSPDefines.NMSP_DEFINES_AUDIO_INPUTSOURCE)) {
                    switch (this.o) {
                        case 0:
                            vector.set(i2, new Parameter(NMSPDefines.NMSP_DEFINES_AUDIO_INPUTSOURCE, "Default audio source".getBytes(), Parameter.Type.SDK));
                            break;
                        case 1:
                            vector.set(i2, new Parameter(NMSPDefines.NMSP_DEFINES_AUDIO_INPUTSOURCE, "Microphone audio source".getBytes(), Parameter.Type.SDK));
                            break;
                        case 6:
                            vector.set(i2, new Parameter(NMSPDefines.NMSP_DEFINES_AUDIO_INPUTSOURCE, "VoiceRecognition audio source".getBytes(), Parameter.Type.SDK));
                            break;
                        default:
                            vector.set(i2, new Parameter(NMSPDefines.NMSP_DEFINES_AUDIO_INPUTSOURCE, "Unkown audio source".getBytes(), Parameter.Type.SDK));
                            break;
                    }
                }
                if (parameter.getName().equals(NMSPDefines.NMSP_DEFINES_ENDPOINTER)) {
                    vector.setElementAt(new Parameter(NMSPDefines.NMSP_DEFINES_ENDPOINTER, new Boolean(this.A.isEndPointingActive()).toString().getBytes(), Parameter.Type.SDK), i2);
                } else if (parameter.getName().equals(NMSPDefines.NMSP_DEFINES_RECORDER_CONTINUES_ON_ENDPOINTER_AND_TIMER_STOPPING)) {
                    vector.setElementAt(new Parameter(NMSPDefines.NMSP_DEFINES_RECORDER_CONTINUES_ON_ENDPOINTER_AND_TIMER_STOPPING, new Boolean(this.J).toString().getBytes(), Parameter.Type.SDK), i2);
                } else if (parameter.getName().equals(NMSPDefines.NMSP_DEFINES_CAPTURING_CONTINUES_ON_ENDPOINTER)) {
                    vector.setElementAt(new Parameter(NMSPDefines.NMSP_DEFINES_CAPTURING_CONTINUES_ON_ENDPOINTER, new Boolean(this.K).toString().getBytes(), Parameter.Type.SDK), i2);
                } else if (parameter.getName().equals(NMSPDefines.NMSP_DEFINES_USE_ENERGY_LEVEL)) {
                    vector.set(i2, new Parameter(NMSPDefines.NMSP_DEFINES_USE_ENERGY_LEVEL, new Boolean(this.H).toString().getBytes(), Parameter.Type.SDK));
                } else {
                    vector.set(i2, new Parameter(parameter.getName(), "unsupported parameter".getBytes(), parameter.getType()));
                }
            }
            i = i2 + 1;
        }
    }

    @Override // com.nuance.nmsp.client.sdk.common.oem.api.MessageSystem.MessageHandler
    public void handleMessage(Object obj, Object obj2) {
        if (this.a.isDebugEnabled()) {
            this.a.debug("---------------------- AudioSystemAndroid +++++ handleMessage() Thread:" + Thread.currentThread());
        }
    }

    @Override // com.nuance.nmsp.client.sdk.common.oem.api.AudioSystem
    public boolean nextPlayback() {
        this.a.info("++++++++++++========== nextPlayback() is not implemented");
        return false;
    }

    @Override // com.nuance.nmsp.client.sdk.common.oem.api.AudioSystem
    public boolean pausePlayback(int i) {
        this.a.info("++++++++++++========== pausePlayback() is not implemented");
        return false;
    }

    @Override // com.nuance.nmsp.client.sdk.common.oem.api.AudioSystem
    public void pauseRecording(int i) {
        this.a.debug("++++++++++++========== pauseRecording() is not implemented");
    }

    @Override // com.nuance.nmsp.client.sdk.common.oem.api.AudioSystem
    public boolean previousPlayback() {
        this.a.info("++++++++++++========== previousPlayback() is not implemented");
        return false;
    }

    @Override // com.nuance.nmsp.client.sdk.common.oem.api.AudioSystem
    public boolean startPlayback(AudioSystem.OutputDevice outputDevice, AudioSystem.AudioCallback audioCallback, AudioSystem.DoneCallback doneCallback, Object obj) {
        byte b2 = 0;
        if (this.a.isDebugEnabled()) {
            this.a.debug("++++++++++++========== startPlayback()");
        }
        if (audioCallback == null) {
            if (!this.a.isErrorEnabled()) {
                return false;
            }
            this.a.error("audioCallback cannot be null.");
            return false;
        }
        if (this.O == null) {
            if (!this.a.isErrorEnabled()) {
                return false;
            }
            this.a.error("inputDevice is BLUETOOTH_HEADSET, but ANDROID_CONTEXT parameter is not passed in!!!");
            return false;
        }
        if (this.O != null ? this.O.checkCallingOrSelfPermission("android.permission.BLUETOOTH") == 0 : true) {
            this.P = Bluetooth.createInstance(this.O);
            if (this.P.isHeadsetConnected()) {
                h();
            }
        } else {
            this.P = null;
        }
        if (outputDevice == AudioSystem.OutputDevice.SPEAKER) {
            this.b = 3;
            g();
        } else {
            if (outputDevice != AudioSystem.OutputDevice.BLUETOOTH_HEADSET || this.P == null) {
                if (this.a.isErrorEnabled()) {
                    this.a.error("Unexpected outputDevice.");
                }
                g();
                return false;
            }
            this.b = this.P.getPlaybackStream();
            if (!this.P.isHeadsetConnected()) {
                g();
            }
        }
        this.v = new a(this, b2);
        try {
            this.v.a(audioCallback, doneCallback, obj);
            return true;
        } catch (AudioSystemException e) {
            this.v.a(e);
            this.v = null;
            return false;
        }
    }

    @Override // com.nuance.nmsp.client.sdk.common.oem.api.AudioSystem
    public boolean startRecording(AudioSystem.InputDevice inputDevice, boolean z2, AudioSystem.AudioCallback audioCallback, AudioSystem.StopCallback stopCallback, AudioSystem.ErrorCallback errorCallback, AudioSystem.StartOfSpeechCallback startOfSpeechCallback, AudioSystem.NoSpeechCallback noSpeechCallback, AudioSystem.EndOfSpeechCallback endOfSpeechCallback, AudioSystem.EndPointerStartedCallback endPointerStartedCallback, AudioSystem.EndPointerStoppedCallback endPointerStoppedCallback, Object obj) {
        if (this.a.isDebugEnabled()) {
            this.a.debug("++++++++++++========== startRecording()");
        }
        if (this.w != null) {
            if (this.a.isDebugEnabled()) {
                this.a.debug("previous recording still running!");
            }
            return false;
        }
        if (audioCallback == null) {
            if (this.a.isErrorEnabled()) {
                this.a.error("audioCallback cannot be null.");
            }
            return false;
        }
        if (this.O == null) {
            if (this.a.isErrorEnabled()) {
                this.a.error("inputDevice is BLUETOOTH_HEADSET, but ANDROID_CONTEXT parameter is not passed in!!!");
            }
            return false;
        }
        if (this.O != null ? this.O.checkCallingOrSelfPermission("android.permission.BLUETOOTH") == 0 : true) {
            this.P = Bluetooth.createInstance(this.O);
            if (this.P.isHeadsetConnected()) {
                h();
            }
        } else {
            this.P = null;
        }
        if (inputDevice == AudioSystem.InputDevice.MICROPHONE) {
            this.o = 6;
            g();
        } else {
            if (inputDevice != AudioSystem.InputDevice.BLUETOOTH_HEADSET || this.P == null) {
                if (this.a.isErrorEnabled()) {
                    this.a.error("Unexpected inputDevice.");
                }
                g();
                return false;
            }
            this.o = this.P.getRecordingSource();
            if (!this.P.isHeadsetConnected()) {
                g();
            }
        }
        this.w = new b(this, (byte) 0);
        try {
            this.w.a(z2, audioCallback, stopCallback, errorCallback, startOfSpeechCallback, endOfSpeechCallback, endPointerStartedCallback, endPointerStoppedCallback, obj);
            return true;
        } catch (AudioSystemException e) {
            this.w.a(e);
            this.w = null;
            return false;
        }
    }

    @Override // com.nuance.nmsp.client.sdk.common.oem.api.AudioSystem
    public void stopPlayback(AudioSystem.StopCallback stopCallback, Object obj) {
        if (this.a.isDebugEnabled()) {
            this.a.debug("++++++++++++========== stopPlayback()");
        }
        if (this.v != null) {
            this.v.a(stopCallback, obj);
        }
        this.v = null;
    }

    @Override // com.nuance.nmsp.client.sdk.common.oem.api.AudioSystem
    public void stopRecording(AudioSystem.StopCallback stopCallback, Object obj) {
        if (this.a.isDebugEnabled()) {
            this.a.debug("++++++++++++========== stopRecording()");
        }
        if (stopCallback == null) {
            return;
        }
        if (this.w == null) {
            stopCallback.stopCallback(AudioSystem.AudioStatus.AUDIO_OK, obj);
        } else {
            this.w.a(stopCallback, null, obj);
        }
    }

    @Override // com.nuance.nmsp.client.sdk.common.oem.api.AudioSystem
    public void turnOffEndPointer(AudioSystem.EndPointerStoppedCallback endPointerStoppedCallback, Object obj) {
        if (this.a.isDebugEnabled()) {
            this.a.debug("++++++++++++========== turnOnEndPointer()");
        }
        if (endPointerStoppedCallback == null || this.w == null) {
            return;
        }
        this.w.a(endPointerStoppedCallback);
    }

    @Override // com.nuance.nmsp.client.sdk.common.oem.api.AudioSystem
    public void turnOnEndPointer(AudioSystem.EndPointerStartedCallback endPointerStartedCallback, Object obj) {
        if (this.a.isDebugEnabled()) {
            this.a.debug("++++++++++++========== turnOnEndPointer()");
        }
        if (endPointerStartedCallback == null || this.w == null) {
            return;
        }
        this.w.a(endPointerStartedCallback);
    }
}
