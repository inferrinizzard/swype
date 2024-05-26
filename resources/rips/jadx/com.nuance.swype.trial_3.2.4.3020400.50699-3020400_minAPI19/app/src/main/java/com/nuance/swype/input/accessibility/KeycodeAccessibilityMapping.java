package com.nuance.swype.input.accessibility;

import android.content.Context;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.util.SparseArray;
import android.util.Xml;
import com.nuance.swype.input.R;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParserException;

/* loaded from: classes.dex */
public class KeycodeAccessibilityMapping {
    private static final String MAPPING_TAG_NAME = "Mapping";
    private SparseArray<KeycodeAccessibilityLabelMapping> mapping = new SparseArray<>();

    public KeycodeAccessibilityMapping(Context context, int resourceId) {
        parseXMLResource(context, resourceId);
    }

    public KeycodeAccessibilityLabelMapping getAccessibilityMapping(int keycode) {
        return this.mapping.get(keycode);
    }

    private void parseXMLResource(Context context, int resourceId) {
        try {
            XmlResourceParser parser = context.getResources().getXml(resourceId);
            try {
                for (int eventType = parser.next(); eventType != 1; eventType = parser.next()) {
                    String tag = parser.getName();
                    switch (eventType) {
                        case 2:
                            if (tag.equals(MAPPING_TAG_NAME)) {
                                KeycodeAccessibilityLabelMapping mp = new KeycodeAccessibilityLabelMapping(context, parser);
                                if (mp.hasCode()) {
                                    this.mapping.put(mp.getCode(), mp);
                                    break;
                                } else {
                                    break;
                                }
                            } else {
                                break;
                            }
                    }
                }
            } finally {
                if (parser != null) {
                    parser.close();
                }
            }
        } catch (IOException e) {
        } catch (XmlPullParserException e2) {
        }
    }

    /* loaded from: classes.dex */
    public static class KeycodeAccessibilityLabelMapping {
        CharSequence altAccessibilityLabel;
        int code = -1;
        CharSequence label;
        CharSequence labelOnPhone;
        CharSequence labelOnShiftedPhone;
        CharSequence labelOnSymbols;
        CharSequence labelOnSymbolsShifted;
        CharSequence labelOnTextShiftedAllCap;
        CharSequence labelOnTextShiftedCap;
        CharSequence labelOnTextShiftedLowerCase;
        CharSequence shiftedAltAccessibilityLabel;

        public KeycodeAccessibilityLabelMapping(Context context, XmlResourceParser parser) {
            extractAttributes(context, parser);
        }

        private void extractAttributes(Context context, XmlResourceParser parser) {
            TypedArray a = context.obtainStyledAttributes(Xml.asAttributeSet(parser), R.styleable.KeycodeAccessibilityLabelMapping);
            this.code = a.getInt(R.styleable.KeycodeAccessibilityLabelMapping_code, -1);
            this.label = a.getText(R.styleable.KeycodeAccessibilityLabelMapping_accessibilityLabel);
            this.labelOnSymbols = a.getText(R.styleable.KeycodeAccessibilityLabelMapping_accessibilityLabelOnSymbols);
            this.labelOnSymbolsShifted = a.getText(R.styleable.KeycodeAccessibilityLabelMapping_accessibilityLabelOnShiftedSymbols);
            this.altAccessibilityLabel = a.getText(R.styleable.KeycodeAccessibilityLabelMapping_altAccessibilityLabel);
            this.shiftedAltAccessibilityLabel = a.getText(R.styleable.KeycodeAccessibilityLabelMapping_shiftedAltAccessibilityLabel);
            this.labelOnTextShiftedLowerCase = a.getText(R.styleable.KeycodeAccessibilityLabelMapping_accessibilityLabelOnLowerCase);
            this.labelOnTextShiftedCap = a.getText(R.styleable.KeycodeAccessibilityLabelMapping_accessibilityLabelOnCap);
            this.labelOnTextShiftedAllCap = a.getText(R.styleable.KeycodeAccessibilityLabelMapping_accessibilityLabelOnAllCap);
            this.labelOnShiftedPhone = a.getText(R.styleable.KeycodeAccessibilityLabelMapping_accessibilityLabelOnShiftedPhone);
            this.labelOnPhone = a.getText(R.styleable.KeycodeAccessibilityLabelMapping_accessibilityLabelOnPhone);
            a.recycle();
        }

        public CharSequence getLabel() {
            return this.label;
        }

        public int getCode() {
            return this.code;
        }

        public boolean hasCode() {
            return this.code != -1;
        }

        public CharSequence getLabelOnSymbols() {
            return this.labelOnSymbols;
        }

        public CharSequence getLabelOnSymbolsShifted() {
            return this.labelOnSymbolsShifted;
        }

        public CharSequence getLabelOnTextShiftedLowerCase() {
            return this.labelOnTextShiftedLowerCase;
        }

        public CharSequence getLabelOnTextShiftedCap() {
            return this.labelOnTextShiftedCap;
        }

        public CharSequence getLabelOnTextShiftedAllCap() {
            return this.labelOnTextShiftedAllCap;
        }

        public CharSequence getAltLabel() {
            return this.altAccessibilityLabel;
        }

        public CharSequence getShiftedAltLabel() {
            return this.shiftedAltAccessibilityLabel;
        }

        public CharSequence getLableOnShiftedPhone() {
            return this.labelOnShiftedPhone;
        }

        public CharSequence getLableOnPhone() {
            return this.labelOnPhone;
        }
    }
}
