package com.facebook.internal;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.net.http.SslError;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.facebook.AccessToken;
import com.facebook.FacebookDialogException;
import com.facebook.FacebookException;
import com.facebook.FacebookGraphResponseException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.FacebookRequestError;
import com.facebook.FacebookSdk;
import com.facebook.FacebookServiceException;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestAsyncTask;
import com.facebook.GraphResponse;
import com.facebook.R;
import com.facebook.share.internal.ShareConstants;
import com.facebook.share.internal.ShareInternalUtility;
import com.facebook.share.widget.ShareDialog;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import org.json.JSONArray;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class WebDialog extends Dialog {
    private static final int API_EC_DIALOG_CANCEL = 4201;
    private static final int BACKGROUND_GRAY = -872415232;
    static final String CANCEL_URI = "fbconnect://cancel";
    static final boolean DISABLE_SSL_CHECK_FOR_TESTING = false;
    private static final String DISPLAY_TOUCH = "touch";
    private static final String LOG_TAG = "FacebookSDK.WebDialog";
    private static final int MAX_PADDING_SCREEN_HEIGHT = 1280;
    private static final int MAX_PADDING_SCREEN_WIDTH = 800;
    private static final double MIN_SCALE_FACTOR = 0.5d;
    private static final int NO_PADDING_SCREEN_HEIGHT = 800;
    private static final int NO_PADDING_SCREEN_WIDTH = 480;
    static final String REDIRECT_URI = "fbconnect://success";
    private FrameLayout contentFrameLayout;
    private ImageView crossImageView;
    private String expectedRedirectUrl;
    private boolean isDetached;
    private boolean isPageFinished;
    private boolean listenerCalled;
    private OnCompleteListener onCompleteListener;
    private ProgressDialog spinner;
    private UploadStagingResourcesTask uploadTask;
    private String url;
    private WebView webView;

    /* loaded from: classes.dex */
    public interface OnCompleteListener {
        void onComplete(Bundle bundle, FacebookException facebookException);
    }

    public WebDialog(Context context, String url) {
        this(context, url, FacebookSdk.getWebDialogTheme());
    }

    public WebDialog(Context context, String url, int theme) {
        super(context, theme == 0 ? FacebookSdk.getWebDialogTheme() : theme);
        this.expectedRedirectUrl = "fbconnect://success";
        this.listenerCalled = false;
        this.isDetached = false;
        this.isPageFinished = false;
        this.url = url;
    }

    public WebDialog(Context context, String action, Bundle parameters, int theme, OnCompleteListener listener) {
        super(context, theme == 0 ? FacebookSdk.getWebDialogTheme() : theme);
        this.expectedRedirectUrl = "fbconnect://success";
        this.listenerCalled = false;
        this.isDetached = false;
        this.isPageFinished = false;
        parameters = parameters == null ? new Bundle() : parameters;
        parameters.putString(ServerProtocol.DIALOG_PARAM_REDIRECT_URI, "fbconnect://success");
        parameters.putString(ServerProtocol.DIALOG_PARAM_DISPLAY, "touch");
        parameters.putString(ServerProtocol.DIALOG_PARAM_SDK_VERSION, String.format(Locale.ROOT, "android-%s", FacebookSdk.getSdkVersion()));
        this.onCompleteListener = listener;
        if (action.equals(ShareDialog.WEB_SHARE_DIALOG) && parameters.containsKey(ShareConstants.WEB_DIALOG_PARAM_MEDIA)) {
            this.uploadTask = new UploadStagingResourcesTask(action, parameters);
        } else {
            Uri uri = Utility.buildUri(ServerProtocol.getDialogAuthority(), FacebookSdk.getGraphApiVersion() + "/dialog/" + action, parameters);
            this.url = uri.toString();
        }
    }

    public void setOnCompleteListener(OnCompleteListener listener) {
        this.onCompleteListener = listener;
    }

    public OnCompleteListener getOnCompleteListener() {
        return this.onCompleteListener;
    }

    @Override // android.app.Dialog, android.view.KeyEvent.Callback
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 4) {
            cancel();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override // android.app.Dialog, android.content.DialogInterface
    public void dismiss() {
        if (this.webView != null) {
            this.webView.stopLoading();
        }
        if (!this.isDetached && this.spinner != null && this.spinner.isShowing()) {
            this.spinner.dismiss();
        }
        super.dismiss();
    }

    @Override // android.app.Dialog
    protected void onStart() {
        super.onStart();
        if (this.uploadTask != null && this.uploadTask.getStatus() == AsyncTask.Status.PENDING) {
            this.uploadTask.execute(new Void[0]);
            this.spinner.show();
        } else {
            resize();
        }
    }

    @Override // android.app.Dialog
    protected void onStop() {
        if (this.uploadTask != null) {
            this.uploadTask.cancel(true);
            this.spinner.dismiss();
        }
        super.onStop();
    }

    @Override // android.app.Dialog, android.view.Window.Callback
    public void onDetachedFromWindow() {
        this.isDetached = true;
        super.onDetachedFromWindow();
    }

    @Override // android.app.Dialog, android.view.Window.Callback
    public void onAttachedToWindow() {
        this.isDetached = false;
        super.onAttachedToWindow();
    }

    @Override // android.app.Dialog
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.spinner = new ProgressDialog(getContext());
        this.spinner.requestWindowFeature(1);
        this.spinner.setMessage(getContext().getString(R.string.com_facebook_loading));
        this.spinner.setCanceledOnTouchOutside(false);
        this.spinner.setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: com.facebook.internal.WebDialog.1
            @Override // android.content.DialogInterface.OnCancelListener
            public void onCancel(DialogInterface dialogInterface) {
                WebDialog.this.cancel();
            }
        });
        requestWindowFeature(1);
        this.contentFrameLayout = new FrameLayout(getContext());
        resize();
        getWindow().setGravity(17);
        getWindow().setSoftInputMode(16);
        createCrossImage();
        if (this.url != null) {
            int crossWidth = this.crossImageView.getDrawable().getIntrinsicWidth();
            setUpWebView((crossWidth / 2) + 1);
        }
        this.contentFrameLayout.addView(this.crossImageView, new ViewGroup.LayoutParams(-2, -2));
        setContentView(this.contentFrameLayout);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setExpectedRedirectUrl(String expectedRedirectUrl) {
        this.expectedRedirectUrl = expectedRedirectUrl;
    }

    protected Bundle parseResponseUri(String urlString) {
        Uri u = Uri.parse(urlString);
        Bundle b = Utility.parseUrlQueryString(u.getQuery());
        b.putAll(Utility.parseUrlQueryString(u.getFragment()));
        return b;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isListenerCalled() {
        return this.listenerCalled;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isPageFinished() {
        return this.isPageFinished;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public WebView getWebView() {
        return this.webView;
    }

    public void resize() {
        Display display = ((WindowManager) getContext().getSystemService("window")).getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int width = metrics.widthPixels < metrics.heightPixels ? metrics.widthPixels : metrics.heightPixels;
        int height = metrics.widthPixels < metrics.heightPixels ? metrics.heightPixels : metrics.widthPixels;
        int dialogWidth = Math.min(getScaledSize(width, metrics.density, NO_PADDING_SCREEN_WIDTH, 800), metrics.widthPixels);
        int dialogHeight = Math.min(getScaledSize(height, metrics.density, 800, 1280), metrics.heightPixels);
        getWindow().setLayout(dialogWidth, dialogHeight);
    }

    private int getScaledSize(int screenSize, float density, int noPaddingSize, int maxPaddingSize) {
        double scaleFactor;
        int scaledSize = (int) (screenSize / density);
        if (scaledSize <= noPaddingSize) {
            scaleFactor = 1.0d;
        } else if (scaledSize >= maxPaddingSize) {
            scaleFactor = 0.5d;
        } else {
            scaleFactor = 0.5d + (((maxPaddingSize - scaledSize) / (maxPaddingSize - noPaddingSize)) * 0.5d);
        }
        return (int) (screenSize * scaleFactor);
    }

    protected void sendSuccessToListener(Bundle values) {
        if (this.onCompleteListener != null && !this.listenerCalled) {
            this.listenerCalled = true;
            this.onCompleteListener.onComplete(values, null);
            dismiss();
        }
    }

    protected void sendErrorToListener(Throwable error) {
        FacebookException facebookException;
        if (this.onCompleteListener != null && !this.listenerCalled) {
            this.listenerCalled = true;
            if (error instanceof FacebookException) {
                facebookException = (FacebookException) error;
            } else {
                facebookException = new FacebookException(error);
            }
            this.onCompleteListener.onComplete(null, facebookException);
            dismiss();
        }
    }

    @Override // android.app.Dialog, android.content.DialogInterface
    public void cancel() {
        if (this.onCompleteListener != null && !this.listenerCalled) {
            sendErrorToListener(new FacebookOperationCanceledException());
        }
    }

    private void createCrossImage() {
        this.crossImageView = new ImageView(getContext());
        this.crossImageView.setOnClickListener(new View.OnClickListener() { // from class: com.facebook.internal.WebDialog.2
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                WebDialog.this.cancel();
            }
        });
        Drawable crossDrawable = getContext().getResources().getDrawable(R.drawable.com_facebook_close);
        this.crossImageView.setImageDrawable(crossDrawable);
        this.crossImageView.setVisibility(4);
    }

    /* JADX INFO: Access modifiers changed from: private */
    @SuppressLint({"SetJavaScriptEnabled"})
    public void setUpWebView(int margin) {
        LinearLayout webViewContainer = new LinearLayout(getContext());
        this.webView = new WebView(getContext().getApplicationContext()) { // from class: com.facebook.internal.WebDialog.3
            @Override // android.webkit.WebView, android.view.View
            public void onWindowFocusChanged(boolean hasWindowFocus) {
                try {
                    super.onWindowFocusChanged(hasWindowFocus);
                } catch (NullPointerException e) {
                }
            }
        };
        this.webView.setVerticalScrollBarEnabled(false);
        this.webView.setHorizontalScrollBarEnabled(false);
        this.webView.setWebViewClient(new DialogWebViewClient());
        this.webView.getSettings().setJavaScriptEnabled(true);
        this.webView.loadUrl(this.url);
        this.webView.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
        this.webView.setVisibility(4);
        this.webView.getSettings().setSavePassword(false);
        this.webView.getSettings().setSaveFormData(false);
        this.webView.setFocusable(true);
        this.webView.setFocusableInTouchMode(true);
        this.webView.setOnTouchListener(new View.OnTouchListener() { // from class: com.facebook.internal.WebDialog.4
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View v, MotionEvent event) {
                if (!v.hasFocus()) {
                    v.requestFocus();
                    return false;
                }
                return false;
            }
        });
        webViewContainer.setPadding(margin, margin, margin, margin);
        webViewContainer.addView(this.webView);
        webViewContainer.setBackgroundColor(BACKGROUND_GRAY);
        this.contentFrameLayout.addView(webViewContainer);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class DialogWebViewClient extends WebViewClient {
        private DialogWebViewClient() {
        }

        @Override // android.webkit.WebViewClient
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Utility.logd(WebDialog.LOG_TAG, "Redirect URL: " + url);
            if (url.startsWith(WebDialog.this.expectedRedirectUrl)) {
                Bundle values = WebDialog.this.parseResponseUri(url);
                String error = values.getString("error");
                if (error == null) {
                    error = values.getString(NativeProtocol.BRIDGE_ARG_ERROR_TYPE);
                }
                String errorMessage = values.getString("error_msg");
                if (errorMessage == null) {
                    errorMessage = values.getString(AnalyticsEvents.PARAMETER_SHARE_ERROR_MESSAGE);
                }
                if (errorMessage == null) {
                    errorMessage = values.getString(NativeProtocol.BRIDGE_ARG_ERROR_DESCRIPTION);
                }
                String errorCodeString = values.getString(NativeProtocol.BRIDGE_ARG_ERROR_CODE);
                int errorCode = -1;
                if (!Utility.isNullOrEmpty(errorCodeString)) {
                    try {
                        errorCode = Integer.parseInt(errorCodeString);
                    } catch (NumberFormatException e) {
                        errorCode = -1;
                    }
                }
                if (Utility.isNullOrEmpty(error) && Utility.isNullOrEmpty(errorMessage) && errorCode == -1) {
                    WebDialog.this.sendSuccessToListener(values);
                    return true;
                }
                if (error != null && (error.equals("access_denied") || error.equals("OAuthAccessDeniedException"))) {
                    WebDialog.this.cancel();
                    return true;
                }
                if (errorCode == WebDialog.API_EC_DIALOG_CANCEL) {
                    WebDialog.this.cancel();
                    return true;
                }
                FacebookRequestError requestError = new FacebookRequestError(errorCode, error, errorMessage);
                WebDialog.this.sendErrorToListener(new FacebookServiceException(requestError, errorMessage));
                return true;
            }
            if (url.startsWith("fbconnect://cancel")) {
                WebDialog.this.cancel();
                return true;
            }
            if (url.contains("touch")) {
                return false;
            }
            try {
                WebDialog.this.getContext().startActivity(new Intent("android.intent.action.VIEW", Uri.parse(url)));
                return true;
            } catch (ActivityNotFoundException e2) {
                return false;
            }
        }

        @Override // android.webkit.WebViewClient
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            WebDialog.this.sendErrorToListener(new FacebookDialogException(description, errorCode, failingUrl));
        }

        @Override // android.webkit.WebViewClient
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            super.onReceivedSslError(view, handler, error);
            handler.cancel();
            WebDialog.this.sendErrorToListener(new FacebookDialogException(null, -11, null));
        }

        @Override // android.webkit.WebViewClient
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            Utility.logd(WebDialog.LOG_TAG, "Webview loading URL: " + url);
            super.onPageStarted(view, url, favicon);
            if (!WebDialog.this.isDetached) {
                WebDialog.this.spinner.show();
            }
        }

        @Override // android.webkit.WebViewClient
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (!WebDialog.this.isDetached) {
                WebDialog.this.spinner.dismiss();
            }
            WebDialog.this.contentFrameLayout.setBackgroundColor(0);
            WebDialog.this.webView.setVisibility(0);
            WebDialog.this.crossImageView.setVisibility(0);
            WebDialog.this.isPageFinished = true;
        }
    }

    /* loaded from: classes.dex */
    public static class Builder {
        private AccessToken accessToken;
        private String action;
        private String applicationId;
        private Context context;
        private OnCompleteListener listener;
        private Bundle parameters;
        private int theme;

        public Builder(Context context, String action, Bundle parameters) {
            this.accessToken = AccessToken.getCurrentAccessToken();
            if (this.accessToken == null) {
                String applicationId = Utility.getMetadataApplicationId(context);
                if (applicationId != null) {
                    this.applicationId = applicationId;
                } else {
                    throw new FacebookException("Attempted to create a builder without a valid access token or a valid default Application ID.");
                }
            }
            finishInit(context, action, parameters);
        }

        public Builder(Context context, String applicationId, String action, Bundle parameters) {
            applicationId = applicationId == null ? Utility.getMetadataApplicationId(context) : applicationId;
            Validate.notNullOrEmpty(applicationId, "applicationId");
            this.applicationId = applicationId;
            finishInit(context, action, parameters);
        }

        public Builder setTheme(int theme) {
            this.theme = theme;
            return this;
        }

        public Builder setOnCompleteListener(OnCompleteListener listener) {
            this.listener = listener;
            return this;
        }

        public WebDialog build() {
            if (this.accessToken != null) {
                this.parameters.putString("app_id", this.accessToken.getApplicationId());
                this.parameters.putString("access_token", this.accessToken.getToken());
            } else {
                this.parameters.putString("app_id", this.applicationId);
            }
            return new WebDialog(this.context, this.action, this.parameters, this.theme, this.listener);
        }

        public String getApplicationId() {
            return this.applicationId;
        }

        public Context getContext() {
            return this.context;
        }

        public int getTheme() {
            return this.theme;
        }

        public Bundle getParameters() {
            return this.parameters;
        }

        public OnCompleteListener getListener() {
            return this.listener;
        }

        private void finishInit(Context context, String action, Bundle parameters) {
            this.context = context;
            this.action = action;
            if (parameters != null) {
                this.parameters = parameters;
            } else {
                this.parameters = new Bundle();
            }
        }
    }

    /* loaded from: classes.dex */
    private class UploadStagingResourcesTask extends AsyncTask<Void, Void, String[]> {
        private String action;
        private Exception[] exceptions;
        private Bundle parameters;

        UploadStagingResourcesTask(String action, Bundle parameters) {
            this.action = action;
            this.parameters = parameters;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public String[] doInBackground(Void... args) {
            String[] params = this.parameters.getStringArray(ShareConstants.WEB_DIALOG_PARAM_MEDIA);
            final String[] results = new String[params.length];
            this.exceptions = new Exception[params.length];
            final CountDownLatch latch = new CountDownLatch(params.length);
            ConcurrentLinkedQueue<GraphRequestAsyncTask> tasks = new ConcurrentLinkedQueue<>();
            AccessToken accessToken = AccessToken.getCurrentAccessToken();
            for (int i = 0; i < params.length; i++) {
                try {
                    if (isCancelled()) {
                        Iterator i$ = tasks.iterator();
                        while (i$.hasNext()) {
                            i$.next().cancel(true);
                        }
                        return null;
                    }
                    Uri uri = Uri.parse(params[i]);
                    final int writeIndex = i;
                    if (Utility.isWebUri(uri)) {
                        results[writeIndex] = uri.toString();
                        latch.countDown();
                    } else {
                        GraphRequest.Callback callback = new GraphRequest.Callback() { // from class: com.facebook.internal.WebDialog.UploadStagingResourcesTask.1
                            @Override // com.facebook.GraphRequest.Callback
                            public void onCompleted(GraphResponse response) {
                                FacebookRequestError error;
                                try {
                                    error = response.getError();
                                } catch (Exception e) {
                                    UploadStagingResourcesTask.this.exceptions[writeIndex] = e;
                                }
                                if (error != null) {
                                    String message = error.getErrorMessage();
                                    if (message == null) {
                                        message = "Error staging photo.";
                                    }
                                    throw new FacebookGraphResponseException(response, message);
                                }
                                JSONObject data = response.getJSONObject();
                                if (data == null) {
                                    throw new FacebookException("Error staging photo.");
                                }
                                String stagedImageUri = data.optString(ShareConstants.MEDIA_URI);
                                if (stagedImageUri == null) {
                                    throw new FacebookException("Error staging photo.");
                                }
                                results[writeIndex] = stagedImageUri;
                                latch.countDown();
                            }
                        };
                        GraphRequestAsyncTask task = ShareInternalUtility.newUploadStagingResourceWithImageRequest(accessToken, uri, callback).executeAsync();
                        tasks.add(task);
                    }
                } catch (Exception e) {
                    Iterator i$2 = tasks.iterator();
                    while (i$2.hasNext()) {
                        i$2.next().cancel(true);
                    }
                    return null;
                }
            }
            latch.await();
            return results;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(String[] results) {
            WebDialog.this.spinner.dismiss();
            Exception[] arr$ = this.exceptions;
            for (Exception e : arr$) {
                if (e != null) {
                    WebDialog.this.sendErrorToListener(e);
                    return;
                }
            }
            if (results == null) {
                WebDialog.this.sendErrorToListener(new FacebookException("Failed to stage photos for web dialog"));
                return;
            }
            List<String> resultList = Arrays.asList(results);
            if (resultList.contains(null)) {
                WebDialog.this.sendErrorToListener(new FacebookException("Failed to stage photos for web dialog"));
                return;
            }
            Utility.putJSONValueInBundle(this.parameters, ShareConstants.WEB_DIALOG_PARAM_MEDIA, new JSONArray((Collection) resultList));
            Uri uri = Utility.buildUri(ServerProtocol.getDialogAuthority(), FacebookSdk.getGraphApiVersion() + "/dialog/" + this.action, this.parameters);
            WebDialog.this.url = uri.toString();
            int crossWidth = WebDialog.this.crossImageView.getDrawable().getIntrinsicWidth();
            WebDialog.this.setUpWebView((crossWidth / 2) + 1);
        }
    }
}
