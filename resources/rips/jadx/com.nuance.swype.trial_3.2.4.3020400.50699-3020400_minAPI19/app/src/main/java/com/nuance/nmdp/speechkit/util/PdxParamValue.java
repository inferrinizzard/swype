package com.nuance.nmdp.speechkit.util;

import com.nuance.nmdp.speechkit.transaction.generic.IGenericParamValue;
import com.nuance.nmdp.speechkit.util.pdx.PdxValue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
public class PdxParamValue implements IGenericParamValue {
    private final byte[] _bytes;
    private final Map<String, IGenericParamValue> _dict;
    private final int _int;
    private final java.util.List<IGenericParamValue> _seq;
    private final String _str;
    private final int _type;

    public PdxParamValue(PdxValue value) {
        this._type = value.getType();
        switch (this._type) {
            case 0:
                this._int = 0;
                this._str = ((PdxValue.String) value).get();
                this._bytes = null;
                this._seq = null;
                this._dict = null;
                return;
            case 1:
                this._int = ((PdxValue.Integer) value).get();
                this._str = null;
                this._bytes = null;
                this._seq = null;
                this._dict = null;
                return;
            case 2:
                this._int = 0;
                this._str = null;
                this._bytes = null;
                this._seq = null;
                this._dict = new HashMap();
                for (Map.Entry<String, PdxValue> e : ((PdxValue.Dictionary) value).getEntries()) {
                    this._dict.put(e.getKey(), new PdxParamValue(e.getValue()));
                }
                return;
            case 3:
                PdxValue.Sequence seq = (PdxValue.Sequence) value;
                this._int = 0;
                this._str = null;
                this._bytes = null;
                this._seq = new ArrayList(seq.size());
                this._dict = null;
                for (PdxValue v : seq.getValues()) {
                    this._seq.add(new PdxParamValue(v));
                }
                return;
            case 4:
                this._int = 0;
                this._str = null;
                this._bytes = ((PdxValue.Bytes) value).get();
                this._seq = null;
                this._dict = null;
                return;
            default:
                this._int = 0;
                this._str = null;
                this._bytes = null;
                this._seq = null;
                this._dict = null;
                return;
        }
    }

    @Override // com.nuance.nmdp.speechkit.transaction.generic.IGenericParamValue
    public Map<String, IGenericParamValue> getDictValue() {
        return this._dict;
    }

    @Override // com.nuance.nmdp.speechkit.transaction.generic.IGenericParamValue
    public java.util.List<IGenericParamValue> getSeqValue() {
        return this._seq;
    }

    @Override // com.nuance.nmdp.speechkit.transaction.generic.IGenericParamValue
    public String getStringValue() {
        return this._str;
    }

    @Override // com.nuance.nmdp.speechkit.transaction.generic.IGenericParamValue
    public int getIntValue() {
        return this._int;
    }

    @Override // com.nuance.nmdp.speechkit.transaction.generic.IGenericParamValue
    public byte[] getBytesValue() {
        return this._bytes;
    }

    @Override // com.nuance.nmdp.speechkit.transaction.generic.IGenericParamValue
    public int getType() {
        return this._type;
    }
}
