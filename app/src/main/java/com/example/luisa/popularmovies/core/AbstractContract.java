package com.example.luisa.popularmovies.core;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Arcia on 9/11/2015.
 */
public abstract class AbstractContract {

    public abstract String getContentAuthority();

    public final Uri BASE_CONTENT_URI = Uri.parse("content://" + getContentAuthority());

    public static final String BASE_PATH = "base";

    public ProviderColumns getBaseEntry() {
        return new ProviderColumns();
    }

    public class ProviderColumns implements BaseColumns {

        public final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(BASE_PATH).build();

        public final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + getContentAuthority() + "/" + BASE_PATH;

        public final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + getContentAuthority() + "/" + BASE_PATH;

        public Uri buildIdUri(long id) {
            return CONTENT_URI.buildUpon().appendPath(Long.toString(id)).build();
        }

        public Uri buildMovieWithId(Object id) {
            return CONTENT_URI.buildUpon().appendPath(String.valueOf(id)).build();
        }
    }
}
