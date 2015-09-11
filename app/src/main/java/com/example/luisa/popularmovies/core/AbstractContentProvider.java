package com.example.luisa.popularmovies.core;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import com.example.luisa.popularmovies.data.MoviesDbHelper;

/**
 * Created by Arcia on 9/11/2015.
 */
public abstract class AbstractContentProvider extends ContentProvider {

    static final int BASE_PATH = 100;

    public abstract AbstractContract getContract();

    public abstract Class<?> getProviderClass();

    private MoviesDbHelper mOpenHelper = null;

    public UriMatcher getUriMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = getContract().getContentAuthority();
        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, AbstractContract.BASE_PATH, BASE_PATH);
        matcher = onBuildUriMatcher(matcher);
        return matcher;
    }

    protected UriMatcher onBuildUriMatcher(UriMatcher matcher) {
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MoviesDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor;
        switch (getUriMatcher().match(uri)) {
            case BASE_PATH:
                retCursor = DataAccessObject.query(
                        getProviderClass(),
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public String getType(Uri uri) {
        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = getUriMatcher().match(uri);
        switch (match) {
            case BASE_PATH:
                return getContract().getBaseEntry().CONTENT_TYPE;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final int match = getUriMatcher().match(uri);
        Uri returnUri;
        switch (match) {
            case BASE_PATH: {
                long _id = DataAccessObject.insert(getProviderClass(), values);
                if (_id > 0)
                    returnUri = getContract().getBaseEntry().buildIdUri(_id);
                else
                    throw new android.database.SQLException("Failed to bulkInsert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final int match = getUriMatcher().match(uri);
        switch (match) {
            case BASE_PATH:
                int returnCount = 0;
                returnCount = DataAccessObject.bulkInsert(getProviderClass(), values);
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final int match = getUriMatcher().match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if (null == selection) selection = "1";
        switch (match) {
            case BASE_PATH:
                rowsDeleted = DataAccessObject.delete(getProviderClass(), selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final int match = getUriMatcher().match(uri);
        int rowsUpdated;

        switch (match) {
            case BASE_PATH:
                rowsUpdated = DataAccessObject.update(getProviderClass(), values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}
