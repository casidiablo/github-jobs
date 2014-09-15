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

package com.github.jobs.templates;

import android.content.Context;
import com.github.jobs.R;
import com.github.jobs.bean.TemplateService;

/** @author cristian */
public abstract class ServiceGenerator {

  private final Context mContext;

  public ServiceGenerator(Context context) {
    mContext = context;
  }

  public Context getContext() {
    return mContext;
  }

  public String getString(int resId) {
    return mContext.getString(resId);
  }

  public String getString(int resId, Object... args) {
    return mContext.getString(resId, args);
  }

  public String generate(TemplateService templateService) {
    return getString(R.string.cover_letter_footer, getLabel(), templateService.getData());
  }

  protected abstract String getLabel();
}
