package com.google.api.client.repackaged.com.google.common.base;

import com.google.api.client.repackaged.com.google.common.base.AbstractIterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
public final class Splitter {
    final int limit;
    final boolean omitEmptyStrings;
    private final Strategy strategy;
    final CharMatcher trimmer;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public interface Strategy {
        Iterator<String> iterator(Splitter splitter, CharSequence charSequence);
    }

    public Splitter(Strategy strategy) {
        this(strategy, CharMatcher.NONE);
    }

    private Splitter(Strategy strategy, CharMatcher trimmer) {
        this.strategy = strategy;
        this.omitEmptyStrings = false;
        this.trimmer = trimmer;
        this.limit = Integer.MAX_VALUE;
    }

    public final List<String> splitToList(CharSequence sequence) {
        Preconditions.checkNotNull(sequence);
        Iterator<String> iterator = this.strategy.iterator(this, sequence);
        List<String> result = new ArrayList<>();
        while (iterator.hasNext()) {
            result.add(iterator.next());
        }
        return Collections.unmodifiableList(result);
    }

    /* loaded from: classes.dex */
    private static abstract class SplittingIterator extends AbstractIterator<String> {
        int limit;
        int offset = 0;
        final boolean omitEmptyStrings;
        final CharSequence toSplit;
        final CharMatcher trimmer;

        abstract int separatorEnd(int i);

        abstract int separatorStart(int i);

        protected SplittingIterator(Splitter splitter, CharSequence toSplit) {
            this.trimmer = splitter.trimmer;
            this.omitEmptyStrings = splitter.omitEmptyStrings;
            this.limit = splitter.limit;
            this.toSplit = toSplit;
        }

        @Override // com.google.api.client.repackaged.com.google.common.base.AbstractIterator
        protected final /* bridge */ /* synthetic */ String computeNext() {
            int i = this.offset;
            while (this.offset != -1) {
                int separatorStart = separatorStart(this.offset);
                if (separatorStart == -1) {
                    separatorStart = this.toSplit.length();
                    this.offset = -1;
                } else {
                    this.offset = separatorEnd(separatorStart);
                }
                if (this.offset == i) {
                    this.offset++;
                    if (this.offset >= this.toSplit.length()) {
                        this.offset = -1;
                    }
                } else {
                    int i2 = i;
                    while (i2 < separatorStart && this.trimmer.matches(this.toSplit.charAt(i2))) {
                        i2++;
                    }
                    int i3 = separatorStart;
                    while (i3 > i2 && this.trimmer.matches(this.toSplit.charAt(i3 - 1))) {
                        i3--;
                    }
                    if (this.omitEmptyStrings && i2 == i3) {
                        i = this.offset;
                    } else {
                        if (this.limit == 1) {
                            i3 = this.toSplit.length();
                            this.offset = -1;
                            while (i3 > i2 && this.trimmer.matches(this.toSplit.charAt(i3 - 1))) {
                                i3--;
                            }
                        } else {
                            this.limit--;
                        }
                        return this.toSplit.subSequence(i2, i3).toString();
                    }
                }
            }
            this.state = AbstractIterator.State.DONE;
            return null;
        }
    }
}
