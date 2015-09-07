package com.example.luisa.popularmovies;

import android.app.Application;

import com.example.luisa.popularmovies.data.MoviesDbHelper;

/**
 * Created by LuisA on 8/28/2015.
 */
public class MoviesApp extends Application {

    private static MoviesApp instance;

    private MoviesDbHelper helper;

    public MoviesDbHelper getHelper() {
        return helper;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        this.helper = new MoviesDbHelper(this);
    }

    public static MoviesApp getInstance() {
        return instance;
    }
}
