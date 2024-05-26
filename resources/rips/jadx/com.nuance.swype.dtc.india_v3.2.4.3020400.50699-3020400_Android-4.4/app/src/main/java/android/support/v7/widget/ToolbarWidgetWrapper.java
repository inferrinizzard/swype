package android.support.v7.widget;

import android.graphics.drawable.Drawable;
import android.support.v7.appcompat.R;
import android.support.v7.view.menu.ActionMenuItem;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPresenter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

/* loaded from: classes.dex */
public final class ToolbarWidgetWrapper implements DecorToolbar {
    private ActionMenuPresenter mActionMenuPresenter;
    private View mCustomView;
    private int mDefaultNavigationContentDescription;
    private Drawable mDefaultNavigationIcon;
    private int mDisplayOpts;
    private final AppCompatDrawableManager mDrawableManager;
    private CharSequence mHomeDescription;
    private Drawable mIcon;
    private Drawable mLogo;
    boolean mMenuPrepared;
    private Drawable mNavIcon;
    private int mNavigationMode;
    private CharSequence mSubtitle;
    CharSequence mTitle;
    private boolean mTitleSet;
    Toolbar mToolbar;
    Window.Callback mWindowCallback;

    public ToolbarWidgetWrapper(Toolbar toolbar) {
        this(toolbar, R.string.abc_action_bar_up_description);
    }

    private ToolbarWidgetWrapper(Toolbar toolbar, int defaultNavigationContentDescription) {
        this.mNavigationMode = 0;
        this.mDefaultNavigationContentDescription = 0;
        this.mToolbar = toolbar;
        this.mTitle = toolbar.getTitle();
        this.mSubtitle = toolbar.getSubtitle();
        this.mTitleSet = this.mTitle != null;
        this.mNavIcon = toolbar.getNavigationIcon();
        TintTypedArray a = TintTypedArray.obtainStyledAttributes(toolbar.getContext(), null, R.styleable.ActionBar, R.attr.actionBarStyle, 0);
        this.mDefaultNavigationIcon = a.getDrawable(R.styleable.ActionBar_homeAsUpIndicator);
        CharSequence title = a.getText(R.styleable.ActionBar_title);
        if (!TextUtils.isEmpty(title)) {
            this.mTitleSet = true;
            setTitleInt(title);
        }
        CharSequence subtitle = a.getText(R.styleable.ActionBar_subtitle);
        if (!TextUtils.isEmpty(subtitle)) {
            this.mSubtitle = subtitle;
            if ((this.mDisplayOpts & 8) != 0) {
                this.mToolbar.setSubtitle(subtitle);
            }
        }
        Drawable logo = a.getDrawable(R.styleable.ActionBar_logo);
        if (logo != null) {
            setLogo(logo);
        }
        Drawable icon = a.getDrawable(R.styleable.ActionBar_icon);
        if (icon != null) {
            setIcon(icon);
        }
        if (this.mNavIcon == null && this.mDefaultNavigationIcon != null) {
            this.mNavIcon = this.mDefaultNavigationIcon;
            updateNavigationIcon();
        }
        setDisplayOptions(a.getInt(R.styleable.ActionBar_displayOptions, 0));
        int customNavId = a.getResourceId(R.styleable.ActionBar_customNavigationLayout, 0);
        if (customNavId != 0) {
            View inflate = LayoutInflater.from(this.mToolbar.getContext()).inflate(customNavId, (ViewGroup) this.mToolbar, false);
            if (this.mCustomView != null && (this.mDisplayOpts & 16) != 0) {
                this.mToolbar.removeView(this.mCustomView);
            }
            this.mCustomView = inflate;
            if (inflate != null && (this.mDisplayOpts & 16) != 0) {
                this.mToolbar.addView(this.mCustomView);
            }
            setDisplayOptions(this.mDisplayOpts | 16);
        }
        int height = a.getLayoutDimension(R.styleable.ActionBar_height, 0);
        if (height > 0) {
            ViewGroup.LayoutParams lp = this.mToolbar.getLayoutParams();
            lp.height = height;
            this.mToolbar.setLayoutParams(lp);
        }
        int contentInsetStart = a.getDimensionPixelOffset(R.styleable.ActionBar_contentInsetStart, -1);
        int contentInsetEnd = a.getDimensionPixelOffset(R.styleable.ActionBar_contentInsetEnd, -1);
        if (contentInsetStart >= 0 || contentInsetEnd >= 0) {
            this.mToolbar.setContentInsetsRelative(Math.max(contentInsetStart, 0), Math.max(contentInsetEnd, 0));
        }
        int titleTextStyle = a.getResourceId(R.styleable.ActionBar_titleTextStyle, 0);
        if (titleTextStyle != 0) {
            this.mToolbar.setTitleTextAppearance(this.mToolbar.getContext(), titleTextStyle);
        }
        int subtitleTextStyle = a.getResourceId(R.styleable.ActionBar_subtitleTextStyle, 0);
        if (subtitleTextStyle != 0) {
            this.mToolbar.setSubtitleTextAppearance(this.mToolbar.getContext(), subtitleTextStyle);
        }
        int popupTheme = a.getResourceId(R.styleable.ActionBar_popupTheme, 0);
        if (popupTheme != 0) {
            this.mToolbar.setPopupTheme(popupTheme);
        }
        a.mWrapped.recycle();
        this.mDrawableManager = AppCompatDrawableManager.get();
        if (defaultNavigationContentDescription != this.mDefaultNavigationContentDescription) {
            this.mDefaultNavigationContentDescription = defaultNavigationContentDescription;
            if (TextUtils.isEmpty(this.mToolbar.getNavigationContentDescription())) {
                int i = this.mDefaultNavigationContentDescription;
                this.mHomeDescription = i == 0 ? null : this.mToolbar.getContext().getString(i);
                updateHomeAccessibility();
            }
        }
        this.mHomeDescription = this.mToolbar.getNavigationContentDescription();
        this.mToolbar.setNavigationOnClickListener(new View.OnClickListener() { // from class: android.support.v7.widget.ToolbarWidgetWrapper.1
            final ActionMenuItem mNavItem;

            {
                this.mNavItem = new ActionMenuItem(ToolbarWidgetWrapper.this.mToolbar.getContext(), ToolbarWidgetWrapper.this.mTitle);
            }

            @Override // android.view.View.OnClickListener
            public final void onClick(View v) {
                if (ToolbarWidgetWrapper.this.mWindowCallback != null && ToolbarWidgetWrapper.this.mMenuPrepared) {
                    ToolbarWidgetWrapper.this.mWindowCallback.onMenuItemSelected(0, this.mNavItem);
                }
            }
        });
    }

