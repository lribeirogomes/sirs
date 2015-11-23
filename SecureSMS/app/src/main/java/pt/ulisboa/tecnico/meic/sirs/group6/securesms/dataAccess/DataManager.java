package pt.ulisboa.tecnico.meic.sirs.group6.securesms.dataAccess;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.meic.sirs.group6.securesms.dataAccess.exceptions.FailedToCreateDataBaseException;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.dataAccess.exceptions.FailedToLoadDataBaseException;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.dataAccess.exceptions.FailedToStoreSMSException;
import pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain.SMSMessage;

/**
 * Created by lribeirogomes on 23/11/15.
 */
public class DataManager {
    private static final String DATABASE_NAME = "securesms",
                                SMS_TABLE_VIEW = "smsview",
                                SMS_TABLE_NAME = "sms",
                                TIME = "Time",
                                ADDR = "DestinationAddress",
                                CONTENT = "Content";
    private static DataBase _dataBase = null;

    private static DataManager ourInstance = new DataManager();

    public static DataManager getInstance(Context context) throws FailedToCreateDataBaseException {
        if(_dataBase != null){
            throw new FailedToCreateDataBaseException();
        }
        _dataBase = new DataBase(context);
        return ourInstance;
    }

    public static DataManager getInstance() throws FailedToLoadDataBaseException {
        if(_dataBase == null){
            throw new FailedToLoadDataBaseException();
        }
        return ourInstance;
    }

    private DataManager() {

    }

    public List<SMSMessage> getMessages(String destinationAddress)  throws FailedToStoreSMSException {
        List<SMSMessage> list = new ArrayList<>();

        try {
            SQLiteDatabase dataBase = _dataBase.getReadableDatabase();
            Cursor cursor = dataBase.query(
                    SMS_TABLE_VIEW,
                    new String[] {TIME, CONTENT},
                    ADDR + "=" + destinationAddress,
                    new String[] {ADDR},
                    null,
                    null,
                    null);

            cursor.moveToFirst();
            while(!cursor.isAfterLast()) {

                list.add(null);
            }

            return list;
        } catch (SQLException exception) {
            throw new FailedToStoreSMSException(exception);
        }
    }

    public void storeSMS(int timestamp, String destinationAddress, byte[] content) throws FailedToStoreSMSException {
        try {
            ContentValues values = new ContentValues();
            values.put(TIME, timestamp);
            values.put(ADDR, destinationAddress);
            values.put(CONTENT, content);

            SQLiteDatabase dataBase = _dataBase.getWritableDatabase();
            dataBase.insertOrThrow(SMS_TABLE_NAME, TIME, values);
        } catch (SQLException exception) {
            throw new FailedToStoreSMSException(exception);
        }
    }

    public void deleteSMS(int timestamp)
    {
        SQLiteDatabase db = _dataBase.getWritableDatabase();
        db.delete(SMS_TABLE_NAME,TIME+"=?", new String [] {String.valueOf(timestamp)});
        db.close();
    }
}
