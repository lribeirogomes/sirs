package pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain;

import pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain.exceptions.FailedToGetEncryptedSMSException;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain.exceptions.FailedToGetStoredSMSException;

/**
 * Created by lribeirogomes on 17/11/15.
 */
public class EncryptedSMSFactory {
    public static EncryptedSMS getEncryptedSMS(String password, String destinationAddress, String data)
            throws FailedToGetEncryptedSMSException {
        byte [] result;

        try {
            // Encrypt SMS Body using password
            StoredSMSFactory smsFactory = new StoredSMSFactory();
            StoredSMS sms = smsFactory.getStoredSMS(password, destinationAddress, data);

            result = sms.getEncryptedData();
            // TODO: get session key
            // Get session key
            //KeyManager manager = KeyManager.getInstance(password.toCharArray());

            // Encrypt SMS Body using session key
            //encryptionService = new EncryptSMSDataWithPasswordService(password, data);
            //encryptionService.Execute();
            //result = encryptionService.getResult();

            return new EncryptedSMS(sms.getDate(), sms.getDestinationAddress(), result) ;
        } catch (
                //FailedToLoadKeyStoreException |
                //FailedToGetResultException |
                //FailedToEncryptSMSException |
                FailedToGetStoredSMSException exception) {
            throw new FailedToGetEncryptedSMSException(exception);
        }
    }
}
