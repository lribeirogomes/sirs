package pt.ulisboa.tecnico.meic.sirs.securesms.service;

import android.telephony.SmsManager;

import java.util.List;

import pt.ulisboa.tecnico.meic.sirs.securesms.domain.SmsMessage;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToEncryptSmsMessageException;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToGetSmsMessageException;
import pt.ulisboa.tecnico.meic.sirs.securesms.service.exceptions.FailedToSendSMSException;

/**
 * Created by lribeirogomes on 15/11/15.
 */
public class SendSmsService extends SecureSmsService {
    private List<String> _phoneNumbers;
    private String _plainTextSMS;

    public SendSmsService (List<String> phoneNumbers,
                           String plainTextSMS) {
        _phoneNumbers = phoneNumbers;
        _plainTextSMS = plainTextSMS;
    }

    public void Execute() throws FailedToSendSMSException {
        short smsPort= 8998;

        try {
            for (String phoneNumber : _phoneNumbers) {
                SmsMessage sms = SmsMessage.getInstance(phoneNumber, _plainTextSMS);

                SmsManager manager = SmsManager.getDefault();
                manager.sendDataMessage(sms.getsender(),
                        null, // TODO: define scAddress if needed
                        smsPort,
                        sms.getEncryptedContent(),
                        null,  // TODO: define sentIntent if needed
                        null); // TODO: define deliveryIntent if needed
            }
        } catch (IllegalArgumentException
                | FailedToGetSmsMessageException
                | FailedToEncryptSmsMessageException exception) {
            throw new FailedToSendSMSException(exception);
        }
    }
}
