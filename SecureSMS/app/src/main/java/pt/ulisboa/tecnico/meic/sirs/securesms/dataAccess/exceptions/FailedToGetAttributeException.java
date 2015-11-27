package pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions;

/**
 * Created by lribeirogomes on 17/11/15.
 */
public class FailedToGetAttributeException extends Exception {
    public FailedToGetAttributeException(Throwable throwable){
        super("Failed to set attribute.", throwable);
    }
}
