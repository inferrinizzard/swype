package com.nuance.connect.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;
import com.nuance.connect.comm.MessageAPI;
import com.nuance.connect.common.Integers;
import com.nuance.connect.common.Strings;
import com.nuance.connect.sqlite.MasterDatabase;
import com.nuance.connect.util.Logger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/* loaded from: classes.dex */
public class CategoryDatabase {
    public static final String CATEGORY_COUNT_OLD = "CATEGORY_COUNT_OLD";
    private static final String CATEGORY_NAME = "ccc";
    public static final String CHECKSUM = "CHECKSUM";
    private static final int DB_VERSION = 1;
    public static final String DELETE_CATEGORY = "DELETE_CATEGORY";
    public static final String FIRST_TIME_DOWNLOADED = "FIRST_TIME_DOWNLOADED";
    public static final String LAST_UPDATE_AVAILABLE = "LAST_UPDATE_AVAILABLE";
    public static final String LAST_UPDATE_FETCHED = "LAST_UPDATE_FETCHED";
    public static final String LAST_USED_AT = "LAST_USED_AT";
    public static final String LL_DELETE_CLEAR = "LL_DELETE_CLEAR";
    public static final String SUBSCRIBED = "SUBSCRIBED";
    public static final int TYPE_CATALOG = 6;
    public static final int TYPE_CHINESE_ADDON_DICTIONARY = 2;
    public static final int TYPE_CUSTOM_CONFIGURATION = 7;
    public static final int TYPE_KEYBOARD_LANGUAGE_ONLY = 1;
    public static final int TYPE_KEYBOARD_PLUS_LANGUAGE_VARIANT = 3;
    public static final int TYPE_UPDATES = 5;
    public static final String UNSUBSCRIBE_PENDING = "UNSUBSCRIBE_PENDING";
    public static final String URL = "URL";
    public static final String USER_INITIATED = "USER_INITIATED";
    private static final CatalogCategory catalogCategory;
    private static final ChineseAddonCategory chineseAddonCategory;
    private static final CustomConfigCategory customConfigCategory;
    private static CategoryDatabase instance;
    private static final LivingLanguageCategory livingLanguageCategory;
    private static final LivingLanguageWithVariantCategory livingLanguageWithVariantCategory;
    private static final Logger.Log log = Logger.getLog(Logger.LoggerType.DEVELOPER, CategoryDatabase.class.getSimpleName());
    private static final PlatformUpdateCategory platformUpdateCategory;
    private static CategoryDatabaseSchema schemaInstance;
    private final MasterDatabase.DatabaseTable catalogTable;
    private final MasterDatabase.DatabaseTable chineseAddonTable;
    private final MasterDatabase.Codec codec;
    private final MasterDatabase.DatabaseTable customConfigTable;
    private final MasterDatabase database;
    private final MasterDatabase.DatabaseTable livingLangTable;
    private final MasterDatabase.DatabaseTable livingLangWithVarTable;
    private final MasterDatabase.DatabaseTable platformUpdateTable;
    private final Set<String> types;
    private final Map<String, MasterDatabase.DatabaseTable> tableMap = new HashMap();
    private final Map<String, CommonCategory> schemaMap = new HashMap();

    /* loaded from: classes.dex */
    public static class CatalogCategory extends CommonCategory {
        private final Map<String, MasterDatabase.ElementType> tableProperties;

        private CatalogCategory() {
            this.tableProperties = super.getTableProperties();
            this.tableProperties.put(MessageAPI.NAME, MasterDatabase.ElementType.STRING);
            this.tableProperties.put(MessageAPI.LOCALE, MasterDatabase.ElementType.STRING);
            this.tableProperties.put(MessageAPI.TITLE, MasterDatabase.ElementType.STRING);
            this.tableProperties.put(MessageAPI.DESCRIPTION, MasterDatabase.ElementType.STRING);
            this.tableProperties.put(MessageAPI.SHORT_DESCRIPTION, MasterDatabase.ElementType.STRING);
            this.tableProperties.put(MessageAPI.THUMBNAIL_URL, MasterDatabase.ElementType.STRING);
            this.tableProperties.put(MessageAPI.PURCHASABLE, MasterDatabase.ElementType.STRING);
            this.tableProperties.put(MessageAPI.CREATION_TIMESTAMP, MasterDatabase.ElementType.STRING);
            this.tableProperties.put(MessageAPI.START, MasterDatabase.ElementType.STRING);
            this.tableProperties.put(MessageAPI.END, MasterDatabase.ElementType.STRING);
            this.tableProperties.put(MessageAPI.FULFILL_UNTIL, MasterDatabase.ElementType.STRING);
            this.tableProperties.put(MessageAPI.SKU, MasterDatabase.ElementType.STRING);
            this.tableProperties.put(MessageAPI.BUNDLED_THEMES, MasterDatabase.ElementType.STRING);
            this.tableProperties.put(MessageAPI.LABELS, MasterDatabase.ElementType.STRING);
            this.tableProperties.put(MessageAPI.MEDIA_URLS, MasterDatabase.ElementType.STRING);
            this.tableProperties.put(MessageAPI.COUNTRIES_INCLUDED, MasterDatabase.ElementType.STRING);
            this.tableProperties.put(MessageAPI.COUNTRIES_EXCLUDED, MasterDatabase.ElementType.STRING);
            this.tableProperties.put(Strings.PROP_TYPE, MasterDatabase.ElementType.STRING);
            this.tableProperties.put(Strings.PROP_BUNDLED_THEMES_SKUS, MasterDatabase.ElementType.STRING);
            this.tableProperties.put(Strings.PROP_BUNDLED_THEMES_CDB, MasterDatabase.ElementType.STRING);
        }

