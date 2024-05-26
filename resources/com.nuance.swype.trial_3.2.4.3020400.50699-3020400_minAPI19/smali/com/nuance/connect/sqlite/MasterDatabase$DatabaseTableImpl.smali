.class Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseTableImpl;
.super Ljava/lang/Object;

# interfaces
.implements Lcom/nuance/connect/sqlite/MasterDatabase$Codec;
.implements Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseTable;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/nuance/connect/sqlite/MasterDatabase;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x2
    name = "DatabaseTableImpl"
.end annotation


# instance fields
.field private final doNotEncrypt:Ljava/util/Set;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/Set",
            "<",
            "Ljava/lang/String;",
            ">;"
        }
    .end annotation
.end field

.field private final tableName:Ljava/lang/String;

.field final synthetic this$0:Lcom/nuance/connect/sqlite/MasterDatabase;


# direct methods
.method private constructor <init>(Lcom/nuance/connect/sqlite/MasterDatabase;Ljava/lang/String;Ljava/util/Set;)V
    .locals 1
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Ljava/lang/String;",
            "Ljava/util/Set",
            "<",
            "Ljava/lang/String;",
            ">;)V"
        }
    .end annotation

    iput-object p1, p0, Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseTableImpl;->this$0:Lcom/nuance/connect/sqlite/MasterDatabase;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    iput-object p2, p0, Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseTableImpl;->tableName:Ljava/lang/String;

    if-nez p3, :cond_0

    new-instance v0, Ljava/util/HashSet;

    invoke-direct {v0}, Ljava/util/HashSet;-><init>()V

    :goto_0
    iput-object v0, p0, Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseTableImpl;->doNotEncrypt:Ljava/util/Set;

    return-void

    :cond_0
    new-instance v0, Ljava/util/HashSet;

    invoke-direct {v0, p3}, Ljava/util/HashSet;-><init>(Ljava/util/Collection;)V

    goto :goto_0
.end method

.method synthetic constructor <init>(Lcom/nuance/connect/sqlite/MasterDatabase;Ljava/lang/String;Ljava/util/Set;Lcom/nuance/connect/sqlite/MasterDatabase$1;)V
    .locals 0

    invoke-direct {p0, p1, p2, p3}, Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseTableImpl;-><init>(Lcom/nuance/connect/sqlite/MasterDatabase;Ljava/lang/String;Ljava/util/Set;)V

    return-void
.end method

