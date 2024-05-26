package com.nuance.nmdp.speechkit.util.audio;

import com.google.api.client.http.ExponentialBackOffPolicy;
import com.nuance.nmdp.speechkit.SpeechKit;
import com.nuance.nmdp.speechkit.oem.OemFile;
import com.nuance.nmdp.speechkit.util.JobRunner;
import com.nuance.nmdp.speechkit.util.List;
import com.nuance.nmdp.speechkit.util.Logger;
import com.nuance.nmsp.client.sdk.common.defines.NMSPDefines;
import com.nuance.nmsp.client.sdk.components.audiorecord.NMSPAudioManager;
import com.nuance.nmsp.client.sdk.components.audiorecord.NMSPAudioRecordListener;
import com.nuance.nmsp.client.sdk.components.audiorecord.Recorder;
import com.nuance.nmsp.client.sdk.components.general.NMSPAudioSink;
import com.nuance.nmsp.client.sdk.components.general.Parameter;
import com.nuance.nmsp.client.sdk.components.general.TransactionProcessingException;
import com.nuance.nmsp.client.sdk.components.resource.common.Manager;
import com.nuance.nmsp.client.sdk.components.resource.nmas.AudioParam;
import com.nuance.swype.input.R;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/* loaded from: classes.dex */
public final class RecorderHelper {
    private final Object _appContext;
    private final Object _context;
    private final int _endpointerDelay;
    private final boolean _inputDisableRecorder;
    private final List.Iterator _inputEnum;
    private final IRecorderHelperListener _listener;
    private final List _output;
    private final String _outputFilename;
    private final AudioParam _param;
    private final SpeechKit.PartialResultsMode _partialResultsMode;
    private Recorder _recorder;
    private static String _staticOutputFilename = null;
    private static final List _staticInput = new List();
    private static boolean _staticInputDisableRecorder = false;
    int RECORDER_CAPTURE_TIMEOUT = ExponentialBackOffPolicy.DEFAULT_MAX_INTERVAL_MILLIS;
    int FRAMES_PER_SECOND = 50;
    private final NMSPAudioRecordListener _recordListener = createRecordListener();
    private boolean _started = false;
    private boolean _stopping = false;
    private boolean _waitingForStop = false;
    private boolean _stopped = false;
    private final Object _stopSync = new Object();

