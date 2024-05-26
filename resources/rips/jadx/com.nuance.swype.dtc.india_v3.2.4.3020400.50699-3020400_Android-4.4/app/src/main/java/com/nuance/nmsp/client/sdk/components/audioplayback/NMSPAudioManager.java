package com.nuance.nmsp.client.sdk.components.audioplayback;

import com.nuance.nmsp.client.sdk.common.defines.NMSPDefines;
import com.nuance.nmsp.client.sdk.components.internal.audioplayback.PlayerImpl;
import com.nuance.nmsp.client.sdk.components.resource.common.Manager;
import java.util.Vector;

/* loaded from: classes.dex */
public class NMSPAudioManager {
    private NMSPAudioManager() {
    }

    public static Player createPlayer(NMSPAudioPlaybackListener nMSPAudioPlaybackListener, Manager manager, Vector vector, NMSPDefines.AudioSystem.OutputDevice outputDevice) {
        if (nMSPAudioPlaybackListener == null) {
            throw new NullPointerException("NMSPAudioPlaybackListener can not be null!");
        }
        if (manager == null) {
            throw new NullPointerException("Manager can not be null!");
        }
        return new PlayerImpl(nMSPAudioPlaybackListener, manager, vector, outputDevice);
    }
}
