package com.google.api.client.json.rpc2;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.nuance.swype.input.BuildInfo;

/* loaded from: classes.dex */
public class JsonRpcRequest extends GenericData {

    @Key
    private Object id;

    @Key
    private final String jsonrpc = BuildInfo.DRAGON_SPEECH_VERSION;

    @Key
    private String method;

    @Key
    private Object params;

    public String getVersion() {
        return BuildInfo.DRAGON_SPEECH_VERSION;
    }

    public Object getId() {
        return this.id;
    }

    public void setId(Object id) {
        this.id = id;
    }

    public String getMethod() {
        return this.method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Object getParameters() {
        return this.params;
    }

    public void setParameters(Object parameters) {
        this.params = parameters;
    }

    @Override // com.google.api.client.util.GenericData
    public JsonRpcRequest set(String fieldName, Object value) {
        return (JsonRpcRequest) super.set(fieldName, value);
    }

    @Override // com.google.api.client.util.GenericData, java.util.AbstractMap
    public JsonRpcRequest clone() {
        return (JsonRpcRequest) super.clone();
    }
}
