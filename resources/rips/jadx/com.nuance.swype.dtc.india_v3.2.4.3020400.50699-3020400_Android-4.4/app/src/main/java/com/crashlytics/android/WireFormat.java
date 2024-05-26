package com.crashlytics.android;

/* loaded from: classes.dex */
final class WireFormat {
    static final int MESSAGE_SET_ITEM_TAG = 11;
    static final int MESSAGE_SET_ITEM_END_TAG = 12;
    static final int MESSAGE_SET_TYPE_ID_TAG = 16;
    static final int MESSAGE_SET_MESSAGE_TAG = 26;

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int makeTag(int fieldNumber, int wireType) {
        return (fieldNumber << 3) | wireType;
    }
}
