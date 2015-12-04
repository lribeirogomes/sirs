

package pt.ulisboa.tecnico.meic.sirs.securesms.presentation;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;

import pt.ulisboa.tecnico.meic.sirs.securesms.R;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.Session;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.SmsMessage;
import pt.ulisboa.tecnico.meic.sirs.securesms.service.ReceiveSmsMessageService;
import pt.ulisboa.tecnico.meic.sirs.securesms.service.exceptions.FailedServiceException;
import pt.ulisboa.tecnico.meic.sirs.securesms.service.exceptions.FailedToGetResultException;

/**
 * Created by Ana Beatriz on 27/11/2015.
 */


public class NotifyIncomingMessageService extends Service {
    public final static String ACTION = "NotifyServiceAction";
    public final static String STOP_SERVICE="StopService";
    public final static int RQS_STOP_SERVICE = 1;
    public static final String ADDRESS = "pt.ulisboa.tecnico.meic.sirs.securesms.address";
    public static final String DATA = "pt.ulisboa.tecnico.meic.sirs.securesms.data";


    private NotifyServiceReceiver notifyServiceReceiver;

    @Override
    public void onCreate() {
        notifyServiceReceiver = new NotifyServiceReceiver();
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        this.unregisterReceiver(notifyServiceReceiver);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION);
        registerReceiver(notifyServiceReceiver, intentFilter);

        Bundle bundle = intent.getExtras();
        String senderAddress = bundle.getString(ADDRESS);
        byte[] completeData = bundle.getByteArray(DATA);
        processReceivedSms(senderAddress, completeData);

        return super.onStartCommand(intent, flags, startId);
    }

    private void processReceivedSms(String senderAddress, byte[] completeData) {
        //@johnny - had to return status in the service because we were already doing if chains anyway
        //TODO: allow to renew a session on receive if it has expired (are we already doing that?)
        try {
            ReceiveSmsMessageService service = new ReceiveSmsMessageService(senderAddress, completeData);
            service.execute();

            Session.Status sessionStatus = service.getResultStatus();

            switch (sessionStatus) {
                case Established: {
                    SmsMessage sms = service.getResultSms();
                    String contactName = sms.getContact().getName();
                    showNotification(contactName, sms.getContent(), senderAddress, false);
                    break;
                }
                case PartialReqReceived: {
                    showNotification("You have a new request", "Click here for more details", senderAddress, true);
                    break;
                }
                default: {
                    //Toast.makeText("Someone tried to send you a message without your approval", Toast.LENGTH_SHORT).show();
                    break;
                }
            }

        } catch (FailedServiceException
                | FailedToGetResultException exception) {
            Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void showNotification(String title, String subtext, String senderAddress, boolean showAckDialog) {
        // Sets an ID for the notification, so it can be updated
        int notifyID = 1;

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_decrypt)
                        .setContentTitle(title)
                        .setContentText(subtext)
                        .setAutoCancel(true);
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, ShowContactMessagesActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(ShowContactMessagesActivity.CONTACT_NUMBER_TO_SHOW, senderAddress);
        bundle.putBoolean(ShowContactMessagesActivity.SHOW_ACK_DIALOG, showAckDialog);
        resultIntent.putExtras(bundle);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(ShowContactMessagesActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(notifyID, mBuilder.build());
    }
    //to stop the service
    public class NotifyServiceReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            int rqs = intent.getIntExtra(STOP_SERVICE, 0);

            if (rqs == RQS_STOP_SERVICE){
                ((NotificationManager) getSystemService(NOTIFICATION_SERVICE))
                        .cancelAll();
                stopSelf();
            }
        }
    }
}