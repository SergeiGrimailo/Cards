package com.gmail.sgrimailo.cards;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.gmail.sgrimailo.cards.db.CardsContract.Cards;
import com.gmail.sgrimailo.cards.db.helper.CardsDBHelper;
import com.gmail.sgrimailo.utils.db.DataBaseHelper;

/**
 * A simple {@link Fragment} subclass.
 */
public class CardsPlayerFragment extends AppCompatDialogFragment {

    public static final String ARG_CARD_ID = String.format("%s.%s",
            CardsPlayerFragment.class.getName(), "ARG_CARD_ID");

    private boolean mShowingBack = true;

    public CardsPlayerFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_cards_player, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Long cardID = getArguments().getLong(ARG_CARD_ID);

        SQLiteDatabase db = new CardsDBHelper(getContext()).getReadableDatabase();

        Cursor cardCursor = DataBaseHelper.getItemByID(db, Cards.TABLE_NAME,
                new String[]{Cards.COLUMN_FRONT_SIDE, Cards.COLUMN_BACK_SIDE}, cardID);
        cardCursor.moveToFirst();

        TextView tvFrontSide = (TextView) getView().findViewById(R.id.tvFrontSideContent);
        tvFrontSide.setText(cardCursor.getString(cardCursor.getColumnIndex(Cards.COLUMN_FRONT_SIDE)));

        TextView tvBackSide = (TextView) getView().findViewById(R.id.tvBackSideContent);
        tvBackSide.setText(cardCursor.getString(cardCursor.getColumnIndex(Cards.COLUMN_BACK_SIDE)));

        flipCard();

        FrameLayout panelCardsHolder = (FrameLayout) getView().findViewById(R.id.lCardsHolder);
        panelCardsHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flipCard();
            }
        });

    }

    private void flipCard() {

        View front = getView().findViewById(R.id.lFrontSide);
        View back = getView().findViewById(R.id.lBackSide);

        if (mShowingBack) {
            front.setVisibility(View.VISIBLE);
            back.setVisibility(View.INVISIBLE);
        } else {
            front.setVisibility(View.INVISIBLE);
            back.setVisibility(View.VISIBLE);
        }

        mShowingBack = !mShowingBack;
    }

}
