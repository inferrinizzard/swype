.class public Lcom/nuance/connect/internal/CategoryServiceInternal$AddonDictionaryImpl;
.super Ljava/lang/Object;

# interfaces
.implements Lcom/nuance/connect/api/AddonDictionariesService$AddonDictionary;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/nuance/connect/internal/CategoryServiceInternal;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x9
    name = "AddonDictionaryImpl"
.end annotation


# instance fields
.field private final category:Ljava/lang/String;

.field private final categoryTranslated:Ljava/lang/String;

.field private final dictionary:Ljava/lang/String;

.field private final id:I

.field private final language:I

.field private final name:Ljava/lang/String;

.field private final nameTranslated:Ljava/lang/String;

.field private final rank:I

.field private revertStatus:Ljava/lang/String;

.field private status:Ljava/lang/String;


# direct methods
.method public constructor <init>(Ljava/lang/String;ILjava/lang/String;Ljava/lang/String;IILjava/lang/String;Ljava/lang/String;)V
    .locals 0

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    iput p2, p0, Lcom/nuance/connect/internal/CategoryServiceInternal$AddonDictionaryImpl;->id:I

    iput-object p3, p0, Lcom/nuance/connect/internal/CategoryServiceInternal$AddonDictionaryImpl;->name:Ljava/lang/String;

    iput-object p4, p0, Lcom/nuance/connect/internal/CategoryServiceInternal$AddonDictionaryImpl;->category:Ljava/lang/String;

    iput p5, p0, Lcom/nuance/connect/internal/CategoryServiceInternal$AddonDictionaryImpl;->language:I

    iput-object p1, p0, Lcom/nuance/connect/internal/CategoryServiceInternal$AddonDictionaryImpl;->dictionary:Ljava/lang/String;

    iput p6, p0, Lcom/nuance/connect/internal/CategoryServiceInternal$AddonDictionaryImpl;->rank:I

    iput-object p7, p0, Lcom/nuance/connect/internal/CategoryServiceInternal$AddonDictionaryImpl;->nameTranslated:Ljava/lang/String;

    iput-object p8, p0, Lcom/nuance/connect/internal/CategoryServiceInternal$AddonDictionaryImpl;->categoryTranslated:Ljava/lang/String;

    return-void
.end method


