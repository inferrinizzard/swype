.class public final Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;
.super Lcom/a/a/h$a;

# interfaces
.implements Lcom/nuance/connect/proto/Prediction$PredictionResultV1OrBuilder;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/nuance/connect/proto/Prediction$PredictionResultV1;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x19
    name = "Builder"
.end annotation

.annotation system Ldalvik/annotation/Signature;
    value = {
        "Lcom/a/a/h$a",
        "<",
        "Lcom/nuance/connect/proto/Prediction$PredictionResultV1;",
        "Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;",
        ">;",
        "Lcom/nuance/connect/proto/Prediction$PredictionResultV1OrBuilder;"
    }
.end annotation


# instance fields
.field private attribute_:Ljava/util/List;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/List",
            "<",
            "Ljava/lang/Integer;",
            ">;"
        }
    .end annotation
.end field

.field private bitField0_:I

.field private fullSpell_:Ljava/util/List;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/List",
            "<",
            "Ljava/lang/Integer;",
            ">;"
        }
    .end annotation
.end field

.field private phrase_:Ljava/util/List;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/List",
            "<",
            "Ljava/lang/Integer;",
            ">;"
        }
    .end annotation
.end field

.field private spell_:Ljava/util/List;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/List",
            "<",
            "Ljava/lang/Integer;",
            ">;"
        }
    .end annotation
.end field


# direct methods
.method private constructor <init>()V
    .locals 1

    invoke-direct {p0}, Lcom/a/a/h$a;-><init>()V

    invoke-static {}, Ljava/util/Collections;->emptyList()Ljava/util/List;

    move-result-object v0

    iput-object v0, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->phrase_:Ljava/util/List;

    invoke-static {}, Ljava/util/Collections;->emptyList()Ljava/util/List;

    move-result-object v0

    iput-object v0, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->spell_:Ljava/util/List;

    invoke-static {}, Ljava/util/Collections;->emptyList()Ljava/util/List;

    move-result-object v0

    iput-object v0, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->fullSpell_:Ljava/util/List;

    invoke-static {}, Ljava/util/Collections;->emptyList()Ljava/util/List;

    move-result-object v0

    iput-object v0, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->attribute_:Ljava/util/List;

    invoke-direct {p0}, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->maybeForceBuilderInitialization()V

    return-void
.end method

.method static synthetic access$1900()Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;
    .locals 1

    invoke-static {}, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->create()Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;

    move-result-object v0

    return-object v0
.end method

.method private static create()Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;
    .locals 1

    new-instance v0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;

    invoke-direct {v0}, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;-><init>()V

    return-object v0
.end method

.method private ensureAttributeIsMutable()V
    .locals 2

    iget v0, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->bitField0_:I

    and-int/lit8 v0, v0, 0x8

    const/16 v1, 0x8

    if-eq v0, v1, :cond_0

    new-instance v0, Ljava/util/ArrayList;

    iget-object v1, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->attribute_:Ljava/util/List;

    invoke-direct {v0, v1}, Ljava/util/ArrayList;-><init>(Ljava/util/Collection;)V

    iput-object v0, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->attribute_:Ljava/util/List;

    iget v0, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->bitField0_:I

    or-int/lit8 v0, v0, 0x8

    iput v0, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->bitField0_:I

    :cond_0
    return-void
.end method

.method private ensureFullSpellIsMutable()V
    .locals 2

    iget v0, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->bitField0_:I

    and-int/lit8 v0, v0, 0x4

    const/4 v1, 0x4

    if-eq v0, v1, :cond_0

    new-instance v0, Ljava/util/ArrayList;

    iget-object v1, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->fullSpell_:Ljava/util/List;

    invoke-direct {v0, v1}, Ljava/util/ArrayList;-><init>(Ljava/util/Collection;)V

    iput-object v0, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->fullSpell_:Ljava/util/List;

    iget v0, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->bitField0_:I

    or-int/lit8 v0, v0, 0x4

    iput v0, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->bitField0_:I

    :cond_0
    return-void
.end method

.method private ensurePhraseIsMutable()V
    .locals 2

    iget v0, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->bitField0_:I

    and-int/lit8 v0, v0, 0x1

    const/4 v1, 0x1

    if-eq v0, v1, :cond_0

    new-instance v0, Ljava/util/ArrayList;

    iget-object v1, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->phrase_:Ljava/util/List;

    invoke-direct {v0, v1}, Ljava/util/ArrayList;-><init>(Ljava/util/Collection;)V

    iput-object v0, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->phrase_:Ljava/util/List;

    iget v0, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->bitField0_:I

    or-int/lit8 v0, v0, 0x1

    iput v0, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->bitField0_:I

    :cond_0
    return-void
