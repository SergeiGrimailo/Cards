package com.gmail.sgrimailo.cards;

import android.content.ContentValues;
import android.widget.EditText;

import com.gmail.sgrimailo.cards.db.CardsContract.Cards;

import java.util.Map;

public class ForeignWordActivity extends CardEditActivity {

    @Override
    protected int getCardUILayoutRes() {
        return R.layout.activity_foreign_word_card;
    }

    @Override
    protected void fillUIWithCard(Map<String, Object> cardFieldsMap) {
        EditText etWord = (EditText) findViewById(R.id.etWord);
        EditText etTranscription = (EditText) findViewById(R.id.etTranscription);
        EditText etUsageExamples = (EditText) findViewById(R.id.etUsageExamples);
        EditText etTranslation = (EditText) findViewById(R.id.etTranslation);

        etWord.setText(cardFieldsMap.get(Cards.COLUMN_WORD).toString());
        etTranscription.setText(cardFieldsMap.get(Cards.COLUMN_TRANSCRIPTION).toString());
        etUsageExamples.setText(cardFieldsMap.get(Cards.COLUMN_USAGE_EXAMPLES).toString());
        etTranslation.setText(cardFieldsMap.get(Cards.COLUMN_MEANING).toString());
    }

    @Override
    protected boolean collectCardValues(ContentValues values) {
        EditText etWord = (EditText) findViewById(R.id.etWord);
        EditText etTranscription = (EditText) findViewById(R.id.etTranscription);
        EditText etUsageExamples = (EditText) findViewById(R.id.etUsageExamples);
        EditText etTranslation = (EditText) findViewById(R.id.etTranslation);

        boolean areValuesReady = hasEditTextValidValue(etWord, true);
        areValuesReady &= hasEditTextValidValue(etTranslation, areValuesReady);

        if (areValuesReady) {
            values.put(Cards.COLUMN_WORD, etWord.getText().toString());
            values.put(Cards.COLUMN_TRANSCRIPTION, etTranscription.getText().toString());
            values.put(Cards.COLUMN_USAGE_EXAMPLES, etUsageExamples.getText().toString());
            values.put(Cards.COLUMN_MEANING, etTranslation.getText().toString());
        }

        return areValuesReady;
    }

    private boolean hasEditTextValidValue(EditText aEditText, boolean firstError) {
        if (aEditText.getText().toString().isEmpty()) {
            aEditText.setError(getString(R.string.alert_type_not_empty_value));
            if (firstError) {
                aEditText.requestFocus();
            }
            return false;
        }
        return true;
    }
}
