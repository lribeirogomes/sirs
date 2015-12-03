package pt.ulisboa.tecnico.meic.sirs.securesms.service.exceptions;

/**
 * Created by lribeirogomes on 16/11/15.
 */
public class SecureSmsException extends Exception {
    public SecureSmsException(String message, Throwable throwable){
        super(message, throwable);
    }

    public SecureSmsException(String message){
        super(message);
    }
}