# virtual methods
.method diffLog(Lcom/nuance/connect/internal/CategoryServiceInternal$AddonDictionaryImpl;)Ljava/lang/String;
    .locals 4

    const/4 v3, 0x0

    new-instance v0, Ljava/lang/StringBuilder;

    invoke-direct {v0}, Ljava/lang/StringBuilder;-><init>()V

    iget-object v1, p0, Lcom/nuance/connect/internal/CategoryServiceInternal$AddonDictionaryImpl;->name:Ljava/lang/String;

    invoke-static {v1}, Ljava/lang/String;->valueOf(Ljava/lang/Object;)Ljava/lang/String;

    move-result-object v1

    iget-object v2, p1, Lcom/nuance/connect/internal/CategoryServiceInternal$AddonDictionaryImpl;->name:Ljava/lang/String;

    invoke-static {v2}, Ljava/lang/String;->valueOf(Ljava/lang/Object;)Ljava/lang/String;

    move-result-object v2

    invoke-virtual {v1, v2}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v1

    if-nez v1, :cond_0

    const-string/jumbo v1, "; Name("

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v1

    iget-object v2, p0, Lcom/nuance/connect/internal/CategoryServiceInternal$AddonDictionaryImpl;->name:Ljava/lang/String;

    invoke-static {v2}, Ljava/lang/String;->valueOf(Ljava/lang/Object;)Ljava/lang/String;

    move-result-object v2

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v1

    const-string/jumbo v2, " => "

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v1

    iget-object v2, p1, Lcom/nuance/connect/internal/CategoryServiceInternal$AddonDictionaryImpl;->name:Ljava/lang/String;

    invoke-static {v2}, Ljava/lang/String;->valueOf(Ljava/lang/Object;)Ljava/lang/String;

    move-result-object v2

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v1

    const-string/jumbo v2, ") "

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    :cond_0
    iget-object v1, p0, Lcom/nuance/connect/internal/CategoryServiceInternal$AddonDictionaryImpl;->category:Ljava/lang/String;

    invoke-static {v1}, Ljava/lang/String;->valueOf(Ljava/lang/Object;)Ljava/lang/String;

    move-result-object v1

    iget-object v2, p1, Lcom/nuance/connect/internal/CategoryServiceInternal$AddonDictionaryImpl;->category:Ljava/lang/String;

    invoke-static {v2}, Ljava/lang/String;->valueOf(Ljava/lang/Object;)Ljava/lang/String;

    move-result-object v2

    invoke-virtual {v1, v2}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v1

    if-nez v1, :cond_1

    const-string/jumbo v1, "; Category("

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v1

    iget-object v2, p0, Lcom/nuance/connect/internal/CategoryServiceInternal$AddonDictionaryImpl;->category:Ljava/lang/String;

    invoke-static {v2}, Ljava/lang/String;->valueOf(Ljava/lang/Object;)Ljava/lang/String;

    move-result-object v2

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v1

    const-string/jumbo v2, " => "

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v1

    iget-object v2, p1, Lcom/nuance/connect/internal/CategoryServiceInternal$AddonDictionaryImpl;->category:Ljava/lang/String;

    invoke-static {v2}, Ljava/lang/String;->valueOf(Ljava/lang/Object;)Ljava/lang/String;

    move-result-object v2

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v1

    const-string/jumbo v2, ") "

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    :cond_1
    iget v1, p0, Lcom/nuance/connect/internal/CategoryServiceInternal$AddonDictionaryImpl;->id:I

    invoke-static {v1}, Ljava/lang/String;->valueOf(I)Ljava/lang/String;

    move-result-object v1

    iget v2, p1, Lcom/nuance/connect/internal/CategoryServiceInternal$AddonDictionaryImpl;->id:I

    invoke-static {v2}, Ljava/lang/String;->valueOf(I)Ljava/lang/String;

    move-result-object v2

    invoke-virtual {v1, v2}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v1

    if-nez v1, :cond_2

    const-string/jumbo v1, "; ID("

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v1

    iget v2, p0, Lcom/nuance/connect/internal/CategoryServiceInternal$AddonDictionaryImpl;->id:I

    invoke-static {v2}, Ljava/lang/String;->valueOf(I)Ljava/lang/String;

    move-result-object v2

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v1

    const-string/jumbo v2, " => "

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v1

    iget v2, p1, Lcom/nuance/connect/internal/CategoryServiceInternal$AddonDictionaryImpl;->id:I

    invoke-static {v2}, Ljava/lang/String;->valueOf(I)Ljava/lang/String;

    move-result-object v2

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v1

    const-string/jumbo v2, ") "

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    :cond_2
    iget v1, p0, Lcom/nuance/connect/internal/CategoryServiceInternal$AddonDictionaryImpl;->language:I

    invoke-static {v1}, Ljava/lang/String;->valueOf(I)Ljava/lang/String;

    move-result-object v1

    iget v2, p1, Lcom/nuance/connect/internal/CategoryServiceInternal$AddonDictionaryImpl;->language:I

    invoke-static {v2}, Ljava/lang/String;->valueOf(I)Ljava/lang/String;

    move-result-object v2

    invoke-virtual {v1, v2}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v1

    if-nez v1, :cond_3

    const-string/jumbo v1, "; Language("

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v1

    iget v2, p0, Lcom/nuance/connect/internal/CategoryServiceInternal$AddonDictionaryImpl;->language:I

    invoke-static {v2}, Ljava/lang/String;->valueOf(I)Ljava/lang/String;

    move-result-object v2

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v1

    const-string/jumbo v2, " => "

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v1

    iget v2, p1, Lcom/nuance/connect/internal/CategoryServiceInternal$AddonDictionaryImpl;->language:I

    invoke-static {v2}, Ljava/lang/String;->valueOf(I)Ljava/lang/String;

    move-result-object v2

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v1

    const-string/jumbo v2, ") "

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    :cond_3
    iget-object v1, p0, Lcom/nuance/connect/internal/CategoryServiceInternal$AddonDictionaryImpl;->dictionary:Ljava/lang/String;

    invoke-static {v1}, Ljava/lang/String;->valueOf(Ljava/lang/Object;)Ljava/lang/String;

    move-result-object v1

    iget-object v2, p1, Lcom/nuance/connect/internal/CategoryServiceInternal$AddonDictionaryImpl;->dictionary:Ljava/lang/String;

    invoke-static {v2}, Ljava/lang/String;->valueOf(Ljava/lang/Object;)Ljava/lang/String;

    move-result-object v2

    invoke-virtual {v1, v2}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v1

    if-nez v1, :cond_4

    const-string/jumbo v1, "; Dictionary("

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v1

    iget-object v2, p0, Lcom/nuance/connect/internal/CategoryServiceInternal$AddonDictionaryImpl;->dictionary:Ljava/lang/String;

    invoke-static {v2}, Ljava/lang/String;->valueOf(Ljava/lang/Object;)Ljava/lang/String;

    move-result-object v2

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v1

    const-string/jumbo v2, " => "

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v1

    iget-object v2, p1, Lcom/nuance/connect/internal/CategoryServiceInternal$AddonDictionaryImpl;->dictionary:Ljava/lang/String;

    invoke-static {v2}, Ljava/lang/String;->valueOf(Ljava/lang/Object;)Ljava/lang/String;

    move-result-object v2

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v1

    const-string/jumbo v2, ") "

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    :cond_4
    iget-object v1, p0, Lcom/nuance/connect/internal/CategoryServiceInternal$AddonDictionaryImpl;->status:Ljava/lang/String;

    invoke-static {v1}, Ljava/lang/String;->valueOf(Ljava/lang/Object;)Ljava/lang/String;

    move-result-object v1

    iget-object v2, p1, Lcom/nuance/connect/internal/CategoryServiceInternal$AddonDictionaryImpl;->status:Ljava/lang/String;

    invoke-static {v2}, Ljava/lang/String;->valueOf(Ljava/lang/Object;)Ljava/lang/String;

    move-result-object v2

    invoke-virtual {v1, v2}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v1

    if-nez v1, :cond_5

    const-string/jumbo v1, "; Status("

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v1

    iget-object v2, p0, Lcom/nuance/connect/internal/CategoryServiceInternal$AddonDictionaryImpl;->status:Ljava/lang/String;

    invoke-static {v2}, Ljava/lang/String;->valueOf(Ljava/lang/Object;)Ljava/lang/String;

    move-result-object v2

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v1

    const-string/jumbo v2, " => "

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v1

    iget-object v2, p1, Lcom/nuance/connect/internal/CategoryServiceInternal$AddonDictionaryImpl;->status:Ljava/lang/String;

    invoke-static {v2}, Ljava/lang/String;->valueOf(Ljava/lang/Object;)Ljava/lang/String;

    move-result-object v2

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v1

    const-string/jumbo v2, ") "

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    :cond_5
    iget-object v1, p0, Lcom/nuance/connect/internal/CategoryServiceInternal$AddonDictionaryImpl;->categoryTranslated:Ljava/lang/String;

    invoke-static {v1}, Ljava/lang/String;->valueOf(Ljava/lang/Object;)Ljava/lang/String;

    move-result-object v1

    iget-object v2, p1, Lcom/nuance/connect/internal/CategoryServiceInternal$AddonDictionaryImpl;->categoryTranslated:Ljava/lang/String;

    invoke-static {v2}, Ljava/lang/String;->valueOf(Ljava/lang/Object;)Ljava/lang/String;

    move-result-object v2

    invoke-virtual {v1, v2}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v1

    if-nez v1, :cond_6

    const-string/jumbo v1, "; categoryTranslated("

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v1

    iget-object v2, p0, Lcom/nuance/connect/internal/CategoryServiceInternal$AddonDictionaryImpl;->categoryTranslated:Ljava/lang/String;

    invoke-static {v2}, Ljava/lang/String;->valueOf(Ljava/lang/Object;)Ljava/lang/String;

    move-result-object v2

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v1

    const-string/jumbo v2, " => "

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v1

    iget-object v2, p1, Lcom/nuance/connect/internal/CategoryServiceInternal$AddonDictionaryImpl;->categoryTranslated:Ljava/lang/String;

    invoke-static {v2}, Ljava/lang/String;->valueOf(Ljava/lang/Object;)Ljava/lang/String;

    move-result-object v2

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v1

    const-string/jumbo v2, ") "

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    :cond_6
    iget-object v1, p0, Lcom/nuance/connect/internal/CategoryServiceInternal$AddonDictionaryImpl;->nameTranslated:Ljava/lang/String;

    invoke-static {v1}, Ljava/lang/String;->valueOf(Ljava/lang/Object;)Ljava/lang/String;

    move-result-object v1

    iget-object v2, p1, Lcom/nuance/connect/internal/CategoryServiceInternal$AddonDictionaryImpl;->nameTranslated:Ljava/lang/String;

    invoke-static {v2}, Ljava/lang/String;->valueOf(Ljava/lang/Object;)Ljava/lang/String;

    move-result-object v2

    invoke-virtual {v1, v2}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v1

    if-nez v1, :cond_7

    const-string/jumbo v1, "; nameTranslated("

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v1

    iget-object v2, p0, Lcom/nuance/connect/internal/CategoryServiceInternal$AddonDictionaryImpl;->nameTranslated:Ljava/lang/String;

    invoke-static {v2}, Ljava/lang/String;->valueOf(Ljava/lang/Object;)Ljava/lang/String;

    move-result-object v2

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v1

    const-string/jumbo v2, " => "

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v1

    iget-object v2, p1, Lcom/nuance/connect/internal/CategoryServiceInternal$AddonDictionaryImpl;->nameTranslated:Ljava/lang/String;

    invoke-static {v2}, Ljava/lang/String;->valueOf(Ljava/lang/Object;)Ljava/lang/String;

    move-result-object v2

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v1

    const-string/jumbo v2, ") "

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    :cond_7
    invoke-virtual {v0}, Ljava/lang/StringBuilder;->length()I

    move-result v1

    if-lez v1, :cond_8

    iget v1, p0, Lcom/nuance/connect/internal/CategoryServiceInternal$AddonDictionaryImpl;->id:I

    invoke-virtual {v0, v3, v1}, Ljava/lang/StringBuilder;->insert(II)Ljava/lang/StringBuilder;

    move-result-object v1

    const-string/jumbo v2, "ID: "

    invoke-virtual {v1, v3, v2}, Ljava/lang/StringBuilder;->insert(ILjava/lang/String;)Ljava/lang/StringBuilder;

    :cond_8
    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method download()V
    .locals 1

    iget-object v0, p0, Lcom/nuance/connect/internal/CategoryServiceInternal$AddonDictionaryImpl;->status:Ljava/lang/String;

    iput-object v0, p0, Lcom/nuance/connect/internal/CategoryServiceInternal$AddonDictionaryImpl;->revertStatus:Ljava/lang/String;

    const-string/jumbo v0, "STATUS_DOWNLOADING"

    iput-object v0, p0, Lcom/nuance/connect/internal/CategoryServiceInternal$AddonDictionaryImpl;->status:Ljava/lang/String;

    return-void
