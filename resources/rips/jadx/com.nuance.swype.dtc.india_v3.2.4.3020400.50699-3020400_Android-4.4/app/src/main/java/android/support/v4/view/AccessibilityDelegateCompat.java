package android.support.v4.view;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v4.view.accessibility.AccessibilityNodeProviderCompat;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeProvider;

/* loaded from: classes.dex */
public class AccessibilityDelegateCompat {
    private static final Object DEFAULT_DELEGATE;
    private static final AccessibilityDelegateImpl IMPL;
    final Object mBridge = IMPL.newAccessiblityDelegateBridge(this);

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public interface AccessibilityDelegateImpl {
        boolean dispatchPopulateAccessibilityEvent(Object obj, View view, AccessibilityEvent accessibilityEvent);

        AccessibilityNodeProviderCompat getAccessibilityNodeProvider(Object obj, View view);

        Object newAccessiblityDelegateBridge(AccessibilityDelegateCompat accessibilityDelegateCompat);

        Object newAccessiblityDelegateDefaultImpl();

        void onInitializeAccessibilityEvent(Object obj, View view, AccessibilityEvent accessibilityEvent);

        void onInitializeAccessibilityNodeInfo(Object obj, View view, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat);

        void onPopulateAccessibilityEvent(Object obj, View view, AccessibilityEvent accessibilityEvent);

        boolean onRequestSendAccessibilityEvent(Object obj, ViewGroup viewGroup, View view, AccessibilityEvent accessibilityEvent);

        boolean performAccessibilityAction(Object obj, View view, int i, Bundle bundle);

        void sendAccessibilityEvent(Object obj, View view, int i);

        void sendAccessibilityEventUnchecked(Object obj, View view, AccessibilityEvent accessibilityEvent);
    }

    /* loaded from: classes.dex */
    static class AccessibilityDelegateStubImpl implements AccessibilityDelegateImpl {
        AccessibilityDelegateStubImpl() {
        }

        @Override // android.support.v4.view.AccessibilityDelegateCompat.AccessibilityDelegateImpl
        public Object newAccessiblityDelegateDefaultImpl() {
            return null;
        }

        @Override // android.support.v4.view.AccessibilityDelegateCompat.AccessibilityDelegateImpl
        public Object newAccessiblityDelegateBridge(AccessibilityDelegateCompat listener) {
            return null;
        }

        @Override // android.support.v4.view.AccessibilityDelegateCompat.AccessibilityDelegateImpl
        public boolean dispatchPopulateAccessibilityEvent(Object delegate, View host, AccessibilityEvent event) {
            return false;
        }

        @Override // android.support.v4.view.AccessibilityDelegateCompat.AccessibilityDelegateImpl
        public void onInitializeAccessibilityEvent(Object delegate, View host, AccessibilityEvent event) {
        }

        @Override // android.support.v4.view.AccessibilityDelegateCompat.AccessibilityDelegateImpl
        public void onInitializeAccessibilityNodeInfo(Object delegate, View host, AccessibilityNodeInfoCompat info) {
        }

        @Override // android.support.v4.view.AccessibilityDelegateCompat.AccessibilityDelegateImpl
        public void onPopulateAccessibilityEvent(Object delegate, View host, AccessibilityEvent event) {
        }

        @Override // android.support.v4.view.AccessibilityDelegateCompat.AccessibilityDelegateImpl
        public boolean onRequestSendAccessibilityEvent(Object delegate, ViewGroup host, View child, AccessibilityEvent event) {
            return true;
        }

        @Override // android.support.v4.view.AccessibilityDelegateCompat.AccessibilityDelegateImpl
        public void sendAccessibilityEvent(Object delegate, View host, int eventType) {
        }

        @Override // android.support.v4.view.AccessibilityDelegateCompat.AccessibilityDelegateImpl
        public void sendAccessibilityEventUnchecked(Object delegate, View host, AccessibilityEvent event) {
        }

        @Override // android.support.v4.view.AccessibilityDelegateCompat.AccessibilityDelegateImpl
        public AccessibilityNodeProviderCompat getAccessibilityNodeProvider(Object delegate, View host) {
            return null;
        }

