package pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain.exceptions;

/**
 * Created by lribeirogomes on 17/11/15.
 */
public class FailedToGetSMSMessageException extends Exception {
    public FailedToGetSMSMessageException(Throwable throwable){
        super("Failed to get SMS message.", throwable);
    }
}
