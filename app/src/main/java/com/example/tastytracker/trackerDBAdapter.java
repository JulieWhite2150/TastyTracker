package com.example.tastytracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class trackerDBAdapter {
    static final String KEY_ROWID = "_id";
    static final String KEY_ITEM = "item";
    static final String KEY_QUANTITY = "quantity";
    static final String KEY_UNIT = "unit";
    static final String TAG = "TrackerDBAdapter";
    static final int DATABASE_VERSION = 1;
    final Context context;
    DatabaseHelper DBHelper;
    SQLiteDatabase db;

    public trackerDBAdapter(Context ctx) {
        this.context = ctx;
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        int householdID;

        DatabaseHelper(Context context, int householdID) {
            super(context, "household_" + householdID + "_database", null, DATABASE_VERSION);
            this.householdID = householdID;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String createCurrentItemsTableQuery = "CREATE TABLE IF NOT EXISTS household_" + householdID + "_currentItems ("
                    + KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + KEY_ITEM + " TEXT, "
                    + KEY_QUANTITY + " INTEGER,"
                    + KEY_UNIT + "TEXT);";

            String createShoppingListTableQuery = "CREATE TABLE IF NOT EXISTS household_" + householdID + "_shoppingList ("
                    + KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + KEY_ITEM + " TEXT);";

            try {
                db.execSQL(createCurrentItemsTableQuery);
                db.execSQL(createShoppingListTableQuery);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS household_" + householdID + "_currentItems");
            db.execSQL("DROP TABLE IF EXISTS household_" + householdID + "_shoppingList");
            onCreate(db);
        }
    }

    public trackerDBAdapter open(int householdID) throws SQLException {
        DBHelper = new DatabaseHelper(context, householdID);
        db = DBHelper.getReadableDatabase();
        return this;
    }

    public void close() {
        DBHelper.close();
    }

    public long insertCurrentItem(int householdID, String item, int quantity, String unit) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_ITEM, item);
        initialValues.put(KEY_QUANTITY, quantity);
        initialValues.put(KEY_UNIT, unit);
        return db.insert("household_" + householdID + "_currentItems", null, initialValues);
    }

    public long insertShoppingListItem(int householdID, String item) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_ITEM, item);
        return db.insert("household_" + householdID + "_shoppingList", null, initialValues);
    }

    public Cursor getShoppingListItems(int householdID) {
        if (db == null || !db.isOpen()) {
            return null;
        }

        String tableName = "household_" + householdID + "_shoppingList";
        String[] columns = new String[]{KEY_ITEM};
        return db.query(tableName, columns, null, null, null, null, null);
    }

    public ArrayList<foodItem> getAllItems(int householdID) {
        ArrayList<foodItem> itemList = new ArrayList<>();

        if (db == null || !db.isOpen()) {
            return itemList;
        }

        String tableName = "household_" + householdID + "_currentItems";
        String[] columns = new String[]{KEY_ITEM, KEY_QUANTITY};
        Cursor cursor = db.query(tableName, columns, null, null, null, null, null);

        if (cursor != null) {
            int colIndexItem = cursor.getColumnIndex(KEY_ITEM);
            int colIndexQuant = cursor.getColumnIndex(KEY_QUANTITY);
            int colIndexUnit = cursor.getColumnIndex(KEY_UNIT);

            while (cursor.moveToNext()) {
                String itemName = cursor.getString(colIndexItem);
                int itemQuantity = cursor.getInt(colIndexQuant);
                String itemUnit = cursor.getString(colIndexUnit);
                itemList.add(new foodItem(itemName, itemQuantity, itemUnit));
            }
            cursor.close();
        }

        return itemList;
    }

    public boolean itemExists(int householdID, String itemName) {
        String tableName = "household_" + householdID + "_currentItems";
        Cursor cursor = db.query(tableName, new String[]{KEY_ITEM}, KEY_ITEM + "=?", new String[]{itemName}, null, null, null);
        boolean exists = cursor != null && cursor.getCount() > 0;
        if (cursor != null) {
            cursor.close();
        }
        return exists;
    }

    public void updateCurrentItem(int householdID, String itemName, int itemQuantity, String itemUnit) {
        ContentValues values = new ContentValues();
        values.put(KEY_QUANTITY, itemQuantity);
        values.put(KEY_UNIT, itemUnit);
        String tableName = "household_" + householdID + "_currentItems";
        db.update(tableName, values, KEY_ITEM + "=?", new String[]{itemName});
    }

    public void printCurrentItems(int householdID) {
        String tableName = "household_" + householdID + "_currentItems";
        Cursor cursor = db.rawQuery("SELECT * FROM " + tableName, null);

        try {
            if (cursor.moveToFirst()) {
                int colIndexItem = cursor.getColumnIndex(KEY_ITEM);
                int colIndexQuant = cursor.getColumnIndex(KEY_QUANTITY);
                int colIndexUnit = cursor.getColumnIndex(KEY_UNIT);
                do {
                    String itemName = cursor.getString(colIndexItem);
                    int quantity = cursor.getInt(colIndexQuant);
                    String unit = cursor.getString(colIndexUnit);
                    Log.d(TAG, "Item: " + itemName + ", Quantity: " + quantity + ", Unit: " + unit);
                } while (cursor.moveToNext());
            }
        } finally {
            cursor.close();
        }
    }
}

