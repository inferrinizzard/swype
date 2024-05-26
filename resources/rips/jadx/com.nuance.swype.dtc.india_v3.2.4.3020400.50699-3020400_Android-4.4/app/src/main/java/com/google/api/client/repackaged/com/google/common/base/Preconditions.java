package com.google.api.client.repackaged.com.google.common.base;

/* loaded from: classes.dex */
public final class Preconditions {
    public static void checkArgument(boolean expression) {
        if (!expression) {
            throw new IllegalArgumentException();
        }
    }

    public static void checkState(boolean expression) {
        if (!expression) {
            throw new IllegalStateException();
        }
    }

    public static <T> T checkNotNull(T reference) {
        if (reference == null) {
            throw new NullPointerException();
        }
        return reference;
    }

    public static int checkPositionIndex(int index, int size) {
        String format;
        if (index >= 0 && index <= size) {
            return index;
        }
        if (index < 0) {
            format = format("%s (%s) must not be negative", "index", Integer.valueOf(index));
        } else {
            if (size < 0) {
                throw new IllegalArgumentException("negative size: " + size);
            }
            format = format("%s (%s) must not be greater than size (%s)", "index", Integer.valueOf(index), Integer.valueOf(size));
        }
        throw new IndexOutOfBoundsException(format);
    }

    public static String format(String template, Object... args) {
        int placeholderStart;
        String template2 = String.valueOf(template);
        StringBuilder builder = new StringBuilder(template2.length() + (args.length * 16));
        int templateStart = 0;
        int i = 0;
        while (i < args.length && (placeholderStart = template2.indexOf("%s", templateStart)) != -1) {
            builder.append(template2.substring(templateStart, placeholderStart));
            builder.append(args[i]);
            templateStart = placeholderStart + 2;
            i++;
        }
        builder.append(template2.substring(templateStart));
        if (i < args.length) {
            builder.append(" [");
            int i2 = i + 1;
            builder.append(args[i]);
            while (true) {
                int i3 = i2;
                if (i3 >= args.length) {
                    break;
                }
                builder.append(", ");
                i2 = i3 + 1;
                builder.append(args[i3]);
            }
            builder.append(']');
        }
        return builder.toString();
    }
}