.end method

.method private ensureSpellIsMutable()V
    .locals 2

    iget v0, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->bitField0_:I

    and-int/lit8 v0, v0, 0x2

    const/4 v1, 0x2

    if-eq v0, v1, :cond_0

    new-instance v0, Ljava/util/ArrayList;

    iget-object v1, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->spell_:Ljava/util/List;

    invoke-direct {v0, v1}, Ljava/util/ArrayList;-><init>(Ljava/util/Collection;)V

    iput-object v0, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->spell_:Ljava/util/List;

    iget v0, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->bitField0_:I

    or-int/lit8 v0, v0, 0x2

    iput v0, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->bitField0_:I

    :cond_0
    return-void
.end method

.method private maybeForceBuilderInitialization()V
    .locals 0

    return-void
.end method


# virtual methods
.method public final addAllAttribute(Ljava/lang/Iterable;)Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;
    .locals 1
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Ljava/lang/Iterable",
            "<+",
            "Ljava/lang/Integer;",
            ">;)",
            "Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;"
        }
    .end annotation

    invoke-direct {p0}, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->ensureAttributeIsMutable()V

    iget-object v0, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->attribute_:Ljava/util/List;

    invoke-static {p1, v0}, Lcom/a/a/h$a;->addAll(Ljava/lang/Iterable;Ljava/util/Collection;)V

    return-object p0
.end method

.method public final addAllFullSpell(Ljava/lang/Iterable;)Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;
    .locals 1
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Ljava/lang/Iterable",
            "<+",
            "Ljava/lang/Integer;",
            ">;)",
            "Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;"
        }
    .end annotation

    invoke-direct {p0}, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->ensureFullSpellIsMutable()V

    iget-object v0, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->fullSpell_:Ljava/util/List;

    invoke-static {p1, v0}, Lcom/a/a/h$a;->addAll(Ljava/lang/Iterable;Ljava/util/Collection;)V

    return-object p0
.end method

.method public final addAllPhrase(Ljava/lang/Iterable;)Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;
    .locals 1
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Ljava/lang/Iterable",
            "<+",
            "Ljava/lang/Integer;",
            ">;)",
            "Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;"
        }
    .end annotation

    invoke-direct {p0}, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->ensurePhraseIsMutable()V

    iget-object v0, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->phrase_:Ljava/util/List;

    invoke-static {p1, v0}, Lcom/a/a/h$a;->addAll(Ljava/lang/Iterable;Ljava/util/Collection;)V

    return-object p0
.end method

.method public final addAllSpell(Ljava/lang/Iterable;)Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;
    .locals 1
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Ljava/lang/Iterable",
            "<+",
            "Ljava/lang/Integer;",
            ">;)",
            "Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;"
        }
    .end annotation

    invoke-direct {p0}, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->ensureSpellIsMutable()V

    iget-object v0, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->spell_:Ljava/util/List;

    invoke-static {p1, v0}, Lcom/a/a/h$a;->addAll(Ljava/lang/Iterable;Ljava/util/Collection;)V

    return-object p0
.end method

.method public final addAttribute(I)Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;
    .locals 2

    invoke-direct {p0}, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->ensureAttributeIsMutable()V

    iget-object v0, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->attribute_:Ljava/util/List;

    invoke-static {p1}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v1

    invoke-interface {v0, v1}, Ljava/util/List;->add(Ljava/lang/Object;)Z

    return-object p0
.end method

.method public final addFullSpell(I)Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;
    .locals 2

    invoke-direct {p0}, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->ensureFullSpellIsMutable()V

    iget-object v0, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->fullSpell_:Ljava/util/List;

    invoke-static {p1}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v1

    invoke-interface {v0, v1}, Ljava/util/List;->add(Ljava/lang/Object;)Z

    return-object p0
.end method

.method public final addPhrase(I)Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;
    .locals 2

    invoke-direct {p0}, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->ensurePhraseIsMutable()V

    iget-object v0, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->phrase_:Ljava/util/List;

    invoke-static {p1}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v1

    invoke-interface {v0, v1}, Ljava/util/List;->add(Ljava/lang/Object;)Z

    return-object p0
.end method

