package android.support.v4.content;

import android.content.Context;
import android.support.v4.util.DebugUtils;
import java.io.FileDescriptor;
import java.io.PrintWriter;

/* loaded from: classes.dex */
public class Loader<D> {
    Context mContext;
    public int mId;
    public OnLoadCompleteListener<D> mListener;
    public OnLoadCanceledListener<D> mOnLoadCanceledListener;
    public boolean mStarted = false;
    public boolean mAbandoned = false;
    public boolean mReset = true;
    public boolean mContentChanged = false;
    public boolean mProcessingChange = false;

    /* loaded from: classes.dex */
    public interface OnLoadCanceledListener<D> {
        void onLoadCanceled$5dda1f52();
    }

    /* loaded from: classes.dex */
    public interface OnLoadCompleteListener<D> {
        void onLoadComplete(Loader<D> loader, D d);
    }

    public Loader(Context context) {
        this.mContext = context.getApplicationContext();
    }

    public final void unregisterListener(OnLoadCompleteListener<D> listener) {
        if (this.mListener == null) {
            throw new IllegalStateException("No listener register");
        }
        if (this.mListener != listener) {
            throw new IllegalArgumentException("Attempting to unregister the wrong listener");
        }
        this.mListener = null;
    }

    public final void unregisterOnLoadCanceledListener(OnLoadCanceledListener<D> listener) {
        if (this.mOnLoadCanceledListener == null) {
            throw new IllegalStateException("No listener register");
        }
        if (this.mOnLoadCanceledListener != listener) {
            throw new IllegalArgumentException("Attempting to unregister the wrong listener");
        }
        this.mOnLoadCanceledListener = null;
    }

    public void onStartLoading() {
    }

    public final boolean cancelLoad() {
        return onCancelLoad();
    }

    protected boolean onCancelLoad() {
        return false;
    }

    public final void forceLoad() {
        onForceLoad();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onForceLoad() {
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(64);
        DebugUtils.buildShortClassTag(this, sb);
        sb.append(" id=");
        sb.append(this.mId);
        sb.append("}");
        return sb.toString();
    }

    public void dump(String prefix, FileDescriptor fd, PrintWriter writer, String[] args) {
        writer.print(prefix);
        writer.print("mId=");
        writer.print(this.mId);
        writer.print(" mListener=");
        writer.println(this.mListener);
        if (this.mStarted || this.mContentChanged || this.mProcessingChange) {
            writer.print(prefix);
            writer.print("mStarted=");
            writer.print(this.mStarted);
            writer.print(" mContentChanged=");
            writer.print(this.mContentChanged);
            writer.print(" mProcessingChange=");
            writer.println(this.mProcessingChange);
        }
        if (this.mAbandoned || this.mReset) {
            writer.print(prefix);
            writer.print("mAbandoned=");
            writer.print(this.mAbandoned);
            writer.print(" mReset=");
            writer.println(this.mReset);
        }
    }
}
