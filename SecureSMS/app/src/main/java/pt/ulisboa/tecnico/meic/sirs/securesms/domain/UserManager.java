package pt.ulisboa.tecnico.meic.sirs.securesms.domain;

import android.content.Context;

import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.DataManager;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToCreateDataBaseException;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToGetAttributeException;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToLoadDataBaseException;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToCreateUserException;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToHashException;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToRetrieveUserException;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToUpdateUserException;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.InvalidAuthenticationException;

/**
 * Created by Ana Beatriz on 26/11/2015.
 */
public class UserManager {
    private static final String USER_NAME = "UserName",
                         PASSWORD_HASH = "passwordHash";

    public static void createUser(Context context) throws
            FailedToCreateUserException {
        try {
            // Create or open storage
            DataManager.createDataManager(context);
        } catch ( FailedToCreateDataBaseException exception) {
            throw new FailedToCreateUserException(exception);
        }
    }

    public static User retrieveUser() throws
            FailedToRetrieveUserException {
        DataManager dm;
        String passwordHash;
        User user;

        try {
            // Get user information from storage
            dm = DataManager.getInstance();
            passwordHash = dm.getAttributeString(USER_NAME, PASSWORD_HASH);

            // Return user
            user = new User(passwordHash);
            return user;
        } catch ( FailedToLoadDataBaseException
                | FailedToGetAttributeException exception) {
            throw new FailedToRetrieveUserException(exception);
        }
    }

    public static void updateUser(User user) throws
            FailedToUpdateUserException {
        String passwordHash;
        DataManager dm;

        try {
            // Get information from user
            passwordHash = user.getPasswordHash();

            // Update user in storage
            dm = DataManager.getInstance();
            dm.setAttribute(USER_NAME, PASSWORD_HASH, passwordHash);
        } catch ( FailedToLoadDataBaseException exception ) {
            throw new FailedToUpdateUserException(exception);
        }
    }
}