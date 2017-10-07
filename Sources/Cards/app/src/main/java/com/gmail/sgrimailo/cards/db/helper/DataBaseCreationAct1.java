package com.gmail.sgrimailo.cards.db.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.gmail.sgrimailo.cards.db.CardsContract;

/**
 * Created by Sergey on 6/7/2017.
 */

final class DataBaseCreationAct1 implements DataBaseCreationAct {

    private static final String CREATE_CARDS_SETS_TABLE = String.format(
            String.format("%s\n %s\n %s",
                    "CREATE TABLE %s (",
                    "%s INTEGER PRIMARY KEY, ",
                    "%s TEXT)"
            ),
            CardsContract.CardSets.TABLE_NAME,
            CardsContract.CardSets._ID,
            CardsContract.CardSets.COLUMN_TITLE
    );


    @Override
    public void doAction(SQLiteDatabase db, Context context) {
        Log.d(CardsContract.CardSets.LOG_TAG,
                String.format("Table Creation:\n%s", CREATE_CARDS_SETS_TABLE));
        db.execSQL(CREATE_CARDS_SETS_TABLE);
    }
}
