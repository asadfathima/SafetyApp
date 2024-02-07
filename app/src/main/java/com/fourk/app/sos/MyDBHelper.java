package com.fourk.app.sos;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 4;
    // Database Name
    private static final String DATABASE_NAME = "crud.db";

    public MyDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_CONTACT = "CREATE TABLE IF NOT EXISTS " + Contact.TABLE + "("
                + Contact.KEY_name + " TEXT PRIMARY KEY ,"
                + Contact.KEY_phone + " TEXT )";
        db.execSQL(CREATE_TABLE_CONTACT);

        String CREATE_TABLE_LOCATION = "CREATE TABLE IF NOT EXISTS " + LocationData.TABLE + "("
                + LocationData.KEY_lat + " TEXT ,"
                + LocationData.KEY_lon + " TEXT )";
        db.execSQL(CREATE_TABLE_LOCATION);

        String CREATE_TABLE_EMAIL = "CREATE TABLE IF NOT EXISTS " + Email.TABLE + "("
                + Email.KEY_name + " TEXT ,"
                + Email.KEY_id + " TEXT )";
        db.execSQL(CREATE_TABLE_EMAIL);

        String CREATE_TABLE_USER = "CREATE TABLE IF NOT EXISTS " + User.TABLE + "("
                + User.KEY_id + " TEXT ,"
                + User.KEY_pass + " TEXT )";
        db.execSQL(CREATE_TABLE_USER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
