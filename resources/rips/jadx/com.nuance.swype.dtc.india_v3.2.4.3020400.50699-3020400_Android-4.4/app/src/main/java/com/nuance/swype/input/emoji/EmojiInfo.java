package com.nuance.swype.input.emoji;

/* loaded from: classes.dex */
public class EmojiInfo {
    public int height;
    public int width;
    public int xPos;
    public int yPos;

    public int getLeft() {
        return this.xPos;
    }

    public int getTop() {
        return this.yPos;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }
}
