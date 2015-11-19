package pt.ulisboa.tecnico.meic.sirs.group6.securesms.io;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import pt.ulisboa.tecnico.meic.sirs.group6.securesms.service.ReceiveSMSService;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.service.exceptions.FailedToReceiveSMSException;

/**
 * Created by joao on 11/1/15.
 */
public class SMSReceiver extends BroadcastReceiver {
    @Override
    public void onReceive( Context context, Intent intent )
    {
        // Get the SendSMSService map from Intent
        Bundle bundle = intent.getExtras();

        // Get received SendSMSService array
        Object[] smsExtra = (Object[]) bundle.get( "pdus" );

        byte[] data;
        String address;
        SmsMessage sms;

        for ( int i = 0; i < smsExtra.length; i++ )
        {
            sms = SmsMessage.createFromPdu((byte[]) smsExtra[i]); //, "3gpp");
            data = sms.getUserData();
            address = sms.getOriginatingAddress();
            ReceiveSMSService service = new ReceiveSMSService(context ,address, data);
            try {
                service.Execute();
            } catch (FailedToReceiveSMSException exception) {
                // TODO:Integrate interface with exception handling
            }
        }
    }
}
