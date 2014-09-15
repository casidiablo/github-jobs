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

package com.github.jobs.templates.services;

import android.content.Context;
import com.github.jobs.R;
import com.github.jobs.templates.ServiceContract;
import com.github.jobs.templates.ServiceGenerator;

/**
 * @author cristian
 * @version 1.0
 */
public class WebsiteService implements ServiceContract {
  public static final String TYPE = "website";

  @Override
  public int getId() {
    return 0; // no needed
  }

  @Override
  public int getServiceLabel() {
    return R.string.lbl_website;
  }

  @Override
  public int getDrawable() {
    return 0; // no needed
  }

  @Override
  public int getHint() {
    return 0; // no needed
  }

  @Override
  public int getAddServiceLabel() {
    return 0; // no needed
  }

  @Override
  public String getType() {
    return TYPE; // no needed
  }

  @Override
  public ServiceGenerator getGenerator(Context context) {
    return new ServiceGenerator(context) {
      @Override
      protected String getLabel() {
        return getString(getServiceLabel());
      }
    };
  }
}
