package pt.ulisboa.tecnico.meic.sirs.securesms.service;

import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.ContactManager;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.SmsMessageManager;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToSendSessionAcknowledgeException;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.Contact;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToRetrieveContactException;
import pt.ulisboa.tecnico.meic.sirs.securesms.service.exceptions.FailedServiceException;

/**
 * Created by joao on 2/12/15.
 */

public class SendSessionAcknowledgmentService extends SecureSmsService {
    private String _phoneNumber;

    public SendSessionAcknowledgmentService(String phoneNumber) {
        _phoneNumber = phoneNumber;
    }

    public void execute() throws FailedServiceException {
        try {
            Contact contact = ContactManager.retrieveContactByPhoneNumber(_phoneNumber);
            byte[] ackSmsMessage = SmsMessageManager.createAckSmsMessage(contact);
            SmsMessageManager.sendSms(_phoneNumber, ackSmsMessage);

        } catch ( IllegalArgumentException
                | FailedToRetrieveContactException
                | FailedToSendSessionAcknowledgeException exception){
            throw new FailedServiceException("Failed to send session acknowledgement", exception);
        }
    }
}