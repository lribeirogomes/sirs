package pt.ulisboa.tecnico.meic.sirs.securesms.service;

import java.util.List;

import pt.ulisboa.tecnico.meic.sirs.securesms.domain.Contact;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.SmsMessage;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.SmsMessageManager;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToRetrieveAllSmsMessagesException;
import pt.ulisboa.tecnico.meic.sirs.securesms.service.exceptions.FailedServiceException;

/**
 * Created by lribeirogomes on 23/11/15.
 */
public class GetMessagesService extends SecureSmsService {
    private Contact _contact;
    private List<SmsMessage> _result;

    public GetMessagesService(Contact contact) {
        _contact = contact;
        _result = null;
    }

    public List<SmsMessage> GetResult() {
        return _result;
    }

    public void Execute() throws FailedServiceException {
        try {
            _result = SmsMessageManager.retrieveAllSmsMessages(_contact);
        } catch ( FailedToRetrieveAllSmsMessagesException exception ) {
            throw new FailedServiceException("get messages", exception);
        }
    }
}