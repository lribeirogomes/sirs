package pt.ulisboa.tecnico.meic.sirs.securesms.service;

import android.content.Context;

import pt.ulisboa.tecnico.meic.sirs.securesms.domain.User;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.UserManager;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToCreateUserException;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToRetrieveAllSmsMessagesException;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToRetrieveUserException;
import pt.ulisboa.tecnico.meic.sirs.securesms.service.exceptions.FailedServiceException;
import pt.ulisboa.tecnico.meic.sirs.securesms.service.exceptions.FailedToGetResultException;

/**
 * Created by lribeirogomes on 22/11/15.
 */
public class GetFirstUseStateService extends SecureSmsService {
    private Context _context;
    private Boolean _result;

    public GetFirstUseStateService(Context context) {
        _context = context;
        _result = null;
    }

    public Boolean getResult() throws FailedToGetResultException {
        if (_result == null) {
            throw new FailedToGetResultException();
        }
        return _result;
    }

    public void Execute() throws FailedServiceException {
        try {
            UserManager.createUser(_context);
            User user = UserManager.retrieveUser();

            _result = user.isFirstUse();
        } catch ( Exception exception) {
            throw new FailedServiceException("get first use state", exception);
        }
    }
}
