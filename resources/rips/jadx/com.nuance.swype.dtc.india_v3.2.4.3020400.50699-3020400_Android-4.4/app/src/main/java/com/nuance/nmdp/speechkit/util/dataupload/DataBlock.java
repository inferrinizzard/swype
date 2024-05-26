package com.nuance.nmdp.speechkit.util.dataupload;

import com.nuance.nmdp.speechkit.util.pdx.PdxValue;
import java.util.Vector;

/* loaded from: classes.dex */
public class DataBlock {
    private int _deleteall = 0;
    private String _userId = null;
    private Vector<Data> _datalist = new Vector<>();

    public void setDeleteAll(boolean delete) {
        this._deleteall = delete ? 1 : 0;
    }

    public void clearDataBlock() {
        this._datalist.removeAllElements();
    }

    public void addData(Data data) {
        if (data != null) {
            this._datalist.add(data);
        }
    }

    public void removeData(Data data) {
        if (data != null) {
            this._datalist.remove(data);
        }
    }

    public Vector<Data> getDataList() {
        return this._datalist;
    }

    public void setUserId(String userId) {
        this._userId = userId;
    }

    public int getChecksum() {
        int checksum = 0;
        for (int idx = 0; idx < this._datalist.size(); idx++) {
            Data data = this._datalist.elementAt(idx);
            if (data != null) {
                checksum += data.getChecksum();
            }
        }
        return checksum;
    }

    public PdxValue.Dictionary getDataBlockDictionary() {
        PdxValue.Dictionary datadict;
        PdxValue.Dictionary dict = new PdxValue.Dictionary();
        dict.put("delete_all", this._deleteall);
        if (this._userId != null) {
            dict.put("common_user_id", this._userId);
        }
        if (!this._datalist.isEmpty()) {
            PdxValue.Sequence seq = new PdxValue.Sequence();
            for (int idx = 0; idx < this._datalist.size(); idx++) {
                Data d = this._datalist.elementAt(idx);
                if (d != null && (datadict = d.getDataDictionary()) != null) {
                    seq.add(datadict);
                }
            }
            dict.put("data_list", seq);
        }
        return dict;
    }

    public String toString() {
        StringBuffer datablock = new StringBuffer();
        datablock.append("delete_all:" + this._deleteall + "\n");
        datablock.append("checksum:" + getChecksum() + "\n");
        if (!this._datalist.isEmpty()) {
            int size = this._datalist.size();
            datablock.append("data_list: elements " + size + "\n");
            for (int idx = 0; idx < size; idx++) {
                Data d = this._datalist.elementAt(idx);
                if (d != null) {
                    datablock.append("data: " + idx + "\n");
                    datablock.append(d.toString());
                }
            }
        }
        return datablock.toString();
    }
}
