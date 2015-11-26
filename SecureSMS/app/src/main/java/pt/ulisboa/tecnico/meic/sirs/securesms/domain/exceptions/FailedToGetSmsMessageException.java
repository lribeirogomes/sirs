package pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions;

/**
 * Created by lribeirogomes on 17/11/15.
 */
public class FailedToGetSmsMessageException extends Exception {
    public FailedToGetSmsMessageException(Throwable throwable){
        super("Failed to get SMS message.", throwable);
    }
}
