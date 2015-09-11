package com.example.luisa.popularmovies.data;

import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import com.example.luisa.popularmovies.core.AbstractContentProvider;
import com.example.luisa.popularmovies.core.AbstractContract;
import com.example.luisa.popularmovies.core.DataAccessObject;
import com.example.luisa.popularmovies.entity.Review;

/**
 * Created by Arcia on 9/11/2015.
 */
public class ReviewProvider extends AbstractContentProvider {

    static final int REVIEW_FROM_MOVIE = 101;

    @Override
    public AbstractContract getContract() {
        return new ReviewContract();
    }

    @Override
    public Class<?> getProviderClass() {
        return Review.class;
    }

    public Cursor loadReviewFromMovie(Uri uri, String[] projection) {
        long id = ReviewContract.ReviewEntry.getIdFromUri(uri);
        return DataAccessObject.query(getProviderClass(),
                projection,
                ReviewContract.ReviewEntry.MOVIE_ID + " = ?",
                new String[]{Long.toString(id)},
                null,
                null,
                null
        );
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        switch (getUriMatcher().match(uri)) {
            case REVIEW_FROM_MOVIE:
                return this.loadReviewFromMovie(uri, projection);
            default:
                return super.query(uri, projection, selection, selectionArgs, sortOrder);
        }
    }

    @Override
    protected UriMatcher onBuildUriMatcher(UriMatcher matcher) {
        super.onBuildUriMatcher(matcher);
        matcher.addURI(getContract().getContentAuthority(), MoviesContract.BASE_PATH + "/*", REVIEW_FROM_MOVIE);
        return matcher;
    }
}
