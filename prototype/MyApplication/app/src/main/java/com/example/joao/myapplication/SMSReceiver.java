package com.example.joao.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by joao on 11/1/15.
 */
public class SMSReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent){

        Bundle bundle = intent.getExtras();

        String recMsgString = "";
        String fromAddress = "";

        SmsMessage recMsg = null;
        byte[] data = null;

        if (bundle != null)
        {
            //---retrieve the SMS message received---
            Object[] pdus = (Object[]) bundle.get("pdus");
            for (int i = 0; i < pdus.length; i++)
            {
                recMsg = SmsMessage.createFromPdu((byte[]) pdus[i], "3gpp");

                try
                {
                    data = recMsg.getUserData();
                }
                catch (Exception e)
                {
                    Intent result = new Intent(context, ReceiveSMSActivity.class);
                    result.putExtra(ReceiveSMSActivity.MESSAGE, "FAILED!!!");
                    result.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(result);
                }
                if (data != null)
                {
                    for (int index = 0; index < data.length; ++index)
                    {
                        recMsgString += Character.toString((char) data[index]);
                    }
                }

                fromAddress = recMsg.getOriginatingAddress();
            }
        }

        Intent result = new Intent(context, ReceiveSMSActivity.class);
        result.putExtra(ReceiveSMSActivity.MESSAGE, "Received SMS from " + fromAddress + ": " + recMsgString);
        result.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(result);
    }
}
