.class Lcom/nuance/connect/comm/CommandQueue$NetworkExpirer;
.super Ljava/lang/Object;

# interfaces
.implements Ljava/lang/Runnable;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/nuance/connect/comm/CommandQueue;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = "NetworkExpirer"
.end annotation


# instance fields
.field private final delayTimeoutMillis:I

.field private dispose:Ljava/lang/Runnable;

.field private expired:Z

.field final synthetic this$0:Lcom/nuance/connect/comm/CommandQueue;


# direct methods
.method public constructor <init>(Lcom/nuance/connect/comm/CommandQueue;)V
    .locals 1

    iput-object p1, p0, Lcom/nuance/connect/comm/CommandQueue$NetworkExpirer;->this$0:Lcom/nuance/connect/comm/CommandQueue;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    invoke-static {p1}, Lcom/nuance/connect/comm/CommandQueue;->access$1000(Lcom/nuance/connect/comm/CommandQueue;)I

    move-result v0

    mul-int/lit16 v0, v0, 0x3e8

    iput v0, p0, Lcom/nuance/connect/comm/CommandQueue$NetworkExpirer;->delayTimeoutMillis:I

    return-void
.end method

.method public constructor <init>(Lcom/nuance/connect/comm/CommandQueue;I)V
    .locals 1

    iput-object p1, p0, Lcom/nuance/connect/comm/CommandQueue$NetworkExpirer;->this$0:Lcom/nuance/connect/comm/CommandQueue;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    mul-int/lit16 v0, p2, 0x3e8

    iput v0, p0, Lcom/nuance/connect/comm/CommandQueue$NetworkExpirer;->delayTimeoutMillis:I

    return-void
.end method

.method private sendTimeoutMessage()V
    .locals 4

    iget-object v0, p0, Lcom/nuance/connect/comm/CommandQueue$NetworkExpirer;->this$0:Lcom/nuance/connect/comm/CommandQueue;

    invoke-static {v0}, Lcom/nuance/connect/comm/CommandQueue;->access$1100(Lcom/nuance/connect/comm/CommandQueue;)Lcom/nuance/connect/comm/CommandQueue$MessageHandler;

    move-result-object v0

    invoke-virtual {v0, p0}, Lcom/nuance/connect/comm/CommandQueue$MessageHandler;->removeCallbacks(Ljava/lang/Runnable;)V

    iget v0, p0, Lcom/nuance/connect/comm/CommandQueue$NetworkExpirer;->delayTimeoutMillis:I

    if-lez v0, :cond_0

    iget-object v0, p0, Lcom/nuance/connect/comm/CommandQueue$NetworkExpirer;->this$0:Lcom/nuance/connect/comm/CommandQueue;

    invoke-static {v0}, Lcom/nuance/connect/comm/CommandQueue;->access$1100(Lcom/nuance/connect/comm/CommandQueue;)Lcom/nuance/connect/comm/CommandQueue$MessageHandler;

    move-result-object v0

    iget v1, p0, Lcom/nuance/connect/comm/CommandQueue$NetworkExpirer;->delayTimeoutMillis:I

    int-to-long v2, v1

    invoke-virtual {v0, p0, v2, v3}, Lcom/nuance/connect/comm/CommandQueue$MessageHandler;->postDelayed(Ljava/lang/Runnable;J)Z

    :cond_0
    return-void
.end method


# virtual methods
.method public complete()V
    .locals 1

    iget-object v0, p0, Lcom/nuance/connect/comm/CommandQueue$NetworkExpirer;->this$0:Lcom/nuance/connect/comm/CommandQueue;

    invoke-static {v0}, Lcom/nuance/connect/comm/CommandQueue;->access$1100(Lcom/nuance/connect/comm/CommandQueue;)Lcom/nuance/connect/comm/CommandQueue$MessageHandler;

    move-result-object v0

    invoke-virtual {v0, p0}, Lcom/nuance/connect/comm/CommandQueue$MessageHandler;->removeCallbacks(Ljava/lang/Runnable;)V

    return-void
.end method

.method public getInterval()I
    .locals 1

    const/16 v0, 0x1000

    return v0
.end method

.method public isExpired()Z
    .locals 1

    iget-boolean v0, p0, Lcom/nuance/connect/comm/CommandQueue$NetworkExpirer;->expired:Z

    return v0
.end method

.method public run()V
    .locals 1

    const/4 v0, 0x1

    iput-boolean v0, p0, Lcom/nuance/connect/comm/CommandQueue$NetworkExpirer;->expired:Z

    iget-object v0, p0, Lcom/nuance/connect/comm/CommandQueue$NetworkExpirer;->dispose:Ljava/lang/Runnable;

    if-eqz v0, :cond_0

    iget-object v0, p0, Lcom/nuance/connect/comm/CommandQueue$NetworkExpirer;->dispose:Ljava/lang/Runnable;

    invoke-interface {v0}, Ljava/lang/Runnable;->run()V

    :cond_0
    return-void
.end method

.method public setExpirer(Ljava/lang/Runnable;)V
    .locals 0

    iput-object p1, p0, Lcom/nuance/connect/comm/CommandQueue$NetworkExpirer;->dispose:Ljava/lang/Runnable;

    return-void
.end method

.method public start()V
    .locals 0

    invoke-direct {p0}, Lcom/nuance/connect/comm/CommandQueue$NetworkExpirer;->sendTimeoutMessage()V

    return-void
.end method

.method public tick()V
    .locals 0

    invoke-direct {p0}, Lcom/nuance/connect/comm/CommandQueue$NetworkExpirer;->sendTimeoutMessage()V

    return-void
.end method
