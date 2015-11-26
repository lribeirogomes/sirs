package pt.ulisboa.tecnico.meic.sirs.securesms.service;

import pt.ulisboa.tecnico.meic.sirs.securesms.domain.User;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToGetPasswordException;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToSetPasswordException;
import pt.ulisboa.tecnico.meic.sirs.securesms.service.exceptions.FailedToLoginException;
import pt.ulisboa.tecnico.meic.sirs.securesms.service.exceptions.InvalidAuthenticationException;

/**
 * Created by lribeirogomes on 22/11/15.
 */
public class ChangePasswordService extends SecureSmsService {
    private String _oldPassword,
                   _newPassword;

    public ChangePasswordService(String oldPassword,
                                 String newPassword) {
        _oldPassword = oldPassword;
        _newPassword = newPassword;
    }

    public void Execute() throws FailedToLoginException {
        try {
            User user = User.getInstance();
            if(!user.validates(_oldPassword)) {
                throw new InvalidAuthenticationException();
            }
            user.setPassword(_newPassword);
        } catch ( InvalidAuthenticationException
                | FailedToGetPasswordException
                | FailedToSetPasswordException exception) {
            throw new FailedToLoginException(exception);
        }
    }
}
