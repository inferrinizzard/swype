package com.nuance.nmsp.client.sdk.oem.bluetooth;

import android.content.Context;
import android.media.AudioManager;

/* loaded from: classes.dex */
final class c extends Bluetooth {
    private AudioManager a;

    public c(Context context) {
        super(context);
        this.a = (AudioManager) this.mContext.getSystemService("audio");
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
        if (AndroidVersion.USE_MUSIC_STREAM_FOR_BLUETOOTH) {
            return;
        }
        this.a.startBluetoothSco();
    }

    @Override // com.nuance.nmsp.client.sdk.oem.bluetooth.Bluetooth
    public final void stopBluetoothSco() {
        if (AndroidVersion.USE_MUSIC_STREAM_FOR_BLUETOOTH) {
            return;
        }
        this.a.stopBluetoothSco();
    }
}