.method public final addSpell(I)Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;
    .locals 2

    invoke-direct {p0}, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->ensureSpellIsMutable()V

    iget-object v0, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->spell_:Ljava/util/List;

    invoke-static {p1}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v1

    invoke-interface {v0, v1}, Ljava/util/List;->add(Ljava/lang/Object;)Z

    return-object p0
.end method

.method public final bridge synthetic build()Lcom/a/a/n;
    .locals 1

    invoke-virtual {p0}, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->build()Lcom/nuance/connect/proto/Prediction$PredictionResultV1;

    move-result-object v0

    return-object v0
.end method

.method public final build()Lcom/nuance/connect/proto/Prediction$PredictionResultV1;
    .locals 2

    invoke-virtual {p0}, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->buildPartial()Lcom/nuance/connect/proto/Prediction$PredictionResultV1;

    move-result-object v0

    invoke-virtual {v0}, Lcom/nuance/connect/proto/Prediction$PredictionResultV1;->isInitialized()Z

    move-result v1

    if-nez v1, :cond_0

    invoke-static {v0}, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->newUninitializedMessageException(Lcom/a/a/n;)Lcom/a/a/v;

    move-result-object v0

    throw v0

    :cond_0
    return-object v0
.end method

.method public final bridge synthetic buildPartial()Lcom/a/a/n;
    .locals 1

    invoke-virtual {p0}, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->buildPartial()Lcom/nuance/connect/proto/Prediction$PredictionResultV1;

    move-result-object v0

    return-object v0
.end method

.method public final buildPartial()Lcom/nuance/connect/proto/Prediction$PredictionResultV1;
    .locals 3

    new-instance v0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1;

    const/4 v1, 0x0

    invoke-direct {v0, p0, v1}, Lcom/nuance/connect/proto/Prediction$PredictionResultV1;-><init>(Lcom/a/a/h$a;Lcom/nuance/connect/proto/Prediction$1;)V

    iget v1, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->bitField0_:I

    and-int/lit8 v1, v1, 0x1

    const/4 v2, 0x1

    if-ne v1, v2, :cond_0

    iget-object v1, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->phrase_:Ljava/util/List;

    invoke-static {v1}, Ljava/util/Collections;->unmodifiableList(Ljava/util/List;)Ljava/util/List;

    move-result-object v1

    iput-object v1, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->phrase_:Ljava/util/List;

    iget v1, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->bitField0_:I

    and-int/lit8 v1, v1, -0x2

    iput v1, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->bitField0_:I

    :cond_0
    iget-object v1, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->phrase_:Ljava/util/List;

    invoke-static {v0, v1}, Lcom/nuance/connect/proto/Prediction$PredictionResultV1;->access$2102(Lcom/nuance/connect/proto/Prediction$PredictionResultV1;Ljava/util/List;)Ljava/util/List;

    iget v1, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->bitField0_:I

    and-int/lit8 v1, v1, 0x2

    const/4 v2, 0x2

    if-ne v1, v2, :cond_1

    iget-object v1, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->spell_:Ljava/util/List;

    invoke-static {v1}, Ljava/util/Collections;->unmodifiableList(Ljava/util/List;)Ljava/util/List;

    move-result-object v1

    iput-object v1, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->spell_:Ljava/util/List;

    iget v1, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->bitField0_:I

    and-int/lit8 v1, v1, -0x3

    iput v1, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->bitField0_:I

    :cond_1
    iget-object v1, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->spell_:Ljava/util/List;

    invoke-static {v0, v1}, Lcom/nuance/connect/proto/Prediction$PredictionResultV1;->access$2202(Lcom/nuance/connect/proto/Prediction$PredictionResultV1;Ljava/util/List;)Ljava/util/List;

    iget v1, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->bitField0_:I

    and-int/lit8 v1, v1, 0x4

    const/4 v2, 0x4

    if-ne v1, v2, :cond_2

    iget-object v1, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->fullSpell_:Ljava/util/List;

    invoke-static {v1}, Ljava/util/Collections;->unmodifiableList(Ljava/util/List;)Ljava/util/List;

    move-result-object v1

    iput-object v1, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->fullSpell_:Ljava/util/List;

    iget v1, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->bitField0_:I

    and-int/lit8 v1, v1, -0x5

    iput v1, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->bitField0_:I

    :cond_2
    iget-object v1, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->fullSpell_:Ljava/util/List;

    invoke-static {v0, v1}, Lcom/nuance/connect/proto/Prediction$PredictionResultV1;->access$2302(Lcom/nuance/connect/proto/Prediction$PredictionResultV1;Ljava/util/List;)Ljava/util/List;

    iget v1, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->bitField0_:I

    and-int/lit8 v1, v1, 0x8

    const/16 v2, 0x8

    if-ne v1, v2, :cond_3

    iget-object v1, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->attribute_:Ljava/util/List;

    invoke-static {v1}, Ljava/util/Collections;->unmodifiableList(Ljava/util/List;)Ljava/util/List;

    move-result-object v1

    iput-object v1, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->attribute_:Ljava/util/List;

    iget v1, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->bitField0_:I

    and-int/lit8 v1, v1, -0x9

    iput v1, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->bitField0_:I

    :cond_3
    iget-object v1, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->attribute_:Ljava/util/List;

    invoke-static {v0, v1}, Lcom/nuance/connect/proto/Prediction$PredictionResultV1;->access$2402(Lcom/nuance/connect/proto/Prediction$PredictionResultV1;Ljava/util/List;)Ljava/util/List;

    return-object v0