        @Override // android.support.v4.view.AccessibilityDelegateCompat.AccessibilityDelegateImpl
        public boolean performAccessibilityAction(Object delegate, View host, int action, Bundle args) {
            return false;
        }
    }

    /* loaded from: classes.dex */
    static class AccessibilityDelegateIcsImpl extends AccessibilityDelegateStubImpl {
        AccessibilityDelegateIcsImpl() {
        }

        @Override // android.support.v4.view.AccessibilityDelegateCompat.AccessibilityDelegateStubImpl, android.support.v4.view.AccessibilityDelegateCompat.AccessibilityDelegateImpl
        public Object newAccessiblityDelegateBridge(final AccessibilityDelegateCompat compat) {
            return new View.AccessibilityDelegate() { // from class: android.support.v4.view.AccessibilityDelegateCompatIcs.1
                public AnonymousClass1() {
                }

                @Override // android.view.View.AccessibilityDelegate
                public final boolean dispatchPopulateAccessibilityEvent(View host, AccessibilityEvent event) {
                    return AccessibilityDelegateBridge.this.dispatchPopulateAccessibilityEvent(host, event);
                }

                @Override // android.view.View.AccessibilityDelegate
                public final void onInitializeAccessibilityEvent(View host, AccessibilityEvent event) {
                    AccessibilityDelegateBridge.this.onInitializeAccessibilityEvent(host, event);
                }

                @Override // android.view.View.AccessibilityDelegate
                public final void onInitializeAccessibilityNodeInfo(View host, AccessibilityNodeInfo info) {
                    AccessibilityDelegateBridge.this.onInitializeAccessibilityNodeInfo(host, info);
                }

                @Override // android.view.View.AccessibilityDelegate
                public final void onPopulateAccessibilityEvent(View host, AccessibilityEvent event) {
                    AccessibilityDelegateBridge.this.onPopulateAccessibilityEvent(host, event);
                }

                @Override // android.view.View.AccessibilityDelegate
                public final boolean onRequestSendAccessibilityEvent(ViewGroup host, View child, AccessibilityEvent event) {
                    return AccessibilityDelegateBridge.this.onRequestSendAccessibilityEvent(host, child, event);
                }

                @Override // android.view.View.AccessibilityDelegate
                public final void sendAccessibilityEvent(View host, int eventType) {
                    AccessibilityDelegateBridge.this.sendAccessibilityEvent(host, eventType);
                }

                @Override // android.view.View.AccessibilityDelegate
                public final void sendAccessibilityEventUnchecked(View host, AccessibilityEvent event) {
                    AccessibilityDelegateBridge.this.sendAccessibilityEventUnchecked(host, event);
                }
            };
        }

        @Override // android.support.v4.view.AccessibilityDelegateCompat.AccessibilityDelegateStubImpl, android.support.v4.view.AccessibilityDelegateCompat.AccessibilityDelegateImpl
        public final Object newAccessiblityDelegateDefaultImpl() {
            return new View.AccessibilityDelegate();
        }

        @Override // android.support.v4.view.AccessibilityDelegateCompat.AccessibilityDelegateStubImpl, android.support.v4.view.AccessibilityDelegateCompat.AccessibilityDelegateImpl
        public final boolean dispatchPopulateAccessibilityEvent(Object delegate, View host, AccessibilityEvent event) {
            return ((View.AccessibilityDelegate) delegate).dispatchPopulateAccessibilityEvent(host, event);
        }

        @Override // android.support.v4.view.AccessibilityDelegateCompat.AccessibilityDelegateStubImpl, android.support.v4.view.AccessibilityDelegateCompat.AccessibilityDelegateImpl
        public final void onInitializeAccessibilityEvent(Object delegate, View host, AccessibilityEvent event) {
            ((View.AccessibilityDelegate) delegate).onInitializeAccessibilityEvent(host, event);
        }

        @Override // android.support.v4.view.AccessibilityDelegateCompat.AccessibilityDelegateStubImpl, android.support.v4.view.AccessibilityDelegateCompat.AccessibilityDelegateImpl
        public final void onInitializeAccessibilityNodeInfo(Object delegate, View host, AccessibilityNodeInfoCompat info) {
            ((View.AccessibilityDelegate) delegate).onInitializeAccessibilityNodeInfo(host, (AccessibilityNodeInfo) info.mInfo);
        }

