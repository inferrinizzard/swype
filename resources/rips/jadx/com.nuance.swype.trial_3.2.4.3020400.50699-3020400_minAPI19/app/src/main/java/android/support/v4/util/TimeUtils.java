package android.support.v4.util;

import com.nuance.swypeconnect.ac.ACBuildConfigRuntime;
import java.io.PrintWriter;

/* loaded from: classes.dex */
public final class TimeUtils {
    private static final Object sFormatSync = new Object();
    private static char[] sFormatStr = new char[24];

    private static int printField$6419d3d2(char[] formatStr, int amt, char suffix, int pos, boolean always) {
        if (always || amt > 0) {
            if (amt > 99) {
                int dig = amt / 100;
                formatStr[pos] = (char) (dig + 48);
                pos++;
                amt -= dig * 100;
            }
            if (amt > 9 || pos != pos) {
                int dig2 = amt / 10;
                formatStr[pos] = (char) (dig2 + 48);
                pos++;
                amt -= dig2 * 10;
            }
            formatStr[pos] = (char) (amt + 48);
            int pos2 = pos + 1;
            formatStr[pos2] = suffix;
            return pos2 + 1;
        }
        return pos;
    }

    private static void formatDuration$112769eb(long j, PrintWriter printWriter) {
        char c;
        int i;
        int i2;
        int i3;
        int i4;
        int i5 = 1;
        synchronized (sFormatSync) {
            if (sFormatStr.length < 0) {
                sFormatStr = new char[0];
            }
            char[] cArr = sFormatStr;
            if (j == 0) {
                cArr[0] = '0';
            } else {
                if (j > 0) {
                    c = '+';
                } else {
                    j = -j;
                    c = '-';
                }
                int i6 = (int) (j % 1000);
                int floor = (int) Math.floor(j / 1000);
                if (floor > 86400) {
                    int i7 = floor / 86400;
                    floor -= 86400 * i7;
                    i = i7;
                } else {
                    i = 0;
                }
                if (floor > 3600) {
                    int i8 = floor / ACBuildConfigRuntime.MINUMUM_REFRESH_INTERVAL;
                    floor -= i8 * ACBuildConfigRuntime.MINUMUM_REFRESH_INTERVAL;
                    i2 = i8;
                } else {
                    i2 = 0;
                }
                if (floor > 60) {
                    int i9 = floor / 60;
                    i3 = floor - (i9 * 60);
                    i4 = i9;
                } else {
                    i3 = floor;
                    i4 = 0;
                }
                cArr[0] = c;
                int printField$6419d3d2 = printField$6419d3d2(cArr, i, 'd', 1, false);
                int printField$6419d3d22 = printField$6419d3d2(cArr, i2, 'h', printField$6419d3d2, printField$6419d3d2 != 1);
                int printField$6419d3d23 = printField$6419d3d2(cArr, i4, 'm', printField$6419d3d22, printField$6419d3d22 != 1);
                int printField$6419d3d24 = printField$6419d3d2(cArr, i6, 'm', printField$6419d3d2(cArr, i3, 's', printField$6419d3d23, printField$6419d3d23 != 1), true);
                cArr[printField$6419d3d24] = 's';
                i5 = printField$6419d3d24 + 1;
            }
            printWriter.print(new String(sFormatStr, 0, i5));
        }
    }

    public static void formatDuration(long duration, PrintWriter pw) {
        formatDuration$112769eb(duration, pw);
    }

    public static void formatDuration(long time, long now, PrintWriter pw) {
        if (time == 0) {
            pw.print("--");
        } else {
            formatDuration$112769eb(time - now, pw);
        }
    }
}
