package com.gmail.sgrimailo.utils.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.gmail.sgrimailo.cards.db.CardsContract;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sergey on 6/16/2017.
 */

public class DataBaseHelper {

    public static Cursor getItemByID(SQLiteDatabase db, String tableName, String[] columns, Long itemID) {
        String selection = String.format("%s = ?", CardsContract.Cards._ID);
        Cursor cursor = db.query(tableName, columns, selection,
                new String[] {itemID.toString()}, null, null, null);
        return cursor;
    }

    public static Map<String, Object> getItemByIdMap(SQLiteDatabase db, String tableName,
                                                     String[] columns, Long itemId) {
        Cursor cursor = getItemByID(db, tableName, columns, itemId);
        if (cursor.moveToFirst()) {
            Map<String, Object> result = new HashMap<>();
            for (int i = 0; i < columns.length; i++) {
                int columnIndex = cursor.getColumnIndex(columns[i]);
                int columnType = cursor.getType(columnIndex);
                switch (columnType) {
                    case Cursor.FIELD_TYPE_STRING:
                        result.put(columns[i], cursor.getString(columnIndex));
                        break;
                    case Cursor.FIELD_TYPE_INTEGER:
                        result.put(columns[i], cursor.getInt(columnIndex));
                        break;
                    case Cursor.FIELD_TYPE_FLOAT:
                        result.put(columns[i], cursor.getFloat(columnIndex));
                        break;
                    case Cursor.FIELD_TYPE_BLOB:
                        result.put(columns[i], cursor.getBlob(columnIndex));
                        break;
                    case Cursor.FIELD_TYPE_NULL:
                        result.put(columns[i], null);
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
}
