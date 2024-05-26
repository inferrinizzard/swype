package com.nuance.swype.usagedata;

import android.content.Context;
import com.nuance.swype.usagedata.CustomDimension;
import java.util.Map;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public interface Analytics {
    void endSession(Context context);

    void setCustomDimension(CustomDimension.Dimension dimension, String str);

    void startSession(Context context);

    void tagEvent(String str, Map<String, String> map);

    void tagScreen(String str);
}
