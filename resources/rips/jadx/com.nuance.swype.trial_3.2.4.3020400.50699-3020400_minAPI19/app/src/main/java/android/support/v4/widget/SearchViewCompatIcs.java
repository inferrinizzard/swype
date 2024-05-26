package android.support.v4.widget;

import android.content.Context;
import android.widget.SearchView;

/* loaded from: classes.dex */
final class SearchViewCompatIcs {

    /* loaded from: classes.dex */
    public static class MySearchView extends SearchView {
        public MySearchView(Context context) {
            super(context);
        }

        @Override // android.widget.SearchView, android.view.CollapsibleActionView
        public void onActionViewCollapsed() {
            setQuery("", false);
            super.onActionViewCollapsed();
        }
    }
}
