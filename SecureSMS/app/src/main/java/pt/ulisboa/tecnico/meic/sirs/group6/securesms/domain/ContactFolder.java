package pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain;

import pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain.exceptions.MethodNotImplementedException;

/**
 * Created by lribeirogomes on 23/11/15.
 */
public class ContactFolder {
    private String _destinationAddress;
    private Session _session;

    public static ContactFolder getInstance(String destinationAddress) {// throws FailedToGetContactException {
        // TODO: integrate obect with database

        return new ContactFolder(destinationAddress);
    }

    private ContactFolder(String destinationAddress) {
        _destinationAddress = destinationAddress;
    }

    public SMS[] getSMSList() throws MethodNotImplementedException {
        throw new MethodNotImplementedException();
    }
}
