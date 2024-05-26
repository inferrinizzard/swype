package com.nuance.connect.util;

/* loaded from: classes.dex */
public class ActionFilter {
    private String action;
    private boolean oneShot;
    private String type;

    public ActionFilter(String str, String str2) {
        this.action = str;
        this.type = str2;
    }

    public String getAction() {
        return this.action;
    }

    public String getType() {
        return this.type;
    }

    public boolean isOneShot() {
        return this.oneShot;
    }

    public boolean matches(ActionFilter actionFilter) {
        if (actionFilter == null) {
            actionFilter = new ActionFilter(null, null);
        }
        return matches(actionFilter.getAction(), actionFilter.getType());
    }

    public boolean matches(String str, String str2) {
        return ((this.action == null && str == null) || (this.action != null && this.action.equals(str))) && ((this.type == null && str2 == null) || (this.type != null && this.type.equals(str2)));
    }

    public void oneShot() {
        this.oneShot = true;
    }

    public void setAction(String str) {
        this.action = str;
    }

    public void setType(String str) {
        this.type = str;
    }
}
