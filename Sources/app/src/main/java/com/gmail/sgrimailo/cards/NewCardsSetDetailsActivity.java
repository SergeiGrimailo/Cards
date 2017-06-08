package com.gmail.sgrimailo.cards;

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

public class NewCardsSetDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_cards_details);
    }

    public void onNextButtonClick(View view) {

        // Creation of new cards set record
        SQLiteOpenHelper helper = new CardsDBHelper(this);
        SQLiteDatabase db = helper.getWritableDatabase();

        EditText etCardsSetName = (EditText) findViewById(R.id.etNewCardsSetName);
        String newCardsSetName = etCardsSetName.getText().toString();
        if (newCardsSetName.isEmpty()) {
            new AlertDialog.Builder(this)
                    .setTitle(android.R.string.dialog_alert_title)
                    .setMessage(R.string.newNameIsEmpty)
                    .show();
            return;
        }

        ContentValues values = new ContentValues();
        values.put(CardSets.COLUMN_TITLE, newCardsSetName);

        long newRowId = db.insert(CardSets.TABLE_NAME, null, values);
        Log.d(CardSets.LOG_TAG,
                String.format("New row has been inserted:\n_ID: %d, TITLE: %s",
                        newRowId, newCardsSetName));

        Intent intent = new Intent(this, CardsSetActivity.class);
        intent.putExtra(CardsSetActivity.EXTRA_CARDS_SET_ID_LABEL, newRowId);
        startActivity(intent);
    }

    public void onCancelButtonClick(View view) {
        onBackPressed();
    }
}
