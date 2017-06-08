package com.gmail.sgrimailo.cards;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class EditCardsActivity extends AppCompatActivity {

    public static final String EXTRA_CARDS_SET_ID_LABEL = "EXTRA_CARDS_SET_ID_LABEL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_cards);
    }
}