.end method

.method public final bridge synthetic clear()Lcom/a/a/h$a;
    .locals 1

    invoke-virtual {p0}, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->clear()Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;

    move-result-object v0

    return-object v0
.end method

.method public final bridge synthetic clear()Lcom/a/a/n$a;
    .locals 1

    invoke-virtual {p0}, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->clear()Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;

    move-result-object v0

    return-object v0
.end method

.method public final clear()Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;
    .locals 1

    invoke-super {p0}, Lcom/a/a/h$a;->clear()Lcom/a/a/h$a;

    invoke-static {}, Ljava/util/Collections;->emptyList()Ljava/util/List;

    move-result-object v0

    iput-object v0, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->phrase_:Ljava/util/List;

    iget v0, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->bitField0_:I

    and-int/lit8 v0, v0, -0x2

    iput v0, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->bitField0_:I

    invoke-static {}, Ljava/util/Collections;->emptyList()Ljava/util/List;

    move-result-object v0

    iput-object v0, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->spell_:Ljava/util/List;

    iget v0, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->bitField0_:I

    and-int/lit8 v0, v0, -0x3

    iput v0, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->bitField0_:I

    invoke-static {}, Ljava/util/Collections;->emptyList()Ljava/util/List;

    move-result-object v0

    iput-object v0, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->fullSpell_:Ljava/util/List;

    iget v0, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->bitField0_:I

    and-int/lit8 v0, v0, -0x5

    iput v0, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->bitField0_:I

    invoke-static {}, Ljava/util/Collections;->emptyList()Ljava/util/List;

    move-result-object v0

    iput-object v0, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->attribute_:Ljava/util/List;

    iget v0, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->bitField0_:I

    and-int/lit8 v0, v0, -0x9

    iput v0, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->bitField0_:I

    return-object p0
.end method

.method public final clearAttribute()Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;
    .locals 1

    invoke-static {}, Ljava/util/Collections;->emptyList()Ljava/util/List;

    move-result-object v0

    iput-object v0, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->attribute_:Ljava/util/List;

    iget v0, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->bitField0_:I

    and-int/lit8 v0, v0, -0x9

    iput v0, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->bitField0_:I

    return-object p0
.end method

.method public final clearFullSpell()Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;
    .locals 1

    invoke-static {}, Ljava/util/Collections;->emptyList()Ljava/util/List;

    move-result-object v0

    iput-object v0, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->fullSpell_:Ljava/util/List;

    iget v0, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->bitField0_:I

    and-int/lit8 v0, v0, -0x5

    iput v0, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->bitField0_:I

    return-object p0
.end method

.method public final clearPhrase()Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;
    .locals 1

    invoke-static {}, Ljava/util/Collections;->emptyList()Ljava/util/List;

    move-result-object v0

    iput-object v0, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->phrase_:Ljava/util/List;

    iget v0, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->bitField0_:I

    and-int/lit8 v0, v0, -0x2

    iput v0, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->bitField0_:I

    return-object p0
.end method

.method public final clearSpell()Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;
    .locals 1

    invoke-static {}, Ljava/util/Collections;->emptyList()Ljava/util/List;

    move-result-object v0

    iput-object v0, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->spell_:Ljava/util/List;

    iget v0, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->bitField0_:I

    and-int/lit8 v0, v0, -0x3

    iput v0, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->bitField0_:I

    return-object p0
