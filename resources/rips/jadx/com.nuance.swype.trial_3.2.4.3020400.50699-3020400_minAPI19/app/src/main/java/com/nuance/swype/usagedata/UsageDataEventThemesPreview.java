package com.nuance.swype.usagedata;

import com.nuance.nmsp.client.sdk.components.core.internal.calllog.SessionEventImpl;
import com.nuance.swype.usagedata.UsageData;

/* loaded from: classes.dex */
public final class UsageDataEventThemesPreview extends UsageDataEvent<UsageDataEventThemesPreview> {
    public Action mAction;
    public String mCategory;
    public Integer mPosition;
    public String mPreviewedFrom;
    public Result mResult;
    public String mThemeName;

    @Override // com.nuance.swype.usagedata.UsageDataEvent
    public final /* bridge */ /* synthetic */ String toString() {
        return super.toString();
    }

    /* loaded from: classes.dex */
    public enum Action {
        APPLY("Apply"),
        BUY("Buy"),
        CANCEL(SessionEventImpl.NMSP_CALLLOG_META_CANCEL),
        INSTALL("Install"),
        UNINSTALL("Uninstall");

        private final String action;

        Action(String s) {
            this.action = s;
        }

        @Override // java.lang.Enum
        public final String toString() {
            return this.action;
        }
    }

    /* loaded from: classes.dex */
    public enum Result {
        SUCCESS("Success"),
        NULL_THEME("NullTheme"),
        AC_EXCEPTION("ACException"),
        ALREADY_OWNED("Already Owned"),
        ABORTED("Aborted");

        private final String result;

        Result(String s) {
            this.result = s;
        }

        @Override // java.lang.Enum
        public final String toString() {
            return this.result;
        }
    }

    public UsageDataEventThemesPreview() {
        super(UsageData.EventTag.STORE_THEMES_PREVIEW.toString());
    }

    @Override // com.nuance.swype.usagedata.UsageDataEvent
    public final void commit() {
        if (this.mAction != null) {
            this.mAttributes.put(UsageData.EventAttribute.USER_ACTION.toString(), this.mAction.toString());
        } else {
            eventError("USER_ACTION not provided!");
        }
        if (this.mResult != null) {
            this.mAttributes.put(UsageData.EventAttribute.RESULT.toString(), this.mResult.toString());
        } else {
            eventError("RESULT not provided!");
        }
        if (this.mThemeName != null) {
            this.mAttributes.put(UsageData.EventAttribute.THEME_NAME.toString(), this.mThemeName);
        } else {
            eventError("THEME_NAME not provided!");
        }
        if (this.mPreviewedFrom != null) {
            this.mAttributes.put(UsageData.EventAttribute.TAB_PREVIEWED_FROM.toString(), this.mPreviewedFrom);
        } else {
            eventError("TAB_PREVIEWED_FROM not provided!");
        }
        if (this.mPosition != null) {
            this.mAttributes.put(UsageData.EventAttribute.POSITION.toString(), Integer.toString(this.mPosition.intValue()));
        } else {
            eventError("POSITION not provided!");
        }
        if (this.mCategory != null) {
            this.mAttributes.put(UsageData.EventAttribute.CATEGORY.toString(), this.mCategory);
        } else {
            eventError("CATEGORY not provided!");
        }
        super.commit();
    }

    @Override // com.nuance.swype.usagedata.UsageDataEvent
    protected final String getLogTag() {
        return "UsageDataEventThemesPreview";
    }
}
