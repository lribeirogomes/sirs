package pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions;

/**
 * Created by lribeirogomes on 17/11/15.
 */
public class FailedToRemoveAttributeException extends Exception {
    public FailedToRemoveAttributeException(Throwable throwable){
        super("Failed to remove attribute.", throwable);
    }
}
