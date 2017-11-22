package com.gmail.sgrimailo.cards;

import android.content.ContentValues;
import android.widget.EditText;

import com.gmail.sgrimailo.cards.db.CardsContract.Cards;

import java.util.Map;

public class DefinitionCardActivity extends CardEditActivity {

    protected void fillUIWithCard(Map<String, Object> aCardFieldsMap) {
        EditText etWord = (EditText) findViewById(R.id.etWord);
        EditText etDefinition = (EditText) findViewById(R.id.etDefinition);
        etWord.setText(aCardFieldsMap.get(Cards.COLUMN_WORD).toString());
        etDefinition.setText(aCardFieldsMap.get(Cards.COLUMN_MEANING).toString());
    }

    @Override
    protected int getCardUILayoutRes() {
        return R.layout.activity_definition_card;
    }

    protected boolean collectCardValues(ContentValues contentValues) {
        EditText edtFrontSideContent = (EditText) findViewById(R.id.etWord);
        EditText edtBackSideContent = (EditText) findViewById(R.id.etDefinition);

        boolean areValuesReady = isEditTextValid(edtFrontSideContent, true);
        areValuesReady &= isEditTextValid(edtBackSideContent, areValuesReady);

        if (areValuesReady) {
            contentValues.put(Cards.COLUMN_WORD, edtFrontSideContent.getText().toString());
            contentValues.put(Cards.COLUMN_MEANING, edtBackSideContent.getText().toString());
        }

        return areValuesReady;
    }

    private boolean isEditTextValid(EditText editText, boolean canBeFirstError) {
        if (editText.getText().toString().isEmpty()) {
            editText.setError(getString(R.string.alert_type_not_empty_value));
            if (canBeFirstError) {
                editText.requestFocus();
            }
            return false;
        }
        return true;
    }
}
