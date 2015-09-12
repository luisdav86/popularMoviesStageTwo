package com.example.luisa.popularmovies;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
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
import com.example.luisa.popularmovies.entity.Video;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MovieDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    static final String DETAIL_URI = "URI";
    static final int VIDEO_TO_SHARE = 0;

    private Uri mUri;
    private Movie mMovie = null;
    private MovieManager mManager = MovieManager.getInstance();
    private ShareActionProvider mShareActionProvider;

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
        setHasOptionsMenu(true);
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

        this.reviews.setOnTouchListener(this.onListViewTouched);
        this.trailers.setOnTouchListener(this.onListViewTouched);
        setListViewHeightBasedOnChildren(this.reviews);
        setListViewHeightBasedOnChildren(this.trailers);
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
        this.trailers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MovieDetailFragment.this.openVideoOnIntent(position);
            }
        });
        this.reviews.setAdapter(mReviewAdapter);
        return view;
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, LinearLayout.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    private View.OnTouchListener onListViewTouched = new View.OnTouchListener() {
        // Setting on Touch Listener for handling the touch inside ScrollView
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // Disallow the touch request for parent scroll on touch of child view
            v.getParent().requestDisallowInterceptTouchEvent(true);
            return false;
        }
    };

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
            if (mShareActionProvider != null) {
                mShareActionProvider.setShareIntent(createShareVideoIntent(getVideoByPosition(VIDEO_TO_SHARE)));
            }
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
            if (!mMovie.hasLocalImage()) {
                Picasso.with(this.getActivity()).load(getString(R.string.images_url) + getString(R.string.images_size) + mMovie.getPosterPath())
                        .fit()
                        .centerCrop()
                        .into(this.thumbail);
            } else {
                Picasso.with(this.getActivity()).load(mMovie.getLocalFileImage())
                        .fit()
                        .centerCrop()
                        .into(this.thumbail);

            }
        }
    }

    private void loadFavoriteButton() {
        if (mMovie != null) {
            if (this.mMovie.isFavorite()) {
                if (!this.mMovie.hasLocalImage()) {
                    BitmapDrawable bitmapDrawable = (BitmapDrawable) this.thumbail.getDrawable();
                    if (bitmapDrawable != null) {
                        Bitmap bitmap = bitmapDrawable.getBitmap();
                        mMovie.setLocalImagePath(Utility.saveToInternalStorage(getActivity(), bitmap, this.mMovie));
                        mMovie.update();
                    }
                }
                this.favoriteButton.setBackgroundColor(getResources().getColor(R.color.sunshine_dark_blue));
                this.favoriteButton.setText(R.string.fragment_movie_detail_favorite_button_selected);
            } else {
                this.favoriteButton.setBackgroundColor(getResources().getColor(R.color.sunshine_light_blue));
                this.favoriteButton.setText(R.string.fragment_movie_detail_favorite_button);
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_detail_fragment, menu);

        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        // If onLoadFinished happens before this, we can go ahead and set the share intent now.
        if (this.canShareVideo()) {
            mShareActionProvider.setShareIntent(createShareVideoIntent(getVideoByPosition(VIDEO_TO_SHARE)));
        }
    }

    private Intent createShareVideoIntent(Video video) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, video.getFullUrl());
        return shareIntent;
    }

    public boolean canShareVideo() {
        return mVideoAdapter != null && mVideoAdapter.getCursor() != null && mVideoAdapter.getCursor().getCount() > 0;
    }

    private Video getVideoByPosition(int position) {
        Video result = null;
        if (null != mVideoAdapter) {
            Cursor c = mVideoAdapter.getCursor();
            if (null != c) {
                if (c.moveToPosition(position)) {
                    try {
                        result = DataAccessObject.mapItem(c, Video.class);
                    } catch (java.lang.InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return result;
    }

    private void openVideoOnIntent(int position) {
        Video video = this.getVideoByPosition(position);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(video.getFullUrl()));
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Log.d(this.getClass().getSimpleName(), "Couldn't call " + video.getFullUrl() + ", no receiving apps installed!");
        }
    }

}

