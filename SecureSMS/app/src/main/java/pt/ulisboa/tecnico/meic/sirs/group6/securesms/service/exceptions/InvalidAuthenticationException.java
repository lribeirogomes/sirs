package pt.ulisboa.tecnico.meic.sirs.group6.securesms.service.exceptions;

/**
 * Created by lribeirogomes on 24/11/15.
 */
public class InvalidAuthenticationException extends SecureSMSException {
    public InvalidAuthenticationException(){
        super("Invalid authentication.");
    }
}