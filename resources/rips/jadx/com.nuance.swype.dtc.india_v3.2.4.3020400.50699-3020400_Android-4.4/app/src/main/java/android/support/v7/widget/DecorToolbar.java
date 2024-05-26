package android.support.v7.widget;

import android.graphics.drawable.Drawable;
import android.support.v7.view.menu.MenuPresenter;
import android.view.Menu;
import android.view.Window;

/* loaded from: classes.dex */
public interface DecorToolbar {
    CharSequence getTitle();

    void setIcon(int i);

    void setIcon(Drawable drawable);

    void setLogo(int i);

    void setMenu(Menu menu, MenuPresenter.Callback callback);

    void setMenuPrepared();

    void setWindowCallback(Window.Callback callback);

    void setWindowTitle(CharSequence charSequence);
}
