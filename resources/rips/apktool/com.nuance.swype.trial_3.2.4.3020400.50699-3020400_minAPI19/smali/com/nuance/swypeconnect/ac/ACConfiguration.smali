.class public final Lcom/nuance/swypeconnect/ac/ACConfiguration;
.super Ljava/lang/Object;


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Lcom/nuance/swypeconnect/ac/ACConfiguration$1;,
        Lcom/nuance/swypeconnect/ac/ACConfiguration$ConfigServiceWrapper;,
        Lcom/nuance/swypeconnect/ac/ACConfiguration$ACSSLProtocol;
    }
.end annotation


# static fields
.field public static final MAX_CONNECTIONS_LIMIT:I = 0xa

.field private static final log:Lcom/nuance/connect/util/Logger$Log;


# instance fields
.field private final config:Lcom/nuance/connect/api/ConfigService;

.field private final manager:Lcom/nuance/swypeconnect/ac/ACManager;


# direct methods
.method static constructor <clinit>()V
    .locals 1

    sget-object v0, Lcom/nuance/connect/util/Logger$LoggerType;->OEM:Lcom/nuance/connect/util/Logger$LoggerType;

    invoke-static {v0}, Lcom/nuance/connect/util/Logger;->getLog(Lcom/nuance/connect/util/Logger$LoggerType;)Lcom/nuance/connect/util/Logger$Log;

    move-result-object v0

    sput-object v0, Lcom/nuance/swypeconnect/ac/ACConfiguration;->log:Lcom/nuance/connect/util/Logger$Log;

    return-void
.end method

.method protected constructor <init>(Lcom/nuance/swypeconnect/ac/ACManager;Lcom/nuance/connect/store/PersistentDataStore;)V
    .locals 3

    const/4 v1, 0x0

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    new-instance v0, Lcom/nuance/swypeconnect/ac/ACConfiguration$ConfigServiceWrapper;

    invoke-direct {v0, p1, v1}, Lcom/nuance/swypeconnect/ac/ACConfiguration$ConfigServiceWrapper;-><init>(Lcom/nuance/swypeconnect/ac/ACManager;Lcom/nuance/swypeconnect/ac/ACConfiguration$1;)V

    iput-object v0, p0, Lcom/nuance/swypeconnect/ac/ACConfiguration;->config:Lcom/nuance/connect/api/ConfigService;

    iput-object p1, p0, Lcom/nuance/swypeconnect/ac/ACConfiguration;->manager:Lcom/nuance/swypeconnect/ac/ACManager;

    const/4 v0, 0x0

    :try_start_0
    invoke-virtual {p0, v0}, Lcom/nuance/swypeconnect/ac/ACConfiguration;->setMinimumSSLProtocol(Lcom/nuance/swypeconnect/ac/ACConfiguration$ACSSLProtocol;)V
    :try_end_0
    .catch Lcom/nuance/swypeconnect/ac/ACException; {:try_start_0 .. :try_end_0} :catch_0

    :goto_0
    return-void

    :catch_0
    move-exception v0

    sget-object v1, Lcom/nuance/swypeconnect/ac/ACConfiguration;->log:Lcom/nuance/connect/util/Logger$Log;

    new-instance v2, Ljava/lang/StringBuilder;

    invoke-direct {v2}, Ljava/lang/StringBuilder;-><init>()V

    invoke-virtual {v0}, Lcom/nuance/swypeconnect/ac/ACException;->getMessage()Ljava/lang/String;

    move-result-object v0

    invoke-virtual {v2, v0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v0

    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v0

    invoke-interface {v1, v0}, Lcom/nuance/connect/util/Logger$Log;->e(Ljava/lang/Object;)V

    goto :goto_0
.end method


# virtual methods
.method public final backgroundData(ZZZ)V
    .locals 3
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Lcom/nuance/swypeconnect/ac/ACException;
        }
    .end annotation

    if-nez p2, :cond_0

    if-eqz p3, :cond_0

    new-instance v0, Lcom/nuance/swypeconnect/ac/ACException;

    const/16 v1, 0x6d

    const-string/jumbo v2, "Roaming cannot be enabled when cellular is disabled"

    invoke-direct {v0, v1, v2}, Lcom/nuance/swypeconnect/ac/ACException;-><init>(ILjava/lang/String;)V

    throw v0

    :cond_0
    new-instance v0, Lcom/nuance/connect/system/NetworkState$NetworkConfiguration;

    invoke-direct {v0, p1, p2, p3}, Lcom/nuance/connect/system/NetworkState$NetworkConfiguration;-><init>(ZZZ)V

    iget-object v1, p0, Lcom/nuance/swypeconnect/ac/ACConfiguration;->config:Lcom/nuance/connect/api/ConfigService;

    invoke-interface {v1, v0}, Lcom/nuance/connect/api/ConfigService;->setBackgroundNetworkState(Lcom/nuance/connect/system/NetworkState$NetworkConfiguration;)V

    return-void
