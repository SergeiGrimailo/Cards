package com.gmail.sgrimailo.utils.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.gmail.sgrimailo.cards.db.CardsContract;

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
}
