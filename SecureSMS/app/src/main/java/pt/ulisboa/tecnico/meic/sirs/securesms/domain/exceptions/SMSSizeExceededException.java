package pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions;

/**
 * Created by joao on 11/14/15.
 */
public class SMSSizeExceededException extends Exception{
    public SMSSizeExceededException(String m){
        super(m);
    }
}
