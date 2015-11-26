package pt.ulisboa.tecnico.meic.sirs.securesms.domain;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.crypto.SecretKey;

import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.DataManager;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.KeyManager;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToLoadDataBaseException;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToLoadKeyStoreException;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToRetrieveKeyException;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToDecryptException;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToDeleteSmsMessageException;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToEncryptException;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToEncryptSmsMessageException;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToGetPasswordException;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToGetSmsMessageException;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToImportIntoDataException;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToStoreSmsMessageException;

/**
 * Created by lribeirogomes on 17/11/15.
 */
public class SmsMessage {
    public static final String TIME = "Time",
            CONTENT = "Content";

    private Date _date;
    private String _sender,
            _content;

    public static SmsMessage getInstance(String sender, byte[] cipherText) throws
            FailedToGetSmsMessageException {
        Calendar calendar = new GregorianCalendar(TimeZone.getDefault());
        Date date = calendar.getTime();
        String passwordHash,
                plainText;
        KeyManager keyManager;
        SecretKey key;
        byte[] cipheredData;
        User password;

        try {
            password = User.getInstance();
            passwordHash = password.getPasswordHash();
            keyManager = KeyManager.getInstance(passwordHash);
            key = keyManager.getSessionKey(sender);
            cipheredData = Cryptography.symmetricDecipher(cipherText, key);
            plainText = Cryptography.decode(cipheredData);

            SmsMessage sms = new SmsMessage(date, sender, plainText);
            sms.onStore();
            return sms;
        } catch ( FailedToGetPasswordException
                | FailedToLoadKeyStoreException
                | FailedToRetrieveKeyException
                | FailedToDecryptException
                | FailedToStoreSmsMessageException exception ) {
            throw new FailedToGetSmsMessageException(exception);
        }
    }

    public static SmsMessage getInstance(String sender, String content) throws
            FailedToGetSmsMessageException {
        Calendar calendar = new GregorianCalendar(TimeZone.getDefault());
        Date date = calendar.getTime();

        try {
            SmsMessage sms = new SmsMessage(date, sender, content);
            sms.onStore();
            return sms;
        } catch ( FailedToStoreSmsMessageException exception ) {
            throw new FailedToGetSmsMessageException(exception);
        }
    }

    public static SmsMessage getInstance(String cipherText, String sender, String password) throws
            FailedToGetSmsMessageException {
        byte[] encodedData;
        byte[] decipheredData;
        String plainText;
        JSONObject data;
        Date date;
        String content;

        try {
            encodedData = Cryptography.encode(cipherText);
            decipheredData = Cryptography.passwordDecipher(encodedData, password);
            plainText = Cryptography.decode(decipheredData);

            data = new JSONObject(plainText);

            date = new Date(data.getLong(TIME) * 1000);
            content = data.getString(CONTENT);

            return new SmsMessage(date, sender, content);
        } catch ( JSONException
                | FailedToDecryptException exception) {
            throw new FailedToGetSmsMessageException(exception);
        }
    }

    protected SmsMessage(Date date, String sender, String content) {
        _date = date;
        _sender = sender;
        _content = content;
    }

    public Date getdate() {
        return _date;
    }
    public String getsender() {
        return _sender;
    }
    public String getContent() {
        return _content;
    }

    public byte[] getEncryptedContent() throws FailedToEncryptSmsMessageException {
        KeyManager keyManager;
        SecretKey key;
        byte[] encodedData;
        byte[] cipheredData;
        String passwordHash;
        User password;

        try {
            password = User.getInstance();
            passwordHash = password.getPasswordHash();
            keyManager = KeyManager.getInstance(passwordHash);
            key = keyManager.getSessionKey(_sender);

            encodedData = Cryptography.encode(_content);
            cipheredData = Cryptography.symmetricCipher(encodedData, key);

            return cipheredData;
        } catch ( FailedToGetPasswordException
                | FailedToLoadKeyStoreException
                | FailedToRetrieveKeyException
                | FailedToEncryptException exception) {
            throw new FailedToEncryptSmsMessageException(exception);
        }
    }

    public void onStore() throws FailedToStoreSmsMessageException {
        String cipherText;
        DataManager dm;
        String passwordHash;
        User password;

        try {
            password = User.getInstance();
            passwordHash = password.getPasswordHash();
            cipherText = exportIntoData(passwordHash);
            dm = DataManager.getInstance();

            dm.add(_sender, cipherText);
        } catch ( FailedToGetPasswordException
                | FailedToImportIntoDataException
                | FailedToLoadDataBaseException exception) {
            throw new FailedToStoreSmsMessageException(exception);
        }
    }

    public void onDelete() throws FailedToDeleteSmsMessageException {
        String cipherText;
        DataManager dm;
        String passwordHash;
        User password;

        try {
            password = User.getInstance();
            passwordHash = password.getPasswordHash();
            cipherText = exportIntoData(passwordHash);
            dm = DataManager.getInstance();

            dm.remove(_sender, cipherText);
        } catch ( FailedToGetPasswordException
                | FailedToImportIntoDataException
                | FailedToLoadDataBaseException exception) {
            throw new FailedToDeleteSmsMessageException(exception);
        }
    }

    private String exportIntoData(String password) throws FailedToImportIntoDataException {
        byte[] encodedData;
        byte[] cipheredData;
        String cipherText;
        JSONObject data;

        try {
            data = new JSONObject();

            data.put(TIME, _date.getTime());
            data.putOpt(CONTENT, _content);

            encodedData = Cryptography.encode(data.toString());
            cipheredData = Cryptography.passwordCipher(encodedData, password);
            cipherText = Cryptography.decode(cipheredData);

            return cipherText;
        } catch ( JSONException
                | FailedToEncryptException exception) {
            throw new FailedToImportIntoDataException(exception);
        }
    }
}
