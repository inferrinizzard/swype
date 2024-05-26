package com.nuance.connect.sqlite;

import java.util.Arrays;

/* loaded from: classes.dex */
class ChinesePredictionDataResultRow {
    final int[] attribute;
    final String fullSpell;
    final String phrase;
    final String predictionId;
    final int resultType;
    final String spell;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ChinesePredictionDataResultRow(String str, int i, String str2, String str3, String str4, int[] iArr) {
        this.predictionId = str;
        this.resultType = i;
        this.phrase = str2;
        this.spell = str3;
        this.fullSpell = str4;
        this.attribute = iArr;
    }

    public String toString() {
        return "ChinesePredictionDataResultRow id=" + this.predictionId + " resultType=" + this.resultType + " phrase=" + this.phrase + " spell=" + this.spell + " fullSpell=" + this.fullSpell + " attribute=" + Arrays.toString(this.attribute);
    }
}
