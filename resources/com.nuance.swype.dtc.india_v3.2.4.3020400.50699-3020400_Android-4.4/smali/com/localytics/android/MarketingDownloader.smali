.class final Lcom/localytics/android/MarketingDownloader;
.super Lcom/localytics/android/BaseUploadThread;
.source "MarketingDownloader.java"


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Lcom/localytics/android/MarketingDownloader$1;
    }
.end annotation


# static fields
.field private static final MANIFEST_HOST_PATH_FORMAT:Ljava/lang/String; = "%s/api/v4/applications/%s"

.field private static final MARKETING_HOST_PATH_FORMAT:Ljava/lang/String; = "%s/api/v2/applications/%s/amp"


# instance fields
.field private mUploadType:Lcom/localytics/android/BaseUploadThread$UploadType;


# direct methods
.method constructor <init>(Lcom/localytics/android/BaseUploadThread$UploadType;Lcom/localytics/android/BaseHandler;Ljava/util/TreeMap;Ljava/lang/String;Lcom/localytics/android/LocalyticsDao;)V
    .locals 0
    .param p1, "uploadType"    # Lcom/localytics/android/BaseUploadThread$UploadType;
    .param p2, "sessionHandler"    # Lcom/localytics/android/BaseHandler;
    .param p4, "customerId"    # Ljava/lang/String;
    .param p5, "localyticsDao"    # Lcom/localytics/android/LocalyticsDao;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Lcom/localytics/android/BaseUploadThread$UploadType;",
            "Lcom/localytics/android/BaseHandler;",
            "Ljava/util/TreeMap",
            "<",
            "Ljava/lang/Integer;",
            "Ljava/lang/Object;",
            ">;",
            "Ljava/lang/String;",
            "Lcom/localytics/android/LocalyticsDao;",
            ")V"
        }
    .end annotation

    .prologue
    .line 35
    .local p3, "data":Ljava/util/TreeMap;, "Ljava/util/TreeMap<Ljava/lang/Integer;Ljava/lang/Object;>;"
    invoke-direct {p0, p2, p3, p4, p5}, Lcom/localytics/android/BaseUploadThread;-><init>(Lcom/localytics/android/BaseHandler;Ljava/util/TreeMap;Ljava/lang/String;Lcom/localytics/android/LocalyticsDao;)V

    .line 36
    iput-object p1, p0, Lcom/localytics/android/MarketingDownloader;->mUploadType:Lcom/localytics/android/BaseUploadThread$UploadType;

    .line 37
    return-void
.end method

