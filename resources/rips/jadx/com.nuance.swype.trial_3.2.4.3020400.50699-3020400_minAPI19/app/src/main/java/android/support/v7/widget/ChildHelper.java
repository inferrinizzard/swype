package android.support.v7.widget;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class ChildHelper {
    final Callback mCallback;
    final Bucket mBucket = new Bucket();
    final List<View> mHiddenViews = new ArrayList();

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public interface Callback {
        void addView(View view, int i);

        void attachViewToParent(View view, int i, ViewGroup.LayoutParams layoutParams);

        void detachViewFromParent(int i);

        View getChildAt(int i);

        int getChildCount();

        RecyclerView.ViewHolder getChildViewHolder(View view);

        int indexOfChild(View view);

        void onEnteredHiddenState(View view);

        void onLeftHiddenState(View view);

        void removeAllViews();

        void removeViewAt(int i);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ChildHelper(Callback callback) {
        this.mCallback = callback;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void hideViewInternal(View child) {
        this.mHiddenViews.add(child);
        this.mCallback.onEnteredHiddenState(child);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean unhideViewInternal(View child) {
        if (!this.mHiddenViews.remove(child)) {
            return false;
        }
        this.mCallback.onLeftHiddenState(child);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void addView(View child, int index, boolean hidden) {
        int offset;
        if (index < 0) {
            offset = this.mCallback.getChildCount();
        } else {
            offset = getOffset(index);
        }
        this.mBucket.insert(offset, hidden);
        if (hidden) {
            hideViewInternal(child);
        }
        this.mCallback.addView(child, offset);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final int getOffset(int index) {
        if (index < 0) {
            return -1;
        }
        int limit = this.mCallback.getChildCount();
        int offset = index;
        while (offset < limit) {
            int removedBefore = this.mBucket.countOnesBefore(offset);
            int diff = index - (offset - removedBefore);
            if (diff == 0) {
                while (this.mBucket.get(offset)) {
                    offset++;
                }
                return offset;
            }
            offset += diff;
        }
        return -1;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final View getChildAt(int index) {
        int offset = getOffset(index);
        return this.mCallback.getChildAt(offset);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void attachViewToParent(View child, int index, ViewGroup.LayoutParams layoutParams, boolean hidden) {
        int offset;
        if (index < 0) {
            offset = this.mCallback.getChildCount();
        } else {
            offset = getOffset(index);
        }
        this.mBucket.insert(offset, hidden);
        if (hidden) {
            hideViewInternal(child);
        }
        this.mCallback.attachViewToParent(child, offset, layoutParams);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final int getChildCount() {
        return this.mCallback.getChildCount() - this.mHiddenViews.size();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final int getUnfilteredChildCount() {
        return this.mCallback.getChildCount();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final View getUnfilteredChildAt(int index) {
        return this.mCallback.getChildAt(index);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void detachViewFromParent(int index) {
        int offset = getOffset(index);
        this.mBucket.remove(offset);
        this.mCallback.detachViewFromParent(offset);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final int indexOfChild(View child) {
        int index = this.mCallback.indexOfChild(child);
        if (index == -1 || this.mBucket.get(index)) {
            return -1;
        }
        return index - this.mBucket.countOnesBefore(index);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean isHidden(View view) {
        return this.mHiddenViews.contains(view);
    }

    public final String toString() {
        return this.mBucket.toString() + ", hidden list:" + this.mHiddenViews.size();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class Bucket {
        long mData = 0;
        Bucket next;

        Bucket() {
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public final void set(int index) {
            while (index >= 64) {
                this.ensureNext();
                this = this.next;
                index -= 64;
            }
            this.mData |= 1 << index;
        }

        private void ensureNext() {
            if (this.next == null) {
                this.next = new Bucket();
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public final void clear(int index) {
            while (index >= 64) {
                if (this.next != null) {
                    this = this.next;
                    index -= 64;
                } else {
                    return;
                }
            }
            this.mData &= (1 << index) ^ (-1);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public final boolean get(int index) {
            while (index >= 64) {
                this.ensureNext();
                this = this.next;
                index -= 64;
            }
            return (this.mData & (1 << index)) != 0;
        }

        final void insert(int index, boolean value) {
            while (true) {
                if (index >= 64) {
                    this.ensureNext();
                    this = this.next;
                    index -= 64;
                } else {
                    boolean lastBit = (this.mData & Long.MIN_VALUE) != 0;
                    long mask = (1 << index) - 1;
                    long before = this.mData & mask;
                    long after = (this.mData & ((-1) ^ mask)) << 1;
                    this.mData = before | after;
                    if (value) {
                        this.set(index);
                    } else {
                        this.clear(index);
                    }
                    if (lastBit || this.next != null) {
                        this.ensureNext();
                        this = this.next;
                        index = 0;
                        value = lastBit;
                    } else {
                        return;
                    }
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public final boolean remove(int index) {
            while (index >= 64) {
                this.ensureNext();
                this = this.next;
                index -= 64;
            }
            long mask = 1 << index;
            boolean value = (this.mData & mask) != 0;
            this.mData &= (-1) ^ mask;
            long mask2 = mask - 1;
            long before = this.mData & mask2;
            long after = Long.rotateRight(this.mData & ((-1) ^ mask2), 1);
            this.mData = before | after;
            if (this.next != null) {
                if (this.next.get(0)) {
                    this.set(63);
                }
                this.next.remove(0);
            }
            return value;
        }

        final int countOnesBefore(int index) {
            if (this.next == null) {
                if (index >= 64) {
                    return Long.bitCount(this.mData);
                }
                return Long.bitCount(this.mData & ((1 << index) - 1));
            }
            if (index < 64) {
                return Long.bitCount(this.mData & ((1 << index) - 1));
            }
            return this.next.countOnesBefore(index - 64) + Long.bitCount(this.mData);
        }

        public final String toString() {
            return this.next == null ? Long.toBinaryString(this.mData) : this.next.toString() + "xx" + Long.toBinaryString(this.mData);
        }
    }
}
