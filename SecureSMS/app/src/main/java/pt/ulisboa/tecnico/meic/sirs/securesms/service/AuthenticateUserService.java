package pt.ulisboa.tecnico.meic.sirs.securesms.service;

import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.KeyManager;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToLoadKeyStoreException;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.User;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.UserManager;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToRetrieveUserException;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToValidatePasswordException;
import pt.ulisboa.tecnico.meic.sirs.securesms.service.exceptions.FailedServiceException;

/**
 * Created by lribeirogomes on 22/11/15.
 */
public class AuthenticateUserService extends SecureSmsService {
    private String _phoneNumber;
    private String _password;

    public AuthenticateUserService(String phoneNumber, String password) {
        _phoneNumber = phoneNumber;
        _password = password;
    }

    public void execute() throws FailedServiceException {
        try {
            User user = UserManager.retrieveUser(_phoneNumber);

            user.validatePassword(_password);
            KeyManager.getInstance(_password);
        } catch ( FailedToRetrieveUserException
                | FailedToValidatePasswordException
                | FailedToLoadKeyStoreException exception ) {
            throw new FailedServiceException("authenticate user", exception);
        }
    }
}

