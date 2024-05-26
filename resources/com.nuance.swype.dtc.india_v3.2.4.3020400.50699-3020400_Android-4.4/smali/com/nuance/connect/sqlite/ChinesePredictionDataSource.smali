.class public Lcom/nuance/connect/sqlite/ChinesePredictionDataSource;
.super Ljava/lang/Object;


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Lcom/nuance/connect/sqlite/ChinesePredictionDataSource$ChinesePredictionDatabaseSchema;,
        Lcom/nuance/connect/sqlite/ChinesePredictionDataSource$ChinesePredictionResultDataReturn;
    }
.end annotation


# static fields
.field private static final DB_NAME:Ljava/lang/String; = "chineseprediction"

.field private static final DB_VERSION:I = 0x2

.field private static final FIELD_RESULT_APPLICATION_NAME:Ljava/lang/String; = "k"

.field private static final FIELD_RESULT_ATTRIBUTE:Ljava/lang/String; = "f"

.field private static final FIELD_RESULT_CCPS_VERSION:Ljava/lang/String; = "i"

.field private static final FIELD_RESULT_CLOUD_TIME:Ljava/lang/String; = "g"

.field private static final FIELD_RESULT_COUNTRY:Ljava/lang/String; = "l"

.field private static final FIELD_RESULT_CREATED_STAMP:Ljava/lang/String; = "j"

.field private static final FIELD_RESULT_FULLSPELL:Ljava/lang/String; = "e"

.field private static final FIELD_RESULT_PHRASE:Ljava/lang/String; = "c"

.field private static final FIELD_RESULT_PREDICTION_ID:Ljava/lang/String; = "a"

.field private static final FIELD_RESULT_SPELL:Ljava/lang/String; = "d"

.field private static final FIELD_RESULT_TOTAL_TIME:Ljava/lang/String; = "h"

.field private static final FIELD_RESULT_TYPE:Ljava/lang/String; = "b"

.field private static final MAX_DATABASE_SIZE:I = 0x100000

.field private static final MAX_WAIT_TRANSMIT_TIME:J = 0x927c0L

.field private static final PREDICTION_TABLE:Ljava/lang/String; = "chineseprediction_aa"

.field public static final RESULT_TYPE_CANCELED:I = 0x2

.field public static final RESULT_TYPE_COMPLETED:I = 0x3

.field public static final RESULT_TYPE_FAILED:I = 0x1

.field public static final RESULT_TYPE_PENDING:I = 0x0

.field private static final ROWID:Ljava/lang/String; = "rowid"

.field public static final UNKNOWN:Ljava/lang/String; = "UNKNOWN"

.field private static final predictionTableSchema:Lcom/nuance/connect/sqlite/MasterDatabase$TableSchema;

.field private static schemaInstance:Lcom/nuance/connect/sqlite/ChinesePredictionDataSource$ChinesePredictionDatabaseSchema;


# instance fields
.field private final context:Landroid/content/Context;

.field private final handler:Lcom/nuance/connect/sqlite/PredictionDatabaseHandlerThread;

.field private log:Lcom/nuance/connect/util/Logger$Log;

.field private final predictionTable:Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseTable;


# direct methods
.method static constructor <clinit>()V
    .locals 1

    new-instance v0, Lcom/nuance/connect/sqlite/ChinesePredictionDataSource$1;

    invoke-direct {v0}, Lcom/nuance/connect/sqlite/ChinesePredictionDataSource$1;-><init>()V

    sput-object v0, Lcom/nuance/connect/sqlite/ChinesePredictionDataSource;->predictionTableSchema:Lcom/nuance/connect/sqlite/MasterDatabase$TableSchema;

    return-void
.end method

.method public constructor <init>(Landroid/content/Context;)V
    .locals 2

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    sget-object v0, Lcom/nuance/connect/util/Logger$LoggerType;->DEVELOPER:Lcom/nuance/connect/util/Logger$LoggerType;

    invoke-virtual {p0}, Ljava/lang/Object;->getClass()Ljava/lang/Class;

    move-result-object v1

    invoke-virtual {v1}, Ljava/lang/Class;->getSimpleName()Ljava/lang/String;

    move-result-object v1

    invoke-static {v0, v1}, Lcom/nuance/connect/util/Logger;->getLog(Lcom/nuance/connect/util/Logger$LoggerType;Ljava/lang/String;)Lcom/nuance/connect/util/Logger$Log;

    move-result-object v0

    iput-object v0, p0, Lcom/nuance/connect/sqlite/ChinesePredictionDataSource;->log:Lcom/nuance/connect/util/Logger$Log;

    invoke-virtual {p1}, Landroid/content/Context;->getApplicationContext()Landroid/content/Context;

    move-result-object v0

    iput-object v0, p0, Lcom/nuance/connect/sqlite/ChinesePredictionDataSource;->context:Landroid/content/Context;

    iget-object v0, p0, Lcom/nuance/connect/sqlite/ChinesePredictionDataSource;->context:Landroid/content/Context;

    invoke-static {v0}, Lcom/nuance/connect/sqlite/MasterDatabase;->from(Landroid/content/Context;)Lcom/nuance/connect/sqlite/MasterDatabase;

    move-result-object v0

    invoke-static {p1}, Lcom/nuance/connect/sqlite/ChinesePredictionDataSource$ChinesePredictionDatabaseSchema;->from(Landroid/content/Context;)Lcom/nuance/connect/sqlite/ChinesePredictionDataSource$ChinesePredictionDatabaseSchema;

    move-result-object v1

    invoke-virtual {v0, v1}, Lcom/nuance/connect/sqlite/MasterDatabase;->connectDatabase(Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseSchema;)V

    sget-object v1, Lcom/nuance/connect/sqlite/ChinesePredictionDataSource;->predictionTableSchema:Lcom/nuance/connect/sqlite/MasterDatabase$TableSchema;

    invoke-virtual {v0, v1}, Lcom/nuance/connect/sqlite/MasterDatabase;->getTableDatabase(Lcom/nuance/connect/sqlite/MasterDatabase$TableSchema;)Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseTable;

    move-result-object v0

    iput-object v0, p0, Lcom/nuance/connect/sqlite/ChinesePredictionDataSource;->predictionTable:Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseTable;

    new-instance v0, Lcom/nuance/connect/sqlite/PredictionDatabaseHandlerThread;

    invoke-direct {v0, p0}, Lcom/nuance/connect/sqlite/PredictionDatabaseHandlerThread;-><init>(Lcom/nuance/connect/sqlite/ChinesePredictionDataSource;)V

    iput-object v0, p0, Lcom/nuance/connect/sqlite/ChinesePredictionDataSource;->handler:Lcom/nuance/connect/sqlite/PredictionDatabaseHandlerThread;

    iget-object v0, p0, Lcom/nuance/connect/sqlite/ChinesePredictionDataSource;->handler:Lcom/nuance/connect/sqlite/PredictionDatabaseHandlerThread;

    invoke-virtual {v0}, Lcom/nuance/connect/sqlite/PredictionDatabaseHandlerThread;->start()V

    return-void
