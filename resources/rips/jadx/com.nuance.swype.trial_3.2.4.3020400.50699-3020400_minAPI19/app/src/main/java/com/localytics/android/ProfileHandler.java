package com.localytics.android;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Looper;
import android.os.Message;
import com.localytics.android.Localytics;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.Future;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class ProfileHandler extends BaseHandler {
    static final String ATTRIBUTE_JSON_KEY_KEY = "attr";
    static final String ATTRIBUTE_JSON_OP_KEY = "op";
    static final String ATTRIBUTE_JSON_VALUE_KEY = "value";
    private static final int MESSAGE_SET_PROFILE_ATTRIBUTE = 301;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ProfileHandler(LocalyticsDao localyticsDao, Looper looper) {
        super(localyticsDao, looper);
        this.siloName = "Profile";
        queueMessage(obtainMessage(1));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.localytics.android.BaseHandler
    public void handleMessageExtended(Message msg) throws Exception {
        switch (msg.what) {
            case 301:
                Localytics.Log.d("Profile handler received MESSAGE_SET_PROFILE_ATTRIBUTE");
                Object[] params = (Object[]) msg.obj;
                final String attribute = (String) params[0];
                final String database = (String) params[1];
                final String customerID = (String) ((Future) params[2]).get();
                _runBatchTransactionOnProvider(new Runnable() { // from class: com.localytics.android.ProfileHandler.1
                    @Override // java.lang.Runnable
                    public void run() {
                        ProfileHandler.this._setAttribute(attribute, database, customerID);
                    }
                });
                return;
            default:
                super.handleMessageExtended(msg);
                return;
        }
    }

    @Override // com.localytics.android.BaseHandler
    protected void _init() {
        this.mProvider = new ProfileProvider(this.siloName.toLowerCase(), this.mLocalyticsDao);
        this.mProvider.vacuumIfNecessary();
    }

    @Override // com.localytics.android.BaseHandler
    protected void _deleteUploadedData(int maxRowToDelete) {
        this.mProvider.remove("changes", "_id <= " + maxRowToDelete, null);
    }

    @Override // com.localytics.android.BaseHandler
    protected void _onUploadCompleted(boolean successful, String responseBody) {
        this.mProvider.vacuumIfNecessary();
    }

    @Override // com.localytics.android.BaseHandler
    protected int _getMaxRowToUpload() {
        int numberOfAttributes = 0;
        Cursor cursor = null;
        try {
            cursor = this.mProvider.query("changes", new String[]{"_id"}, null, null, "_id ASC");
            if (cursor.moveToLast()) {
                numberOfAttributes = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
            }
            if (cursor != null) {
                cursor.close();
            }
            return numberOfAttributes;
        } catch (Throwable th) {
            if (cursor != null) {
                cursor.close();
            }
            return 0;
        }
    }

    @Override // com.localytics.android.BaseHandler
    protected TreeMap<Integer, Object> _getDataToUpload() {
        TreeMap<Integer, Object> attributesMap = new TreeMap<>();
        Cursor cursor = null;
        try {
            cursor = this.mProvider.query("changes", null, null, null, "_id ASC");
            String previousCustomerID = null;
            String previousDatabase = null;
            while (cursor.moveToNext() && attributesMap.size() < 50.0d) {
                int attributeID = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
                String attribute = cursor.getString(cursor.getColumnIndexOrThrow("change"));
                String customerID = cursor.getString(cursor.getColumnIndexOrThrow("customer_id"));
                String database = cursor.getString(cursor.getColumnIndex("scope"));
                if (previousCustomerID == null) {
                    previousCustomerID = customerID;
                    previousDatabase = database;
                }
                if (!previousCustomerID.equals(customerID) || !previousDatabase.equals(database)) {
                    break;
                }
                attributesMap.put(Integer.valueOf(attributeID), new Object[]{customerID, database, attribute});
            }
            if (cursor != null) {
                cursor.close();
            }
        } catch (Throwable th) {
            if (cursor != null) {
                cursor.close();
            }
        }
        return attributesMap;
    }

    @Override // com.localytics.android.BaseHandler
    protected BaseUploadThread _getUploadThread(TreeMap<Integer, Object> data, String customerId) {
        return new ProfileUploadHandler(this, data, customerId, this.mLocalyticsDao);
    }

    private void saveAttributeChange(ProfileOperation op, String attrName, Object attrValue, String database) {
        if (checkAttributeName(attrName)) {
            try {
                JSONObject json = new JSONObject();
                json.put(ATTRIBUTE_JSON_OP_KEY, op.getOperationString());
                json.put(ATTRIBUTE_JSON_KEY_KEY, attrName);
                if (attrValue instanceof Set) {
                    JSONArray jsonArray = new JSONArray();
                    for (Object o : (Set) attrValue) {
                        jsonArray.put(o);
                    }
                    json.put(ATTRIBUTE_JSON_VALUE_KEY, jsonArray);
                } else if (attrValue != null) {
                    json.put(ATTRIBUTE_JSON_VALUE_KEY, attrValue);
                }
                queueMessage(obtainMessage(301, new Object[]{json.toString(), database, this.mLocalyticsDao.getCustomerIdFuture()}));
            } catch (JSONException e) {
                Localytics.Log.w("Caught JSON exception", e);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setProfileAttribute(String attrName, long attrValue, String database) {
        saveAttributeChange(ProfileOperation.ASSIGN, attrName, Long.valueOf(attrValue), database);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setProfileAttribute(String attrName, long[] attrValues, String database) {
        saveAttributeChange(ProfileOperation.ASSIGN, attrName, convertToSet(attrValues), database);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setProfileAttribute(String attrName, String attrValue, String database) {
        saveAttributeChange(ProfileOperation.ASSIGN, attrName, attrValue, database);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setProfileAttribute(String attrName, String[] attrValues, String database) {
        saveAttributeChange(ProfileOperation.ASSIGN, attrName, new HashSet(Arrays.asList(attrValues)), database);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setProfileAttribute(String attrName, Date attrValue, String database) {
        saveAttributeChange(ProfileOperation.ASSIGN, attrName, convertDateToString(attrValue), database);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setProfileAttribute(String attrName, Date[] attrValues, String database) {
        saveAttributeChange(ProfileOperation.ASSIGN, attrName, convertToSet(attrValues), database);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void deleteProfileAttribute(String attrName, String database) {
        saveAttributeChange(ProfileOperation.DELETE, attrName, null, database);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void addProfileAttributesToSet(String attrName, long[] attrValues, String database) {
        saveAttributeChange(ProfileOperation.SETADD, attrName, convertToSet(attrValues), database);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void addProfileAttributesToSet(String attrName, String[] attrValues, String database) {
        saveAttributeChange(ProfileOperation.SETADD, attrName, new HashSet(Arrays.asList(attrValues)), database);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void addProfileAttributesToSet(String attrName, Date[] attrValues, String database) {
        saveAttributeChange(ProfileOperation.SETADD, attrName, convertToSet(attrValues), database);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void removeProfileAttributesFromSet(String attrName, long[] attrValues, String database) {
        saveAttributeChange(ProfileOperation.SETREMOVE, attrName, convertToSet(attrValues), database);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void removeProfileAttributesFromSet(String attrName, String[] attrValues, String database) {
        saveAttributeChange(ProfileOperation.SETREMOVE, attrName, new HashSet(Arrays.asList(attrValues)), database);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void removeProfileAttributesFromSet(String attrName, Date[] attrValues, String database) {
        saveAttributeChange(ProfileOperation.SETREMOVE, attrName, convertToSet(attrValues), database);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void incrementProfileAttribute(String attrName, long incrementVal, String database) {
        saveAttributeChange(ProfileOperation.INCREMENT, attrName, Long.valueOf(incrementVal), database);
    }

    private static boolean checkAttributeName(String attrName) {
        if (attrName == null) {
            Localytics.Log.e("attribute name cannot be null");
            return false;
        }
        String attributeName = attrName.trim();
        if (attributeName.length() == 0) {
            Localytics.Log.e("attribute name cannot be empty");
            return false;
        }
        return true;
    }

    private static Object convertDateToString(Object attrVal) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        if (attrVal instanceof Date) {
            return simpleDateFormat.format(attrVal);
        }
        return null;
    }

    private static Set<Long> convertToSet(long[] longArray) {
        Long[] array = new Long[longArray.length];
        int index = 0;
        for (long l : longArray) {
            array[index] = Long.valueOf(l);
            index++;
        }
        return new HashSet(Arrays.asList(array));
    }

    private Set<Object> convertToSet(Date[] attrValues) {
        Set<Object> strings = new HashSet<>();
        for (Date d : attrValues) {
            strings.add(convertDateToString(d));
        }
        return strings;
    }

    protected void _setAttribute(String attribute, String database, String customerID) {
        ContentValues values = new ContentValues();
        values.put("scope", database);
        values.put("change", attribute);
        values.put("customer_id", customerID);
        this.mProvider.insert("changes", values);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public enum ProfileOperation {
        ASSIGN("assign"),
        DELETE("delete"),
        SETADD("set-add"),
        SETREMOVE("set-remove"),
        INCREMENT("increment");

        private final String operation;

        ProfileOperation(String op) {
            this.operation = op;
        }

        public final String getOperationString() {
            return this.operation;
        }
    }
}
