package pt.ulisboa.tecnico.meic.sirs.group6.securesms.dataAccess;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by lribeirogomes on 23/11/15.
 */
public class DataBase extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "securesms",
                                CONTACTS_TABLE_VIEW = "smsview",
                                CONTACTS_TABLE_NAME = "sms",
                                PHONE_NUMBER = "PhoneNumber",
                                SMS_TABLE_VIEW = "smsview",
                                SMS_TABLE_NAME = "sms",
                                TIME = "Time",
                                ADDR = "DestinationAddress",
                                CONTENT = "Content";

    public DataBase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + SMS_TABLE_NAME + " (" +
                TIME + " INTEGER PRIMARY KEY, " +
                ADDR + " TEXT, " +
                CONTENT + " BLOB);");
        db.execSQL("CREATE TABLE " + SMS_TABLE_NAME + " (" +
                TIME + " INTEGER PRIMARY KEY, " +
                ADDR + " TEXT, " +
                CONTENT + " BLOB);");
        db.execSQL("CREATE VIEW " + SMS_TABLE_VIEW +
                        " AS SELECT " + SMS_TABLE_NAME + "." + TIME + " AS _id," +
                        " " + SMS_TABLE_NAME + "." + TIME + "," +
                        " " + SMS_TABLE_NAME + "." + ADDR + "," +
                        " " + SMS_TABLE_NAME + "." + CONTENT + "" +
                        " FROM " + SMS_TABLE_NAME
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            // Enable foreign key constraints
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }
}