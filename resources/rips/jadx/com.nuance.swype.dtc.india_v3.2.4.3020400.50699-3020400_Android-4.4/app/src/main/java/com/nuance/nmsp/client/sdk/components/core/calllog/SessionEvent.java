package com.nuance.nmsp.client.sdk.components.core.calllog;

/* loaded from: classes.dex */
public interface SessionEvent {
    public static final String NMSP_CALLLOG_APP_REMOTEEVENT = "APP_REMOTEEVENT";
    public static final String NMSP_CALLLOG_CLOSESOCKET_EVENT = "CloseSocket";
    public static final String NMSP_CALLLOG_COMMANDABORT_EVENT = "CommandAbort";
    public static final String NMSP_CALLLOG_COMMANDEXPIRED_EVENT = "CommandExpired";
    public static final String NMSP_CALLLOG_CONNECTED_EVENT = "ConnectionEstablished";
    public static final String NMSP_CALLLOG_CONNECT_EVENT = "Connect";
    public static final String NMSP_CALLLOG_NMASAUDIOPARAM_EVENT = "NMASAudioParameter";
    public static final String NMSP_CALLLOG_NMASCOMMAND_EVENT = "NMASCommand";
    public static final String NMSP_CALLLOG_NMASRESPONSE_EVENT = "NMASResponse";
    public static final String NMSP_CALLLOG_NMSPGW_EVENT = "NMSP_GATEWAY";
    public static final String NMSP_CALLLOG_NMSPSDK_EVENT = "CLIENT";
    public static final String NMSP_CALLLOG_NMSPSESSION = "NMSPSession";
    public static final String NMSP_CALLLOG_NMSPSOCKET_EVENT = "NMSPSocket";
    public static final String NMSP_CALLLOG_OPENSOCKET_EVENT = "OpenSocket";
    public static final String NMSP_CALLLOG_PLAYERSTART_EVENT = "PlayerStart";
    public static final String NMSP_CALLLOG_PLAYERSTOP_EVENT = "PlayerStop";
    public static final String NMSP_CALLLOG_PLAYER_EVENT = "NMSPPlayer";
    public static final String NMSP_CALLLOG_PLAYSTREAMSTART_EVENT = "StreamStart";
    public static final String NMSP_CALLLOG_PLAYSTREAMSTOP_EVENT = "StreamStop";
    public static final String NMSP_CALLLOG_RECORDERSTART_EVENT = "RecorderStart";
    public static final String NMSP_CALLLOG_RECORDERSTOP_EVENT = "RecorderStop";
    public static final String NMSP_CALLLOG_RECORDER_EVENT = "NMSPRecorder";
    public static final String NMSP_CALLLOG_RECORDSTREAMSTART_EVENT = "StreamStart";
    public static final String NMSP_CALLLOG_RECORDSTREAMSTOP_EVENT = "StreamStop";
    public static final String NMSP_CALLLOG_SHUTDOWN_EVENT = "NMSP_ShutDown";
    public static final String NMSP_CALLLOG_SOCKETCLOSED_EVENT = "SocketClosed";
    public static final String NMSP_CALLLOG_SOCKETOPENED_EVENT = "SocketOpened";

    /* loaded from: classes.dex */
    public interface SessionEventCommittedListener {
        void committed(SessionEvent sessionEvent);
    }

    SessionEventBuilder createChildEventBuilder(String str);

    SessionEvent createRemoteChildEvent(String str, SessionEventCommittedListener sessionEventCommittedListener);

    String getId();
}
