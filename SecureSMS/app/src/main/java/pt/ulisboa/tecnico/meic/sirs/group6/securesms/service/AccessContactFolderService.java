package pt.ulisboa.tecnico.meic.sirs.group6.securesms.service;

import pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain.ContactFolder;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain.SMS;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.service.exceptions.FailedToAccessContactFolderException;

/**
 * Created by lribeirogomes on 23/11/15.
 */
public class AccessContactFolderService {
    private String _destinationAddress;

    public AccessContactFolderService(String destinationAddress) {
        _destinationAddress = destinationAddress;
    }

    public void Execute() throws FailedToAccessContactFolderException {
        try {
            ContactFolder contact = ContactFolder.getInstance(_destinationAddress);
            SMS[] smsList = contact.getSMSList();

            // TODO: integrate output with interface
        } catch (Exception exception) { //FailedToGetContactException exception) {
            throw new FailedToAccessContactFolderException(exception);
        }
    }
}
