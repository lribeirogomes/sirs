package pt.ulisboa.tecnico.meic.sirs.group6.securesms.service;

import java.util.List;

import pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain.Contact;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain.SMSMessage;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.service.exceptions.FailedToAccessContactFolderException;

/**
 * Created by lribeirogomes on 23/11/15.
 */
public class AccessContactFolderService extends SecureSMSService {
    private String _destinationAddress;

    public AccessContactFolderService(String destinationAddress) {
        _destinationAddress = destinationAddress;
    }

    public void Execute() throws FailedToAccessContactFolderException {
        try {
            Contact contact = Contact.getInstance(_destinationAddress);
            List<SMSMessage> smsList = contact.getMessages();

            // TODO: integrate output with interface
        } catch (Exception exception) { //FailedToGetContactException exception) {
            throw new FailedToAccessContactFolderException(exception);
        }
    }
}
