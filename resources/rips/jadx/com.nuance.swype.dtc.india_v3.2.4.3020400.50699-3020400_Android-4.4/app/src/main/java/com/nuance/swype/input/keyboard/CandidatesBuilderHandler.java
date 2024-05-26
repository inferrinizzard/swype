package com.nuance.swype.input.keyboard;

import android.os.Message;
import com.nuance.input.swypecorelib.Candidates;
import com.nuance.input.swypecorelib.XT9CoreInput;

/* loaded from: classes.dex */
public class CandidatesBuilderHandler extends WeakReferenceHandlerWrapper<CandidatesBuilderResult> {
    private static final int MSG_BUILD_SUGGESTION_CANDIDATE = 0;
    private final XT9CoreInput coreInput;

    /* loaded from: classes.dex */
    public interface CandidatesBuilderResult {
        void onBuildResult(Candidates candidates, Object obj);
    }

    public CandidatesBuilderHandler(XT9CoreInput coreInput, CandidatesBuilderResult ownerInstance) {
        super(ownerInstance);
        this.coreInput = coreInput;
    }

    @Override // android.os.Handler
    public void handleMessage(Message msg) {
        CandidatesBuilderResult callback = getOwnerInstance();
        if (callback != null && msg.what == 0) {
            BuildParameter buildParameter = (BuildParameter) msg.obj;
            callback.onBuildResult(this.coreInput.getCandidates(buildParameter.source), buildParameter.parameter);
        }
    }

    public void build(Candidates.Source source) {
        build(source, null);
    }

    public void build(Candidates.Source source, Object parameter) {
        removeMessages(0);
        sendMessageDelayed(obtainMessage(0, obtainBuildParameter(source, parameter)), 15L);
    }

    public boolean hasPendingBuild() {
        return hasMessages(0);
    }

    public void removePendingBuild() {
        removeMessages(0);
    }

    private static BuildParameter obtainBuildParameter(Candidates.Source source, Object parameter) {
        return new BuildParameter(source, parameter);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class BuildParameter {
        final Object parameter;
        final Candidates.Source source;

        public BuildParameter(Candidates.Source source, Object parameter) {
            this.source = source;
            this.parameter = parameter;
        }

        public Object getParameter() {
            return this.parameter;
        }
    }
}
