package pt.ulisboa.tecnico.meic.sirs.group6.securesms.service.exceptions;

/**
 * Created by lribeirogomes on 17/11/15.
 */
public class FailedToFirstUseException extends Exception {
    public FailedToFirstUseException(Throwable throwable){
        super("Failed to first use Secure SMS.", throwable);
    }
}
