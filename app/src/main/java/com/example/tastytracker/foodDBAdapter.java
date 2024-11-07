package com.example.tastytracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class foodDBAdapter {
    static final String TAG = "DBAdapter";
    static final String KEY_ROWID = "_id";
    static final String KEY_ITEM = "item";
    static final String KEY_QUANTITY = "quantity";
    static final String KEY_UNIT = "unit";
    static final String KEY_SHOPPED = "shopped";
    static final int DATABASE_VERSION = 2; // Increment version to trigger onUpgrade

    private final Context context;
    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;
    private int householdID;

    public foodDBAdapter(Context ctx) {
        this.context = ctx;
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        private final int householdID;

        DatabaseHelper(Context context, int householdID) {
            super(context, "HouseholdDB_" + householdID, null, DATABASE_VERSION);
            this.householdID = householdID;
        }

        //Create the inventory and shopping list table if they don't already exist
        @Override
        public void onCreate(SQLiteDatabase db) {
            String createInventoryTableQuery = "CREATE TABLE IF NOT EXISTS household_" + householdID + "_inventoryItems ("
                    + KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + KEY_ITEM + " TEXT, "
                    + KEY_QUANTITY + " DOUBLE, "
                    + KEY_UNIT + " TEXT);";

            String createShoppingListTableQuery = "CREATE TABLE IF NOT EXISTS household_" + householdID + "_shoppingList ("
                    + KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + KEY_ITEM + " TEXT, "
                    + KEY_QUANTITY + " DOUBLE, "
                    + KEY_UNIT + " TEXT,"
                    + KEY_SHOPPED + " TINYINT);";

            try {
                db.execSQL(createInventoryTableQuery);
                db.execSQL(createShoppingListTableQuery);
            } catch (SQLException e) {
                e.printStackTrace(); //Print stack trace if there is an error creating the tables
            }
        }

        //If the db has been upgraded since access drop all existing tables in the household
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");

            db.execSQL("DROP TABLE IF EXISTS household_" + householdID + "_inventoryItems");
            db.execSQL("DROP TABLE IF EXISTS household_" + householdID + "_shoppingList");
            onCreate(db);
        }
    }

    //Create a dbAdapter to allow the classes to access the db's
    public foodDBAdapter open(int householdID) throws SQLException {
        DBHelper = new DatabaseHelper(context, householdID);
        db = DBHelper.getWritableDatabase();
        return this;
    }

    //Close the db
    public void close() {
        DBHelper.close();
    }

    /*
     *Method to insert a new item into either the inventory or shopping list table.
     *The method determines which table to insert into based on the value of the String location.
     *The boolean shopped is not necessary for an inventory item but is a parameter to allow for one
     *method to cover all insertion cases, given this all inventory insertions pass "false" for shopped.
     */
    public long insertItem(int householdID, String item, double quantity, String unit, boolean shopped, String location) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_ITEM, item); //Item name, common to both inventory/shopping
        initialValues.put(KEY_QUANTITY, quantity); //Item quantity, common to both inventory/shopping
        initialValues.put(KEY_UNIT, unit); //Item unit, common to both inventory/shopping

        //If the item is being added to the inventory table
        if (location.equals("INVENTORY")){
            return db.insert("household_" + householdID + "_inventoryItems", null, initialValues);
        }

        //If the item is being added to the shopping list table
        else{
            int intShopped; //Shopped is stored as a tiny int in the db, so convert boolean to int
            if (shopped){
                intShopped = 1;
            }
            else{
                intShopped = 0;
            }

            initialValues.put(KEY_SHOPPED, intShopped);
            return db.insert("household_" + householdID + "_shoppingList", null, initialValues);
        }
    }


    /*
     *Method to edit an existing item in either the inventory or shopping list table.
     *The method determines which table to edit based on the value of the String location.
     *The boolean shopped is not necessary here as if the item is being edited on the shopping
     * list then the item has not been shopped.
     */
    public void updateItem(int householdID, String itemName, double itemQuantity, String itemUnit, String location) {
        ContentValues values = new ContentValues();
        values.put(KEY_QUANTITY, itemQuantity);
        values.put(KEY_UNIT, itemUnit);
        String tableName = "household_" + householdID;
        tableName = (location.equals("INVENTORY") ? tableName+"_inventoryItems" : tableName+"_shoppingList");

        db.update(tableName, values, KEY_ITEM + "=?", new String[]{itemName});
    }


    /*
     * Method to determine the quantity of an item in a table.
     * If the method returns 0.0, then the caller will know that
     * the item does not exist in the table, thus they should insert an item rather than edit.
     */
    public double getItemQuant(int householdID, String itemName, String location) {
        String tableName = "household_" + householdID;
        tableName = (location.equals("INVENTORY") ? tableName+"_inventoryItems" : tableName+"_shoppingList");
        String[] columns = {KEY_ROWID, KEY_ITEM, KEY_QUANTITY, KEY_UNIT};
        String selection = KEY_ITEM + "=?";
        String[] selectionArgs = {itemName};

        Cursor cursor = db.query(tableName, columns, selection, selectionArgs, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            int colIndexQuant = cursor.getColumnIndex(KEY_QUANTITY);

            double quant = cursor.getDouble(colIndexQuant);

            cursor.close();
            return quant;
        }

        if (cursor != null) {
            cursor.close();
        }

        return 0.0; //item is not in table
    }


    /*
     * This method is used exclusively by the Checkboxes in the Shopping List Adapter class.
     * This method is used to flip (0,1) the value of shopped when the user clicks on the checkbox.
     * Given that the value of "shopped" for all shopping list items is false on creation,
     * this method keeps track of the user checking (and unchecking) the checkbox of an item by
     * updating the db.
     */
    public void flipShopped(int householdID, String itemName){
        String tableName = "household_" + householdID + "_shoppingList";
        String[] columns = {KEY_SHOPPED};
        String selection = KEY_ITEM + "=?";
        String[] selectionArgs = {itemName};

        Cursor cursor = db.query(tableName, columns, selection, selectionArgs, null, null, null);
        if (cursor != null && cursor.moveToFirst()){
            int columnIndex = cursor.getColumnIndex(KEY_SHOPPED);
            int currentShopped = cursor.getInt(columnIndex);
            int newShopped = (currentShopped == 1) ? 0 : 1; //if 1 then make 0, if 0 make 1

            ContentValues values = new ContentValues();
            values.put(KEY_SHOPPED, newShopped);

            db.update(tableName, values, selection, selectionArgs);
        }

        if (cursor != null){
            cursor.close();
        }
    }

    /*
     * This method is used to delete an item from it's table.
     * It is currently used when a user marks an item as shopped, then the item should be
     * removed from the shopping list-- this method does that.
     * It is currently "generic" to either shopping list/inventory as I think I will use this
     * method again in other contexts.
     */
    public int deleteItem(int householdID, String itemName, String location) {
        String tableName = "household_" + householdID;
        tableName = (location.equals("INVENTORY") ? tableName+"_inventoryItems" : tableName+"_shoppingList");
        String whereClause = KEY_ITEM + "=?";
        String[] whereArgs = {itemName};

        return db.delete(tableName, whereClause, whereArgs);
    }

    /*
     * Method to get an ArrayList of every row of the shopping list table in the db as shoppingListItems.
     * This method is used by the shopping list adapter to properly display the list.
     */
    public ArrayList<shoppingListItem> getShoppingListItems(int householdID) {
        ArrayList<shoppingListItem> list = new ArrayList<>();

        if (db == null || !db.isOpen()) {
            return list;
        }

        String tableName = "household_" + householdID + "_shoppingList";
        String[] columns = new String[]{KEY_ITEM, KEY_QUANTITY, KEY_UNIT};
        Cursor cursor = db.query(tableName, columns, null, null, null, null, null);

        if (cursor != null) {
            int colIndexItem = cursor.getColumnIndex(KEY_ITEM);
            int colIndexQuant = cursor.getColumnIndex(KEY_QUANTITY);
            int colIndexUnit = cursor.getColumnIndex(KEY_UNIT);

            while (cursor.moveToNext()) {
                String itemName = cursor.getString(colIndexItem);
                double itemQuantity = cursor.getDouble(colIndexQuant);
                String itemUnit = cursor.getString(colIndexUnit);
                list.add(new shoppingListItem(itemName, itemQuantity, itemUnit, false));
            }
            cursor.close();
        }

        return list;
    }


    /*
     * Method to get an ArrayList of every row of the inventory table in the db as foodItems.
     * This method is used by the inventory list adapter to properly display the list.
     */
    public ArrayList<foodItem> getInventoryItems(int householdID) {
        ArrayList<foodItem> list = new ArrayList<>();

        if (db == null || !db.isOpen()) {
            return list;
        }

        String tableName = "household_" + householdID + "_inventoryItems";
        String[] columns = new String[]{KEY_ITEM, KEY_QUANTITY, KEY_UNIT};
        Cursor cursor = db.query(tableName, columns, null, null, null, null, null);

        if (cursor != null) {
            int colIndexItem = cursor.getColumnIndex(KEY_ITEM);
            int colIndexQuant = cursor.getColumnIndex(KEY_QUANTITY);
            int colIndexUnit = cursor.getColumnIndex(KEY_UNIT);

            while (cursor.moveToNext()) {
                String itemName = cursor.getString(colIndexItem);
                double itemQuantity = cursor.getDouble(colIndexQuant);
                String itemUnit = cursor.getString(colIndexUnit);
                list.add(new foodItem(itemName, itemQuantity, itemUnit));
            }
            cursor.close();
        }

        return list;
    }


    /*
     * Method to get an ArrayList of shoppingListItems that have values of Shopped = 1 (true) in the db.
     * This method is used by the Mark as Shopped button in the shopping activity to figure out
     * which items need to be added to the inventory and then deleted from the shopping list table.
     */
    public ArrayList<shoppingListItem> getShoppedItems(int householdID) {
        ArrayList<shoppingListItem> itemList = new ArrayList<>();

        if (db == null || !db.isOpen()) {
            return itemList;
        }

        String tableName = "household_" + householdID + "_shoppingList";
        String[] columns = new String[]{KEY_ITEM, KEY_QUANTITY, KEY_UNIT, KEY_SHOPPED};
        Cursor cursor = db.query(tableName, columns, null, null, null, null, null);

        if (cursor != null) {
            int colIndexItem = cursor.getColumnIndex(KEY_ITEM);
            int colIndexQuant = cursor.getColumnIndex(KEY_QUANTITY);
            int colIndexUnit = cursor.getColumnIndex(KEY_UNIT);
            int colIndexShop = cursor.getColumnIndex(KEY_SHOPPED);

            while (cursor.moveToNext()) {
                String itemName = cursor.getString(colIndexItem);
                double itemQuantity = cursor.getDouble(colIndexQuant);
                String itemUnit = cursor.getString(colIndexUnit);
                boolean itemShop = cursor.getInt(colIndexShop) == 1;
                if (itemShop) {
                    itemList.add(new shoppingListItem(itemName, itemQuantity, itemUnit, itemShop));
                }
            }
            cursor.close();
        }
        return itemList;
    }

}

