package pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain;

import javax.crypto.SecretKey;

import pt.ulisboa.tecnico.meic.sirs.group6.securesms.dataAccess.EncryptionManager;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.dataAccess.KeyManager;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.dataAccess.exceptions.FailedToEncryptSMSException;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.dataAccess.exceptions.FailedToLoadKeyStoreException;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.dataAccess.exceptions.FailedToRetrieveKeyException;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain.exceptions.FailedToGetSMSException;

/**
 * Created by lribeirogomes on 17/11/15.
 */
public class EncryptedSMS extends SMS {
    public static EncryptedSMS getInstance(String password, String destinationAddress, String content)
            throws FailedToGetSMSException {
        byte [] result;

        try {
            SMS sms = SMS.getInstance(password, destinationAddress, content);

            KeyManager keyManager = KeyManager.getInstance(password);
            SecretKey key = keyManager.getSessionKey(destinationAddress);

            EncryptionManager encryptionManager = EncryptionManager.getInstance();
            result = encryptionManager.encryptWithSessionKey(content, key);

            return new EncryptedSMS(sms.getTimestamp(), sms.getDestinationAddress(), result) ;
        } catch (
            FailedToLoadKeyStoreException |
            FailedToRetrieveKeyException |
            FailedToEncryptSMSException exception) {
            throw new FailedToGetSMSException(exception);
        }
    }

    private EncryptedSMS (int date, String destinationAddress, byte[] data) {
        super (date, destinationAddress, data);
    }
}
