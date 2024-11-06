package com.example.tastytracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

//Class to Create adapter to access User Information Database
public class userInfoDBAdapter {
    static final String KEY_ROWID = "_id";
    static final String KEY_HOUSEHOLD_ID = "householdID";
    static final String KEY_USERNAME = "username";
    static final String KEY_PASSWORD = "password";
    static final String DATABASE_NAME = "UserInfo";
    static final String DATABASE_TABLE = "users";
    static final int DATABASE_VERSION = 2;

    static final String DATABASE_CREATE =
            "create table users (_id integer primary key autoincrement, "
                    + "householdID integer, username text not null, password text not null);";

    final Context context;
    DatabaseHelper DBHelper;
    SQLiteDatabase db;

    //Constructor
    public userInfoDBAdapter(Context ctx) {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        //Constructor
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL(DATABASE_CREATE); //try to create the db
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        //
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            if (oldVersion < DATABASE_VERSION) {
                db.execSQL("ALTER TABLE " + DATABASE_TABLE + " ADD COLUMN " + KEY_HOUSEHOLD_ID + " integer;");
            }
        }
    }

    public userInfoDBAdapter open() throws SQLException {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        DBHelper.close();
    }

    public long insertUser(int householdID, String username, String password) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_HOUSEHOLD_ID, householdID);
        initialValues.put(KEY_USERNAME, username);
        initialValues.put(KEY_PASSWORD, password);
        return db.insert(DATABASE_TABLE, null, initialValues);
    }

    //Creates a cursor to help index through all current registered users
    public Cursor getAllUsers() {
        return db.query(DATABASE_TABLE, new String[]{KEY_ROWID, KEY_HOUSEHOLD_ID, KEY_USERNAME, KEY_PASSWORD},
                null, null, null, null, null);
    }

    //Delete user, currently no application in this version
    public boolean deleteUser(long rowId) {
        return db.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    /*
     * Index through all users until a matching username is found, if found create a user object and return
     * If no matching username, then return null.
     * Functions also as a method to check if username exists.
     */
    public User getUser(String username){
        String password = null;
        int householdID = 0;

        String[] columns = {KEY_USERNAME, KEY_PASSWORD, KEY_HOUSEHOLD_ID};
        String selection = KEY_USERNAME + "=?";
        String[] selectionArgs = {username};

        Cursor cursor = db.query(DATABASE_TABLE, columns, selection, selectionArgs, null, null, null);

        if (cursor != null && cursor.moveToFirst()){
            int colIndexPassword = cursor.getColumnIndex(KEY_PASSWORD);
            int colIndexHouseholdID = cursor.getColumnIndex(KEY_HOUSEHOLD_ID);
            password = cursor.getString(colIndexPassword);
            householdID = cursor.getInt(colIndexHouseholdID);
            cursor.close();
            return new User(username, password, householdID); //return information on user with matched username
        }

        if (cursor != null) {
            cursor.close();
        }

        return null; //No matching username found, return null
    }

    //Method to update user, no current use in this version
    public boolean updateUser(long rowId, String username, String password, int householdID) {
        ContentValues args = new ContentValues();
        args.put(KEY_USERNAME, username);
        args.put(KEY_PASSWORD, password);
        args.put(KEY_HOUSEHOLD_ID, householdID);
        return db.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }

    //Method to clear all users from db, no current use in this version
    public void clearAllUsers() {
        db.delete(DATABASE_TABLE, null, null);
    }
}


