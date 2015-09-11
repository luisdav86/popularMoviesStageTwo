package com.example.luisa.popularmovies.data;

import android.content.UriMatcher;

import com.example.luisa.popularmovies.entity.Review;

/**
 * Created by Arcia on 9/11/2015.
 */
public class ReviewProvider extends AbstractContentProvider {

    @Override
    public AbstractContract getContract() {
        return new ReviewContract();
    }

    @Override
    public Class<?> getProviderClass() {
        return Review.class;
    }
}
