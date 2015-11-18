package pt.ulisboa.tecnico.meic.sirs.group6.securesms.dataAccess.exceptions;

/**
 * Created by lribeirogomes on 17/11/15.
 */
public class FailedToDecryptSMSException extends Exception {
    public FailedToDecryptSMSException(Throwable throwable){
        super("Failed to decrypt data.", throwable);
    }
}
