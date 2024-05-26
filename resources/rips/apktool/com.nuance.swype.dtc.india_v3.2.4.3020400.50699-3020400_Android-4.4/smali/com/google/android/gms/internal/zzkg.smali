.class public final Lcom/google/android/gms/internal/zzkg;
.super Ljava/lang/Object;


# annotations
.annotation runtime Lcom/google/android/gms/internal/zzin;
.end annotation


# static fields
.field private static final zzcku:Ljava/util/concurrent/ExecutorService;

.field private static final zzckv:Ljava/util/concurrent/ExecutorService;


# direct methods
.method static constructor <clinit>()V
    .locals 2

    const/16 v0, 0xa

    const-string/jumbo v1, "Default"

    invoke-static {v1}, Lcom/google/android/gms/internal/zzkg;->zzcn(Ljava/lang/String;)Ljava/util/concurrent/ThreadFactory;

    move-result-object v1

    invoke-static {v0, v1}, Ljava/util/concurrent/Executors;->newFixedThreadPool(ILjava/util/concurrent/ThreadFactory;)Ljava/util/concurrent/ExecutorService;

    move-result-object v0

    sput-object v0, Lcom/google/android/gms/internal/zzkg;->zzcku:Ljava/util/concurrent/ExecutorService;

    const/4 v0, 0x5

    const-string/jumbo v1, "Loader"

    invoke-static {v1}, Lcom/google/android/gms/internal/zzkg;->zzcn(Ljava/lang/String;)Ljava/util/concurrent/ThreadFactory;

    move-result-object v1

    invoke-static {v0, v1}, Ljava/util/concurrent/Executors;->newFixedThreadPool(ILjava/util/concurrent/ThreadFactory;)Ljava/util/concurrent/ExecutorService;

    move-result-object v0

    sput-object v0, Lcom/google/android/gms/internal/zzkg;->zzckv:Ljava/util/concurrent/ExecutorService;

    return-void
.end method

.method public static zza(ILjava/lang/Runnable;)Lcom/google/android/gms/internal/zzky;
    .locals 2
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(I",
            "Ljava/lang/Runnable;",
            ")",
            "Lcom/google/android/gms/internal/zzky",
            "<",
            "Ljava/lang/Void;",
            ">;"
        }
    .end annotation

    const/4 v0, 0x1

    if-ne p0, v0, :cond_0

    sget-object v0, Lcom/google/android/gms/internal/zzkg;->zzckv:Ljava/util/concurrent/ExecutorService;

    new-instance v1, Lcom/google/android/gms/internal/zzkg$1;

    invoke-direct {v1, p1}, Lcom/google/android/gms/internal/zzkg$1;-><init>(Ljava/lang/Runnable;)V

    invoke-static {v0, v1}, Lcom/google/android/gms/internal/zzkg;->zza(Ljava/util/concurrent/ExecutorService;Ljava/util/concurrent/Callable;)Lcom/google/android/gms/internal/zzky;

    move-result-object v0

    :goto_0
    return-object v0

    :cond_0
    sget-object v0, Lcom/google/android/gms/internal/zzkg;->zzcku:Ljava/util/concurrent/ExecutorService;

    new-instance v1, Lcom/google/android/gms/internal/zzkg$2;

    invoke-direct {v1, p1}, Lcom/google/android/gms/internal/zzkg$2;-><init>(Ljava/lang/Runnable;)V

    invoke-static {v0, v1}, Lcom/google/android/gms/internal/zzkg;->zza(Ljava/util/concurrent/ExecutorService;Ljava/util/concurrent/Callable;)Lcom/google/android/gms/internal/zzky;

    move-result-object v0

    goto :goto_0
.end method

.method public static zza(Ljava/lang/Runnable;)Lcom/google/android/gms/internal/zzky;
    .locals 1
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Ljava/lang/Runnable;",
            ")",
            "Lcom/google/android/gms/internal/zzky",
            "<",
            "Ljava/lang/Void;",
            ">;"
        }
    .end annotation

    const/4 v0, 0x0

    invoke-static {v0, p0}, Lcom/google/android/gms/internal/zzkg;->zza(ILjava/lang/Runnable;)Lcom/google/android/gms/internal/zzky;

    move-result-object v0

    return-object v0
.end method

