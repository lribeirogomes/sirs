package pt.ulisboa.tecnico.meic.sirs.securesms.service;

import pt.ulisboa.tecnico.meic.sirs.securesms.domain.Contact;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToGetContactException;
import pt.ulisboa.tecnico.meic.sirs.securesms.service.exceptions.FailedToAddContactException;

/**
 * Created by lribeirogomes on 23/11/15.
 */
public class AddContactService extends SecureSmsService {
    private String _phoneNumber, _name;

    public AddContactService (String phoneNumber, String name) {
        _phoneNumber = phoneNumber;
        _name = name;
    }

    public void Execute() throws FailedToAddContactException {
        try {
            Contact.getInstance(_phoneNumber, _name);
        } catch ( FailedToGetContactException exception) {
            throw new FailedToAddContactException(exception);
        }
    }
}
