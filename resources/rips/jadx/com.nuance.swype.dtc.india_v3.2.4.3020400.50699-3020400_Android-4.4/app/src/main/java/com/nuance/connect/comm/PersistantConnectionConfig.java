package com.nuance.connect.comm;

/* loaded from: classes.dex */
public class PersistantConnectionConfig {
    private int compressionThreshold;
    private int timeout;
    private String url;

    protected PersistantConnectionConfig() {
    }

    public static PersistantConnectionConfig create(String str, int i, int i2) {
        PersistantConnectionConfig persistantConnectionConfig = new PersistantConnectionConfig();
        persistantConnectionConfig.url = str;
        persistantConnectionConfig.timeout = i;
        persistantConnectionConfig.compressionThreshold = i2;
        return persistantConnectionConfig;
    }

    public int getCompressionThreshold() {
        return this.compressionThreshold;
    }

    public int getTimeoutSeconds() {
        return this.timeout;
    }

    public String getURL() {
        return this.url;
    }
}
