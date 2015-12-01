package pt.ulisboa.tecnico.meic.sirs.securesms.service;

import android.content.Context;
import android.content.Intent;

import java.nio.charset.Charset;

import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.ContactManager;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.Contact;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.SmsMessage;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToRetrieveContactException;
import pt.ulisboa.tecnico.meic.sirs.securesms.service.exceptions.FailedServiceException;
import pt.ulisboa.tecnico.meic.sirs.securesms.service.exceptions.FailedToGetResultException;

/**
 * Created by lribeirogomes on 17/11/15.
 */

public class ReceiveSmsMessageService extends SecureSmsService {
    private String _phoneNumber;
    private byte[] _encryptedSms;
    private SmsMessage _sms;

    public ReceiveSmsMessageService(String phoneNumber, byte[] data) {
        _phoneNumber = phoneNumber;
        _encryptedSms = data;
        _sms = null;
    }

    public void execute() throws FailedServiceException {

        try {
            Contact contact = ContactManager.retrieveContactByPhoneNumber(_phoneNumber);
            DecryptSmsMessageService service = new DecryptSmsMessageService(_phoneNumber, _encryptedSms);
            service.execute();
            _sms = service.getResult();
        } catch (FailedServiceException
                | FailedToGetResultException
                | FailedToRetrieveContactException exception) {
            throw new FailedServiceException("receive sms message", exception);
        }
    }


    public SmsMessage getResult() throws FailedToGetResultException {
        if (_sms == null) {
            throw new FailedToGetResultException();
        }
        return _sms;
    }
}

