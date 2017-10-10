package com.demo.bryanbeale.codetestbryanbeale.Activities;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;

import com.demo.bryanbeale.codetestbryanbeale.BuildConfig;
import com.demo.bryanbeale.codetestbryanbeale.DataModels.Contact;
import com.demo.bryanbeale.codetestbryanbeale.DataModels.Email;
import com.demo.bryanbeale.codetestbryanbeale.DataModels.Phone;
import com.demo.bryanbeale.codetestbryanbeale.ListAdapters.AddressAdapter;
import com.demo.bryanbeale.codetestbryanbeale.ListAdapters.EmailAdapter;
import com.demo.bryanbeale.codetestbryanbeale.ListAdapters.PhoneNumberAdapter;
import com.demo.bryanbeale.codetestbryanbeale.R;
import com.demo.bryanbeale.codetestbryanbeale.Services.ContactService;
import com.demo.bryanbeale.codetestbryanbeale.Services.EditBoxFactory;
import com.demo.bryanbeale.codetestbryanbeale.Util;
import com.demo.bryanbeale.codetestbryanbeale.databinding.ActivityContactDetailBinding;
import com.demo.bryanbeale.codetestbryanbeale.databinding.CardViewBaseContactBinding;
import com.demo.bryanbeale.codetestbryanbeale.databinding.CardViewDeleteContactBinding;
import com.demo.bryanbeale.codetestbryanbeale.databinding.CardViewNameContactBinding;
import com.demo.bryanbeale.codetestbryanbeale.databinding.CardViewSocialFacebookBinding;
import com.demo.bryanbeale.codetestbryanbeale.databinding.CardViewSocialTwitterBinding;
import com.demo.bryanbeale.codetestbryanbeale.databinding.ContentContactDetailBinding;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ContactDetailActivity extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener{

    private final int CAMERA_PIC_REQUEST = 1000;
    private final int GALLERY_PIC_REQUEST = 1001;

    private ContactService contactService;
    private ImageView imageView;
    private AlertDialog dialog;
    private ActivityContactDetailBinding activityContactDetailBinding;
    private ContentContactDetailBinding contentContactDetailBinding;
    private CardViewBaseContactBinding baseContactBinding;
    private CardViewNameContactBinding nameContactBinding;
    private CardViewSocialTwitterBinding twitterBinding;
    private CardViewSocialFacebookBinding facebookBinding;
    private BroadcastReceiver listener;
    private boolean newContact;
    private Calendar myCalendar = Calendar.getInstance();
    private PhoneNumberAdapter phoneAdapter;
    private EmailAdapter emailAdapter;
    private AddressAdapter addressAdapter;
    private CardViewDeleteContactBinding deleteContactBinding;
    ProgressDialog progressDialog;
    private boolean deletePressed;
    boolean isNewPicture;
    public static boolean somethingChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityContactDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_contact_detail);

        contentContactDetailBinding = ContentContactDetailBinding.inflate(LayoutInflater.from(this), activityContactDetailBinding.rootView, true);

        Toolbar toolbar = activityContactDetailBinding.toolbar;
        CollapsingToolbarLayout colToolbar = activityContactDetailBinding.toolbarLayout;
        imageView = activityContactDetailBinding.bgheader;
        setSupportActionBar(toolbar);

        colToolbar.setTitle("Contact Details");

        imageView.setOnClickListener(this);
        imageView.setColorFilter(Color.rgb(123, 123, 123), android.graphics.PorterDuff.Mode.MULTIPLY);

        contactService = new ContactService(this);

        if (contactService.getCurrentContact() == null){
            contactService.setCurrentContact(new Contact());
            newContact = true;
        }

        FloatingActionButton fab = activityContactDetailBinding.fab;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (somethingChanged) save();
                else{
                    finish();
                }
            }
        });

        listener = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (progressDialog != null) {
                    if(progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                }

                if (!deletePressed) {
                    finish();
                }
            }
        };

        if (!(contactService.getCurrentContact().getImagePath() == null || contactService.getCurrentContact().getImagePath().equals(""))){
            imageView.setImageURI(new Uri.Builder().path(contactService.getCurrentContact().getImagePath()).build());

        }

        setupData();
    }

    @Override
    public void onBackPressed() {
        if (somethingChanged){
            AlertDialog.Builder bob = new AlertDialog.Builder(this);

            bob.setTitle("Save Changes?").setMessage("There were changes made. Would you like to save them?").setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    save();
                }
            }).setNeutralButton("Cancel", null).create().show();
        }
        else{
            finish();
        }
    }

    private void save() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Saving");
        progressDialog.setMessage("Saving Contact");
        progressDialog.show();

        List<Phone> pList = new ArrayList<Phone>();
        for (EditBoxFactory.EditBox e :
                phoneAdapter.getEditTexts()) {
            if (e.getText().length() > 0){
                Phone phone = new Phone();
                phone.setContactId(contactService.getCurrentContact().getId());
                phone.setPhoneNumber(e.getText().toString());
                pList.add(phone);
            }

        }

        List<Email> list = new ArrayList<Email>();
        for (EditBoxFactory.EditBox e :
                emailAdapter.getEditTexts()) {
            if (e.getText().length() > 0) {
                Email email = new Email();
                email.setContactId(contactService.getCurrentContact().getId());
                email.setEmail(e.getText().toString());
                list.add(email);
            }
        }
        String[] names = nameContactBinding.firstName.getText().toString().split(" ");
        if (names.length>0) {
            contactService.getCurrentContact().setNameFirst(names[0]);
            if (names.length > 1) {
                contactService.getCurrentContact().setNameLast(names[1]);
            }else{
                contactService.getCurrentContact().setNameLast("");
            }
        }

        contactService.getCurrentContact().setAddressList(addressAdapter.getItems());
        contactService.getCurrentContact().setPhoneNumbersList(pList);
        contactService.getCurrentContact().setEmailAddressList(list);
        contactService.getCurrentContact().setFacebookUser(facebookBinding.facebookEdit.getText().toString().replaceAll(" ", ""));
        contactService.getCurrentContact().setTwitterHandle(twitterBinding.twitterEdit.getText().toString().replaceAll(" ", ""));

        contactService.saveContact(newContact);

        if (isNewPicture) {
            Util.addImageToGallery(contactService.getCurrentContact().getImagePath(), this);
            isNewPicture = false;
        }

        if (newContact){
            newContact = false;
        }
        if (somethingChanged){
            somethingChanged = false;
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        registerReceiver(listener, new IntentFilter(ContactService.CONTACTS_DONE));
    }

    public void onPause(){
        super.onPause();
        unregisterReceiver(listener);
    }

    private void setupData() {
        LayoutInflater inflater = LayoutInflater.from(this);

        nameContactBinding = CardViewNameContactBinding.inflate(inflater, contentContactDetailBinding.cardList, true);

        baseContactBinding = CardViewBaseContactBinding.inflate(inflater, contentContactDetailBinding.cardList, true);

        phoneAdapter = new PhoneNumberAdapter(contactService.getCurrentContact().getPhoneNumbersList(), this, contactService.getCurrentContact().getId());

        emailAdapter = new EmailAdapter(contactService.getCurrentContact().getEmailAddressList(), this, contactService.getCurrentContact().getId());

        addressAdapter = new AddressAdapter(contactService.getCurrentContact().getAddressList(), this, contactService.getCurrentContact().getId());

        contentContactDetailBinding.cardList.addView(phoneAdapter.getView(0, null, contentContactDetailBinding.cardList));

        contentContactDetailBinding.cardList.addView(emailAdapter.getView(0, null, contentContactDetailBinding.cardList));

        contentContactDetailBinding.cardList.addView(addressAdapter.getView(0, null, contentContactDetailBinding.cardList));

        facebookBinding = CardViewSocialFacebookBinding.inflate(inflater, contentContactDetailBinding.cardList, true);

        twitterBinding = CardViewSocialTwitterBinding.inflate(inflater, contentContactDetailBinding.cardList, true);

        if (!newContact) {
            deleteContactBinding = CardViewDeleteContactBinding.inflate(inflater, contentContactDetailBinding.cardList, true);

            baseContactBinding.setContact(contactService.getCurrentContact());
            nameContactBinding.firstName.setText(contactService.getCurrentContact().getNameFirst() + " " + contactService.getCurrentContact().getNameLast());
            onContactChanged(false);
        }
        setUpSocialClicks();

    }

    private void setUpSocialClicks() {

            facebookBinding.facebookEdit.setTextColor(Color.BLUE);
            facebookBinding.facebookEdit.setOnClickListener(this);
            facebookBinding.facebookEdit.setText(contactService.getCurrentContact().getFacebookUser());

            twitterBinding.twitterEdit.setTextColor(Color.BLUE);
            twitterBinding.twitterEdit.setOnClickListener(this);
            twitterBinding.twitterEdit.setText(contactService.getCurrentContact().getTwitterHandle());

        nameContactBinding.firstName.setOnClickListener(this);
    }

    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_contact_detail, menu);
        setSupportActionBar(activityContactDetailBinding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activityContactDetailBinding.toolbar.setNavigationIcon(android.support.v7.appcompat.R.drawable.abc_ic_ab_back_material);
        activityContactDetailBinding.toolbar.setNavigationOnClickListener(this);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_change_image:
                showPicFileDialog();
                return true;
            default:
                onBackPressed();
                return true;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bgheader:
                showPicFileDialog();
                break;
            case R.id.camera_button:
            case R.id.camera:
                if (Util.getPermissionInManifest(this, Manifest.permission.CAMERA) && Util.getPermissionInManifest(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) && Util.getPermissionInManifest(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    openCamera();
                    somethingChanged = true;
                }
                break;
            case R.id.photo:
            case R.id.photo_button:
                if (Util.getPermissionInManifest(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    openFromGallery();
                    somethingChanged = true;
                 }
                break;
            case R.id.birthday:
                showDatePicker();
                somethingChanged = true;
                break;
            case R.id.deleteContact:
            case R.id.deleteButton:
                if (!deletePressed){
                    deleteContactBinding.deleteContact.setBackgroundColor(Util.getThemePrimaryColor(this));
                    deleteContactBinding.deleteButton.setText("Are you sure?");
                    deleteContactBinding.deleteButton.setTextColor(Color.WHITE);
                    deletePressed = true;
                }
                else{
                    contactService.deleteCurrentContact();
                    finish();
                }
                break;
            case R.id.facebookEdit:
                if (facebookBinding.facebookEdit.getText().toString().isEmpty()){
                    toggleFacebookCard();
                }
                else {
                    openFacebook();
                }
                break;
            case R.id.twitterEdit:
                if (twitterBinding.twitterEdit.getText().toString().isEmpty()){
                    toggleTwitterCard();
                }
                else {
                    openTwitter();
                }
                break;
            case R.id.editFacebookButton:
                toggleFacebookCard();
                somethingChanged = true;
                break;
            case R.id.editTwitterButton:
                toggleTwitterCard();
                somethingChanged = true;
                break;
            case R.id.firstName:
            case R.id.editName:
                toggleNameCard();
                somethingChanged = true;
                break;
            case android.R.id.home:
            default:
                onBackPressed();
                break;
        }
    }

    private void toggleTwitterCard() {
        AlertDialog.Builder bob = new AlertDialog.Builder(this);
        final EditText dialogText = new EditText(this);
        dialogText.setFreezesText(true);
        dialogText.setBackgroundColor(Color.TRANSPARENT);
        dialogText.setGravity(Gravity.CENTER_HORIZONTAL);
        dialogText.setInputType(InputType.TYPE_TEXT_VARIATION_URI);
        dialogText.selectAll();
        Util.showKeyboard(this, dialogText);
        dialogText.setText(twitterBinding.twitterEdit.getText());

        bob.setTitle("Enter Twitter Handle").setView(dialogText);
        bob.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                twitterBinding.twitterEdit.setText("");
                somethingChanged = true;
                Util.showKeyboard(getApplicationContext(), dialogText);

            }
        }).setPositiveButton("Confirm Changes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                twitterBinding.twitterEdit.setText(dialogText.getText());

                if (!twitterBinding.twitterEdit.getText().equals(dialogText.getText())) {
                    ContactDetailActivity.somethingChanged = true;
                    Util.showKeyboard(getApplicationContext(), dialogText);

                }
            }
        }).create().show();
    }

    private void toggleFacebookCard() {

        AlertDialog.Builder bob = new AlertDialog.Builder(this);
        final EditText dialogText = new EditText(this);
        dialogText.setFreezesText(true);
        dialogText.setBackgroundColor(Color.TRANSPARENT);
        dialogText.setGravity(Gravity.CENTER_HORIZONTAL);
        Util.showKeyboard(this, dialogText);
        dialogText.selectAll();
        dialogText.setText(facebookBinding.facebookEdit.getText());
        dialogText.setInputType(InputType.TYPE_TEXT_VARIATION_URI);
        bob.setTitle("Enter Facebook Username").setView(dialogText);
        bob.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                facebookBinding.facebookEdit.setText("");
                somethingChanged = true;
                Util.showKeyboard(getApplicationContext(), dialogText);

            }
        }).setPositiveButton("Confirm Changes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                facebookBinding.facebookEdit.setText(dialogText.getText());

                if (!twitterBinding.twitterEdit.getText().equals(dialogText.getText())) {
                    ContactDetailActivity.somethingChanged = true;
                    Util.showKeyboard(getApplicationContext(), dialogText);

                }
            }
        }).create().show();
    }

    private void toggleNameCard(){

        AlertDialog.Builder bob = new AlertDialog.Builder(this);
        final EditText dialogText = new EditText(this);
        dialogText.setFreezesText(true);
        dialogText.setBackgroundColor(Color.TRANSPARENT);
        dialogText.setGravity(Gravity.CENTER_HORIZONTAL);
        dialogText.setText(nameContactBinding.firstName.getText());
        dialogText.selectAll();
        Util.showKeyboard(this, dialogText);

        bob.setTitle("Enter First and Last Name").setView(dialogText);
        bob.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                nameContactBinding.firstName.setText("");
                Util.showKeyboard(getApplicationContext(), dialogText);
                somethingChanged = true;
            }
        }).setPositiveButton("Confirm Changes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                nameContactBinding.firstName.setText(dialogText.getText());

                if (!nameContactBinding.firstName.getText().equals(dialogText.getText())) {
                    ContactDetailActivity.somethingChanged = true;
                    activityContactDetailBinding.toolbarLayout.setTitle(nameContactBinding.firstName.getText().toString());
                    Util.showKeyboard(getApplicationContext(), dialogText);

                }
            }
        }).create().show();
    }

    private void openTwitter() {
        Intent intent = null;

        try {

            // get the Twitter app if possible

            this.getPackageManager().getPackageInfo("com.twitter.android", 0);

            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name="+ twitterBinding.twitterEdit.getText().toString().replaceAll("@", "").replaceAll(" ", "")));

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        } catch (Exception e) {

            // no Twitter app, revert to browser

            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/" + twitterBinding.twitterEdit.getText().toString().replaceAll("@", "").replaceAll(" ", "")));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        }finally {
            startActivity(intent);
        }

    }

    private void openFacebook() {
        Intent intent = null;
        try {
            getPackageManager().getPackageInfo("com.facebook.katana", 0);
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/<id_here>"));
            throw new Exception();
        } catch (Exception e) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/"+ facebookBinding.facebookEdit.getText().toString().replaceAll(" ", "")));
        }finally {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, this, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            datePickerDialog.getDatePicker().setLayoutMode(1);
        }

        if (contactService.getCurrentContact().getBirthDate() != null) {
            String[] datePeices = contactService.getCurrentContact().getBirthDate().split("/");

            DatePicker picker = datePickerDialog.getDatePicker();

            if (datePeices.length == 3) {
                picker.updateDate(Integer.valueOf(datePeices[2]), Integer.valueOf(datePeices[0]) - 1, Integer.valueOf(datePeices[1]));
            }
        }
        datePickerDialog.show();
    }

    private void updateBirthday() {
        String myFormat = "MM/dd/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());

        contactService.getCurrentContact().setBirthDate(sdf.format(myCalendar.getTime()));
        onContactChanged(false);
    }

    private void showPicFileDialog() {
        AlertDialog.Builder bob = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        View v = inflater.inflate(R.layout.dialog_camera_gallery, null);

        v.findViewById(R.id.photo).setOnClickListener(this);
        v.findViewById(R.id.photo_button).setOnClickListener(this);
        v.findViewById(R.id.camera).setOnClickListener(this);
        v.findViewById(R.id.camera_button).setOnClickListener(this);

        dialog = bob.setTitle("Change Image").setView(v).create();
        dialog.show();
    }

    private void openCamera() {
        String name = Util.dateToString(new Date(), "yyyy-MM-dd-hh-mm-ss");
        File destination = new File(Environment.getExternalStorageDirectory(), name + ".jpg");

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".Services.GenericFileProvider", destination));
        contactService.getCurrentContact().setImagePath(destination.getPath());
        startActivityForResult(intent, CAMERA_PIC_REQUEST);
    }

    private void openFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture for Contact"), GALLERY_PIC_REQUEST);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        Uri selectedImageUri;
        Bitmap image;
        try {
            if (resultCode == RESULT_OK) {
                switch (requestCode) {
                    case CAMERA_PIC_REQUEST:
                        File dest = new File(contactService.getCurrentContact().getImagePath());
                        image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".Services.GenericFileProvider", dest));
                        imageView.setImageBitmap(image);
                        isNewPicture = true;
                        break;
                    case GALLERY_PIC_REQUEST:
                        selectedImageUri = data.getData();
                        contactService.getCurrentContact().setImagePath(Util.getImagePath(selectedImageUri, this));
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inJustDecodeBounds = true;
                        imageView.setImageURI(selectedImageUri);
                        break;
                }
            }
            if (dialog != null && dialog.isShowing()){
                dialog.dismiss();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        myCalendar.set(Calendar.YEAR, year);
        myCalendar.set(Calendar.MONTH, month);
        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        updateBirthday();
    }

    public void onContactChanged(boolean setContactValues) {

        String[] names = nameContactBinding.firstName.getText().toString().split(" ");
        if (names.length>0) {
            contactService.getCurrentContact().setNameFirst(names[0]);
            if (names.length > 1) {
                contactService.getCurrentContact().setNameLast(names[1]);
            }else{
                contactService.getCurrentContact().setNameLast("");
            }
        }
        activityContactDetailBinding.toolbarLayout.setTitle(contactService.getCurrentContact().getNameFirst() + " " + contactService.getCurrentContact().getNameLast());

        baseContactBinding.birthday.setText(contactService.getCurrentContact().getBirthDate());
        baseContactBinding.age.setText(getAge());

    }

    private String getAge(){
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        if (contactService.getCurrentContact().getBirthDate() != null) {
            String[] datePeices = contactService.getCurrentContact().getBirthDate().split("/");


            if (datePeices.length == 3) {
                int year = Integer.valueOf(datePeices[2]);
                int month = Integer.valueOf(datePeices[0]) - 1;
                int day = Integer.valueOf(datePeices[1]);

                dob.set(year, month, day);

                int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

                if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
                    age--;
                }

                Integer ageInt = new Integer(age);
                String ageS = ageInt.toString();

                return "Age: "+ageS;
            } else {
                return "";
            }
        } else{
            return "";
        }

    }
}

