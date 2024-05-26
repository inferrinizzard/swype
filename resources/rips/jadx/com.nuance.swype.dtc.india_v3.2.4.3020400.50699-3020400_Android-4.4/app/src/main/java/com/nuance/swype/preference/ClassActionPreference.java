package com.nuance.swype.preference;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;

/* loaded from: classes.dex */
public class ClassActionPreference extends ViewClickPreference {
    static final /* synthetic */ boolean $assertionsDisabled;

    static {
        $assertionsDisabled = !ClassActionPreference.class.desiredAssertionStatus();
    }

    public ClassActionPreference(Context context) {
        super(context);
    }

    public ClassActionPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ClassActionPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.nuance.swype.preference.ViewClickPreference, android.preference.Preference
    public void onBindView(View view) {
        super.onBindView(view);
        Intent intent = getIntent();
        if (intent != null) {
            ComponentName component = intent.getComponent();
            String packageName = getContext().getPackageName();
            if (component == null) {
                String className = intent.getAction();
                if (!$assertionsDisabled && className == null) {
                    throw new AssertionError();
                }
                Intent launchIntent = intent.setAction(null).setClassName(packageName, className);
                if (intent.getExtras() != null) {
                    launchIntent.putExtras(intent.getExtras());
                }
                setIntent(launchIntent);
                return;
            }
            if (!packageName.equals(component.getPackageName())) {
                throw new IllegalStateException(intent.toString());
            }
        }
    }
}
