package com.nuance.swype.input.emoji;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import com.nuance.connect.common.Integers;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.input.R;
import com.nuance.swype.input.emoji.EmojiSkinAdapter;
import com.nuance.swype.util.LogManager;
import java.util.List;

/* loaded from: classes.dex */
public class PopupEmojiSkinList extends PopupWindow implements View.OnTouchListener {
    private static final int index = -1;
    protected static final LogManager.Log log = LogManager.getLog("PopupEmojiSkinList");
    private int billboardHeight;
    private Context context;
    private Rect displaySize;
    private boolean enableEcl;
    private LayoutInflater inflater;
    private EmojiSkinAdapter mAdapter;
    private View mParentView;
    private HorizontalSkinToneListView mRecyclerView;
    private ViewGroup mSkinToneContainer;
    private View pager;
    private Point popupPos;
    private SkinTonePopupManager skinTonePopupMgr;

    public PopupEmojiSkinList(Context context, LayoutInflater inflater, View pager, boolean enableEcl, int billboardHeight) {
        super(inflater.getContext());
        this.popupPos = null;
        this.enableEcl = true;
        this.context = context;
        this.pager = pager;
        this.inflater = inflater;
        this.enableEcl = enableEcl;
        this.billboardHeight = billboardHeight;
        setPopupEmojiList();
    }

    public PopupEmojiSkinList(Context context, LayoutInflater inflater, View pager) {
        super(inflater.getContext());
        this.popupPos = null;
        this.enableEcl = true;
        this.context = context;
        this.pager = pager;
        this.inflater = inflater;
        setPopupEmojiList();
    }

    private void setPopupEmojiList() {
        this.mSkinToneContainer = (ViewGroup) this.inflater.inflate(R.layout.skintone_popup_dialog, (ViewGroup) null);
        setContentView(this.mSkinToneContainer);
        this.mRecyclerView = (HorizontalSkinToneListView) this.mSkinToneContainer.findViewById(R.id.recycler_view);
        this.mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.context);
        linearLayoutManager.setOrientation(0);
        this.mRecyclerView.setLayoutManager(linearLayoutManager);
        log.d("PopupEmojiSkinList()", ": called  >>>>>>>>>> width:" + getScreenWidth() + " , " + getScreenHeight());
        this.mSkinToneContainer.measure(View.MeasureSpec.makeMeasureSpec(getScreenWidth(), Integers.STATUS_SUCCESS), View.MeasureSpec.makeMeasureSpec((getScreenHeight() * 3) / 4, Integers.STATUS_SUCCESS));
        initPopupView();
    }

    public void setSkinToneAdapter(List<Emoji> emojisList, View mParentView, EmojiInfo cell, int defaultSkinTone) {
        log.d("setSkinToneAdapter()", ": called  >>>>>>>>>>" + emojisList.size());
        this.mAdapter = new EmojiSkinAdapter(this.context, emojisList, mParentView);
        this.mAdapter.setDefaultSkinTone(defaultSkinTone);
        this.mRecyclerView.setAdapter(this.mAdapter);
        if (this.skinTonePopupMgr == null) {
            this.skinTonePopupMgr = new SkinTonePopupManager(this.pager, this.context);
        }
        this.popupPos = this.skinTonePopupMgr.preparePopup(this.mSkinToneContainer, this.mRecyclerView, cell);
        this.popupPos.y += this.billboardHeight / 2;
        if (!this.enableEcl) {
            this.popupPos.y -= this.pager.getMeasuredHeight();
        }
        log.d("showPopupKeyboardHelper()", ": called  >>>>>>>>>> :: pos=" + this.popupPos.x + " , y:: " + this.popupPos.y);
        if (mParentView != null && mParentView.getWindowToken() != null) {
            showAtLocation(mParentView, 0, this.popupPos.x, this.popupPos.y);
        }
        this.mParentView = mParentView;
    }

    public void getDefaultSkinTonePopupView() {
        View currentSkinTone = this.skinTonePopupMgr.getDefaultSkinTone();
        int position = this.mRecyclerView.getChildAdapterPosition(currentSkinTone);
        if (position == -1) {
            position = this.skinTonePopupMgr.getSkinTonePosition();
        }
        if (this.mParentView != null && (this.mParentView instanceof EmojiSkinAdapter.OnItemClickListener)) {
            log.d("setDefaultSkinTone()", ": called  >>>>>>>>>> :: position=" + position + " , currentSkinTone:: " + currentSkinTone);
            ((EmojiSkinAdapter.OnItemClickListener) this.mParentView).onItemClick(currentSkinTone, position, false);
        }
    }

    private void initPopupView() {
        log.d("initSkinPopupView(): called  >>>>>>>>>>");
        setWindowLayoutMode(-2, -2);
        setOutsideTouchable(true);
        setWidth(-2);
        setHeight(-2);
        setBackgroundDrawable(null);
        setClippingEnabled(false);
    }

    @Override // android.widget.PopupWindow
    public void showAtLocation(View parent, int gravity, int x, int y) {
        log.d("initSkinPopupView(): called  >>>>>>>>>> :: x==" + x + " , y==" + y);
        super.showAtLocation(parent, gravity, x, y);
    }

    @Override // android.widget.PopupWindow
    public void dismiss() {
        super.dismiss();
    }

    @Override // android.view.View.OnTouchListener
    @SuppressLint({"ClickableViewAccessibility"})
    public boolean onTouch(View view, MotionEvent event) {
        log.d("onTouch(): called  >>>>>>>>>>");
        return true;
    }

    protected int getScreenWidth() {
        return getDisplaySize().width();
    }

    protected int getScreenHeight() {
        return getDisplaySize().height();
    }

    private Rect getDisplaySize() {
        if (this.displaySize == null) {
            this.displaySize = IMEApplication.from(this.context).getDisplayRectSize(new Rect());
        }
        return this.displaySize;
    }

    public void touchMoveHandleBySkinPopup(int x, int y, int xOffset, int yOffset) {
        if (this.skinTonePopupMgr != null) {
            this.skinTonePopupMgr.onMove(x, y, xOffset, yOffset);
            this.skinTonePopupMgr.setSkinTonePosition(this.mAdapter.getSkinTonePosition());
        }
    }

    public void touchMoveHandleBySkinPopup(int x, int y, int xOffset) {
        if (this.skinTonePopupMgr != null) {
            this.skinTonePopupMgr.onMove(x, y, xOffset);
            this.skinTonePopupMgr.setSkinTonePosition(this.mAdapter.getSkinTonePosition());
        }
    }
}
