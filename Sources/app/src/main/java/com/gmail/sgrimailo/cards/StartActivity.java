package com.gmail.sgrimailo.cards;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
    }

    public void onNewCardsButtonClick(View view) {
        Intent intent = new Intent(this, NewCardsDetailsActivity.class);
        startActivity(intent);
    }
}
