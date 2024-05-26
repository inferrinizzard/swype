package com.localytics.android;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.localytics.android.Localytics;
import com.nuance.nmsp.client.sdk.components.core.internal.calllog.SessionEventImpl;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

@TargetApi(11)
/* loaded from: classes.dex */
public final class TestModeListView extends DialogFragment {
    static final String TEST_MODE_LIST_TAG = "marketing_test_mode_list";
    private ListAdapter mAdapter;
    private Map<Integer, MarketingCallable> mCallbacks;
    private final AtomicBoolean mEnterAnimatable = new AtomicBoolean(true);

    private TestModeListView() {
    }

    public static TestModeListView newInstance() {
        TestModeListView fragment = new TestModeListView();
        fragment.setRetainInstance(true);
        return fragment;
    }

    @Override // android.app.Fragment
    public final View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Localytics.Log.d("[TestModeListView]: onCreateView");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override // android.app.Fragment
    public final void onViewStateRestored(Bundle savedInstanceState) {
        Localytics.Log.d("[TestModeListView]: onViewStateRestored");
        super.onViewStateRestored(savedInstanceState);
    }

    @Override // android.app.Fragment
    public final void onResume() {
        Localytics.Log.d("[TestModeListView]: onResume");
        super.onResume();
    }

    @Override // android.app.Fragment
    public final void onPause() {
        Localytics.Log.d("[TestModeListView]: onPause");
        super.onPause();
    }

    @Override // android.app.Fragment
    public final void onDestroy() {
        Localytics.Log.d("[TestModeListView]: onDestroy");
        super.onDestroy();
    }

    @Override // android.app.DialogFragment
    public final void show(FragmentManager manager, String tag) {
        this.mEnterAnimatable.set(true);
        super.show(manager, tag);
    }

    @Override // android.app.Fragment
    public final void onAttach(Activity activity) {
        Localytics.Log.d("[TestModeListView]: onAttach");
        super.onAttach(activity);
    }

    @Override // android.app.DialogFragment, android.app.Fragment
    public final void onDetach() {
        Localytics.Log.d("[TestModeListView]: onDetach");
        super.onDetach();
    }

    @Override // android.app.DialogFragment, android.app.Fragment
    public final void onCreate(Bundle savedInstanceState) {
        Localytics.Log.d("[TestModeListView]: onCreate");
        super.onCreate(savedInstanceState);
    }

    @Override // android.app.DialogFragment
    public final Dialog onCreateDialog(Bundle savedInstanceState) {
        Localytics.Log.d("[TestModeListView]: onCreateDialog");
        return new TestModeDialog(getActivity(), android.R.style.Theme.Dialog);
    }

    @Override // android.app.DialogFragment, android.content.DialogInterface.OnDismissListener
    public final void onDismiss(DialogInterface dialog) {
        Localytics.Log.d("[TestModeListView]: onDismiss");
        super.onDismiss(dialog);
    }

    @Override // android.app.DialogFragment, android.app.Fragment
    public final void onActivityCreated(Bundle arg0) {
        Localytics.Log.d("[TestModeListView]: onActivityCreated");
        super.onActivityCreated(arg0);
    }

    @Override // android.app.DialogFragment, android.app.Fragment
    public final void onStart() {
        Localytics.Log.d("[TestModeListView]: onStart");
        super.onStart();
    }

    @Override // android.app.DialogFragment, android.app.Fragment
    public final void onSaveInstanceState(Bundle arg0) {
        Localytics.Log.d("[TestModeListView]: onSaveInstanceState");
        super.onSaveInstanceState(arg0);
    }

    @Override // android.app.DialogFragment, android.app.Fragment
    public final void onStop() {
        Localytics.Log.d("[TestModeListView]: onStop");
        super.onStop();
    }

    @Override // android.app.DialogFragment, android.app.Fragment
    public final void onDestroyView() {
        Localytics.Log.d("[TestModeListView]: onDestroyView");
        super.onDestroyView();
    }

    public final void setAdapter(ListAdapter adapter) {
        this.mAdapter = adapter;
    }

    public final TestModeListView setCallbacks(Map<Integer, MarketingCallable> callbacks) {
        this.mCallbacks = callbacks;
        return this;
    }

