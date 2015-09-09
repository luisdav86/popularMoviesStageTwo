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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.luisa.popularmovies.Manager.MovieManager;
import com.example.luisa.popularmovies.core.DataAccessObject;
import com.example.luisa.popularmovies.core.LogIt;
import com.example.luisa.popularmovies.data.DBConstants;
import com.example.luisa.popularmovies.data.MoviesContract;
import com.example.luisa.popularmovies.data.MoviesDbHelper;
import com.example.luisa.popularmovies.entity.Movie;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MovieDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    static final String DETAIL_URI = "URI";

    private Uri mUri;
    private Movie mMovie = null;
    private MovieManager mManager = MovieManager.getInstance();

    private TextView title;
    private ImageView thumbail;
    private TextView overview;
    private RatingBar average;
    private TextView releaseDate;
    private ListView trailers;
    private TrailerAdapter mTrailerAdapter;
    private Button favoriteButton;

    private static final int MOVIE_DETAIL_LOADER = 0;

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
        this.trailers = (ListView) view.findViewById(R.id.fragment_movie_detail_trailers);
        this.favoriteButton = (Button) view.findViewById(R.id.fragment_movie_detail_favorite_button);
        this.favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MovieDetailFragment.this.saveMoviePreferenceFavorite();
            }
        });

        mTrailerAdapter = new TrailerAdapter(getActivity(), null, 0);
        this.trailers.setAdapter(mTrailerAdapter);
        loadTrailers();
        return view;
    }

    private void saveMoviePreferenceFavorite() {
        mManager.changeMoviePreferenceFavorite(mMovie);
        this.loadFavoriteButton();
    }

    private void loadTrailers() {

        MoviesDbHelper mOpenHelper = new MoviesDbHelper(this.getActivity());
        mTrailerAdapter.swapCursor(mOpenHelper.getReadableDatabase().query(
                DBConstants.VIDEO_TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        ));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == MOVIE_DETAIL_LOADER) {
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
        }
        return null;
    }

    void onOrderMovieChanged() {
        Uri uri = mUri;
        if (null != uri) {
            long id = MoviesContract.MovieEntry.getIdFromUri(uri);
            Uri updatedUri = MoviesContract.MovieEntry.buildMovieWithId(id);
            mUri = updatedUri;
            getLoaderManager().restartLoader(MOVIE_DETAIL_LOADER, null, this);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == MOVIE_DETAIL_LOADER) {
            if (data != null && data.moveToFirst()) {
                try {
                    mMovie = DataAccessObject.mapItem(data, Movie.class);
                    this.loadMovieData();
                } catch (java.lang.InstantiationException e) {
                    LogIt.e(this, e, e.getMessage());
                } catch (IllegalAccessException e) {
                    LogIt.e(this, e, e.getMessage());
                }
            }
        }
    }

    private void loadMovieData() {
        if (mMovie != null) {
            this.loadFavoriteButton();
            this.title.setText(mMovie.getOriginalTitle());
            this.average.setVisibility(View.VISIBLE);
            this.average.setNumStars(5);
            this.average.setRating(mMovie.getVoteAverage() / 2F);
            this.overview.setText(mMovie.getOverview());
            this.releaseDate.setText(formateDateFromstring("yyyy-MM-dd", "dd, MMM yyyy", mMovie.getReleaseDate()));

            Picasso.with(this.getActivity()).load(getString(R.string.images_url) + getString(R.string.images_size) + mMovie.getPosterPath())
                    .fit()
                    .centerCrop()
                    .into(this.thumbail);
        }
    }

    private void loadFavoriteButton() {
        if (mMovie != null) {
            if (this.mMovie.isFavorite()) {
                this.favoriteButton.setBackgroundColor(getResources().getColor(R.color.sunshine_dark_blue));
            } else {
                this.favoriteButton.setBackgroundColor(getResources().getColor(R.color.sunshine_light_blue));
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
