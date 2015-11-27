package pt.ulisboa.tecnico.meic.sirs.securesms.domain;

import java.util.Date;

import javax.crypto.SecretKey;

import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.KeyManager;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToLoadKeyStoreException;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToRetrieveKeyException;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToEncryptException;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToEncryptSmsMessageException;

/**
 * Created by lribeirogomes on 17/11/15.
 */
public class SmsMessage {
    private long _id,
                 _dateNumber;
    private String _sender,
            _content;

    SmsMessage(long id, long dateNumber, String sender, String content) {
        _id = id;
        _dateNumber = dateNumber;
        _sender = sender;
        _content = content;
    }

    public String getID() { return _sender + _id; }
    public long getDateNumber() {
        return _dateNumber;
    }
    public Date getDate() {
        return new Date(_dateNumber * 1000);
    }
    public String getsender() {
        return _sender;
    }
    public String getContent() {
        return _content;
    }
    public byte[] getEncryptedContent() throws
            FailedToEncryptSmsMessageException {
        KeyManager keyManager;
        SecretKey key;
        byte[] encodedData;
        byte[] cipheredData;

        try {
            // TODO: Reimplement getInstance after implementing getInstance without arguments
            // Encrypt message content
            keyManager = KeyManager.getInstance("dummy");
            key = keyManager.getSessionKey(_sender);
            encodedData = Cryptography.encode(_content);
            cipheredData = Cryptography.symmetricCipher(encodedData, key);

            return cipheredData;
        } catch ( FailedToLoadKeyStoreException
                | FailedToRetrieveKeyException
                | FailedToEncryptException exception) {
            throw new FailedToEncryptSmsMessageException(exception);
        }
    }
}
