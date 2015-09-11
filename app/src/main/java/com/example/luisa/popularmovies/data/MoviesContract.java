package com.example.luisa.popularmovies.data;

import android.net.Uri;

import com.example.luisa.popularmovies.core.AbstractContract;

/**
 * Created by Arcia on 9/11/2015.
 */
public class MoviesContract extends AbstractContract {

    @Override
    public String getContentAuthority() {
        return "com.example.luisa.popularmovies";
    }

    public static final class MovieEntry implements DBConstants.MovieColumns {

        public static long getIdFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }
    }
}
