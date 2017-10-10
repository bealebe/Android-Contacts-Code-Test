package com.demo.bryanbeale.codetestbryanbeale.Services;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.demo.bryanbeale.codetestbryanbeale.Activities.ContactDetailActivity;
import com.demo.bryanbeale.codetestbryanbeale.ListAdapters.EmailAdapter;
import com.demo.bryanbeale.codetestbryanbeale.ListAdapters.PhoneNumberAdapter;
import com.demo.bryanbeale.codetestbryanbeale.R;
import com.demo.bryanbeale.codetestbryanbeale.Util;

/**
 * Created by bryanbeale on 9/26/17.
 *
 */

public class EditBoxFactory {

    public EmailAdapter emailAdapter;
    public PhoneNumberAdapter phoneNumberAdapter;

    public EditBox createEditBox(Context pContext, String pBoxText, boolean emailHint, ViewGroup parent){
        Log.d("EditBoxFactory", "Creating new Box");
        return new EditBox(pContext, pBoxText, emailHint, parent);
    }

    public class EditBox extends LinearLayout implements View.OnClickListener{

        private EditText dialogText;

        public boolean delete = false;
        private boolean isEmail = false;

        private int newBoxId;
        private int newImageId;

        final EditText newBox;
        final ImageButton newImage;
        private boolean isEditing;

        public EditBox(final Context context, String pBoxText, final boolean emailHint, final ViewGroup parent) {
            super(context);
            Log.d("EditBoxFactory", "Setting LinearLayout Commands");
            this.setOrientation(LinearLayout.HORIZONTAL);
            this.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            this.setWeightSum(1);
            //this.set
            LayoutParams imageParams = new LayoutParams(200, ViewGroup.LayoutParams.WRAP_CONTENT, .2f);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                newBoxId = View.generateViewId();
                newImageId = View.generateViewId();
            }else{
                newImageId= 2;
                newBoxId= 1;
            }

            Log.d("EditBoxFactory", "Creating box");
            newBox = new EditText(context);
            newBox.setGravity(Gravity.CENTER_HORIZONTAL);
            newBox.setId(newBoxId);
            newImage = new ImageButton(context);
            newImage.setId(newImageId);
            isEmail = emailHint;

            newBox.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, .8f));
            newBox.setMinimumWidth(700);
            newBox.setBackgroundColor(Color.TRANSPARENT);
            if (emailHint) {
                newBox.setHint("Enter Email");
                newBox.setInputType(InputType.TYPE_CLASS_TEXT);
            }
            else{
                newBox.setHint("Enter Phone");
                newBox.setInputType(InputType.TYPE_CLASS_PHONE);

            }
            newBox.setText(pBoxText);


            Log.d("EditBoxFactory", "Creating Image");
            newImage.setLayoutParams(imageParams);
            newImage.setImageResource(R.drawable.ic_done);
            newImage.setBackgroundColor(Color.TRANSPARENT);
            newImage.setOnClickListener(this);
            toggleBox();

            newBox.setFreezesText(true);


            this.addView(newBox);
            this.addView(newImage);

        }


        public void toggleBox(){
            if (newBox.getText().length() > 0) {
                newBox.setTextColor(Color.BLUE);
                newBox.setFocusable(false);
                newBox.setClickable(true);
                newBox.setOnClickListener(this);
                if (!isEmail){
                    newBox.setText(Util.formatPhoneNumber(newBox.getText().toString()));
                }
                newImage.setImageResource(R.drawable.ic_edit);
                isEditing = false;
            }
            else{
                newBox.setTextColor(Color.BLACK);
                newBox.setFocusable(true);
                newBox.setClickable(false);
                newBox.setOnClickListener(null);
                //newBox.requestFocus();
                newImage.setImageResource(R.drawable.ic_done);
                isEditing = true;
            }
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == newBoxId) {
                if (isEmail) {
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", ((EditText) v).getText().toString(), null));
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
                    emailIntent.putExtra(Intent.EXTRA_TEXT, "Body");
                    getContext().startActivity(Intent.createChooser(emailIntent, "Send email..."));
                } else {
                    if (Util.getPermissionInManifest((ContactDetailActivity)getContext(), Manifest.permission.CALL_PHONE)) {
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + ((EditText)v).getText().toString()));
                        getContext().startActivity(intent);;
                    }
                }
            }else if( v.getId() == newImageId) {
                if (!isEditing) {
                    AlertDialog.Builder bob = new AlertDialog.Builder(v.getContext());
                    bob.setTitle("Edit");
                    dialogText = new EditText(v.getContext());
                    dialogText.setFreezesText(true);
                    dialogText.setBackgroundColor(Color.TRANSPARENT);
                    dialogText.setGravity(Gravity.CENTER_HORIZONTAL);
                    newBox.setFocusable(true);
                    dialogText.setText(newBox.getText());
                    newBox.setFocusable(false);
                    if (!isEmail)
                    {
                        dialogText.setInputType(InputType.TYPE_CLASS_PHONE);
                    }
                    else{
                        dialogText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                    }
                    bob.setView(dialogText);
                    bob.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            delete = true;
                            if (isEmail) {
                                emailAdapter.refreshViews();
                            }else{
                                phoneNumberAdapter.refreshViews();
                            }
                            dialogText = null;
                            ContactDetailActivity.somethingChanged = true;
                        }
                    }).setPositiveButton("Confirm Changes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            newBox.setFocusable(true);
                            newBox.setText(dialogText.getText());
                            newBox.setFocusable(false);

                            if (!newBox.getText().equals(dialogText.getText())) {
                                ContactDetailActivity.somethingChanged = true;
                            }
                            dialogText = null;


                        }
                    }).create().show();
                }else {
                    if (isEditing) {
                        toggleBox();
                    }
                 }
            } else{
                if (isEmail) {
                    emailAdapter.refreshViews();
                }
                else{
                    phoneNumberAdapter.refreshViews();
                }
            }



        }

        public int getTextCount() {
            return newBox.getText().length();
        }

        public EditText getNewBox() {
            return newBox;
        }

        public String getText() {
            return newBox.getText().toString();
        }
    }
}


