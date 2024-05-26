.class public final Lcom/fasterxml/jackson/core/json/JsonWriteContext;
.super Lcom/fasterxml/jackson/core/JsonStreamContext;
.source "JsonWriteContext.java"


# instance fields
.field protected _child:Lcom/fasterxml/jackson/core/json/JsonWriteContext;

.field protected _currentName:Ljava/lang/String;

.field protected final _parent:Lcom/fasterxml/jackson/core/json/JsonWriteContext;


# direct methods
.method public constructor <init>(ILcom/fasterxml/jackson/core/json/JsonWriteContext;)V
    .locals 1
    .param p1, "type"    # I
    .param p2, "parent"    # Lcom/fasterxml/jackson/core/json/JsonWriteContext;

    .prologue
    .line 48
    invoke-direct {p0}, Lcom/fasterxml/jackson/core/JsonStreamContext;-><init>()V

    .line 38
    const/4 v0, 0x0

    iput-object v0, p0, Lcom/fasterxml/jackson/core/json/JsonWriteContext;->_child:Lcom/fasterxml/jackson/core/json/JsonWriteContext;

    .line 49
    iput p1, p0, Lcom/fasterxml/jackson/core/json/JsonWriteContext;->_type:I

    .line 50
    iput-object p2, p0, Lcom/fasterxml/jackson/core/json/JsonWriteContext;->_parent:Lcom/fasterxml/jackson/core/json/JsonWriteContext;

    .line 51
    const/4 v0, -0x1

    iput v0, p0, Lcom/fasterxml/jackson/core/json/JsonWriteContext;->_index:I

    .line 52
    return-void
.end method

.method private reset(I)Lcom/fasterxml/jackson/core/json/JsonWriteContext;
    .locals 1
    .param p1, "type"    # I

    .prologue
    .line 62
    iput p1, p0, Lcom/fasterxml/jackson/core/json/JsonWriteContext;->_type:I

    .line 63
    const/4 v0, -0x1

    iput v0, p0, Lcom/fasterxml/jackson/core/json/JsonWriteContext;->_index:I

    .line 64
    const/4 v0, 0x0

    iput-object v0, p0, Lcom/fasterxml/jackson/core/json/JsonWriteContext;->_currentName:Ljava/lang/String;

    .line 65
    return-object p0
.end method


# virtual methods
.method public final createChildArrayContext()Lcom/fasterxml/jackson/core/json/JsonWriteContext;
    .locals 2

    .prologue
    const/4 v1, 0x1

    .line 70
    iget-object v0, p0, Lcom/fasterxml/jackson/core/json/JsonWriteContext;->_child:Lcom/fasterxml/jackson/core/json/JsonWriteContext;

    .line 71
    .local v0, "ctxt":Lcom/fasterxml/jackson/core/json/JsonWriteContext;
    if-nez v0, :cond_0

    .line 72
    new-instance v0, Lcom/fasterxml/jackson/core/json/JsonWriteContext;

    .end local v0    # "ctxt":Lcom/fasterxml/jackson/core/json/JsonWriteContext;
    invoke-direct {v0, v1, p0}, Lcom/fasterxml/jackson/core/json/JsonWriteContext;-><init>(ILcom/fasterxml/jackson/core/json/JsonWriteContext;)V

    .restart local v0    # "ctxt":Lcom/fasterxml/jackson/core/json/JsonWriteContext;
    iput-object v0, p0, Lcom/fasterxml/jackson/core/json/JsonWriteContext;->_child:Lcom/fasterxml/jackson/core/json/JsonWriteContext;

    move-object v1, v0

    .line 75
    :goto_0
    return-object v1

    :cond_0
    invoke-direct {v0, v1}, Lcom/fasterxml/jackson/core/json/JsonWriteContext;->reset(I)Lcom/fasterxml/jackson/core/json/JsonWriteContext;

    move-result-object v1

    goto :goto_0
.end method

