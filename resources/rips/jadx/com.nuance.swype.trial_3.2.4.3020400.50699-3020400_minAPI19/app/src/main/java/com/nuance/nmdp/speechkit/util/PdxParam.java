package com.nuance.nmdp.speechkit.util;

import com.nuance.nmdp.speechkit.transaction.generic.IGenericParam;
import com.nuance.nmdp.speechkit.transaction.generic.IGenericParamValue;
import com.nuance.nmdp.speechkit.util.pdx.PdxValue;

/* loaded from: classes.dex */
public class PdxParam implements IGenericParam {
    private final String _name;
    private final IGenericParamValue _value;

    public PdxParam(String name, PdxValue value) {
        this._name = name;
        this._value = new PdxParamValue(value);
    }

    @Override // com.nuance.nmdp.speechkit.transaction.generic.IGenericParam
    public String getName() {
        return this._name;
    }

    @Override // com.nuance.nmdp.speechkit.transaction.generic.IGenericParam
    public IGenericParamValue getValue() {
        return this._value;
    }
}
