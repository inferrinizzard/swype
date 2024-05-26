.class public Lcom/google/api/services/gmail/Gmail$Users$Settings$SendAs$Verify;
.super Lcom/google/api/services/gmail/GmailRequest;
.source "Gmail.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/google/api/services/gmail/Gmail$Users$Settings$SendAs;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x1
    name = "Verify"
.end annotation

.annotation system Ldalvik/annotation/Signature;
    value = {
        "Lcom/google/api/services/gmail/GmailRequest",
        "<",
        "Ljava/lang/Void;",
        ">;"
    }
.end annotation


# static fields
.field private static final REST_PATH:Ljava/lang/String; = "{userId}/settings/sendAs/{sendAsEmail}/verify"


# instance fields
.field private sendAsEmail:Ljava/lang/String;
    .annotation runtime Lcom/google/api/client/util/Key;
    .end annotation
.end field

.field final synthetic this$3:Lcom/google/api/services/gmail/Gmail$Users$Settings$SendAs;

.field private userId:Ljava/lang/String;
    .annotation runtime Lcom/google/api/client/util/Key;
    .end annotation
.end field


# direct methods
.method protected constructor <init>(Lcom/google/api/services/gmail/Gmail$Users$Settings$SendAs;Ljava/lang/String;Ljava/lang/String;)V
    .locals 6

    .prologue
    .line 7091
    iput-object p1, p0, Lcom/google/api/services/gmail/Gmail$Users$Settings$SendAs$Verify;->this$3:Lcom/google/api/services/gmail/Gmail$Users$Settings$SendAs;

    .line 7092
    iget-object v0, p1, Lcom/google/api/services/gmail/Gmail$Users$Settings$SendAs;->this$2:Lcom/google/api/services/gmail/Gmail$Users$Settings;

    iget-object v0, v0, Lcom/google/api/services/gmail/Gmail$Users$Settings;->this$1:Lcom/google/api/services/gmail/Gmail$Users;

    iget-object v1, v0, Lcom/google/api/services/gmail/Gmail$Users;->this$0:Lcom/google/api/services/gmail/Gmail;

    const-string/jumbo v2, "POST"

    const-string/jumbo v3, "{userId}/settings/sendAs/{sendAsEmail}/verify"

    const/4 v4, 0x0

    const-class v5, Ljava/lang/Void;

    move-object v0, p0

    invoke-direct/range {v0 .. v5}, Lcom/google/api/services/gmail/GmailRequest;-><init>(Lcom/google/api/services/gmail/Gmail;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Class;)V

    .line 7093
    const-string/jumbo v0, "Required parameter userId must be specified."

    invoke-static {p2, v0}, Lcom/google/api/client/util/Preconditions;->checkNotNull(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Ljava/lang/String;

    iput-object v0, p0, Lcom/google/api/services/gmail/Gmail$Users$Settings$SendAs$Verify;->userId:Ljava/lang/String;

    .line 7094
    const-string/jumbo v0, "Required parameter sendAsEmail must be specified."

    invoke-static {p3, v0}, Lcom/google/api/client/util/Preconditions;->checkNotNull(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Ljava/lang/String;

    iput-object v0, p0, Lcom/google/api/services/gmail/Gmail$Users$Settings$SendAs$Verify;->sendAsEmail:Ljava/lang/String;

    .line 7095
    return-void
.end method


# virtual methods
.method public getSendAsEmail()Ljava/lang/String;
    .locals 1

    .prologue
    .line 7162
    iget-object v0, p0, Lcom/google/api/services/gmail/Gmail$Users$Settings$SendAs$Verify;->sendAsEmail:Ljava/lang/String;

    return-object v0
.end method

.method public getUserId()Ljava/lang/String;
    .locals 1

    .prologue
    .line 7143
    iget-object v0, p0, Lcom/google/api/services/gmail/Gmail$Users$Settings$SendAs$Verify;->userId:Ljava/lang/String;

    return-object v0
.end method

.method public bridge synthetic set(Ljava/lang/String;Ljava/lang/Object;)Lcom/google/api/client/googleapis/services/AbstractGoogleClientRequest;
    .locals 1

    .prologue
    .line 7071
    invoke-virtual {p0, p1, p2}, Lcom/google/api/services/gmail/Gmail$Users$Settings$SendAs$Verify;->set(Ljava/lang/String;Ljava/lang/Object;)Lcom/google/api/services/gmail/Gmail$Users$Settings$SendAs$Verify;

    move-result-object v0

    return-object v0
.end method

.method public bridge synthetic set(Ljava/lang/String;Ljava/lang/Object;)Lcom/google/api/client/googleapis/services/json/AbstractGoogleJsonClientRequest;
    .locals 1

    .prologue
    .line 7071
    invoke-virtual {p0, p1, p2}, Lcom/google/api/services/gmail/Gmail$Users$Settings$SendAs$Verify;->set(Ljava/lang/String;Ljava/lang/Object;)Lcom/google/api/services/gmail/Gmail$Users$Settings$SendAs$Verify;

    move-result-object v0

    return-object v0
.end method

.method public bridge synthetic set(Ljava/lang/String;Ljava/lang/Object;)Lcom/google/api/client/util/GenericData;
    .locals 1

    .prologue
    .line 7071
    invoke-virtual {p0, p1, p2}, Lcom/google/api/services/gmail/Gmail$Users$Settings$SendAs$Verify;->set(Ljava/lang/String;Ljava/lang/Object;)Lcom/google/api/services/gmail/Gmail$Users$Settings$SendAs$Verify;

    move-result-object v0

    return-object v0
.end method

.method public set(Ljava/lang/String;Ljava/lang/Object;)Lcom/google/api/services/gmail/Gmail$Users$Settings$SendAs$Verify;
    .locals 1

    .prologue
    .line 7173
    invoke-super {p0, p1, p2}, Lcom/google/api/services/gmail/GmailRequest;->set(Ljava/lang/String;Ljava/lang/Object;)Lcom/google/api/services/gmail/GmailRequest;

    move-result-object v0

    check-cast v0, Lcom/google/api/services/gmail/Gmail$Users$Settings$SendAs$Verify;

    return-object v0
.end method

.method public bridge synthetic set(Ljava/lang/String;Ljava/lang/Object;)Lcom/google/api/services/gmail/GmailRequest;
    .locals 1

    .prologue
    .line 7071
    invoke-virtual {p0, p1, p2}, Lcom/google/api/services/gmail/Gmail$Users$Settings$SendAs$Verify;->set(Ljava/lang/String;Ljava/lang/Object;)Lcom/google/api/services/gmail/Gmail$Users$Settings$SendAs$Verify;

    move-result-object v0

    return-object v0
.end method

.method public setAlt(Ljava/lang/String;)Lcom/google/api/services/gmail/Gmail$Users$Settings$SendAs$Verify;
    .locals 1

    .prologue
    .line 7099
    invoke-super {p0, p1}, Lcom/google/api/services/gmail/GmailRequest;->setAlt(Ljava/lang/String;)Lcom/google/api/services/gmail/GmailRequest;

    move-result-object v0

    check-cast v0, Lcom/google/api/services/gmail/Gmail$Users$Settings$SendAs$Verify;

    return-object v0
.end method

.method public bridge synthetic setAlt(Ljava/lang/String;)Lcom/google/api/services/gmail/GmailRequest;
    .locals 1

    .prologue
    .line 7071
    invoke-virtual {p0, p1}, Lcom/google/api/services/gmail/Gmail$Users$Settings$SendAs$Verify;->setAlt(Ljava/lang/String;)Lcom/google/api/services/gmail/Gmail$Users$Settings$SendAs$Verify;

    move-result-object v0

    return-object v0
.end method

.method public setFields(Ljava/lang/String;)Lcom/google/api/services/gmail/Gmail$Users$Settings$SendAs$Verify;
    .locals 1

    .prologue
    .line 7104
    invoke-super {p0, p1}, Lcom/google/api/services/gmail/GmailRequest;->setFields(Ljava/lang/String;)Lcom/google/api/services/gmail/GmailRequest;

    move-result-object v0

    check-cast v0, Lcom/google/api/services/gmail/Gmail$Users$Settings$SendAs$Verify;

    return-object v0
.end method

.method public bridge synthetic setFields(Ljava/lang/String;)Lcom/google/api/services/gmail/GmailRequest;
    .locals 1

    .prologue
    .line 7071
    invoke-virtual {p0, p1}, Lcom/google/api/services/gmail/Gmail$Users$Settings$SendAs$Verify;->setFields(Ljava/lang/String;)Lcom/google/api/services/gmail/Gmail$Users$Settings$SendAs$Verify;

    move-result-object v0

    return-object v0
.end method

.method public setKey(Ljava/lang/String;)Lcom/google/api/services/gmail/Gmail$Users$Settings$SendAs$Verify;
    .locals 1

    .prologue
    .line 7109
    invoke-super {p0, p1}, Lcom/google/api/services/gmail/GmailRequest;->setKey(Ljava/lang/String;)Lcom/google/api/services/gmail/GmailRequest;

    move-result-object v0

    check-cast v0, Lcom/google/api/services/gmail/Gmail$Users$Settings$SendAs$Verify;

    return-object v0
.end method

.method public bridge synthetic setKey(Ljava/lang/String;)Lcom/google/api/services/gmail/GmailRequest;
    .locals 1

    .prologue
    .line 7071
    invoke-virtual {p0, p1}, Lcom/google/api/services/gmail/Gmail$Users$Settings$SendAs$Verify;->setKey(Ljava/lang/String;)Lcom/google/api/services/gmail/Gmail$Users$Settings$SendAs$Verify;

    move-result-object v0

    return-object v0
.end method

.method public setOauthToken(Ljava/lang/String;)Lcom/google/api/services/gmail/Gmail$Users$Settings$SendAs$Verify;
    .locals 1

    .prologue
    .line 7114
    invoke-super {p0, p1}, Lcom/google/api/services/gmail/GmailRequest;->setOauthToken(Ljava/lang/String;)Lcom/google/api/services/gmail/GmailRequest;

    move-result-object v0

    check-cast v0, Lcom/google/api/services/gmail/Gmail$Users$Settings$SendAs$Verify;

    return-object v0
.end method

.method public bridge synthetic setOauthToken(Ljava/lang/String;)Lcom/google/api/services/gmail/GmailRequest;
    .locals 1

    .prologue
    .line 7071
    invoke-virtual {p0, p1}, Lcom/google/api/services/gmail/Gmail$Users$Settings$SendAs$Verify;->setOauthToken(Ljava/lang/String;)Lcom/google/api/services/gmail/Gmail$Users$Settings$SendAs$Verify;

    move-result-object v0

    return-object v0
.end method

.method public setPrettyPrint(Ljava/lang/Boolean;)Lcom/google/api/services/gmail/Gmail$Users$Settings$SendAs$Verify;
    .locals 1

    .prologue
    .line 7119
    invoke-super {p0, p1}, Lcom/google/api/services/gmail/GmailRequest;->setPrettyPrint(Ljava/lang/Boolean;)Lcom/google/api/services/gmail/GmailRequest;

    move-result-object v0

    check-cast v0, Lcom/google/api/services/gmail/Gmail$Users$Settings$SendAs$Verify;

    return-object v0
.end method

.method public bridge synthetic setPrettyPrint(Ljava/lang/Boolean;)Lcom/google/api/services/gmail/GmailRequest;
    .locals 1

    .prologue
    .line 7071
    invoke-virtual {p0, p1}, Lcom/google/api/services/gmail/Gmail$Users$Settings$SendAs$Verify;->setPrettyPrint(Ljava/lang/Boolean;)Lcom/google/api/services/gmail/Gmail$Users$Settings$SendAs$Verify;

    move-result-object v0

    return-object v0
.end method

.method public setQuotaUser(Ljava/lang/String;)Lcom/google/api/services/gmail/Gmail$Users$Settings$SendAs$Verify;
    .locals 1

    .prologue
    .line 7124
    invoke-super {p0, p1}, Lcom/google/api/services/gmail/GmailRequest;->setQuotaUser(Ljava/lang/String;)Lcom/google/api/services/gmail/GmailRequest;

    move-result-object v0

    check-cast v0, Lcom/google/api/services/gmail/Gmail$Users$Settings$SendAs$Verify;

    return-object v0
.end method

.method public bridge synthetic setQuotaUser(Ljava/lang/String;)Lcom/google/api/services/gmail/GmailRequest;
    .locals 1

    .prologue
    .line 7071
    invoke-virtual {p0, p1}, Lcom/google/api/services/gmail/Gmail$Users$Settings$SendAs$Verify;->setQuotaUser(Ljava/lang/String;)Lcom/google/api/services/gmail/Gmail$Users$Settings$SendAs$Verify;

    move-result-object v0

    return-object v0
.end method

.method public setSendAsEmail(Ljava/lang/String;)Lcom/google/api/services/gmail/Gmail$Users$Settings$SendAs$Verify;
    .locals 0

    .prologue
    .line 7167
    iput-object p1, p0, Lcom/google/api/services/gmail/Gmail$Users$Settings$SendAs$Verify;->sendAsEmail:Ljava/lang/String;

    .line 7168
    return-object p0
.end method

.method public setUserId(Ljava/lang/String;)Lcom/google/api/services/gmail/Gmail$Users$Settings$SendAs$Verify;
    .locals 0

    .prologue
    .line 7151
    iput-object p1, p0, Lcom/google/api/services/gmail/Gmail$Users$Settings$SendAs$Verify;->userId:Ljava/lang/String;

    .line 7152
    return-object p0
.end method

.method public setUserIp(Ljava/lang/String;)Lcom/google/api/services/gmail/Gmail$Users$Settings$SendAs$Verify;
    .locals 1

    .prologue
    .line 7129
    invoke-super {p0, p1}, Lcom/google/api/services/gmail/GmailRequest;->setUserIp(Ljava/lang/String;)Lcom/google/api/services/gmail/GmailRequest;

    move-result-object v0

    check-cast v0, Lcom/google/api/services/gmail/Gmail$Users$Settings$SendAs$Verify;

    return-object v0
.end method

.method public bridge synthetic setUserIp(Ljava/lang/String;)Lcom/google/api/services/gmail/GmailRequest;
    .locals 1

    .prologue
    .line 7071
    invoke-virtual {p0, p1}, Lcom/google/api/services/gmail/Gmail$Users$Settings$SendAs$Verify;->setUserIp(Ljava/lang/String;)Lcom/google/api/services/gmail/Gmail$Users$Settings$SendAs$Verify;

    move-result-object v0

    return-object v0
.end method
