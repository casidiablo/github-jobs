package com.github.jobs.utils;

import android.content.Context;
import com.github.jobs.R;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class RelativeDate {

    /**
     * This method returns a String representing the relative
     * date by comparing the Calendar being passed in to the
     * date / time that it is right now.
     *
     * @param context
     * @return String representing the relative date
     */

    public static String getRelativeDate(Context context, Calendar calendar) {
        long value;
        int resId;

        Calendar now = GregorianCalendar.getInstance(TimeZone.getTimeZone("UTC")); // Servers use UTC
        int hourInPacific = now.get(Calendar.HOUR_OF_DAY);
        int dayInPacific = now.get(Calendar.DAY_OF_YEAR);
        int hourHere = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int dayHere = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);

        if (dayHere > dayInPacific) {
            hourHere += 24;
        } else if (dayInPacific > dayHere) {
            hourInPacific += 24;
        }

        long nowMilliseconds = now.getTimeInMillis();
        nowMilliseconds -= (hourHere - hourInPacific) * 60 * 60 * 1000;// fix hours
        double diff = (nowMilliseconds - calendar.getTimeInMillis()) / 1000;
        value = (long) Math.floor(diff);

        if (value < 0) {// if for some weird reason, diff is negative... show the default message
            return buildString(context, R.string.few_secs_ago, 1);
        }

        resId = value == 1 ? R.string.one_second_ago : R.string.seconds_ago;
        if (value < 60) {
            return buildString(context, resId, value);
        }

        diff = Math.floor(diff / 60);// 60 = secs in a minute
        value = Math.round(diff);
        resId = value == 1 ? R.string.one_minute_ago : R.string.minutes_ago;
        if (value < 60) {
            return buildString(context, resId, value);
        }

        diff = diff / 60;// 60 = mins in an hour
        value = Math.round(diff);
        resId = value == 1 ? R.string.one_hour_ago : R.string.hours_ago;
        if (value < 24) {
            return buildString(context, resId, value);
        }

        diff = diff / 24;// 24 = hours in a day
        value = Math.round(diff);
        resId = value == 1 ? R.string.one_day_ago : R.string.days_ago;
        if (value < 7) {
            return buildString(context, resId, value);
        }

        diff = diff / 7;// 7 = days in a week
        value = Math.round(diff);
        resId = value == 1 ? R.string.one_week_ago : R.string.weeks_ago;
        if (value < 4) {
            return buildString(context, resId, value);
        }

        diff = diff / 4.28;// 4.28 = weeks in a month
        value = Math.round(diff);
        resId = value == 1 ? R.string.one_month_ago : R.string.months_ago;
        if (value < 12) {
            return buildString(context, resId, value);
        }

        diff = diff / 12;// 12 = months in a year
        value = Math.round(diff);
        resId = value == 1 ? R.string.one_year_ago : R.string.years_ago;
        return buildString(context, resId, value);
    }

    private static String buildString(Context context, int resId, long value) {
        if (value == 1) {
            return context.getString(resId);
        } else {
            return context.getString(resId, value);
        }
    }
}