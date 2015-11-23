package pt.ulisboa.tecnico.meic.sirs.group6.securesms.service.exceptions;

/**
 * Created by lribeirogomes on 16/11/15.
 */
public class FailedToSendSMSException extends SecureSMSException {
    public FailedToSendSMSException(Throwable throwable){
        super("Failed to send sms.", throwable);
    }
}
