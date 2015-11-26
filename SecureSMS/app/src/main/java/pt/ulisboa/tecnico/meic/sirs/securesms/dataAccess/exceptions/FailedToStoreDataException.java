package pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions;

/**
 * Created by lribeirogomes on 17/11/15.
 */
public class FailedToStoreDataException extends Exception {
    public FailedToStoreDataException(){
        super("Failed to store data.");
    }
}
