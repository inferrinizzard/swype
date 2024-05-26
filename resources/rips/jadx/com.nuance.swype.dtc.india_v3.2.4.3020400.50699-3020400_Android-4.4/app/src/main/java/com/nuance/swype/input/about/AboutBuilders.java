package com.nuance.swype.input.about;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.text.Html;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.nuance.nmdp.speechkit.oem.OemConfig;
import com.nuance.nmdp.speechkit.util.Config;
import com.nuance.nmsp.client.sdk.common.defines.NMSPDefines;
import com.nuance.nmsp.client.util.internal.dictationresult.parser.xml.xmlResults.XMLResultsHandler;
import com.nuance.swype.connect.Connect;
import com.nuance.swype.input.BuildInfo;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.input.R;

/* loaded from: classes.dex */
public class AboutBuilders {
    public static final String URL_DATA = "about_url";
    public static final int ABOUT_PREFS_XML = R.xml.about_preferences;
    private static final int[] HEADER_IDS = {R.id.website_link_header, R.id.twitter_link_header, R.id.facebook_link_header, R.id.youtube_link_header};
    private static final int[] HEADER_STRING_IDS = {R.string.about_header_website, R.string.about_header_twitter, R.string.about_header_facebook, R.string.about_header_youtube};
    private static final int[] URL_IDS = {R.id.website_link, R.id.twitter_link, R.id.facebook_link, R.id.youtube_link};

    private AboutBuilders() {
    }

    public static void fixupAboutPreferences(PreferenceScreen screen) {
        Context context = screen.getContext();
        Preference connectPref = screen.findPreference("swype_connect");
        if (connectPref != null) {
            if (Connect.from(screen.getContext()).isStarted()) {
                connectPref.setEnabled(Connect.from(screen.getContext()).isStarted());
            } else {
                screen.removePreference(connectPref);
            }
        }
        Preference dragonPref = screen.findPreference("swype_dragon");
        if (dragonPref != null && IMEApplication.from(context).getSpeechProvider() != 0) {
            screen.removePreference(dragonPref);
        }
        Preference starTrekPref = screen.findPreference("swype_star_trek");
        if (starTrekPref != null && !BuildInfo.from(context).isDownloadableThemesEnabled()) {
            screen.removePreference(starTrekPref);
        }
        Preference twoThumbzPref = screen.findPreference("swype_two_thumbz");
        if (twoThumbzPref != null && !BuildInfo.from(context).isDownloadableThemesEnabled()) {
            screen.removePreference(twoThumbzPref);
        }
        Preference btmPref = screen.findPreference("swype_btm");
        if (btmPref != null && !BuildInfo.from(context).isDownloadableThemesEnabled()) {
            screen.removePreference(btmPref);
        }
    }

