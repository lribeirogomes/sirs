package pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain.exceptions;

/**
 * Created by lribeirogomes on 17/11/15.
 */
public class FailedToStoreContactException extends Exception {
    public FailedToStoreContactException(Throwable throwable){
        super("Failed to store contact.", throwable);
    }
}
