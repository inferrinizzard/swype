package android.support.v4.view.accessibility;

import android.os.Build;
import android.os.Bundle;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeProvider;
import java.util.List;

/* loaded from: classes.dex */
public final class AccessibilityNodeProviderCompat {
    private static final AccessibilityNodeProviderImpl IMPL;
    public final Object mProvider;

    /* loaded from: classes.dex */
    interface AccessibilityNodeProviderImpl {
        Object newAccessibilityNodeProviderBridge(AccessibilityNodeProviderCompat accessibilityNodeProviderCompat);
    }

    /* loaded from: classes.dex */
    static class AccessibilityNodeProviderStubImpl implements AccessibilityNodeProviderImpl {
        AccessibilityNodeProviderStubImpl() {
        }

        @Override // android.support.v4.view.accessibility.AccessibilityNodeProviderCompat.AccessibilityNodeProviderImpl
        public Object newAccessibilityNodeProviderBridge(AccessibilityNodeProviderCompat compat) {
            return null;
        }
    }

    /* loaded from: classes.dex */
    private static class AccessibilityNodeProviderJellyBeanImpl extends AccessibilityNodeProviderStubImpl {
        private AccessibilityNodeProviderJellyBeanImpl() {
        }

        /* synthetic */ AccessibilityNodeProviderJellyBeanImpl(byte b) {
            this();
        }

        @Override // android.support.v4.view.accessibility.AccessibilityNodeProviderCompat.AccessibilityNodeProviderStubImpl, android.support.v4.view.accessibility.AccessibilityNodeProviderCompat.AccessibilityNodeProviderImpl
        public final Object newAccessibilityNodeProviderBridge(final AccessibilityNodeProviderCompat compat) {
            return new AccessibilityNodeProvider() { // from class: android.support.v4.view.accessibility.AccessibilityNodeProviderCompatJellyBean.1
                public AnonymousClass1() {
                }

                @Override // android.view.accessibility.AccessibilityNodeProvider
                public final AccessibilityNodeInfo createAccessibilityNodeInfo(int virtualViewId) {
                    AccessibilityNodeInfoBridge.this.createAccessibilityNodeInfo$54cf32c4();
                    return null;
                }

                @Override // android.view.accessibility.AccessibilityNodeProvider
                public final List<AccessibilityNodeInfo> findAccessibilityNodeInfosByText(String text, int virtualViewId) {
                    return AccessibilityNodeInfoBridge.this.findAccessibilityNodeInfosByText$2393931d();
                }

                @Override // android.view.accessibility.AccessibilityNodeProvider
                public final boolean performAction(int virtualViewId, int action, Bundle arguments) {
                    return AccessibilityNodeInfoBridge.this.performAction$5985f823();
                }
            };
        }
    }

    /* loaded from: classes.dex */
    private static class AccessibilityNodeProviderKitKatImpl extends AccessibilityNodeProviderStubImpl {
        private AccessibilityNodeProviderKitKatImpl() {
        }

        /* synthetic */ AccessibilityNodeProviderKitKatImpl(byte b) {
            this();
        }

        @Override // android.support.v4.view.accessibility.AccessibilityNodeProviderCompat.AccessibilityNodeProviderStubImpl, android.support.v4.view.accessibility.AccessibilityNodeProviderCompat.AccessibilityNodeProviderImpl
        public final Object newAccessibilityNodeProviderBridge(final AccessibilityNodeProviderCompat compat) {
            return new AccessibilityNodeProvider() { // from class: android.support.v4.view.accessibility.AccessibilityNodeProviderCompatKitKat.1
                public AnonymousClass1() {
                }

                @Override // android.view.accessibility.AccessibilityNodeProvider
                public final AccessibilityNodeInfo createAccessibilityNodeInfo(int virtualViewId) {
                    AccessibilityNodeInfoBridge.this.createAccessibilityNodeInfo$54cf32c4();
                    return null;
                }

                @Override // android.view.accessibility.AccessibilityNodeProvider
                public final List<AccessibilityNodeInfo> findAccessibilityNodeInfosByText(String text, int virtualViewId) {
                    return AccessibilityNodeInfoBridge.this.findAccessibilityNodeInfosByText$2393931d();
                }

                @Override // android.view.accessibility.AccessibilityNodeProvider
                public final boolean performAction(int virtualViewId, int action, Bundle arguments) {
                    return AccessibilityNodeInfoBridge.this.performAction$5985f823();
                }

                @Override // android.view.accessibility.AccessibilityNodeProvider
                public final AccessibilityNodeInfo findFocus(int focus) {
                    AccessibilityNodeInfoBridge.this.findFocus$54cf32c4();
                    return null;
                }
            };
        }
    }

    static {
        byte b = 0;
        if (Build.VERSION.SDK_INT >= 19) {
            IMPL = new AccessibilityNodeProviderKitKatImpl(b);
        } else if (Build.VERSION.SDK_INT >= 16) {
            IMPL = new AccessibilityNodeProviderJellyBeanImpl(b);
        } else {
            IMPL = new AccessibilityNodeProviderStubImpl();
        }
    }

    public AccessibilityNodeProviderCompat() {
        this.mProvider = IMPL.newAccessibilityNodeProviderBridge(this);
    }

    public AccessibilityNodeProviderCompat(Object provider) {
        this.mProvider = provider;
    }

    public static AccessibilityNodeInfoCompat createAccessibilityNodeInfo$f3a5639() {
        return null;
    }

    public static boolean performAction$5985f823() {
        return false;
    }

    public static List<AccessibilityNodeInfoCompat> findAccessibilityNodeInfosByText$2393931d() {
        return null;
    }

    public static AccessibilityNodeInfoCompat findFocus$f3a5639() {
        return null;
    }
}
