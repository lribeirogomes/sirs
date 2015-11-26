package pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions;

/**
 * Created by lribeirogomes on 17/11/15.
 */
public class FailedToGetSmsMessagesException extends Exception {
    public FailedToGetSmsMessagesException(Throwable throwable){
        super("Failed to get SMS messages.", throwable);
    }
}