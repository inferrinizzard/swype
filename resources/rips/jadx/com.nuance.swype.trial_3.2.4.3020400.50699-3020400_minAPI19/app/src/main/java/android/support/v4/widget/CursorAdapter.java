package android.support.v4.widget;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.os.Handler;
import android.support.v4.widget.CursorFilter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.FilterQueryProvider;
import android.widget.Filterable;

/* loaded from: classes.dex */
public abstract class CursorAdapter extends BaseAdapter implements CursorFilter.CursorFilterClient, Filterable {
    public Context mContext;
    protected CursorFilter mCursorFilter;
    protected FilterQueryProvider mFilterQueryProvider;
    protected boolean mAutoRequery = true;
    public Cursor mCursor = null;
    protected boolean mDataValid = false;
    protected int mRowIDColumn = -1;
    protected ChangeObserver mChangeObserver = new ChangeObserver();
    protected DataSetObserver mDataSetObserver = new MyDataSetObserver(this, 0);

    public abstract void bindView$4693bf34(View view, Cursor cursor);

    public abstract View newView(Context context, Cursor cursor, ViewGroup viewGroup);

    public CursorAdapter(Context context) {
        this.mContext = context;
    }

    @Override // android.support.v4.widget.CursorFilter.CursorFilterClient
    public final Cursor getCursor() {
        return this.mCursor;
    }

    @Override // android.widget.Adapter
    public int getCount() {
        if (!this.mDataValid || this.mCursor == null) {
            return 0;
        }
        return this.mCursor.getCount();
    }

    @Override // android.widget.Adapter
    public Object getItem(int position) {
        if (!this.mDataValid || this.mCursor == null) {
            return null;
        }
        this.mCursor.moveToPosition(position);
        return this.mCursor;
    }

    @Override // android.widget.Adapter
    public long getItemId(int position) {
        if (this.mDataValid && this.mCursor != null && this.mCursor.moveToPosition(position)) {
            return this.mCursor.getLong(this.mRowIDColumn);
        }
        return 0L;
    }

    @Override // android.widget.BaseAdapter, android.widget.Adapter
    public boolean hasStableIds() {
        return true;
    }

    @Override // android.widget.Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        View v;
        if (!this.mDataValid) {
            throw new IllegalStateException("this should only be called when the cursor is valid");
        }
        if (!this.mCursor.moveToPosition(position)) {
            throw new IllegalStateException("couldn't move cursor to position " + position);
        }
        if (convertView == null) {
            v = newView(this.mContext, this.mCursor, parent);
        } else {
            v = convertView;
        }
        bindView$4693bf34(v, this.mCursor);
        return v;
    }

    @Override // android.widget.BaseAdapter, android.widget.SpinnerAdapter
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View v;
        if (this.mDataValid) {
            this.mCursor.moveToPosition(position);
            if (convertView == null) {
                v = newDropDownView(this.mContext, this.mCursor, parent);
            } else {
                v = convertView;
            }
            bindView$4693bf34(v, this.mCursor);
            return v;
        }
        return null;
    }

    public View newDropDownView(Context context, Cursor cursor, ViewGroup parent) {
        return newView(context, cursor, parent);
    }

    @Override // android.support.v4.widget.CursorFilter.CursorFilterClient
    public CharSequence convertToString(Cursor cursor) {
        return cursor == null ? "" : cursor.toString();
    }

    @Override // android.support.v4.widget.CursorFilter.CursorFilterClient
    public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
        return this.mFilterQueryProvider != null ? this.mFilterQueryProvider.runQuery(constraint) : this.mCursor;
    }

    @Override // android.widget.Filterable
    public Filter getFilter() {
        if (this.mCursorFilter == null) {
            this.mCursorFilter = new CursorFilter(this);
        }
        return this.mCursorFilter;
    }

    protected final void onContentChanged() {
        if (this.mAutoRequery && this.mCursor != null && !this.mCursor.isClosed()) {
            this.mDataValid = this.mCursor.requery();
        }
    }

    /* loaded from: classes.dex */
    private class ChangeObserver extends ContentObserver {
        public ChangeObserver() {
            super(new Handler());
        }

        @Override // android.database.ContentObserver
        public final boolean deliverSelfNotifications() {
            return true;
        }

        @Override // android.database.ContentObserver
        public final void onChange(boolean selfChange) {
            CursorAdapter.this.onContentChanged();
        }
    }

    /* loaded from: classes.dex */
    private class MyDataSetObserver extends DataSetObserver {
        private MyDataSetObserver() {
        }

        /* synthetic */ MyDataSetObserver(CursorAdapter x0, byte b) {
            this();
        }

        @Override // android.database.DataSetObserver
        public final void onChanged() {
            CursorAdapter.this.mDataValid = true;
            CursorAdapter.this.notifyDataSetChanged();
        }

        @Override // android.database.DataSetObserver
        public final void onInvalidated() {
            CursorAdapter.this.mDataValid = false;
            CursorAdapter.this.notifyDataSetInvalidated();
        }
    }

    @Override // android.support.v4.widget.CursorFilter.CursorFilterClient
    public void changeCursor(Cursor cursor) {
        Cursor old;
        if (cursor == this.mCursor) {
            old = null;
        } else {
            old = this.mCursor;
            if (old != null) {
                if (this.mChangeObserver != null) {
                    old.unregisterContentObserver(this.mChangeObserver);
                }
                if (this.mDataSetObserver != null) {
                    old.unregisterDataSetObserver(this.mDataSetObserver);
                }
            }
            this.mCursor = cursor;
            if (cursor != null) {
                if (this.mChangeObserver != null) {
                    cursor.registerContentObserver(this.mChangeObserver);
                }
                if (this.mDataSetObserver != null) {
                    cursor.registerDataSetObserver(this.mDataSetObserver);
                }
                this.mRowIDColumn = cursor.getColumnIndexOrThrow("_id");
                this.mDataValid = true;
                notifyDataSetChanged();
            } else {
                this.mRowIDColumn = -1;
                this.mDataValid = false;
                notifyDataSetInvalidated();
            }
        }
        if (old != null) {
            old.close();
        }
    }
}
