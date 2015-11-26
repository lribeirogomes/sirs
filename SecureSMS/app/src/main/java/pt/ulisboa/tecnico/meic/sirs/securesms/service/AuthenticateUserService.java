package pt.ulisboa.tecnico.meic.sirs.securesms.service;

import java.util.Map;
import java.util.Set;

import pt.ulisboa.tecnico.meic.sirs.securesms.domain.Contact;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.User;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToGetContactsException;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToGetPasswordException;
import pt.ulisboa.tecnico.meic.sirs.securesms.service.exceptions.FailedToLoginException;
import pt.ulisboa.tecnico.meic.sirs.securesms.service.exceptions.InvalidAuthenticationException;

/**
 * Created by lribeirogomes on 22/11/15.
 */
public class AuthenticateUserService extends SecureSmsService {
    private String _password;
    private Map<String, Contact> _result;

    public AuthenticateUserService(String password) {
        _password = password;
        _result = null;
    }

    public Map<String, Contact> getResult() throws NullPointerException {
        if (_result == null) {
            throw new NullPointerException();
        }
        return _result;
    }


    public void Execute() throws FailedToLoginException {
        try {
            User user = User.getInstance();
            if(!user.validates(_password)) {
                throw new InvalidAuthenticationException();
            }

            Map<String, Contact> contacts = user.getContacts();

            _result = contacts;
        } catch ( InvalidAuthenticationException
                | FailedToGetPasswordException
                | FailedToGetContactsException exception ) {
            throw new FailedToLoginException(exception);
        }
    }
}
