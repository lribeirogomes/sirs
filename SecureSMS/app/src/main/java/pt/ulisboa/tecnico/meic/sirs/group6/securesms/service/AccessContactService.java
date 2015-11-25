package pt.ulisboa.tecnico.meic.sirs.group6.securesms.service;

import java.util.Map;
import java.util.Set;

import pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain.Contact;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain.SMSMessage;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain.User;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain.exceptions.FailedToGetContactsException;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain.exceptions.FailedToGetPasswordException;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain.exceptions.FailedToGetSMSMessagesException;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.service.exceptions.FailedToAccessContactException;

/**
 * Created by lribeirogomes on 23/11/15.
 */
public class AccessContactService extends SecureSMSService {
    private String _phoneNumber;
    private Set<SMSMessage> _result;

    public AccessContactService(String phoneNumber) {
        _phoneNumber = phoneNumber;
        _result = null;
    }

    public Set<SMSMessage> getResult() throws NullPointerException {
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
                | FailedToGetSMSMessagesException exception) {
            throw new FailedToAccessContactException(exception);
        }
    }
}
