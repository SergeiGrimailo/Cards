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

public class DataBaseCreationAct4 implements DataBaseCreationAct {

    private final static String SQL_ALTER_CARD_SET_TABLE = String.format(
            "ALTER TABLE %s ADD COLUMN %s INTEGER NOT NULL DEFAULT 0",
            CardSets.TABLE_NAME,
            CardSets.COLUMN_TYPE);

    @Override
    public void doAction(SQLiteDatabase db, Context context) {

        db.execSQL(SQL_ALTER_CARD_SET_TABLE);

        SharedPreferences sharedPreferences = PreferencesHelper.getSharedPreferences(context);
        Long unsortedCategoryID = sharedPreferences.getLong(
                PreferencesHelper.SHARED_UNSORTED_CATEGORY_ID, -1);

        ContentValues cv = new ContentValues();
        cv.put(CardSets.COLUMN_TYPE, 1);
        db.update(CardSets.TABLE_NAME, cv,
                String.format("%s = ?", CardSets._ID),
                new String[] {unsortedCategoryID.toString()});
    }
}
