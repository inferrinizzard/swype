package android.support.v7.view;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.XmlResourceParser;
import android.support.v4.internal.view.SupportMenu;
import android.support.v4.internal.view.SupportMenuItem;
import android.support.v4.view.ActionProvider;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.view.menu.MenuItemImpl;
import android.support.v7.view.menu.MenuItemWrapperICS;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Xml;
import android.view.InflateException;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import org.xmlpull.v1.XmlPullParserException;

/* loaded from: classes.dex */
public final class SupportMenuInflater extends MenuInflater {
    private static final Class<?>[] ACTION_PROVIDER_CONSTRUCTOR_SIGNATURE;
    private static final Class<?>[] ACTION_VIEW_CONSTRUCTOR_SIGNATURE;
    private final Object[] mActionProviderConstructorArguments;
    private final Object[] mActionViewConstructorArguments;
    private Context mContext;
    private Object mRealOwner;

    static {
        Class<?>[] clsArr = {Context.class};
        ACTION_VIEW_CONSTRUCTOR_SIGNATURE = clsArr;
        ACTION_PROVIDER_CONSTRUCTOR_SIGNATURE = clsArr;
    }

    public SupportMenuInflater(Context context) {
        super(context);
        this.mContext = context;
        this.mActionViewConstructorArguments = new Object[]{context};
        this.mActionProviderConstructorArguments = this.mActionViewConstructorArguments;
    }

