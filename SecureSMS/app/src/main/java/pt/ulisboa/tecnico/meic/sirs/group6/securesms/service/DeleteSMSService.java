package pt.ulisboa.tecnico.meic.sirs.group6.securesms.service;

import pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain.SMSMessage;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain.exceptions.FailedToDeleteSMSMessageException;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain.exceptions.FailedToGetSMSMessageException;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain.exceptions.FailedToGetSMSMessagesException;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.service.exceptions.FailedToDeleteSMSException;

/**
 * Created by lribeirogomes on 23/11/15.
 */
public class DeleteSMSService extends SecureSMSService {
    private String _destinationAddress, _data;

    public DeleteSMSService (String destinationAddress, String data) {
        _destinationAddress = destinationAddress;
        _data = data;
    }

    public void Execute() throws FailedToDeleteSMSException {
        try {
            SMSMessage sms = SMSMessage.getInstance(_destinationAddress, _data);
            sms.onDelete();
        } catch ( FailedToGetSMSMessageException
                | FailedToDeleteSMSMessageException exception ) {
            throw new FailedToDeleteSMSException(exception);
        }
    }
}
