package com.nuance.connect.internal;

import java.util.List;

/* loaded from: classes.dex */
public interface Property<T> {
    public static final int FLAG_CONNECT_DAT_OVERRIDE_ALLOWED = 2;
    public static final int FLAG_OEM_OVERRIDE_ALLOWED = 4;
    public static final int FLAG_SERVER_OVERRIDE_ALLOWED = 1;
    public static final int VERIFY_NEG_ONE_INT = 5;
    public static final int VERIFY_NON_NEGATIVE_INT = 4;
    public static final int VERIFY_POSITIVE_INT = 3;
    public static final int VERIFY_TYPE = 1;
    public static final int VERIFY_UNKNOWN = 0;
    public static final int VERIFY_URL = 2;

    /* loaded from: classes.dex */
    public static abstract class BooleanValueListener implements ValueListener<Boolean> {
        @Override // com.nuance.connect.internal.Property.ValueListener
        public Class<Boolean> getTypeArgument() {
            return Boolean.class;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class IntegerValueListener implements ValueListener<Integer> {
        @Override // com.nuance.connect.internal.Property.ValueListener
        public Class<Integer> getTypeArgument() {
            return Integer.class;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class LongValueListener implements ValueListener<Long> {
        @Override // com.nuance.connect.internal.Property.ValueListener
        public Class<Long> getTypeArgument() {
            return Long.class;
        }
    }

    /* loaded from: classes.dex */
    public enum Source {
        DEFAULT,
        SERVER,
        CONNECT_DAT,
        OEM_RUNTIME,
        BUILD,
        USER;

        public static Source from(String str) {
            try {
                return valueOf(str);
            } catch (Exception e) {
                return DEFAULT;
            }
        }
    }

    /* loaded from: classes.dex */
    public static abstract class StringValueListener implements ValueListener<String> {
        @Override // com.nuance.connect.internal.Property.ValueListener
        public Class<String> getTypeArgument() {
            return String.class;
        }
    }

    /* loaded from: classes.dex */
    public interface ValueListener<T> {
        Class<T> getTypeArgument();

        void onValueChanged(Property<T> property);
    }

    /* loaded from: classes.dex */
    public interface Verifier<T> {
        boolean verify(Object obj, Source source, Property<T> property);
    }

    void addListener(ValueListener<T> valueListener);

    void addListener(ValueListener<T> valueListener, boolean z);

    void addListeners(List<ValueListener<T>> list);

    String getKey();

    int getOverrideFlags();

    Source getSource();

    Class<T> getTypeArgument();

    T getValue();

    int getVerification();

    void set(Object obj, Source source);

    void setOverrideFlags(int i);

    void setValue(T t, Source source);

    boolean verify(Object obj, Source source);
}