    @Override // android.support.v7.widget.DecorToolbar
    public final void setWindowCallback(Window.Callback cb) {
        this.mWindowCallback = cb;
    }

    @Override // android.support.v7.widget.DecorToolbar
    public final void setWindowTitle(CharSequence title) {
        if (!this.mTitleSet) {
            setTitleInt(title);
        }
    }

    @Override // android.support.v7.widget.DecorToolbar
    public final CharSequence getTitle() {
        return this.mToolbar.getTitle();
    }

    private void setTitleInt(CharSequence title) {
        this.mTitle = title;
        if ((this.mDisplayOpts & 8) != 0) {
            this.mToolbar.setTitle(title);
        }
    }

    @Override // android.support.v7.widget.DecorToolbar
    public final void setIcon(int resId) {
        Drawable drawable;
        if (resId == 0) {
            drawable = null;
        } else {
            drawable = this.mDrawableManager.getDrawable(this.mToolbar.getContext(), resId, false);
        }
        setIcon(drawable);
    }

    @Override // android.support.v7.widget.DecorToolbar
    public final void setIcon(Drawable d) {
        this.mIcon = d;
        updateToolbarLogo();
    }

    @Override // android.support.v7.widget.DecorToolbar
    public final void setLogo(int resId) {
        Drawable drawable;
        if (resId == 0) {
            drawable = null;
        } else {
            drawable = this.mDrawableManager.getDrawable(this.mToolbar.getContext(), resId, false);
        }
        setLogo(drawable);
    }

    private void setLogo(Drawable d) {
        this.mLogo = d;
        updateToolbarLogo();
    }

    private void updateToolbarLogo() {
        Drawable logo = null;
        if ((this.mDisplayOpts & 2) != 0) {
            if ((this.mDisplayOpts & 1) != 0) {
                logo = this.mLogo != null ? this.mLogo : this.mIcon;
            } else {
                logo = this.mIcon;
            }
        }
        this.mToolbar.setLogo(logo);
    }

    @Override // android.support.v7.widget.DecorToolbar
    public final void setMenuPrepared() {
        this.mMenuPrepared = true;
    }

    @Override // android.support.v7.widget.DecorToolbar
    public final void setMenu(Menu menu, MenuPresenter.Callback cb) {
        if (this.mActionMenuPresenter == null) {
            this.mActionMenuPresenter = new ActionMenuPresenter(this.mToolbar.getContext());
            this.mActionMenuPresenter.mId = R.id.action_menu_presenter;
        }
        this.mActionMenuPresenter.mCallback = cb;
        this.mToolbar.setMenu((MenuBuilder) menu, this.mActionMenuPresenter);
    }

    private void setDisplayOptions(int newOpts) {
        int changed = this.mDisplayOpts ^ newOpts;
        this.mDisplayOpts = newOpts;
        if (changed != 0) {
            if ((changed & 4) != 0) {
                if ((newOpts & 4) != 0) {
                    updateHomeAccessibility();
                }
                updateNavigationIcon();
            }
            if ((changed & 3) != 0) {
                updateToolbarLogo();
            }
            if ((changed & 8) != 0) {
                if ((newOpts & 8) != 0) {
                    this.mToolbar.setTitle(this.mTitle);
                    this.mToolbar.setSubtitle(this.mSubtitle);
                } else {
                    this.mToolbar.setTitle((CharSequence) null);
                    this.mToolbar.setSubtitle((CharSequence) null);
                }
            }
            if ((changed & 16) != 0 && this.mCustomView != null) {
                if ((newOpts & 16) != 0) {
                    this.mToolbar.addView(this.mCustomView);
                } else {
                    this.mToolbar.removeView(this.mCustomView);
                }
            }
        }
    }

    private void updateNavigationIcon() {
        if ((this.mDisplayOpts & 4) != 0) {
            this.mToolbar.setNavigationIcon(this.mNavIcon != null ? this.mNavIcon : this.mDefaultNavigationIcon);
        } else {
            this.mToolbar.setNavigationIcon((Drawable) null);
        }
    }

    private void updateHomeAccessibility() {
        if ((this.mDisplayOpts & 4) != 0) {
            if (TextUtils.isEmpty(this.mHomeDescription)) {
                this.mToolbar.setNavigationContentDescription(this.mDefaultNavigationContentDescription);
            } else {
                this.mToolbar.setNavigationContentDescription(this.mHomeDescription);
            }
        }
    }
}
