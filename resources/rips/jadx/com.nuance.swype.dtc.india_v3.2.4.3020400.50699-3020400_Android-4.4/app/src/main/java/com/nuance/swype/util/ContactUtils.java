package com.nuance.swype.util;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.ContactsContract;
import android.view.KeyEvent;
import com.nuance.swype.input.IME;
import com.nuance.swype.input.IMEApplication;
import com.nuance.swype.input.InputView;
import com.nuance.swype.input.R;
import com.nuance.swype.util.LogManager;

/* loaded from: classes.dex */
public final class ContactUtils {
    private static String contactName;
    private static final LogManager.Log log = LogManager.getLog("ContactUtils");
    private static Context mContext;
    private static AsyncQueryTask queryTask;
    private static int wordSource;

    static /* synthetic */ Context access$202$6263c3eb() {
        mContext = null;
        return null;
    }

    static /* synthetic */ String access$402$16915f7f() {
        contactName = null;
        return null;
    }

    static /* synthetic */ int access$602$134621() {
        wordSource = 0;
        return 0;
    }

    public static void getContactNumberFromPhoneBook(Context context, String name, int wdSource) {
        byte b = 0;
        mContext = context;
        contactName = name;
        wordSource = wdSource;
        if (name != null) {
            AsyncQueryTask asyncQueryTask = new AsyncQueryTask(b);
            queryTask = asyncQueryTask;
            asyncQueryTask.execute(name);
        }
    }

    public static void cancelQueryTask() {
        if (queryTask != null) {
            queryTask.cancel(true);
        }
        queryTask = null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class AsyncQueryTask extends AsyncTask<String, Void, String> {
        private AsyncQueryTask() {
        }

        /* synthetic */ AsyncQueryTask(byte b) {
            this();
        }

        @Override // android.os.AsyncTask
        protected final /* bridge */ /* synthetic */ String doInBackground(String[] strArr) {
            String[] strArr2 = strArr;
            if (isCancelled()) {
                return null;
            }
            return ContactUtils.access$100(strArr2[0]);
        }

        @Override // android.os.AsyncTask
        protected final /* bridge */ /* synthetic */ void onPostExecute(String str) {
            String str2 = str;
            if (!isCancelled()) {
                if (str2 != null) {
                    ContactUtils.access$500(ContactUtils.mContext, ContactUtils.contactName, str2);
                } else {
                    IME ime = IMEApplication.from(ContactUtils.mContext).getIME();
                    if (ime == null) {
                        return;
                    }
                    InputView currentInputView = ime.getCurrentInputView();
                    if (currentInputView != null) {
                        currentInputView.showRemoveUdbWordDialog(ContactUtils.contactName, ContactUtils.wordSource);
                    }
                }
                ContactUtils.access$202$6263c3eb();
                ContactUtils.access$402$16915f7f();
                ContactUtils.access$602$134621();
                ContactUtils.queryTask = null;
            }
        }

        @Override // android.os.AsyncTask
        protected final /* bridge */ /* synthetic */ void onProgressUpdate(Void[] voidArr) {
        }

        @Override // android.os.AsyncTask
        protected final void onCancelled() {
            ContactUtils.access$202$6263c3eb();
            ContactUtils.log.d("AsyncQueryTask onCancelled complete.");
        }
    }

    static /* synthetic */ String access$100(String x0) {
        if (mContext == null) {
            return null;
        }
        if (Build.VERSION.SDK_INT >= 23 && mContext.checkSelfPermission("android.permission.READ_CONTACTS") != 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        Cursor query = mContext.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, "display_name = ?", new String[]{x0}, null);
        if (query == null || !query.moveToFirst()) {
            return null;
        }
        String string = query.getString(query.getColumnIndex("data1"));
        if (string != null && string.length() > 0) {
            sb.append(string);
            query.close();
            return sb.toString();
        }
        query.close();
        return null;
    }

    static /* synthetic */ void access$500(final Context x0, final String x1, final String x2) {
        if (x1 != null && x2 != null && x0 != null) {
            if (Build.VERSION.SDK_INT < 23 || x0.checkSelfPermission("android.permission.READ_CONTACTS") == 0) {
                Resources resources = x0.getResources();
                String[] strArr = {resources.getString(R.string.call), resources.getString(R.string.message), resources.getString(R.string.remove_button)};
                final IME ime = IMEApplication.from(x0).getIME();
                if (ime == null) {
                    return;
                }
                if (ime.mOptionsDialog != null && ime.mOptionsDialog.isShowing()) {
                    return;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(ime.getApplicationContext());
                builder.setCancelable(true);
                builder.setOnKeyListener(new DialogInterface.OnKeyListener() { // from class: com.nuance.swype.util.ContactUtils.1
                    @Override // android.content.DialogInterface.OnKeyListener
                    public final boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        if (keyCode == 5 && event.getAction() == 1) {
                            dialog.dismiss();
                            return false;
                        }
                        return false;
                    }
                });
                builder.setItems(strArr, new DialogInterface.OnClickListener() { // from class: com.nuance.swype.util.ContactUtils.2
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface arg0, int arg1) {
                        switch (arg1) {
                            case 0:
                                try {
                                    Uri DialUri = Uri.parse("tel:" + x2);
                                    Intent DialIntent = new Intent("android.intent.action.DIAL", DialUri);
                                    DialIntent.addFlags(268435456);
                                    x0.startActivity(DialIntent);
                                    return;
                                } catch (ActivityNotFoundException e) {
                                    e.printStackTrace();
                                    return;
                                }
                            case 1:
                                try {
                                    Uri uri = Uri.parse("smsto:" + x2);
                                    Intent MsgIntent = new Intent("android.intent.action.SENDTO", uri);
                                    MsgIntent.addFlags(268435456);
                                    x0.startActivity(MsgIntent);
                                    return;
                                } catch (ActivityNotFoundException e2) {
                                    e2.printStackTrace();
                                    return;
                                }
                            case 2:
                                InputView inputView = ime.getCurrentInputView();
                                if (inputView != null) {
                                    inputView.showRemoveUdbWordDialog(x1, 9);
                                    return;
                                }
                                return;
                            default:
                                return;
                        }
                    }
                });
                builder.setIcon(R.drawable.swype_logo);
                builder.setNegativeButton(android.R.string.cancel, (DialogInterface.OnClickListener) null);
                builder.setTitle(x1);
                ime.mOptionsDialog = builder.create();
                ime.attachDialog(ime.mOptionsDialog);
                ime.mOptionsDialog.show();
            }
        }
    }
}