.end method

.method public final bridge synthetic clone()Lcom/a/a/a$a;
    .locals 1

    invoke-virtual {p0}, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->clone()Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;

    move-result-object v0

    return-object v0
.end method

.method public final bridge synthetic clone()Lcom/a/a/h$a;
    .locals 1

    invoke-virtual {p0}, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->clone()Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;

    move-result-object v0

    return-object v0
.end method

.method public final bridge synthetic clone()Lcom/a/a/n$a;
    .locals 1

    invoke-virtual {p0}, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->clone()Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;

    move-result-object v0

    return-object v0
.end method

.method public final clone()Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;
    .locals 2

    invoke-static {}, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->create()Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;

    move-result-object v0

    invoke-virtual {p0}, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->buildPartial()Lcom/nuance/connect/proto/Prediction$PredictionResultV1;

    move-result-object v1

    invoke-virtual {v0, v1}, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->mergeFrom(Lcom/nuance/connect/proto/Prediction$PredictionResultV1;)Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;

    move-result-object v0

    return-object v0
.end method

.method public final bridge synthetic clone()Ljava/lang/Object;
    .locals 1
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/lang/CloneNotSupportedException;
        }
    .end annotation

    invoke-virtual {p0}, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->clone()Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;

    move-result-object v0

    return-object v0
.end method

.method public final getAttribute(I)I
    .locals 1

    iget-object v0, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->attribute_:Ljava/util/List;

    invoke-interface {v0, p1}, Ljava/util/List;->get(I)Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Ljava/lang/Integer;

    invoke-virtual {v0}, Ljava/lang/Integer;->intValue()I

    move-result v0

    return v0
.end method

.method public final getAttributeCount()I
    .locals 1

    iget-object v0, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->attribute_:Ljava/util/List;

    invoke-interface {v0}, Ljava/util/List;->size()I

    move-result v0

    return v0
.end method

.method public final getAttributeList()Ljava/util/List;
    .locals 1
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "()",
            "Ljava/util/List",
            "<",
            "Ljava/lang/Integer;",
            ">;"
        }
    .end annotation

    iget-object v0, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->attribute_:Ljava/util/List;

    invoke-static {v0}, Ljava/util/Collections;->unmodifiableList(Ljava/util/List;)Ljava/util/List;

    move-result-object v0

    return-object v0
.end method

.method public final bridge synthetic getDefaultInstanceForType()Lcom/a/a/h;
    .locals 1

    invoke-virtual {p0}, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->getDefaultInstanceForType()Lcom/nuance/connect/proto/Prediction$PredictionResultV1;

    move-result-object v0

    return-object v0
.end method

.method public final bridge synthetic getDefaultInstanceForType()Lcom/a/a/n;
    .locals 1

    invoke-virtual {p0}, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->getDefaultInstanceForType()Lcom/nuance/connect/proto/Prediction$PredictionResultV1;

    move-result-object v0

    return-object v0
.end method

.method public final getDefaultInstanceForType()Lcom/nuance/connect/proto/Prediction$PredictionResultV1;
    .locals 1

    invoke-static {}, Lcom/nuance/connect/proto/Prediction$PredictionResultV1;->getDefaultInstance()Lcom/nuance/connect/proto/Prediction$PredictionResultV1;

    move-result-object v0

    return-object v0
.end method

.method public final getFullSpell(I)I
    .locals 1

    iget-object v0, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->fullSpell_:Ljava/util/List;

    invoke-interface {v0, p1}, Ljava/util/List;->get(I)Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Ljava/lang/Integer;

    invoke-virtual {v0}, Ljava/lang/Integer;->intValue()I

    move-result v0

    return v0
.end method

.method public final getFullSpellCount()I
    .locals 1

    iget-object v0, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->fullSpell_:Ljava/util/List;

    invoke-interface {v0}, Ljava/util/List;->size()I

    move-result v0

    return v0
.end method

.method public final getFullSpellList()Ljava/util/List;
    .locals 1
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "()",
            "Ljava/util/List",
            "<",
            "Ljava/lang/Integer;",
            ">;"
        }
    .end annotation

    iget-object v0, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->fullSpell_:Ljava/util/List;

    invoke-static {v0}, Ljava/util/Collections;->unmodifiableList(Ljava/util/List;)Ljava/util/List;

    move-result-object v0

    return-object v0
.end method

