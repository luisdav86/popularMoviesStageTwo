package com.example.luisa.popularmovies.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.os.Build;
import android.os.Bundle;

import com.example.luisa.popularmovies.R;
import com.example.luisa.popularmovies.Utility;
import com.example.luisa.popularmovies.core.DataAccessObject;
import com.example.luisa.popularmovies.core.LogIt;
import com.example.luisa.popularmovies.data.DBConstants;
import com.example.luisa.popularmovies.data.MoviesContract;
import com.example.luisa.popularmovies.data.ReviewContract;
import com.example.luisa.popularmovies.data.VideoContract;
import com.example.luisa.popularmovies.entity.Movie;
import com.example.luisa.popularmovies.entity.Review;
import com.example.luisa.popularmovies.entity.Video;
import com.example.luisa.popularmovies.rest.MovieRestService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by LuisA on 8/28/2015.
 */
public class MovieSyncAdapter extends AbstractThreadedSyncAdapter {

    // Interval at which to sync with the weather, in seconds.
    // 60 seconds (1 minute) * 180 = 3 hours
    public static final int SYNC_INTERVAL = 60 * 180;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL / 3;

    public MovieSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        String apiKey = getContext().getString(R.string.apy_key);
        long movieId = -1;
        try {
            List<Movie> movies = MovieRestService.getMovies(Utility.getPreferredLocation(getContext()), apiKey).getResults();
            ContentResolver resolver = getContext().getContentResolver();
            resolver.bulkInsert(new MoviesContract().getBaseEntry().CONTENT_URI
                    , DataAccessObject.toContentValues(movies));

            for (Movie movie : movies) {

                movieId = movie.getId();

                Map<String, Object> movieIdKeyPar = new HashMap<>();
                movieIdKeyPar.put(DBConstants.ReviewColumns.MOVIE_ID, movieId);

                List<Review> reviews = MovieRestService.getRelatedReviews(movieId, apiKey).getResults();
                resolver.bulkInsert(new ReviewContract().getBaseEntry().CONTENT_URI, DataAccessObject.toContentValues(reviews, movieIdKeyPar));

                movieIdKeyPar = new HashMap<>();
                movieIdKeyPar.put(DBConstants.VideoColumns.MOVIE_ID, movieId);
                List<Video> videos = MovieRestService.getRelatedVideos(movieId, apiKey).getResults();
                resolver.bulkInsert(new VideoContract().getBaseEntry().CONTENT_URI, DataAccessObject.toContentValues(videos, movieIdKeyPar));

            }
        } catch (Exception e )
        {
            LogIt.e(this, e, e.getMessage());
        }
    }

    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if (null == accountManager.getPassword(newAccount)) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        MovieSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }
}
