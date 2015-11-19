package pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain.exceptions;

/**
 * Created by lribeirogomes on 17/11/15.
 */
public class MethodNotImplementedException extends Exception {
    public MethodNotImplementedException(){
        super("Method not implemented.");
    }
    public MethodNotImplementedException(String method){
        super("Method " + method + " not implemented.");
    }
}
