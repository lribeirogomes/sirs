package pt.ulisboa.tecnico.meic.sirs.group6.securesms.dataAccess.exceptions;

/**
 * Created by lribeirogomes on 17/11/15.
 */
public class FailedToEncryptSMSException extends Exception {
    public FailedToEncryptSMSException(Throwable throwable){
        super("Failed to encrypt data.", throwable);
    }
}
