package pt.ulisboa.tecnico.meic.sirs.securesms.presentation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import pt.ulisboa.tecnico.meic.sirs.securesms.service.ReceiveSmsMessageService;
import pt.ulisboa.tecnico.meic.sirs.securesms.service.exceptions.FailedServiceException;

/**
 * Created by joao on 11/1/15.
 */
public class SMSReceiver extends BroadcastReceiver {
    @Override
    public void onReceive( Context context, Intent intent )
    {
        // Get the SendSmsMessageService map from Intent
        Bundle bundle = intent.getExtras();

        // Get received SendSmsMessageService array
        Object[] smsExtra = (Object[]) bundle.get( "pdus" );

        byte[] data;
        String address;
        SmsMessage sms;

        for ( int i = 0; i < smsExtra.length; i++ )
        {
            sms = SmsMessage.createFromPdu((byte[]) smsExtra[i]); //, "3gpp");
            data = sms.getUserData();
            address = sms.getOriginatingAddress();
            ReceiveSmsMessageService service = new ReceiveSmsMessageService(address, data);
            try {
                service.execute();
            } catch (FailedServiceException exception) {
                // TODO:Integrate interface with exception handling
            }
        }
    }
}
