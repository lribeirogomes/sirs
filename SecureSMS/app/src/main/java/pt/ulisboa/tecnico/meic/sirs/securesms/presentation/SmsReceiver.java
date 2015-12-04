package pt.ulisboa.tecnico.meic.sirs.securesms.presentation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import org.spongycastle.util.Arrays;


/**
 * Created by joao on 11/1/15.
 */
public class SmsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        Object[] smsExtra = (Object[]) bundle.get("pdus");


        //Get the date from the SMS
        String address = "";
        byte[] completeData = {};
        android.telephony.SmsMessage androidSms;
        for (int i = 0; i < smsExtra.length; i++) {
            androidSms = android.telephony.SmsMessage.createFromPdu((byte[]) smsExtra[i]); //, "3gpp"); //Deprecated in API 23 unfortunately the replacement is only available in API 23

            byte[] data = androidSms.getUserData();
            if (data != null) {
                completeData = Arrays.concatenate(completeData, data);
            }
            address = androidSms.getOriginatingAddress();
        }

        Intent result = new Intent(context, NotifyIncomingMessageService.class);
        result.putExtra(NotifyIncomingMessageService.ADDRESS, address);
        result.putExtra(NotifyIncomingMessageService.DATA, completeData);
        context.startService(result);
    }
}
