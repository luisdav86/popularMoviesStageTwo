package com.example.luisa.popularmovies.data;

import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import com.example.luisa.popularmovies.core.AbstractContentProvider;
import com.example.luisa.popularmovies.core.AbstractContract;
import com.example.luisa.popularmovies.core.DataAccessObject;
import com.example.luisa.popularmovies.entity.Video;

/**
 * Created by Arcia on 9/11/2015.
 */
public class VideoProvider extends AbstractContentProvider {

    static final int VIDEOS_FROM_MOVIE = 101;

    @Override
    public AbstractContract getContract() {
        return new VideoContract();
    }

    @Override
    public Class<?> getProviderClass() {
        return Video.class;
    }

    public Cursor loadVideosFromMovie(Uri uri, String[] projection) {
        long id = VideoContract.VideoEntry.getIdFromUri(uri);
        return DataAccessObject.query(getProviderClass(),
                projection,
                VideoContract.VideoEntry.MOVIE_ID + " = ?",
                new String[]{Long.toString(id)},
                null,
                null,
                null
        );
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        switch (getUriMatcher().match(uri)) {
            case VIDEOS_FROM_MOVIE:
                return this.loadVideosFromMovie(uri, projection);
            default:
                return super.query(uri, projection, selection, selectionArgs, sortOrder);
        }
    }

    @Override
    protected UriMatcher onBuildUriMatcher(UriMatcher matcher) {
        super.onBuildUriMatcher(matcher);
        matcher.addURI(getContract().getContentAuthority(), MoviesContract.BASE_PATH + "/*", VIDEOS_FROM_MOVIE);
        return matcher;
    }
}
