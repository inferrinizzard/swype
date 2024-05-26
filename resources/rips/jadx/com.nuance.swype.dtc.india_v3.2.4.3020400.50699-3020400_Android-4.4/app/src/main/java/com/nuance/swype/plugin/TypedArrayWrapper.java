package com.nuance.swype.plugin;

import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;
import android.util.TypedValue;

/* loaded from: classes.dex */
public final class TypedArrayWrapper {
    public final TypedArray delegateTypedArray;
    public SparseArray<TypedValue> tValsStyledByApk;

    public TypedArrayWrapper(TypedArray innerTypedArray, SparseArray<TypedValue> innerArray) {
        this.delegateTypedArray = innerTypedArray;
        this.tValsStyledByApk = innerArray;
    }

    public final int getIndex(int at) {
        return this.delegateTypedArray.getIndex(at);
    }

    public final int getResourceId(int index, int defValue) {
        return this.delegateTypedArray.getResourceId(index, defValue);
    }

    public final boolean getBoolean(int index, boolean defValue) {
        TypedValue tVal;
        if (this.tValsStyledByApk == null || (tVal = this.tValsStyledByApk.get(index)) == null) {
            return this.delegateTypedArray.getBoolean(index, defValue);
        }
        ThemeLoader.getInstance();
        return ThemeLoader.getThemedBoolean(tVal);
    }

    public final int getInt(int index, int defValue) {
        return this.delegateTypedArray.getInt(index, defValue);
    }

    public final float getFloat(int index, float defValue) {
        return this.delegateTypedArray.getFloat(index, defValue);
    }

    public final int getColor(int index, int defValue) {
        TypedValue tVal;
        if (this.tValsStyledByApk == null || (tVal = this.tValsStyledByApk.get(index)) == null) {
            return this.delegateTypedArray.getColor(index, defValue);
        }
        ThemeLoader.getInstance();
        return ThemeLoader.getThemedColor(tVal);
    }

    public final float getDimension$255e742(int index) {
        TypedValue tVal;
        if (this.tValsStyledByApk == null || (tVal = this.tValsStyledByApk.get(index)) == null) {
            return this.delegateTypedArray.getDimension(index, 0.0f);
        }
        ThemeLoader.getInstance();
        return ThemeLoader.getThemedDimension(tVal);
    }

    public final int getDimensionPixelSize(int index, int defValue) {
        TypedValue tVal;
        if (this.tValsStyledByApk == null || (tVal = this.tValsStyledByApk.get(index)) == null) {
            return this.delegateTypedArray.getDimensionPixelSize(index, defValue);
        }
        ThemeLoader.getInstance();
        return (int) (ThemeLoader.getThemedDimension(tVal) + 0.5f);
    }

    public final Drawable getDrawable(int index) {
        TypedValue tVal;
        if (this.tValsStyledByApk == null || (tVal = this.tValsStyledByApk.get(index)) == null) {
            return this.delegateTypedArray.getDrawable(index);
        }
        ThemeLoader.getInstance();
        return ThemeLoader.getThemedDrawable(tVal);
    }

    public final boolean hasValue(int index) {
        if (this.tValsStyledByApk == null || this.tValsStyledByApk.get(index) == null) {
            return this.delegateTypedArray.hasValue(index);
        }
        return true;
    }

    public final void recycle() {
        if (this.tValsStyledByApk != null) {
            this.tValsStyledByApk.clear();
            this.tValsStyledByApk = null;
        }
        this.delegateTypedArray.recycle();
    }

    public final String toString() {
        return this.delegateTypedArray.toString();
    }
}
