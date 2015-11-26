package pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions;

/**
 * Created by lribeirogomes on 17/11/15.
 */
public class FailedToStoreSmsMessageException extends Exception {
    public FailedToStoreSmsMessageException(Throwable throwable){
        super("Failed to store SMS message.", throwable);
    }
}
