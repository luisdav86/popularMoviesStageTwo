package com.example.luisa.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Arcia on 8/30/2015.
 */
public class Utility {

    public static String getPreferredLocation(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_movie_key),
                context.getString(R.string.pref_movie_popular));
    }

    public static Set<String>  getPreferredFavoriteMovies(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getStringSet(context.getString(R.string.pref_favorites_movies_key),
                new HashSet<String>());
    }

    public static void savePreferredFavoriteMovie(Context context, Set<String> favoriteMovies) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putStringSet(context.getString(R.string.pref_favorites_movies_key), favoriteMovies);
        editor.commit();
    }
}
