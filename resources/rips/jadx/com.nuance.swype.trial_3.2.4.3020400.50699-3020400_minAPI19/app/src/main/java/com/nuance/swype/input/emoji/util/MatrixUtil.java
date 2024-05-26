package com.nuance.swype.input.emoji.util;

import android.graphics.Matrix;
import android.view.View;

/* loaded from: classes.dex */
public class MatrixUtil {
    private final float[] floatPoints = new float[4];

    public static float[] map(Matrix matrix, View view, float[] points) {
        if (points == null) {
            points = new float[4];
        }
        points[0] = view.getLeft();
        points[1] = view.getTop();
        points[2] = view.getRight();
        points[3] = view.getBottom();
        matrix.mapPoints(points);
        return points;
    }

    public static float[] map(Matrix matrix, int left, int top, int right, int bottom, float[] points) {
        if (points == null) {
            points = new float[4];
        }
        points[0] = left;
        points[1] = top;
        points[2] = right;
        points[3] = bottom;
        matrix.mapPoints(points);
        return points;
    }

    public int[] map(Matrix matrix, int left, int top, int right, int bottom, int[] points) {
        float[] temp = map(matrix, left, top, right, bottom, this.floatPoints);
        if (points == null) {
            points = new int[4];
        }
        points[0] = Util.roundInt(temp[0]);
        points[1] = Util.roundInt(temp[1]);
        points[2] = Util.roundInt(temp[2]);
        points[3] = Util.roundInt(temp[3]);
        return points;
    }
}
