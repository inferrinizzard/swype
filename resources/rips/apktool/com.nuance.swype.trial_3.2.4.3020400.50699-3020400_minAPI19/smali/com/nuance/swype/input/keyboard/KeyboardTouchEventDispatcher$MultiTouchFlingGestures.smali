.class Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$MultiTouchFlingGestures;
.super Ljava/lang/Object;
.source "KeyboardTouchEventDispatcher.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x2
    name = "MultiTouchFlingGestures"
.end annotation


# instance fields
.field private gestureDetectAndDispatched:Z

.field private final minFlingVelocity:I

.field final synthetic this$0:Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher;

.field private final touchGestures:Landroid/util/SparseArray;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Landroid/util/SparseArray",
            "<",
            "Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$TouchGesture;",
            ">;"
        }
    .end annotation
.end field

.field private velocityTracker:Landroid/view/VelocityTracker;


# direct methods
.method private constructor <init>(Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher;Landroid/content/Context;)V
    .locals 2
    .param p2, "context"    # Landroid/content/Context;

    .prologue
    .line 784
    iput-object p1, p0, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$MultiTouchFlingGestures;->this$0:Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 782
    new-instance v1, Landroid/util/SparseArray;

    invoke-direct {v1}, Landroid/util/SparseArray;-><init>()V

    iput-object v1, p0, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$MultiTouchFlingGestures;->touchGestures:Landroid/util/SparseArray;

    .line 785
    invoke-static {p2}, Landroid/view/ViewConfiguration;->get(Landroid/content/Context;)Landroid/view/ViewConfiguration;

    move-result-object v0

    .line 786
    .local v0, "configuration":Landroid/view/ViewConfiguration;
    invoke-virtual {v0}, Landroid/view/ViewConfiguration;->getScaledMinimumFlingVelocity()I

    move-result v1

    iput v1, p0, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$MultiTouchFlingGestures;->minFlingVelocity:I

    .line 787
    return-void
.end method

.method synthetic constructor <init>(Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher;Landroid/content/Context;Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$1;)V
    .locals 0
    .param p1, "x0"    # Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher;
    .param p2, "x1"    # Landroid/content/Context;
    .param p3, "x2"    # Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$1;

    .prologue
    .line 777
    invoke-direct {p0, p1, p2}, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$MultiTouchFlingGestures;-><init>(Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher;Landroid/content/Context;)V

    return-void
.end method

