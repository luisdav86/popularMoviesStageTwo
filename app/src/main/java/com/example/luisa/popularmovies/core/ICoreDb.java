package com.example.luisa.popularmovies.core;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by LuisA on 8/28/2015.
 */
public interface ICoreDb {

    SQLiteDatabase openDataBase() throws SQLException;

    SQLiteDatabase openDataBase(int mode) throws SQLException;

    void close();
}
