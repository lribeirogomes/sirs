package pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions;

/**
 * Created by lribeirogomes on 17/11/15.
 */
public class FailedToRetrieveAllContactsException extends Exception {
    public FailedToRetrieveAllContactsException(Throwable throwable){
        super("Failed to retrieve all contacts.", throwable);
    }
}
