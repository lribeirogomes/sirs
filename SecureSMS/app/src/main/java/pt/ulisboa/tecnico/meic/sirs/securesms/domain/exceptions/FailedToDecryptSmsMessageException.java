package pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions;

/**
 * Created by joao on 11/14/15.
 */
public class FailedToDecryptSmsMessageException extends Exception{
    public FailedToDecryptSmsMessageException(){
        super("Failed to decrypt Sms message");
    }
}