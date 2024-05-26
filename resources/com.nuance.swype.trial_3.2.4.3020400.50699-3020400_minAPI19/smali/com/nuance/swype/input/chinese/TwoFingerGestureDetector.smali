.class public Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector;
.super Ljava/lang/Object;
.source "TwoFingerGestureDetector.java"


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector$Finger;,
        Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector$OnScrollListener;
    }
.end annotation


# static fields
.field private static final INVALID_POINTER_ID:I = -0x1

.field private static final MAX_SCROLL_OFFSET:F = 60.0f

.field private static final MIN_SCROLL_DISTANCE:F = 20.0f


# instance fields
.field private firstPointerId:I

.field private initialFirstFinger:Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector$Finger;

.field private initialSecondFinger:Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector$Finger;

.field private isMultiTouchBegin:Z

.field private isMultiTouchReleaseBegin:Z

.field private scrollListener:Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector$OnScrollListener;

.field private secondPointerId:I


# direct methods
.method public constructor <init>()V
    .locals 3

    .prologue
    const/4 v2, 0x0

    const/4 v1, -0x1

    const/4 v0, 0x0

    .line 7
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 9
    iput-object v0, p0, Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector;->initialFirstFinger:Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector$Finger;

    .line 10
    iput-object v0, p0, Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector;->initialSecondFinger:Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector$Finger;

    .line 11
    iput-object v0, p0, Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector;->scrollListener:Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector$OnScrollListener;

    .line 16
    iput v1, p0, Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector;->firstPointerId:I

    .line 17
    iput v1, p0, Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector;->secondPointerId:I

    .line 18
    iput-boolean v2, p0, Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector;->isMultiTouchBegin:Z

    .line 19
    iput-boolean v2, p0, Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector;->isMultiTouchReleaseBegin:Z

    return-void
.end method

.method private clear()V
    .locals 3

    .prologue
    const/4 v2, 0x0

    const/4 v1, 0x0

    const/4 v0, -0x1

    .line 88
    iput-object v2, p0, Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector;->initialFirstFinger:Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector$Finger;

    .line 89
    iput-object v2, p0, Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector;->initialSecondFinger:Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector$Finger;

    .line 90
    iput v0, p0, Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector;->firstPointerId:I

    .line 91
    iput v0, p0, Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector;->secondPointerId:I

    .line 92
    iput-boolean v1, p0, Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector;->isMultiTouchBegin:Z

    .line 93
    iput-boolean v1, p0, Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector;->isMultiTouchReleaseBegin:Z

    .line 94
    return-void
.end method


