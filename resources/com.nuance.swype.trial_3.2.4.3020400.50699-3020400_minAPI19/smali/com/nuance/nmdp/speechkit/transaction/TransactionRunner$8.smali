.class Lcom/nuance/nmdp/speechkit/transaction/TransactionRunner$8;
.super Ljava/lang/Object;
.source "TransactionRunner.java"

# interfaces
.implements Lcom/nuance/nmdp/speechkit/transaction/vocalize/IVocalizeTransactionListener;


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Lcom/nuance/nmdp/speechkit/transaction/TransactionRunner;->createVocalizeTransaction(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;ZLcom/nuance/nmdp/speechkit/transaction/vocalize/IVocalizeTransactionListener;)Lcom/nuance/nmdp/speechkit/transaction/vocalize/IVocalizeTransaction;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Lcom/nuance/nmdp/speechkit/transaction/TransactionRunner;

.field final synthetic val$listener:Lcom/nuance/nmdp/speechkit/transaction/vocalize/IVocalizeTransactionListener;


# direct methods
.method constructor <init>(Lcom/nuance/nmdp/speechkit/transaction/TransactionRunner;Lcom/nuance/nmdp/speechkit/transaction/vocalize/IVocalizeTransactionListener;)V
    .locals 0

    .prologue
    .line 544
    iput-object p1, p0, Lcom/nuance/nmdp/speechkit/transaction/TransactionRunner$8;->this$0:Lcom/nuance/nmdp/speechkit/transaction/TransactionRunner;

    iput-object p2, p0, Lcom/nuance/nmdp/speechkit/transaction/TransactionRunner$8;->val$listener:Lcom/nuance/nmdp/speechkit/transaction/vocalize/IVocalizeTransactionListener;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public audioStarted(Lcom/nuance/nmdp/speechkit/transaction/ITransaction;)V
    .locals 1
    .param p1, "t"    # Lcom/nuance/nmdp/speechkit/transaction/ITransaction;

    .prologue
    .line 561
    iget-object v0, p0, Lcom/nuance/nmdp/speechkit/transaction/TransactionRunner$8;->val$listener:Lcom/nuance/nmdp/speechkit/transaction/vocalize/IVocalizeTransactionListener;

    invoke-interface {v0, p1}, Lcom/nuance/nmdp/speechkit/transaction/vocalize/IVocalizeTransactionListener;->audioStarted(Lcom/nuance/nmdp/speechkit/transaction/ITransaction;)V

    .line 562
    return-void
.end method

.method public error(Lcom/nuance/nmdp/speechkit/transaction/ITransaction;ILjava/lang/String;Ljava/lang/String;)V
    .locals 1
    .param p1, "t"    # Lcom/nuance/nmdp/speechkit/transaction/ITransaction;
    .param p2, "errorCode"    # I
    .param p3, "errorText"    # Ljava/lang/String;
    .param p4, "suggestion"    # Ljava/lang/String;

    .prologue
    .line 547
    iget-object v0, p0, Lcom/nuance/nmdp/speechkit/transaction/TransactionRunner$8;->val$listener:Lcom/nuance/nmdp/speechkit/transaction/vocalize/IVocalizeTransactionListener;

    invoke-interface {v0, p1, p2, p3, p4}, Lcom/nuance/nmdp/speechkit/transaction/vocalize/IVocalizeTransactionListener;->error(Lcom/nuance/nmdp/speechkit/transaction/ITransaction;ILjava/lang/String;Ljava/lang/String;)V

    .line 549
    const/4 v0, 0x1

    if-ne p2, v0, :cond_0

    .line 550
    iget-object v0, p0, Lcom/nuance/nmdp/speechkit/transaction/TransactionRunner$8;->this$0:Lcom/nuance/nmdp/speechkit/transaction/TransactionRunner;

    invoke-static {v0}, Lcom/nuance/nmdp/speechkit/transaction/TransactionRunner;->access$100(Lcom/nuance/nmdp/speechkit/transaction/TransactionRunner;)V

    .line 551
    :cond_0
    return-void
.end method

.method public transactionDone(Lcom/nuance/nmdp/speechkit/transaction/ITransaction;)V
    .locals 1
    .param p1, "t"    # Lcom/nuance/nmdp/speechkit/transaction/ITransaction;

    .prologue
    .line 555
    iget-object v0, p0, Lcom/nuance/nmdp/speechkit/transaction/TransactionRunner$8;->this$0:Lcom/nuance/nmdp/speechkit/transaction/TransactionRunner;

    invoke-static {v0, p1}, Lcom/nuance/nmdp/speechkit/transaction/TransactionRunner;->access$000(Lcom/nuance/nmdp/speechkit/transaction/TransactionRunner;Lcom/nuance/nmdp/speechkit/transaction/ITransaction;)V

    .line 556
    iget-object v0, p0, Lcom/nuance/nmdp/speechkit/transaction/TransactionRunner$8;->val$listener:Lcom/nuance/nmdp/speechkit/transaction/vocalize/IVocalizeTransactionListener;

    invoke-interface {v0, p1}, Lcom/nuance/nmdp/speechkit/transaction/vocalize/IVocalizeTransactionListener;->transactionDone(Lcom/nuance/nmdp/speechkit/transaction/ITransaction;)V

    .line 557
    return-void
.end method
