package com.nuance.nmdp.speechkit.util.dataupload;

import java.util.Vector;

/* loaded from: classes.dex */
public class Data {
    static final String CONTACTS_STR = "contacts";
    static final String CUSTOM_WORDS_STR = "custom_words";
    static final String HISTORY_STR = "history";
    private Vector<Action> _actions;
    private int _checksum = 0;
    private String _id;
    private DataType _type;

    /* loaded from: classes.dex */
    public enum DataType {
        CONTACTS,
        CUSTOMWORDS,
        HISTORY
    }

    public Data(String id, DataType type) {
        this._id = id;
        if (type == DataType.CONTACTS || type == DataType.CUSTOMWORDS || type == DataType.HISTORY) {
            this._type = type;
        } else {
            this._type = DataType.CONTACTS;
        }
        this._actions = new Vector<>();
    }

    public void clearData() {
        this._id = null;
        this._checksum = 0;
        this._type = null;
        this._actions.removeAllElements();
    }

    public String getId() {
        return this._id;
    }

    void setChecksum(int checksum) {
        this._checksum = checksum;
    }

    public int getChecksum() {
        int checksum = 0;
        for (int idx = 0; idx < this._actions.size(); idx++) {
            Action a = this._actions.elementAt(idx);
            if (a != null) {
                checksum += a.getChecksum();
            }
        }
        this._checksum = checksum;
        return checksum;
    }

    public void setType(DataType type) {
        if (type == DataType.CONTACTS || type == DataType.CUSTOMWORDS || type == DataType.HISTORY) {
            this._type = type;
        } else {
            this._type = DataType.CONTACTS;
        }
    }

    public String getTypeStr() {
        if (this._type == null) {
            return CONTACTS_STR;
        }
        switch (this._type) {
            case CONTACTS:
                return CONTACTS_STR;
            case CUSTOMWORDS:
                return "custom_words";
            case HISTORY:
                return HISTORY_STR;
            default:
                return CONTACTS_STR;
        }
    }

    public void clearActions() {
        this._actions.removeAllElements();
    }

    public void addAction(Action action) {
        if (action != null) {
            this._actions.add(action);
        }
    }

    public void removeAction(Action action) {
        if (action != null) {
            this._actions.remove(action);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Removed duplicated region for block: B:8:0x002f  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public com.nuance.nmdp.speechkit.util.pdx.PdxValue.Dictionary getDataDictionary() {
        /*
            r7 = this;
            com.nuance.nmdp.speechkit.util.pdx.PdxValue$Dictionary r2 = new com.nuance.nmdp.speechkit.util.pdx.PdxValue$Dictionary
            r2.<init>()
            java.lang.String r5 = "id"
            java.lang.String r6 = r7._id
            r2.put(r5, r6)
            com.nuance.nmdp.speechkit.util.dataupload.Data$DataType r5 = r7._type
            if (r5 == 0) goto L1e
            int[] r5 = com.nuance.nmdp.speechkit.util.dataupload.Data.AnonymousClass1.$SwitchMap$com$nuance$nmdp$speechkit$util$dataupload$Data$DataType
            com.nuance.nmdp.speechkit.util.dataupload.Data$DataType r6 = r7._type
            int r6 = r6.ordinal()
            r5 = r5[r6]
            switch(r5) {
                case 1: goto L53;
                case 2: goto L5d;
                case 3: goto L67;
                default: goto L1e;
            }
        L1e:
            java.lang.String r5 = "type"
            java.lang.String r6 = "contacts"
            r2.put(r5, r6)
        L27:
            java.util.Vector<com.nuance.nmdp.speechkit.util.dataupload.Action> r5 = r7._actions
            boolean r5 = r5.isEmpty()
            if (r5 != 0) goto L77
            com.nuance.nmdp.speechkit.util.pdx.PdxValue$Sequence r4 = new com.nuance.nmdp.speechkit.util.pdx.PdxValue$Sequence
            r4.<init>()
            r3 = 0
        L35:
            java.util.Vector<com.nuance.nmdp.speechkit.util.dataupload.Action> r5 = r7._actions
            int r5 = r5.size()
            if (r3 >= r5) goto L71
            java.util.Vector<com.nuance.nmdp.speechkit.util.dataupload.Action> r5 = r7._actions
            java.lang.Object r0 = r5.elementAt(r3)
            com.nuance.nmdp.speechkit.util.dataupload.Action r0 = (com.nuance.nmdp.speechkit.util.dataupload.Action) r0
            if (r0 == 0) goto L50
            com.nuance.nmdp.speechkit.util.pdx.PdxValue$Dictionary r1 = r0.getActionDictionary()
            if (r1 == 0) goto L50
            r4.add(r1)
        L50:
            int r3 = r3 + 1
            goto L35
        L53:
            java.lang.String r5 = "type"
            java.lang.String r6 = "contacts"
            r2.put(r5, r6)
            goto L27
        L5d:
            java.lang.String r5 = "type"
            java.lang.String r6 = "custom_words"
            r2.put(r5, r6)
            goto L27
        L67:
            java.lang.String r5 = "type"
            java.lang.String r6 = "history"
            r2.put(r5, r6)
            goto L27
        L71:
            java.lang.String r5 = "actions"
            r2.put(r5, r4)
        L77:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.nuance.nmdp.speechkit.util.dataupload.Data.getDataDictionary():com.nuance.nmdp.speechkit.util.pdx.PdxValue$Dictionary");
    }

    public String toString() {
        StringBuffer data = new StringBuffer();
        data.append("id:" + this._id + "\n");
        data.append("checksum:" + this._checksum + "\n");
        data.append("type:" + this._type + "\n");
        if (!this._actions.isEmpty()) {
            int size = this._actions.size();
            data.append("action list: " + size + "\n");
            for (int idx = 0; idx < size; idx++) {
                Action act = this._actions.elementAt(idx);
                if (act != null) {
                    data.append("action: " + idx + "\n");
                    data.append(act.toString());
                }
            }
        }
        return data.toString();
    }
}
