package android.support.v7.widget;

import android.app.PendingIntent;
import android.app.SearchableInfo;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.ResultReceiver;
import android.support.v4.content.res.ConfigurationHelper;
import android.support.v4.os.ParcelableCompat;
import android.support.v4.os.ParcelableCompatCreatorCallbacks;
import android.support.v4.view.AbsSavedState;
import android.support.v4.view.KeyEventCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.appcompat.R;
import android.support.v7.view.CollapsibleActionView;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import com.nuance.connect.common.Integers;
import java.lang.reflect.Method;
import java.util.WeakHashMap;

/* loaded from: classes.dex */
public class SearchView extends LinearLayoutCompat implements CollapsibleActionView {
    static final AutoCompleteTextViewReflector HIDDEN_METHOD_INVOKER;
    private static final boolean IS_AT_LEAST_FROYO;
    private Bundle mAppSearchData;
    private boolean mClearingFocus;
    private final ImageView mCloseButton;
    private final ImageView mCollapsedIcon;
    private int mCollapsedImeOptions;
    private final CharSequence mDefaultQueryHint;
    private final AppCompatDrawableManager mDrawableManager;
    private final View mDropDownAnchor;
    private boolean mExpandedInActionView;
    private final ImageView mGoButton;
    private boolean mIconified;
    private boolean mIconifiedByDefault;
    private int mMaxWidth;
    private CharSequence mOldQueryText;
    private final View.OnClickListener mOnClickListener;
    private OnCloseListener mOnCloseListener;
    private final TextView.OnEditorActionListener mOnEditorActionListener;
    private final AdapterView.OnItemClickListener mOnItemClickListener;
    private final AdapterView.OnItemSelectedListener mOnItemSelectedListener;
    private OnQueryTextListener mOnQueryChangeListener;
    private View.OnFocusChangeListener mOnQueryTextFocusChangeListener;
    private View.OnClickListener mOnSearchClickListener;
    private OnSuggestionListener mOnSuggestionListener;
    private final WeakHashMap<String, Drawable.ConstantState> mOutsideDrawablesCache;
    private CharSequence mQueryHint;
    private boolean mQueryRefinement;
    private Runnable mReleaseCursorRunnable;
    private final ImageView mSearchButton;
    private final View mSearchEditFrame;
    private final Drawable mSearchHintIcon;
    private final View mSearchPlate;
    private final SearchAutoComplete mSearchSrcTextView;
    private Rect mSearchSrcTextViewBounds;
    private Rect mSearchSrtTextViewBoundsExpanded;
    private SearchableInfo mSearchable;
    private Runnable mShowImeRunnable;
    private final View mSubmitArea;
    private boolean mSubmitButtonEnabled;
    private final int mSuggestionCommitIconResId;
    private final int mSuggestionRowLayout;
    private CursorAdapter mSuggestionsAdapter;
    private int[] mTemp;
    private int[] mTemp2;
    View.OnKeyListener mTextKeyListener;
    private TextWatcher mTextWatcher;
    private UpdatableTouchDelegate mTouchDelegate;
    private final Runnable mUpdateDrawableStateRunnable;
    private CharSequence mUserQuery;
    private final Intent mVoiceAppSearchIntent;
    private final ImageView mVoiceButton;
    private boolean mVoiceButtonEnabled;
    private final Intent mVoiceWebSearchIntent;

    /* loaded from: classes.dex */
    public interface OnCloseListener {
        boolean onClose();
    }

    /* loaded from: classes.dex */
    public interface OnQueryTextListener {
        boolean onQueryTextSubmit$552c4dfd();
    }

    /* loaded from: classes.dex */
    public interface OnSuggestionListener {
        boolean onSuggestionClick$134632();

        boolean onSuggestionSelect$134632();
    }

    static {
        IS_AT_LEAST_FROYO = Build.VERSION.SDK_INT >= 8;
        HIDDEN_METHOD_INVOKER = new AutoCompleteTextViewReflector();
    }

    public SearchView(Context context) {
        this(context, null);
    }

