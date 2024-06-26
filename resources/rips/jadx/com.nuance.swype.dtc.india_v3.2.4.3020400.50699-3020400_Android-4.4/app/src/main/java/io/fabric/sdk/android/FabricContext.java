package io.fabric.sdk.android;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import java.io.File;

/* loaded from: classes.dex */
final class FabricContext extends ContextWrapper {
    private final String componentName;
    private final String componentPath;

    public FabricContext(Context base, String componentName, String componentPath) {
        super(base);
        this.componentName = componentName;
        this.componentPath = componentPath;
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public final File getDatabasePath(String name) {
        File dir = new File(super.getDatabasePath(name).getParentFile(), this.componentPath);
        dir.mkdirs();
        return new File(dir, name);
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public final SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory) {
        return SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), factory);
    }

    @Override // android.content.ContextWrapper, android.content.Context
    @TargetApi(11)
    public final SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory, DatabaseErrorHandler errorHandler) {
        return SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name).getPath(), factory, errorHandler);
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public final File getFilesDir() {
        return new File(super.getFilesDir(), this.componentPath);
    }

    @Override // android.content.ContextWrapper, android.content.Context
    @TargetApi(8)
    public final File getExternalFilesDir(String type) {
        return new File(super.getExternalFilesDir(type), this.componentPath);
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public final File getCacheDir() {
        return new File(super.getCacheDir(), this.componentPath);
    }

    @Override // android.content.ContextWrapper, android.content.Context
    @TargetApi(8)
    public final File getExternalCacheDir() {
        return new File(super.getExternalCacheDir(), this.componentPath);
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public final SharedPreferences getSharedPreferences(String name, int mode) {
        return super.getSharedPreferences(this.componentName + ":" + name, mode);
    }
}
