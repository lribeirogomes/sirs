package pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain;

import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import pt.ulisboa.tecnico.meic.sirs.group6.securesms.dataAccess.EncryptionManager;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.dataAccess.StoreSMSService;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.dataAccess.exceptions.FailedToEncryptSMSException;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.dataAccess.exceptions.FailedToStoreSMSException;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain.exceptions.FailedToGetSMSException;

/**
 * Created by lribeirogomes on 17/11/15.
 */
public class SMS {
    private int _timestamp;
    private String _destinationAddress;
    private byte[] _content;

    public static SMS getInstance(String password, String destinationAddress, String content)
            throws FailedToGetSMSException {
        byte [] result;
        Calendar calendar = new GregorianCalendar(TimeZone.getDefault());
        int date = calendar.get(GregorianCalendar.DATE);

        try {
            EncryptionManager manager = EncryptionManager.getInstance();

            // Encrypt SMS Body using password
            result = manager.encryptWithPassword(password, content);

            // Store Encrypted SMS
            StoreSMSService storageService = new StoreSMSService(date, destinationAddress, result);
            storageService.Execute();

            return new SMS(date ,destinationAddress, content) ;
        } catch (
                NullPointerException |
                IllegalArgumentException |
                FailedToEncryptSMSException |
                        FailedToStoreSMSException exception) {
            throw new FailedToGetSMSException(exception);
        }
    }

    public static SMS getInstance(String destinationAddress, byte[] content)
            throws FailedToGetSMSException {
        Calendar calendar = new GregorianCalendar(TimeZone.getDefault());
        int date = calendar.get(GregorianCalendar.DATE);

        try {
            // Store Encrypted SMS
            StoreSMSService storageService = new StoreSMSService(date, destinationAddress, content);
            storageService.Execute();

            return new SMS(date ,destinationAddress, content) ;
        } catch (
                NullPointerException |
                        IllegalArgumentException |
                        FailedToStoreSMSException exception) {
            throw new FailedToGetSMSException(exception);
        }
    }

    private SMS(int date, String destinationAddress, String data) {
        this(date, destinationAddress, data.getBytes(Charset.defaultCharset()));
    }
    protected SMS(int date, String destinationAddress, byte[] data) {
        _timestamp = date;
        _destinationAddress = destinationAddress;
        _content = data;
    }

    public int getTimestamp() {
        return _timestamp;
    }
    public String getDestinationAddress() {
        return _destinationAddress;
    }
    public byte[] getContent() {
        return _content;
    }
}
