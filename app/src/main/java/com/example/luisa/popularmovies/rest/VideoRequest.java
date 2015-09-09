package com.example.luisa.popularmovies.rest;

import com.example.luisa.popularmovies.entity.Video;

import java.util.List;

/**
 * Created by LuisA on 9/8/2015.
 */
public class VideoRequest {

    private List<Video> results;

    public List<Video> getResults() {
        return results;
    }

    public void setResults(List<Video> results) {
        this.results = results;
    }
}
