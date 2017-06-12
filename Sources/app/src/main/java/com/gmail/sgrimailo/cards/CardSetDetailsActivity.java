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

import com.gmail.sgrimailo.cards.db.CardsContract.CardSets;
import com.gmail.sgrimailo.cards.db.helper.CardsDBHelper;

public class CardSetDetailsActivity extends AppCompatActivity {

    public static final String EXTRA_CARD_SET_ACTION = String.format("%s.%s",
            CardSetDetailsActivity.class.getName(), "EXTRA_CARD_SET_ACTION");

    public static final int CARD_SET_ACTION_CREATE_NEW_ONE = 1;
    public static final int CARD_SET_ACTION_EDIT_EXISTING_ONE = 2;

    public static final String EXTRA_CARD_SET_ID = String.format("%s.%s",
            CardSetDetailsActivity.class.getName(), "EXTRA_CARD_SET_ID");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_set_details);

        if (!getIntent().hasExtra(EXTRA_CARD_SET_ACTION)) {
            throw new IllegalStateException(String.format("Set up card set activity action (%s)",
                    "EXTRA_CARD_SET_ACTION"));
        }
    }

    public void onDoneButtonClick(View view) {

        // Creation of new cards set record
        SQLiteOpenHelper helper = new CardsDBHelper(this);
        SQLiteDatabase db = helper.getWritableDatabase();

        EditText etCardsSetName = (EditText) findViewById(R.id.etNewCardsSetName);
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

        int action = getIntent().getExtras().getInt(EXTRA_CARD_SET_ACTION);
        if (action == CARD_SET_ACTION_CREATE_NEW_ONE) {
            long newRowId = db.insert(CardSets.TABLE_NAME, null, values);
            Log.d(CardSets.LOG_TAG,
                    String.format("New row has been inserted:\n_ID: %d, TITLE: %s",
                            newRowId, newCardsSetName));

            Intent intent = new Intent(this, CardSetCardsActivity.class);
            intent.putExtra(EXTRA_CARD_SET_ID, newRowId);
            setResult(Activity.RESULT_OK, intent);

        } else {
            throw new RuntimeException("Not implemented yet");
        }
        finish();
    }
}
