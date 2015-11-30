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
    private String _userPhoneNumber;
    private String _userPassword;

    public CreateUserService(String userPhoneNumber, String userPassword) {
        _userPhoneNumber = userPhoneNumber;
        _userPassword = userPassword;

    }
    public void execute() throws FailedServiceException {
        try {
            UserManager.createUser(_userPhoneNumber, _userPassword);

        }catch (FailedToCreateUserException exception) {
            throw new FailedServiceException("create user", exception);
        }
    }


}
