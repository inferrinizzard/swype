.class final Lcom/facebook/share/model/ShareMediaContent$1;
.super Ljava/lang/Object;
.source "ShareMediaContent.java"

# interfaces
.implements Landroid/os/Parcelable$Creator;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/facebook/share/model/ShareMediaContent;
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
        "Lcom/facebook/share/model/ShareMediaContent;",
        ">;"
    }
.end annotation


# direct methods
.method constructor <init>()V
    .locals 0

    .prologue
    .line 67
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public final createFromParcel(Landroid/os/Parcel;)Lcom/facebook/share/model/ShareMediaContent;
    .locals 1
    .param p1, "in"    # Landroid/os/Parcel;

    .prologue
    .line 69
    new-instance v0, Lcom/facebook/share/model/ShareMediaContent;

    invoke-direct {v0, p1}, Lcom/facebook/share/model/ShareMediaContent;-><init>(Landroid/os/Parcel;)V

    return-object v0
.end method

.method public final bridge synthetic createFromParcel(Landroid/os/Parcel;)Ljava/lang/Object;
    .locals 1
    .param p1, "x0"    # Landroid/os/Parcel;

    .prologue
    .line 67
    invoke-virtual {p0, p1}, Lcom/facebook/share/model/ShareMediaContent$1;->createFromParcel(Landroid/os/Parcel;)Lcom/facebook/share/model/ShareMediaContent;

    move-result-object v0

    return-object v0
.end method

.method public final newArray(I)[Lcom/facebook/share/model/ShareMediaContent;
    .locals 1
    .param p1, "size"    # I

    .prologue
    .line 73
    new-array v0, p1, [Lcom/facebook/share/model/ShareMediaContent;

    return-object v0
.end method

.method public final bridge synthetic newArray(I)[Ljava/lang/Object;
    .locals 1
    .param p1, "x0"    # I

    .prologue
    .line 67
    invoke-virtual {p0, p1}, Lcom/facebook/share/model/ShareMediaContent$1;->newArray(I)[Lcom/facebook/share/model/ShareMediaContent;

    move-result-object v0

    return-object v0
.end method
