package com.gmail.sgrimailo.utils.ui;

import android.view.ActionMode;
import android.view.Menu;
import android.widget.AbsListView;
import android.widget.ListView;

/**
 * Created by user on 13.11.2017.
 */

public abstract class AdjustedMultiChoiceModeListener implements AbsListView.MultiChoiceModeListener {

    private boolean moreThanOne = false;
    private boolean moreThanOneMenu = false;

    private final ListView mTargetListView;
    private final int mOneItemMenuRes;
    private final int mMoreThanOneMenuRes;

    public AdjustedMultiChoiceModeListener(ListView listView, int oneItemMenuRes,
                                           int moteThanOneMenuRes) {
        mTargetListView = listView;
        mOneItemMenuRes = oneItemMenuRes;
        mMoreThanOneMenuRes = moteThanOneMenuRes;
    }

    @Override
    public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
        if (moreThanOne) {
            if (mTargetListView.getCheckedItemCount() == 1) {
                moreThanOne = false;
                mode.invalidate();
            }
        } else {
            if (mTargetListView.getCheckedItemCount() > 1) {
                moreThanOne = true;
                mode.invalidate();
            }
        }
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        moreThanOne = mTargetListView.getCheckedItemCount() > 1;
        moreThanOneMenu = !moreThanOne;
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        if (moreThanOne) {
            if (!moreThanOneMenu) {
                menu.clear();
                mode.getMenuInflater().inflate(
                        mMoreThanOneMenuRes, menu);
                moreThanOneMenu = true;
            }
        } else {
            if (moreThanOneMenu) {
                menu.clear();
                mode.getMenuInflater().inflate(
                        mOneItemMenuRes, menu);
                moreThanOneMenu = false;
            }
        }
        return true;
    }
}