.method public final getPhrase(I)I
    .locals 1

    iget-object v0, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->phrase_:Ljava/util/List;

    invoke-interface {v0, p1}, Ljava/util/List;->get(I)Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Ljava/lang/Integer;

    invoke-virtual {v0}, Ljava/lang/Integer;->intValue()I

    move-result v0

    return v0
.end method

.method public final getPhraseCount()I
    .locals 1

    iget-object v0, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->phrase_:Ljava/util/List;

    invoke-interface {v0}, Ljava/util/List;->size()I

    move-result v0

    return v0
.end method

.method public final getPhraseList()Ljava/util/List;
    .locals 1
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "()",
            "Ljava/util/List",
            "<",
            "Ljava/lang/Integer;",
            ">;"
        }
    .end annotation

    iget-object v0, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->phrase_:Ljava/util/List;

    invoke-static {v0}, Ljava/util/Collections;->unmodifiableList(Ljava/util/List;)Ljava/util/List;

    move-result-object v0

    return-object v0
.end method

.method public final getSpell(I)I
    .locals 1

    iget-object v0, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->spell_:Ljava/util/List;

    invoke-interface {v0, p1}, Ljava/util/List;->get(I)Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Ljava/lang/Integer;

    invoke-virtual {v0}, Ljava/lang/Integer;->intValue()I

    move-result v0

    return v0
.end method

.method public final getSpellCount()I
    .locals 1

    iget-object v0, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->spell_:Ljava/util/List;

    invoke-interface {v0}, Ljava/util/List;->size()I

    move-result v0

    return v0
.end method

.method public final getSpellList()Ljava/util/List;
    .locals 1
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "()",
            "Ljava/util/List",
            "<",
            "Ljava/lang/Integer;",
            ">;"
        }
    .end annotation

    iget-object v0, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->spell_:Ljava/util/List;

    invoke-static {v0}, Ljava/util/Collections;->unmodifiableList(Ljava/util/List;)Ljava/util/List;

    move-result-object v0

    return-object v0
.end method

.method public final isInitialized()Z
    .locals 1

    const/4 v0, 0x1

    return v0
.end method

.method public final bridge synthetic mergeFrom(Lcom/a/a/d;Lcom/a/a/f;)Lcom/a/a/a$a;
    .locals 1
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/io/IOException;
        }
    .end annotation

    invoke-virtual {p0, p1, p2}, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->mergeFrom(Lcom/a/a/d;Lcom/a/a/f;)Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;

    move-result-object v0

    return-object v0
.end method

.method public final bridge synthetic mergeFrom(Lcom/a/a/h;)Lcom/a/a/h$a;
    .locals 1

    check-cast p1, Lcom/nuance/connect/proto/Prediction$PredictionResultV1;

    invoke-virtual {p0, p1}, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->mergeFrom(Lcom/nuance/connect/proto/Prediction$PredictionResultV1;)Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;

    move-result-object v0

    return-object v0
.end method

.method public final bridge synthetic mergeFrom(Lcom/a/a/d;Lcom/a/a/f;)Lcom/a/a/n$a;
    .locals 1
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/io/IOException;
        }
    .end annotation

    invoke-virtual {p0, p1, p2}, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->mergeFrom(Lcom/a/a/d;Lcom/a/a/f;)Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;

    move-result-object v0

    return-object v0
.end method

.method public final mergeFrom(Lcom/a/a/d;Lcom/a/a/f;)Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;
    .locals 4
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/io/IOException;
        }
    .end annotation

    .prologue
    .line 0
    const/4 v2, 0x0

    :try_start_0
    sget-object v0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1;->PARSER:Lcom/a/a/p;

    invoke-interface {v0, p1, p2}, Lcom/a/a/p;->parsePartialFrom(Lcom/a/a/d;Lcom/a/a/f;)Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1;
    :try_end_0
    .catch Lcom/a/a/j; {:try_start_0 .. :try_end_0} :catch_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_1

    if-eqz v0, :cond_0

    invoke-virtual {p0, v0}, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->mergeFrom(Lcom/nuance/connect/proto/Prediction$PredictionResultV1;)Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;

    :cond_0
    return-object p0

    :catch_0
    move-exception v0

    move-object v1, v0

    .line 1000
    :try_start_1
    iget-object v0, v1, Lcom/a/a/j;->a:Lcom/a/a/n;

    .line 0
    check-cast v0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1;
    :try_end_1
    .catchall {:try_start_1 .. :try_end_1} :catchall_1

    :try_start_2
    throw v1
    :try_end_2
    .catchall {:try_start_2 .. :try_end_2} :catchall_0

    :catchall_0
    move-exception v1

    move-object v3, v1

    move-object v1, v0

    move-object v0, v3

    :goto_0
    if-eqz v1, :cond_1

    invoke-virtual {p0, v1}, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->mergeFrom(Lcom/nuance/connect/proto/Prediction$PredictionResultV1;)Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;

    :cond_1
    throw v0

    :catchall_1
    move-exception v0

    move-object v1, v2

    goto :goto_0
