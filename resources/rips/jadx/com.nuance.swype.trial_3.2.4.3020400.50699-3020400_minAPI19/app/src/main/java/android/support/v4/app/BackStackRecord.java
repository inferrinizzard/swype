package android.support.v4.app;

import android.os.Build;
import android.os.Looper;
import android.support.v4.app.FragmentTransitionCompat21;
import android.support.v4.util.ArrayMap;
import android.support.v4.util.LogWriter;
import android.support.v4.util.MapCollections;
import android.transition.Transition;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import com.nuance.nmsp.client.util.internal.dictationresult.parser.xml.xmlResults.XMLResultsHandler;
import com.nuance.swype.input.ThemeManager;
import java.io.PrintWriter;
import java.util.ArrayList;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class BackStackRecord extends FragmentTransaction implements Runnable {
    static final boolean SUPPORTS_TRANSITIONS;
    boolean mAddToBackStack;
    int mBreadCrumbShortTitleRes;
    CharSequence mBreadCrumbShortTitleText;
    int mBreadCrumbTitleRes;
    CharSequence mBreadCrumbTitleText;
    boolean mCommitted;
    int mEnterAnim;
    int mExitAnim;
    Op mHead;
    final FragmentManagerImpl mManager;
    String mName;
    int mNumOp;
    int mPopEnterAnim;
    int mPopExitAnim;
    ArrayList<String> mSharedElementSourceNames;
    ArrayList<String> mSharedElementTargetNames;
    Op mTail;
    int mTransition;
    int mTransitionStyle;
    boolean mAllowAddToBackStack = true;
    int mIndex = -1;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class Op {
        int cmd;
        int enterAnim;
        int exitAnim;
        Fragment fragment;
        Op next;
        int popEnterAnim;
        int popExitAnim;
        Op prev;
        ArrayList<Fragment> removed;
    }

    static {
        SUPPORTS_TRANSITIONS = Build.VERSION.SDK_INT >= 21;
    }

    public final String toString() {
        StringBuilder sb = new StringBuilder(128);
        sb.append("BackStackEntry{");
        sb.append(Integer.toHexString(System.identityHashCode(this)));
        if (this.mIndex >= 0) {
            sb.append(" #");
            sb.append(this.mIndex);
        }
        if (this.mName != null) {
            sb.append(XMLResultsHandler.SEP_SPACE);
            sb.append(this.mName);
        }
        sb.append("}");
        return sb.toString();
    }

    public final void dump$ec96877(String prefix, PrintWriter writer) {
        dump(prefix, writer, true);
    }

    public final void dump(String prefix, PrintWriter writer, boolean full) {
        String cmdStr;
        if (full) {
            writer.print(prefix);
            writer.print("mName=");
            writer.print(this.mName);
            writer.print(" mIndex=");
            writer.print(this.mIndex);
            writer.print(" mCommitted=");
            writer.println(this.mCommitted);
            if (this.mTransition != 0) {
                writer.print(prefix);
                writer.print("mTransition=#");
                writer.print(Integer.toHexString(this.mTransition));
                writer.print(" mTransitionStyle=#");
                writer.println(Integer.toHexString(this.mTransitionStyle));
            }
            if (this.mEnterAnim != 0 || this.mExitAnim != 0) {
                writer.print(prefix);
                writer.print("mEnterAnim=#");
                writer.print(Integer.toHexString(this.mEnterAnim));
                writer.print(" mExitAnim=#");
                writer.println(Integer.toHexString(this.mExitAnim));
            }
            if (this.mPopEnterAnim != 0 || this.mPopExitAnim != 0) {
                writer.print(prefix);
                writer.print("mPopEnterAnim=#");
                writer.print(Integer.toHexString(this.mPopEnterAnim));
                writer.print(" mPopExitAnim=#");
                writer.println(Integer.toHexString(this.mPopExitAnim));
            }
            if (this.mBreadCrumbTitleRes != 0 || this.mBreadCrumbTitleText != null) {
                writer.print(prefix);
                writer.print("mBreadCrumbTitleRes=#");
                writer.print(Integer.toHexString(this.mBreadCrumbTitleRes));
                writer.print(" mBreadCrumbTitleText=");
                writer.println(this.mBreadCrumbTitleText);
            }
            if (this.mBreadCrumbShortTitleRes != 0 || this.mBreadCrumbShortTitleText != null) {
                writer.print(prefix);
                writer.print("mBreadCrumbShortTitleRes=#");
                writer.print(Integer.toHexString(this.mBreadCrumbShortTitleRes));
                writer.print(" mBreadCrumbShortTitleText=");
                writer.println(this.mBreadCrumbShortTitleText);
            }
        }
        if (this.mHead != null) {
            writer.print(prefix);
            writer.println("Operations:");
            String innerPrefix = prefix + "    ";
            Op op = this.mHead;
            int num = 0;
            while (op != null) {
                switch (op.cmd) {
                    case 0:
                        cmdStr = "NULL";
                        break;
                    case 1:
                        cmdStr = "ADD";
                        break;
                    case 2:
                        cmdStr = "REPLACE";
                        break;
                    case 3:
                        cmdStr = "REMOVE";
                        break;
                    case 4:
                        cmdStr = "HIDE";
                        break;
                    case 5:
                        cmdStr = "SHOW";
                        break;
                    case 6:
                        cmdStr = "DETACH";
                        break;
                    case 7:
                        cmdStr = "ATTACH";
                        break;
                    default:
                        cmdStr = "cmd=" + op.cmd;
                        break;
                }
                writer.print(prefix);
                writer.print("  Op #");
                writer.print(num);
                writer.print(": ");
                writer.print(cmdStr);
                writer.print(XMLResultsHandler.SEP_SPACE);
                writer.println(op.fragment);
                if (full) {
                    if (op.enterAnim != 0 || op.exitAnim != 0) {
                        writer.print(prefix);
                        writer.print("enterAnim=#");
                        writer.print(Integer.toHexString(op.enterAnim));
                        writer.print(" exitAnim=#");
                        writer.println(Integer.toHexString(op.exitAnim));
                    }
                    if (op.popEnterAnim != 0 || op.popExitAnim != 0) {
                        writer.print(prefix);
                        writer.print("popEnterAnim=#");
                        writer.print(Integer.toHexString(op.popEnterAnim));
                        writer.print(" popExitAnim=#");
                        writer.println(Integer.toHexString(op.popExitAnim));
                    }
                }
                if (op.removed != null && op.removed.size() > 0) {
                    for (int i = 0; i < op.removed.size(); i++) {
                        writer.print(innerPrefix);
                        if (op.removed.size() == 1) {
                            writer.print("Removed: ");
                        } else {
                            if (i == 0) {
                                writer.println("Removed:");
                            }
                            writer.print(innerPrefix);
                            writer.print("  #");
                            writer.print(i);
                            writer.print(": ");
                        }
                        writer.println(op.removed.get(i));
                    }
                }
                op = op.next;
                num++;
            }
        }
    }

    public BackStackRecord(FragmentManagerImpl manager) {
        this.mManager = manager;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void addOp(Op op) {
        if (this.mHead == null) {
            this.mTail = op;
            this.mHead = op;
        } else {
            op.prev = this.mTail;
            this.mTail.next = op;
            this.mTail = op;
        }
        op.enterAnim = this.mEnterAnim;
        op.exitAnim = this.mExitAnim;
        op.popEnterAnim = this.mPopEnterAnim;
        op.popExitAnim = this.mPopExitAnim;
        this.mNumOp++;
    }

    @Override // android.support.v4.app.FragmentTransaction
    public final FragmentTransaction add(Fragment fragment, String tag) {
        doAddOp(0, fragment, tag, 1);
        return this;
    }

    @Override // android.support.v4.app.FragmentTransaction
    public final FragmentTransaction add(int containerViewId, Fragment fragment) {
        doAddOp(containerViewId, fragment, null, 1);
        return this;
    }

    @Override // android.support.v4.app.FragmentTransaction
    public final FragmentTransaction add(int containerViewId, Fragment fragment, String tag) {
        doAddOp(containerViewId, fragment, tag, 1);
        return this;
    }

    private void doAddOp(int containerViewId, Fragment fragment, String tag, int opcmd) {
        fragment.mFragmentManager = this.mManager;
        if (tag != null) {
            if (fragment.mTag != null && !tag.equals(fragment.mTag)) {
                throw new IllegalStateException("Can't change tag of fragment " + fragment + ": was " + fragment.mTag + " now " + tag);
            }
            fragment.mTag = tag;
        }
        if (containerViewId != 0) {
            if (containerViewId == -1) {
                throw new IllegalArgumentException("Can't add fragment " + fragment + " with tag " + tag + " to container view with no id");
            }
            if (fragment.mFragmentId != 0 && fragment.mFragmentId != containerViewId) {
                throw new IllegalStateException("Can't change container ID of fragment " + fragment + ": was " + fragment.mFragmentId + " now " + containerViewId);
            }
            fragment.mFragmentId = containerViewId;
            fragment.mContainerId = containerViewId;
        }
        Op op = new Op();
        op.cmd = opcmd;
        op.fragment = fragment;
        addOp(op);
    }

    @Override // android.support.v4.app.FragmentTransaction
    public final FragmentTransaction remove(Fragment fragment) {
        Op op = new Op();
        op.cmd = 3;
        op.fragment = fragment;
        addOp(op);
        return this;
    }

    @Override // android.support.v4.app.FragmentTransaction
    public final FragmentTransaction detach(Fragment fragment) {
        Op op = new Op();
        op.cmd = 6;
        op.fragment = fragment;
        addOp(op);
        return this;
    }

    @Override // android.support.v4.app.FragmentTransaction
    public final FragmentTransaction attach(Fragment fragment) {
        Op op = new Op();
        op.cmd = 7;
        op.fragment = fragment;
        addOp(op);
        return this;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void bumpBackStackNesting(int amt) {
        if (this.mAddToBackStack) {
            if (FragmentManagerImpl.DEBUG) {
                Log.v("FragmentManager", "Bump nesting in " + this + " by " + amt);
            }
            for (Op op = this.mHead; op != null; op = op.next) {
                if (op.fragment != null) {
                    op.fragment.mBackStackNesting += amt;
                    if (FragmentManagerImpl.DEBUG) {
                        Log.v("FragmentManager", "Bump nesting of " + op.fragment + " to " + op.fragment.mBackStackNesting);
                    }
                }
                if (op.removed != null) {
                    for (int i = op.removed.size() - 1; i >= 0; i--) {
                        Fragment r = op.removed.get(i);
                        r.mBackStackNesting += amt;
                        if (FragmentManagerImpl.DEBUG) {
                            Log.v("FragmentManager", "Bump nesting of " + r + " to " + r.mBackStackNesting);
                        }
                    }
                }
            }
        }
    }

    @Override // android.support.v4.app.FragmentTransaction
    public final int commit() {
        return commitInternal(false);
    }

    @Override // android.support.v4.app.FragmentTransaction
    public final int commitAllowingStateLoss() {
        return commitInternal(true);
    }

    private int commitInternal(boolean allowStateLoss) {
        if (this.mCommitted) {
            throw new IllegalStateException("commit already called");
        }
        if (FragmentManagerImpl.DEBUG) {
            Log.v("FragmentManager", "Commit: " + this);
            LogWriter logw = new LogWriter("FragmentManager");
            PrintWriter pw = new PrintWriter(logw);
            dump$ec96877(ThemeManager.NO_PRICE, pw);
        }
        this.mCommitted = true;
        if (this.mAddToBackStack) {
            this.mIndex = this.mManager.allocBackStackIndex(this);
        } else {
            this.mIndex = -1;
        }
        this.mManager.enqueueAction(this, allowStateLoss);
        return this.mIndex;
    }

    @Override // java.lang.Runnable
    public final void run() {
        if (FragmentManagerImpl.DEBUG) {
            Log.v("FragmentManager", "Run: " + this);
        }
        if (this.mAddToBackStack && this.mIndex < 0) {
            throw new IllegalStateException("addToBackStack() called after commit()");
        }
        bumpBackStackNesting(1);
        TransitionState state = null;
        if (SUPPORTS_TRANSITIONS && this.mManager.mCurState > 0) {
            SparseArray<Fragment> firstOutFragments = new SparseArray<>();
            SparseArray<Fragment> lastInFragments = new SparseArray<>();
            calculateFragments(firstOutFragments, lastInFragments);
            state = beginTransition(firstOutFragments, lastInFragments, false);
        }
        int transitionStyle = state != null ? 0 : this.mTransitionStyle;
        int transition = state != null ? 0 : this.mTransition;
        for (Op op = this.mHead; op != null; op = op.next) {
            int enterAnim = state != null ? 0 : op.enterAnim;
            int exitAnim = state != null ? 0 : op.exitAnim;
            switch (op.cmd) {
                case 1:
                    Fragment f = op.fragment;
                    f.mNextAnim = enterAnim;
                    this.mManager.addFragment(f, false);
                    break;
                case 2:
                    Fragment f2 = op.fragment;
                    int containerId = f2.mContainerId;
                    if (this.mManager.mAdded != null) {
                        for (int i = this.mManager.mAdded.size() - 1; i >= 0; i--) {
                            Fragment old = this.mManager.mAdded.get(i);
                            if (FragmentManagerImpl.DEBUG) {
                                Log.v("FragmentManager", "OP_REPLACE: adding=" + f2 + " old=" + old);
                            }
                            if (old.mContainerId == containerId) {
                                if (old == f2) {
                                    f2 = null;
                                    op.fragment = null;
                                } else {
                                    if (op.removed == null) {
                                        op.removed = new ArrayList<>();
                                    }
                                    op.removed.add(old);
                                    old.mNextAnim = exitAnim;
                                    if (this.mAddToBackStack) {
                                        old.mBackStackNesting++;
                                        if (FragmentManagerImpl.DEBUG) {
                                            Log.v("FragmentManager", "Bump nesting of " + old + " to " + old.mBackStackNesting);
                                        }
                                    }
                                    this.mManager.removeFragment(old, transition, transitionStyle);
                                }
                            }
                        }
                    }
                    if (f2 != null) {
                        f2.mNextAnim = enterAnim;
                        this.mManager.addFragment(f2, false);
                        break;
                    } else {
                        break;
                    }
                case 3:
                    Fragment f3 = op.fragment;
                    f3.mNextAnim = exitAnim;
                    this.mManager.removeFragment(f3, transition, transitionStyle);
                    break;
                case 4:
                    Fragment f4 = op.fragment;
                    f4.mNextAnim = exitAnim;
                    this.mManager.hideFragment(f4, transition, transitionStyle);
                    break;
                case 5:
                    Fragment f5 = op.fragment;
                    f5.mNextAnim = enterAnim;
                    this.mManager.showFragment(f5, transition, transitionStyle);
                    break;
                case 6:
                    Fragment f6 = op.fragment;
                    f6.mNextAnim = exitAnim;
                    this.mManager.detachFragment(f6, transition, transitionStyle);
                    break;
                case 7:
                    Fragment f7 = op.fragment;
                    f7.mNextAnim = enterAnim;
                    this.mManager.attachFragment(f7, transition, transitionStyle);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown cmd: " + op.cmd);
            }
        }
        this.mManager.moveToState(this.mManager.mCurState, transition, transitionStyle, true);
        if (this.mAddToBackStack) {
            FragmentManagerImpl fragmentManagerImpl = this.mManager;
            if (fragmentManagerImpl.mBackStack == null) {
                fragmentManagerImpl.mBackStack = new ArrayList<>();
            }
            fragmentManagerImpl.mBackStack.add(this);
            fragmentManagerImpl.reportBackStackChanged();
        }
    }

    private static void setFirstOut(SparseArray<Fragment> firstOutFragments, SparseArray<Fragment> lastInFragments, Fragment fragment) {
        int containerId;
        if (fragment != null && (containerId = fragment.mContainerId) != 0 && !fragment.isHidden()) {
            if (fragment.isAdded() && fragment.getView() != null && firstOutFragments.get(containerId) == null) {
                firstOutFragments.put(containerId, fragment);
            }
            if (lastInFragments.get(containerId) == fragment) {
                lastInFragments.remove(containerId);
            }
        }
    }

    private void setLastIn(SparseArray<Fragment> firstOutFragments, SparseArray<Fragment> lastInFragments, Fragment fragment) {
        if (fragment != null) {
            int containerId = fragment.mContainerId;
            if (containerId != 0) {
                if (!fragment.isAdded()) {
                    lastInFragments.put(containerId, fragment);
                }
                if (firstOutFragments.get(containerId) == fragment) {
                    firstOutFragments.remove(containerId);
                }
            }
            if (fragment.mState <= 0 && this.mManager.mCurState > 0) {
                this.mManager.makeActive(fragment);
                this.mManager.moveToState(fragment, 1, 0, 0, false);
            }
        }
    }

    private void calculateFragments(SparseArray<Fragment> firstOutFragments, SparseArray<Fragment> lastInFragments) {
        if (this.mManager.mContainer.onHasView()) {
            for (Op op = this.mHead; op != null; op = op.next) {
                switch (op.cmd) {
                    case 1:
                        setLastIn(firstOutFragments, lastInFragments, op.fragment);
                        break;
                    case 2:
                        Fragment f = op.fragment;
                        if (this.mManager.mAdded != null) {
                            for (int i = 0; i < this.mManager.mAdded.size(); i++) {
                                Fragment old = this.mManager.mAdded.get(i);
                                if (f == null || old.mContainerId == f.mContainerId) {
                                    if (old == f) {
                                        f = null;
                                        lastInFragments.remove(old.mContainerId);
                                    } else {
                                        setFirstOut(firstOutFragments, lastInFragments, old);
                                    }
                                }
                            }
                        }
                        setLastIn(firstOutFragments, lastInFragments, op.fragment);
                        break;
                    case 3:
                        setFirstOut(firstOutFragments, lastInFragments, op.fragment);
                        break;
                    case 4:
                        setFirstOut(firstOutFragments, lastInFragments, op.fragment);
                        break;
                    case 5:
                        setLastIn(firstOutFragments, lastInFragments, op.fragment);
                        break;
                    case 6:
                        setFirstOut(firstOutFragments, lastInFragments, op.fragment);
                        break;
                    case 7:
                        setLastIn(firstOutFragments, lastInFragments, op.fragment);
                        break;
                }
            }
        }
    }

    public final void calculateBackFragments(SparseArray<Fragment> firstOutFragments, SparseArray<Fragment> lastInFragments) {
        if (this.mManager.mContainer.onHasView()) {
            for (Op op = this.mTail; op != null; op = op.prev) {
                switch (op.cmd) {
                    case 1:
                        setFirstOut(firstOutFragments, lastInFragments, op.fragment);
                        break;
                    case 2:
                        if (op.removed != null) {
                            for (int i = op.removed.size() - 1; i >= 0; i--) {
                                setLastIn(firstOutFragments, lastInFragments, op.removed.get(i));
                            }
                        }
                        setFirstOut(firstOutFragments, lastInFragments, op.fragment);
                        break;
                    case 3:
                        setLastIn(firstOutFragments, lastInFragments, op.fragment);
                        break;
                    case 4:
                        setLastIn(firstOutFragments, lastInFragments, op.fragment);
                        break;
                    case 5:
                        setFirstOut(firstOutFragments, lastInFragments, op.fragment);
                        break;
                    case 6:
                        setLastIn(firstOutFragments, lastInFragments, op.fragment);
                        break;
                    case 7:
                        setFirstOut(firstOutFragments, lastInFragments, op.fragment);
                        break;
                }
            }
        }
    }

    public final TransitionState popFromBackStack(boolean doStateMove, TransitionState state, SparseArray<Fragment> firstOutFragments, SparseArray<Fragment> lastInFragments) {
        if (FragmentManagerImpl.DEBUG) {
            Log.v("FragmentManager", "popFromBackStack: " + this);
            LogWriter logw = new LogWriter("FragmentManager");
            PrintWriter pw = new PrintWriter(logw);
            dump$ec96877(ThemeManager.NO_PRICE, pw);
        }
        if (SUPPORTS_TRANSITIONS && this.mManager.mCurState > 0) {
            if (state == null) {
                if (firstOutFragments.size() != 0 || lastInFragments.size() != 0) {
                    state = beginTransition(firstOutFragments, lastInFragments, true);
                }
            } else if (!doStateMove) {
                setNameOverrides(state, this.mSharedElementTargetNames, this.mSharedElementSourceNames);
            }
        }
        bumpBackStackNesting(-1);
        int transitionStyle = state != null ? 0 : this.mTransitionStyle;
        int transition = state != null ? 0 : this.mTransition;
        for (Op op = this.mTail; op != null; op = op.prev) {
            int popEnterAnim = state != null ? 0 : op.popEnterAnim;
            int popExitAnim = state != null ? 0 : op.popExitAnim;
            switch (op.cmd) {
                case 1:
                    Fragment f = op.fragment;
                    f.mNextAnim = popExitAnim;
                    this.mManager.removeFragment(f, FragmentManagerImpl.reverseTransit(transition), transitionStyle);
                    break;
                case 2:
                    Fragment f2 = op.fragment;
                    if (f2 != null) {
                        f2.mNextAnim = popExitAnim;
                        this.mManager.removeFragment(f2, FragmentManagerImpl.reverseTransit(transition), transitionStyle);
                    }
                    if (op.removed != null) {
                        for (int i = 0; i < op.removed.size(); i++) {
                            Fragment old = op.removed.get(i);
                            old.mNextAnim = popEnterAnim;
                            this.mManager.addFragment(old, false);
                        }
                        break;
                    } else {
                        break;
                    }
                case 3:
                    Fragment f3 = op.fragment;
                    f3.mNextAnim = popEnterAnim;
                    this.mManager.addFragment(f3, false);
                    break;
                case 4:
                    Fragment f4 = op.fragment;
                    f4.mNextAnim = popEnterAnim;
                    this.mManager.showFragment(f4, FragmentManagerImpl.reverseTransit(transition), transitionStyle);
                    break;
                case 5:
                    Fragment f5 = op.fragment;
                    f5.mNextAnim = popExitAnim;
                    this.mManager.hideFragment(f5, FragmentManagerImpl.reverseTransit(transition), transitionStyle);
                    break;
                case 6:
                    Fragment f6 = op.fragment;
                    f6.mNextAnim = popEnterAnim;
                    this.mManager.attachFragment(f6, FragmentManagerImpl.reverseTransit(transition), transitionStyle);
                    break;
                case 7:
                    Fragment f7 = op.fragment;
                    f7.mNextAnim = popEnterAnim;
                    this.mManager.detachFragment(f7, FragmentManagerImpl.reverseTransit(transition), transitionStyle);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown cmd: " + op.cmd);
            }
        }
        if (doStateMove) {
            this.mManager.moveToState(this.mManager.mCurState, FragmentManagerImpl.reverseTransit(transition), transitionStyle, true);
            state = null;
        }
        if (this.mIndex >= 0) {
            FragmentManagerImpl fragmentManagerImpl = this.mManager;
            int i2 = this.mIndex;
            synchronized (fragmentManagerImpl) {
                fragmentManagerImpl.mBackStackIndices.set(i2, null);
                if (fragmentManagerImpl.mAvailBackStackIndices == null) {
                    fragmentManagerImpl.mAvailBackStackIndices = new ArrayList<>();
                }
                if (FragmentManagerImpl.DEBUG) {
                    Log.v("FragmentManager", "Freeing back stack index " + i2);
                }
                fragmentManagerImpl.mAvailBackStackIndices.add(Integer.valueOf(i2));
            }
            this.mIndex = -1;
        }
        return state;
    }

    private TransitionState beginTransition(SparseArray<Fragment> firstOutFragments, SparseArray<Fragment> lastInFragments, boolean isBack) {
        TransitionState state = new TransitionState();
        state.nonExistentView = new View(this.mManager.mHost.mContext);
        boolean anyTransitionStarted = false;
        for (int i = 0; i < firstOutFragments.size(); i++) {
            if (configureTransitions(firstOutFragments.keyAt(i), state, isBack, firstOutFragments, lastInFragments)) {
                anyTransitionStarted = true;
            }
        }
        for (int i2 = 0; i2 < lastInFragments.size(); i2++) {
            int containerId = lastInFragments.keyAt(i2);
            if (firstOutFragments.get(containerId) == null && configureTransitions(containerId, state, isBack, firstOutFragments, lastInFragments)) {
                anyTransitionStarted = true;
            }
        }
        if (!anyTransitionStarted) {
            return null;
        }
        return state;
    }

    private static Object captureExitingViews(Object exitTransition, Fragment outFragment, ArrayList<View> exitingViews, ArrayMap<String, View> namedViews, View nonExistentView) {
        if (exitTransition != null) {
            View view = outFragment.getView();
            if (exitTransition == null) {
                return exitTransition;
            }
            FragmentTransitionCompat21.captureTransitioningViews(exitingViews, view);
            if (namedViews != null) {
                exitingViews.removeAll(namedViews.values());
            }
            if (exitingViews.isEmpty()) {
                return null;
            }
            exitingViews.add(nonExistentView);
            FragmentTransitionCompat21.addTargets((Transition) exitTransition, exitingViews);
            return exitTransition;
        }
        return exitTransition;
    }

    private ArrayMap<String, View> remapSharedElements(TransitionState state, Fragment outFragment, boolean isBack) {
        ArrayMap<String, View> namedViews = new ArrayMap<>();
        if (this.mSharedElementSourceNames != null) {
            FragmentTransitionCompat21.findNamedViews(namedViews, outFragment.getView());
            if (!isBack) {
                namedViews = remapNames(this.mSharedElementSourceNames, this.mSharedElementTargetNames, namedViews);
            } else {
                MapCollections.retainAllHelper(namedViews, this.mSharedElementTargetNames);
            }
        }
        if (isBack) {
            if (outFragment.mEnterTransitionCallback != null) {
                SharedElementCallback sharedElementCallback = outFragment.mEnterTransitionCallback;
            }
            setBackNameOverrides(state, namedViews, false);
        } else {
            if (outFragment.mExitTransitionCallback != null) {
                SharedElementCallback sharedElementCallback2 = outFragment.mExitTransitionCallback;
            }
            setNameOverrides(state, namedViews, false);
        }
        return namedViews;
    }

    /*  JADX ERROR: JadxRuntimeException in pass: ProcessVariables
        jadx.core.utils.exceptions.JadxRuntimeException: Method arg registers not loaded: android.support.v4.app.FragmentTransitionCompat21.2.<init>(android.view.View, android.transition.Transition, android.view.View, android.support.v4.app.FragmentTransitionCompat21$ViewRetriever, java.util.Map, java.util.Map, java.util.ArrayList, android.transition.Transition):void, class status: GENERATED_AND_UNLOADED
        	at jadx.core.dex.nodes.MethodNode.getArgRegs(MethodNode.java:289)
        	at jadx.core.dex.visitors.regions.variables.ProcessVariables$1.isArgUnused(ProcessVariables.java:146)
        	at jadx.core.dex.visitors.regions.variables.ProcessVariables$1.lambda$isVarUnused$0(ProcessVariables.java:131)
        	at jadx.core.utils.ListUtils.allMatch(ListUtils.java:172)
        	at jadx.core.dex.visitors.regions.variables.ProcessVariables$1.isVarUnused(ProcessVariables.java:131)
        	at jadx.core.dex.visitors.regions.variables.ProcessVariables$1.processBlock(ProcessVariables.java:82)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:64)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$traverseInternal$0(DepthRegionTraversal.java:68)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:68)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$traverseInternal$0(DepthRegionTraversal.java:68)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:68)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$traverseInternal$0(DepthRegionTraversal.java:68)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:68)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$traverseInternal$0(DepthRegionTraversal.java:68)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:68)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$traverseInternal$0(DepthRegionTraversal.java:68)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:68)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverse(DepthRegionTraversal.java:19)
        	at jadx.core.dex.visitors.regions.variables.ProcessVariables.removeUnusedResults(ProcessVariables.java:73)
        	at jadx.core.dex.visitors.regions.variables.ProcessVariables.visit(ProcessVariables.java:48)
        */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r28v0 */
    /* JADX WARN: Type inference failed for: r28v1, types: [android.transition.TransitionSet] */
    /* JADX WARN: Type inference failed for: r28v2, types: [java.lang.Object] */
    /* JADX WARN: Type inference failed for: r28v3, types: [android.transition.TransitionSet] */
    private boolean configureTransitions(final int r45, final android.support.v4.app.BackStackRecord.TransitionState r46, final boolean r47, android.util.SparseArray<android.support.v4.app.Fragment> r48, android.util.SparseArray<android.support.v4.app.Fragment> r49) {
        /*
            Method dump skipped, instructions count: 595
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.app.BackStackRecord.configureTransitions(int, android.support.v4.app.BackStackRecord$TransitionState, boolean, android.util.SparseArray, android.util.SparseArray):boolean");
    }

    private static ArrayMap<String, View> remapNames(ArrayList<String> inMap, ArrayList<String> toGoInMap, ArrayMap<String, View> namedViews) {
        if (!namedViews.isEmpty()) {
            ArrayMap<String, View> remappedViews = new ArrayMap<>();
            int numKeys = inMap.size();
            for (int i = 0; i < numKeys; i++) {
                View view = namedViews.get(inMap.get(i));
                if (view != null) {
                    remappedViews.put(toGoInMap.get(i), view);
                }
            }
            return remappedViews;
        }
        return namedViews;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void excludeHiddenFragments(TransitionState state, int containerId, Object transition) {
        if (this.mManager.mAdded != null) {
            for (int i = 0; i < this.mManager.mAdded.size(); i++) {
                Fragment fragment = this.mManager.mAdded.get(i);
                if (fragment.mView != null && fragment.mContainer != null && fragment.mContainerId == containerId) {
                    if (fragment.mHidden) {
                        if (!state.hiddenFragmentViews.contains(fragment.mView)) {
                            FragmentTransitionCompat21.excludeTarget(transition, fragment.mView, true);
                            state.hiddenFragmentViews.add(fragment.mView);
                        }
                    } else {
                        FragmentTransitionCompat21.excludeTarget(transition, fragment.mView, false);
                        state.hiddenFragmentViews.remove(fragment.mView);
                    }
                }
            }
        }
    }

    private static void setNameOverride(ArrayMap<String, String> overrides, String source, String target) {
        if (source != null && target != null) {
            for (int index = 0; index < overrides.size(); index++) {
                if (source.equals(overrides.valueAt(index))) {
                    overrides.setValueAt(index, target);
                    return;
                }
            }
            overrides.put(source, target);
        }
    }

    private static void setNameOverrides(TransitionState state, ArrayList<String> sourceNames, ArrayList<String> targetNames) {
        if (sourceNames != null) {
            for (int i = 0; i < sourceNames.size(); i++) {
                String source = sourceNames.get(i);
                String target = targetNames.get(i);
                setNameOverride(state.nameOverrides, source, target);
            }
        }
    }

    private void setBackNameOverrides(TransitionState state, ArrayMap<String, View> namedViews, boolean isEnd) {
        int count = this.mSharedElementTargetNames == null ? 0 : this.mSharedElementTargetNames.size();
        for (int i = 0; i < count; i++) {
            String source = this.mSharedElementSourceNames.get(i);
            String originalTarget = this.mSharedElementTargetNames.get(i);
            View view = namedViews.get(originalTarget);
            if (view != null) {
                String target = view.getTransitionName();
                if (isEnd) {
                    setNameOverride(state.nameOverrides, source, target);
                } else {
                    setNameOverride(state.nameOverrides, target, source);
                }
            }
        }
    }

    private static void setNameOverrides(TransitionState state, ArrayMap<String, View> namedViews, boolean isEnd) {
        int count = namedViews.size();
        for (int i = 0; i < count; i++) {
            String source = namedViews.keyAt(i);
            String target = namedViews.valueAt(i).getTransitionName();
            if (isEnd) {
                setNameOverride(state.nameOverrides, source, target);
            } else {
                setNameOverride(state.nameOverrides, target, source);
            }
        }
    }

    /* loaded from: classes.dex */
    public class TransitionState {
        public View nonExistentView;
        public ArrayMap<String, String> nameOverrides = new ArrayMap<>();
        public ArrayList<View> hiddenFragmentViews = new ArrayList<>();
        public FragmentTransitionCompat21.EpicenterView enteringEpicenterView = new FragmentTransitionCompat21.EpicenterView();

        public TransitionState() {
        }
    }

    @Override // android.support.v4.app.FragmentTransaction
    public final FragmentTransaction replace(int containerViewId, Fragment fragment) {
        if (containerViewId == 0) {
            throw new IllegalArgumentException("Must use non-zero containerViewId");
        }
        doAddOp(containerViewId, fragment, null, 2);
        return this;
    }

    @Override // android.support.v4.app.FragmentTransaction
    public final void commitNowAllowingStateLoss() {
        if (this.mAddToBackStack) {
            throw new IllegalStateException("This transaction is already being added to the back stack");
        }
        this.mAllowAddToBackStack = false;
        FragmentManagerImpl fragmentManagerImpl = this.mManager;
        if (fragmentManagerImpl.mExecutingActions) {
            throw new IllegalStateException("FragmentManager is already executing transactions");
        }
        if (Looper.myLooper() != fragmentManagerImpl.mHost.mHandler.getLooper()) {
            throw new IllegalStateException("Must be called from main thread of fragment host");
        }
        fragmentManagerImpl.mExecutingActions = true;
        run();
        fragmentManagerImpl.mExecutingActions = false;
        fragmentManagerImpl.doPendingDeferredStart();
    }

    static /* synthetic */ ArrayMap access$000(BackStackRecord x0, TransitionState x1, boolean x2, Fragment x3) {
        ArrayMap<String, View> arrayMap = new ArrayMap<>();
        View view = x3.getView();
        if (view != null && x0.mSharedElementSourceNames != null) {
            FragmentTransitionCompat21.findNamedViews(arrayMap, view);
            if (x2) {
                arrayMap = remapNames(x0.mSharedElementSourceNames, x0.mSharedElementTargetNames, arrayMap);
            } else {
                MapCollections.retainAllHelper(arrayMap, x0.mSharedElementTargetNames);
            }
        }
        if (x2) {
            if (x3.mExitTransitionCallback != null) {
                SharedElementCallback sharedElementCallback = x3.mExitTransitionCallback;
            }
            x0.setBackNameOverrides(x1, arrayMap, true);
        } else {
            if (x3.mEnterTransitionCallback != null) {
                SharedElementCallback sharedElementCallback2 = x3.mEnterTransitionCallback;
            }
            setNameOverrides(x1, arrayMap, true);
        }
        return arrayMap;
    }

    /* JADX WARN: Multi-variable type inference failed */
    static /* synthetic */ void access$100(BackStackRecord x0, ArrayMap x1, TransitionState x2) {
        View view;
        if (x0.mSharedElementTargetNames == null || x1.isEmpty() || (view = (View) x1.get(x0.mSharedElementTargetNames.get(0))) == null) {
            return;
        }
        x2.enteringEpicenterView.epicenter = view;
    }

    static /* synthetic */ void access$200$4a5f5891(Fragment x2, Fragment x3, boolean x4, ArrayMap x5) {
        if ((x4 ? x3.mEnterTransitionCallback : x2.mEnterTransitionCallback) == null) {
            return;
        }
        new ArrayList(x5.keySet());
        new ArrayList(x5.values());
    }
}
