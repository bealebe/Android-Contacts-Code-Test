package com.demo.bryanbeale.codetestbryanbeale.ListAdapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.demo.bryanbeale.codetestbryanbeale.Activities.ContactDetailActivity;
import com.demo.bryanbeale.codetestbryanbeale.DataModels.Phone;
import com.demo.bryanbeale.codetestbryanbeale.R;
import com.demo.bryanbeale.codetestbryanbeale.Services.EditBoxFactory;
import com.demo.bryanbeale.codetestbryanbeale.databinding.CardViewPhonesContactBinding;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by bryanbeale on 9/24/17.
 *
 */

public class PhoneNumberAdapter extends BaseAdapter implements View.OnClickListener{


    private List<Phone> phoneList = new ArrayList<>();
    private List<EditBoxFactory.EditBox> editTexts = new CopyOnWriteArrayList<>();
    private Context context;
    private CardViewPhonesContactBinding rowItemBinding;
    private EditBoxFactory factory = new EditBoxFactory();

    private int mContactId;

    public PhoneNumberAdapter(List<Phone> pPhoneList, Context pContext, int pContactId){
        phoneList = pPhoneList;
        context = pContext;
        mContactId = pContactId;
        factory.phoneNumberAdapter = this;
    }

    public List<EditBoxFactory.EditBox> getEditTexts() {
        return editTexts;
    }

    @Override
    public int getCount() {
        return phoneList.size();
    }

    @Override
    public Phone getItem(int position) {
        return phoneList.get(position);
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

        rowItemBinding = DataBindingUtil.inflate(inflater, R.layout.card_view_phones_contact, parent, false);

        rowItemBinding.phones.removeAllViews();

        for(Phone e : phoneList) {
            EditBoxFactory.EditBox editBox = factory.createEditBox(context, e.getPhoneNumber(), false, rowItemBinding.phones);
            editTexts.add(editBox);
            rowItemBinding.phones.addView(editBox);
        }

        rowItemBinding.buttonAdd.setOnClickListener(this);

        return rowItemBinding.getRoot();
    }

    @Override
    public void onClick(View v) {
        EditBoxFactory.EditBox editBox = factory.createEditBox(context, "", false, rowItemBinding.phones);
        editTexts.add(editBox);
        rowItemBinding.phones.addView(editBox);
        ContactDetailActivity.somethingChanged = true;
    }

    public void refreshViews(){
        if (rowItemBinding.getRoot() != null){
            for (EditBoxFactory.EditBox e : editTexts) {

                    if (e.delete || e.getText().isEmpty()) {
                        rowItemBinding.phones.removeView(e);
                        editTexts.remove(e);
                    }

            }

        }
    }
}
