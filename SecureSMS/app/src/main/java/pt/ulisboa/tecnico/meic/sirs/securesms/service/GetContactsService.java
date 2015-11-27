package pt.ulisboa.tecnico.meic.sirs.securesms.service;

import java.util.Map;

import pt.ulisboa.tecnico.meic.sirs.securesms.domain.Contact;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.ContactManager;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToRetrieveAllContactsException;
import pt.ulisboa.tecnico.meic.sirs.securesms.service.exceptions.FailedServiceException;

/**
 * Created by lribeirogomes on 23/11/15.
 */
public class GetContactsService extends SecureSmsService {
    private Map<String, Contact> _result;

    public GetContactsService() {
        _result = null;
    }

    public Map<String, Contact> GetResult() {
        return _result;
    }

    public void Execute() throws FailedServiceException {
        try {
            _result = ContactManager.retrieveAllContacts();
        } catch ( FailedToRetrieveAllContactsException exception ) {
            throw new FailedServiceException("get contacts", exception);
        }
    }
}