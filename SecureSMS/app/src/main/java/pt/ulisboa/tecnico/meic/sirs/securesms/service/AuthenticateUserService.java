package pt.ulisboa.tecnico.meic.sirs.securesms.service;

import android.content.Context;

import pt.ulisboa.tecnico.meic.sirs.securesms.domain.User;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.UserManager;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToRetrieveUserException;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToValidatePasswordException;
import pt.ulisboa.tecnico.meic.sirs.securesms.service.exceptions.FailedServiceException;

/**
 * Created by lribeirogomes on 22/11/15.
 */
public class AuthenticateUserService extends SecureSmsService {
    private Context _context;
    private String _phoneNumber;
    private String _password;

    public AuthenticateUserService(Context context, String phoneNumber, String password) {
        _context = context;
        _phoneNumber = phoneNumber;
        _password = password;
    }

    public void execute() throws FailedServiceException {
        try {
            User user = UserManager.retrieveUser(_context, _phoneNumber);

            user.validatePassword(_password);
        } catch ( FailedToRetrieveUserException
                | FailedToValidatePasswordException exception ) {
            throw new FailedServiceException("authenticate user", exception);
        }
    }
}