.method static request(Ljava/lang/String;)Ljava/lang/String;
    .locals 9
    .param p0, "urlString"    # Ljava/lang/String;
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/io/IOException;
        }
    .end annotation

    .prologue
    .line 60
    new-instance v7, Ljava/net/URL;

    invoke-direct {v7, p0}, Ljava/net/URL;-><init>(Ljava/lang/String;)V

    .line 61
    invoke-virtual {v7}, Ljava/net/URL;->openConnection()Ljava/net/URLConnection;

    move-result-object v3

    check-cast v3, Ljava/net/HttpURLConnection;

    .line 62
    .local v3, "connection":Ljava/net/HttpURLConnection;
    const-string/jumbo v7, "Accept-Encoding"

    const-string/jumbo v8, "gzip"

    invoke-virtual {v3, v7, v8}, Ljava/net/HttpURLConnection;->setRequestProperty(Ljava/lang/String;Ljava/lang/String;)V

    .line 64
    const/4 v0, 0x0

    .line 68
    .local v0, "bufferedReader":Ljava/io/BufferedReader;
    :try_start_0
    const-string/jumbo v7, "gzip"

    invoke-virtual {v3}, Ljava/net/HttpURLConnection;->getContentEncoding()Ljava/lang/String;

    move-result-object v8

    invoke-virtual {v7, v8}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v7

    if-eqz v7, :cond_1

    .line 70
    new-instance v4, Ljava/util/zip/GZIPInputStream;

    invoke-virtual {v3}, Ljava/net/HttpURLConnection;->getInputStream()Ljava/io/InputStream;

    move-result-object v7

    invoke-direct {v4, v7}, Ljava/util/zip/GZIPInputStream;-><init>(Ljava/io/InputStream;)V

    .line 71
    .local v4, "gzipInputStream":Ljava/util/zip/GZIPInputStream;
    new-instance v6, Ljava/io/InputStreamReader;

    const-string/jumbo v7, "UTF-8"

    invoke-direct {v6, v4, v7}, Ljava/io/InputStreamReader;-><init>(Ljava/io/InputStream;Ljava/lang/String;)V

    .line 78
    .end local v4    # "gzipInputStream":Ljava/util/zip/GZIPInputStream;
    .local v6, "reader":Ljava/io/InputStreamReader;
    :goto_0
    new-instance v1, Ljava/io/BufferedReader;

    invoke-direct {v1, v6}, Ljava/io/BufferedReader;-><init>(Ljava/io/Reader;)V
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_1

    .line 79
    .end local v0    # "bufferedReader":Ljava/io/BufferedReader;
    .local v1, "bufferedReader":Ljava/io/BufferedReader;
    :try_start_1
    new-instance v2, Ljava/lang/StringBuilder;

    invoke-direct {v2}, Ljava/lang/StringBuilder;-><init>()V

    .line 81
    .local v2, "builder":Ljava/lang/StringBuilder;
    :goto_1
    invoke-virtual {v1}, Ljava/io/BufferedReader;->readLine()Ljava/lang/String;

    move-result-object v5

    .local v5, "line":Ljava/lang/String;
    if-eqz v5, :cond_2

    .line 83
    invoke-virtual {v2, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;
    :try_end_1
    .catchall {:try_start_1 .. :try_end_1} :catchall_0

    goto :goto_1

    .line 90
    .end local v2    # "builder":Ljava/lang/StringBuilder;
    .end local v5    # "line":Ljava/lang/String;
    :catchall_0
    move-exception v7

    move-object v0, v1

    .line 92
    .end local v1    # "bufferedReader":Ljava/io/BufferedReader;
    .end local v6    # "reader":Ljava/io/InputStreamReader;
    .restart local v0    # "bufferedReader":Ljava/io/BufferedReader;
    :goto_2
    if-eqz v0, :cond_0

    .line 94
    :try_start_2
    invoke-virtual {v0}, Ljava/io/BufferedReader;->close()V
    :try_end_2
    .catch Ljava/io/IOException; {:try_start_2 .. :try_end_2} :catch_1

    .line 100
    :cond_0
    :goto_3
    throw v7

    .line 75
    :cond_1
    :try_start_3
    new-instance v6, Ljava/io/InputStreamReader;

    invoke-virtual {v3}, Ljava/net/HttpURLConnection;->getInputStream()Ljava/io/InputStream;

    move-result-object v7

    invoke-direct {v6, v7}, Ljava/io/InputStreamReader;-><init>(Ljava/io/InputStream;)V
    :try_end_3
    .catchall {:try_start_3 .. :try_end_3} :catchall_1

    .restart local v6    # "reader":Ljava/io/InputStreamReader;
    goto :goto_0

    .line 86
    .end local v0    # "bufferedReader":Ljava/io/BufferedReader;
    .restart local v1    # "bufferedReader":Ljava/io/BufferedReader;
    .restart local v2    # "builder":Ljava/lang/StringBuilder;
    .restart local v5    # "line":Ljava/lang/String;
    :cond_2
    :try_start_4
    invoke-virtual {v2}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;
    :try_end_4
    .catchall {:try_start_4 .. :try_end_4} :catchall_0

    move-result-object v7

    .line 94
    :try_start_5
    invoke-virtual {v1}, Ljava/io/BufferedReader;->close()V
    :try_end_5
    .catch Ljava/io/IOException; {:try_start_5 .. :try_end_5} :catch_0

    .line 100
    :goto_4
    return-object v7

    :catch_0
    move-exception v8

    goto :goto_4

    .end local v1    # "bufferedReader":Ljava/io/BufferedReader;
    .end local v2    # "builder":Ljava/lang/StringBuilder;
    .end local v5    # "line":Ljava/lang/String;
    .end local v6    # "reader":Ljava/io/InputStreamReader;
    .restart local v0    # "bufferedReader":Ljava/io/BufferedReader;
    :catch_1
    move-exception v8

    goto :goto_3

    .line 90
    :catchall_1
    move-exception v7

    goto :goto_2
.end method


# virtual methods
.method final uploadData()I
    .locals 8

    .prologue
    const/4 v5, 0x2

    const/4 v7, 0x1

    const/4 v6, 0x0

    .line 42
    invoke-virtual {p0}, Lcom/localytics/android/MarketingDownloader;->getApiKey()Ljava/lang/String;

    move-result-object v0

    .line 43
    .local v0, "apiKey":Ljava/lang/String;
    sget-object v3, Lcom/localytics/android/MarketingDownloader$1;->$SwitchMap$com$localytics$android$BaseUploadThread$UploadType:[I

    iget-object v4, p0, Lcom/localytics/android/MarketingDownloader;->mUploadType:Lcom/localytics/android/BaseUploadThread$UploadType;

    invoke-virtual {v4}, Lcom/localytics/android/BaseUploadThread$UploadType;->ordinal()I

    move-result v4

    aget v3, v3, v4

    packed-switch v3, :pswitch_data_0

    .line 54
    :goto_0
    return v7

    .line 46
    :pswitch_0
    new-instance v3, Ljava/lang/StringBuilder;

    invoke-direct {v3}, Ljava/lang/StringBuilder;-><init>()V

    invoke-static {}, Lcom/localytics/android/Constants;->getHTTPPreface()Ljava/lang/String;

    move-result-object v4

    invoke-virtual {v3, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v3

    const-string/jumbo v4, "%s/api/v2/applications/%s/amp"

    invoke-virtual {v3, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v3

    invoke-virtual {v3}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v3

    new-array v4, v5, [Ljava/lang/Object;

    sget-object v5, Lcom/localytics/android/Constants;->MARKETING_HOST:Ljava/lang/String;

    aput-object v5, v4, v6

    aput-object v0, v4, v7

    invoke-static {v3, v4}, Ljava/lang/String;->format(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;

    move-result-object v2

    .line 47
    .local v2, "marketingUrl":Ljava/lang/String;
    sget-object v3, Lcom/localytics/android/BaseUploadThread$UploadType;->MARKETING:Lcom/localytics/android/BaseUploadThread$UploadType;

    const-string/jumbo v4, ""

    invoke-virtual {p0, v3, v2, v4, v6}, Lcom/localytics/android/MarketingDownloader;->upload(Lcom/localytics/android/BaseUploadThread$UploadType;Ljava/lang/String;Ljava/lang/String;I)Z

    goto :goto_0

    .line 50
    .end local v2    # "marketingUrl":Ljava/lang/String;
    :pswitch_1
    new-instance v3, Ljava/lang/StringBuilder;

    invoke-direct {v3}, Ljava/lang/StringBuilder;-><init>()V

    invoke-static {}, Lcom/localytics/android/Constants;->getHTTPPreface()Ljava/lang/String;

    move-result-object v4

    invoke-virtual {v3, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v3

    const-string/jumbo v4, "%s/api/v4/applications/%s"

    invoke-virtual {v3, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v3

    invoke-virtual {v3}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v3

    new-array v4, v5, [Ljava/lang/Object;

    sget-object v5, Lcom/localytics/android/Constants;->MANIFEST_HOST:Ljava/lang/String;

    aput-object v5, v4, v6

    aput-object v0, v4, v7

    invoke-static {v3, v4}, Ljava/lang/String;->format(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;

    move-result-object v1

    .line 51
    .local v1, "manifestUrl":Ljava/lang/String;
    sget-object v3, Lcom/localytics/android/BaseUploadThread$UploadType;->MANIFEST:Lcom/localytics/android/BaseUploadThread$UploadType;

    const-string/jumbo v4, ""

    invoke-virtual {p0, v3, v1, v4, v6}, Lcom/localytics/android/MarketingDownloader;->upload(Lcom/localytics/android/BaseUploadThread$UploadType;Ljava/lang/String;Ljava/lang/String;I)Z

    goto :goto_0

    .line 43
    nop

    :pswitch_data_0
    .packed-switch 0x1
        :pswitch_0
        :pswitch_1
    .end packed-switch
.end method
