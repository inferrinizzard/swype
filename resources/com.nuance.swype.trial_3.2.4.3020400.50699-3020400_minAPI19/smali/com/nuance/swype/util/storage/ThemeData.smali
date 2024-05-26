.class public final Lcom/nuance/swype/util/storage/ThemeData;
.super Ljava/lang/Object;
.source "ThemeData.java"


# static fields
.field private static final cache:Ljava/util/HashMap;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/HashMap",
            "<",
            "Ljava/lang/String;",
            "Ljava/util/List",
            "<",
            "Lcom/nuance/swype/util/storage/ThemeItemSeed;",
            ">;>;"
        }
    .end annotation
.end field

.field private static final log:Lcom/nuance/swype/util/LogManager$Log;


# direct methods
.method static constructor <clinit>()V
    .locals 1

    .prologue
    .line 14
    const-string/jumbo v0, "ThemeData"

    invoke-static {v0}, Lcom/nuance/swype/util/LogManager;->getLog(Ljava/lang/String;)Lcom/nuance/swype/util/LogManager$Log;

    move-result-object v0

    sput-object v0, Lcom/nuance/swype/util/storage/ThemeData;->log:Lcom/nuance/swype/util/LogManager$Log;

    .line 16
    new-instance v0, Ljava/util/HashMap;

    invoke-direct {v0}, Ljava/util/HashMap;-><init>()V

    sput-object v0, Lcom/nuance/swype/util/storage/ThemeData;->cache:Ljava/util/HashMap;

    return-void
.end method

.method public constructor <init>()V
    .locals 0

    .prologue
    .line 18
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 19
    return-void
.end method

.method public static getCache()Ljava/util/HashMap;
    .locals 1
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "()",
            "Ljava/util/HashMap",
            "<",
            "Ljava/lang/String;",
            "Ljava/util/List",
            "<",
            "Lcom/nuance/swype/util/storage/ThemeItemSeed;",
            ">;>;"
        }
    .end annotation

    .prologue
    .line 71
    sget-object v0, Lcom/nuance/swype/util/storage/ThemeData;->cache:Ljava/util/HashMap;

    return-object v0
.end method

