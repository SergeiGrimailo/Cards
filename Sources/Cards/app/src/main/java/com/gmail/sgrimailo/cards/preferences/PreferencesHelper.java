package com.gmail.sgrimailo.cards.preferences;

import android.content.Context;
import android.content.SharedPreferences;

import com.gmail.sgrimailo.cards.R;

/**
 * Created by Sergey on 6/30/2017.
 */

public class PreferencesHelper {

    public static final String SHARED_UNSORTED_CATEGORY_ID = String.format("%s.%s",
            PreferencesHelper.class.getSimpleName(), "SHARED_UNSORTED_CATEGORY_ID");

    public static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(context.getString(R.string.shared_preferences_name),
                Context.MODE_PRIVATE);
    }

}