.end method

.method public final mergeFrom(Lcom/nuance/connect/proto/Prediction$PredictionResultV1;)Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;
    .locals 2

    invoke-static {}, Lcom/nuance/connect/proto/Prediction$PredictionResultV1;->getDefaultInstance()Lcom/nuance/connect/proto/Prediction$PredictionResultV1;

    move-result-object v0

    if-ne p1, v0, :cond_1

    :cond_0
    :goto_0
    return-object p0

    :cond_1
    invoke-static {p1}, Lcom/nuance/connect/proto/Prediction$PredictionResultV1;->access$2100(Lcom/nuance/connect/proto/Prediction$PredictionResultV1;)Ljava/util/List;

    move-result-object v0

    invoke-interface {v0}, Ljava/util/List;->isEmpty()Z

    move-result v0

    if-nez v0, :cond_2

    iget-object v0, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->phrase_:Ljava/util/List;

    invoke-interface {v0}, Ljava/util/List;->isEmpty()Z

    move-result v0

    if-eqz v0, :cond_5

    invoke-static {p1}, Lcom/nuance/connect/proto/Prediction$PredictionResultV1;->access$2100(Lcom/nuance/connect/proto/Prediction$PredictionResultV1;)Ljava/util/List;

    move-result-object v0

    iput-object v0, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->phrase_:Ljava/util/List;

    iget v0, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->bitField0_:I

    and-int/lit8 v0, v0, -0x2

    iput v0, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->bitField0_:I

    :cond_2
    :goto_1
    invoke-static {p1}, Lcom/nuance/connect/proto/Prediction$PredictionResultV1;->access$2200(Lcom/nuance/connect/proto/Prediction$PredictionResultV1;)Ljava/util/List;

    move-result-object v0

    invoke-interface {v0}, Ljava/util/List;->isEmpty()Z

    move-result v0

    if-nez v0, :cond_3

    iget-object v0, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->spell_:Ljava/util/List;

    invoke-interface {v0}, Ljava/util/List;->isEmpty()Z

    move-result v0

    if-eqz v0, :cond_6

    invoke-static {p1}, Lcom/nuance/connect/proto/Prediction$PredictionResultV1;->access$2200(Lcom/nuance/connect/proto/Prediction$PredictionResultV1;)Ljava/util/List;

    move-result-object v0

    iput-object v0, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->spell_:Ljava/util/List;

    iget v0, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->bitField0_:I

    and-int/lit8 v0, v0, -0x3

    iput v0, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->bitField0_:I

    :cond_3
    :goto_2
    invoke-static {p1}, Lcom/nuance/connect/proto/Prediction$PredictionResultV1;->access$2300(Lcom/nuance/connect/proto/Prediction$PredictionResultV1;)Ljava/util/List;

    move-result-object v0

    invoke-interface {v0}, Ljava/util/List;->isEmpty()Z

    move-result v0

    if-nez v0, :cond_4

    iget-object v0, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->fullSpell_:Ljava/util/List;

    invoke-interface {v0}, Ljava/util/List;->isEmpty()Z

    move-result v0

    if-eqz v0, :cond_7

    invoke-static {p1}, Lcom/nuance/connect/proto/Prediction$PredictionResultV1;->access$2300(Lcom/nuance/connect/proto/Prediction$PredictionResultV1;)Ljava/util/List;

    move-result-object v0

    iput-object v0, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->fullSpell_:Ljava/util/List;

    iget v0, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->bitField0_:I

    and-int/lit8 v0, v0, -0x5

    iput v0, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->bitField0_:I

    :cond_4
    :goto_3
    invoke-static {p1}, Lcom/nuance/connect/proto/Prediction$PredictionResultV1;->access$2400(Lcom/nuance/connect/proto/Prediction$PredictionResultV1;)Ljava/util/List;

    move-result-object v0

    invoke-interface {v0}, Ljava/util/List;->isEmpty()Z

    move-result v0

    if-nez v0, :cond_0

    iget-object v0, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->attribute_:Ljava/util/List;

    invoke-interface {v0}, Ljava/util/List;->isEmpty()Z

    move-result v0

    if-eqz v0, :cond_8

    invoke-static {p1}, Lcom/nuance/connect/proto/Prediction$PredictionResultV1;->access$2400(Lcom/nuance/connect/proto/Prediction$PredictionResultV1;)Ljava/util/List;

    move-result-object v0

    iput-object v0, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->attribute_:Ljava/util/List;

    iget v0, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->bitField0_:I

    and-int/lit8 v0, v0, -0x9

    iput v0, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->bitField0_:I

    goto :goto_0

    :cond_5
    invoke-direct {p0}, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->ensurePhraseIsMutable()V

    iget-object v0, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->phrase_:Ljava/util/List;

    invoke-static {p1}, Lcom/nuance/connect/proto/Prediction$PredictionResultV1;->access$2100(Lcom/nuance/connect/proto/Prediction$PredictionResultV1;)Ljava/util/List;

    move-result-object v1

    invoke-interface {v0, v1}, Ljava/util/List;->addAll(Ljava/util/Collection;)Z

    goto :goto_1

    :cond_6
    invoke-direct {p0}, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->ensureSpellIsMutable()V

    iget-object v0, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->spell_:Ljava/util/List;

    invoke-static {p1}, Lcom/nuance/connect/proto/Prediction$PredictionResultV1;->access$2200(Lcom/nuance/connect/proto/Prediction$PredictionResultV1;)Ljava/util/List;

    move-result-object v1

    invoke-interface {v0, v1}, Ljava/util/List;->addAll(Ljava/util/Collection;)Z

    goto :goto_2

    :cond_7
    invoke-direct {p0}, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->ensureFullSpellIsMutable()V

    iget-object v0, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->fullSpell_:Ljava/util/List;

    invoke-static {p1}, Lcom/nuance/connect/proto/Prediction$PredictionResultV1;->access$2300(Lcom/nuance/connect/proto/Prediction$PredictionResultV1;)Ljava/util/List;

    move-result-object v1

    invoke-interface {v0, v1}, Ljava/util/List;->addAll(Ljava/util/Collection;)Z

    goto :goto_3

    :cond_8
    invoke-direct {p0}, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->ensureAttributeIsMutable()V

    iget-object v0, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->attribute_:Ljava/util/List;

    invoke-static {p1}, Lcom/nuance/connect/proto/Prediction$PredictionResultV1;->access$2400(Lcom/nuance/connect/proto/Prediction$PredictionResultV1;)Ljava/util/List;

    move-result-object v1

    invoke-interface {v0, v1}, Ljava/util/List;->addAll(Ljava/util/Collection;)Z

    goto/16 :goto_0
