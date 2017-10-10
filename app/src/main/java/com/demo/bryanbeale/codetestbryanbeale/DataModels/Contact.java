package com.demo.bryanbeale.codetestbryanbeale.DataModels;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bryanbeale on 9/21/17.
 *
 */

public class Contact extends BaseObservable {

    private int id = 0;
    private String nameFirst = "";
    private String nameLast = "";
    private String birthDate = "";
    private String imagePath = "";
    private String facebookUser="";
    private String twitterHandle="";
    private List<Address> addressList = new ArrayList<>();
    private List<Phone> phoneNumbersList = new ArrayList<>();
    private List<Email> emailAddressList = new ArrayList<>();

    public Contact(){

    }
    @Bindable
    public int getId() {
        return id;
    }
    @Bindable
    public void setId(int id) {
        this.id = id;
    }
    @Bindable
    public String getNameFirst() {
        return nameFirst;
    }
    @Bindable
    public void setNameFirst(String nameFirst) {
        this.nameFirst = nameFirst;
    }
    @Bindable
    public String getNameLast() {
        return nameLast;
    }
    @Bindable
    public void setNameLast(String nameLast) {
        this.nameLast = nameLast;
    }
    @Bindable
    public String getBirthDate() {
        return birthDate;
    }
    @Bindable
    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }
    @Bindable
    public List<Address> getAddressList() {
        return addressList;
    }
    @Bindable
    public String getImagePath() {
        return imagePath;
    }
    @Bindable
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
    public void setAddressList(List<Address> addressList) {
        this.addressList = addressList;
    }
    @Bindable
    public List<Phone> getPhoneNumbersList() {
        return phoneNumbersList;
    }
    @Bindable
    public void setPhoneNumbersList(List<Phone> phoneNumbersList) {
        this.phoneNumbersList = phoneNumbersList;
    }
    @Bindable
    public List<Email> getEmailAddressList() {
        return emailAddressList;
    }
    @Bindable
    public void setEmailAddressList(List<Email> emailAddressList) {
        this.emailAddressList = emailAddressList;
    }

    public String getFacebookUser() {
        return facebookUser;
    }

    public void setFacebookUser(String facebookUser) {
        this.facebookUser = facebookUser;
    }

    public String getTwitterHandle() {
        return twitterHandle;
    }

    public void setTwitterHandle(String twitterHandle) {
        this.twitterHandle = twitterHandle;
    }
}
