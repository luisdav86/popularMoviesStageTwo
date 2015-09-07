package com.example.luisa.popularmovies.entity;

import com.example.luisa.popularmovies.core.BaseBusinessObject;
import com.example.luisa.popularmovies.core.DatabaseField;
import com.example.luisa.popularmovies.core.DatabaseTable;
import com.example.luisa.popularmovies.data.DBConstants;

/**
 * Created by LuisA on 8/31/2015.
 */
@DatabaseTable(name = DBConstants.MOVIES_TABLE_NAME)
public class Review extends BaseBusinessObject {

    @DatabaseField(name = DBConstants.ReviewColumns.MOVIE_ID)
    private String movieId;
    @DatabaseField(name = DBConstants.ReviewColumns.AUTHOR)
    private String author;
    @DatabaseField(name = DBConstants.ReviewColumns.CONTENT)
    private String content;

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
}
