.class public final Lcom/nuance/swypeconnect/ac/ACDLMConnector$ACAlphaDlmEventCallback;
.super Lcom/nuance/swypeconnect/ac/ACDLMConnector$ACAbstractDlmEventCallback;

# interfaces
.implements Lcom/nuance/swypeconnect/ac/ACDLMConnector$ACDlmEventCallback;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/nuance/swypeconnect/ac/ACDLMConnector;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x11
    name = "ACAlphaDlmEventCallback"
.end annotation


# static fields
.field protected static final CORE:I = 0x1


# instance fields
.field final synthetic this$0:Lcom/nuance/swypeconnect/ac/ACDLMConnector;


# direct methods
.method constructor <init>(Lcom/nuance/swypeconnect/ac/ACDLMConnector;)V
    .locals 1

    iput-object p1, p0, Lcom/nuance/swypeconnect/ac/ACDLMConnector$ACAlphaDlmEventCallback;->this$0:Lcom/nuance/swypeconnect/ac/ACDLMConnector;

    const/4 v0, 0x0

    invoke-direct {p0, p1, v0}, Lcom/nuance/swypeconnect/ac/ACDLMConnector$ACAbstractDlmEventCallback;-><init>(Lcom/nuance/swypeconnect/ac/ACDLMConnector;Lcom/nuance/swypeconnect/ac/ACDLMConnector$1;)V

    return-void
.end method


