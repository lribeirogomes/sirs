package pt.ulisboa.tecnico.meic.sirs.securesms.service.exceptions;

/**
 * Created by lribeirogomes on 26/11/15.
 */
public class FailedServiceException extends SecureSmsException {
    public FailedServiceException(String message, Throwable throwable){
        super("Failed to " + message + ".", throwable);
    }
}
