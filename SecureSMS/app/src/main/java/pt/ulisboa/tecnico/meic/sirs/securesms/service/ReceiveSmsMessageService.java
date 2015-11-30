package pt.ulisboa.tecnico.meic.sirs.securesms.service;

import android.content.Context;
import android.content.Intent;

import java.nio.charset.Charset;

import pt.ulisboa.tecnico.meic.sirs.securesms.domain.SmsMessage;
import pt.ulisboa.tecnico.meic.sirs.securesms.presentation.ShowSMSActivity;
import pt.ulisboa.tecnico.meic.sirs.securesms.service.exceptions.FailedServiceException;
import pt.ulisboa.tecnico.meic.sirs.securesms.service.exceptions.FailedToGetResultException;

/**
 * Created by lribeirogomes on 17/11/15.
 */

public class ReceiveSmsMessageService extends SecureSmsService {
    private String _phoneNumber;
    private byte[] _encryptedSms;
    private SmsMessage _result;

    public ReceiveSmsMessageService(String phoneNumber, byte[] data) {
        _phoneNumber = phoneNumber;
        _encryptedSms = data;
        _result = null;
    }

    public SmsMessage getResult() throws FailedToGetResultException {
        if (_result == null) {
            throw new FailedToGetResultException();
        }
        return _result;
    }

    public void execute() throws FailedServiceException {
        SecureSmsService service;

        try {
            service = new DecryptSmsMessageService(_phoneNumber, _encryptedSms);
            service.execute();
        } catch (FailedServiceException exception) {
            throw new FailedServiceException("receive sms message", exception);
        }
    }
}

