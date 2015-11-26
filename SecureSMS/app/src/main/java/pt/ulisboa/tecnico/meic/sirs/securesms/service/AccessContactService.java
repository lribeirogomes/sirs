package pt.ulisboa.tecnico.meic.sirs.securesms.service;

import java.util.List;
import java.util.Map;

import pt.ulisboa.tecnico.meic.sirs.securesms.domain.Contact;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.SmsMessage;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.User;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToGetContactsException;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToGetPasswordException;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToGetSmsMessagesException;
import pt.ulisboa.tecnico.meic.sirs.securesms.service.exceptions.FailedToAccessContactException;

/**
 * Created by lribeirogomes on 23/11/15.
 */
public class AccessContactService extends SecureSmsService {
    private String _phoneNumber;
    private List<SmsMessage> _result;

    public AccessContactService(String phoneNumber) {
        _phoneNumber = phoneNumber;
        _result = null;
    }

    public List<SmsMessage> getResult() throws NullPointerException {
        if (_result == null) {
            throw new NullPointerException();
        }
        return _result;
    }

    public void Execute() throws FailedToAccessContactException {
        try {
            User user = User.getInstance();
            Map<String, Contact> contacts = user.getContacts();
            Contact contact = contacts.get(_phoneNumber);

            _result = contact.getMessages();
        } catch ( FailedToGetPasswordException
                | FailedToGetContactsException
                | FailedToGetSmsMessagesException exception) {
            throw new FailedToAccessContactException(exception);
        }
    }
}
