package com.localytics.android;

import java.util.Map;

/* loaded from: classes.dex */
interface ManifestListener {
    void localyticsDidDownloadManifest(Map<String, Object> map, Map<String, Object> map2, boolean z);

    void localyticsWillDownloadManifest();
}
