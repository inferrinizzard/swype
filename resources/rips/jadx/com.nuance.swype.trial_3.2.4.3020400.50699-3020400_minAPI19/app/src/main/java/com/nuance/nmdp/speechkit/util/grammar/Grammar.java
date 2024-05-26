package com.nuance.nmdp.speechkit.util.grammar;

import com.nuance.nmdp.speechkit.util.pdx.PdxValue;
import java.util.List;
import java.util.Vector;

/* loaded from: classes.dex */
public class Grammar {
    private static final String CONTACTS_STR = "contacts";
    private static final String CUSTOM_WORDS_STR = "custom_words";
    private static final String HISTORY_STR = "history";
    private static final String INSTANT_ITEM_LIST_STR = "instant_item_list";
    private static final String PREDEFINED_STATIC_GRAMMARS_STR = "predefined_static_grammars";
    private static final String URI_STR = "uri";
    private int _checksum;
    private String _id;
    private String _interURI;
    private Vector<String> _itemList;
    private boolean _loadaslmh;
    private boolean _setLoadAsLmhFlag;
    private boolean _setTopicWeightFlag;
    private String _slotTag;
    private int _topicWeight;
    private GrammarType _type;
    private String _uri;
    private String _userId;

    /* loaded from: classes.dex */
    public enum GrammarType {
        CONTACTS,
        CUSTOMWORDS,
        HISTORY,
        URI,
        PREDEFINED_STATIC_GRAMMARS,
        INSTANT_ITEM_LIST
    }

    public Grammar(GrammarType type, String id) {
        this._setLoadAsLmhFlag = false;
        this._setTopicWeightFlag = false;
        this._id = id;
        if (type == GrammarType.CONTACTS || type == GrammarType.CUSTOMWORDS || type == GrammarType.HISTORY || type == GrammarType.URI || type == GrammarType.PREDEFINED_STATIC_GRAMMARS || type == GrammarType.INSTANT_ITEM_LIST) {
            this._type = type;
        } else {
            this._type = GrammarType.CONTACTS;
        }
        if (this._type == GrammarType.INSTANT_ITEM_LIST) {
            this._itemList = new Vector<>();
        }
    }

    public Grammar(GrammarType type, String id, int checksum) {
        this._setLoadAsLmhFlag = false;
        this._setTopicWeightFlag = false;
        this._id = id;
        if (type == GrammarType.CONTACTS || type == GrammarType.CUSTOMWORDS || type == GrammarType.HISTORY) {
            this._type = type;
        } else {
            this._type = GrammarType.CONTACTS;
        }
        this._checksum = checksum;
    }

    public Grammar(String id, String uri) {
        this._setLoadAsLmhFlag = false;
        this._setTopicWeightFlag = false;
        this._type = GrammarType.URI;
        this._id = id;
        this._uri = uri;
        this._checksum = 0;
    }

    public Grammar(String id, List<String> itemList) {
        this._setLoadAsLmhFlag = false;
        this._setTopicWeightFlag = false;
        this._type = GrammarType.INSTANT_ITEM_LIST;
        this._id = id;
        this._itemList = new Vector<>();
        for (String item : itemList) {
            if (item != null) {
                this._itemList.add(item);
            }
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
            case URI:
                return "uri";
            case PREDEFINED_STATIC_GRAMMARS:
                return PREDEFINED_STATIC_GRAMMARS_STR;
            case INSTANT_ITEM_LIST:
                return INSTANT_ITEM_LIST_STR;
            default:
                return CONTACTS_STR;
        }
    }

    public void setUri(String uri) {
        this._uri = uri;
    }

    public void setLoadAsLmh(boolean flag) {
        this._setLoadAsLmhFlag = true;
        this._loadaslmh = flag;
    }

    public void setTopicWeight(int weight) {
        this._setTopicWeightFlag = true;
        this._topicWeight = weight;
    }

    public void setSlotTag(String tag) {
        this._slotTag = tag;
    }

    public void setInterUri(String uri) {
        this._interURI = uri;
    }

    void setChecksum(int checksum) {
        this._checksum = checksum;
    }

    public void setUserId(String userId) {
        this._userId = userId;
    }

    public PdxValue.Dictionary getGrammarDictionary() {
        PdxValue.Dictionary dict = new PdxValue.Dictionary();
        dict.put("id", this._id);
        dict.put("type", getTypeStr());
        if (this._type != GrammarType.PREDEFINED_STATIC_GRAMMARS) {
            if (this._type == GrammarType.CONTACTS || this._type == GrammarType.CUSTOMWORDS || this._type == GrammarType.HISTORY) {
                dict.put("checksum", Integer.toString(this._checksum));
                if (this._userId != null) {
                    dict.put("common_user_id", this._userId);
                }
            } else if (this._type == GrammarType.URI) {
                dict.put("uri", this._uri);
            } else if (this._type == GrammarType.INSTANT_ITEM_LIST) {
                PdxValue.Sequence seq = new PdxValue.Sequence();
                if (!this._itemList.isEmpty()) {
                    for (int idx = 0; idx < this._itemList.size(); idx++) {
                        String item = this._itemList.elementAt(idx);
                        if (item != null) {
                            seq.add(item);
                        }
                    }
                }
                dict.put("item_list", seq);
            }
            if (this._setLoadAsLmhFlag) {
                dict.put("load_as_lmh", this._loadaslmh ? 1 : 0);
            }
            if (this._setTopicWeightFlag) {
                dict.put("topic_weight", this._topicWeight);
            }
            if (this._slotTag != null) {
                dict.put("slot_tag", this._slotTag);
            }
            if (this._interURI != null) {
                dict.put("interpretation_uri", this._interURI);
            }
        }
        return dict;
    }
}
