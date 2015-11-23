package pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain.exceptions;

/**
 * Created by joao on 11/14/15.
 */
public class FailedToSignException extends Exception{
    public FailedToSignException(Throwable throwable){
        super("Failed to produce a signature.", throwable);
    }
}
