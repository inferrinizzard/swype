.class public final Lcom/nuance/swype/usagedata/UsageDataEventThemesPreview;
.super Lcom/nuance/swype/usagedata/UsageDataEvent;
.source "UsageDataEventThemesPreview.java"


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Lcom/nuance/swype/usagedata/UsageDataEventThemesPreview$Result;,
        Lcom/nuance/swype/usagedata/UsageDataEventThemesPreview$Action;
    }
.end annotation

.annotation system Ldalvik/annotation/Signature;
    value = {
        "Lcom/nuance/swype/usagedata/UsageDataEvent",
        "<",
        "Lcom/nuance/swype/usagedata/UsageDataEventThemesPreview;",
        ">;"
    }
.end annotation


# instance fields
.field public mAction:Lcom/nuance/swype/usagedata/UsageDataEventThemesPreview$Action;

.field public mCategory:Ljava/lang/String;

.field public mPosition:Ljava/lang/Integer;

.field public mPreviewedFrom:Ljava/lang/String;

.field public mResult:Lcom/nuance/swype/usagedata/UsageDataEventThemesPreview$Result;

.field public mThemeName:Ljava/lang/String;


# direct methods
.method public constructor <init>()V
    .locals 1

    .prologue
    .line 58
    sget-object v0, Lcom/nuance/swype/usagedata/UsageData$EventTag;->STORE_THEMES_PREVIEW:Lcom/nuance/swype/usagedata/UsageData$EventTag;

    invoke-virtual {v0}, Lcom/nuance/swype/usagedata/UsageData$EventTag;->toString()Ljava/lang/String;

    move-result-object v0

    invoke-direct {p0, v0}, Lcom/nuance/swype/usagedata/UsageDataEvent;-><init>(Ljava/lang/String;)V

    .line 59
    return-void
.end method


