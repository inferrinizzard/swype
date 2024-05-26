package com.nuance.swype.input;

import android.graphics.drawable.Drawable;
import com.nuance.input.swypecorelib.WordCandidate;

/* loaded from: classes.dex */
public class DrawableCandidate extends WordCandidate {
    public final Drawable icon;

    public DrawableCandidate(Drawable icon) {
        super("");
        this.icon = icon;
    }

    @Override // com.nuance.input.swypecorelib.WordCandidate
    public boolean equals(Object o) {
        return this == o;
    }

    @Override // com.nuance.input.swypecorelib.WordCandidate
    public int hashCode() {
        return System.identityHashCode(this);
    }

    @Override // com.nuance.input.swypecorelib.WordCandidate
    public WordCandidate.Source source() {
        return WordCandidate.Source.WORD_SOURCE_DRAWABLE;
    }
}