.method private checkAndDispatchGesture()Z
    .locals 7

    .prologue
    const/4 v4, 0x1

    .line 880
    iget-object v5, p0, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$MultiTouchFlingGestures;->touchGestures:Landroid/util/SparseArray;

    invoke-virtual {v5}, Landroid/util/SparseArray;->size()I

    move-result v5

    if-le v5, v4, :cond_3

    .line 881
    sget-object v1, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$GestureDirection;->FLIP_NONE:Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$GestureDirection;

    .line 882
    .local v1, "gestureDirection":Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$GestureDirection;
    const/4 v3, 0x0

    .line 884
    .local v3, "numOfGesturesDetected":I
    const/4 v2, 0x0

    .local v2, "i":I
    :goto_0
    iget-object v5, p0, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$MultiTouchFlingGestures;->touchGestures:Landroid/util/SparseArray;

    invoke-virtual {v5}, Landroid/util/SparseArray;->size()I

    move-result v5

    if-ge v2, v5, :cond_1

    .line 885
    iget-object v5, p0, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$MultiTouchFlingGestures;->touchGestures:Landroid/util/SparseArray;

    iget-object v6, p0, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$MultiTouchFlingGestures;->touchGestures:Landroid/util/SparseArray;

    invoke-virtual {v6, v2}, Landroid/util/SparseArray;->keyAt(I)I

    move-result v6

    invoke-virtual {v5, v6}, Landroid/util/SparseArray;->get(I)Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$TouchGesture;

    .line 888
    .local v0, "gesture":Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$TouchGesture;
    invoke-static {v0}, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$TouchGesture;->access$1800(Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$TouchGesture;)Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$GestureDirection;

    move-result-object v5

    sget-object v6, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$GestureDirection;->FLIP_NONE:Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$GestureDirection;

    if-eq v5, v6, :cond_0

    sget-object v5, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$GestureDirection;->FLIP_NONE:Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$GestureDirection;

    if-eq v1, v5, :cond_2

    .line 889
    invoke-static {v0}, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$TouchGesture;->access$1800(Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$TouchGesture;)Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$GestureDirection;

    move-result-object v5

    if-eq v5, v1, :cond_2

    .line 890
    :cond_0
    sget-object v1, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$GestureDirection;->FLIP_NONE:Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$GestureDirection;

    .line 898
    .end local v0    # "gesture":Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$TouchGesture;
    :cond_1
    iget-object v5, p0, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$MultiTouchFlingGestures;->touchGestures:Landroid/util/SparseArray;

    invoke-virtual {v5}, Landroid/util/SparseArray;->size()I

    move-result v5

    if-ne v3, v5, :cond_3

    .line 899
    invoke-direct {p0, v1}, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$MultiTouchFlingGestures;->dispatchGesture(Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$GestureDirection;)Z

    move-result v5

    if-eqz v5, :cond_3

    .line 900
    const/4 v2, 0x0

    :goto_1
    iget-object v5, p0, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$MultiTouchFlingGestures;->touchGestures:Landroid/util/SparseArray;

    invoke-virtual {v5}, Landroid/util/SparseArray;->size()I

    move-result v5

    if-ge v2, v5, :cond_4

    .line 901
    iget-object v5, p0, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$MultiTouchFlingGestures;->this$0:Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher;

    invoke-static {v5}, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher;->access$500(Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher;)Lcom/nuance/input/swypecorelib/XT9CoreInput;

    move-result-object v5

    invoke-virtual {v5, v2}, Lcom/nuance/input/swypecorelib/XT9CoreInput;->touchCancel(I)Z

    .line 902
    iget-object v5, p0, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$MultiTouchFlingGestures;->this$0:Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher;

    invoke-static {v5}, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher;->access$600(Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher;)Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$TouchKeyActionHandler;

    move-result-object v5

    const/4 v6, -0x1

    invoke-interface {v5, v2, v6}, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$TouchKeyActionHandler;->touchCanceled(II)V

    .line 900
    add-int/lit8 v2, v2, 0x1

    goto :goto_1

    .line 894
    .restart local v0    # "gesture":Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$TouchGesture;
    :cond_2
    add-int/lit8 v3, v3, 0x1

    .line 895
    invoke-static {v0}, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$TouchGesture;->access$1800(Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$TouchGesture;)Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$GestureDirection;

    move-result-object v1

    .line 884
    add-int/lit8 v2, v2, 0x1

    goto :goto_0

    .line 909
    .end local v0    # "gesture":Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$TouchGesture;
    .end local v1    # "gestureDirection":Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$GestureDirection;
    .end local v2    # "i":I
    .end local v3    # "numOfGesturesDetected":I
    :cond_3
    const/4 v4, 0x0

    :cond_4
    return v4
.end method

