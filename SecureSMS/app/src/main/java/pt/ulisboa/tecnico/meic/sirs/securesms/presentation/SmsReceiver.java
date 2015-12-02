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
        byte[] completeData = {};
        String address = "";
        android.telephony.SmsMessage androidSms;
        for (int i = 0; i < smsExtra.length; i++) {
            androidSms = android.telephony.SmsMessage.createFromPdu((byte[]) smsExtra[i]); //, "3gpp"); //Deprecated in API 23 unfortunately the replacement is only available in API 23

            byte[] data = androidSms.getUserData();
            if (data != null) {
                completeData = Arrays.concatenate(completeData, data);
            }
            address = androidSms.getOriginatingAddress();
        }

        //Figure out where to deliver it
        try {
           if (completeData[0] == SmsMessageType.Text.ordinal()) {
               ReceiveSmsMessageService service = new ReceiveSmsMessageService(address, completeData);
               service.execute();
               SmsMessage sms = service.getResult();
               if (null != sms)
                   showNotification(context, sms.getContact(), new String(completeData, Charset.defaultCharset()));
           }else {
                Intent result = new Intent(context, ComposeMessageActivity.class);
                result.putExtra(ComposeMessageActivity.PHONE_NUMBER, address);
                result.putExtra(ComposeMessageActivity.SESSION_MESSAGE, completeData);
                result.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(result);
            }

        } catch (Exception exception) {
                Toast.makeText(context, exception.getMessage(), Toast.LENGTH_SHORT);
        }

    }

    public void showNotification(Context context, Contact contact, String data) {
        // Sets an ID for the notification, so it can be updated
        int notifyID = 1;

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_decrypt)
                        .setContentTitle(contact.getName())
                        .setContentText(data);
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(context, ShowContactMessagesActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("contactToShowNumber", contact.getPhoneNumber());
        resultIntent.putExtras(bundle);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(ShowContactMessagesActivity.class);
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
