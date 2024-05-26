package com.nuance.swype.input.emoji;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Paint;
import com.nuance.input.swypecorelib.EmojiFilter;
import com.nuance.nmsp.client.util.internal.dictationresult.parser.xml.xmlResults.XMLResultsHandler;
import com.nuance.swype.input.R;
import com.nuance.swype.input.UserPreferences;
import com.nuance.swype.util.EmojiUtils;
import com.nuance.swype.util.LogManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/* loaded from: classes.dex */
public class EmojiLoader {
    private static final int BASE_HEX = 16;
    private static final String COMMA_REGEX = ",";
    private static final String PIPE_REGEX = "\\|";
    private static Context mContext;
    private static int totalRenders;
    private static final LogManager.Log log = LogManager.getLog("EmojiFilter");
    private static List<Emoji> emojis = new ArrayList();
    private static final List skinModifiers = new ArrayList(Arrays.asList(127995, 127996, 127997, 127998, 127999));
    private static final Set<String> filterEmojis = new HashSet(Arrays.asList("1F3FB", "1F3FC", "1F3FD", "1F3FE", "1F3FF"));
    private static final Set<String> filterSet = new HashSet();
    private static final Set problemBins = new HashSet(Arrays.asList("1995ªʲ", "1995ʲ", "1995ʷ", "1995ˣ", "1995ʲʷ", "2010ʷ", "2010ˣ", "2014ˣ"));
    private static Map<String, Emoji> mEmojis = new HashMap();
    private static boolean mInitialized = false;
    private static final EmojiFilter emojiFilter = new EmojiFilter() { // from class: com.nuance.swype.input.emoji.EmojiLoader.1
        @Override // com.nuance.input.swypecorelib.EmojiFilter
        public final boolean canShow(String emojiString) {
            return EmojiLoader.canShowEmoji(emojiString);
        }
    };

