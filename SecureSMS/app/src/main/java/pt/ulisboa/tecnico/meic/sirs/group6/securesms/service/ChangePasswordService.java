package pt.ulisboa.tecnico.meic.sirs.group6.securesms.service;

import pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain.Password;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain.exceptions.FailedToGetPasswordException;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.service.exceptions.FailedToLoginException;

/**
 * Created by lribeirogomes on 22/11/15.
 */
public class ChangePasswordService {
    private String _oldPassword,
                   _newPassword;

    public ChangePasswordService(String oldPassword,
                                 String newPassword) {
        _oldPassword = oldPassword;
        _newPassword = newPassword;
    }

    public void Execute() throws FailedToLoginException {
        try {
            Password password = Password.getInstance(_oldPassword);
            password.setPassword(_newPassword);

            // TODO: integrate output with interface
        } catch (Exception exception) {//FailedToGetPasswordException exception) {
            throw new FailedToLoginException(exception);
        }
    }
}
