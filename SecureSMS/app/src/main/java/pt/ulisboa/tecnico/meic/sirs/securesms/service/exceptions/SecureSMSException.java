package pt.ulisboa.tecnico.meic.sirs.securesms.service.exceptions;

/**
 * Created by lribeirogomes on 16/11/15.
 */
public class SecureSMSException extends Exception {
    public SecureSMSException(String message, Throwable throwable){
        super(message, throwable);
    }
    public SecureSMSException(String message){
        super(message);
    }
}
