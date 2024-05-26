package android.support.v7.widget;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.DataSetObservable;
import android.os.AsyncTask;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class ActivityChooserModel extends DataSetObservable {
    final List<ActivityResolveInfo> mActivities;
    private OnChooseActivityListener mActivityChoserModelPolicy;
    private ActivitySorter mActivitySorter;
    private boolean mCanReadHistoricalData;
    private final Context mContext;
    private final List<HistoricalRecord> mHistoricalRecords;
    private boolean mHistoricalRecordsChanged;
    private final String mHistoryFileName;
    private int mHistoryMaxSize;
    final Object mInstanceLock;
    private Intent mIntent;
    private boolean mReadShareHistoryCalled;
    private boolean mReloadActivities;
    private static final String LOG_TAG = ActivityChooserModel.class.getSimpleName();
    private static final Object sRegistryLock = new Object();
    private static final Map<String, ActivityChooserModel> sDataModelRegistry = new HashMap();

    /* loaded from: classes.dex */
    public interface ActivitySorter {
    }

    /* loaded from: classes.dex */
    public interface OnChooseActivityListener {
        boolean onChooseActivity$63493815();
    }

    static /* synthetic */ boolean access$502$59b42612(ActivityChooserModel x0) {
        x0.mCanReadHistoricalData = true;
        return true;
    }

    public final int getActivityCount() {
        int size;
        synchronized (this.mInstanceLock) {
            ensureConsistentState();
            size = this.mActivities.size();
        }
        return size;
    }

    public final ResolveInfo getActivity(int index) {
        ResolveInfo resolveInfo;
        synchronized (this.mInstanceLock) {
            ensureConsistentState();
            resolveInfo = this.mActivities.get(index).resolveInfo;
        }
        return resolveInfo;
    }

    public final int getActivityIndex(ResolveInfo activity) {
        int i;
        synchronized (this.mInstanceLock) {
            ensureConsistentState();
            List<ActivityResolveInfo> activities = this.mActivities;
            int activityCount = activities.size();
            i = 0;
            while (true) {
                if (i < activityCount) {
                    if (activities.get(i).resolveInfo == activity) {
                        break;
                    }
                    i++;
                } else {
                    i = -1;
                    break;
                }
            }
        }
        return i;
    }

    public final Intent chooseActivity(int index) {
        synchronized (this.mInstanceLock) {
            if (this.mIntent == null) {
                return null;
            }
            ensureConsistentState();
            ActivityResolveInfo chosenActivity = this.mActivities.get(index);
            ComponentName chosenName = new ComponentName(chosenActivity.resolveInfo.activityInfo.packageName, chosenActivity.resolveInfo.activityInfo.name);
            Intent choiceIntent = new Intent(this.mIntent);
            choiceIntent.setComponent(chosenName);
            if (this.mActivityChoserModelPolicy != null) {
                new Intent(choiceIntent);
                if (this.mActivityChoserModelPolicy.onChooseActivity$63493815()) {
                    return null;
                }
            }
            HistoricalRecord historicalRecord = new HistoricalRecord(chosenName, System.currentTimeMillis(), 1.0f);
            addHisoricalRecord(historicalRecord);
            return choiceIntent;
        }
    }

    public final ResolveInfo getDefaultActivity() {
        synchronized (this.mInstanceLock) {
            ensureConsistentState();
            if (!this.mActivities.isEmpty()) {
                return this.mActivities.get(0).resolveInfo;
            }
            return null;
        }
    }

    public final int getHistorySize() {
        int size;
        synchronized (this.mInstanceLock) {
            ensureConsistentState();
            size = this.mHistoricalRecords.size();
        }
        return size;
    }

    private boolean sortActivitiesIfNeeded() {
        if (this.mActivitySorter == null || this.mIntent == null || this.mActivities.isEmpty() || this.mHistoricalRecords.isEmpty()) {
            return false;
        }
        Collections.unmodifiableList(this.mHistoricalRecords);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean addHisoricalRecord(HistoricalRecord historicalRecord) {
        byte b = 0;
        boolean added = this.mHistoricalRecords.add(historicalRecord);
        if (added) {
            this.mHistoricalRecordsChanged = true;
            pruneExcessiveHistoricalRecordsIfNeeded();
            if (!this.mReadShareHistoryCalled) {
                throw new IllegalStateException("No preceding call to #readHistoricalData");
            }
            if (this.mHistoricalRecordsChanged) {
                this.mHistoricalRecordsChanged = false;
                if (!TextUtils.isEmpty(this.mHistoryFileName)) {
                    PersistHistoryAsyncTask persistHistoryAsyncTask = new PersistHistoryAsyncTask(this, b);
                    Object[] objArr = {new ArrayList(this.mHistoricalRecords), this.mHistoryFileName};
                    if (Build.VERSION.SDK_INT < 11) {
                        persistHistoryAsyncTask.execute(objArr);
                    } else {
                        persistHistoryAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, objArr);
                    }
                }
            }
            sortActivitiesIfNeeded();
            notifyChanged();
        }
        return added;
    }

    private void pruneExcessiveHistoricalRecordsIfNeeded() {
        int pruneCount = this.mHistoricalRecords.size() - this.mHistoryMaxSize;
        if (pruneCount > 0) {
            this.mHistoricalRecordsChanged = true;
            for (int i = 0; i < pruneCount; i++) {
                this.mHistoricalRecords.remove(0);
            }
        }
    }

    /* loaded from: classes.dex */
    public static final class HistoricalRecord {
        public final ComponentName activity;
        public final long time;
        public final float weight;

        public HistoricalRecord(String activityName, long time, float weight) {
            this(ComponentName.unflattenFromString(activityName), time, weight);
        }

        public HistoricalRecord(ComponentName activityName, long time, float weight) {
            this.activity = activityName;
            this.time = time;
            this.weight = weight;
        }

        public final int hashCode() {
            int result = (this.activity == null ? 0 : this.activity.hashCode()) + 31;
            return (((result * 31) + ((int) (this.time ^ (this.time >>> 32)))) * 31) + Float.floatToIntBits(this.weight);
        }

        public final boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj != null && getClass() == obj.getClass()) {
                HistoricalRecord other = (HistoricalRecord) obj;
                if (this.activity == null) {
                    if (other.activity != null) {
                        return false;
                    }
                } else if (!this.activity.equals(other.activity)) {
                    return false;
                }
                return this.time == other.time && Float.floatToIntBits(this.weight) == Float.floatToIntBits(other.weight);
            }
            return false;
        }

        public final String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("[");
            builder.append("; activity:").append(this.activity);
            builder.append("; time:").append(this.time);
            builder.append("; weight:").append(new BigDecimal(this.weight));
            builder.append("]");
            return builder.toString();
        }
    }

    /* loaded from: classes.dex */
    public final class ActivityResolveInfo implements Comparable<ActivityResolveInfo> {
        public final ResolveInfo resolveInfo;
        public float weight;

        @Override // java.lang.Comparable
        public final /* bridge */ /* synthetic */ int compareTo(ActivityResolveInfo activityResolveInfo) {
            return Float.floatToIntBits(activityResolveInfo.weight) - Float.floatToIntBits(this.weight);
        }

        public ActivityResolveInfo(ResolveInfo resolveInfo) {
            this.resolveInfo = resolveInfo;
        }

        public final int hashCode() {
            return Float.floatToIntBits(this.weight) + 31;
        }

        public final boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj != null && getClass() == obj.getClass()) {
                ActivityResolveInfo other = (ActivityResolveInfo) obj;
                return Float.floatToIntBits(this.weight) == Float.floatToIntBits(other.weight);
            }
            return false;
        }

        public final String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("[");
            builder.append("resolveInfo:").append(this.resolveInfo.toString());
            builder.append("; weight:").append(new BigDecimal(this.weight));
            builder.append("]");
            return builder.toString();
        }
    }

    private void readHistoricalDataImpl() {
        try {
            FileInputStream fis = this.mContext.openFileInput(this.mHistoryFileName);
            try {
                try {
                    XmlPullParser parser = Xml.newPullParser();
                    parser.setInput(fis, "UTF-8");
                    for (int type = 0; type != 1 && type != 2; type = parser.next()) {
                    }
                    if (!"historical-records".equals(parser.getName())) {
                        throw new XmlPullParserException("Share records file does not start with historical-records tag.");
                    }
                    List<HistoricalRecord> historicalRecords = this.mHistoricalRecords;
                    historicalRecords.clear();
                    while (true) {
                        int type2 = parser.next();
                        if (type2 == 1) {
                            if (fis != null) {
                                try {
                                    fis.close();
                                    return;
                                } catch (IOException e) {
                                    return;
                                }
                            }
                            return;
                        }
                        if (type2 != 3 && type2 != 4) {
                            String nodeName = parser.getName();
                            if (!"historical-record".equals(nodeName)) {
                                throw new XmlPullParserException("Share records file not well-formed.");
                            }
                            String activity = parser.getAttributeValue(null, "activity");
                            long time = Long.parseLong(parser.getAttributeValue(null, "time"));
                            float weight = Float.parseFloat(parser.getAttributeValue(null, "weight"));
                            HistoricalRecord readRecord = new HistoricalRecord(activity, time, weight);
                            historicalRecords.add(readRecord);
                        }
                    }
                } catch (IOException ioe) {
                    Log.e(LOG_TAG, "Error reading historical recrod file: " + this.mHistoryFileName, ioe);
                    if (fis != null) {
                        try {
                            fis.close();
                        } catch (IOException e2) {
                        }
                    }
                } catch (XmlPullParserException xppe) {
                    Log.e(LOG_TAG, "Error reading historical recrod file: " + this.mHistoryFileName, xppe);
                    if (fis != null) {
                        try {
                            fis.close();
                        } catch (IOException e3) {
                        }
                    }
                }
            } catch (Throwable th) {
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e4) {
                    }
                }
                throw th;
            }
        } catch (FileNotFoundException e5) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public final class PersistHistoryAsyncTask extends AsyncTask<Object, Void, Void> {
        private PersistHistoryAsyncTask() {
        }

        /* synthetic */ PersistHistoryAsyncTask(ActivityChooserModel x0, byte b) {
            this();
        }

        /* JADX INFO: Access modifiers changed from: private */
        @Override // android.os.AsyncTask
        public Void doInBackground(Object... args) {
            List<HistoricalRecord> historicalRecords = (List) args[0];
            String hostoryFileName = (String) args[1];
            try {
                FileOutputStream fos = ActivityChooserModel.this.mContext.openFileOutput(hostoryFileName, 0);
                XmlSerializer serializer = Xml.newSerializer();
                try {
                    try {
                        try {
                            serializer.setOutput(fos, null);
                            serializer.startDocument("UTF-8", true);
                            serializer.startTag(null, "historical-records");
                            int recordCount = historicalRecords.size();
                            for (int i = 0; i < recordCount; i++) {
                                HistoricalRecord record = historicalRecords.remove(0);
                                serializer.startTag(null, "historical-record");
                                serializer.attribute(null, "activity", record.activity.flattenToString());
                                serializer.attribute(null, "time", String.valueOf(record.time));
                                serializer.attribute(null, "weight", String.valueOf(record.weight));
                                serializer.endTag(null, "historical-record");
                            }
                            serializer.endTag(null, "historical-records");
                            serializer.endDocument();
                            ActivityChooserModel.access$502$59b42612(ActivityChooserModel.this);
                            if (fos != null) {
                                try {
                                    fos.close();
                                } catch (IOException e) {
                                }
                            }
                        } catch (IOException ioe) {
                            Log.e(ActivityChooserModel.LOG_TAG, "Error writing historical recrod file: " + ActivityChooserModel.this.mHistoryFileName, ioe);
                            ActivityChooserModel.access$502$59b42612(ActivityChooserModel.this);
                            if (fos != null) {
                                try {
                                    fos.close();
                                } catch (IOException e2) {
                                }
                            }
                        } catch (IllegalArgumentException iae) {
                            Log.e(ActivityChooserModel.LOG_TAG, "Error writing historical recrod file: " + ActivityChooserModel.this.mHistoryFileName, iae);
                            ActivityChooserModel.access$502$59b42612(ActivityChooserModel.this);
                            if (fos != null) {
                                try {
                                    fos.close();
                                } catch (IOException e3) {
                                }
                            }
                        }
                    } catch (IllegalStateException ise) {
                        Log.e(ActivityChooserModel.LOG_TAG, "Error writing historical recrod file: " + ActivityChooserModel.this.mHistoryFileName, ise);
                        ActivityChooserModel.access$502$59b42612(ActivityChooserModel.this);
                        if (fos != null) {
                            try {
                                fos.close();
                            } catch (IOException e4) {
                            }
                        }
                    }
                    return null;
                } catch (Throwable th) {
                    ActivityChooserModel.access$502$59b42612(ActivityChooserModel.this);
                    if (fos != null) {
                        try {
                            fos.close();
                        } catch (IOException e5) {
                        }
                    }
                    throw th;
                }
            } catch (FileNotFoundException fnfe) {
                Log.e(ActivityChooserModel.LOG_TAG, "Error writing historical recrod file: " + hostoryFileName, fnfe);
                return null;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void ensureConsistentState() {
        boolean z;
        boolean z2 = true;
        if (!this.mReloadActivities || this.mIntent == null) {
            z = false;
        } else {
            this.mReloadActivities = false;
            this.mActivities.clear();
            List<ResolveInfo> queryIntentActivities = this.mContext.getPackageManager().queryIntentActivities(this.mIntent, 0);
            int size = queryIntentActivities.size();
            for (int i = 0; i < size; i++) {
                this.mActivities.add(new ActivityResolveInfo(queryIntentActivities.get(i)));
            }
            z = true;
        }
        if (this.mCanReadHistoricalData && this.mHistoricalRecordsChanged && !TextUtils.isEmpty(this.mHistoryFileName)) {
            this.mCanReadHistoricalData = false;
            this.mReadShareHistoryCalled = true;
            readHistoricalDataImpl();
        } else {
            z2 = false;
        }
        boolean stateChanged = z | z2;
        pruneExcessiveHistoricalRecordsIfNeeded();
        if (stateChanged) {
            sortActivitiesIfNeeded();
            notifyChanged();
        }
    }
}