        @Override // android.support.v4.view.AccessibilityDelegateCompat.AccessibilityDelegateStubImpl, android.support.v4.view.AccessibilityDelegateCompat.AccessibilityDelegateImpl
        public final void onPopulateAccessibilityEvent(Object delegate, View host, AccessibilityEvent event) {
            ((View.AccessibilityDelegate) delegate).onPopulateAccessibilityEvent(host, event);
        }

        @Override // android.support.v4.view.AccessibilityDelegateCompat.AccessibilityDelegateStubImpl, android.support.v4.view.AccessibilityDelegateCompat.AccessibilityDelegateImpl
        public final boolean onRequestSendAccessibilityEvent(Object delegate, ViewGroup host, View child, AccessibilityEvent event) {
            return ((View.AccessibilityDelegate) delegate).onRequestSendAccessibilityEvent(host, child, event);
        }

        @Override // android.support.v4.view.AccessibilityDelegateCompat.AccessibilityDelegateStubImpl, android.support.v4.view.AccessibilityDelegateCompat.AccessibilityDelegateImpl
        public final void sendAccessibilityEvent(Object delegate, View host, int eventType) {
            ((View.AccessibilityDelegate) delegate).sendAccessibilityEvent(host, eventType);
        }

        @Override // android.support.v4.view.AccessibilityDelegateCompat.AccessibilityDelegateStubImpl, android.support.v4.view.AccessibilityDelegateCompat.AccessibilityDelegateImpl
        public final void sendAccessibilityEventUnchecked(Object delegate, View host, AccessibilityEvent event) {
            ((View.AccessibilityDelegate) delegate).sendAccessibilityEventUnchecked(host, event);
        }
    }

    /* loaded from: classes.dex */
    static class AccessibilityDelegateJellyBeanImpl extends AccessibilityDelegateIcsImpl {
        AccessibilityDelegateJellyBeanImpl() {
        }

        @Override // android.support.v4.view.AccessibilityDelegateCompat.AccessibilityDelegateIcsImpl, android.support.v4.view.AccessibilityDelegateCompat.AccessibilityDelegateStubImpl, android.support.v4.view.AccessibilityDelegateCompat.AccessibilityDelegateImpl
        public final Object newAccessiblityDelegateBridge(final AccessibilityDelegateCompat compat) {
            return new View.AccessibilityDelegate() { // from class: android.support.v4.view.AccessibilityDelegateCompatJellyBean.1
                public AnonymousClass1() {
                }

                @Override // android.view.View.AccessibilityDelegate
                public final boolean dispatchPopulateAccessibilityEvent(View host, AccessibilityEvent event) {
                    return AccessibilityDelegateBridgeJellyBean.this.dispatchPopulateAccessibilityEvent(host, event);
                }

                @Override // android.view.View.AccessibilityDelegate
                public final void onInitializeAccessibilityEvent(View host, AccessibilityEvent event) {
                    AccessibilityDelegateBridgeJellyBean.this.onInitializeAccessibilityEvent(host, event);
                }

                @Override // android.view.View.AccessibilityDelegate
                public final void onInitializeAccessibilityNodeInfo(View host, AccessibilityNodeInfo info) {
                    AccessibilityDelegateBridgeJellyBean.this.onInitializeAccessibilityNodeInfo(host, info);
                }

                @Override // android.view.View.AccessibilityDelegate
                public final void onPopulateAccessibilityEvent(View host, AccessibilityEvent event) {
                    AccessibilityDelegateBridgeJellyBean.this.onPopulateAccessibilityEvent(host, event);
                }

                @Override // android.view.View.AccessibilityDelegate
                public final boolean onRequestSendAccessibilityEvent(ViewGroup host, View child, AccessibilityEvent event) {
                    return AccessibilityDelegateBridgeJellyBean.this.onRequestSendAccessibilityEvent(host, child, event);
                }

                @Override // android.view.View.AccessibilityDelegate
                public final void sendAccessibilityEvent(View host, int eventType) {
                    AccessibilityDelegateBridgeJellyBean.this.sendAccessibilityEvent(host, eventType);
                }

                @Override // android.view.View.AccessibilityDelegate
                public final void sendAccessibilityEventUnchecked(View host, AccessibilityEvent event) {
                    AccessibilityDelegateBridgeJellyBean.this.sendAccessibilityEventUnchecked(host, event);
                }

                @Override // android.view.View.AccessibilityDelegate
                public final AccessibilityNodeProvider getAccessibilityNodeProvider(View host) {
                    return (AccessibilityNodeProvider) AccessibilityDelegateBridgeJellyBean.this.getAccessibilityNodeProvider(host);
                }

                @Override // android.view.View.AccessibilityDelegate
                public final boolean performAccessibilityAction(View host, int action, Bundle args) {
                    return AccessibilityDelegateBridgeJellyBean.this.performAccessibilityAction(host, action, args);
                }
            };
        }