    public static View buildAboutSwype(Context context, View.OnClickListener listener) {
        try {
            String swypeVersionStr = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
            View view = buildView(context, R.layout.about_swype, context.getString(R.string.swype), swypeVersionStr, listener);
            TextView versionTextView = (TextView) view.findViewById(R.id.version);
            versionTextView.setMovementMethod(LinkMovementMethod.getInstance());
            versionTextView.setText(Html.fromHtml("<u>" + ((Object) versionTextView.getText()) + "</u>"));
            versionTextView.setClickable(true);
            versionTextView.setOnClickListener(listener);
            TextView xt9VersionTextView = (TextView) view.findViewById(R.id.xt9_version);
            if (xt9VersionTextView != null) {
                String version = sanitizeVersionNumber(IMEApplication.from(context).getSwypeCoreLibMgr().getSwypeCoreLibInstance().getXT9CoreVersion());
                xt9VersionTextView.append(XMLResultsHandler.SEP_SPACE + version);
            }
            TextView xt9buildId = (TextView) view.findViewById(R.id.xt9_build_id);
            if (xt9buildId != null) {
                String buildId = IMEApplication.from(context).getSwypeCoreLibMgr().getSwypeCoreLibInstance().getXT9CoreBuildId();
                xt9buildId.setText(buildId);
            }
            TextView t9writeVersionTextView = (TextView) view.findViewById(R.id.t9write_version);
            if (t9writeVersionTextView != null) {
                String version2 = sanitizeVersionNumber(IMEApplication.from(context).getSwypeCoreLibMgr().getSwypeCoreLibInstance().getT9WriteAlphaCoreVersion());
                t9writeVersionTextView.append(XMLResultsHandler.SEP_SPACE + version2);
            }
            TextView webLink = (TextView) view.findViewById(R.id.website_link);
            if (webLink != null) {
                webLink.setOnClickListener(new OnURLClickListener());
            }
            TextView webLink2 = (TextView) view.findViewById(R.id.twitter_link);
            if (webLink2 != null) {
                webLink2.setOnClickListener(new OnURLClickListener());
            }
            TextView webLink3 = (TextView) view.findViewById(R.id.facebook_link);
            if (webLink3 != null) {
                webLink3.setOnClickListener(new OnURLClickListener());
            }
            TextView webLink4 = (TextView) view.findViewById(R.id.youtube_link);
            if (webLink4 != null) {
                webLink4.setOnClickListener(new OnURLClickListener());
            }
            return view;
        } catch (PackageManager.NameNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }

    public static View buildAboutConnect(Context context, View.OnClickListener listener) {
        View view = buildView(context, R.layout.about_connect, context.getString(R.string.swype), "", listener);
        TextView scsdkVersion = (TextView) view.findViewById(R.id.version_content);
        if (scsdkVersion != null) {
            String version = Connect.from(context).getVersion();
            scsdkVersion.setText(version);
        }
        TextView swyperIdTextView = (TextView) view.findViewById(R.id.swyperid_content);
        if (swyperIdTextView != null) {
            swyperIdTextView.setText(Connect.from(context).getSwyperId());
        }
        try {
            TextView swibTextView = (TextView) view.findViewById(R.id.swib_content);
            BuildInfo swypeBuildInfo = BuildInfo.from(context);
            if (swibTextView != null) {
                swibTextView.setText(swypeBuildInfo.getSwib() + "." + Connect.from(context).getOemId());
            }
        } catch (Exception e) {
        }
        TextView webLink = (TextView) view.findViewById(R.id.website_link);
        if (webLink != null) {
            webLink.setOnClickListener(new OnURLClickListener());
        }
        return view;
    }

    public static View buildAboutDragon(Context context) {
        String uuid;
        View view = buildView(context, R.layout.about_dragon, context.getString(R.string.dragon), BuildInfo.DRAGON_SPEECH_VERSION, null);
        TextView speechView = (TextView) view.findViewById(R.id.speech_kit_version);
        if (speechView != null) {
            String speech_version = "Speech Kit " + Config.getNmdpVersion();
            speechView.setText(speech_version);
        }
        TextView nmspView = (TextView) view.findViewById(R.id.nmsp_version);
        if (nmspView != null) {
            nmspView.setText(NMSPDefines.VERSION);
        }
        TextView textView = (TextView) view.findViewById(R.id.uuid);
        if (textView != null && (uuid = new OemConfig(context).uid()) != null) {
            textView.setText(uuid);
        }
        TextView webLink = (TextView) view.findViewById(R.id.website_link);
        if (webLink != null) {
            webLink.setOnClickListener(new OnURLClickListener());
        }
        TextView webLink2 = (TextView) view.findViewById(R.id.twitter_link);
        if (webLink2 != null) {
            webLink2.setOnClickListener(new OnURLClickListener());
        }
        TextView webLink3 = (TextView) view.findViewById(R.id.facebook_link);
        if (webLink3 != null) {
            webLink3.setOnClickListener(new OnURLClickListener());
        }
        TextView webLink4 = (TextView) view.findViewById(R.id.youtube_link);
        if (webLink4 != null) {
            webLink4.setOnClickListener(new OnURLClickListener());
        }
        return view;
    }

    private static View buildView(Context context, int layoutId, String productName, String versionNumber, View.OnClickListener listener) {
        View view = LayoutInflater.from(context).inflate(layoutId, (ViewGroup) null);
        updateHeaders(view, productName);
        stripUnderlines(view);
        updateVersionNumber(view, versionNumber);
        return view;
    }

    private static void updateHeaders(View parent, String productName) {
        for (int i = 0; i < HEADER_IDS.length; i++) {
            TextView textView = (TextView) parent.findViewById(HEADER_IDS[i]);
            if (textView != null) {
                textView.setText(parent.getContext().getString(HEADER_STRING_IDS[i], productName));
            }
        }
    }

    private static void stripUnderlines(View parent) {
        for (int id : URL_IDS) {
            TextView textView = (TextView) parent.findViewById(id);
            if (textView != null && (textView instanceof Spannable)) {
                stripUnderlines((Spannable) textView.getText());
            }
        }
    }

    private static void stripUnderlines(Spannable s) {
        for (URLSpan span : (URLSpan[]) s.getSpans(0, s.length(), URLSpan.class)) {
            int start = s.getSpanStart(span);
            int end = s.getSpanEnd(span);
            s.removeSpan(span);
            s.setSpan(new URLSpanNoUnderline(span.getURL()), start, end, 0);
        }
    }

    private static void updateVersionNumber(View parent, String versionNumber) {
        TextView textView = (TextView) parent.findViewById(R.id.version);
        if (textView != null) {
            textView.append(XMLResultsHandler.SEP_SPACE + versionNumber);
        }
    }

    private static String sanitizeVersionNumber(String version) {
        int index = version.lastIndexOf("V");
        if (index != -1) {
            return version.substring(index + 1);
        }
        return version;
    }

    private static void addClickableSpan(View parent, int id, View.OnClickListener listener) {
        TextView textView = (TextView) parent.findViewById(id);
        if (textView != null) {
            textView.setMovementMethod(LinkMovementMethod.getInstance());
            Spannable span = (Spannable) textView.getText();
            ClickableSpanNoUnderline clickSpan = new ClickableSpanNoUnderline(listener);
            span.setSpan(clickSpan, 0, span.length(), 33);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class URLSpanNoUnderline extends URLSpan {
        public URLSpanNoUnderline(String url) {
            super(url);
        }

        @Override // android.text.style.ClickableSpan, android.text.style.CharacterStyle
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setUnderlineText(false);
        }
    }

    /* loaded from: classes.dex */
    private static class ClickableSpanNoUnderline extends ClickableSpan {
        private final View.OnClickListener listener;

        public ClickableSpanNoUnderline(View.OnClickListener listener) {
            this.listener = listener;
        }

        @Override // android.text.style.ClickableSpan, android.text.style.CharacterStyle
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setUnderlineText(false);
        }

        @Override // android.text.style.ClickableSpan
        public void onClick(View widget) {
            if (this.listener != null) {
                this.listener.onClick(widget);
            }
        }
    }

    /* loaded from: classes.dex */
    private static class OnURLClickListener implements View.OnClickListener {
        private OnURLClickListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View v) {
            Context context;
            CharSequence text = ((TextView) v).getText();
            if (text != null && (context = v.getContext()) != null) {
                boolean faildOnOpenBrowser = false;
                try {
                    Intent browserIntent = new Intent("android.intent.action.VIEW", Uri.parse(text.toString()));
                    context.startActivity(browserIntent);
                } catch (Exception e) {
                    faildOnOpenBrowser = true;
                }
                if (faildOnOpenBrowser) {
                    try {
                        Intent webviewIntent = new Intent(context, (Class<?>) AboutWebViewActivity.class);
                        webviewIntent.putExtra(AboutBuilders.URL_DATA, text);
                        context.startActivity(webviewIntent);
                    } catch (Exception e2) {
                    }
                }
            }
        }
    }
}