    public RecorderHelper(Manager manager, int detectionType, int endpointerDelay, int endOfSpeechDuration, int startOfSpeechTimeout, SpeechKit.PartialResultsMode partialResultsMode, java.util.List<Parameter> customRecorderParams, AudioParam param, Object context, Object appContext, IRecorderHelperListener listener) {
        this._listener = listener;
        this._context = context;
        this._endpointerDelay = endpointerDelay;
        this._param = param;
        this._appContext = appContext;
        this._partialResultsMode = partialResultsMode;
        this._inputEnum = _staticInput.size() == 0 ? null : _staticInput.copy().iterator();
        this._inputDisableRecorder = _staticInputDisableRecorder;
        this._outputFilename = _staticOutputFilename;
        this._output = _staticOutputFilename == null ? null : new List();
        if (!this._inputDisableRecorder) {
            List parameters = new List();
            if (customRecorderParams != null) {
                for (Parameter p : customRecorderParams) {
                    parameters.add(p);
                }
            }
            if (this._inputEnum == null) {
                switch (detectionType) {
                    case 1:
                        parameters.add(new Parameter(NMSPDefines.NMSP_DEFINES_ENDPOINTER, "TRUE".getBytes(), Parameter.Type.SDK));
                        break;
                    case 2:
                        parameters.add(new Parameter(NMSPDefines.NMSP_DEFINES_ENDPOINTER, "TRUE".getBytes(), Parameter.Type.SDK));
                        parameters.add(new Parameter(NMSPDefines.NMSP_DEFINES_ENDPOINTER_SPEEX_VAD_LONG_UTTERANCE, "TRUE".getBytes(), Parameter.Type.SDK));
                        break;
                    case 3:
                        parameters.add(new Parameter(NMSPDefines.NMSP_DEFINES_ENDPOINTER, "TRUE".getBytes(), Parameter.Type.SDK));
                        parameters.add(new Parameter(NMSPDefines.NMSP_DEFINES_ENDPOINTER_SPEEX_VAD_LONG_UTTERANCE, "TRUE".getBytes(), Parameter.Type.SDK));
                        parameters.add(new Parameter(NMSPDefines.NMSP_DEFINES_ENDPOINTER_SPEEX_VAD_HISTORY_LENGTH, Integer.toString(R.styleable.ThemeTemplate_symKeyboardFeedbackArrowRight).getBytes(), Parameter.Type.SDK));
                        parameters.add(new Parameter(NMSPDefines.NMSP_DEFINES_ENDPOINTER_SPEEX_VAD_END_LENGTH, Integer.toString(R.styleable.ThemeTemplate_symKeyboardFeedbackArrowRight).getBytes(), Parameter.Type.SDK));
                        parameters.add(new Parameter(NMSPDefines.NMSP_DEFINES_ENDPOINTER_SPEEX_VAD_END_THRESHOLD, Integer.toString(25).getBytes(), Parameter.Type.SDK));
                        break;
                }
            }
            parameters.add(new Parameter(NMSPDefines.NMSP_DEFINES_USE_ENERGY_LEVEL, "TRUE".getBytes(), Parameter.Type.SDK));
            parameters.add(new Parameter(NMSPDefines.NMSP_DEFINES_ANDROID_CONTEXT, this._appContext, Parameter.Type.SDK));
            if (endpointerDelay > 0) {
                int delayFrames = endpointerDelay / 20;
                parameters.add(new Parameter(NMSPDefines.NMSP_DEFINES_ENDPOINTER_SPEEX_VAD_BEGIN_DELAY, Integer.toString(delayFrames + 10).getBytes(), Parameter.Type.SDK));
            }
            if (endOfSpeechDuration > 0) {
                parameters.add(new Parameter(NMSPDefines.NMSP_DEFINES_ENDPOINTER_SPEEX_VAD_HISTORY_LENGTH, Integer.toString(this.FRAMES_PER_SECOND * endOfSpeechDuration).getBytes(), Parameter.Type.SDK));
                parameters.add(new Parameter(NMSPDefines.NMSP_DEFINES_ENDPOINTER_SPEEX_VAD_END_LENGTH, Integer.toString(this.FRAMES_PER_SECOND * endOfSpeechDuration).getBytes(), Parameter.Type.SDK));
            }
            if (startOfSpeechTimeout > 0) {
                parameters.add(new Parameter(NMSPDefines.NMSP_DEFINES_START_OF_SPEECH_TIMEOUT, Integer.valueOf(startOfSpeechTimeout * 1000), Parameter.Type.SDK));
            }
            try {
                this._recorder = NMSPAudioManager.createRecorder(this._recordListener, manager, parameters.toVector(), NMSPDefines.AudioSystem.InputDevice.MICROPHONE);
            } catch (Throwable tr) {
                Logger.error(this, "Error creating recorder", tr);
                this._recorder = null;
            }
        }
    }

    public final void startRecorder() {
        if (!this._started) {
            this._started = true;
            if (this._inputDisableRecorder) {
                this._listener.started(this._context);
                return;
            } else if (this._recorder != null) {
                try {
                    Logger.info(this, "Starting recorder");
                    this._recorder.startRecording();
                    return;
                } catch (Throwable tr) {
                    Logger.error(this, "Error starting recorder", tr);
                }
            }
        } else {
            Logger.error(this, "Recorder already started");
        }
        this._listener.stopped(this._context, 4);
    }

    public final void startCapturing() {
        Logger.info(this, "Capturing audio from recorder");
        int timeout = this.RECORDER_CAPTURE_TIMEOUT;
        switch (this._partialResultsMode) {
            case UTTERANCE_DETECTION_DEFAULT:
            case UTTERANCE_DETECTION_VERY_AGRESSIVE:
            case UTTERANCE_DETECTION_AGRESSIVE:
            case UTTERANCE_DETECTION_AVERAGE:
            case UTTERANCE_DETECTION_CONSERVATIVE:
            case CONTINUOUS_STREAMING_RESULTS:
                timeout = 0;
                break;
        }
        if (this._inputDisableRecorder) {
            if (this._inputEnum != null) {
                while (this._inputEnum.hasMore()) {
                    byte[] bytes = (byte[]) this._inputEnum.next();
                    try {
                        this._param.addAudioBuf(bytes, 0, bytes.length, !this._inputEnum.hasMore());
                    } catch (TransactionProcessingException e) {
                    }
                }
                this._stopping = true;
                this._listener.stopped(this._context, 2);
                return;
            }
            return;
        }
        if (this._inputEnum != null || this._output != null) {
            this._recorder.startCapturing(createCustomAudioSink(this._param), this._endpointerDelay + timeout);
        } else {
            this._recorder.startCapturing(this._param, this._endpointerDelay + timeout);
        }
    }

