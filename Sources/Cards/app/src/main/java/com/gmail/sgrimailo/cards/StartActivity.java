package com.gmail.sgrimailo.cards;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.gmail.sgrimailo.utils.file_explorer.FileExplorerActivity;

public class StartActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_CREATE_NEW_CARD_SET = 1;
    private static final int REQUEST_CODE_EDIT_CARD_SET = 2;
    private static final int REQUEST_CODE_SELECT_CARD_SET = 3;
    private static final int REQUEST_CODE_SELECT_FILE_TO_IMPORT = 4;

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
            if (resultCode == Activity.RESULT_OK) {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                alertBuilder.setTitle("DEBUG");
                alertBuilder.setMessage("Card set has been created!");
                alertBuilder.show();
            }
        } else if (requestCode == REQUEST_CODE_SELECT_CARD_SET) {
            if (resultCode == Activity.RESULT_OK) {
                Long selectedCardSet = data.getLongExtra(CardSetsListActivity.EXTRA_SELECTED_CARD_SET_ID, -1);
                if (selectedCardSet != -1) {
                    Intent intent = new Intent(this, CardsPlayerActivity.class);
                    intent.putExtra(CardsPlayerActivity.EXTRA_CARD_SET_ID, selectedCardSet);
                    startActivity(intent);
                } else {
                    throw new RuntimeException("Unexpected case");
                }
            }
        } else if (requestCode == REQUEST_CODE_SELECT_FILE_TO_IMPORT) {
            if (resultCode == Activity.RESULT_OK) {
                Log.d("file select data", data.toString());
            }
        } else {
            throw new IllegalStateException("Unknown request code");
        }
    }

    public void onRunCardSetButtonClick(View view) {
        Intent intent = new Intent(this, CardSetsListActivity.class);
        intent.putExtra(CardSetsListActivity.EXTRA_RUN_MODE,
                CardSetsListActivity.RUN_MODE_SELECT_CARD_SET);
        startActivityForResult(intent, REQUEST_CODE_SELECT_CARD_SET);
    }

    public void onPickFileButtonClick(View view) {

        Intent intent = new Intent(this, FileExplorerActivity.class);
        startActivityForResult(intent, REQUEST_CODE_SELECT_FILE_TO_IMPORT);

//        Intent intent = new Intent(this, NavigationDrawerActivity.class);
//        startActivity(intent);


    }

    public void onPurchaseButtonClick(View view) {
        //
    }
}
