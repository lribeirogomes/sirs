package pt.ulisboa.tecnico.meic.sirs.group6.securesms.service.exceptions;

/**
 * Created by lribeirogomes on 16/11/15.
 */
public class FailedToAddContactException extends SecureSMSException {
    public FailedToAddContactException(Throwable throwable){
        super("Failed to add contact.", throwable);
    }
}
