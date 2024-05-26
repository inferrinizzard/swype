package com.nuance.nmsp.client.sdk.components.internal.audiorecord;

import com.nuance.nmsp.client.sdk.common.defines.NMSPDefines;
import com.nuance.nmsp.client.sdk.common.oem.api.AudioSystem;
import com.nuance.nmsp.client.sdk.common.oem.api.LogFactory;
import com.nuance.nmsp.client.sdk.common.oem.api.MessageSystem;
import com.nuance.nmsp.client.sdk.common.oem.api.TimerSystem;
import com.nuance.nmsp.client.sdk.common.util.Util;
import com.nuance.nmsp.client.sdk.components.audiorecord.NMSPAudioRecordListener;
import com.nuance.nmsp.client.sdk.components.audiorecord.Recorder;
import com.nuance.nmsp.client.sdk.components.core.calllog.SessionEvent;
import com.nuance.nmsp.client.sdk.components.general.NMSPAudioSink;
import com.nuance.nmsp.client.sdk.components.general.Parameter;
import com.nuance.nmsp.client.sdk.components.general.TransactionProcessingException;
import com.nuance.nmsp.client.sdk.components.resource.common.Manager;
import com.nuance.nmsp.client.sdk.components.resource.common.ResourceException;
import com.nuance.nmsp.client.sdk.components.resource.internal.common.ManagerImpl;
import com.nuance.nmsp.client.sdk.oem.AudioSystemOEM;
import com.nuance.nmsp.client.sdk.oem.BluetoothSystemOEM;
import java.util.Enumeration;
import java.util.Vector;

/* loaded from: classes.dex */
public class RecorderImpl implements AudioSystem.AudioCallback, AudioSystem.EndOfSpeechCallback, AudioSystem.EndPointerStartedCallback, AudioSystem.EndPointerStoppedCallback, AudioSystem.ErrorCallback, AudioSystem.NoSpeechCallback, AudioSystem.StartOfSpeechCallback, AudioSystem.StopCallback, MessageSystem.MessageHandler, Recorder {
    private static final LogFactory.Log a = LogFactory.getLog(RecorderImpl.class);
    private NMSPDefines.Codec b;
    private AudioSystem c;
    private NMSPAudioRecordListener d;
    private MessageSystem e;
    private NMSPAudioSink f;
    private long g;
    private SessionEvent h;
    private int i;
    private TimerSystem.TimerSystemTask j;
    private int k;
    private TimerSystem.TimerSystemTask l;
    private boolean m;
    private boolean n;
    private boolean o;
    private AudioSystem.InputDevice p;
    private Vector q;
    private int r;

    public RecorderImpl(NMSPAudioRecordListener nMSPAudioRecordListener, Manager manager, Vector vector, NMSPDefines.AudioSystem.InputDevice inputDevice) {
        this.e = null;
        this.m = false;
        this.n = false;
        this.o = false;
        this.r = -1;
        a(vector);
        this.d = nMSPAudioRecordListener;
        this.b = ((ManagerImpl) manager).getInputCodec();
        this.j = null;
        this.l = null;
        this.e = ((ManagerImpl) manager).getMsgSys();
        this.q = vector;
        this.m = a(vector, NMSPDefines.NMSP_DEFINES_ENDPOINTER);
        this.n = a(vector, NMSPDefines.NMSP_DEFINES_RECORDER_CONTINUES_ON_ENDPOINTER_AND_TIMER_STOPPING);
        this.o = a(vector, NMSPDefines.NMSP_DEFINES_CAPTURING_CONTINUES_ON_ENDPOINTER);
        this.k = b(vector, NMSPDefines.NMSP_DEFINES_START_OF_SPEECH_TIMEOUT);
        this.c = new AudioSystemOEM(this.e, this.b, vector);
        if (inputDevice.equals(NMSPDefines.AudioSystem.InputDevice.BLUETOOTH_HEADSET)) {
            this.p = AudioSystem.InputDevice.BLUETOOTH_HEADSET;
        } else if (inputDevice.equals(NMSPDefines.AudioSystem.InputDevice.MICROPHONE)) {
            this.p = AudioSystem.InputDevice.MICROPHONE;
        } else if (inputDevice.equals(NMSPDefines.AudioSystem.InputDevice.NETWORK)) {
            this.p = AudioSystem.InputDevice.NETWORK;
        } else if (inputDevice.equals(NMSPDefines.AudioSystem.InputDevice.WIRED_HEADSET)) {
            this.p = AudioSystem.InputDevice.WIRED_HEADSET;
        }
        SessionEvent calllogSession = ((ManagerImpl) manager).getCalllogSession();
        if (calllogSession != null && calllogSession != null) {
            this.h = calllogSession.createChildEventBuilder(SessionEvent.NMSP_CALLLOG_RECORDER_EVENT).commit();
        }
        this.r = 0;
    }

