.class final Lcom/nuance/swypeconnect/ac/ACDLMEventInternal$NewWordSyllablePairDlmEventImpl;
.super Lcom/nuance/swypeconnect/ac/ACDLMEventInternal;

# interfaces
.implements Lcom/nuance/swypeconnect/ac/ACDLMEventData$ACNewWordSyllablePairDLMEvent;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/nuance/swypeconnect/ac/ACDLMEventInternal;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x18
    name = "NewWordSyllablePairDlmEventImpl"
.end annotation


# instance fields
.field public final categoryId:I

.field public final languageId:I

.field public final quality:B

.field public final sylCount:B

.field public final syllables:[B

.field public final useCount:I

.field public final word:[B

.field public final wordLen:I


# direct methods
.method constructor <init>(Ljava/nio/ByteBuffer;I)V
    .locals 3

    const/16 v1, 0x40

    const/4 v2, 0x0

    const/4 v0, 0x0

    invoke-direct {p0, p2, v0}, Lcom/nuance/swypeconnect/ac/ACDLMEventInternal;-><init>(ILcom/nuance/swypeconnect/ac/ACDLMEventInternal$1;)V

    new-array v0, v1, [B

    iput-object v0, p0, Lcom/nuance/swypeconnect/ac/ACDLMEventInternal$NewWordSyllablePairDlmEventImpl;->word:[B

    new-array v0, v1, [B

    iput-object v0, p0, Lcom/nuance/swypeconnect/ac/ACDLMEventInternal$NewWordSyllablePairDlmEventImpl;->syllables:[B

    invoke-virtual {p1}, Ljava/nio/ByteBuffer;->getShort()S

    move-result v0

    iput v0, p0, Lcom/nuance/swypeconnect/ac/ACDLMEventInternal$NewWordSyllablePairDlmEventImpl;->categoryId:I

    invoke-virtual {p1}, Ljava/nio/ByteBuffer;->getShort()S

    move-result v0

    iput v0, p0, Lcom/nuance/swypeconnect/ac/ACDLMEventInternal$NewWordSyllablePairDlmEventImpl;->languageId:I

    invoke-virtual {p1}, Ljava/nio/ByteBuffer;->getShort()S

    move-result v0

    iput v0, p0, Lcom/nuance/swypeconnect/ac/ACDLMEventInternal$NewWordSyllablePairDlmEventImpl;->useCount:I

    invoke-virtual {p1}, Ljava/nio/ByteBuffer;->get()B

    move-result v0

    iput-byte v0, p0, Lcom/nuance/swypeconnect/ac/ACDLMEventInternal$NewWordSyllablePairDlmEventImpl;->quality:B

    invoke-virtual {p1}, Ljava/nio/ByteBuffer;->get()B

    move-result v0

    and-int/lit16 v0, v0, 0xff

    iput v0, p0, Lcom/nuance/swypeconnect/ac/ACDLMEventInternal$NewWordSyllablePairDlmEventImpl;->wordLen:I

    invoke-virtual {p1}, Ljava/nio/ByteBuffer;->get()B

    move-result v0

    iput-byte v0, p0, Lcom/nuance/swypeconnect/ac/ACDLMEventInternal$NewWordSyllablePairDlmEventImpl;->sylCount:B

    iget-object v0, p0, Lcom/nuance/swypeconnect/ac/ACDLMEventInternal$NewWordSyllablePairDlmEventImpl;->word:[B

    iget v1, p0, Lcom/nuance/swypeconnect/ac/ACDLMEventInternal$NewWordSyllablePairDlmEventImpl;->wordLen:I

    mul-int/lit8 v1, v1, 0x2

    invoke-virtual {p1, v0, v2, v1}, Ljava/nio/ByteBuffer;->get([BII)Ljava/nio/ByteBuffer;

    iget-object v0, p0, Lcom/nuance/swypeconnect/ac/ACDLMEventInternal$NewWordSyllablePairDlmEventImpl;->syllables:[B

    iget-byte v1, p0, Lcom/nuance/swypeconnect/ac/ACDLMEventInternal$NewWordSyllablePairDlmEventImpl;->sylCount:B

    mul-int/lit8 v1, v1, 0x2

    invoke-virtual {p1, v0, v2, v1}, Ljava/nio/ByteBuffer;->get([BII)Ljava/nio/ByteBuffer;

    return-void
.end method


# virtual methods
.method public final getCategoryId()I
    .locals 1

    iget v0, p0, Lcom/nuance/swypeconnect/ac/ACDLMEventInternal$NewWordSyllablePairDlmEventImpl;->categoryId:I

    return v0
.end method

.method public final getLanguageId()I
    .locals 1

    iget v0, p0, Lcom/nuance/swypeconnect/ac/ACDLMEventInternal$NewWordSyllablePairDlmEventImpl;->languageId:I

    return v0
.end method

.method public final getSyllables()Ljava/lang/String;
    .locals 5

    new-instance v0, Ljava/lang/String;

    iget-object v1, p0, Lcom/nuance/swypeconnect/ac/ACDLMEventInternal$NewWordSyllablePairDlmEventImpl;->syllables:[B

    const/4 v2, 0x0

    iget-byte v3, p0, Lcom/nuance/swypeconnect/ac/ACDLMEventInternal$NewWordSyllablePairDlmEventImpl;->sylCount:B

    mul-int/lit8 v3, v3, 0x2

    const-string/jumbo v4, "UTF-16"

    invoke-static {v4}, Ljava/nio/charset/Charset;->forName(Ljava/lang/String;)Ljava/nio/charset/Charset;

    move-result-object v4

    invoke-direct {v0, v1, v2, v3, v4}, Ljava/lang/String;-><init>([BIILjava/nio/charset/Charset;)V

    return-object v0
.end method

.method public final getWord()Ljava/lang/String;
    .locals 5

    new-instance v0, Ljava/lang/String;

    iget-object v1, p0, Lcom/nuance/swypeconnect/ac/ACDLMEventInternal$NewWordSyllablePairDlmEventImpl;->word:[B

    const/4 v2, 0x0

    iget v3, p0, Lcom/nuance/swypeconnect/ac/ACDLMEventInternal$NewWordSyllablePairDlmEventImpl;->wordLen:I

    mul-int/lit8 v3, v3, 0x2

    const-string/jumbo v4, "UTF-16"

    invoke-static {v4}, Ljava/nio/charset/Charset;->forName(Ljava/lang/String;)Ljava/nio/charset/Charset;

    move-result-object v4

    invoke-direct {v0, v1, v2, v3, v4}, Ljava/lang/String;-><init>([BIILjava/nio/charset/Charset;)V

    return-object v0
.end method

.method public final toString()Ljava/lang/String;
    .locals 3

    new-instance v0, Ljava/lang/StringBuffer;

    invoke-direct {v0}, Ljava/lang/StringBuffer;-><init>()V

    const-class v1, Lcom/nuance/swypeconnect/ac/ACDLMEventData$ACNewWordSyllablePairDLMEvent;

    invoke-virtual {v1}, Ljava/lang/Class;->getSimpleName()Ljava/lang/String;

    move-result-object v1

    invoke-virtual {v0, v1}, Ljava/lang/StringBuffer;->append(Ljava/lang/String;)Ljava/lang/StringBuffer;

    move-result-object v1

    const-string/jumbo v2, "\nCategory="

    invoke-virtual {v1, v2}, Ljava/lang/StringBuffer;->append(Ljava/lang/String;)Ljava/lang/StringBuffer;

    move-result-object v1

    iget v2, p0, Lcom/nuance/swypeconnect/ac/ACDLMEventInternal$NewWordSyllablePairDlmEventImpl;->categoryId:I

    invoke-virtual {v1, v2}, Ljava/lang/StringBuffer;->append(I)Ljava/lang/StringBuffer;

    move-result-object v1

    const-string/jumbo v2, "\nLanguage="

    invoke-virtual {v1, v2}, Ljava/lang/StringBuffer;->append(Ljava/lang/String;)Ljava/lang/StringBuffer;

    move-result-object v1

    iget v2, p0, Lcom/nuance/swypeconnect/ac/ACDLMEventInternal$NewWordSyllablePairDlmEventImpl;->languageId:I

    invoke-virtual {v1, v2}, Ljava/lang/StringBuffer;->append(I)Ljava/lang/StringBuffer;

    move-result-object v1

    const-string/jumbo v2, "\nUse Count="

    invoke-virtual {v1, v2}, Ljava/lang/StringBuffer;->append(Ljava/lang/String;)Ljava/lang/StringBuffer;

    move-result-object v1

    iget v2, p0, Lcom/nuance/swypeconnect/ac/ACDLMEventInternal$NewWordSyllablePairDlmEventImpl;->useCount:I

    invoke-virtual {v1, v2}, Ljava/lang/StringBuffer;->append(I)Ljava/lang/StringBuffer;

    move-result-object v1

    const-string/jumbo v2, "\nQuality="

    invoke-virtual {v1, v2}, Ljava/lang/StringBuffer;->append(Ljava/lang/String;)Ljava/lang/StringBuffer;

    move-result-object v1

    iget-byte v2, p0, Lcom/nuance/swypeconnect/ac/ACDLMEventInternal$NewWordSyllablePairDlmEventImpl;->quality:B

    invoke-virtual {v1, v2}, Ljava/lang/StringBuffer;->append(I)Ljava/lang/StringBuffer;

    move-result-object v1

    const-string/jumbo v2, "\nWord Len="

    invoke-virtual {v1, v2}, Ljava/lang/StringBuffer;->append(Ljava/lang/String;)Ljava/lang/StringBuffer;

    move-result-object v1

    iget v2, p0, Lcom/nuance/swypeconnect/ac/ACDLMEventInternal$NewWordSyllablePairDlmEventImpl;->wordLen:I

    invoke-virtual {v1, v2}, Ljava/lang/StringBuffer;->append(I)Ljava/lang/StringBuffer;

    move-result-object v1

    const-string/jumbo v2, "\nWord="

    invoke-virtual {v1, v2}, Ljava/lang/StringBuffer;->append(Ljava/lang/String;)Ljava/lang/StringBuffer;

    move-result-object v1

    invoke-virtual {p0}, Lcom/nuance/swypeconnect/ac/ACDLMEventInternal$NewWordSyllablePairDlmEventImpl;->getWord()Ljava/lang/String;

    move-result-object v2

    invoke-virtual {v1, v2}, Ljava/lang/StringBuffer;->append(Ljava/lang/String;)Ljava/lang/StringBuffer;

    move-result-object v1

    const-string/jumbo v2, "\nSyllable Len="

    invoke-virtual {v1, v2}, Ljava/lang/StringBuffer;->append(Ljava/lang/String;)Ljava/lang/StringBuffer;

    move-result-object v1

    iget v2, p0, Lcom/nuance/swypeconnect/ac/ACDLMEventInternal$NewWordSyllablePairDlmEventImpl;->wordLen:I

    invoke-virtual {v1, v2}, Ljava/lang/StringBuffer;->append(I)Ljava/lang/StringBuffer;

    move-result-object v1

    const-string/jumbo v2, "\nSyllables="

    invoke-virtual {v1, v2}, Ljava/lang/StringBuffer;->append(Ljava/lang/String;)Ljava/lang/StringBuffer;

    move-result-object v1

    invoke-virtual {p0}, Lcom/nuance/swypeconnect/ac/ACDLMEventInternal$NewWordSyllablePairDlmEventImpl;->getSyllables()Ljava/lang/String;

    move-result-object v2

    invoke-virtual {v1, v2}, Ljava/lang/StringBuffer;->append(Ljava/lang/String;)Ljava/lang/StringBuffer;

    invoke-virtual {v0}, Ljava/lang/StringBuffer;->toString()Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method
