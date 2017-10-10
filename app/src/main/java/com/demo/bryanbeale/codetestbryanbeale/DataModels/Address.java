package com.demo.bryanbeale.codetestbryanbeale.DataModels;

/**
 * Created by bryanbeale on 9/21/17.
 */

public class Address {

    public Address(){

    }
   private int id = 0;
   private int contactId = 0;
   private String firstLine = "";
   private String secondLine ="";
   private String city ="";
   private String state ="";
   private String zipCode = "";

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

    public String getFirstLine() {
        return firstLine;
    }

    public void setFirstLine(String firstLine) {
        this.firstLine = firstLine;
    }

    public String getSecondLine() {
        return secondLine;
    }

    public void setSecondLine(String secondLine) {
        this.secondLine = secondLine;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String buildAddress() {

        if (secondLine.isEmpty()){
            return firstLine + " ,\n" + city + ", " + state + " " + zipCode;
        }
        else{
            return firstLine + ", \n" + secondLine + ",\n" + city + ", " + state + " " + zipCode;
        }


    }
}
