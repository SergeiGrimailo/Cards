package com.gmail.sgrimailo.cards.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.IOException;
import java.io.Writer;

/**
 * Created by Sergey on 6/29/2017.
 */

public class CardsPersister {

    public static void persistCards(SQLiteDatabase db, Long aCardSetId, Writer aWriter) throws IOException {
        Cursor cursor = db.query(CardsContract.Cards.TABLE_NAME, new String[] {
                CardsContract.Cards.COLUMN_WORD, CardsContract.Cards.COLUMN_MEANING},
                String.format("%s = ?", CardsContract.Cards.COLUMN_CARD_SET_ID),
                new String[] {aCardSetId.toString()}, null, null, null);
        int frontColumnIndex = cursor.getColumnIndex(CardsContract.Cards.COLUMN_WORD);
        int backColumnIndex = cursor.getColumnIndex(CardsContract.Cards.COLUMN_MEANING);
        while (cursor.moveToNext()) {
            aWriter.append(String.format("%s - %s\r\n",
                    cursor.getString(frontColumnIndex), cursor.getString(backColumnIndex)));
        }
    }

}
