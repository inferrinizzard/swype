package io.fabric.sdk.android.services.persistence;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import io.fabric.sdk.android.Kit;

/* loaded from: classes.dex */
public final class PreferenceStoreImpl implements PreferenceStore {
    private final Context context;
    private final String preferenceName;
    private final SharedPreferences sharedPreferences;

    public PreferenceStoreImpl(Context context, String name) {
        if (context == null) {
            throw new IllegalStateException("Cannot get directory before context has been set. Call Fabric.with() first");
        }
        this.context = context;
        this.preferenceName = name;
        this.sharedPreferences = this.context.getSharedPreferences(this.preferenceName, 0);
    }

    @Override // io.fabric.sdk.android.services.persistence.PreferenceStore
    public final SharedPreferences get() {
        return this.sharedPreferences;
    }

    @Override // io.fabric.sdk.android.services.persistence.PreferenceStore
    public final SharedPreferences.Editor edit() {
        return this.sharedPreferences.edit();
    }

    @Override // io.fabric.sdk.android.services.persistence.PreferenceStore
    @TargetApi(9)
    public final boolean save(SharedPreferences.Editor editor) {
        if (Build.VERSION.SDK_INT < 9) {
            return editor.commit();
        }
        editor.apply();
        return true;
    }

    public PreferenceStoreImpl(Kit kit) {
        this(kit.context, kit.getClass().getName());
    }
}