    public SearchView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.searchViewStyle);
    }

    public SearchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mSearchSrcTextViewBounds = new Rect();
        this.mSearchSrtTextViewBoundsExpanded = new Rect();
        this.mTemp = new int[2];
        this.mTemp2 = new int[2];
        this.mShowImeRunnable = new Runnable() { // from class: android.support.v7.widget.SearchView.1
            @Override // java.lang.Runnable
            public final void run() {
                InputMethodManager imm = (InputMethodManager) SearchView.this.getContext().getSystemService("input_method");
                if (imm != null) {
                    AutoCompleteTextViewReflector autoCompleteTextViewReflector = SearchView.HIDDEN_METHOD_INVOKER;
                    SearchView searchView = SearchView.this;
                    if (autoCompleteTextViewReflector.showSoftInputUnchecked != null) {
                        try {
                            autoCompleteTextViewReflector.showSoftInputUnchecked.invoke(imm, 0, null);
                            return;
                        } catch (Exception e) {
                        }
                    }
                    imm.showSoftInput(searchView, 0);
                }
            }
        };
        this.mUpdateDrawableStateRunnable = new Runnable() { // from class: android.support.v7.widget.SearchView.2
            @Override // java.lang.Runnable
            public final void run() {
                SearchView.access$000(SearchView.this);
            }
        };
        this.mReleaseCursorRunnable = new Runnable() { // from class: android.support.v7.widget.SearchView.3
            @Override // java.lang.Runnable
            public final void run() {
                if (SearchView.this.mSuggestionsAdapter != null && (SearchView.this.mSuggestionsAdapter instanceof SuggestionsAdapter)) {
                    SearchView.this.mSuggestionsAdapter.changeCursor(null);
                }
            }
        };
        this.mOutsideDrawablesCache = new WeakHashMap<>();
        this.mOnClickListener = new View.OnClickListener() { // from class: android.support.v7.widget.SearchView.7
            @Override // android.view.View.OnClickListener
            public final void onClick(View v) {
                if (v == SearchView.this.mSearchButton) {
                    SearchView.this.onSearchClicked();
                    return;
                }
                if (v == SearchView.this.mCloseButton) {
                    SearchView.this.onCloseClicked();
                    return;
                }
                if (v == SearchView.this.mGoButton) {
                    SearchView.this.onSubmitQuery();
                } else {
                    if (v != SearchView.this.mVoiceButton) {
                        if (v == SearchView.this.mSearchSrcTextView) {
                            SearchView.this.forceSuggestionQuery();
                            return;
                        }
                        return;
                    }
                    SearchView.access$1100(SearchView.this);
                }
            }
        };
        this.mTextKeyListener = new View.OnKeyListener() { // from class: android.support.v7.widget.SearchView.8
            @Override // android.view.View.OnKeyListener
            public final boolean onKey(View v, int keyCode, KeyEvent event) {
                if (SearchView.this.mSearchable == null) {
                    return false;
                }
                if (!SearchView.this.mSearchSrcTextView.isPopupShowing() || SearchView.this.mSearchSrcTextView.getListSelection() == -1) {
                    if (SearchAutoComplete.access$1600(SearchView.this.mSearchSrcTextView) || !KeyEventCompat.hasNoModifiers(event) || event.getAction() != 1 || keyCode != 66) {
                        return false;
                    }
                    v.cancelLongPress();
                    SearchView.this.launchQuerySearch$6ef37c42(SearchView.this.mSearchSrcTextView.getText().toString());
                    return true;
                }
                return SearchView.this.onSuggestionsKey$33ade166(keyCode, event);
            }
        };
        this.mOnEditorActionListener = new TextView.OnEditorActionListener() { // from class: android.support.v7.widget.SearchView.9
            @Override // android.widget.TextView.OnEditorActionListener
            public final boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                SearchView.this.onSubmitQuery();
                return true;
            }
        };
        this.mOnItemClickListener = new AdapterView.OnItemClickListener() { // from class: android.support.v7.widget.SearchView.10
            @Override // android.widget.AdapterView.OnItemClickListener
            public final void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SearchView.this.onItemClicked$3e6d8123(position);
            }
        };
        this.mOnItemSelectedListener = new AdapterView.OnItemSelectedListener() { // from class: android.support.v7.widget.SearchView.11
            @Override // android.widget.AdapterView.OnItemSelectedListener
            public final void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SearchView.access$1900(SearchView.this, position);
            }

            @Override // android.widget.AdapterView.OnItemSelectedListener
            public final void onNothingSelected(AdapterView<?> parent) {
            }
        };
        this.mTextWatcher = new TextWatcher() { // from class: android.support.v7.widget.SearchView.12
            @Override // android.text.TextWatcher
            public final void beforeTextChanged(CharSequence s, int start, int before, int after) {
            }

            @Override // android.text.TextWatcher
            public final void onTextChanged(CharSequence s, int start, int before, int after) {
                SearchView.access$2000(SearchView.this, s);
            }

            @Override // android.text.TextWatcher
            public final void afterTextChanged(Editable s) {
            }
        };
        this.mDrawableManager = AppCompatDrawableManager.get();
        TintTypedArray a = TintTypedArray.obtainStyledAttributes(context, attrs, R.styleable.SearchView, defStyleAttr, 0);
        LayoutInflater inflater = LayoutInflater.from(context);
        int layoutResId = a.getResourceId(R.styleable.SearchView_layout, R.layout.abc_search_view);
        inflater.inflate(layoutResId, (ViewGroup) this, true);
        this.mSearchSrcTextView = (SearchAutoComplete) findViewById(R.id.search_src_text);
        this.mSearchSrcTextView.setSearchView(this);
        this.mSearchEditFrame = findViewById(R.id.search_edit_frame);
        this.mSearchPlate = findViewById(R.id.search_plate);
        this.mSubmitArea = findViewById(R.id.submit_area);
        this.mSearchButton = (ImageView) findViewById(R.id.search_button);
        this.mGoButton = (ImageView) findViewById(R.id.search_go_btn);
        this.mCloseButton = (ImageView) findViewById(R.id.search_close_btn);
        this.mVoiceButton = (ImageView) findViewById(R.id.search_voice_btn);
        this.mCollapsedIcon = (ImageView) findViewById(R.id.search_mag_icon);
        this.mSearchPlate.setBackgroundDrawable(a.getDrawable(R.styleable.SearchView_queryBackground));
        this.mSubmitArea.setBackgroundDrawable(a.getDrawable(R.styleable.SearchView_submitBackground));
        this.mSearchButton.setImageDrawable(a.getDrawable(R.styleable.SearchView_searchIcon));
        this.mGoButton.setImageDrawable(a.getDrawable(R.styleable.SearchView_goIcon));
        this.mCloseButton.setImageDrawable(a.getDrawable(R.styleable.SearchView_closeIcon));
        this.mVoiceButton.setImageDrawable(a.getDrawable(R.styleable.SearchView_voiceIcon));
        this.mCollapsedIcon.setImageDrawable(a.getDrawable(R.styleable.SearchView_searchIcon));
        this.mSearchHintIcon = a.getDrawable(R.styleable.SearchView_searchHintIcon);
        this.mSuggestionRowLayout = a.getResourceId(R.styleable.SearchView_suggestionRowLayout, R.layout.abc_search_dropdown_item_icons_2line);
        this.mSuggestionCommitIconResId = a.getResourceId(R.styleable.SearchView_commitIcon, 0);
        this.mSearchButton.setOnClickListener(this.mOnClickListener);
        this.mCloseButton.setOnClickListener(this.mOnClickListener);
        this.mGoButton.setOnClickListener(this.mOnClickListener);
        this.mVoiceButton.setOnClickListener(this.mOnClickListener);
        this.mSearchSrcTextView.setOnClickListener(this.mOnClickListener);
        this.mSearchSrcTextView.addTextChangedListener(this.mTextWatcher);
        this.mSearchSrcTextView.setOnEditorActionListener(this.mOnEditorActionListener);
        this.mSearchSrcTextView.setOnItemClickListener(this.mOnItemClickListener);
        this.mSearchSrcTextView.setOnItemSelectedListener(this.mOnItemSelectedListener);
        this.mSearchSrcTextView.setOnKeyListener(this.mTextKeyListener);
        this.mSearchSrcTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() { // from class: android.support.v7.widget.SearchView.4
            @Override // android.view.View.OnFocusChangeListener
            public final void onFocusChange(View v, boolean hasFocus) {
                if (SearchView.this.mOnQueryTextFocusChangeListener != null) {
                    SearchView.this.mOnQueryTextFocusChangeListener.onFocusChange(SearchView.this, hasFocus);
                }
            }
        });
        setIconifiedByDefault(a.getBoolean(R.styleable.SearchView_iconifiedByDefault, true));
        int maxWidth = a.getDimensionPixelSize(R.styleable.SearchView_android_maxWidth, -1);
        if (maxWidth != -1) {
            setMaxWidth(maxWidth);
        }
        this.mDefaultQueryHint = a.getText(R.styleable.SearchView_defaultQueryHint);
        this.mQueryHint = a.getText(R.styleable.SearchView_queryHint);
        int imeOptions = a.getInt(R.styleable.SearchView_android_imeOptions, -1);
        if (imeOptions != -1) {
            setImeOptions(imeOptions);
        }
        int inputType = a.getInt(R.styleable.SearchView_android_inputType, -1);
        if (inputType != -1) {
            setInputType(inputType);
        }
        boolean focusable = a.getBoolean(R.styleable.SearchView_android_focusable, true);
        setFocusable(focusable);
        a.mWrapped.recycle();
        this.mVoiceWebSearchIntent = new Intent("android.speech.action.WEB_SEARCH");
        this.mVoiceWebSearchIntent.addFlags(268435456);
        this.mVoiceWebSearchIntent.putExtra("android.speech.extra.LANGUAGE_MODEL", "web_search");
        this.mVoiceAppSearchIntent = new Intent("android.speech.action.RECOGNIZE_SPEECH");
        this.mVoiceAppSearchIntent.addFlags(268435456);
        this.mDropDownAnchor = findViewById(this.mSearchSrcTextView.getDropDownAnchor());
        if (this.mDropDownAnchor != null) {
            if (Build.VERSION.SDK_INT >= 11) {
                this.mDropDownAnchor.addOnLayoutChangeListener(new View.OnLayoutChangeListener() { // from class: android.support.v7.widget.SearchView.5
                    @Override // android.view.View.OnLayoutChangeListener
                    public final void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                        SearchView.access$300(SearchView.this);
                    }
                });
            } else {
                this.mDropDownAnchor.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() { // from class: android.support.v7.widget.SearchView.6
                    @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
                    public final void onGlobalLayout() {
                        SearchView.access$300(SearchView.this);
                    }
                });
            }
        }
        updateViewsVisibility(this.mIconifiedByDefault);
        updateQueryHint();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getSuggestionRowLayout() {
        return this.mSuggestionRowLayout;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getSuggestionCommitIconResId() {
        return this.mSuggestionCommitIconResId;
    }

    /* JADX WARN: Code restructure failed: missing block: B:34:0x00a4, code lost:            if (r0 == false) goto L46;     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void setSearchableInfo(android.app.SearchableInfo r9) {
        /*
            r8 = this;
            r4 = 0
            r7 = 65536(0x10000, float:9.1835E-41)
            r3 = 0
            r2 = 1
            r8.mSearchable = r9
            android.app.SearchableInfo r0 = r8.mSearchable
            if (r0 == 0) goto L79
            boolean r0 = android.support.v7.widget.SearchView.IS_AT_LEAST_FROYO
            if (r0 == 0) goto L76
            android.support.v7.widget.SearchView$SearchAutoComplete r0 = r8.mSearchSrcTextView
            android.app.SearchableInfo r1 = r8.mSearchable
            int r1 = r1.getSuggestThreshold()
            r0.setThreshold(r1)
            android.support.v7.widget.SearchView$SearchAutoComplete r0 = r8.mSearchSrcTextView
            android.app.SearchableInfo r1 = r8.mSearchable
            int r1 = r1.getImeOptions()
            r0.setImeOptions(r1)
            android.app.SearchableInfo r0 = r8.mSearchable
            int r0 = r0.getInputType()
            r1 = r0 & 15
            if (r1 != r2) goto L3f
            r1 = -65537(0xfffffffffffeffff, float:NaN)
            r0 = r0 & r1
            android.app.SearchableInfo r1 = r8.mSearchable
            java.lang.String r1 = r1.getSuggestAuthority()
            if (r1 == 0) goto L3f
            r0 = r0 | r7
            r1 = 524288(0x80000, float:7.34684E-40)
            r0 = r0 | r1
        L3f:
            android.support.v7.widget.SearchView$SearchAutoComplete r1 = r8.mSearchSrcTextView
            r1.setInputType(r0)
            android.support.v4.widget.CursorAdapter r0 = r8.mSuggestionsAdapter
            if (r0 == 0) goto L4d
            android.support.v4.widget.CursorAdapter r0 = r8.mSuggestionsAdapter
            r0.changeCursor(r4)
        L4d:
            android.app.SearchableInfo r0 = r8.mSearchable
            java.lang.String r0 = r0.getSuggestAuthority()
            if (r0 == 0) goto L76
            android.support.v7.widget.SuggestionsAdapter r0 = new android.support.v7.widget.SuggestionsAdapter
            android.content.Context r1 = r8.getContext()
            android.app.SearchableInfo r5 = r8.mSearchable
            java.util.WeakHashMap<java.lang.String, android.graphics.drawable.Drawable$ConstantState> r6 = r8.mOutsideDrawablesCache
            r0.<init>(r1, r8, r5, r6)
            r8.mSuggestionsAdapter = r0
            android.support.v7.widget.SearchView$SearchAutoComplete r0 = r8.mSearchSrcTextView
            android.support.v4.widget.CursorAdapter r1 = r8.mSuggestionsAdapter
            r0.setAdapter(r1)
            android.support.v4.widget.CursorAdapter r0 = r8.mSuggestionsAdapter
            android.support.v7.widget.SuggestionsAdapter r0 = (android.support.v7.widget.SuggestionsAdapter) r0
            boolean r1 = r8.mQueryRefinement
            if (r1 == 0) goto Lba
            r1 = 2
        L74:
            r0.mQueryRefinement = r1
        L76:
            r8.updateQueryHint()
        L79:
            boolean r0 = android.support.v7.widget.SearchView.IS_AT_LEAST_FROYO
            if (r0 == 0) goto Lcb
            android.app.SearchableInfo r0 = r8.mSearchable
            if (r0 == 0) goto Lc9
            android.app.SearchableInfo r0 = r8.mSearchable
            boolean r0 = r0.getVoiceSearchEnabled()
            if (r0 == 0) goto Lc9
            android.app.SearchableInfo r0 = r8.mSearchable
            boolean r0 = r0.getVoiceSearchLaunchWebSearch()
            if (r0 == 0) goto Lbc
            android.content.Intent r0 = r8.mVoiceWebSearchIntent
        L93:
            if (r0 == 0) goto Lc9
            android.content.Context r1 = r8.getContext()
            android.content.pm.PackageManager r1 = r1.getPackageManager()
            android.content.pm.ResolveInfo r0 = r1.resolveActivity(r0, r7)
            if (r0 == 0) goto Lc7
            r0 = r2
        La4:
            if (r0 == 0) goto Lcb
        La6:
            r8.mVoiceButtonEnabled = r2
            boolean r0 = r8.mVoiceButtonEnabled
            if (r0 == 0) goto Lb4
            android.support.v7.widget.SearchView$SearchAutoComplete r0 = r8.mSearchSrcTextView
            java.lang.String r1 = "nm"
            r0.setPrivateImeOptions(r1)
        Lb4:
            boolean r0 = r8.mIconified
            r8.updateViewsVisibility(r0)
            return
        Lba:
            r1 = r2
            goto L74
        Lbc:
            android.app.SearchableInfo r0 = r8.mSearchable
            boolean r0 = r0.getVoiceSearchLaunchRecognizer()
            if (r0 == 0) goto Lcd
            android.content.Intent r0 = r8.mVoiceAppSearchIntent
            goto L93
        Lc7:
            r0 = r3
            goto La4
        Lc9:
            r0 = r3
            goto La4
        Lcb:
            r2 = r3
            goto La6
        Lcd:
            r0 = r4
            goto L93
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v7.widget.SearchView.setSearchableInfo(android.app.SearchableInfo):void");
    }

    public void setAppSearchData(Bundle appSearchData) {
        this.mAppSearchData = appSearchData;
    }

    public void setImeOptions(int imeOptions) {
        this.mSearchSrcTextView.setImeOptions(imeOptions);
    }

    public int getImeOptions() {
        return this.mSearchSrcTextView.getImeOptions();
    }

    public void setInputType(int inputType) {
        this.mSearchSrcTextView.setInputType(inputType);
    }

    public int getInputType() {
        return this.mSearchSrcTextView.getInputType();
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean requestFocus(int direction, Rect previouslyFocusedRect) {
        if (!this.mClearingFocus && isFocusable()) {
            if (!this.mIconified) {
                boolean result = this.mSearchSrcTextView.requestFocus(direction, previouslyFocusedRect);
                if (result) {
                    updateViewsVisibility(false);
                    return result;
                }
                return result;
            }
            return super.requestFocus(direction, previouslyFocusedRect);
        }
        return false;
    }

    @Override // android.view.ViewGroup, android.view.View
    public void clearFocus() {
        this.mClearingFocus = true;
        setImeVisibility(false);
        super.clearFocus();
        this.mSearchSrcTextView.clearFocus();
        this.mClearingFocus = false;
    }

    public void setOnQueryTextListener(OnQueryTextListener listener) {
        this.mOnQueryChangeListener = listener;
    }

    public void setOnCloseListener(OnCloseListener listener) {
        this.mOnCloseListener = listener;
    }

    public void setOnQueryTextFocusChangeListener(View.OnFocusChangeListener listener) {
        this.mOnQueryTextFocusChangeListener = listener;
    }

    public void setOnSuggestionListener(OnSuggestionListener listener) {
        this.mOnSuggestionListener = listener;
    }

    public void setOnSearchClickListener(View.OnClickListener listener) {
        this.mOnSearchClickListener = listener;
    }

    public CharSequence getQuery() {
        return this.mSearchSrcTextView.getText();
    }

    public void setQuery(CharSequence query, boolean submit) {
        this.mSearchSrcTextView.setText(query);
        if (query != null) {
            this.mSearchSrcTextView.setSelection(this.mSearchSrcTextView.length());
            this.mUserQuery = query;
        }
        if (submit && !TextUtils.isEmpty(query)) {
            onSubmitQuery();
        }
    }

    public void setQueryHint(CharSequence hint) {
        this.mQueryHint = hint;
        updateQueryHint();
    }

    public CharSequence getQueryHint() {
        if (this.mQueryHint != null) {
            CharSequence hint = this.mQueryHint;
            return hint;
        }
        if (IS_AT_LEAST_FROYO && this.mSearchable != null && this.mSearchable.getHintId() != 0) {
            CharSequence hint2 = getContext().getText(this.mSearchable.getHintId());
            return hint2;
        }
        CharSequence hint3 = this.mDefaultQueryHint;
        return hint3;
    }

    public void setIconifiedByDefault(boolean iconified) {
        if (this.mIconifiedByDefault != iconified) {
            this.mIconifiedByDefault = iconified;
            updateViewsVisibility(iconified);
            updateQueryHint();
        }
    }

    public void setIconified(boolean iconify) {
        if (iconify) {
            onCloseClicked();
        } else {
            onSearchClicked();
        }
    }

    public void setSubmitButtonEnabled(boolean enabled) {
        this.mSubmitButtonEnabled = enabled;
        updateViewsVisibility(this.mIconified);
    }

    public void setQueryRefinementEnabled(boolean enable) {
        this.mQueryRefinement = enable;
        if (this.mSuggestionsAdapter instanceof SuggestionsAdapter) {
            ((SuggestionsAdapter) this.mSuggestionsAdapter).mQueryRefinement = enable ? 2 : 1;
        }
    }

    public void setSuggestionsAdapter(CursorAdapter adapter) {
        this.mSuggestionsAdapter = adapter;
        this.mSearchSrcTextView.setAdapter(this.mSuggestionsAdapter);
    }

    public CursorAdapter getSuggestionsAdapter() {
        return this.mSuggestionsAdapter;
    }

    public void setMaxWidth(int maxpixels) {
        this.mMaxWidth = maxpixels;
        requestLayout();
    }

    public int getMaxWidth() {
        return this.mMaxWidth;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v7.widget.LinearLayoutCompat, android.view.ViewGroup, android.view.View
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            SearchAutoComplete searchAutoComplete = this.mSearchSrcTextView;
            Rect rect = this.mSearchSrcTextViewBounds;
            searchAutoComplete.getLocationInWindow(this.mTemp);
            getLocationInWindow(this.mTemp2);
            int i = this.mTemp[1] - this.mTemp2[1];
            int i2 = this.mTemp[0] - this.mTemp2[0];
            rect.set(i2, i, searchAutoComplete.getWidth() + i2, searchAutoComplete.getHeight() + i);
            this.mSearchSrtTextViewBoundsExpanded.set(this.mSearchSrcTextViewBounds.left, 0, this.mSearchSrcTextViewBounds.right, bottom - top);
            if (this.mTouchDelegate == null) {
                this.mTouchDelegate = new UpdatableTouchDelegate(this.mSearchSrtTextViewBoundsExpanded, this.mSearchSrcTextViewBounds, this.mSearchSrcTextView);
                setTouchDelegate(this.mTouchDelegate);
            } else {
                this.mTouchDelegate.setBounds(this.mSearchSrtTextViewBoundsExpanded, this.mSearchSrcTextViewBounds);
            }
        }
    }

    private int getPreferredWidth() {
        return getContext().getResources().getDimensionPixelSize(R.dimen.abc_search_view_preferred_width);
    }

    private int getPreferredHeight() {
        return getContext().getResources().getDimensionPixelSize(R.dimen.abc_search_view_preferred_height);
    }

    private void updateViewsVisibility(boolean collapsed) {
        int iconVisibility;
        this.mIconified = collapsed;
        int visCollapsed = collapsed ? 0 : 8;
        boolean hasText = !TextUtils.isEmpty(this.mSearchSrcTextView.getText());
        this.mSearchButton.setVisibility(visCollapsed);
        updateSubmitButton(hasText);
        this.mSearchEditFrame.setVisibility(collapsed ? 8 : 0);
        if (this.mCollapsedIcon.getDrawable() == null || this.mIconifiedByDefault) {
            iconVisibility = 8;
        } else {
            iconVisibility = 0;
        }
        this.mCollapsedIcon.setVisibility(iconVisibility);
        updateCloseButton();
        updateVoiceButton(hasText ? false : true);
        updateSubmitArea();
    }

    private boolean isSubmitAreaEnabled() {
        return (this.mSubmitButtonEnabled || this.mVoiceButtonEnabled) && !this.mIconified;
    }

    private void updateSubmitButton(boolean hasText) {
        int visibility = 8;
        if (this.mSubmitButtonEnabled && isSubmitAreaEnabled() && hasFocus() && (hasText || !this.mVoiceButtonEnabled)) {
            visibility = 0;
        }
        this.mGoButton.setVisibility(visibility);
    }

    private void updateSubmitArea() {
        int visibility = 8;
        if (isSubmitAreaEnabled() && (this.mGoButton.getVisibility() == 0 || this.mVoiceButton.getVisibility() == 0)) {
            visibility = 0;
        }
        this.mSubmitArea.setVisibility(visibility);
    }

    private void updateCloseButton() {
        boolean showClose = true;
        boolean hasText = !TextUtils.isEmpty(this.mSearchSrcTextView.getText());
        if (!hasText && (!this.mIconifiedByDefault || this.mExpandedInActionView)) {
            showClose = false;
        }
        this.mCloseButton.setVisibility(showClose ? 0 : 8);
        Drawable closeButtonImg = this.mCloseButton.getDrawable();
        if (closeButtonImg != null) {
            closeButtonImg.setState(hasText ? ENABLED_STATE_SET : EMPTY_STATE_SET);
        }
    }

    private void postUpdateFocusedState() {
        post(this.mUpdateDrawableStateRunnable);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        removeCallbacks(this.mUpdateDrawableStateRunnable);
        post(this.mReleaseCursorRunnable);
        super.onDetachedFromWindow();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setImeVisibility(boolean visible) {
        if (visible) {
            post(this.mShowImeRunnable);
            return;
        }
        removeCallbacks(this.mShowImeRunnable);
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService("input_method");
        if (imm != null) {
            imm.hideSoftInputFromWindow(getWindowToken(), 0);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean onSuggestionsKey$33ade166(int keyCode, KeyEvent event) {
        if (this.mSearchable == null || this.mSuggestionsAdapter == null || event.getAction() != 0 || !KeyEventCompat.hasNoModifiers(event)) {
            return false;
        }
        if (keyCode == 66 || keyCode == 84 || keyCode == 61) {
            int position = this.mSearchSrcTextView.getListSelection();
            return onItemClicked$3e6d8123(position);
        }
        if (keyCode == 21 || keyCode == 22) {
            int selPoint = keyCode == 21 ? 0 : this.mSearchSrcTextView.length();
            this.mSearchSrcTextView.setSelection(selPoint);
            this.mSearchSrcTextView.setListSelection(0);
            this.mSearchSrcTextView.clearListSelection();
            HIDDEN_METHOD_INVOKER.ensureImeVisible$3d4581ed(this.mSearchSrcTextView);
            return true;
        }
        if (keyCode != 19 || this.mSearchSrcTextView.getListSelection() != 0) {
        }
        return false;
    }

    private void updateQueryHint() {
        CharSequence hint = getQueryHint();
        SearchAutoComplete searchAutoComplete = this.mSearchSrcTextView;
        if (hint == null) {
            hint = "";
        }
        if (this.mIconifiedByDefault && this.mSearchHintIcon != null) {
            int textSize = (int) (this.mSearchSrcTextView.getTextSize() * 1.25d);
            this.mSearchHintIcon.setBounds(0, 0, textSize, textSize);
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder("   ");
            spannableStringBuilder.setSpan(new ImageSpan(this.mSearchHintIcon), 1, 2, 33);
            spannableStringBuilder.append(hint);
            hint = spannableStringBuilder;
        }
        searchAutoComplete.setHint(hint);
    }

    private void updateVoiceButton(boolean empty) {
        int visibility = 8;
        if (this.mVoiceButtonEnabled && !this.mIconified && empty) {
            visibility = 0;
            this.mGoButton.setVisibility(8);
        }
        this.mVoiceButton.setVisibility(visibility);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onSubmitQuery() {
        CharSequence query = this.mSearchSrcTextView.getText();
        if (query != null && TextUtils.getTrimmedLength(query) > 0) {
            if (this.mOnQueryChangeListener != null) {
                OnQueryTextListener onQueryTextListener = this.mOnQueryChangeListener;
                query.toString();
                if (onQueryTextListener.onQueryTextSubmit$552c4dfd()) {
                    return;
                }
            }
            if (this.mSearchable != null) {
                launchQuerySearch$6ef37c42(query.toString());
            }
            setImeVisibility(false);
            this.mSearchSrcTextView.dismissDropDown();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onCloseClicked() {
        if (TextUtils.isEmpty(this.mSearchSrcTextView.getText())) {
            if (this.mIconifiedByDefault) {
                if (this.mOnCloseListener == null || !this.mOnCloseListener.onClose()) {
                    clearFocus();
                    updateViewsVisibility(true);
                    return;
                }
                return;
            }
            return;
        }
        this.mSearchSrcTextView.setText("");
        this.mSearchSrcTextView.requestFocus();
        setImeVisibility(true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onSearchClicked() {
        updateViewsVisibility(false);
        this.mSearchSrcTextView.requestFocus();
        setImeVisibility(true);
        if (this.mOnSearchClickListener != null) {
            this.mOnSearchClickListener.onClick(this);
        }
    }

    @Override // android.view.View
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        postUpdateFocusedState();
    }

    @Override // android.support.v7.view.CollapsibleActionView
    public final void onActionViewCollapsed() {
        setQuery("", false);
        clearFocus();
        updateViewsVisibility(true);
        this.mSearchSrcTextView.setImeOptions(this.mCollapsedImeOptions);
        this.mExpandedInActionView = false;
    }

    @Override // android.support.v7.view.CollapsibleActionView
    public final void onActionViewExpanded() {
        if (!this.mExpandedInActionView) {
            this.mExpandedInActionView = true;
            this.mCollapsedImeOptions = this.mSearchSrcTextView.getImeOptions();
            this.mSearchSrcTextView.setImeOptions(this.mCollapsedImeOptions | 33554432);
            this.mSearchSrcTextView.setText("");
            setIconified(false);
        }
    }

    /* loaded from: classes.dex */
    static class SavedState extends AbsSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR = ParcelableCompat.newCreator(new ParcelableCompatCreatorCallbacks<SavedState>() { // from class: android.support.v7.widget.SearchView.SavedState.1
            @Override // android.support.v4.os.ParcelableCompatCreatorCallbacks
            public final /* bridge */ /* synthetic */ SavedState[] newArray(int i) {
                return new SavedState[i];
            }

            @Override // android.support.v4.os.ParcelableCompatCreatorCallbacks
            public final /* bridge */ /* synthetic */ SavedState createFromParcel(Parcel parcel, ClassLoader classLoader) {
                return new SavedState(parcel, classLoader);
            }
        });
        boolean isIconified;

        SavedState(Parcelable superState) {
            super(superState);
        }

        public SavedState(Parcel source, ClassLoader loader) {
            super(source, loader);
            this.isIconified = ((Boolean) source.readValue(null)).booleanValue();
        }

        @Override // android.support.v4.view.AbsSavedState, android.os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeValue(Boolean.valueOf(this.isIconified));
        }

        public String toString() {
            return "SearchView.SavedState{" + Integer.toHexString(System.identityHashCode(this)) + " isIconified=" + this.isIconified + "}";
        }
    }

    @Override // android.view.View
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.isIconified = this.mIconified;
        return ss;
    }

    @Override // android.view.View
    protected void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.mSuperState);
        updateViewsVisibility(ss.isIconified);
        requestLayout();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean onItemClicked$3e6d8123(int position) {
        Intent createIntentFromSuggestion$5e9699a8;
        if (this.mOnSuggestionListener != null && this.mOnSuggestionListener.onSuggestionClick$134632()) {
            return false;
        }
        Cursor cursor = this.mSuggestionsAdapter.mCursor;
        if (cursor != null && cursor.moveToPosition(position) && (createIntentFromSuggestion$5e9699a8 = createIntentFromSuggestion$5e9699a8(cursor)) != null) {
            try {
                getContext().startActivity(createIntentFromSuggestion$5e9699a8);
            } catch (RuntimeException e) {
                Log.e("SearchView", "Failed launch activity: " + createIntentFromSuggestion$5e9699a8, e);
            }
        }
        setImeVisibility(false);
        this.mSearchSrcTextView.dismissDropDown();
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setQuery(CharSequence query) {
        this.mSearchSrcTextView.setText(query);
        this.mSearchSrcTextView.setSelection(TextUtils.isEmpty(query) ? 0 : query.length());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void launchQuerySearch$6ef37c42(String query) {
        Intent intent = createIntent("android.intent.action.SEARCH", null, null, query, 0, null);
        getContext().startActivity(intent);
    }

    private Intent createIntent(String action, Uri data, String extraData, String query, int actionKey, String actionMsg) {
        Intent intent = new Intent(action);
        intent.addFlags(268435456);
        if (data != null) {
            intent.setData(data);
        }
        intent.putExtra("user_query", this.mUserQuery);
        if (query != null) {
            intent.putExtra("query", query);
        }
        if (extraData != null) {
            intent.putExtra("intent_extra_data_key", extraData);
        }
        if (this.mAppSearchData != null) {
            intent.putExtra("app_data", this.mAppSearchData);
        }
        if (IS_AT_LEAST_FROYO) {
            intent.setComponent(this.mSearchable.getSearchActivity());
        }
        return intent;
    }

    private Intent createIntentFromSuggestion$5e9699a8(Cursor c) {
        int rowNum;
        String id;
        try {
            String action = SuggestionsAdapter.getColumnString(c, "suggest_intent_action");
            if (action == null && Build.VERSION.SDK_INT >= 8) {
                action = this.mSearchable.getSuggestIntentAction();
            }
            if (action == null) {
                action = "android.intent.action.SEARCH";
            }
            String data = SuggestionsAdapter.getColumnString(c, "suggest_intent_data");
            if (IS_AT_LEAST_FROYO && data == null) {
                data = this.mSearchable.getSuggestIntentData();
            }
            if (data != null && (id = SuggestionsAdapter.getColumnString(c, "suggest_intent_data_id")) != null) {
                data = data + "/" + Uri.encode(id);
            }
            Uri dataUri = data == null ? null : Uri.parse(data);
            String query = SuggestionsAdapter.getColumnString(c, "suggest_intent_query");
            String extraData = SuggestionsAdapter.getColumnString(c, "suggest_intent_extra_data");
            return createIntent(action, dataUri, extraData, query, 0, null);
        } catch (RuntimeException e) {
            try {
                rowNum = c.getPosition();
            } catch (RuntimeException e2) {
                rowNum = -1;
            }
            Log.w("SearchView", "Search suggestions cursor at row " + rowNum + " returned exception.", e);
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void forceSuggestionQuery() {
        AutoCompleteTextViewReflector autoCompleteTextViewReflector = HIDDEN_METHOD_INVOKER;
        SearchAutoComplete searchAutoComplete = this.mSearchSrcTextView;
        if (autoCompleteTextViewReflector.doBeforeTextChanged != null) {
            try {
                autoCompleteTextViewReflector.doBeforeTextChanged.invoke(searchAutoComplete, new Object[0]);
            } catch (Exception e) {
            }
        }
        AutoCompleteTextViewReflector autoCompleteTextViewReflector2 = HIDDEN_METHOD_INVOKER;
        SearchAutoComplete searchAutoComplete2 = this.mSearchSrcTextView;
        if (autoCompleteTextViewReflector2.doAfterTextChanged != null) {
            try {
                autoCompleteTextViewReflector2.doAfterTextChanged.invoke(searchAutoComplete2, new Object[0]);
            } catch (Exception e2) {
            }
        }
    }

    static boolean isLandscapeMode(Context context) {
        return context.getResources().getConfiguration().orientation == 2;
    }

    /* loaded from: classes.dex */
    private static class UpdatableTouchDelegate extends TouchDelegate {
        private final Rect mActualBounds;
        private boolean mDelegateTargeted;
        private final View mDelegateView;
        private final int mSlop;
        private final Rect mSlopBounds;
        private final Rect mTargetBounds;

        public UpdatableTouchDelegate(Rect targetBounds, Rect actualBounds, View delegateView) {
            super(targetBounds, delegateView);
            this.mSlop = ViewConfiguration.get(delegateView.getContext()).getScaledTouchSlop();
            this.mTargetBounds = new Rect();
            this.mSlopBounds = new Rect();
            this.mActualBounds = new Rect();
            setBounds(targetBounds, actualBounds);
            this.mDelegateView = delegateView;
        }

        public final void setBounds(Rect desiredBounds, Rect actualBounds) {
            this.mTargetBounds.set(desiredBounds);
            this.mSlopBounds.set(desiredBounds);
            this.mSlopBounds.inset(-this.mSlop, -this.mSlop);
            this.mActualBounds.set(actualBounds);
        }

        @Override // android.view.TouchDelegate
        public final boolean onTouchEvent(MotionEvent event) {
            int x = (int) event.getX();
            int y = (int) event.getY();
            boolean sendToDelegate = false;
            boolean hit = true;
            switch (event.getAction()) {
                case 0:
                    if (this.mTargetBounds.contains(x, y)) {
                        this.mDelegateTargeted = true;
                        sendToDelegate = true;
                        break;
                    }
                    break;
                case 1:
                case 2:
                    sendToDelegate = this.mDelegateTargeted;
                    if (sendToDelegate && !this.mSlopBounds.contains(x, y)) {
                        hit = false;
                        break;
                    }
                    break;
                case 3:
                    sendToDelegate = this.mDelegateTargeted;
                    this.mDelegateTargeted = false;
                    break;
            }
            if (!sendToDelegate) {
                return false;
            }
            if (hit && !this.mActualBounds.contains(x, y)) {
                event.setLocation(this.mDelegateView.getWidth() / 2, this.mDelegateView.getHeight() / 2);
            } else {
                event.setLocation(x - this.mActualBounds.left, y - this.mActualBounds.top);
            }
            boolean handled = this.mDelegateView.dispatchTouchEvent(event);
            return handled;
        }
    }

    /* loaded from: classes.dex */
    public static class SearchAutoComplete extends AppCompatAutoCompleteTextView {
        private SearchView mSearchView;
        private int mThreshold;

        public SearchAutoComplete(Context context) {
            this(context, null);
        }

        public SearchAutoComplete(Context context, AttributeSet attrs) {
            this(context, attrs, R.attr.autoCompleteTextViewStyle);
        }

        public SearchAutoComplete(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
            this.mThreshold = getThreshold();
        }

        @Override // android.view.View
        protected void onFinishInflate() {
            super.onFinishInflate();
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            setMinWidth((int) TypedValue.applyDimension(1, getSearchViewTextMinWidthDp(), metrics));
        }

        void setSearchView(SearchView searchView) {
            this.mSearchView = searchView;
        }

        @Override // android.widget.AutoCompleteTextView
        public void setThreshold(int threshold) {
            super.setThreshold(threshold);
            this.mThreshold = threshold;
        }

        @Override // android.widget.AutoCompleteTextView
        protected void replaceText(CharSequence text) {
        }

        @Override // android.widget.AutoCompleteTextView
        public void performCompletion() {
        }

        @Override // android.widget.AutoCompleteTextView, android.widget.TextView, android.view.View
        public void onWindowFocusChanged(boolean hasWindowFocus) {
            super.onWindowFocusChanged(hasWindowFocus);
            if (hasWindowFocus && this.mSearchView.hasFocus() && getVisibility() == 0) {
                ((InputMethodManager) getContext().getSystemService("input_method")).showSoftInput(this, 0);
                if (SearchView.isLandscapeMode(getContext())) {
                    SearchView.HIDDEN_METHOD_INVOKER.ensureImeVisible$3d4581ed(this);
                }
            }
        }

        @Override // android.widget.AutoCompleteTextView, android.widget.TextView, android.view.View
        protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
            super.onFocusChanged(focused, direction, previouslyFocusedRect);
            this.mSearchView.onTextFocusChanged();
        }

        @Override // android.widget.AutoCompleteTextView
        public boolean enoughToFilter() {
            return this.mThreshold <= 0 || super.enoughToFilter();
        }

        @Override // android.widget.AutoCompleteTextView, android.widget.TextView, android.view.View
        public boolean onKeyPreIme(int keyCode, KeyEvent event) {
            if (keyCode == 4) {
                if (event.getAction() == 0 && event.getRepeatCount() == 0) {
                    KeyEvent.DispatcherState state = getKeyDispatcherState();
                    if (state == null) {
                        return true;
                    }
                    state.startTracking(event, this);
                    return true;
                }
                if (event.getAction() == 1) {
                    KeyEvent.DispatcherState state2 = getKeyDispatcherState();
                    if (state2 != null) {
                        state2.handleUpEvent(event);
                    }
                    if (event.isTracking() && !event.isCanceled()) {
                        this.mSearchView.clearFocus();
                        this.mSearchView.setImeVisibility(false);
                        return true;
                    }
                }
            }
            return super.onKeyPreIme(keyCode, event);
        }

        private int getSearchViewTextMinWidthDp() {
            Configuration config = getResources().getConfiguration();
            int widthDp = ConfigurationHelper.getScreenWidthDp(getResources());
            int heightDp = ConfigurationHelper.getScreenHeightDp(getResources());
            if (widthDp >= 960 && heightDp >= 720 && config.orientation == 2) {
                return 256;
            }
            if (widthDp >= 600 || (widthDp >= 640 && heightDp >= 480)) {
                return com.nuance.swype.input.R.styleable.ThemeTemplate_btnKeyboardKeyNormalTop;
            }
            return 160;
        }

        static /* synthetic */ boolean access$1600(SearchAutoComplete x0) {
            return TextUtils.getTrimmedLength(x0.getText()) == 0;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class AutoCompleteTextViewReflector {
        Method doAfterTextChanged;
        Method doBeforeTextChanged;
        private Method ensureImeVisible;
        Method showSoftInputUnchecked;

        AutoCompleteTextViewReflector() {
            try {
                this.doBeforeTextChanged = AutoCompleteTextView.class.getDeclaredMethod("doBeforeTextChanged", new Class[0]);
                this.doBeforeTextChanged.setAccessible(true);
            } catch (NoSuchMethodException e) {
            }
            try {
                this.doAfterTextChanged = AutoCompleteTextView.class.getDeclaredMethod("doAfterTextChanged", new Class[0]);
                this.doAfterTextChanged.setAccessible(true);
            } catch (NoSuchMethodException e2) {
            }
            try {
                this.ensureImeVisible = AutoCompleteTextView.class.getMethod("ensureImeVisible", Boolean.TYPE);
                this.ensureImeVisible.setAccessible(true);
            } catch (NoSuchMethodException e3) {
            }
            try {
                this.showSoftInputUnchecked = InputMethodManager.class.getMethod("showSoftInputUnchecked", Integer.TYPE, ResultReceiver.class);
                this.showSoftInputUnchecked.setAccessible(true);
            } catch (NoSuchMethodException e4) {
            }
        }

        final void ensureImeVisible$3d4581ed(AutoCompleteTextView view) {
            if (this.ensureImeVisible != null) {
                try {
                    this.ensureImeVisible.invoke(view, true);
                } catch (Exception e) {
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v7.widget.LinearLayoutCompat, android.view.View
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (this.mIconified) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        switch (widthMode) {
            case Integers.STATUS_SUCCESS /* -2147483648 */:
                if (this.mMaxWidth > 0) {
                    width = Math.min(this.mMaxWidth, width);
                    break;
                } else {
                    width = Math.min(getPreferredWidth(), width);
                    break;
                }
            case 0:
                if (this.mMaxWidth <= 0) {
                    width = getPreferredWidth();
                    break;
                } else {
                    width = this.mMaxWidth;
                    break;
                }
            case 1073741824:
                if (this.mMaxWidth > 0) {
                    width = Math.min(this.mMaxWidth, width);
                    break;
                }
                break;
        }
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        int height = View.MeasureSpec.getSize(heightMeasureSpec);
        switch (heightMode) {
            case Integers.STATUS_SUCCESS /* -2147483648 */:
            case 0:
                height = Math.min(getPreferredHeight(), height);
                break;
        }
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(width, 1073741824), View.MeasureSpec.makeMeasureSpec(height, 1073741824));
    }

    final void onTextFocusChanged() {
        updateViewsVisibility(this.mIconified);
        postUpdateFocusedState();
        if (this.mSearchSrcTextView.hasFocus()) {
            forceSuggestionQuery();
        }
    }

    static /* synthetic */ void access$000(SearchView x0) {
        int[] iArr = x0.mSearchSrcTextView.hasFocus() ? FOCUSED_STATE_SET : EMPTY_STATE_SET;
        Drawable background = x0.mSearchPlate.getBackground();
        if (background != null) {
            background.setState(iArr);
        }
        Drawable background2 = x0.mSubmitArea.getBackground();
        if (background2 != null) {
            background2.setState(iArr);
        }
        x0.invalidate();
    }

    static /* synthetic */ void access$300(SearchView x0) {
        int i;
        int i2;
        if (x0.mDropDownAnchor.getWidth() <= 1) {
            return;
        }
        Resources resources = x0.getContext().getResources();
        int paddingLeft = x0.mSearchPlate.getPaddingLeft();
        Rect rect = new Rect();
        boolean isLayoutRtl = ViewUtils.isLayoutRtl(x0);
        if (x0.mIconifiedByDefault) {
            i = resources.getDimensionPixelSize(R.dimen.abc_dropdownitem_text_padding_left) + resources.getDimensionPixelSize(R.dimen.abc_dropdownitem_icon_width);
        } else {
            i = 0;
        }
        x0.mSearchSrcTextView.getDropDownBackground().getPadding(rect);
        if (isLayoutRtl) {
            i2 = -rect.left;
        } else {
            i2 = paddingLeft - (rect.left + i);
        }
        x0.mSearchSrcTextView.setDropDownHorizontalOffset(i2);
        x0.mSearchSrcTextView.setDropDownWidth((i + ((x0.mDropDownAnchor.getWidth() + rect.left) + rect.right)) - paddingLeft);
    }

    static /* synthetic */ void access$1100(SearchView x0) {
        String str;
        String str2;
        String str3;
        if (x0.mSearchable != null) {
            SearchableInfo searchableInfo = x0.mSearchable;
            try {
                if (!searchableInfo.getVoiceSearchLaunchWebSearch()) {
                    if (searchableInfo.getVoiceSearchLaunchRecognizer()) {
                        Intent intent = x0.mVoiceAppSearchIntent;
                        ComponentName searchActivity = searchableInfo.getSearchActivity();
                        Intent intent2 = new Intent("android.intent.action.SEARCH");
                        intent2.setComponent(searchActivity);
                        PendingIntent activity = PendingIntent.getActivity(x0.getContext(), 0, intent2, 1073741824);
                        Bundle bundle = new Bundle();
                        if (x0.mAppSearchData != null) {
                            bundle.putParcelable("app_data", x0.mAppSearchData);
                        }
                        Intent intent3 = new Intent(intent);
                        int i = 1;
                        if (Build.VERSION.SDK_INT < 8) {
                            str = null;
                            str2 = "free_form";
                            str3 = null;
                        } else {
                            Resources resources = x0.getResources();
                            if (searchableInfo.getVoiceLanguageModeId() == 0) {
                                str2 = "free_form";
                            } else {
                                str2 = resources.getString(searchableInfo.getVoiceLanguageModeId());
                            }
                            str = searchableInfo.getVoicePromptTextId() != 0 ? resources.getString(searchableInfo.getVoicePromptTextId()) : null;
                            str3 = searchableInfo.getVoiceLanguageId() != 0 ? resources.getString(searchableInfo.getVoiceLanguageId()) : null;
                            if (searchableInfo.getVoiceMaxResults() != 0) {
                                i = searchableInfo.getVoiceMaxResults();
                            }
                        }
                        intent3.putExtra("android.speech.extra.LANGUAGE_MODEL", str2);
                        intent3.putExtra("android.speech.extra.PROMPT", str);
                        intent3.putExtra("android.speech.extra.LANGUAGE", str3);
                        intent3.putExtra("android.speech.extra.MAX_RESULTS", i);
                        intent3.putExtra("calling_package", searchActivity != null ? searchActivity.flattenToShortString() : null);
                        intent3.putExtra("android.speech.extra.RESULTS_PENDINGINTENT", activity);
                        intent3.putExtra("android.speech.extra.RESULTS_PENDINGINTENT_BUNDLE", bundle);
                        x0.getContext().startActivity(intent3);
                        return;
                    }
                    return;
                }
                Intent intent4 = new Intent(x0.mVoiceWebSearchIntent);
                ComponentName searchActivity2 = searchableInfo.getSearchActivity();
                intent4.putExtra("calling_package", searchActivity2 != null ? searchActivity2.flattenToShortString() : null);
                x0.getContext().startActivity(intent4);
            } catch (ActivityNotFoundException e) {
                Log.w("SearchView", "Could not find voice search activity");
            }
        }
    }

    static /* synthetic */ boolean access$1900(SearchView x0, int x1) {
        if (x0.mOnSuggestionListener != null && x0.mOnSuggestionListener.onSuggestionSelect$134632()) {
            return false;
        }
        Editable text = x0.mSearchSrcTextView.getText();
        Cursor cursor = x0.mSuggestionsAdapter.mCursor;
        if (cursor != null) {
            if (cursor.moveToPosition(x1)) {
                CharSequence convertToString = x0.mSuggestionsAdapter.convertToString(cursor);
                if (convertToString != null) {
                    x0.setQuery(convertToString);
                } else {
                    x0.setQuery(text);
                }
            } else {
                x0.setQuery(text);
            }
        }
        return true;
    }

    static /* synthetic */ void access$2000(SearchView x0, CharSequence x1) {
        Editable text = x0.mSearchSrcTextView.getText();
        x0.mUserQuery = text;
        boolean z = !TextUtils.isEmpty(text);
        x0.updateSubmitButton(z);
        x0.updateVoiceButton(z ? false : true);
        x0.updateCloseButton();
        x0.updateSubmitArea();
        if (x0.mOnQueryChangeListener != null && !TextUtils.equals(x1, x0.mOldQueryText)) {
            x1.toString();
        }
        x0.mOldQueryText = x1.toString();
    }
}
