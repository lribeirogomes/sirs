package pt.ulisboa.tecnico.meic.sirs.group6.securesms.io;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsMessage;

import pt.ulisboa.tecnico.meic.sirs.group6.securesms.service.exceptions.FailedToReceiveSMSException;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.service.ReceiveSMSService;

/**
 * Created by lribeirogomes on 17/11/15.
 */
public class ReceiveSMSActivity extends AppCompatActivity {

    public static final String MESSAGES = "sms";
    private final String SMS_EXTRA_NAME = "pdus";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra(MESSAGES);

        // Get received SendSMSService array
        Object[] smsExtra = (Object[]) bundle.get( SMS_EXTRA_NAME );

        byte[] data;
        String address;
        SmsMessage sms;

        for ( int i = 0; i < smsExtra.length; i++ )
        {
            sms = SmsMessage.createFromPdu((byte[]) smsExtra[i]); //, "3gpp");
            data = sms.getUserData();
            address = sms.getOriginatingAddress();
            ReceiveSMSService service = new ReceiveSMSService(address, data);
            try {
                service.Execute();
            } catch (FailedToReceiveSMSException exception) {
                // TODO:Integrate interface with exception handling
            }
        }
    }
}
