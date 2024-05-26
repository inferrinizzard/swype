package com.nuance.connect.util;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* loaded from: classes.dex */
public class VersionUtils {
    private static final Version VERSION_5_1 = new Version("5.1");

    /* loaded from: classes.dex */
    public static class Version implements Comparable<Version> {
        private static final Pattern versionPattern = Pattern.compile("^([0-9]+)((?:\\.([0-9]+))?(?:\\.([0-9]+))?(?:\\.([0-9]+))?)?([^\\.0-9]{1}[^\\.]*)?$");
        private int build;
        private final int hashCode;
        private String label;
        private int major;
        private int minor;
        private int patch;
        private final String version;

        public Version(String str) {
            if (str == null) {
                throw new IllegalArgumentException("Version can not be null");
            }
            if (!versionPattern.matcher(str).matches()) {
                throw new IllegalArgumentException("Invalid version format");
            }
            this.version = str;
            generateValues(str);
            this.hashCode = Arrays.hashCode(new Object[]{this.version, getClass()});
        }

        @Override // java.lang.Comparable
        public int compareTo(Version version) {
            int i = 1;
            if (version == null) {
                return 1;
            }
            int i2 = getMajor() < version.getMajor() ? -1 : getMajor() > version.getMajor() ? 1 : 0;
            if (i2 != 0) {
                return i2;
            }
            int i3 = getMinor() < version.getMinor() ? -1 : getMinor() > version.getMinor() ? 1 : 0;
            if (i3 != 0) {
                return i3;
            }
            int i4 = getPatch() < version.getPatch() ? -1 : getPatch() > version.getPatch() ? 1 : 0;
            if (i4 != 0) {
                return i4;
            }
            if (getBuild() < version.getBuild()) {
                i = -1;
            } else if (getBuild() <= version.getBuild()) {
                i = 0;
            }
            return i == 0 ? getLabel().compareTo(version.getLabel()) : i;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            return this.version.equals(((Version) obj).get());
        }

        protected void generateValues(String str) {
            Matcher matcher = versionPattern.matcher(str);
            while (matcher.find()) {
                this.major = Integer.parseInt(matcher.group(1));
                try {
                    this.minor = Integer.parseInt(matcher.group(3));
                } catch (NumberFormatException e) {
                    this.minor = 0;
                }
                try {
                    this.patch = Integer.parseInt(matcher.group(4));
                } catch (NumberFormatException e2) {
                    this.patch = 0;
                } catch (Exception e3) {
                }
                try {
                    this.build = Integer.parseInt(matcher.group(5));
                } catch (NumberFormatException e4) {
                    this.build = 0;
                }
                this.label = matcher.group(6) != null ? matcher.group(6) : "";
            }
        }

        public final String get() {
            return this.version;
        }

        public int getBuild() {
            return this.build;
        }

        public String getLabel() {
            return this.label;
        }

        public int getMajor() {
            return this.major;
        }

        public int getMinor() {
            return this.minor;
        }

        public int getPatch() {
            return this.patch;
        }

        public int hashCode() {
            return this.hashCode;
        }

        public String toString() {
            return this.major + "." + this.minor + "." + this.patch + this.label;
        }
    }

    static boolean hasKeyChanged(Version version, Version version2) {
        return version.compareTo(VERSION_5_1) < 0 && version2.compareTo(VERSION_5_1) >= 0;
    }

    public static boolean isAtLeastVerson(String str, String str2) {
        return new Version(str).compareTo(new Version(str2)) <= 0;
    }

    public static boolean isDataCleanupRequiredOnUpgrade(Version version, Version version2) {
        return hasKeyChanged(version, version2) || (version.compareTo(VERSION_5_1) >= 0 && version.compareTo(new Version("5.1.4")) <= 0);
    }
}
