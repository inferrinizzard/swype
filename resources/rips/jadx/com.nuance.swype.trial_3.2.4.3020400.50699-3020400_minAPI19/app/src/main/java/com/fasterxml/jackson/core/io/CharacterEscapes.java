package com.fasterxml.jackson.core.io;

import com.fasterxml.jackson.core.SerializableString;
import java.io.Serializable;

/* loaded from: classes.dex */
public abstract class CharacterEscapes implements Serializable {
    public abstract int[] getEscapeCodesForAscii();

    public abstract SerializableString getEscapeSequence$428277ea();
}
