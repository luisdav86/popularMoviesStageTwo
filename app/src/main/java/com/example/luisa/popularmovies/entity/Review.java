package com.example.luisa.popularmovies.entity;

import android.provider.BaseColumns;

import com.example.luisa.popularmovies.core.DataAccessObject;
import com.example.luisa.popularmovies.core.DatabaseField;
import com.example.luisa.popularmovies.core.DatabaseTable;
import com.example.luisa.popularmovies.data.DBConstants;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by LuisA on 8/31/2015.
 */
@DatabaseTable(name = DBConstants.REVIEW_TABLE_NAME)
public class Review extends DataAccessObject<String> {

    @DatabaseField(name = BaseColumns._ID, primaryKey = true)
    @SerializedName("id")
    @Expose
    protected String id = "";

    @DatabaseField(name = DBConstants.ReviewColumns.MOVIE_ID)
    private String movieId;
    @DatabaseField(name = DBConstants.ReviewColumns.AUTHOR)
    private String author;
    @DatabaseField(name = DBConstants.ReviewColumns.CONTENT)
    private String content;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String getPrimaryKey() {
        return this.id;
    }
}
