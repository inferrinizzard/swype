package com.nuance.nmsp.client.sdk.oem.bluetooth;

import android.content.Context;

/* loaded from: classes.dex */
final class b extends Bluetooth {
    public b(Context context) {
        super(context);
    }

    @Override // com.nuance.nmsp.client.sdk.oem.bluetooth.Bluetooth
    public final int getPlaybackStream() {
        if (isHeadsetConnected()) {
            return Audio.AUDIO_TRACK_VOICE_CALL_STREAM;
        }
        return 3;
    }

    @Override // com.nuance.nmsp.client.sdk.oem.bluetooth.Bluetooth
    public final int getRecordingSource() {
        if (isHeadsetConnected()) {
            return Audio.AUDIO_RECORD_AUDIO_SOURCE;
        }
        return 6;
    }

    @Override // com.nuance.nmsp.client.sdk.oem.bluetooth.Bluetooth
    public final void startBluetoothScoInternal() {
        this.mHeadset.startVoiceRecognition();
    }

    @Override // com.nuance.nmsp.client.sdk.oem.bluetooth.Bluetooth
    public final void stopBluetoothSco() {
        this.mHeadset.stopVoiceRecognition();
    }
}
