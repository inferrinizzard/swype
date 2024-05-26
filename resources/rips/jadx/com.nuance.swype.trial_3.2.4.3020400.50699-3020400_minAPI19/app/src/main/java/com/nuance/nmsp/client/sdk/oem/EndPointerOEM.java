package com.nuance.nmsp.client.sdk.oem;

import com.nuance.nmsp.client.sdk.common.defines.NMSPDefines;
import com.nuance.nmsp.client.sdk.common.oem.api.LogFactory;
import com.nuance.nmsp.client.sdk.components.general.Parameter;
import java.util.Vector;

/* loaded from: classes.dex */
public class EndPointerOEM {
    private LogFactory.Log a = LogFactory.getLog(getClass());
    private boolean b = false;
    private a c = new a(this, 0);

    /* loaded from: classes.dex */
    public static class EpType {
        public static final EpType DETECT_NOTHING = new EpType(1);
        public static final EpType SPEECH_END = new EpType(2);
        public static final EpType SPEECH_START = new EpType(3);
        private int a;

        private EpType(int i) {
            this.a = 1;
            this.a = i;
        }

        public static EpType getEpType(int i) {
            return SPEECH_END.a == i ? SPEECH_END : SPEECH_START.a == i ? SPEECH_START : DETECT_NOTHING;
        }
    }

    /* loaded from: classes.dex */
    class a {
        int a;
        int b;
        int c;
        int d;
        int e;
        int f;
        int g;
        int h;
        int i;

        private a(EndPointerOEM endPointerOEM) {
        }

        /* synthetic */ a(EndPointerOEM endPointerOEM, byte b) {
            this(endPointerOEM);
        }
    }

    private static native int getCurrentEndPointerState();

    private static native void initializeEndPointer(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9);

    private static native int resetVad();

    public EpType detectEndPointing() {
        return this.b ? EpType.getEpType(getCurrentEndPointerState()) : EpType.DETECT_NOTHING;
    }

    public void initialize(Vector vector) {
        this.c.a = 0;
        this.c.b = 0;
        this.c.c = 50;
        this.c.d = 15;
        this.c.e = 7;
        this.c.g = 50;
        this.c.h = 5;
        this.c.i = 35;
        this.c.f = 0;
        if (vector != null) {
            for (int i = 0; i < vector.size(); i++) {
                Parameter parameter = (Parameter) vector.get(i);
                String name = parameter.getName();
                if (name.equals(NMSPDefines.NMSP_DEFINES_ENDPOINTER)) {
                    if (new String(parameter.getValue()).equalsIgnoreCase("TRUE")) {
                        if (this.a.isDebugEnabled()) {
                            this.a.debug("Stop on end of speech is activated.");
                        }
                        this.c.a = 1;
                    }
                } else if (name.equals(NMSPDefines.NMSP_DEFINES_ENDPOINTER_SPEEX_VAD_LONG_UTTERANCE)) {
                    if (new String(parameter.getValue()).equalsIgnoreCase("TRUE")) {
                        this.c.b = 1;
                    }
                } else if (name.equals(NMSPDefines.NMSP_DEFINES_ENDPOINTER_SPEEX_VAD_HISTORY_LENGTH)) {
                    this.c.c = Integer.parseInt(new String(parameter.getValue()));
                } else if (name.equals("ep.VadBeginLength")) {
                    this.c.d = Integer.parseInt(new String(parameter.getValue()));
                } else if (name.equals("ep.VadBeginThreshold")) {
                    this.c.e = Integer.parseInt(new String(parameter.getValue()));
                } else if (name.equals(NMSPDefines.NMSP_DEFINES_ENDPOINTER_SPEEX_VAD_END_LENGTH)) {
                    this.c.g = Integer.parseInt(new String(parameter.getValue()));
                } else if (name.equals(NMSPDefines.NMSP_DEFINES_ENDPOINTER_SPEEX_VAD_END_THRESHOLD)) {
                    this.c.h = Integer.parseInt(new String(parameter.getValue()));
                } else if (name.equals("ep.VadInterSpeechLength")) {
                    this.c.i = Integer.parseInt(new String(parameter.getValue()));
                } else if (name.equals(NMSPDefines.NMSP_DEFINES_ENDPOINTER_SPEEX_VAD_BEGIN_DELAY)) {
                    this.c.f = Integer.parseInt(new String(parameter.getValue()));
                }
            }
        }
        this.b = this.c.a == 1;
        if (this.b) {
            initializeEndPointer(this.c.a, this.c.b, this.c.c, this.c.d, this.c.e, this.c.f, this.c.g, this.c.h, this.c.i);
            resetEndpointingDetection();
        }
    }

    public boolean isEndPointingActive() {
        return this.b;
    }

    public void resetEndpointingDetection() {
        if (this.b) {
            resetVad();
        }
    }
}
