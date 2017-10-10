package com.demo.bryanbeale.codetestbryanbeale.ListAdapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.demo.bryanbeale.codetestbryanbeale.Activities.ContactDetailActivity;
import com.demo.bryanbeale.codetestbryanbeale.DataModels.Address;
import com.demo.bryanbeale.codetestbryanbeale.R;
import com.demo.bryanbeale.codetestbryanbeale.Util;
import com.demo.bryanbeale.codetestbryanbeale.databinding.CardViewAddressesContactBinding;
import com.demo.bryanbeale.codetestbryanbeale.databinding.CardViewBaseAddressContactBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bryanbeale on 9/24/17.
 *
 */

public class AddressAdapter extends BaseAdapter implements View.OnClickListener, DialogInterface.OnClickListener{


    private static final int NEW_ADDRESS = 1000;
    private List<Address> addressList = new ArrayList<>();
    private Context context;
    private CardViewBaseAddressContactBinding rowItemBinding;
    private  CardViewAddressesContactBinding binding;
    private Address address;
    private int mContactId;
    private boolean addNewView = true;
    private int currentId;

    public AddressAdapter(List<Address> pPhoneList, Context pContext, int pContactId){
        addressList = pPhoneList;
        context = pContext;
        mContactId = pContactId;
    }

    @Override
    public int getCount() {
        return addressList.size();
    }

    @Override
    public Address getItem(int position) {
        return addressList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = null;
        if (inflater == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        rowItemBinding = DataBindingUtil.inflate(inflater, R.layout.card_view_base_address_contact, parent, false);

        rowItemBinding.addresses.removeAllViews();
        int i = 0;
        for (; i < addressList.size(); i++) {

            AddressView newPhone = new AddressView(context, i);
            newPhone.setBackgroundColor(Color.TRANSPARENT);
            newPhone.setHint("ADD NEW ADDRESS");
            newPhone.setOnClickListener(this);
            newPhone.setId(addressList.size() - i);
            newPhone.setFreezesText(true);
            rowItemBinding.addresses.addView(newPhone);

            if (!addressList.isEmpty() && i < addressList.size()) {
                newPhone.setText(addressList.get(i).buildAddress());
            }
        }

        AddressView newPhone = new AddressView(context, i);
        newPhone.setBackgroundColor(Color.TRANSPARENT);
        newPhone.setHint("ADD NEW ADDRESS");
        newPhone.setOnClickListener(this);
        newPhone.setId(addressList.size() + 1);
        newPhone.setFreezesText(true);
        rowItemBinding.addresses.addView(newPhone);

        return rowItemBinding.getRoot();
    }

    @Override
    public void onClick(View v) {

        currentId = v.getId();
        address = new Address();
        addNewView = true;
        if (v.getId() <= addressList.size()){
            address = addressList.get(v.getId() - 1);
            addNewView = false;
        }

        AlertDialog.Builder bob = new AlertDialog.Builder(context);

        binding =  CardViewAddressesContactBinding.inflate(LayoutInflater.from(context));

        binding.address1.setText(address.getFirstLine());
        binding.address2.setText(address.getSecondLine());
        binding.city.setText(address.getCity());
        binding.state.setText(address.getState());
        binding.zip.setText(address.getZipCode());
        binding.showMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = Util.viewOnMap(address.buildAddress());
                context.startActivity(intent);
            }

        });

        if (!binding.address1.getText().toString().isEmpty()){
            binding.showMap.setVisibility(View.VISIBLE);
        }
        else{
            binding.showMap.setVisibility(View.GONE);
        }
        bob.setTitle("Address").setView(binding.getRoot()).setPositiveButton("Finish", this).setNegativeButton("Delete", this).setNeutralButton("Cancel", null).create().show();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            ContactDetailActivity.somethingChanged = true;
            address.setContactId(mContactId);
            address.setCity(binding.city.getText().toString());
            address.setState(binding.state.getText().toString());
            address.setZipCode(binding.zip.getText().toString());
            address.setFirstLine(binding.address1.getText().toString());
            address.setSecondLine(binding.address2.getText().toString());

            if ((rowItemBinding.addresses.findViewById(currentId)) != null) {
                ((AddressView) rowItemBinding.addresses.findViewById(currentId)).setText(address.buildAddress());
                if (addNewView) {
                    addressList.add(address);
                    address = null;
                    AddressView newPhone = new AddressView(context, 0);
                    newPhone.setBackgroundColor(Color.TRANSPARENT);
                    newPhone.setHint("ADD NEW ADDRESS");
                    newPhone.setOnClickListener(this);
                    newPhone.setId(addressList.size() + 1);
                    newPhone.setFreezesText(true);
                    rowItemBinding.addresses.addView(newPhone);
                }
            } else {
                if (!addNewView) {
                    ContactDetailActivity.somethingChanged = true;
                    rowItemBinding.addresses.removeView(rowItemBinding.addresses.findViewById(currentId));
                    addressList.remove(currentId);
                }
            }
        }
        if (which == DialogInterface.BUTTON_NEGATIVE) {
            ContactDetailActivity.somethingChanged = true;
            rowItemBinding.addresses.removeView(rowItemBinding.addresses.findViewById(currentId));
            addressList.remove(currentId-1);
        }
    }

    public List<Address> getItems() {
        return addressList;
    }
}

class AddressView extends android.support.v7.widget.AppCompatButton{

    private int mIndex;

    public AddressView(Context context, int index) {
        super(context);
        mIndex = index;
        setFocusable(false);
        setTextColor(Color.BLUE);
        setClickable(true);
        setHintTextColor(Color.BLACK);
    }

    public int getmIndex() {
        return mIndex;
    }
}
