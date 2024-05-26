package com.nuance.swype.input.emoji;

import android.util.Base64InputStream;
import android.util.Base64OutputStream;
import com.nuance.swype.input.AppPreferences;
import com.nuance.swype.util.LogManager;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONTokener;

/* loaded from: classes.dex */
public class MoveToFirstStrategyEmojiDecorator implements RecentListOrderingStrategy<Emoji, Emoji> {
    private static final String PREF_EMOJI_RECENT_LIST = "pref_emoji_recent_list";
    private static final String defaultRep = "emoji";
    protected static final LogManager.Log log = LogManager.getLog("StrategyEmojiDecorator");
    MoveToFirstStrategyEmoji strategy;

    public MoveToFirstStrategyEmojiDecorator(MoveToFirstStrategyEmoji orderingStrategy) {
        this.strategy = orderingStrategy;
        readBackupDataStore();
        readFromStore();
    }

    @Override // com.nuance.swype.input.emoji.RecentListOrderingStrategy
    public void add(Emoji emoji) {
        this.strategy.add(emoji);
    }

    @Override // com.nuance.swype.input.emoji.RecentListOrderingStrategy
    public List<Emoji> getAll() {
        return this.strategy.getAll();
    }

    @Override // com.nuance.swype.input.emoji.RecentListOrderingStrategy
    public void saveToStore() {
        log.i("saveToStore(): called >>>>>>>>>> ");
        StringBuilder sb = new StringBuilder();
        AppPreferences appPrefs = AppPreferences.from(this.strategy.getContext());
        Iterator<Emoji> it = this.strategy.getCache().iterator();
        while (it.hasNext()) {
            Emoji emoji = it.next();
            String objectOutput = write(emoji);
            sb.append(objectOutput);
            sb.append(",");
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }
        appPrefs.setEmojiRecentCategoryList(sb.toString());
    }

    private String write(Emoji emoji) {
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        try {
            ObjectOutputStream objectOutput = new ObjectOutputStream(arrayOutputStream);
            objectOutput.writeObject(emoji);
            byte[] data = arrayOutputStream.toByteArray();
            objectOutput.close();
            arrayOutputStream.close();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Base64OutputStream b64 = new Base64OutputStream(out, 0);
            b64.write(data);
            b64.close();
            out.close();
            return new String(out.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void read() throws Exception {
        ObjectInputStream in;
        for (String str : AppPreferences.from(this.strategy.getContext()).getEmojiCategoryaRecentList().split(",")) {
            byte[] bytes = str.getBytes();
            if (bytes.length != 0) {
                ObjectInputStream in2 = null;
                try {
                    ByteArrayInputStream byteArray = new ByteArrayInputStream(bytes);
                    Base64InputStream base64InputStream = new Base64InputStream(byteArray, 0);
                    in = new ObjectInputStream(base64InputStream);
                } catch (Throwable th) {
                    th = th;
                }
                try {
                    Emoji emoji = (Emoji) in.readObject();
                    this.strategy.getCache().add(emoji);
                    in.close();
                } catch (Throwable th2) {
                    th = th2;
                    in2 = in;
                    if (in2 != null) {
                        in2.close();
                    }
                    throw th;
                }
            } else {
                return;
            }
        }
    }

    @Override // com.nuance.swype.input.emoji.RecentListOrderingStrategy
    public void readFromStore() {
        try {
            read();
        } catch (Exception e) {
            log.d("readFromStore(): called  : JSONException: ", e.toString());
        }
    }

    public void readBackupDataStore() {
        log.i("readBackupDataStore(): called  : >>>>>>>>: ");
        AppPreferences appPrefs = AppPreferences.from(this.strategy.getContext());
        if (appPrefs.contains(PREF_EMOJI_RECENT_LIST)) {
            this.strategy.getCache().clear();
            String jsonString = appPrefs.getEmojiRecentList();
            appPrefs.remove(PREF_EMOJI_RECENT_LIST);
            if (!jsonString.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                try {
                    JSONArray array = (JSONArray) new JSONTokener(jsonString).nextValue();
                    for (int i = 0; i < array.length(); i++) {
                        Emoji emoji = EmojiLoader.getEmoji(array.getString(i));
                        if (emoji == null) {
                            emoji = new Emoji(defaultRep, true);
                            emoji.setSkinToneSupport(false);
                            emoji.setEmojiDisplayCode(array.getString(i));
                        }
                        String objectOutput = write(emoji);
                        sb.append(objectOutput);
                        sb.append(",");
                    }
                    if (sb.length() > 0) {
                        sb.setLength(sb.length() - 1);
                    }
                    appPrefs.setEmojiRecentCategoryList(sb.toString());
                } catch (Exception e) {
                }
            }
        }
    }

    @Override // com.nuance.swype.input.emoji.RecentListOrderingStrategy
    public int getSize() {
        return this.strategy.getSize();
    }
}
