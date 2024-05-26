package com.nuance.connect.comm;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class AnalyticsDataUsageScribeImpl implements AnalyticsDataUsageScribe {
    private static final String CONNECT_COMMAND_REQUEST = "C002";
    private static final String CONNECT_COMMAND_RESPONSE = "C003";
    private static final String CONNECT_DATAPOINT_DATA = "C001";
    private static final String CONNECT_DATAPOINT_DATA_CELLULAR_RX = "CELLULAR_RX";
    private static final String CONNECT_DATAPOINT_DATA_CELLULAR_TX = "CELLULAR_TX";
    private long lastRxBytes;
    private long lastTxBytes;
    private int trackNetworkUsageLength = -1;
    private long trackNetworkUsageStart = Long.MIN_VALUE;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AnalyticsDataUsageScribeImpl(MessageSendingBus messageSendingBus) {
    }

    private String commandToURL(Command command) {
        return command.commandFamily + "/" + command.version + "/" + command.command;
    }

    @Override // com.nuance.connect.comm.AnalyticsDataUsageScribe
    public void flush() {
    }

    @Override // com.nuance.connect.comm.AnalyticsDataUsageScribe
    public void mark() {
    }

    public void setTrackingInterval(int i) {
    }

    @Override // com.nuance.connect.comm.AnalyticsDataUsageScribe
    public void start() {
    }

    @Override // com.nuance.connect.comm.AnalyticsDataUsageScribe
    public void writeRequest(Command command, long j) {
    }

    @Override // com.nuance.connect.comm.AnalyticsDataUsageScribe
    public void writeResponse(Command command, long j) {
    }
}
