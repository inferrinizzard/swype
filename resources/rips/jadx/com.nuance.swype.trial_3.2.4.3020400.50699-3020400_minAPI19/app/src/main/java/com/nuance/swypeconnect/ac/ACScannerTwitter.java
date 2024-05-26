package com.nuance.swypeconnect.ac;

import com.nuance.connect.api.DLMConnector;
import com.nuance.connect.common.FeaturesLastUsed;
import com.nuance.connect.util.HttpUtils;
import com.nuance.connect.util.Logger;
import com.nuance.swypeconnect.ac.ACScannerService;
import org.json.JSONException;

/* loaded from: classes.dex */
public final class ACScannerTwitter extends ACScanner {
    public static final int HTTP_FAILURE = 1100;
    private static final int MAX_DEFAULT = 100;
    public static final int PARSE_RESPONSE_FAILURE = 1101;
    public static final int RATE_LIMIT_EXCEEDED = 1102;
    private static final String SCANNER_TWITTER_LAST_RUN_CALENDAR = "SCANNER_TWITTER_LAST_RUN_CALENDAR";
    private static final String SCANNER_TWITTER_LAST_SCANNED_ID = "SCANNER_TWITTER_LAST_SCANNED_ID";
    private String accessToken;
    private String accessTokenSecret;
    private String consumerKey;
    private String consumerSecret;
    private ACTwitterScannerType[] scanType;
    private final ACScannerService service;
    private static final Logger.Log log = Logger.getLog(Logger.LoggerType.DEVELOPER, ACScannerTwitter.class.getSimpleName());
    static final ACScannerService.ScannerType TYPE = ACScannerService.ScannerType.TWITTER;

    /* loaded from: classes.dex */
    public enum ACTwitterScannerType {
        FRIENDS,
        MEMBERSHIPS,
        DIRECT_MESSAGES,
        STATUS
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ACScannerTwitter(ACScannerService aCScannerService) {
        super(aCScannerService, SCANNER_TWITTER_LAST_RUN_CALENDAR);
        this.service = aCScannerService;
        this.maxToProcess = 100;
    }

    private void scanDirectMessages(DLMConnector.ScannerBucket scannerBucket) {
        log.d("scan direct messages");
    }

    private void scanFriends(final DLMConnector.ScannerBucket scannerBucket) throws JSONException, HttpUtils.HttpUtilsException {
        log.d("scan friends list");
        HttpUtils.friendsListCallback(this.consumerKey, this.consumerSecret, this.accessToken, this.accessTokenSecret, new HttpUtils.FriendListCallback() { // from class: com.nuance.swypeconnect.ac.ACScannerTwitter.2
            @Override // com.nuance.connect.util.HttpUtils.FriendListCallback
            public void friend(String str, String str2) {
                ACScannerTwitter.log.d("friend name=", str, ", screenName=", str2);
                scannerBucket.scan(str);
                scannerBucket.scan(str2);
            }
        });
    }

    private void scanMemberships(DLMConnector.ScannerBucket scannerBucket) {
        log.d("scan memberships");
    }

    private void scanStatus(final DLMConnector.ScannerBucket scannerBucket) throws JSONException, HttpUtils.HttpUtilsException {
        log.d("scan status timeline");
        HttpUtils.userTimelineCallback(this.consumerKey, this.consumerSecret, this.accessToken, this.accessTokenSecret, this.maxToProcess, this.store.readLong(SCANNER_TWITTER_LAST_SCANNED_ID, -1L), new HttpUtils.TimelineCallback() { // from class: com.nuance.swypeconnect.ac.ACScannerTwitter.1
            @Override // com.nuance.connect.util.HttpUtils.TimelineCallback
            public void saveSinceId(long j) {
                ACScannerTwitter.this.store.saveLong(ACScannerTwitter.SCANNER_TWITTER_LAST_SCANNED_ID, j);
            }

            @Override // com.nuance.connect.util.HttpUtils.TimelineCallback
            public void tweet(String str, String str2, String str3, long j) {
                scannerBucket.scan(str3);
            }
        });
    }

    @Override // com.nuance.swypeconnect.ac.ACScanner
    public final void clearLastRun() {
        super.clearLastRun();
        this.store.delete(SCANNER_TWITTER_LAST_SCANNED_ID);
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
        this.service.getManager().getConnect().updateFeatureLastUsed(FeaturesLastUsed.Feature.SCANNER_TWITTER, System.currentTimeMillis());
        startScan();
        log.d("scan");
        if (this.scanType == null) {
            log.e("Scan type is null");
            endScan(false);
            return;
        }
        for (ACTwitterScannerType aCTwitterScannerType : this.scanType) {
            DLMConnector.ScannerBucket bucket = getBucket(this.scanCoreList, 1, false);
            try {
                if (ACTwitterScannerType.STATUS.equals(aCTwitterScannerType)) {
                    scanStatus(bucket);
                } else if (ACTwitterScannerType.DIRECT_MESSAGES.equals(aCTwitterScannerType)) {
                    scanDirectMessages(bucket);
                } else if (ACTwitterScannerType.FRIENDS.equals(aCTwitterScannerType)) {
                    scanFriends(bucket);
                } else if (ACTwitterScannerType.MEMBERSHIPS.equals(aCTwitterScannerType)) {
                    scanMemberships(bucket);
                } else {
                    log.d("Type not supported: ", aCTwitterScannerType);
                }
            } catch (HttpUtils.HttpUtilsException e) {
                log.e("Failed scan: " + e.getMessage());
                if (e.rateIsExceeded()) {
                    this.callback.onFailure(RATE_LIMIT_EXCEEDED, "Rate limit exceeded: " + e.getMessage());
                } else {
                    this.callback.onFailure(1100, "Failed to connect to twitter: " + e.getMessage());
                }
            } catch (JSONException e2) {
                log.e("Failed scan: " + e2.getMessage());
                this.callback.onFailure(1101, "Scanning response failed (JSON): " + e2.getMessage());
            } finally {
                bucket.close();
            }
        }
        endScan(true);
    }

    public final void setScanType(ACTwitterScannerType[] aCTwitterScannerTypeArr) {
        this.scanType = aCTwitterScannerTypeArr;
    }

    public final void setTokens(String str, String str2, String str3, String str4) {
        this.consumerKey = str;
        this.consumerSecret = str2;
        this.accessToken = str3;
        this.accessTokenSecret = str4;
    }

    @Override // com.nuance.swypeconnect.ac.ACScanner
    public final void start(ACScannerService.ACScannerCallback aCScannerCallback) throws ACScannerException {
        if (this.consumerKey == null || this.consumerSecret == null || this.accessToken == null || this.accessTokenSecret == null) {
            throw new ACScannerException(103);
        }
        super.start(aCScannerCallback);
        this.service.scheduleScan(this);
    }
}
