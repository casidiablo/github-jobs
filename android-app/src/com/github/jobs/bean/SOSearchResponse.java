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

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author cristian
 */
public class SOSearchResponse {
  private List<SOUser> items;
  @JsonProperty("quota_remaining")
  private int quotaRemaining;
  @JsonProperty("quota_max")
  private int quotaMax;
  @JsonProperty("has_more")
  private boolean hasMore;

  public List<SOUser> getItems() {
    return items;
  }

  public void setItems(List<SOUser> items) {
    this.items = items;
  }

  public int getQuotaRemaining() {
    return quotaRemaining;
  }

  public void setQuotaRemaining(int quotaRemaining) {
    this.quotaRemaining = quotaRemaining;
  }

  public int getQuotaMax() {
    return quotaMax;
  }

  public void setQuotaMax(int quotaMax) {
    this.quotaMax = quotaMax;
  }

  public boolean isHasMore() {
    return hasMore;
  }

  public void setHasMore(boolean hasMore) {
    this.hasMore = hasMore;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    SOSearchResponse that = (SOSearchResponse) o;

    if (hasMore != that.hasMore) return false;
    if (quotaMax != that.quotaMax) return false;
    if (quotaRemaining != that.quotaRemaining) return false;
    if (items != null ? !items.equals(that.items) : that.items != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = items != null ? items.hashCode() : 0;
    result = 31 * result + quotaRemaining;
    result = 31 * result + quotaMax;
    result = 31 * result + (hasMore ? 1 : 0);
    return result;
  }

  @Override
  public String toString() {
    return "SOSearchResponse{" +
        "items=" + items +
        ", quotaRemaining=" + quotaRemaining +
        ", quotaMax=" + quotaMax +
        ", hasMore=" + hasMore +
        '}';
  }
}
