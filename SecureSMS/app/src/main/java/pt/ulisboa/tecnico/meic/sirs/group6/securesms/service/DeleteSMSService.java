package pt.ulisboa.tecnico.meic.sirs.group6.securesms.service;

import pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain.SMS;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain.exceptions.FailedToGetSMSException;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.service.exceptions.FailedToDeleteSMSException;

/**
 * Created by lribeirogomes on 23/11/15.
 */
public class DeleteSMSService {
    private String _password, _destinationAddress, _data;

    public DeleteSMSService (String password,
                           String destinationAddress,
                           String data) {
        _password = password;
        _destinationAddress = destinationAddress;
        _data = data;
    }

    public void Execute() throws FailedToDeleteSMSException {
        short smsPort= 8998;

        try {
            SMS sms = SMS.getInstance(_password, _destinationAddress, _data);

            // TODO: integrate output with interface
        } catch (
                FailedToGetSMSException exception) {
            throw new FailedToDeleteSMSException(exception);
        }
    }
}
