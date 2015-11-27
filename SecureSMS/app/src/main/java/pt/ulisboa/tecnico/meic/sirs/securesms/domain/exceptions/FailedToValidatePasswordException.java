package pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions;

/**
 * Created by lribeirogomes on 17/11/15.
 */
public class FailedToValidatePasswordException extends Exception {
    public FailedToValidatePasswordException(Throwable throwable){
        super("Failed to validate password.", throwable);
    }
}
