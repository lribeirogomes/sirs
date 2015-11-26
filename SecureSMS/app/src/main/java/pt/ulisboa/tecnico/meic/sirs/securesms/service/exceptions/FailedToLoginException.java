package pt.ulisboa.tecnico.meic.sirs.securesms.service.exceptions;

/**
 * Created by lribeirogomes on 17/11/15.
 */
public class FailedToLoginException extends SecureSMSException {
    public FailedToLoginException(Throwable throwable){
        super("Failed to create password.", throwable);
    }
}
