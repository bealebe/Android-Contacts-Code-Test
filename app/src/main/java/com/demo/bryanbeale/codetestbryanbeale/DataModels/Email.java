package com.demo.bryanbeale.codetestbryanbeale.DataModels;

import android.widget.EditText;

/**
 * Created by bryanbeale on 9/21/17.
 */

public class Email {

    private int id = 0;
    private int contactId = 0;
    private String email = "";
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
