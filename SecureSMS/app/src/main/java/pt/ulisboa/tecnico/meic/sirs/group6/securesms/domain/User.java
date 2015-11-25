package pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain;

import android.content.Context;
import android.telephony.TelephonyManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.Key;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import pt.ulisboa.tecnico.meic.sirs.group6.securesms.dataAccess.DataManager;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.dataAccess.exceptions.FailedToCreateDataBaseException;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.dataAccess.exceptions.FailedToLoadDataBaseException;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.dataAccess.exceptions.FailedToRetrieveDataException;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain.exceptions.FailedToGetContactException;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain.exceptions.FailedToGetContactsException;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain.exceptions.FailedToGetPasswordException;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain.exceptions.FailedToHashException;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain.exceptions.FailedToImportIntoDataException;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain.exceptions.FailedToSetPasswordException;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain.exceptions.FailedToStorePasswordException;

/**
 * Created by lribeirogomes on 23/11/15.
 */
public class User {
    private String _phoneNumber,
                   _passwordHash;
    private Key _priEncryptKey,
                _priSignKey;
    private Map<String, Contact> _contacts;

    private static final String USER = "User",
                                PHONE_NUMBER = "PhoneNumber",
                                PASSWORD = "Password";
    private static User _user = null;

    public static User getInstance(Context context) throws FailedToGetPasswordException {
        DataManager dm;
        TelephonyManager telephonyManager;
        Set<String> dataSet;
        String object,
               passwordHash,
               phoneNumber;
        JSONObject json;

        try {
            // Get all user information (from one user, actually)
            dm = DataManager.getInstance(context);
            dataSet = dm.getAll(USER);
            object = "";
            for (String data : dataSet) {
                object = data;
            }

            // Export user information from json object
            if (object.equals("")) {
                json = new JSONObject();
                passwordHash = "";
                phoneNumber = "";
            } else {
                json = new JSONObject(object);
                passwordHash = json.optString(PASSWORD);
                phoneNumber = json.optString(PHONE_NUMBER);
            }

            // Get phone number if nonexistent
            if (phoneNumber.equals("")){
                // Get phone number from telephone
                telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
                phoneNumber = telephonyManager.getLine1Number();

                // Store new user information
                _user = new User(phoneNumber, passwordHash);
                _user.onStore();
            }

            // Return user information
            _user = new User(phoneNumber, passwordHash);
            return _user;
        } catch ( JSONException
                | FailedToCreateDataBaseException
                | FailedToRetrieveDataException
                | FailedToStorePasswordException exception) {
            throw new FailedToGetPasswordException(exception);
        }
    }

    public static User getInstance() throws FailedToGetPasswordException {
        return _user;
    }

    public String getPasswordHash() {
        return _passwordHash;
    }
    public String getPhoneNumber() {
        return _phoneNumber;
    }

    public void onStore() throws FailedToStorePasswordException {
        DataManager dm;
        String data;

        try {
            // Prepare user information for storage
            data = exportIntoData();

            // Store user information
            dm = DataManager.getInstance();
            dm.add(USER, data);
        } catch ( FailedToLoadDataBaseException
                | FailedToImportIntoDataException exception) {
            throw new FailedToStorePasswordException(exception);
        }
    }

    private User(String phoneNumber, String passwordHash) {
        _phoneNumber = phoneNumber;
        _passwordHash = passwordHash;
    }

    public void setPassword(String password) throws FailedToSetPasswordException {
        byte[] encodedData,
               encodedHash;

        try {
            // Hash new password
            encodedData = Cryptography.encode(password);
            encodedHash = Cryptography.hash(encodedData);
            _passwordHash = Cryptography.decode(encodedHash);

            // Store user information
            onStore();
        } catch ( FailedToHashException
                | FailedToStorePasswordException exception) {
            throw new FailedToSetPasswordException(exception);
        }
    }

    public Map<String, Contact> getContacts() throws FailedToGetContactsException {
        Set<String> dataSet;
        Map<String, Contact> contacts;
        Contact contact;
        DataManager dm;

        if (_contacts != null) {
            return _contacts;
        }

        try {
            // Create contacts hash map
            contacts = new HashMap<>();

            // Get all contacts from storage
            dm = DataManager.getInstance();
            dataSet = dm.getAll(Contact.CONTACTS);
            for (String data : dataSet) {
                // Decrypt each contact and add into set
                contact = Contact.getInstance(data);
                contacts.put(contact.getPhoneNumber(), contact);
            }

            // Return set of contacts
            _contacts = contacts;
            return contacts;
        } catch ( FailedToGetContactException
                | FailedToLoadDataBaseException
                | FailedToRetrieveDataException exception) {
            throw new FailedToGetContactsException(exception);
        }
    }

    public boolean validates(String string) {
        return _passwordHash.equals(string);
    }

    private String exportIntoData() throws FailedToImportIntoDataException {
        JSONObject data;

        try {
            // Create json object
            data = new JSONObject();

            // Add user information into json object
            data.put(PHONE_NUMBER, _phoneNumber);
            data.put(PASSWORD, _passwordHash);

            // Return json object in string format
            return data.toString();
        } catch ( JSONException exception ) {
            throw new FailedToImportIntoDataException(exception);
        }
    }
}
