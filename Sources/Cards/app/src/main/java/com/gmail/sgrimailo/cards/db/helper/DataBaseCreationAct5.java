package com.gmail.sgrimailo.cards.db.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.gmail.sgrimailo.cards.db.CardsContract.Cards;

/**
 * Created by user on 19.11.2017.
 */

public class DataBaseCreationAct5 implements DataBaseCreationAct {

    private static String SQL_ALTER_CARDS_ADD_WORD = String.format(
            "ALTER TABLE %s ADD COLUMN %s TEXT",
            Cards.TABLE_NAME, Cards.COLUMN_WORD);
    private static String SQL_ALTER_CARDS_ADD_TRANSCRIPTION = String.format(
            "ALTER TABLE %s ADD COLUMN %s TEXT",
            Cards.TABLE_NAME, Cards.COLUMN_TRANSCRIPTION);
    private static String SQL_ALTER_CARDS_ADD_USAGE_EXAMPLES = String.format(
            "ALTER TABLE %s ADD COLUMN %s TEXT",
            Cards.TABLE_NAME, Cards.COLUMN_USAGE_EXAMPLES);
    private static String SQL_ALTER_CARDS_ADD_MEANING = String.format(
            "ALTER TABLE %s ADD COLUMN %s TEXT",
            Cards.TABLE_NAME, Cards.COLUMN_MEANING);

    private final static String SQL_UPDATE_CARDS_WORD = String.format(
            "UPDATE %s SET %s = %s",
            Cards.TABLE_NAME, Cards.COLUMN_WORD, Cards.COLUMN_FRONT_SIDE
    );

    private final static String SQL_UPDATE_CARDS_MEANING = String.format(
            "UPDATE %s SET %s = %s",
            Cards.TABLE_NAME, Cards.COLUMN_MEANING, Cards.COLUMN_BACK_SIDE
    );

    @Override
    public void doAction(SQLiteDatabase db, Context context) {
        db.execSQL(SQL_ALTER_CARDS_ADD_WORD);
        db.execSQL(SQL_ALTER_CARDS_ADD_TRANSCRIPTION);
        db.execSQL(SQL_ALTER_CARDS_ADD_USAGE_EXAMPLES);
        db.execSQL(SQL_ALTER_CARDS_ADD_MEANING);

        db.execSQL(SQL_UPDATE_CARDS_WORD);
        db.execSQL(SQL_UPDATE_CARDS_MEANING);
    }
}