# virtual methods
.method public final commit()V
    .locals 3

    .prologue
    .line 93
    iget-object v0, p0, Lcom/nuance/swype/usagedata/UsageDataEventThemesPreview;->mAction:Lcom/nuance/swype/usagedata/UsageDataEventThemesPreview$Action;

    if-eqz v0, :cond_0

    .line 94
    iget-object v0, p0, Lcom/nuance/swype/usagedata/UsageDataEventThemesPreview;->mAttributes:Ljava/util/Map;

    sget-object v1, Lcom/nuance/swype/usagedata/UsageData$EventAttribute;->USER_ACTION:Lcom/nuance/swype/usagedata/UsageData$EventAttribute;

    invoke-virtual {v1}, Lcom/nuance/swype/usagedata/UsageData$EventAttribute;->toString()Ljava/lang/String;

    move-result-object v1

    iget-object v2, p0, Lcom/nuance/swype/usagedata/UsageDataEventThemesPreview;->mAction:Lcom/nuance/swype/usagedata/UsageDataEventThemesPreview$Action;

    invoke-virtual {v2}, Lcom/nuance/swype/usagedata/UsageDataEventThemesPreview$Action;->toString()Ljava/lang/String;

    move-result-object v2

    invoke-interface {v0, v1, v2}, Ljava/util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    .line 100
    :goto_0
    iget-object v0, p0, Lcom/nuance/swype/usagedata/UsageDataEventThemesPreview;->mResult:Lcom/nuance/swype/usagedata/UsageDataEventThemesPreview$Result;

    if-eqz v0, :cond_1

    .line 101
    iget-object v0, p0, Lcom/nuance/swype/usagedata/UsageDataEventThemesPreview;->mAttributes:Ljava/util/Map;

    sget-object v1, Lcom/nuance/swype/usagedata/UsageData$EventAttribute;->RESULT:Lcom/nuance/swype/usagedata/UsageData$EventAttribute;

    invoke-virtual {v1}, Lcom/nuance/swype/usagedata/UsageData$EventAttribute;->toString()Ljava/lang/String;

    move-result-object v1

    iget-object v2, p0, Lcom/nuance/swype/usagedata/UsageDataEventThemesPreview;->mResult:Lcom/nuance/swype/usagedata/UsageDataEventThemesPreview$Result;

    invoke-virtual {v2}, Lcom/nuance/swype/usagedata/UsageDataEventThemesPreview$Result;->toString()Ljava/lang/String;

    move-result-object v2

    invoke-interface {v0, v1, v2}, Ljava/util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    .line 107
    :goto_1
    iget-object v0, p0, Lcom/nuance/swype/usagedata/UsageDataEventThemesPreview;->mThemeName:Ljava/lang/String;

    if-eqz v0, :cond_2

    .line 108
    iget-object v0, p0, Lcom/nuance/swype/usagedata/UsageDataEventThemesPreview;->mAttributes:Ljava/util/Map;

    sget-object v1, Lcom/nuance/swype/usagedata/UsageData$EventAttribute;->THEME_NAME:Lcom/nuance/swype/usagedata/UsageData$EventAttribute;

    invoke-virtual {v1}, Lcom/nuance/swype/usagedata/UsageData$EventAttribute;->toString()Ljava/lang/String;

    move-result-object v1

    iget-object v2, p0, Lcom/nuance/swype/usagedata/UsageDataEventThemesPreview;->mThemeName:Ljava/lang/String;

    invoke-interface {v0, v1, v2}, Ljava/util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    .line 114
    :goto_2
    iget-object v0, p0, Lcom/nuance/swype/usagedata/UsageDataEventThemesPreview;->mPreviewedFrom:Ljava/lang/String;

    if-eqz v0, :cond_3

    .line 115
    iget-object v0, p0, Lcom/nuance/swype/usagedata/UsageDataEventThemesPreview;->mAttributes:Ljava/util/Map;

    sget-object v1, Lcom/nuance/swype/usagedata/UsageData$EventAttribute;->TAB_PREVIEWED_FROM:Lcom/nuance/swype/usagedata/UsageData$EventAttribute;

    invoke-virtual {v1}, Lcom/nuance/swype/usagedata/UsageData$EventAttribute;->toString()Ljava/lang/String;

    move-result-object v1

    iget-object v2, p0, Lcom/nuance/swype/usagedata/UsageDataEventThemesPreview;->mPreviewedFrom:Ljava/lang/String;

    invoke-interface {v0, v1, v2}, Ljava/util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    .line 121
    :goto_3
    iget-object v0, p0, Lcom/nuance/swype/usagedata/UsageDataEventThemesPreview;->mPosition:Ljava/lang/Integer;

    if-eqz v0, :cond_4

    .line 122
    iget-object v0, p0, Lcom/nuance/swype/usagedata/UsageDataEventThemesPreview;->mAttributes:Ljava/util/Map;

    sget-object v1, Lcom/nuance/swype/usagedata/UsageData$EventAttribute;->POSITION:Lcom/nuance/swype/usagedata/UsageData$EventAttribute;

    invoke-virtual {v1}, Lcom/nuance/swype/usagedata/UsageData$EventAttribute;->toString()Ljava/lang/String;

    move-result-object v1

    iget-object v2, p0, Lcom/nuance/swype/usagedata/UsageDataEventThemesPreview;->mPosition:Ljava/lang/Integer;

    invoke-virtual {v2}, Ljava/lang/Integer;->intValue()I

    move-result v2

    invoke-static {v2}, Ljava/lang/Integer;->toString(I)Ljava/lang/String;

    move-result-object v2

    invoke-interface {v0, v1, v2}, Ljava/util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    .line 128
    :goto_4
    iget-object v0, p0, Lcom/nuance/swype/usagedata/UsageDataEventThemesPreview;->mCategory:Ljava/lang/String;

    if-eqz v0, :cond_5

    .line 129
    iget-object v0, p0, Lcom/nuance/swype/usagedata/UsageDataEventThemesPreview;->mAttributes:Ljava/util/Map;

    sget-object v1, Lcom/nuance/swype/usagedata/UsageData$EventAttribute;->CATEGORY:Lcom/nuance/swype/usagedata/UsageData$EventAttribute;

    invoke-virtual {v1}, Lcom/nuance/swype/usagedata/UsageData$EventAttribute;->toString()Ljava/lang/String;

    move-result-object v1

    iget-object v2, p0, Lcom/nuance/swype/usagedata/UsageDataEventThemesPreview;->mCategory:Ljava/lang/String;

    invoke-interface {v0, v1, v2}, Ljava/util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    .line 135
    :goto_5
    invoke-super {p0}, Lcom/nuance/swype/usagedata/UsageDataEvent;->commit()V

    .line 136
    return-void

    .line 97
    :cond_0
    const-string/jumbo v0, "USER_ACTION not provided!"

    invoke-virtual {p0, v0}, Lcom/nuance/swype/usagedata/UsageDataEventThemesPreview;->eventError(Ljava/lang/String;)V

    goto :goto_0

    .line 104
    :cond_1
    const-string/jumbo v0, "RESULT not provided!"

    invoke-virtual {p0, v0}, Lcom/nuance/swype/usagedata/UsageDataEventThemesPreview;->eventError(Ljava/lang/String;)V

    goto :goto_1

    .line 111
    :cond_2
    const-string/jumbo v0, "THEME_NAME not provided!"

    invoke-virtual {p0, v0}, Lcom/nuance/swype/usagedata/UsageDataEventThemesPreview;->eventError(Ljava/lang/String;)V

    goto :goto_2

    .line 118
    :cond_3
    const-string/jumbo v0, "TAB_PREVIEWED_FROM not provided!"

    invoke-virtual {p0, v0}, Lcom/nuance/swype/usagedata/UsageDataEventThemesPreview;->eventError(Ljava/lang/String;)V

    goto :goto_3

    .line 125
    :cond_4
    const-string/jumbo v0, "POSITION not provided!"

    invoke-virtual {p0, v0}, Lcom/nuance/swype/usagedata/UsageDataEventThemesPreview;->eventError(Ljava/lang/String;)V

    goto :goto_4

    .line 132
    :cond_5
    const-string/jumbo v0, "CATEGORY not provided!"

    invoke-virtual {p0, v0}, Lcom/nuance/swype/usagedata/UsageDataEventThemesPreview;->eventError(Ljava/lang/String;)V

    goto :goto_5
.end method

.method protected final getLogTag()Ljava/lang/String;
    .locals 1

    .prologue
    .line 140
    const-string/jumbo v0, "UsageDataEventThemesPreview"

    return-object v0
.end method

.method public final bridge synthetic toString()Ljava/lang/String;
    .locals 1

    .prologue
    .line 6
    invoke-super {p0}, Lcom/nuance/swype/usagedata/UsageDataEvent;->toString()Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method
