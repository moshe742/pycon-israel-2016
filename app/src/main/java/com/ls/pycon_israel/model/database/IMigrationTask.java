package com.ls.pycon_israel.model.database;

import android.database.sqlite.SQLiteDatabase;

public interface IMigrationTask {
    void onUpgrade(SQLiteDatabase theDb, int oldVersion, int newVersion);
}
