package com.nuance.nmdp.speechkit.transaction.custom_words_synchronize;

import com.facebook.GraphResponse;
import com.facebook.internal.NativeProtocol;
import com.nuance.nmdp.speechkit.CustomWordsSynchronizeResult;
import com.nuance.nmdp.speechkit.util.Logger;
import com.nuance.nmsp.client.sdk.components.core.pdx.Dictionary;
import com.nuance.nmsp.client.sdk.components.core.pdx.Sequence;
import com.nuance.nmsp.client.sdk.components.resource.nmas.QueryResult;
import java.util.Collection;
import java.util.Set;

/* loaded from: classes.dex */
public class CustomWordsSynchronizeStartingState extends CustomWordsSynchronizeTransactionStateBase {
    public static final String CUSTOM_WORDS_TYPE = "custom_words";
    public static final String CUSTOM_WORDS_UPLOAD_COMMAND = "NVC_DATA_UPLOAD_CMD";
    private static final int DEFAULT_COMMAND_TIMEOUT_MS = 30000;
    public static final String XT9_UDB_ENTRIES = "xt9_udb_entries";
    private CustomWordsSynchronizeTransaction _transaction;

    public CustomWordsSynchronizeStartingState(CustomWordsSynchronizeTransaction transaction) {
        super(transaction);
        this._transaction = transaction;
    }

    @Override // com.nuance.nmdp.speechkit.transaction.ITransactionState
    public void enter() {
        try {
            this._transaction.createNmasResource();
            this._transaction.createPdxCommand(CUSTOM_WORDS_UPLOAD_COMMAND, DEFAULT_COMMAND_TIMEOUT_MS);
            Dictionary dataBlockDict = this._transaction.createDictionary();
            if (this._transaction.getDeleteAllFlag()) {
                dataBlockDict.addInteger("delete_all", 1);
            } else {
                Sequence dataListSeq = this._transaction.createSequence();
                Dictionary customWordsDict = this._transaction.createDictionary();
                customWordsDict.addUTF8String("id", XT9_UDB_ENTRIES);
                customWordsDict.addUTF8String("type", CUSTOM_WORDS_TYPE);
                Sequence actionsSeq = this._transaction.createSequence();
                if (this._transaction.getNeedClearAllCustomWordsFlag()) {
                    Dictionary clearAllDict = this._transaction.createDictionary();
                    clearAllDict.addUTF8String(NativeProtocol.WEB_DIALOG_ACTION, "clear_all");
                    actionsSeq.addDictionary(clearAllDict);
                    this._transaction.setNeedClearAllCustomWordsFlag(false);
                }
                Set<String> customWordsAddSet = this._transaction.getToBeAddedCustomWords();
                if (customWordsAddSet != null && customWordsAddSet.size() > 0) {
                    Dictionary addDict = this._transaction.createDictionary();
                    addDict.addUTF8String(NativeProtocol.WEB_DIALOG_ACTION, "add");
                    Dictionary addContentDict = this._transaction.createDictionary();
                    addContentDict.addSequence("list", getCustomWordsSequence(customWordsAddSet));
                    addDict.addDictionary("content", addContentDict);
                    actionsSeq.addDictionary(addDict);
                }
                Set<String> customWordsRemoveSet = this._transaction.getToBeRemovedCustomWords();
                if (customWordsRemoveSet != null && customWordsRemoveSet.size() > 0) {
                    Dictionary removeDict = this._transaction.createDictionary();
                    removeDict.addUTF8String(NativeProtocol.WEB_DIALOG_ACTION, "remove");
                    Dictionary removeContentDict = this._transaction.createDictionary();
                    removeContentDict.addSequence("list", getCustomWordsSequence(customWordsRemoveSet));
                    removeDict.addDictionary("content", removeContentDict);
                    actionsSeq.addDictionary(removeDict);
                }
                customWordsDict.addSequence("actions", actionsSeq);
                dataListSeq.addDictionary(customWordsDict);
                dataBlockDict.addSequence("data_list", dataListSeq);
            }
            this._transaction.sendDictParam("DATA_BLOCK", dataBlockDict);
            Dictionary uploadDoneDictParam = this._transaction.createDictionary();
            uploadDoneDictParam.addInteger("num_data_blocks", 1);
            Sequence checksumsSeq = this._transaction.createSequence();
            Dictionary checksum = this._transaction.createDictionary();
            checksum.addUTF8String("id", XT9_UDB_ENTRIES);
            checksum.addUTF8String("type", CUSTOM_WORDS_TYPE);
            checksum.addUTF8String("current_checksum", this._transaction.getOldChecksum());
            checksum.addUTF8String("new_checksum", this._transaction.getNewChecksum());
            checksum.addUTF8String("algorithm_id", this._transaction.getAlgorithmID());
            checksumsSeq.addDictionary(checksum);
            uploadDoneDictParam.addSequence("checksums", checksumsSeq);
            this._transaction.sendDictParam("UPLOAD_DONE", uploadDoneDictParam);
            this._transaction.endPdxCommand();
        } catch (Throwable tr) {
            Logger.error(this, "Error starting CustomWordsSynchronizeStartingState", tr);
            error(6);
        }
    }

    private Sequence getCustomWordsSequence(Collection<String> customWords) {
        Sequence customWordsSeq = this._transaction.createSequence();
        for (String w : customWords) {
            customWordsSeq.addUTF8String(w);
        }
        return customWordsSeq;
    }

    @Override // com.nuance.nmdp.speechkit.transaction.TransactionStateBase, com.nuance.nmdp.speechkit.transaction.ITransactionState
    public void queryResult(QueryResult result) {
        if (result == null || result.getResultType().compareTo(CUSTOM_WORDS_UPLOAD_COMMAND) != 0) {
            error(0);
            return;
        }
        Sequence resultList = result.getSequence("result_list");
        if (resultList == null || resultList.size() <= 0) {
            error(8);
            return;
        }
        Dictionary resultItem = resultList.getDictionary(0);
        String status = resultItem.getUTF8String("status");
        if (status.compareTo(GraphResponse.SUCCESS_KEY) != 0) {
            if (resultItem.getInteger("force_upload") == 1) {
                this._transaction.switchToState(new ErrorState(this._transaction, 7, "Data is out of sync", "Reset and upload all data to server again", true));
                return;
            } else {
                this._transaction.switchToState(new ErrorState(this._transaction, 6, null, null, true));
                return;
            }
        }
        int deletedAllUserInformation = 0;
        String id = null;
        String type = null;
        String checksum = null;
        int finalCount = 0;
        int forceUpload = 0;
        if (this._transaction.getDeleteAllFlag()) {
            deletedAllUserInformation = 1;
            this._transaction.setDeleteAllFlag(false);
        } else {
            id = resultItem.getUTF8String("id");
            type = resultItem.getUTF8String("type");
            checksum = resultItem.getUTF8String("checksum");
            finalCount = resultItem.getInteger("final_count");
            forceUpload = resultItem.getInteger("force_upload");
        }
        CustomWordsSynchronizeResult syncResult = new CustomWordsSynchronizeResult(id, type, status, checksum, finalCount, forceUpload, deletedAllUserInformation);
        this._transaction.switchToState(new FinishedState(this._transaction, syncResult));
    }
}
