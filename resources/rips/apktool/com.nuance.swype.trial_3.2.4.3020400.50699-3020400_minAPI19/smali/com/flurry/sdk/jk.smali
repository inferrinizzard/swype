.class public final Lcom/flurry/sdk/jk;
.super Landroid/content/BroadcastReceiver;
.source "SourceFile"


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Lcom/flurry/sdk/jk$a;
    }
.end annotation


# static fields
.field private static c:Lcom/flurry/sdk/jk;


# instance fields
.field a:Z

.field public b:Z

.field private d:Z


# direct methods
.method private constructor <init>()V
    .locals 3

    .prologue
    const/4 v0, 0x0

    .line 67
    invoke-direct {p0}, Landroid/content/BroadcastReceiver;-><init>()V

    .line 47
    iput-boolean v0, p0, Lcom/flurry/sdk/jk;->d:Z

    .line 68
    invoke-static {}, Lcom/flurry/sdk/jr;->a()Lcom/flurry/sdk/jr;

    move-result-object v1

    .line 1097
    iget-object v1, v1, Lcom/flurry/sdk/jr;->a:Landroid/content/Context;

    .line 69
    const-string/jumbo v2, "android.permission.ACCESS_NETWORK_STATE"

    invoke-virtual {v1, v2}, Landroid/content/Context;->checkCallingOrSelfPermission(Ljava/lang/String;)I

    move-result v2

    if-nez v2, :cond_0

    const/4 v0, 0x1

    :cond_0
    iput-boolean v0, p0, Lcom/flurry/sdk/jk;->d:Z

    .line 71
    invoke-direct {p0, v1}, Lcom/flurry/sdk/jk;->a(Landroid/content/Context;)Z

    move-result v0

    iput-boolean v0, p0, Lcom/flurry/sdk/jk;->b:Z

    .line 73
    iget-boolean v0, p0, Lcom/flurry/sdk/jk;->d:Z

    if-eqz v0, :cond_1

    .line 74
    invoke-direct {p0}, Lcom/flurry/sdk/jk;->d()V

    .line 76
    :cond_1
    return-void
.end method

.method public static declared-synchronized a()Lcom/flurry/sdk/jk;
    .locals 2

    .prologue
    .line 21
    const-class v1, Lcom/flurry/sdk/jk;

    monitor-enter v1

    :try_start_0
    sget-object v0, Lcom/flurry/sdk/jk;->c:Lcom/flurry/sdk/jk;

    if-nez v0, :cond_0

    .line 22
    new-instance v0, Lcom/flurry/sdk/jk;

    invoke-direct {v0}, Lcom/flurry/sdk/jk;-><init>()V

    sput-object v0, Lcom/flurry/sdk/jk;->c:Lcom/flurry/sdk/jk;

    .line 25
    :cond_0
    sget-object v0, Lcom/flurry/sdk/jk;->c:Lcom/flurry/sdk/jk;
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    monitor-exit v1

    return-object v0

    .line 21
    :catchall_0
    move-exception v0

    monitor-exit v1

    throw v0
.end method

.method private a(Landroid/content/Context;)Z
    .locals 2

    .prologue
    const/4 v0, 0x1

    .line 120
    iget-boolean v1, p0, Lcom/flurry/sdk/jk;->d:Z

    if-eqz v1, :cond_0

    if-nez p1, :cond_1

    .line 129
    :cond_0
    :goto_0
    return v0

    .line 127
    :cond_1
    invoke-static {}, Lcom/flurry/sdk/jk;->f()Landroid/net/ConnectivityManager;

    move-result-object v1

    .line 128
    invoke-virtual {v1}, Landroid/net/ConnectivityManager;->getActiveNetworkInfo()Landroid/net/NetworkInfo;

    move-result-object v1

    .line 129
    if-eqz v1, :cond_2

    invoke-virtual {v1}, Landroid/net/NetworkInfo;->isConnected()Z

    move-result v1

    if-nez v1, :cond_0

    :cond_2
    const/4 v0, 0x0

    goto :goto_0
.end method

.method private declared-synchronized d()V
    .locals 3

    .prologue
    .line 83
    monitor-enter p0

    :try_start_0
    iget-boolean v0, p0, Lcom/flurry/sdk/jk;->a:Z
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    if-eqz v0, :cond_0

    .line 92
    :goto_0
    monitor-exit p0

    return-void

    .line 87
    :cond_0
    :try_start_1
    invoke-static {}, Lcom/flurry/sdk/jr;->a()Lcom/flurry/sdk/jr;

    move-result-object v0

    .line 2097
    iget-object v0, v0, Lcom/flurry/sdk/jr;->a:Landroid/content/Context;

    .line 88
    invoke-direct {p0, v0}, Lcom/flurry/sdk/jk;->a(Landroid/content/Context;)Z

    move-result v1

    iput-boolean v1, p0, Lcom/flurry/sdk/jk;->b:Z

    .line 89
    new-instance v1, Landroid/content/IntentFilter;

    const-string/jumbo v2, "android.net.conn.CONNECTIVITY_CHANGE"

    invoke-direct {v1, v2}, Landroid/content/IntentFilter;-><init>(Ljava/lang/String;)V

    invoke-virtual {v0, p0, v1}, Landroid/content/Context;->registerReceiver(Landroid/content/BroadcastReceiver;Landroid/content/IntentFilter;)Landroid/content/Intent;

    .line 91
    const/4 v0, 0x1

    iput-boolean v0, p0, Lcom/flurry/sdk/jk;->a:Z
    :try_end_1
    .catchall {:try_start_1 .. :try_end_1} :catchall_0

    goto :goto_0

    .line 83
    :catchall_0
    move-exception v0

    monitor-exit p0

    throw v0
