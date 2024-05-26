package com.nuance.swype.input.emoji;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabWidget;
import com.nuance.android.compat.ViewCompat;
import com.nuance.android.util.WeakReferenceHandler;
import com.nuance.swype.input.AppPreferences;
import com.nuance.swype.input.IME;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.input.InputView;
import com.nuance.swype.input.KeyboardEx;
import com.nuance.swype.input.R;
import com.nuance.swype.input.chinese.ChineseFSHandWritingInputView;
import com.nuance.swype.input.emoji.EmojiCategoryGroup;
import com.nuance.swype.input.emoji.EmojiPageView;
import com.nuance.swype.input.emoji.EmojiViewPager;
import com.nuance.swype.input.emoji.finger.FingerInfo;
import com.nuance.swype.input.emoji.finger.FingerStateListener;
import com.nuance.swype.input.emoji.finger.Fingerable;
import com.nuance.swype.input.korean.KoreanHandWritingInputView;
import com.nuance.swype.input.view.InputContainerView;
import com.nuance.swype.stats.UsageManager;
import com.nuance.swype.usagedata.UsageData;
import com.nuance.swype.util.Callback;
import com.nuance.swype.util.LogManager;
import com.nuance.swype.util.ViewUtil;
import com.nuance.swype.view.OverlayView;
import com.nuance.swype.view.TintDrawable;
import com.nuance.swype.view.TintDrawableCompat;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/* loaded from: classes.dex */
public class EmojiInputController implements TabHost.OnTabChangeListener, EmojiViewPager.PagerSizeChangeListener {
    private static final boolean ENABLE_ANIM_DEFAULT = true;
    private static final int MSG_PRESS_HOLD = 1001;
    private static final LogManager.Log log = LogManager.getLog("EmojiInputController");
    private static final LogManager.Trace trace = LogManager.getTrace();
    private EmojiPagerAdapter adapter;
    private EmojiCategoryGroup.Iterator catIter;
    private EmojiCategoryList categoryList;
    private Context context;
    private ImageButton deleteButton;
    private EmojiInputView emojiInputView;
    private LayoutInflater inflater;
    private ImageButton keyboardButton;
    private EmojiLayerState layerState;
    private int longPressHoldTimeout;
    private EmojiPageIndicatorView pageIndicatorView;
    private EmojiViewPager pager;
    private ImageButton spaceButton;
    private TabHost tabHost;
    private float textScale = 1.0f;
    private final View.OnTouchListener actionKeyTouchHandler = new View.OnTouchListener() { // from class: com.nuance.swype.input.emoji.EmojiInputController.5
        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        /* JADX WARN: Code restructure failed: missing block: B:6:0x0016, code lost:            return true;     */
        @Override // android.view.View.OnTouchListener
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
                com.nuance.swype.input.emoji.EmojiInputController r2 = com.nuance.swype.input.emoji.EmojiInputController.this
                r2.playKeyFeedback(r0)
                com.nuance.swype.input.emoji.EmojiInputController r2 = com.nuance.swype.input.emoji.EmojiInputController.this
                com.nuance.swype.input.emoji.EmojiInputController r3 = com.nuance.swype.input.emoji.EmojiInputController.this
                int r3 = com.nuance.swype.input.emoji.EmojiInputController.access$700(r3)
                r2.sendPressHoldMessage(r3, r0)
                goto L16
            L32:
                r7.setPressed(r3)
                com.nuance.swype.input.emoji.EmojiInputController r2 = com.nuance.swype.input.emoji.EmojiInputController.this
                android.os.Handler r2 = com.nuance.swype.input.emoji.EmojiInputController.access$800(r2)
                r2.removeMessages(r5)
                com.nuance.swype.input.emoji.EmojiInputController r2 = com.nuance.swype.input.emoji.EmojiInputController.this
                r2.handleKey(r0)
                goto L16
            L44:
                r7.setPressed(r3)
                com.nuance.swype.input.emoji.EmojiInputController r2 = com.nuance.swype.input.emoji.EmojiInputController.this
                android.os.Handler r2 = com.nuance.swype.input.emoji.EmojiInputController.access$800(r2)
                r2.removeMessages(r5)
                goto L16
            */
            throw new UnsupportedOperationException("Method not decompiled: com.nuance.swype.input.emoji.EmojiInputController.AnonymousClass5.onTouch(android.view.View, android.view.MotionEvent):boolean");
        }
    };
    private Handler.Callback handlerCallback = new Handler.Callback() { // from class: com.nuance.swype.input.emoji.EmojiInputController.6
        @Override // android.os.Handler.Callback
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case EmojiInputController.MSG_PRESS_HOLD /* 1001 */:
                    int actionKey = msg.arg1;
                    EmojiInputController.this.handleLongPressOnKey(actionKey);
                    IME ime = IMEApplication.from(EmojiInputController.this.context).getIME();
                    if (ime != null && ime.getCurrentInputView() != null) {
                        int delay = ime.getCurrentInputView().computeLongPressableTimeout();
                        EmojiInputController.this.sendPressHoldMessage(delay, actionKey);
                    }
                    return true;
                default:
                    return false;
            }
        }
    };
    private boolean showOnStart = false;
    private Callback<? extends Runnable> resetShowOnStartCallback = Callback.create(new Runnable() { // from class: com.nuance.swype.input.emoji.EmojiInputController.7
        @Override // java.lang.Runnable
        public void run() {
            EmojiInputController.log.d("run(): resetting show on start flag");
            EmojiInputController.this.showOnStart = false;
        }
    });
    private final Handler pressHoldHandler = WeakReferenceHandler.create(this.handlerCallback);

    public void setContext(Context context) {
        this.context = context;
        this.layerState = new EmojiLayerState(this.context);
        this.longPressHoldTimeout = this.context.getResources().getInteger(R.integer.short_press_timeout_ms);
        int maxSize = context.getResources().getInteger(R.integer.emoji_recent_list_max);
        MoveToFirstStrategyEmoji strategy = new MoveToFirstStrategyEmoji(context, maxSize);
        MoveToFirstStrategyEmojiDecorator decorator = new MoveToFirstStrategyEmojiDecorator(strategy);
        RecentListManager recentListManager = new RecentListManager(decorator);
        this.categoryList = new EmojiCategoryList(context, recentListManager);
    }

    @SuppressLint({"InflateParams"})
    private void createView() {
        if (this.emojiInputView == null) {
            log.d("createView(): inflating emoji input view...");
            IMEApplication app = IMEApplication.from(this.context);
            this.inflater = app.getThemedLayoutInflater(LayoutInflater.from(this.context));
            app.getThemeLoader().setLayoutInflaterFactory(this.inflater);
            this.emojiInputView = (EmojiInputView) this.inflater.inflate(R.layout.emoji_input_view, (ViewGroup) null);
            Drawable background = app.getThemedDrawable(R.attr.emojiBackground);
            ViewCompat.setBackground(this.emojiInputView, background);
            app.getThemeLoader().applyTheme(this.emojiInputView);
            this.pager = (EmojiViewPager) this.emojiInputView.findViewById(R.id.emoji_input_view_pager);
            app.getThemeLoader().applyTheme(this.pager);
            this.pageIndicatorView = (EmojiPageIndicatorView) this.emojiInputView.findViewById(R.id.emoji_page_indicator_view);
            this.pager.setPagerSizeChangeListener(this);
            initActionKeys();
            initCategoryTabs();
        }
    }

    public void updatePageIndicator(int pageNumber, int pageCount) {
        log.d("updatePageIndicator(): ", Integer.valueOf(pageNumber), "/", Integer.valueOf(pageCount));
        this.pageIndicatorView.setNumberOfPages(pageCount);
        this.pageIndicatorView.setActivePage(pageNumber);
        this.pageIndicatorView.setVisibility(pageCount <= 1 ? 4 : 0);
    }

    @Override // com.nuance.swype.input.emoji.EmojiViewPager.PagerSizeChangeListener
    public void onPagerSizeChanged() {
        log.d("onPagerSizeChanged()");
        EmojiGridParams grid = createGrid();
        if (grid != null) {
            createAdapter(grid);
            this.layerState.loadState();
            String catName = this.layerState.getCategoryName();
            int item = this.layerState.getCategoryItem();
            EmojiCategory cat = this.categoryList.getCategoryFromName(catName);
            if (cat == null || !cat.hasItems()) {
                cat = this.categoryList.getDefaultCategory();
                item = 0;
            }
            int item2 = Math.min(item, cat.getEmojiList().size() - 1);
            EmojiCategoryGroup catGroup = this.adapter.getGroup();
            int localPage = catGroup.getGrid().getPageForItem(item2);
            int globalPage = catGroup.getGlobalPage(cat) + localPage;
            this.catIter.moveToGlobalPage(globalPage);
            this.pager.setCurrentItem(globalPage, false);
            onGlobalPageChanged();
        }
    }

    private boolean setTab(EmojiCategory category) {
        String tabId = category.getName();
        if (this.tabHost.getCurrentTabTag().equals(tabId)) {
            return false;
        }
        this.tabHost.setCurrentTabByTag(tabId);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class ActiveFingerTracker implements FingerStateListener {
        private Set<Integer> active;

        private ActiveFingerTracker() {
            this.active = new HashSet();
        }

        @Override // com.nuance.swype.input.emoji.finger.FingerStateListener
        public void onFingerPress(Fingerable object, FingerInfo info) {
            if (info.getPressState() == FingerInfo.PressState.SHORT) {
                addActive(info.getPointerId());
            }
        }

        private void addActive(int pointerId) {
            if (this.active.add(Integer.valueOf(pointerId)) && this.active.size() == 1) {
                EmojiInputController.this.pager.setAllowScroll(false);
            }
        }

        private void removeActive(int pointerId) {
            if (this.active.remove(Integer.valueOf(pointerId)) && this.active.size() == 0) {
                EmojiInputController.this.pager.setAllowScroll(true);
            }
        }

        @Override // com.nuance.swype.input.emoji.finger.FingerStateListener
        public void onFingerMove(Fingerable object, FingerInfo info) {
        }

        @Override // com.nuance.swype.input.emoji.finger.FingerStateListener
        public void onFingerRelease(Fingerable object, FingerInfo info, boolean isEscape) {
            if (!isEscape) {
                removeActive(info.getPointerId());
            }
        }

        @Override // com.nuance.swype.input.emoji.finger.FingerStateListener
        public void onFingerCancel(Fingerable object, FingerInfo info) {
            removeActive(info.getPointerId());
        }
    }

    private void createAdapter(EmojiGridParams grid) {
        EmojiPageView.Listener pageViewListener = new EmojiPageView.Listener() { // from class: com.nuance.swype.input.emoji.EmojiInputController.1
            @Override // com.nuance.swype.input.emoji.EmojiPageView.Listener
            public void playFeedback(String item, EmojiPageView.EmojiFeedback feedback) {
                switch (AnonymousClass8.$SwitchMap$com$nuance$swype$input$emoji$EmojiPageView$EmojiFeedback[feedback.ordinal()]) {
                    case 1:
                    case 2:
                        EmojiInputController.this.playKeyFeedback(0);
                        return;
                    default:
                        return;
                }
            }

            @Override // com.nuance.swype.input.emoji.EmojiPageView.Listener
            public void onSelect(Emoji item) {
                EmojiInputController.log.d("onSelect()");
                IME ime = IMEApplication.from(EmojiInputController.this.context).getIME();
                EmojiCategory cat = EmojiInputController.this.catIter.getCategory();
                if (cat instanceof EmojiCategoryRecents) {
                    EmojiInputController.log.d("onSelect(): ", " called : if >>> ");
                    ((EmojiCategoryRecents) cat).add(item, false);
                    EmojiInputController.this.adapter.refreshDynamicPages();
                    UsageData.recordEmojiSelected(UsageData.EmojiSelectedSource.RECENT);
                } else {
                    EmojiInputController.log.d("onSelect(): ", " called : else >>>>>>");
                    EmojiCategoryRecents recents = EmojiInputController.this.categoryList.getRecentCat();
                    EmojiCategoryGroup group = EmojiInputController.this.adapter.getGroup();
                    EmojiCategoryRecents recentCat = EmojiInputController.this.categoryList.getRecentCat();
                    int oldCount = group.getPageCount(recents);
                    recentCat.add(item, false);
                    int newCount = group.getPageCount(recents);
                    EmojiInputController.this.adapter.onCategoryChanged(recentCat, oldCount, newCount);
                    UsageData.recordEmojiSelected(UsageData.EmojiSelectedSource.PICKER);
                }
                ime.onText(item.getEmojiDisplayCode(), 0L);
            }
        };
        InputContainerView containerView = IMEApplication.from(this.context).getIME().getInputContainerView();
        OverlayView overlayView = containerView != null ? containerView.getOverlayViewCreate() : null;
        EmojiCategoryGroup catGroup = new EmojiCategoryGroup(this.context, grid, this.categoryList);
        this.catIter = catGroup.iterator();
        this.adapter = new EmojiPagerAdapter(this.context, overlayView, this.inflater, catGroup, pageViewListener, new ActiveFingerTracker(), this.pager);
        this.pager.setAdapter(this.adapter);
        this.pager.setOffscreenPageLimit(0);
        this.pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() { // from class: com.nuance.swype.input.emoji.EmojiInputController.2
            @Override // android.support.v4.view.ViewPager.OnPageChangeListener
            public void onPageSelected(int pageNumber) {
                EmojiInputController.log.d("onPageSelected(): ", Integer.valueOf(pageNumber));
                EmojiInputController.this.onGlobalPageChanged();
            }

            @Override // android.support.v4.view.ViewPager.OnPageChangeListener
            public void onPageScrolled(int pos, float positionOffset, int positionOffsetPixels) {
                EmojiPageView pageView = EmojiInputController.this.adapter.getActivePageView();
                if (pageView != null) {
                    pageView.onPageScrolled(pos, positionOffset, positionOffsetPixels);
                }
            }

            @Override // android.support.v4.view.ViewPager.OnPageChangeListener
            public void onPageScrollStateChanged(int state) {
                EmojiPageView pageView = EmojiInputController.this.adapter.getActivePageView();
                if (pageView != null) {
                    pageView.onPageScrollStateChanged(state == 1);
                }
            }
        });
    }

    /* renamed from: com.nuance.swype.input.emoji.EmojiInputController$8, reason: invalid class name */
    /* loaded from: classes.dex */
    static /* synthetic */ class AnonymousClass8 {
        static final /* synthetic */ int[] $SwitchMap$com$nuance$swype$input$emoji$EmojiPageView$EmojiFeedback = new int[EmojiPageView.EmojiFeedback.values().length];

        static {
            try {
                $SwitchMap$com$nuance$swype$input$emoji$EmojiPageView$EmojiFeedback[EmojiPageView.EmojiFeedback.FEEDBACK_PRESS.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$nuance$swype$input$emoji$EmojiPageView$EmojiFeedback[EmojiPageView.EmojiFeedback.FEEDBACK_POPUP.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
        }
    }

    @SuppressLint({"InflateParams"})
    private EmojiGridParams createGrid() {
        EmojiPageView protoPage = (EmojiPageView) this.inflater.inflate(R.layout.emoji_page, (ViewGroup) null);
        int horPadding = ViewUtil.getHorPadding(protoPage);
        int verPadding = ViewUtil.getVerPadding(protoPage);
        int pagerHorPadding = ViewUtil.getHorPadding(this.pager);
        int pagerVerPadding = ViewUtil.getVerPadding(this.pager);
        int contentWidth = (this.pager.getMeasuredWidth() - pagerHorPadding) - horPadding;
        int contentHeight = (this.pager.getMeasuredHeight() - pagerVerPadding) - verPadding;
        if (contentHeight > 0 && contentWidth > 0) {
            return new EmojiGridParams(this.context, contentWidth, contentHeight, this.textScale);
        }
        log.d("createGrid(): measured content area is empty");
        return null;
    }

    protected void onGlobalPageChanged() {
        log.d("onGlobalPageChanged(): called >>>>>>>>>>>: ");
        EmojiCategory oldCat = this.catIter.getCategory();
        int globalPage = this.pager.getCurrentItem();
        this.catIter.moveToGlobalPage(globalPage);
        EmojiCategory cat = this.catIter.getCategory();
        int localPage = this.catIter.getLocalPage();
        setTab(cat);
        log.d("onGlobalPageChanged(): cur: ", cat, "; old: ", oldCat, "; page: ", Integer.valueOf(globalPage));
        EmojiCategoryGroup catGroup = this.adapter.getGroup();
        if (!cat.equals(oldCat)) {
            EmojiCategoryRecents recentCat = this.categoryList.getRecentCat();
            if (recentCat.commit()) {
                this.adapter.onCategoryChanged(recentCat, 0, 0);
            }
            this.layerState.saveState(catGroup, this.catIter);
        } else if (catGroup.getGrid().getPageForItem(this.layerState.getCategoryItem()) != localPage) {
            this.layerState.saveState(catGroup, this.catIter);
        }
        updatePageIndicator(localPage, this.catIter.getLocalPageCount());
    }

    @SuppressLint({"InflateParams"})
    private void initCategoryTabs() {
        this.tabHost = (TabHost) this.emojiInputView.findViewById(R.id.emoji_input_view_categories);
        IMEApplication.from(this.context).getThemeLoader().applyTheme(this.tabHost);
        this.tabHost.setup();
        for (EmojiCategory category : this.categoryList.getAllCategories()) {
            TabHost.TabSpec newTabSpec = this.tabHost.newTabSpec(category.getName());
            newTabSpec.setContent(R.id.emoji_input_view_empty_view);
            ImageView view = (ImageView) this.inflater.inflate(R.layout.emoji_category_item, (ViewGroup) null);
            TintDrawable drawable = TintDrawableCompat.createTintDrawable(this.context, category.getIconRes());
            ColorStateList color = view.getContext().getResources().getColorStateList(R.color.color_emoji_category_icon);
            drawable.setTintParams(color, PorterDuff.Mode.SRC_ATOP);
            view.setImageDrawable(drawable);
            newTabSpec.setIndicator(view);
            this.tabHost.addTab(newTabSpec);
        }
        this.tabHost.setOnTabChangedListener(this);
        TabWidget tabWidget = (TabWidget) this.emojiInputView.findViewById(android.R.id.tabs);
        int count = tabWidget.getChildCount();
        View.OnTouchListener listener = new View.OnTouchListener() { // from class: com.nuance.swype.input.emoji.EmojiInputController.3
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == 1) {
                    EmojiInputController.this.playKeyFeedback(0);
                }
                return false;
            }
        };
        for (int i = 0; i < count; i++) {
            tabWidget.getChildAt(i).setSoundEffectsEnabled(false);
            tabWidget.getChildAt(i).setOnTouchListener(listener);
        }
    }

    private void initActionKeys() {
        this.keyboardButton = (ImageButton) this.emojiInputView.findViewById(R.id.emoji_input_view_keyboard_key);
        this.keyboardButton.setOnClickListener(new View.OnClickListener() { // from class: com.nuance.swype.input.emoji.EmojiInputController.4
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                EmojiInputController.this.playKeyFeedback(0);
                EmojiInputController.this.hide();
            }
        });
        setButtonBackground(this.keyboardButton, R.drawable.emoji_key_action);
        setButtonIcon(this.keyboardButton, R.attr.emojiIconKeyboard);
        this.spaceButton = (ImageButton) this.emojiInputView.findViewById(R.id.emoji_input_view_space_key);
        this.spaceButton.setOnTouchListener(this.actionKeyTouchHandler);
        setButtonBackground(this.spaceButton, R.drawable.emoji_key_space);
        this.deleteButton = (ImageButton) this.emojiInputView.findViewById(R.id.emoji_input_view_delete_key);
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

    public void show() {
        UsageManager usageMgr = UsageManager.from(this.context);
        if (usageMgr != null) {
            usageMgr.getKeyboardUsageScribe().recordKeyboardLayerChange(IMEApplication.from(this.context).getIME().getCurrentInputView().getKeyboardLayer(), KeyboardEx.KeyboardLayerType.KEYBOARD_EMOJI);
        }
        log.d("show(): isActive: ", Boolean.valueOf(isActive()), "; text scale: ", Float.valueOf(this.textScale));
        showView(true, true);
    }

    public void hide() {
        log.d("hide(): isActive: ", Boolean.valueOf(isActive()));
        hideView(true);
    }

    private boolean showView(boolean enableAnim, boolean resetState) {
        if (this.emojiInputView != null) {
            if (this.emojiInputView.getWindowToken() != null) {
                return false;
            }
            log.d("showView(): current emoji input view is detached (re-creating)");
            this.emojiInputView = null;
        }
        log.d("showView(): showing ui...");
        if (resetState) {
            this.layerState.resetState(this.categoryList.getDefaultCategory().getName());
        }
        createView();
        IMEApplication.from(this.context).getIME().setCoverView(this.emojiInputView, enableAnim);
        onViewActiveStateChanged(true);
        return true;
    }

    private boolean hideView(boolean enableAnim) {
        if (this.categoryList != null) {
            this.categoryList.getRecentCat().commit();
        }
        if (this.emojiInputView == null) {
            return false;
        }
        if (this.emojiInputView.getWindowToken() == null) {
            log.d("hideView(): can't hide detached view");
            this.emojiInputView = null;
            return false;
        }
        log.d("hideView(): hiding ui...");
        IMEApplication.from(this.context).getIME().setCoverView(null, enableAnim);
        onViewActiveStateChanged(false);
        this.emojiInputView = null;
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
        if (this.catIter != null) {
            EmojiCategory cur = this.categoryList.getCategoryFromName(tabId);
            EmojiCategory old = this.catIter.getCategory();
            log.d("onTabChanged():  called  :: old: ", old, "; cur: ", cur);
            if (!cur.equals(old)) {
                int globalPage = this.adapter.getGroup().getGlobalPage(cur);
                log.d("onTabChanged(): called  :: setting global page: ", Integer.valueOf(globalPage));
                this.pager.setCurrentItem(globalPage, false);
                onGlobalPageChanged();
            }
        }
    }

    public boolean isActive() {
        return this.emojiInputView != null;
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
                    inputView.flushCurrentActiveWord();
                    inputView.setKeyboardLayer(KeyboardEx.KeyboardLayerType.KEYBOARD_TEXT);
                    return;
                }
                return;
            }
            if (inputView instanceof ChineseFSHandWritingInputView) {
                inputView.setKeyboardLayer(KeyboardEx.KeyboardLayerType.KEYBOARD_EMOJI);
            }
            if (inputView instanceof KoreanHandWritingInputView) {
                inputView.setKeyboardLayer(KeyboardEx.KeyboardLayerType.KEYBOARD_EDIT);
            }
        }
    }

    /* loaded from: classes.dex */
    public static class EmojiLayerState {
        private AppPreferences appPrefs;
        private int categoryItem;
        private String categoryName;

        public EmojiLayerState(Context context) {
            this.appPrefs = AppPreferences.from(context);
            loadState();
        }

        public void resetState(String categoryName) {
            this.categoryName = categoryName;
            this.categoryItem = 0;
            saveState();
        }

        public void saveState(EmojiCategoryGroup catGroup, EmojiCategoryGroup.Iterator iter) {
            this.categoryName = iter.getCategory().getName();
            this.categoryItem = catGroup.getGrid().getFirstItemOnPage(iter.getLocalPage());
            saveState();
        }

        public void loadState() {
            this.categoryName = this.appPrefs.getLastUsedEmojiCategoryName();
            this.categoryItem = this.appPrefs.getLastUsedEmojiCategoryItem();
            EmojiInputController.log.d("loadState(): cat: ", this.categoryName, "; first item: ", Integer.valueOf(this.categoryItem));
        }

        private void saveState() {
            EmojiInputController.log.d("saveState(): cat: ", this.categoryName, "; first item: ", Integer.valueOf(this.categoryItem));
            this.appPrefs.setLastUsedEmojiCategoryName(this.categoryName);
            this.appPrefs.setLastUsedEmojiCategoryItem(this.categoryItem);
        }

        public String getCategoryName() {
            return this.categoryName;
        }

        public int getCategoryItem() {
            return this.categoryItem;
        }
    }

    public boolean isEmoji(CharSequence text) {
        EmojiCategoryGroup group;
        List<EmojiCategory> emojiList;
        if (this.adapter != null && (group = this.adapter.getGroup()) != null && (emojiList = group.getAllCategories()) != null) {
            Iterator<EmojiCategory> it = emojiList.iterator();
            while (it.hasNext()) {
                List<Emoji> emojis = it.next().getEmojiList();
                if (emojis != null && emojis.size() > 0) {
                    Iterator<Emoji> it2 = emojis.iterator();
                    while (it2.hasNext()) {
                        if (it2.next().getEmojiDisplayCode().equals(text.toString())) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }
        return false;
    }

    public boolean addPopupSkinToneInRecentCat(Emoji emoji) {
        boolean isAdded = addEmojiInRecentCat(emoji);
        IMEApplication.from(this.context).getIME().onText(emoji.getEmojiDisplayCode(), 0L);
        return isAdded;
    }

    public boolean addEmojiInRecentCat(Emoji emoji) {
        EmojiCategoryRecents recentCat;
        if (this.categoryList == null || emoji == null || (recentCat = this.categoryList.getRecentCat()) == null) {
            return false;
        }
        recentCat.add(emoji, false);
        if (this.adapter != null) {
            this.adapter.refreshDynamicPages();
        }
        return true;
    }

    public void dismissSkinTonePopup() {
        EmojiPageView emojiPageView;
        if (this.adapter != null && (emojiPageView = this.adapter.getActivePageView()) != null) {
            emojiPageView.dismissEmojiPopup();
        }
    }
}
