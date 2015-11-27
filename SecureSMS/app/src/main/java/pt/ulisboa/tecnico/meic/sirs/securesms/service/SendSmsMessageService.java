package pt.ulisboa.tecnico.meic.sirs.securesms.service;

import android.telephony.SmsManager;

import java.util.List;

import pt.ulisboa.tecnico.meic.sirs.securesms.domain.SmsMessage;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.SmsMessageManager;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToEncryptSmsMessageException;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToCreateSmsMessageException;
import pt.ulisboa.tecnico.meic.sirs.securesms.service.exceptions.FailedServiceException;

/**
 * Created by lribeirogomes on 15/11/15.
 */
public class SendSmsMessageService extends SecureSmsService {
    private List<String> _phoneNumbers;
    private String _plainTextSms;

    public SendSmsMessageService(List<String> phoneNumbers,
                                 String plainTextSms) {
        _phoneNumbers = phoneNumbers;
        _plainTextSms = plainTextSms;
    }

    public void Execute() throws FailedServiceException {
        short smsPort= 8998;

        try {
            for (String phoneNumber : _phoneNumbers) {
                SmsMessage sms = SmsMessageManager.createSmsMessage(phoneNumber, _plainTextSms);

                SmsManager manager = SmsManager.getDefault();
                manager.sendDataMessage(sms.getsender(),
                        null, // TODO: define scAddress if needed
                        smsPort,
                        sms.getEncryptedContent(),
                        null,  // TODO: define sentIntent if needed
                        null); // TODO: define deliveryIntent if needed
            }
        } catch ( IllegalArgumentException
                | FailedToCreateSmsMessageException
                | FailedToEncryptSmsMessageException exception) {
            throw new FailedServiceException("send sms message", exception);
        }
    }
}
