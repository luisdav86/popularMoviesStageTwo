package com.example.luisa.popularmovies.data;

import android.provider.BaseColumns;

/**
 * Created by LuisA on 8/28/2015.
 */
public class DBConstants {

    public static final String MOVIES_TABLE_NAME = "movies";

    public static final String VIDEO_TABLE_NAME = "video";

    public static final String REVIEW_TABLE_NAME = "review";

    public interface VideoColumns extends BaseColumns {
        String MOVIE_ID = "movie_id";
        String ISO = "iso";
        String KEY = "key";
        String NAME = "name";
        String SITE = "site";
        String SIZE = "size";
        String TYPE = "type";
    }

    public interface ReviewColumns extends BaseColumns {
        String MOVIE_ID = "movie_id";
        String AUTHOR = "author";
        String CONTENT = "content";
    }

    public interface MovieColumns extends BaseColumns {
        String POPULARITY = "popularity";
        String ORIGINAL_TITLE = "original_title";
        String POSTER_PATH = "poster_path";
        String OVERVIEW = "overview";
        String VOTE_AVERAGE = "vote_average";
        String RELEASE_DATE = "release_date";
    }
}
