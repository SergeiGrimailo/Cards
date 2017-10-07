package com.gmail.sgrimailo.cards.db.helper;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;

import com.gmail.sgrimailo.cards.db.CardsContract.CardSets;
import com.gmail.sgrimailo.cards.preferences.PreferencesHelper;

/**
 * Created by Sergey on 6/30/2017.
 */

public class DataBaseCreationAct3 implements DataBaseCreationAct {

    @Override
    public void doAction(SQLiteDatabase db, Context context) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(CardSets.COLUMN_TITLE, "<Unsorted>");
        long newRowId = db.insert(CardSets.TABLE_NAME, "", contentValues);
        if (newRowId > -1) {
            SharedPreferences sharedPreferences = PreferencesHelper.getSharedPreferences(context);
            sharedPreferences.edit().putLong(PreferencesHelper.SHARED_UNSORTED_CATEGORY_ID,
                    newRowId).commit();
        } else {
            throw new RuntimeException("Data base updated error");
        }
    }
}
