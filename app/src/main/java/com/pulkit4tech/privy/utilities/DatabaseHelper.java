package com.pulkit4tech.privy.utilities;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "mydb";
    public static final String TABLE_NAME = "privy";
    private static final int DATABASE_VERSION = 2;
    private static final String CREATE_DB_TABLE = " CREATE TABLE " + TABLE_NAME
            + " (id TEXT PRIMARY KEY, "
            + " name TEXT NOT NULL,"
            + " lat REAL NOT NULL,"
            + " lng REAL NOT NULL,"
            + " rating REAL DEFAULT 0.0,"
            + " vicinity TEXT DEFAULT \"NA\");";

    DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_DB_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
