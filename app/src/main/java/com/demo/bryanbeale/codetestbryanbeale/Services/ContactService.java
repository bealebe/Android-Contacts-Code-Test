package com.demo.bryanbeale.codetestbryanbeale.Services;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.demo.bryanbeale.codetestbryanbeale.DataModels.Address;
import com.demo.bryanbeale.codetestbryanbeale.DataModels.Contact;
import com.demo.bryanbeale.codetestbryanbeale.DataModels.Email;
import com.demo.bryanbeale.codetestbryanbeale.DataModels.Phone;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bryan beale on 9/21/17.
 *
 */

public class ContactService {


    public static final String CONTACTS_DONE = "CONTACTS_DONE";

    public void fireOffInitialContactRetrieval() {
        new GetContactsAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, dbMethodTypes.ALL_CONTACTS.value);
    }

    public void saveContact(boolean newContact) {
        if(newContact){
            new GetContactsAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, dbMethodTypes.INSERT_CONTACT.value);
        } else{
            new GetContactsAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, dbMethodTypes.UPDATE_CONTACT.value);
        }
    }

    public void deleteCurrentContact() {
        new GetContactsAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, dbMethodTypes.DELETE_CONTACT.value);
    }

    private enum dbMethodTypes{
        ALL_CONTACTS(0),
        ONE_CONTACT(1),
        DELETE_CONTACT(2),
        UPDATE_CONTACT(3),
        INSERT_CONTACT(4),
        CONTACT_COUNT(5);


        private int value;


        dbMethodTypes(int value){
            this.value = value;
        }

        public static dbMethodTypes fromInt(int value){
            switch (value){
                case 0:
                    return ALL_CONTACTS;
                case 1:
                    return ONE_CONTACT;
                case 2:
                    return DELETE_CONTACT;
                case 3:
                    return UPDATE_CONTACT;
                case 4:
                    return INSERT_CONTACT;
                case 5:
                    return CONTACT_COUNT;
                default:
                    return ALL_CONTACTS;
            }
        }
    }

    private static List<Contact> contactList = new ArrayList<>();
    private static Contact currentContact;
    private DBHandler db;
    private Context context;

    public  ContactService(Context context){
        this.context = context;
        db = new DBHandler(context);
    }

    private int getContactCount() {
       return db.getContactsCount();
    }

    private void insertContact() {
        db.InsertContact(currentContact);
    }

    private void updateContact() {
        db.UpdateContact(currentContact);
    }

    private void deleteContact() {
        db.DeleteContact(currentContact);
    }

    private void getContact(Integer param) {
       currentContact = db.getContact(param);
    }

    private void getAllContacts() {
        if (getContactCount() <= 0){
            buildMockData();
        }

        contactList = db.getAllContacts();
    }

    public List<Contact> getContactList() {
        return contactList;
    }

    public Contact getCurrentContact(){
        return currentContact;
    }

    public void setCurrentContact(Contact currentContact) {
        ContactService.currentContact = currentContact;
    }

    private class GetContactsAsyncTask extends AsyncTask<Integer, Void, Void>{

        @Override
        public void onPreExecute(){

        }

        @Override
        protected Void doInBackground(Integer... params) {


            switch (dbMethodTypes.fromInt(params[0])){

                case ALL_CONTACTS:
                    getAllContacts();
                    break;
                case ONE_CONTACT:
                    getContact(params[1]);
                    break;
                case DELETE_CONTACT:
                    deleteContact();
                    currentContact = null;
                    break;
                case UPDATE_CONTACT:
                    updateContact();
                    //currentContact = null;
                    break;
                case INSERT_CONTACT:
                    insertContact();
                    getAllContacts();
                   // currentContact = null;
                    break;
                case CONTACT_COUNT:
                    getContactCount();
                    break;
            }

            return null;
        }

        public void onPostExecute(Void paa){
            context.sendBroadcast(new Intent(CONTACTS_DONE));
        }

    }

    private void buildMockData() {

        List<Address> mockadd = new ArrayList<>();
        List<Email> emails = new ArrayList<>();
        List<Phone> phones = new ArrayList<>();
        int i = 1;

        Contact con = new Contact();
        con.setId(i);
        con.setNameFirst("Bryan");
        con.setNameLast("Beale");
        con.setBirthDate("09/11/1990");

        Address add1 = new Address();
        add1.setZipCode("78954");
        add1.setState("Texas");
        add1.setCity("Fake City");
        add1.setId(i);
        add1.setFirstLine("3 Fake Land");
        add1.setSecondLine("");
        add1.setContactId(con.getId());
        mockadd.add(add1);

        Email emal = new Email();
        emal.setEmail("bryan.beale@gmail.com");
        emal.setContactId(con.getId());
        emal.setId(i);
        emails.add(emal);

        Phone p = new Phone();
        p.setId(i);
        p.setPhoneNumber("(936) 256-4587");
        p.setContactId(con.getId());
        phones.add(p);

        con.setAddressList(mockadd);
        con.setEmailAddressList(emails);
        con.setPhoneNumbersList(phones);

        db.InsertContact(con);

        i++;

        mockadd= new ArrayList<>();
        emails = new ArrayList<>();
        phones = new ArrayList<>();
        con = new Contact();
        con.setId(i);
        con.setNameFirst("Bob");
        con.setNameLast("Barker");
        con.setBirthDate("06/22/1983");

        add1 = new Address();
        add1.setZipCode("75657");
        add1.setState("Texas");
        add1.setCity("Nowhere");
        add1.setId(i);
        add1.setFirstLine("1234 Fake Street");
        add1.setSecondLine("");
        add1.setContactId(con.getId());
        mockadd.add(add1);

        emal = new Email();
        emal.setEmail("Bob@fake.com");
        emal.setContactId(con.getId());
        emal.setId(i);
        emails.add(emal);

        p = new Phone();
        p.setId(i);
        p.setPhoneNumber("(936) 759-8879");
        p.setContactId(con.getId());
        phones.add(p);

        con.setAddressList(mockadd);
        con.setEmailAddressList(emails);
        con.setPhoneNumbersList(phones);

        db.InsertContact(con);

    }



}
