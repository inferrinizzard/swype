.class public Lcom/nuance/swype/input/AutospaceHandler;
.super Ljava/lang/Object;
.source "AutospaceHandler.java"


# instance fields
.field private final charUtils:Lcom/nuance/swype/util/CharacterUtilities;

.field private forceSingleAutospace:Z

.field private final ime:Lcom/nuance/swype/input/IME;

.field private final imeApp:Lcom/nuance/swype/input/IMEApplication;

.field private noAutospace:Z

.field private noAutospaceLanguage:Z

.field private suppressSingleAutospace:Z

.field private webSearchUrlBoxAutoSpace:Z


# direct methods
.method public constructor <init>(Lcom/nuance/swype/input/IME;)V
    .locals 1
    .param p1, "ime"    # Lcom/nuance/swype/input/IME;

    .prologue
    .line 54
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 55
    iput-object p1, p0, Lcom/nuance/swype/input/AutospaceHandler;->ime:Lcom/nuance/swype/input/IME;

    .line 56
    invoke-virtual {p1}, Lcom/nuance/swype/input/IME;->getApplication()Landroid/app/Application;

    move-result-object v0

    check-cast v0, Lcom/nuance/swype/input/IMEApplication;

    iput-object v0, p0, Lcom/nuance/swype/input/AutospaceHandler;->imeApp:Lcom/nuance/swype/input/IMEApplication;

    .line 57
    iget-object v0, p0, Lcom/nuance/swype/input/AutospaceHandler;->imeApp:Lcom/nuance/swype/input/IMEApplication;

    invoke-virtual {v0}, Lcom/nuance/swype/input/IMEApplication;->getCharacterUtilities()Lcom/nuance/swype/util/CharacterUtilities;

    move-result-object v0

    iput-object v0, p0, Lcom/nuance/swype/input/AutospaceHandler;->charUtils:Lcom/nuance/swype/util/CharacterUtilities;

    .line 58
    return-void
.end method

.method private autoSpaceEnabled()Z
    .locals 1

    .prologue
    .line 281
    iget-boolean v0, p0, Lcom/nuance/swype/input/AutospaceHandler;->noAutospace:Z

    if-nez v0, :cond_1

    iget-boolean v0, p0, Lcom/nuance/swype/input/AutospaceHandler;->suppressSingleAutospace:Z

    if-eqz v0, :cond_0

    iget-boolean v0, p0, Lcom/nuance/swype/input/AutospaceHandler;->forceSingleAutospace:Z

    if-eqz v0, :cond_1

    :cond_0
    const/4 v0, 0x1

    :goto_0
    return v0

    :cond_1
    const/4 v0, 0x0

    goto :goto_0
.end method

.method private sendAutospace()V
    .locals 1

    .prologue
    .line 276
    iget-object v0, p0, Lcom/nuance/swype/input/AutospaceHandler;->ime:Lcom/nuance/swype/input/IME;

    invoke-virtual {v0}, Lcom/nuance/swype/input/IME;->sendSpace()V

    .line 277
    const/4 v0, 0x0

    iput-boolean v0, p0, Lcom/nuance/swype/input/AutospaceHandler;->forceSingleAutospace:Z

    .line 278
    return-void
.end method

.method private sendAutospaceWCheck()Z
    .locals 2

    .prologue
    .line 263
    const/4 v0, 0x0

    .line 264
    .local v0, "isAutoSpaceSent":Z
    invoke-virtual {p0}, Lcom/nuance/swype/input/AutospaceHandler;->shouldAutoSpace()Z

    move-result v1

    if-eqz v1, :cond_0

    .line 265
    invoke-direct {p0}, Lcom/nuance/swype/input/AutospaceHandler;->sendAutospace()V

    .line 266
    const/4 v0, 0x1

    .line 268
    :cond_0
    const/4 v1, 0x0

    iput-boolean v1, p0, Lcom/nuance/swype/input/AutospaceHandler;->suppressSingleAutospace:Z

    .line 269
    return v0
.end method

