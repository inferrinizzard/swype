.class public abstract Landroid/support/v7/widget/GridLayoutManager$SpanSizeLookup;
.super Ljava/lang/Object;
.source "GridLayoutManager.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v7/widget/GridLayoutManager;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x409
    name = "SpanSizeLookup"
.end annotation


# instance fields
.field private mCacheSpanIndices:Z

.field final mSpanIndexCache:Landroid/util/SparseIntArray;


# direct methods
.method public constructor <init>()V
    .locals 1

    .prologue
    .line 816
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 818
    new-instance v0, Landroid/util/SparseIntArray;

    invoke-direct {v0}, Landroid/util/SparseIntArray;-><init>()V

    iput-object v0, p0, Landroid/support/v7/widget/GridLayoutManager$SpanSizeLookup;->mSpanIndexCache:Landroid/util/SparseIntArray;

    .line 820
    const/4 v0, 0x0

    iput-boolean v0, p0, Landroid/support/v7/widget/GridLayoutManager$SpanSizeLookup;->mCacheSpanIndices:Z

    return-void
.end method

.method public static getSpanGroupIndex(II)I
    .locals 4
    .param p0, "adapterPosition"    # I
    .param p1, "spanCount"    # I

    .prologue
    .line 956
    const/4 v2, 0x0

    .line 957
    .local v2, "span":I
    const/4 v0, 0x0

    .line 959
    .local v0, "group":I
    const/4 v1, 0x0

    .local v1, "i":I
    :goto_0
    if-ge v1, p0, :cond_2

    .line 961
    add-int/lit8 v2, v2, 0x1

    .line 962
    if-ne v2, p1, :cond_1

    .line 963
    const/4 v2, 0x0

    .line 964
    add-int/lit8 v0, v0, 0x1

    .line 959
    :cond_0
    :goto_1
    add-int/lit8 v1, v1, 0x1

    goto :goto_0

    .line 965
    :cond_1
    if-le v2, p1, :cond_0

    .line 967
    const/4 v2, 0x1

    .line 968
    add-int/lit8 v0, v0, 0x1

    goto :goto_1

    .line 971
    :cond_2
    add-int/lit8 v3, v2, 0x1

    if-le v3, p1, :cond_3

    .line 972
    add-int/lit8 v0, v0, 0x1

    .line 974
    :cond_3
    return v0
.end method


# virtual methods
.method final getCachedSpanIndex(II)I
    .locals 4
    .param p1, "position"    # I
    .param p2, "spanCount"    # I

    .prologue
    const/4 v3, -0x1

    .line 859
    iget-boolean v2, p0, Landroid/support/v7/widget/GridLayoutManager$SpanSizeLookup;->mCacheSpanIndices:Z

    if-nez v2, :cond_1

    .line 860
    invoke-virtual {p0, p1, p2}, Landroid/support/v7/widget/GridLayoutManager$SpanSizeLookup;->getSpanIndex(II)I

    move-result v0

    .line 868
    :cond_0
    :goto_0
    return v0

    .line 862
    :cond_1
    iget-object v2, p0, Landroid/support/v7/widget/GridLayoutManager$SpanSizeLookup;->mSpanIndexCache:Landroid/util/SparseIntArray;

    invoke-virtual {v2, p1, v3}, Landroid/util/SparseIntArray;->get(II)I

    move-result v0

    .line 863
    .local v0, "existing":I
    if-ne v0, v3, :cond_0

    .line 866
    invoke-virtual {p0, p1, p2}, Landroid/support/v7/widget/GridLayoutManager$SpanSizeLookup;->getSpanIndex(II)I

    move-result v1

    .line 867
    .local v1, "value":I
    iget-object v2, p0, Landroid/support/v7/widget/GridLayoutManager$SpanSizeLookup;->mSpanIndexCache:Landroid/util/SparseIntArray;

    invoke-virtual {v2, p1, v1}, Landroid/util/SparseIntArray;->put(II)V

    move v0, v1

    .line 868
    goto :goto_0
.end method

