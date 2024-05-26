package android.support.v4.app;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.media.session.MediaController;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityCompatApi23;
import android.support.v4.app.LoaderManagerImpl;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.util.ContainerHelpers;
import android.support.v4.util.SimpleArrayMap;
import android.support.v4.util.SparseArrayCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import com.nuance.nmsp.client.util.internal.dictationresult.parser.xml.xmlResults.XMLResultsHandler;
import com.nuance.swype.input.ThemeManager;
import java.io.FileDescriptor;
import java.io.PrintWriter;

/* loaded from: classes.dex */
public class FragmentActivity extends BaseFragmentActivityJB implements ActivityCompat.OnRequestPermissionsResultCallback, ActivityCompatApi23.RequestPermissionsRequestCodeValidator {
    static final String ALLOCATED_REQUEST_INDICIES_TAG = "android:support:request_indicies";
    static final String FRAGMENTS_TAG = "android:support:fragments";
    private static final int HONEYCOMB = 11;
    static final int MAX_NUM_PENDING_FRAGMENT_ACTIVITY_RESULTS = 65534;
    static final int MSG_REALLY_STOPPED = 1;
    static final int MSG_RESUME_PENDING = 2;
    static final String NEXT_CANDIDATE_REQUEST_INDEX_TAG = "android:support:next_request_index";
    static final String REQUEST_FRAGMENT_WHO_TAG = "android:support:request_fragment_who";
    private static final String TAG = "FragmentActivity";
    boolean mCreated;
    MediaControllerCompat mMediaController;
    int mNextCandidateRequestIndex;
    boolean mOptionsMenuInvalidated;
    SparseArrayCompat<String> mPendingFragmentActivityResults;
    boolean mReallyStopped;
    boolean mRequestedPermissionsFromFragment;
    boolean mResumed;
    boolean mRetaining;
    boolean mStopped;
    final Handler mHandler = new Handler() { // from class: android.support.v4.app.FragmentActivity.1
        @Override // android.os.Handler
        public final void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if (FragmentActivity.this.mStopped) {
                        FragmentActivity.this.doReallyStop(false);
                        return;
                    }
                    return;
                case 2:
                    FragmentActivity.this.onResumeFragments();
                    FragmentActivity.this.mFragments.execPendingActions();
                    return;
                default:
                    super.handleMessage(msg);
                    return;
            }
        }
    };
    final FragmentController mFragments = new FragmentController(new HostCallbacks());

    @Override // android.support.v4.app.BaseFragmentActivityHoneycomb, android.app.Activity, android.view.LayoutInflater.Factory2
    public /* bridge */ /* synthetic */ View onCreateView(View x0, String x1, Context x2, AttributeSet x3) {
        return super.onCreateView(x0, x1, x2, x3);
    }

    @Override // android.support.v4.app.BaseFragmentActivityDonut, android.app.Activity, android.view.LayoutInflater.Factory
    public /* bridge */ /* synthetic */ View onCreateView(String x0, Context x1, AttributeSet x2) {
        return super.onCreateView(x0, x1, x2);
    }

    @Override // android.support.v4.app.BaseFragmentActivityJB, android.app.Activity
    public /* bridge */ /* synthetic */ void startActivityForResult(Intent intent, int i, Bundle bundle) {
        super.startActivityForResult(intent, i, bundle);
    }

    @Override // android.support.v4.app.BaseFragmentActivityEclair, android.app.Activity
    public /* bridge */ /* synthetic */ void startIntentSenderForResult(IntentSender intentSender, int i, Intent intent, int i2, int i3, int i4) throws IntentSender.SendIntentException {
        super.startIntentSenderForResult(intentSender, i, intent, i2, i3, i4);
    }

    @Override // android.support.v4.app.BaseFragmentActivityJB, android.app.Activity
    public /* bridge */ /* synthetic */ void startIntentSenderForResult(IntentSender intentSender, int i, Intent intent, int i2, int i3, int i4, Bundle bundle) throws IntentSender.SendIntentException {
        super.startIntentSenderForResult(intentSender, i, intent, i2, i3, i4, bundle);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class NonConfigurationInstances {
        Object custom;
        FragmentManagerNonConfig fragments;
        SimpleArrayMap<String, LoaderManager> loaders;

        NonConfigurationInstances() {
        }
    }

    @Override // android.app.Activity
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        this.mFragments.noteStateNotSaved();
        int requestIndex = requestCode >> 16;
        if (requestIndex != 0) {
            int requestIndex2 = requestIndex - 1;
            String who = this.mPendingFragmentActivityResults.get(requestIndex2);
            this.mPendingFragmentActivityResults.remove(requestIndex2);
            if (who == null) {
                Log.w(TAG, "Activity result delivered for unknown Fragment.");
                return;
            }
            Fragment targetFragment = this.mFragments.findFragmentByWho(who);
            if (targetFragment == null) {
                Log.w(TAG, "Activity result no fragment exists for who: " + who);
                return;
            } else {
                targetFragment.onActivityResult(65535 & requestCode, resultCode, data);
                return;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override // android.app.Activity
    public void onBackPressed() {
        if (!this.mFragments.mHost.mFragmentManager.popBackStackImmediate()) {
            onBackPressedNotHandled();
        }
    }

    public final void setSupportMediaController(MediaControllerCompat mediaController) {
        this.mMediaController = mediaController;
        if (Build.VERSION.SDK_INT < 21) {
            return;
        }
        setMediaController((MediaController) mediaController.mImpl.getMediaController());
    }

    public final MediaControllerCompat getSupportMediaController() {
        return this.mMediaController;
    }

    public void supportFinishAfterTransition() {
        ActivityCompat.finishAfterTransition(this);
    }

    public void setEnterSharedElementCallback(SharedElementCallback callback) {
        ActivityCompat.setEnterSharedElementCallback(this, callback);
    }

    public void setExitSharedElementCallback(SharedElementCallback listener) {
        ActivityCompat.setExitSharedElementCallback(this, listener);
    }

    public void supportPostponeEnterTransition() {
        ActivityCompat.postponeEnterTransition(this);
    }

    public void supportStartPostponedEnterTransition() {
        ActivityCompat.startPostponedEnterTransition(this);
    }

    @Override // android.app.Activity
    public void onMultiWindowModeChanged(boolean isInMultiWindowMode) {
        this.mFragments.mHost.mFragmentManager.dispatchMultiWindowModeChanged(isInMultiWindowMode);
    }

    @Override // android.app.Activity
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode) {
        this.mFragments.mHost.mFragmentManager.dispatchPictureInPictureModeChanged(isInPictureInPictureMode);
    }

    @Override // android.app.Activity, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        this.mFragments.mHost.mFragmentManager.dispatchConfigurationChanged(newConfig);
    }

    @Override // android.support.v4.app.BaseFragmentActivityDonut, android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        FragmentController fragmentController = this.mFragments;
        fragmentController.mHost.mFragmentManager.attachController(fragmentController.mHost, fragmentController.mHost, null);
        super.onCreate(savedInstanceState);
        NonConfigurationInstances nc = (NonConfigurationInstances) getLastNonConfigurationInstance();
        if (nc != null) {
            this.mFragments.mHost.mAllLoaderManagers = nc.loaders;
        }
        if (savedInstanceState != null) {
            Parcelable p = savedInstanceState.getParcelable(FRAGMENTS_TAG);
            this.mFragments.mHost.mFragmentManager.restoreAllState(p, nc != null ? nc.fragments : null);
            if (savedInstanceState.containsKey(NEXT_CANDIDATE_REQUEST_INDEX_TAG)) {
                this.mNextCandidateRequestIndex = savedInstanceState.getInt(NEXT_CANDIDATE_REQUEST_INDEX_TAG);
                int[] requestCodes = savedInstanceState.getIntArray(ALLOCATED_REQUEST_INDICIES_TAG);
                String[] fragmentWhos = savedInstanceState.getStringArray(REQUEST_FRAGMENT_WHO_TAG);
                if (requestCodes == null || fragmentWhos == null || requestCodes.length != fragmentWhos.length) {
                    Log.w(TAG, "Invalid requestCode mapping in savedInstanceState.");
                } else {
                    this.mPendingFragmentActivityResults = new SparseArrayCompat<>(requestCodes.length);
                    for (int i = 0; i < requestCodes.length; i++) {
                        this.mPendingFragmentActivityResults.put(requestCodes[i], fragmentWhos[i]);
                    }
                }
            }
        }
        if (this.mPendingFragmentActivityResults == null) {
            this.mPendingFragmentActivityResults = new SparseArrayCompat<>();
            this.mNextCandidateRequestIndex = 0;
        }
        this.mFragments.mHost.mFragmentManager.dispatchCreate();
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public boolean onCreatePanelMenu(int featureId, Menu menu) {
        if (featureId == 0) {
            boolean onCreatePanelMenu = super.onCreatePanelMenu(featureId, menu);
            FragmentController fragmentController = this.mFragments;
            boolean show = onCreatePanelMenu | fragmentController.mHost.mFragmentManager.dispatchCreateOptionsMenu(menu, getMenuInflater());
            if (Build.VERSION.SDK_INT < 11) {
                return true;
            }
            return show;
        }
        boolean show2 = super.onCreatePanelMenu(featureId, menu);
        return show2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.support.v4.app.BaseFragmentActivityDonut
    public final View dispatchFragmentsOnCreateView(View parent, String name, Context context, AttributeSet attrs) {
        return this.mFragments.mHost.mFragmentManager.onCreateView(parent, name, context, attrs);
    }

    @Override // android.app.Activity
    public void onDestroy() {
        super.onDestroy();
        doReallyStop(false);
        this.mFragments.mHost.mFragmentManager.dispatchDestroy();
        FragmentHostCallback<?> fragmentHostCallback = this.mFragments.mHost;
        if (fragmentHostCallback.mLoaderManager == null) {
            return;
        }
        fragmentHostCallback.mLoaderManager.doDestroy();
    }

    @Override // android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (Build.VERSION.SDK_INT >= 5 || keyCode != 4 || event.getRepeatCount() != 0) {
            return super.onKeyDown(keyCode, event);
        }
        onBackPressed();
        return true;
    }

    @Override // android.app.Activity, android.content.ComponentCallbacks
    public void onLowMemory() {
        super.onLowMemory();
        this.mFragments.mHost.mFragmentManager.dispatchLowMemory();
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (super.onMenuItemSelected(featureId, item)) {
            return true;
        }
        switch (featureId) {
            case 0:
                return this.mFragments.mHost.mFragmentManager.dispatchOptionsItemSelected(item);
            case 6:
                return this.mFragments.mHost.mFragmentManager.dispatchContextItemSelected(item);
            default:
                return false;
        }
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public void onPanelClosed(int featureId, Menu menu) {
        switch (featureId) {
            case 0:
                this.mFragments.mHost.mFragmentManager.dispatchOptionsMenuClosed(menu);
                break;
        }
        super.onPanelClosed(featureId, menu);
    }

    @Override // android.app.Activity
    public void onPause() {
        super.onPause();
        this.mResumed = false;
        if (this.mHandler.hasMessages(2)) {
            this.mHandler.removeMessages(2);
            onResumeFragments();
        }
        this.mFragments.mHost.mFragmentManager.moveToState$2563266(4);
    }

    @Override // android.app.Activity
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        this.mFragments.noteStateNotSaved();
    }

    @Override // android.app.Activity
    public void onStateNotSaved() {
        this.mFragments.noteStateNotSaved();
    }

    @Override // android.app.Activity
    public void onResume() {
        super.onResume();
        this.mHandler.sendEmptyMessage(2);
        this.mResumed = true;
        this.mFragments.execPendingActions();
    }

    @Override // android.app.Activity
    protected void onPostResume() {
        super.onPostResume();
        this.mHandler.removeMessages(2);
        onResumeFragments();
        this.mFragments.execPendingActions();
    }

    public void onResumeFragments() {
        this.mFragments.mHost.mFragmentManager.dispatchResume();
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public boolean onPreparePanel(int featureId, View view, Menu menu) {
        if (featureId != 0 || menu == null) {
            return super.onPreparePanel(featureId, view, menu);
        }
        if (this.mOptionsMenuInvalidated) {
            this.mOptionsMenuInvalidated = false;
            menu.clear();
            onCreatePanelMenu(featureId, menu);
        }
        return onPrepareOptionsPanel(view, menu) | this.mFragments.mHost.mFragmentManager.dispatchPrepareOptionsMenu(menu);
    }

    protected boolean onPrepareOptionsPanel(View view, Menu menu) {
        return super.onPreparePanel(0, view, menu);
    }

    @Override // android.app.Activity
    public final Object onRetainNonConfigurationInstance() {
        boolean z;
        if (this.mStopped) {
            doReallyStop(true);
        }
        Object custom = onRetainCustomNonConfigurationInstance();
        FragmentManagerNonConfig fragments = this.mFragments.mHost.mFragmentManager.retainNonConfig();
        FragmentHostCallback<?> fragmentHostCallback = this.mFragments.mHost;
        if (fragmentHostCallback.mAllLoaderManagers != null) {
            int size = fragmentHostCallback.mAllLoaderManagers.size();
            LoaderManagerImpl[] loaderManagerImplArr = new LoaderManagerImpl[size];
            for (int i = size - 1; i >= 0; i--) {
                loaderManagerImplArr[i] = (LoaderManagerImpl) fragmentHostCallback.mAllLoaderManagers.valueAt(i);
            }
            boolean z2 = fragmentHostCallback.mRetainLoaders;
            z = false;
            for (int i2 = 0; i2 < size; i2++) {
                LoaderManagerImpl loaderManagerImpl = loaderManagerImplArr[i2];
                if (!loaderManagerImpl.mRetaining && z2) {
                    if (!loaderManagerImpl.mStarted) {
                        loaderManagerImpl.doStart();
                    }
                    loaderManagerImpl.doRetain();
                }
                if (loaderManagerImpl.mRetaining) {
                    z = true;
                } else {
                    loaderManagerImpl.doDestroy();
                    fragmentHostCallback.mAllLoaderManagers.remove(loaderManagerImpl.mWho);
                }
            }
        } else {
            z = false;
        }
        SimpleArrayMap<String, LoaderManager> loaders = z ? fragmentHostCallback.mAllLoaderManagers : null;
        if (fragments == null && loaders == null && custom == null) {
            return null;
        }
        NonConfigurationInstances nci = new NonConfigurationInstances();
        nci.custom = custom;
        nci.fragments = fragments;
        nci.loaders = loaders;
        return nci;
    }

    @Override // android.app.Activity
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Parcelable p = this.mFragments.mHost.mFragmentManager.saveAllState();
        if (p != null) {
            outState.putParcelable(FRAGMENTS_TAG, p);
        }
        if (this.mPendingFragmentActivityResults.size() > 0) {
            outState.putInt(NEXT_CANDIDATE_REQUEST_INDEX_TAG, this.mNextCandidateRequestIndex);
            int[] requestCodes = new int[this.mPendingFragmentActivityResults.size()];
            String[] fragmentWhos = new String[this.mPendingFragmentActivityResults.size()];
            for (int i = 0; i < this.mPendingFragmentActivityResults.size(); i++) {
                requestCodes[i] = this.mPendingFragmentActivityResults.keyAt(i);
                fragmentWhos[i] = this.mPendingFragmentActivityResults.valueAt(i);
            }
            outState.putIntArray(ALLOCATED_REQUEST_INDICIES_TAG, requestCodes);
            outState.putStringArray(REQUEST_FRAGMENT_WHO_TAG, fragmentWhos);
        }
    }

    @Override // android.app.Activity
    public void onStart() {
        super.onStart();
        this.mStopped = false;
        this.mReallyStopped = false;
        this.mHandler.removeMessages(1);
        if (!this.mCreated) {
            this.mCreated = true;
            this.mFragments.mHost.mFragmentManager.dispatchActivityCreated();
        }
        this.mFragments.noteStateNotSaved();
        this.mFragments.execPendingActions();
        this.mFragments.doLoaderStart();
        this.mFragments.mHost.mFragmentManager.dispatchStart();
        FragmentHostCallback<?> fragmentHostCallback = this.mFragments.mHost;
        if (fragmentHostCallback.mAllLoaderManagers == null) {
            return;
        }
        int size = fragmentHostCallback.mAllLoaderManagers.size();
        LoaderManagerImpl[] loaderManagerImplArr = new LoaderManagerImpl[size];
        for (int i = size - 1; i >= 0; i--) {
            loaderManagerImplArr[i] = (LoaderManagerImpl) fragmentHostCallback.mAllLoaderManagers.valueAt(i);
        }
        for (int i2 = 0; i2 < size; i2++) {
            LoaderManagerImpl loaderManagerImpl = loaderManagerImplArr[i2];
            if (loaderManagerImpl.mRetaining) {
                if (LoaderManagerImpl.DEBUG) {
                    Log.v("LoaderManager", "Finished Retaining in " + loaderManagerImpl);
                }
                loaderManagerImpl.mRetaining = false;
                for (int size2 = loaderManagerImpl.mLoaders.size() - 1; size2 >= 0; size2--) {
                    LoaderManagerImpl.LoaderInfo valueAt = loaderManagerImpl.mLoaders.valueAt(size2);
                    if (valueAt.mRetaining) {
                        if (LoaderManagerImpl.DEBUG) {
                            Log.v("LoaderManager", "  Finished Retaining: " + valueAt);
                        }
                        valueAt.mRetaining = false;
                        if (valueAt.mStarted != valueAt.mRetainingStarted && !valueAt.mStarted) {
                            valueAt.stop();
                        }
                    }
                    if (valueAt.mStarted && valueAt.mHaveData && !valueAt.mReportNextStart) {
                        valueAt.callOnLoadFinished(valueAt.mLoader, valueAt.mData);
                    }
                }
            }
            loaderManagerImpl.doReportStart();
        }
    }

    @Override // android.app.Activity
    public void onStop() {
        super.onStop();
        this.mStopped = true;
        this.mHandler.sendEmptyMessage(1);
        this.mFragments.mHost.mFragmentManager.dispatchStop();
    }

    public Object onRetainCustomNonConfigurationInstance() {
        return null;
    }

    public Object getLastCustomNonConfigurationInstance() {
        NonConfigurationInstances nc = (NonConfigurationInstances) getLastNonConfigurationInstance();
        if (nc != null) {
            return nc.custom;
        }
        return null;
    }

    public void supportInvalidateOptionsMenu() {
        if (Build.VERSION.SDK_INT < 11) {
            this.mOptionsMenuInvalidated = true;
        } else {
            invalidateOptionsMenu();
        }
    }

    @Override // android.app.Activity
    public void dump(String prefix, FileDescriptor fd, PrintWriter writer, String[] args) {
        int i = Build.VERSION.SDK_INT;
        writer.print(prefix);
        writer.print("Local FragmentActivity ");
        writer.print(Integer.toHexString(System.identityHashCode(this)));
        writer.println(" State:");
        String innerPrefix = prefix + ThemeManager.NO_PRICE;
        writer.print(innerPrefix);
        writer.print("mCreated=");
        writer.print(this.mCreated);
        writer.print("mResumed=");
        writer.print(this.mResumed);
        writer.print(" mStopped=");
        writer.print(this.mStopped);
        writer.print(" mReallyStopped=");
        writer.println(this.mReallyStopped);
        FragmentHostCallback<?> fragmentHostCallback = this.mFragments.mHost;
        writer.print(innerPrefix);
        writer.print("mLoadersStarted=");
        writer.println(fragmentHostCallback.mLoadersStarted);
        if (fragmentHostCallback.mLoaderManager != null) {
            writer.print(innerPrefix);
            writer.print("Loader Manager ");
            writer.print(Integer.toHexString(System.identityHashCode(fragmentHostCallback.mLoaderManager)));
            writer.println(":");
            fragmentHostCallback.mLoaderManager.dump(innerPrefix + ThemeManager.NO_PRICE, fd, writer, args);
        }
        this.mFragments.mHost.mFragmentManager.dump(prefix, fd, writer, args);
        writer.print(prefix);
        writer.println("View Hierarchy:");
        dumpViewHierarchy(prefix + ThemeManager.NO_PRICE, writer, getWindow().getDecorView());
    }

    /* JADX WARN: Failed to find 'out' block for switch in B:39:0x00ee. Please report as an issue. */
    private static String viewToString(View view) {
        String pkgname;
        StringBuilder out = new StringBuilder(128);
        out.append(view.getClass().getName());
        out.append('{');
        out.append(Integer.toHexString(System.identityHashCode(view)));
        out.append(' ');
        switch (view.getVisibility()) {
            case 0:
                out.append('V');
                break;
            case 4:
                out.append('I');
                break;
            case 8:
                out.append('G');
                break;
            default:
                out.append('.');
                break;
        }
        out.append(view.isFocusable() ? 'F' : '.');
        out.append(view.isEnabled() ? 'E' : '.');
        out.append(view.willNotDraw() ? '.' : 'D');
        out.append(view.isHorizontalScrollBarEnabled() ? 'H' : '.');
        out.append(view.isVerticalScrollBarEnabled() ? 'V' : '.');
        out.append(view.isClickable() ? 'C' : '.');
        out.append(view.isLongClickable() ? 'L' : '.');
        out.append(' ');
        out.append(view.isFocused() ? 'F' : '.');
        out.append(view.isSelected() ? 'S' : '.');
        out.append(view.isPressed() ? 'P' : '.');
        out.append(' ');
        out.append(view.getLeft());
        out.append(',');
        out.append(view.getTop());
        out.append('-');
        out.append(view.getRight());
        out.append(',');
        out.append(view.getBottom());
        int id = view.getId();
        if (id != -1) {
            out.append(" #");
            out.append(Integer.toHexString(id));
            Resources r = view.getResources();
            if (id != 0 && r != null) {
                switch ((-16777216) & id) {
                    case 16777216:
                        pkgname = "android";
                        String typename = r.getResourceTypeName(id);
                        String entryname = r.getResourceEntryName(id);
                        out.append(XMLResultsHandler.SEP_SPACE);
                        out.append(pkgname);
                        out.append(":");
                        out.append(typename);
                        out.append("/");
                        out.append(entryname);
                        break;
                    case 2130706432:
                        pkgname = "app";
                        String typename2 = r.getResourceTypeName(id);
                        String entryname2 = r.getResourceEntryName(id);
                        out.append(XMLResultsHandler.SEP_SPACE);
                        out.append(pkgname);
                        out.append(":");
                        out.append(typename2);
                        out.append("/");
                        out.append(entryname2);
                        break;
                    default:
                        try {
                            pkgname = r.getResourcePackageName(id);
                            String typename22 = r.getResourceTypeName(id);
                            String entryname22 = r.getResourceEntryName(id);
                            out.append(XMLResultsHandler.SEP_SPACE);
                            out.append(pkgname);
                            out.append(":");
                            out.append(typename22);
                            out.append("/");
                            out.append(entryname22);
                            break;
                        } catch (Resources.NotFoundException e) {
                            break;
                        }
                }
            }
        }
        out.append("}");
        return out.toString();
    }

    private void dumpViewHierarchy(String prefix, PrintWriter writer, View view) {
        ViewGroup grp;
        int N;
        writer.print(prefix);
        if (view == null) {
            writer.println("null");
            return;
        }
        writer.println(viewToString(view));
        if ((view instanceof ViewGroup) && (N = (grp = (ViewGroup) view).getChildCount()) > 0) {
            String prefix2 = prefix + ThemeManager.NO_PRICE;
            for (int i = 0; i < N; i++) {
                dumpViewHierarchy(prefix2, writer, grp.getChildAt(i));
            }
        }
    }

    void doReallyStop(boolean retaining) {
        if (!this.mReallyStopped) {
            this.mReallyStopped = true;
            this.mRetaining = retaining;
            this.mHandler.removeMessages(1);
            onReallyStop();
            return;
        }
        if (retaining) {
            this.mFragments.doLoaderStart();
            this.mFragments.doLoaderStop(true);
        }
    }

    void onReallyStop() {
        this.mFragments.doLoaderStop(this.mRetaining);
        this.mFragments.mHost.mFragmentManager.moveToState$2563266(2);
    }

    public void onAttachFragment(Fragment fragment) {
    }

    public FragmentManager getSupportFragmentManager() {
        return this.mFragments.mHost.mFragmentManager;
    }

    public LoaderManager getSupportLoaderManager() {
        FragmentHostCallback<?> fragmentHostCallback = this.mFragments.mHost;
        if (fragmentHostCallback.mLoaderManager != null) {
            return fragmentHostCallback.mLoaderManager;
        }
        fragmentHostCallback.mCheckedForLoaderManager = true;
        fragmentHostCallback.mLoaderManager = fragmentHostCallback.getLoaderManager("(root)", fragmentHostCallback.mLoadersStarted, true);
        return fragmentHostCallback.mLoaderManager;
    }

    @Override // android.app.Activity
    public void startActivityForResult(Intent intent, int requestCode) {
        if (!this.mStartedActivityFromFragment && requestCode != -1) {
            checkForValidRequestCode(requestCode);
        }
        super.startActivityForResult(intent, requestCode);
    }

    @Override // android.support.v4.app.ActivityCompatApi23.RequestPermissionsRequestCodeValidator
    public final void validateRequestPermissionsRequestCode(int requestCode) {
        if (!this.mRequestedPermissionsFromFragment && requestCode != -1) {
            checkForValidRequestCode(requestCode);
        }
    }

    @Override // android.app.Activity, android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        int index = (requestCode >> 16) & 65535;
        if (index != 0) {
            int index2 = index - 1;
            String who = this.mPendingFragmentActivityResults.get(index2);
            this.mPendingFragmentActivityResults.remove(index2);
            if (who == null) {
                Log.w(TAG, "Activity result delivered for unknown Fragment.");
                return;
            }
            Fragment frag = this.mFragments.findFragmentByWho(who);
            if (frag == null) {
                Log.w(TAG, "Activity result no fragment exists for who: " + who);
            } else {
                frag.onRequestPermissionsResult(requestCode & 65535, permissions, grantResults);
            }
        }
    }

    public void startActivityFromFragment(Fragment fragment, Intent intent, int requestCode) {
        startActivityFromFragment(fragment, intent, requestCode, (Bundle) null);
    }

    public void startActivityFromFragment(Fragment fragment, Intent intent, int requestCode, Bundle options) {
        this.mStartedActivityFromFragment = true;
        try {
            if (requestCode == -1) {
                ActivityCompat.startActivityForResult(this, intent, -1, options);
            } else {
                checkForValidRequestCode(requestCode);
                int requestIndex = allocateRequestIndex(fragment);
                ActivityCompat.startActivityForResult(this, intent, ((requestIndex + 1) << 16) + (65535 & requestCode), options);
                this.mStartedActivityFromFragment = false;
            }
        } finally {
            this.mStartedActivityFromFragment = false;
        }
    }

    public void startIntentSenderFromFragment(Fragment fragment, IntentSender intent, int requestCode, Intent fillInIntent, int flagsMask, int flagsValues, int extraFlags, Bundle options) throws IntentSender.SendIntentException {
        this.mStartedIntentSenderFromFragment = true;
        try {
            if (requestCode == -1) {
                ActivityCompat.startIntentSenderForResult(this, intent, requestCode, fillInIntent, flagsMask, flagsValues, extraFlags, options);
            } else {
                checkForValidRequestCode(requestCode);
                int requestIndex = allocateRequestIndex(fragment);
                ActivityCompat.startIntentSenderForResult(this, intent, ((requestIndex + 1) << 16) + (65535 & requestCode), fillInIntent, flagsMask, flagsValues, extraFlags, options);
                this.mStartedIntentSenderFromFragment = false;
            }
        } finally {
            this.mStartedIntentSenderFromFragment = false;
        }
    }

    private int allocateRequestIndex(Fragment fragment) {
        if (this.mPendingFragmentActivityResults.size() >= MAX_NUM_PENDING_FRAGMENT_ACTIVITY_RESULTS) {
            throw new IllegalStateException("Too many pending Fragment activity results.");
        }
        while (true) {
            SparseArrayCompat<String> sparseArrayCompat = this.mPendingFragmentActivityResults;
            int i = this.mNextCandidateRequestIndex;
            if (sparseArrayCompat.mGarbage) {
                sparseArrayCompat.gc();
            }
            if (ContainerHelpers.binarySearch(sparseArrayCompat.mKeys, sparseArrayCompat.mSize, i) >= 0) {
                this.mNextCandidateRequestIndex = (this.mNextCandidateRequestIndex + 1) % MAX_NUM_PENDING_FRAGMENT_ACTIVITY_RESULTS;
            } else {
                int requestIndex = this.mNextCandidateRequestIndex;
                this.mPendingFragmentActivityResults.put(requestIndex, fragment.mWho);
                this.mNextCandidateRequestIndex = (this.mNextCandidateRequestIndex + 1) % MAX_NUM_PENDING_FRAGMENT_ACTIVITY_RESULTS;
                return requestIndex;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void requestPermissionsFromFragment(Fragment fragment, String[] permissions, int requestCode) {
        if (requestCode == -1) {
            ActivityCompat.requestPermissions(this, permissions, requestCode);
            return;
        }
        checkForValidRequestCode(requestCode);
        try {
            this.mRequestedPermissionsFromFragment = true;
            int requestIndex = allocateRequestIndex(fragment);
            ActivityCompat.requestPermissions(this, permissions, ((requestIndex + 1) << 16) + (65535 & requestCode));
        } finally {
            this.mRequestedPermissionsFromFragment = false;
        }
    }

    /* loaded from: classes.dex */
    class HostCallbacks extends FragmentHostCallback<FragmentActivity> {
        public HostCallbacks() {
            super(FragmentActivity.this);
        }

        @Override // android.support.v4.app.FragmentHostCallback
        public final void onDump$ec96877(String prefix, PrintWriter writer, String[] args) {
            FragmentActivity.this.dump(prefix, null, writer, args);
        }

        @Override // android.support.v4.app.FragmentHostCallback
        public final boolean onShouldSaveFragmentState$6585081f() {
            return !FragmentActivity.this.isFinishing();
        }

        @Override // android.support.v4.app.FragmentHostCallback
        public final LayoutInflater onGetLayoutInflater() {
            return FragmentActivity.this.getLayoutInflater().cloneInContext(FragmentActivity.this);
        }

        @Override // android.support.v4.app.FragmentHostCallback
        public final void onSupportInvalidateOptionsMenu() {
            FragmentActivity.this.supportInvalidateOptionsMenu();
        }

        @Override // android.support.v4.app.FragmentHostCallback
        public final void onStartActivityFromFragment(Fragment fragment, Intent intent, int requestCode, Bundle options) {
            FragmentActivity.this.startActivityFromFragment(fragment, intent, requestCode, options);
        }

        @Override // android.support.v4.app.FragmentHostCallback
        public final void onStartIntentSenderFromFragment(Fragment fragment, IntentSender intent, int requestCode, Intent fillInIntent, int flagsMask, int flagsValues, int extraFlags, Bundle options) throws IntentSender.SendIntentException {
            FragmentActivity.this.startIntentSenderFromFragment(fragment, intent, requestCode, fillInIntent, flagsMask, flagsValues, extraFlags, options);
        }

        @Override // android.support.v4.app.FragmentHostCallback
        public final void onRequestPermissionsFromFragment(Fragment fragment, String[] permissions, int requestCode) {
            FragmentActivity.this.requestPermissionsFromFragment(fragment, permissions, requestCode);
        }

        @Override // android.support.v4.app.FragmentHostCallback
        public final boolean onShouldShowRequestPermissionRationale(String permission) {
            return ActivityCompat.shouldShowRequestPermissionRationale(FragmentActivity.this, permission);
        }

        @Override // android.support.v4.app.FragmentHostCallback
        public final boolean onHasWindowAnimations() {
            return FragmentActivity.this.getWindow() != null;
        }

        @Override // android.support.v4.app.FragmentHostCallback
        public final int onGetWindowAnimations() {
            Window w = FragmentActivity.this.getWindow();
            if (w == null) {
                return 0;
            }
            return w.getAttributes().windowAnimations;
        }

        @Override // android.support.v4.app.FragmentHostCallback
        public final void onAttachFragment(Fragment fragment) {
            FragmentActivity.this.onAttachFragment(fragment);
        }

        @Override // android.support.v4.app.FragmentHostCallback, android.support.v4.app.FragmentContainer
        public final View onFindViewById(int id) {
            return FragmentActivity.this.findViewById(id);
        }

        @Override // android.support.v4.app.FragmentHostCallback, android.support.v4.app.FragmentContainer
        public final boolean onHasView() {
            Window w = FragmentActivity.this.getWindow();
            return (w == null || w.peekDecorView() == null) ? false : true;
        }

        @Override // android.support.v4.app.FragmentHostCallback
        public final /* bridge */ /* synthetic */ FragmentActivity onGetHost() {
            return FragmentActivity.this;
        }
    }
}
