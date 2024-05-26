package com.nuance.input.swypecorelib;

import java.util.Locale;

/* loaded from: classes.dex */
public class DummyTextChecker implements TextChecker {
    @Override // com.nuance.input.swypecorelib.TextChecker
    public void checkRegion(String buffer, TextRegion region) {
        int count = 0;
        boolean inWord = true;
        for (int i = 0; i != region.getLength(); i++) {
            if (Character.isWhitespace(buffer.charAt(region.getOffset() + i))) {
                if (inWord && count == 4) {
                    Correction corr = new Correction(i - 4, 4);
                    region.addCorrection(corr);
                    for (int j = 0; j != 5; j++) {
                        corr.addSuggestion(getSuggestion(corr, j, buffer, region));
                    }
                }
                count = 0;
                inWord = false;
            } else {
                inWord = true;
                count++;
            }
        }
    }

    private String getSuggestion(Correction corr, int listIndex, String buffer, TextRegion region) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i != corr.getLength(); i++) {
            builder.append(buffer.charAt(region.getOffset() + i + corr.getOffset()));
        }
        String corrStr = builder.toString();
        switch (listIndex) {
            case 0:
                return corrStr;
            case 1:
                return corrStr.substring(0, corrStr.length() - 1);
            case 2:
                return corrStr.toUpperCase(Locale.getDefault());
            default:
                for (int i2 = 0; i2 != listIndex - 2; i2++) {
                    builder.append("X");
                }
                return builder.toString();
        }
    }
}
