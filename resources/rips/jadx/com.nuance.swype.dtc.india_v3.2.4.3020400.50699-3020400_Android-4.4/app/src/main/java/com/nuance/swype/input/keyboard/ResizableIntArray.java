package com.nuance.swype.input.keyboard;

import java.util.Arrays;

/* loaded from: classes.dex */
public class ResizableIntArray {
    private int[] array;
    private int size;

    public ResizableIntArray() {
        reset(2);
    }

    public void add(int x) {
        if (this.size == this.array.length) {
            resize(this.array.length * 2);
        }
        int[] iArr = this.array;
        int i = this.size;
        this.size = i + 1;
        iArr[i] = x;
    }

    public void add(ResizableIntArray array) {
        for (int i = 0; i < array.size; i++) {
            add(array.array[i]);
        }
    }

    public int[] getRawArray() {
        return Arrays.copyOf(this.array, this.size);
    }

    public int getLast() {
        return this.array[this.size - 1];
    }

    void resize(int newCapacity) {
        int[] temp = new int[newCapacity];
        System.arraycopy(this.array, 0, temp, 0, this.size);
        this.array = temp;
    }

    void reset(int capacity) {
        this.array = new int[capacity];
        this.size = 0;
    }
}
