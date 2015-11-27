package pt.ulisboa.tecnico.meic.sirs.securesms.domain;

import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToHashException;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToSetPasswordException;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToValidatePasswordException;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.InvalidAuthenticationException;

/**
 * Created by lribeirogomes on 23/11/15.
 */
public class User {
    private String _passwordHash,
                   _password;

    User(String passwordHash) {
        _passwordHash = passwordHash;
        _password = null;
    }

    String getPasswordHash() {
        return _passwordHash;
    }
    String getPassword() {
        if (_password == null) {
            // throws new exception
        }
        String password = _password;
        _password = null;
        return password;
    }
    public boolean isFirstUse() {
        return _passwordHash.equals("");
    }

    public void validatesPassword(String password) throws
            FailedToValidatePasswordException {
        byte[] encodedPassword,
                encodedPasswordHash;
        String passwordHash;

        try {
            // Hash password
            encodedPassword = Cryptography.encode(password);
            encodedPasswordHash = Cryptography.hash(encodedPassword);
            passwordHash = Cryptography.decode(encodedPasswordHash);

            // If password is invalid
            if (passwordHash.equals(_passwordHash)) {
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
        String passwordHash;

        try {
            if (!_passwordHash.equals("")) {
                validatesPassword(oldPassword);
            }

            _password = newPassword;

            // Hash new password
            encodedPassword = Cryptography.encode(newPassword);
            encodedPasswordHash = Cryptography.hash(encodedPassword);
            passwordHash = Cryptography.decode(encodedPasswordHash);

            _passwordHash = passwordHash;
        } catch ( FailedToValidatePasswordException
                | FailedToHashException exception ) {
            throw new FailedToSetPasswordException(exception);
        }
    }
}
