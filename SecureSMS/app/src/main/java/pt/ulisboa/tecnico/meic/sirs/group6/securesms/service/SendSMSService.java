package pt.ulisboa.tecnico.meic.sirs.group6.securesms.service;

import android.telephony.SmsManager;

import java.nio.charset.Charset;

import javax.crypto.SecretKey;

import pt.ulisboa.tecnico.meic.sirs.group6.securesms.dataAccess.KeyManager;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.dataAccess.exceptions.FailedToLoadKeyStoreException;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.dataAccess.exceptions.FailedToRetrieveKeyException;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain.Cryptography;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain.SMSMessage;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain.exceptions.FailedToEncryptException;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain.exceptions.FailedToGetSMSException;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.service.exceptions.FailedToSendSMSException;

/**
 * Created by lribeirogomes on 15/11/15.
 */
public class SendSMSService extends SecureSMSService {
    private String _password, _phoneNumber, _plainTextSMS;

    public SendSMSService (String password,
                           String phoneNumber,
                           String plainTextSMS) {
        _password = password;
        _phoneNumber = phoneNumber;
        _plainTextSMS = plainTextSMS;
    }

    public void Execute() throws FailedToSendSMSException {
        short smsPort= 8998;

        try {
            SMSMessage sms = SMSMessage.getInstance(_phoneNumber, _plainTextSMS.getBytes(Charset.defaultCharset()));

            KeyManager keyManager = KeyManager.getInstance(_password);
            SecretKey key = keyManager.getSessionKey(_phoneNumber);

            byte[] cipheredData = Cryptography.symmetricCipher(sms.getContent(), key);

            SmsManager manager = SmsManager.getDefault();
            manager.sendDataMessage(sms.getsender(),
                    null, // TODO: define scAddress if needed
                    smsPort,
                    cipheredData,
                    null,  // TODO: define sentIntent if needed
                    null); // TODO: define deliveryIntent if needed
        } catch ( IllegalArgumentException
                | FailedToLoadKeyStoreException
                | FailedToRetrieveKeyException
                | FailedToEncryptException
                | FailedToGetSMSException exception) {
            throw new FailedToSendSMSException(exception);
        }
    }
}
