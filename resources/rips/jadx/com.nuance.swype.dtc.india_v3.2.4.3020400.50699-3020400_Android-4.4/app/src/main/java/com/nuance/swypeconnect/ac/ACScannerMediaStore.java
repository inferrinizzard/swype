package com.nuance.swypeconnect.ac;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import com.facebook.share.internal.ShareConstants;
import com.nuance.connect.api.DLMConnector;
import com.nuance.connect.common.FeaturesLastUsed;
import com.nuance.connect.util.Logger;
import com.nuance.connect.util.PermissionUtils;
import com.nuance.swypeconnect.ac.ACScannerService;
import java.util.ArrayList;
import java.util.Arrays;

/* loaded from: classes.dex */
public final class ACScannerMediaStore extends ACScanner {
    private static final int MEDIA_MAX_DEFAULT = -1;
    private static final String SCANNER_MEDIA_LAST_RUN_CALENDAR = "SCANNER_MEDIA_LAST_RUN_CALENDAR";
    private ArrayList<ACScannerMediaStoreType> scanTypes;
    private static final Logger.Log log = Logger.getLog(Logger.LoggerType.DEVELOPER, ACScannerMediaStore.class.getSimpleName());
    static final ACScannerService.ScannerType TYPE = null;

    /* loaded from: classes.dex */
    public enum ACScannerMediaStoreType {
        ALL,
        AUDIO_TITLE,
        AUDIO_ARTISTS,
        AUDIO_ALBUMS,
        VIDEO_TITLE,
        VIDEO_DESCRIPTION
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class MediaStoreReader {
        protected String dateField;

        private MediaStoreReader() {
            this.dateField = "date_added";
        }

        public static String getString(Cursor cursor, int i) {
            return cursor.getString(i);
        }

        public Cursor read(Context context, Uri uri, String[] strArr, String str, long j) {
            String str2 = str.equals("") ? "" : str + " AND ";
            try {
                return context.getContentResolver().query(uri, strArr, str2 + this.dateField + ">" + String.valueOf(j), null, this.dateField + " DESC");
            } catch (SQLiteException e) {
                return null;
            }
        }
    }

    public ACScannerMediaStore(ACScannerService aCScannerService) {
        super(aCScannerService, SCANNER_MEDIA_LAST_RUN_CALENDAR);
        this.maxToProcess = -1;
        setScanType(new ACScannerMediaStoreType[]{ACScannerMediaStoreType.ALL});
    }

    private void processMediaStore(Context context, MediaStoreReader mediaStoreReader, DLMConnector.ScannerBucket scannerBucket, Uri uri, String[] strArr, String str, long j) {
        Cursor read = mediaStoreReader.read(context, uri, strArr, str, j);
        if (read != null) {
            while (read.moveToNext()) {
                try {
                    if (this.maxToProcess != -1 && this.currentProcess > this.maxToProcess) {
                        for (int i = 0; i < strArr.length; i++) {
                            String string = MediaStoreReader.getString(read, i);
                            if (!TextUtils.isEmpty(string)) {
                                scannerBucket.scan(string);
                                this.currentProcess++;
                            }
                        }
                    }
                } finally {
                    log.d("Media Scanner processed " + this.currentProcess + " items.");
                    read.close();
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.nuance.swypeconnect.ac.ACScanner
    public final void fail(int i, String str) {
        if (this.callback != null) {
            this.callback.onFailure(i, str);
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
        this.service.getManager().getConnect().updateFeatureLastUsed(FeaturesLastUsed.Feature.SCANNER_MEDIA, System.currentTimeMillis());
        startScan();
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        DLMConnector.ScannerBucket bucket = getBucket(this.scanCoreList, 0, false);
        MediaStoreReader mediaStoreReader = new MediaStoreReader();
        Context context = this.service.getContext();
        long timeInMillis = this.lastRunCalendar != null ? this.lastRunCalendar.getTimeInMillis() : 0L;
        log.d("begin media store scanning");
        log.d("scan since:" + timeInMillis);
        if (this.scanTypes.contains(ACScannerMediaStoreType.ALL)) {
            arrayList2.add("album");
            arrayList2.add("artist");
            arrayList2.add(ShareConstants.WEB_DIALOG_PARAM_TITLE);
        } else {
            if (this.scanTypes.contains(ACScannerMediaStoreType.AUDIO_ALBUMS)) {
                arrayList2.add("album");
            }
            if (this.scanTypes.contains(ACScannerMediaStoreType.AUDIO_ARTISTS)) {
                arrayList2.add("artist");
            }
            if (this.scanTypes.contains(ACScannerMediaStoreType.AUDIO_TITLE)) {
                arrayList2.add(ShareConstants.WEB_DIALOG_PARAM_TITLE);
            }
        }
        if (arrayList2.size() > 0) {
            String[] strArr = (String[]) arrayList2.toArray(new String[arrayList2.size()]);
            StringBuilder sb = new StringBuilder();
            sb.append("is_music");
            sb.append(" != 0");
            processMediaStore(context, mediaStoreReader, bucket, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, strArr, sb.toString(), timeInMillis);
            processMediaStore(context, mediaStoreReader, bucket, MediaStore.Audio.Media.INTERNAL_CONTENT_URI, strArr, sb.toString(), timeInMillis);
        }
        if (this.scanTypes.contains(ACScannerMediaStoreType.ALL)) {
            arrayList.add(ShareConstants.WEB_DIALOG_PARAM_TITLE);
            arrayList.add("description");
        } else {
            if (this.scanTypes.contains(ACScannerMediaStoreType.VIDEO_TITLE)) {
                arrayList.add(ShareConstants.WEB_DIALOG_PARAM_TITLE);
            }
            if (this.scanTypes.contains(ACScannerMediaStoreType.VIDEO_DESCRIPTION)) {
                arrayList.add("description");
            }
        }
        if (arrayList.size() > 0) {
            String[] strArr2 = (String[]) arrayList.toArray(new String[arrayList.size()]);
            processMediaStore(context, mediaStoreReader, bucket, MediaStore.Video.Media.EXTERNAL_CONTENT_URI, strArr2, "", timeInMillis);
            processMediaStore(context, mediaStoreReader, bucket, MediaStore.Video.Media.INTERNAL_CONTENT_URI, strArr2, "", timeInMillis);
        }
        log.d("end media store scanning");
        bucket.close();
        endScan(true);
    }

    public final void setScanType(ACScannerMediaStoreType[] aCScannerMediaStoreTypeArr) {
        if (aCScannerMediaStoreTypeArr == null) {
            this.scanTypes = null;
        } else {
            this.scanTypes = new ArrayList<>(Arrays.asList(aCScannerMediaStoreTypeArr));
        }
    }

    @Override // com.nuance.swypeconnect.ac.ACScanner
    public final void start(ACScannerService.ACScannerCallback aCScannerCallback) throws ACScannerException {
        super.start(aCScannerCallback);
        if (this.scanTypes == null || this.scanTypes.size() == 0) {
            throw new ACScannerException(101);
        }
        if (!PermissionUtils.checkPermission(this.service.getContext(), 16, PermissionUtils.READ_EXTERNAL_STORAGE)) {
            throw new ACScannerException(100, "Media Scanner requires permission to read external storage");
        }
        this.callback = aCScannerCallback;
        this.service.scheduleScan(this);
    }
}
