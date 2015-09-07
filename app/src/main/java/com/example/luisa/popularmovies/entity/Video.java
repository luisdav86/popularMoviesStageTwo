package com.example.luisa.popularmovies.entity;

import com.example.luisa.popularmovies.core.BaseBusinessObject;
import com.example.luisa.popularmovies.core.DatabaseField;
import com.example.luisa.popularmovies.core.DatabaseTable;
import com.example.luisa.popularmovies.data.DBConstants;

/**
 * Created by LuisA on 8/31/2015.
 */
@DatabaseTable(name = DBConstants.MOVIES_TABLE_NAME)
public class Video extends BaseBusinessObject {

    @DatabaseField(name = DBConstants.VideoColumns.MOVIE_ID)
    private String movieId;

    @DatabaseField(name = DBConstants.VideoColumns.KEY)
    private String key;

    @DatabaseField(name = DBConstants.VideoColumns.NAME)
    private String name;

    @DatabaseField(name = DBConstants.VideoColumns.SITE)
    private String site;

    @DatabaseField(name = DBConstants.VideoColumns.SIZE)
    private String size;

    @DatabaseField(name = DBConstants.VideoColumns.TYPE)
    private String type;

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;

    }
}
