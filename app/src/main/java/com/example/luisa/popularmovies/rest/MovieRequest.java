package com.example.luisa.popularmovies.rest;

import com.example.luisa.popularmovies.entity.Movie;

import java.util.List;

/**
 * Created by LuisA on 8/27/2015.
 */
public class MovieRequest {

    private List<Movie> results;

    public List<Movie> getResults() {
        return results;
    }

    public void setResults(List<Movie> results) {
        this.results = results;
    }
}
