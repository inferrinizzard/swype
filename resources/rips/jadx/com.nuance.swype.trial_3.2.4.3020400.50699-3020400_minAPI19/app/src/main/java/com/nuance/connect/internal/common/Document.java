package com.nuance.connect.internal.common;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;

/* loaded from: classes.dex */
public class Document implements Serializable, Comparable<Document> {
    private static final String DEFAULT_LOCALE = "en";
    public static final int DOC_CLASS_LEGAL = 1;
    public static final String ID_SEPARATOR = "_";
    private static final long serialVersionUID = -955481978051059888L;
    private final int documentClass;
    private int hashCode;
    private final String id;
    private final int revision;
    private final int type;
    private long acceptedTimestamp = Long.MIN_VALUE;
    private boolean accepted = false;
    HashMap<String, String> translations = new HashMap<>();

    /* loaded from: classes.dex */
    public enum DocumentType {
        TERMS_OF_SERVICE(1, 1),
        EULA(2, 1),
        PRIVACY_POLICY(3, 1),
        DATA_OPT_IN(4, 1);

        private int documentClass;
        private int id;

        DocumentType(int i, int i2) {
            this.id = i;
            this.documentClass = i2;
        }

        public static DocumentType fromId(int i, int i2) {
            for (DocumentType documentType : values()) {
                if (documentType.getDocTypeId() == i && documentType.getDocumentClassId() == i2) {
                    return documentType;
                }
            }
            return null;
        }

        public final int getDocTypeId() {
            return this.id;
        }

        public final int getDocumentClassId() {
            return this.documentClass;
        }
    }

    public Document(int i, int i2, int i3, String str, String str2) {
        this.id = String.valueOf(i) + ID_SEPARATOR + String.valueOf(i3);
        this.type = i;
        this.documentClass = i2;
        this.revision = i3;
        addTranslation(str, str2);
        updateHashCode();
    }

    private void addTranslation(String str, String str2) {
        if (str2 == null || str2.length() == 0) {
            str2 = Locale.getDefault().toString();
        }
        this.translations.put(str2.toLowerCase(), str);
        updateHashCode();
    }

    public static String getCompoundKey(int i, int i2, int i3) {
        return String.valueOf(i) + ID_SEPARATOR + String.valueOf(i2) + ID_SEPARATOR + String.valueOf(i3);
    }

    public static String getPrimaryKey(int i, int i2) {
        return String.valueOf(i) + ID_SEPARATOR + String.valueOf(i2);
    }

    private void updateHashCode() {
        this.hashCode = hashCode();
    }

    public void addTranslation(HashMap<String, String> hashMap) {
        this.translations.putAll(hashMap);
        updateHashCode();
    }

    @Override // java.lang.Comparable
    public int compareTo(Document document) {
        if (this.type < document.getType()) {
            return -1;
        }
        if (this.type > document.getType()) {
            return 1;
        }
        return this.revision - document.getRevision();
    }

    public boolean equals(Object obj) {
        return (obj instanceof Document) && obj.hashCode() == this.hashCode;
    }

    public boolean getAccepted() {
        return this.accepted;
    }

    public long getAcceptedTimestamp() {
        return this.acceptedTimestamp;
    }

    public String getCompoundKey() {
        return getCompoundKey(this.type, this.documentClass, this.revision);
    }

    public int getDocumentClass() {
        return this.documentClass;
    }

    public String getDocumentPath(String str) {
        String str2 = this.translations.get(str.toLowerCase());
        return str2 == null ? this.translations.get(DEFAULT_LOCALE.toLowerCase()) : str2;
    }

    public String getId() {
        return this.id;
    }

    public Set<String> getLocales() {
        return this.translations.keySet();
    }

    public String getPrimaryKey() {
        return getPrimaryKey(this.type, this.documentClass);
    }

    public int getRevision() {
        return this.revision;
    }

    public HashMap<String, String> getTranslations() {
        return this.translations;
    }

    public int getType() {
        return this.type;
    }

    public int hashCode() {
        return Arrays.hashCode(new Object[]{Integer.valueOf(this.type), Integer.valueOf(this.documentClass), Integer.valueOf(this.revision), Long.valueOf(this.acceptedTimestamp), this.translations});
    }

    public void setAcceptedTimestamp(long j) {
        this.acceptedTimestamp = j;
        updateHashCode();
        this.accepted = true;
    }

    public String toString() {
        return "Document(" + this.id + ")\ntype[" + this.type + "]\nclass[" + this.documentClass + "]\nrevision[" + this.revision + "]\nacceptedTimestamp[" + this.acceptedTimestamp + "]\naccepted[" + this.accepted + "]\n";
    }
}
