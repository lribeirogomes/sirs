package pt.ulisboa.tecnico.meic.sirs.securesms.service.exceptions;

/**
 * Created by lribeirogomes on 17/11/15.
 */
public class FailedToFirstUseException extends SecureSMSException {
    public FailedToFirstUseException(Throwable throwable){
        super("Failed to first use Secure SmsMessage.", throwable);
    }
}
