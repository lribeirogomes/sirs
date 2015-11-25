package pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain.exceptions;

/**
 * Created by lribeirogomes on 17/11/15.
 */
public class FailedToGetSMSMessagesException extends Exception {
    public FailedToGetSMSMessagesException(Throwable throwable){
        super("Failed to get SMS messages.", throwable);
    }
}
