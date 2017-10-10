package com.demo.bryanbeale.codetestbryanbeale.ListAdapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.SectionIndexer;

import com.demo.bryanbeale.codetestbryanbeale.DataModels.Contact;
import com.demo.bryanbeale.codetestbryanbeale.R;
import com.demo.bryanbeale.codetestbryanbeale.Util;
import com.demo.bryanbeale.codetestbryanbeale.databinding.RowItemBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Created by bryanbeale on 9/21/17.
 */

public class ContactListAdapter extends BaseAdapter implements Filterable, SectionIndexer{

    private List<Contact> mContacts;
    private List<Contact> mContactFilterList;
    private ValueFilter valueFilter;
    private LayoutInflater inflater;
    private Context mContext;
    HashMap<String, Integer> mapIndex;
    String[] sections;

    public ContactListAdapter(List<Contact> pContacts, Context pContext){
        mContacts = pContacts;
        mContactFilterList = pContacts;
        mContext = pContext;

        mapIndex = new LinkedHashMap<String, Integer>();

        for (int x = 0; x < mContacts.size(); x++) {
            String nameFirst = mContacts.get(x).getNameFirst();
            if (nameFirst.isEmpty()){
                continue;
            }
            String ch = nameFirst.substring(0, 1);
            ch = ch.toUpperCase(Locale.US);

            // HashMap will prevent duplicates
            mapIndex.put(ch, x);
        }

        Set<String> sectionLetters = mapIndex.keySet();

        // create a list from the set to sort
        ArrayList<String> sectionList = new ArrayList<String>(sectionLetters);

        Log.d("sectionList", sectionList.toString());
        Collections.sort(sectionList);

        sections = new String[sectionList.size()];

        sectionList.toArray(sections);
    }

    @Override
    public int getCount() {
        return mContacts.size();
    }

    @Override
    public Contact getItem(int position) {
        return mContacts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (inflater == null) {
            inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        RowItemBinding rowItemBinding = DataBindingUtil.inflate(inflater, R.layout.row_item, parent, false);

        rowItemBinding.stringName.setText(mContacts.get(position).getNameFirst() + " " + mContacts.get(position).getNameLast());
        if (!mContacts.get(position).getPhoneNumbersList().isEmpty()){
            rowItemBinding.stringPhoneNumber.setText(mContacts.get(position).getPhoneNumbersList().get(0).getPhoneNumber());
        }
        else if (!mContacts.get(position).getEmailAddressList().isEmpty()){
            rowItemBinding.stringPhoneNumber.setText(mContacts.get(position).getEmailAddressList().get(0).getEmail());

        }
        try {
            if (mContacts.get(position).getImagePath().isEmpty()){
                throw new Exception();
            }
            rowItemBinding.rowImage.setImageBitmap(Util.getThumbnail(mContext.getContentResolver(), mContacts.get(position).getImagePath()));
        } catch (Exception e) {
            rowItemBinding.rowImage.setImageResource(R.mipmap.ic_launcher_round);
        }

        return rowItemBinding.getRoot();
    }

    @Override
    public Filter getFilter() {
        if (valueFilter == null){
            valueFilter = new ValueFilter();
        }
        return valueFilter;
    }

    @Override
    public String[] getSections() {
        return sections;
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        return mapIndex.get(sections[sectionIndex]);
    }

    @Override
    public int getSectionForPosition(int position) {
        return 0;
    }

    private class ValueFilter extends Filter{

        @Override
        protected FilterResults performFiltering(final CharSequence constraint) {
            FilterResults results = new FilterResults();

            if(constraint != null && constraint.length() > 0){
                List<Contact> filterlist = new ArrayList<>();

                for (int i = 0; i < mContactFilterList.size(); i++){
                    if ((mContactFilterList.get(i).getNameFirst().toUpperCase()).contains(constraint.toString().toUpperCase()) ||
                            (mContactFilterList.get(i).getNameLast().toUpperCase()).contains(constraint.toString().toUpperCase())){
                        filterlist.add(mContactFilterList.get(i));
                    }
                }
                results.count = filterlist.size();
                results.values = filterlist;
            }
            else{
                results.count = mContactFilterList.size();
                results.values = mContactFilterList;
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mContacts = (List<Contact>) results.values;
            notifyDataSetChanged();
        }
    }
}
