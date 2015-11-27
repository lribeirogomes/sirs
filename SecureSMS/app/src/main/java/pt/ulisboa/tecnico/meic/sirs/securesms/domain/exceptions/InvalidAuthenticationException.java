package pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions;

import pt.ulisboa.tecnico.meic.sirs.securesms.service.exceptions.SecureSmsException;

/**
 * Created by lribeirogomes on 24/11/15.
 */
public class InvalidAuthenticationException extends Exception {
    public InvalidAuthenticationException(){
        super("Invalid authentication.");
    }
}