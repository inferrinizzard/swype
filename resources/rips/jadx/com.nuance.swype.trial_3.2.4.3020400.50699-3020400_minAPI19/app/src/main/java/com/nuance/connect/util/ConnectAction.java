package com.nuance.connect.util;

import android.os.Bundle;

/* loaded from: classes.dex */
public class ConnectAction {
    private Bundle extras;
    private ActionFilter filter;

    public ConnectAction(ActionFilter actionFilter, Bundle bundle) {
        this.filter = actionFilter;
        this.extras = bundle;
    }

    public Bundle getExtras() {
        return this.extras;
    }

    public ActionFilter getFilter() {
        return this.filter;
    }
}
