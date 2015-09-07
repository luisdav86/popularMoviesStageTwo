package com.example.luisa.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.media.Rating;
import android.provider.ContactsContract;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.luisa.popularmovies.core.DataAccessObject;
import com.example.luisa.popularmovies.core.LogIt;
import com.example.luisa.popularmovies.entity.Movie;
import com.squareup.picasso.Picasso;

/**
 * Created by Arcia on 8/28/2015.
 */
public class MovieAdapter extends CursorAdapter {

    public MovieAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_movie, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        try {
            Movie movie = DataAccessObject.mapItem(cursor, Movie.class);
            viewHolder.title.setText(movie.getOriginalTitle());
            viewHolder.rating.setRating(movie.getVoteAverage() / 2.0F);
            Picasso.with(context).load(context.getString(R.string.images_url) + context.getString(R.string.images_size) + movie.getPosterPath()).fit().centerCrop().into(viewHolder.imageView);
        } catch (InstantiationException e) {
            LogIt.e(this, e, e.getMessage());
        } catch (IllegalAccessException e) {
            LogIt.e(this, e, e.getMessage());
        }

    }

    public static class ViewHolder {
        ImageView imageView;
        TextView title;
        RatingBar rating;

        public ViewHolder(View view) {
            this.imageView = (ImageView) view.findViewById(R.id.list_item_movie_thumbail);
            this.title = (TextView) view.findViewById(R.id.list_item_movie_original_title);
            this.rating = (RatingBar) view.findViewById(R.id.list_item_vote_ratingBar);
        }
    }
}
