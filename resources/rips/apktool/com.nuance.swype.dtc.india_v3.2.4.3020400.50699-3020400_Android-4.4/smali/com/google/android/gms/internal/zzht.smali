.class public final Lcom/google/android/gms/internal/zzht;
.super Lcom/google/android/gms/internal/zzho$zza;


# annotations
.annotation runtime Lcom/google/android/gms/internal/zzin;
.end annotation


# instance fields
.field private final zzawh:Lcom/google/android/gms/ads/purchase/InAppPurchaseListener;


# direct methods
.method public constructor <init>(Lcom/google/android/gms/ads/purchase/InAppPurchaseListener;)V
    .locals 0

    invoke-direct {p0}, Lcom/google/android/gms/internal/zzho$zza;-><init>()V

    iput-object p1, p0, Lcom/google/android/gms/internal/zzht;->zzawh:Lcom/google/android/gms/ads/purchase/InAppPurchaseListener;

    return-void
.end method


# virtual methods
.method public final zza(Lcom/google/android/gms/internal/zzhn;)V
    .locals 2

    iget-object v0, p0, Lcom/google/android/gms/internal/zzht;->zzawh:Lcom/google/android/gms/ads/purchase/InAppPurchaseListener;

    new-instance v1, Lcom/google/android/gms/internal/zzhw;

    invoke-direct {v1, p1}, Lcom/google/android/gms/internal/zzhw;-><init>(Lcom/google/android/gms/internal/zzhn;)V

    invoke-interface {v0, v1}, Lcom/google/android/gms/ads/purchase/InAppPurchaseListener;->onInAppPurchaseRequested(Lcom/google/android/gms/ads/purchase/InAppPurchase;)V

    return-void
.end method
