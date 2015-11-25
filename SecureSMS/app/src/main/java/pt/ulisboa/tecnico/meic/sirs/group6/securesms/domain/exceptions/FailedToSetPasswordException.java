package pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain.exceptions;

/**
 * Created by lribeirogomes on 22/11/15.
 */
public class FailedToSetPasswordException extends Exception {
    public FailedToSetPasswordException(Throwable throwable){
        super("Failed to set password.", throwable);
    }
}

