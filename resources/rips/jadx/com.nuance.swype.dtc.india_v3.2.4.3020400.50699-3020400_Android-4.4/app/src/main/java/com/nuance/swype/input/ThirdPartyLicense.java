package com.nuance.swype.input;

import android.content.Context;
import android.content.Intent;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/* loaded from: classes.dex */
public class ThirdPartyLicense {
    private boolean mIsLicensingOn;
    private Object mLicenseChecker;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ThirdPartyLicense(Context context) {
        this.mLicenseChecker = null;
        this.mIsLicensingOn = false;
        this.mIsLicensingOn = IMEApplication.from(context).getBuildInfo().isLicensingOn();
        String vendorCheckerClassName = context.getResources().getString(R.string.vendor_licensing);
        String.format("checker class name: %s", vendorCheckerClassName);
        try {
            Class<?> checkerClass = Class.forName(vendorCheckerClassName);
            String.format("checker class name: %s : get class already", vendorCheckerClassName);
            Constructor<?> constructor = checkerClass.getConstructor(Context.class, String.class, String.class);
            BuildInfo buildInfo = IMEApplication.from(context).getBuildInfo();
            this.mLicenseChecker = constructor.newInstance(context, "Swype", buildInfo.getBuildVersion());
            this.mLicenseChecker.getClass().getDeclaredMethod("isLicenseValid", new Class[0]).invoke(this.mLicenseChecker, new Object[0]);
        } catch (InvocationTargetException e) {
            Object[] objArr = new Object[1];
            objArr[0] = e.getMessage() != null ? e.getMessage() : "no exception message";
            String.format("catch InvocationTargetException when invoke 'isLicenseValid':%s", objArr);
        } catch (Throwable th) {
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isValid() {
        if (!this.mIsLicensingOn) {
            return true;
        }
        if (this.mLicenseChecker == null) {
            return false;
        }
        try {
            return ((Boolean) this.mLicenseChecker.getClass().getDeclaredMethod("isLicenseValid", new Class[0]).invoke(this.mLicenseChecker, new Object[0])).booleanValue();
        } catch (InvocationTargetException e) {
            Object[] objArr = new Object[1];
            objArr[0] = e.getMessage() != null ? e.getMessage() : "no exception message";
            String.format("catch InvocationTargetException when invoke 'isLicenseValid':%s", objArr);
            return false;
        } catch (Throwable th) {
            return false;
        }
    }

    public Intent getActivityIntentForInvalidLicense() {
        if (this.mLicenseChecker == null) {
            return null;
        }
        try {
            return (Intent) this.mLicenseChecker.getClass().getDeclaredMethod("getActivityIntentForInvalidLicense", new Class[0]).invoke(this.mLicenseChecker, new Object[0]);
        } catch (Throwable th) {
            return null;
        }
    }
}
