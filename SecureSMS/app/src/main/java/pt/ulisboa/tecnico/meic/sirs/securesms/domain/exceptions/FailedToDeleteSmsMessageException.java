package pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions;

/**
 * Created by lribeirogomes on 17/11/15.
 */
public class FailedToDeleteSmsMessageException extends Exception {
    public FailedToDeleteSmsMessageException(Throwable throwable){
        super("Failed to delete SMS message.", throwable);
    }
}
