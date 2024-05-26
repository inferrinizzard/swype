package com.nuance.nmdp.speechkit;

import com.facebook.GraphResponse;
import com.nuance.nmdp.speechkit.LanguageTable;
import com.nuance.nmdp.speechkit.util.Logger;
import com.nuance.nmdp.speechkit.util.parsers.IPdxParser;
import com.nuance.nmsp.client.sdk.components.core.pdx.Dictionary;
import com.nuance.nmsp.client.sdk.components.core.pdx.Sequence;
import com.nuance.nmsp.client.sdk.components.resource.nmas.QueryResult;
import com.nuance.swype.input.AppPreferences;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class LanguageTableImpl implements LanguageTable {
    private int _checksum;
    private boolean _status = false;
    private LanguageImpl[] _languages = null;
    private String _language = null;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class LanguageImpl implements LanguageTable.Language {
        private String[] _addresses;
        private String _description;
        private String _lang;

        LanguageImpl() {
        }

        final boolean parse(Dictionary dict) {
            this._lang = dict.getUTF8String("lang");
            this._description = dict.getUTF8String("description");
            if (this._lang == null || this._description == null) {
                return false;
            }
            Sequence addresses = dict.getSequence("addresses");
            int size = addresses.size();
            this._addresses = new String[size];
            for (int i = 0; i < size; i++) {
                this._addresses[i] = addresses.getUTF8String(i);
                if (this._addresses == null) {
                    return false;
                }
            }
            return true;
        }

        @Override // com.nuance.nmdp.speechkit.LanguageTable.Language
        public final String getLanguage() {
            return this._lang;
        }

        @Override // com.nuance.nmdp.speechkit.LanguageTable.Language
        public final String getDescription() {
            return this._description;
        }

        @Override // com.nuance.nmdp.speechkit.LanguageTable.Language
        public final int getAddressCount() {
            return this._addresses.length;
        }

        @Override // com.nuance.nmdp.speechkit.LanguageTable.Language
        public final String getAddress(int index) {
            if (index < 0 || index >= this._addresses.length) {
                return null;
            }
            return this._addresses[index];
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final IPdxParser<LanguageTable> getParser() {
        return new IPdxParser<LanguageTable>() { // from class: com.nuance.nmdp.speechkit.LanguageTableImpl.1
            @Override // com.nuance.nmdp.speechkit.util.parsers.IParser
            public boolean parse(QueryResult result) {
                try {
                    String status = result.getUTF8String("status");
                    LanguageTableImpl.this._status = GraphResponse.SUCCESS_KEY.equals(status);
                    if (!LanguageTableImpl.this._status) {
                        LanguageTableImpl.this._language = null;
                    } else {
                        LanguageTableImpl.this._language = result.getUTF8String("dictation_language_to_use");
                    }
                    Sequence languages = result.getSequence(AppPreferences.AVAILABLE_LANGUAGES);
                    LanguageTableImpl.this._checksum = result.getInteger("available_langs_checksum");
                    if (languages == null) {
                        return false;
                    }
                    int size = languages.size();
                    LanguageTableImpl.this._languages = new LanguageImpl[size];
                    for (int i = 0; i < size; i++) {
                        LanguageTableImpl.this._languages[i] = new LanguageImpl();
                        if (!LanguageTableImpl.this._languages[i].parse(languages.getDictionary(i))) {
                            return false;
                        }
                    }
                    return true;
                } catch (Throwable tr) {
                    Logger.error(this, "Error parsing language table request result", tr);
                    return false;
                }
            }

            @Override // com.nuance.nmdp.speechkit.util.parsers.IParser
            public LanguageTable getParsed() {
                return LanguageTableImpl.this;
            }

            @Override // com.nuance.nmdp.speechkit.util.parsers.IParser
            public boolean expectMore() {
                return false;
            }
        };
    }

    @Override // com.nuance.nmdp.speechkit.LanguageTable
    public final String getLanguage() {
        return this._language;
    }

    @Override // com.nuance.nmdp.speechkit.LanguageTable
    public final boolean isStatusSuccess() {
        return this._status;
    }

    @Override // com.nuance.nmdp.speechkit.LanguageTable
    public final int getAvailableLanguageCount() {
        return this._languages.length;
    }

    @Override // com.nuance.nmdp.speechkit.LanguageTable
    public final LanguageTable.Language getAvailableLanguage(int index) {
        if (index < 0 || index >= this._languages.length) {
            return null;
        }
        return this._languages[index];
    }

    @Override // com.nuance.nmdp.speechkit.LanguageTable
    public final int getChecksum() {
        return this._checksum;
    }
}
