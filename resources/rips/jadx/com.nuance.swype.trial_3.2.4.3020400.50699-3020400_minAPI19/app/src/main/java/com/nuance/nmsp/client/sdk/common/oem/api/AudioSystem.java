package com.nuance.nmsp.client.sdk.common.oem.api;

import com.nuance.nmsp.client.sdk.common.util.Enum;
import java.util.Vector;

/* loaded from: classes.dex */
public interface AudioSystem {

    /* loaded from: classes.dex */
    public interface AudioCallback {
        void audioCallback(Vector vector, Vector vector2, IntegerMutable integerMutable, IntegerMutable integerMutable2, Float f, Object obj);

        void audioCallback(byte[] bArr, Object obj, IntegerMutable integerMutable, IntegerMutable integerMutable2, Float f, Object obj2);
    }

    /* loaded from: classes.dex */
    public static class AudioStatus extends Enum {
        public static final AudioStatus AUDIO_OK = new AudioStatus(0);
        public static final AudioStatus AUDIO_ERROR = new AudioStatus(1);

        private AudioStatus(int i) {
            super(i);
        }
    }

    /* loaded from: classes.dex */
    public interface DoneCallback {
        void doneCallback(AudioStatus audioStatus, Object obj);
    }

    /* loaded from: classes.dex */
    public interface EndOfSpeechCallback {
        void endOfSpeechCallback(Object obj);
    }

    /* loaded from: classes.dex */
    public interface EndPointerStartedCallback {
        void endPointerStartedCallback(Object obj);
    }

    /* loaded from: classes.dex */
    public interface EndPointerStoppedCallback {
        void endPointerStoppedCallback(Object obj);
    }

    /* loaded from: classes.dex */
    public interface ErrorCallback {
        void errorCallback(Object obj);
    }

    /* loaded from: classes.dex */
    public static class InputDevice extends Enum {
        public static InputDevice MICROPHONE = new InputDevice(0);
        public static InputDevice NETWORK = new InputDevice(1);
        public static InputDevice BLUETOOTH_HEADSET = new InputDevice(2);
        public static InputDevice WIRED_HEADSET = new InputDevice(3);

        private InputDevice(int i) {
            super(i);
        }
    }

    /* loaded from: classes.dex */
    public static class IntegerMutable {
        public int i;

        public IntegerMutable(int i) {
            this.i = i;
        }
    }

    /* loaded from: classes.dex */
    public interface NoSpeechCallback {
        void noSpeechCallback(Object obj);
    }

    /* loaded from: classes.dex */
    public static class OutputDevice extends Enum {
        public static OutputDevice SPEAKER = new OutputDevice(0);
        public static OutputDevice NETWORK = new OutputDevice(1);
        public static OutputDevice BLUETOOTH_HEADSET = new OutputDevice(2);
        public static OutputDevice BLUETOOTH_A2DP = new OutputDevice(3);
        public static OutputDevice WIRED_HEADSET = new OutputDevice(4);

        private OutputDevice(int i) {
            super(i);
        }
    }

    /* loaded from: classes.dex */
    public interface StartOfSpeechCallback {
        void startOfSpeechCallback(Object obj);
    }

    /* loaded from: classes.dex */
    public interface StopCallback {
        void stopCallback(AudioStatus audioStatus, Object obj);
    }

    InputDevice getInputDevice();

    OutputDevice getOutputDevice();

    Vector getParams(Vector vector);

    boolean nextPlayback();

    boolean pausePlayback(int i);

    void pauseRecording(int i);

    boolean previousPlayback();

    boolean startPlayback(OutputDevice outputDevice, AudioCallback audioCallback, DoneCallback doneCallback, Object obj);

    boolean startRecording(InputDevice inputDevice, boolean z, AudioCallback audioCallback, StopCallback stopCallback, ErrorCallback errorCallback, StartOfSpeechCallback startOfSpeechCallback, NoSpeechCallback noSpeechCallback, EndOfSpeechCallback endOfSpeechCallback, EndPointerStartedCallback endPointerStartedCallback, EndPointerStoppedCallback endPointerStoppedCallback, Object obj);

    void stopPlayback(StopCallback stopCallback, Object obj);

    void stopRecording(StopCallback stopCallback, Object obj);

    void turnOffEndPointer(EndPointerStoppedCallback endPointerStoppedCallback, Object obj);

    void turnOnEndPointer(EndPointerStartedCallback endPointerStartedCallback, Object obj);
}
