package android.support.graphics.drawable;

import android.graphics.Path;
import android.util.Log;
import java.util.ArrayList;

/* loaded from: classes.dex */
final class PathParser {
    static float[] copyOfRange$7b60297f(float[] original, int end) {
        if (end < 0) {
            throw new IllegalArgumentException();
        }
        int originalLength = original.length;
        if (originalLength < 0) {
            throw new ArrayIndexOutOfBoundsException();
        }
        int resultLength = end + 0;
        int copyLength = Math.min(resultLength, originalLength + 0);
        float[] result = new float[resultLength];
        System.arraycopy(original, 0, result, 0, copyLength);
        return result;
    }

    public static PathDataNode[] createNodesFromPathData(String pathData) {
        if (pathData == null) {
            return null;
        }
        int start = 0;
        int end = 1;
        ArrayList<PathDataNode> list = new ArrayList<>();
        while (end < pathData.length()) {
            int end2 = nextStart(pathData, end);
            String s = pathData.substring(start, end2).trim();
            if (s.length() > 0) {
                float[] val = getFloats(s);
                addNode(list, s.charAt(0), val);
            }
            start = end2;
            end = end2 + 1;
        }
        if (end - start == 1 && start < pathData.length()) {
            addNode(list, pathData.charAt(start), new float[0]);
        }
        return (PathDataNode[]) list.toArray(new PathDataNode[list.size()]);
    }

    public static PathDataNode[] deepCopyNodes(PathDataNode[] source) {
        if (source == null) {
            return null;
        }
        PathDataNode[] copy = new PathDataNode[source.length];
        for (int i = 0; i < source.length; i++) {
            copy[i] = new PathDataNode(source[i], (byte) 0);
        }
        return copy;
    }

    private static int nextStart(String s, int end) {
        while (end < s.length()) {
            char c = s.charAt(end);
            if (((c - 'A') * (c - 'Z') <= 0 || (c - 'a') * (c - 'z') <= 0) && c != 'e' && c != 'E') {
                break;
            }
            end++;
        }
        return end;
    }

