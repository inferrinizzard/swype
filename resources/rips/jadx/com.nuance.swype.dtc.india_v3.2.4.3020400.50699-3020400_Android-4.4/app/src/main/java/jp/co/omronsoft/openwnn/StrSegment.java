package jp.co.omronsoft.openwnn;

/* loaded from: classes.dex */
public class StrSegment {
    public int from;
    public String string;
    public int to;

    public StrSegment() {
        this(null, -1, -1);
    }

    public StrSegment(String str) {
        this(str, -1, -1);
    }

    public StrSegment(char[] chars) {
        this(new String(chars), -1, -1);
    }

    public StrSegment(String str, int from, int to) {
        this.string = str;
        this.from = from;
        this.to = to;
    }
}