        @Override // android.support.v4.view.AccessibilityDelegateCompat.AccessibilityDelegateStubImpl, android.support.v4.view.AccessibilityDelegateCompat.AccessibilityDelegateImpl
        public final AccessibilityNodeProviderCompat getAccessibilityNodeProvider(Object delegate, View host) {
            AccessibilityNodeProvider provider = ((View.AccessibilityDelegate) delegate).getAccessibilityNodeProvider(host);
            if (provider != null) {
                return new AccessibilityNodeProviderCompat(provider);
            }
            return null;
        }

        @Override // android.support.v4.view.AccessibilityDelegateCompat.AccessibilityDelegateStubImpl, android.support.v4.view.AccessibilityDelegateCompat.AccessibilityDelegateImpl
        public final boolean performAccessibilityAction(Object delegate, View host, int action, Bundle args) {
            return ((View.AccessibilityDelegate) delegate).performAccessibilityAction(host, action, args);
        }
    }

    static {
        if (Build.VERSION.SDK_INT >= 16) {
            IMPL = new AccessibilityDelegateJellyBeanImpl();
        } else if (Build.VERSION.SDK_INT >= 14) {
            IMPL = new AccessibilityDelegateIcsImpl();
        } else {
            IMPL = new AccessibilityDelegateStubImpl();
        }
        DEFAULT_DELEGATE = IMPL.newAccessiblityDelegateDefaultImpl();
    }

    public static void sendAccessibilityEvent(View host, int eventType) {
        IMPL.sendAccessibilityEvent(DEFAULT_DELEGATE, host, eventType);
    }

    public static void sendAccessibilityEventUnchecked(View host, AccessibilityEvent event) {
        IMPL.sendAccessibilityEventUnchecked(DEFAULT_DELEGATE, host, event);
    }

    public boolean dispatchPopulateAccessibilityEvent(View host, AccessibilityEvent event) {
        return IMPL.dispatchPopulateAccessibilityEvent(DEFAULT_DELEGATE, host, event);
    }

    public static void onPopulateAccessibilityEvent(View host, AccessibilityEvent event) {
        IMPL.onPopulateAccessibilityEvent(DEFAULT_DELEGATE, host, event);
    }

    public void onInitializeAccessibilityEvent(View host, AccessibilityEvent event) {
        IMPL.onInitializeAccessibilityEvent(DEFAULT_DELEGATE, host, event);
    }

    public void onInitializeAccessibilityNodeInfo(View host, AccessibilityNodeInfoCompat info) {
        IMPL.onInitializeAccessibilityNodeInfo(DEFAULT_DELEGATE, host, info);
    }

    public boolean onRequestSendAccessibilityEvent(ViewGroup host, View child, AccessibilityEvent event) {
        return IMPL.onRequestSendAccessibilityEvent(DEFAULT_DELEGATE, host, child, event);
    }

    public static AccessibilityNodeProviderCompat getAccessibilityNodeProvider(View host) {
        return IMPL.getAccessibilityNodeProvider(DEFAULT_DELEGATE, host);
    }

    public boolean performAccessibilityAction(View host, int action, Bundle args) {
        return IMPL.performAccessibilityAction(DEFAULT_DELEGATE, host, action, args);
    }
}