    private static void addNode(ArrayList<PathDataNode> list, char cmd, float[] val) {
        list.add(new PathDataNode(cmd, val, (byte) 0));
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class ExtractFloatResult {
        int mEndPosition;
        boolean mEndWithNegOrDot;

        private ExtractFloatResult() {
        }

        /* synthetic */ ExtractFloatResult(byte b) {
            this();
        }
    }

    /* JADX WARN: Failed to find 'out' block for switch in B:19:0x0047. Please report as an issue. */
    /* JADX WARN: Removed duplicated region for block: B:25:0x008b A[Catch: NumberFormatException -> 0x005c, TryCatch #0 {NumberFormatException -> 0x005c, blocks: (B:13:0x001f, B:15:0x0034, B:16:0x003c, B:18:0x0042, B:19:0x0047, B:21:0x004c, B:39:0x0057, B:43:0x0080, B:23:0x0085, B:25:0x008b, B:26:0x0097, B:29:0x009e, B:47:0x00a2), top: B:12:0x001f }] */
    /* JADX WARN: Removed duplicated region for block: B:28:0x009e A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:32:0x009b A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:35:0x00a8  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private static float[] getFloats(java.lang.String r14) {
        /*
            r8 = 0
            char r8 = r14.charAt(r8)
            r9 = 122(0x7a, float:1.71E-43)
            if (r8 != r9) goto L1b
            r8 = 1
        La:
            r9 = 0
            char r9 = r14.charAt(r9)
            r10 = 90
            if (r9 != r10) goto L1d
            r9 = 1
        L14:
            r8 = r8 | r9
            if (r8 == 0) goto L1f
            r8 = 0
            float[] r8 = new float[r8]
        L1a:
            return r8
        L1b:
            r8 = 0
            goto La
        L1d:
            r9 = 0
            goto L14
        L1f:
            int r8 = r14.length()     // Catch: java.lang.NumberFormatException -> L5c
            float[] r5 = new float[r8]     // Catch: java.lang.NumberFormatException -> L5c
            r0 = 0
            r6 = 1
            android.support.graphics.drawable.PathParser$ExtractFloatResult r4 = new android.support.graphics.drawable.PathParser$ExtractFloatResult     // Catch: java.lang.NumberFormatException -> L5c
            r8 = 0
            r4.<init>(r8)     // Catch: java.lang.NumberFormatException -> L5c
            int r7 = r14.length()     // Catch: java.lang.NumberFormatException -> L5c
            r1 = r0
        L32:
            if (r6 >= r7) goto La2
            r10 = 0
            r8 = 0
            r4.mEndWithNegOrDot = r8     // Catch: java.lang.NumberFormatException -> L5c
            r9 = 0
            r8 = 0
            r11 = r8
            r12 = r6
        L3c:
            int r8 = r14.length()     // Catch: java.lang.NumberFormatException -> L5c
            if (r12 >= r8) goto L85
            r8 = 0
            char r13 = r14.charAt(r12)     // Catch: java.lang.NumberFormatException -> L5c
            switch(r13) {
                case 32: goto L51;
                case 44: goto L51;
                case 45: goto L53;
                case 46: goto L7a;
                case 69: goto L83;
                case 101: goto L83;
                default: goto L4a;
            }     // Catch: java.lang.NumberFormatException -> L5c
        L4a:
            if (r10 != 0) goto L85
            int r11 = r12 + 1
            r12 = r11
            r11 = r8
            goto L3c
        L51:
            r10 = 1
            goto L4a
        L53:
            if (r12 == r6) goto L4a
            if (r11 != 0) goto L4a
            r10 = 1
            r11 = 1
            r4.mEndWithNegOrDot = r11     // Catch: java.lang.NumberFormatException -> L5c
            goto L4a
        L5c:
            r2 = move-exception
            java.lang.RuntimeException r8 = new java.lang.RuntimeException
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            java.lang.String r10 = "error in parsing \""
            r9.<init>(r10)
            java.lang.StringBuilder r9 = r9.append(r14)
            java.lang.String r10 = "\""
            java.lang.StringBuilder r9 = r9.append(r10)
            java.lang.String r9 = r9.toString()
            r8.<init>(r9, r2)
            throw r8
        L7a:
            if (r9 != 0) goto L7e
            r9 = 1
            goto L4a
        L7e:
            r10 = 1
            r11 = 1
            r4.mEndWithNegOrDot = r11     // Catch: java.lang.NumberFormatException -> L5c
            goto L4a
        L83:
            r8 = 1
            goto L4a
        L85:
            r4.mEndPosition = r12     // Catch: java.lang.NumberFormatException -> L5c
            int r3 = r4.mEndPosition     // Catch: java.lang.NumberFormatException -> L5c
            if (r6 >= r3) goto La8
            int r0 = r1 + 1
            java.lang.String r8 = r14.substring(r6, r3)     // Catch: java.lang.NumberFormatException -> L5c
            float r8 = java.lang.Float.parseFloat(r8)     // Catch: java.lang.NumberFormatException -> L5c
            r5[r1] = r8     // Catch: java.lang.NumberFormatException -> L5c
        L97:
            boolean r8 = r4.mEndWithNegOrDot     // Catch: java.lang.NumberFormatException -> L5c
            if (r8 == 0) goto L9e
            r6 = r3
            r1 = r0
            goto L32
        L9e:
            int r6 = r3 + 1
            r1 = r0
            goto L32
        La2:
            float[] r8 = copyOfRange$7b60297f(r5, r1)     // Catch: java.lang.NumberFormatException -> L5c
            goto L1a
        La8:
            r0 = r1
            goto L97
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.graphics.drawable.PathParser.getFloats(java.lang.String):float[]");
    }

    /* loaded from: classes.dex */
    public static class PathDataNode {
        float[] params;
        char type;

        /* synthetic */ PathDataNode(char x0, float[] x1, byte b) {
            this(x0, x1);
        }

        /* synthetic */ PathDataNode(PathDataNode x0, byte b) {
            this(x0);
        }

        private PathDataNode(char type, float[] params) {
            this.type = type;
            this.params = params;
        }

        private PathDataNode(PathDataNode n) {
            this.type = n.type;
            this.params = PathParser.copyOfRange$7b60297f(n.params, n.params.length);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static void drawArc(Path p, float x0, float y0, float x1, float y1, float a, float b, float theta, boolean isMoreThanHalf, boolean isPositiveArc) {
            double cx;
            double cy;
            while (true) {
                double thetaD = Math.toRadians(theta);
                double cosTheta = Math.cos(thetaD);
                double sinTheta = Math.sin(thetaD);
                double x0p = ((x0 * cosTheta) + (y0 * sinTheta)) / a;
                double y0p = (((-x0) * sinTheta) + (y0 * cosTheta)) / b;
                double x1p = ((x1 * cosTheta) + (y1 * sinTheta)) / a;
                double y1p = (((-x1) * sinTheta) + (y1 * cosTheta)) / b;
                double dx = x0p - x1p;
                double dy = y0p - y1p;
                double xm = (x0p + x1p) / 2.0d;
                double ym = (y0p + y1p) / 2.0d;
                double dsq = (dx * dx) + (dy * dy);
                if (dsq == 0.0d) {
                    Log.w("PathParser", " Points are coincident");
                    return;
                }
                double disc = (1.0d / dsq) - 0.25d;
                if (disc < 0.0d) {
                    Log.w("PathParser", "Points are too far apart " + dsq);
                    float adjust = (float) (Math.sqrt(dsq) / 1.99999d);
                    a *= adjust;
                    b *= adjust;
                } else {
                    double s = Math.sqrt(disc);
                    double sdx = s * dx;
                    double sdy = s * dy;
                    if (isMoreThanHalf == isPositiveArc) {
                        cx = xm - sdy;
                        cy = ym + sdx;
                    } else {
                        cx = xm + sdy;
                        cy = ym - sdx;
                    }
                    double eta0 = Math.atan2(y0p - cy, x0p - cx);
                    double sweep = Math.atan2(y1p - cy, x1p - cx) - eta0;
                    if (isPositiveArc != (sweep >= 0.0d)) {
                        if (sweep > 0.0d) {
                            sweep -= 6.283185307179586d;
                        } else {
                            sweep += 6.283185307179586d;
                        }
                    }
                    double cx2 = cx * a;
                    double cy2 = cy * b;
                    arcToBezier(p, (cx2 * cosTheta) - (cy2 * sinTheta), (cx2 * sinTheta) + (cy2 * cosTheta), a, b, x0, y0, thetaD, eta0, sweep);
                    return;
                }
            }
        }

        private static void arcToBezier(Path p, double cx, double cy, double a, double b, double e1x, double e1y, double theta, double start, double sweep) {
            int numSegments = (int) Math.ceil(Math.abs((4.0d * sweep) / 3.141592653589793d));
            double eta1 = start;
            double cosTheta = Math.cos(theta);
            double sinTheta = Math.sin(theta);
            double cosEta1 = Math.cos(eta1);
            double sinEta1 = Math.sin(eta1);
            double ep1x = (((-a) * cosTheta) * sinEta1) - ((b * sinTheta) * cosEta1);
            double ep1y = ((-a) * sinTheta * sinEta1) + (b * cosTheta * cosEta1);
            double anglePerSegment = sweep / numSegments;
            for (int i = 0; i < numSegments; i++) {
                double eta2 = eta1 + anglePerSegment;
                double sinEta2 = Math.sin(eta2);
                double cosEta2 = Math.cos(eta2);
                double e2x = (((a * cosTheta) * cosEta2) + cx) - ((b * sinTheta) * sinEta2);
                double e2y = (a * sinTheta * cosEta2) + cy + (b * cosTheta * sinEta2);
                double ep2x = (((-a) * cosTheta) * sinEta2) - ((b * sinTheta) * cosEta2);
                double ep2y = ((-a) * sinTheta * sinEta2) + (b * cosTheta * cosEta2);
                double tanDiff2 = Math.tan((eta2 - eta1) / 2.0d);
                double alpha = (Math.sin(eta2 - eta1) * (Math.sqrt(4.0d + ((3.0d * tanDiff2) * tanDiff2)) - 1.0d)) / 3.0d;
                double q1x = e1x + (alpha * ep1x);
                double q1y = e1y + (alpha * ep1y);
                double q2x = e2x - (alpha * ep2x);
                double q2y = e2y - (alpha * ep2y);
                p.cubicTo((float) q1x, (float) q1y, (float) q2x, (float) q2y, (float) e2x, (float) e2y);
                eta1 = eta2;
                e1x = e2x;
                e1y = e2y;
                ep1x = ep2x;
                ep1y = ep2y;
            }
        }
    }
}
