package com.gmail.sgrimailo.cards;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class StartActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_CREATE_NEW_CARD_SET = 1;
    private static final int REQUEST_CODE_EDIT_CARD_SET = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
    }

    public void onViewCardSetsButtonClick(View view) {
        Intent intent = new Intent(this, CardSetsListActivity.class);
        startActivity(intent);
    }

    public void onNewCardsButtonClick(View view) {
        Intent intent = new Intent(this, CardSetDetailsActivity.class);
        intent.putExtra(CardSetDetailsActivity.EXTRA_CARD_SET_ACTION,
                CardSetDetailsActivity.CARD_SET_ACTION_CREATE_NEW_ONE);
        startActivityForResult(intent, REQUEST_CODE_CREATE_NEW_CARD_SET);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CREATE_NEW_CARD_SET) {
            if (resultCode == Activity.RESULT_OK) {

                Long createdCardSetID = data.getLongExtra(CardSetDetailsActivity.EXTRA_CARD_SET_ID,
                        -1);
                Intent intent = new Intent(this, CardSetCardsActivity.class);
                intent.putExtra(CardSetCardsActivity.EXTRA_CARD_SET_ID, createdCardSetID);
                startActivityForResult(intent, REQUEST_CODE_EDIT_CARD_SET);
            }
        } else if (requestCode == REQUEST_CODE_EDIT_CARD_SET) {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
            alertBuilder.setTitle("DEBUG");
            alertBuilder.setMessage("Card set has been created!");
            alertBuilder.show();
        }
    }
}
