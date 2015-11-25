package pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain.exceptions;

/**
 * Created by lribeirogomes on 17/11/15.
 */
public class FailedToStoreSMSMessageException extends Exception {
    public FailedToStoreSMSMessageException(Throwable throwable){
        super("Failed to store SMS message.", throwable);
    }
}
