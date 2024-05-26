package android.support.graphics.drawable;

import android.annotation.TargetApi;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.os.Build;
import android.support.graphics.drawable.PathParser;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.util.ArrayMap;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Xml;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

@TargetApi(21)
/* loaded from: classes.dex */
public final class VectorDrawableCompat extends VectorDrawableCommon {
    static final PorterDuff.Mode DEFAULT_TINT_MODE = PorterDuff.Mode.SRC_IN;
    boolean mAllowCaching;
    private Drawable.ConstantState mCachedConstantStateDelegate;
    private ColorFilter mColorFilter;
    private boolean mMutated;
    private PorterDuffColorFilter mTintFilter;
    private final Rect mTmpBounds;
    private final float[] mTmpFloats;
    private final Matrix mTmpMatrix;
    VectorDrawableCompatState mVectorState;

    @Override // android.support.graphics.drawable.VectorDrawableCommon, android.graphics.drawable.Drawable
    public final /* bridge */ /* synthetic */ void applyTheme(Resources.Theme theme) {
        super.applyTheme(theme);
    }

    @Override // android.support.graphics.drawable.VectorDrawableCommon, android.graphics.drawable.Drawable
    public final /* bridge */ /* synthetic */ void clearColorFilter() {
        super.clearColorFilter();
    }

    @Override // android.support.graphics.drawable.VectorDrawableCommon, android.graphics.drawable.Drawable
    public final /* bridge */ /* synthetic */ ColorFilter getColorFilter() {
        return super.getColorFilter();
    }

    @Override // android.support.graphics.drawable.VectorDrawableCommon, android.graphics.drawable.Drawable
    public final /* bridge */ /* synthetic */ Drawable getCurrent() {
        return super.getCurrent();
    }

    @Override // android.support.graphics.drawable.VectorDrawableCommon, android.graphics.drawable.Drawable
    public final /* bridge */ /* synthetic */ int getLayoutDirection() {
        return super.getLayoutDirection();
    }

    @Override // android.support.graphics.drawable.VectorDrawableCommon, android.graphics.drawable.Drawable
    public final /* bridge */ /* synthetic */ int getMinimumHeight() {
        return super.getMinimumHeight();
    }

    @Override // android.support.graphics.drawable.VectorDrawableCommon, android.graphics.drawable.Drawable
    public final /* bridge */ /* synthetic */ int getMinimumWidth() {
        return super.getMinimumWidth();
    }

    @Override // android.support.graphics.drawable.VectorDrawableCommon, android.graphics.drawable.Drawable
    public final /* bridge */ /* synthetic */ boolean getPadding(Rect rect) {
        return super.getPadding(rect);
    }

    @Override // android.support.graphics.drawable.VectorDrawableCommon, android.graphics.drawable.Drawable
    public final /* bridge */ /* synthetic */ int[] getState() {
        return super.getState();
    }

    @Override // android.support.graphics.drawable.VectorDrawableCommon, android.graphics.drawable.Drawable
    public final /* bridge */ /* synthetic */ Region getTransparentRegion() {
        return super.getTransparentRegion();
    }

    @Override // android.support.graphics.drawable.VectorDrawableCommon, android.graphics.drawable.Drawable
    public final /* bridge */ /* synthetic */ boolean isAutoMirrored() {
        return super.isAutoMirrored();
    }

    @Override // android.support.graphics.drawable.VectorDrawableCommon, android.graphics.drawable.Drawable
    public final /* bridge */ /* synthetic */ void jumpToCurrentState() {
        super.jumpToCurrentState();
    }

    @Override // android.support.graphics.drawable.VectorDrawableCommon, android.graphics.drawable.Drawable
    public final /* bridge */ /* synthetic */ void setAutoMirrored(boolean z) {
        super.setAutoMirrored(z);
    }

    @Override // android.support.graphics.drawable.VectorDrawableCommon, android.graphics.drawable.Drawable
    public final /* bridge */ /* synthetic */ void setChangingConfigurations(int i) {
        super.setChangingConfigurations(i);
    }

    @Override // android.support.graphics.drawable.VectorDrawableCommon, android.graphics.drawable.Drawable
    public final /* bridge */ /* synthetic */ void setColorFilter(int i, PorterDuff.Mode mode) {
        super.setColorFilter(i, mode);
    }

    @Override // android.support.graphics.drawable.VectorDrawableCommon, android.graphics.drawable.Drawable
    public final /* bridge */ /* synthetic */ void setFilterBitmap(boolean z) {
        super.setFilterBitmap(z);
    }

    @Override // android.support.graphics.drawable.VectorDrawableCommon, android.graphics.drawable.Drawable
    public final /* bridge */ /* synthetic */ void setHotspot(float f, float f2) {
        super.setHotspot(f, f2);
    }

    @Override // android.support.graphics.drawable.VectorDrawableCommon, android.graphics.drawable.Drawable
    public final /* bridge */ /* synthetic */ void setHotspotBounds(int i, int i2, int i3, int i4) {
        super.setHotspotBounds(i, i2, i3, i4);
    }

    @Override // android.support.graphics.drawable.VectorDrawableCommon, android.graphics.drawable.Drawable
    public final /* bridge */ /* synthetic */ boolean setState(int[] iArr) {
        return super.setState(iArr);
    }

    /* synthetic */ VectorDrawableCompat(byte b) {
        this();
    }

    /* synthetic */ VectorDrawableCompat(VectorDrawableCompatState x0, byte b) {
        this(x0);
    }

    private VectorDrawableCompat() {
        this.mAllowCaching = true;
        this.mTmpFloats = new float[9];
        this.mTmpMatrix = new Matrix();
        this.mTmpBounds = new Rect();
        this.mVectorState = new VectorDrawableCompatState();
    }

    private VectorDrawableCompat(VectorDrawableCompatState state) {
        this.mAllowCaching = true;
        this.mTmpFloats = new float[9];
        this.mTmpMatrix = new Matrix();
        this.mTmpBounds = new Rect();
        this.mVectorState = state;
        this.mTintFilter = updateTintFilter$5c32a288(state.mTint, state.mTintMode);
    }

    @Override // android.graphics.drawable.Drawable
    public final Drawable mutate() {
        if (this.mDelegateDrawable != null) {
            this.mDelegateDrawable.mutate();
        } else if (!this.mMutated && super.mutate() == this) {
            this.mVectorState = new VectorDrawableCompatState(this.mVectorState);
            this.mMutated = true;
        }
        return this;
    }

    @Override // android.graphics.drawable.Drawable
    public final Drawable.ConstantState getConstantState() {
        if (this.mDelegateDrawable != null) {
            return new VectorDrawableDelegateState(this.mDelegateDrawable.getConstantState());
        }
        this.mVectorState.mChangingConfigurations = getChangingConfigurations();
        return this.mVectorState;
    }

