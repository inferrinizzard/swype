.class final Lcom/google/android/gms/internal/zzkx$1;
.super Ljava/lang/Object;

# interfaces
.implements Ljava/lang/Runnable;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/google/android/gms/internal/zzkx;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic zzcnq:Lcom/google/android/gms/internal/zzkv;

.field final synthetic zzcnr:Lcom/google/android/gms/internal/zzkx$zza;

.field final synthetic zzcns:Lcom/google/android/gms/internal/zzky;


# direct methods
.method constructor <init>(Lcom/google/android/gms/internal/zzkv;Lcom/google/android/gms/internal/zzkx$zza;Lcom/google/android/gms/internal/zzky;)V
    .locals 0

    iput-object p1, p0, Lcom/google/android/gms/internal/zzkx$1;->zzcnq:Lcom/google/android/gms/internal/zzkv;

    iput-object p2, p0, Lcom/google/android/gms/internal/zzkx$1;->zzcnr:Lcom/google/android/gms/internal/zzkx$zza;

    iput-object p3, p0, Lcom/google/android/gms/internal/zzkx$1;->zzcns:Lcom/google/android/gms/internal/zzky;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public final run()V
    .locals 3

    :try_start_0
    iget-object v0, p0, Lcom/google/android/gms/internal/zzkx$1;->zzcnq:Lcom/google/android/gms/internal/zzkv;

    iget-object v1, p0, Lcom/google/android/gms/internal/zzkx$1;->zzcnr:Lcom/google/android/gms/internal/zzkx$zza;

    iget-object v2, p0, Lcom/google/android/gms/internal/zzkx$1;->zzcns:Lcom/google/android/gms/internal/zzky;

    invoke-interface {v2}, Lcom/google/android/gms/internal/zzky;->get()Ljava/lang/Object;

    move-result-object v2

    invoke-interface {v1, v2}, Lcom/google/android/gms/internal/zzkx$zza;->apply(Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v1

    invoke-virtual {v0, v1}, Lcom/google/android/gms/internal/zzkv;->zzh(Ljava/lang/Object;)V
    :try_end_0
    .catch Ljava/util/concurrent/CancellationException; {:try_start_0 .. :try_end_0} :catch_2
    .catch Ljava/lang/InterruptedException; {:try_start_0 .. :try_end_0} :catch_1
    .catch Ljava/util/concurrent/ExecutionException; {:try_start_0 .. :try_end_0} :catch_0

    :goto_0
    return-void

    :catch_0
    move-exception v0

    :goto_1
    iget-object v0, p0, Lcom/google/android/gms/internal/zzkx$1;->zzcnq:Lcom/google/android/gms/internal/zzkv;

    const/4 v1, 0x1

    invoke-virtual {v0, v1}, Lcom/google/android/gms/internal/zzkv;->cancel(Z)Z

    goto :goto_0

    :catch_1
    move-exception v0

    goto :goto_1

    :catch_2
    move-exception v0

    goto :goto_1
.end method
