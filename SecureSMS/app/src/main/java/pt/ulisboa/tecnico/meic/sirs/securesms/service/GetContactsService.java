package pt.ulisboa.tecnico.meic.sirs.securesms.service;

import java.util.ArrayList;
import java.util.Map;

import pt.ulisboa.tecnico.meic.sirs.securesms.domain.Contact;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.ContactManager;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToRetrieveAllContactsException;
import pt.ulisboa.tecnico.meic.sirs.securesms.service.exceptions.FailedServiceException;
import pt.ulisboa.tecnico.meic.sirs.securesms.service.exceptions.FailedToGetResultException;

/**
 * Created by lribeirogomes on 23/11/15.
 */
public class GetContactsService extends SecureSmsService {
    private ArrayList<Contact> _result;

    public GetContactsService() {
        _result = null;
    }

    public ArrayList<Contact> getResult() throws FailedToGetResultException {
        if (_result == null) {
            throw new FailedToGetResultException();
        }
        return _result;
    }

    public void execute() throws FailedServiceException {
        try {
            _result = ContactManager.retrieveAllContacts();
        } catch ( FailedToRetrieveAllContactsException exception ) {
            throw new FailedServiceException("get contacts", exception);
        }
    }
}