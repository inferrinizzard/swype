package com.google.api.client.util;

import com.google.api.client.http.ExponentialBackOffPolicy;
import java.io.IOException;

/* loaded from: classes.dex */
public final class ExponentialBackOff implements BackOff {
    public int currentIntervalMillis;
    public final int initialIntervalMillis;
    public final int maxElapsedTimeMillis;
    public final int maxIntervalMillis;
    public final double multiplier;
    private final NanoClock nanoClock;
    public final double randomizationFactor;
    long startTimeNanos;

    public ExponentialBackOff() {
        this(new Builder());
    }

    protected ExponentialBackOff(Builder builder) {
        this.initialIntervalMillis = builder.initialIntervalMillis;
        this.randomizationFactor = builder.randomizationFactor;
        this.multiplier = builder.multiplier;
        this.maxIntervalMillis = builder.maxIntervalMillis;
        this.maxElapsedTimeMillis = builder.maxElapsedTimeMillis;
        this.nanoClock = builder.nanoClock;
        com.google.api.client.repackaged.com.google.common.base.Preconditions.checkArgument(this.initialIntervalMillis > 0);
        com.google.api.client.repackaged.com.google.common.base.Preconditions.checkArgument(0.0d <= this.randomizationFactor && this.randomizationFactor < 1.0d);
        com.google.api.client.repackaged.com.google.common.base.Preconditions.checkArgument(this.multiplier >= 1.0d);
        com.google.api.client.repackaged.com.google.common.base.Preconditions.checkArgument(this.maxIntervalMillis >= this.initialIntervalMillis);
        com.google.api.client.repackaged.com.google.common.base.Preconditions.checkArgument(this.maxElapsedTimeMillis > 0);
        reset();
    }

    public final void reset() {
        this.currentIntervalMillis = this.initialIntervalMillis;
        this.startTimeNanos = this.nanoClock.nanoTime();
    }

    @Override // com.google.api.client.util.BackOff
    public final long nextBackOffMillis() throws IOException {
        if (getElapsedTimeMillis() > this.maxElapsedTimeMillis) {
            return -1L;
        }
        double d = this.randomizationFactor;
        double random = Math.random();
        int i = this.currentIntervalMillis;
        double d2 = d * i;
        double d3 = i - d2;
        int randomizedInterval = (int) (((((d2 + i) - d3) + 1.0d) * random) + d3);
        if (this.currentIntervalMillis >= this.maxIntervalMillis / this.multiplier) {
            this.currentIntervalMillis = this.maxIntervalMillis;
        } else {
            this.currentIntervalMillis = (int) (this.currentIntervalMillis * this.multiplier);
        }
        return randomizedInterval;
    }

    public final long getElapsedTimeMillis() {
        return (this.nanoClock.nanoTime() - this.startTimeNanos) / 1000000;
    }

    /* loaded from: classes.dex */
    public static class Builder {
        public int initialIntervalMillis = 500;
        public double randomizationFactor = 0.5d;
        public double multiplier = 1.5d;
        public int maxIntervalMillis = ExponentialBackOffPolicy.DEFAULT_MAX_INTERVAL_MILLIS;
        public int maxElapsedTimeMillis = ExponentialBackOffPolicy.DEFAULT_MAX_ELAPSED_TIME_MILLIS;
        public NanoClock nanoClock = NanoClock.SYSTEM;

        public final ExponentialBackOff build() {
            return new ExponentialBackOff(this);
        }
    }
}
