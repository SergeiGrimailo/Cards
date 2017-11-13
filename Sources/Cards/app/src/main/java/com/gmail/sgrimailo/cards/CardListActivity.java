package com.gmail.sgrimailo.cards;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.gmail.sgrimailo.cards.db.CardsContract.Cards;
import com.gmail.sgrimailo.cards.db.helper.CardsDBHelper;
import com.gmail.sgrimailo.utils.db.DataBaseHelper;
import com.gmail.sgrimailo.utils.ui.AdjustedMultiChoiceModeListener;

public class CardListActivity extends AppCompatActivity {

    public static final String EXTRA_CARD_SET_ID = String.format("%s.%s",
            CardListActivity.class.getName(), "EXTRA_CARD_SET_ID");

    private static final int REQUEST_CODE_CREATE_NEW_CARD = 1;
    private static final int REQUEST_CODE_EDIT_CARD = 2;

    private Long cardSetID;
    private ListView mCardsListView;
    private ActionMode mCardsListActionMode = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_list);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mCardsListView = (ListView) findViewById(R.id.lstvCards);
        mCardsListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        mCardsListView.setMultiChoiceModeListener(new AdjustedMultiChoiceModeListener(
                mCardsListView, R.menu.card_list_context_menu1, R.menu.card_list_context_menu2) {

            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id,
                                                  boolean checked) {
                super.onItemCheckedStateChanged(mode, position, id, checked);
                mode.setTitle(String.format(getString(R.string.selected_items_template),
                        mCardsListView.getCheckedItemCount()));
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mCardsListActionMode = mode;
                return super.onCreateActionMode(mode, menu);
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_edit:
                        editCheckedCards();
                        break;
                    case R.id.action_remove:
                        deleteCheckedCards();
                        break;
                }
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                mCardsListActionMode = null;
            }
        });
        mCardsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //selectedItemID = id;
            }
        });

        processIntent(getIntent());
    }

    private void processIntent(Intent intent) {
        if (!intent.hasExtra(EXTRA_CARD_SET_ID)) {
            throw new IllegalStateException(String.format("Set up card set id (%s)",
                    "EXTRA_CARD_SET_ID"));
        }

        cardSetID = intent.getLongExtra(EXTRA_CARD_SET_ID, -1);

        updateListView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.card_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                onAddButtonClick(null);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void updateListView() {
        SQLiteDatabase db = new CardsDBHelper(this).getReadableDatabase();

        String[] columns = {Cards._ID, Cards.COLUMN_CARD_SET_ID, Cards.COLUMN_FRONT_SIDE,
                Cards.COLUMN_BACK_SIDE};
        String selection = String.format("%s = ?", Cards.COLUMN_CARD_SET_ID);

        Cursor cardsCursor = db.query(Cards.TABLE_NAME, columns, selection,
                new String[] {cardSetID.toString()}, null, null, null);
        SimpleCursorAdapter cardsAdapter = new SimpleCursorAdapter(this, R.layout.list_item_card,
                cardsCursor, new String[] {Cards._ID,
                Cards.COLUMN_CARD_SET_ID, Cards.COLUMN_FRONT_SIDE, Cards.COLUMN_BACK_SIDE},
                new int[] {R.id.tvID, R.id.tvCardSetID, R.id.tvFrontSide, R.id.tvBackSide}, 0);

        mCardsListView.setAdapter(cardsAdapter);
    }

    private void clearSelection() {
        if (mCardsListActionMode != null) {
            mCardsListActionMode.finish();
        }
    }

    public void onAddButtonClick(View view) {
        Intent intent = new Intent(this, CardDetailsActivity.class);
        intent.putExtra(CardDetailsActivity.EXTRA_CARD_ACTION,
                CardDetailsActivity.CARD_ACTION_CREATE_NEW);
        intent.putExtra(CardDetailsActivity.EXTRA_CARD_SET_ID, cardSetID);
        startActivityForResult(intent, REQUEST_CODE_CREATE_NEW_CARD);
    }

    public void editCheckedCards() {
        if (mCardsListView.getCheckedItemCount() > 0) {
            Intent intent = new Intent(this, CardDetailsActivity.class);
            intent.putExtra(CardDetailsActivity.EXTRA_CARD_ACTION,
                    CardDetailsActivity.CARD_ACTION_EDIT_EXISTING_ONE);
            intent.putExtra(CardDetailsActivity.EXTRA_CARD_ID,
                    mCardsListView.getCheckedItemIds()[0]);
            startActivityForResult(intent, REQUEST_CODE_EDIT_CARD);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CREATE_NEW_CARD) {
            if (resultCode == Activity.RESULT_OK) {
                updateListView();
            }
        } else if (requestCode == REQUEST_CODE_EDIT_CARD) {
            if (resultCode == Activity.RESULT_OK) {
                clearSelection();
                updateListView();
            }
        }
    }

    public void deleteCheckedCards() {
        if (mCardsListView.getCheckedItemCount() > 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            //builder.setTitle(R.string.alert_title_remove);
            builder.setMessage(String.format(
                    getString(R.string.alert_question_remove_card_template),
                    mCardsListView.getCheckedItemCount()));
            builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    for (Long cardId: mCardsListView.getCheckedItemIds()) {
                        SQLiteOpenHelper helper = new CardsDBHelper(CardListActivity.this);
                        SQLiteDatabase db = helper.getWritableDatabase();

                        DataBaseHelper.deleteItemById(db, Cards.TABLE_NAME, cardId);
                    }
                    clearSelection();
                    updateListView();
                }
            });
            builder.setNegativeButton(android.R.string.no, null);
            builder.show();
        }
    }

}
