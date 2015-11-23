package pt.ulisboa.tecnico.meic.sirs.group6.securesms.service.exceptions;

/**
 * Created by lribeirogomes on 17/11/15.
 */
public class FailedToDeleteSMSException extends Exception {
    public FailedToDeleteSMSException(Throwable throwable){
        super("Failed to delete SMS.", throwable);
    }
}
