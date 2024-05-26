package com.nuance.input.swypecorelib;

/* loaded from: classes.dex */
public class T9WriteJapanese extends T9WriteCJK {
    private static native long Write_Japanese_create(String str);

    private static native void Write_Japanese_destroy(long j);

    /* JADX INFO: Access modifiers changed from: package-private */
    public T9WriteJapanese() {
        super(new T9WriteCJKSetting());
    }

    @Override // com.nuance.input.swypecorelib.T9Write
    protected long create_native_context(String databaseConfigFile) {
        this.nativeContext = Write_Japanese_create(databaseConfigFile);
        return this.nativeContext;
    }

    @Override // com.nuance.input.swypecorelib.T9Write
    protected void destroy_native_context(long nativeContext) {
        Write_Japanese_destroy(nativeContext);
    }
}
