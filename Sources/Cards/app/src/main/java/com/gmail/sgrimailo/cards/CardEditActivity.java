package com.gmail.sgrimailo.cards;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.gmail.sgrimailo.cards.db.CardsContract;
import com.gmail.sgrimailo.cards.db.helper.CardsDBHelper;
import com.gmail.sgrimailo.utils.db.DataBaseFacade;

import java.util.Map;

/*package*/ abstract class CardEditActivity extends AppCompatActivity {

    public static final String EXTRA_CARD_ACTION = String.format("%s.%s",
            DefinitionCardActivity.class.getName(), "EXTRA_CARD_ACTION");

    public static final int CARD_ACTION_CREATE_NEW = 1;
    public static final int CARD_ACTION_EDIT_EXISTING_ONE = 2;

    public static final String EXTRA_CARD_SET_ID = String.format("%s.%s",
            DefinitionCardActivity.class.getName(), "EXTRA_CARD_SET_ID");

    public static final String EXTRA_CARD_ID = "EXTRA_CARD_ID";

    protected Integer cardAction;
    protected Long cardSetId;
    protected Long cardId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(getCardUILayoutRes());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        processIntent(getIntent());

        if (cardAction == CARD_ACTION_EDIT_EXISTING_ONE) {
            DataBaseFacade dbFacade = new DataBaseFacade(
                    new CardsDBHelper(this).getReadableDatabase());
            Map<String, Object> cardFieldsMap = dbFacade.getItemByIdMap(CardsContract.Cards.TABLE_NAME,
                    new String[] {}, cardId);
            if (cardFieldsMap != null) {
                fillUIWithCard(cardFieldsMap);
            } else {
                throw new IllegalStateException(String.format("Couldn't find card (id: %d)", cardId));
            }
            getSupportActionBar().setTitle(R.string.action_edit_definition);
        } else {
            getSupportActionBar().setTitle(R.string.action_add_definition);
        }
    }

    @Override
    protected void onDestroy() {
        if (mCreateOrUpdateTask != null) {
            // TODO What should we do?
            //mCreateOrUpdateTask.cancel(false);
        }
        super.onDestroy();
    }

    protected abstract int getCardUILayoutRes();

    protected abstract void fillUIWithCard(Map<String, Object> cardFieldsMap);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.done_button_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_done) {
            onDoneButtonClick();
            return true;
        } else if (item.getItemId() == android.R.id.home) {
            setResult(Activity.RESULT_CANCELED);
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    public void onDoneButtonClick() {
        if (mCreateOrUpdateTask == null) {
            ContentValues values = new ContentValues();
            if (collectCardValues(values)) {
                mCreateOrUpdateTask = new CreateOrUpdateCardTask();
                mCreateOrUpdateTask.execute(values);
            }
        }
    }

    /**
     * Collects card fields' values, returns true if successful.
     *
     * @param values
     * @return True - collecting got success, false - otherwise.
     */
    protected abstract boolean collectCardValues(ContentValues values);

    private CreateOrUpdateCardTask mCreateOrUpdateTask = null;

    private class CreateOrUpdateCardTask extends AsyncTask<ContentValues, Void, Long> {

        @Override
        protected Long doInBackground(ContentValues... params) {
            try {
                ContentValues values = params[0];
                SQLiteOpenHelper helper = new CardsDBHelper(CardEditActivity.this);
                SQLiteDatabase db = helper.getWritableDatabase();

                if (cardAction == CARD_ACTION_CREATE_NEW) {

                    values.put(CardsContract.Cards.COLUMN_CARD_SET_ID, cardSetId);
                    long newRowId = db.insertOrThrow(CardsContract.Cards.TABLE_NAME, null, values);

                    Log.d(CardsContract.Cards.LOG_TAG,
                            String.format("New card has been inserted - table: %s, id: %d, values: %s",
                                    CardsContract.Cards.TABLE_NAME, newRowId, values));
                    return newRowId;

                } else if (cardAction == CARD_ACTION_EDIT_EXISTING_ONE) {
                    String whereClause = String.format("%s = ?", CardsContract.Cards._ID);
                    int rowNumber = db.update(CardsContract.Cards.TABLE_NAME, values,
                            whereClause, new String[]{cardId.toString()});
                    if (rowNumber == 1) {
                        Log.d(CardsContract.Cards.LOG_TAG,
                                String.format("Card has been edited - id: %s, values: %s",
                                        cardId, values));
                        return cardId;
                    } else {
                        throw new RuntimeException("Unexpected case");
                    }
                } else {
                    throw new RuntimeException("Unknown action");
                }
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(Long aCardId) {
            if (aCardId != null) {
                Intent result = new Intent();
                getIntent().putExtra(EXTRA_CARD_ID, aCardId);
                setResult(Activity.RESULT_OK, result);
                finish();
            } else {
                //TODO Show something smart...
            }
            mCreateOrUpdateTask = null;
        }
    }

}
