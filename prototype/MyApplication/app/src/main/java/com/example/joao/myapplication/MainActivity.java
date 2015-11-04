package com.example.joao.myapplication;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;


import android.app.PendingIntent;
import android.content.Intent;
import android.telephony.SmsManager;
import android.widget.Toast;

import org.spongycastle.jce.spec.IESParameterSpec;
import org.spongycastle.util.encoders.Hex;

import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;
import java.security.Signature;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;

public class MainActivity extends AppCompatActivity {


    static {
        Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);
    }

    private KeyPair ECKeyPair = null;

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

    private KeyPair getECKeys() throws NoSuchAlgorithmException, NoSuchProviderException {
        //Generate EC Keys

        if (ECKeyPair == null){
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC", "BC");
            keyGen.initialize(224, new SecureRandom());
            ECKeyPair = keyGen.generateKeyPair();
        }
        return ECKeyPair;

    }

    private void doRSAEncryption(byte[] message){
        try{
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048, new SecureRandom());

            KeyPair keys = keyGen.generateKeyPair();

            PrivateKey privKey = keys.getPrivate();
            PublicKey pubKey = keys.getPublic();

            Cipher cipher = Cipher.getInstance("RSA");

            cipher.init(Cipher.ENCRYPT_MODE, pubKey);

            byte[] encrypted = cipher.doFinal(message);

            TextView text = (TextView) findViewById(R.id.textView);
            text.setText("output length: " + encrypted.length + " bytes");

        }catch (Exception e){
            TextView text = (TextView) findViewById(R.id.textView);
            text.setText(e.getMessage());
        }
    }

    private void doECEncryption(byte[] message){ //TODO: Study ECIES, this is a real mess!
        try {
            //Get EC Keys
            KeyPair keys = getECKeys();
            PrivateKey privKey = keys.getPrivate();
            //byte[] privKeyEncoded = privKey.getEncoded();
            PublicKey pubKey = keys.getPublic();
            //byte[] pubKeyEncoded = pubKey.getEncoded();


            Cipher cipher = Cipher.getInstance("ECIES"); //, "SC"); works this way but I have no idea what it means ("Spongy castle"??? why does it accept BC everywhere else?)
            IESParameterSpec iesParams = new IESParameterSpec(null, null, 0, 0); //MAC key size 0 works, wonder whats happening under the hood...
            cipher.init(Cipher.ENCRYPT_MODE, pubKey, iesParams);
            byte[] encrypted = cipher.doFinal(message);


            Cipher dcipher = Cipher.getInstance("ECIES");
            IESParameterSpec dIesParams = new IESParameterSpec(null, null, 0, 0); //MAC key size 0 works, wonder whats happening under the hood...
            dcipher.init(Cipher.DECRYPT_MODE, privKey, dIesParams);
            byte[] decrypted = dcipher.doFinal(encrypted);

            TextView text = (TextView) findViewById(R.id.textView);
            if( Arrays.equals(message, decrypted))
                text.setText("Encryption successfull! output length: " + encrypted.length + " bytes Encrypted message: " + Hex.toHexString(encrypted));
            else
                text.setText("Failed! Decryption output not equal to original message!");


        } catch (Exception e) {   //Pokemon exception handling!!
            TextView text = (TextView) findViewById(R.id.textView);
            text.setText(e.getMessage());
        }

    }

    public void clickECIES(View view){
        byte[] message = { 3, 2 ,5, 6, 3, 12, 3, 4, 5, 2, 4, 5, 6, 32, 45, 2}; //128bits long This is where we place the session key
        doECEncryption(message);
    }

    public void doECSignature(View view) {
        try {
            KeyPair keys = getECKeys();


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

    private byte[] generateAESKey() throws NoSuchAlgorithmException{
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128);
        Key key = keyGen.generateKey();
        return key.getEncoded();
    }


    public void clickKek(View view){
        try{
            byte[] aesKey = generateAESKey();
            doECEncryption(aesKey);
        }catch (Exception e){
            TextView text = (TextView) findViewById(R.id.textView);
            text.setText("Something went REALLY wrong!");
        }
    }

    public void clickKekRSA(View view){
        try{
            byte[] aesKey = generateAESKey();
            doRSAEncryption(aesKey);
        }catch (Exception e){
            TextView text = (TextView) findViewById(R.id.textView);
            text.setText("Something went REALLY wrong!");
        }
    }

    public void sendSMS(View view) {
        final int SENT = 1;
        final short SMS_PORT = 8998;
        String phoneNumber = "5556";       //Other emulator phone number

        EditText phoneNumberEditText = (EditText)findViewById(R.id.phoneEditText);
        phoneNumber = phoneNumberEditText.getText().toString();
        if (phoneNumber.isEmpty())
            phoneNumber = "5556";


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

