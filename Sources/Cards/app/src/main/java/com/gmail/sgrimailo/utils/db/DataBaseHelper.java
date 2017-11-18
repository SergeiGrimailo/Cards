package com.gmail.sgrimailo.utils.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

import com.gmail.sgrimailo.cards.BuildConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sergey on 6/16/2017.
 */

public class DataBaseHelper {

    private static final String LOG_TAG = String.format("%s.%s",
            DataBaseHelper.class.getSimpleName(), "LOG_TAG");

    public static Cursor getItemById(SQLiteDatabase db, String tableName, String[] columns, Long itemID) {
        String selection = String.format("%s = ?", BaseColumns._ID);
        Cursor cursor = db.query(tableName, columns, selection,
                new String[]{itemID.toString()}, null, null, null);
        return cursor;
    }

    public static Map<String, Object> getItemByIdMap(SQLiteDatabase db, String tableName,
                                                     String[] columns, Long itemId) {
        String[] fetchedColumns = columns;
        Cursor cursor = getItemById(db, tableName, columns, itemId);
        if (cursor.moveToFirst()) {
            Map<String, Object> result = new HashMap<>();
            if (fetchedColumns == null) {
                fetchedColumns = cursor.getColumnNames();
            }
            for (int i = 0; i < fetchedColumns.length; i++) {
                int columnIndex = cursor.getColumnIndex(fetchedColumns[i]);
                int columnType = cursor.getType(columnIndex);
                switch (columnType) {
                    case Cursor.FIELD_TYPE_STRING:
                        result.put(fetchedColumns[i], cursor.getString(columnIndex));
                        break;
                    case Cursor.FIELD_TYPE_INTEGER:
                        result.put(fetchedColumns[i], cursor.getInt(columnIndex));
                        break;
                    case Cursor.FIELD_TYPE_FLOAT:
                        result.put(fetchedColumns[i], cursor.getFloat(columnIndex));
                        break;
                    case Cursor.FIELD_TYPE_BLOB:
                        result.put(fetchedColumns[i], cursor.getBlob(columnIndex));
                        break;
                    case Cursor.FIELD_TYPE_NULL:
                        result.put(fetchedColumns[i], null);
                        break;
                    default:
                        throw new RuntimeException("Unknown cursor field type");
                }
            }
            return result;
        } else {
            return null;
        }
    }

    public static String getItemColumnValue(SQLiteDatabase db, String tableName,
                                            String columnName, Long itemId) {
        Map<String, Object> itemProperties = getItemByIdMap(db, tableName,
                new String[] { columnName }, itemId);
        Object columnValue = itemProperties.get(columnName);
        return columnValue != null? columnValue.toString(): "";
    }

    public static void deleteItemById(SQLiteDatabase db, String tableName, Long itemID) {
        Map<String, Object> item = getItemByIdMap(db, tableName, null, itemID);
        if (item != null) {
            String whereClause = String.format("%s = ?", BaseColumns._ID);
            db.delete(tableName, whereClause, new String[]{itemID.toString()});
            if (BuildConfig.DEBUG) Log.d(LOG_TAG, String.format(
                    "Row has been removed from table %s,\nObject map: %s",
                    tableName, item.toString()));
        }
    }

    public static void deleteItemsByFieldValue(SQLiteDatabase db, String tableName,
                                               String fieldName, Object fieldValue) {
        String whereClause = String.format("%s = ?", fieldName);
        db.delete(tableName, whereClause, new String[]{fieldValue.toString()});
        if (BuildConfig.DEBUG) Log.d(LOG_TAG, String.format(
                "Rows has been deleted from table '%s' where '%s'",
                tableName, whereClause.replace("?", fieldValue.toString())));
    }
}
