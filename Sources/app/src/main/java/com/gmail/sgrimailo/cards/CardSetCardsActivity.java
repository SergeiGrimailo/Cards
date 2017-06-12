package com.gmail.sgrimailo.cards;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.gmail.sgrimailo.cards.db.CardsContract;
import com.gmail.sgrimailo.cards.db.CardsContract.Cards;
import com.gmail.sgrimailo.cards.db.helper.CardsDBHelper;

public class CardSetCardsActivity extends AppCompatActivity {

    public static final String EXTRA_CARD_SET_ID = String.format("%s.%s",
            CardSetCardsActivity.class.getName(), "EXTRA_CARD_SET_ID");

    private static final int REQUEST_CODE_CREATE_NEW_CARD = 1;
    private Long cardSetID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_set_cards);

        processIntent(getIntent());
    }

    private void processIntent(Intent intent) {
        if (!intent.hasExtra(EXTRA_CARD_SET_ID)) {
            throw new IllegalStateException(String.format("Set up card set id (%s)",
                    "EXTRA_CARD_SET_ID"));
        }

        cardSetID = intent.getLongExtra(EXTRA_CARD_SET_ID, -1);

        updateListView();
    }

    private void updateListView() {
        SQLiteDatabase db = new CardsDBHelper(this).getReadableDatabase();

        String[] columns = {Cards._ID, Cards.COLUMN_CARD_SET_ID, Cards.COLUMN_FRONT_SIDE,
                Cards.COLUMN_BACK_SIDE};
        String selection = String.format("%s = ?", Cards.COLUMN_CARD_SET_ID);

        Cursor cardsCursor = db.query(Cards.TABLE_NAME, columns, selection,
                new String[] {cardSetID.toString()}, null, null, null);
        SimpleCursorAdapter cardsAdapter = new SimpleCursorAdapter(this, R.layout.list_item_card,
                cardsCursor, new String[] {Cards._ID,
                Cards.COLUMN_CARD_SET_ID, Cards.COLUMN_FRONT_SIDE, Cards.COLUMN_BACK_SIDE},
                new int[] {R.id.tvID, R.id.tvCardSetID, R.id.tvFrontSide, R.id.tvBackSide}, 0);

        ListView cardsListView = (ListView) findViewById(R.id.lstvCards);
        cardsListView.setAdapter(cardsAdapter);
    }

    public void refreshButtonClick(View view) {
        processIntent(getIntent());
    }

    public void addButtonClick(View view) {
        Intent intent = new Intent(this, CardDetailsActivity.class);
        intent.putExtra(CardDetailsActivity.EXTRA_CARD_ACTION,
                CardDetailsActivity.CARD_ACTION_CREATE_NEW);
        intent.putExtra(CardDetailsActivity.EXTRA_CARD_SET_ID, cardSetID);
        startActivityForResult(intent, REQUEST_CODE_CREATE_NEW_CARD);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CREATE_NEW_CARD) {
            if (resultCode == Activity.RESULT_OK) {
                updateListView();
            }
        }
    }
}
