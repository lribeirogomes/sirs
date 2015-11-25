package pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain.exceptions;

/**
 * Created by lribeirogomes on 22/11/15.
 */
public class FailedToGetPasswordException extends Exception {
    public FailedToGetPasswordException(Throwable throwable){
        super("Failed to get password.", throwable);
    }
}

