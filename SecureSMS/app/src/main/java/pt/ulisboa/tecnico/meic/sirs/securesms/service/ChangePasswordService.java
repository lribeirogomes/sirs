package pt.ulisboa.tecnico.meic.sirs.securesms.service;

import pt.ulisboa.tecnico.meic.sirs.securesms.domain.User;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.UserManager;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToRetrieveUserException;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToSetPasswordException;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToUpdateUserException;
import pt.ulisboa.tecnico.meic.sirs.securesms.service.exceptions.FailedServiceException;

/**
 * Created by lribeirogomes on 22/11/15.
 */
public class ChangePasswordService extends SecureSmsService {
    private String _oldPassword,
                   _newPassword;

    public ChangePasswordService(String oldPassword, String newPassword) {
        _oldPassword = oldPassword;
        _newPassword = newPassword;
    }

    public void Execute() throws FailedServiceException {
        try {
            User user = UserManager.retrieveUser();

            user.setPassword(_oldPassword, _newPassword);
            UserManager.updateUser(user);
        } catch ( FailedToRetrieveUserException
                | FailedToSetPasswordException
                | FailedToUpdateUserException exception) {
            throw new FailedServiceException("change password", exception);
        }
    }
}
