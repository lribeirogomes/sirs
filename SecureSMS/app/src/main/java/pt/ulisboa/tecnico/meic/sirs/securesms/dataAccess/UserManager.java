package pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess;

import android.content.Context;
import android.widget.Toast;

import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToCreateDataBaseException;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToGetAttributeException;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToLoadDataBaseException;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToLoadKeyStoreException;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.Cryptography;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.User;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToCreateUserException;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToHashException;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToRetrieveUserException;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToUpdateUserException;

/**
 * Created by Ana Beatriz on 26/11/2015.
 */
public class UserManager {

    public static void createUser(String phoneNumber, String password) throws  FailedToCreateUserException {
        byte[] encodedPassword;
        byte[] encodedPasswordHash;
        String passwordHash;

        try {
            // Hash password
            encodedPassword = Cryptography.decodeFromStorage(password);
            encodedPasswordHash = Cryptography.hash(encodedPassword);
            passwordHash = Cryptography.encodeForStorage(encodedPasswordHash);

            // Create or open storage
            DataManager dm = DataManager.getInstance();
            dm.setCurrentUser(phoneNumber);

            dm.setAttribute(dm.USER, dm.USER_ID, phoneNumber);
            dm.setAttribute(dm.USER, dm.PASSWORD_HASH, passwordHash);
            dm.setAttribute(dm.USER, dm.CONTACT_COUNT, 0);


        } catch ( FailedToHashException | FailedToLoadDataBaseException exception) {
            throw new FailedToCreateUserException(exception);
        }
    }

    public static User retrieveUser(String phoneNumber) throws FailedToRetrieveUserException {
        DataManager dm;
        String encodedPasswordHash;
        String userId;
        byte[] passwordHash;
        User user;

        try {
            // Get user information from storage
            dm = DataManager.getInstance();
            dm.setCurrentUser(phoneNumber);
            userId = dm.getAttributeString(dm.USER, dm.USER_ID);

            encodedPasswordHash = dm.getAttributeString(dm.USER, dm.PASSWORD_HASH);
            passwordHash = Cryptography.decodeFromStorage(encodedPasswordHash);

            // Return user
            user = new User(userId, passwordHash);
            return user;
        } catch ( FailedToLoadDataBaseException
                | FailedToGetAttributeException exception) {
            throw new FailedToRetrieveUserException(exception);
        }
    }

    // TODO: warning! This cannot be implemented for now
    /*public static void updateUser(User user) throws FailedToUpdateUserException {
        String passwordHash;
        DataManager dm;
        try {
            // Get information from user
            passwordHash = user.getPasswordHash();
            // TODO: Reimplement after bla
            // Update password in key manager
            // KeyManager.getInstance(user.getPassword());
            // Update user in storage
            dm = DataManager.getInstance();
            dm.setAttribute(dm.USER, dm.PASSWORD_HASH, passwordHash);
        } catch ( FailedToLoadDataBaseException exception ) {
            throw new FailedToUpdateUserException(exception);
        }
    }*/
}