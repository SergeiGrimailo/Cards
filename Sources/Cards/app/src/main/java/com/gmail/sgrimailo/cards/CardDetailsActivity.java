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

import com.gmail.sgrimailo.cards.db.CardsContract.Cards;
import com.gmail.sgrimailo.cards.db.helper.CardsDBHelper;

public class CardDetailsActivity extends AppCompatActivity {

    public static final String EXTRA_CARD_ACTION = String.format("%s.%s",
            CardDetailsActivity.class.getName(), "EXTRA_CARD_ACTION");

    public static final int CARD_ACTION_CREATE_NEW = 1;
    public static final int CARD_ACTION_EDIT_EXISTING_ONE = 2;

    public static final String EXTRA_CARD_SET_ID = String.format("%s.%s",
            CardDetailsActivity.class.getName(), "EXTRA_CARD_SET_ID");
    public static final String EXTRA_CARD_ID = "EXTRA_CARD_ID";
    private Integer cardAction;
    private Long cardSetId;
    private Long cardId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_details);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        processIntent(getIntent());

        if (cardAction == CARD_ACTION_EDIT_EXISTING_ONE) {

            SQLiteOpenHelper helper = new CardsDBHelper(this);
            SQLiteDatabase db = helper.getReadableDatabase();

            String[] columns = {};
            String selection = String.format("%s = ?", Cards._ID);
            Cursor cardCursor = db.query(Cards.TABLE_NAME, columns, selection,
                    new String[] {cardId.toString()}, null, null, null);
            if (cardCursor.moveToFirst()) {
                int frontSideColumnInd = cardCursor.getColumnIndex(Cards.COLUMN_FRONT_SIDE);
                int backSideColumnInd = cardCursor.getColumnIndex(Cards.COLUMN_BACK_SIDE);

                EditText edtFrontSideContent = (EditText) findViewById(R.id.edtFrontSideContent);
                EditText edtBackSideContent = (EditText) findViewById(R.id.edtBackSideContent);

                edtFrontSideContent.setText(cardCursor.getString(frontSideColumnInd));
                edtBackSideContent.setText(cardCursor.getString(backSideColumnInd));

            } else {
                throw new IllegalStateException(String.format("Couldn't find card (id: %d)", cardId));
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
        } else if (item.getItemId() == android.R.id.home) {
            setResult(Activity.RESULT_CANCELED);
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void processIntent(Intent intent) {
        if (!intent.hasExtra(EXTRA_CARD_ACTION)) {
            throw new IllegalStateException(
                    String.format("Set up card details activity action (%s)", "EXTRA_CARD_ACTION"));
        } else {
            cardAction = intent.getIntExtra(EXTRA_CARD_ACTION, 0);
            if (cardAction == CARD_ACTION_CREATE_NEW) {
                if (!intent.hasExtra(EXTRA_CARD_SET_ID)) {
                    throw new IllegalStateException(
                            String.format("Set up card set id where new card be placed to (%s)",
                                    "EXTRA_CARD_SET_ID"));
                } else {
                    cardSetId = intent.getLongExtra(EXTRA_CARD_SET_ID, -1);
                }
            } else if (cardAction == CARD_ACTION_EDIT_EXISTING_ONE) {
                if (!intent.hasExtra(EXTRA_CARD_ID)) {
                    throw new IllegalStateException(
                            String.format("Set up card id to be edited (%s)", "EXTRA_CARD_ID"));
                } else {
                    cardId = intent.getLongExtra(EXTRA_CARD_ID, -1);
                }
            } else {
                throw new IllegalStateException("Unknown card action");
            }
        }
    }

    public void onDoneButtonClick(View view) {

        SQLiteOpenHelper helper = new CardsDBHelper(this);
        SQLiteDatabase db = helper.getWritableDatabase();

        EditText edtFrontSideContent = (EditText) findViewById(R.id.edtFrontSideContent);
        EditText edtBackSideContent = (EditText) findViewById(R.id.edtBackSideContent);

        if (edtFrontSideContent.getText().toString().isEmpty()
                || edtBackSideContent.getText().toString().isEmpty()) {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
            alertBuilder.setTitle(android.R.string.dialog_alert_title);
            alertBuilder.setMessage(R.string.type_not_empty_content);
            alertBuilder.show();
            return;
        }

        ContentValues values = new ContentValues();

        values.put(Cards.COLUMN_FRONT_SIDE, edtFrontSideContent.getText().toString());
        values.put(Cards.COLUMN_BACK_SIDE, edtBackSideContent.getText().toString());


        if (cardAction == CARD_ACTION_CREATE_NEW) {

            values.put(Cards.COLUMN_CARD_SET_ID, cardSetId);
            long newRowId = db.insertOrThrow(Cards.TABLE_NAME, null, values);

            Log.d(Cards.LOG_TAG,
                    String.format("New card has been inserted - table: %s, id: %d, values: %s",
                            Cards.TABLE_NAME, newRowId, values));
            cardId = newRowId;

        } else if (cardAction == CARD_ACTION_EDIT_EXISTING_ONE) {

            String whereClause = String.format("%s = ?", Cards._ID);
            int rowNumber = db.update(Cards.TABLE_NAME, values,
                    whereClause, new String[] {cardId.toString()});

            if (rowNumber == 1) {
                Log.d(Cards.LOG_TAG,
                        String.format("Card has been edited - id: %s, values: %s",
                                cardId, values));
            } else {
                throw new RuntimeException("Unexpected case");
            }

        } else {
            throw new RuntimeException("Unknown action");
        }

        Intent result = new Intent();
        getIntent().putExtra(EXTRA_CARD_ID, cardId);
        setResult(Activity.RESULT_OK, result);

        finish();
    }
}
