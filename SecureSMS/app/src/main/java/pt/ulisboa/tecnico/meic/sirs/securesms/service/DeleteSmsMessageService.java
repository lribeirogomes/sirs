package pt.ulisboa.tecnico.meic.sirs.securesms.service;

import pt.ulisboa.tecnico.meic.sirs.securesms.domain.Contact;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.SmsMessage;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.SmsMessageManager;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToDeleteSmsMessageException;
import pt.ulisboa.tecnico.meic.sirs.securesms.service.exceptions.FailedServiceException;

/**
 * Created by lribeirogomes on 23/11/15.
 */
public class DeleteSmsMessageService extends SecureSmsService {
    private SmsMessage _smsMessage;

    public DeleteSmsMessageService(SmsMessage smsMessage) {
        _smsMessage = smsMessage;
    }

    public void execute() throws FailedServiceException {
        try {
            SmsMessageManager.deleteSmsMessage(new Contact(null,null,null), _smsMessage);
        } catch ( FailedToDeleteSmsMessageException exception ) {
            throw new FailedServiceException("delete sms message", exception);
        }
    }
}
