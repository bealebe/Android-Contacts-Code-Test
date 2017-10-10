package com.demo.bryanbeale.codetestbryanbeale.Activities;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.demo.bryanbeale.codetestbryanbeale.DataModels.Contact;
import com.demo.bryanbeale.codetestbryanbeale.ListAdapters.ContactListAdapter;
import com.demo.bryanbeale.codetestbryanbeale.R;
import com.demo.bryanbeale.codetestbryanbeale.Services.ContactService;
import com.demo.bryanbeale.codetestbryanbeale.Util;
import com.demo.bryanbeale.codetestbryanbeale.databinding.ActivityMainBinding;
import com.demo.bryanbeale.codetestbryanbeale.databinding.CardViewQuickContactBinding;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemLongClickListener{

    private ActivityMainBinding binding;
    private ContactService contactService;
    private ContactListAdapter adapter;
    BroadcastReceiver listener;
    CardViewQuickContactBinding quick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        contactService = new ContactService(getApplicationContext());

        setupBinding();

        setupData();

    }

    private void setupData() {
        List<Contact> contactList = contactService.getContactList();

        adapter = new ContactListAdapter(contactList, this);

        binding.contactList.setAdapter(adapter);

        binding.searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });

        binding.swiper.setOnRefreshListener(this);

        for (TextView textView : Util.findChildrenByClass(binding.searchBar, TextView.class)) {
            textView.setTextColor(Color.WHITE);
            textView.setHintTextColor(Color.WHITE);
        }
        for (ImageView textView : Util.findChildrenByClass(binding.searchBar, ImageView.class)) {
            textView.setColorFilter(Color.WHITE);
        }


        listener = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                adapter = new ContactListAdapter(contactService.getContactList(), context);
                binding.contactList.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                binding.swiper.setRefreshing(false);
            }
        };

    }

    private void setupBinding() {
        binding.searchBar.setActivated(true);
        binding.searchBar.setQueryHint("Search for contacts here");
        binding.searchBar.setIconified(false);
        binding.searchBar.clearFocus();

        binding.fab.setOnClickListener(this);

        binding.contactList.setOnItemClickListener(this);
        binding.contactList.setFastScrollEnabled(true);
        binding.contactList.setFastScrollAlwaysVisible(true);
        binding.contactList.setOnItemLongClickListener(this);
    }

    public void navigateToDetails(){
        Intent newIntent = new Intent(MainActivity.this, ContactDetailActivity.class);
        startActivityForResult(newIntent, 0);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        contactService.setCurrentContact(null);
        onRefresh();
    }

        @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fab:
                navigateToDetails();
                break;
            case R.id.search_bar:
                binding.searchBar.setIconified(false);
                //binding.searchBar.requestFocus();
                break;
            case R.id.phone:
                openPhone();
                break;
            case R.id.email:
                openEmails();
                break;
            case R.id.address:
                openMaps();
                break;
            case R.id.faceBook:
                openFacebook();
                break;
            case R.id.twitter:
                openTwitter();
                break;
        }
    }

    private void openFacebook() {
        Intent intent = null;
        try {
            getPackageManager().getPackageInfo("com.facebook.katana", 0);
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/<id_here>"));
            throw new Exception();
        } catch (Exception e) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/"+ quick.faceBook.getText().toString().replaceAll(" ", "")));
        }finally {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    private void openMaps() {
        Intent intent = Util.viewOnMap(quick.address.getText().toString());
        startActivity(intent);
    }

    private void openTwitter() {
        Intent intent = null;
        try {

            // get the Twitter app if possible
            this.getPackageManager().getPackageInfo("com.twitter.android", 0);

            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name="+ quick.twitter.getText().toString().replaceAll("@", "").replaceAll(" ", "")));

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        } catch (Exception e) {

            // no Twitter app, revert to browser

            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/" + quick.twitter.getText().toString().replaceAll("@", "").replaceAll(" ", "")));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        }finally {
            startActivity(intent);
        }
    }

    private void openEmails() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", quick.email.getText().toString(), null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Body");
        startActivity(Intent.createChooser(emailIntent, "Send email..."));
    }

    private void openPhone() {
        if (Util.getPermissionInManifest(this, Manifest.permission.CALL_PHONE)) {
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + quick.phone.getText().toString()));
            startActivity(intent);
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        contactService.setCurrentContact(adapter.getItem(position));
        navigateToDetails();
    }

    @Override
    public void onRefresh() {
        contactService.fireOffInitialContactRetrieval();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        Contact contact = adapter.getItem(position);

        quick = CardViewQuickContactBinding.inflate(getLayoutInflater(), null, false);
        //show contact name

        quick.phone.setOnClickListener(this);
        quick.email.setOnClickListener(this);
        quick.address.setOnClickListener(this);
        quick.faceBook.setOnClickListener(this);
        quick.twitter.setOnClickListener(this);


        if (!contact.getPhoneNumbersList().isEmpty()){
            quick.phone.setText(contact.getPhoneNumbersList().get(0).getPhoneNumber());
        }else{
            quick.phone.setVisibility(View.GONE);
        }
        if (!contact.getEmailAddressList().isEmpty()){
            quick.email.setText(contact.getEmailAddressList().get(0).getEmail());
        }else{
            quick.email.setVisibility(View.GONE);
        }
        if (!contact.getAddressList().isEmpty()){
            quick.address.setText(contact.getAddressList().get(0).buildAddress());
        }else{
            quick.address.setVisibility(View.GONE);
        }
        if (!contact.getFacebookUser().isEmpty()){
            quick.faceBook.setText(contact.getFacebookUser());
        }else{
            quick.faceBook.setVisibility(View.GONE);
        }
        if (!contact.getTwitterHandle().isEmpty()){
            quick.twitter.setText(contact.getTwitterHandle());
        }else{
            quick.twitter.setVisibility(View.GONE);
        }

        AlertDialog.Builder bob = new AlertDialog.Builder(this);
        bob.setTitle(contact.getNameFirst() + " " + contact.getNameLast()).setView(quick.getRoot()).setPositiveButton("Okay", null).create().show();

        return true;
    }
}
