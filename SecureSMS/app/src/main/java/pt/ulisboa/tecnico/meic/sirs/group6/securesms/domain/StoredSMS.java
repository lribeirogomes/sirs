package pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain;

import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import pt.ulisboa.tecnico.meic.sirs.group6.securesms.dataAccess.EncryptDBDataService;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.dataAccess.EncryptionService;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.dataAccess.StoreSMSService;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.dataAccess.exceptions.FailedToEncryptSMSException;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.dataAccess.exceptions.FailedToGetResultException;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.dataAccess.exceptions.FailedToStoreEncryptedSMSException;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain.exceptions.FailedToGetStoredSMSException;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain.exceptions.MethodNotImplementedException;

/**
 * Created by lribeirogomes on 17/11/15.
 */
public class StoredSMS extends AbstractSMS {
    public StoredSMS getInstance(String password, String destinationAddress, String data)
            throws FailedToGetStoredSMSException {
        byte [] result;
        Calendar calendar = new GregorianCalendar(TimeZone.getDefault());
        int date = calendar.get(GregorianCalendar.DATE);

        try {
            // Encrypt SMS Body using password
            EncryptionService encryptionService = new EncryptDBDataService(password, data);
            encryptionService.Execute();
            result = encryptionService.getResult();

            // Store Encrypted SMS
            StoreSMSService storageService = new StoreSMSService(date, destinationAddress, result);
            storageService.Execute();

            return new StoredSMS(date ,destinationAddress, data) ;
        } catch (
                NullPointerException |
                        IllegalArgumentException |
                        FailedToGetResultException |
                        FailedToEncryptSMSException |
                        FailedToStoreEncryptedSMSException exception) {
            throw new FailedToGetStoredSMSException(exception);
        }
    }

    public EncryptedSMS[] getInstance(String destinationAddress, byte[] data)
            throws MethodNotImplementedException {
        throw new MethodNotImplementedException("getAllEncryptedSMS");
    }

    public EncryptedSMS[] getAllInstances()
            throws MethodNotImplementedException {
        throw new MethodNotImplementedException("getAllEncryptedSMS");
    }

    private StoredSMS (int date, String destinationAddress, String data) {
        super (date, destinationAddress, data.getBytes(Charset.defaultCharset()));
    }
}