.end method

.method protected final customConfigurationNameCheck(Ljava/lang/String;)Z
    .locals 2

    if-eqz p1, :cond_0

    invoke-virtual {p1}, Ljava/lang/String;->length()I

    move-result v0

    if-eqz v0, :cond_0

    invoke-virtual {p1}, Ljava/lang/String;->length()I

    move-result v0

    const/16 v1, 0x32

    if-gt v0, v1, :cond_0

    const-string/jumbo v0, ","

    invoke-virtual {p1, v0}, Ljava/lang/String;->contains(Ljava/lang/CharSequence;)Z

    move-result v0

    if-nez v0, :cond_0

    const-string/jumbo v0, ":"

    invoke-virtual {p1, v0}, Ljava/lang/String;->contains(Ljava/lang/CharSequence;)Z

    move-result v0

    if-eqz v0, :cond_1

    :cond_0
    const/4 v0, 0x0

    :goto_0
    return v0

    :cond_1
    const/4 v0, 0x1

    goto :goto_0
.end method

.method public final deleteConfigurationProperty(Ljava/lang/String;)V
    .locals 2
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Lcom/nuance/swypeconnect/ac/ACException;
        }
    .end annotation

    :try_start_0
    iget-object v0, p0, Lcom/nuance/swypeconnect/ac/ACConfiguration;->config:Lcom/nuance/connect/api/ConfigService;

    invoke-interface {v0, p1}, Lcom/nuance/connect/api/ConfigService;->deleteConfigurationProperty(Ljava/lang/String;)V
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    return-void

    :catch_0
    move-exception v0

    new-instance v0, Lcom/nuance/swypeconnect/ac/ACException;

    const/16 v1, 0x85

    invoke-direct {v0, v1}, Lcom/nuance/swypeconnect/ac/ACException;-><init>(I)V

    throw v0
.end method

.method public final enableEnhancedPassiveLocation()V
    .locals 0

    return-void
.end method

.method public final foregroundData(ZZZ)V
    .locals 3
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Lcom/nuance/swypeconnect/ac/ACException;
        }
    .end annotation

    if-nez p2, :cond_0

    if-eqz p3, :cond_0

    new-instance v0, Lcom/nuance/swypeconnect/ac/ACException;

    const/16 v1, 0x6d

    const-string/jumbo v2, "Roaming cannot be enabled when cellular is disabled"

    invoke-direct {v0, v1, v2}, Lcom/nuance/swypeconnect/ac/ACException;-><init>(ILjava/lang/String;)V

    throw v0

    :cond_0
    new-instance v0, Lcom/nuance/connect/system/NetworkState$NetworkConfiguration;

    invoke-direct {v0, p1, p2, p3}, Lcom/nuance/connect/system/NetworkState$NetworkConfiguration;-><init>(ZZZ)V

    iget-object v1, p0, Lcom/nuance/swypeconnect/ac/ACConfiguration;->config:Lcom/nuance/connect/api/ConfigService;

    invoke-interface {v1, v0}, Lcom/nuance/connect/api/ConfigService;->setForegroundNetworkState(Lcom/nuance/connect/system/NetworkState$NetworkConfiguration;)V

    return-void
.end method

.method public final getConcurrentConnectionLimit()I
    .locals 1

    iget-object v0, p0, Lcom/nuance/swypeconnect/ac/ACConfiguration;->config:Lcom/nuance/connect/api/ConfigService;

    invoke-interface {v0}, Lcom/nuance/connect/api/ConfigService;->getConnectionConcurrentLimit()I

    move-result v0

    return v0
.end method

