package pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess;

import android.content.Context;

import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToCreateDataBaseException;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToGetAttributeException;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToLoadDataBaseException;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToLoadKeyStoreException;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.User;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToCreateUserException;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToRetrieveUserException;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToUpdateUserException;

/**
 * Created by Ana Beatriz on 26/11/2015.
 */
public class UserManager {

    public static void createUser(Context context, String phoneNumber, String password) throws  FailedToCreateUserException {
        try {
            // Create or open storage
            DataManager dm = DataManager.createDataManager(context, phoneNumber);

            dm.setAttribute(dm.USER, dm.CONTACT_COUNT, 0);
            dm.setAttribute(dm.USER, dm.PASSWORD_HASH, password);

        } catch ( FailedToCreateDataBaseException exception) {
            throw new FailedToCreateUserException(exception);
        }
    }

    public static User retrieveUser(Context context, String phoneNumber) throws FailedToRetrieveUserException {
        DataManager dm;
        String passwordHash;
        User user;

        try {
            // Get user information from storage
            dm = DataManager.createDataManager(context, phoneNumber);
            passwordHash = dm.getAttributeString(dm.USER, dm.PASSWORD_HASH);

            // Return user
            user = new User(passwordHash);
            return user;
        } catch ( FailedToCreateDataBaseException
                | FailedToGetAttributeException exception) {
            throw new FailedToRetrieveUserException(exception);
        }
    }

    public static void updateUser(User user) throws FailedToUpdateUserException {
        String passwordHash;
        DataManager dm;

        try {
            // Get information from user
            passwordHash = user.getPasswordHash();

            // Update password in key manager
            KeyManager.getInstance(user.getPassword());

            // Update user in storage
            dm = DataManager.getInstance();
            dm.setAttribute(dm.USER, dm.PASSWORD_HASH, passwordHash);
        } catch ( FailedToLoadDataBaseException
                | FailedToLoadKeyStoreException exception ) {
            throw new FailedToUpdateUserException(exception);
        }
    }
}