package com.nuance.nmdp.speechkit;

import com.nuance.nmdp.speechkit.util.pdx.PdxValue;

/* loaded from: classes.dex */
public interface GenericRecognition {
    PdxValue.Dictionary getAppserverResult();

    PdxValue.Dictionary getFullResult();

    boolean isFinalResult();
}
