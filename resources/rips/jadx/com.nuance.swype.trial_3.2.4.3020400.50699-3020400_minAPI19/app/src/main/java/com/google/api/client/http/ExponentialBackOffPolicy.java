package com.google.api.client.http;

import com.google.api.client.repackaged.com.google.common.base.Preconditions;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.client.util.NanoClock;
import com.nuance.connect.comm.MessageAPI;
import java.io.IOException;

@Deprecated
/* loaded from: classes.dex */
public class ExponentialBackOffPolicy implements BackOffPolicy {
    public static final int DEFAULT_INITIAL_INTERVAL_MILLIS = 500;
    public static final int DEFAULT_MAX_ELAPSED_TIME_MILLIS = 900000;
    public static final int DEFAULT_MAX_INTERVAL_MILLIS = 60000;
    public static final double DEFAULT_MULTIPLIER = 1.5d;
    public static final double DEFAULT_RANDOMIZATION_FACTOR = 0.5d;
    private final ExponentialBackOff exponentialBackOff;

    public ExponentialBackOffPolicy() {
        this(new Builder());
    }

    protected ExponentialBackOffPolicy(Builder builder) {
        this.exponentialBackOff = builder.exponentialBackOffBuilder.build();
    }

    @Override // com.google.api.client.http.BackOffPolicy
    public boolean isBackOffRequired(int statusCode) {
        switch (statusCode) {
            case 500:
            case 503:
                return true;
            case MessageAPI.INSTALL_FAILED /* 501 */:
            case 502:
            default:
                return false;
        }
    }

    @Override // com.google.api.client.http.BackOffPolicy
    public final void reset() {
        this.exponentialBackOff.reset();
    }

    @Override // com.google.api.client.http.BackOffPolicy
    public long getNextBackOffMillis() throws IOException {
        return this.exponentialBackOff.nextBackOffMillis();
    }

    public final int getInitialIntervalMillis() {
        return this.exponentialBackOff.initialIntervalMillis;
    }

    public final double getRandomizationFactor() {
        return this.exponentialBackOff.randomizationFactor;
    }

    public final int getCurrentIntervalMillis() {
        return this.exponentialBackOff.currentIntervalMillis;
    }

    public final double getMultiplier() {
        return this.exponentialBackOff.multiplier;
    }

    public final int getMaxIntervalMillis() {
        return this.exponentialBackOff.maxIntervalMillis;
    }

    public final int getMaxElapsedTimeMillis() {
        return this.exponentialBackOff.maxElapsedTimeMillis;
    }

    public final long getElapsedTimeMillis() {
        return this.exponentialBackOff.getElapsedTimeMillis();
    }

    public static Builder builder() {
        return new Builder();
    }

    @Deprecated
    /* loaded from: classes.dex */
    public static class Builder {
        final ExponentialBackOff.Builder exponentialBackOffBuilder = new ExponentialBackOff.Builder();

        protected Builder() {
        }

        public ExponentialBackOffPolicy build() {
            return new ExponentialBackOffPolicy(this);
        }

        public final int getInitialIntervalMillis() {
            return this.exponentialBackOffBuilder.initialIntervalMillis;
        }

        public Builder setInitialIntervalMillis(int initialIntervalMillis) {
            this.exponentialBackOffBuilder.initialIntervalMillis = initialIntervalMillis;
            return this;
        }

        public final double getRandomizationFactor() {
            return this.exponentialBackOffBuilder.randomizationFactor;
        }

        public Builder setRandomizationFactor(double randomizationFactor) {
            this.exponentialBackOffBuilder.randomizationFactor = randomizationFactor;
            return this;
        }

        public final double getMultiplier() {
            return this.exponentialBackOffBuilder.multiplier;
        }

        public Builder setMultiplier(double multiplier) {
            this.exponentialBackOffBuilder.multiplier = multiplier;
            return this;
        }

        public final int getMaxIntervalMillis() {
            return this.exponentialBackOffBuilder.maxIntervalMillis;
        }

        public Builder setMaxIntervalMillis(int maxIntervalMillis) {
            this.exponentialBackOffBuilder.maxIntervalMillis = maxIntervalMillis;
            return this;
        }

        public final int getMaxElapsedTimeMillis() {
            return this.exponentialBackOffBuilder.maxElapsedTimeMillis;
        }

        public Builder setMaxElapsedTimeMillis(int maxElapsedTimeMillis) {
            this.exponentialBackOffBuilder.maxElapsedTimeMillis = maxElapsedTimeMillis;
            return this;
        }

        public final NanoClock getNanoClock() {
            return this.exponentialBackOffBuilder.nanoClock;
        }

        public Builder setNanoClock(NanoClock nanoClock) {
            this.exponentialBackOffBuilder.nanoClock = (NanoClock) Preconditions.checkNotNull(nanoClock);
            return this;
        }
    }
}