.method public final getConfigurationPropertyBool(Ljava/lang/String;)Z
    .locals 2
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Lcom/nuance/swypeconnect/ac/ACException;
        }
    .end annotation

    :try_start_0
    iget-object v0, p0, Lcom/nuance/swypeconnect/ac/ACConfiguration;->config:Lcom/nuance/connect/api/ConfigService;

    invoke-interface {v0, p1}, Lcom/nuance/connect/api/ConfigService;->getConfigurationProperty(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v0

    invoke-static {v0}, Ljava/lang/Boolean;->valueOf(Ljava/lang/String;)Ljava/lang/Boolean;

    move-result-object v0

    invoke-virtual {v0}, Ljava/lang/Boolean;->booleanValue()Z
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    move-result v0

    return v0

    :catch_0
    move-exception v0

    new-instance v0, Lcom/nuance/swypeconnect/ac/ACException;

    const/16 v1, 0x85

    invoke-direct {v0, v1}, Lcom/nuance/swypeconnect/ac/ACException;-><init>(I)V

    throw v0
.end method

.method public final getConfigurationPropertyInt(Ljava/lang/String;)I
    .locals 2
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Lcom/nuance/swypeconnect/ac/ACException;
        }
    .end annotation

    :try_start_0
    iget-object v0, p0, Lcom/nuance/swypeconnect/ac/ACConfiguration;->config:Lcom/nuance/connect/api/ConfigService;

    invoke-interface {v0, p1}, Lcom/nuance/connect/api/ConfigService;->getConfigurationProperty(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v0

    invoke-static {v0}, Ljava/lang/Integer;->parseInt(Ljava/lang/String;)I
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    move-result v0

    return v0

    :catch_0
    move-exception v0

    new-instance v0, Lcom/nuance/swypeconnect/ac/ACException;

    const/16 v1, 0x85

    invoke-direct {v0, v1}, Lcom/nuance/swypeconnect/ac/ACException;-><init>(I)V

    throw v0
.end method

.method public final getConfigurationPropertyString(Ljava/lang/String;)Ljava/lang/String;
    .locals 2
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Lcom/nuance/swypeconnect/ac/ACException;
        }
    .end annotation

    :try_start_0
    iget-object v0, p0, Lcom/nuance/swypeconnect/ac/ACConfiguration;->config:Lcom/nuance/connect/api/ConfigService;

    invoke-interface {v0, p1}, Lcom/nuance/connect/api/ConfigService;->getConfigurationProperty(Ljava/lang/String;)Ljava/lang/String;
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    move-result-object v0

    return-object v0

    :catch_0
    move-exception v0

    new-instance v0, Lcom/nuance/swypeconnect/ac/ACException;

    const/16 v1, 0x85

    invoke-direct {v0, v1}, Lcom/nuance/swypeconnect/ac/ACException;-><init>(I)V

    throw v0
.end method

