package com.gmail.sgrimailo.cards.db.helper;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.gmail.sgrimailo.cards.db.CardsContract;

/**
 * Created by Sergey on 6/7/2017.
 */

final class DataBaseCreationAct2 implements DataBaseCreationAct {

    private static final String CREATE_CARDS_TABLE = String.format(
            String.format("%s\n %s\n %s\n %s\n %s",
                    "CREATE TABLE %s (",
                    "%s INTEGER PRIMARY KEY, ",
                    "%s INTEGER, ",
                    "%s TEXT, ",
                    "%s TEXT)"
            ),
            CardsContract.Cards.TABLE_NAME,
            CardsContract.Cards._ID,
            CardsContract.Cards.COLUMN_CARD_SET_ID,
            CardsContract.Cards.COLUMN_FRONT_SIDE,
            CardsContract.Cards.COLUMN_BACK_SIDE
    );


    @Override
    public void doAction(SQLiteDatabase db) {
        Log.d(CardsContract.Cards.LOG_TAG,
                String.format("Table Creation:\n%s", CREATE_CARDS_TABLE));
        db.execSQL(CREATE_CARDS_TABLE);
    }
}
