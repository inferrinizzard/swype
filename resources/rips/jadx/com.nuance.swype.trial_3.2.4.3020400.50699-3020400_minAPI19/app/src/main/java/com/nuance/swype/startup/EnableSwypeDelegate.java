package com.nuance.swype.startup;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.nuance.swype.input.QuickToast;
import com.nuance.swype.input.R;
import com.nuance.swype.input.settings.SettingsDispatch;
import com.nuance.swype.util.LogManager;
import com.nuance.swype.util.SafeMessageHandler;

/* loaded from: classes.dex */
public class EnableSwypeDelegate extends StartupDelegate {
    private static final LogManager.Log log = LogManager.getLog("EnableSwypeDelegate");
    private boolean displayedInputMethods;
    private ImeSettingsHandler mSettingsHandler;
    private boolean showPopupTip;

    static /* synthetic */ boolean access$002$32f5abfe(EnableSwypeDelegate x0) {
        x0.displayedInputMethods = true;
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static EnableSwypeDelegate newInstance(Bundle savedInstanceState) {
        EnableSwypeDelegate f = new EnableSwypeDelegate();
        f.setArguments(savedInstanceState);
        return f;
    }

    @Override // com.nuance.swype.startup.StartupDelegate, android.app.Fragment
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mSettingsHandler = new ImeSettingsHandler(this, getActivity());
    }

    @Override // com.nuance.swype.startup.StartupDelegate, android.app.Fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        String version = getActivity().getString(R.string.ime_name);
        String msg = String.format(getActivity().getString(R.string.startup_enable_swype), version);
        loadTemplateToContentView(inflater, R.layout.startup_template_three_quarters, R.layout.startup_enable_swype, msg);
        ImageView enableSwypeButton = (ImageView) this.view.findViewById(R.id.enable_swype_button);
        enableSwypeButton.setClickable(true);
        enableSwypeButton.setOnClickListener(new View.OnClickListener() { // from class: com.nuance.swype.startup.EnableSwypeDelegate.1
            @Override // android.view.View.OnClickListener
            public final void onClick(View arg0) {
                if (!EnableSwypeDelegate.this.mStartupSequenceInfo.isSwypeEnabled()) {
                    Intent intent = new Intent("android.settings.INPUT_METHOD_SETTINGS");
                    EnableSwypeDelegate.this.getActivity().startActivityForResult(intent, 0);
                    EnableSwypeDelegate.access$002$32f5abfe(EnableSwypeDelegate.this);
                    EnableSwypeDelegate.this.mSettingsHandler.sendEmptyMessageDelayed(0, 200L);
                    return;
                }
                EnableSwypeDelegate.this.mActivityCallbacks.startNextScreen(EnableSwypeDelegate.this.mFlags, EnableSwypeDelegate.this.mResultData);
            }
        });
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override // com.nuance.swype.startup.StartupDelegate, android.app.Fragment
    public void onResume() {
        super.onResume();
        if (this.displayedInputMethods) {
            this.mSettingsHandler.sendEmptyMessage(1);
            validateStatus();
        }
        this.displayedInputMethods = false;
    }

    /* loaded from: classes.dex */
    private static final class ImeSettingsHandler extends SafeMessageHandler<EnableSwypeDelegate> {
        private final Context mContext;

        public ImeSettingsHandler(EnableSwypeDelegate parent, Context context) {
            super(parent);
            this.mContext = context;
        }

        @Override // com.nuance.swype.util.SafeMessageHandler
        public final /* bridge */ /* synthetic */ void handleMessage(Message message, EnableSwypeDelegate enableSwypeDelegate) {
            switch (message.what) {
                case 0:
                    if (StartupDelegate.isSwypeEnabled(this.mContext)) {
                        EnableSwypeDelegate.log.d("MSG_START_POLLING_IME_SETTINGS: swype enabled, invoking wizard");
                        Context context = this.mContext;
                        Intent intent = new Intent();
                        intent.setClass(context, SettingsDispatch.class);
                        context.startActivity(intent);
                        return;
                    }
                    EnableSwypeDelegate.log.d("MSG_START_POLLING_IME_SETTINGS: swype not enabled, polling...");
                    if (!hasMessages(0)) {
                        sendMessageDelayed(obtainMessage(0), 200L);
                        return;
                    }
                    return;
                case 1:
                    EnableSwypeDelegate.log.d("MSG_STOP_POLLING_IME_SETTINGS");
                    removeMessages(0);
                    return;
                default:
                    EnableSwypeDelegate.log.e("handleMessage: unknown message type: " + message.what);
                    return;
            }
        }
    }

    private void validateStatus() {
        if (this.mStartupSequenceInfo.isSwypeEnabled()) {
            this.mActivityCallbacks.startNextScreen(this.mFlags, this.mResultData);
        } else if (this.showPopupTip) {
            String version = getActivity().getString(R.string.ime_name);
            String msg = String.format(getActivity().getString(R.string.startup_enable_swype_toast), version);
            Point displaySize = new Point();
            getActivity().getWindowManager().getDefaultDisplay().getSize(displaySize);
            QuickToast.show(getActivity(), msg, 1, displaySize.y / 2);
        }
        this.showPopupTip = true;
    }

    @Override // com.nuance.swype.startup.StartupDelegate
    public final void onBackPressed() {
        if (!this.mStartupSequenceInfo.isSwypeEnabled()) {
            showSelectSwypeDialog();
        }
        this.showPopupTip = false;
    }

    @Override // android.app.Fragment
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            validateStatus();
        }
    }
}
