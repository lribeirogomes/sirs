package pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain.exceptions;

/**
 * Created by lribeirogomes on 17/11/15.
 */
public class FailedToGetSMSException extends Exception {
    public FailedToGetSMSException(Throwable throwable){
        super("Failed to get SMSMessage.", throwable);
    }
}
