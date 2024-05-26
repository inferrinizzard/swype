package com.nuance.nmdp.speechkit;

import com.nuance.nmdp.speechkit.DataUploadResult;
import com.nuance.nmdp.speechkit.util.Logger;
import com.nuance.nmdp.speechkit.util.parsers.IPdxParser;
import com.nuance.nmsp.client.sdk.components.core.pdx.Dictionary;
import com.nuance.nmsp.client.sdk.components.core.pdx.Sequence;
import com.nuance.nmsp.client.sdk.components.resource.nmas.QueryResult;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class DataUploadResultImpl implements DataUploadResult {
    private boolean _isVocRegenerated = false;
    private DataResultImpl[] _dataResults = null;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class DataResultImpl implements DataUploadResult.DataResult {
        private String _checksum;
        private int _count;
        private int _force;
        private String _id;
        private String _status;
        private String _type;

        DataResultImpl() {
        }

        final boolean parse(Dictionary dict) {
            if (dict.containsKey("id")) {
                this._id = dict.getUTF8String("id");
            }
            if (dict.containsKey("type")) {
                this._type = dict.getUTF8String("type");
            }
            if (dict.containsKey("status")) {
                this._status = dict.getUTF8String("status");
            }
            if (dict.containsKey("checksum")) {
                this._checksum = dict.getUTF8String("checksum");
            }
            if (dict.containsKey("force_upload")) {
                this._force = dict.getInteger("force_upload");
            }
            if (dict.containsKey("final_count")) {
                this._count = dict.getInteger("final_count");
                return true;
            }
            return true;
        }

        @Override // com.nuance.nmdp.speechkit.DataUploadResult.DataResult
        public final String getId() {
            return this._id;
        }

        @Override // com.nuance.nmdp.speechkit.DataUploadResult.DataResult
        public final String getType() {
            return this._type;
        }

        @Override // com.nuance.nmdp.speechkit.DataUploadResult.DataResult
        public final String getStatus() {
            return this._status;
        }

        @Override // com.nuance.nmdp.speechkit.DataUploadResult.DataResult
        public final int getChecksum() {
            try {
                return Integer.parseInt(this._checksum);
            } catch (Exception e) {
                return 0;
            }
        }

        @Override // com.nuance.nmdp.speechkit.DataUploadResult.DataResult
        public final int getDataCount() {
            return this._count;
        }

        @Override // com.nuance.nmdp.speechkit.DataUploadResult.DataResult
        public final int getForceUpload() {
            return this._force;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final IPdxParser<DataUploadResult> getParser() {
        return new IPdxParser<DataUploadResult>() { // from class: com.nuance.nmdp.speechkit.DataUploadResultImpl.1
            @Override // com.nuance.nmdp.speechkit.util.parsers.IParser
            public boolean parse(QueryResult result) {
                try {
                    if (result.getInteger("voc_pregeneration_status") == 1) {
                        DataUploadResultImpl.this._isVocRegenerated = true;
                    } else {
                        DataUploadResultImpl.this._isVocRegenerated = false;
                    }
                    Sequence dataResults = result.getSequence("result_list");
                    if (dataResults == null) {
                        return false;
                    }
                    int size = dataResults.size();
                    DataUploadResultImpl.this._dataResults = new DataResultImpl[size];
                    for (int i = 0; i < size; i++) {
                        DataUploadResultImpl.this._dataResults[i] = new DataResultImpl();
                        if (!DataUploadResultImpl.this._dataResults[i].parse(dataResults.getDictionary(i))) {
                            return false;
                        }
                    }
                    return true;
                } catch (Throwable tr) {
                    Logger.error(this, "Error parsing result", tr);
                    return false;
                }
            }

            @Override // com.nuance.nmdp.speechkit.util.parsers.IParser
            public DataUploadResult getParsed() {
                return DataUploadResultImpl.this;
            }

            @Override // com.nuance.nmdp.speechkit.util.parsers.IParser
            public boolean expectMore() {
                return false;
            }
        };
    }

    @Override // com.nuance.nmdp.speechkit.DataUploadResult
    public final boolean isVocRegenerated() {
        return this._isVocRegenerated;
    }

    @Override // com.nuance.nmdp.speechkit.DataUploadResult
    public final DataUploadResult.DataResult getDataResult(int index) {
        if (index < 0 || index >= this._dataResults.length) {
            return null;
        }
        return this._dataResults[index];
    }

    @Override // com.nuance.nmdp.speechkit.DataUploadResult
    public final int getDataResultCount() {
        return this._dataResults.length;
    }
}
