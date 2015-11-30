package pt.ulisboa.tecnico.meic.sirs.securesms.domain;

import android.util.Log;

import java.util.Arrays;

import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToHashException;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToSetPasswordException;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToValidatePasswordException;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.InvalidAuthenticationException;

/**
 * Created by lribeirogomes on 23/11/15.
 */
public class User {
    private byte[] _passwordHash;

    public User(byte[] passwordHash) {
        _passwordHash = passwordHash;
    }

    public byte[] getPasswordHash() {
        return _passwordHash;
    }

    public void validatePassword(String encodedPassword) throws
            FailedToValidatePasswordException {
        byte[] password,
               passwordHash;

        try {
            // Hash password
            password = Cryptography.decodeFromStorage(encodedPassword);
            passwordHash = Cryptography.hash(password);

            // If password is invalid
            if (!Arrays.equals(passwordHash, _passwordHash)) {
                // Throw invalid authentication exception
                throw new InvalidAuthenticationException();
            }
        } catch ( FailedToHashException
                | InvalidAuthenticationException exception ) {
            throw new FailedToValidatePasswordException(exception);
        }
    }

    public void setPassword(String oldPassword, String newPassword) throws
            FailedToSetPasswordException {
        byte[] encodedPassword,
                encodedPasswordHash;

        try {
            if (!_passwordHash.equals("")) {
                validatePassword(oldPassword);
            }

            // Hash new password
            encodedPassword = Cryptography.decodeFromStorage(newPassword);
            encodedPasswordHash = Cryptography.hash(encodedPassword);

            _passwordHash = encodedPasswordHash;
        } catch ( FailedToValidatePasswordException
                | FailedToHashException exception ) {
            throw new FailedToSetPasswordException(exception);
        }
    }
}
