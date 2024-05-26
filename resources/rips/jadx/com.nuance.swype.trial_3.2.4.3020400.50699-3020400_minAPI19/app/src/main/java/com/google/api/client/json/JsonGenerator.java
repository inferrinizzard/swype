package com.google.api.client.json;

import com.google.api.client.repackaged.com.google.common.base.Preconditions;
import com.google.api.client.util.ClassInfo;
import com.google.api.client.util.Data;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.FieldInfo;
import com.google.api.client.util.GenericData;
import com.google.api.client.util.Types;
import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

/* loaded from: classes.dex */
public abstract class JsonGenerator {
    public abstract void close() throws IOException;

    public abstract void flush() throws IOException;

    public abstract JsonFactory getFactory();

    public abstract void writeBoolean(boolean z) throws IOException;

    public abstract void writeEndArray() throws IOException;

    public abstract void writeEndObject() throws IOException;

    public abstract void writeFieldName(String str) throws IOException;

    public abstract void writeNull() throws IOException;

    public abstract void writeNumber(double d) throws IOException;

    public abstract void writeNumber(float f) throws IOException;

    public abstract void writeNumber(int i) throws IOException;

    public abstract void writeNumber(long j) throws IOException;

    public abstract void writeNumber(String str) throws IOException;

    public abstract void writeNumber(BigDecimal bigDecimal) throws IOException;

    public abstract void writeNumber(BigInteger bigInteger) throws IOException;

    public abstract void writeStartArray() throws IOException;

    public abstract void writeStartObject() throws IOException;

    public abstract void writeString(String str) throws IOException;

    public final void serialize(Object value) throws IOException {
        serialize(false, value);
    }

    private void serialize(boolean isJsonString, Object value) throws IOException {
        boolean isJsonStringForField;
        if (value != null) {
            Class<?> valueClass = value.getClass();
            if (Data.isNull(value)) {
                writeNull();
                return;
            }
            if (value instanceof String) {
                writeString((String) value);
                return;
            }
            if (value instanceof Number) {
                if (isJsonString) {
                    writeString(value.toString());
                    return;
                }
                if (value instanceof BigDecimal) {
                    writeNumber((BigDecimal) value);
                    return;
                }
                if (value instanceof BigInteger) {
                    writeNumber((BigInteger) value);
                    return;
                }
                if (value instanceof Long) {
                    writeNumber(((Long) value).longValue());
                    return;
                }
                if (value instanceof Float) {
                    float floatValue = ((Number) value).floatValue();
                    Preconditions.checkArgument((Float.isInfinite(floatValue) || Float.isNaN(floatValue)) ? false : true);
                    writeNumber(floatValue);
                    return;
                } else {
                    if ((value instanceof Integer) || (value instanceof Short) || (value instanceof Byte)) {
                        writeNumber(((Number) value).intValue());
                        return;
                    }
                    double doubleValue = ((Number) value).doubleValue();
                    Preconditions.checkArgument((Double.isInfinite(doubleValue) || Double.isNaN(doubleValue)) ? false : true);
                    writeNumber(doubleValue);
                    return;
                }
            }
            if (value instanceof Boolean) {
                writeBoolean(((Boolean) value).booleanValue());
                return;
            }
            if (value instanceof DateTime) {
                writeString(((DateTime) value).toStringRfc3339());
                return;
            }
            if ((value instanceof Iterable) || valueClass.isArray()) {
                writeStartArray();
                for (Object o : Types.iterableOf(value)) {
                    serialize(isJsonString, o);
                }
                writeEndArray();
                return;
            }
            if (!valueClass.isEnum()) {
                writeStartObject();
                boolean isMapNotGenericData = (value instanceof Map) && !(value instanceof GenericData);
                ClassInfo classInfo = isMapNotGenericData ? null : ClassInfo.of(valueClass);
                for (Map.Entry<String, Object> entry : Data.mapOf(value).entrySet()) {
                    Object fieldValue = entry.getValue();
                    if (fieldValue != null) {
                        String fieldName = entry.getKey();
                        if (isMapNotGenericData) {
                            isJsonStringForField = isJsonString;
                        } else {
                            FieldInfo fieldInfo = classInfo.getFieldInfo(fieldName);
                            Field field = fieldInfo == null ? null : fieldInfo.field;
                            isJsonStringForField = (field == null || field.getAnnotation(JsonString.class) == null) ? false : true;
                        }
                        writeFieldName(fieldName);
                        serialize(isJsonStringForField, fieldValue);
                    }
                }
                writeEndObject();
                return;
            }
            String name = FieldInfo.of((Enum<?>) value).name;
            if (name == null) {
                writeNull();
            } else {
                writeString(name);
            }
        }
    }

    public void enablePrettyPrint() throws IOException {
    }
}