    /* JADX WARN: Code restructure failed: missing block: B:27:0x00b4, code lost:            if (r8 == false) goto L29;     */
    @Override // android.graphics.drawable.Drawable
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final void draw(android.graphics.Canvas r13) {
        /*
            Method dump skipped, instructions count: 339
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.graphics.drawable.VectorDrawableCompat.draw(android.graphics.Canvas):void");
    }

    @Override // android.graphics.drawable.Drawable
    public final int getAlpha() {
        if (this.mDelegateDrawable != null) {
            return DrawableCompat.getAlpha(this.mDelegateDrawable);
        }
        return this.mVectorState.mVPathRenderer.mRootAlpha;
    }

    @Override // android.graphics.drawable.Drawable
    public final void setAlpha(int alpha) {
        if (this.mDelegateDrawable != null) {
            this.mDelegateDrawable.setAlpha(alpha);
        } else {
            if (this.mVectorState.mVPathRenderer.mRootAlpha == alpha) {
                return;
            }
            this.mVectorState.mVPathRenderer.mRootAlpha = alpha;
            invalidateSelf();
        }
    }

    @Override // android.graphics.drawable.Drawable
    public final void setColorFilter(ColorFilter colorFilter) {
        if (this.mDelegateDrawable != null) {
            this.mDelegateDrawable.setColorFilter(colorFilter);
        } else {
            this.mColorFilter = colorFilter;
            invalidateSelf();
        }
    }

    private PorterDuffColorFilter updateTintFilter$5c32a288(ColorStateList tint, PorterDuff.Mode tintMode) {
        if (tint == null || tintMode == null) {
            return null;
        }
        int color = tint.getColorForState(getState(), 0);
        return new PorterDuffColorFilter(color, tintMode);
    }

    @Override // android.graphics.drawable.Drawable, android.support.v4.graphics.drawable.TintAwareDrawable
    public final void setTint(int tint) {
        if (this.mDelegateDrawable != null) {
            DrawableCompat.setTint(this.mDelegateDrawable, tint);
        } else {
            setTintList(ColorStateList.valueOf(tint));
        }
    }

    @Override // android.graphics.drawable.Drawable, android.support.v4.graphics.drawable.TintAwareDrawable
    public final void setTintList(ColorStateList tint) {
        if (this.mDelegateDrawable != null) {
            DrawableCompat.setTintList(this.mDelegateDrawable, tint);
            return;
        }
        VectorDrawableCompatState state = this.mVectorState;
        if (state.mTint != tint) {
            state.mTint = tint;
            this.mTintFilter = updateTintFilter$5c32a288(tint, state.mTintMode);
            invalidateSelf();
        }
    }

    @Override // android.graphics.drawable.Drawable, android.support.v4.graphics.drawable.TintAwareDrawable
    public final void setTintMode(PorterDuff.Mode tintMode) {
        if (this.mDelegateDrawable != null) {
            DrawableCompat.setTintMode(this.mDelegateDrawable, tintMode);
            return;
        }
        VectorDrawableCompatState state = this.mVectorState;
        if (state.mTintMode != tintMode) {
            state.mTintMode = tintMode;
            this.mTintFilter = updateTintFilter$5c32a288(state.mTint, tintMode);
            invalidateSelf();
        }
    }

    @Override // android.graphics.drawable.Drawable
    public final boolean isStateful() {
        if (this.mDelegateDrawable != null) {
            return this.mDelegateDrawable.isStateful();
        }
        return super.isStateful() || !(this.mVectorState == null || this.mVectorState.mTint == null || !this.mVectorState.mTint.isStateful());
    }

    @Override // android.graphics.drawable.Drawable
    protected final boolean onStateChange(int[] stateSet) {
        if (this.mDelegateDrawable != null) {
            return this.mDelegateDrawable.setState(stateSet);
        }
        VectorDrawableCompatState state = this.mVectorState;
        if (state.mTint != null && state.mTintMode != null) {
            this.mTintFilter = updateTintFilter$5c32a288(state.mTint, state.mTintMode);
            invalidateSelf();
            return true;
        }
        return false;
    }

    @Override // android.graphics.drawable.Drawable
    public final int getOpacity() {
        if (this.mDelegateDrawable != null) {
            return this.mDelegateDrawable.getOpacity();
        }
        return -3;
    }

    @Override // android.graphics.drawable.Drawable
    public final int getIntrinsicWidth() {
        return this.mDelegateDrawable != null ? this.mDelegateDrawable.getIntrinsicWidth() : (int) this.mVectorState.mVPathRenderer.mBaseWidth;
    }

    @Override // android.graphics.drawable.Drawable
    public final int getIntrinsicHeight() {
        return this.mDelegateDrawable != null ? this.mDelegateDrawable.getIntrinsicHeight() : (int) this.mVectorState.mVPathRenderer.mBaseHeight;
    }

    @Override // android.graphics.drawable.Drawable
    public final boolean canApplyTheme() {
        if (this.mDelegateDrawable != null) {
            DrawableCompat.canApplyTheme(this.mDelegateDrawable);
            return false;
        }
        return false;
    }

    public static VectorDrawableCompat create(Resources res, int resId, Resources.Theme theme) {
        int type;
        Drawable drawable;
        if (Build.VERSION.SDK_INT >= 23) {
            VectorDrawableCompat drawable2 = new VectorDrawableCompat();
            if (Build.VERSION.SDK_INT < 21) {
                drawable = res.getDrawable(resId);
            } else {
                drawable = res.getDrawable(resId, theme);
            }
            drawable2.mDelegateDrawable = drawable;
            drawable2.mCachedConstantStateDelegate = new VectorDrawableDelegateState(drawable2.mDelegateDrawable.getConstantState());
            return drawable2;
        }
        try {
            XmlPullParser parser = res.getXml(resId);
            AttributeSet attrs = Xml.asAttributeSet(parser);
            do {
                type = parser.next();
                if (type == 2) {
                    break;
                }
            } while (type != 1);
            if (type != 2) {
                throw new XmlPullParserException("No start tag found");
            }
            return createFromXmlInner(res, parser, attrs, theme);
        } catch (IOException e) {
            Log.e("VectorDrawableCompat", "parser error", e);
            return null;
        } catch (XmlPullParserException e2) {
            Log.e("VectorDrawableCompat", "parser error", e2);
            return null;
        }
    }

    public static VectorDrawableCompat createFromXmlInner(Resources r, XmlPullParser parser, AttributeSet attrs, Resources.Theme theme) throws XmlPullParserException, IOException {
        VectorDrawableCompat drawable = new VectorDrawableCompat();
        drawable.inflate(r, parser, attrs, theme);
        return drawable;
    }

    @Override // android.graphics.drawable.Drawable
    public final void inflate(Resources res, XmlPullParser parser, AttributeSet attrs) throws XmlPullParserException, IOException {
        if (this.mDelegateDrawable != null) {
            this.mDelegateDrawable.inflate(res, parser, attrs);
        } else {
            inflate(res, parser, attrs, null);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public final void inflate(Resources res, XmlPullParser parser, AttributeSet attrs, Resources.Theme theme) throws XmlPullParserException, IOException {
        if (this.mDelegateDrawable != null) {
            DrawableCompat.inflate(this.mDelegateDrawable, res, parser, attrs, theme);
            return;
        }
        VectorDrawableCompatState state = this.mVectorState;
        VPathRenderer pathRenderer = new VPathRenderer();
        state.mVPathRenderer = pathRenderer;
        TypedArray a = obtainAttributes(res, theme, attrs, AndroidResources.styleable_VectorDrawableTypeArray);
        VectorDrawableCompatState vectorDrawableCompatState = this.mVectorState;
        VPathRenderer vPathRenderer = vectorDrawableCompatState.mVPathRenderer;
        int namedInt$7903c789 = TypedArrayUtils.getNamedInt$7903c789(a, parser, "tintMode", 6);
        PorterDuff.Mode mode = PorterDuff.Mode.SRC_IN;
        switch (namedInt$7903c789) {
            case 3:
                mode = PorterDuff.Mode.SRC_OVER;
                break;
            case 5:
                mode = PorterDuff.Mode.SRC_IN;
                break;
            case 9:
                mode = PorterDuff.Mode.SRC_ATOP;
                break;
            case 14:
                mode = PorterDuff.Mode.MULTIPLY;
                break;
            case 15:
                mode = PorterDuff.Mode.SCREEN;
                break;
            case 16:
                mode = PorterDuff.Mode.ADD;
                break;
        }
        vectorDrawableCompatState.mTintMode = mode;
        ColorStateList colorStateList = a.getColorStateList(1);
        if (colorStateList != null) {
            vectorDrawableCompatState.mTint = colorStateList;
        }
        boolean z = vectorDrawableCompatState.mAutoMirrored;
        if (TypedArrayUtils.hasAttribute(parser, "autoMirrored")) {
            z = a.getBoolean(5, z);
        }
        vectorDrawableCompatState.mAutoMirrored = z;
        vPathRenderer.mViewportWidth = TypedArrayUtils.getNamedFloat(a, parser, "viewportWidth", 7, vPathRenderer.mViewportWidth);
        vPathRenderer.mViewportHeight = TypedArrayUtils.getNamedFloat(a, parser, "viewportHeight", 8, vPathRenderer.mViewportHeight);
        if (vPathRenderer.mViewportWidth <= 0.0f) {
            throw new XmlPullParserException(a.getPositionDescription() + "<vector> tag requires viewportWidth > 0");
        }
        if (vPathRenderer.mViewportHeight <= 0.0f) {
            throw new XmlPullParserException(a.getPositionDescription() + "<vector> tag requires viewportHeight > 0");
        }
        vPathRenderer.mBaseWidth = a.getDimension(3, vPathRenderer.mBaseWidth);
        vPathRenderer.mBaseHeight = a.getDimension(2, vPathRenderer.mBaseHeight);
        if (vPathRenderer.mBaseWidth <= 0.0f) {
            throw new XmlPullParserException(a.getPositionDescription() + "<vector> tag requires width > 0");
        }
        if (vPathRenderer.mBaseHeight <= 0.0f) {
            throw new XmlPullParserException(a.getPositionDescription() + "<vector> tag requires height > 0");
        }
        vPathRenderer.mRootAlpha = (int) (TypedArrayUtils.getNamedFloat(a, parser, "alpha", 4, vPathRenderer.mRootAlpha / 255.0f) * 255.0f);
        String string = a.getString(0);
        if (string != null) {
            vPathRenderer.mRootName = string;
            vPathRenderer.mVGTargetsMap.put(string, vPathRenderer);
        }
        a.recycle();
        state.mChangingConfigurations = getChangingConfigurations();
        state.mCacheDirty = true;
        inflateInternal(res, parser, attrs, theme);
        this.mTintFilter = updateTintFilter$5c32a288(state.mTint, state.mTintMode);
    }

    private void inflateInternal(Resources res, XmlPullParser parser, AttributeSet attrs, Resources.Theme theme) throws XmlPullParserException, IOException {
        VectorDrawableCompatState state = this.mVectorState;
        VPathRenderer pathRenderer = state.mVPathRenderer;
        boolean noPathTag = true;
        Stack<VGroup> groupStack = new Stack<>();
        groupStack.push(pathRenderer.mRootGroup);
        int eventType = parser.getEventType();
        while (eventType != 1) {
            if (eventType == 2) {
                String tagName = parser.getName();
                VGroup currentGroup = groupStack.peek();
                if ("path".equals(tagName)) {
                    VFullPath path = new VFullPath();
                    TypedArray obtainAttributes = VectorDrawableCommon.obtainAttributes(res, theme, attrs, AndroidResources.styleable_VectorDrawablePath);
                    path.updateStateFromTypedArray(obtainAttributes, parser);
                    obtainAttributes.recycle();
                    currentGroup.mChildren.add(path);
                    if (path.mPathName != null) {
                        pathRenderer.mVGTargetsMap.put(path.mPathName, path);
                    }
                    noPathTag = false;
                    state.mChangingConfigurations |= path.mChangingConfigurations;
                } else if ("clip-path".equals(tagName)) {
                    VClipPath path2 = new VClipPath();
                    if (TypedArrayUtils.hasAttribute(parser, "pathData")) {
                        TypedArray obtainAttributes2 = VectorDrawableCommon.obtainAttributes(res, theme, attrs, AndroidResources.styleable_VectorDrawableClipPath);
                        path2.updateStateFromTypedArray(obtainAttributes2);
                        obtainAttributes2.recycle();
                    }
                    currentGroup.mChildren.add(path2);
                    if (path2.mPathName != null) {
                        pathRenderer.mVGTargetsMap.put(path2.mPathName, path2);
                    }
                    state.mChangingConfigurations |= path2.mChangingConfigurations;
                } else if ("group".equals(tagName)) {
                    VGroup newChildGroup = new VGroup();
                    TypedArray obtainAttributes3 = VectorDrawableCommon.obtainAttributes(res, theme, attrs, AndroidResources.styleable_VectorDrawableGroup);
                    newChildGroup.mThemeAttrs = null;
                    newChildGroup.mRotate = TypedArrayUtils.getNamedFloat(obtainAttributes3, parser, "rotation", 5, newChildGroup.mRotate);
                    newChildGroup.mPivotX = obtainAttributes3.getFloat(1, newChildGroup.mPivotX);
                    newChildGroup.mPivotY = obtainAttributes3.getFloat(2, newChildGroup.mPivotY);
                    newChildGroup.mScaleX = TypedArrayUtils.getNamedFloat(obtainAttributes3, parser, "scaleX", 3, newChildGroup.mScaleX);
                    newChildGroup.mScaleY = TypedArrayUtils.getNamedFloat(obtainAttributes3, parser, "scaleY", 4, newChildGroup.mScaleY);
                    newChildGroup.mTranslateX = TypedArrayUtils.getNamedFloat(obtainAttributes3, parser, "translateX", 6, newChildGroup.mTranslateX);
                    newChildGroup.mTranslateY = TypedArrayUtils.getNamedFloat(obtainAttributes3, parser, "translateY", 7, newChildGroup.mTranslateY);
                    String string = obtainAttributes3.getString(0);
                    if (string != null) {
                        newChildGroup.mGroupName = string;
                    }
                    newChildGroup.mLocalMatrix.reset();
                    newChildGroup.mLocalMatrix.postTranslate(-newChildGroup.mPivotX, -newChildGroup.mPivotY);
                    newChildGroup.mLocalMatrix.postScale(newChildGroup.mScaleX, newChildGroup.mScaleY);
                    newChildGroup.mLocalMatrix.postRotate(newChildGroup.mRotate, 0.0f, 0.0f);
                    newChildGroup.mLocalMatrix.postTranslate(newChildGroup.mTranslateX + newChildGroup.mPivotX, newChildGroup.mTranslateY + newChildGroup.mPivotY);
                    obtainAttributes3.recycle();
                    currentGroup.mChildren.add(newChildGroup);
                    groupStack.push(newChildGroup);
                    if (newChildGroup.mGroupName != null) {
                        pathRenderer.mVGTargetsMap.put(newChildGroup.mGroupName, newChildGroup);
                    }
                    state.mChangingConfigurations |= newChildGroup.mChangingConfigurations;
                }
            } else if (eventType == 3 && "group".equals(parser.getName())) {
                groupStack.pop();
            }
            eventType = parser.next();
        }
        if (noPathTag) {
            StringBuffer tag = new StringBuffer();
            if (tag.length() > 0) {
                tag.append(" or ");
            }
            tag.append("path");
            throw new XmlPullParserException("no " + ((Object) tag) + " defined");
        }
    }

    @Override // android.support.graphics.drawable.VectorDrawableCommon, android.graphics.drawable.Drawable
    protected final void onBoundsChange(Rect bounds) {
        if (this.mDelegateDrawable != null) {
            this.mDelegateDrawable.setBounds(bounds);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public final int getChangingConfigurations() {
        return this.mDelegateDrawable != null ? this.mDelegateDrawable.getChangingConfigurations() : super.getChangingConfigurations() | this.mVectorState.getChangingConfigurations();
    }

    @Override // android.graphics.drawable.Drawable
    public final void invalidateSelf() {
        if (this.mDelegateDrawable != null) {
            this.mDelegateDrawable.invalidateSelf();
        } else {
            super.invalidateSelf();
        }
    }

    @Override // android.graphics.drawable.Drawable
    public final void scheduleSelf(Runnable what, long when) {
        if (this.mDelegateDrawable != null) {
            this.mDelegateDrawable.scheduleSelf(what, when);
        } else {
            super.scheduleSelf(what, when);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public final boolean setVisible(boolean visible, boolean restart) {
        return this.mDelegateDrawable != null ? this.mDelegateDrawable.setVisible(visible, restart) : super.setVisible(visible, restart);
    }

    @Override // android.graphics.drawable.Drawable
    public final void unscheduleSelf(Runnable what) {
        if (this.mDelegateDrawable != null) {
            this.mDelegateDrawable.unscheduleSelf(what);
        } else {
            super.unscheduleSelf(what);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class VectorDrawableDelegateState extends Drawable.ConstantState {
        private final Drawable.ConstantState mDelegateState;

        public VectorDrawableDelegateState(Drawable.ConstantState state) {
            this.mDelegateState = state;
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public final Drawable newDrawable() {
            VectorDrawableCompat drawableCompat = new VectorDrawableCompat((byte) 0);
            drawableCompat.mDelegateDrawable = (VectorDrawable) this.mDelegateState.newDrawable();
            return drawableCompat;
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public final Drawable newDrawable(Resources res) {
            VectorDrawableCompat drawableCompat = new VectorDrawableCompat((byte) 0);
            drawableCompat.mDelegateDrawable = (VectorDrawable) this.mDelegateState.newDrawable(res);
            return drawableCompat;
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public final Drawable newDrawable(Resources res, Resources.Theme theme) {
            VectorDrawableCompat drawableCompat = new VectorDrawableCompat((byte) 0);
            drawableCompat.mDelegateDrawable = (VectorDrawable) this.mDelegateState.newDrawable(res, theme);
            return drawableCompat;
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public final boolean canApplyTheme() {
            return this.mDelegateState.canApplyTheme();
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public final int getChangingConfigurations() {
            return this.mDelegateState.getChangingConfigurations();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class VectorDrawableCompatState extends Drawable.ConstantState {
        boolean mAutoMirrored;
        boolean mCacheDirty;
        boolean mCachedAutoMirrored;
        Bitmap mCachedBitmap;
        int mCachedRootAlpha;
        ColorStateList mCachedTint;
        PorterDuff.Mode mCachedTintMode;
        int mChangingConfigurations;
        Paint mTempPaint;
        ColorStateList mTint;
        PorterDuff.Mode mTintMode;
        VPathRenderer mVPathRenderer;

        public VectorDrawableCompatState(VectorDrawableCompatState copy) {
            this.mTint = null;
            this.mTintMode = VectorDrawableCompat.DEFAULT_TINT_MODE;
            if (copy != null) {
                this.mChangingConfigurations = copy.mChangingConfigurations;
                this.mVPathRenderer = new VPathRenderer(copy.mVPathRenderer);
                if (copy.mVPathRenderer.mFillPaint != null) {
                    this.mVPathRenderer.mFillPaint = new Paint(copy.mVPathRenderer.mFillPaint);
                }
                if (copy.mVPathRenderer.mStrokePaint != null) {
                    this.mVPathRenderer.mStrokePaint = new Paint(copy.mVPathRenderer.mStrokePaint);
                }
                this.mTint = copy.mTint;
                this.mTintMode = copy.mTintMode;
                this.mAutoMirrored = copy.mAutoMirrored;
            }
        }

        public final void updateCachedBitmap(int width, int height) {
            this.mCachedBitmap.eraseColor(0);
            Canvas tmpCanvas = new Canvas(this.mCachedBitmap);
            this.mVPathRenderer.draw$65b72e48(tmpCanvas, width, height);
        }

        public VectorDrawableCompatState() {
            this.mTint = null;
            this.mTintMode = VectorDrawableCompat.DEFAULT_TINT_MODE;
            this.mVPathRenderer = new VPathRenderer();
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public final Drawable newDrawable() {
            return new VectorDrawableCompat(this, (byte) 0);
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public final Drawable newDrawable(Resources res) {
            return new VectorDrawableCompat(this, (byte) 0);
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public final int getChangingConfigurations() {
            return this.mChangingConfigurations;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class VPathRenderer {
        private static final Matrix IDENTITY_MATRIX = new Matrix();
        float mBaseHeight;
        float mBaseWidth;
        private int mChangingConfigurations;
        private Paint mFillPaint;
        private final Matrix mFinalPathMatrix;
        private final Path mPath;
        private PathMeasure mPathMeasure;
        private final Path mRenderPath;
        int mRootAlpha;
        private final VGroup mRootGroup;
        String mRootName;
        private Paint mStrokePaint;
        final ArrayMap<String, Object> mVGTargetsMap;
        float mViewportHeight;
        float mViewportWidth;

        public VPathRenderer() {
            this.mFinalPathMatrix = new Matrix();
            this.mBaseWidth = 0.0f;
            this.mBaseHeight = 0.0f;
            this.mViewportWidth = 0.0f;
            this.mViewportHeight = 0.0f;
            this.mRootAlpha = 255;
            this.mRootName = null;
            this.mVGTargetsMap = new ArrayMap<>();
            this.mRootGroup = new VGroup();
            this.mPath = new Path();
            this.mRenderPath = new Path();
        }

        public VPathRenderer(VPathRenderer copy) {
            this.mFinalPathMatrix = new Matrix();
            this.mBaseWidth = 0.0f;
            this.mBaseHeight = 0.0f;
            this.mViewportWidth = 0.0f;
            this.mViewportHeight = 0.0f;
            this.mRootAlpha = 255;
            this.mRootName = null;
            this.mVGTargetsMap = new ArrayMap<>();
            this.mRootGroup = new VGroup(copy.mRootGroup, this.mVGTargetsMap);
            this.mPath = new Path(copy.mPath);
            this.mRenderPath = new Path(copy.mRenderPath);
            this.mBaseWidth = copy.mBaseWidth;
            this.mBaseHeight = copy.mBaseHeight;
            this.mViewportWidth = copy.mViewportWidth;
            this.mViewportHeight = copy.mViewportHeight;
            this.mChangingConfigurations = copy.mChangingConfigurations;
            this.mRootAlpha = copy.mRootAlpha;
            this.mRootName = copy.mRootName;
            if (copy.mRootName != null) {
                this.mVGTargetsMap.put(copy.mRootName, this);
            }
        }

        public final void draw$65b72e48(Canvas canvas, int w, int h) {
            drawGroupTree(this.mRootGroup, IDENTITY_MATRIX, canvas, w, h, null);
        }

        private void drawGroupTree(VGroup currentGroup, Matrix currentMatrix, Canvas canvas, int w, int h, ColorFilter filter) {
            currentGroup.mStackedMatrix.set(currentMatrix);
            currentGroup.mStackedMatrix.preConcat(currentGroup.mLocalMatrix);
            for (int i = 0; i < currentGroup.mChildren.size(); i++) {
                Object child = currentGroup.mChildren.get(i);
                if (child instanceof VGroup) {
                    VGroup childGroup = (VGroup) child;
                    drawGroupTree(childGroup, currentGroup.mStackedMatrix, canvas, w, h, filter);
                } else if (child instanceof VPath) {
                    VPath childPath = (VPath) child;
                    float f = w / this.mViewportWidth;
                    float f2 = h / this.mViewportHeight;
                    float min = Math.min(f, f2);
                    Matrix matrix = currentGroup.mStackedMatrix;
                    this.mFinalPathMatrix.set(matrix);
                    this.mFinalPathMatrix.postScale(f, f2);
                    float[] fArr = {0.0f, 1.0f, 1.0f, 0.0f};
                    matrix.mapVectors(fArr);
                    float hypot = (float) Math.hypot(fArr[0], fArr[1]);
                    float hypot2 = (float) Math.hypot(fArr[2], fArr[3]);
                    float f3 = (fArr[3] * fArr[0]) - (fArr[1] * fArr[2]);
                    float max = Math.max(hypot, hypot2);
                    float f4 = 0.0f;
                    if (max > 0.0f) {
                        f4 = Math.abs(f3) / max;
                    }
                    if (f4 != 0.0f) {
                        childPath.toPath(this.mPath);
                        Path path = this.mPath;
                        this.mRenderPath.reset();
                        if (childPath.isClipPath()) {
                            this.mRenderPath.addPath(path, this.mFinalPathMatrix);
                            canvas.clipPath(this.mRenderPath, Region.Op.REPLACE);
                        } else {
                            VFullPath vFullPath = (VFullPath) childPath;
                            if (vFullPath.mTrimPathStart != 0.0f || vFullPath.mTrimPathEnd != 1.0f) {
                                float f5 = (vFullPath.mTrimPathStart + vFullPath.mTrimPathOffset) % 1.0f;
                                float f6 = (vFullPath.mTrimPathEnd + vFullPath.mTrimPathOffset) % 1.0f;
                                if (this.mPathMeasure == null) {
                                    this.mPathMeasure = new PathMeasure();
                                }
                                this.mPathMeasure.setPath(this.mPath, false);
                                float length = this.mPathMeasure.getLength();
                                float f7 = f5 * length;
                                float f8 = f6 * length;
                                path.reset();
                                if (f7 > f8) {
                                    this.mPathMeasure.getSegment(f7, length, path, true);
                                    this.mPathMeasure.getSegment(0.0f, f8, path, true);
                                } else {
                                    this.mPathMeasure.getSegment(f7, f8, path, true);
                                }
                                path.rLineTo(0.0f, 0.0f);
                            }
                            this.mRenderPath.addPath(path, this.mFinalPathMatrix);
                            if (vFullPath.mFillColor != 0) {
                                if (this.mFillPaint == null) {
                                    this.mFillPaint = new Paint();
                                    this.mFillPaint.setStyle(Paint.Style.FILL);
                                    this.mFillPaint.setAntiAlias(true);
                                }
                                Paint paint = this.mFillPaint;
                                paint.setColor(VectorDrawableCompat.access$900(vFullPath.mFillColor, vFullPath.mFillAlpha));
                                paint.setColorFilter(filter);
                                canvas.drawPath(this.mRenderPath, paint);
                            }
                            if (vFullPath.mStrokeColor != 0) {
                                if (this.mStrokePaint == null) {
                                    this.mStrokePaint = new Paint();
                                    this.mStrokePaint.setStyle(Paint.Style.STROKE);
                                    this.mStrokePaint.setAntiAlias(true);
                                }
                                Paint paint2 = this.mStrokePaint;
                                if (vFullPath.mStrokeLineJoin != null) {
                                    paint2.setStrokeJoin(vFullPath.mStrokeLineJoin);
                                }
                                if (vFullPath.mStrokeLineCap != null) {
                                    paint2.setStrokeCap(vFullPath.mStrokeLineCap);
                                }
                                paint2.setStrokeMiter(vFullPath.mStrokeMiterlimit);
                                paint2.setColor(VectorDrawableCompat.access$900(vFullPath.mStrokeColor, vFullPath.mStrokeAlpha));
                                paint2.setColorFilter(filter);
                                paint2.setStrokeWidth(f4 * min * vFullPath.mStrokeWidth);
                                canvas.drawPath(this.mRenderPath, paint2);
                            }
                        }
                    }
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class VGroup {
        int mChangingConfigurations;
        final ArrayList<Object> mChildren;
        String mGroupName;
        final Matrix mLocalMatrix;
        float mPivotX;
        float mPivotY;
        float mRotate;
        float mScaleX;
        float mScaleY;
        final Matrix mStackedMatrix;
        int[] mThemeAttrs;
        float mTranslateX;
        float mTranslateY;

        public VGroup(VGroup copy, ArrayMap<String, Object> targetsMap) {
            VPath newPath;
            this.mStackedMatrix = new Matrix();
            this.mChildren = new ArrayList<>();
            this.mRotate = 0.0f;
            this.mPivotX = 0.0f;
            this.mPivotY = 0.0f;
            this.mScaleX = 1.0f;
            this.mScaleY = 1.0f;
            this.mTranslateX = 0.0f;
            this.mTranslateY = 0.0f;
            this.mLocalMatrix = new Matrix();
            this.mGroupName = null;
            this.mRotate = copy.mRotate;
            this.mPivotX = copy.mPivotX;
            this.mPivotY = copy.mPivotY;
            this.mScaleX = copy.mScaleX;
            this.mScaleY = copy.mScaleY;
            this.mTranslateX = copy.mTranslateX;
            this.mTranslateY = copy.mTranslateY;
            this.mThemeAttrs = copy.mThemeAttrs;
            this.mGroupName = copy.mGroupName;
            this.mChangingConfigurations = copy.mChangingConfigurations;
            if (this.mGroupName != null) {
                targetsMap.put(this.mGroupName, this);
            }
            this.mLocalMatrix.set(copy.mLocalMatrix);
            ArrayList<Object> children = copy.mChildren;
            for (int i = 0; i < children.size(); i++) {
                Object copyChild = children.get(i);
                if (copyChild instanceof VGroup) {
                    VGroup copyGroup = (VGroup) copyChild;
                    this.mChildren.add(new VGroup(copyGroup, targetsMap));
                } else {
                    if (copyChild instanceof VFullPath) {
                        newPath = new VFullPath((VFullPath) copyChild);
                    } else if (copyChild instanceof VClipPath) {
                        newPath = new VClipPath((VClipPath) copyChild);
                    } else {
                        throw new IllegalStateException("Unknown object in the tree!");
                    }
                    this.mChildren.add(newPath);
                    if (newPath.mPathName != null) {
                        targetsMap.put(newPath.mPathName, newPath);
                    }
                }
            }
        }

        public VGroup() {
            this.mStackedMatrix = new Matrix();
            this.mChildren = new ArrayList<>();
            this.mRotate = 0.0f;
            this.mPivotX = 0.0f;
            this.mPivotY = 0.0f;
            this.mScaleX = 1.0f;
            this.mScaleY = 1.0f;
            this.mTranslateX = 0.0f;
            this.mTranslateY = 0.0f;
            this.mLocalMatrix = new Matrix();
            this.mGroupName = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class VPath {
        int mChangingConfigurations;
        protected PathParser.PathDataNode[] mNodes;
        String mPathName;

        public VPath() {
            this.mNodes = null;
        }

        public VPath(VPath copy) {
            this.mNodes = null;
            this.mPathName = copy.mPathName;
            this.mChangingConfigurations = copy.mChangingConfigurations;
            this.mNodes = PathParser.deepCopyNodes(copy.mNodes);
        }

        public final void toPath(Path path) {
            int i;
            float f;
            float f2;
            float f3;
            float f4;
            float f5;
            float f6;
            float f7;
            float f8;
            float f9;
            path.reset();
            if (this.mNodes != null) {
                PathParser.PathDataNode[] pathDataNodeArr = this.mNodes;
                float[] fArr = new float[6];
                char c = 'm';
                int i2 = 0;
                while (true) {
                    int i3 = i2;
                    char c2 = c;
                    if (i3 >= pathDataNodeArr.length) {
                        return;
                    }
                    char c3 = pathDataNodeArr[i3].type;
                    float[] fArr2 = pathDataNodeArr[i3].params;
                    float f10 = fArr[0];
                    float f11 = fArr[1];
                    float f12 = fArr[2];
                    float f13 = fArr[3];
                    float f14 = fArr[4];
                    float f15 = fArr[5];
                    switch (c3) {
                        case 'A':
                        case 'a':
                            i = 7;
                            break;
                        case 'C':
                        case 'c':
                            i = 6;
                            break;
                        case 'H':
                        case 'V':
                        case 'h':
                        case 'v':
                            i = 1;
                            break;
                        case 'L':
                        case 'M':
                        case 'T':
                        case 'l':
                        case 'm':
                        case 't':
                            i = 2;
                            break;
                        case 'Q':
                        case 'S':
                        case 'q':
                        case 's':
                            i = 4;
                            break;
                        case 'Z':
                        case 'z':
                            path.close();
                            path.moveTo(f14, f15);
                            f13 = f15;
                            f12 = f14;
                            f11 = f15;
                            f10 = f14;
                            i = 2;
                            break;
                        default:
                            i = 2;
                            break;
                    }
                    int i4 = 0;
                    float f16 = f15;
                    float f17 = f14;
                    float f18 = f11;
                    float f19 = f10;
                    while (i4 < fArr2.length) {
                        switch (c3) {
                            case 'A':
                                PathParser.PathDataNode.drawArc(path, f19, f18, fArr2[i4 + 5], fArr2[i4 + 6], fArr2[i4 + 0], fArr2[i4 + 1], fArr2[i4 + 2], fArr2[i4 + 3] != 0.0f, fArr2[i4 + 4] != 0.0f);
                                f12 = fArr2[i4 + 5];
                                f13 = fArr2[i4 + 6];
                                f = f16;
                                f2 = f17;
                                f3 = f13;
                                f4 = f12;
                                break;
                            case 'C':
                                path.cubicTo(fArr2[i4 + 0], fArr2[i4 + 1], fArr2[i4 + 2], fArr2[i4 + 3], fArr2[i4 + 4], fArr2[i4 + 5]);
                                float f20 = fArr2[i4 + 4];
                                float f21 = fArr2[i4 + 5];
                                f12 = fArr2[i4 + 2];
                                f13 = fArr2[i4 + 3];
                                f3 = f21;
                                f4 = f20;
                                f = f16;
                                f2 = f17;
                                break;
                            case 'H':
                                path.lineTo(fArr2[i4 + 0], f18);
                                f = f16;
                                f3 = f18;
                                f4 = fArr2[i4 + 0];
                                f2 = f17;
                                break;
                            case 'L':
                                path.lineTo(fArr2[i4 + 0], fArr2[i4 + 1]);
                                float f22 = fArr2[i4 + 0];
                                f3 = fArr2[i4 + 1];
                                f4 = f22;
                                f = f16;
                                f2 = f17;
                                break;
                            case 'M':
                                f2 = fArr2[i4 + 0];
                                f = fArr2[i4 + 1];
                                if (i4 > 0) {
                                    path.lineTo(fArr2[i4 + 0], fArr2[i4 + 1]);
                                    f3 = f;
                                    f4 = f2;
                                    f = f16;
                                    f2 = f17;
                                    break;
                                } else {
                                    path.moveTo(fArr2[i4 + 0], fArr2[i4 + 1]);
                                    f3 = f;
                                    f4 = f2;
                                    break;
                                }
                            case 'Q':
                                path.quadTo(fArr2[i4 + 0], fArr2[i4 + 1], fArr2[i4 + 2], fArr2[i4 + 3]);
                                f12 = fArr2[i4 + 0];
                                f13 = fArr2[i4 + 1];
                                float f23 = fArr2[i4 + 2];
                                f3 = fArr2[i4 + 3];
                                f4 = f23;
                                f = f16;
                                f2 = f17;
                                break;
                            case 'S':
                                if (c2 == 'c' || c2 == 's' || c2 == 'C' || c2 == 'S') {
                                    f7 = (2.0f * f18) - f13;
                                    f8 = (2.0f * f19) - f12;
                                } else {
                                    f7 = f18;
                                    f8 = f19;
                                }
                                path.cubicTo(f8, f7, fArr2[i4 + 0], fArr2[i4 + 1], fArr2[i4 + 2], fArr2[i4 + 3]);
                                f12 = fArr2[i4 + 0];
                                f13 = fArr2[i4 + 1];
                                float f24 = fArr2[i4 + 2];
                                f3 = fArr2[i4 + 3];
                                f4 = f24;
                                f = f16;
                                f2 = f17;
                                break;
                            case 'T':
                                if (c2 == 'q' || c2 == 't' || c2 == 'Q' || c2 == 'T') {
                                    f19 = (2.0f * f19) - f12;
                                    f18 = (2.0f * f18) - f13;
                                }
                                path.quadTo(f19, f18, fArr2[i4 + 0], fArr2[i4 + 1]);
                                float f25 = fArr2[i4 + 0];
                                f13 = f18;
                                f12 = f19;
                                f3 = fArr2[i4 + 1];
                                f4 = f25;
                                f = f16;
                                f2 = f17;
                                break;
                            case 'V':
                                path.lineTo(f19, fArr2[i4 + 0]);
                                f2 = f17;
                                f3 = fArr2[i4 + 0];
                                f4 = f19;
                                f = f16;
                                break;
                            case 'a':
                                PathParser.PathDataNode.drawArc(path, f19, f18, fArr2[i4 + 5] + f19, fArr2[i4 + 6] + f18, fArr2[i4 + 0], fArr2[i4 + 1], fArr2[i4 + 2], fArr2[i4 + 3] != 0.0f, fArr2[i4 + 4] != 0.0f);
                                f12 = f19 + fArr2[i4 + 5];
                                f13 = f18 + fArr2[i4 + 6];
                                f = f16;
                                f2 = f17;
                                f3 = f13;
                                f4 = f12;
                                break;
                            case 'c':
                                path.rCubicTo(fArr2[i4 + 0], fArr2[i4 + 1], fArr2[i4 + 2], fArr2[i4 + 3], fArr2[i4 + 4], fArr2[i4 + 5]);
                                f12 = f19 + fArr2[i4 + 2];
                                f13 = f18 + fArr2[i4 + 3];
                                float f26 = f19 + fArr2[i4 + 4];
                                f3 = f18 + fArr2[i4 + 5];
                                f4 = f26;
                                f = f16;
                                f2 = f17;
                                break;
                            case 'h':
                                path.rLineTo(fArr2[i4 + 0], 0.0f);
                                f = f16;
                                f3 = f18;
                                f4 = f19 + fArr2[i4 + 0];
                                f2 = f17;
                                break;
                            case 'l':
                                path.rLineTo(fArr2[i4 + 0], fArr2[i4 + 1]);
                                float f27 = f19 + fArr2[i4 + 0];
                                f3 = f18 + fArr2[i4 + 1];
                                f4 = f27;
                                f = f16;
                                f2 = f17;
                                break;
                            case 'm':
                                f2 = f19 + fArr2[i4 + 0];
                                f = f18 + fArr2[i4 + 1];
                                if (i4 > 0) {
                                    path.rLineTo(fArr2[i4 + 0], fArr2[i4 + 1]);
                                    f3 = f;
                                    f4 = f2;
                                    f = f16;
                                    f2 = f17;
                                    break;
                                } else {
                                    path.rMoveTo(fArr2[i4 + 0], fArr2[i4 + 1]);
                                    f3 = f;
                                    f4 = f2;
                                    break;
                                }
                            case 'q':
                                path.rQuadTo(fArr2[i4 + 0], fArr2[i4 + 1], fArr2[i4 + 2], fArr2[i4 + 3]);
                                f12 = f19 + fArr2[i4 + 0];
                                f13 = f18 + fArr2[i4 + 1];
                                float f28 = f19 + fArr2[i4 + 2];
                                f3 = f18 + fArr2[i4 + 3];
                                f4 = f28;
                                f = f16;
                                f2 = f17;
                                break;
                            case 's':
                                float f29 = 0.0f;
                                if (c2 != 'c' && c2 != 's' && c2 != 'C' && c2 != 'S') {
                                    f9 = 0.0f;
                                } else {
                                    f29 = f18 - f13;
                                    f9 = f19 - f12;
                                }
                                path.rCubicTo(f9, f29, fArr2[i4 + 0], fArr2[i4 + 1], fArr2[i4 + 2], fArr2[i4 + 3]);
                                f12 = f19 + fArr2[i4 + 0];
                                f13 = f18 + fArr2[i4 + 1];
                                float f30 = f19 + fArr2[i4 + 2];
                                f3 = f18 + fArr2[i4 + 3];
                                f4 = f30;
                                f = f16;
                                f2 = f17;
                                break;
                            case 't':
                                if (c2 != 'q' && c2 != 't' && c2 != 'Q' && c2 != 'T') {
                                    f5 = 0.0f;
                                    f6 = 0.0f;
                                } else {
                                    f5 = f18 - f13;
                                    f6 = f19 - f12;
                                }
                                path.rQuadTo(f6, f5, fArr2[i4 + 0], fArr2[i4 + 1]);
                                f12 = f19 + f6;
                                f13 = f18 + f5;
                                float f31 = f19 + fArr2[i4 + 0];
                                f3 = f18 + fArr2[i4 + 1];
                                f4 = f31;
                                f = f16;
                                f2 = f17;
                                break;
                            case 'v':
                                path.rLineTo(0.0f, fArr2[i4 + 0]);
                                f2 = f17;
                                f3 = f18 + fArr2[i4 + 0];
                                f4 = f19;
                                f = f16;
                                break;
                            default:
                                f = f16;
                                f2 = f17;
                                f3 = f18;
                                f4 = f19;
                                break;
                        }
                        i4 += i;
                        f16 = f;
                        f17 = f2;
                        f18 = f3;
                        f19 = f4;
                        c2 = c3;
                    }
                    fArr[0] = f19;
                    fArr[1] = f18;
                    fArr[2] = f12;
                    fArr[3] = f13;
                    fArr[4] = f17;
                    fArr[5] = f16;
                    c = pathDataNodeArr[i3].type;
                    i2 = i3 + 1;
                }
            }
        }

        public boolean isClipPath() {
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class VClipPath extends VPath {
        public VClipPath() {
        }

        public VClipPath(VClipPath copy) {
            super(copy);
        }

        final void updateStateFromTypedArray(TypedArray a) {
            String pathName = a.getString(0);
            if (pathName != null) {
                this.mPathName = pathName;
            }
            String pathData = a.getString(1);
            if (pathData != null) {
                this.mNodes = PathParser.createNodesFromPathData(pathData);
            }
        }

        @Override // android.support.graphics.drawable.VectorDrawableCompat.VPath
        public final boolean isClipPath() {
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class VFullPath extends VPath {
        float mFillAlpha;
        int mFillColor;
        int mFillRule;
        float mStrokeAlpha;
        int mStrokeColor;
        Paint.Cap mStrokeLineCap;
        Paint.Join mStrokeLineJoin;
        float mStrokeMiterlimit;
        float mStrokeWidth;
        private int[] mThemeAttrs;
        float mTrimPathEnd;
        float mTrimPathOffset;
        float mTrimPathStart;

        public VFullPath() {
            this.mStrokeColor = 0;
            this.mStrokeWidth = 0.0f;
            this.mFillColor = 0;
            this.mStrokeAlpha = 1.0f;
            this.mFillAlpha = 1.0f;
            this.mTrimPathStart = 0.0f;
            this.mTrimPathEnd = 1.0f;
            this.mTrimPathOffset = 0.0f;
            this.mStrokeLineCap = Paint.Cap.BUTT;
            this.mStrokeLineJoin = Paint.Join.MITER;
            this.mStrokeMiterlimit = 4.0f;
        }

        public VFullPath(VFullPath copy) {
            super(copy);
            this.mStrokeColor = 0;
            this.mStrokeWidth = 0.0f;
            this.mFillColor = 0;
            this.mStrokeAlpha = 1.0f;
            this.mFillAlpha = 1.0f;
            this.mTrimPathStart = 0.0f;
            this.mTrimPathEnd = 1.0f;
            this.mTrimPathOffset = 0.0f;
            this.mStrokeLineCap = Paint.Cap.BUTT;
            this.mStrokeLineJoin = Paint.Join.MITER;
            this.mStrokeMiterlimit = 4.0f;
            this.mThemeAttrs = copy.mThemeAttrs;
            this.mStrokeColor = copy.mStrokeColor;
            this.mStrokeWidth = copy.mStrokeWidth;
            this.mStrokeAlpha = copy.mStrokeAlpha;
            this.mFillColor = copy.mFillColor;
            this.mFillRule = copy.mFillRule;
            this.mFillAlpha = copy.mFillAlpha;
            this.mTrimPathStart = copy.mTrimPathStart;
            this.mTrimPathEnd = copy.mTrimPathEnd;
            this.mTrimPathOffset = copy.mTrimPathOffset;
            this.mStrokeLineCap = copy.mStrokeLineCap;
            this.mStrokeLineJoin = copy.mStrokeLineJoin;
            this.mStrokeMiterlimit = copy.mStrokeMiterlimit;
        }

        final void updateStateFromTypedArray(TypedArray a, XmlPullParser parser) {
            this.mThemeAttrs = null;
            if (TypedArrayUtils.hasAttribute(parser, "pathData")) {
                String pathName = a.getString(0);
                if (pathName != null) {
                    this.mPathName = pathName;
                }
                String pathData = a.getString(2);
                if (pathData != null) {
                    this.mNodes = PathParser.createNodesFromPathData(pathData);
                }
                this.mFillColor = TypedArrayUtils.getNamedColor(a, parser, "fillColor", 1, this.mFillColor);
                this.mFillAlpha = TypedArrayUtils.getNamedFloat(a, parser, "fillAlpha", 12, this.mFillAlpha);
                int lineCap = TypedArrayUtils.getNamedInt$7903c789(a, parser, "strokeLineCap", 8);
                Paint.Cap cap = this.mStrokeLineCap;
                switch (lineCap) {
                    case 0:
                        cap = Paint.Cap.BUTT;
                        break;
                    case 1:
                        cap = Paint.Cap.ROUND;
                        break;
                    case 2:
                        cap = Paint.Cap.SQUARE;
                        break;
                }
                this.mStrokeLineCap = cap;
                int lineJoin = TypedArrayUtils.getNamedInt$7903c789(a, parser, "strokeLineJoin", 9);
                Paint.Join join = this.mStrokeLineJoin;
                switch (lineJoin) {
                    case 0:
                        join = Paint.Join.MITER;
                        break;
                    case 1:
                        join = Paint.Join.ROUND;
                        break;
                    case 2:
                        join = Paint.Join.BEVEL;
                        break;
                }
                this.mStrokeLineJoin = join;
                this.mStrokeMiterlimit = TypedArrayUtils.getNamedFloat(a, parser, "strokeMiterLimit", 10, this.mStrokeMiterlimit);
                this.mStrokeColor = TypedArrayUtils.getNamedColor(a, parser, "strokeColor", 3, this.mStrokeColor);
                this.mStrokeAlpha = TypedArrayUtils.getNamedFloat(a, parser, "strokeAlpha", 11, this.mStrokeAlpha);
                this.mStrokeWidth = TypedArrayUtils.getNamedFloat(a, parser, "strokeWidth", 4, this.mStrokeWidth);
                this.mTrimPathEnd = TypedArrayUtils.getNamedFloat(a, parser, "trimPathEnd", 6, this.mTrimPathEnd);
                this.mTrimPathOffset = TypedArrayUtils.getNamedFloat(a, parser, "trimPathOffset", 7, this.mTrimPathOffset);
                this.mTrimPathStart = TypedArrayUtils.getNamedFloat(a, parser, "trimPathStart", 5, this.mTrimPathStart);
            }
        }
    }

    static /* synthetic */ int access$900(int x0, float x1) {
        return (((int) (Color.alpha(x0) * x1)) << 24) | (16777215 & x0);
    }
}
