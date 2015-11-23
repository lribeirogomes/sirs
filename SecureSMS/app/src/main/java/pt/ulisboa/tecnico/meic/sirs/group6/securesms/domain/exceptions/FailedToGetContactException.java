package pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain.exceptions;

/**
 * Created by lribeirogomes on 17/11/15.
 */
public class FailedToGetContactException extends Exception {
    public FailedToGetContactException(Throwable throwable){
        super("Failed to get contact folder.", throwable);
    }
}
