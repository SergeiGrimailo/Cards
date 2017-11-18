package com.gmail.sgrimailo.utils.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

/**
 * Created by user on 18.11.2017.
 */

public class DataBaseFacade {

    private SQLiteDatabase db;

    public DataBaseFacade(SQLiteDatabase db) {
        this.db = db;
    }

    public Cursor getItemById(String tableName, String[] columns, Long itemId) {
        return DataBaseHelper.getItemById(db, tableName, columns, itemId);
    }

    public Map<String, Object> getItemByIdMap(
            String tableName, String[] columns, Long itemId) {
        return DataBaseHelper.getItemByIdMap(db, tableName, columns, itemId);
    }

    public String getItemColumnValue(String tableName, String columnName, Long itemId) {
        return DataBaseHelper.getItemColumnValue(db, tableName, columnName, itemId);
    }

    public void deleteItemById(String tableName, Long itemId) {
        DataBaseHelper.deleteItemById(db, tableName, itemId);
    }

    public void deleteItemsByFieldValue(
            String tableName, String fieldName, Object fieldValue) {
        DataBaseHelper.deleteItemsByFieldValue(db, tableName, fieldName, fieldValue);
    }

    public Cursor getItems(String tableName, String[] columns, String selection, String[] arguments) {
        return db.query(tableName, columns, selection, arguments, null, null, null);
    }
}