.method public final getCustomCloudConfiguration(Ljava/lang/String;)Ljava/lang/String;
    .locals 4
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Lcom/nuance/swypeconnect/ac/ACException;
        }
    .end annotation

    :try_start_0
    iget-object v0, p0, Lcom/nuance/swypeconnect/ac/ACConfiguration;->config:Lcom/nuance/connect/api/ConfigService;

    invoke-interface {v0, p1}, Lcom/nuance/connect/api/ConfigService;->getCustomExternalConfiguration(Ljava/lang/String;)Ljava/lang/String;
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    move-result-object v0

    return-object v0

    :catch_0
    move-exception v0

    new-instance v0, Lcom/nuance/swypeconnect/ac/ACException;

    const/16 v1, 0x85

    new-instance v2, Ljava/lang/StringBuilder;

    const-string/jumbo v3, "Cannot find a value for "

    invoke-direct {v2, v3}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    invoke-virtual {v2, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v2

    invoke-virtual {v2}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v2

    invoke-direct {v0, v1, v2}, Lcom/nuance/swypeconnect/ac/ACException;-><init>(ILjava/lang/String;)V

    throw v0
.end method

.method public final getCustomCloudConfigurationKeys()[Ljava/lang/String;
    .locals 1

    iget-object v0, p0, Lcom/nuance/swypeconnect/ac/ACConfiguration;->config:Lcom/nuance/connect/api/ConfigService;

    invoke-interface {v0}, Lcom/nuance/connect/api/ConfigService;->getCustomExternalConfigurationKeys()[Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public final getCustomerString()Ljava/lang/String;
    .locals 1

    iget-object v0, p0, Lcom/nuance/swypeconnect/ac/ACConfiguration;->config:Lcom/nuance/connect/api/ConfigService;

    invoke-interface {v0}, Lcom/nuance/connect/api/ConfigService;->getCustomerString()Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public final getDownloadIdleTimeout()I
    .locals 1

    iget-object v0, p0, Lcom/nuance/swypeconnect/ac/ACConfiguration;->config:Lcom/nuance/connect/api/ConfigService;

    invoke-interface {v0}, Lcom/nuance/connect/api/ConfigService;->getDownloadIdleTimeout()I

    move-result v0

    return v0
.end method

.method public final getRefreshInterval()I
    .locals 1

    iget-object v0, p0, Lcom/nuance/swypeconnect/ac/ACConfiguration;->config:Lcom/nuance/connect/api/ConfigService;

    invoke-interface {v0}, Lcom/nuance/connect/api/ConfigService;->getRefreshInterval()I

    move-result v0

    return v0
.end method

.method public final setConcurrentConnectionLimit(I)V
    .locals 3
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Lcom/nuance/swypeconnect/ac/ACException;
        }
    .end annotation

    const/16 v0, 0xa

    if-gt p1, v0, :cond_0

    if-gtz p1, :cond_1

    :cond_0
    new-instance v0, Lcom/nuance/swypeconnect/ac/ACException;

    const/16 v1, 0x71

    const-string/jumbo v2, "The max connection limit is 10. You may not set it below 0, or above the limit."

    invoke-direct {v0, v1, v2}, Lcom/nuance/swypeconnect/ac/ACException;-><init>(ILjava/lang/String;)V

    throw v0

    :cond_1
    iget-object v0, p0, Lcom/nuance/swypeconnect/ac/ACConfiguration;->config:Lcom/nuance/connect/api/ConfigService;

    invoke-interface {v0, p1}, Lcom/nuance/connect/api/ConfigService;->setConnectionConcurrentLimit(I)V

    return-void
.end method

.method public final setConfigurationProperty(Ljava/lang/String;IZ)V
    .locals 2
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Lcom/nuance/swypeconnect/ac/ACException;
        }
    .end annotation

    invoke-virtual {p0, p1}, Lcom/nuance/swypeconnect/ac/ACConfiguration;->customConfigurationNameCheck(Ljava/lang/String;)Z

    move-result v0

    if-eqz v0, :cond_0

    iget-object v0, p0, Lcom/nuance/swypeconnect/ac/ACConfiguration;->config:Lcom/nuance/connect/api/ConfigService;

    invoke-static {p2}, Ljava/lang/Integer;->toString(I)Ljava/lang/String;

    move-result-object v1

    invoke-interface {v0, p1, v1, p3}, Lcom/nuance/connect/api/ConfigService;->setConfigurationProperty(Ljava/lang/String;Ljava/lang/String;Z)V

    return-void

    :cond_0
    new-instance v0, Lcom/nuance/swypeconnect/ac/ACException;

    const/16 v1, 0x86

    invoke-direct {v0, v1}, Lcom/nuance/swypeconnect/ac/ACException;-><init>(I)V

    throw v0
.end method

.method public final setConfigurationProperty(Ljava/lang/String;Ljava/lang/String;Z)V
    .locals 2
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Lcom/nuance/swypeconnect/ac/ACException;
        }
    .end annotation

    invoke-virtual {p0, p1}, Lcom/nuance/swypeconnect/ac/ACConfiguration;->customConfigurationNameCheck(Ljava/lang/String;)Z

    move-result v0

    if-eqz v0, :cond_0

    iget-object v0, p0, Lcom/nuance/swypeconnect/ac/ACConfiguration;->config:Lcom/nuance/connect/api/ConfigService;

    invoke-interface {v0, p1, p2, p3}, Lcom/nuance/connect/api/ConfigService;->setConfigurationProperty(Ljava/lang/String;Ljava/lang/String;Z)V

    return-void

    :cond_0
    new-instance v0, Lcom/nuance/swypeconnect/ac/ACException;

    const/16 v1, 0x86

    invoke-direct {v0, v1}, Lcom/nuance/swypeconnect/ac/ACException;-><init>(I)V

    throw v0
.end method

