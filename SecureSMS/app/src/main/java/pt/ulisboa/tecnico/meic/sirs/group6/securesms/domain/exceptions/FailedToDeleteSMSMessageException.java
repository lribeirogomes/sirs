package pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain.exceptions;

/**
 * Created by lribeirogomes on 17/11/15.
 */
public class FailedToDeleteSMSMessageException extends Exception {
    public FailedToDeleteSMSMessageException(Throwable throwable){
        super("Failed to delete SMS message.", throwable);
    }
}
