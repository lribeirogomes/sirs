package pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions;

/**
 * Created by joao on 11/14/15.
 */
public class FailedToEncryptSmsMessageException extends Exception{
    public FailedToEncryptSmsMessageException(Throwable throwable){
        super("Failed to encrypt SMS message", throwable);
    }
}