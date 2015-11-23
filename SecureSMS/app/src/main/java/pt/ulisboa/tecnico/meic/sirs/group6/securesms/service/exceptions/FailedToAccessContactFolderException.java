package pt.ulisboa.tecnico.meic.sirs.group6.securesms.service.exceptions;

/**
 * Created by lribeirogomes on 16/11/15.
 */
public class FailedToAccessContactFolderException extends Exception {
    public FailedToAccessContactFolderException(Throwable throwable){
        super("Failed to access contact folder.", throwable);
    }
}
