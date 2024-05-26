package com.nuance.swype.widget;

import android.annotation.SuppressLint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.nuance.android.compat.ViewCompat;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.input.R;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
public final class PopupLanguageList extends PopupWindow implements View.OnTouchListener {
    public LayoutInflater inflater;
    public final List<View> languageViews;
    public PopupLanguageListener listener;
    public final TextView moreLanguages;
    private TouchDelegate originalDelegate;
    private final int[] outLocation;
    private View parentView;
    private final String themeId;

    /* loaded from: classes.dex */
    public interface PopupLanguageListener {
        void onHardLangSelected(String str);

        void onLanguageSelected(String str);

        void onMoreLanguages();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class TouchRedirect extends TouchDelegate {
        private boolean isRedirecting;
        private int offset;
        private PopupLanguageList popup;

        public TouchRedirect(PopupLanguageList popup) {
            super(new Rect(), popup.getContentView());
            this.popup = popup;
        }

        @Override // android.view.TouchDelegate
        public final boolean onTouchEvent(MotionEvent event) {
            if (!this.isRedirecting && this.popup.isShowing()) {
                int[] location = new int[2];
                this.popup.getContentView().getLocationOnScreen(location);
                int popupY = location[1];
                if (popupY != 0) {
                    this.isRedirecting = true;
                    this.popup.parentView.getLocationOnScreen(location);
                    this.offset = location[1] - popupY;
                }
            }
            MotionEvent redirectEvent = MotionEvent.obtainNoHistory(event);
            redirectEvent.setLocation(event.getX(), event.getY() + this.offset);
            return this.popup.onTouch(null, redirectEvent);
        }
    }

    public PopupLanguageList(LayoutInflater inflater, String themeId) {
        super(inflater.getContext());
        this.languageViews = new ArrayList();
        this.outLocation = new int[2];
        this.inflater = inflater;
        this.themeId = themeId;
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.popup_language_list, (ViewGroup) null);
        IMEApplication.from(inflater.getContext()).getThemeLoader().applyTheme(view);
        setContentView(view);
        view.setOnTouchListener(this);
        this.moreLanguages = (TextView) view.findViewById(R.id.more_languages);
        setPopupColorText(this.moreLanguages);
        setPopupBackground(this.moreLanguages);
        setWindowLayoutMode(-2, -2);
        setWidth(-2);
        setHeight(-2);
        setBackgroundDrawable(null);
        int padding_in_px = view.getResources().getDimensionPixelSize(R.dimen.popup_language_list_padding);
        this.moreLanguages.setPadding(padding_in_px, padding_in_px, padding_in_px, padding_in_px);
    }

    @Override // android.widget.PopupWindow
    public final void showAtLocation(View parent, int gravity, int x, int y) {
        super.showAtLocation(parent, gravity, x, y);
        if (this.parentView != null) {
            detach();
        }
        this.parentView = parent;
        this.originalDelegate = this.parentView.getTouchDelegate();
        this.parentView.setTouchDelegate(new TouchRedirect(this));
    }

    @Override // android.widget.PopupWindow
    public final void dismiss() {
        super.dismiss();
        detach();
    }

    public static void setPopupBackground(View view) {
        Drawable drawable = view.getContext().getResources().getDrawable(R.drawable.btn_keyboard_popup_key);
        ViewCompat.setBackground(view, drawable);
    }

    private void detach() {
        if (this.parentView != null) {
            this.parentView.setTouchDelegate(this.originalDelegate);
            this.parentView = null;
            this.originalDelegate = null;
        }
    }

    public static void setPopupColorText(TextView view) {
        view.setTextColor(view.getContext().getResources().getColorStateList(R.color.color_popup_text_menu));
    }

    @Override // android.view.View.OnTouchListener
    @SuppressLint({"ClickableViewAccessibility"})
    public final boolean onTouch(View view, MotionEvent event) {
        if (event.getActionMasked() == 1) {
            if (this.listener != null) {
                if (this.moreLanguages.isPressed()) {
                    this.listener.onMoreLanguages();
                } else {
                    Iterator<View> it = this.languageViews.iterator();
                    while (true) {
                        if (!it.hasNext()) {
                            break;
                        }
                        View langView = it.next();
                        if (langView.isPressed()) {
                            this.listener.onLanguageSelected(langView.getTag().toString());
                            break;
                        }
                    }
                }
            }
            dismiss();
        } else {
            View closestView = this.moreLanguages;
            int minDistance = distanceToView(event, closestView);
            closestView.setPressed(false);
            for (View langView2 : this.languageViews) {
                int distance = distanceToView(event, langView2);
                if (distance < minDistance) {
                    closestView = langView2;
                    minDistance = distance;
                }
                langView2.setPressed(false);
            }
            closestView.setPressed(true);
        }
        return true;
    }

    private int distanceToView(MotionEvent event, View view) {
        view.getLocationInWindow(this.outLocation);
        int distance = ((int) event.getY()) - (this.outLocation[1] + (view.getHeight() / 2));
        if (distance < 0) {
            return -distance;
        }
        return distance;
    }
}
