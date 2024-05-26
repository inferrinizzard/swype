package com.nuance.swype.input;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import com.nuance.swype.input.emoji.Emoji;
import com.nuance.swype.input.emoji.SkinToneView;
import com.nuance.swype.input.settings.SettingsPrefs;
import com.nuance.swype.util.LogManager;
import java.util.List;

/* loaded from: classes.dex */
public class EmojiSkinToneListAdapter extends BaseAdapter {
    private static final LogManager.Log log = LogManager.getLog("EmojiSkinToneListAdapter");
    final Context mContext;
    private SettingsPrefs settingsPrefs;
    final List<Emoji> skinToneList;
    float textSizeUnscaled;

    public EmojiSkinToneListAdapter(List<Emoji> skinToneList, Context mContext, SettingsPrefs settingsPrefs) {
        this.skinToneList = skinToneList;
        this.mContext = mContext;
        this.settingsPrefs = settingsPrefs;
        Resources r = mContext.getResources();
        this.textSizeUnscaled = r.getDimension(R.dimen.emoji_text_size);
    }

    @Override // android.widget.Adapter
    public int getCount() {
        return this.skinToneList.size();
    }

    @Override // android.widget.Adapter
    public Object getItem(int i) {
        return null;
    }

    @Override // android.widget.Adapter
    public long getItemId(int position) {
        return position;
    }

    @Override // android.widget.Adapter
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(this.mContext).inflate(R.layout.emoji_skin_tone_item, parent, false);
            SkinToneView tv_skin_name = (SkinToneView) convertView.findViewById(R.id.tv_skin_name);
            RadioButton rd_skin_btn = (RadioButton) convertView.findViewById(R.id.rd_skin_btn);
            convertView.setTag(new Holder(tv_skin_name, rd_skin_btn));
        }
        final Holder holder = (Holder) convertView.getTag();
        String codePoint = this.skinToneList.get(position).getEmojiDisplayCode();
        setSkinToneValue(holder.tv_skin_name, codePoint);
        holder.rd_skin_btn.setChecked(false);
        final UserPreferences userPrefs = UserPreferences.from(this.mContext);
        if (userPrefs.getDefaultEmojiSkin().getSkinValue() == this.skinToneList.get(position).getDefaultSkinToneColor()) {
            holder.rd_skin_btn.setChecked(true);
        } else {
            holder.rd_skin_btn.setChecked(false);
        }
        holder.rd_skin_btn.setOnClickListener(new View.OnClickListener() { // from class: com.nuance.swype.input.EmojiSkinToneListAdapter.1
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                holder.rd_skin_btn.setChecked(true);
                Emoji.SkinToneEnum skinToneEnum = Emoji.SkinToneEnum.getSkinToneFromCode(EmojiSkinToneListAdapter.this.skinToneList.get(position).getDefaultSkinToneColor());
                userPrefs.setDefaultEmojiSkin(skinToneEnum);
                EmojiSkinToneListAdapter.this.settingsPrefs.setUserSelectEmojiSkinTone();
            }
        });
        return convertView;
    }

    /* loaded from: classes.dex */
    private static class Holder {
        private final RadioButton rd_skin_btn;
        private final SkinToneView tv_skin_name;

        public Holder(SkinToneView tv_skin_name, RadioButton rd_skin_btn) {
            this.tv_skin_name = tv_skin_name;
            this.rd_skin_btn = rd_skin_btn;
        }
    }

    public void setSkinToneValue(SkinToneView drawView, String codePoint) {
        drawView.setPaintTextSize(this.textSizeUnscaled);
        drawView.setTextValue(codePoint);
    }
}
