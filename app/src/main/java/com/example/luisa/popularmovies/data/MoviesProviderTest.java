package com.example.luisa.popularmovies.data;

import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import com.example.luisa.popularmovies.core.DataAccessObject;
import com.example.luisa.popularmovies.entity.Movie;

/**
 * Created by Arcia on 9/11/2015.
 */
public class MoviesProviderTest extends AbstractContentProvider {

    static final int MOVIE_WITH_ID = 101;

    @Override
    public AbstractContract getContract() {
        return new MoviesContracTest();
    }

    @Override
    public Class<?> getProviderClass() {
        return Movie.class;
    }

    @Override
    protected UriMatcher onBuildUriMatcher(UriMatcher matcher) {
        super.onBuildUriMatcher(matcher);
        matcher.addURI(getContract().getContentAuthority(), MoviesContract.PATH_MOVIES + "/*", MOVIE_WITH_ID);
        return matcher;
    }

    private Cursor getMovieById(
            Uri uri, String[] projection) {
        long id = MoviesContract.MovieEntry.getIdFromUri(uri);
        return DataAccessObject.query(getProviderClass(),
                projection,
                "_ID = ?",
                new String[]{Long.toString(id)},
                null,
                null,
                null
        );
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        switch (getUriMatcher().match(uri)) {
            case MOVIE_WITH_ID:
                return getMovieById(uri, null);
            default:
                return super.query(uri, projection, selection, selectionArgs, sortOrder);
        }
    }

    @Override
    public String getType(Uri uri) {
        final int match = getUriMatcher().match(uri);
        switch (match) {
            case MOVIE_WITH_ID:
                return MoviesContract.MovieEntry.CONTENT_ITEM_TYPE;
            default:
                return super.getType(uri);
        }
    }
}
