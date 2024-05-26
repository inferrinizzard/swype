package com.nuance.swype.usagedata;

/* loaded from: classes.dex */
public final class CustomDimension {

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public enum Dimension {
        KEYBOARD_LANGUAGE(0, "KEYBOARD_LANGUAGE"),
        DEVICE_TYPE(1, "DEVICE_TYPE"),
        KEYBOARD_LAYOUT(2, "KEYBOARD_LAYOUT"),
        ABC_KEYBOARD_MODE(3, "ABC_KEYBOARD_MODE"),
        THEME_NAME(4, "THEME_NAME"),
        BILINGUAL(5, "BILINGUAL"),
        AUTO_CORRECT(6, "AUTO_CORRECT"),
        NUMBER_ROW(7, "NUMBER_ROW"),
        SECONDARY_ENABLED(8, "SECONDARY_ENABLED"),
        PACKAGE_ID(9, "PACKAGE_ID"),
        ENABLE_HWR(10, "ENABLE_HWR"),
        KB_HEIGHT_PORTRAIT(11, "KB_HEIGHT_PORTRAIT"),
        KB_HEIGHT_LANDSCAPE(12, "KB_HEIGHT_LANDSCAPE"),
        DICTIONARY_BEHAVIOR(13, "DICTIONARY_BEHAVIOR"),
        DICTIONARY_WORDS(14, "DICTIONARY_WORDS"),
        SOUND_ON_KEYPRESS(15, "SOUND_ON_KEYPRESS"),
        NEXT_WORD_PREDICTION(16, "NEXT_WORD_PREDICTION"),
        LONG_PRESS_DELAY(17, "LONG_PRESS_DELAY"),
        EMOJI_PREDICTION(18, "EMOJI_PREDICTION"),
        CLOUD_INPUT(19, "CLOUD_INPUT");

        final int index;
        private final String tag;

        Dimension(int dimIndex, String dimTag) {
            this.index = dimIndex;
            this.tag = dimTag;
        }

        @Override // java.lang.Enum
        public final String toString() {
            return this.tag;
        }
    }

    /* loaded from: classes.dex */
    public enum DeviceType {
        HANDSET("Handset"),
        TABLET("Tablet");

        private final String deviceType;

        DeviceType(String s) {
            this.deviceType = s;
        }

        @Override // java.lang.Enum
        public final String toString() {
            return this.deviceType;
        }
    }

    /* loaded from: classes.dex */
    public enum DictionaryBehavior {
        MANUAL("Manual Add"),
        AUTOMATIC("Automatic Add");

        private final String deviceType;

        DictionaryBehavior(String s) {
            this.deviceType = s;
        }

        @Override // java.lang.Enum
        public final String toString() {
            return this.deviceType;
        }
    }

    public static String isEnabled(boolean enabled) {
        return enabled ? "on" : "off";
    }
}
