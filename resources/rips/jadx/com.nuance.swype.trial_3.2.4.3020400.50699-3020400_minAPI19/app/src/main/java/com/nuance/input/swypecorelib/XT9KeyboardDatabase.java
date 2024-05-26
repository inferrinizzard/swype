package com.nuance.input.swypecorelib;

import java.util.List;

/* loaded from: classes.dex */
public class XT9KeyboardDatabase {
    public final int height;
    public final List<Key> keys;
    public final int pages;
    public final int width;

    public XT9KeyboardDatabase(int width, int height, int pages, List<Key> keys) {
        this.width = width;
        this.height = height;
        this.pages = pages;
        this.keys = keys;
    }

    /* loaded from: classes.dex */
    public static final class Key {
        public final char[] codes;
        public final int height;
        public final char[] multitapChars;
        public final char[] shiftCodes;
        public final int type;
        public final int width;
        public final int x;
        public final int y;

        public Key(char[] codes, char[] shiftCodes, char[] multitapChars, int type, int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.type = type;
            this.codes = codes;
            this.shiftCodes = shiftCodes;
            this.multitapChars = multitapChars;
        }
    }
}
