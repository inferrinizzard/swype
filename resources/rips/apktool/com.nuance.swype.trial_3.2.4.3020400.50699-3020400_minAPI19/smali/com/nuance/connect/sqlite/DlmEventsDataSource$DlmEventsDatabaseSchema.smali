.class Lcom/nuance/connect/sqlite/DlmEventsDataSource$DlmEventsDatabaseSchema;
.super Ljava/lang/Object;

# interfaces
.implements Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseSchema;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/nuance/connect/sqlite/DlmEventsDataSource;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0xa
    name = "DlmEventsDatabaseSchema"
.end annotation


# instance fields
.field private final context:Landroid/content/Context;


# direct methods
.method private constructor <init>(Landroid/content/Context;)V
    .locals 1

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    invoke-virtual {p1}, Landroid/content/Context;->getApplicationContext()Landroid/content/Context;

    move-result-object v0

    iput-object v0, p0, Lcom/nuance/connect/sqlite/DlmEventsDataSource$DlmEventsDatabaseSchema;->context:Landroid/content/Context;

    return-void
.end method

.method public static declared-synchronized from(Landroid/content/Context;)Lcom/nuance/connect/sqlite/DlmEventsDataSource$DlmEventsDatabaseSchema;
    .locals 2

    const-class v1, Lcom/nuance/connect/sqlite/DlmEventsDataSource$DlmEventsDatabaseSchema;

    monitor-enter v1

    :try_start_0
    invoke-static {}, Lcom/nuance/connect/sqlite/DlmEventsDataSource;->access$000()Lcom/nuance/connect/sqlite/DlmEventsDataSource$DlmEventsDatabaseSchema;

    move-result-object v0

    if-nez v0, :cond_0

    new-instance v0, Lcom/nuance/connect/sqlite/DlmEventsDataSource$DlmEventsDatabaseSchema;

    invoke-direct {v0, p0}, Lcom/nuance/connect/sqlite/DlmEventsDataSource$DlmEventsDatabaseSchema;-><init>(Landroid/content/Context;)V

    invoke-static {v0}, Lcom/nuance/connect/sqlite/DlmEventsDataSource;->access$002(Lcom/nuance/connect/sqlite/DlmEventsDataSource$DlmEventsDatabaseSchema;)Lcom/nuance/connect/sqlite/DlmEventsDataSource$DlmEventsDatabaseSchema;

    :cond_0
    invoke-static {}, Lcom/nuance/connect/sqlite/DlmEventsDataSource;->access$000()Lcom/nuance/connect/sqlite/DlmEventsDataSource$DlmEventsDatabaseSchema;
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    move-result-object v0

    monitor-exit v1

    return-object v0

    :catchall_0
    move-exception v0

    monitor-exit v1

    throw v0
.end method


# virtual methods
.method public doNotEncrypt()Ljava/util/Set;
    .locals 2
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "()",
            "Ljava/util/Set",
            "<",
            "Ljava/lang/String;",
            ">;"
        }
    .end annotation

    new-instance v0, Ljava/util/HashSet;

    invoke-direct {v0}, Ljava/util/HashSet;-><init>()V

    const-string/jumbo v1, "rowid"

    invoke-virtual {v0, v1}, Ljava/util/HashSet;->add(Ljava/lang/Object;)Z

    return-object v0
.end method

.method public getName()Ljava/lang/String;
    .locals 1

    const-string/jumbo v0, "dlmevents"

    return-object v0
.end method

.method public getTableSchemas()Ljava/util/Set;
    .locals 2
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "()",
            "Ljava/util/Set",
            "<",
            "Lcom/nuance/connect/sqlite/MasterDatabase$TableSchema;",
            ">;"
        }
    .end annotation

    new-instance v0, Ljava/util/HashSet;

    invoke-direct {v0}, Ljava/util/HashSet;-><init>()V

    invoke-static {}, Lcom/nuance/connect/sqlite/DlmEventsDataSource;->access$100()Lcom/nuance/connect/sqlite/MasterDatabase$TableSchema;

    move-result-object v1

    invoke-virtual {v0, v1}, Ljava/util/HashSet;->add(Ljava/lang/Object;)Z

    invoke-static {}, Lcom/nuance/connect/sqlite/DlmEventsDataSource;->access$200()Lcom/nuance/connect/sqlite/MasterDatabase$TableSchema;

    move-result-object v1

    invoke-virtual {v0, v1}, Ljava/util/HashSet;->add(Ljava/lang/Object;)Z

    invoke-static {}, Lcom/nuance/connect/sqlite/DlmEventsDataSource;->access$300()Lcom/nuance/connect/sqlite/MasterDatabase$TableSchema;

    move-result-object v1

    invoke-virtual {v0, v1}, Ljava/util/HashSet;->add(Ljava/lang/Object;)Z

    invoke-static {}, Lcom/nuance/connect/sqlite/DlmEventsDataSource;->access$400()Lcom/nuance/connect/sqlite/MasterDatabase$TableSchema;

    move-result-object v1

    invoke-virtual {v0, v1}, Ljava/util/HashSet;->add(Ljava/lang/Object;)Z

    invoke-static {}, Lcom/nuance/connect/sqlite/DlmEventsDataSource;->access$500()Lcom/nuance/connect/sqlite/MasterDatabase$TableSchema;

    move-result-object v1

    invoke-virtual {v0, v1}, Ljava/util/HashSet;->add(Ljava/lang/Object;)Z

    return-object v0
.end method

.method public onMigration()V
    .locals 2

    iget-object v0, p0, Lcom/nuance/connect/sqlite/DlmEventsDataSource$DlmEventsDatabaseSchema;->context:Landroid/content/Context;

    const-string/jumbo v1, "dlmevents"

    invoke-virtual {v0, v1}, Landroid/content/Context;->deleteDatabase(Ljava/lang/String;)Z

    return-void
.end method

.method public onUpgrade(Landroid/database/sqlite/SQLiteDatabase;II)V
    .locals 0

    return-void
.end method