.end method

.method public final setAttribute(II)Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;
    .locals 2

    invoke-direct {p0}, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->ensureAttributeIsMutable()V

    iget-object v0, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->attribute_:Ljava/util/List;

    invoke-static {p2}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v1

    invoke-interface {v0, p1, v1}, Ljava/util/List;->set(ILjava/lang/Object;)Ljava/lang/Object;

    return-object p0
.end method

.method public final setFullSpell(II)Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;
    .locals 2

    invoke-direct {p0}, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->ensureFullSpellIsMutable()V

    iget-object v0, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->fullSpell_:Ljava/util/List;

    invoke-static {p2}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v1

    invoke-interface {v0, p1, v1}, Ljava/util/List;->set(ILjava/lang/Object;)Ljava/lang/Object;

    return-object p0
.end method

.method public final setPhrase(II)Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;
    .locals 2

    invoke-direct {p0}, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->ensurePhraseIsMutable()V

    iget-object v0, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->phrase_:Ljava/util/List;

    invoke-static {p2}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v1

    invoke-interface {v0, p1, v1}, Ljava/util/List;->set(ILjava/lang/Object;)Ljava/lang/Object;

    return-object p0
.end method

.method public final setSpell(II)Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;
    .locals 2

    invoke-direct {p0}, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->ensureSpellIsMutable()V

    iget-object v0, p0, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->spell_:Ljava/util/List;

    invoke-static {p2}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v1

    invoke-interface {v0, p1, v1}, Ljava/util/List;->set(ILjava/lang/Object;)Ljava/lang/Object;

    return-object p0
.end method
