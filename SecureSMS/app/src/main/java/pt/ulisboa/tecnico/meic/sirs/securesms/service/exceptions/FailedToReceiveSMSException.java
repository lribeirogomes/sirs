package pt.ulisboa.tecnico.meic.sirs.securesms.service.exceptions;

/**
 * Created by lribeirogomes on 17/11/15.
 */
public class FailedToReceiveSMSException extends SecureSMSException {
    public FailedToReceiveSMSException(Throwable throwable){
        super("Failed to receive sms.", throwable);
    }
}