    static /* synthetic */ TimerSystem.TimerSystemTask a(RecorderImpl recorderImpl, TimerSystem.TimerSystemTask timerSystemTask) {
        recorderImpl.j = null;
        return null;
    }

    private void a() {
        if (this.i > 0) {
            this.j = new TimerSystem.TimerSystemTask() { // from class: com.nuance.nmsp.client.sdk.components.internal.audiorecord.RecorderImpl.1
                @Override // java.lang.Runnable
                public final void run() {
                    RecorderImpl.a(RecorderImpl.this, (TimerSystem.TimerSystemTask) null);
                    if (RecorderImpl.this.n) {
                        RecorderImpl.this.handleStopCapturing(true);
                    } else {
                        RecorderImpl.this.a(true);
                    }
                }
            };
            this.e.scheduleTask(this.j, this.i);
        }
    }

    private void a(NMSPAudioSink nMSPAudioSink, int i) {
        if (a.isDebugEnabled()) {
            a.debug("RecorderImpl.handleStartRecording(" + nMSPAudioSink + ") _state:" + this.r);
        }
        if (new BluetoothSystemOEM(this.q).isBluetoothHeadsetConnected()) {
            this.b = Util.adjustCodecForBluetooth(this.b);
        }
        this.i = i;
        if (this.r == 0) {
            this.f = nMSPAudioSink;
            if (!this.c.startRecording(this.p, this.m && nMSPAudioSink != null, this, this, this, this, this, this, this, this, null)) {
                if (a.isErrorEnabled()) {
                    a.error("RecorderImpl.handleStartRecording() startRecording() failed!!!");
                }
                this.r = 8;
                a(Recorder.RECORD_ERROR, (Object) null);
                return;
            }
            if (this.m) {
                if (nMSPAudioSink == null) {
                    this.r = 1;
                    if (this.k > 0) {
                        this.l = new TimerSystem.TimerSystemTask() { // from class: com.nuance.nmsp.client.sdk.components.internal.audiorecord.RecorderImpl.2
                            @Override // java.lang.Runnable
                            public final void run() {
                                RecorderImpl.b(RecorderImpl.this, (TimerSystem.TimerSystemTask) null);
                                if (RecorderImpl.this.n) {
                                    RecorderImpl.this.handleStopCapturing(true);
                                } else {
                                    RecorderImpl.this.a(true);
                                }
                                RecorderImpl.this.a(Recorder.NO_SPEECH, (Object) null);
                            }
                        };
                        this.e.scheduleTask(this.l, this.k);
                    }
                } else {
                    this.r = 2;
                }
            } else if (nMSPAudioSink == null) {
                this.r = 1;
            } else {
                a();
                this.r = 4;
            }
            a("STARTED", (Object) null);
        }
    }

