package pt.ulisboa.tecnico.meic.sirs.securesms.service;

import android.content.Context;

import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.DataManager;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.UserManager;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToCreateDataBaseException;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToCreateUserException;
import pt.ulisboa.tecnico.meic.sirs.securesms.service.exceptions.FailedServiceException;

/**
 * Created by Ana Beatriz on 30/11/2015.
 */
public class CreateUserService extends SecureSmsService {
    private Context _context;
    private String _userPhoneNumber;
    private String _userPassword;

    public CreateUserService(Context context, String userPhoneNumber, String userPassword) {
        _context = context;
        _userPhoneNumber = userPhoneNumber;
        _userPassword = userPassword;

    }
    public void execute() throws FailedServiceException {
        try {
            UserManager.createUser(_context, _userPhoneNumber, _userPassword);

        }catch (FailedToCreateUserException exception) {
            throw new FailedServiceException("Failed to create user", exception);
        }
    }


}