.end method

.method static synthetic access$000()Lcom/nuance/connect/sqlite/ChinesePredictionDataSource$ChinesePredictionDatabaseSchema;
    .locals 1

    sget-object v0, Lcom/nuance/connect/sqlite/ChinesePredictionDataSource;->schemaInstance:Lcom/nuance/connect/sqlite/ChinesePredictionDataSource$ChinesePredictionDatabaseSchema;

    return-object v0
.end method

.method static synthetic access$002(Lcom/nuance/connect/sqlite/ChinesePredictionDataSource$ChinesePredictionDatabaseSchema;)Lcom/nuance/connect/sqlite/ChinesePredictionDataSource$ChinesePredictionDatabaseSchema;
    .locals 0

    sput-object p0, Lcom/nuance/connect/sqlite/ChinesePredictionDataSource;->schemaInstance:Lcom/nuance/connect/sqlite/ChinesePredictionDataSource$ChinesePredictionDatabaseSchema;

    return-object p0
.end method

.method static synthetic access$100()Lcom/nuance/connect/sqlite/MasterDatabase$TableSchema;
    .locals 1

    sget-object v0, Lcom/nuance/connect/sqlite/ChinesePredictionDataSource;->predictionTableSchema:Lcom/nuance/connect/sqlite/MasterDatabase$TableSchema;

    return-object v0
.end method

.method private getIntegerArray(Ljava/lang/String;)Ljava/util/ArrayList;
    .locals 3
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Ljava/lang/String;",
            ")",
            "Ljava/util/ArrayList",
            "<",
            "Ljava/lang/Integer;",
            ">;"
        }
    .end annotation

    new-instance v1, Ljava/util/ArrayList;

    invoke-direct {v1}, Ljava/util/ArrayList;-><init>()V

    if-eqz p1, :cond_0

    const/4 v0, 0x0

    :goto_0
    invoke-virtual {p1}, Ljava/lang/String;->length()I

    move-result v2

    if-ge v0, v2, :cond_0

    invoke-virtual {p1, v0}, Ljava/lang/String;->charAt(I)C

    move-result v2

    invoke-static {v2}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v2

    invoke-virtual {v1, v0, v2}, Ljava/util/ArrayList;->add(ILjava/lang/Object;)V

    add-int/lit8 v0, v0, 0x1

    goto :goto_0

    :cond_0
    return-object v1
.end method

