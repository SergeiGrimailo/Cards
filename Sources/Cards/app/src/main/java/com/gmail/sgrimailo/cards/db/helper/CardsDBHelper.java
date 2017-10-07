package com.gmail.sgrimailo.cards.db.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.gmail.sgrimailo.cards.db.CardsContract;

/**
 * Created by Sergey on 6/6/2017.
 */

public class CardsDBHelper extends SQLiteOpenHelper {

    private static final String DATA_BASE_NAME = "Cards.db";
    private static final int DATA_BASE_VERSION = 4;

    private final Context mContext;

    public CardsDBHelper(Context context) {
        super(context, DATA_BASE_NAME, null, DATA_BASE_VERSION);
        mContext = context;
    }

    @Override
    public SQLiteDatabase getReadableDatabase() {
        Log.d(CardsContract.LOG_TAG, "getReadableDatabase()");
        return super.getReadableDatabase();
    }

    @Override
    public SQLiteDatabase getWritableDatabase() {
        Log.d(CardsContract.LOG_TAG, "getWritableDatabase()");
        return super.getWritableDatabase();
    }

    protected DataBaseCreationAct[] getActions() throws ClassNotFoundException,
            IllegalAccessException, InstantiationException {

        DataBaseCreationAct[] acts = new DataBaseCreationAct[DATA_BASE_VERSION];
        String currentPackageName = getClass().getPackage().getName();
        for (int i = 0; i < DATA_BASE_VERSION; i++) {
            Class<? extends DataBaseCreationAct> actClass = (Class<? extends DataBaseCreationAct>)
                    Class.forName(String.format("%s.%s%d", currentPackageName,
                            DataBaseCreationAct.class.getSimpleName(), i + 1));
            acts[i] = actClass.newInstance();
        }
        return acts;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(CardsContract.LOG_TAG, "Data base creation.");
        DataBaseCreationAct[] actions;
        try {
            actions = getActions();
        } catch (Exception e) {
            throw new RuntimeException("Couldn't create data base.", e);
        }
        for (int i = 0; i < DATA_BASE_VERSION; i++) {
            actions[i].doAction(db, mContext);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(CardsContract.LOG_TAG, String.format(
                "Data base upgrade from version: %d to version: %d", oldVersion, newVersion));
        DataBaseCreationAct[] actions;
        try {
            actions = getActions();
        } catch (Exception e) {
            throw new RuntimeException(
                    String.format("Couldn't upgrade data base from version: %d to version: %d.",
                            oldVersion, newVersion), e);
        }
        for (int i = oldVersion; i < newVersion; i++) {
            actions[i].doAction(db, mContext);
        }
    }
}
