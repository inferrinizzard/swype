.class Lcom/nuance/connect/internal/CategoryServiceInternal$10;
.super Ljava/lang/Object;

# interfaces
.implements Ljava/lang/Runnable;


# instance fields
.field final synthetic this$0:Lcom/nuance/connect/internal/CategoryServiceInternal;

.field final synthetic val$items:Ljava/util/List;


# direct methods
.method constructor <init>(Lcom/nuance/connect/internal/CategoryServiceInternal;Ljava/util/List;)V
    .locals 0

    iput-object p1, p0, Lcom/nuance/connect/internal/CategoryServiceInternal$10;->this$0:Lcom/nuance/connect/internal/CategoryServiceInternal;

    iput-object p2, p0, Lcom/nuance/connect/internal/CategoryServiceInternal$10;->val$items:Ljava/util/List;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public run()V
    .locals 5

    const/4 v1, 0x0

    iget-object v0, p0, Lcom/nuance/connect/internal/CategoryServiceInternal$10;->this$0:Lcom/nuance/connect/internal/CategoryServiceInternal;

    invoke-static {v0}, Lcom/nuance/connect/internal/CategoryServiceInternal;->access$800(Lcom/nuance/connect/internal/CategoryServiceInternal;)Lcom/nuance/connect/util/ConcurrentCallbackSet;

    move-result-object v0

    new-array v2, v1, [Lcom/nuance/connect/api/CatalogService$CatalogCallback;

    invoke-virtual {v0, v2}, Lcom/nuance/connect/util/ConcurrentCallbackSet;->toArray([Ljava/lang/Object;)[Ljava/lang/Object;

    move-result-object v0

    check-cast v0, [Lcom/nuance/connect/api/CatalogService$CatalogCallback;

    array-length v2, v0

    :goto_0
    if-ge v1, v2, :cond_0

    aget-object v3, v0, v1

    iget-object v4, p0, Lcom/nuance/connect/internal/CategoryServiceInternal$10;->val$items:Ljava/util/List;

    invoke-interface {v3, v4}, Lcom/nuance/connect/api/CatalogService$CatalogCallback;->catalogItemInstalledUpdates(Ljava/util/List;)V

    add-int/lit8 v1, v1, 0x1

    goto :goto_0

    :cond_0
    return-void
.end method
