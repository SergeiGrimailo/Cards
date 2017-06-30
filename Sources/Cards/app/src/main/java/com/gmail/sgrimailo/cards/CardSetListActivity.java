package com.gmail.sgrimailo.cards;

import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.gmail.sgrimailo.cards.db.CardsContract.CardSets;
import com.gmail.sgrimailo.cards.db.CardsContract.Cards;
import com.gmail.sgrimailo.cards.db.CardsPersister;
import com.gmail.sgrimailo.cards.db.helper.CardsDBHelper;
import com.gmail.sgrimailo.cards.preferences.PreferencesHelper;
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


    private Long selectedCardSetId = null;

    private static String genTag(String aTagName) {
        return String.format("%s.%s", CardSetListActivity.class.getSimpleName(), aTagName);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_set_list);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        updateListView();
        ListView cardSetsListView = (ListView) findViewById(R.id.lstvCardSets);
        cardSetsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(CardSetListActivity.class.getName(), String.format("Item selected - position: %d, id: %d", position, id));
                selectedCardSetId = id;
            }
        });
    }

    private void updateListView() {
        SQLiteDatabase db = new CardsDBHelper(this).getReadableDatabase();

        String[] projection = {CardSets._ID, CardSets.COLUMN_TITLE};
        Cursor cardSetsCursor = db.query(CardSets.TABLE_NAME, projection, null, null, null, null,
                String.format("%s DESC", CardSets.COLUMN_TYPE));

        CursorAdapter cardSetsAdapter = new SimpleCursorAdapter(this, R.layout.list_item_card_set,
                cardSetsCursor, new String[]{CardSets._ID, CardSets.COLUMN_TITLE},
                new int[]{R.id.tvCardSetID, R.id.tvCardSetTitle}, 0);

        ListView cardSetsListView = (ListView) findViewById(R.id.lstvCardSets);
        cardSetsListView.setAdapter(cardSetsAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        int runMode = getIntent().getIntExtra(EXTRA_RUN_MODE, 0);
        if (runMode == RUN_MODE_SELECT_CARD_SET) {
            getMenuInflater().inflate(R.menu.done_button_menu, menu);
        } else {
            getMenuInflater().inflate(R.menu.card_set_list_menu, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_to_file_action:
                saveSelectedCardSetToFile(null);
                return true;
            case R.id.action_add:
                onAddButtonClick(null);
                break;
            case R.id.action_edit:
                onEditButtonClick(null);
                break;
            case R.id.action_remove:
                onDeleteButtonClick(null);
                break;
            case R.id.action_show_cards:
                onCardsButtonClick(null);
                break;
            case R.id.action_done:
                onSelectButtonClick(null);
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_CREATE_NEW_CARD_SET:
            case REQUEST_CODE_EDIT_CARD_SET:
                if (resultCode == Activity.RESULT_OK) {
                    updateListView();
                }
                break;
            case REQUEST_CODE_CREATE_TEXT_DOCUMENT:
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        OutputStream outStr = getContentResolver().openOutputStream(data.getData());
                        try {
                            Writer writer = new OutputStreamWriter(outStr);
                            CardsPersister.persistCards(new CardsDBHelper(this).getReadableDatabase(),
                                    selectedCardSetId, writer);
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

    }

    public void onAddButtonClick(View view) {
        Intent intent = new Intent(this, CardSetDetailsActivity.class);
        intent.putExtra(CardSetDetailsActivity.EXTRA_CARD_SET_ACTION,
                CardSetDetailsActivity.CARD_SET_ACTION_CREATE_NEW_ONE);
        startActivityForResult(intent, REQUEST_CODE_CREATE_NEW_CARD_SET);
    }

    public void onCardsButtonClick(View view) {
        if (selectedCardSetId != null) {
            Intent intent = new Intent(this, CardListActivity.class);
            intent.putExtra(CardListActivity.EXTRA_CARD_SET_ID, selectedCardSetId);
            startActivity(intent);
        }
    }

    public void onEditButtonClick(View view) {
        if (selectedCardSetId != null) {
            Intent intent = new Intent(this, CardSetDetailsActivity.class);
            intent.putExtra(CardSetDetailsActivity.EXTRA_CARD_SET_ACTION,
                    CardSetDetailsActivity.CARD_SET_ACTION_EDIT_EXISTING_ONE);
            intent.putExtra(CardSetDetailsActivity.EXTRA_CARD_SET_ID, selectedCardSetId);
            startActivityForResult(intent, REQUEST_CODE_EDIT_CARD_SET);
        }
    }

    public void onDeleteButtonClick(View view) {
        if (selectedCardSetId != null) {
            final SQLiteDatabase db = new CardsDBHelper(this).getWritableDatabase();
            Map<String, Object> catToBeDeleted = DataBaseHelper.getItemByIdMap(db,
                    CardSets.TABLE_NAME, CardSets.COLUMNS_FULL_SET, selectedCardSetId);
            if (catToBeDeleted != null) {
                Integer type = (Integer) catToBeDeleted.get(CardSets.COLUMN_TYPE);
                if (type > 0) {
                    new AlertDialog.Builder(this)
                            .setMessage(R.string.alert_couldnt_delete_system_category)
                            .setPositiveButton(android.R.string.ok, null)
                            .show();
                } else {
                    Cursor cardsCursor = db.query(Cards.TABLE_NAME, new String[]{Cards._ID},
                            String.format("%s = ?", Cards.COLUMN_CARD_SET_ID),
                            new String[]{selectedCardSetId.toString()}, null, null, null);
                    if (cardsCursor.getCount() > 0) {
                        DialogInterface.OnClickListener posBtnListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPreferences shp = PreferencesHelper
                                        .getSharedPreferences(CardSetListActivity.this);
                                Long unsortedCat = shp.getLong(
                                        PreferencesHelper.SHARED_UNSORTED_CATEGORY_ID, -1);
                                if (unsortedCat > -1) {
                                    ContentValues cv = new ContentValues();
                                    cv.put(Cards.COLUMN_CARD_SET_ID, unsortedCat);
                                    db.update(Cards.TABLE_NAME, cv,
                                            String.format("%s = ?", Cards.COLUMN_CARD_SET_ID),
                                            new String[] {selectedCardSetId.toString()});
                                    deleteCardSet(db, CardSetListActivity.this.selectedCardSetId);
                                } else {
                                    new AlertDialog.Builder(CardSetListActivity.this)
                                            .setMessage(R.string.couldnt_find_unsorted_cat)
                                            .setPositiveButton(android.R.string.ok, null).show();
                                }
                            }
                        };
                        DialogInterface.OnClickListener negBtnListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DataBaseHelper.deleteItemByFieldValue(db, Cards.TABLE_NAME,
                                        Cards.COLUMN_CARD_SET_ID, selectedCardSetId);
                                deleteCardSet(db, CardSetListActivity.this.selectedCardSetId);
                            }
                        };
                        new AlertDialog.Builder(this)
                                .setMessage(R.string.alert_question_move_cards_to_unsorted)
                                .setPositiveButton(R.string.action_move_to_unsorted,
                                        posBtnListener)
                                .setNegativeButton(R.string.action_delete_all_underlaying_cards,
                                        negBtnListener)
                                .show();
                    } else {
                        deleteCardSet(db, selectedCardSetId);
                    }
                }
            }
        }
    }

    private void deleteCardSet(SQLiteDatabase db, Long cardSetId) {
        DataBaseHelper.deleteItemById(db, CardSets.TABLE_NAME, cardSetId);
        updateListView();
    }

    public void onSelectButtonClick(View view) {
        if (selectedCardSetId != null) {
            Intent data = new Intent();
            data.putExtra(EXTRA_SELECTED_CARD_SET_ID, selectedCardSetId);
            setResult(Activity.RESULT_OK, data);
            finish();
        }
    }

    public void saveSelectedCardSetToFile(View view) {
        if (selectedCardSetId != null) {
            // ask for file name
            SQLiteDatabase db = new CardsDBHelper(this).getReadableDatabase();
            Map<String, Object> cardSetFields = DataBaseHelper.getItemByIdMap(
                    db, CardSets.TABLE_NAME, new String[]{CardSets.COLUMN_TITLE},
                    selectedCardSetId);
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
