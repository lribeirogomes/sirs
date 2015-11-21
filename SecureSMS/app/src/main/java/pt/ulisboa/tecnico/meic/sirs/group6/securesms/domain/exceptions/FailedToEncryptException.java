package pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain.exceptions;

/**
 * Created by joao on 11/14/15.
 */
public class FailedToEncryptException extends Exception{
    public FailedToEncryptException(String m){
        super(m);
    }
}
