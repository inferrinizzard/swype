.class Lcom/localytics/android/MarketingWebViewManager$1;
.super Lcom/localytics/android/MarketingCallable;
.source "MarketingWebViewManager.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Lcom/localytics/android/MarketingWebViewManager;->getJavaScriptCallbacks(Ljava/util/Map;)Landroid/util/SparseArray;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Lcom/localytics/android/MarketingWebViewManager;

.field final synthetic val$JS_STRINGS_THAT_MEAN_NULL:Ljava/util/List;


# direct methods
.method constructor <init>(Lcom/localytics/android/MarketingWebViewManager;Ljava/util/List;)V
    .locals 0

    .prologue
    .line 453
    iput-object p1, p0, Lcom/localytics/android/MarketingWebViewManager$1;->this$0:Lcom/localytics/android/MarketingWebViewManager;

    iput-object p2, p0, Lcom/localytics/android/MarketingWebViewManager$1;->val$JS_STRINGS_THAT_MEAN_NULL:Ljava/util/List;

    invoke-direct {p0}, Lcom/localytics/android/MarketingCallable;-><init>()V

    return-void
.end method


# virtual methods
.method call([Ljava/lang/Object;)Ljava/lang/Object;
    .locals 18
    .param p1, "params"    # [Ljava/lang/Object;

    .prologue
    .line 459
    const/4 v14, 0x0

    :try_start_0
    aget-object v8, p1, v14

    check-cast v8, Ljava/lang/String;

    .line 460
    .local v8, "event":Ljava/lang/String;
    const/4 v14, 0x1

    aget-object v2, p1, v14

    check-cast v2, Ljava/lang/String;

    .line 461
    .local v2, "attributes":Ljava/lang/String;
    const/4 v14, 0x2

    aget-object v12, p1, v14

    check-cast v12, Ljava/lang/String;

    .line 462
    .local v12, "thirdParam":Ljava/lang/String;
    const-wide/16 v4, 0x0

    .line 464
    .local v4, "customerValueIncrease":J
    new-instance v11, Ljava/util/HashMap;

    invoke-direct {v11}, Ljava/util/HashMap;-><init>()V

    .line 466
    .local v11, "nativeAttributes":Ljava/util/Map;, "Ljava/util/Map<Ljava/lang/String;Ljava/lang/String;>;"
    invoke-static {v8}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    move-result v14

    if-eqz v14, :cond_0

    .line 468
    const-string/jumbo v14, "event cannot be null or empty"

    invoke-static {v14}, Lcom/localytics/android/Localytics$Log;->e(Ljava/lang/String;)I

    .line 471
    :cond_0
    invoke-static {v2}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    move-result v14

    if-nez v14, :cond_1

    move-object/from16 v0, p0

    iget-object v14, v0, Lcom/localytics/android/MarketingWebViewManager$1;->val$JS_STRINGS_THAT_MEAN_NULL:Ljava/util/List;

    invoke-interface {v14, v2}, Ljava/util/List;->contains(Ljava/lang/Object;)Z

    move-result v14

    if-nez v14, :cond_1

    .line 473
    new-instance v14, Lorg/json/JSONObject;

    invoke-direct {v14, v2}, Lorg/json/JSONObject;-><init>(Ljava/lang/String;)V

    invoke-static {v14}, Lcom/localytics/android/JsonHelper;->toMap(Lorg/json/JSONObject;)Ljava/util/Map;

    move-result-object v14

    .line 474
    invoke-interface {v14}, Ljava/util/Map;->entrySet()Ljava/util/Set;

    move-result-object v14

    invoke-interface {v14}, Ljava/util/Set;->iterator()Ljava/util/Iterator;

    move-result-object v9

    .local v9, "i$":Ljava/util/Iterator;
    :goto_0
    invoke-interface {v9}, Ljava/util/Iterator;->hasNext()Z

    move-result v14

    if-eqz v14, :cond_1

    invoke-interface {v9}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v6

    check-cast v6, Ljava/util/Map$Entry;

    .line 476
    .local v6, "entry":Ljava/util/Map$Entry;, "Ljava/util/Map$Entry<Ljava/lang/String;Ljava/lang/Object;>;"
    invoke-interface {v6}, Ljava/util/Map$Entry;->getKey()Ljava/lang/Object;

    move-result-object v14

    invoke-interface {v6}, Ljava/util/Map$Entry;->getValue()Ljava/lang/Object;

    move-result-object v15

    invoke-static {v15}, Ljava/lang/String;->valueOf(Ljava/lang/Object;)Ljava/lang/String;

    move-result-object v15

    invoke-interface {v11, v14, v15}, Ljava/util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    goto :goto_0

    .line 540
    .end local v2    # "attributes":Ljava/lang/String;
    .end local v4    # "customerValueIncrease":J
    .end local v6    # "entry":Ljava/util/Map$Entry;, "Ljava/util/Map$Entry<Ljava/lang/String;Ljava/lang/Object;>;"
    .end local v8    # "event":Ljava/lang/String;
    .end local v9    # "i$":Ljava/util/Iterator;
    .end local v11    # "nativeAttributes":Ljava/util/Map;, "Ljava/util/Map<Ljava/lang/String;Ljava/lang/String;>;"
    .end local v12    # "thirdParam":Ljava/lang/String;
    :catch_0
    move-exception v3

    .line 542
    .local v3, "e":Ljava/lang/Exception;
    const-string/jumbo v14, "MarketingCallable ON_MARKETING_JS_TAG_EVENT exception"

    invoke-static {v14, v3}, Lcom/localytics/android/Localytics$Log;->e(Ljava/lang/String;Ljava/lang/Throwable;)I

    .line 545
    .end local v3    # "e":Ljava/lang/Exception;
    :goto_1
    const/4 v14, 0x0

    return-object v14

    .line 482
    .restart local v2    # "attributes":Ljava/lang/String;
    .restart local v4    # "customerValueIncrease":J
    .restart local v8    # "event":Ljava/lang/String;
    .restart local v11    # "nativeAttributes":Ljava/util/Map;, "Ljava/util/Map<Ljava/lang/String;Ljava/lang/String;>;"
    .restart local v12    # "thirdParam":Ljava/lang/String;
    :cond_1
    :try_start_1
    invoke-static {v12}, Ljava/lang/Long;->valueOf(Ljava/lang/String;)Ljava/lang/Long;

    move-result-object v14

    invoke-virtual {v14}, Ljava/lang/Long;->longValue()J
    :try_end_1
    .catch Ljava/lang/NumberFormatException; {:try_start_1 .. :try_end_1} :catch_1
    .catch Ljava/lang/Exception; {:try_start_1 .. :try_end_1} :catch_0

    move-result-wide v4

    .line 505
    :cond_2
    :goto_2
    if-eqz v2, :cond_7

    .line 511
    :try_start_2
    invoke-interface {v11}, Ljava/util/Map;->isEmpty()Z

    move-result v14

    if-eqz v14, :cond_3

    .line 513
    const-string/jumbo v14, "attributes is empty.  Did the caller make an error?"

    invoke-static {v14}, Lcom/localytics/android/Localytics$Log;->w(Ljava/lang/String;)I

    .line 516
    :cond_3
    invoke-interface {v11}, Ljava/util/Map;->size()I

    move-result v14

    const/16 v15, 0x32

    if-le v14, v15, :cond_4

    .line 518
    const-string/jumbo v14, "attributes size is %d, exceeding the maximum size of %d.  Did the caller make an error?"

    const/4 v15, 0x2

    new-array v15, v15, [Ljava/lang/Object;

    const/16 v16, 0x0

    invoke-interface {v11}, Ljava/util/Map;->size()I

    move-result v17

    invoke-static/range {v17 .. v17}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v17

    aput-object v17, v15, v16

    const/16 v16, 0x1

    const/16 v17, 0x32

    invoke-static/range {v17 .. v17}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v17

    aput-object v17, v15, v16

    invoke-static {v14, v15}, Ljava/lang/String;->format(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;

    move-result-object v14

    invoke-static {v14}, Lcom/localytics/android/Localytics$Log;->w(Ljava/lang/String;)I

    .line 521
    :cond_4
    invoke-interface {v11}, Ljava/util/Map;->entrySet()Ljava/util/Set;

    move-result-object v14

    invoke-interface {v14}, Ljava/util/Set;->iterator()Ljava/util/Iterator;

    move-result-object v9

    .restart local v9    # "i$":Ljava/util/Iterator;
    :cond_5
    :goto_3
    invoke-interface {v9}, Ljava/util/Iterator;->hasNext()Z

    move-result v14

    if-eqz v14, :cond_7

    invoke-interface {v9}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v7

    check-cast v7, Ljava/util/Map$Entry;

    .line 523
    .local v7, "entry":Ljava/util/Map$Entry;, "Ljava/util/Map$Entry<Ljava/lang/String;Ljava/lang/String;>;"
    invoke-interface {v7}, Ljava/util/Map$Entry;->getKey()Ljava/lang/Object;

    move-result-object v10

    check-cast v10, Ljava/lang/String;

    .line 524
    .local v10, "key":Ljava/lang/String;
    invoke-interface {v7}, Ljava/util/Map$Entry;->getValue()Ljava/lang/Object;

    move-result-object v14

    invoke-static {v14}, Ljava/lang/String;->valueOf(Ljava/lang/Object;)Ljava/lang/String;

    move-result-object v13

    .line 526
    .local v13, "value":Ljava/lang/String;
    invoke-static {v10}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    move-result v14

    if-eqz v14, :cond_6

    .line 528
    const-string/jumbo v14, "attributes cannot contain null or empty keys"

    invoke-static {v14}, Lcom/localytics/android/Localytics$Log;->e(Ljava/lang/String;)I

    .line 531
    :cond_6
    invoke-static {v13}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    move-result v14

    if-eqz v14, :cond_5

    .line 533
    const-string/jumbo v14, "attributes cannot contain null or empty values"

    invoke-static {v14}, Lcom/localytics/android/Localytics$Log;->e(Ljava/lang/String;)I
    :try_end_2
    .catch Ljava/lang/Exception; {:try_start_2 .. :try_end_2} :catch_0

    goto :goto_3

    .end local v7    # "entry":Ljava/util/Map$Entry;, "Ljava/util/Map$Entry<Ljava/lang/String;Ljava/lang/String;>;"
    .end local v9    # "i$":Ljava/util/Iterator;
    .end local v10    # "key":Ljava/lang/String;
    .end local v13    # "value":Ljava/lang/String;
    :catch_1
    move-exception v14

    .line 490
    :try_start_3
    invoke-static {v12}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    move-result v14

    if-nez v14, :cond_2

    move-object/from16 v0, p0

    iget-object v14, v0, Lcom/localytics/android/MarketingWebViewManager$1;->val$JS_STRINGS_THAT_MEAN_NULL:Ljava/util/List;

    invoke-interface {v14, v12}, Ljava/util/List;->contains(Ljava/lang/Object;)Z

    move-result v14

    if-nez v14, :cond_2

    .line 492
    new-instance v14, Lorg/json/JSONObject;

    invoke-direct {v14, v12}, Lorg/json/JSONObject;-><init>(Ljava/lang/String;)V

    invoke-static {v14}, Lcom/localytics/android/JsonHelper;->toMap(Lorg/json/JSONObject;)Ljava/util/Map;

    move-result-object v14

    .line 493
    invoke-interface {v14}, Ljava/util/Map;->entrySet()Ljava/util/Set;

    move-result-object v14

    invoke-interface {v14}, Ljava/util/Set;->iterator()Ljava/util/Iterator;

    move-result-object v9

    .restart local v9    # "i$":Ljava/util/Iterator;
    :goto_4
    invoke-interface {v9}, Ljava/util/Iterator;->hasNext()Z

    move-result v14

    if-eqz v14, :cond_2

    invoke-interface {v9}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v6

    check-cast v6, Ljava/util/Map$Entry;

    .line 495
    .restart local v6    # "entry":Ljava/util/Map$Entry;, "Ljava/util/Map$Entry<Ljava/lang/String;Ljava/lang/Object;>;"
    invoke-interface {v6}, Ljava/util/Map$Entry;->getKey()Ljava/lang/Object;

    move-result-object v14

    invoke-interface {v6}, Ljava/util/Map$Entry;->getValue()Ljava/lang/Object;

    move-result-object v15

    invoke-static {v15}, Ljava/lang/String;->valueOf(Ljava/lang/Object;)Ljava/lang/String;

    move-result-object v15

    invoke-interface {v11, v14, v15}, Ljava/util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    :try_end_3
    .catch Lorg/json/JSONException; {:try_start_3 .. :try_end_3} :catch_2
    .catch Ljava/lang/Exception; {:try_start_3 .. :try_end_3} :catch_0

    goto :goto_4

    .end local v6    # "entry":Ljava/util/Map$Entry;, "Ljava/util/Map$Entry<Ljava/lang/String;Ljava/lang/Object;>;"
    .end local v9    # "i$":Ljava/util/Iterator;
    :catch_2
    move-exception v14

    goto/16 :goto_2

    .line 538
    :cond_7
    :try_start_4
    move-object/from16 v0, p0

    iget-object v14, v0, Lcom/localytics/android/MarketingWebViewManager$1;->this$0:Lcom/localytics/android/MarketingWebViewManager;

    iget-object v14, v14, Lcom/localytics/android/MarketingWebViewManager;->mLocalyticsDao:Lcom/localytics/android/LocalyticsDao;

    invoke-interface {v14, v8, v11, v4, v5}, Lcom/localytics/android/LocalyticsDao;->tagEvent(Ljava/lang/String;Ljava/util/Map;J)V
    :try_end_4
    .catch Ljava/lang/Exception; {:try_start_4 .. :try_end_4} :catch_0

    goto/16 :goto_1
.end method
