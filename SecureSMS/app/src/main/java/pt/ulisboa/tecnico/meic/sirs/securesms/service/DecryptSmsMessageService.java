package pt.ulisboa.tecnico.meic.sirs.securesms.service;

import pt.ulisboa.tecnico.meic.sirs.securesms.domain.SmsMessage;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.SmsMessageManager;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToCreateSmsMessageException;
import pt.ulisboa.tecnico.meic.sirs.securesms.service.exceptions.FailedServiceException;

/**
 * Created by lribeirogomes on 17/11/15.
 */
public class DecryptSmsMessageService extends SecureSmsService {
    private String _phoneNumber;
    private byte[] _encryptedSms;
    private String _result;

    public DecryptSmsMessageService(String phoneNumber,
                                    byte[] data) {
        _phoneNumber = phoneNumber;
        _encryptedSms = data;
        _result = null;
    }

    public String getResult() throws NullPointerException {
        if (_result == null) {
            throw new NullPointerException();
        }
        return _result;
    }

    public void Execute() throws FailedServiceException {
        try {
            SmsMessage smsMessage = SmsMessageManager.createSmsMessage(_phoneNumber, _encryptedSms);
            _result = smsMessage.getContent();
        } catch (FailedToCreateSmsMessageException exception) {
            throw new FailedServiceException("decrypt sms message", exception);
        }
    }
}
