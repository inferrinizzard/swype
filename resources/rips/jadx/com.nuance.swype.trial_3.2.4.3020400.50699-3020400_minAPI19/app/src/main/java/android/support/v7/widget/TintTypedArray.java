package android.support.v7.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v7.content.res.AppCompatResources;
import android.util.AttributeSet;

/* loaded from: classes.dex */
public final class TintTypedArray {
    private final Context mContext;
    public final TypedArray mWrapped;

    public static TintTypedArray obtainStyledAttributes(Context context, AttributeSet set, int[] attrs) {
        return new TintTypedArray(context, context.obtainStyledAttributes(set, attrs));
    }

    public static TintTypedArray obtainStyledAttributes(Context context, AttributeSet set, int[] attrs, int defStyleAttr, int defStyleRes) {
        return new TintTypedArray(context, context.obtainStyledAttributes(set, attrs, defStyleAttr, defStyleRes));
    }

    public static TintTypedArray obtainStyledAttributes(Context context, int resid, int[] attrs) {
        return new TintTypedArray(context, context.obtainStyledAttributes(resid, attrs));
    }

    private TintTypedArray(Context context, TypedArray array) {
        this.mContext = context;
        this.mWrapped = array;
    }

    public final Drawable getDrawable(int index) {
        int resourceId;
        return (!this.mWrapped.hasValue(index) || (resourceId = this.mWrapped.getResourceId(index, 0)) == 0) ? this.mWrapped.getDrawable(index) : AppCompatDrawableManager.get().getDrawable(this.mContext, resourceId, false);
    }

    public final Drawable getDrawableIfKnown(int index) {
        int resourceId;
        if (!this.mWrapped.hasValue(index) || (resourceId = this.mWrapped.getResourceId(index, 0)) == 0) {
            return null;
        }
        return AppCompatDrawableManager.get().getDrawable(this.mContext, resourceId, true);
    }

    public final CharSequence getText(int index) {
        return this.mWrapped.getText(index);
    }

    public final boolean getBoolean(int index, boolean defValue) {
        return this.mWrapped.getBoolean(index, defValue);
    }

    public final int getInt(int index, int defValue) {
        return this.mWrapped.getInt(index, defValue);
    }

    public final int getColor(int index, int defValue) {
        return this.mWrapped.getColor(index, defValue);
    }

    public final ColorStateList getColorStateList(int index) {
        int resourceId;
        ColorStateList value;
        return (!this.mWrapped.hasValue(index) || (resourceId = this.mWrapped.getResourceId(index, 0)) == 0 || (value = AppCompatResources.getColorStateList(this.mContext, resourceId)) == null) ? this.mWrapped.getColorStateList(index) : value;
    }

    public final int getInteger(int index, int defValue) {
        return this.mWrapped.getInteger(index, defValue);
    }

    public final int getDimensionPixelOffset(int index, int defValue) {
        return this.mWrapped.getDimensionPixelOffset(index, defValue);
    }

    public final int getDimensionPixelSize(int index, int defValue) {
        return this.mWrapped.getDimensionPixelSize(index, defValue);
    }

    public final int getLayoutDimension(int index, int defValue) {
        return this.mWrapped.getLayoutDimension(index, defValue);
    }

    public final int getResourceId(int index, int defValue) {
        return this.mWrapped.getResourceId(index, defValue);
    }

    public final boolean hasValue(int index) {
        return this.mWrapped.hasValue(index);
    }
}
