.class final Lcom/bumptech/glide/load/engine/cache/MemorySizeCalculator$DisplayMetricsScreenDimensions;
.super Ljava/lang/Object;
.source "MemorySizeCalculator.java"

# interfaces
.implements Lcom/bumptech/glide/load/engine/cache/MemorySizeCalculator$ScreenDimensions;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/bumptech/glide/load/engine/cache/MemorySizeCalculator;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0xa
    name = "DisplayMetricsScreenDimensions"
.end annotation


# instance fields
.field private final displayMetrics:Landroid/util/DisplayMetrics;


# direct methods
.method public constructor <init>(Landroid/util/DisplayMetrics;)V
    .locals 0
    .param p1, "displayMetrics"    # Landroid/util/DisplayMetrics;

    .prologue
    .line 105
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 106
    iput-object p1, p0, Lcom/bumptech/glide/load/engine/cache/MemorySizeCalculator$DisplayMetricsScreenDimensions;->displayMetrics:Landroid/util/DisplayMetrics;

    .line 107
    return-void
.end method


# virtual methods
.method public final getHeightPixels()I
    .locals 1

    .prologue
    .line 116
    iget-object v0, p0, Lcom/bumptech/glide/load/engine/cache/MemorySizeCalculator$DisplayMetricsScreenDimensions;->displayMetrics:Landroid/util/DisplayMetrics;

    iget v0, v0, Landroid/util/DisplayMetrics;->heightPixels:I

    return v0
.end method

.method public final getWidthPixels()I
    .locals 1

    .prologue
    .line 111
    iget-object v0, p0, Lcom/bumptech/glide/load/engine/cache/MemorySizeCalculator$DisplayMetricsScreenDimensions;->displayMetrics:Landroid/util/DisplayMetrics;

    iget v0, v0, Landroid/util/DisplayMetrics;->widthPixels:I

    return v0
.end method
