package pt.ulisboa.tecnico.meic.sirs.group6.securesms.dataAccess;

import pt.ulisboa.tecnico.meic.sirs.group6.securesms.dataAccess.exceptions.FailedToStoreSMSException;

/**
 * Created by lribeirogomes on 17/11/15.
 */
public class StoreSMSService {
    private int _date;
    private String _destinationAddress;
    private byte [] _encrypted;

    public StoreSMSService(int date, String destinationAddress, byte[] encrypted) {
        _date = date;
        _destinationAddress = destinationAddress;
        _encrypted = encrypted;
    }

    public void Execute () throws FailedToStoreSMSException {
        String DB_FULL_PATH = "securesms";
        String TABLE_SMS= "SMS";

        try {
            /*ContentValues values=new ContentValues();
            values.put("Time",_date);
            values.put("DestinationAddress",_destinationAddress);
            values.put("Data",_encrypted);

            SQLiteDatabase dataBase = SQLiteDatabase.openOrCreateDatabase(DB_FULL_PATH, null);
            dataBase.execSQL("" +
                    "IF (EXISTS (SELECT * " +
                    "    FROM sys.databases" +
                    "    WHERE name = '" + TABLE_SMS + "'))" +
                    "BEGIN" +
                    "    CREATE TABLE " + TABLE_SMS + "(Time int, DestinationAddress VARCHAR(20), Data VARCHAR(20))" +
                    "END");
            dataBase.insertOrThrow("Data", null, values);*/
        } catch (Exception exception) {//SQLException exception) {
            throw new FailedToStoreSMSException(exception);
        }
    }
}
