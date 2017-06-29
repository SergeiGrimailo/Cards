package com.gmail.sgrimailo.cards;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.gmail.sgrimailo.cards.db.CardsPersister;
import com.gmail.sgrimailo.cards.db.CardsContract.CardSets;
import com.gmail.sgrimailo.cards.db.helper.CardsDBHelper;
import com.gmail.sgrimailo.utils.db.DataBaseHelper;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;

public class CardSetListActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_CREATE_NEW_CARD_SET = 1;
    private static final int REQUEST_CODE_EDIT_CARD_SET = 2;
    private static final int REQUEST_CODE_CREATE_TEXT_DOCUMENT = 3;

    public static final String EXTRA_RUN_MODE = genTag("EXTRA_RUN_MODE");

    public static final int RUN_MODE_SELECT_CARD_SET = 1;

    public static final String EXTRA_SELECTED_CARD_SET_ID = genTag("EXTRA_SELECTED_CARD_SET_ID");

    private static final String LOG_TAG = CardSetListActivity.class.getSimpleName();


    private Long selectedCardSetID = null;

    private static String genTag(String aTagName) {
        return String.format("%s.%s", CardSetListActivity.class.getSimpleName(), aTagName);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_set_list);

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
                Log.d(CardSetListActivity.class.getName(), String.format("Item selected - position: %d, id: %d", position, id));
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.card_set_list_actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_to_file_action:
                saveSelectedCardSetToFile(null);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_CREATE_NEW_CARD_SET:
            case  REQUEST_CODE_EDIT_CARD_SET:
                if (resultCode == Activity.RESULT_OK) {
                    updateListView();
                }
                break;
            case REQUEST_CODE_CREATE_TEXT_DOCUMENT:
                try {
                    OutputStream outStr = getContentResolver().openOutputStream(data.getData());
                    try{
                        Writer writer = new OutputStreamWriter(outStr);
                        CardsPersister.persistCards(new CardsDBHelper(this).getReadableDatabase(),
                                selectedCardSetID, writer);
                        writer.flush();
                        writer.close();
                    } finally {
                        outStr.close();
                    }
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Failed to import card set to file.", e);
                }
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

    public void saveSelectedCardSetToFile(View view) {
        if (selectedCardSetID != null) {
            // ask for file name
            SQLiteDatabase db = new CardsDBHelper(this).getReadableDatabase();
            Map<String, Object> cardSetFields = DataBaseHelper.getItemByIdMap(
                    db, CardSets.TABLE_NAME, new String[] {CardSets.COLUMN_TITLE},
                    selectedCardSetID);
            String cardSetTitle = (String) cardSetFields.get(CardSets.COLUMN_TITLE);

            Intent createDocumentIntent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            createDocumentIntent.putExtra(Intent.EXTRA_TITLE, cardSetTitle);
            createDocumentIntent.setType("text/plain");
            createDocumentIntent.addCategory(Intent.CATEGORY_OPENABLE);

            if (createDocumentIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(createDocumentIntent, REQUEST_CODE_CREATE_TEXT_DOCUMENT);
            }
        }
    }
}
