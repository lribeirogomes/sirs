package pt.ulisboa.tecnico.meic.sirs.securesms.service;

import pt.ulisboa.tecnico.meic.sirs.securesms.domain.SmsMessage;
import pt.ulisboa.tecnico.meic.sirs.securesms.service.exceptions.FailedServiceException;

/**
 * Created by lribeirogomes on 17/11/15.
 */
public class ReceiveSmsMessageService extends SecureSmsService {
    private String _phoneNumber;
    private byte[] _encryptedSms;
    private SmsMessage _result;

    public ReceiveSmsMessageService(String phoneNumber,
                                    byte[] data) {
        _phoneNumber = phoneNumber;
        _encryptedSms = data;
        _result = null;
    }

    public SmsMessage getResult() throws NullPointerException {
        if (_result == null) {
            throw new NullPointerException();
        }
        return _result;
    }

    public void Execute() throws FailedServiceException {
        SecureSmsService service;

        try {
            service = new DecryptSmsMessageService(_phoneNumber, _encryptedSms);
            service.Execute();
        } catch (FailedServiceException exception) {
            throw new FailedServiceException("receive sms message", exception);
        }
    }
}
