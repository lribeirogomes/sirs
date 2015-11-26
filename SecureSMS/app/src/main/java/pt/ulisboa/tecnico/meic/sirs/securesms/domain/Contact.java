package pt.ulisboa.tecnico.meic.sirs.securesms.domain;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.Key;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.DataManager;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToLoadDataBaseException;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToRetrieveDataException;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToDecryptException;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToEncryptException;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToGetContactException;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToGetPasswordException;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToGetSmsMessageException;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToGetSmsMessagesException;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToImportIntoDataException;
import pt.ulisboa.tecnico.meic.sirs.securesms.domain.exceptions.FailedToStoreContactException;

/**
 * Created by lribeirogomes on 23/11/15.
 */
public class Contact {
    public static final String CONTACTS = "Contacts",
                               PHONE_NUMBER = "PhoneNumber",
                               NAME = "Name";

    private String _phoneNumber,
                   _name;
    private Key _pubEncryptKey,
                _pubSignKey;
    private Session _session;
    private List<SmsMessage> _messages;

    public static Contact getInstance(String phoneNumber, String name) throws
            FailedToGetContactException {
        try {
            Contact contact = new Contact(phoneNumber, name);
            contact.onStore();
            return contact;
        } catch ( FailedToStoreContactException exception ) {
            throw new FailedToGetContactException(exception);
        }
    }

    public static Contact getInstance(String cipherText) throws
            FailedToGetContactException {
        byte[] encodedData;
        byte[] decipheredData;
        String plainText,
               content,
               name,
               passwordHash;
        JSONObject data;
        User user;

        try {
            user = User.getInstance();
            passwordHash = user.getPasswordHash();
            encodedData = Cryptography.encode(cipherText);
            decipheredData = Cryptography.passwordDecipher(encodedData, passwordHash);
            plainText = Cryptography.decode(decipheredData);

            data = new JSONObject(plainText);
            content = data.optString(PHONE_NUMBER);
            name = data.optString(NAME);

            return new Contact(content, name);
        } catch ( JSONException
                | FailedToGetPasswordException
                | FailedToDecryptException exception) {
            throw new FailedToGetContactException(exception);
        }
    }

    private Contact(String phoneNumber, String name) {
        _phoneNumber = phoneNumber;
        _name = name;
        _messages = null;
    }

    public String getPhoneNumber() {
        return _phoneNumber;
    }
    public String getName() {
        return _name;
    }

    public void onStore() throws FailedToStoreContactException {
        String cipherText;
        DataManager dm;
        String passwordHash;
        User password;

        try {
            password = User.getInstance();
            passwordHash = password.getPasswordHash();
            cipherText = exportIntoData(passwordHash);
            dm = DataManager.getInstance();

            dm.add(CONTACTS, cipherText);
        } catch ( FailedToGetPasswordException
                | FailedToImportIntoDataException
                | FailedToLoadDataBaseException exception) {
            throw new FailedToStoreContactException(exception);
        }
    }

    public List<SmsMessage> getMessages()  throws FailedToGetSmsMessagesException {
        Set<String> dataSet;
        List<SmsMessage> messages;
        SmsMessage message;
        DataManager dm;
        String passwordHash;
        User password;

        if (_messages != null) {
            return _messages;
        }

        try {
            password = User.getInstance();
            passwordHash = password.getPasswordHash();
            messages = new ArrayList<>();
            dm = DataManager.getInstance();

            dataSet = dm.getAll(_phoneNumber);
            for (String data : dataSet) {
                message = SmsMessage.getInstance(data, _phoneNumber, passwordHash);
                messages.add(message);
            }

            _messages = messages;
            return messages;
        } catch ( FailedToGetPasswordException
                | FailedToLoadDataBaseException
                | FailedToGetSmsMessageException
                | FailedToRetrieveDataException exception) {
            throw new FailedToGetSmsMessagesException(exception);
        }
    }

    private String exportIntoData(String password) throws FailedToImportIntoDataException {
        byte[] encodedData;
        byte[] cipheredData;
        String cipherText;
        JSONObject data;

        try {
            data = new JSONObject();

            data.put(PHONE_NUMBER, _phoneNumber);
            data.put(NAME, _name);

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
