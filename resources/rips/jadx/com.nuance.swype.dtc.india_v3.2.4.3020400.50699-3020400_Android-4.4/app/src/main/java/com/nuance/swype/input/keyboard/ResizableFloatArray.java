package com.nuance.swype.input.keyboard;

import java.util.Arrays;

/* loaded from: classes.dex */
public class ResizableFloatArray {
    private float[] array;
    private int size;

    public ResizableFloatArray() {
        reset(2);
    }

    public void add(float x) {
        if (this.size == this.array.length) {
            resize(this.array.length * 2);
        }
        float[] fArr = this.array;
        int i = this.size;
        this.size = i + 1;
        fArr[i] = x;
    }

    public void add(ResizableFloatArray array) {
        for (int i = 0; i < array.size; i++) {
            add(array.array[i]);
        }
    }

    public float[] getRawArray() {
        return Arrays.copyOf(this.array, this.size);
    }

    public float getLast() {
        return this.array[this.size - 1];
    }

    private void resize(int newCapacity) {
        float[] temp = new float[newCapacity];
        System.arraycopy(this.array, 0, temp, 0, this.size);
        this.array = temp;
    }

    private void reset(int capacity) {
        this.array = new float[capacity];
        this.size = 0;
    }
}
