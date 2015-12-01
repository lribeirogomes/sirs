package pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions;

/**
 * Created by joao on 11/14/15.
 */
public class FailedToSendSessionAcknowledgeException extends Exception{
    public FailedToSendSessionAcknowledgeException(String m){
        super(m);
    }
}
