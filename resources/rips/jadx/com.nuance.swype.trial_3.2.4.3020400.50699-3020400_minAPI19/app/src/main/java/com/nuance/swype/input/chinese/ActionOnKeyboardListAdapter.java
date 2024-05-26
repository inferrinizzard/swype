package com.nuance.swype.input.chinese;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;
import com.nuance.input.swypecorelib.SwypeCoreLibrary;
import com.nuance.swype.input.AppPreferences;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.input.InputMethods;
import com.nuance.swype.input.R;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class ActionOnKeyboardListAdapter extends BaseAdapter {
    private final Context context;
    public List<ActionItem> mActionOnKeyboard = new ArrayList();

    @SuppressLint({"PrivateResource"})
    public ActionOnKeyboardListAdapter(Context context, InputMethods.InputMode currentInputMode) {
        this.context = context;
        AppPreferences appPrefs = AppPreferences.from(context);
        boolean hwrOn = false;
        SwypeCoreLibrary corelib = IMEApplication.from(context).getSwypeCoreLibMgr().getSwypeCoreLibInstance();
        if (corelib.isWriteChineseBuildEnabled()) {
            if (corelib.isTraceBuildEnabled()) {
                hwrOn = appPrefs.getBoolean(currentInputMode.getHandwritingOnKeyboardKey(), false);
            } else {
                hwrOn = appPrefs.getBoolean(currentInputMode.getHandwritingOnKeyboardKey(), true);
            }
        }
        if (corelib.isWriteChineseBuildEnabled()) {
            if (isPhoneticQwerty(currentInputMode.mInputMode)) {
                if (corelib.isTraceBuildEnabled()) {
                    hwrOn = appPrefs.getBoolean(currentInputMode.getHandwritingOnKeyboardKey(), false);
                } else {
                    hwrOn = appPrefs.getBoolean(currentInputMode.getHandwritingOnKeyboardKey(), true);
                }
            } else {
                hwrOn = appPrefs.getBoolean(currentInputMode.getHandwritingOnKeyboardKey(), true);
            }
        }
        boolean traceOn = corelib.isTraceBuildEnabled() ? appPrefs.getBoolean(currentInputMode.getTraceOnKeyboardKey(), true) : false;
        if ((corelib.isWriteChineseBuildEnabled() || corelib.isTraceBuildEnabled()) && !isPhoneticQwerty(currentInputMode.mInputMode)) {
            this.mActionOnKeyboard.add(new ActionItem(context.getString(R.string.action_on_keyboard_off), (hwrOn || traceOn) ? false : true));
        }
        if (corelib.isWriteChineseBuildEnabled()) {
            this.mActionOnKeyboard.add(new ActionItem(context.getString(R.string.handwriting_on_chinese_keyboard), hwrOn));
        }
        if (isPhoneticQwerty(currentInputMode.mInputMode) && corelib.isTraceBuildEnabled()) {
            this.mActionOnKeyboard.add(new ActionItem(context.getString(R.string.trace_on_chinese_keyboard), traceOn));
        }
    }

    private boolean isPhoneticQwerty(String mode) {
        return InputMethods.CHINESE_INPUT_MODE_PINYIN_QWERTY.equals(mode) || InputMethods.CHINESE_INPUT_MODE_DOUBLEPINYIN.equals(mode) || InputMethods.CHINESE_INPUT_MODE_ZHUYIN_QWERTY.equals(mode);
    }

    @Override // android.widget.Adapter
    public int getCount() {
        return this.mActionOnKeyboard.size();
    }

    @Override // android.widget.Adapter
    public ActionItem getItem(int position) {
        return this.mActionOnKeyboard.get(position);
    }

    @Override // android.widget.Adapter
    public long getItemId(int position) {
        return position;
    }

    @Override // android.widget.Adapter
    @SuppressLint({"PrivateResource"})
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(this.context).inflate(R.layout.action_on_keyboard_item, parent, false);
        }
        ActionItem actionItem = this.mActionOnKeyboard.get(position);
        TextView label = (TextView) convertView.findViewById(android.R.id.text1);
        if (label != null) {
            label.setText(actionItem.actionName);
        }
        RadioButton button = (RadioButton) convertView.findViewById(android.R.id.button1);
        if (button != null) {
            button.setChecked(actionItem.isChecked);
        }
        return convertView;
    }

    /* loaded from: classes.dex */
    public static class ActionItem {
        public final String actionName;
        public final boolean isChecked;

        public ActionItem(String name, boolean checked) {
            this.actionName = name;
            this.isChecked = checked;
        }
    }
}