.method public static zza(Ljava/util/concurrent/Callable;)Lcom/google/android/gms/internal/zzky;
    .locals 1
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "<T:",
            "Ljava/lang/Object;",
            ">(",
            "Ljava/util/concurrent/Callable",
            "<TT;>;)",
            "Lcom/google/android/gms/internal/zzky",
            "<TT;>;"
        }
    .end annotation

    sget-object v0, Lcom/google/android/gms/internal/zzkg;->zzcku:Ljava/util/concurrent/ExecutorService;

    invoke-static {v0, p0}, Lcom/google/android/gms/internal/zzkg;->zza(Ljava/util/concurrent/ExecutorService;Ljava/util/concurrent/Callable;)Lcom/google/android/gms/internal/zzky;

    move-result-object v0

    return-object v0
.end method

.method public static zza(Ljava/util/concurrent/ExecutorService;Ljava/util/concurrent/Callable;)Lcom/google/android/gms/internal/zzky;
    .locals 5
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "<T:",
            "Ljava/lang/Object;",
            ">(",
            "Ljava/util/concurrent/ExecutorService;",
            "Ljava/util/concurrent/Callable",
            "<TT;>;)",
            "Lcom/google/android/gms/internal/zzky",
            "<TT;>;"
        }
    .end annotation

    .prologue
    .line 0
    new-instance v1, Lcom/google/android/gms/internal/zzkv;

    invoke-direct {v1}, Lcom/google/android/gms/internal/zzkv;-><init>()V

    :try_start_0
    new-instance v0, Lcom/google/android/gms/internal/zzkg$3;

    invoke-direct {v0, v1, p1}, Lcom/google/android/gms/internal/zzkg$3;-><init>(Lcom/google/android/gms/internal/zzkv;Ljava/util/concurrent/Callable;)V

    invoke-interface {p0, v0}, Ljava/util/concurrent/ExecutorService;->submit(Ljava/lang/Runnable;)Ljava/util/concurrent/Future;

    move-result-object v0

    new-instance v2, Lcom/google/android/gms/internal/zzkg$4;

    invoke-direct {v2, v1, v0}, Lcom/google/android/gms/internal/zzkg$4;-><init>(Lcom/google/android/gms/internal/zzkv;Ljava/util/concurrent/Future;)V

    .line 1000
    iget-object v0, v1, Lcom/google/android/gms/internal/zzkv;->zzcnp:Lcom/google/android/gms/internal/zzkz;

    .line 2000
    iget-object v3, v0, Lcom/google/android/gms/internal/zzkz;->zzcnx:Ljava/lang/Object;

    monitor-enter v3
    :try_end_0
    .catch Ljava/util/concurrent/RejectedExecutionException; {:try_start_0 .. :try_end_0} :catch_0

    :try_start_1
    iget-boolean v4, v0, Lcom/google/android/gms/internal/zzkz;->zzcoa:Z

    if-eqz v4, :cond_0

    invoke-static {v2}, Lcom/google/android/gms/internal/zzkz;->zzf(Ljava/lang/Runnable;)V

    :goto_0
    monitor-exit v3

    .line 0
    :goto_1
    return-object v1

    .line 2000
    :cond_0
    iget-object v0, v0, Lcom/google/android/gms/internal/zzkz;->zzcnz:Ljava/util/List;

    invoke-interface {v0, v2}, Ljava/util/List;->add(Ljava/lang/Object;)Z

    goto :goto_0

    :catchall_0
    move-exception v0

    monitor-exit v3
    :try_end_1
    .catchall {:try_start_1 .. :try_end_1} :catchall_0

    :try_start_2
    throw v0
    :try_end_2
    .catch Ljava/util/concurrent/RejectedExecutionException; {:try_start_2 .. :try_end_2} :catch_0

    .line 0
    :catch_0
    move-exception v0

    const-string/jumbo v2, "Thread execution is rejected."

    invoke-static {v2, v0}, Lcom/google/android/gms/internal/zzkd;->zzd(Ljava/lang/String;Ljava/lang/Throwable;)V

    const/4 v0, 0x1

    invoke-virtual {v1, v0}, Lcom/google/android/gms/internal/zzkv;->cancel(Z)Z

    goto :goto_1
.end method

.method private static zzcn(Ljava/lang/String;)Ljava/util/concurrent/ThreadFactory;
    .locals 1

    new-instance v0, Lcom/google/android/gms/internal/zzkg$5;

    invoke-direct {v0, p0}, Lcom/google/android/gms/internal/zzkg$5;-><init>(Ljava/lang/String;)V

    return-object v0
.end method
