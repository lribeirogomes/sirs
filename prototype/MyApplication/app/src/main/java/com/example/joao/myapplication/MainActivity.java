package com.example.joao.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


import android.app.PendingIntent;
import android.content.Intent;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.widget.Toast;

import java.security.Signature;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.KeyPair;
import java.security.KeyPairGenerator;

public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void doEC(View view) {
        try {
            //Generate EC Keys
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC");
            keyGen.initialize(224);
            KeyPair keys = keyGen.generateKeyPair();

            PrivateKey privKey = keys.getPrivate();
            //byte[] privKeyEncoded = privKey.getEncoded();
            PublicKey pubKey = keys.getPublic();
            //byte[] pubKeyEncoded = pubKey.getEncoded();

            //Sign some text
            Signature dsa = Signature.getInstance("SHA224withECDSA");
            dsa.initSign(privKey);
            String str = "This is a string to sign";
            byte[] strByte = str.getBytes("UTF-8");
            dsa.update(strByte);
            byte[] realSig = dsa.sign();


            //Verify the signature
            dsa.initVerify(pubKey);
            dsa.update(strByte);

            TextView text = (TextView) findViewById(R.id.textView);
            if (dsa.verify(realSig))
                text.setText("Signature verified successfully! Signature length: " + realSig.length + " bytes");
            else
                text.setText("Something went wrong!");

        } catch (Exception e) {   //Pokemon exception handling!!
            TextView text = (TextView) findViewById(R.id.textView);
            text.setText("Something went REALLY wrong!");
        }

    }

    public void sendSMS(View view) {
        final int SENT = 1;
        final short SMS_PORT = 8998;
        final String phoneNumber = "5556";       //Other emulator phone number

        String str = "This is a long sms text that we want to send in a data sms along with a 64 bytes signature";   //91 characters
        byte[] message = str.getBytes();

        PendingIntent sent = this.createPendingResult(SENT, new Intent(), PendingIntent.FLAG_UPDATE_CURRENT);
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendDataMessage(phoneNumber, null, SMS_PORT, message, sent, null);
        TextView text = (TextView) findViewById(R.id.textView);
        text.setText("Sending SMS to " + phoneNumber);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String toast = null;
        final int SENT = 1;
        switch (requestCode) {
            case SENT:
                switch (resultCode) {
                    case RESULT_OK:
                        toast = "SMS Sent successfully";
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        toast = "Generic Failure";
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        toast = "Radio Off";
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        toast = "Null Pdu";
                        break;
                }
                break;
        }

        if (toast != null) {
            Toast.makeText(this, toast, Toast.LENGTH_LONG).show();
        }

    }

}

