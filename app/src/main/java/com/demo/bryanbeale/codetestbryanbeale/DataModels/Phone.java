package com.demo.bryanbeale.codetestbryanbeale.DataModels;

import android.widget.EditText;

/**
 * Created by bryanbeale on 9/21/17.
 */

public class Phone {

    private int id = 0;
    private int contactId = 0;
    private String phoneNumber = "";
    private EditText editText;

    public EditText getEditText() {
        return editText;
    }

    public void setEditText(EditText editText) {
        this.editText = editText;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getContactId() {
        return contactId;
    }

    public void setContactId(int contactId) {
        this.contactId = contactId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }


}
