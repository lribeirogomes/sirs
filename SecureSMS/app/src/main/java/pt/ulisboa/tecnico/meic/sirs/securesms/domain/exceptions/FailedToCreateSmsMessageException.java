package pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions;

/**
 * Created by lribeirogomes on 17/11/15.
 */
public class FailedToCreateSmsMessageException extends Exception {
    public FailedToCreateSmsMessageException(Throwable throwable){
        super("Failed to create Sms message.", throwable);
    }
}
