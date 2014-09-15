/*
 * Copyright 2014 Some Dev
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
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;
import com.github.jobs.R;

import static com.github.jobs.utils.AnalyticsHelper.*;

public class WebsiteHelper {

  public static boolean launchWebsite(Context context, String url) {
    try {
      getTracker(context).trackEvent(CATEGORY_ABOUT, ACTION_OPEN, url);
      Intent intent = new Intent(Intent.ACTION_VIEW);
      intent.addCategory(Intent.CATEGORY_BROWSABLE);
      intent.setData(Uri.parse(url));
      context.startActivity(intent);
      return true;
    } catch (Exception e) {
      Toast.makeText(context, R.string.could_not_launch_url, Toast.LENGTH_LONG).show();
      return false;
    }
  }
}
