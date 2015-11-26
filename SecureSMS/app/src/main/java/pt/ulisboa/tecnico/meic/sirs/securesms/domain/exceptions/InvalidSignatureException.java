package pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions;

/**
 * Created by joao on 11/14/15.
 */
public class InvalidSignatureException extends Exception{
    public InvalidSignatureException(String m){
        super(m);
    }
}
