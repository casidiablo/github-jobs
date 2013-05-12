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

/**
 * @author cristian
 * @version 1.0
 */
public class BadgeCount implements Parcelable {
  private int gold;
  private int silver;
  private int bronze;

  public BadgeCount() {
  }

  public BadgeCount(Parcel in) {
    gold = (Integer) in.readValue(null);
    silver = (Integer) in.readValue(null);
    bronze = (Integer) in.readValue(null);
  }

  public int getGold() {
    return gold;
  }

  public void setGold(int gold) {
    this.gold = gold;
  }

  public int getSilver() {
    return silver;
  }

  public void setSilver(int silver) {
    this.silver = silver;
  }

  public int getBronze() {
    return bronze;
  }

  public void setBronze(int bronze) {
    this.bronze = bronze;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    BadgeCount that = (BadgeCount) o;

    if (bronze != that.bronze) return false;
    if (gold != that.gold) return false;
    if (silver != that.silver) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = gold;
    result = 31 * result + silver;
    result = 31 * result + bronze;
    return result;
  }

  @Override
  public String toString() {
    return "BadgeCount{" +
        "gold=" + gold +
        ", silver=" + silver +
        ", bronze=" + bronze +
        '}';
  }

  @Override
  public int describeContents() {
    return hashCode();
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeValue(gold);
    dest.writeValue(silver);
    dest.writeValue(bronze);
  }

  @SuppressWarnings("UnusedDeclaration")
  public static final Parcelable.Creator<BadgeCount> CREATOR =
      new Parcelable.Creator<BadgeCount>() {
        public BadgeCount createFromParcel(Parcel in) {
          return new BadgeCount(in);
        }

        public BadgeCount[] newArray(int size) {
          return new BadgeCount[size];
        }
      };
}
