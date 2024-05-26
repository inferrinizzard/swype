package com.nuance.swype.usagedata;

import com.nuance.swype.usagedata.UsageData;
import com.nuance.swype.usagedata.UsageDataEvent;
import com.nuance.swype.util.LogManager;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
abstract class UsageDataEvent<T extends UsageDataEvent<T>> {
    protected final LogManager.Log log = LogManager.getLog(getLogTag());
    protected Map<String, String> mAttributes = new HashMap();
    private final String mTagEvent;

    protected abstract String getLogTag();

    /* JADX INFO: Access modifiers changed from: protected */
    public UsageDataEvent(String tagEvent) {
        this.mTagEvent = tagEvent;
    }

    public final void eventError(String err) {
        int i = 0;
        while (this.mAttributes.get(UsageData.EventAttribute.EVENT_ERR.toString() + i) != null && i < 10) {
            i++;
        }
        this.mAttributes.put(UsageData.EventAttribute.EVENT_ERR.toString() + i, err);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void commit() {
        UsageData.recordEvent(this.mTagEvent, this.mAttributes);
    }

    public String toString() {
        StringBuffer map = new StringBuffer();
        map.append(super.toString()).append("mAttributesMap: ").append(this.mAttributes.isEmpty() ? "empty" : "");
        for (Map.Entry<String, String> e : this.mAttributes.entrySet()) {
            map.append("{ ").append(e.getKey()).append(",").append(e.getValue()).append(" }, ");
        }
        return map.toString();
    }
}
