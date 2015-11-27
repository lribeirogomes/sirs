package pt.ulisboa.tecnico.meic.sirs.securesms.service;

import pt.ulisboa.tecnico.meic.sirs.securesms.domain.SmsMessageManager;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToCreateSmsMessageException;
import pt.ulisboa.tecnico.meic.sirs.securesms.service.exceptions.FailedServiceException;

/**
 * Created by lribeirogomes on 23/11/15.
 */
public class CreateMessageService extends SecureSmsService {
    private String _sender,
                   _content;

    public CreateMessageService(String sender, String content) {
        _sender = sender;
        _content = content;
    }

    public void Execute() throws FailedServiceException {
        try {
            SmsMessageManager.createSmsMessage(_sender, _content);
        } catch (FailedToCreateSmsMessageException exception) {
            throw new FailedServiceException("create message", exception);
        }
    }
}