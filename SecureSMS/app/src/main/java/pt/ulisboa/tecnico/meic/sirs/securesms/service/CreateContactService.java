package pt.ulisboa.tecnico.meic.sirs.securesms.service;

import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.ContactManager;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToCreateContactException;
import pt.ulisboa.tecnico.meic.sirs.securesms.service.exceptions.FailedServiceException;

/**
 * Created by lribeirogomes on 23/11/15.
 */
public class CreateContactService extends SecureSmsService {
    private String _contactName,
                   _phoneNumber;

    public CreateContactService(String contactName, String phoneNumber) {
        _contactName = contactName;
        _phoneNumber = phoneNumber;
    }

    public void execute() throws FailedServiceException {
        try {
            ContactManager.createContact(_contactName, _phoneNumber);
        } catch ( FailedToCreateContactException exception) {
            throw new FailedServiceException("create contact", exception);
        }
    }
}