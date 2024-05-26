package com.nuance.connect.sqlite;

/* loaded from: classes.dex */
class DlmEvent {
    final String appname;
    final int category;
    final String event;
    final boolean highPriority;
    final int inputType;
    final String locale;
    final String location;
    final long timestamp;

    /* JADX INFO: Access modifiers changed from: package-private */
    public DlmEvent(String str, int i, long j, String str2, String str3, String str4, int i2, boolean z) {
        this.event = str;
        this.category = i;
        this.timestamp = j;
        this.appname = str2;
        this.location = str3;
        this.locale = str4;
        this.inputType = i2;
        this.highPriority = z;
    }
}