    public final void stopRecorder() {
        if (this._started && !this._stopping) {
            this._stopping = true;
            if (!this._inputDisableRecorder) {
                if (this._recorder != null) {
                    synchronized (this._stopSync) {
                        try {
                            if (!this._stopped) {
                                Logger.info(this, "Stopping recorder");
                                this._recorder.stopRecording();
                                this._waitingForStop = true;
                                while (!this._stopped) {
                                    try {
                                        this._stopSync.wait();
                                    } catch (InterruptedException e) {
                                    }
                                }
                            }
                        } catch (Throwable tr) {
                            Logger.error(this, "Error stopping recorder", tr);
                            this._stopped = true;
                        }
                    }
                    return;
                }
                Logger.error(this, "Can't stop recorder because it wasn't started");
                this._listener.stopped(this._context, 4);
            }
        }
    }

    private NMSPAudioSink createCustomAudioSink(final AudioParam param) {
        return new NMSPAudioSink() { // from class: com.nuance.nmdp.speechkit.util.audio.RecorderHelper.1
            @Override // com.nuance.nmsp.client.sdk.components.general.NMSPAudioSink
            public void addAudioBuf(byte[] audioBytes, int offset, int length, boolean isLastBuff) throws TransactionProcessingException {
                if (RecorderHelper.this._inputEnum != null) {
                    if (RecorderHelper.this._inputEnum.hasMore()) {
                        byte[] bytes = (byte[]) RecorderHelper.this._inputEnum.next();
                        boolean end = !RecorderHelper.this._inputEnum.hasMore();
                        param.addAudioBuf(bytes, 0, bytes.length, end);
                        if (end) {
                            JobRunner.addJob(new Runnable() { // from class: com.nuance.nmdp.speechkit.util.audio.RecorderHelper.1.1
                                @Override // java.lang.Runnable
                                public void run() {
                                    RecorderHelper.this.stopRecorder();
                                }
                            });
                        }
                    }
                } else {
                    param.addAudioBuf(audioBytes, offset, length, isLastBuff);
                }
                if (RecorderHelper.this._output != null) {
                    int len = length - offset;
                    byte[] bytes2 = new byte[len];
                    for (int i = 0; i < len; i++) {
                        bytes2[i] = audioBytes[offset + i];
                    }
                    RecorderHelper.this._output.add(bytes2);
                    if (isLastBuff) {
                        JobRunner.addJob(new Runnable() { // from class: com.nuance.nmdp.speechkit.util.audio.RecorderHelper.1.2
                            @Override // java.lang.Runnable
                            public void run() {
                                RecorderHelper.this.writeOutputFile();
                            }
                        });
                    }
                }
            }
        };
    }

