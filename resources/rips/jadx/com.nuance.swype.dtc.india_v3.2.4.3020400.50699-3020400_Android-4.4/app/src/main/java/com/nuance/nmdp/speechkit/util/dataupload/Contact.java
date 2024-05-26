package com.nuance.nmdp.speechkit.util.dataupload;

import com.nuance.nmdp.speechkit.util.pdx.PdxValue;

/* loaded from: classes.dex */
public class Contact {
    private String _firstname;
    private String _lastname;
    private String _middlename;
    private String _firstname_phonetic = null;
    private String _lastname_phonetic = null;
    private String _middlename_phonetic = null;
    private String _fullname = null;
    private String _nickname = null;
    private String _company = null;

    public Contact(String firstname, String middlename, String lastname) {
        this._firstname = firstname;
        this._lastname = lastname;
        this._middlename = middlename;
    }

    public void setFirstName(String name) {
        this._firstname = name;
    }

    public void setFirstNamePhonetic(String name) {
        this._firstname_phonetic = name;
    }

    public void setLastName(String name) {
        this._lastname = name;
    }

    public void setLastNamePhonetic(String name) {
        this._lastname_phonetic = name;
    }

    public void setMiddleName(String name) {
        this._middlename = name;
    }

    public void setMiddleNamePhonetic(String name) {
        this._middlename_phonetic = name;
    }

    public void setFullName(String name) {
        this._fullname = name;
    }

    public void setNickName(String name) {
        this._nickname = name;
    }

    public void setCompany(String company) {
        this._company = company;
    }

    public int getCheckSum() {
        int checksum = 0;
        if (this._firstname != null) {
            checksum = this._firstname.hashCode() + 0;
        }
        if (this._firstname_phonetic != null) {
            checksum += this._firstname_phonetic.hashCode();
        }
        if (this._lastname != null) {
            checksum += this._lastname.hashCode();
        }
        if (this._lastname_phonetic != null) {
            checksum += this._lastname_phonetic.hashCode();
        }
        if (this._fullname != null) {
            checksum += this._fullname.hashCode();
        }
        if (this._nickname != null) {
            checksum += this._nickname.hashCode();
        }
        if (this._middlename != null) {
            checksum += this._middlename.hashCode();
        }
        if (this._middlename_phonetic != null) {
            checksum += this._middlename_phonetic.hashCode();
        }
        if (this._company != null) {
            return checksum + this._company.hashCode();
        }
        return checksum;
    }

    public String getFirstName() {
        return this._firstname;
    }

    public String getFirstNamePhonetic() {
        return this._firstname_phonetic;
    }

    public String getLastName() {
        return this._lastname;
    }

    public String getLastNamePhonetic() {
        return this._lastname_phonetic;
    }

    public String getMiddleName() {
        return this._middlename;
    }

    public String getMiddleNamePhonetic() {
        return this._middlename_phonetic;
    }

    public String getFullName() {
        return this._fullname;
    }

    public String getNickName() {
        return this._nickname;
    }

    public String getCompany() {
        return this._company;
    }

    public void clearData() {
        this._firstname = null;
        this._firstname_phonetic = null;
        this._lastname = null;
        this._lastname_phonetic = null;
        this._middlename = null;
        this._middlename_phonetic = null;
        this._fullname = null;
        this._nickname = null;
        this._company = null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public PdxValue.Dictionary getContactDictionary() {
        PdxValue.Dictionary dict = new PdxValue.Dictionary();
        if (this._fullname != null) {
            dict.put("full_name", this._fullname);
        }
        if (this._firstname != null) {
            dict.put("first_name", this._firstname);
        }
        if (this._firstname_phonetic != null) {
            dict.put("first_name_phonetic", this._firstname_phonetic);
        }
        if (this._lastname != null) {
            dict.put("last_name", this._lastname);
        }
        if (this._lastname_phonetic != null) {
            dict.put("last_name_phonetic", this._lastname_phonetic);
        }
        if (this._nickname != null) {
            dict.put("nick_name", this._nickname);
        }
        if (this._middlename != null) {
            dict.put("middle_name", this._middlename);
        }
        if (this._middlename_phonetic != null) {
            dict.put("middle_name_phonetic", this._middlename_phonetic);
        }
        if (this._company != null) {
            dict.put("company", this._company);
        }
        return dict;
    }

    public String toString() {
        StringBuffer contact = new StringBuffer();
        if (this._firstname != null) {
            contact.append("firstname:" + this._firstname + "\n");
        }
        if (this._firstname_phonetic != null) {
            contact.append("firstname_phonetic:" + this._firstname_phonetic + "\n");
        }
        if (this._middlename != null) {
            contact.append("middlename:" + this._middlename + "\n");
        }
        if (this._middlename_phonetic != null) {
            contact.append("middlename_phonetic:" + this._middlename_phonetic + "\n");
        }
        if (this._lastname != null) {
            contact.append("lastname:" + this._lastname + "\n");
        }
        if (this._lastname_phonetic != null) {
            contact.append("lastname_phonetic:" + this._lastname_phonetic + "\n");
        }
        if (this._fullname != null) {
            contact.append("fullname:" + this._fullname + "\n");
        }
        if (this._nickname != null) {
            contact.append("nickname:" + this._nickname + "\n");
        }
        if (this._company != null) {
            contact.append("company:" + this._company + "\n");
        }
        return contact.toString();
    }
}
