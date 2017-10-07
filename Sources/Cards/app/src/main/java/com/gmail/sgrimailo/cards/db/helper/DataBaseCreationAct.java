package com.gmail.sgrimailo.cards.db.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Sergey on 6/7/2017.
 */

interface DataBaseCreationAct {
    void doAction(SQLiteDatabase db, Context context);
}
