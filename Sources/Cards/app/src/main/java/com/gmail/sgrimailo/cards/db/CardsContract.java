package com.gmail.sgrimailo.cards.db;

import android.provider.BaseColumns;

/**
 * Created by Sergey on 6/6/2017.
 */

public final class CardsContract {

    public static final String LOG_TAG = "CardsDataBase";

    private CardsContract() {}

    public static final class CardSets implements BaseColumns {

        public static final String LOG_TAG = String.format("%s - %s",
                CardsContract.LOG_TAG, CardSets.class.getSimpleName());

        public static final String TABLE_NAME = "card_sets";

        public static final String COLUMN_TITLE = "title";

        public enum CardSetType { UserDefined, Unsorted };
        /**
         * 0 - user-defined card set,
         * 1 - unsorted card set
         */
        public static final String COLUMN_TYPE = "type";

        public static final String[] COLUMNS_FULL_SET = {_ID, COLUMN_TYPE, COLUMN_TITLE};
    }

    public static final class Cards implements BaseColumns {

        public static final String LOG_TAG = String.format("%s - %s",
                CardsContract.LOG_TAG, Cards.class.getSimpleName());

        public static final String TABLE_NAME = "cards";

        public static final String COLUMN_CARD_SET_ID = "card_set_id";

        public static final String COLUMN_FRONT_SIDE = "front_side";
        public static final String COLUMN_BACK_SIDE = "back_side";

        public static final String COLUMN_WORD = "word";
        public static final String COLUMN_TRANSCRIPTION = "transcription";
        public static final String COLUMN_USAGE_EXAMPLES = "usage_examples";
        public static final String COLUMN_MEANING = "meaning";

        public enum CardType { Definition, ForeignWord };

        /**
         * For available values see @CardType enumeration.
         */
        public static final String COLUMN_TYPE = "type";
    }

}
