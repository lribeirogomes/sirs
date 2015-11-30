package pt.ulisboa.tecnico.meic.sirs.securesms.service;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.meic.sirs.securesms.domain.Contact;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.SmsMessage;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.SmsMessageManager;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToRetrieveAllSmsMessagesException;
import pt.ulisboa.tecnico.meic.sirs.securesms.service.exceptions.FailedServiceException;
import pt.ulisboa.tecnico.meic.sirs.securesms.service.exceptions.FailedToGetResultException;

/**
 * Created by lribeirogomes on 23/11/15.
 */
public class GetMessagesService extends SecureSmsService {
    private Contact _contact;
    private List<SmsMessage> _result;

    public GetMessagesService(Contact contact) {
        _contact = contact;
        _result = new ArrayList<SmsMessage>();
    }

    public List<SmsMessage> getResult() throws FailedToGetResultException {
        if (_result == null) {
            throw new FailedToGetResultException();
        }
        return _result;
    }

    public void execute() throws FailedServiceException {
        try {
            _result = SmsMessageManager.retrieveAllSmsMessages(_contact);
        } catch ( FailedToRetrieveAllSmsMessagesException exception ) {
            throw new FailedServiceException("get messages", exception);
        }
    }
}