package pt.ulisboa.tecnico.meic.sirs.group6.securesms.service;

import android.telephony.SmsManager;

import pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain.EncryptedSMS;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain.exceptions.FailedToGetSMSException;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.service.exceptions.FailedToSendSMSException;

/**
 * Created by lribeirogomes on 15/11/15.
 */
public class SendSMSService {
    private String _password, _destinationAddress, _data;

    public SendSMSService (String password,
                           String destinationAddress,
                           String data) {
        _password = password;
        _destinationAddress = destinationAddress;
        _data = data;
    }

    public void Execute() throws FailedToSendSMSException {
        short smsPort= 8998;

        try {
            EncryptedSMS sms = EncryptedSMS.getInstance(_password, _destinationAddress, _data);
            SmsManager manager = SmsManager.getDefault();
            manager.sendDataMessage(sms.getDestinationAddress(),
                    null, // TODO: define scAddress if needed
                    smsPort,
                    sms.getContent(),
                    null,  // TODO: define sentIntent if needed
                    null); // TODO: define deliveryIntent if needed

            // TODO: integrate output with interface
        } catch (
                IllegalArgumentException |
                FailedToGetSMSException exception) {
            throw new FailedToSendSMSException(exception);
        }
    }
}
