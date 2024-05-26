package com.nuance.nmdp.speechkit;

import com.nuance.nmdp.speechkit.util.pdx.PdxValue;

/* loaded from: classes.dex */
interface ICommand {
    void addParam(String str, PdxValue.Dictionary dictionary);

    void addParam(String str, PdxValue.String string);

    void cancel();

    void start();
}