.method public final setConfigurationProperty(Ljava/lang/String;ZZ)V
    .locals 2
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Lcom/nuance/swypeconnect/ac/ACException;
        }
    .end annotation

    invoke-virtual {p0, p1}, Lcom/nuance/swypeconnect/ac/ACConfiguration;->customConfigurationNameCheck(Ljava/lang/String;)Z

    move-result v0

    if-eqz v0, :cond_0

    iget-object v0, p0, Lcom/nuance/swypeconnect/ac/ACConfiguration;->config:Lcom/nuance/connect/api/ConfigService;

    invoke-static {p2}, Ljava/lang/Boolean;->toString(Z)Ljava/lang/String;

    move-result-object v1

    invoke-interface {v0, p1, v1, p3}, Lcom/nuance/connect/api/ConfigService;->setConfigurationProperty(Ljava/lang/String;Ljava/lang/String;Z)V

    return-void

    :cond_0
    new-instance v0, Lcom/nuance/swypeconnect/ac/ACException;

    const/16 v1, 0x86

    invoke-direct {v0, v1}, Lcom/nuance/swypeconnect/ac/ACException;-><init>(I)V

    throw v0
.end method

.method public final setCustomerString(Ljava/lang/String;)V
    .locals 1

    iget-object v0, p0, Lcom/nuance/swypeconnect/ac/ACConfiguration;->config:Lcom/nuance/connect/api/ConfigService;

    invoke-interface {v0, p1}, Lcom/nuance/connect/api/ConfigService;->setCustomerString(Ljava/lang/String;)V

    return-void
.end method

.method public final setDownloadIdleTimeout(I)V
    .locals 3
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Lcom/nuance/swypeconnect/ac/ACException;
        }
    .end annotation

    const/16 v0, 0xa

    if-ge p1, v0, :cond_0

    const/4 v0, -0x1

    if-eq p1, v0, :cond_0

    new-instance v0, Lcom/nuance/swypeconnect/ac/ACException;

    const/16 v1, 0x70

    const-string/jumbo v2, "The download idle timeout must be either -1 or greater than 10."

    invoke-direct {v0, v1, v2}, Lcom/nuance/swypeconnect/ac/ACException;-><init>(ILjava/lang/String;)V

    throw v0

    :cond_0
    iget-object v0, p0, Lcom/nuance/swypeconnect/ac/ACConfiguration;->config:Lcom/nuance/connect/api/ConfigService;

    invoke-interface {v0, p1}, Lcom/nuance/connect/api/ConfigService;->setDownloadIdleTimeout(I)V

    return-void
.end method

.method public final setLocationServiceAllowed(Z)V
    .locals 1
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Lcom/nuance/swypeconnect/ac/ACException;
        }
    .end annotation

    iget-object v0, p0, Lcom/nuance/swypeconnect/ac/ACConfiguration;->manager:Lcom/nuance/swypeconnect/ac/ACManager;

    invoke-virtual {v0, p1}, Lcom/nuance/swypeconnect/ac/ACManager;->setLocationServiceAllowed(Z)V

    return-void
.end method

.method public final setLogLevel(I)V
    .locals 1

    iget-object v0, p0, Lcom/nuance/swypeconnect/ac/ACConfiguration;->config:Lcom/nuance/connect/api/ConfigService;

    invoke-interface {v0, p1}, Lcom/nuance/connect/api/ConfigService;->setLogLevel(I)V

    return-void
.end method

