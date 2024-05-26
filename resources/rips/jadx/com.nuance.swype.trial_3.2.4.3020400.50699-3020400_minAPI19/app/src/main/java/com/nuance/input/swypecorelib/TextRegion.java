package com.nuance.input.swypecorelib;

import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class TextRegion extends TextSpan {
    private List<Correction> corrections;
    private boolean hasTerminator;
    private int hash;
    private boolean rendered;

    public TextRegion(String buffer, int offset, int length, boolean hasTerminator) {
        super(offset, length);
        this.corrections = new ArrayList();
        this.hash = calcHash(buffer);
        this.rendered = false;
        this.hasTerminator = hasTerminator;
    }

    public static boolean hasSameString(TextRegion region1, TextRegion region2) {
        return region1.hash == region2.hash;
    }

    public String extractString(String buffer) {
        int end = getOffset() + getLength();
        if (buffer.length() < end) {
            return null;
        }
        return buffer.substring(getOffset(), end);
    }

    public boolean hasTerminator() {
        return this.hasTerminator;
    }

    public boolean isRendered() {
        return this.rendered;
    }

    public void markRendered() {
        this.rendered = true;
    }

    public void applyCorrection(Correction currCorrection, int newCorrLen, String newVal) {
        int sizeChange = newCorrLen - currCorrection.getLength();
        if (sizeChange != 0) {
            grow(sizeChange);
            for (Correction correction : this.corrections) {
                if (correction.getOffset() > currCorrection.getOffset()) {
                    correction.move(sizeChange);
                }
            }
        }
        this.corrections.remove(currCorrection);
        this.hash = calcHash(newVal);
    }

    public void clearCorrections() {
        this.corrections.clear();
    }

    public void addCorrection(Correction corr) {
        this.corrections.add(corr);
    }

    public Correction getCorrection(int index) {
        if (index >= this.corrections.size()) {
            return null;
        }
        return this.corrections.get(index);
    }

    public int getCorrectionCount() {
        return this.corrections.size();
    }

    public Correction getCorrectionFromPosition(int pos) {
        int relativePos = pos - getOffset();
        for (Correction correction : this.corrections) {
            if (correction.doesContain(relativePos)) {
                return correction;
            }
        }
        return null;
    }

    public List<Correction> findCorrections(int start, int len) {
        int corrCount = getCorrectionCount();
        List<Correction> corr = new ArrayList<>();
        for (int index1 = 0; index1 != corrCount; index1++) {
            Correction c = getCorrection(index1);
            if (c.getOffset() >= start) {
                if (c.getOffset() >= start + len) {
                    break;
                }
                corr.add(c);
            }
        }
        return corr;
    }

    public String toString() {
        StringBuilder res = new StringBuilder();
        res.append("Offset=");
        res.append(getOffset());
        res.append(", Length= ");
        res.append(getLength());
        res.append(", Hash= ");
        res.append(this.hash);
        res.append(", State= ");
        res.append(this.rendered ? "rendered" : "unrendered");
        res.append(", Terminated= ");
        res.append(this.hasTerminator ? "T" : "F");
        res.append(",Corrections = [");
        boolean first = true;
        for (Correction correction : this.corrections) {
            if (first) {
                first = false;
            } else {
                res.append(",");
            }
            res.append(correction.toString());
        }
        res.append("]");
        return res.toString();
    }

    private int calcHash(String val) {
        return val.substring(getOffset(), getOffset() + getLength()).hashCode();
    }
}
