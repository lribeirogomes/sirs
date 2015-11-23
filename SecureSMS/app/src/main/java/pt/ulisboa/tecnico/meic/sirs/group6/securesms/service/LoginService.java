package pt.ulisboa.tecnico.meic.sirs.group6.securesms.service;

import pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain.Contact;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain.Password;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.service.exceptions.FailedToLoginException;

/**
 * Created by lribeirogomes on 22/11/15.
 */
public class LoginService extends SecureSMSService {
    private String _password;

    public LoginService(String password) {
        _password = password;
    }

    public void Execute() throws FailedToLoginException {
        try {
            Password password = Password.getInstance(_password);
            Contact[] contactFolderList = password.getContactFolderList();

            // TODO: integrate output with interface
        } catch (Exception exception) {//FailedToGetPasswordException exception) {
            throw new FailedToLoginException(exception);
        }
    }
}
