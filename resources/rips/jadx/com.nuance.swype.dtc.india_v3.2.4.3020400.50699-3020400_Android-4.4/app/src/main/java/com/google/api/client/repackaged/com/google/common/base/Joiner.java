package com.google.api.client.repackaged.com.google.common.base;

import java.io.IOException;
import java.util.Iterator;

/* loaded from: classes.dex */
public final class Joiner {
    private final String separator;

    public Joiner(String separator) {
        this.separator = (String) Preconditions.checkNotNull(separator);
    }

    private static CharSequence toString(Object part) {
        Preconditions.checkNotNull(part);
        return part instanceof CharSequence ? (CharSequence) part : part.toString();
    }

    public final StringBuilder appendTo(StringBuilder builder, Iterator<?> parts) {
        try {
            Preconditions.checkNotNull(builder);
            if (parts.hasNext()) {
                builder.append(toString(parts.next()));
                while (parts.hasNext()) {
                    builder.append((CharSequence) this.separator);
                    builder.append(toString(parts.next()));
                }
            }
            return builder;
        } catch (IOException impossible) {
            throw new AssertionError(impossible);
        }
    }
}
