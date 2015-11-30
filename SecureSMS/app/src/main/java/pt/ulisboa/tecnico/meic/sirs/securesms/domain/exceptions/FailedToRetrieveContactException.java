package pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions;

/**
 * Created by Ana Beatriz on 30/11/2015.
 */
public class FailedToRetrieveContactException extends Exception {
    public FailedToRetrieveContactException(Throwable throwable){
        super("Failed to retrieve contact.", throwable);
    }
}
