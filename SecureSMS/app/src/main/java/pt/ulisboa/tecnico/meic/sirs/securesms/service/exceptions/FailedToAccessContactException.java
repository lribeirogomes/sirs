package pt.ulisboa.tecnico.meic.sirs.securesms.service.exceptions;

/**
 * Created by lribeirogomes on 16/11/15.
 */
public class FailedToAccessContactException extends SecureSMSException {
    public FailedToAccessContactException(Throwable throwable){
        super("Failed to access contact.", throwable);
    }
}
