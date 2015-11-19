package pt.ulisboa.tecnico.meic.sirs.group6.securesms.dataAccess.exceptions;

/**
 * Created by lribeirogomes on 17/11/15.
 */
public class FailedToStoreEncryptedSMSException extends Exception{
    public FailedToStoreEncryptedSMSException(Throwable throwable){
        super("Failed do store sms.", throwable);
    }
}
