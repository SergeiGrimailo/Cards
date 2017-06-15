package com.gmail.sgrimailo.cards;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * A simple {@link Fragment} subclass.
 */
public class CardsPlayerFragment extends AppCompatDialogFragment {

    private boolean mShowingBack = true;

    public CardsPlayerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_cards_player, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        flipCard();

        FrameLayout panelCardsHolder = (FrameLayout) getView().findViewById(R.id.panelCardsHolder);
        panelCardsHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flipCard();
            }
        });

    }

    private void flipCard() {

        View front = getView().findViewById(R.id.tvFront);
        View back = getView().findViewById(R.id.tvBack);

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