    private void a(String str) {
        if (this.h != null) {
            this.h.createChildEventBuilder(str).commit();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void a(String str, Object obj) {
        if (this.d != null) {
            try {
                this.d.recorderUpdate(this, str, obj);
            } catch (Throwable th) {
                if (a.isErrorEnabled()) {
                    a.error("Got an exp while calling NMSPAudioRecordListener.recorderUpdate(" + str + ", " + obj + ")[" + th.getClass().getName() + "] msg [" + th.getMessage() + "]");
                }
            }
        }
    }

    private static void a(Vector vector) {
        if (vector != null) {
            Enumeration elements = vector.elements();
            while (elements.hasMoreElements()) {
                Parameter parameter = (Parameter) elements.nextElement();
                if (parameter.getType() != Parameter.Type.SDK) {
                    throw new IllegalArgumentException("Parameter type: " + parameter.getType() + " not allowed. ");
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void a(boolean z) {
        if (a.isDebugEnabled()) {
            a.debug("RecorderImpl.handleStopRecording() _state:" + this.r);
        }
        if (this.r != 2 && this.r != 4 && this.r != 5) {
            if (this.r == 1 || this.r == 3 || this.r == 6) {
                this.c.stopRecording(this, null);
                this.r = 7;
                return;
            }
            return;
        }
        if (z) {
            a(Recorder.CAPTURE_TIMEOUT, (Object) null);
        }
        if (this.r == 4) {
            c();
            b();
        }
        this.c.stopRecording(this, null);
        this.r = 9;
    }

    private static boolean a(Vector vector, String str) {
        if (vector != null) {
            Enumeration elements = vector.elements();
            while (elements.hasMoreElements()) {
                Parameter parameter = (Parameter) elements.nextElement();
                if (parameter.getType() == Parameter.Type.SDK && parameter.getName().equals(str)) {
                    return new String(parameter.getValue()).equals("TRUE");
                }
            }
        }
        return false;
    }

    private static int b(Vector vector, String str) {
        if (vector != null) {
            Enumeration elements = vector.elements();
            while (elements.hasMoreElements()) {
                Parameter parameter = (Parameter) elements.nextElement();
                if (parameter.getType() == Parameter.Type.SDK && parameter.getName().equals(str)) {
                    int intValue = ((Integer) parameter.getValueRaw()).intValue();
                    if (intValue < 0) {
                        throw new IllegalArgumentException("This value cannot be negative");
                    }
                    return intValue;
                }
            }
        }
        return 0;
    }

    static /* synthetic */ TimerSystem.TimerSystemTask b(RecorderImpl recorderImpl, TimerSystem.TimerSystemTask timerSystemTask) {
        recorderImpl.l = null;
        return null;
    }

    private void b() {
        if (this.j != null) {
            this.e.cancelTask(this.j);
            this.j = null;
        }
    }

    private void c() {
        if (this.l != null) {
            this.e.cancelTask(this.l);
            this.l = null;
        }
    }

    private void d() {
        try {
            this.f.addAudioBuf(null, 0, 0, true);
        } catch (TransactionProcessingException e) {
            if (a.isWarnEnabled()) {
                a.error("RecorderImpl.finishAudioSink() TransactionProcessingException:" + e);
            }
        }
        if (a.isDebugEnabled()) {
            a.debug("RecorderImpl::finishAudioSink send the last audio buffer from recorder");
        }
    }

    @Override // com.nuance.nmsp.client.sdk.common.oem.api.AudioSystem.AudioCallback
    public void audioCallback(Vector vector, Vector vector2, AudioSystem.IntegerMutable integerMutable, AudioSystem.IntegerMutable integerMutable2, Float f, Object obj) {
        if (a.isErrorEnabled()) {
            a.error("RecorderImpl.audioCallback() not implemented!!!");
        }
    }

    @Override // com.nuance.nmsp.client.sdk.common.oem.api.AudioSystem.AudioCallback
    public void audioCallback(byte[] bArr, Object obj, AudioSystem.IntegerMutable integerMutable, AudioSystem.IntegerMutable integerMutable2, Float f, Object obj2) {
        int i = 0;
        if (a.isDebugEnabled()) {
            a.debug("RecorderImpl.audioCallback() _state:" + this.r);
        }
        if (this.r == 4) {
            if (this.g == 0) {
                this.g = System.currentTimeMillis();
                a("StreamStart");
            }
            if (Util.isAMRCodec(this.b) || Util.isSpeexCodec(this.b)) {
                i = integerMutable2.i;
                bArr = (byte[]) obj;
            } else if (Util.isPCMCodec(this.b)) {
                i = integerMutable.i;
            } else {
                bArr = null;
            }
            if (a.isDebugEnabled()) {
                a.debug("========================= Recorder::audioCallback len[" + i + "] ======================");
            }
            try {
                this.f.addAudioBuf(bArr, 0, i, false);
                a(Recorder.BUFFER_RECORDED, f);
            } catch (TransactionProcessingException e) {
                if (a.isWarnEnabled()) {
                    a.warn("RecorderImpl.audioCallback() TransactionProcessingException:" + e);
                }
                a(Recorder.RECORD_ERROR, (Object) null);
                if (this.r == 4) {
                    c();
                    b();
                }
                this.c.stopRecording(this, null);
                this.r = 7;
            }
        }
    }

    @Override // com.nuance.nmsp.client.sdk.common.oem.api.AudioSystem.EndOfSpeechCallback
    public void endOfSpeechCallback(Object obj) {
        if (a.isDebugEnabled()) {
            a.debug("RecorderImpl::endOfSpeechCallback() _state:" + this.r);
        }
        if (this.r == 4) {
            a(Recorder.END_OF_SPEECH, (Object) null);
            if (this.o) {
                return;
            }
            handleStopCapturing(false);
        }
    }

    @Override // com.nuance.nmsp.client.sdk.common.oem.api.AudioSystem.EndPointerStartedCallback
    public void endPointerStartedCallback(Object obj) {
        if (a.isDebugEnabled()) {
            a.debug("RecorderImpl::endPointerStartedCallback() _state:" + this.r);
        }
        if (this.r == 2) {
            a();
            this.r = 4;
        } else if (this.r == 3) {
            this.c.turnOffEndPointer(this, null);
            this.r = 6;
        }
    }

    @Override // com.nuance.nmsp.client.sdk.common.oem.api.AudioSystem.EndPointerStoppedCallback
    public void endPointerStoppedCallback(Object obj) {
        if (a.isDebugEnabled()) {
            a.debug("RecorderImpl::endPointerStoppedCallback() _state:" + this.r);
        }
        if (this.r == 5) {
            this.c.turnOnEndPointer(this, null);
            this.r = 2;
        } else if (this.r == 6) {
            this.r = 1;
        }
    }

    @Override // com.nuance.nmsp.client.sdk.common.oem.api.AudioSystem.ErrorCallback
    public void errorCallback(Object obj) {
        if (a.isDebugEnabled()) {
            a.debug("RecorderImpl.errorCallback() _state:" + this.r);
        }
        if (this.r == 1 || this.r == 2 || this.r == 3 || this.r == 4 || this.r == 5 || this.r == 6) {
            if (this.r == 4) {
                c();
                b();
            }
            a(Recorder.RECORD_ERROR, (Object) null);
            this.r = 7;
        }
    }

    @Override // com.nuance.nmsp.client.sdk.components.audiorecord.Recorder
    public Vector getParams(Vector vector) throws ResourceException {
        if (vector == null) {
            return null;
        }
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 >= vector.size()) {
                return this.c.getParams(vector);
            }
            Parameter parameter = (Parameter) vector.elementAt(i2);
            if (parameter.getType() != Parameter.Type.SDK) {
                if (a.isErrorEnabled()) {
                    a.error("IllegalArgumentException Parameter type: " + parameter.getType() + " not allowed. ");
                }
                throw new IllegalArgumentException("Parameter type: " + parameter.getType() + " not allowed. ");
            }
            i = i2 + 1;
        }
    }

    @Override // com.nuance.nmsp.client.sdk.common.oem.api.MessageSystem.MessageHandler
    public void handleMessage(Object obj, Object obj2) {
        MessageSystem.MessageData messageData = (MessageSystem.MessageData) obj;
        switch (messageData.command) {
            case 1:
                a((NMSPAudioSink) null, 0);
                return;
            case 2:
                Object[] objArr = (Object[]) messageData.data;
                a((NMSPAudioSink) objArr[0], ((Integer) objArr[1]).intValue());
                return;
            case 3:
                a(false);
                return;
            case 4:
                Object[] objArr2 = (Object[]) messageData.data;
                handleStartCapturing((NMSPAudioSink) objArr2[0], ((Integer) objArr2[1]).intValue());
                return;
            case 5:
                handleStopCapturing(false);
                return;
            default:
                return;
        }
    }

    public void handleStartCapturing(NMSPAudioSink nMSPAudioSink, int i) {
        if (a.isDebugEnabled()) {
            a.debug("RecorderImpl.handleStartCapturing(" + nMSPAudioSink + ") _state:" + this.r);
        }
        this.i = i;
        this.f = nMSPAudioSink;
        if (this.r == 1) {
            if (this.m) {
                this.c.turnOnEndPointer(this, null);
                this.r = 2;
                return;
            } else {
                a();
                this.r = 4;
                return;
            }
        }
        if (this.r == 3) {
            this.r = 2;
        } else if (this.r == 6) {
            this.r = 5;
        }
    }

    public void handleStopCapturing(boolean z) {
        if (a.isDebugEnabled()) {
            a.debug("RecorderImpl.handleStopCapturing() _state:" + this.r);
        }
        if (this.r == 2) {
            d();
            if (z) {
                a(Recorder.CAPTURE_TIMEOUT, (Object) null);
            }
            this.r = 3;
            return;
        }
        if (this.r != 4) {
            if (this.r == 5) {
                d();
                this.r = 6;
                return;
            }
            return;
        }
        d();
        if (z) {
            a(Recorder.CAPTURE_TIMEOUT, (Object) null);
        }
        if (this.r == 4) {
            c();
            b();
        }
        if (!this.m) {
            this.r = 1;
        } else {
            this.c.turnOffEndPointer(this, null);
            this.r = 6;
        }
    }

    @Override // com.nuance.nmsp.client.sdk.common.oem.api.AudioSystem.NoSpeechCallback
    public void noSpeechCallback(Object obj) {
    }

    @Override // com.nuance.nmsp.client.sdk.components.audiorecord.Recorder
    public void startCapturing(NMSPAudioSink nMSPAudioSink, int i) {
        if (a.isDebugEnabled()) {
            a.debug("RecorderImpl.startCapturing(" + nMSPAudioSink + ")");
        }
        if (nMSPAudioSink == null) {
            throw new IllegalArgumentException("audioSink cannot be null.");
        }
        if (i < 0) {
            throw new IllegalArgumentException("audioCaptureTime is invalid.");
        }
        this.e.send(new MessageSystem.MessageData((byte) 4, new Object[]{nMSPAudioSink, new Integer(i)}), this, Thread.currentThread(), this.e.getVRAddr()[0]);
    }

    @Override // com.nuance.nmsp.client.sdk.common.oem.api.AudioSystem.StartOfSpeechCallback
    public void startOfSpeechCallback(Object obj) {
        if (a.isDebugEnabled()) {
            a.debug("RecorderImpl::startOfSpeechCallback() _state:" + this.r);
        }
        if (this.r == 4) {
            a(Recorder.START_OF_SPEECH, (Object) null);
            c();
        }
    }

    @Override // com.nuance.nmsp.client.sdk.components.audiorecord.Recorder
    public void startRecording() {
        if (a.isDebugEnabled()) {
            a.debug("RecorderImpl.startRecording()");
        }
        this.g = 0L;
        a(SessionEvent.NMSP_CALLLOG_RECORDERSTART_EVENT);
        this.e.send(new MessageSystem.MessageData((byte) 1, null), this, Thread.currentThread(), this.e.getVRAddr()[0]);
    }

    @Override // com.nuance.nmsp.client.sdk.components.audiorecord.Recorder
    public void startRecording(NMSPAudioSink nMSPAudioSink, int i) {
        if (a.isDebugEnabled()) {
            a.debug("RecorderImpl.startRecording(" + nMSPAudioSink + ")");
        }
        if (nMSPAudioSink == null) {
            throw new IllegalArgumentException("audioSink cannot be null.");
        }
        if (i < 0) {
            throw new IllegalArgumentException("audioCaptureTime is invalid.");
        }
        this.g = 0L;
        a(SessionEvent.NMSP_CALLLOG_RECORDERSTART_EVENT);
        this.e.send(new MessageSystem.MessageData((byte) 2, new Object[]{nMSPAudioSink, new Integer(i)}), this, Thread.currentThread(), this.e.getVRAddr()[0]);
    }

    @Override // com.nuance.nmsp.client.sdk.common.oem.api.AudioSystem.StopCallback
    public void stopCallback(AudioSystem.AudioStatus audioStatus, Object obj) {
        if (a.isDebugEnabled()) {
            a.debug("RecorderImpl.stopCallback() _state:" + this.r);
        }
        a("StreamStop");
        if (this.r == 1 || this.r == 3 || this.r == 7) {
            a("STOPPED", (Object) null);
            this.r = 8;
            return;
        }
        if (this.r == 2 || this.r == 4 || this.r == 5 || this.r == 6 || this.r == 9) {
            if (this.r == 4) {
                c();
                b();
            }
            d();
            a("STOPPED", (Object) null);
            this.r = 8;
        }
    }

    @Override // com.nuance.nmsp.client.sdk.components.audiorecord.Recorder
    public void stopCapturing() {
        if (a.isDebugEnabled()) {
            a.debug("RecorderImpl.startCapturing(" + this.f + ")");
        }
        this.e.send(new MessageSystem.MessageData((byte) 5, null), this, Thread.currentThread(), this.e.getVRAddr()[0]);
    }

    @Override // com.nuance.nmsp.client.sdk.components.audiorecord.Recorder
    public void stopRecording() {
        if (a.isDebugEnabled()) {
            a.debug("RecorderImpl.stop()");
        }
        a(SessionEvent.NMSP_CALLLOG_RECORDERSTOP_EVENT);
        this.e.send(new MessageSystem.MessageData((byte) 3, null), this, Thread.currentThread(), this.e.getVRAddr()[0]);
    }
}
