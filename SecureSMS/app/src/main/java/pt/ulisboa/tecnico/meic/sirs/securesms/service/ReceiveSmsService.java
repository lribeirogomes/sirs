package pt.ulisboa.tecnico.meic.sirs.securesms.service;

import pt.ulisboa.tecnico.meic.sirs.securesms.domain.SmsMessage;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToGetSmsMessageException;
import pt.ulisboa.tecnico.meic.sirs.securesms.service.exceptions.FailedToReceiveSMSException;

/**
 * Created by lribeirogomes on 17/11/15.
 */
public class ReceiveSmsService extends SecureSmsService {
    private String _phoneNumber;
    private byte[] _encryptedSMS;
    private SmsMessage _result;

    public ReceiveSmsService (String phoneNumber,
                              byte[] data) {
        _phoneNumber = phoneNumber;
        _encryptedSMS = data;
        _result = null;
    }

    public SmsMessage getResult() throws NullPointerException {
        if (_result == null) {
            throw new NullPointerException();
        }
        return _result;
    }

    public void Execute() throws FailedToReceiveSMSException {
        try {
            _result = SmsMessage.getInstance(_phoneNumber, _encryptedSMS);
        } catch (FailedToGetSmsMessageException exception) {
            throw new FailedToReceiveSMSException(exception);
        }
    }
}
