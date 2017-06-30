package com.gmail.sgrimailo.cards;


import android.animation.Animator;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
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

    private static final int ANIMATION_DURATION = 1000;

    private boolean mShowingBack = true;
    private FrameLayout mCardSidesHolder;

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

        Cursor cardCursor = DataBaseHelper.getItemById(db, Cards.TABLE_NAME,
                new String[]{Cards.COLUMN_FRONT_SIDE, Cards.COLUMN_BACK_SIDE}, cardID);
        cardCursor.moveToFirst();

        TextView tvFrontSide = (TextView) getView().findViewById(R.id.tvFrontSideContent);
        tvFrontSide.setText(cardCursor.getString(cardCursor.getColumnIndex(Cards.COLUMN_FRONT_SIDE)));

        TextView tvBackSide = (TextView) getView().findViewById(R.id.tvBackSideContent);
        tvBackSide.setText(cardCursor.getString(cardCursor.getColumnIndex(Cards.COLUMN_BACK_SIDE)));

        flipCardSides();

        mCardSidesHolder = (FrameLayout) getView().findViewById(R.id.lCardSidesHolder);
        mCardSidesHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flipCardSidesAnimated();
            }
        });
    }

    private void flipCardSides() {

        final View front = getView().findViewById(R.id.lFrontSide);
        final View back = getView().findViewById(R.id.lBackSide);

        if (mShowingBack) {
            back.setVisibility(View.INVISIBLE);
            front.setVisibility(View.VISIBLE);
        } else {
            front.setVisibility(View.INVISIBLE);
            back.setVisibility(View.VISIBLE);
        }

        mShowingBack = !mShowingBack;
    }

    private void flipCardSidesAnimated() {
        mCardSidesHolder.animate().rotationXBy(90f).setDuration(ANIMATION_DURATION)
                .setInterpolator(new AccelerateInterpolator()).setListener(
                new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        flipCardSides();
                        mCardSidesHolder.setRotationX(-90f);
                        mCardSidesHolder.animate().setInterpolator(new DecelerateInterpolator()).setListener(null);
                        mCardSidesHolder.animate().rotationXBy(90f).setDuration(ANIMATION_DURATION).start();
                    }
                }
        ).start();
    }

    class AnimatorListenerAdapter implements Animator.AnimatorListener {
        @Override
        public void onAnimationStart(Animator animation) {
        }

        @Override
        public void onAnimationEnd(Animator animation) {
        }

        @Override
        public void onAnimationCancel(Animator animation) {
        }

        @Override
        public void onAnimationRepeat(Animator animation) {
        }
    }

}
