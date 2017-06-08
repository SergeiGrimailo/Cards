package com.gmail.sgrimailo.cards.db;

import android.provider.BaseColumns;

/**
 * Created by Sergey on 6/6/2017.
 */

public final class CardsContract {

    public static final String LOG_TAG = "CardsDataBase";

    private CardsContract() {}

    public static final class CardSets implements BaseColumns {

        public static final String LOG_TAG = String.format("%s - %s",CardsContract.LOG_TAG, CardSets.class.getSimpleName());

        public static final String TABLE_NAME = "card_sets";

        public static final String COLUMN_TITLE = "title";
    }

}
