package com.example.luisa.popularmovies.core;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by LuisA on 8/28/2015.
 */
public class DateUtil {

    public static Date parseISO8601(String date) throws ParseException {
        Date result = null;

        if (!TextUtils.isEmpty(date)) {
            SimpleDateFormat dateFormat = new SimpleDateFormat(
                    CoreConstants.ISO8601_DATETIME_FORMAT);

            result = dateFormat.parse(date);
        }

        return result;
    }

    public static String formatISO8601(Date date) {
        if (date != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat(
                    CoreConstants.ISO8601_DATETIME_FORMAT);
            return dateFormat.format(date);
        }

        return null;
    }
}