.method public final createChildObjectContext()Lcom/fasterxml/jackson/core/json/JsonWriteContext;
    .locals 2

    .prologue
    const/4 v1, 0x2

    .line 80
    iget-object v0, p0, Lcom/fasterxml/jackson/core/json/JsonWriteContext;->_child:Lcom/fasterxml/jackson/core/json/JsonWriteContext;

    .line 81
    .local v0, "ctxt":Lcom/fasterxml/jackson/core/json/JsonWriteContext;
    if-nez v0, :cond_0

    .line 82
    new-instance v0, Lcom/fasterxml/jackson/core/json/JsonWriteContext;

    .end local v0    # "ctxt":Lcom/fasterxml/jackson/core/json/JsonWriteContext;
    invoke-direct {v0, v1, p0}, Lcom/fasterxml/jackson/core/json/JsonWriteContext;-><init>(ILcom/fasterxml/jackson/core/json/JsonWriteContext;)V

    .restart local v0    # "ctxt":Lcom/fasterxml/jackson/core/json/JsonWriteContext;
    iput-object v0, p0, Lcom/fasterxml/jackson/core/json/JsonWriteContext;->_child:Lcom/fasterxml/jackson/core/json/JsonWriteContext;

    move-object v1, v0

    .line 85
    :goto_0
    return-object v1

    :cond_0
    invoke-direct {v0, v1}, Lcom/fasterxml/jackson/core/json/JsonWriteContext;->reset(I)Lcom/fasterxml/jackson/core/json/JsonWriteContext;

    move-result-object v1

    goto :goto_0
.end method

.method public final getParent()Lcom/fasterxml/jackson/core/json/JsonWriteContext;
    .locals 1

    .prologue
    .line 91
    iget-object v0, p0, Lcom/fasterxml/jackson/core/json/JsonWriteContext;->_parent:Lcom/fasterxml/jackson/core/json/JsonWriteContext;

    return-object v0
.end method

.method public final toString()Ljava/lang/String;
    .locals 4

    .prologue
    const/16 v3, 0x22

    .line 174
    new-instance v0, Ljava/lang/StringBuilder;

    const/16 v1, 0x40

    invoke-direct {v0, v1}, Ljava/lang/StringBuilder;-><init>(I)V

    .line 1144
    .local v0, "sb":Ljava/lang/StringBuilder;
    iget v1, p0, Lcom/fasterxml/jackson/core/json/JsonWriteContext;->_type:I

    const/4 v2, 0x2

    if-ne v1, v2, :cond_1

    .line 1145
    const/16 v1, 0x7b

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(C)Ljava/lang/StringBuilder;

    .line 1146
    iget-object v1, p0, Lcom/fasterxml/jackson/core/json/JsonWriteContext;->_currentName:Ljava/lang/String;

    if-eqz v1, :cond_0

    .line 1147
    invoke-virtual {v0, v3}, Ljava/lang/StringBuilder;->append(C)Ljava/lang/StringBuilder;

    .line 1149
    iget-object v1, p0, Lcom/fasterxml/jackson/core/json/JsonWriteContext;->_currentName:Ljava/lang/String;

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    .line 1150
    invoke-virtual {v0, v3}, Ljava/lang/StringBuilder;->append(C)Ljava/lang/StringBuilder;

    .line 1154
    :goto_0
    const/16 v1, 0x7d

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(C)Ljava/lang/StringBuilder;

    .line 176
    :goto_1
    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v1

    return-object v1

    .line 1152
    :cond_0
    const/16 v1, 0x3f

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(C)Ljava/lang/StringBuilder;

    goto :goto_0

    .line 1155
    :cond_1
    iget v1, p0, Lcom/fasterxml/jackson/core/json/JsonWriteContext;->_type:I

    const/4 v2, 0x1

    if-ne v1, v2, :cond_2

    .line 1156
    const/16 v1, 0x5b

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(C)Ljava/lang/StringBuilder;

    .line 1157
    invoke-virtual {p0}, Lcom/fasterxml/jackson/core/json/JsonWriteContext;->getCurrentIndex()I

    move-result v1

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    .line 1158
    const/16 v1, 0x5d

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(C)Ljava/lang/StringBuilder;

    goto :goto_1

    .line 1161
    :cond_2
    const-string/jumbo v1, "/"

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    goto :goto_1
.end method

