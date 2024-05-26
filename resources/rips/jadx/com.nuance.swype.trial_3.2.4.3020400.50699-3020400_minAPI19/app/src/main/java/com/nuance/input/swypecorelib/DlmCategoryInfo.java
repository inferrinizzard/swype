package com.nuance.input.swypecorelib;

/* loaded from: classes.dex */
public class DlmCategoryInfo {
    public final int id;
    public final String info;
    public final String name;
    public static final DlmCategoryInfo EMPTY = new DlmCategoryInfo(0, "", "");
    static char[] nativeName = new char[32];
    static char[] nativeInfo = new char[32];
    static int[] nativeID = new int[1];
    static int[] nativeNameLen = new int[1];
    static int[] nativeInfoLen = new int[1];

    /* JADX INFO: Access modifiers changed from: package-private */
    public DlmCategoryInfo(int id, String name, String info) {
        this.id = id;
        this.name = name;
        this.info = info;
    }
}
