package pt.ulisboa.tecnico.meic.sirs.group6.securesms.service;

import android.telephony.SmsManager;

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
            //StoredSMS storedSMS = StoredSMS.getInstance(_password, _destinationAddress, _data);
            //EncryptedSMS encryptedSMS = EncryptedSMS.getEncryptedSMS(storedSMS);
            SmsManager manager = SmsManager.getDefault();
            manager.sendDataMessage(_destinationAddress,
                    null, // TODO: define scAddress if needed
                    SMS_PORT,
                    _data.getBytes("UTF-8"),
                    null,  // TODO: define sentIntent if needed
                    null); // TODO: define deliveryIntent if needed
        } /*catch (
                IllegalArgumentException |
                FailedToGetStoredSMSException |
                FailedToGetEncryptedSMSException exception) {*/
        catch (Exception exception) {
            throw new FailedToSendSMSException(exception);
        }
    }
}
