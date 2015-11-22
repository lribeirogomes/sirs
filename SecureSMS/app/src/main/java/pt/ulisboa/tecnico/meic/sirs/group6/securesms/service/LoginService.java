package pt.ulisboa.tecnico.meic.sirs.group6.securesms.service;

import pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain.Password;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain.exceptions.FailedToGetPasswordException;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.service.exceptions.FailedToLoginException;

/**
 * Created by lribeirogomes on 22/11/15.
 */
public class LoginService {
    private String _password;

    public LoginService(String password) {
        _password = password;
    }

    public void Execute() throws FailedToLoginException {
        try {
            Password.getInstance(_password);
        } catch (FailedToGetPasswordException exception) {
            throw new FailedToLoginException(exception);
        }
    }
}
