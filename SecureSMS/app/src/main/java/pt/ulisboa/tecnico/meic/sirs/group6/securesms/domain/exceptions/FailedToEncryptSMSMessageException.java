package pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain.exceptions;

/**
 * Created by joao on 11/14/15.
 */
public class FailedToEncryptSMSMessageException extends Exception{
    public FailedToEncryptSMSMessageException(Throwable throwable){
        super("Failed to encrypt SMS message", throwable);
    }
}
