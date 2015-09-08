package com.example.luisa.popularmovies.rest;

import retrofit.RestAdapter;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;


/**
 * Created by LuisA on 8/27/2015.
 */
public class MovieRestService {

    private static final String PUPULARITY_ORDER = "popularity.desc";
    private static final String AVERAGE_ORDER = "vote_average.desc";

    private static final String API_URL = "http://api.themoviedb.org";
    private static final String API_FIELDS = "/3/discover/movie?";

    private static final String API_REVIEW_FIELDS = "/3/movie/{id}/reviews";
    private static final String API_VIDEOS_FIELDS = "/3/movie/{id}/videos";


    private static interface IMovies {
        @GET(API_FIELDS)
        MovieRequest getMovies(@Query("sort_by") String sortBy, @Query("api_key") String apyKey);

        @GET(API_REVIEW_FIELDS)
        ReviewRequest getReviews(@Path("id") String id, @Query("api_key") String apyKey);

        @GET(API_VIDEOS_FIELDS)
        VideoRequest getVideos(@Path("id") String id, @Query("api_key") String apyKey);
    }

    public static IMovies createRestAdapter() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(API_URL)
                .build();
        return restAdapter.create(IMovies.class);
    }

    public static MovieRequest getMovies(String sortCriteria, String apiKey) {
        if (!sortCriteria.equals(AVERAGE_ORDER)) {
            sortCriteria = PUPULARITY_ORDER;
        }
        return createRestAdapter().getMovies(sortCriteria, apiKey);
    }

    public static ReviewRequest getRelatedReviews(long id, String apiKey) {
        return createRestAdapter().getReviews(Long.toString(id), apiKey);
    }

    public static VideoRequest getRelatedVideos(long id, String apiKey) {
        return createRestAdapter().getVideos(Long.toString(id), apiKey);
    }
}
