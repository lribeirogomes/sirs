package pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain.exceptions.FailedToGetPasswordException;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain.exceptions.MethodNotImplementedException;

/**
 * Created by lribeirogomes on 22/11/15.
 */
public class Password {
    private static Password ourInstance = new Password();

    public static Password getInstance() throws FailedToGetPasswordException {
        try {
            // TODO: get passwordDigest inside DataBase

            // if password doesn't exist
            //     return null

            return ourInstance;
        } catch (Exception exception) {
            throw new FailedToGetPasswordException(exception);
        }
    }

    public static Password getInstance(String passwordName) throws FailedToGetPasswordException {
        MessageDigest md;

        try {
            md = MessageDigest.getInstance("SHA-512");

            md.update(passwordName.getBytes());
            byte[] passwordDigest = md.digest();

            // TODO: store passwordDigest inside DataBase

            return ourInstance;
        } catch (NoSuchAlgorithmException exception) {
            throw new FailedToGetPasswordException(exception);
        }
    }

    private Password() {
    }

    public void setPassword(String password) throws MethodNotImplementedException {
        throw new MethodNotImplementedException("getAllEncryptedSMS");
    }
}
