package com.localytics.android;

import android.text.TextUtils;
import com.localytics.android.ICreativeDownloadTask;
import com.localytics.android.Localytics;
import com.nuance.swype.input.hardkey.HardKeyboardManager;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Proxy;
import java.net.URL;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class CreativeDownloadTask implements ICreativeDownloadTask {
    private ICreativeDownloadTaskCallback mCallback;
    private MarketingMessage mCampaign;
    private LocalyticsDao mLocalyticsDao;
    private ICreativeDownloadTask.Priority mPriority;
    private String mRemoteFileLocation;
    private int mSequence;

    public CreativeDownloadTask(MarketingMessage campaign, ICreativeDownloadTask.Priority priority, int sequence, LocalyticsDao localyticsDao, ICreativeDownloadTaskCallback callback) {
        this.mCampaign = campaign;
        this.mRemoteFileLocation = JsonHelper.getSafeStringFromMap(campaign, "download_url");
        this.mPriority = priority;
        this.mSequence = sequence;
        this.mLocalyticsDao = localyticsDao;
        this.mCallback = callback;
    }

    @Override // com.localytics.android.ICreativeDownloadTask
    public ICreativeDownloadTask.Priority getPriority() {
        return this.mPriority;
    }

    @Override // com.localytics.android.ICreativeDownloadTask
    public void setPriority(ICreativeDownloadTask.Priority priority) {
        this.mPriority = priority;
    }

    @Override // com.localytics.android.ICreativeDownloadTask
    public int getSequence() {
        return this.mSequence;
    }

    @Override // com.localytics.android.ICreativeDownloadTask
    public MarketingMessage getCampaign() {
        return this.mCampaign;
    }

    @Override // java.lang.Runnable
    public void run() {
        boolean success = false;
        String localFileLocation = JsonHelper.getSafeStringFromMap(this.mCampaign, "local_file_location");
        if (!TextUtils.isEmpty(localFileLocation)) {
            success = downloadFile(this.mRemoteFileLocation, localFileLocation, true, this.mLocalyticsDao.getProxy());
        }
        if (success) {
            this.mCallback.onComplete(this);
        } else {
            this.mCallback.onError(this);
        }
    }

    boolean downloadFile(String remoteFilePath, String localFilePath, boolean isOverwrite, Proxy proxy) {
        File file = new File(localFilePath);
        if (file.exists() && !isOverwrite) {
            Localytics.Log.w(String.format("The file %s does exist and overwrite is turned off.", file.getAbsolutePath()));
            return true;
        }
        File dir = file.getParentFile();
        if (!dir.mkdirs() && !dir.isDirectory()) {
            Localytics.Log.w(String.format("Could not create the directory %s for saving file.", dir.getAbsolutePath()));
            return false;
        }
        File tempFile = new File(String.format("%s_%s_temp", localFilePath, Long.valueOf(this.mLocalyticsDao.getCurrentTimeMillis())));
        try {
            try {
            } catch (IOException e) {
                Localytics.Log.e("Failed to download campaign creative", e);
                if (tempFile.exists() && !tempFile.delete()) {
                    Localytics.Log.e("Failed to delete temporary file for campaign");
                }
            }
            if (!tempFile.createNewFile()) {
                if (tempFile.exists() && !tempFile.delete()) {
                    Localytics.Log.e("Failed to delete temporary file for campaign");
                }
                return false;
            }
            InputStream is = BaseUploadThread.createURLConnection(new URL(remoteFilePath), proxy).getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is, HardKeyboardManager.META_CTRL_RIGHT_ON);
            FileOutputStream fos = new FileOutputStream(tempFile.getPath());
            byte[] buffer = new byte[8192];
            while (true) {
                int read = bis.read(buffer);
                if (read == -1) {
                    break;
                }
                fos.write(buffer, 0, read);
            }
            fos.close();
            if (tempFile.renameTo(file)) {
                if (tempFile.exists() && !tempFile.delete()) {
                    Localytics.Log.e("Failed to delete temporary file for campaign");
                }
                return true;
            }
            Localytics.Log.e("Failed to create permanent file for campaign");
            if (tempFile.exists() && !tempFile.delete()) {
                Localytics.Log.e("Failed to delete temporary file for campaign");
            }
            return false;
        } catch (Throwable th) {
            if (tempFile.exists() && !tempFile.delete()) {
                Localytics.Log.e("Failed to delete temporary file for campaign");
            }
            throw th;
        }
    }

    @Override // java.lang.Comparable
    public int compareTo(ICreativeDownloadTask another) {
        ICreativeDownloadTask.Priority left = this.mPriority;
        ICreativeDownloadTask.Priority right = another.getPriority();
        return left == right ? this.mSequence - another.getSequence() : right.ordinal() - left.ordinal();
    }
}
