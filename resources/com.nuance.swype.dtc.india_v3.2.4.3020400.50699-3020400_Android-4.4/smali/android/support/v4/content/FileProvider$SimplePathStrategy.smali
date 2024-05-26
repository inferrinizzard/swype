.class final Landroid/support/v4/content/FileProvider$SimplePathStrategy;
.super Ljava/lang/Object;
.source "FileProvider.java"

# interfaces
.implements Landroid/support/v4/content/FileProvider$PathStrategy;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v4/content/FileProvider;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x8
    name = "SimplePathStrategy"
.end annotation


# instance fields
.field private final mAuthority:Ljava/lang/String;

.field final mRoots:Ljava/util/HashMap;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/HashMap",
            "<",
            "Ljava/lang/String;",
            "Ljava/io/File;",
            ">;"
        }
    .end annotation
.end field


# direct methods
.method public constructor <init>(Ljava/lang/String;)V
    .locals 1
    .param p1, "authority"    # Ljava/lang/String;

    .prologue
    .line 635
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 633
    new-instance v0, Ljava/util/HashMap;

    invoke-direct {v0}, Ljava/util/HashMap;-><init>()V

    iput-object v0, p0, Landroid/support/v4/content/FileProvider$SimplePathStrategy;->mRoots:Ljava/util/HashMap;

    .line 636
    iput-object p1, p0, Landroid/support/v4/content/FileProvider$SimplePathStrategy;->mAuthority:Ljava/lang/String;

    .line 637
    return-void
.end method


# virtual methods
.method public final getFileForUri(Landroid/net/Uri;)Ljava/io/File;
    .locals 8
    .param p1, "uri"    # Landroid/net/Uri;

    .prologue
    const/4 v6, 0x1

    .line 699
    invoke-virtual {p1}, Landroid/net/Uri;->getEncodedPath()Ljava/lang/String;

    move-result-object v1

    .line 701
    .local v1, "path":Ljava/lang/String;
    const/16 v5, 0x2f

    invoke-virtual {v1, v5, v6}, Ljava/lang/String;->indexOf(II)I

    move-result v3

    .line 702
    .local v3, "splitIndex":I
    invoke-virtual {v1, v6, v3}, Ljava/lang/String;->substring(II)Ljava/lang/String;

    move-result-object v5

    invoke-static {v5}, Landroid/net/Uri;->decode(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v4

    .line 703
    .local v4, "tag":Ljava/lang/String;
    add-int/lit8 v5, v3, 0x1

    invoke-virtual {v1, v5}, Ljava/lang/String;->substring(I)Ljava/lang/String;

    move-result-object v5

    invoke-static {v5}, Landroid/net/Uri;->decode(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v1

    .line 705
    iget-object v5, p0, Landroid/support/v4/content/FileProvider$SimplePathStrategy;->mRoots:Ljava/util/HashMap;

    invoke-virtual {v5, v4}, Ljava/util/HashMap;->get(Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v2

    check-cast v2, Ljava/io/File;

    .line 706
    .local v2, "root":Ljava/io/File;
    if-nez v2, :cond_0

    .line 707
    new-instance v5, Ljava/lang/IllegalArgumentException;

    new-instance v6, Ljava/lang/StringBuilder;

    const-string/jumbo v7, "Unable to find configured root for "

    invoke-direct {v6, v7}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    invoke-virtual {v6, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    move-result-object v6

    invoke-virtual {v6}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v6

    invoke-direct {v5, v6}, Ljava/lang/IllegalArgumentException;-><init>(Ljava/lang/String;)V

    throw v5

    .line 710
    :cond_0
    new-instance v0, Ljava/io/File;

    invoke-direct {v0, v2, v1}, Ljava/io/File;-><init>(Ljava/io/File;Ljava/lang/String;)V

    .line 712
    .local v0, "file":Ljava/io/File;
    :try_start_0
    invoke-virtual {v0}, Ljava/io/File;->getCanonicalFile()Ljava/io/File;
    :try_end_0
    .catch Ljava/io/IOException; {:try_start_0 .. :try_end_0} :catch_0

    move-result-object v0

    .line 717
    invoke-virtual {v0}, Ljava/io/File;->getPath()Ljava/lang/String;

    move-result-object v5

    invoke-virtual {v2}, Ljava/io/File;->getPath()Ljava/lang/String;

    move-result-object v6

    invoke-virtual {v5, v6}, Ljava/lang/String;->startsWith(Ljava/lang/String;)Z

    move-result v5

    if-nez v5, :cond_1

    .line 718
    new-instance v5, Ljava/lang/SecurityException;

    const-string/jumbo v6, "Resolved path jumped beyond configured root"

    invoke-direct {v5, v6}, Ljava/lang/SecurityException;-><init>(Ljava/lang/String;)V

    throw v5

    .line 714
    :catch_0
    move-exception v5

    new-instance v5, Ljava/lang/IllegalArgumentException;

    new-instance v6, Ljava/lang/StringBuilder;

    const-string/jumbo v7, "Failed to resolve canonical path for "

    invoke-direct {v6, v7}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    invoke-virtual {v6, v0}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    move-result-object v6

    invoke-virtual {v6}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v6

    invoke-direct {v5, v6}, Ljava/lang/IllegalArgumentException;-><init>(Ljava/lang/String;)V

    throw v5

    .line 721
    :cond_1
    return-object v0
.end method
