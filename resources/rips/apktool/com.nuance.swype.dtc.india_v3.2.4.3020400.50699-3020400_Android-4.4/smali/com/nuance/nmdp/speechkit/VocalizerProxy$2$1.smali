.class Lcom/nuance/nmdp/speechkit/VocalizerProxy$2$1;
.super Ljava/lang/Object;
.source "VocalizerProxy.java"

# interfaces
.implements Ljava/lang/Runnable;


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Lcom/nuance/nmdp/speechkit/VocalizerProxy$2;->onSpeakingBegin(Lcom/nuance/nmdp/speechkit/Vocalizer;Ljava/lang/String;Ljava/lang/Object;)V
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$1:Lcom/nuance/nmdp/speechkit/VocalizerProxy$2;

.field final synthetic val$context:Ljava/lang/Object;

.field final synthetic val$text:Ljava/lang/String;


# direct methods
.method constructor <init>(Lcom/nuance/nmdp/speechkit/VocalizerProxy$2;Ljava/lang/String;Ljava/lang/Object;)V
    .locals 0

    .prologue
    .line 40
    iput-object p1, p0, Lcom/nuance/nmdp/speechkit/VocalizerProxy$2$1;->this$1:Lcom/nuance/nmdp/speechkit/VocalizerProxy$2;

    iput-object p2, p0, Lcom/nuance/nmdp/speechkit/VocalizerProxy$2$1;->val$text:Ljava/lang/String;

    iput-object p3, p0, Lcom/nuance/nmdp/speechkit/VocalizerProxy$2$1;->val$context:Ljava/lang/Object;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public run()V
    .locals 4

    .prologue
    .line 43
    iget-object v0, p0, Lcom/nuance/nmdp/speechkit/VocalizerProxy$2$1;->this$1:Lcom/nuance/nmdp/speechkit/VocalizerProxy$2;

    iget-object v0, v0, Lcom/nuance/nmdp/speechkit/VocalizerProxy$2;->this$0:Lcom/nuance/nmdp/speechkit/VocalizerProxy;

    invoke-static {v0}, Lcom/nuance/nmdp/speechkit/VocalizerProxy;->access$300(Lcom/nuance/nmdp/speechkit/VocalizerProxy;)Lcom/nuance/nmdp/speechkit/Vocalizer$Listener;

    move-result-object v0

    iget-object v1, p0, Lcom/nuance/nmdp/speechkit/VocalizerProxy$2$1;->this$1:Lcom/nuance/nmdp/speechkit/VocalizerProxy$2;

    iget-object v1, v1, Lcom/nuance/nmdp/speechkit/VocalizerProxy$2;->this$0:Lcom/nuance/nmdp/speechkit/VocalizerProxy;

    iget-object v2, p0, Lcom/nuance/nmdp/speechkit/VocalizerProxy$2$1;->val$text:Ljava/lang/String;

    iget-object v3, p0, Lcom/nuance/nmdp/speechkit/VocalizerProxy$2$1;->val$context:Ljava/lang/Object;

    invoke-interface {v0, v1, v2, v3}, Lcom/nuance/nmdp/speechkit/Vocalizer$Listener;->onSpeakingBegin(Lcom/nuance/nmdp/speechkit/Vocalizer;Ljava/lang/String;Ljava/lang/Object;)V

    .line 44
    return-void
.end method
