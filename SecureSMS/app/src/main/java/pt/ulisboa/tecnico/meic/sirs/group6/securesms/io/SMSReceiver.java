package pt.ulisboa.tecnico.meic.sirs.group6.securesms.io;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by joao on 11/1/15.
 */
public class SMSReceiver extends BroadcastReceiver {
    @Override
    public void onReceive( Context context, Intent intent )
    {
        // Get the SendSMSService map from Intent
        Bundle bundle = intent.getExtras();

        if ( bundle != null )
        {
            Intent result = new Intent(context, ReceiveSMSActivity.class);
            result.putExtra(ReceiveSMSActivity.MESSAGES, bundle);
            result.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(result);
        }
    }
}
