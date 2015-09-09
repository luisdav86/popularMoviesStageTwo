package com.example.luisa.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by LuisA on 9/9/2015.
 */
public class TrailerAdapter extends CursorAdapter {

    public TrailerAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_trailer, parent, false);

        TrailerHolder viewHolder = new TrailerHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TrailerHolder viewHolder = (TrailerHolder) view.getTag();
        viewHolder.title.setText(context.getString(R.string.list_item_trailer_title) + " " + Integer.toString(cursor.getPosition() + 1));
    }

    public static class TrailerHolder {
        TextView title;

        public TrailerHolder(View view) {
            this.title = (TextView) view.findViewById(R.id.list_item_trailer_title);
        }
    }
}
