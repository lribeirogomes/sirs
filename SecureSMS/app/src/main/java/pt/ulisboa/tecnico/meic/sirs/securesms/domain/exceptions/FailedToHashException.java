package pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions;

/**
 * Created by joao on 11/14/15.
 */
public class FailedToHashException extends Exception{
    public FailedToHashException(Throwable throwable){
        super("Failed to produce a hash.", throwable);
    }
}
