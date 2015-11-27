package pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions;

/**
 * Created by lribeirogomes on 17/11/15.
 */
public class FailedToUpdateUserException extends Exception {
    public FailedToUpdateUserException(Throwable throwable){
        super("Failed to update user.", throwable);
    }
}