.method private parseStringToIntegerArray(Ljava/lang/String;)Ljava/util/ArrayList;
    .locals 5
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Ljava/lang/String;",
            ")",
            "Ljava/util/ArrayList",
            "<",
            "Ljava/lang/Integer;",
            ">;"
        }
    .end annotation

    new-instance v1, Ljava/util/ArrayList;

    invoke-direct {v1}, Ljava/util/ArrayList;-><init>()V

    if-eqz p1, :cond_0

    const-string/jumbo v0, ","

    invoke-virtual {p1, v0}, Ljava/lang/String;->split(Ljava/lang/String;)[Ljava/lang/String;

    move-result-object v2

    const/4 v0, 0x0

    :goto_0
    array-length v3, v2

    if-ge v0, v3, :cond_0

    :try_start_0
    aget-object v3, v2, v0

    invoke-static {v3}, Ljava/lang/Integer;->parseInt(Ljava/lang/String;)I

    move-result v3

    invoke-static {v3}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v3

    invoke-virtual {v1, v0, v3}, Ljava/util/ArrayList;->add(ILjava/lang/Object;)V
    :try_end_0
    .catch Ljava/lang/NumberFormatException; {:try_start_0 .. :try_end_0} :catch_0

    :goto_1
    add-int/lit8 v0, v0, 0x1

    goto :goto_0

    :catch_0
    move-exception v3

    iget-object v3, p0, Lcom/nuance/connect/sqlite/ChinesePredictionDataSource;->log:Lcom/nuance/connect/util/Logger$Log;

    const-string/jumbo v4, "NumberFormatException in parseStringToIntegerArray() string: "

    invoke-interface {v3, v4, p1}, Lcom/nuance/connect/util/Logger$Log;->d(Ljava/lang/Object;Ljava/lang/Object;)V

    goto :goto_1

    :cond_0
    return-object v1
.end method


# virtual methods
.method deletePredictions(I)V
    .locals 4

    iget-object v0, p0, Lcom/nuance/connect/sqlite/ChinesePredictionDataSource;->log:Lcom/nuance/connect/util/Logger$Log;

    const-string/jumbo v1, "deletePredictions beforeRowId="

    invoke-static {p1}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v2

    invoke-interface {v0, v1, v2}, Lcom/nuance/connect/util/Logger$Log;->d(Ljava/lang/Object;Ljava/lang/Object;)V

    :try_start_0
    new-instance v1, Ljava/lang/StringBuilder;

    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    const/4 v0, 0x1

    new-array v0, v0, [Ljava/lang/String;

    const/4 v2, 0x0

    invoke-static {p1}, Ljava/lang/String;->valueOf(I)Ljava/lang/String;

    move-result-object v3

    aput-object v3, v0, v2

    if-lez p1, :cond_0

    const-string/jumbo v2, "rowid"

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    const-string/jumbo v2, " < ?"

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    :goto_0
    iget-object v2, p0, Lcom/nuance/connect/sqlite/ChinesePredictionDataSource;->predictionTable:Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseTable;

    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v1

    invoke-interface {v2, v1, v0}, Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseTable;->delete(Ljava/lang/String;[Ljava/lang/String;)I

    move-result v0

    iget-object v1, p0, Lcom/nuance/connect/sqlite/ChinesePredictionDataSource;->log:Lcom/nuance/connect/util/Logger$Log;

    const-string/jumbo v2, "deletePredictions rows="

    invoke-static {v0}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v0

    invoke-interface {v1, v2, v0}, Lcom/nuance/connect/util/Logger$Log;->d(Ljava/lang/Object;Ljava/lang/Object;)V

    :goto_1
    return-void

    :cond_0
    const/4 v0, 0x1

    invoke-virtual {v1, v0}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;
    :try_end_0
    .catch Landroid/database/sqlite/SQLiteException; {:try_start_0 .. :try_end_0} :catch_0

    const/4 v0, 0x0

    goto :goto_0

    :catch_0
    move-exception v0

    iget-object v1, p0, Lcom/nuance/connect/sqlite/ChinesePredictionDataSource;->log:Lcom/nuance/connect/util/Logger$Log;

    const-string/jumbo v2, "deletePredictions failed: "

    invoke-virtual {v0}, Landroid/database/sqlite/SQLiteException;->getMessage()Ljava/lang/String;

    move-result-object v0

    invoke-interface {v1, v2, v0}, Lcom/nuance/connect/util/Logger$Log;->e(Ljava/lang/Object;Ljava/lang/Object;)V

    goto :goto_1
.end method

.method public deletePredictionsFrom(I)V
    .locals 4

    const/4 v3, 0x0

    iget-object v0, p0, Lcom/nuance/connect/sqlite/ChinesePredictionDataSource;->handler:Lcom/nuance/connect/sqlite/PredictionDatabaseHandlerThread;

    const/4 v1, 0x1

    invoke-static {v3}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v2

    invoke-virtual {v0, v1, p1, v3, v2}, Lcom/nuance/connect/sqlite/PredictionDatabaseHandlerThread;->process(IIILjava/lang/Object;)V

    return-void
.end method

.method public getPendingLogSize()I
    .locals 11

    const/4 v9, 0x0

    const/4 v10, 0x0

    iget-object v0, p0, Lcom/nuance/connect/sqlite/ChinesePredictionDataSource;->log:Lcom/nuance/connect/util/Logger$Log;

    const-string/jumbo v1, "getPendingLogSize"

    invoke-interface {v0, v1}, Lcom/nuance/connect/util/Logger$Log;->d(Ljava/lang/Object;)V

    :try_start_0
    iget-object v0, p0, Lcom/nuance/connect/sqlite/ChinesePredictionDataSource;->predictionTable:Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseTable;

    const/4 v1, 0x0

    const/4 v2, 0x0

    const/4 v3, 0x0

    const/4 v4, 0x0

    const/4 v5, 0x0

    const/4 v6, 0x0

    const/4 v7, 0x0

    const/4 v8, 0x0

    invoke-interface/range {v0 .. v8}, Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseTable;->query(Z[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    move-result-object v1

    if-eqz v1, :cond_1

    :try_start_1
    invoke-interface {v1}, Landroid/database/Cursor;->getCount()I
    :try_end_1
    .catchall {:try_start_1 .. :try_end_1} :catchall_1

    move-result v0

    :goto_0
    if-eqz v1, :cond_0

    :try_start_2
    invoke-interface {v1}, Landroid/database/Cursor;->close()V
    :try_end_2
    .catch Landroid/database/sqlite/SQLiteException; {:try_start_2 .. :try_end_2} :catch_1

    :cond_0
    :goto_1
    return v0

    :cond_1
    move v0, v9

    goto :goto_0

    :catchall_0
    move-exception v0

    move-object v1, v10

    :goto_2
    if-eqz v1, :cond_2

    :try_start_3
    invoke-interface {v1}, Landroid/database/Cursor;->close()V

    :cond_2
    throw v0
    :try_end_3
    .catch Landroid/database/sqlite/SQLiteException; {:try_start_3 .. :try_end_3} :catch_0

    :catch_0
    move-exception v0

    move-object v1, v0

    move v0, v9

    :goto_3
    iget-object v2, p0, Lcom/nuance/connect/sqlite/ChinesePredictionDataSource;->log:Lcom/nuance/connect/util/Logger$Log;

    const-string/jumbo v3, "getPendingLogSize failed: "

    invoke-virtual {v1}, Landroid/database/sqlite/SQLiteException;->getMessage()Ljava/lang/String;

    move-result-object v1

    invoke-interface {v2, v3, v1}, Lcom/nuance/connect/util/Logger$Log;->e(Ljava/lang/Object;Ljava/lang/Object;)V

    goto :goto_1

    :catch_1
    move-exception v1

    goto :goto_3

    :catchall_1
    move-exception v0

    goto :goto_2
.end method

.method public getPredictions(Ljava/lang/String;)Lcom/nuance/connect/sqlite/ChinesePredictionDataSource$ChinesePredictionResultDataReturn;
    .locals 15

    const/high16 v13, -0x80000000

    const/4 v14, 0x0

    const/4 v12, 0x0

    iget-object v2, p0, Lcom/nuance/connect/sqlite/ChinesePredictionDataSource;->log:Lcom/nuance/connect/util/Logger$Log;

    const-string/jumbo v3, "getPredictions"

    invoke-interface {v2, v3}, Lcom/nuance/connect/util/Logger$Log;->d(Ljava/lang/Object;)V

    invoke-static {}, Lcom/nuance/connect/proto/Prediction$LoggingRequestV1;->newBuilder()Lcom/nuance/connect/proto/Prediction$LoggingRequestV1$Builder;

    move-result-object v2

    move-object/from16 v0, p1

    invoke-virtual {v2, v0}, Lcom/nuance/connect/proto/Prediction$LoggingRequestV1$Builder;->setDeviceID(Ljava/lang/String;)Lcom/nuance/connect/proto/Prediction$LoggingRequestV1$Builder;

    move-result-object v11

    :try_start_0
    invoke-static {}, Landroid/os/SystemClock;->uptimeMillis()J

    move-result-wide v2

    const-wide/32 v4, 0x927c0

    add-long/2addr v4, v2

    const/4 v6, 0x3

    new-array v6, v6, [Ljava/lang/String;

    const/4 v7, 0x0

    const-string/jumbo v8, "0"

    aput-object v8, v6, v7

    const/4 v7, 0x1

    invoke-static {v2, v3}, Ljava/lang/String;->valueOf(J)Ljava/lang/String;

    move-result-object v2

    aput-object v2, v6, v7

    const/4 v2, 0x2

    invoke-static {v4, v5}, Ljava/lang/String;->valueOf(J)Ljava/lang/String;

    move-result-object v3

    aput-object v3, v6, v2
    :try_end_0
    .catch Landroid/database/sqlite/SQLiteException; {:try_start_0 .. :try_end_0} :catch_2

    :try_start_1
    iget-object v2, p0, Lcom/nuance/connect/sqlite/ChinesePredictionDataSource;->predictionTable:Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseTable;

    const/4 v3, 0x0

    const/16 v4, 0xc

    new-array v4, v4, [Ljava/lang/String;

    const/4 v5, 0x0

    const-string/jumbo v7, "rowid"

    aput-object v7, v4, v5

    const/4 v5, 0x1

    const-string/jumbo v7, "a"

    aput-object v7, v4, v5

    const/4 v5, 0x2

    const-string/jumbo v7, "i"

    aput-object v7, v4, v5

    const/4 v5, 0x3

    const-string/jumbo v7, "h"

    aput-object v7, v4, v5

    const/4 v5, 0x4

    const-string/jumbo v7, "g"

    aput-object v7, v4, v5

    const/4 v5, 0x5

    const-string/jumbo v7, "b"

    aput-object v7, v4, v5

    const/4 v5, 0x6

    const-string/jumbo v7, "k"

    aput-object v7, v4, v5

    const/4 v5, 0x7

    const-string/jumbo v7, "l"

    aput-object v7, v4, v5

    const/16 v5, 0x8

    const-string/jumbo v7, "c"

    aput-object v7, v4, v5

    const/16 v5, 0x9

    const-string/jumbo v7, "d"

    aput-object v7, v4, v5

    const/16 v5, 0xa

    const-string/jumbo v7, "e"

    aput-object v7, v4, v5

    const/16 v5, 0xb

    const-string/jumbo v7, "f"

    aput-object v7, v4, v5

    new-instance v5, Ljava/lang/StringBuilder;

    const-string/jumbo v7, "("

    invoke-direct {v5, v7}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    iget-object v7, p0, Lcom/nuance/connect/sqlite/ChinesePredictionDataSource;->predictionTable:Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseTable;

    const-string/jumbo v8, "b"

    invoke-interface {v7, v8}, Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseTable;->getColumnName(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v7

    invoke-virtual {v5, v7}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v5

    const-string/jumbo v7, " != ?) OR ("

    invoke-virtual {v5, v7}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v5

    iget-object v7, p0, Lcom/nuance/connect/sqlite/ChinesePredictionDataSource;->predictionTable:Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseTable;

    const-string/jumbo v8, "j"

    invoke-interface {v7, v8}, Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseTable;->getColumnName(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v7

    invoke-virtual {v5, v7}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v5

    const-string/jumbo v7, " < ? OR "

    invoke-virtual {v5, v7}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v5

    iget-object v7, p0, Lcom/nuance/connect/sqlite/ChinesePredictionDataSource;->predictionTable:Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseTable;

    const-string/jumbo v8, "j"

    invoke-interface {v7, v8}, Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseTable;->getColumnName(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v7

    invoke-virtual {v5, v7}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v5

    const-string/jumbo v7, " > ?)"

    invoke-virtual {v5, v7}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v5

    invoke-virtual {v5}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v5

    const/4 v7, 0x0

    const/4 v8, 0x0

    const/4 v9, 0x0

    const/4 v10, 0x0

    invoke-interface/range {v2 .. v10}, Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseTable;->query(Z[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    :try_end_1
    .catchall {:try_start_1 .. :try_end_1} :catchall_1

    move-result-object v3

    if-eqz v3, :cond_0

    :try_start_2
    invoke-interface {v3}, Landroid/database/Cursor;->getCount()I

    move-result v2

    if-gtz v2, :cond_2

    :cond_0
    new-instance v2, Lcom/nuance/connect/sqlite/ChinesePredictionDataSource$ChinesePredictionResultDataReturn;

    const/high16 v4, -0x80000000

    const/4 v5, 0x0

    invoke-direct {v2, v4, v5}, Lcom/nuance/connect/sqlite/ChinesePredictionDataSource$ChinesePredictionResultDataReturn;-><init>(ILcom/nuance/connect/proto/Prediction$LoggingRequestV1;)V
    :try_end_2
    .catchall {:try_start_2 .. :try_end_2} :catchall_2

    if-eqz v3, :cond_1

    :try_start_3
    invoke-interface {v3}, Landroid/database/Cursor;->close()V
    :try_end_3
    .catch Landroid/database/sqlite/SQLiteException; {:try_start_3 .. :try_end_3} :catch_2

    :cond_1
    :goto_0
    return-object v2

    :cond_2
    :try_start_4
    invoke-interface {v3}, Landroid/database/Cursor;->moveToFirst()Z
    :try_end_4
    .catchall {:try_start_4 .. :try_end_4} :catchall_2

    move v4, v13

    move v5, v14

    :cond_3
    :try_start_5
    new-instance v6, Landroid/content/ContentValues;

    invoke-direct {v6}, Landroid/content/ContentValues;-><init>()V

    invoke-static {v3, v6}, Landroid/database/DatabaseUtils;->cursorRowToContentValues(Landroid/database/Cursor;Landroid/content/ContentValues;)V

    iget-object v2, p0, Lcom/nuance/connect/sqlite/ChinesePredictionDataSource;->log:Lcom/nuance/connect/util/Logger$Log;

    const-string/jumbo v7, "getPredictions.map == "

    invoke-virtual {v6}, Landroid/content/ContentValues;->toString()Ljava/lang/String;

    move-result-object v8

    invoke-interface {v2, v7, v8}, Lcom/nuance/connect/util/Logger$Log;->d(Ljava/lang/Object;Ljava/lang/Object;)V

    invoke-static {}, Lcom/nuance/connect/proto/Prediction$PredictionResultV1;->newBuilder()Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;

    move-result-object v2

    const-string/jumbo v7, "c"

    invoke-virtual {v6, v7}, Landroid/content/ContentValues;->getAsString(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v7

    invoke-direct {p0, v7}, Lcom/nuance/connect/sqlite/ChinesePredictionDataSource;->getIntegerArray(Ljava/lang/String;)Ljava/util/ArrayList;

    move-result-object v7

    invoke-virtual {v2, v7}, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->addAllPhrase(Ljava/lang/Iterable;)Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;

    move-result-object v2

    const-string/jumbo v7, "d"

    invoke-virtual {v6, v7}, Landroid/content/ContentValues;->getAsString(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v7

    invoke-direct {p0, v7}, Lcom/nuance/connect/sqlite/ChinesePredictionDataSource;->getIntegerArray(Ljava/lang/String;)Ljava/util/ArrayList;

    move-result-object v7

    invoke-virtual {v2, v7}, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->addAllSpell(Ljava/lang/Iterable;)Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;

    move-result-object v2

    const-string/jumbo v7, "e"

    invoke-virtual {v6, v7}, Landroid/content/ContentValues;->getAsString(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v7

    invoke-direct {p0, v7}, Lcom/nuance/connect/sqlite/ChinesePredictionDataSource;->getIntegerArray(Ljava/lang/String;)Ljava/util/ArrayList;

    move-result-object v7

    invoke-virtual {v2, v7}, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->addAllFullSpell(Ljava/lang/Iterable;)Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;

    move-result-object v2

    const-string/jumbo v7, "f"

    invoke-virtual {v6, v7}, Landroid/content/ContentValues;->getAsString(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v7

    invoke-direct {p0, v7}, Lcom/nuance/connect/sqlite/ChinesePredictionDataSource;->parseStringToIntegerArray(Ljava/lang/String;)Ljava/util/ArrayList;

    move-result-object v7

    invoke-virtual {v2, v7}, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->addAllAttribute(Ljava/lang/Iterable;)Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;

    move-result-object v2

    invoke-virtual {v2}, Lcom/nuance/connect/proto/Prediction$PredictionResultV1$Builder;->build()Lcom/nuance/connect/proto/Prediction$PredictionResultV1;
    :try_end_5
    .catchall {:try_start_5 .. :try_end_5} :catchall_0

    move-result-object v7

    :try_start_6
    iget-object v2, p0, Lcom/nuance/connect/sqlite/ChinesePredictionDataSource;->log:Lcom/nuance/connect/util/Logger$Log;

    const-string/jumbo v8, "prediction transaction id: "

    const-string/jumbo v9, "a"

    invoke-virtual {v6, v9}, Landroid/content/ContentValues;->getAsString(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v9

    invoke-interface {v2, v8, v9}, Lcom/nuance/connect/util/Logger$Log;->d(Ljava/lang/Object;Ljava/lang/Object;)V

    invoke-static {}, Lcom/nuance/connect/proto/Prediction$LoggingTransactionV1;->newBuilder()Lcom/nuance/connect/proto/Prediction$LoggingTransactionV1$Builder;

    move-result-object v2

    const-string/jumbo v8, "a"

    invoke-virtual {v6, v8}, Landroid/content/ContentValues;->getAsString(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v8

    invoke-virtual {v2, v8}, Lcom/nuance/connect/proto/Prediction$LoggingTransactionV1$Builder;->setTransactionID(Ljava/lang/String;)Lcom/nuance/connect/proto/Prediction$LoggingTransactionV1$Builder;

    move-result-object v2

    const-string/jumbo v8, "i"

    invoke-virtual {v6, v8}, Landroid/content/ContentValues;->getAsString(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v8

    invoke-virtual {v2, v8}, Lcom/nuance/connect/proto/Prediction$LoggingTransactionV1$Builder;->setCcpsVersion(Ljava/lang/String;)Lcom/nuance/connect/proto/Prediction$LoggingTransactionV1$Builder;

    move-result-object v2

    const-string/jumbo v8, "h"

    invoke-virtual {v6, v8}, Landroid/content/ContentValues;->getAsInteger(Ljava/lang/String;)Ljava/lang/Integer;

    move-result-object v8

    invoke-virtual {v8}, Ljava/lang/Integer;->intValue()I

    move-result v8

    invoke-virtual {v2, v8}, Lcom/nuance/connect/proto/Prediction$LoggingTransactionV1$Builder;->setTotalTime(I)Lcom/nuance/connect/proto/Prediction$LoggingTransactionV1$Builder;

    move-result-object v2

    const-string/jumbo v8, "g"

    invoke-virtual {v6, v8}, Landroid/content/ContentValues;->getAsInteger(Ljava/lang/String;)Ljava/lang/Integer;

    move-result-object v8

    invoke-virtual {v8}, Ljava/lang/Integer;->intValue()I

    move-result v8

    invoke-virtual {v2, v8}, Lcom/nuance/connect/proto/Prediction$LoggingTransactionV1$Builder;->setCloudTime(I)Lcom/nuance/connect/proto/Prediction$LoggingTransactionV1$Builder;

    move-result-object v2

    const-string/jumbo v8, "b"

    invoke-virtual {v6, v8}, Landroid/content/ContentValues;->getAsInteger(Ljava/lang/String;)Ljava/lang/Integer;

    move-result-object v8

    invoke-virtual {v8}, Ljava/lang/Integer;->intValue()I

    move-result v8

    invoke-virtual {v2, v8}, Lcom/nuance/connect/proto/Prediction$LoggingTransactionV1$Builder;->setResultCode(I)Lcom/nuance/connect/proto/Prediction$LoggingTransactionV1$Builder;

    move-result-object v8

    const-string/jumbo v2, "k"

    invoke-virtual {v6, v2}, Landroid/content/ContentValues;->getAsString(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v2

    if-eqz v2, :cond_5

    const-string/jumbo v2, "k"

    invoke-virtual {v6, v2}, Landroid/content/ContentValues;->getAsString(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v2

    :goto_1
    invoke-virtual {v8, v2}, Lcom/nuance/connect/proto/Prediction$LoggingTransactionV1$Builder;->setApplicationName(Ljava/lang/String;)Lcom/nuance/connect/proto/Prediction$LoggingTransactionV1$Builder;

    move-result-object v8

    const-string/jumbo v2, "l"

    invoke-virtual {v6, v2}, Landroid/content/ContentValues;->getAsString(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v2

    if-eqz v2, :cond_6

    const-string/jumbo v2, "l"

    invoke-virtual {v6, v2}, Landroid/content/ContentValues;->getAsString(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v2

    :goto_2
    invoke-virtual {v8, v2}, Lcom/nuance/connect/proto/Prediction$LoggingTransactionV1$Builder;->setCountryCode(Ljava/lang/String;)Lcom/nuance/connect/proto/Prediction$LoggingTransactionV1$Builder;

    move-result-object v2

    invoke-virtual {v2, v7}, Lcom/nuance/connect/proto/Prediction$LoggingTransactionV1$Builder;->setPredictionResult(Lcom/nuance/connect/proto/Prediction$PredictionResultV1;)Lcom/nuance/connect/proto/Prediction$LoggingTransactionV1$Builder;

    move-result-object v2

    invoke-virtual {v2}, Lcom/nuance/connect/proto/Prediction$LoggingTransactionV1$Builder;->build()Lcom/nuance/connect/proto/Prediction$LoggingTransactionV1;

    move-result-object v7

    iget-object v2, p0, Lcom/nuance/connect/sqlite/ChinesePredictionDataSource;->log:Lcom/nuance/connect/util/Logger$Log;

    invoke-virtual {v7}, Lcom/nuance/connect/proto/Prediction$LoggingTransactionV1;->getTransactionID()Ljava/lang/String;

    move-result-object v8

    invoke-interface {v2, v8}, Lcom/nuance/connect/util/Logger$Log;->d(Ljava/lang/Object;)V

    move-object v0, v11

    check-cast v0, Lcom/nuance/connect/proto/Prediction$LoggingRequestV1$Builder;

    move-object v2, v0

    invoke-virtual {v2, v7}, Lcom/nuance/connect/proto/Prediction$LoggingRequestV1$Builder;->addTransactions(Lcom/nuance/connect/proto/Prediction$LoggingTransactionV1;)Lcom/nuance/connect/proto/Prediction$LoggingRequestV1$Builder;

    const-string/jumbo v2, "rowid"

    invoke-virtual {v6, v2}, Landroid/content/ContentValues;->getAsInteger(Ljava/lang/String;)Ljava/lang/Integer;

    move-result-object v2

    invoke-virtual {v2}, Ljava/lang/Integer;->intValue()I
    :try_end_6
    .catch Ljava/lang/Exception; {:try_start_6 .. :try_end_6} :catch_0
    .catchall {:try_start_6 .. :try_end_6} :catchall_0

    move-result v4

    :goto_3
    add-int/lit8 v5, v5, 0x1

    :try_start_7
    invoke-interface {v3}, Landroid/database/Cursor;->moveToNext()Z
    :try_end_7
    .catchall {:try_start_7 .. :try_end_7} :catchall_0

    move-result v2

    if-nez v2, :cond_3

    if-eqz v3, :cond_4

    :try_start_8
    invoke-interface {v3}, Landroid/database/Cursor;->close()V
    :try_end_8
    .catch Landroid/database/sqlite/SQLiteException; {:try_start_8 .. :try_end_8} :catch_1

    :cond_4
    :goto_4
    if-lez v5, :cond_8

    new-instance v2, Lcom/nuance/connect/sqlite/ChinesePredictionDataSource$ChinesePredictionResultDataReturn;

    check-cast v11, Lcom/nuance/connect/proto/Prediction$LoggingRequestV1$Builder;

    invoke-virtual {v11}, Lcom/nuance/connect/proto/Prediction$LoggingRequestV1$Builder;->build()Lcom/nuance/connect/proto/Prediction$LoggingRequestV1;

    move-result-object v3

    invoke-direct {v2, v4, v3}, Lcom/nuance/connect/sqlite/ChinesePredictionDataSource$ChinesePredictionResultDataReturn;-><init>(ILcom/nuance/connect/proto/Prediction$LoggingRequestV1;)V

    goto/16 :goto_0

    :cond_5
    :try_start_9
    const-string/jumbo v2, "UNKNOWN"

    goto :goto_1

    :cond_6
    const-string/jumbo v2, "UNKNOWN"
    :try_end_9
    .catch Ljava/lang/Exception; {:try_start_9 .. :try_end_9} :catch_0
    .catchall {:try_start_9 .. :try_end_9} :catchall_0

    goto :goto_2

    :catch_0
    move-exception v2

    :try_start_a
    invoke-virtual {v2}, Ljava/lang/Exception;->printStackTrace()V
    :try_end_a
    .catchall {:try_start_a .. :try_end_a} :catchall_0

    goto :goto_3

    :catchall_0
    move-exception v2

    :goto_5
    if-eqz v3, :cond_7

    :try_start_b
    invoke-interface {v3}, Landroid/database/Cursor;->close()V

    :cond_7
    throw v2
    :try_end_b
    .catch Landroid/database/sqlite/SQLiteException; {:try_start_b .. :try_end_b} :catch_1

    :catch_1
    move-exception v2

    move v14, v5

    :goto_6
    iget-object v3, p0, Lcom/nuance/connect/sqlite/ChinesePredictionDataSource;->log:Lcom/nuance/connect/util/Logger$Log;

    const-string/jumbo v5, "getPredictions failed: "

    invoke-virtual {v2}, Landroid/database/sqlite/SQLiteException;->getMessage()Ljava/lang/String;

    move-result-object v2

    invoke-interface {v3, v5, v2}, Lcom/nuance/connect/util/Logger$Log;->e(Ljava/lang/Object;Ljava/lang/Object;)V

    move v5, v14

    goto :goto_4

    :cond_8
    new-instance v2, Lcom/nuance/connect/sqlite/ChinesePredictionDataSource$ChinesePredictionResultDataReturn;

    invoke-direct {v2, v13, v12}, Lcom/nuance/connect/sqlite/ChinesePredictionDataSource$ChinesePredictionResultDataReturn;-><init>(ILcom/nuance/connect/proto/Prediction$LoggingRequestV1;)V

    goto/16 :goto_0

    :catch_2
    move-exception v2

    move v4, v13

    goto :goto_6

    :catchall_1
    move-exception v2

    move-object v3, v12

    move v4, v13

    move v5, v14

    goto :goto_5

    :catchall_2
    move-exception v2

    move v4, v13

    move v5, v14

    goto :goto_5
.end method

.method public insertPrediction(Ljava/lang/String;Ljava/lang/String;IJJLjava/lang/String;Ljava/lang/String;)Z
    .locals 12

    new-instance v1, Lcom/nuance/connect/sqlite/ChinesePredictionDataRow;

    move-object v2, p1

    move-object v3, p2

    move-wide/from16 v4, p4

    move-wide/from16 v6, p6

    move v8, p3

    move-object/from16 v9, p8

    move-object/from16 v10, p9

    invoke-direct/range {v1 .. v10}, Lcom/nuance/connect/sqlite/ChinesePredictionDataRow;-><init>(Ljava/lang/String;Ljava/lang/String;JJILjava/lang/String;Ljava/lang/String;)V

    iget-object v0, p0, Lcom/nuance/connect/sqlite/ChinesePredictionDataSource;->handler:Lcom/nuance/connect/sqlite/PredictionDatabaseHandlerThread;

    invoke-virtual {v0, v1}, Lcom/nuance/connect/sqlite/PredictionDatabaseHandlerThread;->addPrediction(Ljava/lang/Object;)V

    const/4 v0, 0x1

    return v0
.end method

.method insertPredictionObject(Lcom/nuance/connect/sqlite/ChinesePredictionDataRow;)Z
    .locals 6

    const/4 v0, 0x0

    iget-object v1, p0, Lcom/nuance/connect/sqlite/ChinesePredictionDataSource;->log:Lcom/nuance/connect/util/Logger$Log;

    const-string/jumbo v2, "insertPredictionObject row="

    invoke-virtual {p1}, Lcom/nuance/connect/sqlite/ChinesePredictionDataRow;->toString()Ljava/lang/String;

    move-result-object v3

    invoke-interface {v1, v2, v3}, Lcom/nuance/connect/util/Logger$Log;->d(Ljava/lang/Object;Ljava/lang/Object;)V

    iget-object v1, p0, Lcom/nuance/connect/sqlite/ChinesePredictionDataSource;->predictionTable:Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseTable;

    invoke-interface {v1}, Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseTable;->isFull()Z

    move-result v1

    if-eqz v1, :cond_0

    :goto_0
    return v0

    :cond_0
    :try_start_0
    iget-object v1, p0, Lcom/nuance/connect/sqlite/ChinesePredictionDataSource;->predictionTable:Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseTable;

    invoke-interface {v1}, Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseTable;->beginTransaction()V

    new-instance v1, Landroid/content/ContentValues;

    invoke-direct {v1}, Landroid/content/ContentValues;-><init>()V

    const-string/jumbo v2, "a"

    iget-object v3, p1, Lcom/nuance/connect/sqlite/ChinesePredictionDataRow;->predictionId:Ljava/lang/String;

    invoke-virtual {v1, v2, v3}, Landroid/content/ContentValues;->put(Ljava/lang/String;Ljava/lang/String;)V

    const-string/jumbo v2, "i"

    iget-object v3, p1, Lcom/nuance/connect/sqlite/ChinesePredictionDataRow;->ccpsVersion:Ljava/lang/String;

    invoke-virtual {v1, v2, v3}, Landroid/content/ContentValues;->put(Ljava/lang/String;Ljava/lang/String;)V

    const-string/jumbo v2, "b"

    iget v3, p1, Lcom/nuance/connect/sqlite/ChinesePredictionDataRow;->resultType:I

    invoke-static {v3}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v3

    invoke-virtual {v1, v2, v3}, Landroid/content/ContentValues;->put(Ljava/lang/String;Ljava/lang/Integer;)V

    const-string/jumbo v2, "g"

    iget-wide v4, p1, Lcom/nuance/connect/sqlite/ChinesePredictionDataRow;->cloudTime:J

    invoke-static {v4, v5}, Ljava/lang/Long;->valueOf(J)Ljava/lang/Long;

    move-result-object v3

    invoke-virtual {v1, v2, v3}, Landroid/content/ContentValues;->put(Ljava/lang/String;Ljava/lang/Long;)V

    const-string/jumbo v2, "h"

    iget-wide v4, p1, Lcom/nuance/connect/sqlite/ChinesePredictionDataRow;->totalTime:J

    invoke-static {v4, v5}, Ljava/lang/Long;->valueOf(J)Ljava/lang/Long;

    move-result-object v3

    invoke-virtual {v1, v2, v3}, Landroid/content/ContentValues;->put(Ljava/lang/String;Ljava/lang/Long;)V

    const-string/jumbo v2, "j"

    invoke-static {}, Landroid/os/SystemClock;->uptimeMillis()J

    move-result-wide v4

    invoke-static {v4, v5}, Ljava/lang/Long;->valueOf(J)Ljava/lang/Long;

    move-result-object v3

    invoke-virtual {v1, v2, v3}, Landroid/content/ContentValues;->put(Ljava/lang/String;Ljava/lang/Long;)V

    const-string/jumbo v2, "k"

    iget-object v3, p1, Lcom/nuance/connect/sqlite/ChinesePredictionDataRow;->applicationName:Ljava/lang/String;

    invoke-virtual {v1, v2, v3}, Landroid/content/ContentValues;->put(Ljava/lang/String;Ljava/lang/String;)V

    const-string/jumbo v2, "l"

    iget-object v3, p1, Lcom/nuance/connect/sqlite/ChinesePredictionDataRow;->countryCode:Ljava/lang/String;

    invoke-virtual {v1, v2, v3}, Landroid/content/ContentValues;->put(Ljava/lang/String;Ljava/lang/String;)V

    iget-object v2, p0, Lcom/nuance/connect/sqlite/ChinesePredictionDataSource;->predictionTable:Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseTable;

    const/4 v3, 0x0

    invoke-interface {v2, v3, v1}, Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseTable;->insert(Ljava/lang/String;Landroid/content/ContentValues;)J

    move-result-wide v2

    const-wide/16 v4, 0x0

    cmp-long v1, v2, v4

    if-gez v1, :cond_1

    iget-object v1, p0, Lcom/nuance/connect/sqlite/ChinesePredictionDataSource;->log:Lcom/nuance/connect/util/Logger$Log;

    const-string/jumbo v2, "insertPredictionObject failed to insert event: "

    invoke-virtual {p1}, Lcom/nuance/connect/sqlite/ChinesePredictionDataRow;->toString()Ljava/lang/String;

    move-result-object v3

    invoke-interface {v1, v2, v3}, Lcom/nuance/connect/util/Logger$Log;->e(Ljava/lang/Object;Ljava/lang/Object;)V
    :try_end_0
    .catch Landroid/database/SQLException; {:try_start_0 .. :try_end_0} :catch_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    :goto_1
    iget-object v1, p0, Lcom/nuance/connect/sqlite/ChinesePredictionDataSource;->predictionTable:Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseTable;

    :goto_2
    invoke-interface {v1}, Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseTable;->endTransaction()V

    goto :goto_0

    :cond_1
    :try_start_1
    iget-object v1, p0, Lcom/nuance/connect/sqlite/ChinesePredictionDataSource;->predictionTable:Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseTable;

    invoke-interface {v1}, Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseTable;->setTransactionSuccessful()V
    :try_end_1
    .catch Landroid/database/SQLException; {:try_start_1 .. :try_end_1} :catch_0
    .catchall {:try_start_1 .. :try_end_1} :catchall_0

    const/4 v0, 0x1

    goto :goto_1

    :catch_0
    move-exception v1

    :try_start_2
    iget-object v2, p0, Lcom/nuance/connect/sqlite/ChinesePredictionDataSource;->log:Lcom/nuance/connect/util/Logger$Log;

    const-string/jumbo v3, "insertPredictionObject failed to insert prediction: ("

    invoke-virtual {p1}, Lcom/nuance/connect/sqlite/ChinesePredictionDataRow;->toString()Ljava/lang/String;

    move-result-object v4

    const-string/jumbo v5, ") SQL message"

    invoke-virtual {v1}, Landroid/database/SQLException;->getMessage()Ljava/lang/String;

    move-result-object v1

    invoke-interface {v2, v3, v4, v5, v1}, Lcom/nuance/connect/util/Logger$Log;->e(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V
    :try_end_2
    .catchall {:try_start_2 .. :try_end_2} :catchall_0

    iget-object v1, p0, Lcom/nuance/connect/sqlite/ChinesePredictionDataSource;->predictionTable:Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseTable;

    goto :goto_2

    :catchall_0
    move-exception v0

    iget-object v1, p0, Lcom/nuance/connect/sqlite/ChinesePredictionDataSource;->predictionTable:Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseTable;

    invoke-interface {v1}, Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseTable;->endTransaction()V

    throw v0
.end method

.method insertPredictionResultObject(Lcom/nuance/connect/sqlite/ChinesePredictionDataResultRow;)Z
    .locals 6

    const/4 v1, 0x1

    const/4 v0, 0x0

    iget-object v2, p0, Lcom/nuance/connect/sqlite/ChinesePredictionDataSource;->log:Lcom/nuance/connect/util/Logger$Log;

    const-string/jumbo v3, "insertPredictionResultObject row="

    invoke-virtual {p1}, Lcom/nuance/connect/sqlite/ChinesePredictionDataResultRow;->toString()Ljava/lang/String;

    move-result-object v4

    invoke-interface {v2, v3, v4}, Lcom/nuance/connect/util/Logger$Log;->d(Ljava/lang/Object;Ljava/lang/Object;)V

    iget-object v2, p0, Lcom/nuance/connect/sqlite/ChinesePredictionDataSource;->predictionTable:Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseTable;

    invoke-interface {v2}, Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseTable;->isFull()Z

    move-result v2

    if-eqz v2, :cond_0

    :goto_0
    return v0

    :cond_0
    :try_start_0
    iget-object v2, p0, Lcom/nuance/connect/sqlite/ChinesePredictionDataSource;->predictionTable:Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseTable;

    invoke-interface {v2}, Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseTable;->beginTransaction()V

    new-instance v3, Landroid/content/ContentValues;

    invoke-direct {v3}, Landroid/content/ContentValues;-><init>()V

    const-string/jumbo v2, "b"

    iget v4, p1, Lcom/nuance/connect/sqlite/ChinesePredictionDataResultRow;->resultType:I

    invoke-static {v4}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v4

    invoke-virtual {v3, v2, v4}, Landroid/content/ContentValues;->put(Ljava/lang/String;Ljava/lang/Integer;)V

    const-string/jumbo v2, "c"

    iget-object v4, p1, Lcom/nuance/connect/sqlite/ChinesePredictionDataResultRow;->phrase:Ljava/lang/String;

    invoke-virtual {v3, v2, v4}, Landroid/content/ContentValues;->put(Ljava/lang/String;Ljava/lang/String;)V

    const-string/jumbo v2, "d"

    iget-object v4, p1, Lcom/nuance/connect/sqlite/ChinesePredictionDataResultRow;->spell:Ljava/lang/String;

    invoke-virtual {v3, v2, v4}, Landroid/content/ContentValues;->put(Ljava/lang/String;Ljava/lang/String;)V

    const-string/jumbo v2, "e"

    iget-object v4, p1, Lcom/nuance/connect/sqlite/ChinesePredictionDataResultRow;->fullSpell:Ljava/lang/String;

    invoke-virtual {v3, v2, v4}, Landroid/content/ContentValues;->put(Ljava/lang/String;Ljava/lang/String;)V

    const-string/jumbo v2, ""

    iget-object v4, p1, Lcom/nuance/connect/sqlite/ChinesePredictionDataResultRow;->attribute:[I

    if-eqz v4, :cond_1

    iget-object v4, p1, Lcom/nuance/connect/sqlite/ChinesePredictionDataResultRow;->attribute:[I

    array-length v4, v4

    if-lez v4, :cond_1

    iget-object v2, p1, Lcom/nuance/connect/sqlite/ChinesePredictionDataResultRow;->attribute:[I

    const-string/jumbo v4, ","

    invoke-static {v2, v4}, Lcom/nuance/connect/util/StringUtils;->implode([ILjava/lang/String;)Ljava/lang/String;

    move-result-object v2

    :cond_1
    const-string/jumbo v4, "f"

    invoke-virtual {v3, v4, v2}, Landroid/content/ContentValues;->put(Ljava/lang/String;Ljava/lang/String;)V

    const/4 v2, 0x1

    new-array v2, v2, [Ljava/lang/String;

    const/4 v4, 0x0

    iget-object v5, p1, Lcom/nuance/connect/sqlite/ChinesePredictionDataResultRow;->predictionId:Ljava/lang/String;

    aput-object v5, v2, v4

    iget-object v4, p0, Lcom/nuance/connect/sqlite/ChinesePredictionDataSource;->predictionTable:Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseTable;

    const-string/jumbo v5, "a = ?"

    invoke-interface {v4, v3, v5, v2}, Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseTable;->update(Landroid/content/ContentValues;Ljava/lang/String;[Ljava/lang/String;)I

    move-result v2

    if-nez v2, :cond_2

    iget-object v1, p0, Lcom/nuance/connect/sqlite/ChinesePredictionDataSource;->log:Lcom/nuance/connect/util/Logger$Log;

    const-string/jumbo v2, "insertPredictionResultObject failed to create result for prediction: "

    iget-object v3, p1, Lcom/nuance/connect/sqlite/ChinesePredictionDataResultRow;->predictionId:Ljava/lang/String;

    invoke-interface {v1, v2, v3}, Lcom/nuance/connect/util/Logger$Log;->e(Ljava/lang/Object;Ljava/lang/Object;)V
    :try_end_0
    .catch Landroid/database/SQLException; {:try_start_0 .. :try_end_0} :catch_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    :goto_1
    iget-object v1, p0, Lcom/nuance/connect/sqlite/ChinesePredictionDataSource;->predictionTable:Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseTable;

    :goto_2
    invoke-interface {v1}, Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseTable;->endTransaction()V

    goto :goto_0

    :cond_2
    if-le v2, v1, :cond_3

    :try_start_1
    iget-object v1, p0, Lcom/nuance/connect/sqlite/ChinesePredictionDataSource;->log:Lcom/nuance/connect/util/Logger$Log;

    const-string/jumbo v2, "insertPredictionResultObject failed to properly create result for prediction: "

    iget-object v3, p1, Lcom/nuance/connect/sqlite/ChinesePredictionDataResultRow;->predictionId:Ljava/lang/String;

    invoke-interface {v1, v2, v3}, Lcom/nuance/connect/util/Logger$Log;->e(Ljava/lang/Object;Ljava/lang/Object;)V
    :try_end_1
    .catch Landroid/database/SQLException; {:try_start_1 .. :try_end_1} :catch_0
    .catchall {:try_start_1 .. :try_end_1} :catchall_0

    goto :goto_1

    :catch_0
    move-exception v1

    :try_start_2
    iget-object v2, p0, Lcom/nuance/connect/sqlite/ChinesePredictionDataSource;->log:Lcom/nuance/connect/util/Logger$Log;

    const-string/jumbo v3, "insertPredictionResultObject failed to create result for prediction: "

    iget-object v4, p1, Lcom/nuance/connect/sqlite/ChinesePredictionDataResultRow;->predictionId:Ljava/lang/String;

    const-string/jumbo v5, " SQL message"

    invoke-virtual {v1}, Landroid/database/SQLException;->getMessage()Ljava/lang/String;

    move-result-object v1

    invoke-interface {v2, v3, v4, v5, v1}, Lcom/nuance/connect/util/Logger$Log;->e(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V
    :try_end_2
    .catchall {:try_start_2 .. :try_end_2} :catchall_0

    iget-object v1, p0, Lcom/nuance/connect/sqlite/ChinesePredictionDataSource;->predictionTable:Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseTable;

    goto :goto_2

    :cond_3
    :try_start_3
    iget-object v2, p0, Lcom/nuance/connect/sqlite/ChinesePredictionDataSource;->predictionTable:Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseTable;

    invoke-interface {v2}, Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseTable;->setTransactionSuccessful()V
    :try_end_3
    .catch Landroid/database/SQLException; {:try_start_3 .. :try_end_3} :catch_0
    .catchall {:try_start_3 .. :try_end_3} :catchall_0

    move v0, v1

    goto :goto_1

    :catchall_0
    move-exception v0

    iget-object v1, p0, Lcom/nuance/connect/sqlite/ChinesePredictionDataSource;->predictionTable:Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseTable;

    invoke-interface {v1}, Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseTable;->endTransaction()V

    throw v0
.end method

.method public isFull()Z
    .locals 1

    iget-object v0, p0, Lcom/nuance/connect/sqlite/ChinesePredictionDataSource;->predictionTable:Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseTable;

    invoke-interface {v0}, Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseTable;->isFull()Z

    move-result v0

    return v0
.end method

.method public logPredictionResult(Ljava/lang/String;ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;[I)Z
    .locals 7

    new-instance v0, Lcom/nuance/connect/sqlite/ChinesePredictionDataResultRow;

    move-object v1, p1

    move v2, p2

    move-object v3, p3

    move-object v4, p4

    move-object v5, p5

    move-object v6, p6

    invoke-direct/range {v0 .. v6}, Lcom/nuance/connect/sqlite/ChinesePredictionDataResultRow;-><init>(Ljava/lang/String;ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;[I)V

    iget-object v1, p0, Lcom/nuance/connect/sqlite/ChinesePredictionDataSource;->handler:Lcom/nuance/connect/sqlite/PredictionDatabaseHandlerThread;

    invoke-virtual {v1, v0}, Lcom/nuance/connect/sqlite/PredictionDatabaseHandlerThread;->addPrediction(Ljava/lang/Object;)V

    const/4 v0, 0x1

    return v0
.end method

.method public reset()V
    .locals 2

    iget-object v0, p0, Lcom/nuance/connect/sqlite/ChinesePredictionDataSource;->log:Lcom/nuance/connect/util/Logger$Log;

    const-string/jumbo v1, "ChinesePredictionDataSource.reset()"

    invoke-interface {v0, v1}, Lcom/nuance/connect/util/Logger$Log;->v(Ljava/lang/Object;)V

    iget-object v0, p0, Lcom/nuance/connect/sqlite/ChinesePredictionDataSource;->predictionTable:Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseTable;

    invoke-interface {v0}, Lcom/nuance/connect/sqlite/MasterDatabase$DatabaseTable;->deleteAll()Z

    return-void
.end method
