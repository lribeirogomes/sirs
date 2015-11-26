package pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions;

/**
 * Created by lribeirogomes on 17/11/15.
 */
public class FailedToGetContactsException extends Exception {
    public FailedToGetContactsException(Throwable throwable){
        super("Failed to get contacts.", throwable);
    }
}
