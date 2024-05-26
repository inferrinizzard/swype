package com.nuance.nmdp.speechkit.util.dataupload;

import com.nuance.nmsp.client.util.internal.dictationresult.parser.xml.xmlResults.XMLResultsHandler;
import java.util.Vector;

/* loaded from: classes.dex */
public class Action {
    static final String ADD_STR = "add";
    static final String CLEAR_ALL_STR = "clear_all";
    static final String REMOVE_STR = "remove";
    private ActionType _action;
    private Vector<Contact> _contacts;
    private Vector<String> _words;

    /* loaded from: classes.dex */
    public enum ActionType {
        ADD,
        REMOVE,
        CLEARALL
    }

    public Action(ActionType type) {
        if (type == ActionType.ADD || type == ActionType.REMOVE || type == ActionType.CLEARALL) {
            this._action = type;
        } else {
            this._action = ActionType.ADD;
        }
        this._contacts = new Vector<>();
        this._words = new Vector<>();
    }

    public void setType(ActionType type) {
        if (type == ActionType.ADD || type == ActionType.REMOVE || type == ActionType.CLEARALL) {
            this._action = type;
        } else {
            this._action = ActionType.ADD;
        }
    }

    public void cleanData() {
        this._action = null;
        this._contacts.removeAllElements();
        this._words.removeAllElements();
    }

    public void addContact(Contact contact) {
        this._contacts.add(contact);
    }

    public void removeContact(Contact contact) {
        this._contacts.remove(contact);
    }

    public void clearContacts() {
        this._contacts.removeAllElements();
    }

    public void addWord(String word) {
        this._words.add(word);
    }

    public void removeWord(String word) {
        this._words.remove(word);
    }

    public void clearWords() {
        this._words.removeAllElements();
    }

    public int getChecksum() {
        int checksum = 0;
        for (int idx = 0; idx < this._contacts.size(); idx++) {
            Contact c = this._contacts.elementAt(idx);
            if (c != null) {
                checksum += c.getCheckSum();
            }
        }
        for (int idx2 = 0; idx2 < this._words.size(); idx2++) {
            String w = this._words.elementAt(idx2);
            if (w != null) {
                checksum += w.hashCode();
            }
        }
        return checksum;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Removed duplicated region for block: B:25:0x007b  */
    /* JADX WARN: Removed duplicated region for block: B:8:0x002c  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public com.nuance.nmdp.speechkit.util.pdx.PdxValue.Dictionary getActionDictionary() {
        /*
            r9 = this;
            com.nuance.nmdp.speechkit.util.pdx.PdxValue$Dictionary r3 = new com.nuance.nmdp.speechkit.util.pdx.PdxValue$Dictionary
            r3.<init>()
            com.nuance.nmdp.speechkit.util.pdx.PdxValue$Dictionary r2 = new com.nuance.nmdp.speechkit.util.pdx.PdxValue$Dictionary
            r2.<init>()
            com.nuance.nmdp.speechkit.util.dataupload.Action$ActionType r7 = r9._action
            if (r7 == 0) goto L1b
            int[] r7 = com.nuance.nmdp.speechkit.util.dataupload.Action.AnonymousClass1.$SwitchMap$com$nuance$nmdp$speechkit$util$dataupload$Action$ActionType
            com.nuance.nmdp.speechkit.util.dataupload.Action$ActionType r8 = r9._action
            int r8 = r8.ordinal()
            r7 = r7[r8]
            switch(r7) {
                case 1: goto L50;
                case 2: goto L5a;
                case 3: goto L64;
                default: goto L1b;
            }
        L1b:
            java.lang.String r7 = "action"
            java.lang.String r8 = "add"
            r3.put(r7, r8)
        L24:
            java.util.Vector<com.nuance.nmdp.speechkit.util.dataupload.Contact> r7 = r9._contacts
            boolean r7 = r7.isEmpty()
            if (r7 != 0) goto L7b
            com.nuance.nmdp.speechkit.util.pdx.PdxValue$Sequence r5 = new com.nuance.nmdp.speechkit.util.pdx.PdxValue$Sequence
            r5.<init>()
            r4 = 0
        L32:
            java.util.Vector<com.nuance.nmdp.speechkit.util.dataupload.Contact> r7 = r9._contacts
            int r7 = r7.size()
            if (r4 >= r7) goto L6e
            java.util.Vector<com.nuance.nmdp.speechkit.util.dataupload.Contact> r7 = r9._contacts
            java.lang.Object r0 = r7.elementAt(r4)
            com.nuance.nmdp.speechkit.util.dataupload.Contact r0 = (com.nuance.nmdp.speechkit.util.dataupload.Contact) r0
            if (r0 == 0) goto L4d
            com.nuance.nmdp.speechkit.util.pdx.PdxValue$Dictionary r1 = r0.getContactDictionary()
            if (r1 == 0) goto L4d
            r5.add(r1)
        L4d:
            int r4 = r4 + 1
            goto L32
        L50:
            java.lang.String r7 = "action"
            java.lang.String r8 = "add"
            r3.put(r7, r8)
            goto L24
        L5a:
            java.lang.String r7 = "action"
            java.lang.String r8 = "remove"
            r3.put(r7, r8)
            goto L24
        L64:
            java.lang.String r7 = "action"
            java.lang.String r8 = "clear_all"
            r3.put(r7, r8)
            goto L24
        L6e:
            java.lang.String r7 = "list"
            r2.put(r7, r5)
        L74:
            java.lang.String r7 = "content"
            r3.put(r7, r2)
            return r3
        L7b:
            java.util.Vector<java.lang.String> r7 = r9._words
            boolean r7 = r7.isEmpty()
            if (r7 != 0) goto L74
            com.nuance.nmdp.speechkit.util.pdx.PdxValue$Sequence r5 = new com.nuance.nmdp.speechkit.util.pdx.PdxValue$Sequence
            r5.<init>()
            r4 = 0
        L89:
            java.util.Vector<java.lang.String> r7 = r9._words
            int r7 = r7.size()
            if (r4 >= r7) goto La1
            java.util.Vector<java.lang.String> r7 = r9._words
            java.lang.Object r6 = r7.elementAt(r4)
            java.lang.String r6 = (java.lang.String) r6
            if (r6 == 0) goto L9e
            r5.add(r6)
        L9e:
            int r4 = r4 + 1
            goto L89
        La1:
            java.lang.String r7 = "list"
            r2.put(r7, r5)
            goto L74
        */
        throw new UnsupportedOperationException("Method not decompiled: com.nuance.nmdp.speechkit.util.dataupload.Action.getActionDictionary():com.nuance.nmdp.speechkit.util.pdx.PdxValue$Dictionary");
    }

    public String toString() {
        StringBuffer action = new StringBuffer();
        action.append("action:" + this._action + "\n");
        if (!this._contacts.isEmpty()) {
            int size = this._contacts.size();
            action.append("contact list: " + size + "\n");
            for (int idx = 0; idx < size; idx++) {
                Contact c = this._contacts.elementAt(idx);
                if (c != null) {
                    action.append("contact: " + idx + "\n");
                    action.append(c.toString());
                }
            }
        }
        if (!this._words.isEmpty()) {
            int size2 = this._words.size();
            action.append("word list: " + size2 + "\n");
            for (int idx2 = 0; idx2 < size2; idx2++) {
                String word = this._words.elementAt(idx2);
                if (word != null) {
                    action.append("word: " + idx2 + XMLResultsHandler.SEP_SPACE + word + "\n");
                }
            }
        }
        return action.toString();
    }
}
