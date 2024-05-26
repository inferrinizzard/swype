package com.crashlytics.android.answers;

import io.fabric.sdk.android.Fabric;
import io.fabric.sdk.android.services.common.Crash;

/* loaded from: classes.dex */
public class Answers extends AnswersKit {
    @Override // com.crashlytics.android.answers.AnswersKit, io.fabric.sdk.android.Kit
    public final /* bridge */ /* synthetic */ String getIdentifier() {
        return super.getIdentifier();
    }

    @Override // com.crashlytics.android.answers.AnswersKit, io.fabric.sdk.android.Kit
    public final /* bridge */ /* synthetic */ String getVersion() {
        return super.getVersion();
    }

    @Override // com.crashlytics.android.answers.AnswersKit
    public final /* bridge */ /* synthetic */ void onException(Crash.FatalException x0) {
        super.onException(x0);
    }

    public static Answers getInstance() {
        return (Answers) Fabric.getKit(Answers.class);
    }
}
