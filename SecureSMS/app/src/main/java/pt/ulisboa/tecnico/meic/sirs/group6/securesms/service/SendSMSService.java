package pt.ulisboa.tecnico.meic.sirs.group6.securesms.service;

import android.telephony.SmsManager;

import pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain.EncryptedSMS;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain.EncryptedSMSFactory;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain.exceptions.FailedToGetEncryptedSMSException;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.service.exceptions.FailedToSendSMSException;

/**
 * Created by lribeirogomes on 15/11/15.
 */
public class SendSMSService {
    private final short SMS_PORT= 8998;
    private String _password, _destinationAddress, _data;

    public SendSMSService (String password,
                           String destinationAddress,
                           String data) {
        _password = password;
        _destinationAddress = destinationAddress;
        _data = data;
    }

    public void Execute() throws FailedToSendSMSException {
        try {
            EncryptedSMSFactory smsFactory = new EncryptedSMSFactory();
            EncryptedSMS sms = smsFactory.getEncryptedSMS(_password, _destinationAddress, _data);
            SmsManager manager = SmsManager.getDefault();
            manager.sendDataMessage(sms.getDestinationAddress(),
                    null, // TODO: define scAddress if needed
                    SMS_PORT,
                    sms.getEncryptedData(),
                    null,  // TODO: define sentIntent if needed
                    null); // TODO: define deliveryIntent if needed
        } catch (
                IllegalArgumentException |
                FailedToGetEncryptedSMSException exception) {
            throw new FailedToSendSMSException(exception);
        }
    }
}
