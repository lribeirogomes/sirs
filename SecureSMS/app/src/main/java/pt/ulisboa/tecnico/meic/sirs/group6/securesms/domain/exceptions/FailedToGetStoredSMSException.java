package pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain.exceptions;

/**
 * Created by lribeirogomes on 17/11/15.
 */
public class FailedToGetStoredSMSException extends Exception {
    public FailedToGetStoredSMSException(Throwable throwable){
        super("Failed to get stored SMS.", throwable);
    }
}
