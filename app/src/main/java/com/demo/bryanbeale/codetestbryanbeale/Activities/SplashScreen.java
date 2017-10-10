package com.demo.bryanbeale.codetestbryanbeale.Activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Window;

import com.demo.bryanbeale.codetestbryanbeale.R;
import com.demo.bryanbeale.codetestbryanbeale.Services.ContactService;

public class SplashScreen extends Activity {

    BroadcastReceiver listener;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_splash_screen);

        listener = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Intent newIntent = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(newIntent);
            }
        };

        Thread task = new Thread(){
            @Override
            public void run() {
                try {
                    sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {

                    new ContactService(getApplicationContext()).fireOffInitialContactRetrieval();
                }
            }
        };

        task.start();



    }

    @Override
    public void onResume(){
        super.onResume();
        registerReceiver(listener, new IntentFilter(ContactService.CONTACTS_DONE));
    }

    public void onPause(){
        super.onPause();
        unregisterReceiver(listener);
        this.finish();
    }



}
