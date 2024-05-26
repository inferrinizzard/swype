package android.support.v7.widget;

import android.support.v4.util.ArrayMap;
import android.support.v4.util.LongSparseArray;
import android.support.v4.util.Pools;
import android.support.v7.widget.RecyclerView;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class ViewInfoStore {
    final ArrayMap<RecyclerView.ViewHolder, InfoRecord> mLayoutHolderMap = new ArrayMap<>();
    final LongSparseArray<RecyclerView.ViewHolder> mOldChangedHolders = new LongSparseArray<>();

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public interface ProcessCallback {
        void processAppeared(RecyclerView.ViewHolder viewHolder, RecyclerView.ItemAnimator.ItemHolderInfo itemHolderInfo, RecyclerView.ItemAnimator.ItemHolderInfo itemHolderInfo2);

        void processDisappeared(RecyclerView.ViewHolder viewHolder, RecyclerView.ItemAnimator.ItemHolderInfo itemHolderInfo, RecyclerView.ItemAnimator.ItemHolderInfo itemHolderInfo2);

        void processPersistent(RecyclerView.ViewHolder viewHolder, RecyclerView.ItemAnimator.ItemHolderInfo itemHolderInfo, RecyclerView.ItemAnimator.ItemHolderInfo itemHolderInfo2);

        void unused(RecyclerView.ViewHolder viewHolder);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void clear() {
        this.mLayoutHolderMap.clear();
        LongSparseArray<RecyclerView.ViewHolder> longSparseArray = this.mOldChangedHolders;
        int i = longSparseArray.mSize;
        Object[] objArr = longSparseArray.mValues;
        for (int i2 = 0; i2 < i; i2++) {
            objArr[i2] = null;
        }
        longSparseArray.mSize = 0;
        longSparseArray.mGarbage = false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void addToPreLayout(RecyclerView.ViewHolder holder, RecyclerView.ItemAnimator.ItemHolderInfo info) {
        InfoRecord record = this.mLayoutHolderMap.get(holder);
        if (record == null) {
            record = InfoRecord.obtain();
            this.mLayoutHolderMap.put(holder, record);
        }
        record.preInfo = info;
        record.flags |= 4;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean isDisappearing(RecyclerView.ViewHolder holder) {
        InfoRecord record = this.mLayoutHolderMap.get(holder);
        return (record == null || (record.flags & 1) == 0) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final RecyclerView.ItemAnimator.ItemHolderInfo popFromLayoutStep(RecyclerView.ViewHolder vh, int flag) {
        InfoRecord record;
        RecyclerView.ItemAnimator.ItemHolderInfo info = null;
        int index = this.mLayoutHolderMap.indexOfKey(vh);
        if (index >= 0 && (record = this.mLayoutHolderMap.valueAt(index)) != null && (record.flags & flag) != 0) {
            record.flags &= flag ^ (-1);
            if (flag == 4) {
                info = record.preInfo;
            } else if (flag == 8) {
                info = record.postInfo;
            } else {
                throw new IllegalArgumentException("Must provide flag PRE or POST");
            }
            if ((record.flags & 12) == 0) {
                this.mLayoutHolderMap.removeAt(index);
                InfoRecord.recycle(record);
            }
        }
        return info;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void addToOldChangeHolders(long key, RecyclerView.ViewHolder holder) {
        this.mOldChangedHolders.put(key, holder);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void addToPostLayout(RecyclerView.ViewHolder holder, RecyclerView.ItemAnimator.ItemHolderInfo info) {
        InfoRecord record = this.mLayoutHolderMap.get(holder);
        if (record == null) {
            record = InfoRecord.obtain();
            this.mLayoutHolderMap.put(holder, record);
        }
        record.postInfo = info;
        record.flags |= 8;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void addToDisappearedInLayout(RecyclerView.ViewHolder holder) {
        InfoRecord record = this.mLayoutHolderMap.get(holder);
        if (record == null) {
            record = InfoRecord.obtain();
            this.mLayoutHolderMap.put(holder, record);
        }
        record.flags |= 1;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void removeFromDisappearedInLayout(RecyclerView.ViewHolder holder) {
        InfoRecord record = this.mLayoutHolderMap.get(holder);
        if (record != null) {
            record.flags &= -2;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void removeViewHolder(RecyclerView.ViewHolder holder) {
        int i = this.mOldChangedHolders.size() - 1;
        while (true) {
            if (i < 0) {
                break;
            }
            if (holder != this.mOldChangedHolders.valueAt(i)) {
                i--;
            } else {
                LongSparseArray<RecyclerView.ViewHolder> longSparseArray = this.mOldChangedHolders;
                if (longSparseArray.mValues[i] != LongSparseArray.DELETED) {
                    longSparseArray.mValues[i] = LongSparseArray.DELETED;
                    longSparseArray.mGarbage = true;
                }
            }
        }
        InfoRecord info = this.mLayoutHolderMap.remove(holder);
        if (info != null) {
            InfoRecord.recycle(info);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class InfoRecord {
        static Pools.Pool<InfoRecord> sPool = new Pools.SimplePool(20);
        int flags;
        RecyclerView.ItemAnimator.ItemHolderInfo postInfo;
        RecyclerView.ItemAnimator.ItemHolderInfo preInfo;

        private InfoRecord() {
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static InfoRecord obtain() {
            InfoRecord record = sPool.acquire();
            return record == null ? new InfoRecord() : record;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static void recycle(InfoRecord record) {
            record.flags = 0;
            record.preInfo = null;
            record.postInfo = null;
            sPool.release(record);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static void drainCache() {
            do {
            } while (sPool.acquire() != null);
        }
    }
}
