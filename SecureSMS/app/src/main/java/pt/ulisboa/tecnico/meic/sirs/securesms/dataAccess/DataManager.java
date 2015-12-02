package pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess;

import java.util.LinkedHashSet;
import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToAddAttributeException;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToCreateDataBaseException;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToGetAttributeException;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToLoadDataBaseException;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToRemoveAttributeException;

public class DataManager {

    private static DataManager dataManager;
    private Context _context;
    private String _userId;

    public static final String DATABASE = "Database";
    public static final String USER_TABLE = "Users";
    public static final String USER = "";
    //user file and attributes
    private static final String USER_CLASS = "User";
    public static final String USER_ID = "UserId";
    public static final String PASSWORD_HASH = "PasswordHash";
    public static final String CONTACT_TABLE = "Contacts";
    public static final String CONTACT_COUNT = "ContactCount";

    //contact file and attributes
    public static final String CONTACT_CLASS = "Contact";
    public static final String CONTACT_NAME = "Name";
    public static final String CONTACT_PHONE_NUMBER = "PhoneNumber";
    public static final String MESSAGE_TABLE = "Messages";
    public static final String MESSAGE_COUNT = "MessagesCount";

    //message file and attributes
    public static final String MESSAGE_CLASS = "Message";
    public static final String MESSAGE_DIRECTION = "MessageDirection";
    public static final String MESSAGE_DATE_NUMBER = "MessageDate";
    public static final String MESSAGE_CONTENT = "MessageContent";

    //session file and attributes
    public static final String SESSION_CLASS = "Session";


    private DataManager() {}

    public static DataManager createDataManager(Context context) throws FailedToCreateDataBaseException {
        if (dataManager == null) {
            dataManager = new DataManager();
        }
        dataManager = new DataManager();
        dataManager._context = context;
        return dataManager;
    }

    public static DataManager getInstance() throws FailedToLoadDataBaseException {
        if (dataManager == null) {
            throw new FailedToLoadDataBaseException();
        }
        return dataManager;
    }

    public static void setCurrentUser(String userId) {
        dataManager._userId = userId;
    }

    public static Context getContext() {
        return dataManager._context;
    }

    private Editor getEditor(String spFilename) {
        spFilename = USER_CLASS+_userId+spFilename;
        return _context.getSharedPreferences(spFilename, Context.MODE_PRIVATE).edit();
    }

    public String getAttributeString(String spFilename, String attributeName) throws
            FailedToGetAttributeException {
        try {
            spFilename = USER_CLASS+_userId+spFilename;
            String attribute = _context.getSharedPreferences(spFilename, Context.MODE_PRIVATE).
                    getString(attributeName, "");

            return attribute;
        } catch ( ClassCastException exception) {
            throw new FailedToGetAttributeException(exception);
        }
    }

    public long getAttributeLong(String spFilename, String attributeName) throws
            FailedToGetAttributeException {
        try {
            spFilename = USER_CLASS+_userId+spFilename;
            long attribute = _context.getSharedPreferences(spFilename, Context.MODE_PRIVATE).
                    getLong(attributeName, -1);

            return attribute;
        } catch ( ClassCastException exception) {
            throw new FailedToGetAttributeException(exception);
        }
    }

    public int getAttributeInt(String spFilename, String attributeName) throws
            FailedToGetAttributeException {
        try {
            spFilename = USER_CLASS+_userId+spFilename;
            int attribute = _context.getSharedPreferences(spFilename, Context.MODE_PRIVATE).
                    getInt(attributeName, -1);

            return attribute;
        } catch ( ClassCastException exception) {
            throw new FailedToGetAttributeException(exception);
        }
    }

    public Set<String> getAttributeSet(String spFilename, String attributeName) throws
            FailedToGetAttributeException {
        try {
            spFilename = USER_CLASS+_userId+spFilename;
            return _context.getSharedPreferences(spFilename, Context.MODE_PRIVATE).
                    getStringSet(attributeName, new LinkedHashSet<String>());
        } catch (ClassCastException exception) {
            throw new FailedToGetAttributeException(exception);
        }
    }

    public void setAttribute(String spFilename, String attributeName, String value) {
        Editor editor = getEditor(spFilename);

        editor.putString(attributeName, value);
        editor.commit();
    }

    public void setAttribute(String spFilename, String attributeName, long value) {
        Editor editor = getEditor(spFilename);

        editor.putLong(attributeName, value);
        editor.commit();
    }

    public void setAttribute(String spFilename, String attributeName, int value) {
        Editor editor = getEditor(spFilename);

        editor.putInt(attributeName, value);
        editor.commit();
    }

    public void addAttribute(String spFilename, String attributeName, String value) throws
            FailedToAddAttributeException {
        try {
            Editor editor = getEditor(spFilename);

            Set<String> dataSet = getAttributeSet(spFilename, attributeName);
            dataSet.add(value);

            editor.putStringSet(attributeName, dataSet);
            editor.commit();
        } catch (FailedToGetAttributeException exception) {
            throw new FailedToAddAttributeException(exception);
        }
    }

    public void removeAttribute(String spFilename, String attributeName, String value) throws
            FailedToRemoveAttributeException {
        try {
            Editor editor = getEditor(spFilename);

            Set<String> dataSet = getAttributeSet(spFilename, attributeName);
            dataSet.remove(value);

            editor.putStringSet(attributeName, dataSet);
            editor.commit();
        } catch (FailedToGetAttributeException exception) {
            throw new FailedToRemoveAttributeException(exception);
        }
    }

    public void cleanAttribute(String spFilename) {
        Editor editor = getEditor(spFilename);

        editor.clear();
        editor.commit();
    }
}