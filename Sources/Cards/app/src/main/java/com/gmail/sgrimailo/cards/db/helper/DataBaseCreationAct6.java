package com.gmail.sgrimailo.cards.db.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.gmail.sgrimailo.cards.db.CardsContract.Cards;

/**
 * Created by user on 22.11.2017.
 */

public class DataBaseCreationAct6 implements DataBaseCreationAct {

    private static String SQL_ALTER_CARDS_TABLE = String.format(
            "ALTER TABLE %s ADD COLUMN %s INTEGER NOT NULL DEFAULT 0",
            Cards.TABLE_NAME, Cards.COLUMN_TYPE);

    @Override
    public void doAction(SQLiteDatabase db, Context context) {
        db.execSQL(SQL_ALTER_CARDS_TABLE);
    }
}
