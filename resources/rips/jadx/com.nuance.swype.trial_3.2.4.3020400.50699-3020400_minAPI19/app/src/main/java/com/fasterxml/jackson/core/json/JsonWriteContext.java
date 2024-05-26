package com.fasterxml.jackson.core.json;

import com.fasterxml.jackson.core.JsonStreamContext;

/* loaded from: classes.dex */
public final class JsonWriteContext extends JsonStreamContext {
    protected JsonWriteContext _child = null;
    protected String _currentName;
    protected final JsonWriteContext _parent;

    public JsonWriteContext(int type, JsonWriteContext parent) {
        this._type = type;
        this._parent = parent;
        this._index = -1;
    }

    private JsonWriteContext reset(int type) {
        this._type = type;
        this._index = -1;
        this._currentName = null;
        return this;
    }

    public final JsonWriteContext createChildArrayContext() {
        JsonWriteContext ctxt = this._child;
        if (ctxt != null) {
            return ctxt.reset(1);
        }
        JsonWriteContext ctxt2 = new JsonWriteContext(1, this);
        this._child = ctxt2;
        return ctxt2;
    }

    public final JsonWriteContext createChildObjectContext() {
        JsonWriteContext ctxt = this._child;
        if (ctxt != null) {
            return ctxt.reset(2);
        }
        JsonWriteContext ctxt2 = new JsonWriteContext(2, this);
        this._child = ctxt2;
        return ctxt2;
    }

    public final JsonWriteContext getParent() {
        return this._parent;
    }

    public final int writeFieldName(String name) {
        if (this._type != 2 || this._currentName != null) {
            return 4;
        }
        this._currentName = name;
        return this._index < 0 ? 0 : 1;
    }

    public final int writeValue() {
        if (this._type == 2) {
            if (this._currentName == null) {
                return 5;
            }
            this._currentName = null;
            this._index++;
            return 2;
        }
        if (this._type == 1) {
            int ix = this._index;
            this._index++;
            return ix < 0 ? 0 : 1;
        }
        this._index++;
        return this._index == 0 ? 0 : 3;
    }

    public final String toString() {
        StringBuilder sb = new StringBuilder(64);
        if (this._type == 2) {
            sb.append('{');
            if (this._currentName != null) {
                sb.append('\"');
                sb.append(this._currentName);
                sb.append('\"');
            } else {
                sb.append('?');
            }
            sb.append('}');
        } else if (this._type == 1) {
            sb.append('[');
            sb.append(getCurrentIndex());
            sb.append(']');
        } else {
            sb.append("/");
        }
        return sb.toString();
    }
}
