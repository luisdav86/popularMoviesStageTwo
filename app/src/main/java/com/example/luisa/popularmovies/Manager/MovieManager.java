package com.example.luisa.popularmovies.Manager;

import com.example.luisa.popularmovies.entity.Movie;

/**
 * Created by LuisA on 9/9/2015.
 */
public class MovieManager {

    private static MovieManager instance;

    private MovieManager() {

    }

    public static MovieManager getInstance() {
        if (instance == null) {
            instance = new MovieManager();
        }
        return instance;
    }

    public boolean changeMoviePreferenceFavorite(Movie movie) {
        try {
            movie.setFavorite(!movie.isFavorite());
            return movie.update() > 0;
        }catch (Exception e){
            return false;
        }
    }

}
