package pt.ulisboa.tecnico.meic.sirs.securesms.service;

import pt.ulisboa.tecnico.meic.sirs.securesms.domain.SmsMessage;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToEncryptSmsMessageException;
import pt.ulisboa.tecnico.meic.sirs.securesms.service.exceptions.FailedServiceException;

/**
 * Created by lribeirogomes on 15/11/15.
 */
public class EncryptSmsMessageService extends SecureSmsService {
    private SmsMessage _smsMessage;
    private byte[] _result;

    public EncryptSmsMessageService(SmsMessage smsMessage) {
        _smsMessage = smsMessage;
        _result = null;
    }

    public byte[] getResult() throws NullPointerException {
        if (_result == null) {
            throw new NullPointerException();
        }
        return _result;
    }

    public void Execute() throws FailedServiceException {
        try {
            _result = _smsMessage.getEncryptedContent();
        } catch (FailedToEncryptSmsMessageException exception) {
            throw new FailedServiceException("encrypt sms message", exception);
        }
    }
}
