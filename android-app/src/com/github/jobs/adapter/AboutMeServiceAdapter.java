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

package com.github.jobs.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import com.github.jobs.R;
import com.github.jobs.bean.AboutMeService;
import com.telly.groundy.adapter.Layout;
import com.telly.groundy.adapter.ListBaseAdapter;
import com.telly.groundy.adapter.ResourceId;

/**
 * @author cristian
 * @version 1.0
 */
public class AboutMeServiceAdapter extends ListBaseAdapter<AboutMeService, AboutMeServiceAdapter.ViewHolder> {

  private static final String FACEBOOK = "facebook";

  public AboutMeServiceAdapter(Context context) {
    super(context, ViewHolder.class);
  }

  @Override
  public void populateHolder(int position, View view, ViewGroup parent, AboutMeService item, ViewHolder holder) {
    holder.label.setCheckMarkDrawable(R.drawable.btn_check_holo_light);
    if (FACEBOOK.equals(item.getPlatform())) {
      holder.label.setText(getContext().getString(R.string.lbl_facebook));
    } else {
      holder.label.setText(item.getDisplayName());
    }
  }

  @Layout(android.R.layout.simple_list_item_multiple_choice)
  public static class ViewHolder {
    @ResourceId(android.R.id.text1)
    CheckedTextView label;
  }
}
