package com.nuance.swype.startup;

import android.graphics.Point;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import com.nuance.swype.input.QuickToast;
import com.nuance.swype.input.R;

/* loaded from: classes.dex */
public class SelectSwypeDelegate extends StartupDelegate {
    private boolean showPopupTip;

    static /* synthetic */ boolean access$002$11ff5da5(SelectSwypeDelegate x0) {
        x0.showPopupTip = true;
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static SelectSwypeDelegate newInstance(Bundle savedInstanceState) {
        SelectSwypeDelegate f = new SelectSwypeDelegate();
        f.setArguments(savedInstanceState);
        return f;
    }

    @Override // com.nuance.swype.startup.StartupDelegate, android.app.Fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        String version = getActivity().getString(R.string.ime_name);
        String msg = String.format(getActivity().getString(R.string.startup_select_swype), version);
        loadTemplateToContentView(inflater, R.layout.startup_template_three_quarters, R.layout.startup_select_swype, msg);
        ImageView selectSwypeButton = (ImageView) this.view.findViewById(R.id.select_swype_button);
        selectSwypeButton.setClickable(true);
        selectSwypeButton.setOnClickListener(new View.OnClickListener() { // from class: com.nuance.swype.startup.SelectSwypeDelegate.1
            @Override // android.view.View.OnClickListener
            public final void onClick(View arg0) {
                SelectSwypeDelegate.access$002$11ff5da5(SelectSwypeDelegate.this);
                if (!SelectSwypeDelegate.this.mStartupSequenceInfo.isSwypeSelected()) {
                    ((InputMethodManager) SelectSwypeDelegate.this.getActivity().getSystemService("input_method")).showInputMethodPicker();
                } else {
                    SelectSwypeDelegate.this.mActivityCallbacks.startNextScreen(SelectSwypeDelegate.this.mFlags, SelectSwypeDelegate.this.mResultData);
                }
            }
        });
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override // com.nuance.swype.startup.StartupDelegate
    public final void onWindowFocusChanged(boolean hasFocus) {
        if (this.mStartupSequenceInfo.isSwypeSelected()) {
            this.mActivityCallbacks.startNextScreen(this.mFlags, this.mResultData);
            return;
        }
        if (this.showPopupTip && hasFocus) {
            String version = getActivity().getString(R.string.ime_name);
            String msg = String.format(getActivity().getString(R.string.startup_select_swype_toast), version);
            Point displaySize = new Point();
            getActivity().getWindowManager().getDefaultDisplay().getSize(displaySize);
            QuickToast.show(getActivity(), msg, 1, displaySize.y / 2);
            this.showPopupTip = false;
        }
    }

    @Override // com.nuance.swype.startup.StartupDelegate
    public final void onBackPressed() {
        if (!this.mStartupSequenceInfo.isSwypeSelected()) {
            showSelectSwypeDialog();
        }
        this.showPopupTip = false;
    }
}
