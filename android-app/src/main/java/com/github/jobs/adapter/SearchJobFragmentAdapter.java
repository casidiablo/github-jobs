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

package com.github.jobs.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import com.github.jobs.R;
import com.github.jobs.bean.SearchPack;
import com.github.jobs.ui.fragment.JobListFragment;

import java.util.ArrayList;
import java.util.List;

public class SearchJobFragmentAdapter extends FragmentStatePagerAdapter {
  private final List<SearchPack> mSearchPacks = new ArrayList<SearchPack>();
  private final Context mContext;

  public SearchJobFragmentAdapter(Context context, FragmentManager fm) {
    super(fm);
    mContext = context;
    mSearchPacks.add(new SearchPack());
  }

  @Override
  public Fragment getItem(int position) {
    return JobListFragment.newInstance(mSearchPacks.get(position));
  }

  @Override
  public int getCount() {
    return mSearchPacks.size();
  }

  @Override
  public CharSequence getPageTitle(int position) {
    String toString = mSearchPacks.get(position).toString();
    if (TextUtils.isEmpty(toString)) {
      return mContext.getString(R.string.all_jobs);
    }
    return toString;
  }

  public int positionFor(SearchPack searchPack) {
    return mSearchPacks.indexOf(searchPack);
  }

  public void addSearch(SearchPack searchPack) {
    mSearchPacks.add(searchPack);
    notifyDataSetChanged();
  }

  public void removeItem(SearchPack searchPack) {
    mSearchPacks.remove(searchPack);
    notifyDataSetChanged();
  }

  public boolean containsSearch(SearchPack searchPack) {
    return mSearchPacks.contains(searchPack);
  }
}
