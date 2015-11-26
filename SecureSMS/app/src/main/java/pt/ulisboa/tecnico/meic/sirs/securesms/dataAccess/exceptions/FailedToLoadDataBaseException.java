package pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions;

/**
 * Created by joao on 11/14/15.
 */
public class FailedToLoadDataBaseException extends Exception{
    public FailedToLoadDataBaseException(){
        super("Failed to load database.");
    }
}
