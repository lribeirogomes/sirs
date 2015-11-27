package pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess;

import java.io.InvalidClassException;
import java.util.IllegalFormatException;
import java.util.InvalidPropertiesFormatException;
import java.util.LinkedHashSet;
import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences.Editor;

import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToAddAttributeException;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToCreateDataBaseException;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToGetAttributeException;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToLoadDataBaseException;
import pt.ulisboa.tecnico.meic.sirs.securesms.dataAccess.exceptions.FailedToRemoveAttributeException;

public class DataManager {

    private Context _context;
    private static DataManager dataManager;

    public static void createDataManager(Context context) throws FailedToCreateDataBaseException {
        if (dataManager != null) {
            throw new FailedToCreateDataBaseException();
        }
        dataManager = new DataManager();
        dataManager._context = context;
    }

    public static DataManager getInstance() throws FailedToLoadDataBaseException {
        if (dataManager == null) {
            throw new FailedToLoadDataBaseException();
        }
        return dataManager;
    }

    private DataManager() {

    }

    private Editor getEditor(String sharedPreferencesName) {
        return _context.getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE).edit();
    }

    public String getAttributeString(String className, String attributeName) throws
            FailedToGetAttributeException {
        try {
            String attribute = _context.getSharedPreferences(className, Context.MODE_PRIVATE).
                    getString(attributeName, "");

            return attribute;
        } catch ( ClassCastException exception) {
            throw new FailedToGetAttributeException(exception);
        }
    }

    public long getAttributeLong(String className, String attributeName) throws
            FailedToGetAttributeException {
        try {
            long attribute = _context.getSharedPreferences(className, Context.MODE_PRIVATE).
                    getLong(attributeName, -1);

            return attribute;
        } catch ( ClassCastException exception) {
            throw new FailedToGetAttributeException(exception);
        }
    }

    public Set<String> getAttributeSet(String className, String attributeName) throws
            FailedToGetAttributeException {
        try {
            return _context.getSharedPreferences(className, Context.MODE_PRIVATE).
                    getStringSet(attributeName, new LinkedHashSet<String>());
        } catch (ClassCastException exception) {
            throw new FailedToGetAttributeException(exception);
        }
    }

    public void setAttribute(String className, String attributeName, String value) {
            Editor editor = getEditor(className);

            editor.putString(attributeName, value);
            editor.commit();
    }

    public void setAttribute(String className, String attributeName, long value) {
            Editor editor = getEditor(className);

            editor.putLong(attributeName, value);
            editor.commit();
    }

    public void addAttribute(String className, String attributeName, String value) throws
            FailedToAddAttributeException {
        try {
            Editor editor = getEditor(className);

            Set<String> dataSet = getAttributeSet(className, attributeName);
            dataSet.add(value);

            editor.putStringSet(attributeName, dataSet);
            editor.commit();
        } catch (FailedToGetAttributeException exception) {
            throw new FailedToAddAttributeException(exception);
        }
    }

    public void removeAttribute(String className, String attributeName, String value) throws
            FailedToRemoveAttributeException {
        try {
            Editor editor = getEditor(className);

            Set<String> dataSet = getAttributeSet(className, attributeName);
            dataSet.remove(value);

            editor.putStringSet(attributeName, dataSet);
            editor.commit();
        } catch (FailedToGetAttributeException exception) {
            throw new FailedToRemoveAttributeException(exception);
        }
    }

    public void cleanAttribute(String className) {
        Editor editor = getEditor(className);

        editor.clear();
        editor.commit();
    }
}