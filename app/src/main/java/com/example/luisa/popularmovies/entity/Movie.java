package com.example.luisa.popularmovies.entity;

import com.example.luisa.popularmovies.core.BaseBusinessObject;
import com.example.luisa.popularmovies.core.DatabaseField;
import com.example.luisa.popularmovies.core.DatabaseTable;
import com.example.luisa.popularmovies.data.DBConstants;
import com.google.gson.annotations.SerializedName;


/**
 * Created by LuisA on 8/27/2015.
 */
@DatabaseTable(name = DBConstants.MOVIES_TABLE_NAME)
public class Movie extends BaseBusinessObject {

    @DatabaseField(name = DBConstants.MovieColumns.ORIGINAL_TITLE)
    @SerializedName(value = "original_title")
    private String originalTitle;

    @DatabaseField(name = DBConstants.MovieColumns.POPULARITY)
    @SerializedName(value = "popularity")
    private String popularity;

    @DatabaseField(name = DBConstants.MovieColumns.POSTER_PATH)
    @SerializedName(value = "poster_path")
    private String posterPath;

    @DatabaseField(name = DBConstants.MovieColumns.OVERVIEW)
    @SerializedName(value = "overview")
    private String overview;

    @DatabaseField(name = DBConstants.MovieColumns.VOTE_AVERAGE)
    @SerializedName(value = "vote_average")
    private float voteAverage;

    @DatabaseField(name = DBConstants.MovieColumns.RELEASE_DATE)
    @SerializedName(value = "release_date")
    private String releaseDate;



    public String getPopularity() {
        return popularity;
    }

    public void setPopularity(String popularity) {
        this.popularity = popularity;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public float getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(float voteAverage) {
        this.voteAverage = voteAverage;
    }

}