    /* loaded from: classes.dex */
    final class TestModeDialog extends Dialog {
        private TranslateAnimation mAnimIn;
        private TranslateAnimation mAnimOut;
        private RelativeLayout mDialogLayout;
        private DisplayMetrics mMetrics;

        public TestModeDialog(Context context, int theme) {
            super(context, theme);
            setupViews();
            createAnimations();
            adjustLayout();
        }

        private void setupViews() {
            float dip = TestModeListView.this.getResources().getDisplayMetrics().density;
            this.mDialogLayout = new RelativeLayout(getContext());
            this.mDialogLayout.setLayoutParams(new RelativeLayout.LayoutParams(-1, -1));
            this.mDialogLayout.setBackgroundColor(-1);
            RelativeLayout relativeLayout = new RelativeLayout(getContext());
            RelativeLayout.LayoutParams topParams = new RelativeLayout.LayoutParams(-1, -2);
            topParams.addRule(10);
            relativeLayout.setId(1);
            relativeLayout.setLayoutParams(topParams);
            relativeLayout.setBackgroundColor(Color.argb(255, 73, 73, 73));
            relativeLayout.setPadding((int) ((8.0f * dip) + 0.5f), (int) ((12.0f * dip) + 0.5f), (int) ((8.0f * dip) + 0.5f), (int) ((12.0f * dip) + 0.5f));
            this.mDialogLayout.addView(relativeLayout);
            TextView close = new TextView(getContext());
            close.setText("Close");
            close.setTextSize(22.0f);
            close.setTextColor(new ColorStateList(new int[][]{new int[]{android.R.attr.state_pressed}, new int[]{android.R.attr.state_focused}, new int[0]}, new int[]{Color.argb(255, 255, 0, 0), Color.argb(255, 0, 91, 255), Color.argb(255, 0, 91, 255)}));
            close.setOnClickListener(new View.OnClickListener() { // from class: com.localytics.android.TestModeListView.TestModeDialog.1
                @Override // android.view.View.OnClickListener
                public void onClick(View v) {
                    MarketingCallable callable;
                    TestModeDialog.this.mDialogLayout.startAnimation(TestModeDialog.this.mAnimOut);
                    if (TestModeListView.this.mCallbacks != null && (callable = (MarketingCallable) TestModeListView.this.mCallbacks.get(9)) != null) {
                        callable.call(null);
                    }
                }
            });
            RelativeLayout.LayoutParams paramsClose = new RelativeLayout.LayoutParams(-2, -2);
            paramsClose.addRule(9);
            close.setLayoutParams(paramsClose);
            relativeLayout.addView(close);
            TextView title = new TextView(getContext());
            title.setText("Localytics");
            title.setTextSize(22.0f);
            title.setTextColor(-1);
            RelativeLayout.LayoutParams paramsTitle = new RelativeLayout.LayoutParams(-2, -2);
            paramsTitle.addRule(13);
            title.setLayoutParams(paramsTitle);
            relativeLayout.addView(title);
            TextView menu = new TextView(getContext());
            menu.setText("Menu");
            menu.setTextSize(22.0f);
            menu.setTextColor(new ColorStateList(new int[][]{new int[]{android.R.attr.state_pressed}, new int[]{android.R.attr.state_focused}, new int[0]}, new int[]{Color.argb(255, 255, 0, 0), Color.argb(255, 0, 91, 255), Color.argb(255, 0, 91, 255)}));
            menu.setOnClickListener(new View.OnClickListener() { // from class: com.localytics.android.TestModeListView.TestModeDialog.2
                @Override // android.view.View.OnClickListener
                public void onClick(View v) {
                    if (TestModeListView.this.mCallbacks != null) {
                        new MenuDialog(TestModeListView.this.getActivity(), android.R.style.Theme.Dialog).show();
                    }
                }
            });
            RelativeLayout.LayoutParams paramsMenu = new RelativeLayout.LayoutParams(-2, -2);
            paramsMenu.addRule(11);
            menu.setLayoutParams(paramsMenu);
            relativeLayout.addView(menu);
            RelativeLayout bottomLayout = new RelativeLayout(getContext());
            RelativeLayout.LayoutParams bottomParams = new RelativeLayout.LayoutParams(-1, -1);
            bottomParams.addRule(3, 1);
            bottomLayout.setLayoutParams(bottomParams);
            this.mDialogLayout.addView(bottomLayout);
            ListView listView = new ListView(getContext());
            listView.setAdapter(TestModeListView.this.mAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: com.localytics.android.TestModeListView.TestModeDialog.3
                @Override // android.widget.AdapterView.OnItemClickListener
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    MarketingCallable callable;
                    if (TestModeListView.this.mCallbacks != null && (callable = (MarketingCallable) TestModeListView.this.mCallbacks.get(10)) != null) {
                        callable.call(new Object[]{parent.getItemAtPosition(position)});
                    }
                }
            });
            bottomLayout.addView(listView);
            requestWindowFeature(1);
            setContentView(this.mDialogLayout);
        }

        private void createAnimations() {
            this.mAnimIn = new TranslateAnimation(2, 0.0f, 2, 0.0f, 2, 1.0f, 2, 0.0f);
            this.mAnimIn.setDuration(250L);
            this.mAnimOut = new TranslateAnimation(2, 0.0f, 2, 0.0f, 2, 0.0f, 2, 1.0f);
            this.mAnimOut.setDuration(250L);
            this.mAnimOut.setAnimationListener(new Animation.AnimationListener() { // from class: com.localytics.android.TestModeListView.TestModeDialog.4
                @Override // android.view.animation.Animation.AnimationListener
                public void onAnimationStart(Animation animation) {
                }

                @Override // android.view.animation.Animation.AnimationListener
                public void onAnimationEnd(Animation animation) {
                    TestModeListView.this.dismiss();
                }

                @Override // android.view.animation.Animation.AnimationListener
                public void onAnimationRepeat(Animation animation) {
                }
            });
        }

        @SuppressLint({"NewApi"})
        private void adjustLayout() {
            this.mMetrics = new DisplayMetrics();
            ((WindowManager) TestModeListView.this.getActivity().getSystemService("window")).getDefaultDisplay().getMetrics(this.mMetrics);
            Window window = getWindow();
            window.setBackgroundDrawable(new ColorDrawable(0));
            window.setGravity(17);
            WindowManager.LayoutParams params = window.getAttributes();
            params.dimAmount = 0.0f;
            params.width = this.mMetrics.widthPixels;
            window.setAttributes(params);
            if (TestModeListView.this.mEnterAnimatable.getAndSet(false)) {
                this.mDialogLayout.startAnimation(this.mAnimIn);
            }
            window.setFlags(1024, 1024);
        }

        @Override // android.app.Dialog, android.view.KeyEvent.Callback
        public final boolean onKeyDown(int keyCode, KeyEvent event) {
            MarketingCallable callable;
            if (keyCode != 4) {
                return super.onKeyDown(keyCode, event);
            }
            this.mDialogLayout.startAnimation(this.mAnimOut);
            if (TestModeListView.this.mCallbacks != null && (callable = (MarketingCallable) TestModeListView.this.mCallbacks.get(9)) != null) {
                callable.call(null);
            }
            return super.onKeyDown(keyCode, event);
        }
    }

    /* loaded from: classes.dex */
    class MenuDialog extends Dialog {
        private LinearLayout mDialogLayout;
        private DisplayMetrics mMetrics;

        public MenuDialog(Context context, int theme) {
            super(context, theme);
            setupViews();
            adjustLayout();
        }

        @TargetApi(16)
        private void setupViews() {
            this.mDialogLayout = new LinearLayout(getContext());
            this.mDialogLayout.setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
            this.mDialogLayout.setGravity(16);
            this.mDialogLayout.setOrientation(1);
            int transparentColor = getContext().getResources().getColor(android.R.color.transparent);
            MenuItemAdapter menuAdapter = new MenuItemAdapter(TestModeListView.this.getActivity());
            ListView menus = new ListView(TestModeListView.this.getActivity());
            menus.setAdapter((ListAdapter) menuAdapter);
            if (DatapointHelper.getApiLevel() >= 16) {
                menus.setBackground(new ColorDrawable(transparentColor));
            } else {
                menus.setBackgroundColor(transparentColor);
            }
            menus.setDividerHeight(1);
            menus.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: com.localytics.android.TestModeListView.MenuDialog.1
                @Override // android.widget.AdapterView.OnItemClickListener
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    MarketingCallable callable;
                    MarketingCallable callable2;
                    MarketingCallable callable3;
                    if (position == 0) {
                        Constants.setTestModeEnabled(false);
                        TestModeListView.this.dismiss();
                    } else if (position == 1) {
                        if (TestModeListView.this.mCallbacks != null && (callable3 = (MarketingCallable) TestModeListView.this.mCallbacks.get(11)) != null) {
                            callable3.call(null);
                        }
                    } else if (position == 2) {
                        if (TestModeListView.this.mCallbacks != null && (callable2 = (MarketingCallable) TestModeListView.this.mCallbacks.get(12)) != null) {
                            callable2.call(null);
                        }
                    } else if (position == 3 && TestModeListView.this.mCallbacks != null && (callable = (MarketingCallable) TestModeListView.this.mCallbacks.get(13)) != null) {
                        callable.call(null);
                    }
                    MenuDialog.this.dismiss();
                }
            });
            this.mDialogLayout.addView(menus);
            View space = new View(TestModeListView.this.getActivity());
            if (DatapointHelper.getApiLevel() >= 16) {
                space.setBackground(new ColorDrawable(transparentColor));
            } else {
                space.setBackgroundColor(transparentColor);
            }
            space.setLayoutParams(new LinearLayout.LayoutParams(-1, (int) ((TestModeListView.this.getResources().getDisplayMetrics().density * 10.0f) + 0.5f)));
            this.mDialogLayout.addView(space);
            CancelItemAdapter cancelAdapter = new CancelItemAdapter(TestModeListView.this.getActivity());
            ListView cancel = new ListView(TestModeListView.this.getActivity());
            cancel.setAdapter((ListAdapter) cancelAdapter);
            if (DatapointHelper.getApiLevel() >= 16) {
                cancel.setBackground(new ColorDrawable(transparentColor));
            } else {
                space.setBackgroundColor(transparentColor);
            }
            cancel.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: com.localytics.android.TestModeListView.MenuDialog.2
                @Override // android.widget.AdapterView.OnItemClickListener
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    MenuDialog.this.dismiss();
                }
            });
            this.mDialogLayout.addView(cancel);
            requestWindowFeature(1);
            setContentView(this.mDialogLayout);
        }

        private void adjustLayout() {
            this.mMetrics = new DisplayMetrics();
            ((WindowManager) TestModeListView.this.getActivity().getSystemService("window")).getDefaultDisplay().getMetrics(this.mMetrics);
            Window window = getWindow();
            window.setBackgroundDrawable(new ColorDrawable(0));
            WindowManager.LayoutParams params = window.getAttributes();
            window.setAttributes(params);
            window.setFlags(1024, 1024);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public final class MenuItemAdapter extends BaseAdapter {
        private final String[] MENU_ITEMS = {"Disable Test Mode", "Refresh", "Copy Push Token", "Copy Install ID"};
        private final Context mContext;

        public MenuItemAdapter(Context context) {
            this.mContext = context;
        }

        @Override // android.widget.Adapter
        public final int getCount() {
            return this.MENU_ITEMS.length;
        }

        @Override // android.widget.Adapter
        public final String getItem(int position) {
            return this.MENU_ITEMS[position];
        }

        @Override // android.widget.Adapter
        public final long getItemId(int position) {
            return position;
        }

        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        /* JADX WARN: Code restructure failed: missing block: B:3:0x0027, code lost:            return r0;     */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        private android.graphics.drawable.Drawable getShape(int r10) {
            /*
                r9 = this;
                r8 = 2
                r7 = 1
                r6 = 3
                r5 = 0
                r4 = 0
                com.localytics.android.TestModeListView r2 = com.localytics.android.TestModeListView.this
                android.content.res.Resources r2 = r2.getResources()
                android.util.DisplayMetrics r2 = r2.getDisplayMetrics()
                float r2 = r2.density
                r3 = 1090519040(0x41000000, float:8.0)
                float r1 = r2 * r3
                android.graphics.drawable.GradientDrawable r0 = new android.graphics.drawable.GradientDrawable
                android.graphics.drawable.GradientDrawable$Orientation r2 = android.graphics.drawable.GradientDrawable.Orientation.TL_BR
                int[] r3 = new int[r6]
                r3 = {x0060: FILL_ARRAY_DATA , data: [-1, -1, -1} // fill-array
                r0.<init>(r2, r3)
                r0.setGradientType(r5)
                switch(r10) {
                    case 0: goto L28;
                    case 1: goto L27;
                    case 2: goto L27;
                    case 3: goto L44;
                    default: goto L27;
                }
            L27:
                return r0
            L28:
                r2 = 8
                float[] r2 = new float[r2]
                r2[r5] = r1
                r2[r7] = r1
                r2[r8] = r1
                r2[r6] = r1
                r3 = 4
                r2[r3] = r4
                r3 = 5
                r2[r3] = r4
                r3 = 6
                r2[r3] = r4
                r3 = 7
                r2[r3] = r4
                r0.setCornerRadii(r2)
                goto L27
            L44:
                r2 = 8
                float[] r2 = new float[r2]
                r2[r5] = r4
                r2[r7] = r4
                r2[r8] = r4
                r2[r6] = r4
                r3 = 4
                r2[r3] = r1
                r3 = 5
                r2[r3] = r1
                r3 = 6
                r2[r3] = r1
                r3 = 7
                r2[r3] = r1
                r0.setCornerRadii(r2)
                goto L27
            */
            throw new UnsupportedOperationException("Method not decompiled: com.localytics.android.TestModeListView.MenuItemAdapter.getShape(int):android.graphics.drawable.Drawable");
        }

        @Override // android.widget.Adapter
        @TargetApi(16)
        public final View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (convertView == null) {
                LinearLayout layout = new LinearLayout(this.mContext);
                layout.setLayoutParams(new AbsListView.LayoutParams(-1, -2));
                layout.setOrientation(1);
                int padding = (int) ((this.mContext.getResources().getDisplayMetrics().density * 6.0f) + 0.5f);
                layout.setPadding(0, padding, 0, padding);
                TextView item = new TextView(this.mContext);
                item.setId(1);
                item.setTextSize(26.0f);
                item.setTextColor(Color.argb(255, 0, 91, 255));
                item.setGravity(17);
                layout.addView(item);
                view = layout;
                if (DatapointHelper.getApiLevel() >= 16) {
                    view.setBackground(getShape(position));
                } else {
                    view.setBackgroundColor(-1);
                }
            }
            TextView item2 = (TextView) view.findViewById(1);
            item2.setText(this.MENU_ITEMS[position]);
            switch (position) {
                case 0:
                    item2.setTextColor(-65536);
                default:
                    return view;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public final class CancelItemAdapter extends BaseAdapter {
        private final String[] MENU_ITEMS = {SessionEventImpl.NMSP_CALLLOG_META_CANCEL};
        private final Context mContext;

        CancelItemAdapter(Context context) {
            this.mContext = context;
        }

        @Override // android.widget.Adapter
        public final int getCount() {
            return this.MENU_ITEMS.length;
        }

        @Override // android.widget.Adapter
        public final String getItem(int position) {
            return this.MENU_ITEMS[position];
        }

        @Override // android.widget.Adapter
        public final long getItemId(int position) {
            return position;
        }

        @Override // android.widget.Adapter
        @TargetApi(16)
        public final View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (convertView == null) {
                LinearLayout layout = new LinearLayout(this.mContext);
                layout.setLayoutParams(new AbsListView.LayoutParams(-1, -2));
                layout.setOrientation(1);
                int padding = (int) ((this.mContext.getResources().getDisplayMetrics().density * 6.0f) + 0.5f);
                layout.setPadding(0, padding, 0, padding);
                TextView item = new TextView(this.mContext);
                item.setId(1);
                item.setTextSize(26.0f);
                item.setTextColor(Color.argb(255, 0, 91, 255));
                item.setGravity(17);
                layout.addView(item);
                view = layout;
                float radius = TestModeListView.this.getResources().getDisplayMetrics().density * 8.0f;
                GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.TL_BR, new int[]{-1, -1, -1});
                gradientDrawable.setGradientType(0);
                gradientDrawable.setCornerRadii(new float[]{radius, radius, radius, radius, radius, radius, radius, radius});
                if (DatapointHelper.getApiLevel() >= 16) {
                    view.setBackground(gradientDrawable);
                } else {
                    view.setBackgroundColor(-1);
                }
            }
            TextView item2 = (TextView) view.findViewById(1);
            item2.setText(this.MENU_ITEMS[position]);
            item2.setTypeface(null, 1);
            return view;
        }
    }
}
