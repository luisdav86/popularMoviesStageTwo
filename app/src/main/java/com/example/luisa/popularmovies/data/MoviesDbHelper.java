package com.example.luisa.popularmovies.data;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.luisa.popularmovies.core.DatabaseUtil;
import com.example.luisa.popularmovies.core.ICoreDb;
import com.example.luisa.popularmovies.entity.Movie;
import com.example.luisa.popularmovies.entity.Review;
import com.example.luisa.popularmovies.entity.Video;

/**
 * Created by LuisA on 8/28/2015.
 */

public class MoviesDbHelper extends SQLiteOpenHelper implements ICoreDb {

    private static final int DATABASE_VERSION = 2;

    static final String DATABASE_NAME = "movies.db";

    protected SQLiteDatabase db;

    public MoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        DatabaseUtil.createTable(Movie.class, db);
        DatabaseUtil.createTable(Video.class, db);
        DatabaseUtil.createTable(Review.class, db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion < 2) {
          //  DatabaseUtil.createTable(Video.class, db);
           // DatabaseUtil.createTable(Review.class, db);
        }
        db.setVersion(DATABASE_VERSION);
    }

    @Override
    public SQLiteDatabase openDataBase() throws SQLException {
        return openDataBase(SQLiteDatabase.OPEN_READWRITE);
    }

    public SQLiteDatabase openDataBase(int mode) throws SQLException {
        if (db != null && db.isOpen()) {
            return db;
        }
        db = getWritableDatabase();
        return db;
    }
}
