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

        //http://api.themoviedb.org/3/movie/44947/reviews??api_key=1a6321ee87822a379dbf9f8f2c37107e
        //http://api.themoviedb.org/3/movie/44947/reviews?&api_key=1a6321ee87822a379dbf9f8f2c37107e
        @GET(API_REVIEW_FIELDS)
        Object getReviews(@Path("id") String id, @Query("api_key") String apyKey);

        //http://api.themoviedb.org/3/movie/44947/videos?&api_key=1a6321ee87822a379dbf9f8f2c37107e
        @GET(API_VIDEOS_FIELDS)
        Object getVideos(@Path("id") String id, @Query("api_key") String apyKey);
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

    public static Object getRelatedReviews(int id, String apiKey) {
        return createRestAdapter().getReviews(Integer.toString(id), apiKey);
    }

    public static Object getRelatedVideos(int id, String apiKey) {
        return createRestAdapter().getVideos(Integer.toString(id), apiKey);
    }
}
