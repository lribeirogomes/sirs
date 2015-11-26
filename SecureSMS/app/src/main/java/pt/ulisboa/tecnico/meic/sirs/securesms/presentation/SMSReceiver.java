package pt.ulisboa.tecnico.meic.sirs.securesms.presentation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import pt.ulisboa.tecnico.meic.sirs.securesms.service.ReceiveSmsService;
import pt.ulisboa.tecnico.meic.sirs.securesms.service.exceptions.FailedToReceiveSMSException;

/**
 * Created by joao on 11/1/15.
 */
public class SMSReceiver extends BroadcastReceiver {
    @Override
    public void onReceive( Context context, Intent intent )
    {
        // Get the SendSmsService map from Intent
        Bundle bundle = intent.getExtras();

        // Get received SendSmsService array
        Object[] smsExtra = (Object[]) bundle.get( "pdus" );

        byte[] data;
        String address;
        SmsMessage sms;

        for ( int i = 0; i < smsExtra.length; i++ )
        {
            sms = SmsMessage.createFromPdu((byte[]) smsExtra[i]); //, "3gpp");
            data = sms.getUserData();
            address = sms.getOriginatingAddress();
            ReceiveSmsService service = new ReceiveSmsService(address, data);
            try {
                service.Execute();
            } catch (FailedToReceiveSMSException exception) {
                // TODO:Integrate interface with exception handling
            }
        }
    }
}