.method private encryptContentValues(Landroid/content/ContentValues;)Landroid/content/ContentValues;
    .locals 5

    new-instance v2, Landroid/content/ContentValues;

    invoke-direct {v2}, Landroid/content/ContentValues;-><init>()V

    invoke-virtual {p1}, Landroid/content/ContentValues;->valueSet()Ljava/util/Set;

    move-result-object v0

    invoke-interface {v0}, Ljava/util/Set;->iterator()Ljava/util/Iterator;

    move-result-object v3

    :goto_0
    invoke-interface {v3}, Ljava/util/Iterator;->hasNext()Z

    move-result v0

    if-eqz v0, :cond_a

    invoke-interface {v3}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Ljava/util/Map$Entry;

    invoke-interface {v0}, Ljava/util/Map$Entry;->getValue()Ljava/lang/Object;

    move-result-object v1

    invoke-interface {v0}, Ljava/util/Map$Entry;->getKey()Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Ljava/lang/String;

    invoke-virtual {p0, v0}, Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseTableImpl;->getActualColumnName(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v4

    instance-of v0, v1, Ljava/lang/String;

    if-eqz v0, :cond_0

    move-object v0, v1

    check-cast v0, Ljava/lang/String;

    invoke-virtual {p0, v0}, Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseTableImpl;->encryptString(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v0

    invoke-virtual {v2, v4, v0}, Landroid/content/ContentValues;->put(Ljava/lang/String;Ljava/lang/String;)V

    goto :goto_0

    :cond_0
    instance-of v0, v1, Ljava/lang/Byte;

    if-eqz v0, :cond_1

    check-cast v1, Ljava/lang/Byte;

    invoke-virtual {v2, v4, v1}, Landroid/content/ContentValues;->put(Ljava/lang/String;Ljava/lang/Byte;)V

    goto :goto_0

    :cond_1
    instance-of v0, v1, Ljava/lang/Short;

    if-eqz v0, :cond_2

    check-cast v1, Ljava/lang/Short;

    invoke-virtual {v2, v4, v1}, Landroid/content/ContentValues;->put(Ljava/lang/String;Ljava/lang/Short;)V

    goto :goto_0

    :cond_2
    instance-of v0, v1, Ljava/lang/Integer;

    if-eqz v0, :cond_3

    check-cast v1, Ljava/lang/Integer;

    invoke-virtual {v2, v4, v1}, Landroid/content/ContentValues;->put(Ljava/lang/String;Ljava/lang/Integer;)V

    goto :goto_0

    :cond_3
    instance-of v0, v1, Ljava/lang/Long;

    if-eqz v0, :cond_4

    check-cast v1, Ljava/lang/Long;

    invoke-virtual {v2, v4, v1}, Landroid/content/ContentValues;->put(Ljava/lang/String;Ljava/lang/Long;)V

    goto :goto_0

    :cond_4
    instance-of v0, v1, Ljava/lang/Float;

    if-eqz v0, :cond_5

    check-cast v1, Ljava/lang/Float;

    invoke-virtual {v2, v4, v1}, Landroid/content/ContentValues;->put(Ljava/lang/String;Ljava/lang/Float;)V

    goto :goto_0

    :cond_5
    instance-of v0, v1, Ljava/lang/Double;

    if-eqz v0, :cond_6

    check-cast v1, Ljava/lang/Double;

    invoke-virtual {v2, v4, v1}, Landroid/content/ContentValues;->put(Ljava/lang/String;Ljava/lang/Double;)V

    goto :goto_0

    :cond_6
    instance-of v0, v1, Ljava/lang/Boolean;

    if-eqz v0, :cond_7

    check-cast v1, Ljava/lang/Boolean;

    invoke-virtual {v2, v4, v1}, Landroid/content/ContentValues;->put(Ljava/lang/String;Ljava/lang/Boolean;)V

    goto :goto_0

    :cond_7
    instance-of v0, v1, [B

    if-eqz v0, :cond_8

    check-cast v1, [B

    check-cast v1, [B

    invoke-virtual {v2, v4, v1}, Landroid/content/ContentValues;->put(Ljava/lang/String;[B)V

    goto :goto_0

    :cond_8
    if-nez v1, :cond_9

    invoke-virtual {v2, v4}, Landroid/content/ContentValues;->putNull(Ljava/lang/String;)V

    goto :goto_0

    :cond_9
    invoke-static {}, Lcom/nuance/connect/sqlite/MasterDatabase;->access$100()Lcom/nuance/connect/util/Logger$Log;

    move-result-object v0

    const-string/jumbo v4, "Unknown type for object: "

    invoke-interface {v0, v4, v1}, Lcom/nuance/connect/util/Logger$Log;->e(Ljava/lang/Object;Ljava/lang/Object;)V

    goto/16 :goto_0

    :cond_a
    return-object v2
.end method

.method private encryptStringArray([Ljava/lang/String;)[Ljava/lang/String;
    .locals 3

    if-eqz p1, :cond_1

    array-length v0, p1

    new-array v1, v0, [Ljava/lang/String;

    const/4 v0, 0x0

    :goto_0
    array-length v2, p1

    if-ge v0, v2, :cond_0

    aget-object v2, p1, v0

    invoke-virtual {p0, v2}, Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseTableImpl;->encryptString(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v2

    aput-object v2, v1, v0

    add-int/lit8 v0, v0, 0x1

    goto :goto_0

    :cond_0
    move-object p1, v1

    :cond_1
    return-object p1
.end method

.method private getActualColumnNamesArray([Ljava/lang/String;)[Ljava/lang/String;
    .locals 3

    if-eqz p1, :cond_1

    array-length v0, p1

    new-array v1, v0, [Ljava/lang/String;

    const/4 v0, 0x0

    :goto_0
    array-length v2, p1

    if-ge v0, v2, :cond_0

    aget-object v2, p1, v0

    invoke-virtual {p0, v2}, Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseTableImpl;->getActualColumnName(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v2

    aput-object v2, v1, v0

    add-int/lit8 v0, v0, 0x1

    goto :goto_0

    :cond_0
    move-object p1, v1

    :cond_1
    return-object p1
.end method


# virtual methods
.method public beginTransaction()V
    .locals 1

    iget-object v0, p0, Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseTableImpl;->this$0:Lcom/nuance/connect/sqlite/MasterDatabase;

    invoke-static {v0}, Lcom/nuance/connect/sqlite/MasterDatabase;->access$000(Lcom/nuance/connect/sqlite/MasterDatabase;)Lcom/nuance/connect/sqlite/SQLDataSource;

    move-result-object v0

    invoke-virtual {v0}, Lcom/nuance/connect/sqlite/SQLDataSource;->beginTransaction()V

    return-void
.end method

.method public decryptString(Ljava/lang/String;)Ljava/lang/String;
    .locals 1

    iget-object v0, p0, Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseTableImpl;->doNotEncrypt:Ljava/util/Set;

    invoke-interface {v0, p1}, Ljava/util/Set;->contains(Ljava/lang/Object;)Z

    move-result v0

    if-eqz v0, :cond_0

    :goto_0
    return-object p1

    :cond_0
    invoke-static {}, Lcom/nuance/connect/sqlite/MasterDatabase;->access$200()Lcom/nuance/connect/sqlite/MasterDatabase$Codec;

    move-result-object v0

    invoke-interface {v0, p1}, Lcom/nuance/connect/sqlite/MasterDatabase$Codec;->decryptString(Ljava/lang/String;)Ljava/lang/String;

    move-result-object p1

    goto :goto_0
.end method

.method public delete(Ljava/lang/String;[Ljava/lang/String;)I
    .locals 6

    const/4 v0, 0x0

    :try_start_0
    iget-object v1, p0, Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseTableImpl;->this$0:Lcom/nuance/connect/sqlite/MasterDatabase;

    invoke-static {v1}, Lcom/nuance/connect/sqlite/MasterDatabase;->access$000(Lcom/nuance/connect/sqlite/MasterDatabase;)Lcom/nuance/connect/sqlite/SQLDataSource;

    move-result-object v1

    invoke-virtual {v1}, Lcom/nuance/connect/sqlite/SQLDataSource;->incrementOpenRefCount()I
    :try_end_0
    .catch Ljava/lang/IllegalStateException; {:try_start_0 .. :try_end_0} :catch_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    move-result v1

    :try_start_1
    iget-object v2, p0, Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseTableImpl;->this$0:Lcom/nuance/connect/sqlite/MasterDatabase;

    invoke-static {v2}, Lcom/nuance/connect/sqlite/MasterDatabase;->access$000(Lcom/nuance/connect/sqlite/MasterDatabase;)Lcom/nuance/connect/sqlite/SQLDataSource;

    move-result-object v2

    invoke-virtual {v2}, Lcom/nuance/connect/sqlite/SQLDataSource;->getDatabase()Landroid/database/sqlite/SQLiteDatabase;

    move-result-object v2

    invoke-virtual {p0}, Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseTableImpl;->getName()Ljava/lang/String;

    move-result-object v3

    invoke-direct {p0, p2}, Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseTableImpl;->encryptStringArray([Ljava/lang/String;)[Ljava/lang/String;

    move-result-object v4

    invoke-virtual {v2, v3, p1, v4}, Landroid/database/sqlite/SQLiteDatabase;->delete(Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;)I
    :try_end_1
    .catch Ljava/lang/IllegalStateException; {:try_start_1 .. :try_end_1} :catch_1
    .catchall {:try_start_1 .. :try_end_1} :catchall_1

    move-result v0

    iget-object v2, p0, Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseTableImpl;->this$0:Lcom/nuance/connect/sqlite/MasterDatabase;

    invoke-static {v2}, Lcom/nuance/connect/sqlite/MasterDatabase;->access$000(Lcom/nuance/connect/sqlite/MasterDatabase;)Lcom/nuance/connect/sqlite/SQLDataSource;

    move-result-object v2

    invoke-virtual {v2, v1}, Lcom/nuance/connect/sqlite/SQLDataSource;->decrementOpenRefCount(I)V

    :goto_0
    return v0

    :catch_0
    move-exception v1

    move v1, v0

    :goto_1
    :try_start_2
    invoke-static {}, Lcom/nuance/connect/sqlite/MasterDatabase;->access$100()Lcom/nuance/connect/util/Logger$Log;

    move-result-object v2

    const-string/jumbo v3, "delete could not open database: "

    invoke-virtual {p0}, Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseTableImpl;->getName()Ljava/lang/String;

    move-result-object v4

    invoke-interface {v2, v3, v4}, Lcom/nuance/connect/util/Logger$Log;->e(Ljava/lang/Object;Ljava/lang/Object;)V
    :try_end_2
    .catchall {:try_start_2 .. :try_end_2} :catchall_1

    iget-object v2, p0, Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseTableImpl;->this$0:Lcom/nuance/connect/sqlite/MasterDatabase;

    invoke-static {v2}, Lcom/nuance/connect/sqlite/MasterDatabase;->access$000(Lcom/nuance/connect/sqlite/MasterDatabase;)Lcom/nuance/connect/sqlite/SQLDataSource;

    move-result-object v2

    invoke-virtual {v2, v1}, Lcom/nuance/connect/sqlite/SQLDataSource;->decrementOpenRefCount(I)V

    goto :goto_0

    :catchall_0
    move-exception v1

    move-object v5, v1

    move v1, v0

    move-object v0, v5

    :goto_2
    iget-object v2, p0, Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseTableImpl;->this$0:Lcom/nuance/connect/sqlite/MasterDatabase;

    invoke-static {v2}, Lcom/nuance/connect/sqlite/MasterDatabase;->access$000(Lcom/nuance/connect/sqlite/MasterDatabase;)Lcom/nuance/connect/sqlite/SQLDataSource;

    move-result-object v2

    invoke-virtual {v2, v1}, Lcom/nuance/connect/sqlite/SQLDataSource;->decrementOpenRefCount(I)V

    throw v0

    :catchall_1
    move-exception v0

    goto :goto_2

    :catch_1
    move-exception v2

    goto :goto_1
.end method

.method public deleteAll()Z
    .locals 2

    const-string/jumbo v0, "1"

    const/4 v1, 0x0

    invoke-virtual {p0, v0, v1}, Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseTableImpl;->delete(Ljava/lang/String;[Ljava/lang/String;)I

    move-result v0

    if-ltz v0, :cond_0

    const/4 v0, 0x1

    :goto_0
    return v0

    :cond_0
    const/4 v0, 0x0

    goto :goto_0
.end method

.method public encryptString(Ljava/lang/String;)Ljava/lang/String;
    .locals 1

    iget-object v0, p0, Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseTableImpl;->doNotEncrypt:Ljava/util/Set;

    invoke-interface {v0, p1}, Ljava/util/Set;->contains(Ljava/lang/Object;)Z

    move-result v0

    if-eqz v0, :cond_0

    :goto_0
    return-object p1

    :cond_0
    invoke-static {}, Lcom/nuance/connect/sqlite/MasterDatabase;->access$200()Lcom/nuance/connect/sqlite/MasterDatabase$Codec;

    move-result-object v0

    invoke-interface {v0, p1}, Lcom/nuance/connect/sqlite/MasterDatabase$Codec;->encryptString(Ljava/lang/String;)Ljava/lang/String;

    move-result-object p1

    goto :goto_0
.end method

.method public endTransaction()V
    .locals 1

    iget-object v0, p0, Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseTableImpl;->this$0:Lcom/nuance/connect/sqlite/MasterDatabase;

    invoke-static {v0}, Lcom/nuance/connect/sqlite/MasterDatabase;->access$000(Lcom/nuance/connect/sqlite/MasterDatabase;)Lcom/nuance/connect/sqlite/SQLDataSource;

    move-result-object v0

    invoke-virtual {v0}, Lcom/nuance/connect/sqlite/SQLDataSource;->endTransaction()V

    return-void
.end method

.method public getActualColumnName(Ljava/lang/String;)Ljava/lang/String;
    .locals 1

    iget-object v0, p0, Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseTableImpl;->doNotEncrypt:Ljava/util/Set;

    invoke-interface {v0, p1}, Ljava/util/Set;->contains(Ljava/lang/Object;)Z

    move-result v0

    if-eqz v0, :cond_0

    :goto_0
    return-object p1

    :cond_0
    iget-object v0, p0, Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseTableImpl;->this$0:Lcom/nuance/connect/sqlite/MasterDatabase;

    invoke-virtual {v0, p1}, Lcom/nuance/connect/sqlite/MasterDatabase;->getActualColumnName(Ljava/lang/String;)Ljava/lang/String;

    move-result-object p1

    goto :goto_0
.end method

.method public getCodec()Lcom/nuance/connect/sqlite/MasterDatabase$Codec;
    .locals 0

    return-object p0
.end method

.method public getColumnName(Ljava/lang/String;)Ljava/lang/String;
    .locals 1

    invoke-virtual {p0, p1}, Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseTableImpl;->getActualColumnName(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public getLogicalColumnName(Ljava/lang/String;)Ljava/lang/String;
    .locals 1

    iget-object v0, p0, Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseTableImpl;->doNotEncrypt:Ljava/util/Set;

    invoke-interface {v0, p1}, Ljava/util/Set;->contains(Ljava/lang/Object;)Z

    move-result v0

    if-eqz v0, :cond_0

    :goto_0
    return-object p1

    :cond_0
    iget-object v0, p0, Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseTableImpl;->this$0:Lcom/nuance/connect/sqlite/MasterDatabase;

    invoke-static {v0, p1}, Lcom/nuance/connect/sqlite/MasterDatabase;->access$300(Lcom/nuance/connect/sqlite/MasterDatabase;Ljava/lang/String;)Ljava/lang/String;

    move-result-object p1

    goto :goto_0
.end method

.method public getName()Ljava/lang/String;
    .locals 1

    iget-object v0, p0, Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseTableImpl;->tableName:Ljava/lang/String;

    return-object v0
.end method

.method public insert(Ljava/lang/String;Landroid/content/ContentValues;)J
    .locals 4

    const/4 v0, 0x0

    :try_start_0
    iget-object v1, p0, Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseTableImpl;->this$0:Lcom/nuance/connect/sqlite/MasterDatabase;

    invoke-static {v1}, Lcom/nuance/connect/sqlite/MasterDatabase;->access$000(Lcom/nuance/connect/sqlite/MasterDatabase;)Lcom/nuance/connect/sqlite/SQLDataSource;

    move-result-object v1

    invoke-virtual {v1}, Lcom/nuance/connect/sqlite/SQLDataSource;->incrementOpenRefCount()I
    :try_end_0
    .catch Ljava/lang/IllegalStateException; {:try_start_0 .. :try_end_0} :catch_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    move-result v2

    :try_start_1
    iget-object v0, p0, Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseTableImpl;->this$0:Lcom/nuance/connect/sqlite/MasterDatabase;

    invoke-static {v0}, Lcom/nuance/connect/sqlite/MasterDatabase;->access$000(Lcom/nuance/connect/sqlite/MasterDatabase;)Lcom/nuance/connect/sqlite/SQLDataSource;

    move-result-object v0

    invoke-virtual {v0}, Lcom/nuance/connect/sqlite/SQLDataSource;->getDatabase()Landroid/database/sqlite/SQLiteDatabase;

    move-result-object v0

    invoke-virtual {p0}, Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseTableImpl;->getName()Ljava/lang/String;

    move-result-object v1

    invoke-direct {p0, p2}, Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseTableImpl;->encryptContentValues(Landroid/content/ContentValues;)Landroid/content/ContentValues;

    move-result-object v3

    invoke-virtual {v0, v1, p1, v3}, Landroid/database/sqlite/SQLiteDatabase;->insert(Ljava/lang/String;Ljava/lang/String;Landroid/content/ContentValues;)J
    :try_end_1
    .catch Ljava/lang/IllegalStateException; {:try_start_1 .. :try_end_1} :catch_1
    .catchall {:try_start_1 .. :try_end_1} :catchall_1

    move-result-wide v0

    iget-object v3, p0, Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseTableImpl;->this$0:Lcom/nuance/connect/sqlite/MasterDatabase;

    invoke-static {v3}, Lcom/nuance/connect/sqlite/MasterDatabase;->access$000(Lcom/nuance/connect/sqlite/MasterDatabase;)Lcom/nuance/connect/sqlite/SQLDataSource;

    move-result-object v3

    invoke-virtual {v3, v2}, Lcom/nuance/connect/sqlite/SQLDataSource;->decrementOpenRefCount(I)V

    :goto_0
    return-wide v0

    :catch_0
    move-exception v1

    :goto_1
    :try_start_2
    invoke-static {}, Lcom/nuance/connect/sqlite/MasterDatabase;->access$100()Lcom/nuance/connect/util/Logger$Log;

    move-result-object v1

    const-string/jumbo v2, "insert could not open database: "

    invoke-virtual {p0}, Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseTableImpl;->getName()Ljava/lang/String;

    move-result-object v3

    invoke-interface {v1, v2, v3}, Lcom/nuance/connect/util/Logger$Log;->e(Ljava/lang/Object;Ljava/lang/Object;)V
    :try_end_2
    .catchall {:try_start_2 .. :try_end_2} :catchall_0

    iget-object v1, p0, Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseTableImpl;->this$0:Lcom/nuance/connect/sqlite/MasterDatabase;

    invoke-static {v1}, Lcom/nuance/connect/sqlite/MasterDatabase;->access$000(Lcom/nuance/connect/sqlite/MasterDatabase;)Lcom/nuance/connect/sqlite/SQLDataSource;

    move-result-object v1

    invoke-virtual {v1, v0}, Lcom/nuance/connect/sqlite/SQLDataSource;->decrementOpenRefCount(I)V

    const-wide/16 v0, -0x1

    goto :goto_0

    :catchall_0
    move-exception v1

    move v2, v0

    move-object v0, v1

    :goto_2
    iget-object v1, p0, Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseTableImpl;->this$0:Lcom/nuance/connect/sqlite/MasterDatabase;

    invoke-static {v1}, Lcom/nuance/connect/sqlite/MasterDatabase;->access$000(Lcom/nuance/connect/sqlite/MasterDatabase;)Lcom/nuance/connect/sqlite/SQLDataSource;

    move-result-object v1

    invoke-virtual {v1, v2}, Lcom/nuance/connect/sqlite/SQLDataSource;->decrementOpenRefCount(I)V

    throw v0

    :catchall_1
    move-exception v0

    goto :goto_2

    :catch_1
    move-exception v0

    move v0, v2

    goto :goto_1
.end method

.method public insertWithOnConflict(Ljava/lang/String;Landroid/content/ContentValues;I)J
    .locals 4

    const/4 v0, 0x0

    :try_start_0
    iget-object v1, p0, Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseTableImpl;->this$0:Lcom/nuance/connect/sqlite/MasterDatabase;

    invoke-static {v1}, Lcom/nuance/connect/sqlite/MasterDatabase;->access$000(Lcom/nuance/connect/sqlite/MasterDatabase;)Lcom/nuance/connect/sqlite/SQLDataSource;

    move-result-object v1

    invoke-virtual {v1}, Lcom/nuance/connect/sqlite/SQLDataSource;->incrementOpenRefCount()I
    :try_end_0
    .catch Ljava/lang/IllegalStateException; {:try_start_0 .. :try_end_0} :catch_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    move-result v2

    :try_start_1
    iget-object v0, p0, Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseTableImpl;->this$0:Lcom/nuance/connect/sqlite/MasterDatabase;

    invoke-static {v0}, Lcom/nuance/connect/sqlite/MasterDatabase;->access$000(Lcom/nuance/connect/sqlite/MasterDatabase;)Lcom/nuance/connect/sqlite/SQLDataSource;

    move-result-object v0

    invoke-virtual {v0}, Lcom/nuance/connect/sqlite/SQLDataSource;->getDatabase()Landroid/database/sqlite/SQLiteDatabase;

    move-result-object v0

    invoke-virtual {p0}, Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseTableImpl;->getName()Ljava/lang/String;

    move-result-object v1

    invoke-direct {p0, p2}, Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseTableImpl;->encryptContentValues(Landroid/content/ContentValues;)Landroid/content/ContentValues;

    move-result-object v3

    invoke-virtual {v0, v1, p1, v3, p3}, Landroid/database/sqlite/SQLiteDatabase;->insertWithOnConflict(Ljava/lang/String;Ljava/lang/String;Landroid/content/ContentValues;I)J
    :try_end_1
    .catch Ljava/lang/IllegalStateException; {:try_start_1 .. :try_end_1} :catch_1
    .catchall {:try_start_1 .. :try_end_1} :catchall_1

    move-result-wide v0

    iget-object v3, p0, Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseTableImpl;->this$0:Lcom/nuance/connect/sqlite/MasterDatabase;

    invoke-static {v3}, Lcom/nuance/connect/sqlite/MasterDatabase;->access$000(Lcom/nuance/connect/sqlite/MasterDatabase;)Lcom/nuance/connect/sqlite/SQLDataSource;

    move-result-object v3

    invoke-virtual {v3, v2}, Lcom/nuance/connect/sqlite/SQLDataSource;->decrementOpenRefCount(I)V

    :goto_0
    return-wide v0

    :catch_0
    move-exception v1

    :goto_1
    :try_start_2
    invoke-static {}, Lcom/nuance/connect/sqlite/MasterDatabase;->access$100()Lcom/nuance/connect/util/Logger$Log;

    move-result-object v1

    const-string/jumbo v2, "insertWithOnConflict could not open database: "

    invoke-virtual {p0}, Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseTableImpl;->getName()Ljava/lang/String;

    move-result-object v3

    invoke-interface {v1, v2, v3}, Lcom/nuance/connect/util/Logger$Log;->e(Ljava/lang/Object;Ljava/lang/Object;)V
    :try_end_2
    .catchall {:try_start_2 .. :try_end_2} :catchall_0

    iget-object v1, p0, Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseTableImpl;->this$0:Lcom/nuance/connect/sqlite/MasterDatabase;

    invoke-static {v1}, Lcom/nuance/connect/sqlite/MasterDatabase;->access$000(Lcom/nuance/connect/sqlite/MasterDatabase;)Lcom/nuance/connect/sqlite/SQLDataSource;

    move-result-object v1

    invoke-virtual {v1, v0}, Lcom/nuance/connect/sqlite/SQLDataSource;->decrementOpenRefCount(I)V

    const-wide/16 v0, -0x1

    goto :goto_0

    :catchall_0
    move-exception v1

    move v2, v0

    move-object v0, v1

    :goto_2
    iget-object v1, p0, Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseTableImpl;->this$0:Lcom/nuance/connect/sqlite/MasterDatabase;

    invoke-static {v1}, Lcom/nuance/connect/sqlite/MasterDatabase;->access$000(Lcom/nuance/connect/sqlite/MasterDatabase;)Lcom/nuance/connect/sqlite/SQLDataSource;

    move-result-object v1

    invoke-virtual {v1, v2}, Lcom/nuance/connect/sqlite/SQLDataSource;->decrementOpenRefCount(I)V

    throw v0

    :catchall_1
    move-exception v0

    goto :goto_2

    :catch_1
    move-exception v0

    move v0, v2

    goto :goto_1
.end method

.method public isFull()Z
    .locals 1

    const/4 v0, 0x0

    return v0
.end method

.method public query(Z[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    .locals 13

    :try_start_0
    new-instance v11, Lcom/nuance/connect/sqlite/MasterDatabase$DecryptionCursor;

    iget-object v1, p0, Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseTableImpl;->this$0:Lcom/nuance/connect/sqlite/MasterDatabase;

    invoke-static {v1}, Lcom/nuance/connect/sqlite/MasterDatabase;->access$000(Lcom/nuance/connect/sqlite/MasterDatabase;)Lcom/nuance/connect/sqlite/SQLDataSource;

    move-result-object v12

    iget-object v1, p0, Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseTableImpl;->this$0:Lcom/nuance/connect/sqlite/MasterDatabase;

    invoke-static {v1}, Lcom/nuance/connect/sqlite/MasterDatabase;->access$000(Lcom/nuance/connect/sqlite/MasterDatabase;)Lcom/nuance/connect/sqlite/SQLDataSource;

    move-result-object v1

    invoke-virtual {v1}, Lcom/nuance/connect/sqlite/SQLDataSource;->getDatabase()Landroid/database/sqlite/SQLiteDatabase;

    move-result-object v1

    invoke-virtual {p0}, Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseTableImpl;->getName()Ljava/lang/String;

    move-result-object v3

    invoke-direct {p0, p2}, Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseTableImpl;->getActualColumnNamesArray([Ljava/lang/String;)[Ljava/lang/String;

    move-result-object v4

    move-object/from16 v0, p4

    invoke-direct {p0, v0}, Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseTableImpl;->encryptStringArray([Ljava/lang/String;)[Ljava/lang/String;

    move-result-object v6

    move v2, p1

    move-object/from16 v5, p3

    move-object/from16 v7, p5

    move-object/from16 v8, p6

    move-object/from16 v9, p7

    move-object/from16 v10, p8

    invoke-virtual/range {v1 .. v10}, Landroid/database/sqlite/SQLiteDatabase;->query(ZLjava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;

    move-result-object v1

    invoke-direct {v11, v12, v1, p0}, Lcom/nuance/connect/sqlite/MasterDatabase$DecryptionCursor;-><init>(Lcom/nuance/connect/sqlite/SQLDataSource;Landroid/database/Cursor;Lcom/nuance/connect/sqlite/MasterDatabase$Codec;)V
    :try_end_0
    .catch Ljava/lang/IllegalStateException; {:try_start_0 .. :try_end_0} :catch_0

    move-object v1, v11

    :goto_0
    return-object v1

    :catch_0
    move-exception v1

    invoke-static {}, Lcom/nuance/connect/sqlite/MasterDatabase;->access$100()Lcom/nuance/connect/util/Logger$Log;

    move-result-object v1

    const-string/jumbo v2, "query could not open database: "

    invoke-virtual {p0}, Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseTableImpl;->getName()Ljava/lang/String;

    move-result-object v3

    invoke-interface {v1, v2, v3}, Lcom/nuance/connect/util/Logger$Log;->e(Ljava/lang/Object;Ljava/lang/Object;)V

    const/4 v1, 0x0

    goto :goto_0
.end method

.method public setTransactionSuccessful()V
    .locals 1

    iget-object v0, p0, Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseTableImpl;->this$0:Lcom/nuance/connect/sqlite/MasterDatabase;

    invoke-static {v0}, Lcom/nuance/connect/sqlite/MasterDatabase;->access$000(Lcom/nuance/connect/sqlite/MasterDatabase;)Lcom/nuance/connect/sqlite/SQLDataSource;

    move-result-object v0

    invoke-virtual {v0}, Lcom/nuance/connect/sqlite/SQLDataSource;->setTransactionSuccessful()V

    return-void
.end method

.method public update(Landroid/content/ContentValues;Ljava/lang/String;[Ljava/lang/String;)I
    .locals 7

    const/4 v0, 0x0

    :try_start_0
    iget-object v1, p0, Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseTableImpl;->this$0:Lcom/nuance/connect/sqlite/MasterDatabase;

    invoke-static {v1}, Lcom/nuance/connect/sqlite/MasterDatabase;->access$000(Lcom/nuance/connect/sqlite/MasterDatabase;)Lcom/nuance/connect/sqlite/SQLDataSource;

    move-result-object v1

    invoke-virtual {v1}, Lcom/nuance/connect/sqlite/SQLDataSource;->incrementOpenRefCount()I
    :try_end_0
    .catch Ljava/lang/IllegalStateException; {:try_start_0 .. :try_end_0} :catch_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    move-result v1

    :try_start_1
    iget-object v2, p0, Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseTableImpl;->this$0:Lcom/nuance/connect/sqlite/MasterDatabase;

    invoke-static {v2}, Lcom/nuance/connect/sqlite/MasterDatabase;->access$000(Lcom/nuance/connect/sqlite/MasterDatabase;)Lcom/nuance/connect/sqlite/SQLDataSource;

    move-result-object v2

    invoke-virtual {v2}, Lcom/nuance/connect/sqlite/SQLDataSource;->getDatabase()Landroid/database/sqlite/SQLiteDatabase;

    move-result-object v2

    invoke-virtual {p0}, Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseTableImpl;->getName()Ljava/lang/String;

    move-result-object v3

    invoke-direct {p0, p1}, Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseTableImpl;->encryptContentValues(Landroid/content/ContentValues;)Landroid/content/ContentValues;

    move-result-object v4

    invoke-direct {p0, p3}, Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseTableImpl;->encryptStringArray([Ljava/lang/String;)[Ljava/lang/String;

    move-result-object v5

    invoke-virtual {v2, v3, v4, p2, v5}, Landroid/database/sqlite/SQLiteDatabase;->update(Ljava/lang/String;Landroid/content/ContentValues;Ljava/lang/String;[Ljava/lang/String;)I
    :try_end_1
    .catch Ljava/lang/IllegalStateException; {:try_start_1 .. :try_end_1} :catch_1
    .catchall {:try_start_1 .. :try_end_1} :catchall_1

    move-result v0

    iget-object v2, p0, Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseTableImpl;->this$0:Lcom/nuance/connect/sqlite/MasterDatabase;

    invoke-static {v2}, Lcom/nuance/connect/sqlite/MasterDatabase;->access$000(Lcom/nuance/connect/sqlite/MasterDatabase;)Lcom/nuance/connect/sqlite/SQLDataSource;

    move-result-object v2

    invoke-virtual {v2, v1}, Lcom/nuance/connect/sqlite/SQLDataSource;->decrementOpenRefCount(I)V

    :goto_0
    return v0

    :catch_0
    move-exception v1

    move v1, v0

    :goto_1
    :try_start_2
    invoke-static {}, Lcom/nuance/connect/sqlite/MasterDatabase;->access$100()Lcom/nuance/connect/util/Logger$Log;

    move-result-object v2

    const-string/jumbo v3, "update could not open database: "

    invoke-virtual {p0}, Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseTableImpl;->getName()Ljava/lang/String;

    move-result-object v4

    invoke-interface {v2, v3, v4}, Lcom/nuance/connect/util/Logger$Log;->e(Ljava/lang/Object;Ljava/lang/Object;)V
    :try_end_2
    .catchall {:try_start_2 .. :try_end_2} :catchall_1

    iget-object v2, p0, Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseTableImpl;->this$0:Lcom/nuance/connect/sqlite/MasterDatabase;

    invoke-static {v2}, Lcom/nuance/connect/sqlite/MasterDatabase;->access$000(Lcom/nuance/connect/sqlite/MasterDatabase;)Lcom/nuance/connect/sqlite/SQLDataSource;

    move-result-object v2

    invoke-virtual {v2, v1}, Lcom/nuance/connect/sqlite/SQLDataSource;->decrementOpenRefCount(I)V

    goto :goto_0

    :catchall_0
    move-exception v1

    move-object v6, v1

    move v1, v0

    move-object v0, v6

    :goto_2
    iget-object v2, p0, Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseTableImpl;->this$0:Lcom/nuance/connect/sqlite/MasterDatabase;

    invoke-static {v2}, Lcom/nuance/connect/sqlite/MasterDatabase;->access$000(Lcom/nuance/connect/sqlite/MasterDatabase;)Lcom/nuance/connect/sqlite/SQLDataSource;

    move-result-object v2

    invoke-virtual {v2, v1}, Lcom/nuance/connect/sqlite/SQLDataSource;->decrementOpenRefCount(I)V

    throw v0

    :catchall_1
    move-exception v0

    goto :goto_2

    :catch_1
    move-exception v2

    goto :goto_1
.end method
