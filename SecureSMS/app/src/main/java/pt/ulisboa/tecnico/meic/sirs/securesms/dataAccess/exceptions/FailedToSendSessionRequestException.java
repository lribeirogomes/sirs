package pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions;

/**
 * Created by joao on 11/14/15.
 */
public class FailedToSendSessionRequestException extends Exception{
    public FailedToSendSessionRequestException(String m){
        super(m);
    }
}
