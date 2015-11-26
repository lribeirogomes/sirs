package pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions;

/**
 * Created by lribeirogomes on 24/11/15.
 */
public class FailedToImportIntoDataException extends Exception {
    public FailedToImportIntoDataException(Throwable throwable){
        super("Failed to import to JSON.", throwable);
    }
}