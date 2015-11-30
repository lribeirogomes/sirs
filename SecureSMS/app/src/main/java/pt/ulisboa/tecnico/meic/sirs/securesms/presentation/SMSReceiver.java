package pt.ulisboa.tecnico.meic.sirs.securesms.presentation;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import java.nio.charset.Charset;

import pt.ulisboa.tecnico.meic.sirs.securesms.R;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.ContactManager;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.SmsMessageManager;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.Contact;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.SmsMessage;


/**
 * Created by joao on 11/1/15.
 */
public class SmsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Get the SendSmsMessageService map from Intent
        Bundle bundle = intent.getExtras();

        // Get received SendSmsMessageService array
        Object[] smsExtra = (Object[]) bundle.get("pdus");

        byte[] data;
        String address;
        android.telephony.SmsMessage androidSms;

        for (int i = 0; i < smsExtra.length; i++) {
            try {
                androidSms = android.telephony.SmsMessage.createFromPdu((byte[]) smsExtra[i]); //, "3gpp");
                address = androidSms.getOriginatingAddress();
                data = androidSms.getUserData();

                Contact contact = ContactManager.retrieveContactByPhoneNumber(address);
                SmsMessage sms = SmsMessageManager.createSmsMessage(contact, data);

                showNotification(context, contact.getName(), new String(data, Charset.defaultCharset()));


            } catch (Exception exception) {
                // TODO:Integrate interface with exception handling
            }
        }
    }

    public void showNotification(Context context, String address, String data) {
        // Sets an ID for the notification, so it can be updated
        int notifyID = 1;

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_decrypt)
                        .setContentTitle(address)
                        .setContentText(data);
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(context, ShowSMSActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(ShowSMSActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(notifyID, mBuilder.build());
    }

}