.end method

.method public getCategory()Ljava/lang/String;
    .locals 1

    iget-object v0, p0, Lcom/nuance/connect/internal/CategoryServiceInternal$AddonDictionaryImpl;->category:Ljava/lang/String;

    return-object v0
.end method

.method public getCategoryTranslated()Ljava/lang/String;
    .locals 1

    iget-object v0, p0, Lcom/nuance/connect/internal/CategoryServiceInternal$AddonDictionaryImpl;->categoryTranslated:Ljava/lang/String;

    return-object v0
.end method

.method public getDictionary()Ljava/lang/String;
    .locals 1

    iget-object v0, p0, Lcom/nuance/connect/internal/CategoryServiceInternal$AddonDictionaryImpl;->dictionary:Ljava/lang/String;

    return-object v0
.end method

.method public getId()I
    .locals 1

    iget v0, p0, Lcom/nuance/connect/internal/CategoryServiceInternal$AddonDictionaryImpl;->id:I

    return v0
.end method

.method public getLanguage()I
    .locals 1

    iget v0, p0, Lcom/nuance/connect/internal/CategoryServiceInternal$AddonDictionaryImpl;->language:I

    return v0
.end method

.method public getName()Ljava/lang/String;
    .locals 1

    iget-object v0, p0, Lcom/nuance/connect/internal/CategoryServiceInternal$AddonDictionaryImpl;->name:Ljava/lang/String;

    return-object v0
