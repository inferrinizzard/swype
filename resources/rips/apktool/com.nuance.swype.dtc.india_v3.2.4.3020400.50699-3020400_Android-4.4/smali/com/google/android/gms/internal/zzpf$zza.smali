.class public abstract Lcom/google/android/gms/internal/zzpf$zza;
.super Landroid/os/Binder;

# interfaces
.implements Lcom/google/android/gms/internal/zzpf;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/google/android/gms/internal/zzpf;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x409
    name = "zza"
.end annotation

.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Lcom/google/android/gms/internal/zzpf$zza$zza;
    }
.end annotation


# direct methods
.method public static zzdm(Landroid/os/IBinder;)Lcom/google/android/gms/internal/zzpf;
    .locals 2

    if-nez p0, :cond_0

    const/4 v0, 0x0

    :goto_0
    return-object v0

    :cond_0
    const-string/jumbo v0, "com.google.android.gms.clearcut.internal.IClearcutLoggerService"

    invoke-interface {p0, v0}, Landroid/os/IBinder;->queryLocalInterface(Ljava/lang/String;)Landroid/os/IInterface;

    move-result-object v0

    if-eqz v0, :cond_1

    instance-of v1, v0, Lcom/google/android/gms/internal/zzpf;

    if-eqz v1, :cond_1

    check-cast v0, Lcom/google/android/gms/internal/zzpf;

    goto :goto_0

    :cond_1
    new-instance v0, Lcom/google/android/gms/internal/zzpf$zza$zza;

    invoke-direct {v0, p0}, Lcom/google/android/gms/internal/zzpf$zza$zza;-><init>(Landroid/os/IBinder;)V

    goto :goto_0
.end method


# virtual methods
.method public onTransact(ILandroid/os/Parcel;Landroid/os/Parcel;I)Z
    .locals 5
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Landroid/os/RemoteException;
        }
    .end annotation

    .prologue
    const/4 v2, 0x0

    const/4 v3, 0x1

    .line 0
    sparse-switch p1, :sswitch_data_0

    invoke-super {p0, p1, p2, p3, p4}, Landroid/os/Binder;->onTransact(ILandroid/os/Parcel;Landroid/os/Parcel;I)Z

    move-result v0

    :goto_0
    return v0

    :sswitch_0
    const-string/jumbo v0, "com.google.android.gms.clearcut.internal.IClearcutLoggerService"

    invoke-virtual {p3, v0}, Landroid/os/Parcel;->writeString(Ljava/lang/String;)V

    move v0, v3

    goto :goto_0

    :sswitch_1
    const-string/jumbo v0, "com.google.android.gms.clearcut.internal.IClearcutLoggerService"

    invoke-virtual {p2, v0}, Landroid/os/Parcel;->enforceInterface(Ljava/lang/String;)V

    invoke-virtual {p2}, Landroid/os/Parcel;->readStrongBinder()Landroid/os/IBinder;

    move-result-object v1

    .line 1000
    if-nez v1, :cond_0

    move-object v1, v2

    .line 0
    :goto_1
    invoke-virtual {p2}, Landroid/os/Parcel;->readInt()I

    move-result v0

    if-eqz v0, :cond_2

    sget-object v0, Lcom/google/android/gms/clearcut/LogEventParcelable;->CREATOR:Lcom/google/android/gms/clearcut/zzd;

    invoke-virtual {v0, p2}, Lcom/google/android/gms/clearcut/zzd;->createFromParcel(Landroid/os/Parcel;)Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Lcom/google/android/gms/clearcut/LogEventParcelable;

    :goto_2
    invoke-virtual {p0, v1, v0}, Lcom/google/android/gms/internal/zzpf$zza;->zza(Lcom/google/android/gms/internal/zzpe;Lcom/google/android/gms/clearcut/LogEventParcelable;)V

    move v0, v3

    goto :goto_0

    .line 1000
    :cond_0
    const-string/jumbo v0, "com.google.android.gms.clearcut.internal.IClearcutLoggerCallbacks"

    invoke-interface {v1, v0}, Landroid/os/IBinder;->queryLocalInterface(Ljava/lang/String;)Landroid/os/IInterface;

    move-result-object v0

    if-eqz v0, :cond_1

    instance-of v4, v0, Lcom/google/android/gms/internal/zzpe;

    if-eqz v4, :cond_1

    check-cast v0, Lcom/google/android/gms/internal/zzpe;

    move-object v1, v0

    goto :goto_1

    :cond_1
    new-instance v0, Lcom/google/android/gms/internal/zzpe$zza$zza;

    invoke-direct {v0, v1}, Lcom/google/android/gms/internal/zzpe$zza$zza;-><init>(Landroid/os/IBinder;)V

    move-object v1, v0

    goto :goto_1

    :cond_2
    move-object v0, v2

    .line 0
    goto :goto_2

    :sswitch_data_0
    .sparse-switch
        0x1 -> :sswitch_1
        0x5f4e5446 -> :sswitch_0
    .end sparse-switch
.end method
