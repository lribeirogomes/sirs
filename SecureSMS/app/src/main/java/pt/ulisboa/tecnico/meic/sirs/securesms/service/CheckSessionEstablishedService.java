package pt.ulisboa.tecnico.meic.sirs.securesms.service;


import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.ContactManager;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.SessionManager;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.Contact;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.Session;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToRetrieveContactException;
import pt.ulisboa.tecnico.meic.sirs.securesms.service.exceptions.FailedServiceException;

/**
 * Created by joao on 2/12/15.
 */

public class CheckSessionEstablishedService extends SecureSmsService {
    private boolean _isEstablished;
    String _phoneNumber;

    public CheckSessionEstablishedService(String phoneNumber){
       _phoneNumber = phoneNumber;
    }

    public void execute() throws FailedServiceException {
        try {
            Contact contact = ContactManager.retrieveContactByPhoneNumber(_phoneNumber);
            Session.Status status = SessionManager.checkSessionStatus(contact);
            if (Session.Status.Established == status)
                _isEstablished = true;
            else
                _isEstablished = false;
        }catch (FailedToRetrieveContactException e){
            throw new FailedServiceException("Failed to check session status", e);
        }
    }

    public boolean getResult(){
        return _isEstablished;
    }
}