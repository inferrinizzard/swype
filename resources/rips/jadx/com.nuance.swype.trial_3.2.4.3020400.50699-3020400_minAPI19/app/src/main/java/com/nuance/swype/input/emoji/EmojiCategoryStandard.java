package com.nuance.swype.input.emoji;

import android.content.Context;
import android.content.res.Resources;
import com.nuance.swype.util.LogManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* loaded from: classes.dex */
public class EmojiCategoryStandard extends EmojiCategory<Emoji> {
    private static final int BASE_HEX = 16;
    private static final String COMMA_REGEX = ",";
    private static final int TYPE_INT = 0;
    private static final int TYPE_STRING = 1;
    private static final LogManager.Log log = LogManager.getLog("EmojiCategoryStandard");
    private final int arrayRes;
    private final int arrayType;
    private Context context;
    private List<Emoji> emojis;

    public EmojiCategoryStandard(Context context, int arrayRes, int arrayType, String name, int iconResId) {
        super(name, iconResId);
        this.context = context;
        this.arrayRes = arrayRes;
        this.arrayType = arrayType;
    }

    @Override // com.nuance.swype.input.emoji.AbstractCategory
    public boolean hasItems() {
        return !getEmojiList().isEmpty();
    }

    @Override // com.nuance.swype.input.emoji.EmojiCategory
    public List<Emoji> getEmojiList() {
        log.d("EmojiCategoryStandard()", "getEmojiList(): called");
        if (this.emojis == null) {
            load();
        }
        return Collections.unmodifiableList(this.emojis);
    }

    @Override // com.nuance.swype.input.emoji.EmojiCategory
    public boolean isRecentCategory() {
        return false;
    }

    private void load() {
        Resources res = this.context.getResources();
        switch (this.arrayType) {
            case 0:
                loadCodePoints(res);
                return;
            case 1:
                loadStrings(res);
                return;
            default:
                throw new AssertionError("Bad array type");
        }
    }

    private void loadCodePoints(Resources res) {
        log.d("loadCodePoints(): called :: arrayType=" + this.arrayType);
        StringBuffer sb = new StringBuffer();
        String[] resources = res.getStringArray(this.arrayRes);
        this.emojis = new ArrayList();
        for (String resource : resources) {
            sb.setLength(0);
            for (String str : resource.split(",")) {
                int code = Integer.parseInt(str, 16);
                sb.appendCodePoint(code);
            }
            String emojiString = sb.toString();
            Emoji emoji = EmojiLoader.getEmoji(emojiString);
            if (emoji != null && emoji.isCanDisplay()) {
                log.d("loadCodePoints: called " + this.name + " :: added emoji ::  " + EmojiLoader.convertToHexString(emojiString) + " :: emoji size==" + emoji.getEmojiSkinToneList().size() + " :: emoji size==" + emoji.getEmojiDisplayCode());
                this.emojis.add(emoji);
            }
        }
    }

    private void loadStrings(Resources res) {
        String[] strings = res.getStringArray(this.arrayRes);
        this.emojis = new ArrayList();
        for (String code : strings) {
            Emoji emoji = new Emoji("string", true);
            emoji.setSkinToneSupport(false);
            emoji.setEmojiCode(code);
            emoji.setEmojiDisplayCode(code);
            this.emojis.add(emoji);
        }
    }

    @Override // com.nuance.swype.input.emoji.AbstractCategory
    public boolean isDynamic() {
        return false;
    }

    @Override // com.nuance.swype.input.emoji.AbstractCategory
    public List<String> getItemList() {
        return null;
    }
}
