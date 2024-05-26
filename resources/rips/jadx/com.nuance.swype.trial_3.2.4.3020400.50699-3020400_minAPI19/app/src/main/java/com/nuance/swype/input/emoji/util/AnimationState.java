package com.nuance.swype.input.emoji.util;

import android.graphics.Matrix;

/* loaded from: classes.dex */
public class AnimationState {
    private final float xPivot;
    private final float yPivot;
    private float scale = 1.0f;
    private float yTrans = 0.0f;
    private float xTrans = 0.0f;
    private float rotate = 0.0f;
    private Matrix matrix = new Matrix();
    private final MatrixUtil matrixUtil = new MatrixUtil();
    private boolean matrixInvalid = true;

    public AnimationState(int xPivot, int yPivot) {
        this.xPivot = xPivot;
        this.yPivot = yPivot;
    }

    public Matrix getMatrix() {
        if (this.matrixInvalid) {
            this.matrix.setScale(this.scale, this.scale, this.xPivot, this.yPivot);
            this.matrix.preRotate(this.rotate, this.xPivot, this.yPivot);
            this.matrix.postTranslate(this.xTrans, this.yTrans);
            this.matrixInvalid = false;
        }
        return this.matrix;
    }

    public void reset() {
        this.rotate = 0.0f;
        this.scale = 1.0f;
        this.xTrans = 0.0f;
        this.yTrans = 0.0f;
        this.matrixInvalid = true;
    }

    public void transform(float rotate, float scale, float xTrans, float yTrans) {
        this.rotate = rotate;
        this.scale = scale;
        this.xTrans = xTrans;
        this.yTrans = yTrans;
        this.matrixInvalid = true;
    }

    public int[] map(int left, int top, int right, int bottom, int[] points) {
        return this.matrixUtil.map(getMatrix(), left, top, right, bottom, points);
    }

    public float getTransX() {
        return this.xTrans;
    }

    public float getTransY() {
        return this.yTrans;
    }

    public float getScale() {
        return this.scale;
    }

    public float getRotate() {
        return this.rotate;
    }
}