.end method

.method private static f()Landroid/net/ConnectivityManager;
    .locals 2

    .prologue
    .line 171
    invoke-static {}, Lcom/flurry/sdk/jr;->a()Lcom/flurry/sdk/jr;

    move-result-object v0

    .line 4097
    iget-object v0, v0, Lcom/flurry/sdk/jr;->a:Landroid/content/Context;

    .line 171
    const-string/jumbo v1, "connectivity"

    invoke-virtual {v0, v1}, Landroid/content/Context;->getSystemService(Ljava/lang/String;)Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Landroid/net/ConnectivityManager;

    return-object v0
.end method


# virtual methods
.method public final c()Lcom/flurry/sdk/jk$a;
    .locals 2

    .prologue
    .line 134
    iget-boolean v0, p0, Lcom/flurry/sdk/jk;->d:Z

    if-nez v0, :cond_0

    .line 138
    sget-object v0, Lcom/flurry/sdk/jk$a;->a:Lcom/flurry/sdk/jk$a;

    .line 166
    :goto_0
    return-object v0

    .line 141
    :cond_0
    invoke-static {}, Lcom/flurry/sdk/jk;->f()Landroid/net/ConnectivityManager;

    move-result-object v0

    .line 143
    invoke-virtual {v0}, Landroid/net/ConnectivityManager;->getActiveNetworkInfo()Landroid/net/NetworkInfo;

    move-result-object v0

    .line 144
    if-eqz v0, :cond_1

    invoke-virtual {v0}, Landroid/net/NetworkInfo;->isConnected()Z

    move-result v1

    if-nez v1, :cond_2

    .line 145
    :cond_1
    sget-object v0, Lcom/flurry/sdk/jk$a;->a:Lcom/flurry/sdk/jk$a;

    goto :goto_0

    .line 148
    :cond_2
    invoke-virtual {v0}, Landroid/net/NetworkInfo;->getType()I

    move-result v1

    packed-switch v1, :pswitch_data_0

    .line 163
    :pswitch_0
    invoke-virtual {v0}, Landroid/net/NetworkInfo;->isConnected()Z

    move-result v0

    if-eqz v0, :cond_3

    .line 164
    sget-object v0, Lcom/flurry/sdk/jk$a;->b:Lcom/flurry/sdk/jk$a;

    goto :goto_0

    .line 150
    :pswitch_1
    sget-object v0, Lcom/flurry/sdk/jk$a;->c:Lcom/flurry/sdk/jk$a;

    goto :goto_0

    .line 157
    :pswitch_2
    sget-object v0, Lcom/flurry/sdk/jk$a;->d:Lcom/flurry/sdk/jk$a;

    goto :goto_0

    .line 160
    :pswitch_3
    sget-object v0, Lcom/flurry/sdk/jk$a;->a:Lcom/flurry/sdk/jk$a;

    goto :goto_0

    .line 166
    :cond_3
    sget-object v0, Lcom/flurry/sdk/jk$a;->a:Lcom/flurry/sdk/jk$a;

    goto :goto_0

    .line 148
    :pswitch_data_0
    .packed-switch 0x0
        :pswitch_2
        :pswitch_1
        :pswitch_2
        :pswitch_2
        :pswitch_2
        :pswitch_2
        :pswitch_0
        :pswitch_0
        :pswitch_3
    .end packed-switch
.end method

.method public final onReceive(Landroid/content/Context;Landroid/content/Intent;)V
    .locals 2

    .prologue
    .line 107
    invoke-direct {p0, p1}, Lcom/flurry/sdk/jk;->a(Landroid/content/Context;)Z

    move-result v0

    .line 109
    iget-boolean v1, p0, Lcom/flurry/sdk/jk;->b:Z

    if-eq v1, v0, :cond_0

    .line 110
    iput-boolean v0, p0, Lcom/flurry/sdk/jk;->b:Z

    .line 112
    new-instance v1, Lcom/flurry/sdk/jj;

    invoke-direct {v1}, Lcom/flurry/sdk/jj;-><init>()V

    .line 113
    iput-boolean v0, v1, Lcom/flurry/sdk/jj;->a:Z

    .line 114
    invoke-virtual {p0}, Lcom/flurry/sdk/jk;->c()Lcom/flurry/sdk/jk$a;

    move-result-object v0

    iput-object v0, v1, Lcom/flurry/sdk/jj;->b:Lcom/flurry/sdk/jk$a;

    .line 4025
    invoke-static {}, Lcom/flurry/sdk/kb;->a()Lcom/flurry/sdk/kb;

    move-result-object v0

    invoke-virtual {v0, v1}, Lcom/flurry/sdk/kb;->a(Lcom/flurry/sdk/jz;)V

    .line 117
    :cond_0
    return-void
.end method
