.class public interface abstract Lcom/nuance/swypeconnect/ac/ACPlatformUpdateService$ACPlatformUpdateDownloadCallback;
.super Ljava/lang/Object;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/nuance/swypeconnect/ac/ACPlatformUpdateService;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x609
    name = "ACPlatformUpdateDownloadCallback"
.end annotation


# virtual methods
.method public abstract downloadComplete(Ljava/io/File;)V
.end method

.method public abstract downloadFailed(I)V
.end method

.method public abstract downloadPercentage(I)V
.end method

.method public abstract downloadStarted()V
.end method

.method public abstract downloadStopped(I)V
.end method
