package pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain.exceptions;

/**
 * Created by joao on 11/14/15.
 */
public class FailedToDecryptException extends Exception{
    public FailedToDecryptException(Throwable throwable){
        super("Failed to decrypt data", throwable);
    }
}
