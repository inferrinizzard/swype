.class final Lcom/google/android/gms/internal/zzcy$2;
.super Lcom/google/android/gms/internal/zzcy;


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Lcom/google/android/gms/internal/zzcy;->zza(ILjava/lang/String;I)Lcom/google/android/gms/internal/zzcy;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation

.annotation system Ldalvik/annotation/Signature;
    value = {
        "Lcom/google/android/gms/internal/zzcy",
        "<",
        "Ljava/lang/Integer;",
        ">;"
    }
.end annotation


# direct methods
.method constructor <init>(ILjava/lang/String;Ljava/lang/Integer;)V
    .locals 1

    const/4 v0, 0x0

    invoke-direct {p0, p1, p2, p3, v0}, Lcom/google/android/gms/internal/zzcy;-><init>(ILjava/lang/String;Ljava/lang/Object;B)V

    return-void
.end method


# virtual methods
.method public final synthetic zza(Landroid/content/SharedPreferences;)Ljava/lang/Object;
    .locals 2

    .prologue
    .line 0
    .line 2000
    iget-object v1, p0, Lcom/google/android/gms/internal/zzcy;->zzaxp:Ljava/lang/String;

    .line 3000
    iget-object v0, p0, Lcom/google/android/gms/internal/zzcy;->zzaxq:Ljava/lang/Object;

    .line 1000
    check-cast v0, Ljava/lang/Integer;

    invoke-virtual {v0}, Ljava/lang/Integer;->intValue()I

    move-result v0

    invoke-interface {p1, v1, v0}, Landroid/content/SharedPreferences;->getInt(Ljava/lang/String;I)I

    move-result v0

    invoke-static {v0}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v0

    .line 0
    return-object v0
.end method
