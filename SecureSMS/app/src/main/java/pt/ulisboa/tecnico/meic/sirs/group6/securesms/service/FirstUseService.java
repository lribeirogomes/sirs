package pt.ulisboa.tecnico.meic.sirs.group6.securesms.service;

import pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain.Password;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain.exceptions.FailedToGetPasswordException;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.service.exceptions.FailedToFirstUseException;

/**
 * Created by lribeirogomes on 22/11/15.
 */
public class FirstUseService extends SecureSMSService {
    public void Execute() throws FailedToFirstUseException {
        try {
            Password password = Password.getInstance();

            if (password == null) {
                // TODO: tell user to create first password
            } else {
                // TODO: tell user to login
            }
        } catch (FailedToGetPasswordException exception) {
            throw new FailedToFirstUseException(exception);
        }
    }
}
