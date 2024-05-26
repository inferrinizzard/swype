package com.nuance.input.swypecorelib;

/* loaded from: classes.dex */
public class T9WriteChinese extends T9WriteCJK {
    private static native long Write_Chinese_create(String str);

    private static native void Write_Chinese_destroy(long j);

    /* JADX INFO: Access modifiers changed from: package-private */
    public T9WriteChinese() {
        super(new T9WriteCJKSetting());
        this.settings.setRecognitionMode(1);
    }

    @Override // com.nuance.input.swypecorelib.T9Write
    protected long create_native_context(String databaseConfigFile) {
        this.nativeContext = Write_Chinese_create(databaseConfigFile);
        return this.nativeContext;
    }

    @Override // com.nuance.input.swypecorelib.T9Write
    protected void destroy_native_context(long nativeContext) {
        Write_Chinese_destroy(nativeContext);
    }
}
