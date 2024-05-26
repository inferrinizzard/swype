package com.google.api.client.util;

/* loaded from: classes.dex */
public final class Objects {

    /* loaded from: classes.dex */
    public static final class ToStringHelper {
        private final String className;
        private ValueHolder holderHead = new ValueHolder(0);
        private ValueHolder holderTail = this.holderHead;
        private boolean omitNullValues;

        public ToStringHelper(String className) {
            this.className = className;
        }

        public final String toString() {
            boolean omitNullValuesSnapshot = this.omitNullValues;
            String nextSeparator = "";
            StringBuilder builder = new StringBuilder(32).append(this.className).append('{');
            for (ValueHolder valueHolder = this.holderHead.next; valueHolder != null; valueHolder = valueHolder.next) {
                if (!omitNullValuesSnapshot || valueHolder.value != null) {
                    builder.append(nextSeparator);
                    nextSeparator = ", ";
                    if (valueHolder.name != null) {
                        builder.append(valueHolder.name).append('=');
                    }
                    builder.append(valueHolder.value);
                }
            }
            return builder.append('}').toString();
        }

        /* loaded from: classes.dex */
        private static final class ValueHolder {
            String name;
            ValueHolder next;
            Object value;

            private ValueHolder() {
            }

            /* synthetic */ ValueHolder(byte b) {
                this();
            }
        }

        public final ToStringHelper add(String name, Object value) {
            ValueHolder valueHolder = new ValueHolder((byte) 0);
            this.holderTail.next = valueHolder;
            this.holderTail = valueHolder;
            valueHolder.value = value;
            valueHolder.name = (String) com.google.api.client.repackaged.com.google.common.base.Preconditions.checkNotNull(name);
            return this;
        }
    }

    public static boolean equal(Object a, Object b) {
        return a == b || (a != null && a.equals(b));
    }
}
