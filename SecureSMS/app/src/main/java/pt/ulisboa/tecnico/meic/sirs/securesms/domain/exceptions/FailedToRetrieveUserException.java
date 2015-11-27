package pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions;

/**
 * Created by lribeirogomes on 17/11/15.
 */
public class FailedToRetrieveUserException extends Exception {
    public FailedToRetrieveUserException(Throwable throwable){
        super("Failed to retrieve user.", throwable);
    }
}
