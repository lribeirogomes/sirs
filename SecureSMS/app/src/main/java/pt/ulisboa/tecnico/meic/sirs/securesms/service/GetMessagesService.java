package pt.ulisboa.tecnico.meic.sirs.securesms.service;

import java.util.List;

import pt.ulisboa.tecnico.meic.sirs.securesms.domain.Contact;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.SmsMessage;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.SmsMessageManager;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToRetrieveAllSmsMessagesException;
import pt.ulisboa.tecnico.meic.sirs.securesms.service.exceptions.FailedServiceException;
import pt.ulisboa.tecnico.meic.sirs.securesms.service.exceptions.FailedToGetResultException;

/**
 * Created by lribeirogomes on 23/11/15.
 */
public class GetMessagesService extends SecureSmsService {
    private String _phoneNumber;
    private List<SmsMessage> _result;

    public GetMessagesService(String phoneNumber) {
        _phoneNumber = phoneNumber;
        _result = null;
    }

    public List<SmsMessage> GetResult() throws FailedToGetResultException {
        if (_result == null) {
            throw new FailedToGetResultException();
        }
        return _result;
    }

    public void Execute() throws FailedServiceException {
        try {
            _result = SmsMessageManager.retrieveAllSmsMessages(_phoneNumber);
        } catch ( FailedToRetrieveAllSmsMessagesException exception ) {
            throw new FailedServiceException("get messages", exception);
        }
    }
}