package com.nuance.swype.input;

import android.graphics.Path;
import android.graphics.Point;
import android.view.MotionEvent;
import com.nuance.input.swypecorelib.KeyType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/* loaded from: classes.dex */
public abstract class Stroke {
    protected int mStrokeWidth;

    public abstract void clear();

    public abstract Arc[] getArcs();

    public abstract Path[] getPaths();

    public abstract void handleMotionEvent(MotionEvent motionEvent);

    public abstract void handleTouchEnd(int i, int i2, KeyType keyType, boolean z);

    public abstract void handleTouchMoved(int i, int i2, float[] fArr, float[] fArr2, int[] iArr, boolean z);

    public static Stroke create(int strokeWidth) {
        return new MultiStroke(strokeWidth);
    }

    /* loaded from: classes.dex */
    public static class MultiStroke extends Stroke {
        private static final int MAX_POINTER_INDICES = 11;
        private Path[] mPaths = new Path[11];
        private Arc[] mArcs = new Arc[11];

        public MultiStroke(int strokeWidth) {
            this.mStrokeWidth = strokeWidth;
            for (int i = 0; i < this.mPaths.length; i++) {
                this.mPaths[i] = new Path();
            }
            for (int i2 = 0; i2 < this.mArcs.length; i2++) {
                this.mArcs[i2] = new Arc();
            }
        }

        @Override // com.nuance.swype.input.Stroke
        public void handleMotionEvent(MotionEvent me) {
            int action = me.getAction();
            int actionCode = action & 255;
            int pointerIndex = action >> 8;
            int pointerId = MotionEventWrapper.getPointerId(me, pointerIndex);
            if (actionCode == 0 || actionCode == 5) {
                if (actionCode == 0) {
                    clear();
                }
                int x = (int) MotionEventWrapper.getX(me, pointerIndex);
                int y = (int) MotionEventWrapper.getY(me, pointerIndex);
                this.mPaths[pointerId].moveTo(x, y);
                this.mArcs[pointerId].addPoint(x, y);
                return;
            }
            if (actionCode == 2) {
                int pointerCount = MotionEventWrapper.getPointerCount(me);
                for (int index = 0; index < pointerCount; index++) {
                    int thisPointerId = MotionEventWrapper.getPointerId(me, index);
                    int histories = me.getHistorySize();
                    Arc Arc = this.mArcs[thisPointerId];
                    Path path = this.mPaths[thisPointerId];
                    for (int i = 0; i < histories; i++) {
                        int x2 = (int) MotionEventWrapper.getHistoricalX(me, index, i);
                        int y2 = (int) MotionEventWrapper.getHistoricalY(me, index, i);
                        path.lineTo(x2, y2);
                        Arc.addPoint(x2, y2);
                    }
                    int x3 = (int) MotionEventWrapper.getX(me, index);
                    int y3 = (int) MotionEventWrapper.getY(me, index);
                    path.lineTo(x3, y3);
                    Arc.addPoint(x3, y3);
                }
                return;
            }
            if (actionCode == 1) {
                ensureMininumPath(this.mArcs[0], this.mPaths[0]);
                ensureMininumPath(this.mArcs[1], this.mPaths[1]);
            }
        }

        @Override // com.nuance.swype.input.Stroke
        public void handleTouchMoved(int pointerId, int keyIndex, float[] xcoords, float[] ycoords, int[] times, boolean canBeTraced) {
            for (int i = this.mArcs[pointerId].mPoints.size(); i < xcoords.length; i++) {
                int x = (int) xcoords[i];
                int y = (int) ycoords[i];
                if (i == 0) {
                    this.mPaths[pointerId].moveTo(x, y);
                    this.mArcs[pointerId].addPoint(x, y);
                } else {
                    Arc Arc = this.mArcs[pointerId];
                    this.mPaths[pointerId].lineTo(x, y);
                    Arc.addPoint(x, y);
                }
            }
        }

        @Override // com.nuance.swype.input.Stroke
        public void handleTouchEnd(int pointerId, int keyIndex, KeyType keyType, boolean isTraced) {
            ensureMininumPath(this.mArcs[0], this.mPaths[0]);
            ensureMininumPath(this.mArcs[1], this.mPaths[1]);
        }

        private void ensureMininumPath(Arc arc, Path path) {
            if (arc.mPoints.size() > 0 && arc.mPoints.size() < this.mStrokeWidth) {
                for (int i = 0; i < this.mStrokeWidth - arc.mPoints.size(); i++) {
                    path.lineTo(arc.mPoints.get(arc.mPoints.size() - 1).x + 1 + i, arc.mPoints.get(arc.mPoints.size() - 1).y + 1 + i);
                }
            }
        }

        @Override // com.nuance.swype.input.Stroke
        public void clear() {
            clearArcs();
            clearPaths();
        }

        private void clearArcs() {
            for (Arc arc : this.mArcs) {
                arc.clear();
            }
        }

        private void clearPaths() {
            for (Path path : this.mPaths) {
                path.rewind();
            }
        }

        @Override // com.nuance.swype.input.Stroke
        public Path[] getPaths() {
            return (Path[]) Arrays.copyOf(this.mPaths, this.mPaths.length);
        }

        @Override // com.nuance.swype.input.Stroke
        public Arc[] getArcs() {
            return (Arc[]) Arrays.copyOf(this.mArcs, this.mArcs.length);
        }
    }

    /* loaded from: classes.dex */
    public static class Arc {
        public List<Point> mPoints = new ArrayList();

        public void clear() {
            this.mPoints.clear();
        }

        public void addPoint(int x, int y) {
            this.mPoints.add(new Point(x, y));
        }
    }
}
