.class Lcom/nuance/swype/input/settings/ChinesePrefs$6;
.super Ljava/lang/Object;
.source "ChinesePrefs.java"

# interfaces
.implements Ljava/lang/Runnable;


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Lcom/nuance/swype/input/settings/ChinesePrefs;->retrieveDictionaryNamesInBackground()V
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Lcom/nuance/swype/input/settings/ChinesePrefs;


# direct methods
.method constructor <init>(Lcom/nuance/swype/input/settings/ChinesePrefs;)V
    .locals 0
    .param p1, "this$0"    # Lcom/nuance/swype/input/settings/ChinesePrefs;

    .prologue
    .line 398
    iput-object p1, p0, Lcom/nuance/swype/input/settings/ChinesePrefs$6;->this$0:Lcom/nuance/swype/input/settings/ChinesePrefs;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public run()V
    .locals 8

    .prologue
    .line 400
    iget-object v4, p0, Lcom/nuance/swype/input/settings/ChinesePrefs$6;->this$0:Lcom/nuance/swype/input/settings/ChinesePrefs;

    const/4 v5, 0x0

    invoke-static {v4, v5}, Lcom/nuance/swype/input/settings/ChinesePrefs;->access$502(Lcom/nuance/swype/input/settings/ChinesePrefs;Ljava/util/List;)Ljava/util/List;

    .line 402
    iget-object v4, p0, Lcom/nuance/swype/input/settings/ChinesePrefs$6;->this$0:Lcom/nuance/swype/input/settings/ChinesePrefs;

    new-instance v5, Ljava/util/ArrayList;

    invoke-direct {v5}, Ljava/util/ArrayList;-><init>()V

    invoke-static {v4, v5}, Lcom/nuance/swype/input/settings/ChinesePrefs;->access$602(Lcom/nuance/swype/input/settings/ChinesePrefs;Ljava/util/List;)Ljava/util/List;

    .line 403
    iget-object v4, p0, Lcom/nuance/swype/input/settings/ChinesePrefs$6;->this$0:Lcom/nuance/swype/input/settings/ChinesePrefs;

    new-instance v5, Ljava/util/ArrayList;

    invoke-direct {v5}, Ljava/util/ArrayList;-><init>()V

    invoke-static {v4, v5}, Lcom/nuance/swype/input/settings/ChinesePrefs;->access$702(Lcom/nuance/swype/input/settings/ChinesePrefs;Ljava/util/List;)Ljava/util/List;

    .line 404
    iget-object v4, p0, Lcom/nuance/swype/input/settings/ChinesePrefs$6;->this$0:Lcom/nuance/swype/input/settings/ChinesePrefs;

    invoke-static {v4}, Lcom/nuance/swype/input/settings/ChinesePrefs;->access$800(Lcom/nuance/swype/input/settings/ChinesePrefs;)Lcom/nuance/swype/input/CategoryDBList;

    move-result-object v4

    if-eqz v4, :cond_0

    iget-object v4, p0, Lcom/nuance/swype/input/settings/ChinesePrefs$6;->this$0:Lcom/nuance/swype/input/settings/ChinesePrefs;

    invoke-static {v4}, Lcom/nuance/swype/input/settings/ChinesePrefs;->access$900(Lcom/nuance/swype/input/settings/ChinesePrefs;)Lcom/nuance/swype/input/InputMethods$Language;

    move-result-object v4

    if-eqz v4, :cond_0

    .line 405
    iget-object v4, p0, Lcom/nuance/swype/input/settings/ChinesePrefs$6;->this$0:Lcom/nuance/swype/input/settings/ChinesePrefs;

    invoke-static {v4}, Lcom/nuance/swype/input/settings/ChinesePrefs;->access$900(Lcom/nuance/swype/input/settings/ChinesePrefs;)Lcom/nuance/swype/input/InputMethods$Language;

    move-result-object v4

    iget-object v4, v4, Lcom/nuance/swype/input/InputMethods$Language;->mEnglishName:Ljava/lang/String;

    if-eqz v4, :cond_0

    .line 406
    iget-object v4, p0, Lcom/nuance/swype/input/settings/ChinesePrefs$6;->this$0:Lcom/nuance/swype/input/settings/ChinesePrefs;

    iget-object v5, p0, Lcom/nuance/swype/input/settings/ChinesePrefs$6;->this$0:Lcom/nuance/swype/input/settings/ChinesePrefs;

    invoke-static {v5}, Lcom/nuance/swype/input/settings/ChinesePrefs;->access$800(Lcom/nuance/swype/input/settings/ChinesePrefs;)Lcom/nuance/swype/input/CategoryDBList;

    move-result-object v5

    iget-object v6, p0, Lcom/nuance/swype/input/settings/ChinesePrefs$6;->this$0:Lcom/nuance/swype/input/settings/ChinesePrefs;

    invoke-static {v6}, Lcom/nuance/swype/input/settings/ChinesePrefs;->access$900(Lcom/nuance/swype/input/settings/ChinesePrefs;)Lcom/nuance/swype/input/InputMethods$Language;

    move-result-object v6

    iget-object v6, v6, Lcom/nuance/swype/input/InputMethods$Language;->mEnglishName:Ljava/lang/String;

    invoke-virtual {v5, v6}, Lcom/nuance/swype/input/CategoryDBList;->getShowableCDBs(Ljava/lang/String;)Ljava/util/List;

    move-result-object v5

    invoke-static {v4, v5}, Lcom/nuance/swype/input/settings/ChinesePrefs;->access$502(Lcom/nuance/swype/input/settings/ChinesePrefs;Ljava/util/List;)Ljava/util/List;

    .line 412
    :cond_0
    iget-object v4, p0, Lcom/nuance/swype/input/settings/ChinesePrefs$6;->this$0:Lcom/nuance/swype/input/settings/ChinesePrefs;

    invoke-static {v4}, Lcom/nuance/swype/input/settings/ChinesePrefs;->access$500(Lcom/nuance/swype/input/settings/ChinesePrefs;)Ljava/util/List;

    move-result-object v4

    if-eqz v4, :cond_5

    iget-object v4, p0, Lcom/nuance/swype/input/settings/ChinesePrefs$6;->this$0:Lcom/nuance/swype/input/settings/ChinesePrefs;

    invoke-static {v4}, Lcom/nuance/swype/input/settings/ChinesePrefs;->access$500(Lcom/nuance/swype/input/settings/ChinesePrefs;)Ljava/util/List;

    move-result-object v4

    invoke-interface {v4}, Ljava/util/List;->size()I

    move-result v4

    if-lez v4, :cond_5

    .line 413
    iget-object v4, p0, Lcom/nuance/swype/input/settings/ChinesePrefs$6;->this$0:Lcom/nuance/swype/input/settings/ChinesePrefs;

    invoke-static {v4}, Lcom/nuance/swype/input/settings/ChinesePrefs;->access$500(Lcom/nuance/swype/input/settings/ChinesePrefs;)Ljava/util/List;

    move-result-object v4

    invoke-interface {v4}, Ljava/util/List;->iterator()Ljava/util/Iterator;

    move-result-object v4

    :cond_1
    :goto_0
    invoke-interface {v4}, Ljava/util/Iterator;->hasNext()Z

    move-result v5

    if-eqz v5, :cond_4

    invoke-interface {v4}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Ljava/lang/String;

    .line 414
    .local v0, "cdb":Ljava/lang/String;
    iget-object v5, p0, Lcom/nuance/swype/input/settings/ChinesePrefs$6;->this$0:Lcom/nuance/swype/input/settings/ChinesePrefs;

    invoke-static {v5}, Lcom/nuance/swype/input/settings/ChinesePrefs;->access$800(Lcom/nuance/swype/input/settings/ChinesePrefs;)Lcom/nuance/swype/input/CategoryDBList;

    move-result-object v5

    invoke-virtual {v5, v0}, Lcom/nuance/swype/input/CategoryDBList;->getFileName(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v1

    .line 415
    .local v1, "cdbName":Ljava/lang/String;
    if-eqz v1, :cond_2

    .line 416
    invoke-virtual {v1}, Ljava/lang/String;->trim()Ljava/lang/String;

    move-result-object v1

    .line 419
    :cond_2
    iget-object v5, p0, Lcom/nuance/swype/input/settings/ChinesePrefs$6;->this$0:Lcom/nuance/swype/input/settings/ChinesePrefs;

    invoke-static {v5, v1}, Lcom/nuance/swype/input/settings/ChinesePrefs;->access$1000(Lcom/nuance/swype/input/settings/ChinesePrefs;Ljava/lang/String;)Ljava/lang/String;

    move-result-object v3

    .line 420
    .local v3, "name":Ljava/lang/String;
    if-eqz v3, :cond_1

    .line 421
    invoke-virtual {v3}, Ljava/lang/String;->trim()Ljava/lang/String;

    move-result-object v3

    .line 426
    iget-object v5, p0, Lcom/nuance/swype/input/settings/ChinesePrefs$6;->this$0:Lcom/nuance/swype/input/settings/ChinesePrefs;

    invoke-static {v5}, Lcom/nuance/swype/input/settings/ChinesePrefs;->access$600(Lcom/nuance/swype/input/settings/ChinesePrefs;)Ljava/util/List;

    move-result-object v5

    invoke-static {v3}, Ljava/lang/String;->valueOf(Ljava/lang/Object;)Ljava/lang/String;

    move-result-object v6

    invoke-interface {v5, v6}, Ljava/util/List;->add(Ljava/lang/Object;)Z

    .line 427
    iget-object v5, p0, Lcom/nuance/swype/input/settings/ChinesePrefs$6;->this$0:Lcom/nuance/swype/input/settings/ChinesePrefs;

    invoke-static {v5, v1}, Lcom/nuance/swype/input/settings/ChinesePrefs;->access$1100(Lcom/nuance/swype/input/settings/ChinesePrefs;Ljava/lang/String;)Ljava/lang/String;

    move-result-object v2

    .line 428
    .local v2, "description":Ljava/lang/String;
    if-eqz v2, :cond_3

    .line 429
    invoke-virtual {v2}, Ljava/lang/String;->trim()Ljava/lang/String;

    move-result-object v2

    .line 431
    :cond_3
    iget-object v5, p0, Lcom/nuance/swype/input/settings/ChinesePrefs$6;->this$0:Lcom/nuance/swype/input/settings/ChinesePrefs;

    invoke-static {v5}, Lcom/nuance/swype/input/settings/ChinesePrefs;->access$700(Lcom/nuance/swype/input/settings/ChinesePrefs;)Ljava/util/List;

    move-result-object v5

    invoke-static {v2}, Ljava/lang/String;->valueOf(Ljava/lang/Object;)Ljava/lang/String;

    move-result-object v6

    invoke-interface {v5, v6}, Ljava/util/List;->add(Ljava/lang/Object;)Z

    goto :goto_0

    .line 433
    .end local v0    # "cdb":Ljava/lang/String;
    .end local v1    # "cdbName":Ljava/lang/String;
    .end local v2    # "description":Ljava/lang/String;
    .end local v3    # "name":Ljava/lang/String;
    :cond_4
    iget-object v4, p0, Lcom/nuance/swype/input/settings/ChinesePrefs$6;->this$0:Lcom/nuance/swype/input/settings/ChinesePrefs;

    iget-object v5, p0, Lcom/nuance/swype/input/settings/ChinesePrefs$6;->this$0:Lcom/nuance/swype/input/settings/ChinesePrefs;

    invoke-static {v5}, Lcom/nuance/swype/input/settings/ChinesePrefs;->access$500(Lcom/nuance/swype/input/settings/ChinesePrefs;)Ljava/util/List;

    move-result-object v5

    iget-object v6, p0, Lcom/nuance/swype/input/settings/ChinesePrefs$6;->this$0:Lcom/nuance/swype/input/settings/ChinesePrefs;

    invoke-static {v6}, Lcom/nuance/swype/input/settings/ChinesePrefs;->access$600(Lcom/nuance/swype/input/settings/ChinesePrefs;)Ljava/util/List;

    move-result-object v6

    iget-object v7, p0, Lcom/nuance/swype/input/settings/ChinesePrefs$6;->this$0:Lcom/nuance/swype/input/settings/ChinesePrefs;

    invoke-static {v7}, Lcom/nuance/swype/input/settings/ChinesePrefs;->access$700(Lcom/nuance/swype/input/settings/ChinesePrefs;)Ljava/util/List;

    move-result-object v7

    invoke-static {v4, v5, v6, v7}, Lcom/nuance/swype/input/settings/ChinesePrefs;->access$1200(Lcom/nuance/swype/input/settings/ChinesePrefs;Ljava/util/List;Ljava/util/List;Ljava/util/List;)V

    .line 435
    :cond_5
    return-void
.end method
