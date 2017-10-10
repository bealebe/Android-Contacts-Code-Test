package com.demo.bryanbeale.codetestbryanbeale.Services;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.demo.bryanbeale.codetestbryanbeale.DataModels.Address;
import com.demo.bryanbeale.codetestbryanbeale.DataModels.Contact;
import com.demo.bryanbeale.codetestbryanbeale.DataModels.Email;
import com.demo.bryanbeale.codetestbryanbeale.DataModels.Phone;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bryanbeale on 9/21/17.
 *
 */

public class DBHandler extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 3;
    // Database Name
    private static final String DATABASE_NAME = "contacts_db";
    // table names
    private static final String TABLE_CONTACTS = "contacts";
    private static final String TABLE_ADDRESSES = "addresses";
    private static final String TABLE_PHONE_NUMBERS = "phone_numbers";
    private static final String TABLE_EMAIL = "email";


    //contacts columns;
    private static final String CONTACT_KEY_ID = "id";
    private static final String CONTACT_LAST_NAME = "last_name";
    private static final String CONTACT_FIRST_NAME = "first_name";
    private static final String CONTACT_BIRTH_DAY = "birth_day";
    private static final String CONTACT_IMAGE_PATH= "image_path";
    private static final String CONTACT_TWITTER="twitter_handle";
    private static final String CONTACT_FACEBOOK="facebook_username";

    //address columns
    private static final String ADDRESS_KEY_ID = "id";
    private static final String ADDRESS_CONTACT_ID = "contact_id";
    private static final String ADDRESS_FIRST_LINE = "first_line";
    private static final String ADDRESS_SECOND_LINE = "second_line";
    private static final String ADDRESS_CITY = "city";
    private static final String ADDRESS_STATE = "state";
    private static final String ADDRESS_ZIP = "zip";

    //Phone columns
    private static final String PHONE_KEY_ID = "id";
    private static final String PHONE_CONTACT_ID = "contact_id";
    private static final String PHONE_NUMBER = "number";

    //email columns
    private static final String EMAIL_KEY_ID = "id";
    private static final String EMAIL_CONTACT_ID = "contact_id";
    private static final String EMAIL_ADDRESS = "email";

    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "("
                + CONTACT_KEY_ID + " INTEGER PRIMARY KEY," + CONTACT_LAST_NAME + " TEXT, " + CONTACT_FIRST_NAME + " TEXT, " + CONTACT_BIRTH_DAY + " TEXT," + CONTACT_IMAGE_PATH + " TEXT," + CONTACT_FACEBOOK + " TEXT," +CONTACT_TWITTER + " TEXT" +")";
        db.execSQL(CREATE_CONTACTS_TABLE);

        String CREATE_ADDRESSES_TABLE = "CREATE TABLE " + TABLE_ADDRESSES + "("
                + ADDRESS_KEY_ID + " INTEGER PRIMARY KEY," + ADDRESS_CONTACT_ID + " INTEGER," + ADDRESS_FIRST_LINE + " TEXT, " + ADDRESS_SECOND_LINE + " TEXT, "
                + ADDRESS_CITY + " TEXT, " + ADDRESS_STATE + " TEXT, " + ADDRESS_ZIP + " TEXT" + ")";
        db.execSQL(CREATE_ADDRESSES_TABLE);

        String CREATE_PHONE_TABLE = "CREATE TABLE " + TABLE_PHONE_NUMBERS + "("
                + PHONE_KEY_ID + " INTEGER PRIMARY KEY," + PHONE_CONTACT_ID + " INTEGER, " + PHONE_NUMBER + " TEXT)";
        db.execSQL(CREATE_PHONE_TABLE);

        String CREATE_EMAIL_TABLE = "CREATE TABLE " + TABLE_EMAIL + "("
                + EMAIL_KEY_ID + " INTEGER PRIMARY KEY," + EMAIL_CONTACT_ID + " INTEGER, " + EMAIL_ADDRESS + " TEXT)";
        db.execSQL(CREATE_EMAIL_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ADDRESSES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PHONE_NUMBERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EMAIL);

        onCreate(db);
    }

    public void DeleteContact(Contact pContact){
        SQLiteDatabase db = getWritableDatabase();

        db.delete(TABLE_CONTACTS, CONTACT_KEY_ID + "=?", new String[]{String.valueOf(pContact.getId())});

        for (Address addr: pContact.getAddressList()){
            DeleteAddress(addr, db);
        }

        for (Phone ph: pContact.getPhoneNumbersList()){
            DeletePhone(ph, db);
        }

        for (Email email: pContact.getEmailAddressList()){
            DeleteEmail(email, db);
        }


        db.close();
    }

    public int getContactsCount(){
        String count = "SELECT * FROM " + TABLE_CONTACTS;
        SQLiteDatabase db = getReadableDatabase();

        Cursor c = db.rawQuery(count, null);

        int contactCount = c.getCount();

        c.close();

        return contactCount;
    }

    public void InsertContact(Contact pContact){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(CONTACT_LAST_NAME, pContact.getNameLast());
        values.put(CONTACT_FIRST_NAME, pContact.getNameFirst());
        values.put(CONTACT_BIRTH_DAY, pContact.getBirthDate());
        values.put(CONTACT_IMAGE_PATH, pContact.getImagePath());
        values.put(CONTACT_FACEBOOK, pContact.getFacebookUser());
        values.put(CONTACT_TWITTER, pContact.getTwitterHandle());

        int contactid = (int)db.insert(TABLE_CONTACTS, null, values);

        for (Address addr: pContact.getAddressList()){
            addr.setContactId(contactid);
            InsertAddress(addr, db);
        }

        for (Phone ph: pContact.getPhoneNumbersList()){
            ph.setContactId(contactid);

            InsertPhone(ph, db);
        }

        for (Email email: pContact.getEmailAddressList()){
            email.setContactId(contactid);

            InsertEmail(email, db);
        }

        db.close();
    }

    public int UpdateContact(Contact pContact){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        pContact.setTwitterHandle(pContact.getTwitterHandle().replaceAll("@", ""));

        values.put(CONTACT_LAST_NAME, pContact.getNameLast());
        values.put(CONTACT_FIRST_NAME, pContact.getNameFirst());
        values.put(CONTACT_BIRTH_DAY, pContact.getBirthDate());
        values.put(CONTACT_IMAGE_PATH, pContact.getImagePath());
        values.put(CONTACT_FACEBOOK, pContact.getFacebookUser());
        values.put(CONTACT_TWITTER, pContact.getTwitterHandle());

        DeletePhoneForContact(pContact.getId(), db);

        DeleteEmailForContact(pContact.getId(), db);

        DeleteAddressForContact(pContact.getId(), db);

        for (Address addr: pContact.getAddressList()){
            InsertAddress(addr, db);
        }

        for (Phone ph: pContact.getPhoneNumbersList()){

            InsertPhone(ph, db);
        }

        for (Email email: pContact.getEmailAddressList()){
            InsertEmail(email, db);
        }

        int returnVal = db.update(TABLE_CONTACTS, values, CONTACT_KEY_ID + " = ?", new String[]{String.valueOf(pContact.getId())});

        db.close();

        return returnVal;
    }

    private void DeletePhoneForContact(int pContactId, SQLiteDatabase db) {
        db.delete(TABLE_PHONE_NUMBERS, PHONE_CONTACT_ID + "=?", new String[]{String.valueOf(pContactId)});
    }

    private void DeleteEmailForContact(int pContactId, SQLiteDatabase db) {
        db.delete(TABLE_EMAIL, EMAIL_CONTACT_ID + "=?", new String[]{String.valueOf(pContactId)});
    }

    private void DeleteAddressForContact(int pContactId, SQLiteDatabase db) {
        db.delete(TABLE_ADDRESSES, ADDRESS_CONTACT_ID + "=?", new String[]{String.valueOf(pContactId)});
    }

    public Contact getContact(int id){
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(TABLE_CONTACTS, new String[]{ CONTACT_KEY_ID, CONTACT_FIRST_NAME, CONTACT_LAST_NAME, CONTACT_BIRTH_DAY, CONTACT_IMAGE_PATH, CONTACT_FACEBOOK, CONTACT_TWITTER}, CONTACT_KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null);

        if (cursor != null){
            cursor.moveToFirst();
        }

        Contact contact = new Contact();

        contact.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(CONTACT_KEY_ID))));
        contact.setNameFirst(cursor.getString(cursor.getColumnIndex(CONTACT_FIRST_NAME)));
        contact.setNameLast(cursor.getString(cursor.getColumnIndex(CONTACT_LAST_NAME)));
        contact.setBirthDate(cursor.getString(cursor.getColumnIndex(CONTACT_BIRTH_DAY)));
        contact.setImagePath(cursor.getString(cursor.getColumnIndex(CONTACT_IMAGE_PATH)));

        contact.setTwitterHandle(cursor.getString(cursor.getColumnIndex(CONTACT_TWITTER)));
        contact.setFacebookUser(cursor.getString(cursor.getColumnIndex(CONTACT_FACEBOOK)));

        contact.setAddressList(getAddressesForContact(contact.getId(), db));
        contact.setEmailAddressList(getEmailListForContact(contact.getId(), db));
        contact.setPhoneNumbersList(getPhoneListForContact(contact.getId(), db));

        db.close();
        return contact;
    }

    public List<Contact> getAllContacts(){
        SQLiteDatabase db = getReadableDatabase();
        List<Contact> contacts = new ArrayList<>();

        String selectAll = "SELECT * FROM " + TABLE_CONTACTS + " ORDER BY " + CONTACT_FIRST_NAME;

        Cursor cursor = db.rawQuery(selectAll, null);

        if (cursor.moveToFirst()){
            do {
                Contact contact = new Contact();
                contact.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(CONTACT_KEY_ID))));
                contact.setNameFirst(cursor.getString(cursor.getColumnIndex(CONTACT_FIRST_NAME)));
                contact.setNameLast(cursor.getString(cursor.getColumnIndex(CONTACT_LAST_NAME)));
                contact.setBirthDate(cursor.getString(cursor.getColumnIndex(CONTACT_BIRTH_DAY)));
                contact.setImagePath(cursor.getString(cursor.getColumnIndex(CONTACT_IMAGE_PATH)));
                contact.setTwitterHandle(cursor.getString(cursor.getColumnIndex(CONTACT_TWITTER)));
                contact.setFacebookUser(cursor.getString(cursor.getColumnIndex(CONTACT_FACEBOOK)));


                contact.setAddressList(getAddressesForContact(contact.getId(), db));
                contact.setEmailAddressList(getEmailListForContact(contact.getId(), db));
                contact.setPhoneNumbersList(getPhoneListForContact(contact.getId(), db));

                contacts.add(contact);
            }while (cursor.moveToNext());
        }

        db.close();

        return contacts;
    }

    public List<Address> getAddressesForContact(int pContactId, SQLiteDatabase db){
        List<Address> addresses = new ArrayList<>();

        String selectAll = "SELECT * FROM " + TABLE_ADDRESSES + " WHERE " + ADDRESS_CONTACT_ID + " = ?";

        Cursor cursor = db.rawQuery(selectAll, new String[]{String.valueOf(pContactId)});

        if (cursor != null && cursor.getCount() > 0){
            cursor.moveToFirst();
            do{
                Address add = new Address();
                add.setCity(cursor.getString(cursor.getColumnIndex(ADDRESS_CITY)));
                add.setContactId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(ADDRESS_CONTACT_ID))));
                add.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(ADDRESS_KEY_ID))));
                add.setFirstLine(cursor.getString(cursor.getColumnIndex(ADDRESS_FIRST_LINE)));
                add.setSecondLine(cursor.getString(cursor.getColumnIndex(ADDRESS_SECOND_LINE)));
                add.setState(cursor.getString(cursor.getColumnIndex(ADDRESS_STATE)));
                add.setZipCode(cursor.getString(cursor.getColumnIndex(ADDRESS_ZIP)));

                addresses.add(add);
            }while (cursor.moveToNext());
        }

        return addresses;
     }

    public void InsertAddress(Address pAddress, SQLiteDatabase db){
        ContentValues values = new ContentValues();

        values.put(ADDRESS_CONTACT_ID, pAddress.getContactId());
        values.put(ADDRESS_FIRST_LINE, pAddress.getFirstLine());
        values.put(ADDRESS_SECOND_LINE, pAddress.getSecondLine());
        values.put(ADDRESS_CITY, pAddress.getCity());
        values.put(ADDRESS_STATE, pAddress.getState());
        values.put(ADDRESS_ZIP, pAddress.getZipCode());

        db.insert(TABLE_ADDRESSES, null, values);
    }

    public int UpdateAddress(Address pAddress, SQLiteDatabase db){
        ContentValues values = new ContentValues();

        values.put(ADDRESS_CONTACT_ID, pAddress.getContactId());
        values.put(ADDRESS_FIRST_LINE, pAddress.getFirstLine());
        values.put(ADDRESS_SECOND_LINE, pAddress.getSecondLine());
        values.put(ADDRESS_CITY, pAddress.getCity());
        values.put(ADDRESS_STATE, pAddress.getState());
        values.put(ADDRESS_ZIP, pAddress.getZipCode());

        return db.update(TABLE_ADDRESSES, values, ADDRESS_KEY_ID + " = ?", new String[]{String.valueOf(pAddress.getId())});
    }

    private void DeleteAddress(Address addr, SQLiteDatabase db) {
        db.delete(TABLE_ADDRESSES, ADDRESS_KEY_ID + "=?", new String[]{String.valueOf(addr.getId())});
    }

    private void DeleteAddress(Address addr) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_ADDRESSES, ADDRESS_KEY_ID + "=?", new String[]{String.valueOf(addr.getId())});
        db.close();
    }

    public List<Email> getEmailListForContact(int pContactId, SQLiteDatabase db){
        List<Email> emails = new ArrayList<>();

        Cursor cursor = db.query(TABLE_EMAIL, new String[]{ EMAIL_ADDRESS, EMAIL_CONTACT_ID, EMAIL_KEY_ID}, EMAIL_CONTACT_ID + "=?",
                new String[]{String.valueOf(pContactId)}, null, null, null);


        if (cursor != null && cursor.getCount() > 0){
            cursor.moveToFirst();
            do{
                Email em = new Email();
                em.setEmail(cursor.getString(cursor.getColumnIndex(EMAIL_ADDRESS)));
                em.setContactId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(EMAIL_CONTACT_ID))));
                em.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(EMAIL_KEY_ID))));

                emails.add(em);
            }while (cursor.moveToNext());
        }

        return emails;
    }

    public void InsertEmail(Email pEmail, SQLiteDatabase db){
        ContentValues values = new ContentValues();

        values.put(EMAIL_CONTACT_ID, pEmail.getContactId());
        values.put(EMAIL_ADDRESS, pEmail.getEmail());

        db.insert(TABLE_EMAIL, null, values);
    }

    public int UpdateEmail(Email pEmail, SQLiteDatabase db){

        ContentValues values = new ContentValues();

        values.put(EMAIL_CONTACT_ID, pEmail.getContactId());
        values.put(EMAIL_ADDRESS, pEmail.getEmail());

        return db.update(TABLE_EMAIL, values, EMAIL_KEY_ID + " = ?", new String[]{String.valueOf(pEmail.getId())});

    }

    private void DeleteEmail(Email email, SQLiteDatabase db) {
        db.delete(TABLE_EMAIL, EMAIL_KEY_ID + "=?", new String[]{String.valueOf(email.getId())});
    }

    private void DeleteEmail(Email email) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_EMAIL, EMAIL_KEY_ID + "=?", new String[]{String.valueOf(email.getId())});
        db.close();
    }

    public List<Phone> getPhoneListForContact(int pContactId, SQLiteDatabase db){

        List<Phone> numbers = new ArrayList<>();

        Cursor cursor = db.query(TABLE_PHONE_NUMBERS, new String[]{ PHONE_NUMBER, PHONE_KEY_ID, PHONE_CONTACT_ID}, PHONE_CONTACT_ID + "=?",
                new String[]{String.valueOf(pContactId)}, null, null, null);


        if (cursor != null && cursor.getCount() > 0){
            cursor.moveToFirst();
            do{
                Phone p = new Phone();
                p.setPhoneNumber(cursor.getString(cursor.getColumnIndex(PHONE_NUMBER)));
                p.setContactId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(PHONE_CONTACT_ID))));
                p.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(PHONE_KEY_ID))));

                numbers.add(p);
            }while (cursor.moveToNext());
        }

        return numbers;
    }

    public void InsertPhone(Phone pPhone, SQLiteDatabase db){
        boolean hasDB = db != null;

        if (!hasDB){
            db = getWritableDatabase();
        }

        ContentValues values = new ContentValues();

        values.put(PHONE_CONTACT_ID, pPhone.getContactId());
        values.put(PHONE_NUMBER, pPhone.getPhoneNumber());

        db.insert(TABLE_PHONE_NUMBERS, null, values);

        if (!hasDB){
            db.close();
        }
    }

    public int UpdatePhone(Phone pPhone, SQLiteDatabase db){
        boolean hasDB = db != null;

        if (!hasDB){
            db = getWritableDatabase();
        }

        ContentValues values = new ContentValues();

        values.put(PHONE_CONTACT_ID, pPhone.getContactId());
        values.put(PHONE_NUMBER, pPhone.getPhoneNumber());

        int updateVal = db.update(TABLE_PHONE_NUMBERS, values, PHONE_KEY_ID + " = ?", new String[]{String.valueOf(pPhone.getId())});

        if (!hasDB){
            db.close();
        }

        return updateVal;
    }


    private void DeletePhone(Phone ph, SQLiteDatabase db) {
        db.delete(TABLE_PHONE_NUMBERS, PHONE_KEY_ID + "=?", new String[]{String.valueOf(ph.getId())});
    }

    public void DeletePhone(Phone ph) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_PHONE_NUMBERS, PHONE_KEY_ID + "=?", new String[]{String.valueOf(ph.getId())});
        db.close();
    }
}
