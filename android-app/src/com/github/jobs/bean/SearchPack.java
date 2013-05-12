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

package com.github.jobs.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import com.github.jobs.utils.StringUtils;

public class SearchPack implements Parcelable {
  private String search;
  private String location;
  private boolean fullTime = true;
  private int page;

  public String getSearch() {
    return search;
  }

  public void setSearch(String search) {
    this.search = search;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public boolean isFullTime() {
    return fullTime;
  }

  public void setFullTime(boolean fullTime) {
    this.fullTime = fullTime;
  }

  public int getPage() {
    return page;
  }

  public void setPage(int page) {
    this.page = page;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    SearchPack that = (SearchPack) o;

    if (fullTime != that.fullTime) return false;
    if (location != null ? !location.equals(that.location) : that.location != null) return false;
    if (search != null ? !search.equals(that.search) : that.search != null) return false;

    return true;
  }

  @Override
  public String toString() {
    String toString = "";
    if (!TextUtils.isEmpty(search)) {
      toString = search;
    }
    if (!TextUtils.isEmpty(location)) {
      if (!TextUtils.isEmpty(this.search)) {
        toString += ", ";
      }
      toString += location;
    }
    if (!fullTime) {
      toString += " (part time)";
    }
    return toString;
  }

  @Override
  public int hashCode() {
    int result = search != null ? StringUtils.trim(search.toLowerCase()).hashCode() : 0;
    result =
        31 * result + (location != null ? StringUtils.trim(location.toLowerCase()).hashCode() : 0);
    result = 31 * result + (fullTime ? 1 : 0);
    return result;
  }

  public boolean isDefault() {
    return search == null && location == null && fullTime;
  }

  @Override
  public int describeContents() {
    return hashCode();
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeValue(search);
    dest.writeValue(location);
    dest.writeValue(fullTime);
    dest.writeValue(page);
  }

  @SuppressWarnings("UnusedDeclaration")
  public static final Creator<SearchPack> CREATOR = new Creator<SearchPack>() {
    @Override
    public SearchPack createFromParcel(Parcel source) {
      SearchPack searchPack = new SearchPack();
      searchPack.setSearch((String) source.readValue(null));
      searchPack.setLocation((String) source.readValue(null));
      searchPack.setFullTime((Boolean) source.readValue(null));
      searchPack.setPage((Integer) source.readValue(null));
      return searchPack;
    }

    @Override
    public SearchPack[] newArray(int size) {
      return new SearchPack[size];
    }
  };
}
