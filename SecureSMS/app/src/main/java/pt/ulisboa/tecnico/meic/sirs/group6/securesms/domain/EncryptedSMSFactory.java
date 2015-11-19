package pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain;

import pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain.exceptions.FailedToGetEncryptedSMSException;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain.exceptions.FailedToGetStoredSMSException;

/**
 * Created by lribeirogomes on 17/11/15.
 */
public class EncryptedSMSFactory {
    public static EncryptedSMS getEncryptedSMS(StoredSMS sms)
            throws FailedToGetEncryptedSMSException {
        byte [] result;

        try {
            result = sms.getEncryptedData();
            // TODO: get session key
            // Get session key
            //KeyManager manager = KeyManager.getInstance(password.toCharArray());

            // Encrypt SMS Body using session key
            //encryptionService = new EncryptDBDataService(password, data);
            //encryptionService.Execute();
            //result = encryptionService.getResult();

            return new EncryptedSMS(sms.getDate(), sms.getDestinationAddress(), result) ;
        } catch (
                //FailedToLoadKeyStoreException |
                //FailedToGetResultException |
                //FailedToEncryptSMSException |
                Exception exception) {//FailedToGetStoredSMSException exception) {
            throw new FailedToGetEncryptedSMSException(exception);
        }
    }
}
