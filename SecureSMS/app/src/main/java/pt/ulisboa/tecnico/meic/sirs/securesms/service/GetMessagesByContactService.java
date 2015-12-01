package pt.ulisboa.tecnico.meic.sirs.securesms.service;


import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.ContactManager;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.Contact;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.SmsMessage;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.SmsMessageManager;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToRetrieveAllSmsMessagesException;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToRetrieveContactException;
import pt.ulisboa.tecnico.meic.sirs.securesms.service.exceptions.FailedServiceException;
import pt.ulisboa.tecnico.meic.sirs.securesms.service.exceptions.FailedToGetResultException;

/**
 * Created by lribeirogomes on 23/11/15.
 */
public class GetMessagesByContactService extends SecureSmsService {
    private String _contactPhoneNumber;
    private ArrayList<SmsMessage> _messages;

    public GetMessagesByContactService(String contactPhoneNumber) {
        _contactPhoneNumber = contactPhoneNumber;
    }

    public void execute() throws FailedServiceException {
        try {
            Contact contact = ContactManager.retrieveContactByPhoneNumber(_contactPhoneNumber);
            _messages = SmsMessageManager.retrieveAllSmsMessages(contact);
        } catch ( FailedToRetrieveAllSmsMessagesException
                | FailedToRetrieveContactException exception ) {
            throw new FailedServiceException("get messages", exception);
        }
    }

    public ArrayList<SmsMessage> getResult() throws FailedToGetResultException {
        if (_messages == null) {
            throw new FailedToGetResultException();
        }
        return _messages;
    }
}