package pt.ulisboa.tecnico.meic.sirs.securesms.service;

import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.SmsMessageManager;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.Contact;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToCreateSmsMessageException;
import pt.ulisboa.tecnico.meic.sirs.securesms.service.exceptions.FailedServiceException;

/**
 * Created by lribeirogomes on 23/11/15.
 */
public class CreateSmsMessageService extends SecureSmsService {
    private Contact _contact;
    private String _content;

    public CreateSmsMessageService(Contact contact, String content) {
        _contact = contact;
        _content = content;
    }

    public void execute() throws FailedServiceException {
        try {
            SmsMessageManager.createSmsMessage(_contact, _content);
        } catch (FailedToCreateSmsMessageException exception) {
            throw new FailedServiceException("create message", exception);
        }
    }
}