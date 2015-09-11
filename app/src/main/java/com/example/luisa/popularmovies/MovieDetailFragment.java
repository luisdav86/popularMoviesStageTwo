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
import com.example.luisa.popularmovies.data.MoviesContract;
import com.example.luisa.popularmovies.data.ReviewContract;
import com.example.luisa.popularmovies.data.VideoContract;
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
    private ListView reviews;
    private VideoAdapter mVideoAdapter;
    private ReviewAdapter mReviewAdapter;
    private Button favoriteButton;

    private static final int MOVIE_DETAIL_LOADER = 0;
    private static final int MOVIE_REVIEW_LOADER = 1;
    private static final int MOVIE_VIDEO_LOADER = 2;

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
        this.reviews = (ListView) view.findViewById(R.id.fragment_movie_detail_review);
        this.favoriteButton = (Button) view.findViewById(R.id.fragment_movie_detail_favorite_button);
        this.favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MovieDetailFragment.this.saveMoviePreferenceFavorite();
            }
        });

        mVideoAdapter = new VideoAdapter(getActivity(), null, 0);
        mReviewAdapter = new ReviewAdapter(getActivity(), null, 0);
        this.trailers.setAdapter(mVideoAdapter);
        this.reviews.setAdapter(mReviewAdapter);
        return view;
    }

    private void saveMoviePreferenceFavorite() {
        mManager.changeMoviePreferenceFavorite(mMovie);
        this.loadFavoriteButton();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_VIDEO_LOADER, null, this);
        getLoaderManager().initLoader(MOVIE_DETAIL_LOADER, null, this);
        getLoaderManager().initLoader(MOVIE_REVIEW_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (null != mUri) {
            switch (id) {
                case MOVIE_DETAIL_LOADER:
                    // Now create and return a CursorLoader that will take care of
                    // creating a Cursor for the data being displayed.
                    return createMoviesCursorLoader();
                case MOVIE_VIDEO_LOADER:
                    return createVideoCursorLoader();
                case MOVIE_REVIEW_LOADER:
                    return createReviewCursorLoader();
                default:
                    return null;
            }
        }
        return null;
    }

    private Loader<Cursor> createReviewCursorLoader() {
        Uri uri = new ReviewContract().getBaseEntry().buildIdUri(MoviesContract.MovieEntry.getIdFromUri(mUri));
        return new CursorLoader(
                getActivity(),
                uri,
                null,
                null,
                null,
                null
        );
    }

    private Loader<Cursor> createVideoCursorLoader() {
        Uri uri = new VideoContract().getBaseEntry().buildIdUri(MoviesContract.MovieEntry.getIdFromUri(mUri));
        return new CursorLoader(
                getActivity(),
                uri,
                null,
                null,
                null,
                null
        );
    }

    private Loader<Cursor> createMoviesCursorLoader() {
        return new CursorLoader(
                getActivity(),
                mUri,
                null,
                null,
                null,
                null
        );
    }

    void onOrderMovieChanged() {
        Uri uri = mUri;
        if (null != uri) {
            long id = MoviesContract.MovieEntry.getIdFromUri(uri);
            Uri updatedUri = new MoviesContract().getBaseEntry().buildMovieWithId(id);
            mUri = updatedUri;
            getLoaderManager().restartLoader(MOVIE_DETAIL_LOADER, null, this);
            getLoaderManager().restartLoader(MOVIE_VIDEO_LOADER, null, this);
            getLoaderManager().restartLoader(MOVIE_REVIEW_LOADER, null, this);
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
        } else if (loader.getId() == MOVIE_VIDEO_LOADER) {
            mVideoAdapter.swapCursor(data);
        } else if (loader.getId() == MOVIE_REVIEW_LOADER) {
            mReviewAdapter.swapCursor(data);
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
