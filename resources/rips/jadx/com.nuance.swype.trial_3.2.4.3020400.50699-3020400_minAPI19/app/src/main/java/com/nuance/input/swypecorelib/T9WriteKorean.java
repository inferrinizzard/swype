package com.nuance.input.swypecorelib;

/* loaded from: classes.dex */
public class T9WriteKorean extends T9WriteCJK {
    private static native long Write_Korean_create(String str);

    private static native void Write_Korean_destroy(long j);

    /* JADX INFO: Access modifiers changed from: package-private */
    public T9WriteKorean() {
        super(new T9WriteCJKSetting());
    }

    @Override // com.nuance.input.swypecorelib.T9Write
    protected long create_native_context(String databaseConfigFile) {
        this.nativeContext = Write_Korean_create(databaseConfigFile);
        return this.nativeContext;
    }

    @Override // com.nuance.input.swypecorelib.T9Write
    protected void destroy_native_context(long nativeContext) {
        Write_Korean_destroy(nativeContext);
    }
}
