package pt.ulisboa.tecnico.meic.sirs.securesms.service;

import pt.ulisboa.tecnico.meic.sirs.securesms.domain.ContactManager;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToCreateContactException;
import pt.ulisboa.tecnico.meic.sirs.securesms.service.exceptions.FailedServiceException;

/**
 * Created by lribeirogomes on 23/11/15.
 */
public class CreateContactService extends SecureSmsService {
    private String _phoneNumber,
                   _contactName;

    public CreateContactService(String phoneNumber, String contactName) {
        _phoneNumber = phoneNumber;
        _contactName = contactName;
    }

    public void Execute() throws FailedServiceException {
        try {
            ContactManager.createContact(_phoneNumber, _contactName);
        } catch ( FailedToCreateContactException exception) {
            throw new FailedServiceException("create contact", exception);
        }
    }
}