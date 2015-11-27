package pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions;

/**
 * Created by lribeirogomes on 22/11/15.
 */
public class FailedToCreateUserException extends Exception {
    public FailedToCreateUserException(Throwable throwable){
        super("Failed to get password.", throwable);
    }
}

