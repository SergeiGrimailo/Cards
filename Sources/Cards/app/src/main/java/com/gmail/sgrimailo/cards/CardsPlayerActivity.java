package com.gmail.sgrimailo.cards;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.gmail.sgrimailo.cards.db.CardsContract;
import com.gmail.sgrimailo.cards.db.CardsContract.Cards;
import com.gmail.sgrimailo.cards.db.helper.CardsDBHelper;

public class CardsPlayerActivity extends AppCompatActivity {

    public static final String EXTRA_CARD_SET_ID = String.format("%s.%s",
            CardsPlayerActivity.class.getName(), "EXTRA_CARD_SET_ID");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cards_player);

        Long cardSetID = getIntent().getLongExtra(EXTRA_CARD_SET_ID, -1);

        SQLiteOpenHelper helper = new CardsDBHelper(this);
        SQLiteDatabase db = helper.getReadableDatabase();
        String[] columns = {Cards._ID, Cards.COLUMN_FRONT_SIDE, Cards.COLUMN_BACK_SIDE};
        String selection = String.format("%s = ?", Cards.COLUMN_CARD_SET_ID);
        Cursor cardSetCursor = db.query(Cards.TABLE_NAME, columns, selection,
                new String[] {cardSetID.toString()}, null, null, null);

        ViewPager vpCardsPlayer = (ViewPager) findViewById(R.id.vpCardsPlayer);
        vpCardsPlayer.setAdapter(new CardsPlayerAdapter(getSupportFragmentManager(), cardSetCursor));
    }

    private class CardsPlayerAdapter extends FragmentStatePagerAdapter {

        private final Cursor mCardSetCursor;
        private final int mIDColumnIndex;

        public CardsPlayerAdapter(FragmentManager fm, Cursor cardSetCursor) {
            super(fm);
            mCardSetCursor = cardSetCursor;
            mIDColumnIndex = mCardSetCursor.getColumnIndex(Cards._ID);
        }

        @Override
        public Fragment getItem(int position) {

            CardsPlayerFragment cardsPlayerFragment = new CardsPlayerFragment();
            Bundle args = new Bundle();

            mCardSetCursor.moveToPosition(position);
            args.putLong(CardsPlayerFragment.ARG_CARD_ID, mCardSetCursor.getLong(mIDColumnIndex));

            cardsPlayerFragment.setArguments(args);
            return cardsPlayerFragment;
        }

        @Override
        public int getCount() {
            return mCardSetCursor.getCount();
        }
    }
}
