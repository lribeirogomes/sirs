package pt.ulisboa.tecnico.meic.sirs.securesms.service;

import pt.ulisboa.tecnico.meic.sirs.securesms.domain.User;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.UserManager;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToRetrieveUserException;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToValidatePasswordException;
import pt.ulisboa.tecnico.meic.sirs.securesms.service.exceptions.FailedServiceException;

/**
 * Created by lribeirogomes on 22/11/15.
 */
public class AuthenticateUserService extends SecureSmsService {
    private String _password;

    public AuthenticateUserService(String password) {
        _password = password;
    }

    public void Execute() throws FailedServiceException {
        try {
            User user = UserManager.retrieveUser();

            user.validatesPassword(_password);
        } catch ( FailedToRetrieveUserException
                | FailedToValidatePasswordException exception ) {
            throw new FailedServiceException("authenticate user", exception);
        }
    }
}