    private NMSPAudioRecordListener createRecordListener() {
        return new NMSPAudioRecordListener() { // from class: com.nuance.nmdp.speechkit.util.audio.RecorderHelper.2
            private int _stopReason = 0;
            private boolean _recorderStarted = false;

            private void handleStop() {
                synchronized (RecorderHelper.this._stopSync) {
                    RecorderHelper.this._stopped = true;
                    if (RecorderHelper.this._waitingForStop) {
                        RecorderHelper.this._stopSync.notify();
                        RecorderHelper.this._waitingForStop = false;
                    }
                }
            }

            @Override // com.nuance.nmsp.client.sdk.components.audiorecord.NMSPAudioRecordListener
            public void recorderUpdate(Recorder recorder, String event, Object eventData) {
                if (recorder != RecorderHelper.this._recorder) {
                    Logger.warn(RecorderHelper.this, "Event " + event + " received for invalid recorder");
                    return;
                }
                if (event == Recorder.BUFFER_RECORDED) {
                    if (eventData instanceof Float) {
                        RecorderHelper.this._listener.signalEnergyUpdate(RecorderHelper.this._context, ((Float) eventData).floatValue());
                        return;
                    }
                    return;
                }
                if (event == "STARTED") {
                    this._recorderStarted = true;
                    RecorderHelper.this._listener.started(RecorderHelper.this._context);
                    return;
                }
                if (event == "STOPPED") {
                    Logger.info(RecorderHelper.this, "Recorder stopped");
                    handleStop();
                    RecorderHelper.this._listener.stopped(RecorderHelper.this._context, this._stopReason);
                    return;
                }
                if (event == Recorder.RECORD_ERROR) {
                    Logger.error(RecorderHelper.this, "Recorder error");
                    this._stopReason = 4;
                    if (!this._recorderStarted) {
                        handleStop();
                        RecorderHelper.this._listener.stopped(RecorderHelper.this._context, this._stopReason);
                        return;
                    }
                    return;
                }
                if (event == Recorder.NO_SPEECH) {
                    Logger.info(RecorderHelper.this, "Recorder event (no speech)");
                    this._stopReason = 1;
                } else if (event == Recorder.END_OF_SPEECH) {
                    Logger.info(RecorderHelper.this, "Recorder event (end of speech)");
                    this._stopReason = 2;
                } else if (event == Recorder.CAPTURE_TIMEOUT) {
                    Logger.info(RecorderHelper.this, "Recorder event (timeout)");
                    this._stopReason = 3;
                }
            }
        };
    }

    public static void writeToFile(String filename) {
        _staticOutputFilename = filename;
    }

    public static boolean readFromFile(String filename, boolean disableRecorder) {
        int b;
        _staticInput.clear();
        _staticInputDisableRecorder = false;
        if (filename != null) {
            InputStream stream = OemFile.openForRead(filename);
            if (stream != null) {
                while (true) {
                    int l = 0;
                    int count = 0;
                    while (true) {
                        if (count >= 5) {
                            Logger.error("RecorderHelper", "Too many audio frame size bytes");
                            _staticInput.clear();
                            b = -1;
                            break;
                        }
                        try {
                            b = stream.read();
                            count++;
                            if (b < 0) {
                                break;
                            }
                            l = (l << 7) | (b & 127);
                            if ((b & 128) == 0) {
                                break;
                            }
                        } catch (IOException e) {
                            Logger.error("RecorderHelper", "Error reading from stream");
                            _staticInput.clear();
                            b = -1;
                        }
                    }
                    if (b >= 0) {
                        if (l != 0) {
                            byte[] buffer = new byte[l];
                            int bytesRead = -1;
                            try {
                                bytesRead = stream.read(buffer, 0, l);
                            } catch (IOException e2) {
                            }
                            if (bytesRead != l) {
                                Logger.error("RecorderHelper", "Could not read requested number of bytes.");
                                _staticInput.clear();
                                break;
                            }
                            _staticInput.add(buffer);
                        }
                    }
                }
                try {
                    stream.close();
                    break;
                } catch (IOException e3) {
                }
            }
            _staticInput.trim();
            if (_staticInput.size() > 0) {
                _staticInputDisableRecorder = disableRecorder;
                return true;
            }
        }
        _staticInput.trim();
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void writeOutputFile() {
        OutputStream stream;
        List.Iterator e = this._output.iterator();
        if (e.hasMore() && (stream = OemFile.openForWrite(this._outputFilename)) != null) {
            while (e.hasMore()) {
                byte[] buffer = (byte[]) e.next();
                byte[] sizeBytes = new byte[5];
                int length = buffer.length;
                byte b = 0;
                int numSizeBytes = 0;
                while (length > 0) {
                    byte b2 = (byte) (((byte) (length & 127)) | b);
                    length >>= 7;
                    sizeBytes[4 - numSizeBytes] = b2;
                    b = Byte.MIN_VALUE;
                    numSizeBytes++;
                }
                try {
                    stream.write(sizeBytes, 5 - numSizeBytes, numSizeBytes);
                    stream.write(buffer);
                } catch (IOException ex) {
                    Logger.error(this, "Error writing audio to file", ex);
                }
            }
            try {
                stream.close();
            } catch (IOException e2) {
            }
            this._output.clear();
        }
    }
}
