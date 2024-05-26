package com.nuance.connect.util;

import com.nuance.connect.common.Integers;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Pattern;
import org.json.JSONArray;
import org.json.JSONException;

/* loaded from: classes.dex */
public class StringUtils {
    public static final String DELIMITER = ",";
    public static final String SECONDARY_DELIMITER = "^^^";
    public static final String TERTIARY_DELIMITER = "^$^";

    public static String genericListToString(Collection<?> collection, String str) {
        if (collection == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (Object obj : collection) {
            if (sb.length() > 0) {
                sb.append(str);
            }
            sb.append(obj.toString());
        }
        return sb.toString();
    }

    public static String getFileContents(File file) {
        Scanner scanner;
        Throwable th;
        Scanner scanner2 = null;
        try {
            scanner = new Scanner(file, "UTF-8");
            try {
                String next = scanner.useDelimiter("\\A").next();
                if (scanner == null) {
                    return next;
                }
                scanner.close();
                return next;
            } catch (FileNotFoundException e) {
                scanner2 = scanner;
                if (scanner2 != null) {
                    scanner2.close();
                }
                return "";
            } catch (NoSuchElementException e2) {
                if (scanner != null) {
                    scanner.close();
                }
                return "";
            } catch (Throwable th2) {
                th = th2;
                if (scanner != null) {
                    scanner.close();
                }
                throw th;
            }
        } catch (FileNotFoundException e3) {
        } catch (NoSuchElementException e4) {
            scanner = null;
        } catch (Throwable th3) {
            scanner = null;
            th = th3;
        }
    }

    public static String implode(Collection<String> collection, String str) {
        return listToString(collection, str);
    }

    public static String implode(JSONArray jSONArray, String str) {
        ArrayList arrayList = new ArrayList();
        if (jSONArray != null) {
            for (int i = 0; i < jSONArray.length(); i++) {
                try {
                    arrayList.add(jSONArray.get(i).toString());
                } catch (JSONException e) {
                }
            }
        }
        return implode(arrayList, str);
    }

    public static String implode(int[] iArr, String str) {
        if (iArr == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < iArr.length; i++) {
            if (i != 0) {
                sb.append(str);
            }
            sb.append(String.valueOf(iArr[i]));
        }
        return sb.toString();
    }

    public static String implode(String[] strArr, String str) {
        if (strArr == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < strArr.length; i++) {
            if (i != 0) {
                sb.append(str);
            }
            sb.append(strArr[i]);
        }
        return sb.toString();
    }

    public static boolean isValidSwyperId(String str) {
        if (str == null) {
            return false;
        }
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) < '3' || str.charAt(i) > 'z') {
                return false;
            }
        }
        return true;
    }

    public static boolean isValidUUID(String str) {
        return Pattern.compile("^[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}$").matcher(str).matches();
    }

    public static String listToString(Collection<String> collection, String str) {
        if (collection == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (String str2 : collection) {
            if (sb.length() > 0) {
                sb.append(str);
            }
            sb.append(str2);
        }
        return sb.toString();
    }

    public static String normalizeEmail(String str) {
        if (str == null) {
            return str;
        }
        String[] split = str.split("@");
        return split.length == 2 ? split[0] + "@" + split[1].toLowerCase(Locale.ENGLISH) : str;
    }

    public static boolean nullSafeEquals(String str, String str2) {
        return (str == null && str2 == null) || (str != null && str.equals(str2));
    }

    public static double safeStringToDouble(String str) {
        try {
            return Double.parseDouble(str);
        } catch (NumberFormatException e) {
            return 0.0d;
        }
    }

    public static int safeStringToInt(String str) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return Integers.STATUS_SUCCESS;
        }
    }

    public static int[] safeStringToIntArray(String str) {
        if (str == null || str.length() == 0) {
            return new int[0];
        }
        try {
            String[] split = str.split(",");
            int[] iArr = new int[split.length];
            for (int i = 0; i < split.length; i++) {
                iArr[i] = Integer.parseInt(split[i]);
            }
            return iArr;
        } catch (Exception e) {
            return new int[0];
        }
    }

    public static ArrayList<String> scanWordsTokens(String str, int i, int i2) {
        ArrayList<String> arrayList = new ArrayList<>();
        int length = str.length();
        int i3 = 0;
        while (i3 < length) {
            if (Character.isLetter(str.charAt(i3))) {
                int i4 = i3 + 1;
                while (i4 < length) {
                    char charAt = str.charAt(i4);
                    if (charAt != '-' && charAt != '\'' && !Character.isLetter(charAt)) {
                        break;
                    }
                    i4++;
                }
                String substring = str.substring(i3, i4);
                i3 = i4 - 1;
                int length2 = substring.length();
                if (length2 < i2 && length2 > i) {
                    arrayList.add(substring);
                }
            }
            i3++;
        }
        return arrayList;
    }

    public static List<String> stringToList(String str, String str2) {
        return str != null ? Arrays.asList(str.split(str2)) : new ArrayList();
    }

    public static Set<String> stringToSet(String str, String str2) {
        return str != null ? new HashSet(Arrays.asList(str.split(str2))) : new HashSet();
    }
}
