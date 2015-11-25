package pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain.exceptions;

/**
 * Created by lribeirogomes on 17/11/15.
 */
public class FailedToStorePasswordException extends Exception {
    public FailedToStorePasswordException(Throwable throwable){
        super("Failed to store password.", throwable);
    }
}
