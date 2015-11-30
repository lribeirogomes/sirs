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
    private String _id;
    private long _dateNumber;
    private Contact _contact;
    private String _content;

    public SmsMessage(String id, Contact contact, long dateNumber, String content) {
        _id = id;
        _contact = contact;
        _dateNumber = dateNumber;
        _content = content;
    }

    public String getId() { return _id; }

    public Contact getContact() {return _contact; }

    public Date getDate() { return new Date(_dateNumber * 1000); }

    public String getContent() { return _content; }

    public byte[] getEncryptedContent() throws
            FailedToEncryptSmsMessageException {
        KeyManager keyManager;
        SecretKey key;
        byte[] encodedData;
        byte[] cipheredData;

        try {
            // TODO: Reimplement encryption
            // Encrypt message content
            // keyManager = KeyManager.getInstance("dummy");
            // key = keyManager.getSessionKey(_sender);
            encodedData = Cryptography.encode(_content);
            // cipheredData = Cryptography.symmetricCipher(encodedData, key);

            // return cipheredData;
            return encodedData;
        } catch //( FailedToLoadKeyStoreException
                //| FailedToRetrieveKeyException
                //| FailedToEncryptException exception) {
                (Exception exception) {
            throw new FailedToEncryptSmsMessageException(exception);
        }
    }
}
