package pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions;

/**
 * Created by lribeirogomes on 17/11/15.
 */
public class FailedToCreateContactException extends Exception {
    public FailedToCreateContactException(Throwable throwable){
        super("Failed to create contact.", throwable);
    }
}
