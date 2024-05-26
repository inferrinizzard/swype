package com.nuance.swype.input.emoji;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.nuance.swype.input.R;
import com.nuance.swype.util.LogManager;
import java.util.List;

/* loaded from: classes.dex */
public class EmojiSkinAdapter extends RecyclerView.Adapter<ViewHolder> {
    protected static final LogManager.Log log = LogManager.getLog("EmojiSkinAdapter");
    private View parent;
    private List<Emoji> skinToneList;
    private int skinTonePosition;
    private int skinToneValue;
    private float textSizeUnscaled;

    /* loaded from: classes.dex */
    public interface OnItemClickListener {
        void onItemClick(View view, int i, boolean z);
    }

    public EmojiSkinAdapter(Context context, List<Emoji> skinToneList, View parent) {
        this.skinToneList = skinToneList;
        this.textSizeUnscaled = context.getResources().getDimension(R.dimen.emoji_text_size);
        this.parent = parent;
    }

    private void setSkinTonePosition(int position) {
        this.skinTonePosition = position;
    }

    public int getSkinTonePosition() {
        return this.skinTonePosition;
    }

    @Override // android.support.v7.widget.RecyclerView.Adapter
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.emoji_skin_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override // android.support.v7.widget.RecyclerView.Adapter
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        log.d("createCells()", "called : default value==" + this.skinToneValue);
        Emoji emoji = this.skinToneList.get(i);
        if (emoji != null && emoji.getEmojiSkinType() != null && this.skinToneValue == emoji.getEmojiSkinType().getSkinValue()) {
            viewHolder.itemView.setPressed(true);
            setSkinTonePosition(i);
        }
        if (emoji != null) {
            setSkinToneValue(viewHolder.tv_skin_name, emoji.getEmojiDisplayCode());
        }
        viewHolder.setClickListener(new OnItemClickListener() { // from class: com.nuance.swype.input.emoji.EmojiSkinAdapter.1
            @Override // com.nuance.swype.input.emoji.EmojiSkinAdapter.OnItemClickListener
            public void onItemClick(View view, int position, boolean isLongClick) {
                if (EmojiSkinAdapter.this.parent instanceof EmojiPageView) {
                    ((EmojiPageView) EmojiSkinAdapter.this.parent).setSelectedSkinTone(position);
                }
            }
        });
    }

    public void setSkinToneValue(SkinToneView drawView, String codePoint) {
        drawView.setPaintTextSize(this.textSizeUnscaled);
        drawView.setTextValue(codePoint);
    }

    @Override // android.support.v7.widget.RecyclerView.Adapter
    public int getItemCount() {
        return this.skinToneList.size();
    }

    public void setDefaultSkinTone(int skinToneValue) {
        this.skinToneValue = skinToneValue;
    }

    /* loaded from: classes.dex */
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private OnItemClickListener clickListener;
        public View itemView;
        public SkinToneView tv_skin_name;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.tv_skin_name = (SkinToneView) itemView.findViewById(R.id.tv_skin_name);
            itemView.setOnClickListener(this);
        }

        public void setClickListener(OnItemClickListener itemClickListener) {
            this.clickListener = itemClickListener;
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            if (this.clickListener != null) {
                this.clickListener.onItemClick(view, getPosition(), false);
            }
        }
    }
}