    /* JADX WARN: Removed duplicated region for block: B:70:0x043f  */
    /* JADX WARN: Removed duplicated region for block: B:75:0x04ce  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static void init(android.content.Context r36) {
        /*
            Method dump skipped, instructions count: 1479
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.nuance.swype.input.emoji.EmojiLoader.init(android.content.Context):void");
    }

    private static Emoji findEmojiForSkinTone(String emojiCode) {
        return mEmojis.get(convertToEmojiString(new int[]{getBaseHexCode(emojiCode)}));
    }

    private static Emoji getEmoji(String resourceType, boolean isRenderable, int skinColorCode, boolean hasSkinModifier) {
        Emoji emojiInstance = new Emoji(resourceType, isRenderable);
        emojiInstance.setDefaultSkinToneColor(skinColorCode);
        emojiInstance.setSkinToneSupport(hasSkinModifier);
        return emojiInstance;
    }

    public static EmojiFilter getFilter() {
        return emojiFilter;
    }

    static boolean canShowEmoji(String emojiString) {
        if (!mInitialized) {
            log.e("canShowEmoji: not initialized! can't filter emoji for display");
        }
        Emoji emoji = mEmojis.get(emojiString);
        if (emoji != null) {
            return emoji.isRenderable();
        }
        log.e("canShowEmoji: Unknown emoji: " + convertToHexString(emojiString));
        return false;
    }

    public static String convertToHexString(String emojiString) {
        StringBuffer emojiStringCodes = new StringBuffer();
        int length = emojiString.length();
        int offset = 0;
        while (offset < length) {
            int codepoint = emojiString.codePointAt(offset);
            emojiStringCodes.append(Integer.toHexString(codepoint) + XMLResultsHandler.SEP_SPACE);
            offset += Character.charCount(codepoint);
        }
        return emojiStringCodes.toString();
    }

    private static String convertToEmojiString(int[] codePoints) {
        StringBuilder sb = new StringBuilder();
        for (int i : codePoints) {
            sb.appendCodePoint(i);
        }
        return sb.toString();
    }

    @TargetApi(23)
    private static boolean canRenderEmoji(String testRender) {
        Paint paint = new Paint();
        try {
            return paint.hasGlyph(testRender);
        } catch (NoSuchMethodError e) {
            totalRenders++;
            log.d("canRenderEmoji: hasGlyph not available");
            float testRenderWidth = paint.measureText(testRender);
            log.d("canRenderEmoji: testRenderWidth: " + testRenderWidth);
            float tofuWidth = paint.measureText("\ufffe");
            log.d("canRenderEmoji: tofuWidth: " + tofuWidth);
            return testRenderWidth > tofuWidth;
        }
    }

    public static List<Emoji> getEmojiList() {
        if (emojis.size() > 0) {
            return emojis;
        }
        String emoji_unicode = mContext.getResources().getString(R.string.default_emoji_unicode);
        int code = getBaseHexCode(emoji_unicode);
        log.d("init: getEmojiList() : caalled >>>>>>>> code ==" + code);
        int[] codePoints = new int[2];
        codePoints[0] = code;
        Emoji emoji = new Emoji("emoji", true);
        emoji.setEmojiCode(emoji_unicode);
        emoji.setEmojiDisplayCode(convertToEmojiString(codePoints));
        emoji.setDefaultSkinToneColor(-1);
        emojis.add(emoji);
        for (int i = 0; i < skinModifiers.size(); i++) {
            int skin_tone_code = ((Integer) skinModifiers.get(i)).intValue();
            codePoints[1] = skin_tone_code;
            Emoji emoji2 = new Emoji("emoji", true);
            emoji2.setEmojiCode(emoji_unicode);
            emoji2.setDefaultSkinToneColor(skin_tone_code);
            String displayCode = convertToEmojiString(codePoints);
            emoji2.setEmojiDisplayCode(displayCode);
            emojis.add(emoji2);
        }
        return emojis;
    }

    public static Emoji getEmoji(String emojiString) {
        Emoji emoji = mEmojis.get(emojiString);
        if (emoji != null) {
            return emoji;
        }
        return null;
    }

    private static int getBaseHexCode(String unicode) {
        return Integer.parseInt(unicode, 16);
    }

    public static Emoji getDefaultSkinToneCode(Context context, Emoji emoji, EmojiCacheManager<String, Integer> emojiCacheManager) {
        int defaultSkinToneValue;
        String emojiCode = emoji.getEmojiCode();
        if (emojiCacheManager.getObjectFromCache(emojiCode) != null) {
            defaultSkinToneValue = emojiCacheManager.getObjectFromCache(emojiCode).intValue();
        } else {
            UserPreferences userPrefs = UserPreferences.from(context);
            defaultSkinToneValue = userPrefs.getCachedEmojiSkinTone(emojiCode, 0);
            if (defaultSkinToneValue == 0) {
                defaultSkinToneValue = userPrefs.getDefaultEmojiSkin().getSkinValue();
            }
        }
        if (emoji.getEmojiSkinType() != null && defaultSkinToneValue != emoji.getEmojiSkinType().getSkinValue()) {
            List<Emoji> emojiSkinToneList = emoji.getEmojiSkinToneList();
            int i = 0;
            while (true) {
                if (i < emojiSkinToneList.size()) {
                    if (emojiSkinToneList.get(i).getEmojiSkinType() == null || defaultSkinToneValue != emojiSkinToneList.get(i).getEmojiSkinType().getSkinValue()) {
                        i++;
                    } else {
                        emoji.setLastUsedSkinTone(emojiSkinToneList.get(i));
                        emoji = emojiSkinToneList.get(i);
                        break;
                    }
                } else {
                    break;
                }
            }
            return emoji;
        }
        return emoji;
    }

    public static boolean isEmoji(String emoji) {
        if (emoji == null || emoji.length() == 0) {
            return false;
        }
        int cursor = emoji.length();
        char ch = emoji.charAt(cursor - 1);
        return EmojiUtils.characterBefore(emoji, cursor, 1) > 1 || EmojiUtils.characterIsEmoji(ch);
    }
}
