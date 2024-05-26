package android.support.v4.content.res;

import android.content.res.Resources;
import android.os.Build;

/* loaded from: classes.dex */
public final class ConfigurationHelper {
    private static final ConfigurationHelperImpl IMPL;

    /* loaded from: classes.dex */
    private interface ConfigurationHelperImpl {
        int getScreenHeightDp(Resources resources);

        int getScreenWidthDp(Resources resources);

        int getSmallestScreenWidthDp(Resources resources);
    }

    static {
        byte b = 0;
        int sdk = Build.VERSION.SDK_INT;
        if (sdk >= 17) {
            IMPL = new JellybeanMr1Impl(b);
        } else if (sdk >= 13) {
            IMPL = new HoneycombMr2Impl(b);
        } else {
            IMPL = new DonutImpl(b);
        }
    }

    /* loaded from: classes.dex */
    private static class DonutImpl implements ConfigurationHelperImpl {
        private DonutImpl() {
        }

        /* synthetic */ DonutImpl(byte b) {
            this();
        }

        @Override // android.support.v4.content.res.ConfigurationHelper.ConfigurationHelperImpl
        public int getScreenHeightDp(Resources resources) {
            return ConfigurationHelperDonut.getScreenHeightDp(resources);
        }

        @Override // android.support.v4.content.res.ConfigurationHelper.ConfigurationHelperImpl
        public int getScreenWidthDp(Resources resources) {
            return ConfigurationHelperDonut.getScreenWidthDp(resources);
        }

        @Override // android.support.v4.content.res.ConfigurationHelper.ConfigurationHelperImpl
        public int getSmallestScreenWidthDp(Resources resources) {
            return Math.min(ConfigurationHelperDonut.getScreenWidthDp(resources), ConfigurationHelperDonut.getScreenHeightDp(resources));
        }
    }

    /* loaded from: classes.dex */
    private static class HoneycombMr2Impl extends DonutImpl {
        private HoneycombMr2Impl() {
            super((byte) 0);
        }

        /* synthetic */ HoneycombMr2Impl(byte b) {
            this();
        }

        @Override // android.support.v4.content.res.ConfigurationHelper.DonutImpl, android.support.v4.content.res.ConfigurationHelper.ConfigurationHelperImpl
        public final int getScreenHeightDp(Resources resources) {
            return resources.getConfiguration().screenHeightDp;
        }

        @Override // android.support.v4.content.res.ConfigurationHelper.DonutImpl, android.support.v4.content.res.ConfigurationHelper.ConfigurationHelperImpl
        public final int getScreenWidthDp(Resources resources) {
            return resources.getConfiguration().screenWidthDp;
        }

        @Override // android.support.v4.content.res.ConfigurationHelper.DonutImpl, android.support.v4.content.res.ConfigurationHelper.ConfigurationHelperImpl
        public final int getSmallestScreenWidthDp(Resources resources) {
            return resources.getConfiguration().smallestScreenWidthDp;
        }
    }

    /* loaded from: classes.dex */
    private static class JellybeanMr1Impl extends HoneycombMr2Impl {
        private JellybeanMr1Impl() {
            super((byte) 0);
        }

        /* synthetic */ JellybeanMr1Impl(byte b) {
            this();
        }
    }

    public static int getScreenHeightDp(Resources resources) {
        return IMPL.getScreenHeightDp(resources);
    }

    public static int getScreenWidthDp(Resources resources) {
        return IMPL.getScreenWidthDp(resources);
    }

    public static int getSmallestScreenWidthDp(Resources resources) {
        return IMPL.getSmallestScreenWidthDp(resources);
    }
}
