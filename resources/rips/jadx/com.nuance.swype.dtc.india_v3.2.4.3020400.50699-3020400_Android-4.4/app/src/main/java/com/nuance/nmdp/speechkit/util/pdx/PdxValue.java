package com.nuance.nmdp.speechkit.util.pdx;

import com.nuance.nmdp.speechkit.util.Logger;
import com.nuance.nmsp.client.sdk.common.protocols.ProtocolDefines;
import com.nuance.swype.input.ThemeManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/* loaded from: classes.dex */
public abstract class PdxValue {
    public static final int TYPE_BYTES = 4;
    public static final int TYPE_DICT = 2;
    public static final int TYPE_INT = 1;
    public static final int TYPE_SEQ = 3;
    public static final int TYPE_SEQ_CHUNK = 6;
    public static final int TYPE_SEQ_END = 7;
    public static final int TYPE_SEQ_START = 5;
    public static final int TYPE_STRING = 0;
    private final int _type;

    public abstract java.lang.String toString(java.lang.String str);

    /* loaded from: classes.dex */
    public static class Dictionary extends DictionaryBase {
        public Dictionary() {
            this(null);
        }

        public Dictionary(Map<java.lang.String, PdxValue> values) {
            super(values, 2);
        }
    }

    /* loaded from: classes.dex */
    public static class SeqenceStart extends DictionaryBase {
        public SeqenceStart() {
            this(null);
        }

        public SeqenceStart(Map<java.lang.String, PdxValue> values) {
            super(values, 5);
        }
    }

    /* loaded from: classes.dex */
    public static class SeqenceChunk extends DictionaryBase {
        public SeqenceChunk() {
            this(null);
        }

        public SeqenceChunk(Map<java.lang.String, PdxValue> values) {
            super(values, 6);
        }
    }

    /* loaded from: classes.dex */
    public static class SeqenceEnd extends DictionaryBase {
        public SeqenceEnd() {
            this(null);
        }

        public SeqenceEnd(Map<java.lang.String, PdxValue> values) {
            super(values, 7);
        }
    }

    /* loaded from: classes.dex */
    public static abstract class DictionaryBase extends PdxValue {
        private final Map<java.lang.String, PdxValue> _values;

        DictionaryBase(Map<java.lang.String, PdxValue> values, int type) {
            super(type);
            this._values = new HashMap();
            if (values != null) {
                this._values.putAll(values);
            }
        }

        public void put(java.lang.String key, PdxValue value) {
            if (canPut(key, value)) {
                this._values.put(key, value);
            }
        }

        public void put(java.lang.String key, java.lang.String value) {
            if (canPut(key, value)) {
                this._values.put(key, new String(value));
            }
        }

        public void put(java.lang.String key, int value) {
            if (canPut(key, new Integer(value))) {
                this._values.put(key, new Integer(value));
            }
        }

        public void put(java.lang.String key, byte[] value) {
            if (canPut(key, value)) {
                this._values.put(key, new Bytes(value));
            }
        }

        public Set<Map.Entry<java.lang.String, PdxValue>> getEntries() {
            return this._values.entrySet();
        }

        public PdxValue get(java.lang.String key) {
            return this._values.get(key);
        }

        @Override // com.nuance.nmdp.speechkit.util.pdx.PdxValue
        public java.lang.String toString(java.lang.String indent) {
            StringBuilder builder = new StringBuilder();
            builder.append("{\n");
            java.lang.String itemIndent = indent + ThemeManager.NO_PRICE;
            for (Map.Entry<java.lang.String, PdxValue> entry : this._values.entrySet()) {
                builder.append(itemIndent);
                builder.append(entry.getKey());
                builder.append(" : ");
                builder.append(entry.getValue().toString(itemIndent + ThemeManager.NO_PRICE));
                builder.append(",\n");
            }
            builder.append(indent);
            builder.append("}");
            return builder.toString();
        }

        private boolean canPut(java.lang.String key, Object value) {
            if (key != null && value != null) {
                return true;
            }
            Logger.warn(this, "ignore this put action since either the key or the value is null: key[" + key + "] value[" + value + "]");
            return false;
        }
    }

    /* loaded from: classes.dex */
    public static final class Sequence extends PdxValue {
        private final List<PdxValue> _values;

        public Sequence() {
            this(null);
        }

        public Sequence(List<PdxValue> values) {
            super(3);
            this._values = new ArrayList();
            if (values != null) {
                this._values.addAll(values);
            }
        }

        public final void add(PdxValue value) {
            if (canAdd(value)) {
                this._values.add(value);
            }
        }

        public final void add(java.lang.String value) {
            if (canAdd(value)) {
                this._values.add(new String(value));
            }
        }

        public final void add(int value) {
            this._values.add(new Integer(value));
        }

        public final void add(byte[] value) {
            if (canAdd(value)) {
                this._values.add(new Bytes(value));
            }
        }

        public final List<PdxValue> getValues() {
            return this._values;
        }

        public final PdxValue get(int index) {
            return this._values.get(index);
        }

        public final int size() {
            return this._values.size();
        }

        @Override // com.nuance.nmdp.speechkit.util.pdx.PdxValue
        public final java.lang.String toString(java.lang.String indent) {
            StringBuilder builder = new StringBuilder();
            builder.append("[\n");
            java.lang.String itemIndent = indent + ThemeManager.NO_PRICE;
            for (PdxValue val : this._values) {
                builder.append(itemIndent);
                builder.append(val.toString(itemIndent));
                builder.append(",\n");
            }
            builder.append(indent);
            builder.append("]");
            return builder.toString();
        }

        private boolean canAdd(Object value) {
            if (value != null) {
                return true;
            }
            Logger.warn(this, "ignore this add action since the value is null: value[" + value + "]");
            return false;
        }
    }

    /* loaded from: classes.dex */
    public static final class Integer extends PdxValue {
        private final int _value;

        public Integer(int value) {
            super(1);
            this._value = value;
        }

        public final int get() {
            return this._value;
        }

        @Override // com.nuance.nmdp.speechkit.util.pdx.PdxValue
        public final java.lang.String toString(java.lang.String indent) {
            return java.lang.Integer.toString(this._value);
        }
    }

    /* loaded from: classes.dex */
    public static final class String extends PdxValue {
        private final java.lang.String _value;

        public String(java.lang.String value) {
            super(0);
            this._value = value;
        }

        public final java.lang.String get() {
            return this._value;
        }

        @Override // com.nuance.nmdp.speechkit.util.pdx.PdxValue
        public final java.lang.String toString(java.lang.String indent) {
            return this._value.replace("\n", "\n" + indent);
        }
    }

    /* loaded from: classes.dex */
    public static final class Bytes extends PdxValue {
        private final byte[] _bytes;

        public Bytes(byte[] bytes) {
            super(4);
            this._bytes = bytes;
        }

        public final byte[] get() {
            return this._bytes;
        }

        @Override // com.nuance.nmdp.speechkit.util.pdx.PdxValue
        public final java.lang.String toString(java.lang.String indent) {
            char[] hexMap = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
            StringBuilder builder = new StringBuilder();
            builder.append("0x");
            byte[] arr$ = this._bytes;
            for (byte b : arr$) {
                byte high = (byte) ((b >> 4) & 15);
                byte low = (byte) (b & ProtocolDefines.XMODE_PROTOCOL_BB_HANDSHAKE);
                builder.append(hexMap[high]);
                builder.append(hexMap[low]);
            }
            return builder.toString();
        }
    }

    PdxValue(int type) {
        this._type = type;
    }

    public final int getType() {
        return this._type;
    }
}
