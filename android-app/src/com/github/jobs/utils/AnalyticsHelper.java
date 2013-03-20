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

import android.app.Activity;
import android.content.Context;
import com.google.analytics.tracking.android.EasyTracker;

/**
 * @author Evelio Tarazona Cáceres <evelio@evelio.info>
 * @author Cristian
 */
public final class AnalyticsHelper {
  public static final String CATEGORY_SEARCH = "search";
  public static final String CATEGORY_ABOUT = "about";
  public static final String CATEGORY_JOBS = "jobs";
  public static final String CATEGORY_SUBSCRIBE = "subscribe";
  public static final String ACTION_OPEN = "open";
  public static final String ACTION_EMAIL = "email";
  public static final String ACTION_REMOVE = "remove";
  public static final String ACTION_APPLY = "apply";
  public static final String ACTION_BACK = "go_back_home";
  public static final String ACTION_SHARE = "apply";
  public static final String ACTION_SWIPE = "swipe";
  public static final String ACTION_FOLLOW_CONTEXT = "follow_context_menu";
  public static final String ACTION_OPEN_CONTEXT = "open_context_menu";
  public static final String ACTION_CANCEL = "cancel";
  public static final String ACTION_SEARCH = "perform_search";
  public static final String LABEL_DIALOG = "dialog";
  public static final String LABEL_LEFT = "left";
  public static final String LABEL_RIGHT = "right";
  public static final String LABEL_FROM_DETAILS = "from_job_details";
  public static final String LABEL_DETAILS = "details";
  public static final String LABEL_APPLY = "apply";
  public static final String LABEL_CONTACT = "contact";
  public static final String LABEL_SHARE = "share";
  public static final String NAME_HOME = "/home";
  public static final String NAME_SEARCH_DIALOG = "/search_dialog";
  public static final String NAME_SUBSCRIBE_DIALOG = "/subscribe_dialog";
  public static final String NAME_DETAILS = "/job_details";
  public static final String NAME_HOW_TO_APPLY = "/how_to_apply";
  public static final String NAME_TEMPLATES = "/templates";
  public static final String NAME_EDIT_TEMPLATES = "/edit_templates";

  private static AnalyticsHelper INSTANCE;
  private Context mContext;

  public AnalyticsHelper(Context context) {
    setContext(context);
  }

  public static AnalyticsHelper getTracker(Context context) {
    if (INSTANCE == null) {
      INSTANCE = new AnalyticsHelper(context);
    }
    return INSTANCE;
  }

  /**
   * Sets the context to use to the applicationContext of the Context context.
   *
   * @param context the Context to use to fetch the applicationContext
   */
  private void setContext(Context context) {
    if (context != null && mContext == null) {
      mContext = context.getApplicationContext();
      try {
        EasyTracker.getInstance().setContext(context);
      } catch (Exception ignored) {
      }
    }
  }

  /**
   * Must be Called when activity is started, aka onStart
   *
   * @param activity Activity in onStart method
   */
  public void onActivityStarted(Activity activity) {
    // This call will ensure that the Activity in question is tracked properly,
    // based on the setting of ga_auto_activity_tracking parameter.  It will
    // also ensure that startNewSession is called appropriately.
    try {
      EasyTracker.getInstance().activityStart(activity);
    } catch (Exception ignored) {
    }
  }

  /**
   * Must be Called when activity is stopping, aka onStop
   *
   * @param activity Activity in onStop method
   */
  public void onActivityStopped(Activity activity) {
    // This call is needed to ensure time spent in an Activity and an
    // Application are measured accurately.
    try {
      EasyTracker.getInstance().activityStop(activity);
    } catch (Exception ignored) {
    }
  }

  /**
   * Track an Event.
   *
   * @param category the category of the event
   * @param action   the action of the event
   * @param label    the label of the event, can be null
   */
  public void trackEvent(String category, String action, String label) {
    try {
      EasyTracker.getTracker().trackEvent(category, action, label, 0L);
    } catch (Exception ignored) {
    }
  }

  /**
   * Track a pageview, which is analogous to an Activity.  If null is passed
   * in as input, no pageview will be tracked.
   *
   * @param name The name of the Activity or view to be tracked.
   */
  public void trackPageView(String name) {
    try {
      EasyTracker.getTracker().trackView(name);
    } catch (Throwable ignored) {
    }
  }
}
