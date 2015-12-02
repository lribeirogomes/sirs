package pt.ulisboa.tecnico.meic.sirs.securesms.presentation;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.widget.Toast;

import org.spongycastle.util.Arrays;

import java.nio.charset.Charset;

import pt.ulisboa.tecnico.meic.sirs.securesms.R;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.ContactManager;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.SessionManager;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.SmsMessageManager;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.Contact;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.SmsMessage;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.SmsMessageType;
import pt.ulisboa.tecnico.meic.sirs.securesms.service.ReceiveSmsMessageService;


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

        Intent result = new Intent(context, ReceiveSmsActivity.class);
        result.putExtra(ReceiveSmsActivity.ADDRESS, address);
        result.putExtra(ReceiveSmsActivity.DATA, completeData);
        result.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(result);
    }
}
