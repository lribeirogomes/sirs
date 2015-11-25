package pt.ulisboa.tecnico.meic.sirs.group6.securesms.service;

import android.telephony.SmsManager;

import java.util.List;

import pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain.SMSMessage;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain.exceptions.FailedToEncryptSMSMessageException;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain.exceptions.FailedToGetSMSMessageException;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.service.exceptions.FailedToSendSMSException;

/**
 * Created by lribeirogomes on 15/11/15.
 */
public class SendSMSService extends SecureSMSService {
    private List<String> _phoneNumbers;
    private String _plainTextSMS;

    public SendSMSService (List<String> phoneNumbers,
                           String plainTextSMS) {
        _phoneNumbers = phoneNumbers;
        _plainTextSMS = plainTextSMS;
    }

    public void Execute() throws FailedToSendSMSException {
        short smsPort= 8998;

        try {
            for (String phoneNumber : _phoneNumbers) {
                SMSMessage sms = SMSMessage.getInstance(phoneNumber, _plainTextSMS);

                SmsManager manager = SmsManager.getDefault();
                manager.sendDataMessage(sms.getsender(),
                        null, // TODO: define scAddress if needed
                        smsPort,
                        sms.getEncryptedContent(),
                        null,  // TODO: define sentIntent if needed
                        null); // TODO: define deliveryIntent if needed
            }
        } catch (IllegalArgumentException
                | FailedToGetSMSMessageException
                | FailedToEncryptSMSMessageException exception) {
            throw new FailedToSendSMSException(exception);
        }
    }
}
