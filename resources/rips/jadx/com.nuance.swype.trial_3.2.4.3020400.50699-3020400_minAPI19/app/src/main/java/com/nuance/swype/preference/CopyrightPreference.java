package com.nuance.swype.preference;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.Preference;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.nuance.nmsp.client.util.internal.dictationresult.parser.xml.xmlResults.XMLResultsHandler;
import com.nuance.swype.input.BuildInfo;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.input.R;

/* loaded from: classes.dex */
public class CopyrightPreference extends Preference {
    public CopyrightPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public CopyrightPreference(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }

    @Override // android.preference.Preference
    protected View onCreateView(ViewGroup parent) {
        setLayoutResource(R.layout.copyright);
        View view = super.onCreateView(parent);
        TextView link = (TextView) view.findViewById(R.id.tos);
        String link_text = getContext().getResources().getString(R.string.terms_of_service);
        String link_url = getContext().getResources().getString(R.string.tos_url);
        link.setMovementMethod(LinkMovementMethod.getInstance());
        link.setText(Html.fromHtml("<a href=\"" + link_url + "\">" + link_text + "</a>"));
        TextView link2 = (TextView) view.findViewById(R.id.eula);
        if (IMEApplication.from(getContext()).getBuildInfo().getBuildType() == BuildInfo.BuildType.PRODUCTION) {
            link2.setVisibility(8);
        } else {
            String link_text2 = getContext().getResources().getString(R.string.legal_doc_eula);
            String link_url2 = getContext().getResources().getString(R.string.eula_url);
            link2.setMovementMethod(LinkMovementMethod.getInstance());
            link2.setText(Html.fromHtml("<a href=\"" + link_url2 + "\">" + link_text2 + "</a>"));
        }
        PackageManager packageManager = getContext().getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(getContext().getPackageName(), 0);
            TextView buildVersionText = (TextView) view.findViewById(R.id.version);
            if (packageInfo.versionName != null) {
                buildVersionText.append(XMLResultsHandler.SEP_SPACE);
                buildVersionText.append(packageInfo.versionName);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return view;
    }
}
