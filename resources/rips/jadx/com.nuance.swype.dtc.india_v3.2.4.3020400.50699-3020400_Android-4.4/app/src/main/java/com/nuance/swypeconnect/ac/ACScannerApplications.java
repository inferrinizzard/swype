package com.nuance.swypeconnect.ac;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import com.nuance.connect.api.DLMConnector;
import com.nuance.connect.util.Logger;
import com.nuance.connect.util.PermissionUtils;
import com.nuance.swype.input.hardkey.HardKeyboardManager;
import com.nuance.swypeconnect.ac.ACScannerService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

/* loaded from: classes.dex */
public final class ACScannerApplications extends ACScanner {
    private static final int MAX_DEFAULT = 100;
    private static final String SCANNER_APPLICATION_LAST_RUN_CALENDAR = "SCANNER_APPLICATION_LAST_RUN_CALENDAR";
    private ArrayList<ACScannerApplicationTypes> scanTypes;
    private static final Logger.Log log = Logger.getLog(Logger.LoggerType.DEVELOPER, ACScannerApplications.class.getSimpleName());
    static final ACScannerService.ScannerType TYPE = ACScannerService.ScannerType.APPLICATION;

    /* loaded from: classes.dex */
    public enum ACScannerApplicationTypes {
        ALL,
        NON_SYSTEM
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ACScannerApplications(ACScannerService aCScannerService) {
        super(aCScannerService, SCANNER_APPLICATION_LAST_RUN_CALENDAR);
        setScanType(new ACScannerApplicationTypes[]{ACScannerApplicationTypes.NON_SYSTEM});
        this.maxToProcess = 100;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.nuance.swypeconnect.ac.ACScanner
    public final void fail(int i, String str) {
        if (this.callback != null) {
            this.callback.onFailure(i, str);
        }
    }

    protected final Calendar getInstalledTime(PackageInfo packageInfo) {
        Calendar calendar = null;
        try {
            long j = packageInfo.firstInstallTime;
            log.d("firstInstallTime: " + j);
            calendar = Calendar.getInstance();
            calendar.setTimeInMillis(j);
            return calendar;
        } catch (Exception e) {
            return calendar;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.nuance.swypeconnect.ac.ACScanner
    public final ACScannerService.ScannerType getType() {
        return TYPE;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.nuance.swypeconnect.ac.ACScanner
    public final void scan() {
        Calendar calendar;
        startScan();
        log.d("scan");
        PackageManager packageManager = this.service.getContext().getPackageManager();
        DLMConnector.ScannerBucket bucket = getBucket(this.scanCoreList, 0, true);
        try {
            for (ApplicationInfo applicationInfo : packageManager.getInstalledApplications(128)) {
                CharSequence applicationLabel = packageManager.getApplicationLabel(applicationInfo);
                if (applicationLabel != null && !applicationInfo.packageName.equals(applicationLabel)) {
                    log.d("checking application: " + applicationLabel.toString());
                    if (!this.scanTypes.contains(ACScannerApplicationTypes.NON_SYSTEM) || ((applicationInfo.flags & 1) == 0 && (applicationInfo.flags & 128) == 0)) {
                        try {
                            calendar = getInstalledTime(packageManager.getPackageInfo(applicationInfo.packageName, HardKeyboardManager.META_CTRL_ON));
                        } catch (PackageManager.NameNotFoundException e) {
                            calendar = null;
                        } catch (Exception e2) {
                            calendar = null;
                        }
                        if (calendar == null || this.lastRunCalendar == null || this.lastRunCalendar.before(calendar)) {
                            this.currentProcess++;
                            if (this.currentProcess > this.maxToProcess) {
                                break;
                            }
                            log.d("added application: " + applicationLabel.toString());
                            bucket.scan(applicationLabel.toString());
                        }
                    }
                }
            }
            bucket.close();
            endScan(true);
        } catch (Throwable th) {
            bucket.close();
            throw th;
        }
    }

    public final void setScanType(ACScannerApplicationTypes[] aCScannerApplicationTypesArr) {
        if (aCScannerApplicationTypesArr == null) {
            this.scanTypes = null;
        } else {
            this.scanTypes = new ArrayList<>(Arrays.asList(aCScannerApplicationTypesArr));
        }
    }

    @Override // com.nuance.swypeconnect.ac.ACScanner
    public final void start(ACScannerService.ACScannerCallback aCScannerCallback) throws ACScannerException {
        super.start(aCScannerCallback);
        if (!PermissionUtils.checkPermission(this.service.getContext(), "android.permission.GET_TASKS")) {
            throw new ACScannerException(100, "Application Scanner needs Manifest.permission.GET_TASKS");
        }
        this.callback = aCScannerCallback;
        this.service.scheduleScan(this);
    }
}
