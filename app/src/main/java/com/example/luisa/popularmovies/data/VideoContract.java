package com.example.luisa.popularmovies.data;

import android.net.Uri;

import com.example.luisa.popularmovies.core.AbstractContract;

/**
 * Created by Arcia on 9/11/2015.
 */
public class VideoContract extends AbstractContract {

    @Override
    public String getContentAuthority() {
        return "com.example.luisa.popularmovies.video";
    }

    public static final class VideoEntry implements DBConstants.VideoColumns {

        public static long getIdFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }
    }


}
