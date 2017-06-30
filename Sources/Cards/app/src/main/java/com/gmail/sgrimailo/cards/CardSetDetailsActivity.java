package com.gmail.sgrimailo.cards;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.gmail.sgrimailo.cards.db.CardsContract.CardSets;
import com.gmail.sgrimailo.cards.db.helper.CardsDBHelper;

public class CardSetDetailsActivity extends AppCompatActivity {

    public static final String EXTRA_CARD_SET_ACTION = String.format("%s.%s",
            CardSetDetailsActivity.class.getName(), "EXTRA_CARD_SET_ACTION");

    public static final int CARD_SET_ACTION_CREATE_NEW_ONE = 1;
    public static final int CARD_SET_ACTION_EDIT_EXISTING_ONE = 2;

    public static final String EXTRA_CARD_SET_ID = String.format("%s.%s",
            CardSetDetailsActivity.class.getName(), "EXTRA_CARD_SET_ID");
    private Integer cardSetAction;
    private Long cardSetID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_set_details);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (!getIntent().hasExtra(EXTRA_CARD_SET_ACTION)) {
            throw new IllegalStateException(String.format("Set up card set activity action (%s)",
                    "EXTRA_CARD_SET_ACTION"));
        } else {
            cardSetAction = getIntent().getIntExtra(EXTRA_CARD_SET_ACTION, -1);
            if (cardSetAction == CARD_SET_ACTION_EDIT_EXISTING_ONE) {
                if (!getIntent().hasExtra(EXTRA_CARD_SET_ID)) {
                    throw new IllegalStateException(
                            String.format("Set up card set id (%s)", "EXTRA_CARD_SET_ID"));
                } else {
                    cardSetID = getIntent().getLongExtra(EXTRA_CARD_SET_ID, -1);

                    SQLiteOpenHelper helper = new CardsDBHelper(this);
                    SQLiteDatabase db = helper.getReadableDatabase();

                    String[] columns = {CardSets.COLUMN_TITLE};
                    String selection = String.format("%s = ?", CardSets._ID);
                    Cursor cardSetCursor = db.query(CardSets.TABLE_NAME, columns, selection, new String[] {
                            cardSetID.toString()}, null, null, null);
                    if (cardSetCursor.moveToFirst()) {
                        EditText cardSetNameEditText = (EditText) findViewById(R.id.edt_card_set_name);
                        int cardSetTitleColumnInd = cardSetCursor.getColumnIndex(CardSets.COLUMN_TITLE);
                        cardSetNameEditText.setText(cardSetCursor.getString(cardSetTitleColumnInd));
                    }
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.done_button_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_done) {
            onDoneButtonClick(null);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    public void onDoneButtonClick(View view) {

        // Creation of new cards set record
        SQLiteOpenHelper helper = new CardsDBHelper(this);
        SQLiteDatabase db = helper.getWritableDatabase();

        EditText etCardsSetName = (EditText) findViewById(R.id.edt_card_set_name);
        String newCardsSetName = etCardsSetName.getText().toString();
        if (newCardsSetName.isEmpty()) {
            new AlertDialog.Builder(this)
                    .setTitle(android.R.string.dialog_alert_title)
                    .setMessage(R.string.warning_enter_not_empty_name)
                    .show();
            return;
        }

        ContentValues values = new ContentValues();
        values.put(CardSets.COLUMN_TITLE, newCardsSetName);

        if (cardSetAction == CARD_SET_ACTION_CREATE_NEW_ONE) {
            cardSetID = db.insert(CardSets.TABLE_NAME, null, values);
            Log.d(CardSets.LOG_TAG,
                    String.format("New row has been inserted:\n_ID: %d, TITLE: %s",
                            cardSetID, newCardsSetName));
        } else {
            String whereClause = String.format("%s = ?", CardSets._ID);
            if (db.update(CardSets.TABLE_NAME, values, whereClause, new String[]
                    {cardSetID.toString()}) == 1) {
                Log.d(CardSets.LOG_TAG, String.format(
                        "Card set has been updated (id: %s, values: %s)", cardSetID, values));
            } else {
                throw new RuntimeException("Unexpected case");
            }
        }

        Intent intent = new Intent(this, CardListActivity.class);
        intent.putExtra(EXTRA_CARD_SET_ID, cardSetID);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}
