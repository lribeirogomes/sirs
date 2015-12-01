package pt.ulisboa.tecnico.meic.sirs.securesms.service;

import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.ContactManager;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.Contact;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.SmsMessage;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.SmsMessageManager;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToCreateSmsMessageException;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToRetrieveContactException;
import pt.ulisboa.tecnico.meic.sirs.securesms.service.exceptions.FailedServiceException;
import pt.ulisboa.tecnico.meic.sirs.securesms.service.exceptions.FailedToGetResultException;

/**
 * Created by lribeirogomes on 17/11/15.
 */
public class DecryptSmsMessageService extends SecureSmsService {
    private String _phoneNumber;
    private byte[] _encryptedSms;
    private SmsMessage _sms;

    public DecryptSmsMessageService(String phoneNumber, byte[] data) {
        _phoneNumber = phoneNumber;
        _encryptedSms = data;
        _sms = null;
    }

    public void execute() throws FailedServiceException {
        try {
            Contact contact = ContactManager.retrieveContactByPhoneNumber(_phoneNumber);
            _sms = SmsMessageManager.createSmsMessage(contact, _encryptedSms);
        } catch (FailedToCreateSmsMessageException | FailedToRetrieveContactException exception) {
            throw new FailedServiceException("decrypt sms message", exception);
        }
    }

    public SmsMessage getResult() throws FailedToGetResultException {
        if (_sms == null) {
            throw new FailedToGetResultException();
        }
        return _sms;
    }

}
