package pt.ulisboa.tecnico.meic.sirs.securesms.service;


import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.ContactManager;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.SessionManager;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToDeleteSessionException;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.Contact;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.Session;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToRetrieveContactException;
import pt.ulisboa.tecnico.meic.sirs.securesms.service.exceptions.FailedServiceException;

/**
 * Created by joao on 2/12/15.
 */

public class DeleteSessionService extends SecureSmsService {
    String _phoneNumber;

    public DeleteSessionService(String phoneNumber){
       _phoneNumber = phoneNumber;
    }

    public void execute() throws FailedServiceException {
        try {
            Contact contact = ContactManager.retrieveContactByPhoneNumber(_phoneNumber);
            SessionManager.delete(contact);
        }catch (FailedToDeleteSessionException
                | FailedToRetrieveContactException e){
            throw new FailedServiceException("Failed to delete the session", e);
        }
    }
}