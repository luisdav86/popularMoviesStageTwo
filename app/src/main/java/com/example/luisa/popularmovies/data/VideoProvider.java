package com.example.luisa.popularmovies.data;

import com.example.luisa.popularmovies.entity.Video;

/**
 * Created by Arcia on 9/11/2015.
 */
public class VideoProvider extends AbstractContentProvider {

    @Override
    public AbstractContract getContract() {
        return new VideoContract();
    }

    @Override
    public Class<?> getProviderClass() {
        return Video.class;
    }
}
