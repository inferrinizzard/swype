package com.nuance.input.swypecorelib.usagedata;

import android.graphics.Point;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
class SessionDataTracePoints {
    private float[] xCoords = new float[2];
    private float[] yCoords = new float[2];
    private int size = 0;

    /* JADX INFO: Access modifiers changed from: package-private */
    public void add(float[] x, float[] y) {
        for (int i = 0; i < x.length; i = i + 1 + 1) {
            if (this.size + 1 > this.xCoords.length) {
                resize(this.xCoords.length * 2);
            }
            this.xCoords[this.size] = x[i];
            this.yCoords[this.size] = y[i];
            this.size++;
        }
    }

    private void resize(int newSize) {
        float[] tx = new float[newSize];
        float[] ty = new float[newSize];
        System.arraycopy(this.xCoords, 0, tx, 0, this.size);
        System.arraycopy(this.yCoords, 0, ty, 0, this.size);
        this.xCoords = tx;
        this.yCoords = ty;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public List<Point> getPoints() {
        List<Point> points = new ArrayList<>();
        for (int i = 0; i < this.size; i++) {
            points.add(new Point((int) this.xCoords[i], (int) this.yCoords[i]));
        }
        return points;
    }
}
