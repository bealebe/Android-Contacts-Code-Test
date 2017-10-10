package com.demo.bryanbeale.codetestbryanbeale.ListAdapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.demo.bryanbeale.codetestbryanbeale.Activities.ContactDetailActivity;
import com.demo.bryanbeale.codetestbryanbeale.DataModels.Email;
import com.demo.bryanbeale.codetestbryanbeale.R;
import com.demo.bryanbeale.codetestbryanbeale.Services.EditBoxFactory;
import com.demo.bryanbeale.codetestbryanbeale.databinding.CardViewEmailContactBinding;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by bryanbeale on 9/24/17.
 *
 */

public class EmailAdapter extends BaseAdapter implements View.OnClickListener {


    private List<Email> emails = new ArrayList<>();
    private List<EditBoxFactory.EditBox> editTexts = new CopyOnWriteArrayList<>();
    private Context context;
    private CardViewEmailContactBinding rowItemBinding;
    private EditBoxFactory factory = new EditBoxFactory();

    private int mContactId;

    public EmailAdapter(List<Email> pPhoneList, Context pContext, int pContactId){
        emails = pPhoneList;
        context = pContext;
        mContactId = pContactId;
        factory.emailAdapter = this;
    }

    public List<EditBoxFactory.EditBox> getEditTexts() {
        return editTexts;
    }

    @Override
    public int getCount() {
        return emails.size();
    }

    @Override
    public Email getItem(int position) {
        return emails.get(position);
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

        rowItemBinding = DataBindingUtil.inflate(inflater, R.layout.card_view_email_contact, parent, false);

        rowItemBinding.emails.removeAllViews();

        for(Email e : emails) {
            EditBoxFactory.EditBox editBox = factory.createEditBox(context, e.getEmail(), true, rowItemBinding.emails);
            editTexts.add(editBox);
            rowItemBinding.emails.addView(editBox);
        }

        rowItemBinding.buttonAdd.setOnClickListener(this);

        return rowItemBinding.getRoot();
    }

    @Override
    public void onClick(View v) {
        EditBoxFactory.EditBox editBox = factory.createEditBox(context, "", true, rowItemBinding.emails);
        editTexts.add(editBox);
        rowItemBinding.emails.addView(editBox);
        ContactDetailActivity.somethingChanged = true;
    }

    public void refreshViews(){
        if (rowItemBinding.getRoot() != null){
            for (EditBoxFactory.EditBox e : editTexts) {
                if (e.delete || e.getText().isEmpty()){
                    rowItemBinding.emails.removeView(e);
                    editTexts.remove(e);
                }
            }

        }
    }
}