# virtual methods
.method public final onAlphaDlmEvent([BZ)V
    .locals 7

    iget-object v0, p0, Lcom/nuance/swypeconnect/ac/ACDLMConnector$ACAlphaDlmEventCallback;->this$0:Lcom/nuance/swypeconnect/ac/ACDLMConnector;

    invoke-static {v0}, Lcom/nuance/swypeconnect/ac/ACDLMConnector;->access$100(Lcom/nuance/swypeconnect/ac/ACDLMConnector;)Lcom/nuance/swypeconnect/ac/ACManager;

    move-result-object v0

    invoke-virtual {v0}, Lcom/nuance/swypeconnect/ac/ACManager;->getConnect()Lcom/nuance/connect/api/ConnectServiceManager;

    move-result-object v0

    invoke-interface {v0}, Lcom/nuance/connect/api/ConnectServiceManager;->isLicensed()Z

    move-result v0

    if-eqz v0, :cond_1

    const/4 v0, 0x2

    invoke-static {p1, v0}, Landroid/util/Base64;->encodeToString([BI)Ljava/lang/String;

    move-result-object v3

    iget-object v0, p0, Lcom/nuance/swypeconnect/ac/ACDLMConnector$ACAlphaDlmEventCallback;->this$0:Lcom/nuance/swypeconnect/ac/ACDLMConnector;

    invoke-static {v0}, Lcom/nuance/swypeconnect/ac/ACDLMConnector;->access$200(Lcom/nuance/swypeconnect/ac/ACDLMConnector;)Lcom/nuance/connect/api/DLMConnector;

    move-result-object v1

    const/4 v2, 0x1

    invoke-static {}, Ljava/lang/System;->currentTimeMillis()J

    move-result-wide v4

    move v6, p2

    invoke-interface/range {v1 .. v6}, Lcom/nuance/connect/api/DLMConnector;->onDlmEvent(ILjava/lang/String;JZ)V

    iget-object v0, p0, Lcom/nuance/swypeconnect/ac/ACDLMConnector$ACAlphaDlmEventCallback;->this$0:Lcom/nuance/swypeconnect/ac/ACDLMConnector;

    invoke-static {v0}, Lcom/nuance/swypeconnect/ac/ACDLMConnector;->access$100(Lcom/nuance/swypeconnect/ac/ACDLMConnector;)Lcom/nuance/swypeconnect/ac/ACManager;

    move-result-object v0

    invoke-virtual {v0}, Lcom/nuance/swypeconnect/ac/ACManager;->getReportingService()Lcom/nuance/swypeconnect/ac/ACReportingService;

    move-result-object v3

    if-eqz v3, :cond_1

    invoke-virtual {v3}, Lcom/nuance/swypeconnect/ac/ACReportingService;->isEnabled()Z

    move-result v0

    if-eqz v0, :cond_1

    invoke-static {}, Lcom/nuance/swypeconnect/ac/ACDLMConnector;->access$300()Lcom/nuance/swypeconnect/ac/ACDLMEventInternal$Compiler;

    move-result-object v0

    invoke-virtual {v0, p1}, Lcom/nuance/swypeconnect/ac/ACDLMEventInternal$Compiler;->compileAll([B)[Lcom/nuance/swypeconnect/ac/ACDLMEventData$ACDLMEvent;

    move-result-object v4

    if-eqz v4, :cond_1

    array-length v5, v4

    const/4 v0, 0x0

    move v2, v0

    :goto_0
    if-ge v2, v5, :cond_1

    aget-object v1, v4, v2

    invoke-interface {v1}, Lcom/nuance/swypeconnect/ac/ACDLMEventData$ACDLMEvent;->getType()I

    move-result v0

    packed-switch v0, :pswitch_data_0

    :cond_0
    :goto_1
    add-int/lit8 v0, v2, 0x1

    move v2, v0

    goto :goto_0

    :pswitch_0
    instance-of v0, v1, Lcom/nuance/swypeconnect/ac/ACDLMEventInternal$AddWordDlmEventImpl;

    if-eqz v0, :cond_0

    move-object v0, v1

    check-cast v0, Lcom/nuance/swypeconnect/ac/ACDLMEventInternal$AddWordDlmEventImpl;

    iget-boolean v0, v0, Lcom/nuance/swypeconnect/ac/ACDLMEventInternal$AddWordDlmEventImpl;->highPriority:Z

    if-eqz v0, :cond_0

    invoke-virtual {v3}, Lcom/nuance/swypeconnect/ac/ACReportingService;->getHelper()Lcom/nuance/swypeconnect/ac/ACReportingLogHelper;

    move-result-object v0

    check-cast v1, Lcom/nuance/swypeconnect/ac/ACDLMEventData$ACAddWordDLMEvent;

    invoke-interface {v1}, Lcom/nuance/swypeconnect/ac/ACDLMEventData$ACAddWordDLMEvent;->getWord()Ljava/lang/String;

    move-result-object v1

    invoke-virtual {v0, v1}, Lcom/nuance/swypeconnect/ac/ACReportingLogHelper;->recordUdbAdd(Ljava/lang/String;)V

    goto :goto_1

    :pswitch_1
    invoke-virtual {v3}, Lcom/nuance/swypeconnect/ac/ACReportingService;->getHelper()Lcom/nuance/swypeconnect/ac/ACReportingLogHelper;

    move-result-object v0

    check-cast v1, Lcom/nuance/swypeconnect/ac/ACDLMEventData$ACDeleteWordDLMEvent;

    invoke-interface {v1}, Lcom/nuance/swypeconnect/ac/ACDLMEventData$ACDeleteWordDLMEvent;->getWord()Ljava/lang/String;

    move-result-object v1

    invoke-virtual {v0, v1}, Lcom/nuance/swypeconnect/ac/ACReportingLogHelper;->recordUdbDelete(Ljava/lang/String;)V

    goto :goto_1

    :cond_1
    return-void

    :pswitch_data_0
    .packed-switch 0x15
        :pswitch_0
        :pswitch_1
    .end packed-switch
.end method

.method public final onReset(Z)V
    .locals 3

    iget-object v0, p0, Lcom/nuance/swypeconnect/ac/ACDLMConnector$ACAlphaDlmEventCallback;->this$0:Lcom/nuance/swypeconnect/ac/ACDLMConnector;

    invoke-static {v0}, Lcom/nuance/swypeconnect/ac/ACDLMConnector;->access$400(Lcom/nuance/swypeconnect/ac/ACDLMConnector;)Lcom/nuance/connect/util/Logger$Log;

    move-result-object v0

    const-string/jumbo v1, "ACAlphaDlmEventCallback.onReset userCategory="

    invoke-static {p1}, Ljava/lang/Boolean;->valueOf(Z)Ljava/lang/Boolean;

    move-result-object v2

    invoke-interface {v0, v1, v2}, Lcom/nuance/connect/util/Logger$Log;->v(Ljava/lang/Object;Ljava/lang/Object;)V

    iget-object v0, p0, Lcom/nuance/swypeconnect/ac/ACDLMConnector$ACAlphaDlmEventCallback;->this$0:Lcom/nuance/swypeconnect/ac/ACDLMConnector;

    invoke-static {v0}, Lcom/nuance/swypeconnect/ac/ACDLMConnector;->access$100(Lcom/nuance/swypeconnect/ac/ACDLMConnector;)Lcom/nuance/swypeconnect/ac/ACManager;

    move-result-object v0

    invoke-virtual {v0}, Lcom/nuance/swypeconnect/ac/ACManager;->getConnect()Lcom/nuance/connect/api/ConnectServiceManager;

    move-result-object v0

    invoke-interface {v0}, Lcom/nuance/connect/api/ConnectServiceManager;->isLicensed()Z

    move-result v0

    if-eqz v0, :cond_0

    iget-object v0, p0, Lcom/nuance/swypeconnect/ac/ACDLMConnector$ACAlphaDlmEventCallback;->this$0:Lcom/nuance/swypeconnect/ac/ACDLMConnector;

    invoke-static {v0}, Lcom/nuance/swypeconnect/ac/ACDLMConnector;->access$200(Lcom/nuance/swypeconnect/ac/ACDLMConnector;)Lcom/nuance/connect/api/DLMConnector;

    move-result-object v0

    const/4 v1, 0x1

    invoke-interface {v0, v1, p1}, Lcom/nuance/connect/api/DLMConnector;->onReset(IZ)V

    :cond_0
    return-void
.end method

.method public final bridge synthetic registerObserver(Lcom/nuance/swypeconnect/ac/ACDLMConnector$ACDlmEventCallbackObserver;)V
    .locals 0

    invoke-super {p0, p1}, Lcom/nuance/swypeconnect/ac/ACDLMConnector$ACAbstractDlmEventCallback;->registerObserver(Lcom/nuance/swypeconnect/ac/ACDLMConnector$ACDlmEventCallbackObserver;)V

    return-void
.end method

.method public final resume()V
    .locals 4

    const/4 v1, 0x0

    iget-object v0, p0, Lcom/nuance/swypeconnect/ac/ACDLMConnector$ACAlphaDlmEventCallback;->this$0:Lcom/nuance/swypeconnect/ac/ACDLMConnector;

    invoke-static {v0}, Lcom/nuance/swypeconnect/ac/ACDLMConnector;->access$400(Lcom/nuance/swypeconnect/ac/ACDLMConnector;)Lcom/nuance/connect/util/Logger$Log;

    move-result-object v0

    const-string/jumbo v2, "ACAlphaDlmEventCallback.resume"

    invoke-interface {v0, v2}, Lcom/nuance/connect/util/Logger$Log;->v(Ljava/lang/Object;)V

    iget-object v0, p0, Lcom/nuance/swypeconnect/ac/ACDLMConnector$ACAlphaDlmEventCallback;->this$0:Lcom/nuance/swypeconnect/ac/ACDLMConnector;

    invoke-static {v0}, Lcom/nuance/swypeconnect/ac/ACDLMConnector;->access$100(Lcom/nuance/swypeconnect/ac/ACDLMConnector;)Lcom/nuance/swypeconnect/ac/ACManager;

    move-result-object v0

    invoke-virtual {v0}, Lcom/nuance/swypeconnect/ac/ACManager;->getConnect()Lcom/nuance/connect/api/ConnectServiceManager;

    move-result-object v0

    invoke-interface {v0}, Lcom/nuance/connect/api/ConnectServiceManager;->isLicensed()Z

    move-result v0

    if-eqz v0, :cond_0

    iget-object v0, p0, Lcom/nuance/swypeconnect/ac/ACDLMConnector$ACAlphaDlmEventCallback;->this$0:Lcom/nuance/swypeconnect/ac/ACDLMConnector;

    invoke-static {v0}, Lcom/nuance/swypeconnect/ac/ACDLMConnector;->access$200(Lcom/nuance/swypeconnect/ac/ACDLMConnector;)Lcom/nuance/connect/api/DLMConnector;

    move-result-object v0

    const/4 v2, 0x1

    invoke-interface {v0, v2}, Lcom/nuance/connect/api/DLMConnector;->resume(I)V

    iget-object v0, p0, Lcom/nuance/swypeconnect/ac/ACDLMConnector$ACAlphaDlmEventCallback;->observers:Lcom/nuance/connect/util/ConcurrentCallbackSet;

    new-array v2, v1, [Lcom/nuance/swypeconnect/ac/ACDLMConnector$ACDlmEventCallbackObserver;

    invoke-virtual {v0, v2}, Lcom/nuance/connect/util/ConcurrentCallbackSet;->toArray([Ljava/lang/Object;)[Ljava/lang/Object;

    move-result-object v0

    check-cast v0, [Lcom/nuance/swypeconnect/ac/ACDLMConnector$ACDlmEventCallbackObserver;

    array-length v2, v0

    :goto_0
    if-ge v1, v2, :cond_0

    aget-object v3, v0, v1

    invoke-interface {v3}, Lcom/nuance/swypeconnect/ac/ACDLMConnector$ACDlmEventCallbackObserver;->onResume()V

    add-int/lit8 v1, v1, 0x1

    goto :goto_0

    :cond_0
    return-void
.end method

.method public final bridge synthetic unregisterObserver(Lcom/nuance/swypeconnect/ac/ACDLMConnector$ACDlmEventCallbackObserver;)V
    .locals 0

    invoke-super {p0, p1}, Lcom/nuance/swypeconnect/ac/ACDLMConnector$ACAbstractDlmEventCallback;->unregisterObserver(Lcom/nuance/swypeconnect/ac/ACDLMConnector$ACDlmEventCallbackObserver;)V

    return-void
.end method

.method public final bridge synthetic unregisterObservers()V
    .locals 0

    invoke-super {p0}, Lcom/nuance/swypeconnect/ac/ACDLMConnector$ACAbstractDlmEventCallback;->unregisterObservers()V

    return-void
.end method

.method public final yield()V
    .locals 4

    const/4 v1, 0x0

    iget-object v0, p0, Lcom/nuance/swypeconnect/ac/ACDLMConnector$ACAlphaDlmEventCallback;->this$0:Lcom/nuance/swypeconnect/ac/ACDLMConnector;

    invoke-static {v0}, Lcom/nuance/swypeconnect/ac/ACDLMConnector;->access$400(Lcom/nuance/swypeconnect/ac/ACDLMConnector;)Lcom/nuance/connect/util/Logger$Log;

    move-result-object v0

    const-string/jumbo v2, "ACAlphaDlmEventCallback.yield"

    invoke-interface {v0, v2}, Lcom/nuance/connect/util/Logger$Log;->v(Ljava/lang/Object;)V

    iget-object v0, p0, Lcom/nuance/swypeconnect/ac/ACDLMConnector$ACAlphaDlmEventCallback;->this$0:Lcom/nuance/swypeconnect/ac/ACDLMConnector;

    invoke-static {v0}, Lcom/nuance/swypeconnect/ac/ACDLMConnector;->access$100(Lcom/nuance/swypeconnect/ac/ACDLMConnector;)Lcom/nuance/swypeconnect/ac/ACManager;

    move-result-object v0

    invoke-virtual {v0}, Lcom/nuance/swypeconnect/ac/ACManager;->getConnect()Lcom/nuance/connect/api/ConnectServiceManager;

    move-result-object v0

    invoke-interface {v0}, Lcom/nuance/connect/api/ConnectServiceManager;->isLicensed()Z

    move-result v0

    if-eqz v0, :cond_0

    iget-object v0, p0, Lcom/nuance/swypeconnect/ac/ACDLMConnector$ACAlphaDlmEventCallback;->this$0:Lcom/nuance/swypeconnect/ac/ACDLMConnector;

    invoke-static {v0}, Lcom/nuance/swypeconnect/ac/ACDLMConnector;->access$200(Lcom/nuance/swypeconnect/ac/ACDLMConnector;)Lcom/nuance/connect/api/DLMConnector;

    move-result-object v0

    const/4 v2, 0x1

    invoke-interface {v0, v2}, Lcom/nuance/connect/api/DLMConnector;->yield(I)V

    iget-object v0, p0, Lcom/nuance/swypeconnect/ac/ACDLMConnector$ACAlphaDlmEventCallback;->observers:Lcom/nuance/connect/util/ConcurrentCallbackSet;

    new-array v2, v1, [Lcom/nuance/swypeconnect/ac/ACDLMConnector$ACDlmEventCallbackObserver;

    invoke-virtual {v0, v2}, Lcom/nuance/connect/util/ConcurrentCallbackSet;->toArray([Ljava/lang/Object;)[Ljava/lang/Object;

    move-result-object v0

    check-cast v0, [Lcom/nuance/swypeconnect/ac/ACDLMConnector$ACDlmEventCallbackObserver;

    array-length v2, v0

    :goto_0
    if-ge v1, v2, :cond_0

    aget-object v3, v0, v1

    invoke-interface {v3}, Lcom/nuance/swypeconnect/ac/ACDLMConnector$ACDlmEventCallbackObserver;->onYield()V

    add-int/lit8 v1, v1, 0x1

    goto :goto_0

    :cond_0
    return-void
.end method
