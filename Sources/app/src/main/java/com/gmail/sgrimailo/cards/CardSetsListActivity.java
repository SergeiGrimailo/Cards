package com.gmail.sgrimailo.cards;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.gmail.sgrimailo.cards.db.CardsContract;
import com.gmail.sgrimailo.cards.db.CardsContract.CardSets;
import com.gmail.sgrimailo.cards.db.helper.CardsDBHelper;

public class CardSetsListActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_CREATE_NEW_CARD_SET = 1;
    private Long selectedCardSetID = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_sets_list);

        updateListView();

        ListView cardSetsListView = (ListView) findViewById(R.id.lstvCardSets);
        cardSetsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(CardSetsListActivity.class.getName(), String.format("Item selected - position: %d, id: %d", position, id));
                selectedCardSetID = id;
            }
        });
    }

    @NonNull
    private void updateListView() {
        SQLiteDatabase db = new CardsDBHelper(this).getReadableDatabase();

        String[] projection = {CardSets._ID, CardSets.COLUMN_TITLE};
        Cursor cardSetsCursor = db.query(CardSets.TABLE_NAME, projection, null, null, null, null, null);

        CursorAdapter cardSetsAdapter = new SimpleCursorAdapter(this, R.layout.list_item_card_set,
                cardSetsCursor, new String[] {CardSets._ID, CardSets.COLUMN_TITLE},
                new int[] {R.id.tvCardSetID, R.id.tvCardSetTitle}, 0);

        ListView cardSetsListView = (ListView) findViewById(R.id.lstvCardSets);
        cardSetsListView.setAdapter(cardSetsAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        updateListView();
    }

    public void onAddButtonClick(View view) {
        Intent intent = new Intent(this, CardSetDetailsActivity.class);
        intent.putExtra(CardSetDetailsActivity.EXTRA_CARD_SET_ACTION,
                CardSetDetailsActivity.CARD_SET_ACTION_CREATE_NEW_ONE);
        startActivityForResult(intent, REQUEST_CODE_CREATE_NEW_CARD_SET);
    }

    public void onEditButtonClick(View view) {
        if (selectedCardSetID != null) {
            Intent intent = new Intent(this, CardSetCardsActivity.class);
            intent.putExtra(CardSetCardsActivity.EXTRA_CARD_SET_ID, selectedCardSetID);
            startActivity(intent);
        }
    }

    public void onRefreshButtonClick(View view) {
        updateListView();
    }
}
