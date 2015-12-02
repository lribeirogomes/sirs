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
    private String _senderAddress = "";

    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle bundle = intent.getExtras();
        Object[] smsExtra = (Object[]) bundle.get("pdus");


        //Get the date from the SMS
        byte[] completeData = {};
        android.telephony.SmsMessage androidSms;
        for (int i = 0; i < smsExtra.length; i++) {
            androidSms = android.telephony.SmsMessage.createFromPdu((byte[]) smsExtra[i]); //, "3gpp"); //Deprecated in API 23 unfortunately the replacement is only available in API 23

            byte[] data = androidSms.getUserData();
            if (data != null) {
                completeData = Arrays.concatenate(completeData, data);
            }
            _senderAddress = androidSms.getOriginatingAddress();
        }

        //Figure out where to deliver it
        try {
            ReceiveSmsMessageService service = new ReceiveSmsMessageService(_senderAddress, completeData);
            service.execute();
            //TEST CODE

            showNotification(context, "You have a new request", "Click here for more details", true);


            //TEST CODE


      /*      if (completeData[0] == SmsMessageType.Text.ordinal()) {
                SmsMessage sms = service.getResult();
                String contactName = sms.getContact().getName();
                showNotification(context, contactName, sms.getContent(), false);
            }else {
                showNotification(context, "You have a new request", "Click here for more details", true);
            }*/

        } catch (Exception exception) {
            Toast.makeText(context, exception.getMessage(), Toast.LENGTH_SHORT);
        }

    }



    public void showNotification(Context context, String title, String subtext, boolean showAckDialog) {
        // Sets an ID for the notification, so it can be updated
        int notifyID = 1;

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_decrypt)
                        .setContentTitle(title)
                        .setContentText(subtext);
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(context, ShowContactMessagesActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(ShowContactMessagesActivity.CONTACT_NUMBER_TO_SHOW, _senderAddress);
        bundle.putBoolean(ShowContactMessagesActivity.SHOW_ACK_DIALOG, showAckDialog);
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