# virtual methods
.method public onTouchEvent(Landroid/view/MotionEvent;)Z
    .locals 12
    .param p1, "ev"    # Landroid/view/MotionEvent;

    .prologue
    const/4 v11, 0x0

    const v10, 0xff00

    const/4 v9, -0x1

    const/4 v1, 0x1

    const/4 v7, 0x0

    .line 97
    iget-object v8, p0, Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector;->scrollListener:Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector$OnScrollListener;

    if-nez v8, :cond_1

    .line 217
    :cond_0
    :goto_0
    return v7

    .line 100
    :cond_1
    invoke-virtual {p1}, Landroid/view/MotionEvent;->getAction()I

    move-result v0

    .line 102
    .local v0, "action":I
    and-int/lit16 v8, v0, 0xff

    packed-switch v8, :pswitch_data_0

    .line 213
    :pswitch_0
    invoke-direct {p0}, Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector;->clear()V

    goto :goto_0

    .line 104
    :pswitch_1
    invoke-static {p1, v7}, Lcom/nuance/swype/input/MotionEventWrapper;->getPointerId(Landroid/view/MotionEvent;I)I

    move-result v8

    iput v8, p0, Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector;->firstPointerId:I

    goto :goto_0

    .line 109
    :pswitch_2
    iget-boolean v8, p0, Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector;->isMultiTouchBegin:Z

    if-nez v8, :cond_2

    iget-object v8, p0, Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector;->initialFirstFinger:Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector$Finger;

    if-nez v8, :cond_2

    iget-object v8, p0, Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector;->initialSecondFinger:Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector$Finger;

    if-nez v8, :cond_2

    .line 111
    iget v7, p0, Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector;->firstPointerId:I

    invoke-static {p1, v7}, Lcom/nuance/swype/input/MotionEventWrapper;->findPointerIndex(Landroid/view/MotionEvent;I)I

    move-result v4

    .line 113
    .local v4, "pointerIndex":I
    invoke-static {p1, v4}, Lcom/nuance/swype/input/MotionEventWrapper;->getX(Landroid/view/MotionEvent;I)F

    move-result v5

    .line 114
    .local v5, "x":F
    invoke-static {p1, v4}, Lcom/nuance/swype/input/MotionEventWrapper;->getY(Landroid/view/MotionEvent;I)F

    move-result v6

    .line 115
    .local v6, "y":F
    new-instance v7, Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector$Finger;

    invoke-direct {v7, v11}, Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector$Finger;-><init>(Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector$1;)V

    iput-object v7, p0, Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector;->initialFirstFinger:Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector$Finger;

    .line 116
    iget-object v7, p0, Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector;->initialFirstFinger:Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector$Finger;

    invoke-virtual {v7, v5}, Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector$Finger;->setStartX(F)V

    .line 117
    iget-object v7, p0, Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector;->initialFirstFinger:Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector$Finger;

    invoke-virtual {v7, v6}, Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector$Finger;->setStartY(F)V

    .line 119
    and-int v7, v0, v10

    shr-int/lit8 v2, v7, 0x8

    .line 121
    .local v2, "pointer2Index":I
    invoke-static {p1, v2}, Lcom/nuance/swype/input/MotionEventWrapper;->getPointerId(Landroid/view/MotionEvent;I)I

    move-result v7

    iput v7, p0, Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector;->secondPointerId:I

    .line 123
    invoke-static {p1, v2}, Lcom/nuance/swype/input/MotionEventWrapper;->getX(Landroid/view/MotionEvent;I)F

    move-result v5

    .line 124
    invoke-static {p1, v2}, Lcom/nuance/swype/input/MotionEventWrapper;->getY(Landroid/view/MotionEvent;I)F

    move-result v6

    .line 125
    new-instance v7, Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector$Finger;

    invoke-direct {v7, v11}, Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector$Finger;-><init>(Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector$1;)V

    iput-object v7, p0, Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector;->initialSecondFinger:Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector$Finger;

    .line 126
    iget-object v7, p0, Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector;->initialSecondFinger:Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector$Finger;

    invoke-virtual {v7, v5}, Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector$Finger;->setStartX(F)V

    .line 127
    iget-object v7, p0, Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector;->initialSecondFinger:Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector$Finger;

    invoke-virtual {v7, v6}, Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector$Finger;->setStartY(F)V

    .line 128
    iput-boolean v1, p0, Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector;->isMultiTouchBegin:Z

    move v7, v1

    .line 129
    goto :goto_0

    .line 130
    .end local v2    # "pointer2Index":I
    .end local v4    # "pointerIndex":I
    .end local v5    # "x":F
    .end local v6    # "y":F
    :cond_2
    iget-boolean v8, p0, Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector;->isMultiTouchReleaseBegin:Z

    if-eqz v8, :cond_3

    .line 131
    iput-boolean v7, p0, Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector;->isMultiTouchBegin:Z

    :cond_3
    move v7, v1

    .line 133
    goto :goto_0

    .line 137
    :pswitch_3
    iget-object v8, p0, Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector;->initialFirstFinger:Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector$Finger;

    if-nez v8, :cond_4

    iget-object v8, p0, Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector;->initialSecondFinger:Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector$Finger;

    if-eqz v8, :cond_0

    :cond_4
    move v7, v1

    goto :goto_0

    .line 141
    :pswitch_4
    iget-boolean v7, p0, Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector;->isMultiTouchBegin:Z

    if-eqz v7, :cond_5

    .line 142
    and-int v7, v0, v10

    shr-int/lit8 v4, v7, 0x8

    .line 144
    .restart local v4    # "pointerIndex":I
    invoke-static {p1, v4}, Lcom/nuance/swype/input/MotionEventWrapper;->getPointerId(Landroid/view/MotionEvent;I)I

    move-result v3

    .line 146
    .local v3, "pointerId":I
    invoke-static {p1, v4}, Lcom/nuance/swype/input/MotionEventWrapper;->getX(Landroid/view/MotionEvent;I)F

    move-result v5

    .line 147
    .restart local v5    # "x":F
    invoke-static {p1, v4}, Lcom/nuance/swype/input/MotionEventWrapper;->getY(Landroid/view/MotionEvent;I)F

    move-result v6

    .line 149
    .restart local v6    # "y":F
    iget v7, p0, Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector;->firstPointerId:I

    if-ne v3, v7, :cond_6

    .line 150
    iget-object v7, p0, Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector;->initialFirstFinger:Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector$Finger;

    invoke-virtual {v7, v5}, Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector$Finger;->setEndX(F)V

    .line 151
    iget-object v7, p0, Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector;->initialFirstFinger:Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector$Finger;

    invoke-virtual {v7, v6}, Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector$Finger;->setEndY(F)V

    .line 152
    iput v9, p0, Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector;->firstPointerId:I

    .line 153
    iput-boolean v1, p0, Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector;->isMultiTouchReleaseBegin:Z

    .end local v3    # "pointerId":I
    .end local v4    # "pointerIndex":I
    .end local v5    # "x":F
    .end local v6    # "y":F
    :cond_5
    :goto_1
    move v7, v1

    .line 161
    goto/16 :goto_0

    .line 154
    .restart local v3    # "pointerId":I
    .restart local v4    # "pointerIndex":I
    .restart local v5    # "x":F
    .restart local v6    # "y":F
    :cond_6
    iget v7, p0, Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector;->secondPointerId:I

    if-ne v3, v7, :cond_5

    .line 155
    iget-object v7, p0, Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector;->initialSecondFinger:Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector$Finger;

    invoke-virtual {v7, v5}, Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector$Finger;->setEndX(F)V

    .line 156
    iget-object v7, p0, Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector;->initialSecondFinger:Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector$Finger;

    invoke-virtual {v7, v6}, Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector$Finger;->setEndY(F)V

    .line 157
    iput v9, p0, Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector;->secondPointerId:I

    .line 158
    iput-boolean v1, p0, Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector;->isMultiTouchReleaseBegin:Z

    goto :goto_1

    .line 165
    .end local v3    # "pointerId":I
    .end local v4    # "pointerIndex":I
    .end local v5    # "x":F
    .end local v6    # "y":F
    :pswitch_5
    iget-boolean v8, p0, Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector;->isMultiTouchBegin:Z

    if-eqz v8, :cond_e

    iget-boolean v8, p0, Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector;->isMultiTouchReleaseBegin:Z

    if-eqz v8, :cond_e

    .line 167
    invoke-static {p1, v7}, Lcom/nuance/swype/input/MotionEventWrapper;->getPointerId(Landroid/view/MotionEvent;I)I

    move-result v8

    .line 166
    invoke-static {p1, v8}, Lcom/nuance/swype/input/MotionEventWrapper;->findPointerIndex(Landroid/view/MotionEvent;I)I

    move-result v4

    .line 168
    .restart local v4    # "pointerIndex":I
    invoke-static {p1, v4}, Lcom/nuance/swype/input/MotionEventWrapper;->getX(Landroid/view/MotionEvent;I)F

    move-result v5

    .line 169
    .restart local v5    # "x":F
    invoke-static {p1, v4}, Lcom/nuance/swype/input/MotionEventWrapper;->getY(Landroid/view/MotionEvent;I)F

    move-result v6

    .line 172
    .restart local v6    # "y":F
    iget v8, p0, Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector;->firstPointerId:I

    if-eq v8, v9, :cond_9

    .line 173
    iget-object v8, p0, Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector;->initialFirstFinger:Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector$Finger;

    invoke-virtual {v8, v5}, Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector$Finger;->setEndX(F)V

    .line 174
    iget-object v8, p0, Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector;->initialFirstFinger:Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector$Finger;

    invoke-virtual {v8, v6}, Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector$Finger;->setEndY(F)V

    .line 175
    iput v9, p0, Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector;->firstPointerId:I

    .line 182
    :cond_7
    :goto_2
    iget-object v8, p0, Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector;->initialFirstFinger:Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector$Finger;

    invoke-virtual {v8}, Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector$Finger;->isScrollLeft()Z

    move-result v8

    if-eqz v8, :cond_a

    iget-object v8, p0, Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector;->initialSecondFinger:Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector$Finger;

    .line 183
    invoke-virtual {v8}, Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector$Finger;->isScrollLeft()Z

    move-result v8

    if-eqz v8, :cond_a

    .line 185
    iget-object v7, p0, Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector;->scrollListener:Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector$OnScrollListener;

    invoke-interface {v7}, Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector$OnScrollListener;->onScrollLeft()Z

    move-result v1

    .line 199
    .local v1, "bHandled":Z
    :cond_8
    :goto_3
    invoke-direct {p0}, Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector;->clear()V

    move v7, v1

    .line 200
    goto/16 :goto_0

    .line 176
    .end local v1    # "bHandled":Z
    :cond_9
    iget v8, p0, Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector;->secondPointerId:I

    if-eq v8, v9, :cond_7

    .line 177
    iget-object v8, p0, Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector;->initialSecondFinger:Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector$Finger;

    invoke-virtual {v8, v5}, Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector$Finger;->setEndX(F)V

    .line 178
    iget-object v8, p0, Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector;->initialSecondFinger:Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector$Finger;

    invoke-virtual {v8, v6}, Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector$Finger;->setEndY(F)V

    .line 179
    iput v9, p0, Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector;->secondPointerId:I

    goto :goto_2

    .line 186
    :cond_a
    iget-object v8, p0, Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector;->initialFirstFinger:Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector$Finger;

    invoke-virtual {v8}, Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector$Finger;->isScrollRight()Z

    move-result v8

    if-eqz v8, :cond_b

    iget-object v8, p0, Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector;->initialSecondFinger:Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector$Finger;

    .line 187
    invoke-virtual {v8}, Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector$Finger;->isScrollRight()Z

    move-result v8

    if-eqz v8, :cond_b

    .line 189
    iget-object v7, p0, Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector;->scrollListener:Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector$OnScrollListener;

    invoke-interface {v7}, Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector$OnScrollListener;->onScrollRight()Z

    move-result v1

    goto :goto_3

    .line 190
    :cond_b
    iget-object v8, p0, Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector;->initialFirstFinger:Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector$Finger;

    invoke-virtual {v8}, Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector$Finger;->isScrollUp()Z

    move-result v8

    if-eqz v8, :cond_c

    iget-object v8, p0, Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector;->initialSecondFinger:Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector$Finger;

    .line 191
    invoke-virtual {v8}, Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector$Finger;->isScrollUp()Z

    move-result v8

    if-eqz v8, :cond_c

    .line 193
    iget-object v7, p0, Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector;->scrollListener:Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector$OnScrollListener;

    invoke-interface {v7}, Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector$OnScrollListener;->onScrollUp()Z

    move-result v1

    goto :goto_3

    .line 195
    :cond_c
    iget-object v8, p0, Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector;->initialFirstFinger:Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector$Finger;

    invoke-virtual {v8}, Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector$Finger;->isScrollDown()Z

    move-result v8

    if-eqz v8, :cond_d

    iget-object v8, p0, Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector;->initialSecondFinger:Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector$Finger;

    .line 196
    invoke-virtual {v8}, Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector$Finger;->isScrollDown()Z

    move-result v8

    if-eqz v8, :cond_d

    iget-object v8, p0, Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector;->scrollListener:Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector$OnScrollListener;

    invoke-interface {v8}, Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector$OnScrollListener;->onScrollDown()Z

    move-result v8

    if-nez v8, :cond_8

    :cond_d
    move v1, v7

    goto :goto_3

    .line 202
    .end local v4    # "pointerIndex":I
    .end local v5    # "x":F
    .end local v6    # "y":F
    :cond_e
    invoke-direct {p0}, Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector;->clear()V

    goto/16 :goto_0

    .line 208
    :pswitch_6
    invoke-direct {p0}, Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector;->clear()V

    goto/16 :goto_0

    .line 102
    nop

    :pswitch_data_0
    .packed-switch 0x0
        :pswitch_1
        :pswitch_5
        :pswitch_3
        :pswitch_6
        :pswitch_0
        :pswitch_2
        :pswitch_4
    .end packed-switch
.end method

.method public setScrollListener(Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector$OnScrollListener;)V
    .locals 0
    .param p1, "aScrollListener"    # Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector$OnScrollListener;

    .prologue
    .line 84
    iput-object p1, p0, Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector;->scrollListener:Lcom/nuance/swype/input/chinese/TwoFingerGestureDetector$OnScrollListener;

    .line 85
    return-void
.end method