    @Override // android.view.MenuInflater
    public final void inflate(int menuRes, Menu menu) {
        if (!(menu instanceof SupportMenu)) {
            super.inflate(menuRes, menu);
            return;
        }
        XmlResourceParser parser = null;
        try {
            try {
                parser = this.mContext.getResources().getLayout(menuRes);
                AttributeSet attrs = Xml.asAttributeSet(parser);
                parseMenu(parser, attrs, menu);
            } catch (IOException e) {
                throw new InflateException("Error inflating menu XML", e);
            } catch (XmlPullParserException e2) {
                throw new InflateException("Error inflating menu XML", e2);
            }
        } finally {
            if (parser != null) {
                parser.close();
            }
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:10:0x0048, code lost:            if (r1 != false) goto L79;     */
    /* JADX WARN: Code restructure failed: missing block: B:11:0x004a, code lost:            r5 = r13.getName();     */
    /* JADX WARN: Code restructure failed: missing block: B:12:0x0055, code lost:            if (r5.equals("group") == false) goto L21;     */
    /* JADX WARN: Code restructure failed: missing block: B:13:0x0057, code lost:            r7 = r2.this$0.mContext.obtainStyledAttributes(r14, android.support.v7.appcompat.R.styleable.MenuGroup);        r2.groupId = r7.getResourceId(android.support.v7.appcompat.R.styleable.MenuGroup_android_id, 0);        r2.groupCategory = r7.getInt(android.support.v7.appcompat.R.styleable.MenuGroup_android_menuCategory, 0);        r2.groupOrder = r7.getInt(android.support.v7.appcompat.R.styleable.MenuGroup_android_orderInCategory, 0);        r2.groupCheckable = r7.getInt(android.support.v7.appcompat.R.styleable.MenuGroup_android_checkableBehavior, 0);        r2.groupVisible = r7.getBoolean(android.support.v7.appcompat.R.styleable.MenuGroup_android_visible, true);        r2.groupEnabled = r7.getBoolean(android.support.v7.appcompat.R.styleable.MenuGroup_android_enabled, true);        r7.recycle();     */
    /* JADX WARN: Code restructure failed: missing block: B:15:0x0025, code lost:            r0 = r13.next();     */
    /* JADX WARN: Code restructure failed: missing block: B:17:0x00a2, code lost:            if (r5.equals("item") == false) goto L45;     */
    /* JADX WARN: Code restructure failed: missing block: B:18:0x00a4, code lost:            r8 = r2.this$0.mContext.obtainStyledAttributes(r14, android.support.v7.appcompat.R.styleable.MenuItem);        r2.itemId = r8.getResourceId(android.support.v7.appcompat.R.styleable.MenuItem_android_id, 0);        r2.itemCategoryOrder = (r8.getInt(android.support.v7.appcompat.R.styleable.MenuItem_android_menuCategory, r2.groupCategory) & (-65536)) | (r8.getInt(android.support.v7.appcompat.R.styleable.MenuItem_android_orderInCategory, r2.groupOrder) & 65535);        r2.itemTitle = r8.getText(android.support.v7.appcompat.R.styleable.MenuItem_android_title);        r2.itemTitleCondensed = r8.getText(android.support.v7.appcompat.R.styleable.MenuItem_android_titleCondensed);        r2.itemIconResId = r8.getResourceId(android.support.v7.appcompat.R.styleable.MenuItem_android_icon, 0);        r2.itemAlphabeticShortcut = android.support.v7.view.SupportMenuInflater.MenuState.getShortcut(r8.getString(android.support.v7.appcompat.R.styleable.MenuItem_android_alphabeticShortcut));        r2.itemNumericShortcut = android.support.v7.view.SupportMenuInflater.MenuState.getShortcut(r8.getString(android.support.v7.appcompat.R.styleable.MenuItem_android_numericShortcut));     */
    /* JADX WARN: Code restructure failed: missing block: B:19:0x0108, code lost:            if (r8.hasValue(android.support.v7.appcompat.R.styleable.MenuItem_android_checkable) == false) goto L40;     */
    /* JADX WARN: Code restructure failed: missing block: B:21:0x0111, code lost:            if (r8.getBoolean(android.support.v7.appcompat.R.styleable.MenuItem_android_checkable, false) == false) goto L39;     */
    /* JADX WARN: Code restructure failed: missing block: B:22:0x0113, code lost:            r7 = 1;     */
    /* JADX WARN: Code restructure failed: missing block: B:23:0x0114, code lost:            r2.itemCheckable = r7;     */
    /* JADX WARN: Code restructure failed: missing block: B:24:0x0116, code lost:            r2.itemChecked = r8.getBoolean(android.support.v7.appcompat.R.styleable.MenuItem_android_checked, false);        r2.itemVisible = r8.getBoolean(android.support.v7.appcompat.R.styleable.MenuItem_android_visible, r2.groupVisible);        r2.itemEnabled = r8.getBoolean(android.support.v7.appcompat.R.styleable.MenuItem_android_enabled, r2.groupEnabled);        r2.itemShowAsAction = r8.getInt(android.support.v7.appcompat.R.styleable.MenuItem_showAsAction, -1);        r2.itemListenerMethodName = r8.getString(android.support.v7.appcompat.R.styleable.MenuItem_android_onClick);        r2.itemActionViewLayout = r8.getResourceId(android.support.v7.appcompat.R.styleable.MenuItem_actionLayout, 0);        r2.itemActionViewClassName = r8.getString(android.support.v7.appcompat.R.styleable.MenuItem_actionViewClass);        r2.itemActionProviderClassName = r8.getString(android.support.v7.appcompat.R.styleable.MenuItem_actionProviderClass);     */
    /* JADX WARN: Code restructure failed: missing block: B:25:0x015f, code lost:            if (r2.itemActionProviderClassName == null) goto L41;     */
    /* JADX WARN: Code restructure failed: missing block: B:26:0x0161, code lost:            r7 = true;     */
    /* JADX WARN: Code restructure failed: missing block: B:27:0x0162, code lost:            if (r7 == false) goto L42;     */
    /* JADX WARN: Code restructure failed: missing block: B:29:0x0166, code lost:            if (r2.itemActionViewLayout != 0) goto L42;     */
    /* JADX WARN: Code restructure failed: missing block: B:31:0x016a, code lost:            if (r2.itemActionViewClassName != null) goto L42;     */
    /* JADX WARN: Code restructure failed: missing block: B:32:0x016c, code lost:            r2.itemActionProvider = (android.support.v4.view.ActionProvider) r2.newInstance(r2.itemActionProviderClassName, android.support.v7.view.SupportMenuInflater.ACTION_PROVIDER_CONSTRUCTOR_SIGNATURE, r2.this$0.mActionProviderConstructorArguments);     */
    /* JADX WARN: Code restructure failed: missing block: B:33:0x017c, code lost:            r8.recycle();        r2.itemAdded = false;     */
    /* JADX WARN: Code restructure failed: missing block: B:35:0x018d, code lost:            if (r7 == false) goto L44;     */
    /* JADX WARN: Code restructure failed: missing block: B:36:0x018f, code lost:            android.util.Log.w("SupportMenuInflater", "Ignoring attribute 'actionProviderClass'. Action view already specified.");     */
    /* JADX WARN: Code restructure failed: missing block: B:37:0x0198, code lost:            r2.itemActionProvider = null;     */
    /* JADX WARN: Code restructure failed: missing block: B:38:0x018b, code lost:            r7 = false;     */
    /* JADX WARN: Code restructure failed: missing block: B:39:0x0184, code lost:            r7 = 0;     */
    /* JADX WARN: Code restructure failed: missing block: B:40:0x0186, code lost:            r2.itemCheckable = r2.groupCheckable;     */
    /* JADX WARN: Code restructure failed: missing block: B:42:0x01a3, code lost:            if (r5.equals("menu") == false) goto L48;     */
    /* JADX WARN: Code restructure failed: missing block: B:43:0x01a5, code lost:            r4 = r2.addSubMenuItem();        parseMenu(r13, r14, r4);     */
    /* JADX WARN: Code restructure failed: missing block: B:45:0x01ae, code lost:            r1 = true;        r6 = r5;     */
    /* JADX WARN: Code restructure failed: missing block: B:48:0x01b2, code lost:            r5 = r13.getName();     */
    /* JADX WARN: Code restructure failed: missing block: B:49:0x01b6, code lost:            if (r1 == false) goto L54;     */
    /* JADX WARN: Code restructure failed: missing block: B:51:0x01bc, code lost:            if (r5.equals(r6) == false) goto L54;     */
    /* JADX WARN: Code restructure failed: missing block: B:52:0x01be, code lost:            r1 = false;        r6 = null;     */
    /* JADX WARN: Code restructure failed: missing block: B:55:0x01c9, code lost:            if (r5.equals("group") == false) goto L57;     */
    /* JADX WARN: Code restructure failed: missing block: B:56:0x01cb, code lost:            r2.resetGroup();     */
    /* JADX WARN: Code restructure failed: missing block: B:59:0x01d7, code lost:            if (r5.equals("item") == false) goto L67;     */
    /* JADX WARN: Code restructure failed: missing block: B:61:0x01db, code lost:            if (r2.itemAdded != false) goto L86;     */
    /* JADX WARN: Code restructure failed: missing block: B:63:0x01df, code lost:            if (r2.itemActionProvider == null) goto L66;     */
    /* JADX WARN: Code restructure failed: missing block: B:65:0x01e7, code lost:            if (r2.itemActionProvider.hasSubMenu() == false) goto L66;     */
    /* JADX WARN: Code restructure failed: missing block: B:66:0x01e9, code lost:            r2.addSubMenuItem();     */
    /* JADX WARN: Code restructure failed: missing block: B:68:0x01ee, code lost:            r2.itemAdded = true;        r2.setItem(r2.menu.add(r2.groupId, r2.itemId, r2.itemCategoryOrder, r2.itemTitle));     */
    /* JADX WARN: Code restructure failed: missing block: B:72:0x020b, code lost:            if (r5.equals("menu") == false) goto L89;     */
    /* JADX WARN: Code restructure failed: missing block: B:73:0x020d, code lost:            r3 = true;     */
    /* JADX WARN: Code restructure failed: missing block: B:78:0x0218, code lost:            throw new java.lang.RuntimeException("Unexpected end of document");     */
    /* JADX WARN: Code restructure failed: missing block: B:7:0x001f, code lost:            r3 = false;     */
    /* JADX WARN: Code restructure failed: missing block: B:82:0x0219, code lost:            return;     */
    /* JADX WARN: Code restructure failed: missing block: B:8:0x0020, code lost:            if (r3 != false) goto L76;     */
    /* JADX WARN: Code restructure failed: missing block: B:9:0x0022, code lost:            switch(r0) {            case 1: goto L77;            case 2: goto L17;            case 3: goto L49;            default: goto L78;        };     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private void parseMenu(org.xmlpull.v1.XmlPullParser r13, android.util.AttributeSet r14, android.view.Menu r15) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
        /*
            Method dump skipped, instructions count: 548
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v7.view.SupportMenuInflater.parseMenu(org.xmlpull.v1.XmlPullParser, android.util.AttributeSet, android.view.Menu):void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class InflatedOnMenuItemClickListener implements MenuItem.OnMenuItemClickListener {
        private static final Class<?>[] PARAM_TYPES = {MenuItem.class};
        private Method mMethod;
        private Object mRealOwner;

        public InflatedOnMenuItemClickListener(Object realOwner, String methodName) {
            this.mRealOwner = realOwner;
            Class<?> c = realOwner.getClass();
            try {
                this.mMethod = c.getMethod(methodName, PARAM_TYPES);
            } catch (Exception e) {
                InflateException ex = new InflateException("Couldn't resolve menu item onClick handler " + methodName + " in class " + c.getName());
                ex.initCause(e);
                throw ex;
            }
        }

        @Override // android.view.MenuItem.OnMenuItemClickListener
        public final boolean onMenuItemClick(MenuItem item) {
            try {
                if (this.mMethod.getReturnType() == Boolean.TYPE) {
                    return ((Boolean) this.mMethod.invoke(this.mRealOwner, item)).booleanValue();
                }
                this.mMethod.invoke(this.mRealOwner, item);
                return true;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class MenuState {
        int groupCategory;
        int groupCheckable;
        boolean groupEnabled;
        int groupId;
        int groupOrder;
        boolean groupVisible;
        ActionProvider itemActionProvider;
        String itemActionProviderClassName;
        String itemActionViewClassName;
        int itemActionViewLayout;
        boolean itemAdded;
        char itemAlphabeticShortcut;
        int itemCategoryOrder;
        int itemCheckable;
        boolean itemChecked;
        boolean itemEnabled;
        int itemIconResId;
        int itemId;
        String itemListenerMethodName;
        char itemNumericShortcut;
        int itemShowAsAction;
        CharSequence itemTitle;
        CharSequence itemTitleCondensed;
        boolean itemVisible;
        Menu menu;

        public MenuState(Menu menu) {
            this.menu = menu;
            resetGroup();
        }

        public final void resetGroup() {
            this.groupId = 0;
            this.groupCategory = 0;
            this.groupOrder = 0;
            this.groupCheckable = 0;
            this.groupVisible = true;
            this.groupEnabled = true;
        }

        static char getShortcut(String shortcutString) {
            if (shortcutString == null) {
                return (char) 0;
            }
            return shortcutString.charAt(0);
        }

        final void setItem(MenuItem item) {
            item.setChecked(this.itemChecked).setVisible(this.itemVisible).setEnabled(this.itemEnabled).setCheckable(this.itemCheckable > 0).setTitleCondensed(this.itemTitleCondensed).setIcon(this.itemIconResId).setAlphabeticShortcut(this.itemAlphabeticShortcut).setNumericShortcut(this.itemNumericShortcut);
            if (this.itemShowAsAction >= 0) {
                MenuItemCompat.setShowAsAction(item, this.itemShowAsAction);
            }
            if (this.itemListenerMethodName != null) {
                if (SupportMenuInflater.this.mContext.isRestricted()) {
                    throw new IllegalStateException("The android:onClick attribute cannot be used within a restricted context");
                }
                item.setOnMenuItemClickListener(new InflatedOnMenuItemClickListener(SupportMenuInflater.access$400(SupportMenuInflater.this), this.itemListenerMethodName));
            }
            if (this.itemCheckable >= 2) {
                if (item instanceof MenuItemImpl) {
                    ((MenuItemImpl) item).setExclusiveCheckable(true);
                } else if (item instanceof MenuItemWrapperICS) {
                    MenuItemWrapperICS menuItemWrapperICS = (MenuItemWrapperICS) item;
                    try {
                        if (menuItemWrapperICS.mSetExclusiveCheckableMethod == null) {
                            menuItemWrapperICS.mSetExclusiveCheckableMethod = ((SupportMenuItem) menuItemWrapperICS.mWrappedObject).getClass().getDeclaredMethod("setExclusiveCheckable", Boolean.TYPE);
                        }
                        menuItemWrapperICS.mSetExclusiveCheckableMethod.invoke(menuItemWrapperICS.mWrappedObject, true);
                    } catch (Exception e) {
                        Log.w("MenuItemWrapper", "Error while calling setExclusiveCheckable", e);
                    }
                }
            }
            boolean actionViewSpecified = false;
            if (this.itemActionViewClassName != null) {
                View actionView = (View) newInstance(this.itemActionViewClassName, SupportMenuInflater.ACTION_VIEW_CONSTRUCTOR_SIGNATURE, SupportMenuInflater.this.mActionViewConstructorArguments);
                MenuItemCompat.setActionView(item, actionView);
                actionViewSpecified = true;
            }
            if (this.itemActionViewLayout > 0) {
                if (!actionViewSpecified) {
                    MenuItemCompat.setActionView(item, this.itemActionViewLayout);
                } else {
                    Log.w("SupportMenuInflater", "Ignoring attribute 'itemActionViewLayout'. Action view already specified.");
                }
            }
            if (this.itemActionProvider != null) {
                MenuItemCompat.setActionProvider(item, this.itemActionProvider);
            }
        }

        public final SubMenu addSubMenuItem() {
            this.itemAdded = true;
            SubMenu subMenu = this.menu.addSubMenu(this.groupId, this.itemId, this.itemCategoryOrder, this.itemTitle);
            setItem(subMenu.getItem());
            return subMenu;
        }

        final <T> T newInstance(String str, Class<?>[] clsArr, Object[] objArr) {
            try {
                Constructor<?> constructor = SupportMenuInflater.this.mContext.getClassLoader().loadClass(str).getConstructor(clsArr);
                constructor.setAccessible(true);
                return (T) constructor.newInstance(objArr);
            } catch (Exception e) {
                Log.w("SupportMenuInflater", "Cannot instantiate class: " + str, e);
                return null;
            }
        }
    }

    static /* synthetic */ Object access$400(SupportMenuInflater x0) {
        if (x0.mRealOwner == null) {
            Context context = x0.mContext;
            while (!(context instanceof Activity) && (context instanceof ContextWrapper)) {
                context = ((ContextWrapper) context).getBaseContext();
            }
            x0.mRealOwner = context;
        }
        return x0.mRealOwner;
    }
}
