package com.nuance.swype.startup;

import android.os.Bundle;

/* loaded from: classes.dex */
public class StartupConnectTOSDelegate extends ConnectTOSDelegate {
    /* JADX INFO: Access modifiers changed from: package-private */
    public static StartupConnectTOSDelegate newInstance(Bundle savedInstanceState) {
        StartupConnectTOSDelegate f = new StartupConnectTOSDelegate();
        f.setArguments(savedInstanceState);
        return f;
    }
}
