package pt.ulisboa.tecnico.meic.sirs.securesms.service;

import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.KeyManager;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToLoadKeyStoreException;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.User;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.UserManager;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToRetrieveUserException;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToSetPasswordException;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToUpdateUserException;
import pt.ulisboa.tecnico.meic.sirs.securesms.service.exceptions.FailedServiceException;

/**
 * Created by lribeirogomes on 22/11/15.
 */
public class DefinePasswordService extends SecureSmsService {
    private String _newPassword;

    public DefinePasswordService(String newPassword) {
        _newPassword = newPassword;
    }

    public void Execute() throws FailedServiceException {
        try {
            User user = UserManager.retrieveUser();

            user.setPassword("", _newPassword);
            UserManager.updateUser(user);
        } catch ( FailedToRetrieveUserException
                | FailedToSetPasswordException
                | FailedToUpdateUserException exception) {
            throw new FailedServiceException("define password", exception);
        }
    }
}
