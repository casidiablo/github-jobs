/*
 * Copyright 2012 CodeSlap
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.jobs.utils;

import android.content.Context;
import com.github.jobs.R;
import org.joda.time.DateTime;
import org.joda.time.Period;

public class RelativeDate {

  /**
   * This method returns a String representing the relative
   * date by comparing the Calendar being passed in to the
   * date / time that it is right now.
   *
   * @param context used to build the string response
   * @param time    time to compare with current time
   * @return a string representing the time ago
   */
  public static String getTimeAgo(Context context, long time) {
    DateTime baseDate = new DateTime(time);
    DateTime now = new DateTime();
    Period period = new Period(baseDate, now);

    if (period.getSeconds() < 0 || period.getMinutes() < 0) {
      return context.getString(R.string.just_now);
    }

    if (period.getYears() > 0) {
      int resId = period.getYears() == 1 ? R.string.one_year_ago : R.string.years_ago;
      return buildString(context, resId, period.getYears());
    }

    if (period.getMonths() > 0) {
      int resId = period.getMonths() == 1 ? R.string.one_month_ago : R.string.months_ago;
      return buildString(context, resId, period.getMonths());
    }

    if (period.getWeeks() > 0) {
      int resId = period.getWeeks() == 1 ? R.string.one_week_ago : R.string.weeks_ago;
      return buildString(context, resId, period.getWeeks());
    }

    if (period.getDays() > 0) {
      int resId = period.getDays() == 1 ? R.string.one_day_ago : R.string.days_ago;
      return buildString(context, resId, period.getDays());
    }

    if (period.getHours() > 0) {
      int resId = period.getHours() == 1 ? R.string.one_hour_ago : R.string.hours_ago;
      return buildString(context, resId, period.getHours());
    }

    if (period.getMinutes() > 0) {
      int resId = period.getMinutes() == 1 ? R.string.one_minute_ago : R.string.minutes_ago;
      return buildString(context, resId, period.getMinutes());
    }

    int resId = period.getSeconds() == 1 ? R.string.one_second_ago : R.string.seconds_ago;
    return buildString(context, resId, period.getSeconds());
  }

  private static String buildString(Context context, int resId, int value) {
    if (value == 1) {
      return context.getString(resId);
    } else {
      return context.getString(resId, value);
    }
  }
}