.method public static getThemeSeed(Ljava/lang/String;Ljava/lang/String;)Lcom/nuance/swype/util/storage/ThemeItemSeed;
    .locals 7
    .param p0, "sku"    # Ljava/lang/String;
    .param p1, "categoryId"    # Ljava/lang/String;

    .prologue
    const/4 v4, 0x0

    .line 85
    sget-object v3, Lcom/nuance/swype/util/storage/ThemeData;->cache:Ljava/util/HashMap;

    invoke-virtual {v3}, Ljava/util/HashMap;->isEmpty()Z

    move-result v3

    if-nez v3, :cond_0

    if-nez p0, :cond_1

    :cond_0
    move-object v1, v4

    .line 110
    :goto_0
    return-object v1

    .line 88
    :cond_1
    if-nez p1, :cond_4

    .line 90
    sget-object v3, Lcom/nuance/swype/util/storage/ThemeData;->cache:Ljava/util/HashMap;

    invoke-virtual {v3}, Ljava/util/HashMap;->keySet()Ljava/util/Set;

    move-result-object v3

    invoke-interface {v3}, Ljava/util/Set;->iterator()Ljava/util/Iterator;

    move-result-object v5

    :cond_2
    invoke-interface {v5}, Ljava/util/Iterator;->hasNext()Z

    move-result v3

    if-eqz v3, :cond_7

    invoke-interface {v5}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Ljava/lang/String;

    .line 91
    .local v0, "category":Ljava/lang/String;
    sget-object v3, Lcom/nuance/swype/util/storage/ThemeData;->cache:Ljava/util/HashMap;

    invoke-virtual {v3, v0}, Ljava/util/HashMap;->get(Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v3

    check-cast v3, Ljava/util/List;

    invoke-interface {v3}, Ljava/util/List;->iterator()Ljava/util/Iterator;

    move-result-object v3

    :cond_3
    invoke-interface {v3}, Ljava/util/Iterator;->hasNext()Z

    move-result v6

    if-eqz v6, :cond_2

    invoke-interface {v3}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Lcom/nuance/swype/util/storage/ThemeItemSeed;

    .line 6162
    .local v1, "seed":Lcom/nuance/swype/util/storage/ThemeItemSeed;
    iget-object v6, v1, Lcom/nuance/swype/util/storage/ThemeItemSeed;->sku:Ljava/lang/String;

    .line 92
    invoke-virtual {v6, p0}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v6

    if-eqz v6, :cond_3

    goto :goto_0

    .line 98
    .end local v0    # "category":Ljava/lang/String;
    .end local v1    # "seed":Lcom/nuance/swype/util/storage/ThemeItemSeed;
    :cond_4
    sget-object v3, Lcom/nuance/swype/util/storage/ThemeData;->cache:Ljava/util/HashMap;

    invoke-virtual {v3, p1}, Ljava/util/HashMap;->get(Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v2

    check-cast v2, Ljava/util/List;

    .line 99
    .local v2, "seeds":Ljava/util/List;, "Ljava/util/List<Lcom/nuance/swype/util/storage/ThemeItemSeed;>;"
    if-eqz v2, :cond_6

    .line 100
    invoke-interface {v2}, Ljava/util/List;->iterator()Ljava/util/Iterator;

    move-result-object v3

    :cond_5
    invoke-interface {v3}, Ljava/util/Iterator;->hasNext()Z

    move-result v5

    if-eqz v5, :cond_7

    invoke-interface {v3}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Lcom/nuance/swype/util/storage/ThemeItemSeed;

    .line 7162
    .restart local v1    # "seed":Lcom/nuance/swype/util/storage/ThemeItemSeed;
    iget-object v5, v1, Lcom/nuance/swype/util/storage/ThemeItemSeed;->sku:Ljava/lang/String;

    .line 101
    invoke-virtual {v5, p0}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v5

    if-eqz v5, :cond_5

    goto :goto_0

    .line 106
    .end local v1    # "seed":Lcom/nuance/swype/util/storage/ThemeItemSeed;
    :cond_6
    sget-object v3, Lcom/nuance/swype/util/storage/ThemeData;->log:Lcom/nuance/swype/util/LogManager$Log;

    new-instance v5, Ljava/lang/StringBuilder;

    invoke-direct {v5}, Ljava/lang/StringBuilder;-><init>()V

    invoke-virtual {v5, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v5

    const-string/jumbo v6, " not found"

    invoke-virtual {v5, v6}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v5

    invoke-virtual {v5}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v5

    invoke-interface {v3, v5}, Lcom/nuance/swype/util/LogManager$Log;->e(Ljava/lang/Object;)V

    .end local v2    # "seeds":Ljava/util/List;, "Ljava/util/List<Lcom/nuance/swype/util/storage/ThemeItemSeed;>;"
    :cond_7
    move-object v1, v4

    .line 110
    goto :goto_0
.end method

.method public static getTotalCacheCount()I
    .locals 4

    .prologue
    .line 75
    const/4 v1, 0x0

    .line 76
    .local v1, "count":I
    sget-object v2, Lcom/nuance/swype/util/storage/ThemeData;->cache:Ljava/util/HashMap;

    invoke-virtual {v2}, Ljava/util/HashMap;->keySet()Ljava/util/Set;

    move-result-object v2

    invoke-interface {v2}, Ljava/util/Set;->iterator()Ljava/util/Iterator;

    move-result-object v3

    :cond_0
    :goto_0
    invoke-interface {v3}, Ljava/util/Iterator;->hasNext()Z

    move-result v2

    if-eqz v2, :cond_1

    invoke-interface {v3}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Ljava/lang/String;

    .line 77
    .local v0, "category":Ljava/lang/String;
    sget-object v2, Lcom/nuance/swype/util/storage/ThemeData;->cache:Ljava/util/HashMap;

    invoke-virtual {v2, v0}, Ljava/util/HashMap;->get(Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v2

    if-eqz v2, :cond_0

    .line 78
    sget-object v2, Lcom/nuance/swype/util/storage/ThemeData;->cache:Ljava/util/HashMap;

    invoke-virtual {v2, v0}, Ljava/util/HashMap;->get(Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v2

    check-cast v2, Ljava/util/List;

    invoke-interface {v2}, Ljava/util/List;->size()I

    move-result v2

    add-int/2addr v1, v2

    goto :goto_0

    .line 81
    .end local v0    # "category":Ljava/lang/String;
    :cond_1
    return v1
.end method

.method public static isCacheEmpty()Z
    .locals 1

    .prologue
    .line 114
    sget-object v0, Lcom/nuance/swype/util/storage/ThemeData;->cache:Ljava/util/HashMap;

    invoke-virtual {v0}, Ljava/util/HashMap;->isEmpty()Z

    move-result v0

    return v0
.end method

.method public static isItemDeleted()Z
    .locals 6

    .prologue
    const/4 v3, 0x0

    .line 118
    sget-object v2, Lcom/nuance/swype/util/storage/ThemeData;->cache:Ljava/util/HashMap;

    if-nez v2, :cond_0

    move v2, v3

    .line 128
    .local v0, "category":Ljava/lang/String;
    :goto_0
    return v2

    .line 121
    .end local v0    # "category":Ljava/lang/String;
    :cond_0
    sget-object v2, Lcom/nuance/swype/util/storage/ThemeData;->cache:Ljava/util/HashMap;

    invoke-virtual {v2}, Ljava/util/HashMap;->keySet()Ljava/util/Set;

    move-result-object v2

    invoke-interface {v2}, Ljava/util/Set;->iterator()Ljava/util/Iterator;

    move-result-object v4

    .restart local v0    # "category":Ljava/lang/String;
    :cond_1
    invoke-interface {v4}, Ljava/util/Iterator;->hasNext()Z

    move-result v2

    if-eqz v2, :cond_3

    invoke-interface {v4}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v0

    .end local v0    # "category":Ljava/lang/String;
    check-cast v0, Ljava/lang/String;

    .line 122
    .restart local v0    # "category":Ljava/lang/String;
    sget-object v2, Lcom/nuance/swype/util/storage/ThemeData;->cache:Ljava/util/HashMap;

    invoke-virtual {v2, v0}, Ljava/util/HashMap;->get(Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v2

    check-cast v2, Ljava/util/List;

    invoke-interface {v2}, Ljava/util/List;->iterator()Ljava/util/Iterator;

    move-result-object v2

    :cond_2
    invoke-interface {v2}, Ljava/util/Iterator;->hasNext()Z

    move-result v5

    if-eqz v5, :cond_1

    invoke-interface {v2}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Lcom/nuance/swype/util/storage/ThemeItemSeed;

    .line 123
    .local v1, "seed":Lcom/nuance/swype/util/storage/ThemeItemSeed;
    if-eqz v1, :cond_2

    .line 8116
    iget-boolean v5, v1, Lcom/nuance/swype/util/storage/ThemeItemSeed;->isExisted:Z

    .line 123
    if-nez v5, :cond_2

    .line 124
    const/4 v2, 0x1

    goto :goto_0

    .end local v1    # "seed":Lcom/nuance/swype/util/storage/ThemeItemSeed;
    :cond_3
    move v2, v3

    .line 128
    goto :goto_0
.end method

.method public static resetItemDeleteStatus()V
    .locals 5

    .prologue
    .line 132
    sget-object v2, Lcom/nuance/swype/util/storage/ThemeData;->cache:Ljava/util/HashMap;

    if-nez v2, :cond_1

    .line 142
    .local v0, "category":Ljava/lang/String;
    :cond_0
    return-void

    .line 135
    .end local v0    # "category":Ljava/lang/String;
    :cond_1
    sget-object v2, Lcom/nuance/swype/util/storage/ThemeData;->cache:Ljava/util/HashMap;

    invoke-virtual {v2}, Ljava/util/HashMap;->keySet()Ljava/util/Set;

    move-result-object v2

    invoke-interface {v2}, Ljava/util/Set;->iterator()Ljava/util/Iterator;

    move-result-object v3

    .restart local v0    # "category":Ljava/lang/String;
    :cond_2
    invoke-interface {v3}, Ljava/util/Iterator;->hasNext()Z

    move-result v2

    if-eqz v2, :cond_0

    invoke-interface {v3}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v0

    .end local v0    # "category":Ljava/lang/String;
    check-cast v0, Ljava/lang/String;

    .line 136
    .restart local v0    # "category":Ljava/lang/String;
    sget-object v2, Lcom/nuance/swype/util/storage/ThemeData;->cache:Ljava/util/HashMap;

    invoke-virtual {v2, v0}, Ljava/util/HashMap;->get(Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v2

    check-cast v2, Ljava/util/List;

    invoke-interface {v2}, Ljava/util/List;->iterator()Ljava/util/Iterator;

    move-result-object v2

    :cond_3
    :goto_0
    invoke-interface {v2}, Ljava/util/Iterator;->hasNext()Z

    move-result v4

    if-eqz v4, :cond_2

    invoke-interface {v2}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Lcom/nuance/swype/util/storage/ThemeItemSeed;

    .line 137
    .local v1, "seed":Lcom/nuance/swype/util/storage/ThemeItemSeed;
    if-eqz v1, :cond_3

    .line 9112
    const/4 v4, 0x0

    iput-boolean v4, v1, Lcom/nuance/swype/util/storage/ThemeItemSeed;->isExisted:Z

    goto :goto_0
.end method

.method public static syncToCache(Lcom/nuance/swype/util/storage/ThemeItemSeed;)Z
    .locals 10
    .param p0, "seed"    # Lcom/nuance/swype/util/storage/ThemeItemSeed;

    .prologue
    const/4 v9, 0x1

    const/4 v2, 0x0

    .line 146
    .line 10112
    iput-boolean v9, p0, Lcom/nuance/swype/util/storage/ThemeItemSeed;->isExisted:Z

    .line 10120
    iget-object v0, p0, Lcom/nuance/swype/util/storage/ThemeItemSeed;->categoryKey:Ljava/lang/String;

    .line 148
    .local v0, "categoryKey":Ljava/lang/String;
    sget-object v6, Lcom/nuance/swype/util/storage/ThemeData;->cache:Ljava/util/HashMap;

    invoke-virtual {v6, v0}, Ljava/util/HashMap;->get(Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v4

    check-cast v4, Ljava/util/List;

    .line 149
    .local v4, "seeds":Ljava/util/List;, "Ljava/util/List<Lcom/nuance/swype/util/storage/ThemeItemSeed;>;"
    if-eqz v4, :cond_5

    .line 150
    new-instance v6, Ljava/util/ArrayList;

    invoke-direct {v6, v4}, Ljava/util/ArrayList;-><init>(Ljava/util/Collection;)V

    .line 152
    invoke-interface {v6}, Ljava/util/List;->iterator()Ljava/util/Iterator;

    move-result-object v6

    :cond_0
    invoke-interface {v6}, Ljava/util/Iterator;->hasNext()Z

    move-result v7

    if-eqz v7, :cond_5

    invoke-interface {v6}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v3

    check-cast v3, Lcom/nuance/swype/util/storage/ThemeItemSeed;

    .line 153
    .local v3, "item":Lcom/nuance/swype/util/storage/ThemeItemSeed;
    if-eqz v3, :cond_0

    .line 10162
    iget-object v7, v3, Lcom/nuance/swype/util/storage/ThemeItemSeed;->sku:Ljava/lang/String;

    .line 11162
    iget-object v8, p0, Lcom/nuance/swype/util/storage/ThemeItemSeed;->sku:Ljava/lang/String;

    .line 153
    invoke-virtual {v7, v8}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v7

    if-eqz v7, :cond_0

    .line 12120
    iget-object v7, v3, Lcom/nuance/swype/util/storage/ThemeItemSeed;->categoryKey:Ljava/lang/String;

    .line 154
    invoke-virtual {v7, v0}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v7

    if-eqz v7, :cond_0

    .line 156
    const-string/jumbo v6, "  "

    .line 12194
    iget-object v7, p0, Lcom/nuance/swype/util/storage/ThemeItemSeed;->price:Ljava/lang/String;

    .line 156
    invoke-virtual {v6, v7}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v6

    if-eqz v6, :cond_2

    .line 157
    invoke-static {}, Lcom/nuance/swype/service/impl/AccountUtil;->isGoogleAccountMissing()Z

    move-result v6

    if-nez v6, :cond_2

    .line 158
    invoke-static {}, Lcom/nuance/swype/service/impl/AccountUtil;->isGoogleAccountSignedIn()Z

    move-result v6

    if-eqz v6, :cond_2

    .line 159
    invoke-interface {v4, p0}, Ljava/util/List;->remove(Ljava/lang/Object;)Z

    .line 160
    invoke-interface {v4}, Ljava/util/List;->isEmpty()Z

    move-result v6

    if-eqz v6, :cond_1

    .line 161
    sget-object v6, Lcom/nuance/swype/util/storage/ThemeData;->cache:Ljava/util/HashMap;

    invoke-virtual {v6, v4}, Ljava/util/HashMap;->remove(Ljava/lang/Object;)Ljava/lang/Object;

    .line 212
    .end local v3    # "item":Lcom/nuance/swype/util/storage/ThemeItemSeed;
    :cond_1
    :goto_0
    return v2

    .line 13170
    .restart local v3    # "item":Lcom/nuance/swype/util/storage/ThemeItemSeed;
    :cond_2
    iget-object v6, p0, Lcom/nuance/swype/util/storage/ThemeItemSeed;->themeCategoryLabel:Ljava/lang/String;

    .line 13174
    iput-object v6, v3, Lcom/nuance/swype/util/storage/ThemeItemSeed;->themeCategoryLabel:Ljava/lang/String;

    .line 14146
    iget-object v6, p0, Lcom/nuance/swype/util/storage/ThemeItemSeed;->themeName:Ljava/lang/String;

    .line 14150
    iput-object v6, v3, Lcom/nuance/swype/util/storage/ThemeItemSeed;->themeName:Ljava/lang/String;

    .line 15136
    iget-object v6, p0, Lcom/nuance/swype/util/storage/ThemeItemSeed;->themeCategories:Ljava/util/List;

    .line 169
    invoke-virtual {v3, v6}, Lcom/nuance/swype/util/storage/ThemeItemSeed;->setThemeCategories(Ljava/util/List;)V

    .line 16132
    iget v6, v3, Lcom/nuance/swype/util/storage/ThemeItemSeed;->type:I

    .line 171
    sget-object v7, Lcom/nuance/connect/api/CatalogService$CatalogItem$Type;->BUNDLE:Lcom/nuance/connect/api/CatalogService$CatalogItem$Type;

    invoke-virtual {v7}, Lcom/nuance/connect/api/CatalogService$CatalogItem$Type;->ordinal()I

    move-result v7

    if-ne v6, v7, :cond_3

    .line 17100
    iget-object v6, p0, Lcom/nuance/swype/util/storage/ThemeItemSeed;->skuList:Ljava/util/List;

    .line 18094
    if-eqz v6, :cond_3

    .line 18095
    new-instance v7, Ljava/util/ArrayList;

    invoke-direct {v7, v6}, Ljava/util/ArrayList;-><init>(Ljava/util/Collection;)V

    iput-object v7, v3, Lcom/nuance/swype/util/storage/ThemeItemSeed;->skuList:Ljava/util/List;

    .line 18206
    :cond_3
    iget-boolean v6, p0, Lcom/nuance/swype/util/storage/ThemeItemSeed;->isPurchased:Z

    .line 19202
    iput-boolean v6, v3, Lcom/nuance/swype/util/storage/ThemeItemSeed;->isPurchased:Z

    .line 20194
    iget-object v6, p0, Lcom/nuance/swype/util/storage/ThemeItemSeed;->price:Ljava/lang/String;

    .line 177
    if-eqz v6, :cond_4

    .line 21194
    iget-object v6, p0, Lcom/nuance/swype/util/storage/ThemeItemSeed;->price:Ljava/lang/String;

    .line 21198
    iput-object v6, v3, Lcom/nuance/swype/util/storage/ThemeItemSeed;->price:Ljava/lang/String;

    .line 22112
    :cond_4
    iput-boolean v9, v3, Lcom/nuance/swype/util/storage/ThemeItemSeed;->isExisted:Z

    goto :goto_0

    .line 187
    .end local v3    # "item":Lcom/nuance/swype/util/storage/ThemeItemSeed;
    :cond_5
    const-string/jumbo v6, "  "

    .line 22194
    iget-object v7, p0, Lcom/nuance/swype/util/storage/ThemeItemSeed;->price:Ljava/lang/String;

    .line 187
    invoke-virtual {v6, v7}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v6

    if-eqz v6, :cond_6

    .line 188
    invoke-static {}, Lcom/nuance/swype/service/impl/AccountUtil;->isGoogleAccountMissing()Z

    move-result v6

    if-nez v6, :cond_6

    .line 189
    invoke-static {}, Lcom/nuance/swype/service/impl/AccountUtil;->isGoogleAccountSignedIn()Z

    move-result v6

    if-nez v6, :cond_1

    .line 195
    :cond_6
    if-eqz v4, :cond_7

    .line 197
    :try_start_0
    invoke-interface {v4, p0}, Ljava/util/List;->add(Ljava/lang/Object;)Z

    .line 206
    :goto_1
    const/4 v2, 0x1

    .local v2, "flag":Z
    goto :goto_0

    .line 200
    .end local v2    # "flag":Z
    :cond_7
    new-instance v5, Ljava/util/ArrayList;

    invoke-direct {v5}, Ljava/util/ArrayList;-><init>()V
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    .line 201
    .end local v4    # "seeds":Ljava/util/List;, "Ljava/util/List<Lcom/nuance/swype/util/storage/ThemeItemSeed;>;"
    .local v5, "seeds":Ljava/util/List;, "Ljava/util/List<Lcom/nuance/swype/util/storage/ThemeItemSeed;>;"
    :try_start_1
    invoke-interface {v5, p0}, Ljava/util/List;->add(Ljava/lang/Object;)Z

    .line 202
    sget-object v6, Lcom/nuance/swype/util/storage/ThemeData;->cache:Ljava/util/HashMap;

    invoke-virtual {v6, v0, v5}, Ljava/util/HashMap;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    .line 203
    sget-object v6, Lcom/nuance/swype/util/storage/ThemeData;->log:Lcom/nuance/swype/util/LogManager$Log;

    const/4 v7, 0x4

    new-array v7, v7, [Ljava/lang/Object;

    const/4 v8, 0x0

    const-string/jumbo v9, "put sku into cache:"

    aput-object v9, v7, v8

    const/4 v8, 0x1

    .line 23162
    iget-object v9, p0, Lcom/nuance/swype/util/storage/ThemeItemSeed;->sku:Ljava/lang/String;

    .line 203
    aput-object v9, v7, v8

    const/4 v8, 0x2

    const-string/jumbo v9, " item purchased:"

    aput-object v9, v7, v8

    const/4 v8, 0x3

    .line 23206
    iget-boolean v9, p0, Lcom/nuance/swype/util/storage/ThemeItemSeed;->isPurchased:Z

    .line 203
    invoke-static {v9}, Ljava/lang/Boolean;->valueOf(Z)Ljava/lang/Boolean;

    move-result-object v9

    aput-object v9, v7, v8

    invoke-interface {v6, v7}, Lcom/nuance/swype/util/LogManager$Log;->d([Ljava/lang/Object;)V
    :try_end_1
    .catch Ljava/lang/Exception; {:try_start_1 .. :try_end_1} :catch_1

    move-object v4, v5

    .end local v5    # "seeds":Ljava/util/List;, "Ljava/util/List<Lcom/nuance/swype/util/storage/ThemeItemSeed;>;"
    .restart local v4    # "seeds":Ljava/util/List;, "Ljava/util/List<Lcom/nuance/swype/util/storage/ThemeItemSeed;>;"
    goto :goto_1

    .line 207
    :catch_0
    move-exception v1

    .line 208
    .local v1, "e":Ljava/lang/Exception;
    :goto_2
    const/4 v2, 0x0

    .line 209
    .restart local v2    # "flag":Z
    invoke-virtual {v1}, Ljava/lang/Exception;->printStackTrace()V

    goto/16 :goto_0

    .line 207
    .end local v1    # "e":Ljava/lang/Exception;
    .end local v2    # "flag":Z
    .end local v4    # "seeds":Ljava/util/List;, "Ljava/util/List<Lcom/nuance/swype/util/storage/ThemeItemSeed;>;"
    .restart local v5    # "seeds":Ljava/util/List;, "Ljava/util/List<Lcom/nuance/swype/util/storage/ThemeItemSeed;>;"
    :catch_1
    move-exception v1

    move-object v4, v5

    .end local v5    # "seeds":Ljava/util/List;, "Ljava/util/List<Lcom/nuance/swype/util/storage/ThemeItemSeed;>;"
    .restart local v4    # "seeds":Ljava/util/List;, "Ljava/util/List<Lcom/nuance/swype/util/storage/ThemeItemSeed;>;"
    goto :goto_2
.end method

.method public static update(Lcom/nuance/swype/util/storage/ThemeItemSeed;)Z
    .locals 6
    .param p0, "seed"    # Lcom/nuance/swype/util/storage/ThemeItemSeed;

    .prologue
    .line 50
    const/4 v0, 0x0

    .line 2120
    .local v0, "flag":Z
    iget-object v2, p0, Lcom/nuance/swype/util/storage/ThemeItemSeed;->categoryKey:Ljava/lang/String;

    .line 54
    .local v2, "key":Ljava/lang/String;
    :try_start_0
    sget-object v3, Lcom/nuance/swype/util/storage/ThemeData;->cache:Ljava/util/HashMap;

    invoke-virtual {v3, v2}, Ljava/util/HashMap;->get(Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v3

    check-cast v3, Ljava/util/List;

    .line 56
    invoke-interface {v3}, Ljava/util/List;->iterator()Ljava/util/Iterator;

    move-result-object v3

    :cond_0
    :goto_0
    invoke-interface {v3}, Ljava/util/Iterator;->hasNext()Z

    move-result v4

    if-eqz v4, :cond_1

    invoke-interface {v3}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Lcom/nuance/swype/util/storage/ThemeItemSeed;

    .line 2162
    .local v1, "item":Lcom/nuance/swype/util/storage/ThemeItemSeed;
    iget-object v4, v1, Lcom/nuance/swype/util/storage/ThemeItemSeed;->sku:Ljava/lang/String;

    .line 3162
    iget-object v5, p0, Lcom/nuance/swype/util/storage/ThemeItemSeed;->sku:Ljava/lang/String;

    .line 57
    invoke-virtual {v4, v5}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v4

    if-eqz v4, :cond_0

    .line 3225
    iget-boolean v4, p0, Lcom/nuance/swype/util/storage/ThemeItemSeed;->isInstalled:Z

    .line 58
    invoke-virtual {v1, v4}, Lcom/nuance/swype/util/storage/ThemeItemSeed;->setInstalled(Z)V

    .line 4206
    iget-boolean v4, p0, Lcom/nuance/swype/util/storage/ThemeItemSeed;->isPurchased:Z

    .line 5202
    iput-boolean v4, v1, Lcom/nuance/swype/util/storage/ThemeItemSeed;->isPurchased:Z

    .line 5235
    iget-boolean v4, p0, Lcom/nuance/swype/util/storage/ThemeItemSeed;->isInstalling:Z

    .line 60
    invoke-virtual {v1, v4}, Lcom/nuance/swype/util/storage/ThemeItemSeed;->setInstalling(Z)V
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    .line 61
    const/4 v0, 0x1

    goto :goto_0

    .line 65
    .end local v1    # "item":Lcom/nuance/swype/util/storage/ThemeItemSeed;
    :catch_0
    move-exception v3

    invoke-virtual {v3}, Ljava/lang/Exception;->printStackTrace()V

    .line 67
    :cond_1
    return v0
.end method

.method public static update(Ljava/lang/String;Lcom/nuance/swype/inapp/CatalogManager$ThemeStatusChange;)Z
    .locals 11
    .param p0, "sku"    # Ljava/lang/String;
    .param p1, "type"    # Lcom/nuance/swype/inapp/CatalogManager$ThemeStatusChange;

    .prologue
    const/4 v10, 0x1

    const/4 v9, 0x0

    .line 22
    const/4 v1, 0x0

    .line 24
    .local v1, "flag":Z
    sget-object v3, Lcom/nuance/swype/util/storage/ThemeData;->cache:Ljava/util/HashMap;

    invoke-virtual {v3}, Ljava/util/HashMap;->keySet()Ljava/util/Set;

    move-result-object v3

    invoke-interface {v3}, Ljava/util/Set;->iterator()Ljava/util/Iterator;

    move-result-object v4

    :cond_0
    invoke-interface {v4}, Ljava/util/Iterator;->hasNext()Z

    move-result v3

    if-eqz v3, :cond_7

    invoke-interface {v4}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Ljava/lang/String;

    .line 25
    .local v0, "category":Ljava/lang/String;
    sget-object v3, Lcom/nuance/swype/util/storage/ThemeData;->cache:Ljava/util/HashMap;

    invoke-virtual {v3, v0}, Ljava/util/HashMap;->get(Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v3

    check-cast v3, Ljava/util/List;

    invoke-interface {v3}, Ljava/util/List;->iterator()Ljava/util/Iterator;

    move-result-object v3

    :cond_1
    :goto_0
    invoke-interface {v3}, Ljava/util/Iterator;->hasNext()Z

    move-result v5

    if-eqz v5, :cond_0

    invoke-interface {v3}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v2

    check-cast v2, Lcom/nuance/swype/util/storage/ThemeItemSeed;

    .line 1162
    .local v2, "seed":Lcom/nuance/swype/util/storage/ThemeItemSeed;
    iget-object v5, v2, Lcom/nuance/swype/util/storage/ThemeItemSeed;->sku:Ljava/lang/String;

    .line 26
    invoke-virtual {v5, p0}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v5

    if-eqz v5, :cond_1

    .line 27
    sget-object v5, Lcom/nuance/swype/inapp/CatalogManager$ThemeStatusChange;->THEME_INSTALLED:Lcom/nuance/swype/inapp/CatalogManager$ThemeStatusChange;

    if-ne p1, v5, :cond_3

    .line 28
    invoke-virtual {v2, v10}, Lcom/nuance/swype/util/storage/ThemeItemSeed;->setInstalled(Z)V

    .line 40
    :cond_2
    :goto_1
    sget-object v5, Lcom/nuance/swype/util/storage/ThemeData;->log:Lcom/nuance/swype/util/LogManager$Log;

    const/4 v6, 0x4

    new-array v6, v6, [Ljava/lang/Object;

    const-string/jumbo v7, "update sku:"

    aput-object v7, v6, v9

    aput-object p0, v6, v10

    const/4 v7, 0x2

    const-string/jumbo v8, " status:"

    aput-object v8, v6, v7

    const/4 v7, 0x3

    invoke-virtual {p1}, Lcom/nuance/swype/inapp/CatalogManager$ThemeStatusChange;->ordinal()I

    move-result v8

    invoke-static {v8}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v8

    aput-object v8, v6, v7

    invoke-interface {v5, v6}, Lcom/nuance/swype/util/LogManager$Log;->d([Ljava/lang/Object;)V

    .line 41
    const/4 v1, 0x1

    goto :goto_0

    .line 29
    :cond_3
    sget-object v5, Lcom/nuance/swype/inapp/CatalogManager$ThemeStatusChange;->THEME_PURCHASED:Lcom/nuance/swype/inapp/CatalogManager$ThemeStatusChange;

    if-ne p1, v5, :cond_4

    .line 1202
    iput-boolean v10, v2, Lcom/nuance/swype/util/storage/ThemeItemSeed;->isPurchased:Z

    goto :goto_1

    .line 31
    :cond_4
    sget-object v5, Lcom/nuance/swype/inapp/CatalogManager$ThemeStatusChange;->THEME_INSTALL_CANCELED:Lcom/nuance/swype/inapp/CatalogManager$ThemeStatusChange;

    if-ne p1, v5, :cond_5

    .line 32
    invoke-virtual {v2, v9}, Lcom/nuance/swype/util/storage/ThemeItemSeed;->setInstalled(Z)V

    .line 33
    invoke-virtual {v2, v9}, Lcom/nuance/swype/util/storage/ThemeItemSeed;->setInstalling(Z)V

    goto :goto_1

    .line 34
    :cond_5
    sget-object v5, Lcom/nuance/swype/inapp/CatalogManager$ThemeStatusChange;->THEME_UNINSTALLED:Lcom/nuance/swype/inapp/CatalogManager$ThemeStatusChange;

    if-ne p1, v5, :cond_6

    .line 35
    invoke-virtual {v2, v9}, Lcom/nuance/swype/util/storage/ThemeItemSeed;->setInstalled(Z)V

    .line 36
    invoke-virtual {v2, v9}, Lcom/nuance/swype/util/storage/ThemeItemSeed;->setInstalling(Z)V

    goto :goto_1

    .line 37
    :cond_6
    sget-object v5, Lcom/nuance/swype/inapp/CatalogManager$ThemeStatusChange;->THEME_INSTALLING:Lcom/nuance/swype/inapp/CatalogManager$ThemeStatusChange;

    if-ne p1, v5, :cond_2

    .line 38
    invoke-virtual {v2, v10}, Lcom/nuance/swype/util/storage/ThemeItemSeed;->setInstalling(Z)V

    goto :goto_1

    .line 46
    .end local v0    # "category":Ljava/lang/String;
    .end local v2    # "seed":Lcom/nuance/swype/util/storage/ThemeItemSeed;
    :cond_7
    return v1
.end method
