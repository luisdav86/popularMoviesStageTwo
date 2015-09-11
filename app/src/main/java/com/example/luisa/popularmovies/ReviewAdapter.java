package com.example.luisa.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.luisa.popularmovies.core.DataAccessObject;
import com.example.luisa.popularmovies.core.LogIt;
import com.example.luisa.popularmovies.entity.Movie;
import com.example.luisa.popularmovies.entity.Review;
import com.squareup.picasso.Picasso;

/**
 * Created by Arcia on 9/11/2015.
 */
public class ReviewAdapter extends CursorAdapter {

    public ReviewAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_review, parent, false);
        TrailerHolder viewHolder = new TrailerHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TrailerHolder viewHolder = (TrailerHolder) view.getTag();
        try {
            Review review = DataAccessObject.mapItem(cursor, Review.class);
            viewHolder.title.setText(review.getContent());
        } catch (InstantiationException e) {
            LogIt.e(this, e, e.getMessage());
        } catch (IllegalAccessException e) {
            LogIt.e(this, e, e.getMessage());
        }
    }

    public static class TrailerHolder {
        TextView title;

        public TrailerHolder(View view) {
            this.title = (TextView) view.findViewById(R.id.list_item_review_content);
        }
    }
}
