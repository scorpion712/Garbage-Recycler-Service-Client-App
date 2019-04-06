package com.example.lauti.finalintromoviles.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.lauti.finalintromoviles.model.UserContract;

public class UsersDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "users.db";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + UserContract.UserEntry.TABLE_NAME;

    public UsersDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + UserContract.UserEntry.TABLE_NAME + " ("
                + UserContract.UserEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + UserContract.UserEntry.FIRSTNAME + " TEXT NOT NULL,"
                + UserContract.UserEntry.LASTNAME + " TEXT NOT NULL,"
                + UserContract.UserEntry.USERNAME + " TEXT NOT NULL,"
                + UserContract.UserEntry.ADDRESS + " TEXT ,"
                + UserContract.UserEntry.EMAIL + " TEXT ,"
                + "UNIQUE (" + UserContract.UserEntry.USERNAME + "))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public int getEntriesCount(){
        Cursor c = this.getReadableDatabase().query(UserContract.UserEntry.TABLE_NAME,null,null,null,null,null,null);
        int count = 0;
        while (c.moveToNext())
            count += 1;
        c.close();
        return count;
    }

}