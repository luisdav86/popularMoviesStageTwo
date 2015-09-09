package com.example.luisa.popularmovies.rest;

import com.example.luisa.popularmovies.entity.Review;

import java.util.List;

/**
 * Created by LuisA on 9/8/2015.
 */
public class ReviewRequest {

    private List<Review> results;

    public List<Review> getResults() {
        return results;
    }

    public void setResults(List<Review> results) {
        this.results = results;
    }
}
