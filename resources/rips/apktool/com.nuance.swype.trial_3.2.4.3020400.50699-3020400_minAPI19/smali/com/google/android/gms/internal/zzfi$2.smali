.class final Lcom/google/android/gms/internal/zzfi$2;
.super Lcom/google/android/gms/ads/internal/client/zzw$zza;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/google/android/gms/internal/zzfi;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic zzbjt:Lcom/google/android/gms/internal/zzfi;


# direct methods
.method constructor <init>(Lcom/google/android/gms/internal/zzfi;)V
    .locals 0

    iput-object p1, p0, Lcom/google/android/gms/internal/zzfi$2;->zzbjt:Lcom/google/android/gms/internal/zzfi;

    invoke-direct {p0}, Lcom/google/android/gms/ads/internal/client/zzw$zza;-><init>()V

    return-void
.end method


# virtual methods
.method public final onAppEvent(Ljava/lang/String;Ljava/lang/String;)V
    .locals 2
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Landroid/os/RemoteException;
        }
    .end annotation

    .prologue
    .line 0
    iget-object v0, p0, Lcom/google/android/gms/internal/zzfi$2;->zzbjt:Lcom/google/android/gms/internal/zzfi;

    .line 1000
    iget-object v0, v0, Lcom/google/android/gms/internal/zzfi;->zzalc:Ljava/util/List;

    .line 0
    new-instance v1, Lcom/google/android/gms/internal/zzfi$2$1;

    invoke-direct {v1, p0, p1, p2}, Lcom/google/android/gms/internal/zzfi$2$1;-><init>(Lcom/google/android/gms/internal/zzfi$2;Ljava/lang/String;Ljava/lang/String;)V

    invoke-interface {v0, v1}, Ljava/util/List;->add(Ljava/lang/Object;)Z

    return-void
.end method