.method public final writeFieldName(Ljava/lang/String;)I
    .locals 3
    .param p1, "name"    # Ljava/lang/String;

    .prologue
    const/4 v0, 0x4

    .line 105
    iget v1, p0, Lcom/fasterxml/jackson/core/json/JsonWriteContext;->_type:I

    const/4 v2, 0x2

    if-ne v1, v2, :cond_0

    .line 106
    iget-object v1, p0, Lcom/fasterxml/jackson/core/json/JsonWriteContext;->_currentName:Ljava/lang/String;

    if-eqz v1, :cond_1

    .line 112
    :cond_0
    :goto_0
    return v0

    .line 109
    :cond_1
    iput-object p1, p0, Lcom/fasterxml/jackson/core/json/JsonWriteContext;->_currentName:Ljava/lang/String;

    .line 110
    iget v0, p0, Lcom/fasterxml/jackson/core/json/JsonWriteContext;->_index:I

    if-gez v0, :cond_2

    const/4 v0, 0x0

    goto :goto_0

    :cond_2
    const/4 v0, 0x1

    goto :goto_0
.end method

.method public final writeValue()I
    .locals 5

    .prologue
    const/4 v1, 0x2

    const/4 v3, 0x1

    const/4 v2, 0x0

    .line 118
    iget v4, p0, Lcom/fasterxml/jackson/core/json/JsonWriteContext;->_type:I

    if-ne v4, v1, :cond_1

    .line 119
    iget-object v2, p0, Lcom/fasterxml/jackson/core/json/JsonWriteContext;->_currentName:Ljava/lang/String;

    if-nez v2, :cond_0

    .line 120
    const/4 v1, 0x5

    .line 137
    :goto_0
    return v1

    .line 122
    :cond_0
    const/4 v2, 0x0

    iput-object v2, p0, Lcom/fasterxml/jackson/core/json/JsonWriteContext;->_currentName:Ljava/lang/String;

    .line 123
    iget v2, p0, Lcom/fasterxml/jackson/core/json/JsonWriteContext;->_index:I

    add-int/lit8 v2, v2, 0x1

    iput v2, p0, Lcom/fasterxml/jackson/core/json/JsonWriteContext;->_index:I

    goto :goto_0

    .line 128
    :cond_1
    iget v1, p0, Lcom/fasterxml/jackson/core/json/JsonWriteContext;->_type:I

    if-ne v1, v3, :cond_3

    .line 129
    iget v0, p0, Lcom/fasterxml/jackson/core/json/JsonWriteContext;->_index:I

    .line 130
    .local v0, "ix":I
    iget v1, p0, Lcom/fasterxml/jackson/core/json/JsonWriteContext;->_index:I

    add-int/lit8 v1, v1, 0x1

    iput v1, p0, Lcom/fasterxml/jackson/core/json/JsonWriteContext;->_index:I

    .line 131
    if-gez v0, :cond_2

    move v1, v2

    goto :goto_0

    :cond_2
    move v1, v3

    goto :goto_0

    .line 136
    .end local v0    # "ix":I
    :cond_3
    iget v1, p0, Lcom/fasterxml/jackson/core/json/JsonWriteContext;->_index:I

    add-int/lit8 v1, v1, 0x1

    iput v1, p0, Lcom/fasterxml/jackson/core/json/JsonWriteContext;->_index:I

    .line 137
    iget v1, p0, Lcom/fasterxml/jackson/core/json/JsonWriteContext;->_index:I

    if-nez v1, :cond_4

    move v1, v2

    goto :goto_0

    :cond_4
    const/4 v1, 0x3

    goto :goto_0
.end method
