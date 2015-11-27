package pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions;

/**
 * Created by lribeirogomes on 17/11/15.
 */
public class FailedToAddAttributeException extends Exception {
    public FailedToAddAttributeException(Throwable throwable){
        super("Failed to add attribute.", throwable);
    }
}
