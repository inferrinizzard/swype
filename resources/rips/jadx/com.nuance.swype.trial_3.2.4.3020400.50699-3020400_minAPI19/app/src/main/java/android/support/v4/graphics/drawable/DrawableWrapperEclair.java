package android.support.v4.graphics.drawable;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableWrapperDonut;

/* loaded from: classes.dex */
final class DrawableWrapperEclair extends DrawableWrapperDonut {
    /* JADX INFO: Access modifiers changed from: package-private */
    public DrawableWrapperEclair(Drawable drawable) {
        super(drawable);
    }

    DrawableWrapperEclair(DrawableWrapperDonut.DrawableWrapperState state, Resources resources) {
        super(state, resources);
    }

    @Override // android.support.v4.graphics.drawable.DrawableWrapperDonut
    final DrawableWrapperDonut.DrawableWrapperState mutateConstantState() {
        return new DrawableWrapperStateEclair(this.mState);
    }

    @Override // android.support.v4.graphics.drawable.DrawableWrapperDonut
    protected final Drawable newDrawableFromState(Drawable.ConstantState state, Resources res) {
        return state.newDrawable(res);
    }

    /* loaded from: classes.dex */
    private static class DrawableWrapperStateEclair extends DrawableWrapperDonut.DrawableWrapperState {
        DrawableWrapperStateEclair(DrawableWrapperDonut.DrawableWrapperState orig) {
            super(orig);
        }

        @Override // android.support.v4.graphics.drawable.DrawableWrapperDonut.DrawableWrapperState, android.graphics.drawable.Drawable.ConstantState
        public final Drawable newDrawable(Resources res) {
            return new DrawableWrapperEclair(this, res);
        }
    }
}
