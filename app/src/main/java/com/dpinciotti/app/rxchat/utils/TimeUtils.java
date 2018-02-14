package com.dpinciotti.app.rxchat.utils;

import android.content.Context;
import android.text.format.DateFormat;
import android.text.format.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeUtils {

    public static String getTimeSpanString(Context context, long time, boolean showTime) {

        int flags = DateUtils.FORMAT_ABBREV_RELATIVE | DateUtils.FORMAT_ABBREV_ALL;

        if (showTime) {
            flags |= DateUtils.FORMAT_SHOW_TIME;
        }

        long now = new Date().getTime();
        long duration = now - time;

        if (duration < DateUtils.HOUR_IN_MILLIS) {
            return DateUtils.getRelativeTimeSpanString(time, now, 0).toString();
        } else if (DateUtils.isToday(time)) {
            SimpleDateFormat timeInstance;
            if (DateFormat.is24HourFormat(context)) {
                timeInstance = new SimpleDateFormat("H:mm", Locale.US);
            } else {
                timeInstance = new SimpleDateFormat("h:mm a", Locale.US);
            }

            return timeInstance.format(time);
        } else if (duration < DateUtils.WEEK_IN_MILLIS) {
            return DateUtils.formatDateTime(context, time, flags | DateUtils.FORMAT_SHOW_WEEKDAY);
        }

        return DateUtils.formatDateTime(context, time, flags | DateUtils.FORMAT_SHOW_DATE);
    }
}
