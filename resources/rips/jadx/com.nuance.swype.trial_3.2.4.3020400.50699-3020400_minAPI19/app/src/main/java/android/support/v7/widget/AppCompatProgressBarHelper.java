package android.support.v7.widget;

import android.R;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Shader;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.support.v4.graphics.drawable.DrawableWrapper;
import android.util.AttributeSet;
import android.widget.ProgressBar;

/* loaded from: classes.dex */
class AppCompatProgressBarHelper {
    private static final int[] TINT_ATTRS = {R.attr.indeterminateDrawable, R.attr.progressDrawable};
    final AppCompatDrawableManager mDrawableManager;
    Bitmap mSampleTile;
    private final ProgressBar mView;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AppCompatProgressBarHelper(ProgressBar view, AppCompatDrawableManager drawableManager) {
        this.mView = view;
        this.mDrawableManager = drawableManager;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void loadFromAttributes(AttributeSet attrs, int defStyleAttr) {
        TintTypedArray a = TintTypedArray.obtainStyledAttributes(this.mView.getContext(), attrs, TINT_ATTRS, defStyleAttr, 0);
        Drawable drawable = a.getDrawableIfKnown(0);
        if (drawable != null) {
            ProgressBar progressBar = this.mView;
            if (drawable instanceof AnimationDrawable) {
                AnimationDrawable animationDrawable = (AnimationDrawable) drawable;
                int numberOfFrames = animationDrawable.getNumberOfFrames();
                AnimationDrawable animationDrawable2 = new AnimationDrawable();
                animationDrawable2.setOneShot(animationDrawable.isOneShot());
                for (int i = 0; i < numberOfFrames; i++) {
                    Drawable tileify = tileify(animationDrawable.getFrame(i), true);
                    tileify.setLevel(10000);
                    animationDrawable2.addFrame(tileify, animationDrawable.getDuration(i));
                }
                animationDrawable2.setLevel(10000);
                drawable = animationDrawable2;
            }
            progressBar.setIndeterminateDrawable(drawable);
        }
        Drawable drawable2 = a.getDrawableIfKnown(1);
        if (drawable2 != null) {
            this.mView.setProgressDrawable(tileify(drawable2, false));
        }
        a.mWrapped.recycle();
    }

    /* JADX WARN: Multi-variable type inference failed */
    private Drawable tileify(Drawable drawable, boolean clip) {
        if (drawable instanceof DrawableWrapper) {
            Drawable inner = ((DrawableWrapper) drawable).getWrappedDrawable();
            if (inner != null) {
                ((DrawableWrapper) drawable).setWrappedDrawable(tileify(inner, clip));
            }
        } else {
            if (drawable instanceof LayerDrawable) {
                LayerDrawable background = (LayerDrawable) drawable;
                int N = background.getNumberOfLayers();
                Drawable[] outDrawables = new Drawable[N];
                for (int i = 0; i < N; i++) {
                    int id = background.getId(i);
                    outDrawables[i] = tileify(background.getDrawable(i), id == 16908301 || id == 16908303);
                }
                LayerDrawable newBg = new LayerDrawable(outDrawables);
                for (int i2 = 0; i2 < N; i2++) {
                    newBg.setId(i2, background.getId(i2));
                }
                return newBg;
            }
            if (drawable instanceof BitmapDrawable) {
                BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
                Bitmap tileBitmap = bitmapDrawable.getBitmap();
                if (this.mSampleTile == null) {
                    this.mSampleTile = tileBitmap;
                }
                ShapeDrawable shapeDrawable = new ShapeDrawable(new RoundRectShape(new float[]{5.0f, 5.0f, 5.0f, 5.0f, 5.0f, 5.0f, 5.0f, 5.0f}, null, null));
                BitmapShader bitmapShader = new BitmapShader(tileBitmap, Shader.TileMode.REPEAT, Shader.TileMode.CLAMP);
                shapeDrawable.getPaint().setShader(bitmapShader);
                shapeDrawable.getPaint().setColorFilter(bitmapDrawable.getPaint().getColorFilter());
                return clip ? new ClipDrawable(shapeDrawable, 3, 1) : shapeDrawable;
            }
        }
        return drawable;
    }
}
