package pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions;

/**
 * Created by lribeirogomes on 17/11/15.
 */
public class FailedToDeleteContactException extends Exception {
    public FailedToDeleteContactException(Throwable throwable){
        super("Failed to delete contact.", throwable);
    }
}
