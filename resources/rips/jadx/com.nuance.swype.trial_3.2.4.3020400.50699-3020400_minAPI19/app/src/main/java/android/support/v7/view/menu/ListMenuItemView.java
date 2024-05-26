package android.support.v7.view.menu;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.appcompat.R;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.TintTypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

/* loaded from: classes.dex */
public class ListMenuItemView extends LinearLayout implements MenuView.ItemView {
    private Drawable mBackground;
    private CheckBox mCheckBox;
    private boolean mForceShowIcon;
    private ImageView mIconView;
    private LayoutInflater mInflater;
    private MenuItemImpl mItemData;
    private int mMenuType;
    private boolean mPreserveIconSpacing;
    private RadioButton mRadioButton;
    private TextView mShortcutView;
    private Drawable mSubMenuArrow;
    private ImageView mSubMenuArrowView;
    private int mTextAppearance;
    private Context mTextAppearanceContext;
    private TextView mTitleView;

    public ListMenuItemView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.listMenuViewStyle);
    }

    public ListMenuItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs);
        TintTypedArray a = TintTypedArray.obtainStyledAttributes(getContext(), attrs, R.styleable.MenuView, defStyleAttr, 0);
        this.mBackground = a.getDrawable(R.styleable.MenuView_android_itemBackground);
        this.mTextAppearance = a.getResourceId(R.styleable.MenuView_android_itemTextAppearance, -1);
        this.mPreserveIconSpacing = a.getBoolean(R.styleable.MenuView_preserveIconSpacing, false);
        this.mTextAppearanceContext = context;
        this.mSubMenuArrow = a.getDrawable(R.styleable.MenuView_subMenuArrow);
        a.mWrapped.recycle();
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        setBackgroundDrawable(this.mBackground);
        this.mTitleView = (TextView) findViewById(R.id.title);
        if (this.mTextAppearance != -1) {
            this.mTitleView.setTextAppearance(this.mTextAppearanceContext, this.mTextAppearance);
        }
        this.mShortcutView = (TextView) findViewById(R.id.shortcut);
        this.mSubMenuArrowView = (ImageView) findViewById(R.id.submenuarrow);
        if (this.mSubMenuArrowView != null) {
            this.mSubMenuArrowView.setImageDrawable(this.mSubMenuArrow);
        }
    }

    @Override // android.support.v7.view.menu.MenuView.ItemView
    public final void initialize$667f453d(MenuItemImpl itemData) {
        this.mItemData = itemData;
        this.mMenuType = 0;
        setVisibility(itemData.isVisible() ? 0 : 8);
        setTitle(itemData.getTitleForItemView(this));
        setCheckable(itemData.isCheckable());
        setShortcut(itemData.shouldShowShortcut(), itemData.getShortcut());
        setIcon(itemData.getIcon());
        setEnabled(itemData.isEnabled());
        setSubMenuArrowVisible(itemData.hasSubMenu());
    }

    public void setForceShowIcon(boolean forceShow) {
        this.mForceShowIcon = forceShow;
        this.mPreserveIconSpacing = forceShow;
    }

    public void setTitle(CharSequence title) {
        if (title != null) {
            this.mTitleView.setText(title);
            if (this.mTitleView.getVisibility() != 0) {
                this.mTitleView.setVisibility(0);
                return;
            }
            return;
        }
        if (this.mTitleView.getVisibility() != 8) {
            this.mTitleView.setVisibility(8);
        }
    }

    @Override // android.support.v7.view.menu.MenuView.ItemView
    public MenuItemImpl getItemData() {
        return this.mItemData;
    }

    public void setCheckable(boolean checkable) {
        CompoundButton compoundButton;
        CompoundButton otherCompoundButton;
        if (checkable || this.mRadioButton != null || this.mCheckBox != null) {
            if (this.mItemData.isExclusiveCheckable()) {
                if (this.mRadioButton == null) {
                    insertRadioButton();
                }
                compoundButton = this.mRadioButton;
                otherCompoundButton = this.mCheckBox;
            } else {
                if (this.mCheckBox == null) {
                    insertCheckBox();
                }
                compoundButton = this.mCheckBox;
                otherCompoundButton = this.mRadioButton;
            }
            if (checkable) {
                compoundButton.setChecked(this.mItemData.isChecked());
                int newVisibility = checkable ? 0 : 8;
                if (compoundButton.getVisibility() != newVisibility) {
                    compoundButton.setVisibility(newVisibility);
                }
                if (otherCompoundButton != null && otherCompoundButton.getVisibility() != 8) {
                    otherCompoundButton.setVisibility(8);
                    return;
                }
                return;
            }
            if (this.mCheckBox != null) {
                this.mCheckBox.setVisibility(8);
            }
            if (this.mRadioButton != null) {
                this.mRadioButton.setVisibility(8);
            }
        }
    }

    public void setChecked(boolean checked) {
        CompoundButton compoundButton;
        if (this.mItemData.isExclusiveCheckable()) {
            if (this.mRadioButton == null) {
                insertRadioButton();
            }
            compoundButton = this.mRadioButton;
        } else {
            if (this.mCheckBox == null) {
                insertCheckBox();
            }
            compoundButton = this.mCheckBox;
        }
        compoundButton.setChecked(checked);
    }

    private void setSubMenuArrowVisible(boolean hasSubmenu) {
        if (this.mSubMenuArrowView != null) {
            this.mSubMenuArrowView.setVisibility(hasSubmenu ? 0 : 8);
        }
    }

    public void setShortcut(boolean showShortcut, char shortcutKey) {
        String sb;
        int newVisibility = (showShortcut && this.mItemData.shouldShowShortcut()) ? 0 : 8;
        if (newVisibility == 0) {
            TextView textView = this.mShortcutView;
            char shortcut = this.mItemData.getShortcut();
            if (shortcut == 0) {
                sb = "";
            } else {
                StringBuilder sb2 = new StringBuilder(MenuItemImpl.sPrependShortcutLabel);
                switch (shortcut) {
                    case '\b':
                        sb2.append(MenuItemImpl.sDeleteShortcutLabel);
                        break;
                    case '\n':
                        sb2.append(MenuItemImpl.sEnterShortcutLabel);
                        break;
                    case ' ':
                        sb2.append(MenuItemImpl.sSpaceShortcutLabel);
                        break;
                    default:
                        sb2.append(shortcut);
                        break;
                }
                sb = sb2.toString();
            }
            textView.setText(sb);
        }
        if (this.mShortcutView.getVisibility() != newVisibility) {
            this.mShortcutView.setVisibility(newVisibility);
        }
    }

    public void setIcon(Drawable icon) {
        boolean showIcon = this.mItemData.mMenu.mOptionalIconsVisible || this.mForceShowIcon;
        if (showIcon || this.mPreserveIconSpacing) {
            if (this.mIconView != null || icon != null || this.mPreserveIconSpacing) {
                if (this.mIconView == null) {
                    this.mIconView = (ImageView) getInflater().inflate(R.layout.abc_list_menu_item_icon, (ViewGroup) this, false);
                    addView(this.mIconView, 0);
                }
                if (icon != null || this.mPreserveIconSpacing) {
                    ImageView imageView = this.mIconView;
                    if (!showIcon) {
                        icon = null;
                    }
                    imageView.setImageDrawable(icon);
                    if (this.mIconView.getVisibility() != 0) {
                        this.mIconView.setVisibility(0);
                        return;
                    }
                    return;
                }
                this.mIconView.setVisibility(8);
            }
        }
    }

    @Override // android.widget.LinearLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (this.mIconView != null && this.mPreserveIconSpacing) {
            ViewGroup.LayoutParams lp = getLayoutParams();
            LinearLayout.LayoutParams iconLp = (LinearLayout.LayoutParams) this.mIconView.getLayoutParams();
            if (lp.height > 0 && iconLp.width <= 0) {
                iconLp.width = lp.height;
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void insertRadioButton() {
        LayoutInflater inflater = getInflater();
        this.mRadioButton = (RadioButton) inflater.inflate(R.layout.abc_list_menu_item_radio, (ViewGroup) this, false);
        addView(this.mRadioButton);
    }

    private void insertCheckBox() {
        LayoutInflater inflater = getInflater();
        this.mCheckBox = (CheckBox) inflater.inflate(R.layout.abc_list_menu_item_checkbox, (ViewGroup) this, false);
        addView(this.mCheckBox);
    }

    @Override // android.support.v7.view.menu.MenuView.ItemView
    public final boolean prefersCondensedTitle() {
        return false;
    }

    private LayoutInflater getInflater() {
        if (this.mInflater == null) {
            this.mInflater = LayoutInflater.from(getContext());
        }
        return this.mInflater;
    }
}
