.class final Lcom/facebook/share/model/ShareVideo$1;
.super Ljava/lang/Object;
.source "ShareVideo.java"

# interfaces
.implements Landroid/os/Parcelable$Creator;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/facebook/share/model/ShareVideo;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x8
    name = null
.end annotation

.annotation system Ldalvik/annotation/Signature;
    value = {
        "Ljava/lang/Object;",
        "Landroid/os/Parcelable$Creator",
        "<",
        "Lcom/facebook/share/model/ShareVideo;",
        ">;"
    }
.end annotation


# direct methods
.method constructor <init>()V
    .locals 0

    .prologue
    .line 65
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public final createFromParcel(Landroid/os/Parcel;)Lcom/facebook/share/model/ShareVideo;
    .locals 1
    .param p1, "source"    # Landroid/os/Parcel;

    .prologue
    .line 69
    new-instance v0, Lcom/facebook/share/model/ShareVideo;

    invoke-direct {v0, p1}, Lcom/facebook/share/model/ShareVideo;-><init>(Landroid/os/Parcel;)V

    return-object v0
.end method

.method public final bridge synthetic createFromParcel(Landroid/os/Parcel;)Ljava/lang/Object;
    .locals 1
    .param p1, "x0"    # Landroid/os/Parcel;

    .prologue
    .line 65
    invoke-virtual {p0, p1}, Lcom/facebook/share/model/ShareVideo$1;->createFromParcel(Landroid/os/Parcel;)Lcom/facebook/share/model/ShareVideo;

    move-result-object v0

    return-object v0
.end method

.method public final newArray(I)[Lcom/facebook/share/model/ShareVideo;
    .locals 1
    .param p1, "size"    # I

    .prologue
    .line 74
    new-array v0, p1, [Lcom/facebook/share/model/ShareVideo;

    return-object v0
.end method

.method public final bridge synthetic newArray(I)[Ljava/lang/Object;
    .locals 1
    .param p1, "x0"    # I

    .prologue
    .line 65
    invoke-virtual {p0, p1}, Lcom/facebook/share/model/ShareVideo$1;->newArray(I)[Lcom/facebook/share/model/ShareVideo;

    move-result-object v0

    return-object v0
.end method