.method public getSpanIndex(II)I
    .locals 9
    .param p1, "position"    # I
    .param p2, "spanCount"    # I

    .prologue
    const/4 v4, 0x0

    .line 896
    const/4 v5, 0x1

    if-ne v5, p2, :cond_1

    move v2, v4

    .line 922
    :cond_0
    :goto_0
    return v2

    .line 899
    :cond_1
    const/4 v2, 0x0

    .line 900
    .local v2, "span":I
    const/4 v3, 0x0

    .line 902
    .local v3, "startPos":I
    iget-boolean v5, p0, Landroid/support/v7/widget/GridLayoutManager$SpanSizeLookup;->mCacheSpanIndices:Z

    if-eqz v5, :cond_4

    iget-object v5, p0, Landroid/support/v7/widget/GridLayoutManager$SpanSizeLookup;->mSpanIndexCache:Landroid/util/SparseIntArray;

    invoke-virtual {v5}, Landroid/util/SparseIntArray;->size()I

    move-result v5

    if-lez v5, :cond_4

    .line 1927
    iget-object v5, p0, Landroid/support/v7/widget/GridLayoutManager$SpanSizeLookup;->mSpanIndexCache:Landroid/util/SparseIntArray;

    invoke-virtual {v5}, Landroid/util/SparseIntArray;->size()I

    move-result v5

    add-int/lit8 v5, v5, -0x1

    move v6, v4

    .line 1929
    :goto_1
    if-gt v6, v5, :cond_3

    .line 1930
    add-int v7, v6, v5

    ushr-int/lit8 v7, v7, 0x1

    .line 1931
    iget-object v8, p0, Landroid/support/v7/widget/GridLayoutManager$SpanSizeLookup;->mSpanIndexCache:Landroid/util/SparseIntArray;

    invoke-virtual {v8, v7}, Landroid/util/SparseIntArray;->keyAt(I)I

    move-result v8

    .line 1932
    if-ge v8, p1, :cond_2

    .line 1933
    add-int/lit8 v6, v7, 0x1

    goto :goto_1

    .line 1935
    :cond_2
    add-int/lit8 v5, v7, -0x1

    .line 1937
    goto :goto_1

    .line 1938
    :cond_3
    add-int/lit8 v5, v6, -0x1

    .line 1939
    if-ltz v5, :cond_6

    iget-object v6, p0, Landroid/support/v7/widget/GridLayoutManager$SpanSizeLookup;->mSpanIndexCache:Landroid/util/SparseIntArray;

    invoke-virtual {v6}, Landroid/util/SparseIntArray;->size()I

    move-result v6

    if-ge v5, v6, :cond_6

    .line 1940
    iget-object v6, p0, Landroid/support/v7/widget/GridLayoutManager$SpanSizeLookup;->mSpanIndexCache:Landroid/util/SparseIntArray;

    invoke-virtual {v6, v5}, Landroid/util/SparseIntArray;->keyAt(I)I

    move-result v1

    .line 904
    .local v1, "prevKey":I
    :goto_2
    if-ltz v1, :cond_4

    .line 905
    iget-object v5, p0, Landroid/support/v7/widget/GridLayoutManager$SpanSizeLookup;->mSpanIndexCache:Landroid/util/SparseIntArray;

    invoke-virtual {v5, v1}, Landroid/util/SparseIntArray;->get(I)I

    move-result v5

    add-int/lit8 v2, v5, 0x1

    .line 906
    add-int/lit8 v3, v1, 0x1

    .line 909
    .end local v1    # "prevKey":I
    :cond_4
    move v0, v3

    .local v0, "i":I
    :goto_3
    if-ge v0, p1, :cond_8

    .line 911
    add-int/lit8 v2, v2, 0x1

    .line 912
    if-ne v2, p2, :cond_7

    .line 913
    const/4 v2, 0x0

    .line 909
    :cond_5
    :goto_4
    add-int/lit8 v0, v0, 0x1

    goto :goto_3

    .line 1942
    .end local v0    # "i":I
    :cond_6
    const/4 v1, -0x1

    goto :goto_2

    .line 914
    .restart local v0    # "i":I
    :cond_7
    if-le v2, p2, :cond_5

    .line 916
    const/4 v2, 0x1

    goto :goto_4

    .line 919
    :cond_8
    add-int/lit8 v5, v2, 0x1

    if-le v5, p2, :cond_0

    move v2, v4

    .line 922
    goto :goto_0
.end method
