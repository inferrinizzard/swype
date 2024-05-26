package com.nuance.swype.input.chinese.symbol;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TabHost;
import com.nuance.android.compat.ViewCompat;
import com.nuance.android.util.WeakReferenceHandler;
import com.nuance.swype.input.IME;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.input.InputView;
import com.nuance.swype.input.KeyboardEx;
import com.nuance.swype.input.KeyboardViewEx;
import com.nuance.swype.input.R;
import com.nuance.swype.input.chinese.ChineseFSHandWritingInputView;
import com.nuance.swype.input.chinese.ChineseKeyboardViewEx;
import com.nuance.swype.input.emoji.AbstractCategory;
import com.nuance.swype.input.emoji.MoveToFirstStrategy;
import com.nuance.swype.input.emoji.RecentListManager;
import com.nuance.swype.input.korean.KoreanHandWritingInputView;
import com.nuance.swype.input.view.InputContainerView;
import com.nuance.swype.stats.UsageManager;
import com.nuance.swype.util.Callback;
import com.nuance.swype.util.LogManager;
import com.nuance.swype.view.TintDrawable;
import com.nuance.swype.view.TintDrawableCompat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/* loaded from: classes.dex */
public class SymbolInputController implements ViewPager.OnPageChangeListener, TabHost.OnTabChangeListener {
    private static final int MSG_PRESS_HOLD = 1001;
    private static final boolean enableAnimDefault = true;
    private static final LogManager.Log log = LogManager.getLog("SymbolInputController");
    private static final LogManager.Trace trace = LogManager.getTrace();
    private SymbolViewPagerAdapter adapter;
    private SymbolCategoryList categoryList;
    private Context context;
    private ImageButton deleteButton;
    private HorizontalScrollView hScrollView;
    private LayoutInflater inflater;
    private ImageButton keyboardButton;
    private int longPressHoldTimeout;
    private SymbolViewPager pager;
    private ImageButton spaceButton;
    private SymbolInputView symbolInputView;
    private TabHost tabHost;
    protected List<ArrayList<KeyboardEx.GridKeyInfo>> mRows = new ArrayList();
    private float textScale = 1.0f;
    private final View.OnTouchListener actionKeyTouchHandler = new View.OnTouchListener() { // from class: com.nuance.swype.input.chinese.symbol.SymbolInputController.3
        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        /* JADX WARN: Code restructure failed: missing block: B:6:0x0016, code lost:            return true;     */
        @Override // android.view.View.OnTouchListener
        @android.annotation.SuppressLint({"ClickableViewAccessibility"})
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        public boolean onTouch(android.view.View r7, android.view.MotionEvent r8) {
            /*
                r6 = this;
                r5 = 1001(0x3e9, float:1.403E-42)
                r4 = 1
                r3 = 0
                r0 = -1
                int r1 = r7.getId()
                int r2 = com.nuance.swype.input.R.id.emoji_input_view_space_key
                if (r1 != r2) goto L17
                r0 = 32
            Lf:
                int r2 = r8.getAction()
                switch(r2) {
                    case 0: goto L1e;
                    case 1: goto L32;
                    case 2: goto L16;
                    case 3: goto L44;
                    default: goto L16;
                }
            L16:
                return r4
            L17:
                int r2 = com.nuance.swype.input.R.id.emoji_input_view_delete_key
                if (r1 != r2) goto Lf
                r0 = 8
                goto Lf
            L1e:
                r7.setPressed(r4)
                com.nuance.swype.input.chinese.symbol.SymbolInputController r2 = com.nuance.swype.input.chinese.symbol.SymbolInputController.this
                r2.playKeyFeedback(r0)
                com.nuance.swype.input.chinese.symbol.SymbolInputController r2 = com.nuance.swype.input.chinese.symbol.SymbolInputController.this
                com.nuance.swype.input.chinese.symbol.SymbolInputController r3 = com.nuance.swype.input.chinese.symbol.SymbolInputController.this
                int r3 = com.nuance.swype.input.chinese.symbol.SymbolInputController.access$300(r3)
                r2.sendPressHoldMessage(r3, r0)
                goto L16
            L32:
                r7.setPressed(r3)
                com.nuance.swype.input.chinese.symbol.SymbolInputController r2 = com.nuance.swype.input.chinese.symbol.SymbolInputController.this
                android.os.Handler r2 = com.nuance.swype.input.chinese.symbol.SymbolInputController.access$400(r2)
                r2.removeMessages(r5)
                com.nuance.swype.input.chinese.symbol.SymbolInputController r2 = com.nuance.swype.input.chinese.symbol.SymbolInputController.this
                r2.handleKey(r0)
                goto L16
            L44:
                r7.setPressed(r3)
                com.nuance.swype.input.chinese.symbol.SymbolInputController r2 = com.nuance.swype.input.chinese.symbol.SymbolInputController.this
                android.os.Handler r2 = com.nuance.swype.input.chinese.symbol.SymbolInputController.access$400(r2)
                r2.removeMessages(r5)
                goto L16
            */
            throw new UnsupportedOperationException("Method not decompiled: com.nuance.swype.input.chinese.symbol.SymbolInputController.AnonymousClass3.onTouch(android.view.View, android.view.MotionEvent):boolean");
        }
    };
    private Handler.Callback handlerCallback = new Handler.Callback() { // from class: com.nuance.swype.input.chinese.symbol.SymbolInputController.4
        @Override // android.os.Handler.Callback
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case SymbolInputController.MSG_PRESS_HOLD /* 1001 */:
                    int actionKey = msg.arg1;
                    SymbolInputController.this.handleLongPressOnKey(actionKey);
                    IME ime = IMEApplication.from(SymbolInputController.this.context).getIME();
                    if (ime != null && ime.getCurrentInputView() != null) {
                        int delay = ime.getCurrentInputView().computeLongPressableTimeout();
                        SymbolInputController.this.sendPressHoldMessage(delay, actionKey);
                    }
                    return true;
                default:
                    return false;
            }
        }
    };
    private boolean showOnStart = false;
    private Callback<? extends Runnable> resetShowOnStartCallback = Callback.create(new Runnable() { // from class: com.nuance.swype.input.chinese.symbol.SymbolInputController.5
        @Override // java.lang.Runnable
        public void run() {
            SymbolInputController.log.d("run(): resetting show on start flag");
            SymbolInputController.this.showOnStart = false;
        }
    });
    private final Handler pressHoldHandler = WeakReferenceHandler.create(this.handlerCallback);

    public void setContext(Context context) {
        this.context = context;
        this.longPressHoldTimeout = this.context.getResources().getInteger(R.integer.short_press_timeout_ms);
        int maxSize = context.getResources().getInteger(R.integer.emoji_recent_list_max);
        MoveToFirstStrategy strategy = new MoveToFirstStrategy(context, maxSize);
        MoveToFirstStrategySymbolDecorator decorator = new MoveToFirstStrategySymbolDecorator(strategy);
        RecentListManager recentListManager = new RecentListManager(decorator);
        this.categoryList = new SymbolCategoryList(context, recentListManager);
    }

    @SuppressLint({"InflateParams"})
    private void createView() {
        if (this.symbolInputView == null) {
            log.d("createView(): inflating symbol input view...");
            IMEApplication app = IMEApplication.from(this.context);
            IME ime = app.getIME();
            InputContainerView containerView = ime.getInputContainerView();
            if (containerView != null) {
                containerView.detachOverlayView();
            }
            this.inflater = app.getThemedLayoutInflater(ime.getLayoutInflater());
            IMEApplication.from(ime).getThemeLoader().setLayoutInflaterFactory(this.inflater);
            this.symbolInputView = (SymbolInputView) this.inflater.inflate(R.layout.symbol_input_view, (ViewGroup) null);
            Drawable background = app.getThemedDrawable(R.attr.emojiBackground);
            ViewCompat.setBackground(this.symbolInputView, background);
            app.getThemeLoader().applyTheme(this.symbolInputView);
            this.pager = (SymbolViewPager) this.symbolInputView.findViewById(R.id.symbol_input_view_pager);
            this.pager.setOnPageChangeListener(this);
            initActionKeys();
            initCategoryTabs();
            createAdapter();
            app.getThemeLoader().applyTheme(this.pager);
        }
    }

    private void createAdapter() {
        FrameLayout flv;
        ChineseKeyboardViewEx keyboardViewEx;
        this.adapter = new SymbolViewPagerAdapter();
        this.pager.setAdapter(this.adapter);
        for (AbstractCategory category : this.categoryList.getAllCategories()) {
            String tag = category.getName();
            if (tag.equalsIgnoreCase(this.context.getResources().getString(R.string.chinese_symbol_recent))) {
                flv = (FrameLayout) this.inflater.inflate(R.layout.symbol_view_recent, (ViewGroup) this.pager, false);
                keyboardViewEx = (ChineseKeyboardViewEx) flv.findViewById(R.id.symbol_keyboardViewEx_recent);
            } else {
                if (!tag.equalsIgnoreCase(this.context.getResources().getString(R.string.chinese_symbol_english))) {
                    if (tag.equalsIgnoreCase(this.context.getResources().getString(R.string.chinese_symbol_chinese_symbols))) {
                        flv = (FrameLayout) this.inflater.inflate(R.layout.symbol_view_chinese_symbols, (ViewGroup) this.pager, false);
                        keyboardViewEx = (ChineseKeyboardViewEx) flv.findViewById(R.id.symbol_keyboardViewEx_chinese_symbols);
                    } else if (tag.equalsIgnoreCase(this.context.getResources().getString(R.string.chinese_symbol_emotions))) {
                        flv = (FrameLayout) this.inflater.inflate(R.layout.symbol_view_emotions, (ViewGroup) this.pager, false);
                        keyboardViewEx = (ChineseKeyboardViewEx) flv.findViewById(R.id.symbol_keyboardViewEx_emotions);
                    } else if (tag.equalsIgnoreCase(this.context.getResources().getString(R.string.chinese_symbol_network))) {
                        flv = (FrameLayout) this.inflater.inflate(R.layout.symbol_view_network, (ViewGroup) this.pager, false);
                        keyboardViewEx = (ChineseKeyboardViewEx) flv.findViewById(R.id.symbol_keyboardViewEx_network);
                    } else if (tag.equalsIgnoreCase(this.context.getResources().getString(R.string.chinese_symbol_picture))) {
                        flv = (FrameLayout) this.inflater.inflate(R.layout.symbol_view_picture, (ViewGroup) this.pager, false);
                        keyboardViewEx = (ChineseKeyboardViewEx) flv.findViewById(R.id.symbol_keyboardViewEx_picture);
                    }
                }
                flv = (FrameLayout) this.inflater.inflate(R.layout.symbol_view_english, (ViewGroup) this.pager, false);
                keyboardViewEx = (ChineseKeyboardViewEx) flv.findViewById(R.id.symbol_keyboardViewEx_english);
            }
            final IME ime = IMEApplication.from(this.context).getIME();
            keyboardViewEx.setInputView(ime.getCurrentInputView());
            keyboardViewEx.setKeyBackground(R.drawable.emoji_key_action);
            List<CharSequence> csList = new ArrayList<>();
            for (String str : category.getItemList()) {
                csList.add(str);
            }
            keyboardViewEx.setSymbolSource(csList);
            keyboardViewEx.setDoubleBuffered(false);
            setGridCandidates(csList, ime.getCurrentInputView().getMeasuredWidth());
            int height = (ime.getCurrentInputView().getMeasuredWidth() * 1) / 2;
            KeyboardEx keyboard = new KeyboardEx(this.context, R.xml.kbd_chinese_symbol_template, this.mRows, ime.getCurrentInputView().getMeasuredWidth(), height, ime.getCurrentInputView().getKeyboard().getKeyboardDockMode(), true);
            keyboardViewEx.setKeyboard(keyboard);
            keyboardViewEx.setIme(ime);
            keyboardViewEx.setOnKeyboardActionListener(new KeyboardViewEx.KeyboardActionAdapter() { // from class: com.nuance.swype.input.chinese.symbol.SymbolInputController.1
                @Override // com.nuance.swype.input.KeyboardViewEx.OnKeyboardActionListener
                public void onText(CharSequence text, long eventTime) {
                    if (ime.getCurrentInputView().getCurrentInputLanguage().isChineseLanguage()) {
                        if (SymbolInputController.this.tabHost.getCurrentTab() != 0) {
                            SymbolInputController.this.categoryList.getRecentCat().add(text.toString(), false);
                            SymbolInputController.this.refreshDynamicPages();
                            SymbolInputController.this.adapter.notifyDataSetChanged();
                        }
                        InputView iv = ime.getCurrentInputView();
                        if (iv != null) {
                            iv.onText(text, 0L);
                            iv.moveCursorToMiddle(ime.getCurrentInputConnection(), text.toString());
                        }
                    }
                }
            });
            log.d("createAdapter...tag: ", tag, "...view: ", flv);
            this.adapter.addView(flv);
        }
        this.adapter.notifyDataSetChanged();
    }

    public void refreshDynamicPages() {
        View view = this.adapter.getView(0);
        ChineseKeyboardViewEx keyboardViewEx = (ChineseKeyboardViewEx) view.findViewById(R.id.symbol_keyboardViewEx_recent);
        Collection<AbstractCategory> categories = this.categoryList.getAllCategories();
        AbstractCategory recentCat = null;
        for (AbstractCategory category : categories) {
            if (category.getName().equalsIgnoreCase(this.context.getResources().getString(R.string.chinese_symbol_recent))) {
                recentCat = category;
            }
        }
        List<CharSequence> csList = new ArrayList<>();
        for (String str : recentCat.getItemList()) {
            csList.add(str);
        }
        keyboardViewEx.setSymbolSource(csList);
        keyboardViewEx.setDoubleBuffered(false);
        IME ime = IMEApplication.from(this.context).getIME();
        setGridCandidates(csList, ime.getCurrentInputView().getMeasuredWidth());
        int height = (ime.getCurrentInputView().getMeasuredWidth() * 1) / 2;
        KeyboardEx keyboard = new KeyboardEx(this.context, R.xml.kbd_chinese_symbol_template, this.mRows, ime.getCurrentInputView().getMeasuredWidth(), height, ime.getCurrentInputView().getKeyboard().getKeyboardDockMode(), true);
        keyboardViewEx.setKeyboard(keyboard);
        keyboardViewEx.removeAllPendingActions();
        view.invalidate();
        keyboardViewEx.invalidate();
    }

    @SuppressLint({"InflateParams"})
    private void initCategoryTabs() {
        this.tabHost = (TabHost) this.symbolInputView.findViewById(R.id.emoji_input_view_categories);
        this.tabHost.setup();
        IMEApplication app = IMEApplication.from(this.context);
        this.inflater = app.getThemedLayoutInflater(LayoutInflater.from(this.context));
        this.hScrollView = (HorizontalScrollView) this.symbolInputView.findViewById(R.id.horizontalScrollView);
        for (AbstractCategory category : this.categoryList.getAllCategories()) {
            TabHost.TabSpec newTabSpec = this.tabHost.newTabSpec(category.getName());
            newTabSpec.setContent(R.id.emoji_input_view_empty_view);
            ImageView view = (ImageView) this.inflater.inflate(R.layout.symbol_category_item, (ViewGroup) null);
            TintDrawable drawable = TintDrawableCompat.createTintDrawable(this.context, category.getIconRes());
            ColorStateList color = view.getContext().getResources().getColorStateList(R.color.color_emoji_category_icon);
            drawable.setTintParams(color, PorterDuff.Mode.SRC_ATOP);
            view.setImageDrawable(drawable);
            newTabSpec.setIndicator(view);
            this.tabHost.addTab(newTabSpec);
        }
        this.tabHost.setOnTabChangedListener(this);
    }

    private void initActionKeys() {
        this.keyboardButton = (ImageButton) this.symbolInputView.findViewById(R.id.emoji_input_view_keyboard_key);
        this.keyboardButton.setOnClickListener(new View.OnClickListener() { // from class: com.nuance.swype.input.chinese.symbol.SymbolInputController.2
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                SymbolInputController.this.playKeyFeedback(0);
                SymbolInputController.this.hide();
            }
        });
        setButtonBackground(this.keyboardButton, R.drawable.emoji_key_action);
        setButtonIcon(this.keyboardButton, R.attr.emojiIconKeyboard);
        this.spaceButton = (ImageButton) this.symbolInputView.findViewById(R.id.emoji_input_view_space_key);
        this.spaceButton.setOnTouchListener(this.actionKeyTouchHandler);
        setButtonBackground(this.spaceButton, R.drawable.emoji_key_space);
        this.deleteButton = (ImageButton) this.symbolInputView.findViewById(R.id.emoji_input_view_delete_key);
        this.deleteButton.setOnTouchListener(this.actionKeyTouchHandler);
        setButtonBackground(this.deleteButton, R.drawable.emoji_key_action);
        setButtonIcon(this.deleteButton, R.attr.emojiIconDelete);
    }

    private void setButtonBackground(View view, int resId) {
        Drawable background = view.getContext().getResources().getDrawable(resId);
        ViewCompat.setBackground(view, background);
    }

    private void setButtonIcon(ImageButton view, int attrId) {
        Drawable icon = IMEApplication.from(this.context).getThemedDrawable(attrId);
        view.setImageDrawable(icon);
    }

    protected void sendPressHoldMessage(int delay, int actionKey) {
        Message msg = this.pressHoldHandler.obtainMessage(MSG_PRESS_HOLD);
        msg.arg1 = actionKey;
        this.pressHoldHandler.sendMessageDelayed(msg, delay);
    }

    protected void handleKey(int keycode) {
        IMEApplication.from(this.context).getIME().getCurrentInputView().handleKey(keycode, false, 0);
    }

    protected void handleLongPressOnKey(int actionKey) {
        handleKey(actionKey);
    }

    protected void playKeyFeedback(int keycode) {
        IME ime = IMEApplication.from(this.context).getIME();
        ime.vibrate();
        ime.playKeyClick(keycode);
    }

    public void setTextScale(float scale) {
        this.textScale = scale;
    }

    public void show() {
        UsageManager usageMgr = UsageManager.from(this.context);
        if (usageMgr != null) {
            usageMgr.getKeyboardUsageScribe().recordKeyboardLayerChange(IMEApplication.from(this.context).getIME().getCurrentInputView().getKeyboardLayer(), KeyboardEx.KeyboardLayerType.KEYBOARD_SYMBOLS);
        }
        log.d("show(): isActive: ", Boolean.valueOf(isActive()), "; text scale: ", Float.valueOf(this.textScale));
        showView(true, true);
    }

    public void hide() {
        log.d("hide(): isActive: ", Boolean.valueOf(isActive()));
        hideView(true);
    }

    private boolean showView(boolean enableAnim, boolean resetState) {
        if (this.symbolInputView != null) {
            if (this.symbolInputView.getWindowToken() != null) {
                return false;
            }
            log.d("showView(): current symbol input view is detached (re-creating)");
            this.symbolInputView = null;
        }
        log.d("showView(): showing ui...");
        createView();
        this.tabHost.setCurrentTabByTag(this.categoryList.getDefaultCategory().getName());
        IMEApplication.from(this.context).getIME().setCoverView(this.symbolInputView, enableAnim);
        onViewActiveStateChanged(true);
        return true;
    }

    private boolean hideView(boolean enableAnim) {
        if (this.symbolInputView == null) {
            return false;
        }
        if (this.categoryList != null && this.categoryList.getRecentCat() != null) {
            this.categoryList.getRecentCat().commit();
        }
        if (this.symbolInputView.getWindowToken() == null) {
            log.d("hideView(): can't hide detached view");
            this.symbolInputView = null;
            return false;
        }
        log.d("hideView(): hiding ui...");
        IMEApplication.from(this.context).getIME().setCoverView(null, enableAnim);
        onViewActiveStateChanged(false);
        this.symbolInputView = null;
        UsageManager usageMgr = UsageManager.from(this.context);
        if (usageMgr != null) {
            usageMgr.getKeyboardUsageScribe().recordKeyboardLayerChange(KeyboardEx.KeyboardLayerType.KEYBOARD_EMOJI, IMEApplication.from(this.context).getIME().getCurrentInputView().getKeyboardLayer());
        }
        return true;
    }

    public void onFinishInputView(boolean finishing) {
    }

    public void onConfigChanged(boolean isOrientationChange) {
        log.d("onConfigChanged(): is or: ", Boolean.valueOf(isOrientationChange), "; active: " + isActive());
        this.resetShowOnStartCallback.stop();
        this.showOnStart = isActive();
    }

    public void onStartInputView(boolean restarting) {
        log.d("onStartInputView(): show on start: ", Boolean.valueOf(this.showOnStart), "; restarting: ", Boolean.valueOf(restarting));
        if (this.showOnStart) {
            showView(false, false);
            this.resetShowOnStartCallback.start();
        } else if (!restarting) {
            hideView(false);
        }
    }

    @Override // android.widget.TabHost.OnTabChangeListener
    public void onTabChanged(String tabId) {
        log.d("onTabChanged(): tabId: ", tabId);
        if (this.categoryList != null) {
            int cur = 0;
            for (AbstractCategory cat : this.categoryList.getAllCategories()) {
                if (tabId.equalsIgnoreCase(cat.getName())) {
                    break;
                } else {
                    cur++;
                }
            }
            int old = this.pager.getCurrentItem();
            if (cur != old) {
                this.pager.setCurrentItem(cur, false);
            }
            if (this.categoryList.getRecentCat() != null) {
                this.categoryList.getRecentCat().commit();
            }
            log.d("onTabChanged(): old: ", Integer.valueOf(old), "; cur: ", Integer.valueOf(cur));
        }
    }

    public boolean isActive() {
        return this.symbolInputView != null;
    }

    private void onViewActiveStateChanged(boolean isShowing) {
        InputView inputView = IMEApplication.from(this.context).getIME().getCurrentInputView();
        if (inputView != null) {
            inputView.requestAutospaceOverrideTo(!isShowing);
            if (!isShowing) {
                if (inputView instanceof ChineseFSHandWritingInputView) {
                    inputView.setKeyboardLayer(KeyboardEx.KeyboardLayerType.KEYBOARD_TEXT);
                }
                if (inputView instanceof KoreanHandWritingInputView) {
                    inputView.setKeyboardLayer(KeyboardEx.KeyboardLayerType.KEYBOARD_TEXT);
                    return;
                }
                return;
            }
            if (inputView instanceof ChineseFSHandWritingInputView) {
                inputView.setKeyboardLayer(KeyboardEx.KeyboardLayerType.KEYBOARD_SYMBOLS);
            }
            if (inputView instanceof KoreanHandWritingInputView) {
                inputView.setKeyboardLayer(KeyboardEx.KeyboardLayerType.KEYBOARD_EDIT);
            }
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:48:0x0189, code lost:            r12 = r4.size() - 1;        r14 = r4.get(r12).intValue() + r17;        r4.set(r12, java.lang.Integer.valueOf(r14));        r5.get(r12).width = r14;     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void setGridCandidates(java.util.List<java.lang.CharSequence> r26, int r27) {
        /*
            Method dump skipped, instructions count: 633
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.nuance.swype.input.chinese.symbol.SymbolInputController.setGridCandidates(java.util.List, int):void");
    }

    @Override // android.support.v4.view.ViewPager.OnPageChangeListener
    public void onPageScrollStateChanged(int arg0) {
    }

    @Override // android.support.v4.view.ViewPager.OnPageChangeListener
    public void onPageScrolled(int arg0, float arg1, int arg2) {
        int pos = this.pager.getCurrentItem();
        int preTabPosition = this.tabHost.getCurrentTab();
        log.d("onPageScrolled...pos: ", Integer.valueOf(pos), "...preTabPosition: ", Integer.valueOf(preTabPosition));
        boolean pageChanged = pos != preTabPosition;
        int width = this.tabHost.getCurrentTabView().getWidth();
        if (width > 0) {
            int availableTabCount = IMEApplication.from(this.context).getDisplayWidth() / width;
            log.d("onPageScrolled...tab width:  ", Integer.valueOf(width), "...availableTabCount: ", Integer.valueOf(availableTabCount), "...mTabHost.getTabWidget().getChildCount(): ", Integer.valueOf(this.tabHost.getTabWidget().getChildCount()));
            if (preTabPosition < pos && preTabPosition >= availableTabCount - 1) {
                if (this.hScrollView != null) {
                    this.hScrollView.scrollBy(width, 0);
                }
            } else if (preTabPosition > pos && preTabPosition <= this.tabHost.getTabWidget().getChildCount() - availableTabCount && this.hScrollView != null) {
                this.hScrollView.scrollBy(-width, 0);
            }
            if (pageChanged) {
                this.tabHost.setCurrentTab(pos);
            }
        }
    }

    @Override // android.support.v4.view.ViewPager.OnPageChangeListener
    public void onPageSelected(int arg0) {
    }
}
