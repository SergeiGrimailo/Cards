package com.gmail.sgrimailo.cards;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class CardsSetActivity extends AppCompatActivity {

    public static final String EXTRA_CARDS_SET_ID_LABEL = "EXTRA_CARDS_SET_ID_LABEL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_cards);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Integer cardsSetID = intent.getExtras().getInt(EXTRA_CARDS_SET_ID_LABEL);
        if (cardsSetID != null) {
            //
        }
    }

    public void addButtonClick(View view) {
        //
    }
}
