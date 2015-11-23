package pt.ulisboa.tecnico.meic.sirs.group6.securesms.service.exceptions;

/**
 * Created by lribeirogomes on 17/11/15.
 */
public class MethodNotImplementedException extends SecureSMSException {
    public MethodNotImplementedException(){
        super("Method not implemented.");
    }
    public MethodNotImplementedException(String method){
        super("Method " + method + " not implemented.");
    }
}
