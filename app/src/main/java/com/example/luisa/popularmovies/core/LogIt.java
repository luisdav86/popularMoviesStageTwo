package com.example.luisa.popularmovies.core;

import android.util.Log;

/**
 * Created by LuisA on 8/28/2015.
 */
public class LogIt {

    public static void e(Object src, Throwable t, Object... messages) {
        StringBuilder builder = new StringBuilder();
        String message = "";
        if (t != null) {
            builder.append(t.getMessage()).append(": ");
        } else {
            builder.append("ERROR: ");
        }
        for (Object m : messages) {
            message = m + ", ";
            builder.append(message);
        }
        Class<?> clasz = src instanceof Class ? (Class<?>) src : src.getClass();
        Log.e(clasz.getName(), builder.toString(), t);
    }
}
