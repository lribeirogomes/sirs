package pt.ulisboa.tecnico.meic.sirs.securesms.presentation;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import pt.ulisboa.tecnico.meic.sirs.securesms.R;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.SmsMessage;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.SmsMessageType;
import pt.ulisboa.tecnico.meic.sirs.securesms.service.ReceiveSmsMessageService;

/**
 * Created by Ana Beatriz on 27/11/2015.
 */
public class ReceiveSmsActivity extends AppCompatActivity {
    public static final String ADDRESS = "pt.ulisboa.tecnico.meic.sirs.securesms.address";
    public static final String DATA = "pt.ulisboa.tecnico.meic.sirs.securesms.data";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Bundle bundle = getIntent().getExtras();
        String senderAddress = bundle.getString(ADDRESS);
        byte[] completeData = bundle.getByteArray(DATA);


        //Figure out where to deliver it
        try {
            ReceiveSmsMessageService service = new ReceiveSmsMessageService(senderAddress, completeData);
            service.execute();


            if (completeData[0] == SmsMessageType.Text.ordinal()) {
                SmsMessage sms = service.getResult();
                String contactName = sms.getContact().getName();
                showNotification(getApplicationContext(), contactName, sms.getContent(), senderAddress, false);
            }else {
                showNotification(getApplicationContext(), "You have a new request", "Click here for more details", senderAddress, true);
            }

        } catch (Exception exception) {
            Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_SHORT);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    public void showNotification(Context context, String title, String subtext, String senderAddress, boolean showAckDialog) {
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
        bundle.putString(ShowContactMessagesActivity.CONTACT_NUMBER_TO_SHOW, senderAddress);
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