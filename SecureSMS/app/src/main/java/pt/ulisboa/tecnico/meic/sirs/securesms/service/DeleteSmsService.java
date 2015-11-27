package pt.ulisboa.tecnico.meic.sirs.securesms.service;

import pt.ulisboa.tecnico.meic.sirs.securesms.domain.SmsMessage;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.SmsMessageManager;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToDeleteSmsMessageException;
import pt.ulisboa.tecnico.meic.sirs.securesms.service.exceptions.FailedServiceException;

/**
 * Created by lribeirogomes on 23/11/15.
 */
public class DeleteSmsService extends SecureSmsService {
    private SmsMessage _smsMessage;

    public DeleteSmsService (SmsMessage smsMessage) {
        _smsMessage = smsMessage;
    }

    public void Execute() throws FailedServiceException {
        try {
            SmsMessageManager.deleteSmsMessage(_smsMessage);
        } catch ( FailedToDeleteSmsMessageException exception ) {
            throw new FailedServiceException("delete sms message", exception);
        }
    }
}
