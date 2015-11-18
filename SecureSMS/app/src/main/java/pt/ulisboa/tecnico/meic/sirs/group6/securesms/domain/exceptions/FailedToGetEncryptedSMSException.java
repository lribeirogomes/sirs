package pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain.exceptions;

/**
 * Created by lribeirogomes on 17/11/15.
 */
public class FailedToGetEncryptedSMSException extends Exception {
    public FailedToGetEncryptedSMSException(Throwable throwable){
        super("Failed to get encrypted SMS.", throwable);
    }
}
