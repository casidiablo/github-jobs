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
import android.widget.ImageView;
import com.github.jobs.R;
import com.github.jobs.bean.Service;

/**
 * @author cristian
 * @version 1.0
 */
public class ServicesAdapter extends ListBaseAdapter<Service, ServicesAdapter.ViewHolder> {

  public ServicesAdapter(Context context) {
    super(context, ViewHolder.class);
  }

  @Override
  public long getItemId(int position) {
    return getItem(position).getId();
  }

  @Override
  public void populateHolder(int position, View view, ViewGroup parent, Service item,
      ViewHolder holder) {
    holder.item.setImageResource(item.getDrawable());
  }

  @Layout(R.layout.service_item)
  public static class ViewHolder {
    ImageView item;
  }
}
