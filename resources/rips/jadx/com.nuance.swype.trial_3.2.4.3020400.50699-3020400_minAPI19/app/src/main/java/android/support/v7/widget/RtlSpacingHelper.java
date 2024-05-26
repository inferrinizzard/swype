package android.support.v7.widget;

import com.nuance.connect.common.Integers;

/* loaded from: classes.dex */
final class RtlSpacingHelper {
    int mLeft = 0;
    int mRight = 0;
    int mStart = Integers.STATUS_SUCCESS;
    int mEnd = Integers.STATUS_SUCCESS;
    int mExplicitLeft = 0;
    int mExplicitRight = 0;
    boolean mIsRtl = false;
    boolean mIsRelative = false;

    public final void setRelative(int start, int end) {
        this.mStart = start;
        this.mEnd = end;
        this.mIsRelative = true;
        if (this.mIsRtl) {
            if (end != Integer.MIN_VALUE) {
                this.mLeft = end;
            }
            if (start != Integer.MIN_VALUE) {
                this.mRight = start;
                return;
            }
            return;
        }
        if (start != Integer.MIN_VALUE) {
            this.mLeft = start;
        }
        if (end != Integer.MIN_VALUE) {
            this.mRight = end;
        }
    }

    public final void setAbsolute(int left, int right) {
        this.mIsRelative = false;
        if (left != Integer.MIN_VALUE) {
            this.mExplicitLeft = left;
            this.mLeft = left;
        }
        if (right != Integer.MIN_VALUE) {
            this.mExplicitRight = right;
            this.mRight = right;
        }
    }
}