        @Override // com.nuance.connect.sqlite.CategoryDatabase.CommonCategory
        public int getCategoryType() {
            return 6;
        }

        @Override // com.nuance.connect.sqlite.MasterDatabase.TableSchema
        public String getTableName() {
            return "category_cat";
        }

        @Override // com.nuance.connect.sqlite.CategoryDatabase.CommonCategory, com.nuance.connect.sqlite.MasterDatabase.TableSchema
        public Map<String, MasterDatabase.ElementType> getTableProperties() {
            return this.tableProperties;
        }
    }

    /* loaded from: classes.dex */
    private static class CategoryDatabaseSchema implements MasterDatabase.DatabaseSchema {
        private final Context context;

        private CategoryDatabaseSchema(Context context) {
            this.context = context.getApplicationContext();
        }

        public static synchronized CategoryDatabaseSchema from(Context context) {
            CategoryDatabaseSchema categoryDatabaseSchema;
            synchronized (CategoryDatabaseSchema.class) {
                if (CategoryDatabase.schemaInstance == null) {
                    CategoryDatabaseSchema unused = CategoryDatabase.schemaInstance = new CategoryDatabaseSchema(context);
                }
                categoryDatabaseSchema = CategoryDatabase.schemaInstance;
            }
            return categoryDatabaseSchema;
        }

        @Override // com.nuance.connect.sqlite.MasterDatabase.DatabaseSchema
        public Set<String> doNotEncrypt() {
            HashSet hashSet = new HashSet();
            hashSet.add(CategoryDatabase.CATEGORY_NAME);
            return hashSet;
        }

        @Override // com.nuance.connect.sqlite.MasterDatabase.DatabaseSchema
        public String getName() {
            return "CategoryDb";
        }

        @Override // com.nuance.connect.sqlite.MasterDatabase.DatabaseSchema
        public Set<MasterDatabase.TableSchema> getTableSchemas() {
            HashSet hashSet = new HashSet();
            hashSet.add(CategoryDatabase.catalogCategory);
            hashSet.add(CategoryDatabase.chineseAddonCategory);
            hashSet.add(CategoryDatabase.livingLanguageCategory);
            hashSet.add(CategoryDatabase.livingLanguageWithVariantCategory);
            hashSet.add(CategoryDatabase.platformUpdateCategory);
            hashSet.add(CategoryDatabase.customConfigCategory);
            return hashSet;
        }

        @Override // com.nuance.connect.sqlite.MasterDatabase.DatabaseSchema
        public void onMigration() {
        }

