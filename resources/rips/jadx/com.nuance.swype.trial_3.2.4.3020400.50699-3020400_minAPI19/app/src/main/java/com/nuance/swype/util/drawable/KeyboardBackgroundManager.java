package com.nuance.swype.util.drawable;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.input.InputMethods;
import com.nuance.swype.input.KeyboardEx;
import com.nuance.swype.input.KeyboardStyle;
import com.nuance.swype.input.R;
import com.nuance.swype.input.ThemeManager;
import com.nuance.swype.util.BitmapUtil;
import com.nuance.swype.util.DisplayUtils;
import com.nuance.swype.util.DrawingUtils;
import com.nuance.swype.util.LogManager;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/* loaded from: classes.dex */
public final class KeyboardBackgroundManager {
    public static final LogManager.Log log = LogManager.getLog("KeyboardBackgroundManager");
    private final Context mContext;
    private KeyboardEx.KeyboardDockMode mDockMode;
    private Drawable mDrawable;
    private int mOrientation;
    public boolean mReloadRequiredFromResources;
    private String mSku;

    public KeyboardBackgroundManager(Context context) {
        this.mContext = context;
    }

    public final synchronized Drawable getCachedDrawable() {
        Drawable drawable;
        synchronized (this) {
            LogManager.Log log2 = log;
            Object[] objArr = new Object[1];
            objArr[0] = "getting cached drawable. Is null? " + (this.mDrawable == null);
            log2.d(objArr);
            drawable = this.mDrawable;
        }
        return drawable;
    }

    public final boolean shouldLoadFromDisk(ThemeManager.SwypeTheme theme, KeyboardEx.KeyboardDockMode dockMode) {
        if (DrawingUtils.getKeyboardScale(this.mContext) != 1.0f) {
            return false;
        }
        InputMethods.Language currentLanguage = IMEApplication.from(this.mContext).getCurrentLanguage();
        if (currentLanguage == null || !currentLanguage.getCurrentInputMode().isHandwriting()) {
            return !(theme.getSource() == ThemeManager.SwypeTheme.THEME_SOURCE.EMBEDDED && !theme.getDisplayName(this.mContext.getResources()).contains("/")) && dockMode == KeyboardEx.KeyboardDockMode.DOCK_FULL;
        }
        return false;
    }

    public final synchronized boolean hasConfigChanged(String themeSku, KeyboardEx.KeyboardDockMode dockMode, int orientation) {
        boolean z;
        if ((this.mSku != null || themeSku == null) && themeSku != null && !this.mSku.equals(themeSku) && this.mDockMode == dockMode) {
            z = this.mOrientation != orientation;
        }
        return z;
    }

    public final synchronized Drawable loadDrawableFromResource(String sku, KeyboardEx.KeyboardDockMode mode, int orientation, KeyboardStyle style) {
        this.mSku = sku;
        this.mDockMode = mode;
        this.mOrientation = orientation;
        this.mDrawable = null;
        log.d("Loading background from resources");
        this.mDrawable = style.getDrawable(R.attr.background);
        setReloadRequiredFromResources(false);
        return this.mDrawable;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void loadBackground(String sku, KeyboardEx.KeyboardDockMode mode, int orientation, Handler handler) {
        boolean z = true;
        synchronized (this) {
            this.mSku = sku;
            this.mDockMode = mode;
            this.mOrientation = orientation;
            this.mDrawable = null;
            File bitmapFile = getBitmapFile(this.mSku);
            log.d("Bitmap file path is : " + bitmapFile.getAbsolutePath());
            if (bitmapFile.exists()) {
                log.d("Background file found on disk, loading...");
                Bitmap decodeFile = BitmapFactory.decodeFile(bitmapFile.getAbsolutePath());
                if (decodeFile != null) {
                    this.mDrawable = new BitmapDrawable(this.mContext.getResources(), decodeFile);
                } else {
                    log.d("loading failed.");
                    z = false;
                }
            } else {
                log.d("Drawable file not found.");
                z = false;
            }
            if (z) {
                log.d("Notifying the handler");
                setReloadRequiredFromResources(false);
                if (handler != null) {
                    log.d("Notifying background load complete.");
                    handler.obtainMessage(124, this.mDrawable).sendToTarget();
                }
            }
        }
    }

    private File getBitmapFile(String sku) {
        File themeFolder = new File(sku).getParentFile();
        return new File(themeFolder, this.mOrientation == 1 ? "background.png" : "background-land.png");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void saveCachedDrawable(int width, int height) {
        Drawable drawable = this.mDrawable;
        log.d("Creating bitmap width: " + width + ", height: " + height);
        Bitmap bitmap = BitmapUtil.createDeviceOptimizedBitmap(width, height, DisplayUtils.isLowEndDevice(this.mContext));
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        try {
            File filePath = getBitmapFile(this.mSku).getAbsoluteFile();
            log.d("Writing to " + filePath.getAbsolutePath());
            if (filePath.createNewFile()) {
                FileOutputStream fileOutputStream = new FileOutputStream(filePath);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                fileOutputStream.close();
            }
        } catch (IOException e) {
            log.e("Error saving file: " + e.getMessage());
        }
    }

    public final void setReloadRequiredFromResources(boolean reloadRequired) {
        this.mReloadRequiredFromResources = reloadRequired;
        log.d("reload required is now set to: " + this.mReloadRequiredFromResources);
    }
}
