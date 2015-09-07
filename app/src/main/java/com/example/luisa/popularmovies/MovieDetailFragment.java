package com.example.luisa.popularmovies;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.luisa.popularmovies.core.DataAccessObject;
import com.example.luisa.popularmovies.core.LogIt;
import com.example.luisa.popularmovies.data.MoviesContract;
import com.example.luisa.popularmovies.entity.Movie;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MovieDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    static final String DETAIL_URI = "URI";

    private Uri mUri;

    private TextView title;
    private ImageView thumbail;
    private TextView overview;
    private RatingBar average;
    private TextView releaseDate;

    private static final int DETAIL_LOADER = 0;

    public MovieDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Bundle arguments = getArguments();
            if (arguments != null) {
                mUri = arguments.getParcelable(MovieDetailFragment.DETAIL_URI);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        this.title = (TextView) view.findViewById(R.id.fragment_movie_detail_original_title);
        this.thumbail = (ImageView) view.findViewById(R.id.fragment_movie_detail_thumbail);
        this.overview = (TextView) view.findViewById(R.id.fragment_movie_detail_overview);
        this.average = (RatingBar) view.findViewById(R.id.fragment_movie_detail_ratingBar);
        this.releaseDate = (TextView) view.findViewById(R.id.fragment_movie_detail_release_date);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (null != mUri) {
            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    null,
                    null,
                    null,
                    null
            );
        }
        return null;
    }

    void onOrderMovieChanged() {
        Uri uri = mUri;
        if (null != uri) {
            long id = MoviesContract.MovieEntry.getIdFromUri(uri);
            Uri updatedUri = MoviesContract.MovieEntry.buildMovieWithId(id);
            mUri = updatedUri;
            getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            try {
                Movie movie = DataAccessObject.mapItem(data, Movie.class);
                this.title.setText(movie.getOriginalTitle());
                this.average.setVisibility(View.VISIBLE);
                this.average.setNumStars(5);
                this.average.setRating(movie.getVoteAverage() / 2F);
                this.overview.setText(movie.getOverview());
                this.releaseDate.setText(formateDateFromstring("yyyy-MM-dd", "dd, MMM yyyy", movie.getReleaseDate()));

                Picasso.with(this.getActivity()).load(getString(R.string.images_url) + getString(R.string.images_size) + movie.getPosterPath())
                        .fit()
                        .centerCrop()
                        .into(this.thumbail);
            } catch (java.lang.InstantiationException e) {
                LogIt.e(this, e, e.getMessage());
            } catch (IllegalAccessException e) {
                LogIt.e(this, e, e.getMessage());
            }
        }
    }

    public static String formateDateFromstring(String inputFormat, String outputFormat, String inputDate) {

        Date parsed = null;
        String outputDate = "";

        SimpleDateFormat df_input = new SimpleDateFormat(inputFormat, java.util.Locale.getDefault());
        SimpleDateFormat df_output = new SimpleDateFormat(outputFormat, java.util.Locale.getDefault());

        try {
            parsed = df_input.parse(inputDate);
            outputDate = df_output.format(parsed);

        } catch (ParseException e) {
            LogIt.e(e, e, e.getMessage());
        }

        return outputDate;

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
