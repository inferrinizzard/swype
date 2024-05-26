package com.nuance.input.swypecorelib;

/* loaded from: classes.dex */
public class TextSpan {
    private int length;
    private int offset;

    public TextSpan(int offset, int length) {
        this.offset = offset;
        this.length = length;
    }

    public int getOffset() {
        return this.offset;
    }

    public int getLength() {
        return this.length;
    }

    public int getEnd() {
        return this.offset + this.length;
    }

    public void move(int distanceToMove) {
        if (this.offset + distanceToMove >= 0) {
            this.offset += distanceToMove;
        }
    }

    public void grow(int amountToGrowOrShrink) {
        this.length += amountToGrowOrShrink;
    }

    public boolean doesContain(int pos) {
        return pos >= this.offset && pos < this.offset + this.length;
    }
}
