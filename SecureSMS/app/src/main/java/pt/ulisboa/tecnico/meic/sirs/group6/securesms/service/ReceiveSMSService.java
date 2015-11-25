package pt.ulisboa.tecnico.meic.sirs.group6.securesms.service;

import pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain.SMSMessage;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain.exceptions.FailedToGetSMSMessageException;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.service.exceptions.FailedToReceiveSMSException;

/**
 * Created by lribeirogomes on 17/11/15.
 */
public class ReceiveSMSService extends SecureSMSService {
    private String _phoneNumber;
    private byte[] _encryptedSMS;
    private SMSMessage _result;

    public ReceiveSMSService (String phoneNumber,
                              byte[] data) {
        _phoneNumber = phoneNumber;
        _encryptedSMS = data;
        _result = null;
    }

    public SMSMessage getResult() throws NullPointerException {
        if (_result == null) {
            throw new NullPointerException();
        }
        return _result;
    }

    public void Execute() throws FailedToReceiveSMSException {
        try {
            _result = SMSMessage.getInstance(_phoneNumber, _encryptedSMS);
        } catch (FailedToGetSMSMessageException exception) {
            throw new FailedToReceiveSMSException(exception);
        }
    }
}
