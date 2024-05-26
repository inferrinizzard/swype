package com.nuance.swype.input;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import com.nuance.swype.input.Stroke;
import com.nuance.swype.input.settings.InputPrefs;
import java.util.List;

/* loaded from: classes.dex */
public class HandWritingView extends View {
    public FaddingStrokeQueue mFaddingStrokeQueue;
    public InSelectionAreaListener mInSelectionAreaListener;
    public OnWritingAction mOnWritingActionListener;

    /* loaded from: classes.dex */
    public interface InSelectionAreaListener {
        boolean getCurrentScreenMode();

        boolean isSpeechPopupShowing();

        boolean pointInSelectionArea(int i, int i2);

        void resetArea(int i);

        boolean transferKeyEvent(MotionEvent motionEvent);
    }

    /* loaded from: classes.dex */
    public interface OnWritingAction {
        void penDown(View view);

        void penUp(List<Point> list, View view);

        void penUp(Stroke.Arc[] arcArr, View view);
    }

    public HandWritingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HandWritingView(Context context, AttributeSet attrs, int def) {
        super(context, attrs, def);
        int penColor = InputPrefs.getPenColor(UserPreferences.from(getContext()), UserPreferences.HWR_AUTO_ACCEPT_COLOR, 24, context);
        this.mFaddingStrokeQueue = new FaddingStrokeQueue(context, penColor, this);
    }

    @Override // android.view.View
    public void onDraw(Canvas c) {
        super.onDraw(c);
        this.mFaddingStrokeQueue.draw(c);
    }

    @Override // android.view.View
    public void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        int penColor = InputPrefs.getPenColor(UserPreferences.from(getContext()), UserPreferences.HWR_AUTO_ACCEPT_COLOR, 24, getContext());
        this.mFaddingStrokeQueue = null;
        this.mFaddingStrokeQueue = new FaddingStrokeQueue(getContext(), penColor, this);
    }

    public void setSelectionAreaListener(InSelectionAreaListener condition) {
        this.mInSelectionAreaListener = condition;
    }

    public InSelectionAreaListener getInSelectionAreaListener() {
        return this.mInSelectionAreaListener;
    }

    public void setOnWritingActionListener(OnWritingAction listener) {
        this.mOnWritingActionListener = listener;
    }

    public OnWritingAction getOnWritingActionListener() {
        return this.mOnWritingActionListener;
    }

    @Override // android.view.View
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int h = View.MeasureSpec.getSize(heightMeasureSpec);
        int w = View.MeasureSpec.getSize(widthMeasureSpec);
        setMeasuredDimension(w, h);
    }
}
