package com.gmail.sgrimailo.cards;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
        processIntent(getIntent());
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

        if (cardAction == CARD_ACTION_CREATE_NEW) {

            ContentValues values = new ContentValues();
            values.put(Cards.COLUMN_CARD_SET_ID, cardSetId);
            values.put(Cards.COLUMN_FRONT_SIDE, edtFrontSideContent.getText().toString());
            values.put(Cards.COLUMN_BACK_SIDE, edtBackSideContent.getText().toString());

            long newRowId = db.insertOrThrow(Cards.TABLE_NAME, null, values);
            Log.d(Cards.LOG_TAG,
                    String.format("New row has been inserted - table: %s, id: %d, values: %s",
                            Cards.TABLE_NAME, newRowId, values));

            Intent result = new Intent();
            getIntent().putExtra(EXTRA_CARD_ID, newRowId);
            setResult(Activity.RESULT_OK, result);
        } else {
            throw new RuntimeException("Not implemented yet");
        }
        finish();
    }
}