.method private shouldAutoSpace(Ljava/lang/CharSequence;I)Z
    .locals 5
    .param p1, "cs"    # Ljava/lang/CharSequence;
    .param p2, "off"    # I

    .prologue
    const/4 v1, 0x1

    const/4 v2, 0x0

    .line 314
    if-lez p2, :cond_3

    invoke-interface {p1}, Ljava/lang/CharSequence;->length()I

    move-result v3

    if-gt p2, v3, :cond_3

    .line 315
    add-int/lit8 v3, p2, -0x1

    invoke-interface {p1, v3}, Ljava/lang/CharSequence;->charAt(I)C

    move-result v0

    .line 316
    .local v0, "c":C
    iget-object v3, p0, Lcom/nuance/swype/input/AutospaceHandler;->charUtils:Lcom/nuance/swype/util/CharacterUtilities;

    .line 1245
    invoke-static {v0}, Lcom/nuance/swype/util/CharacterUtilities;->isControl(C)Z

    move-result v4

    if-nez v4, :cond_0

    invoke-static {v0}, Lcom/nuance/swype/util/CharacterUtilities;->isWhiteSpace(C)Z

    move-result v4

    if-nez v4, :cond_0

    .line 1246
    invoke-virtual {v3, v0}, Lcom/nuance/swype/util/CharacterUtilities;->isEmbeddedPunct(C)Z

    move-result v4

    if-nez v4, :cond_0

    .line 2164
    iget-object v3, v3, Lcom/nuance/swype/util/CharacterUtilities;->leftPunct:[C

    invoke-static {v3, v0}, Ljava/util/Arrays;->binarySearch([CC)I

    move-result v3

    if-ltz v3, :cond_1

    move v3, v1

    .line 1246
    :goto_0
    if-eqz v3, :cond_2

    :cond_0
    move v3, v1

    .line 316
    :goto_1
    if-nez v3, :cond_3

    .line 320
    .end local v0    # "c":C
    :goto_2
    return v1

    .restart local v0    # "c":C
    :cond_1
    move v3, v2

    .line 2164
    goto :goto_0

    :cond_2
    move v3, v2

    .line 1246
    goto :goto_1

    .end local v0    # "c":C
    :cond_3
    move v1, v2

    .line 320
    goto :goto_2
.end method

.method private shouldAutospaceText(Ljava/lang/CharSequence;I)Z
    .locals 9
    .param p1, "cs"    # Ljava/lang/CharSequence;
    .param p2, "lastInputType"    # I

    .prologue
    const/16 v8, 0x20

    const/4 v7, 0x2

    const/4 v5, 0x1

    const/4 v4, 0x0

    .line 331
    const/4 v2, 0x1

    .line 332
    .local v2, "outsideWord":Z
    iget-object v3, p0, Lcom/nuance/swype/input/AutospaceHandler;->ime:Lcom/nuance/swype/input/IME;

    invoke-virtual {v3}, Lcom/nuance/swype/input/IME;->getCurrentInputConnection()Landroid/view/inputmethod/InputConnection;

    move-result-object v1

    .line 333
    .local v1, "ic":Landroid/view/inputmethod/InputConnection;
    if-eqz v1, :cond_2

    .line 334
    if-eq p2, v7, :cond_0

    if-eq p2, v5, :cond_0

    const/4 v3, 0x4

    if-ne p2, v3, :cond_3

    :cond_0
    move v2, v5

    .line 337
    :goto_0
    if-nez v2, :cond_2

    .line 338
    invoke-interface {v1, v5, v4}, Landroid/view/inputmethod/InputConnection;->getTextAfterCursor(II)Ljava/lang/CharSequence;

    move-result-object v0

    .line 339
    .local v0, "charAfter":Ljava/lang/CharSequence;
    invoke-static {v0}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    move-result v3

    if-nez v3, :cond_1

    .line 340
    invoke-interface {v0, v4}, Ljava/lang/CharSequence;->charAt(I)C

    move-result v3

    invoke-static {v3}, Ljava/lang/Character;->isLetter(C)Z

    move-result v3

    if-nez v3, :cond_4

    :cond_1
    move v3, v5

    :goto_1
    or-int/lit8 v2, v3, 0x0

    .line 344
    .end local v0    # "charAfter":Ljava/lang/CharSequence;
    :cond_2
    if-eqz p1, :cond_6

    if-eqz v2, :cond_6

    .line 346
    invoke-interface {p1}, Ljava/lang/CharSequence;->length()I

    move-result v3

    if-lez v3, :cond_6

    .line 347
    invoke-interface {p1, v4}, Ljava/lang/CharSequence;->charAt(I)C

    move-result v3

    if-eq v3, v8, :cond_6

    iget-object v3, p0, Lcom/nuance/swype/input/AutospaceHandler;->charUtils:Lcom/nuance/swype/util/CharacterUtilities;

    .line 348
    invoke-interface {p1, v4}, Ljava/lang/CharSequence;->charAt(I)C

    move-result v6

    invoke-virtual {v3, v6}, Lcom/nuance/swype/util/CharacterUtilities;->isEmbeddedPunct(C)Z

    move-result v3

    if-nez v3, :cond_6

    iget-object v3, p0, Lcom/nuance/swype/input/AutospaceHandler;->charUtils:Lcom/nuance/swype/util/CharacterUtilities;

    .line 3111
    invoke-interface {p1}, Ljava/lang/CharSequence;->length()I

    move-result v6

    if-ne v6, v7, :cond_5

    .line 3112
    invoke-interface {p1, v4}, Ljava/lang/CharSequence;->charAt(I)C

    move-result v6

    invoke-virtual {v3, v6}, Lcom/nuance/swype/util/CharacterUtilities;->isTerminalPunctuation(I)Z

    move-result v3

    if-eqz v3, :cond_5

    .line 3113
    invoke-interface {p1, v5}, Ljava/lang/CharSequence;->charAt(I)C

    move-result v3

    if-ne v3, v8, :cond_5

    move v3, v5

    .line 349
    :goto_2
    if-nez v3, :cond_6

    .line 350
    invoke-virtual {p0}, Lcom/nuance/swype/input/AutospaceHandler;->shouldAutoSpace()Z

    move-result v3

    if-eqz v3, :cond_6

    :goto_3
    return v5

    :cond_3
    move v2, v4

    .line 334
    goto :goto_0

    .restart local v0    # "charAfter":Ljava/lang/CharSequence;
    :cond_4
    move v3, v4

    .line 340
    goto :goto_1

    .end local v0    # "charAfter":Ljava/lang/CharSequence;
    :cond_5
    move v3, v4

    .line 3113
    goto :goto_2

    :cond_6
    move v5, v4

    .line 350
    goto :goto_3
.end method


# virtual methods
.method public onCharKey(I)V
    .locals 3
    .param p1, "charKey"    # I

    .prologue
    const/4 v1, 0x0

    .line 116
    iget-object v0, p0, Lcom/nuance/swype/input/AutospaceHandler;->charUtils:Lcom/nuance/swype/util/CharacterUtilities;

    int-to-char v2, p1

    invoke-virtual {v0, v2}, Lcom/nuance/swype/util/CharacterUtilities;->isEmbeddedPunct(C)Z

    move-result v0

    if-nez v0, :cond_0

    iget-object v0, p0, Lcom/nuance/swype/input/AutospaceHandler;->charUtils:Lcom/nuance/swype/util/CharacterUtilities;

    int-to-char v2, p1

    invoke-virtual {v0, v2}, Lcom/nuance/swype/util/CharacterUtilities;->isWordCompounder(I)Z

    move-result v0

    if-nez v0, :cond_0

    const/4 v0, 0x1

    :goto_0
    iput-boolean v0, p0, Lcom/nuance/swype/input/AutospaceHandler;->forceSingleAutospace:Z

    .line 117
    iput-boolean v1, p0, Lcom/nuance/swype/input/AutospaceHandler;->suppressSingleAutospace:Z

    .line 118
    return-void

    :cond_0
    move v0, v1

    .line 116
    goto :goto_0
.end method

.method public onKey(I)Z
    .locals 3
    .param p1, "keyCode"    # I

    .prologue
    const/4 v0, 0x1

    const/4 v1, 0x0

    .line 127
    const/16 v2, -0x6a

    if-ne p1, v2, :cond_0

    .line 128
    iput-boolean v1, p0, Lcom/nuance/swype/input/AutospaceHandler;->forceSingleAutospace:Z

    .line 129
    iput-boolean v0, p0, Lcom/nuance/swype/input/AutospaceHandler;->suppressSingleAutospace:Z

    .line 144
    :goto_0
    return v0

    .line 131
    :cond_0
    const/16 v2, 0xb53

    if-ne p1, v2, :cond_1

    .line 132
    iput-boolean v1, p0, Lcom/nuance/swype/input/AutospaceHandler;->forceSingleAutospace:Z

    .line 133
    iput-boolean v1, p0, Lcom/nuance/swype/input/AutospaceHandler;->suppressSingleAutospace:Z

    goto :goto_0

    .line 135
    :cond_1
    const/16 v2, 0x8

    if-ne p1, v2, :cond_4

    .line 136
    iput-boolean v1, p0, Lcom/nuance/swype/input/AutospaceHandler;->forceSingleAutospace:Z

    .line 137
    iget-object v2, p0, Lcom/nuance/swype/input/AutospaceHandler;->ime:Lcom/nuance/swype/input/IME;

    invoke-virtual {v2}, Lcom/nuance/swype/input/IME;->isEditorComposingText()Z

    move-result v2

    if-nez v2, :cond_3

    :goto_1
    iput-boolean v0, p0, Lcom/nuance/swype/input/AutospaceHandler;->suppressSingleAutospace:Z

    :cond_2
    :goto_2
    move v0, v1

    .line 144
    goto :goto_0

    :cond_3
    move v0, v1

    .line 137
    goto :goto_1

    .line 138
    :cond_4
    const/16 v2, 0x20

    if-ne p1, v2, :cond_5

    .line 139
    iput-boolean v1, p0, Lcom/nuance/swype/input/AutospaceHandler;->forceSingleAutospace:Z

    goto :goto_2

    .line 140
    :cond_5
    const/16 v2, 0xa

    if-ne p1, v2, :cond_2

    .line 141
    iput-boolean v1, p0, Lcom/nuance/swype/input/AutospaceHandler;->forceSingleAutospace:Z

    .line 142
    iput-boolean v0, p0, Lcom/nuance/swype/input/AutospaceHandler;->suppressSingleAutospace:Z

    goto :goto_2
.end method

.method public onSelectCandidate(Lcom/nuance/input/swypecorelib/Candidates$Source;Lcom/nuance/input/swypecorelib/WordCandidate;Lcom/nuance/input/swypecorelib/WordCandidate;I)V
    .locals 14
    .param p1, "source"    # Lcom/nuance/input/swypecorelib/Candidates$Source;
    .param p2, "defaultCandidate"    # Lcom/nuance/input/swypecorelib/WordCandidate;
    .param p3, "selectedCandidate"    # Lcom/nuance/input/swypecorelib/WordCandidate;
    .param p4, "lastInput"    # I

    .prologue
    .line 177
    sget-object v12, Lcom/nuance/input/swypecorelib/Candidates$Source;->NEXT_WORD_PREDICTION:Lcom/nuance/input/swypecorelib/Candidates$Source;

    if-ne p1, v12, :cond_5

    .line 178
    invoke-virtual/range {p3 .. p3}, Lcom/nuance/input/swypecorelib/WordCandidate;->shouldRemoveSpaceBefore()Z

    move-result v12

    if-nez v12, :cond_2

    iget-boolean v12, p0, Lcom/nuance/swype/input/AutospaceHandler;->noAutospaceLanguage:Z

    if-nez v12, :cond_2

    .line 179
    const/4 v12, 0x6

    move/from16 v0, p4

    if-ne v0, v12, :cond_1

    .line 180
    invoke-direct {p0}, Lcom/nuance/swype/input/AutospaceHandler;->sendAutospace()V

    .line 256
    :cond_0
    :goto_0
    const/4 v12, 0x0

    iput-boolean v12, p0, Lcom/nuance/swype/input/AutospaceHandler;->suppressSingleAutospace:Z

    .line 257
    return-void

    .line 182
    :cond_1
    invoke-direct {p0}, Lcom/nuance/swype/input/AutospaceHandler;->sendAutospaceWCheck()Z

    goto :goto_0

    .line 186
    :cond_2
    iget-object v12, p0, Lcom/nuance/swype/input/AutospaceHandler;->ime:Lcom/nuance/swype/input/IME;

    invoke-virtual {v12}, Lcom/nuance/swype/input/IME;->getAppSpecificInputConnection()Lcom/nuance/swype/input/appspecific/AppSpecificInputConnection;

    move-result-object v4

    .line 187
    .local v4, "ic":Lcom/nuance/swype/input/appspecific/AppSpecificInputConnection;
    if-eqz v4, :cond_3

    .line 190
    const/4 v12, 0x2

    const/4 v13, 0x0

    invoke-virtual {v4, v12, v13}, Lcom/nuance/swype/input/appspecific/AppSpecificInputConnection;->getTextBeforeCursor(II)Ljava/lang/CharSequence;

    move-result-object v10

    .line 191
    .local v10, "seq":Ljava/lang/CharSequence;
    if-eqz v10, :cond_3

    invoke-interface {v10}, Ljava/lang/CharSequence;->length()I

    move-result v12

    const/4 v13, 0x2

    if-ne v12, v13, :cond_3

    const/4 v12, 0x0

    invoke-interface {v10, v12}, Ljava/lang/CharSequence;->charAt(I)C

    move-result v12

    invoke-static {v12}, Lcom/nuance/swype/util/CharacterUtilities;->isWhiteSpace(C)Z

    move-result v12

    if-nez v12, :cond_3

    .line 192
    invoke-interface {v10}, Ljava/lang/CharSequence;->length()I

    move-result v12

    add-int/lit8 v12, v12, -0x1

    invoke-interface {v10, v12}, Ljava/lang/CharSequence;->charAt(I)C

    move-result v12

    const/16 v13, 0x20

    if-ne v12, v13, :cond_3

    .line 193
    const/4 v12, 0x1

    const/4 v13, 0x0

    invoke-virtual {v4, v12, v13}, Lcom/nuance/swype/input/appspecific/AppSpecificInputConnection;->deleteSurroundingText(II)Z

    .line 196
    .end local v10    # "seq":Ljava/lang/CharSequence;
    :cond_3
    if-eqz p3, :cond_0

    invoke-virtual/range {p3 .. p3}, Lcom/nuance/input/swypecorelib/WordCandidate;->length()I

    move-result v12

    if-lez v12, :cond_0

    .line 197
    invoke-virtual/range {p3 .. p3}, Lcom/nuance/input/swypecorelib/WordCandidate;->toString()Ljava/lang/String;

    move-result-object v12

    invoke-virtual/range {p3 .. p3}, Lcom/nuance/input/swypecorelib/WordCandidate;->length()I

    move-result v13

    add-int/lit8 v13, v13, -0x1

    invoke-virtual {v12, v13}, Ljava/lang/String;->charAt(I)C

    move-result v5

    .line 199
    .local v5, "lastChar":C
    iget-object v12, p0, Lcom/nuance/swype/input/AutospaceHandler;->charUtils:Lcom/nuance/swype/util/CharacterUtilities;

    invoke-virtual {v12, v5}, Lcom/nuance/swype/util/CharacterUtilities;->isEmbeddedPunct(C)Z

    move-result v12

    if-eqz v12, :cond_4

    iget-object v12, p0, Lcom/nuance/swype/input/AutospaceHandler;->charUtils:Lcom/nuance/swype/util/CharacterUtilities;

    invoke-virtual {v12, v5}, Lcom/nuance/swype/util/CharacterUtilities;->isWordCompounder(I)Z

    move-result v12

    if-nez v12, :cond_4

    const/4 v12, 0x1

    :goto_1
    iput-boolean v12, p0, Lcom/nuance/swype/input/AutospaceHandler;->forceSingleAutospace:Z

    goto :goto_0

    :cond_4
    const/4 v12, 0x0

    goto :goto_1

    .line 204
    .end local v4    # "ic":Lcom/nuance/swype/input/appspecific/AppSpecificInputConnection;
    .end local v5    # "lastChar":C
    :cond_5
    invoke-virtual/range {p3 .. p3}, Lcom/nuance/input/swypecorelib/WordCandidate;->shouldRemoveSpaceBefore()Z

    move-result v9

    .line 205
    .local v9, "removeSpaceBeforeSelected":Z
    invoke-virtual/range {p2 .. p2}, Lcom/nuance/input/swypecorelib/WordCandidate;->shouldRemoveSpaceBefore()Z

    move-result v8

    .line 209
    .local v8, "removeSpaceBeforeDefault":Z
    if-eq v9, v8, :cond_8

    .line 211
    iget-object v12, p0, Lcom/nuance/swype/input/AutospaceHandler;->ime:Lcom/nuance/swype/input/IME;

    invoke-virtual {v12}, Lcom/nuance/swype/input/IME;->getAppSpecificInputConnection()Lcom/nuance/swype/input/appspecific/AppSpecificInputConnection;

    move-result-object v4

    .line 212
    .restart local v4    # "ic":Lcom/nuance/swype/input/appspecific/AppSpecificInputConnection;
    if-eqz v4, :cond_8

    .line 214
    invoke-virtual/range {p2 .. p2}, Lcom/nuance/input/swypecorelib/WordCandidate;->length()I

    move-result v12

    add-int/lit8 v7, v12, 0x1

    .line 215
    .local v7, "lengthWordPlusOne":I
    const/4 v12, 0x0

    invoke-virtual {v4, v7, v12}, Lcom/nuance/swype/input/appspecific/AppSpecificInputConnection;->getTextBeforeCursor(II)Ljava/lang/CharSequence;

    move-result-object v11

    .line 216
    .local v11, "textBefore":Ljava/lang/CharSequence;
    if-eqz v11, :cond_6

    invoke-interface {v11}, Ljava/lang/CharSequence;->length()I

    move-result v6

    .line 219
    .local v6, "lengthTextBefore":I
    :goto_2
    if-ne v6, v7, :cond_8

    .line 223
    const/4 v2, 0x0

    .line 224
    .local v2, "hasSpaceBefore":Z
    const/4 v3, 0x0

    .local v3, "i":I
    :goto_3
    if-ge v3, v6, :cond_7

    if-nez v2, :cond_7

    .line 225
    invoke-interface {v11, v3}, Ljava/lang/CharSequence;->charAt(I)C

    move-result v12

    invoke-static {v12}, Ljava/lang/Character;->isWhitespace(C)Z

    move-result v2

    .line 224
    add-int/lit8 v3, v3, 0x1

    goto :goto_3

    .line 216
    .end local v2    # "hasSpaceBefore":Z
    .end local v3    # "i":I
    .end local v6    # "lengthTextBefore":I
    :cond_6
    const/4 v6, 0x0

    goto :goto_2

    .line 230
    .restart local v2    # "hasSpaceBefore":Z
    .restart local v3    # "i":I
    .restart local v6    # "lengthTextBefore":I
    :cond_7
    if-eqz v9, :cond_9

    if-eqz v2, :cond_9

    .line 231
    const/4 v12, 0x1

    const/4 v13, 0x0

    invoke-virtual {v4, v12, v13}, Lcom/nuance/swype/input/appspecific/AppSpecificInputConnection;->deleteSurroundingText(II)Z

    .line 242
    .end local v2    # "hasSpaceBefore":Z
    .end local v3    # "i":I
    .end local v4    # "ic":Lcom/nuance/swype/input/appspecific/AppSpecificInputConnection;
    .end local v6    # "lengthTextBefore":I
    .end local v7    # "lengthWordPlusOne":I
    .end local v11    # "textBefore":Ljava/lang/CharSequence;
    :cond_8
    :goto_4
    invoke-virtual/range {p3 .. p3}, Lcom/nuance/input/swypecorelib/WordCandidate;->toString()Ljava/lang/String;

    move-result-object v12

    invoke-virtual/range {p3 .. p3}, Lcom/nuance/input/swypecorelib/WordCandidate;->length()I

    move-result v13

    add-int/lit8 v13, v13, -0x1

    invoke-virtual {v12, v13}, Ljava/lang/String;->charAt(I)C

    move-result v12

    invoke-static {v12}, Ljava/lang/Character;->isWhitespace(C)Z

    move-result v12

    if-eqz v12, :cond_0

    .line 243
    iget-object v12, p0, Lcom/nuance/swype/input/AutospaceHandler;->ime:Lcom/nuance/swype/input/IME;

    invoke-virtual {v12}, Lcom/nuance/swype/input/IME;->getCurrentInputConnection()Landroid/view/inputmethod/InputConnection;

    move-result-object v4

    .line 245
    .local v4, "ic":Landroid/view/inputmethod/InputConnection;
    if-eqz v4, :cond_0

    .line 246
    const/4 v12, 0x1

    const/4 v13, 0x0

    invoke-interface {v4, v12, v13}, Landroid/view/inputmethod/InputConnection;->getTextAfterCursor(II)Ljava/lang/CharSequence;

    move-result-object v1

    .line 247
    .local v1, "cSeqAfter":Ljava/lang/CharSequence;
    invoke-static {v1}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    move-result v12

    if-nez v12, :cond_0

    .line 248
    const/4 v12, 0x0

    invoke-interface {v1, v12}, Ljava/lang/CharSequence;->charAt(I)C

    move-result v12

    invoke-static {v12}, Ljava/lang/Character;->isWhitespace(C)Z

    move-result v12

    if-eqz v12, :cond_0

    .line 249
    const/4 v12, 0x0

    const/4 v13, 0x1

    invoke-interface {v4, v12, v13}, Landroid/view/inputmethod/InputConnection;->deleteSurroundingText(II)Z

    goto/16 :goto_0

    .line 232
    .end local v1    # "cSeqAfter":Ljava/lang/CharSequence;
    .restart local v2    # "hasSpaceBefore":Z
    .restart local v3    # "i":I
    .local v4, "ic":Lcom/nuance/swype/input/appspecific/AppSpecificInputConnection;
    .restart local v6    # "lengthTextBefore":I
    .restart local v7    # "lengthWordPlusOne":I
    .restart local v11    # "textBefore":Ljava/lang/CharSequence;
    :cond_9
    if-nez v9, :cond_8

    if-nez v2, :cond_8

    invoke-direct {p0}, Lcom/nuance/swype/input/AutospaceHandler;->autoSpaceEnabled()Z

    move-result v12

    if-eqz v12, :cond_8

    .line 235
    invoke-direct {p0}, Lcom/nuance/swype/input/AutospaceHandler;->sendAutospace()V

    goto :goto_4
.end method

.method public onText(Ljava/lang/CharSequence;IZ)Z
    .locals 4
    .param p1, "text"    # Ljava/lang/CharSequence;
    .param p2, "lastInputType"    # I
    .param p3, "sendSpace"    # Z

    .prologue
    const/4 v2, 0x0

    .line 152
    invoke-direct {p0, p1, p2}, Lcom/nuance/swype/input/AutospaceHandler;->shouldAutospaceText(Ljava/lang/CharSequence;I)Z

    move-result v1

    .line 154
    .local v1, "shouldSendSpace":Z
    if-eqz v1, :cond_0

    if-eqz p3, :cond_0

    .line 155
    invoke-direct {p0}, Lcom/nuance/swype/input/AutospaceHandler;->sendAutospace()V

    .line 158
    :cond_0
    iput-boolean v2, p0, Lcom/nuance/swype/input/AutospaceHandler;->suppressSingleAutospace:Z

    .line 162
    if-eqz p1, :cond_2

    .line 164
    invoke-interface {p1}, Ljava/lang/CharSequence;->length()I

    move-result v3

    add-int/lit8 v3, v3, -0x1

    invoke-interface {p1, v3}, Ljava/lang/CharSequence;->charAt(I)C

    move-result v0

    .line 166
    .local v0, "lastChar":C
    iget-object v3, p0, Lcom/nuance/swype/input/AutospaceHandler;->charUtils:Lcom/nuance/swype/util/CharacterUtilities;

    invoke-virtual {v3, v0}, Lcom/nuance/swype/util/CharacterUtilities;->isEmbeddedPunct(C)Z

    move-result v3

    if-eqz v3, :cond_1

    iget-object v3, p0, Lcom/nuance/swype/input/AutospaceHandler;->charUtils:Lcom/nuance/swype/util/CharacterUtilities;

    invoke-virtual {v3, v0}, Lcom/nuance/swype/util/CharacterUtilities;->isWordCompounder(I)Z

    move-result v3

    if-nez v3, :cond_1

    const/4 v2, 0x1

    :cond_1
    iput-boolean v2, p0, Lcom/nuance/swype/input/AutospaceHandler;->forceSingleAutospace:Z

    .line 168
    .end local v0    # "lastChar":C
    :cond_2
    return v1
.end method

.method public onTrace(Lcom/nuance/input/swypecorelib/WordCandidate;I)V
    .locals 2
    .param p1, "newWord"    # Lcom/nuance/input/swypecorelib/WordCandidate;
    .param p2, "lastInputType"    # I

    .prologue
    const/4 v1, 0x0

    .line 102
    invoke-virtual {p1}, Lcom/nuance/input/swypecorelib/WordCandidate;->shouldRemoveSpaceBefore()Z

    move-result v0

    if-eqz v0, :cond_0

    .line 103
    iput-boolean v1, p0, Lcom/nuance/swype/input/AutospaceHandler;->suppressSingleAutospace:Z

    .line 104
    iput-boolean v1, p0, Lcom/nuance/swype/input/AutospaceHandler;->forceSingleAutospace:Z

    .line 109
    :goto_0
    return-void

    .line 108
    :cond_0
    invoke-virtual {p1}, Lcom/nuance/input/swypecorelib/WordCandidate;->toString()Ljava/lang/String;

    move-result-object v0

    const/4 v1, 0x1

    invoke-virtual {p0, v0, p2, v1}, Lcom/nuance/swype/input/AutospaceHandler;->onText(Ljava/lang/CharSequence;IZ)Z

    goto :goto_0
.end method

.method public onUpdateEditorInfo(Lcom/nuance/swype/input/InputFieldInfo;)V
    .locals 3
    .param p1, "inputFieldInfo"    # Lcom/nuance/swype/input/InputFieldInfo;

    .prologue
    const/4 v2, 0x1

    const/4 v1, 0x0

    .line 65
    iput-boolean v1, p0, Lcom/nuance/swype/input/AutospaceHandler;->noAutospace:Z

    .line 66
    iput-boolean v1, p0, Lcom/nuance/swype/input/AutospaceHandler;->webSearchUrlBoxAutoSpace:Z

    .line 67
    invoke-virtual {p1}, Lcom/nuance/swype/input/InputFieldInfo;->isEmailAddressField()Z

    move-result v0

    if-nez v0, :cond_0

    .line 68
    invoke-virtual {p1}, Lcom/nuance/swype/input/InputFieldInfo;->isPasswordField()Z

    move-result v0

    if-eqz v0, :cond_3

    .line 69
    :cond_0
    iput-boolean v2, p0, Lcom/nuance/swype/input/AutospaceHandler;->noAutospace:Z

    .line 82
    :cond_1
    :goto_0
    iget-boolean v0, p0, Lcom/nuance/swype/input/AutospaceHandler;->noAutospace:Z

    if-nez v0, :cond_2

    .line 83
    iput-boolean v1, p0, Lcom/nuance/swype/input/AutospaceHandler;->suppressSingleAutospace:Z

    .line 85
    :cond_2
    return-void

    .line 70
    :cond_3
    invoke-virtual {p1}, Lcom/nuance/swype/input/InputFieldInfo;->isURLField()Z

    move-result v0

    if-eqz v0, :cond_1

    .line 75
    invoke-virtual {p1}, Lcom/nuance/swype/input/InputFieldInfo;->isWebSearchField()Z

    move-result v0

    if-nez v0, :cond_4

    invoke-virtual {p1}, Lcom/nuance/swype/input/InputFieldInfo;->isURLWithSearchField()Z

    move-result v0

    if-nez v0, :cond_4

    iget-object v0, p0, Lcom/nuance/swype/input/AutospaceHandler;->imeApp:Lcom/nuance/swype/input/IMEApplication;

    .line 76
    invoke-virtual {v0}, Lcom/nuance/swype/input/IMEApplication;->getAppSpecificBehavior()Lcom/nuance/swype/input/appspecific/AppSpecificBehavior;

    move-result-object v0

    invoke-virtual {v0}, Lcom/nuance/swype/input/appspecific/AppSpecificBehavior;->shouldAutoSpaceInUrlField()Z

    move-result v0

    if-nez v0, :cond_4

    .line 77
    iput-boolean v2, p0, Lcom/nuance/swype/input/AutospaceHandler;->noAutospace:Z

    goto :goto_0

    .line 79
    :cond_4
    iput-boolean v2, p0, Lcom/nuance/swype/input/AutospaceHandler;->webSearchUrlBoxAutoSpace:Z

    goto :goto_0
.end method

.method public resetSingleAutospaceFlags()V
    .locals 1

    .prologue
    const/4 v0, 0x0

    .line 354
    iput-boolean v0, p0, Lcom/nuance/swype/input/AutospaceHandler;->suppressSingleAutospace:Z

    .line 355
    iput-boolean v0, p0, Lcom/nuance/swype/input/AutospaceHandler;->forceSingleAutospace:Z

    .line 356
    return-void
.end method

.method public setEnabled(ZZ)V
    .locals 1
    .param p1, "enabled"    # Z
    .param p2, "disabledLang"    # Z

    .prologue
    .line 93
    if-nez p1, :cond_0

    const/4 v0, 0x1

    :goto_0
    iput-boolean v0, p0, Lcom/nuance/swype/input/AutospaceHandler;->noAutospace:Z

    .line 94
    iput-boolean p2, p0, Lcom/nuance/swype/input/AutospaceHandler;->noAutospaceLanguage:Z

    .line 95
    return-void

    .line 93
    :cond_0
    const/4 v0, 0x0

    goto :goto_0
.end method

.method public shouldAutoSpace()Z
    .locals 5

    .prologue
    const/4 v3, 0x0

    .line 289
    invoke-direct {p0}, Lcom/nuance/swype/input/AutospaceHandler;->autoSpaceEnabled()Z

    move-result v4

    if-eqz v4, :cond_1

    .line 290
    iget-object v4, p0, Lcom/nuance/swype/input/AutospaceHandler;->ime:Lcom/nuance/swype/input/IME;

    invoke-virtual {v4}, Lcom/nuance/swype/input/IME;->getAppSpecificInputConnection()Lcom/nuance/swype/input/appspecific/AppSpecificInputConnection;

    move-result-object v2

    .line 291
    .local v2, "ic":Lcom/nuance/swype/input/appspecific/AppSpecificInputConnection;
    if-eqz v2, :cond_1

    .line 292
    const v4, 0xffff

    invoke-virtual {v2, v4, v3}, Lcom/nuance/swype/input/appspecific/AppSpecificInputConnection;->getTextBeforeCursor(II)Ljava/lang/CharSequence;

    move-result-object v0

    .line 293
    .local v0, "cs":Ljava/lang/CharSequence;
    if-eqz v0, :cond_2

    .line 294
    invoke-virtual {v2}, Lcom/nuance/swype/input/appspecific/AppSpecificInputConnection;->hasSelection()Z

    move-result v4

    if-eqz v4, :cond_0

    iget-boolean v4, p0, Lcom/nuance/swype/input/AutospaceHandler;->webSearchUrlBoxAutoSpace:Z

    if-eqz v4, :cond_1

    .line 295
    :cond_0
    invoke-interface {v0}, Ljava/lang/CharSequence;->length()I

    move-result v1

    .line 296
    .local v1, "cursorStart":I
    invoke-direct {p0, v0, v1}, Lcom/nuance/swype/input/AutospaceHandler;->shouldAutoSpace(Ljava/lang/CharSequence;I)Z

    move-result v3

    .line 306
    .end local v0    # "cs":Ljava/lang/CharSequence;
    .end local v1    # "cursorStart":I
    .end local v2    # "ic":Lcom/nuance/swype/input/appspecific/AppSpecificInputConnection;
    :cond_1
    :goto_0
    return v3

    .line 302
    .restart local v0    # "cs":Ljava/lang/CharSequence;
    .restart local v2    # "ic":Lcom/nuance/swype/input/appspecific/AppSpecificInputConnection;
    :cond_2
    iget-object v3, p0, Lcom/nuance/swype/input/AutospaceHandler;->imeApp:Lcom/nuance/swype/input/IMEApplication;

    invoke-virtual {v3}, Lcom/nuance/swype/input/IMEApplication;->getAppSpecificBehavior()Lcom/nuance/swype/input/appspecific/AppSpecificBehavior;

    move-result-object v3

    invoke-virtual {v3}, Lcom/nuance/swype/input/appspecific/AppSpecificBehavior;->shouldAutoSpaceOnTextExtractFailure()Z

    move-result v3

    goto :goto_0
.end method
