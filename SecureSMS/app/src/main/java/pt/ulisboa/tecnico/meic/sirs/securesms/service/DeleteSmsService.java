package pt.ulisboa.tecnico.meic.sirs.securesms.service;

import pt.ulisboa.tecnico.meic.sirs.securesms.domain.SmsMessage;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToDeleteSmsMessageException;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToGetSmsMessageException;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToGetSmsMessagesException;
import pt.ulisboa.tecnico.meic.sirs.securesms.service.exceptions.FailedToDeleteSMSException;

/**
 * Created by lribeirogomes on 23/11/15.
 */
public class DeleteSmsService extends SecureSmsService {
    private String _destinationAddress, _data;

    public DeleteSmsService (String destinationAddress, String data) {
        _destinationAddress = destinationAddress;
        _data = data;
    }

    public void Execute() throws FailedToDeleteSMSException {
        try {
            SmsMessage sms = SmsMessage.getInstance(_destinationAddress, _data);
            sms.onDelete();
        } catch ( FailedToGetSmsMessageException
                | FailedToDeleteSmsMessageException exception ) {
            throw new FailedToDeleteSMSException(exception);
        }
    }
}