.method public final setMinimumSSLProtocol(Lcom/nuance/swypeconnect/ac/ACConfiguration$ACSSLProtocol;)V
    .locals 5
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Lcom/nuance/swypeconnect/ac/ACException;
        }
    .end annotation

    const/16 v4, 0x6d

    iget-object v0, p0, Lcom/nuance/swypeconnect/ac/ACConfiguration;->manager:Lcom/nuance/swypeconnect/ac/ACManager;

    invoke-virtual {v0}, Lcom/nuance/swypeconnect/ac/ACManager;->isConnectStarted()Z

    move-result v0

    if-eqz v0, :cond_0

    new-instance v0, Lcom/nuance/swypeconnect/ac/ACException;

    const-string/jumbo v1, "Cannot set the protocol after ACManager.start() is called"

    invoke-direct {v0, v4, v1}, Lcom/nuance/swypeconnect/ac/ACException;-><init>(ILjava/lang/String;)V

    throw v0

    :cond_0
    if-nez p1, :cond_1

    :try_start_0
    sget-object p1, Lcom/nuance/swypeconnect/ac/ACConfiguration$ACSSLProtocol;->TLSv1_1:Lcom/nuance/swypeconnect/ac/ACConfiguration$ACSSLProtocol;

    :cond_1
    invoke-static {p1}, Lcom/nuance/swypeconnect/ac/ACConfiguration$ACSSLProtocol;->access$100(Lcom/nuance/swypeconnect/ac/ACConfiguration$ACSSLProtocol;)Ljava/lang/String;

    move-result-object v0

    invoke-static {v0}, Ljavax/net/ssl/SSLContext;->getInstance(Ljava/lang/String;)Ljavax/net/ssl/SSLContext;
    :try_end_0
    .catch Ljava/security/NoSuchAlgorithmException; {:try_start_0 .. :try_end_0} :catch_0

    iget-object v0, p0, Lcom/nuance/swypeconnect/ac/ACConfiguration;->config:Lcom/nuance/connect/api/ConfigService;

    invoke-static {p1}, Lcom/nuance/swypeconnect/ac/ACConfiguration$ACSSLProtocol;->access$100(Lcom/nuance/swypeconnect/ac/ACConfiguration$ACSSLProtocol;)Ljava/lang/String;

    move-result-object v1

    invoke-interface {v0, v1}, Lcom/nuance/connect/api/ConfigService;->setMinimumSSLProtocol(Ljava/lang/String;)V

    return-void

    :catch_0
    move-exception v0

    sget-object v1, Lcom/nuance/swypeconnect/ac/ACConfiguration;->log:Lcom/nuance/connect/util/Logger$Log;

    new-instance v2, Ljava/lang/StringBuilder;

    const-string/jumbo v3, "Failed to initialize SSL protocol "

    invoke-direct {v2, v3}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    invoke-virtual {v2, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    move-result-object v2

    invoke-virtual {v2}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v2

    invoke-interface {v1, v2}, Lcom/nuance/connect/util/Logger$Log;->e(Ljava/lang/Object;)V

    new-instance v1, Lcom/nuance/swypeconnect/ac/ACException;

    new-instance v2, Ljava/lang/StringBuilder;

    const-string/jumbo v3, "Failed to use SSL protocol: "

    invoke-direct {v2, v3}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    invoke-virtual {v0}, Ljava/security/NoSuchAlgorithmException;->getMessage()Ljava/lang/String;

    move-result-object v0

    invoke-virtual {v2, v0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v0

    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v0

    invoke-direct {v1, v4, v0}, Lcom/nuance/swypeconnect/ac/ACException;-><init>(ILjava/lang/String;)V

    throw v1
.end method

.method public final setRefreshInterval(I)V
    .locals 4
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Lcom/nuance/swypeconnect/ac/ACException;
        }
    .end annotation

    if-eqz p1, :cond_0

    iget-object v0, p0, Lcom/nuance/swypeconnect/ac/ACConfiguration;->config:Lcom/nuance/connect/api/ConfigService;

    invoke-interface {v0}, Lcom/nuance/connect/api/ConfigService;->getMinimumRefreshInterval()I

    move-result v0

    if-ge p1, v0, :cond_0

    new-instance v0, Lcom/nuance/swypeconnect/ac/ACException;

    const/16 v1, 0x71

    new-instance v2, Ljava/lang/StringBuilder;

    const-string/jumbo v3, "Refresh Interval cannot be less then "

    invoke-direct {v2, v3}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    iget-object v3, p0, Lcom/nuance/swypeconnect/ac/ACConfiguration;->config:Lcom/nuance/connect/api/ConfigService;

    invoke-interface {v3}, Lcom/nuance/connect/api/ConfigService;->getMinimumRefreshInterval()I

    move-result v3

    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    move-result-object v2

    invoke-virtual {v2}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v2

    invoke-direct {v0, v1, v2}, Lcom/nuance/swypeconnect/ac/ACException;-><init>(ILjava/lang/String;)V

    throw v0

    :cond_0
    iget-object v0, p0, Lcom/nuance/swypeconnect/ac/ACConfiguration;->config:Lcom/nuance/connect/api/ConfigService;

    invoke-interface {v0, p1}, Lcom/nuance/connect/api/ConfigService;->setRefreshInterval(I)V

    return-void
.end method
