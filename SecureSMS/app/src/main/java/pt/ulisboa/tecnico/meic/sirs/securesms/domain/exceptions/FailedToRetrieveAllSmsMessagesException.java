package pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions;

/**
 * Created by lribeirogomes on 17/11/15.
 */
public class FailedToRetrieveAllSmsMessagesException extends Exception {
    public FailedToRetrieveAllSmsMessagesException(Throwable throwable){
        super("Failed to retrieve all Sms messages.", throwable);
    }
}