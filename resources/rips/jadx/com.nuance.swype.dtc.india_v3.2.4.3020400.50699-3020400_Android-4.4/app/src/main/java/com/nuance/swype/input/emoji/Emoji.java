package com.nuance.swype.input.emoji;

import android.os.Build;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class Emoji implements Serializable {
    private static final long serialVersionUID = 1;
    private boolean canDisplay;
    private int defaultSkinToneColor;
    private String emojiCode;
    private String emojiDisplayCode;
    private boolean isFilterEmoji;
    private boolean isSkinToneSupport;
    private Emoji lastUsedSkinTone;
    private final String mDefaultRep;
    private final boolean mRenderable;
    private Emoji parentEmoji;
    private SkinToneEnum emojiSkinType = null;
    private boolean isSkinTone = false;
    private List<Emoji> emojiSkinToneList = new ArrayList();

    /* loaded from: classes.dex */
    public enum SkinToneEnum {
        NORMAL(-1),
        LIGHT(127995),
        MEDIUM_LIGHT(127996),
        MEDIUM(127997),
        MEDIUM_DARK(127998),
        DARK(127999);

        private int skinValue;

        SkinToneEnum(int skinValue) {
            this.skinValue = skinValue;
        }

        public final int getSkinValue() {
            return this.skinValue;
        }

        public static SkinToneEnum getSkinToneFromCode(int code) {
            for (SkinToneEnum e : values()) {
                if (code == e.skinValue) {
                    return e;
                }
            }
            return NORMAL;
        }
    }

    public Emoji(String defaultRep, boolean renderable) {
        this.mDefaultRep = defaultRep;
        this.mRenderable = renderable;
        setCanDisplay(renderable);
        this.emojiSkinToneList.add(this);
    }

    public boolean isRenderable() {
        return this.mRenderable;
    }

    public String defaultRep() {
        return this.mDefaultRep;
    }

    public boolean isSkinToneSupport() {
        if (Build.VERSION.SDK_INT <= 23) {
            return false;
        }
        return this.isSkinToneSupport;
    }

    public void setSkinToneSupport(boolean skinToneSupport) {
        this.isSkinToneSupport = skinToneSupport;
    }

    public boolean isFilterEmoji() {
        return this.isFilterEmoji;
    }

    public void setFilterEmoji(boolean filterEmoji) {
        this.isFilterEmoji = filterEmoji;
    }

    public Emoji getLastUsedSkinTone() {
        return this.lastUsedSkinTone;
    }

    public void setLastUsedSkinTone(Emoji lastUsedSkinTone) {
        this.lastUsedSkinTone = lastUsedSkinTone;
    }

    public String getEmojiDisplayCode() {
        return this.emojiDisplayCode;
    }

    public void setEmojiDisplayCode(String emojiDisplayCode) {
        this.emojiDisplayCode = emojiDisplayCode;
    }

    public String getEmojiCode() {
        return this.emojiCode;
    }

    public void setEmojiCode(String emojiCode) {
        this.emojiCode = emojiCode;
    }

    public int getDefaultSkinToneColor() {
        return this.defaultSkinToneColor;
    }

    public void setDefaultSkinToneColor(int defaultSkinToneColor) {
        this.defaultSkinToneColor = defaultSkinToneColor;
    }

    public boolean isCanDisplay() {
        return this.canDisplay;
    }

    public void setCanDisplay(boolean canDisplay) {
        this.canDisplay = canDisplay;
    }

    public List<Emoji> getEmojiSkinToneList() {
        return this.emojiSkinToneList;
    }

    public void setEmojiSkinToneList(Emoji emoji) {
        this.emojiSkinToneList.add(emoji);
    }

    public SkinToneEnum getEmojiSkinType() {
        return this.emojiSkinType;
    }

    public void setEmojiSkinType(SkinToneEnum emojiSkinType) {
        this.emojiSkinType = emojiSkinType;
    }

    public void clearSkinList() {
        if (this.emojiSkinToneList.size() > 0) {
            this.emojiSkinToneList.clear();
        }
    }

    public boolean isSkinTone() {
        return this.isSkinTone;
    }

    public void setSkinTone(boolean skinTone) {
        this.isSkinTone = skinTone;
    }

    public Emoji getParentEmoji() {
        return this.parentEmoji;
    }

    public void setParentEmoji(Emoji parentEmoji) {
        this.parentEmoji = parentEmoji;
    }
}
