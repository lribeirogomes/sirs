package pt.ulisboa.tecnico.meic.sirs.group6.securesms.dataAccess.exceptions;

/**
 * Created by lribeirogomes on 17/11/15.
 */
public class FailedToStoreSMSException extends Exception{
    public FailedToStoreSMSException(Throwable throwable){
        super("Failed do store sms.", throwable);
    }
}