.method private detectGesture(Landroid/view/View;ILcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$TouchGesture;)Z
    .locals 11
    .param p1, "view"    # Landroid/view/View;
    .param p2, "pointerId"    # I
    .param p3, "gesture"    # Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$TouchGesture;

    .prologue
    const/4 v8, 0x4

    const/4 v7, 0x3

    const/4 v10, 0x2

    const/4 v2, 0x1

    const/4 v3, 0x0

    .line 858
    iget-object v4, p0, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$MultiTouchFlingGestures;->velocityTracker:Landroid/view/VelocityTracker;

    const/16 v5, 0x3e8

    invoke-virtual {v4, v5}, Landroid/view/VelocityTracker;->computeCurrentVelocity(I)V

    .line 859
    iget-object v4, p0, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$MultiTouchFlingGestures;->velocityTracker:Landroid/view/VelocityTracker;

    invoke-virtual {v4, p2}, Landroid/view/VelocityTracker;->getXVelocity(I)F

    move-result v0

    .line 860
    .local v0, "velocityX":F
    iget-object v4, p0, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$MultiTouchFlingGestures;->velocityTracker:Landroid/view/VelocityTracker;

    invoke-virtual {v4, p2}, Landroid/view/VelocityTracker;->getYVelocity(I)F

    move-result v1

    .line 862
    .local v1, "velocityY":F
    sget-object v4, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher;->log:Lcom/nuance/swype/util/LogManager$Log;

    new-array v5, v8, [Ljava/lang/Object;

    const-string/jumbo v6, "velocityX("

    aput-object v6, v5, v3

    invoke-static {p2}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v6

    aput-object v6, v5, v2

    const-string/jumbo v6, ") = "

    aput-object v6, v5, v10

    invoke-static {v0}, Ljava/lang/Float;->valueOf(F)Ljava/lang/Float;

    move-result-object v6

    aput-object v6, v5, v7

    invoke-interface {v4, v5}, Lcom/nuance/swype/util/LogManager$Log;->d([Ljava/lang/Object;)V

    .line 863
    sget-object v4, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher;->log:Lcom/nuance/swype/util/LogManager$Log;

    new-array v5, v8, [Ljava/lang/Object;

    const-string/jumbo v6, "velocityY("

    aput-object v6, v5, v3

    invoke-static {p2}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v6

    aput-object v6, v5, v2

    const-string/jumbo v6, ") = "

    aput-object v6, v5, v10

    invoke-static {v1}, Ljava/lang/Float;->valueOf(F)Ljava/lang/Float;

    move-result-object v6

    aput-object v6, v5, v7

    invoke-interface {v4, v5}, Lcom/nuance/swype/util/LogManager$Log;->d([Ljava/lang/Object;)V

    .line 865
    invoke-static {v0}, Ljava/lang/Math;->abs(F)F

    move-result v4

    iget v5, p0, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$MultiTouchFlingGestures;->minFlingVelocity:I

    int-to-float v5, v5

    cmpl-float v4, v4, v5

    if-gtz v4, :cond_0

    invoke-static {v1}, Ljava/lang/Math;->abs(F)F

    move-result v4

    iget v5, p0, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$MultiTouchFlingGestures;->minFlingVelocity:I

    int-to-float v5, v5

    cmpl-float v4, v4, v5

    if-lez v4, :cond_3

    .line 866
    :cond_0
    invoke-static {v0}, Ljava/lang/Math;->abs(F)F

    move-result v4

    iget v5, p0, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$MultiTouchFlingGestures;->minFlingVelocity:I

    int-to-float v5, v5

    cmpl-float v4, v4, v5

    if-gtz v4, :cond_1

    invoke-static {v1}, Ljava/lang/Math;->abs(F)F

    move-result v4

    iget v5, p0, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$MultiTouchFlingGestures;->minFlingVelocity:I

    int-to-float v5, v5

    cmpl-float v4, v4, v5

    if-lez v4, :cond_3

    .line 867
    :cond_1
    invoke-static {p3}, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$TouchGesture;->access$1400(Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$TouchGesture;)F

    move-result v4

    invoke-static {p3}, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$TouchGesture;->access$1500(Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$TouchGesture;)F

    move-result v5

    sub-float/2addr v4, v5

    invoke-static {v4}, Ljava/lang/Math;->abs(F)F

    move-result v4

    invoke-virtual {p1}, Landroid/view/View;->getWidth()I

    move-result v5

    int-to-double v6, v5

    const-wide/high16 v8, 0x3fe0000000000000L    # 0.5

    mul-double/2addr v6, v8

    double-to-int v5, v6

    int-to-float v5, v5

    cmpl-float v4, v4, v5

    if-gtz v4, :cond_2

    .line 868
    invoke-static {p3}, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$TouchGesture;->access$1600(Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$TouchGesture;)F

    move-result v4

    invoke-static {p3}, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$TouchGesture;->access$1700(Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$TouchGesture;)F

    move-result v5

    sub-float/2addr v4, v5

    invoke-static {v4}, Ljava/lang/Math;->abs(F)F

    move-result v4

    invoke-virtual {p1}, Landroid/view/View;->getHeight()I

    move-result v5

    int-to-double v6, v5

    const-wide v8, 0x3fe3333333333333L    # 0.6

    mul-double/2addr v6, v8

    double-to-int v5, v6

    int-to-float v5, v5

    cmpl-float v4, v4, v5

    if-lez v4, :cond_3

    .line 869
    :cond_2
    invoke-static {p3}, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$TouchGesture;->access$1400(Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$TouchGesture;)F

    move-result v4

    invoke-static {p3}, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$TouchGesture;->access$1600(Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$TouchGesture;)F

    move-result v5

    invoke-static {p3}, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$TouchGesture;->access$1500(Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$TouchGesture;)F

    move-result v6

    invoke-static {p3}, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$TouchGesture;->access$1700(Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$TouchGesture;)F

    move-result v7

    invoke-direct {p0, v4, v5, v6, v7}, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$MultiTouchFlingGestures;->getGestureDirection(FFFF)Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$GestureDirection;

    move-result-object v4

    invoke-static {p3, v4}, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$TouchGesture;->access$1802(Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$TouchGesture;Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$GestureDirection;)Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$GestureDirection;

    .line 874
    :cond_3
    sget-object v4, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher;->log:Lcom/nuance/swype/util/LogManager$Log;

    new-array v5, v10, [Ljava/lang/Object;

    const-string/jumbo v6, "gestureDirection = "

    aput-object v6, v5, v3

    invoke-static {p3}, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$TouchGesture;->access$1800(Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$TouchGesture;)Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$GestureDirection;

    move-result-object v6

    aput-object v6, v5, v2

    invoke-interface {v4, v5}, Lcom/nuance/swype/util/LogManager$Log;->d([Ljava/lang/Object;)V

    .line 876
    invoke-static {p3}, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$TouchGesture;->access$1800(Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$TouchGesture;)Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$GestureDirection;

    move-result-object v4

    sget-object v5, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$GestureDirection;->FLIP_NONE:Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$GestureDirection;

    if-eq v4, v5, :cond_4

    :goto_0
    return v2

    :cond_4
    move v2, v3

    goto :goto_0
.end method

.method private dispatchGesture(Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$GestureDirection;)Z
    .locals 4
    .param p1, "gestureDirection"    # Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$GestureDirection;

    .prologue
    const/4 v3, 0x1

    const/4 v0, 0x0

    .line 931
    sget-object v1, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$2;->$SwitchMap$com$nuance$swype$input$keyboard$KeyboardTouchEventDispatcher$GestureDirection:[I

    invoke-virtual {p1}, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$GestureDirection;->ordinal()I

    move-result v2

    aget v1, v1, v2

    packed-switch v1, :pswitch_data_0

    .line 953
    :goto_0
    return v0

    .line 933
    :pswitch_0
    sget-object v1, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher;->log:Lcom/nuance/swype/util/LogManager$Log;

    new-array v2, v3, [Ljava/lang/Object;

    const-string/jumbo v3, "fling up"

    aput-object v3, v2, v0

    invoke-interface {v1, v2}, Lcom/nuance/swype/util/LogManager$Log;->d([Ljava/lang/Object;)V

    .line 935
    iget-object v0, p0, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$MultiTouchFlingGestures;->this$0:Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher;

    invoke-static {v0}, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher;->access$1900(Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher;)Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$FlingGestureListener;

    move-result-object v0

    invoke-interface {v0}, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$FlingGestureListener;->onFlingUp()Z

    move-result v0

    goto :goto_0

    .line 938
    :pswitch_1
    sget-object v1, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher;->log:Lcom/nuance/swype/util/LogManager$Log;

    new-array v2, v3, [Ljava/lang/Object;

    const-string/jumbo v3, "fling down"

    aput-object v3, v2, v0

    invoke-interface {v1, v2}, Lcom/nuance/swype/util/LogManager$Log;->d([Ljava/lang/Object;)V

    .line 940
    iget-object v0, p0, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$MultiTouchFlingGestures;->this$0:Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher;

    invoke-static {v0}, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher;->access$1900(Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher;)Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$FlingGestureListener;

    move-result-object v0

    invoke-interface {v0}, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$FlingGestureListener;->onFlingDown()Z

    move-result v0

    goto :goto_0

    .line 943
    :pswitch_2
    sget-object v1, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher;->log:Lcom/nuance/swype/util/LogManager$Log;

    new-array v2, v3, [Ljava/lang/Object;

    const-string/jumbo v3, "fling left"

    aput-object v3, v2, v0

    invoke-interface {v1, v2}, Lcom/nuance/swype/util/LogManager$Log;->d([Ljava/lang/Object;)V

    .line 945
    iget-object v0, p0, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$MultiTouchFlingGestures;->this$0:Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher;

    invoke-static {v0}, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher;->access$1900(Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher;)Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$FlingGestureListener;

    move-result-object v0

    invoke-interface {v0}, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$FlingGestureListener;->onFlingLeft()Z

    move-result v0

    goto :goto_0

    .line 948
    :pswitch_3
    sget-object v1, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher;->log:Lcom/nuance/swype/util/LogManager$Log;

    new-array v2, v3, [Ljava/lang/Object;

    const-string/jumbo v3, "fling right"

    aput-object v3, v2, v0

    invoke-interface {v1, v2}, Lcom/nuance/swype/util/LogManager$Log;->d([Ljava/lang/Object;)V

    .line 950
    iget-object v0, p0, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$MultiTouchFlingGestures;->this$0:Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher;

    invoke-static {v0}, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher;->access$1900(Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher;)Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$FlingGestureListener;

    move-result-object v0

    invoke-interface {v0}, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$FlingGestureListener;->onFlingRight()Z

    move-result v0

    goto :goto_0

    .line 931
    :pswitch_data_0
    .packed-switch 0x1
        :pswitch_0
        :pswitch_1
        :pswitch_2
        :pswitch_3
    .end packed-switch
.end method

.method private getGestureDirection(FFFF)Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$GestureDirection;
    .locals 10
    .param p1, "x1"    # F
    .param p2, "y1"    # F
    .param p3, "x2"    # F
    .param p4, "y2"    # F

    .prologue
    const/16 v9, 0x87

    const/16 v8, 0x2d

    const/16 v7, -0x2d

    const/16 v6, -0x87

    .line 913
    sget-object v1, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$GestureDirection;->FLIP_NONE:Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$GestureDirection;

    .line 915
    .local v1, "direction":Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$GestureDirection;
    sub-float v2, p2, p4

    float-to-double v2, v2

    sub-float v4, p3, p1

    float-to-double v4, v4

    invoke-static {v2, v3, v4, v5}, Ljava/lang/Math;->atan2(DD)D

    move-result-wide v2

    invoke-static {v2, v3}, Ljava/lang/Math;->toDegrees(D)D

    move-result-wide v2

    invoke-static {v2, v3}, Ljava/lang/Math;->ceil(D)D

    move-result-wide v2

    double-to-int v0, v2

    .line 917
    .local v0, "angle":I
    if-le v0, v8, :cond_1

    if-gt v0, v9, :cond_1

    .line 918
    sget-object v1, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$GestureDirection;->FLIP_UP:Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$GestureDirection;

    .line 927
    :cond_0
    :goto_0
    return-object v1

    .line 919
    :cond_1
    if-lt v0, v9, :cond_2

    const/16 v2, 0xb4

    if-lt v0, v2, :cond_3

    :cond_2
    if-ge v0, v6, :cond_4

    const/16 v2, -0xb4

    if-le v0, v2, :cond_4

    .line 920
    :cond_3
    sget-object v1, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$GestureDirection;->FLIP_LEFT:Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$GestureDirection;

    goto :goto_0

    .line 921
    :cond_4
    if-ge v0, v7, :cond_5

    if-lt v0, v6, :cond_5

    .line 922
    sget-object v1, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$GestureDirection;->FLIP_DOWN:Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$GestureDirection;

    goto :goto_0

    .line 923
    :cond_5
    if-le v0, v7, :cond_0

    if-gt v0, v8, :cond_0

    .line 924
    sget-object v1, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$GestureDirection;->FLIP_RIGHT:Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$GestureDirection;

    goto :goto_0
.end method


# virtual methods
.method onTouch(Landroid/view/View;Landroid/view/MotionEvent;)Z
    .locals 8
    .param p1, "view"    # Landroid/view/View;
    .param p2, "event"    # Landroid/view/MotionEvent;

    .prologue
    const/4 v6, 0x0

    const/4 v4, 0x1

    const/4 v3, 0x0

    const/4 v7, -0x1

    .line 790
    const/4 v2, 0x0

    .line 792
    .local v2, "touchGestureDetected":Z
    iget-object v5, p0, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$MultiTouchFlingGestures;->velocityTracker:Landroid/view/VelocityTracker;

    if-nez v5, :cond_0

    .line 793
    invoke-static {}, Landroid/view/VelocityTracker;->obtain()Landroid/view/VelocityTracker;

    move-result-object v5

    iput-object v5, p0, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$MultiTouchFlingGestures;->velocityTracker:Landroid/view/VelocityTracker;

    .line 796
    :cond_0
    invoke-virtual {p2}, Landroid/view/MotionEvent;->getActionIndex()I

    move-result v5

    invoke-virtual {p2, v5}, Landroid/view/MotionEvent;->getPointerId(I)I

    move-result v0

    .line 798
    .local v0, "pointerId":I
    invoke-virtual {p2}, Landroid/view/MotionEvent;->getActionMasked()I

    move-result v5

    packed-switch v5, :pswitch_data_0

    .line 854
    :cond_1
    :goto_0
    :pswitch_0
    iget-boolean v5, p0, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$MultiTouchFlingGestures;->gestureDetectAndDispatched:Z

    if-nez v5, :cond_2

    if-eqz v2, :cond_3

    :cond_2
    move v3, v4

    :cond_3
    return v3

    .line 800
    :pswitch_1
    iput-boolean v3, p0, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$MultiTouchFlingGestures;->gestureDetectAndDispatched:Z

    .line 801
    iget-object v5, p0, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$MultiTouchFlingGestures;->velocityTracker:Landroid/view/VelocityTracker;

    invoke-virtual {v5}, Landroid/view/VelocityTracker;->clear()V

    .line 802
    iget-object v5, p0, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$MultiTouchFlingGestures;->touchGestures:Landroid/util/SparseArray;

    invoke-virtual {v5}, Landroid/util/SparseArray;->clear()V

    .line 803
    new-instance v1, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$TouchGesture;

    invoke-direct {v1, v6}, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$TouchGesture;-><init>(Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$1;)V

    .line 804
    .local v1, "touchGesture":Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$TouchGesture;
    invoke-virtual {p2}, Landroid/view/MotionEvent;->getX()F

    move-result v5

    invoke-virtual {p2}, Landroid/view/MotionEvent;->getY()F

    move-result v6

    invoke-static {v1, v5, v6}, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$TouchGesture;->access$1200(Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$TouchGesture;FF)V

    .line 805
    iget-object v5, p0, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$MultiTouchFlingGestures;->touchGestures:Landroid/util/SparseArray;

    invoke-virtual {v5, v0, v1}, Landroid/util/SparseArray;->put(ILjava/lang/Object;)V

    .line 806
    iget-object v5, p0, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$MultiTouchFlingGestures;->velocityTracker:Landroid/view/VelocityTracker;

    invoke-virtual {v5, p2}, Landroid/view/VelocityTracker;->addMovement(Landroid/view/MotionEvent;)V

    goto :goto_0

    .line 811
    .end local v1    # "touchGesture":Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$TouchGesture;
    :pswitch_2
    new-instance v1, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$TouchGesture;

    invoke-direct {v1, v6}, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$TouchGesture;-><init>(Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$1;)V

    .line 812
    .restart local v1    # "touchGesture":Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$TouchGesture;
    invoke-virtual {p2}, Landroid/view/MotionEvent;->getActionIndex()I

    move-result v5

    invoke-virtual {p2, v5}, Landroid/view/MotionEvent;->getX(I)F

    move-result v5

    invoke-virtual {p2}, Landroid/view/MotionEvent;->getActionIndex()I

    move-result v6

    invoke-virtual {p2, v6}, Landroid/view/MotionEvent;->getY(I)F

    move-result v6

    invoke-static {v1, v5, v6}, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$TouchGesture;->access$1200(Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$TouchGesture;FF)V

    .line 813
    iget-object v5, p0, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$MultiTouchFlingGestures;->touchGestures:Landroid/util/SparseArray;

    invoke-virtual {v5, v0, v1}, Landroid/util/SparseArray;->put(ILjava/lang/Object;)V

    .line 814
    iget-object v5, p0, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$MultiTouchFlingGestures;->velocityTracker:Landroid/view/VelocityTracker;

    invoke-virtual {v5, p2}, Landroid/view/VelocityTracker;->addMovement(Landroid/view/MotionEvent;)V

    goto :goto_0

    .line 819
    .end local v1    # "touchGesture":Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$TouchGesture;
    :pswitch_3
    iget-object v5, p0, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$MultiTouchFlingGestures;->velocityTracker:Landroid/view/VelocityTracker;

    invoke-virtual {v5, p2}, Landroid/view/VelocityTracker;->addMovement(Landroid/view/MotionEvent;)V

    goto :goto_0

    .line 824
    :pswitch_4
    iget-boolean v5, p0, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$MultiTouchFlingGestures;->gestureDetectAndDispatched:Z

    if-nez v5, :cond_1

    .line 825
    iget-object v5, p0, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$MultiTouchFlingGestures;->touchGestures:Landroid/util/SparseArray;

    invoke-virtual {v5, v0}, Landroid/util/SparseArray;->get(I)Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$TouchGesture;

    .line 826
    .restart local v1    # "touchGesture":Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$TouchGesture;
    invoke-virtual {p2}, Landroid/view/MotionEvent;->getActionIndex()I

    move-result v5

    invoke-virtual {p2, v5}, Landroid/view/MotionEvent;->getX(I)F

    move-result v5

    invoke-virtual {p2}, Landroid/view/MotionEvent;->getActionIndex()I

    move-result v6

    invoke-virtual {p2, v6}, Landroid/view/MotionEvent;->getY(I)F

    move-result v6

    invoke-static {v1, v5, v6}, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$TouchGesture;->access$1300(Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$TouchGesture;FF)V

    .line 827
    iget-object v5, p0, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$MultiTouchFlingGestures;->velocityTracker:Landroid/view/VelocityTracker;

    invoke-virtual {v5, p2}, Landroid/view/VelocityTracker;->addMovement(Landroid/view/MotionEvent;)V

    .line 828
    invoke-direct {p0, p1, v0, v1}, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$MultiTouchFlingGestures;->detectGesture(Landroid/view/View;ILcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$TouchGesture;)Z

    move-result v2

    .line 829
    if-eqz v2, :cond_4

    .line 830
    iget-object v5, p0, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$MultiTouchFlingGestures;->this$0:Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher;

    invoke-static {v5}, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher;->access$500(Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher;)Lcom/nuance/input/swypecorelib/XT9CoreInput;

    move-result-object v5

    invoke-virtual {v5, v0}, Lcom/nuance/input/swypecorelib/XT9CoreInput;->touchCancel(I)Z

    .line 831
    iget-object v5, p0, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$MultiTouchFlingGestures;->this$0:Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher;

    invoke-static {v5}, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher;->access$600(Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher;)Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$TouchKeyActionHandler;

    move-result-object v5

    invoke-interface {v5, v0, v7}, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$TouchKeyActionHandler;->touchCanceled(II)V

    .line 833
    :cond_4
    invoke-direct {p0}, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$MultiTouchFlingGestures;->checkAndDispatchGesture()Z

    move-result v5

    iput-boolean v5, p0, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$MultiTouchFlingGestures;->gestureDetectAndDispatched:Z

    goto/16 :goto_0

    .line 839
    .end local v1    # "touchGesture":Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$TouchGesture;
    :pswitch_5
    iget-object v5, p0, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$MultiTouchFlingGestures;->touchGestures:Landroid/util/SparseArray;

    invoke-virtual {v5}, Landroid/util/SparseArray;->size()I

    move-result v5

    if-le v5, v4, :cond_1

    iget-boolean v5, p0, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$MultiTouchFlingGestures;->gestureDetectAndDispatched:Z

    if-nez v5, :cond_1

    .line 840
    iget-object v5, p0, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$MultiTouchFlingGestures;->touchGestures:Landroid/util/SparseArray;

    invoke-virtual {v5, v0}, Landroid/util/SparseArray;->get(I)Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$TouchGesture;

    .line 841
    .restart local v1    # "touchGesture":Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$TouchGesture;
    invoke-virtual {p2}, Landroid/view/MotionEvent;->getActionIndex()I

    move-result v5

    invoke-virtual {p2, v5}, Landroid/view/MotionEvent;->getX(I)F

    move-result v5

    invoke-virtual {p2}, Landroid/view/MotionEvent;->getActionIndex()I

    move-result v6

    invoke-virtual {p2, v6}, Landroid/view/MotionEvent;->getY(I)F

    move-result v6

    invoke-static {v1, v5, v6}, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$TouchGesture;->access$1300(Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$TouchGesture;FF)V

    .line 842
    iget-object v5, p0, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$MultiTouchFlingGestures;->velocityTracker:Landroid/view/VelocityTracker;

    invoke-virtual {v5, p2}, Landroid/view/VelocityTracker;->addMovement(Landroid/view/MotionEvent;)V

    .line 843
    invoke-direct {p0, p1, v0, v1}, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$MultiTouchFlingGestures;->detectGesture(Landroid/view/View;ILcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$TouchGesture;)Z

    move-result v2

    .line 844
    if-eqz v2, :cond_5

    .line 845
    iget-object v5, p0, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$MultiTouchFlingGestures;->this$0:Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher;

    invoke-static {v5}, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher;->access$500(Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher;)Lcom/nuance/input/swypecorelib/XT9CoreInput;

    move-result-object v5

    invoke-virtual {v5, v0}, Lcom/nuance/input/swypecorelib/XT9CoreInput;->touchCancel(I)Z

    .line 846
    iget-object v5, p0, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$MultiTouchFlingGestures;->this$0:Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher;

    invoke-static {v5}, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher;->access$600(Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher;)Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$TouchKeyActionHandler;

    move-result-object v5

    invoke-interface {v5, v0, v7}, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$TouchKeyActionHandler;->touchCanceled(II)V

    .line 848
    :cond_5
    invoke-direct {p0}, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$MultiTouchFlingGestures;->checkAndDispatchGesture()Z

    move-result v5

    iput-boolean v5, p0, Lcom/nuance/swype/input/keyboard/KeyboardTouchEventDispatcher$MultiTouchFlingGestures;->gestureDetectAndDispatched:Z

    goto/16 :goto_0

    .line 798
    :pswitch_data_0
    .packed-switch 0x0
        :pswitch_1
        :pswitch_5
        :pswitch_3
        :pswitch_0
        :pswitch_0
        :pswitch_2
        :pswitch_4
    .end packed-switch
.end method
