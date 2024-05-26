package io.fabric.sdk.android.services.settings;

import android.content.Context;
import android.graphics.BitmapFactory;
import io.fabric.sdk.android.Fabric;
import io.fabric.sdk.android.services.common.CommonUtils;

/* loaded from: classes.dex */
public final class IconRequest {
    public final String hash;
    public final int height;
    public final int iconResourceId;
    public final int width;

    private IconRequest(String hash, int iconResourceId, int width, int height) {
        this.hash = hash;
        this.iconResourceId = iconResourceId;
        this.width = width;
        this.height = height;
    }

    public static IconRequest build(Context context, String iconHash) {
        if (iconHash == null) {
            return null;
        }
        try {
            int iconId = CommonUtils.getAppIconResourceId(context);
            Fabric.getLogger();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(context.getResources(), iconId, options);
            IconRequest iconRequest = new IconRequest(iconHash, iconId, options.outWidth, options.outHeight);
            return iconRequest;
        } catch (Exception e) {
            Fabric.getLogger().e("Fabric", "Failed to load icon", e);
            return null;
        }
    }
}
