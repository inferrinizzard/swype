package android.support.v4.app;

import java.util.List;

/* loaded from: classes.dex */
public final class FragmentManagerNonConfig {
    final List<FragmentManagerNonConfig> mChildNonConfigs;
    final List<Fragment> mFragments;

    /* JADX INFO: Access modifiers changed from: package-private */
    public FragmentManagerNonConfig(List<Fragment> fragments, List<FragmentManagerNonConfig> childNonConfigs) {
        this.mFragments = fragments;
        this.mChildNonConfigs = childNonConfigs;
    }
}
