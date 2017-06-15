package com.gmail.sgrimailo.cards;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.gmail.sgrimailo.cards.db.CardsContract.CardSets;
import com.gmail.sgrimailo.cards.db.helper.CardsDBHelper;

public class CardSetsListActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_CREATE_NEW_CARD_SET = 1;
    private static final int REQUEST_CODE_EDIT_CARD_SET = 2;

    public static final String EXTRA_RUN_MODE = String.format("%s.%s",
            CardSetsListActivity.class.getName(), "EXTRA_RUN_MODE");

    public static final int RUN_MODE_SELECT_CARD_SET = 1;

    public static final String EXTRA_SELECTED_CARD_SET_ID = String.format("%s.%s",
            CardSetsListActivity.class.getName(), "EXTRA_SELECTED_CARD_SET_ID");

    private Long selectedCardSetID = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_sets_list);

        int runMode = getIntent().getIntExtra(EXTRA_RUN_MODE, 0);
        View panelCardSetsEditing = findViewById(R.id.panelCardSetsEditing);
        View panelCardSetSelect = findViewById(R.id.panelCardSetSelect);
        if (runMode == RUN_MODE_SELECT_CARD_SET) {
            panelCardSetsEditing.setVisibility(View.INVISIBLE);
            panelCardSetSelect.setVisibility(View.VISIBLE);
        } else {
            panelCardSetsEditing.setVisibility(View.VISIBLE);
            panelCardSetSelect.setVisibility(View.INVISIBLE);
        }

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
        if (resultCode == Activity.RESULT_OK
                && (requestCode == REQUEST_CODE_CREATE_NEW_CARD_SET
                    || requestCode == REQUEST_CODE_EDIT_CARD_SET)) {
            updateListView();
        }
    }

    public void onAddButtonClick(View view) {
        Intent intent = new Intent(this, CardSetDetailsActivity.class);
        intent.putExtra(CardSetDetailsActivity.EXTRA_CARD_SET_ACTION,
                CardSetDetailsActivity.CARD_SET_ACTION_CREATE_NEW_ONE);
        startActivityForResult(intent, REQUEST_CODE_CREATE_NEW_CARD_SET);
    }

    public void onCardsButtonClick(View view) {
        if (selectedCardSetID != null) {
            Intent intent = new Intent(this, CardSetCardsActivity.class);
            intent.putExtra(CardSetCardsActivity.EXTRA_CARD_SET_ID, selectedCardSetID);
            startActivity(intent);
        }
    }

    public void onEditButtonClick(View view) {
        if (selectedCardSetID != null) {
            Intent intent = new Intent(this, CardSetDetailsActivity.class);
            intent.putExtra(CardSetDetailsActivity.EXTRA_CARD_SET_ACTION,
                    CardSetDetailsActivity.CARD_SET_ACTION_EDIT_EXISTING_ONE);
            intent.putExtra(CardSetDetailsActivity.EXTRA_CARD_SET_ID, selectedCardSetID);
            startActivityForResult(intent, REQUEST_CODE_EDIT_CARD_SET);
        }
    }

    public void onSelectCardSetButtonClick(View view) {
        if (selectedCardSetID != null) {
            Intent data = new Intent();
            data.putExtra(EXTRA_SELECTED_CARD_SET_ID, selectedCardSetID);
            setResult(Activity.RESULT_OK, data);
            finish();
        }
    }
}