        @Override // com.nuance.connect.sqlite.MasterDatabase.DatabaseSchema
        public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
        }
    }

    /* loaded from: classes.dex */
    public static class ChineseAddonCategory extends CommonCategory {
        private final Map<String, MasterDatabase.ElementType> tableProperties;

        private ChineseAddonCategory() {
            this.tableProperties = super.getTableProperties();
            this.tableProperties.put(MessageAPI.NAME, MasterDatabase.ElementType.STRING);
            this.tableProperties.put(MessageAPI.LANGUAGE_ID, MasterDatabase.ElementType.STRING);
            this.tableProperties.put(MessageAPI.LOCALE, MasterDatabase.ElementType.STRING);
            this.tableProperties.put(MessageAPI.DESCRIPTION, MasterDatabase.ElementType.STRING);
            this.tableProperties.put(MessageAPI.RANK, MasterDatabase.ElementType.STRING);
            this.tableProperties.put(MessageAPI.NAME_TRANSLATED, MasterDatabase.ElementType.STRING);
            this.tableProperties.put(MessageAPI.DESCRIPTION_TRANSLATED, MasterDatabase.ElementType.STRING);
        }

        @Override // com.nuance.connect.sqlite.CategoryDatabase.CommonCategory
        public int getCategoryType() {
            return 2;
        }

        @Override // com.nuance.connect.sqlite.MasterDatabase.TableSchema
        public String getTableName() {
            return "category_chaddon";
        }

        @Override // com.nuance.connect.sqlite.CategoryDatabase.CommonCategory, com.nuance.connect.sqlite.MasterDatabase.TableSchema
        public Map<String, MasterDatabase.ElementType> getTableProperties() {
            return this.tableProperties;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class CommonCategory implements MasterDatabase.TableSchema {
        public abstract int getCategoryType();

        @Override // com.nuance.connect.sqlite.MasterDatabase.TableSchema
        public final String getPrimaryKey() {
            return CategoryDatabase.CATEGORY_NAME;
        }

        @Override // com.nuance.connect.sqlite.MasterDatabase.TableSchema
        public Map<String, MasterDatabase.ElementType> getTableProperties() {
            HashMap hashMap = new HashMap();
            hashMap.put(CategoryDatabase.CATEGORY_NAME, MasterDatabase.ElementType.STRING);
            hashMap.put(MessageAPI.CATEGORY_ID, MasterDatabase.ElementType.STRING);
            hashMap.put(CategoryDatabase.SUBSCRIBED, MasterDatabase.ElementType.STRING);
            hashMap.put(CategoryDatabase.UNSUBSCRIBE_PENDING, MasterDatabase.ElementType.STRING);
            hashMap.put(CategoryDatabase.USER_INITIATED, MasterDatabase.ElementType.STRING);
            hashMap.put(Strings.MAP_KEY_STEP, MasterDatabase.ElementType.STRING);
            hashMap.put("URL", MasterDatabase.ElementType.STRING);
            hashMap.put("CHECKSUM", MasterDatabase.ElementType.STRING);
            hashMap.put(Strings.MAP_KEY_FILE_LOCATION, MasterDatabase.ElementType.STRING);
            hashMap.put(CategoryDatabase.LAST_UPDATE_FETCHED, MasterDatabase.ElementType.STRING);
            hashMap.put(CategoryDatabase.LAST_UPDATE_AVAILABLE, MasterDatabase.ElementType.STRING);
            return hashMap;
        }
    }

    /* loaded from: classes.dex */
    public static class CustomConfigCategory extends CommonCategory {
        private final Map<String, MasterDatabase.ElementType> tableProperties;

        private CustomConfigCategory() {
            this.tableProperties = super.getTableProperties();
        }

        @Override // com.nuance.connect.sqlite.CategoryDatabase.CommonCategory
        public int getCategoryType() {
            return 7;
        }

        @Override // com.nuance.connect.sqlite.MasterDatabase.TableSchema
        public String getTableName() {
            return "category_cconf";
        }

        @Override // com.nuance.connect.sqlite.CategoryDatabase.CommonCategory, com.nuance.connect.sqlite.MasterDatabase.TableSchema
        public Map<String, MasterDatabase.ElementType> getTableProperties() {
            return this.tableProperties;
        }
    }

    /* loaded from: classes.dex */
    public static class LivingLanguageCategory extends CommonCategory {
        private final Map<String, MasterDatabase.ElementType> tableProperties;

        private LivingLanguageCategory() {
            this.tableProperties = super.getTableProperties();
            this.tableProperties.put(MessageAPI.LANGUAGE_ID, MasterDatabase.ElementType.STRING);
            this.tableProperties.put(CategoryDatabase.LL_DELETE_CLEAR, MasterDatabase.ElementType.STRING);
            this.tableProperties.put(CategoryDatabase.DELETE_CATEGORY, MasterDatabase.ElementType.STRING);
            this.tableProperties.put(CategoryDatabase.CATEGORY_COUNT_OLD, MasterDatabase.ElementType.STRING);
            this.tableProperties.put(CategoryDatabase.LAST_USED_AT, MasterDatabase.ElementType.STRING);
            this.tableProperties.put(Strings.CATEGORY_COUNT, MasterDatabase.ElementType.STRING);
            this.tableProperties.put(CategoryDatabase.FIRST_TIME_DOWNLOADED, MasterDatabase.ElementType.STRING);
        }

        @Override // com.nuance.connect.sqlite.CategoryDatabase.CommonCategory
        public int getCategoryType() {
            return 1;
        }

        @Override // com.nuance.connect.sqlite.MasterDatabase.TableSchema
        public String getTableName() {
            return "category_ll";
        }

        @Override // com.nuance.connect.sqlite.CategoryDatabase.CommonCategory, com.nuance.connect.sqlite.MasterDatabase.TableSchema
        public Map<String, MasterDatabase.ElementType> getTableProperties() {
            return this.tableProperties;
        }
    }

    /* loaded from: classes.dex */
    public static class LivingLanguageWithVariantCategory extends CommonCategory {
        private final Map<String, MasterDatabase.ElementType> tableProperties;

        private LivingLanguageWithVariantCategory() {
            this.tableProperties = super.getTableProperties();
            this.tableProperties.put(MessageAPI.LANGUAGE_ID, MasterDatabase.ElementType.STRING);
            this.tableProperties.put(MessageAPI.LOCALE, MasterDatabase.ElementType.STRING);
            this.tableProperties.put(MessageAPI.COUNTRY_LIST, MasterDatabase.ElementType.STRING);
            this.tableProperties.put(CategoryDatabase.LL_DELETE_CLEAR, MasterDatabase.ElementType.STRING);
            this.tableProperties.put(CategoryDatabase.DELETE_CATEGORY, MasterDatabase.ElementType.STRING);
            this.tableProperties.put(CategoryDatabase.CATEGORY_COUNT_OLD, MasterDatabase.ElementType.STRING);
            this.tableProperties.put(CategoryDatabase.LAST_USED_AT, MasterDatabase.ElementType.STRING);
            this.tableProperties.put(Strings.CATEGORY_COUNT, MasterDatabase.ElementType.STRING);
            this.tableProperties.put(CategoryDatabase.FIRST_TIME_DOWNLOADED, MasterDatabase.ElementType.STRING);
        }

        @Override // com.nuance.connect.sqlite.CategoryDatabase.CommonCategory
        public int getCategoryType() {
            return 3;
        }

        @Override // com.nuance.connect.sqlite.MasterDatabase.TableSchema
        public String getTableName() {
            return "category_llwvar";
        }

        @Override // com.nuance.connect.sqlite.CategoryDatabase.CommonCategory, com.nuance.connect.sqlite.MasterDatabase.TableSchema
        public Map<String, MasterDatabase.ElementType> getTableProperties() {
            return this.tableProperties;
        }
    }

    /* loaded from: classes.dex */
    public static class PlatformUpdateCategory extends CommonCategory {
        private final Map<String, MasterDatabase.ElementType> tableProperties;

        private PlatformUpdateCategory() {
            this.tableProperties = super.getTableProperties();
        }

        @Override // com.nuance.connect.sqlite.CategoryDatabase.CommonCategory
        public int getCategoryType() {
            return 5;
        }

        @Override // com.nuance.connect.sqlite.MasterDatabase.TableSchema
        public String getTableName() {
            return "category_up";
        }

        @Override // com.nuance.connect.sqlite.CategoryDatabase.CommonCategory, com.nuance.connect.sqlite.MasterDatabase.TableSchema
        public Map<String, MasterDatabase.ElementType> getTableProperties() {
            return this.tableProperties;
        }
    }

    static {
        catalogCategory = new CatalogCategory();
        chineseAddonCategory = new ChineseAddonCategory();
        livingLanguageCategory = new LivingLanguageCategory();
        livingLanguageWithVariantCategory = new LivingLanguageWithVariantCategory();
        platformUpdateCategory = new PlatformUpdateCategory();
        customConfigCategory = new CustomConfigCategory();
    }

    private CategoryDatabase(Context context) {
        this.database = MasterDatabase.from(context);
        this.database.connectDatabase(CategoryDatabaseSchema.from(context));
        this.catalogTable = this.database.getTableDatabase(catalogCategory);
        this.chineseAddonTable = this.database.getTableDatabase(chineseAddonCategory);
        this.livingLangTable = this.database.getTableDatabase(livingLanguageCategory);
        this.livingLangWithVarTable = this.database.getTableDatabase(livingLanguageWithVariantCategory);
        this.platformUpdateTable = this.database.getTableDatabase(platformUpdateCategory);
        this.customConfigTable = this.database.getTableDatabase(customConfigCategory);
        this.tableMap.put(this.catalogTable.getName(), this.catalogTable);
        this.tableMap.put(this.chineseAddonTable.getName(), this.chineseAddonTable);
        this.tableMap.put(this.livingLangTable.getName(), this.livingLangTable);
        this.tableMap.put(this.livingLangWithVarTable.getName(), this.livingLangWithVarTable);
        this.tableMap.put(this.platformUpdateTable.getName(), this.platformUpdateTable);
        this.tableMap.put(this.customConfigTable.getName(), this.customConfigTable);
        this.schemaMap.put(this.catalogTable.getName(), catalogCategory);
        this.schemaMap.put(this.chineseAddonTable.getName(), chineseAddonCategory);
        this.schemaMap.put(this.livingLangTable.getName(), livingLanguageCategory);
        this.schemaMap.put(this.livingLangWithVarTable.getName(), livingLanguageWithVariantCategory);
        this.schemaMap.put(this.platformUpdateTable.getName(), platformUpdateCategory);
        this.schemaMap.put(this.customConfigTable.getName(), customConfigCategory);
        this.codec = this.catalogTable.getCodec();
        this.types = Collections.unmodifiableSet(this.tableMap.keySet());
    }

    private Map<String, String> convertCursorPositionToProps(Cursor cursor) {
        HashMap hashMap = new HashMap();
        for (int i = 0; i < cursor.getColumnCount(); i++) {
            hashMap.put(cursor.getColumnName(i), cursor.getString(i));
        }
        hashMap.remove(CATEGORY_NAME);
        return hashMap;
    }

    public static synchronized CategoryDatabase from(Context context) {
        CategoryDatabase categoryDatabase;
        synchronized (CategoryDatabase.class) {
            if (instance == null) {
                instance = new CategoryDatabase(context.getApplicationContext());
            }
            categoryDatabase = instance;
        }
        return categoryDatabase;
    }

    private Set<String> getCategoriesFromCursor(Cursor cursor) {
        HashSet hashSet = new HashSet();
        if (cursor != null) {
            boolean moveToFirst = cursor.moveToFirst();
            while (moveToFirst) {
                hashSet.add(cursor.getString(0));
                moveToFirst = cursor.moveToNext();
            }
        }
        return hashSet;
    }

    private boolean hasCategory(String str, Collection<String> collection) {
        Cursor cursor;
        SQLiteQueryBuilder sQLiteQueryBuilder = new SQLiteQueryBuilder();
        try {
        } catch (SQLiteException e) {
            log.e("hasCategory failed; message: " + e.getMessage());
        } catch (Exception e2) {
            log.e("hasCategory failed; message: " + e2.getMessage());
        }
        try {
            sQLiteQueryBuilder.appendWhere("ccc=\"" + this.database.getDatabaseString(str) + "\"");
            ArrayList arrayList = new ArrayList();
            HashSet hashSet = new HashSet();
            hashSet.add(CATEGORY_NAME);
            Iterator<String> it = collection.iterator();
            while (it.hasNext()) {
                sQLiteQueryBuilder.setTables(it.next());
                arrayList.add(sQLiteQueryBuilder.buildUnionSubQuery(null, new String[]{CATEGORY_NAME}, hashSet, 0, CATEGORY_NAME, null, null, null));
            }
            cursor = this.database.rawQuery(sQLiteQueryBuilder.buildUnionQuery((String[]) arrayList.toArray(new String[arrayList.size()]), null, null), null, this.codec);
            try {
                if (cursor.getCount() > 0) {
                    if (cursor != null) {
                        cursor.close();
                    }
                    return true;
                }
                if (cursor != null) {
                    cursor.close();
                }
                return false;
            } catch (Throwable th) {
                th = th;
                if (cursor != null) {
                    cursor.close();
                }
                throw th;
            }
        } catch (Throwable th2) {
            th = th2;
            cursor = null;
        }
    }

    private void propWrite(String str, ContentValues contentValues) {
        for (String str2 : this.types) {
            if (hasCategory(str, str2)) {
                propWrite(str, str2, contentValues);
                return;
            }
        }
    }

    private void propWrite(String str, String str2, ContentValues contentValues) {
        ContentValues contentValues2 = new ContentValues();
        for (String str3 : contentValues.keySet()) {
            contentValues2.put(str3, contentValues.getAsString(str3));
        }
        MasterDatabase.DatabaseTable tableDatabase = this.database.getTableDatabase(str2);
        try {
            if (tableDatabase == null) {
                log.e("propWrite() table is null: " + str2);
                return;
            }
            try {
                tableDatabase.beginTransaction();
                if (tableDatabase.update(contentValues2, "ccc=?", new String[]{str}) < 0) {
                    log.e("save failed to insert property for category: ", str, "; values: ", contentValues2);
                } else {
                    tableDatabase.setTransactionSuccessful();
                }
            } finally {
                tableDatabase.endTransaction();
            }
        } catch (SQLException e) {
            log.e("propWrite() Error in [CategoryDatabase] category(", str, "): " + e.getMessage());
        } catch (Exception e2) {
            log.e("propWrite() Error in [CategoryDatabase] category(", str, "): " + e2.getMessage());
        }
    }

    private void propsWrite(String str, Map<String, String> map) {
        map.remove(CATEGORY_NAME);
        for (String str2 : this.types) {
            MasterDatabase.DatabaseTable tableDatabase = this.database.getTableDatabase(str2);
            if (tableDatabase == null) {
                log.e("propsWrite() table is null: " + str2);
            } else {
                try {
                    if (hasCategory(str, str2)) {
                        try {
                            tableDatabase.beginTransaction();
                            ContentValues contentValues = new ContentValues();
                            for (Map.Entry<String, String> entry : map.entrySet()) {
                                contentValues.put(entry.getKey(), entry.getValue());
                            }
                            contentValues.put(CATEGORY_NAME, str);
                            if (tableDatabase.update(contentValues, "ccc=?", new String[]{str}) < 0) {
                                log.e("save failed to insert property for category: ", str, "; values: ", contentValues);
                            } else {
                                tableDatabase.setTransactionSuccessful();
                            }
                            tableDatabase.endTransaction();
                            return;
                        } catch (Throwable th) {
                            tableDatabase.endTransaction();
                            throw th;
                        }
                    }
                } catch (SQLException e) {
                    log.e("propsWrite() Error in [CategoryDatabase] category(", str, "): " + e.getMessage());
                    return;
                } catch (Exception e2) {
                    log.e("propsWrite() Error in [CategoryDatabase] category(", str, "): " + e2.getMessage());
                    return;
                }
            }
        }
        log.d("Did not find id: " + str);
    }

    public boolean addCategory(String str, String str2, ContentValues contentValues) {
        boolean z;
        MasterDatabase.DatabaseTable tableDatabase = this.database.getTableDatabase(str2);
        if (tableDatabase == null) {
            log.e("table is null: " + str2);
            return false;
        }
        ContentValues contentValues2 = new ContentValues();
        for (String str3 : contentValues.keySet()) {
            try {
                contentValues2.put(str3, contentValues.getAsString(str3));
            } finally {
                tableDatabase.endTransaction();
            }
        }
        try {
            contentValues2.put(CATEGORY_NAME, str);
            tableDatabase.beginTransaction();
            if (tableDatabase.insertWithOnConflict(null, contentValues2, 5) >= 0) {
                tableDatabase.setTransactionSuccessful();
                z = true;
            } else {
                z = false;
            }
        } catch (SQLException e) {
            log.e("addCategory failed to add the category: (", str, ") SQL message", e.getMessage());
            tableDatabase.endTransaction();
            z = false;
        }
        return z;
    }

    public boolean addCategory(String str, String str2, Map<String, String> map) {
        ContentValues contentValues = new ContentValues();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (entry.getValue() == null) {
                contentValues.putNull(entry.getKey());
            } else {
                contentValues.put(entry.getKey(), entry.getValue());
            }
        }
        return addCategory(str, str2, contentValues);
    }

    public Set<String> allCategoryIDs() {
        SQLiteQueryBuilder sQLiteQueryBuilder = new SQLiteQueryBuilder();
        ArrayList arrayList = new ArrayList();
        HashSet hashSet = new HashSet();
        hashSet.add(CATEGORY_NAME);
        Iterator<String> it = this.types.iterator();
        while (it.hasNext()) {
            sQLiteQueryBuilder.setTables(it.next());
            arrayList.add(sQLiteQueryBuilder.buildUnionSubQuery(null, new String[]{CATEGORY_NAME}, hashSet, 0, CATEGORY_NAME, null, null, null));
        }
        Cursor rawQuery = this.database.rawQuery(sQLiteQueryBuilder.buildUnionQuery((String[]) arrayList.toArray(new String[arrayList.size()]), null, null), null, this.codec);
        try {
            return getCategoriesFromCursor(rawQuery);
        } finally {
            if (rawQuery != null) {
                rawQuery.close();
            }
        }
    }

    public Set<String> allCategoryIDs(String str) {
        MasterDatabase.DatabaseTable tableDatabase = this.database.getTableDatabase(str);
        if (tableDatabase == null) {
            log.e("allCategoryIDs: table is null: " + str);
            return Collections.EMPTY_SET;
        }
        Cursor query = tableDatabase.query(false, new String[]{CATEGORY_NAME}, null, null, null, null, null, null);
        try {
            Set<String> categoriesFromCursor = getCategoriesFromCursor(query);
        } finally {
            if (query != null) {
                query.close();
            }
        }
    }

    public Map<String, Map<String, String>> allWithProperty(String str) {
        return allWithProperty(str, this.types);
    }

    public Map<String, Map<String, String>> allWithProperty(final String str, Collection<String> collection) {
        return allWithPropertyMap(new HashMap<String, String>() { // from class: com.nuance.connect.sqlite.CategoryDatabase.1
            {
                put(str, null);
            }
        }, collection, false);
    }

    public Map<String, Map<String, String>> allWithPropertyMap(Map<String, String> map, Collection<String> collection, boolean z) {
        Cursor cursor;
        Map<String, Map<String, String>> hashMap;
        SQLiteQueryBuilder sQLiteQueryBuilder = new SQLiteQueryBuilder();
        try {
            try {
                ArrayList arrayList = new ArrayList();
                arrayList.add(CATEGORY_NAME);
                ArrayList arrayList2 = new ArrayList();
                StringBuilder sb = new StringBuilder();
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    String key = entry.getKey();
                    if (key == null || "".equals(key)) {
                        log.d("ignoring a blank or null key in allWithPropertyMap");
                    } else {
                        String actualColumnName = this.database.getActualColumnName(key);
                        arrayList.add(actualColumnName);
                        if (entry.getValue() != null) {
                            if (sb.length() > 0) {
                                sb.append(" AND ");
                            }
                            sb.append(actualColumnName);
                            sb.append(" = '").append(this.database.getDatabaseString(entry.getValue())).append("'");
                        } else if (!z) {
                            if (sb.length() > 0) {
                                sb.append(" AND ");
                            }
                            sb.append(actualColumnName);
                            sb.append(" IS NOT NULL");
                        }
                    }
                }
                for (String str : collection) {
                    sQLiteQueryBuilder.setTables(str);
                    if (this.schemaMap.get(str).getTableProperties().keySet().containsAll(map.keySet())) {
                        arrayList2.add(sQLiteQueryBuilder.buildUnionSubQuery(null, (String[]) arrayList.toArray(new String[arrayList.size()]), new HashSet(arrayList), 0, CATEGORY_NAME, sb.toString(), null, null));
                    }
                }
                cursor = this.database.rawQuery(sQLiteQueryBuilder.buildUnionQuery((String[]) arrayList2.toArray(new String[arrayList2.size()]), null, null), null, this.codec);
                if (cursor != null) {
                    try {
                        if (cursor.moveToFirst()) {
                            hashMap = new HashMap<>();
                            do {
                                hashMap.put(cursor.getString(cursor.getColumnIndex(CATEGORY_NAME)), convertCursorPositionToProps(cursor));
                            } while (cursor.moveToNext());
                            if (cursor != null) {
                                cursor.close();
                            }
                            return hashMap;
                        }
                    } catch (Throwable th) {
                        th = th;
                        if (cursor != null) {
                            cursor.close();
                        }
                        throw th;
                    }
                }
                hashMap = Collections.EMPTY_MAP;
                if (cursor != null) {
                    cursor.close();
                }
                return hashMap;
            } catch (Throwable th2) {
                th = th2;
                cursor = null;
            }
        } catch (SQLiteException e) {
            log.e("allWithProperty failed; message: " + e.getMessage());
            return Collections.EMPTY_MAP;
        } catch (Exception e2) {
            log.e("allWithProperty failed; message: " + e2.getMessage());
            return Collections.EMPTY_MAP;
        }
    }

    public boolean containsProperty(String str, String str2) {
        if (this.schemaMap.containsKey(str)) {
            return this.schemaMap.get(str).getTableProperties().containsKey(str2);
        }
        log.e("containsProperty() table not found: " + str);
        return false;
    }

    public void deleteAll() {
        Iterator<Map.Entry<String, MasterDatabase.DatabaseTable>> it = this.tableMap.entrySet().iterator();
        while (it.hasNext()) {
            it.next().getValue().deleteAll();
        }
    }

    public boolean deleteCategory(String str) {
        boolean z;
        for (String str2 : this.types) {
            if (hasCategory(str, str2)) {
                MasterDatabase.DatabaseTable tableDatabase = this.database.getTableDatabase(str2);
                if (tableDatabase == null) {
                    log.e("deleteCategory() table is null: " + str2);
                } else {
                    try {
                        try {
                            tableDatabase.beginTransaction();
                            long delete = tableDatabase.delete("ccc=?", new String[]{str});
                            if (delete != 1) {
                                log.e("deleteCategory error removing the category: (", str, ") index = ", Long.valueOf(delete));
                                tableDatabase.endTransaction();
                                z = false;
                            } else {
                                tableDatabase.setTransactionSuccessful();
                                tableDatabase.endTransaction();
                                z = true;
                            }
                            return z;
                        } catch (SQLException e) {
                            log.e("deleteCategory error removing the category: (", str, ") SQL message", e.getMessage());
                            tableDatabase.endTransaction();
                        }
                    } catch (Throwable th) {
                        tableDatabase.endTransaction();
                        throw th;
                    }
                }
            }
        }
        return false;
    }

    public boolean getBoolProp(String str, String str2) {
        return Boolean.parseBoolean(getProp(str, str2));
    }

    public Map<String, Map<String, String>> getCategorySet(Collection<String> collection, String str) {
        Cursor cursor;
        if (collection == null || collection.isEmpty()) {
            return null;
        }
        MasterDatabase.DatabaseTable tableDatabase = this.database.getTableDatabase(str);
        try {
            if (tableDatabase == null) {
                log.e("getCategorySet() table is null: " + str);
                return null;
            }
            try {
                StringBuilder sb = new StringBuilder();
                Iterator<String> it = collection.iterator();
                while (it.hasNext()) {
                    it.next();
                    if (sb.length() > 0) {
                        sb.append(" OR ");
                    }
                    sb.append("ccc = ?");
                }
                cursor = tableDatabase.query(false, null, sb.toString(), (String[]) collection.toArray(new String[collection.size()]), null, null, null, null);
                if (cursor != null) {
                    try {
                        if (cursor.moveToFirst()) {
                            HashMap hashMap = new HashMap();
                            do {
                                hashMap.put(cursor.getString(cursor.getColumnIndex(CATEGORY_NAME)), convertCursorPositionToProps(cursor));
                            } while (cursor.moveToNext());
                            if (cursor == null) {
                                return hashMap;
                            }
                            cursor.close();
                            return hashMap;
                        }
                    } catch (Throwable th) {
                        th = th;
                        if (cursor != null) {
                            cursor.close();
                        }
                        throw th;
                    }
                }
                if (cursor != null) {
                    cursor.close();
                }
                return null;
            } catch (Throwable th2) {
                th = th2;
                cursor = null;
            }
        } catch (SQLException e) {
            log.e("getCategorySet() SQLException in [CategoryDatabase] categories(", collection, "): " + e.getMessage());
            return null;
        } catch (Exception e2) {
            log.e("getCategorySet() Exception in [CategoryDatabase] categories(", collection, "): " + e2.getMessage());
            return null;
        }
    }

    public String getCategoryType(String str) {
        for (String str2 : this.types) {
            if (hasCategory(str, str2)) {
                return str2;
            }
        }
        return "";
    }

    public int getIntProp(String str, String str2) {
        try {
            return Integer.parseInt(getProp(str, str2));
        } catch (NumberFormatException e) {
            return Integers.STATUS_SUCCESS;
        }
    }

    public long getLongProp(String str, String str2) {
        try {
            return Long.parseLong(getProp(str, str2));
        } catch (NumberFormatException e) {
            return Long.MIN_VALUE;
        }
    }

    public String getProp(String str, String str2) {
        Cursor cursor;
        for (String str3 : this.types) {
            CommonCategory commonCategory = this.schemaMap.get(str3);
            if (commonCategory != null && commonCategory.getTableProperties().containsKey(str2) && hasCategory(str, str3)) {
                MasterDatabase.DatabaseTable tableDatabase = this.database.getTableDatabase(str3);
                if (tableDatabase == null) {
                    log.e("getProp() table is null: " + str3);
                } else {
                    try {
                        Cursor query = tableDatabase.query(false, new String[]{CATEGORY_NAME, str2}, "ccc = ?", new String[]{str}, null, null, null, null);
                        if (query != null) {
                            try {
                                if (query.moveToFirst()) {
                                    if (query.getCount() > 1) {
                                        log.e("getProp() Unexpected error in CategoryDatabase; count=" + query.getCount(), "; category=", str, "; columns=", Integer.valueOf(query.getColumnCount()));
                                        do {
                                            for (int i = 0; i < query.getColumnCount(); i++) {
                                                log.e("    Column: " + query.getColumnName(i) + "; value: " + query.getString(i));
                                            }
                                        } while (query.moveToNext());
                                        query.moveToFirst();
                                    }
                                    if (query.getCount() > 0 && query.getColumnCount() > 1) {
                                        String string = query.getString(1);
                                        if (query == null) {
                                            return string;
                                        }
                                        query.close();
                                        return string;
                                    }
                                    if (query != null) {
                                        try {
                                            query.close();
                                        } catch (SQLException e) {
                                            e.printStackTrace();
                                            log.e("getProp() SQLException in [CategoryDatabase] category(", str, "): " + e.getMessage());
                                        } catch (Exception e2) {
                                            e2.printStackTrace();
                                            log.e("getProp() Exception in [CategoryDatabase] category(", str, "): " + e2.getMessage());
                                        }
                                    }
                                }
                            } catch (Throwable th) {
                                th = th;
                                cursor = query;
                                if (cursor != null) {
                                    cursor.close();
                                }
                                throw th;
                            }
                        }
                        if (query != null) {
                            query.close();
                        }
                    } catch (Throwable th2) {
                        th = th2;
                        cursor = null;
                    }
                }
            }
        }
        return null;
    }

    public Map<String, String> getProps(String str) {
        Cursor cursor;
        for (String str2 : this.types) {
            MasterDatabase.DatabaseTable tableDatabase = this.database.getTableDatabase(str2);
            if (tableDatabase == null) {
                log.e("getProps() table is null: " + str2);
            } else {
                try {
                    cursor = tableDatabase.query(false, null, "ccc = ?", new String[]{str}, null, null, null, null);
                    if (cursor != null) {
                        try {
                            if (cursor.moveToFirst()) {
                                Map<String, String> convertCursorPositionToProps = convertCursorPositionToProps(cursor);
                                if (cursor == null) {
                                    return convertCursorPositionToProps;
                                }
                                cursor.close();
                                return convertCursorPositionToProps;
                            }
                        } catch (Throwable th) {
                            th = th;
                            if (cursor != null) {
                                cursor.close();
                            }
                            throw th;
                        }
                    }
                    if (cursor != null) {
                        try {
                            cursor.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                            log.e("getProps() SQLException in [CategoryDatabase] category(", str, "): " + e.getMessage());
                        } catch (Exception e2) {
                            e2.printStackTrace();
                            log.e("getProps() Exception in [CategoryDatabase] category(", str, "): " + e2.getMessage());
                        }
                    }
                } catch (Throwable th2) {
                    th = th2;
                    cursor = null;
                }
            }
        }
        return null;
    }

    public int getStep(String str) {
        return getIntProp(str, Strings.MAP_KEY_STEP);
    }

    public String getTableForType(int i) {
        for (Map.Entry<String, CommonCategory> entry : this.schemaMap.entrySet()) {
            if (entry.getValue().getCategoryType() == i) {
                return entry.getKey();
            }
        }
        log.e("getTableForType type not found in list of schemas: " + i);
        return null;
    }

    public int getTypeForTable(String str) {
        if (this.schemaMap.containsKey(str)) {
            return this.schemaMap.get(str).getCategoryType();
        }
        log.e("getTypeForTable type not found in list of schemas: " + str);
        return 0;
    }

    public boolean hasCategory(String str) {
        return hasCategory(str, this.types);
    }

    public boolean hasCategory(String str, String str2) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(str2);
        return hasCategory(str, arrayList);
    }

    public boolean isEmpty() {
        return allCategoryIDs().isEmpty();
    }

    public boolean isInstalled(String str) {
        return getStep(str) == 7;
    }

    public List<String> listFromPropEquals(String str, String str2) {
        ArrayList arrayList = new ArrayList();
        for (Map.Entry<String, Map<String, String>> entry : allWithProperty(str).entrySet()) {
            try {
                String str3 = entry.getValue().get(str);
                if (str3 != null && str3.equals(str2)) {
                    arrayList.add(entry.getKey());
                }
            } catch (NumberFormatException e) {
            }
        }
        return arrayList;
    }

    public List<String> listFromSteps(List<Integer> list) {
        ArrayList arrayList = new ArrayList();
        for (Map.Entry<String, Map<String, String>> entry : allWithProperty(Strings.MAP_KEY_STEP).entrySet()) {
            try {
                if (list.contains(Integer.valueOf(Integer.parseInt(entry.getValue().get(Strings.MAP_KEY_STEP))))) {
                    arrayList.add(entry.getKey());
                }
            } catch (NumberFormatException e) {
            }
        }
        return arrayList;
    }

    public void removeProp(String str, String str2) {
        setProp(str, str2, (String) null);
    }

    public void setProp(String str, String str2, int i) {
        setProp(str, str2, Integer.toString(i));
    }

    public void setProp(String str, String str2, long j) {
        setProp(str, str2, Long.toString(j));
    }

    public void setProp(String str, String str2, String str3) {
        ContentValues contentValues = new ContentValues();
        if (str3 == null) {
            contentValues.putNull(str2);
        } else {
            contentValues.put(str2, str3);
        }
        propWrite(str, contentValues);
    }

    public void setProp(String str, String str2, boolean z) {
        setProp(str, str2, Boolean.toString(z));
    }

    public void setProps(String str, Map<String, String> map) {
        propsWrite(str, map);
    }

    public void setStep(String str, int i) {
        setProp(str, Strings.MAP_KEY_STEP, i);
    }
}