.end method

.method public getNameTranslated()Ljava/lang/String;
    .locals 1

    iget-object v0, p0, Lcom/nuance/connect/internal/CategoryServiceInternal$AddonDictionaryImpl;->nameTranslated:Ljava/lang/String;

    return-object v0
.end method

.method public getRank()I
    .locals 1

    iget v0, p0, Lcom/nuance/connect/internal/CategoryServiceInternal$AddonDictionaryImpl;->rank:I

    return v0
.end method

.method public getStatus()Ljava/lang/String;
    .locals 1

    iget-object v0, p0, Lcom/nuance/connect/internal/CategoryServiceInternal$AddonDictionaryImpl;->status:Ljava/lang/String;

    return-object v0
.end method

.method public hasUpdate()Z
    .locals 2

    const-string/jumbo v0, "STATUS_INSTALLED_WITH_UPDATE"

    iget-object v1, p0, Lcom/nuance/connect/internal/CategoryServiceInternal$AddonDictionaryImpl;->status:Ljava/lang/String;

    invoke-virtual {v0, v1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v0

    return v0
.end method

.method public isInstalled()Z
    .locals 2

    const-string/jumbo v0, "STATUS_INSTALLED"

    iget-object v1, p0, Lcom/nuance/connect/internal/CategoryServiceInternal$AddonDictionaryImpl;->status:Ljava/lang/String;

    invoke-virtual {v0, v1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v0

    if-nez v0, :cond_0

    const-string/jumbo v0, "STATUS_INSTALLED_WITH_UPDATE"

    iget-object v1, p0, Lcom/nuance/connect/internal/CategoryServiceInternal$AddonDictionaryImpl;->status:Ljava/lang/String;

    invoke-virtual {v0, v1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v0

    if-eqz v0, :cond_1

    :cond_0
    const/4 v0, 0x1

    :goto_0
    return v0

    :cond_1
    const/4 v0, 0x0

    goto :goto_0
.end method

.method public isSubscribed()Z
    .locals 2

    const-string/jumbo v0, "STATUS_AVAILABLE"

    iget-object v1, p0, Lcom/nuance/connect/internal/CategoryServiceInternal$AddonDictionaryImpl;->status:Ljava/lang/String;

    invoke-virtual {v0, v1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v0

    if-nez v0, :cond_0

    const/4 v0, 0x1

    :goto_0
    return v0

    :cond_0
    const/4 v0, 0x0

    goto :goto_0
.end method

.method revert()V
    .locals 1

    iget-object v0, p0, Lcom/nuance/connect/internal/CategoryServiceInternal$AddonDictionaryImpl;->revertStatus:Ljava/lang/String;

    if-eqz v0, :cond_0

    iget-object v0, p0, Lcom/nuance/connect/internal/CategoryServiceInternal$AddonDictionaryImpl;->revertStatus:Ljava/lang/String;

    iput-object v0, p0, Lcom/nuance/connect/internal/CategoryServiceInternal$AddonDictionaryImpl;->status:Ljava/lang/String;

    :goto_0
    return-void

    :cond_0
    const-string/jumbo v0, "STATUS_AVAILABLE"

    iput-object v0, p0, Lcom/nuance/connect/internal/CategoryServiceInternal$AddonDictionaryImpl;->status:Ljava/lang/String;

    goto :goto_0
.end method

.method public setStatus(Ljava/lang/String;)V
    .locals 1

    if-eqz p1, :cond_0

    const-string/jumbo v0, "STATUS_UNINSTALL_PENDING"

    invoke-virtual {p1, v0}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v0

    if-eqz v0, :cond_0

    const/4 v0, 0x0

    iput-object v0, p0, Lcom/nuance/connect/internal/CategoryServiceInternal$AddonDictionaryImpl;->revertStatus:Ljava/lang/String;

    :cond_0
    iput-object p1, p0, Lcom/nuance/connect/internal/CategoryServiceInternal$AddonDictionaryImpl;->status:Ljava/lang/String;

    return-void
.end method

.method wasInstalled()Z
    .locals 2

    iget-object v0, p0, Lcom/nuance/connect/internal/CategoryServiceInternal$AddonDictionaryImpl;->revertStatus:Ljava/lang/String;

    if-eqz v0, :cond_1

    const-string/jumbo v0, "STATUS_INSTALLED"

    iget-object v1, p0, Lcom/nuance/connect/internal/CategoryServiceInternal$AddonDictionaryImpl;->revertStatus:Ljava/lang/String;

    invoke-virtual {v0, v1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v0

    if-nez v0, :cond_0

    const-string/jumbo v0, "STATUS_INSTALLED_WITH_UPDATE"

    iget-object v1, p0, Lcom/nuance/connect/internal/CategoryServiceInternal$AddonDictionaryImpl;->revertStatus:Ljava/lang/String;

    invoke-virtual {v0, v1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v0

    if-eqz v0, :cond_1

    :cond_0
    const/4 v0, 0x1

    :goto_0
    return v0

    :cond_1
    const/4 v0, 0x0

    goto :goto_0
.end method
