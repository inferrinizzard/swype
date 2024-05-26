package android.support.v4.app;

import android.os.Bundle;

/* loaded from: classes.dex */
final class RemoteInputCompatBase {

    /* loaded from: classes.dex */
    public static abstract class RemoteInput {

        /* loaded from: classes.dex */
        public interface Factory {
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public abstract boolean getAllowFreeFormInput();

        /* JADX INFO: Access modifiers changed from: protected */
        public abstract CharSequence[] getChoices();

        /* JADX INFO: Access modifiers changed from: protected */
        public abstract Bundle getExtras();

        /* JADX INFO: Access modifiers changed from: protected */
        public abstract CharSequence getLabel();

        /* JADX INFO: Access modifiers changed from: protected */
        public abstract String getResultKey();
    }
}
