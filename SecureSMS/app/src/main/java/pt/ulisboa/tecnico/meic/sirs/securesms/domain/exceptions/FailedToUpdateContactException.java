package pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions;

/**
 * Created by lribeirogomes on 17/11/15.
 */
public class FailedToUpdateContactException extends Exception {
    public FailedToUpdateContactException(Throwable throwable){
        super("Failed to update contact.", throwable);
    }
}
